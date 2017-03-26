package org.springframework.test.context.junit.jupiter;

import org.junit.jupiter.api.extension.*;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.AnnotatedElement;
import java.util.*;

/**
 * This extension uses the {@link SpringExtension#getApplicationContext(ExtensionContext)}}
 * method which is currently package private, that's why this class is located in the same
 * package :-\
 * <br/>
 * This will be fixed in the next spring release 5.0 RC1, see issue
 * <a href="https://jira.spring.io/browse/SPR-15340>SPR-15340</a>.
 */
public class MongoCleanupExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(TestExtensionContext context) throws Exception {
        MongoCleaner mongoCleaner = getMongoCleaner(context);
        List<Class<?>> entityTypesToCleanup = getEntityTypesToCleanup(context);
        mongoCleaner.prepare(entityTypesToCleanup);
    }

    @Override
    public void afterEach(TestExtensionContext context) throws Exception {
        MongoCleaner mongoCleaner = getMongoCleaner(context);
        Map<Class<?>, Set<String>> cleanupResult = mongoCleaner.cleanup();
        cleanupResult.forEach((entityType, ids) -> {
            context.publishReportEntry(String.format("deleted %s entities", entityType.getSimpleName()), ids.toString());
        });
    }

    protected MongoCleaner getMongoCleaner(ExtensionContext context) {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        MongoCleaner mongoCleaner = applicationContext.getBean(MongoCleaner.class);
        return mongoCleaner;
    }

    protected List<Class<?>> getEntityTypesToCleanup(ExtensionContext context) {
        Optional<AnnotatedElement> element = context.getElement();
        MongoCleanup annotation = AnnotationUtils.findAnnotation(context.getTestClass().get(), MongoCleanup.class);
        return Arrays.asList(annotation.value());
    }


}
