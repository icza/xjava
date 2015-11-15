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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link OneValueSet}.
 * 
 * @author Andras Belicza
 */
public class OneValueSetTest extends BaseTest {
    
    /** */
    private OneValueSet<String> set;
    
    /**
     * @throws Exception a
     */
    @Before
    public void setUp() throws Exception {
        set = new OneValueSet<>("one");
    }
    
    /** */
    @Test
    public void testGeneral() {
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        
        assertTrue(set.contains("one"));
        assertTrue(set.contains(new String("one")));
        assertFalse(set.contains(new String("two")));
    }
    
    /** */
    @Test
    public void testIterator() {
        int counter = 0;
        for (Iterator<String> i = set.iterator(); i.hasNext();) {
            counter++;
            assertSame("one", i.next());
        }
        assertEquals(1, counter);
    }
    
    /** */
    @Test(expected = NoSuchElementException.class)
    public void testIteratorNSEE() {
        Iterator<String> it = set.iterator();
        it.next();
        it.next();
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove() {
        Iterator<String> it = set.iterator();
        it.next();
        it.remove();
    }
    
    /** */
    @Test
    public void testToObjectArray() {
        Object[] array = set.toArray();
        
        assertEquals(1, array.length);
        assertArrayEquals(new Object[] { "one" }, array);
    }
    
    /** */
    @Test
    public void testToEmptyStringArray() {
        String[] array = new String[0];
        
        String[] array2 = set.toArray(array);
        
        assertNotSame(array, array2);
        assertArrayEquals(new Object[] { "one" }, array2);
    }
    
    /** */
    @Test
    public void testToExactStringArray() {
        String[] array = new String[1];
        
        String[] array2 = set.toArray(array);
        
        assertSame(array, array2);
        assertArrayEquals(new Object[] { "one" }, array2);
    }
    
    /** */
    @Test
    public void testToBigStringArray() {
        String[] array = { "1", "2", "3" };
        
        String[] array2 = set.toArray(array);
        
        assertSame(array, array2);
        assertArrayEquals(new Object[] { "one", null, "3" }, array2);
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testAdd() {
        set.add("two");
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        set.remove("two");
    }
    
    /** */
    @Test
    public void testContainsAll() {
        assertTrue(set.containsAll(Arrays.asList()));
        assertTrue(set.containsAll(Arrays.asList("one")));
        assertTrue(set.containsAll(Arrays.asList("one", "one")));
        
        assertFalse(set.containsAll(Arrays.asList("two")));
        assertFalse(set.containsAll(Arrays.asList("one", "two")));
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testAddAll() {
        set.addAll(Arrays.asList("two"));
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testRetainAll() {
        set.retainAll(Arrays.asList("two"));
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll() {
        set.removeAll(Arrays.asList("two"));
    }
    
    /** */
    @Test(expected = UnsupportedOperationException.class)
    public void testClear() {
        set.clear();
    }
    
    /** */
    @Test
    public void testEquals() {
        assertEquals(set, new OneValueSet<>("one"));
        assertEquals(set, new OneValueSet<>(new String("one")));
        assertNotEquals(set, new OneValueSet<>("two"));
    }
    
    /** */
    @Test
    public void testClone() {
        testClone(set);
    }
    
    /** */
    @Test
    public void testSerialization() {
        testSerialization(set);
    }
    
}
