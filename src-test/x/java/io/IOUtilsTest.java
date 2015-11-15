/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link IOUtils}.
 * 
 * @author Andras Belicza
 */
public class IOUtilsTest extends BaseTest {
    
    /** Saved logging level of class {@link IOUtils} to be restored after tests. */
    private static Level savedLoggingLevel;
    
    /**
     * @throws Exception a
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Disable logging to not pollute console
        savedLoggingLevel = Logger.getLogger(IOUtils.class.getName()).getLevel();
        Logger.getLogger(IOUtils.class.getName()).setLevel(Level.OFF);
    }
    
    /**
     * @throws Exception a
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        Logger.getLogger(IOUtils.class.getName()).setLevel(savedLoggingLevel);
    }
    
    
    /** */
    static class NotSerializableExample implements Serializable {
        /** */
        private static final long serialVersionUID = 1L;
        /** The type {@link Object} is not serializable. */
        public Object o = new Object();
    }
    
    /** */
    @Test
    public void testSerialize() {
        assertNull(IOUtils.serialize(new NotSerializableExample()));
        
        byte[] out = { -84, -19, 0, 5, 116, 0, 3, 97, 98, 99 };
        
        assertArrayEquals(out, IOUtils.serialize("abc"));
        
        out = new byte[] { -84, -19, 0, 5, 117, 114, 0, 2, 91, 67, -80, 38, 102, -80, -30, 93, -124, -84, 2,
                0, 0, 120, 112, 0, 0, 0, 1, 0, 97 };
        assertArrayEquals(out, IOUtils.serialize(new char[] { 'a' }));
        
        out = new byte[] { -84, -19, 0, 5, 115, 114, 0, 19, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 67,
                104, 97, 114, 97, 99, 116, 101, 114, 52, -117, 71, -39, 107, 26, 38, 120, 2, 0, 1, 67, 0, 5,
                118, 97, 108, 117, 101, 120, 112, 0, 97 };
        
        assertArrayEquals(out, IOUtils.serialize(new Character('a')));
        
        HashMap<String, Integer> m = new HashMap<>();
        m.put("1", 1);
        m.put("2", 2);
        m.put("3", 3);
        assertEquals(m, IOUtils.deserialize(IOUtils.serialize(m)));
        
        assertEquals((Object) null, IOUtils.deserialize(IOUtils.serialize(null)));
    }
    
    /** */
    @Test
    public void testDeserialize() {
        byte[] in = {};
        assertNull(IOUtils.deserialize(in));
        
        in = new byte[] { -84, -19, 0, 5, 116, 0, 3, 97, 98, 99 };
        
        assertEquals("abc", IOUtils.deserialize(in));
        
        in = new byte[] { -84, -19, 0, 5, 117, 114, 0, 2, 91, 67, -80, 38, 102, -80, -30, 93, -124, -84, 2,
                0, 0, 120, 112, 0, 0, 0, 1, 0, 97 };
        assertArrayEquals(new char[] { 'a' }, IOUtils.deserialize(in));
        
        in = new byte[] { -84, -19, 0, 5, 115, 114, 0, 19, 106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 67,
                104, 97, 114, 97, 99, 116, 101, 114, 52, -117, 71, -39, 107, 26, 38, 120, 2, 0, 1, 67, 0, 5,
                118, 97, 108, 117, 101, 120, 112, 0, 97 };
        
        assertEquals(new Character('a'), IOUtils.deserialize(in));
        
        HashMap<String, Integer> m = new HashMap<>();
        m.put("1", 1);
        m.put("2", 2);
        m.put("3", 3);
        assertEquals(m, IOUtils.deserialize(IOUtils.serialize(m)));
        
        assertEquals((Object) null, IOUtils.deserialize(IOUtils.serialize(null)));
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testTryReadFully() throws IOException {
        byte[] src = { 0, 1, 2, 3, 4 };
        byte[] target = new byte[src.length];
        
        assertEquals(src.length, IOUtils.tryReadFully(new ByteArrayInputStream(src), target));
        assertArrayEquals(src, target);
        
        target = new byte[src.length + 2];
        assertEquals(src.length, IOUtils.tryReadFully(new ByteArrayInputStream(src), target));
        for (int i = 0; i < src.length; i++)
            assertEquals(src[i], target[i]);
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testReadFully() throws IOException {
        byte[] src = { 0, 1, 2, 3, 4 };
        byte[] target = new byte[src.length];
        
        IOUtils.readFully(new ByteArrayInputStream(src), target);
        
        assertArrayEquals(src, target);
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IOException.class)
    public void testReadFullyEOF() throws IOException {
        byte[] src = { 0, 1, 2, 3, 4 };
        byte[] target = new byte[src.length + 2];
        
        IOUtils.readFully(new ByteArrayInputStream(src), target);
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testReadAllBytes() throws IOException {
        byte[] src = { 0, 1, 2, 3, 4 };
        
        byte[] target = IOUtils.readAllBytes(new ByteArrayInputStream(src));
        
        assertArrayEquals(src, target);
        
        // Test a big array which will be read in multiple, complete chunks
        src = new byte[5 * 16 * 1024];
        for (int i = src.length - 1; i >= 0; i--)
            src[i] = (byte) i;
        
        target = IOUtils.readAllBytes(new ByteArrayInputStream(src));
        
        assertArrayEquals(src, target);
        
        // Test a big array which will be read in multiple chunks including an incomplete one
        src = new byte[5 * 16 * 1024 + 4];
        for (int i = src.length - 1; i >= 0; i--)
            src[i] = (byte) i;
        
        target = IOUtils.readAllBytes(new ByteArrayInputStream(src));
        
        assertArrayEquals(src, target);
    }
    
}
