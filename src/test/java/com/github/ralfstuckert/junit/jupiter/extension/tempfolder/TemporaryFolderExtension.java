package com.github.ralfstuckert.junit.jupiter.extension.tempfolder;

import org.junit.jupiter.api.extension.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Created by Ralf on 08.03.2017.
 */
public class TemporaryFolderExtension implements ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback, TestInstancePostProcessor {

    @Override
    public void beforeTestExecution(TestExtensionContext extensionContext) throws Exception {
        // clean up test instance
        prepareTemporaryFolder(extensionContext);

        if (extensionContext.getParent().isPresent()) {
            // clean up injected member
            prepareTemporaryFolder(extensionContext.getParent().get());
        }
    }

    @Override
    public void afterTestExecution(TestExtensionContext extensionContext) throws Exception {
        // clean up test instance
        cleanUpTemporaryFolder(extensionContext);

        if (extensionContext.getParent().isPresent()) {
            // clean up injected member
            cleanUpTemporaryFolder(extensionContext.getParent().get());
        }
    }

    protected void prepareTemporaryFolder(ExtensionContext extensionContext) throws IOException {
        TemporaryFolder temporaryFolder = getTemporaryFolder(extensionContext);
        if (temporaryFolder != null) {
            temporaryFolder.before();
        }
    }

    protected void cleanUpTemporaryFolder(ExtensionContext extensionContext) {
        TemporaryFolder temporaryFolder = getTemporaryFolder(extensionContext);
        if (temporaryFolder != null) {
            temporaryFolder.after();
        }
    }

    @Override
    public boolean supports(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        return parameter.getType().isAssignableFrom(TemporaryFolder.class) ||
                (parameter.getType().isAssignableFrom(File.class) && (parameter.isAnnotationPresent(TempFolder.class)
                        || parameter.isAnnotationPresent(TempFile.class)));
    }

    @Override
    public Object resolve(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        try {
            TemporaryFolder temporaryFolder = createTemporaryFolder(extensionContext);

            Parameter parameter = parameterContext.getParameter();
            if (parameter.getType().isAssignableFrom(TemporaryFolder.class)) {
                return temporaryFolder;
            }
            if (parameter.isAnnotationPresent(TempFolder.class)) {
                return temporaryFolder.newFolder();
            }
            if (parameter.isAnnotationPresent(TempFile.class)) {
                TempFile annotation = parameter.getAnnotation(TempFile.class);
                if (!annotation.value().isEmpty()) {
                    return temporaryFolder.newFile(annotation.value());
                }
                return temporaryFolder.newFile();
            }

            throw new ParameterResolutionException("unable to resolve parameter for " + parameterContext);
        } catch (IOException e) {
            throw new ParameterResolutionException("failed to create temp file or folder", e);
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        for (Field field : testInstance.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(TemporaryFolder.class)) {
                TemporaryFolder temporaryFolder = createTemporaryFolder(context);
                field.setAccessible(true);
                field.set(testInstance, temporaryFolder);
            }
        }
    }

    protected TemporaryFolder getTemporaryFolder(ExtensionContext extensionContext) {
        return getStore(extensionContext).get(extensionContext.getTestClass().get(), TemporaryFolder.class);
    }

    protected TemporaryFolder createTemporaryFolder(ExtensionContext extensionContext) {
        TemporaryFolder temporaryFolder = getStore(extensionContext).getOrComputeIfAbsent(extensionContext.getTestClass().get(), this::createTemporaryFolder, TemporaryFolder.class);
        return temporaryFolder;
    }

    protected ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass(), context));
    }

    private TemporaryFolder createTemporaryFolder(Class<?> t) {
        TemporaryFolder temporaryFolder = new TemporaryFolder();
        return temporaryFolder;
    }

}