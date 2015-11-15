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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link ArrayMap}.
 * 
 * @author Andras Belicza
 */
public class ArrayMapTest extends BaseTest {
    
    /** */
    private ArrayMap<String, Integer> map;
    
    /**
     * @throws Exception a
     */
    @Before
    public void setUp() throws Exception {
        map = new ArrayMap<>();
    }
    
    /** */
    @Test
    public void testGeneral() {
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        
        map.put("1", 1);
        
        assertFalse(map.isEmpty());
        assertEquals(1, map.size());
        
        assertTrue(map.containsKey("1"));
        assertTrue(map.containsValue(1));
        assertFalse(map.containsKey("2"));
        assertEquals((Integer) 1, map.get("1"));
        assertEquals("1", map.getKeyByValue(1));
        
        map.entrySet().iterator().next().setValue(11);
        assertEquals((Integer) 11, map.get("1"));
        assertTrue(map.containsValue(11));
        
        map.remove("1");
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        
        ArrayMap<String, Integer> map2 = new ArrayMap<>();
        map2.put("1", 1);
        map2.put("2", 2);
        map2.put("3", 3);
        
        map.putAll(map2);
        
        assertEquals(map, map2);
        
        map2 = new ArrayMap<>(map);
        
        assertEquals(map, map2);
        
        HashMap<String, Integer> map3 = new HashMap<>(map);
        
        assertEquals(map, map3);
        assertEquals(map3, map);
        
        map.clear();
        
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
        
        map.put("1", 1);
        map.put("2", 1);
        map.put("3", 2);
        map.put("4", 2);
        
        // First by insertion order
        assertEquals("1", map.getKeyByValue(1));
        assertEquals("3", map.getKeyByValue(2));
    }
    
    /** */
    @Test
    public void testIterator() {
        String[] in = { "0", "1", "2", "3", "4" };
        
        for (int i = 0; i < in.length; i++)
            map.put(in[i], i);
        
        // ArrayMap preserves insertion order
        int counter = 0;
        for (Entry<String, Integer> entry : map.entrySet()) {
            assertEquals(Integer.toString(counter), entry.getKey());
            assertEquals((Integer) counter, entry.getValue());
            counter++;
        }
        assertEquals(in.length, counter);
    }
    
    /** */
    @Test
    public void testClone() {
        testClone(map);
        
        for (int i = 0; i < 100; i++)
            map.put(Integer.toString(i), i);
        
        testClone(map);
    }
    
    /** */
    @Test
    public void testSerialization() {
        map.put("1", 1);
        map.put("2", 2);
        testSerialization(map);
    }
    
}
