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

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link CollUtils}.
 * 
 * @author Andras Belicza
 */
public class CollUtilsTest extends BaseTest {
    
    /** */
    @Test
    public void testNullAwareComparator() {
        Comparator<String> c = CollUtils.nullAwareComparator(String.CASE_INSENSITIVE_ORDER);
        
        String[] in = { "a", "b", null, "c", "abc", null };
        String[] out = { null, null, "a", "abc", "b", "c" };
        
        Arrays.sort(in, c);
        
        assertArrayEquals(out, in);
    }
    
}
