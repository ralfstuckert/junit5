package com.github.ralfstuckert.junit.jupiter;

import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TempFile;
import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TempFolder;
import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TemporaryFolder;
import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TemporaryFolderExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(TemporaryFolderExtension.class)
public class TempFolderTest {

    private List<File> createdFiles = new ArrayList<>();
    private TemporaryFolder temporaryFolder;


    private void rememberFile(File file) {
        createdFiles.add(file);
    }

    private void checkFileAndParentHasBeenDeleted(File file) {
        assertFalse(file.exists(), String.format("file %s has not been deleted", file.getAbsolutePath()));
        assertFalse(file.getParentFile().exists(), String.format("folder %s has not been deleted", file.getParentFile().getAbsolutePath()));
    }

    @BeforeEach
    public void setUp() throws IOException {
        assertNotNull(temporaryFolder);

        createdFiles.clear();

        // create a file in set up
        File file = temporaryFolder.newFile();
        rememberFile(file);
    }

    @AfterEach
    public void tearDown() throws Exception {
        for (File file : createdFiles) {
            checkFileAndParentHasBeenDeleted(file);
        }
    }

    @Test
    public void testTemporaryFolderInjection() throws Exception  {
        File file = temporaryFolder.newFile();
        rememberFile(file);
        assertNotNull(file);
        assertTrue(file.isFile());

        File folder = temporaryFolder.newFolder();
        rememberFile(folder);
        assertNotNull(folder);
        assertTrue(folder.isDirectory());
    }

    @Test
    public void testTemporaryFolderAsParameter(final TemporaryFolder tempFolder) throws Exception {
        assertNotNull(tempFolder);
        assertNotSame(tempFolder, temporaryFolder);

        File file = tempFolder.newFile();
        rememberFile(file);
        assertNotNull(file);
        assertTrue(file.isFile());
    }

    @Test
    public void testTempFolder(@TempFolder final File folder) {
        rememberFile(folder);
        assertNotNull(folder);
        assertTrue(folder.exists());
        assertTrue(folder.isDirectory());
    }

    @Test
    public void testTempFile(@TempFile("hihi") final File file) {
        rememberFile(file);
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertEquals("hihi", file.getName());
    }


}
