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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link BaseInternable}.
 * 
 * @author Andras Belicza
 */
public class BaseInternableTest extends BaseTest {
    
    /** */
    private TestInternable ti;
    
    /**
     * @throws Exception s
     */
    @Before
    public void setUp() throws Exception {
        ti = new TestInternable(0);
    }
    
    /** */
    @Test
    public void testHashCodeCaching() {
        assertEquals(0, ti.customEqualsCount); // Should be 0 for now
        
        ti.hashCode();
        
        assertEquals(1, ti.customHashCodeCount); // Should be 1 now
        
        ti.hashCode();
        
        assertEquals(1, ti.customHashCodeCount); // Still should be 1
    }
    
    /** */
    @Test
    public void testUninternedEquals() {
        TestInternable ti2 = new TestInternable(0);
        
        ti.equals(ti2);
        
        assertEquals(1, ti.customHashCodeCount);
        assertEquals(1, ti2.customHashCodeCount);
        // Custom hash code should be the same, so customEquals() should also be called:
        assertEquals(1, ti.customEqualsCount);
        // But only on ti, not on ti2:
        assertEquals(0, ti2.customEqualsCount);
    }
    
    /** */
    @Test
    public void testUninternedNotEquals() {
        TestInternable ti2 = new TestInternable(1);
        
        ti.equals(ti2);
        
        assertEquals(1, ti.customHashCodeCount);
        assertEquals(1, ti2.customHashCodeCount);
        // Custom hash code decided, so no customEquals() should be called:
        assertEquals(0, ti.customEqualsCount);
        assertEquals(0, ti2.customEqualsCount);
    }
    
    /** */
    @Test
    public void testMarkInterned() {
        assertFalse(ti.isInterned()); // Should not yet be marked as interned
        
        ti.markInterned();
        
        assertTrue(ti.isInterned()); // Now should be marked as interned
    }
    
    /** */
    @Test
    public void testInternedState() {
        assertFalse(ti.isInterned()); // Should not yet be marked as interned
        
        ti.intern();
        
        assertTrue(ti.isInterned()); // Now should be marked as interned
    }
    
    /** */
    @Test
    public void testIntern() {
        // Ensure ti has an equal in the pool:
        new TestInternable(0).intern();
        
        TestInternable ti2 = ti.intern();
        
        assertNotSame(ti, ti2);
        assertEquals(ti, ti2);
        
        assertTrue(ti2.isInterned()); // The returned interned should be "interned"
        assertFalse(ti.isInterned()); // ti should not be marked as interned
    }
    
}
