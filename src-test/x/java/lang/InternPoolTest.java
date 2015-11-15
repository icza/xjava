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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link InternPool}.
 * 
 * @author Andras Belicza
 */
public class InternPoolTest extends BaseTest {
    
    /** */
    private InternPool<TestInternable> pool;
    
    /**
     * @throws Exception a
     */
    @Before
    public void setUp() throws Exception {
        pool = new InternPool<>();
    }
    
    /** */
    @Test
    public void testInternedState() {
        TestInternable ti = new TestInternable(0);
        assertFalse(ti.isInterned()); // Should not yet be marked as interned
        
        pool.intern(ti);
        
        assertTrue(ti.isInterned()); // Now should be marked as interned
    }
    
    /** */
    @Test
    public void testNonExisting() {
        TestInternable ti = new TestInternable(0);
        
        TestInternable ti2 = pool.intern(ti);
        
        assertSame(ti, ti2);
    }
    
    /** */
    @Test
    public void testExisting() {
        pool.intern(new TestInternable(0));
        
        TestInternable ti = new TestInternable(0);
        
        TestInternable ti2 = pool.intern(ti);
        
        assertNotSame(ti, ti2);
        
        assertTrue(ti2.isInterned()); // The returned interned should be "interned"
        assertFalse(ti.isInterned()); // ti should not be marked as interned
    }
    
    /** */
    @Test
    public void testDifferent() {
        TestInternable ti = new TestInternable(0);
        pool.intern(ti);
        
        TestInternable ti2 = new TestInternable(1);
        TestInternable ti3 = pool.intern(ti2);
        
        assertSame(ti2, ti3);
        assertNotEquals(ti, ti3);
    }
    
    /** */
    @Test
    public void testSameClassInternPool() {
        InternPool<?> pool1 = InternPool.getClassInternPool(TestInternable.class);
        InternPool<?> pool2 = InternPool.getClassInternPool(TestInternable.class);
        
        assertSame(pool1, pool2);
    }
    
    /** */
    @Test
    public void testDifferentClassInternPool() {
        class TestInternalbe2 extends TestInternable {
            public TestInternalbe2(int value) {
                super(value);
            }
        }
        
        InternPool<?> pool1 = InternPool.getClassInternPool(TestInternable.class);
        InternPool<?> pool2 = InternPool.getClassInternPool(TestInternalbe2.class);
        
        assertNotSame(pool1, pool2);
    }
    
}
