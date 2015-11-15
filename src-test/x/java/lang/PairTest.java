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

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link Pair}.
 * 
 * @author Andras Belicza
 */
public class PairTest extends BaseTest {
    
    /** */
    @Test
    public void testEquals() {
        assertEquals(new Pair<>("one", 1), new Pair<>("one", 1));
        assertEquals(new Pair<>("one", null), new Pair<>("one", null));
        assertEquals(new Pair<>(null, null), new Pair<>(null, null));
        
        assertNotEquals(new Pair<>("one", 1), new Pair<>("one", 2));
        assertNotEquals(new Pair<>("one", 1), new Pair<>("two", 1));
        assertNotEquals(new Pair<>("one", 1), new Pair<>("one", null));
        assertNotEquals(new Pair<>(null, 1), new Pair<>("one", null));
        assertNotEquals(new Pair<>(null, null), new Pair<>("one", 1));
    }
    
    /** */
    @Test
    public void testClone() {
        testClone(new Pair<>("one", 1));
    }
    
    /** */
    @Test
    public void testSerialization() {
        testSerialization(new Pair<>("one", 1));
    }
    
}
