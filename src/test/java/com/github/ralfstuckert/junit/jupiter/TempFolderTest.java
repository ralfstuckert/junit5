package com.github.ralfstuckert.junit.jupiter;

import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TempFile;
import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TempFolder;
import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TemporaryFolder;
import com.github.ralfstuckert.junit.jupiter.extension.tempfolder.TemporaryFolderExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

@ExtendWith(TemporaryFolderExtension.class)
public class TempFolderTest {

    private TemporaryFolder temporaryFolder;

    @Test
    public void testTemporaryFolderAsParameter(final TemporaryFolder folder) {
        assertNotNull(folder);
    }

    @Test
    public void testTemporaryFolderInjection() {
        assertNotNull(temporaryFolder);
    }

    @Test
    public void testInjectedTemporaryFolderSameAsParameter(final TemporaryFolder folder) {
        assertNotNull(folder);
        assertNotNull(temporaryFolder);
    }

    @Test
    public void testTempFolder(@TempFolder final File folder) {
        assertNotNull(folder);
        assertTrue(folder.exists());
        assertTrue(folder.isDirectory());
    }

    @Test
    public void testTempFile(@TempFile("hihi") final File file) {
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.isFile());
        assertEquals("hihi", file.getName());
    }


}
