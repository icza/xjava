/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.nio.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import x.java.BaseTest;
import x.java.lang.Pair;

/**
 * JUnit test of {@link PathUtils}.
 * 
 * @author Andras Belicza
 */
public class PathUtilsTest extends BaseTest {
    
    /** Saved logging level of class {@link PathUtils} to be restored after tests. */
    private static Level savedLoggingLevel;
    
    /**
     * @throws Exception a
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Disable logging to not pollute console
        savedLoggingLevel = Logger.getLogger(PathUtils.class.getName()).getLevel();
        Logger.getLogger(PathUtils.class.getName()).setLevel(Level.OFF);
    }
    
    /**
     * @throws Exception a
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Logger.getLogger(PathUtils.class.getName()).setLevel(savedLoggingLevel);
    }
    
    
    /** */
    @Test
    public void testGetFileNameWithoutExt() {
        String[] in = { "atxt", "a.txt", "a.txt.txt", ".txt", ".txt.dat", "asdf/as.txt", "", "asd." };
        String[] out = { "atxt", "a", "a.txt", ".txt", ".txt", "as", "", "asd" };
        
        for (int i = 0; i < in.length; i++)
            assertEquals(out[i], PathUtils.getFileNameWithoutExt(Paths.get(in[i])));
    }
    
    /** */
    @Test
    public void testGetFileNameAndExt() {
        String[] in = { "atxt", "a.txt", "a.txt.txt", ".txt", ".txt.dat", "asdf/as.txt", "", "asd." };
        String[] out = { "atxt", null, "a", "txt", "a.txt", "txt", ".txt", null, ".txt", "dat", "as", "txt",
                "", null, "asd", "" };
        
        for (int i = 0; i < in.length; i++) {
            Pair<String, String> nameExt = PathUtils.getFileNameAndExt(Paths.get(in[i]));
            assertEquals(out[i * 2], nameExt.value1);
            assertEquals(out[i * 2 + 1], nameExt.value2);
        }
    }
    
    /** */
    @Test
    public void testUniqueFile() {
        // Non-existing files are unique
        Path nonExisting = Paths.get("non-existing-file");
        
        assertEquals(nonExisting, PathUtils.uniqueFile(nonExisting));
        
        Path in = Paths.get("bin-test", PathUtilsTest.class.getName().replace('.', '/') + ".class");
        Path out = Paths.get("bin-test", PathUtilsTest.class.getName().replace('.', '/') + " (2).class");
        
        assertEquals(out, PathUtils.uniqueFile(in));
        
        in = Paths.get("bin-test", PathUtilsTest.class.getPackage().getName().replace('.', '/'),
                "test-file.txt");
        out = Paths.get("bin-test", PathUtilsTest.class.getPackage().getName().replace('.', '/'),
                "test-file (3).txt");
        
        assertEquals(out, PathUtils.uniqueFile(in));
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testDeletePath() throws IOException {
        assertTrue(PathUtils.deletePath(Paths.get("non-existing-folder")));
        
        // Test on a file
        Path testFolder = Paths.get("test-delete-path");
        Path testFile = testFolder.resolve("test.txt");
        try {
            Files.createDirectories(testFolder);
            Files.createFile(testFile);
            
            assertTrue(PathUtils.deletePath(testFile));
            
            assertFalse(Files.exists(testFile));
        } finally {
            Files.deleteIfExists(testFile);
            Files.deleteIfExists(testFolder);
        }
        
        // Test on a folder
        try {
            Files.createDirectories(testFolder);
            Files.createFile(testFile);
            
            assertTrue(PathUtils.deletePath(testFolder));
            
            assertFalse(Files.exists(testFolder));
        } finally {
            Files.deleteIfExists(testFile);
            Files.deleteIfExists(testFolder);
        }
        
        // Test on a folder having a subfolder
        Path testSubfolder = testFolder.resolve("subfolder");
        try {
            Files.createDirectories(testSubfolder);
            Files.createFile(testFile);
            
            assertTrue(PathUtils.deletePath(testFolder));
            
            assertFalse(Files.exists(testFolder));
        } finally {
            Files.deleteIfExists(testFile);
            Files.deleteIfExists(testSubfolder);
            Files.deleteIfExists(testFolder);
        }
    }
    
    /** */
    @Test
    public void testPathSize() {
        // -1 is returned for non-existing paths
        assertEquals(-1L, PathUtils.pathSize(Paths.get("non-existing-folder")));
        
        Path testFolder = Paths.get("bin-test", PathUtilsTest.class.getPackage().getName().replace('.', '/'),
                "size21bytes");
        
        assertEquals(10L, PathUtils.pathSize(testFolder.resolve("10-bytes.txt")));
        
        assertEquals(0L, PathUtils.pathSize(testFolder.resolve("empty")));
        
        assertEquals(11L, PathUtils.pathSize(testFolder.resolve("size11bytes")));
        
        assertEquals(21L, PathUtils.pathSize(testFolder));
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testEnsureFolderExists() throws IOException {
        Path testFolder = Paths.get("test-ensure-folder-exists");
        try {
            assertTrue(PathUtils.ensureFolderExists(testFolder));
            
            assertTrue(Files.exists(testFolder));
            assertTrue(Files.isDirectory(testFolder));
            
            // Calling on an existing folder:
            assertFalse(PathUtils.ensureFolderExists(testFolder));
        } finally {
            Files.deleteIfExists(testFolder);
        }
        
        // Folder with subfolder:
        Path testSubfolder = testFolder.resolve("subfolder");
        try {
            assertTrue(PathUtils.ensureFolderExists(testSubfolder));
            
            assertTrue(Files.exists(testSubfolder));
            assertTrue(Files.isDirectory(testSubfolder));
        } finally {
            Files.deleteIfExists(testSubfolder);
            Files.deleteIfExists(testFolder);
        }
    }
    
    /** */
    @Test(expected = RuntimeException.class)
    public void testEnsureFolderExistsException() {
        // Calling on an existing file:
        Path existingFile = Paths.get("bin-test", PathUtilsTest.class.getName().replace('.', '/') + ".class");
        
        PathUtils.ensureFolderExists(existingFile);
    }
    
}
