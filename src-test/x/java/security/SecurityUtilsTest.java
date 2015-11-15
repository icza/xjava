/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import x.java.BaseTest;
import x.java.lang.Ref;

/**
 * JUnit test of {@link SecurityUtils}.
 * 
 * @author Andras Belicza
 */
public class SecurityUtilsTest extends BaseTest {
    
    /** Saved logging level of class {@link SecurityUtils} to be restored after tests. */
    private static Level savedLoggingLevel;
    
    /**
     * @throws Exception a
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Disable logging to not pollute console
        savedLoggingLevel = Logger.getLogger(SecurityUtils.class.getName()).getLevel();
        Logger.getLogger(SecurityUtils.class.getName()).setLevel(Level.OFF);
    }
    
    /**
     * @throws Exception a
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Logger.getLogger(SecurityUtils.class.getName()).setLevel(savedLoggingLevel);
    }
    
    
    /** */
    @Test
    public void testFileDigest() {
        final Path file = Paths.get("bin-test",
                SecurityUtilsTest.class.getPackage().getName().replace('.', '/'), "sample.txt");
        
        Ref<Exception> ex = new Ref<>();
        String s = SecurityUtils.fileDigest("MD5", file, ex);
        
        assertEquals("2ac685871943bc36825544e97d7491a9", s);
        
        ex = new Ref<>();
        s = SecurityUtils.fileDigest("MD5", file.resolveSibling("non-existing-file.txt"), ex);
        
        assertEquals("", s);
        assertTrue(ex.value instanceof IOException);
        
        ex = new Ref<>();
        s = SecurityUtils.fileDigest("unknownAlgorithm", file, ex);
        
        assertEquals("", s);
        assertTrue(ex.value instanceof NoSuchAlgorithmException);
        
        // Shouldn't cause any error if optional exception parameter is missing
        
        s = SecurityUtils.fileDigest("MD5", file.resolveSibling("non-existing-file.txt"), ex);
        assertEquals("", s);
        
        s = SecurityUtils.fileDigest("unknownAlgorithm", file, ex);
        assertEquals("", s);
    }
    
    /**
     * @throws UnsupportedEncodingException s
     */
    @Test
    public void testStreamDigest() throws UnsupportedEncodingException {
        byte[] src = "Sample text file with some data.".getBytes("UTF-8");
        
        Ref<Exception> ex = new Ref<>();
        String s = SecurityUtils.streamDigest("MD5", new ByteArrayInputStream(src), 32, ex);
        
        assertEquals("2ac685871943bc36825544e97d7491a9", s);
        
        ex = new Ref<>();
        s = SecurityUtils.streamDigest("MD5", new ByteArrayInputStream(src), -1, ex);
        
        assertEquals("2ac685871943bc36825544e97d7491a9", s);
        
        ex = new Ref<>();
        s = SecurityUtils.streamDigest("MD5", new ByteArrayInputStream(src), 6, ex);
        
        assertEquals("c5dd1b2697720fe692c529688d3f4f8d", s);
        
        ex = new Ref<>();
        s = SecurityUtils.streamDigest("MD5", new ByteArrayInputStream(src), src.length + 2, ex);
        
        assertEquals("", s);
        assertTrue(ex.value instanceof IOException);
        
        // Shouldn't cause any error if optional exception parameter is missing
        s = SecurityUtils.streamDigest("MD5", new ByteArrayInputStream(src), src.length + 2);
        
        assertEquals("", s);
    }
    
}
