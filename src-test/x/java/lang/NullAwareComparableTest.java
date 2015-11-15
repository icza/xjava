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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link NullAwareComparable}.
 * 
 * @author Andras Belicza
 */
public class NullAwareComparableTest extends BaseTest {
    
    /** */
    @Test
    public void testCompareTo() {
        Integer[] in = { 0, null, 1, 3, null };
        Integer[] out = { null, null, 0, 1, 3 };
        
        List<NullAwareComparable<Integer>> list = new ArrayList<>();
        
        for (int i = 0; i < in.length; i++)
            list.add(new NullAwareComparable<Integer>(in[i]));
        
        Collections.sort(list);
        
        for (int i = 0; i < in.length; i++)
            assertEquals(out[i], list.get(i).value);
    }
    
    /** */
    @Test
    public void testToString() {
        assertEquals("", new NullAwareComparable<Integer>(null).toString());
        assertEquals("1", new NullAwareComparable<Integer>(Integer.valueOf(1)).toString());
    }
    
    /** */
    @Test
    public void testGetPercent() {
        assertEquals("-", NullAwareComparable.getPercent(null).toString());
        assertEquals("0.00%", NullAwareComparable.getPercent(0.0).toString());
        assertEquals("10.00%", NullAwareComparable.getPercent(10.0).toString());
        assertEquals("15.12%", NullAwareComparable.getPercent(15.123).toString());
    }
    
    /** */
    @Test
    public void testSerialization() {
        testSerialization(new NullAwareComparable<>("one"));
    }
    
}
