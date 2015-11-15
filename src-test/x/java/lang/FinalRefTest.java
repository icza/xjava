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
 * JUnit test of {@link FinalRef}.
 * 
 * @author Andras Belicza
 */
public class FinalRefTest extends BaseTest {
    
    /** */
    @Test
    public void testEquals() {
        assertEquals(new FinalRef<>("1"), new FinalRef<>("1"));
        
        assertNotEquals(new FinalRef<>("1"), new FinalRef<>("2"));
    }
    
    /** */
    @Test
    public void testSerialization() {
        testSerialization(new FinalRef<>("1"));
    }
    
}
