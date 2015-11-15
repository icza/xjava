/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.AfterClass;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link PersistentMap}.
 * 
 * @author Andras Belicza
 */
public class PersistentMapTest extends BaseTest {
    
    /** */
    private static final Path ROOT_FOLDER = Paths.get("persistent-map-test");
    
    /**
     * @throws Exception a
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        PersistentMap.delete(ROOT_FOLDER);
        Files.deleteIfExists(ROOT_FOLDER);
    }
    
    
    /**
     * @throws IOException a
     */
    @Test
    public void testGeneral() throws IOException {
        byte[] in = { 0, 1, 2, 3, 4 };
        
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            map.clear();
            assertEquals(0, (int) map.size());
            map.put("one", in);
            assertEquals(1, (int) map.size());
            assertTrue(map.contains("one"));
            assertArrayEquals(in, map.get("one"));
            
            map.putObj("obj", "Test string");
            assertEquals("Test string", map.getObj("obj"));
            
            // Object value can be null:
            map.putObj("null-obj", null);
            assertTrue(map.contains("null-obj"));
            assertNull(map.getObj("null-obj"));
        }
        
        // Should be persistent:
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            assertEquals(3, (int) map.size());
            assertArrayEquals(in, map.get("one"));
            assertEquals("Test string", map.getObj("obj"));
        }
        
        // Should clear in case of version mismatch:
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "2")) {
            assertEquals(0, (int) map.size());
            assertNull(map.get("one"));
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IOException.class)
    public void testLocking() throws IOException {
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            try (PersistentMap map2 = new PersistentMap(ROOT_FOLDER, "1")) {
            }
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testListener() throws IOException {
        byte[] in = { 0, 1, 2, 3, 4 };
        
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            PropertyChangeListener pcl;
            map.addListener(pcl = new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    assertSame(map, evt.getSource());
                    assertNull(evt.getPropertyName());
                    assertNull(evt.getNewValue());
                }
            });
            map.clear();
            map.removeListener(pcl);
            
            map.addListener(pcl = new PropertyChangeListener() {
                @Override
                public void propertyChange(final PropertyChangeEvent evt) {
                    assertSame(map, evt.getSource());
                    assertEquals("one", evt.getPropertyName());
                    assertArrayEquals(in, (byte[]) evt.getNewValue());
                    assertArrayEquals(in, map.get("one"));
                }
            });
            map.put("one", in);
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test
    public void testCloseTolerant() throws IOException {
        PersistentMap map = new PersistentMap(ROOT_FOLDER, "1");
        map.put("one", new byte[0]);
        
        assertFalse(map.isClosed());
        
        map.close();
        
        assertTrue(map.isClosed());
        
        assertNull(map.size());
        assertNull(map.get("one"));
        assertNull(map.contains("one"));
        assertNull(map.getObj("obj"));
        
        // Calling the following methods should not throw an exception
        map.put("one", new byte[0]);
        map.putObj("obj", "Test string");
        map.clear();
        map.close();
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetNullKey() throws IOException {
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            map.get(null);
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetObjNullKey() throws IOException {
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            map.getObj(null);
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutNullKey() throws IOException {
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            map.put(null, new byte[0]);
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutNullValue() throws IOException {
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            map.put("one", null);
        }
    }
    
    /**
     * @throws IOException a
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutObjNullKey() throws IOException {
        try (PersistentMap map = new PersistentMap(ROOT_FOLDER, "1")) {
            map.putObj(null, "Test string");
        }
    }
    
}
