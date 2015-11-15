/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link StructView}.
 * 
 * @author Andras Belicza
 */
public class StructViewTest extends BaseTest {
    
    /** */
    private HashMap<String, Object> source;
    
    /** */
    private StructView sv;
    
    /**
     * @throws Exception a
     */
    @Before
    public void setUp() throws Exception {
        Map<String, Object> m = source = new HashMap<>();
        
        m.put("one", 1);
        m.put("two", "-2-");
        
        Map<String, Object> m2 = new HashMap<>();
        m2.put("31", 31);
        m2.put("32", "-32-");
        m.put("three", m2);
        
        sv = new StructView(m);
    }
    
    /** */
    @Test
    public void testGeneral() {
        assertEquals((Integer) 1, sv.get("one"));
        assertEquals("-2-", sv.get("two"));
        assertNull(sv.get("four"));
        
        assertEquals((Integer) 31, sv.get("three", "31"));
        assertEquals("-32-", sv.get("three", "32"));
        assertEquals("-32-", sv.get(new String[] { "three", "32" }));
        assertNull(sv.get("four", "32"));
        
        assertSame(source, sv.getStruct());
    }
    
    /** */
    @Test
    public void testEquals() {
        Map<String, Object> source2 = new HashMap<>(source);
        
        StructView sv2 = new StructView(source2);
        
        assertEquals(sv2, sv);
        
        source2.put("ten", 10);
        
        assertNotEquals(sv2, sv);
    }
    
    /** */
    @Test
    public void testSerialization() {
        testSerialization(sv);
    }
    
}
