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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link MathUtils}.
 * 
 * @author Andras Belicza
 */
public class MathUtilsTest extends BaseTest {
    
    /** */
    @Test
    public void testRandom() {
        Random r = new Random();
        
        for (int i = -5; i <= 5; i += 5)
            assertEquals(i, MathUtils.random(i, i, r));
        
        for (int i = 0; i < 100; i++) {
            int min = r.nextInt(10);
            int max = min + r.nextInt(10);
            
            int n = MathUtils.random(min, max, r);
            assertTrue(n >= min);
            assertTrue(n <= max);
        }
    }
    
    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testRandomException() {
        MathUtils.random(2, 1, new Random());
    }
    
    /** */
    @Test
    public void testIRandomValue() {
        assertNull(MathUtils.randomValue(new String[0], new double[0]));
        
        // We cannot control the private Random inside the java.lang.Math.
        
        String[] in = { "0", "1", "2" };
        double[] weights = { 0.2, 0.8, 0 };
        
        for (int i = 0; i < 100; i++) {
            String value = MathUtils.randomValue(in, weights);
            switch (value) {
            case "0":
            case "1":
                break;
            case "2":
            default:
                fail();
            }
        }
        
        weights = new double[] { 0.2, 0, 0.8 };
        
        for (int i = 0; i < 100; i++) {
            String value = MathUtils.randomValue(in, weights);
            switch (value) {
            case "0":
            case "2":
                break;
            case "3":
            default:
                fail();
            }
        }
        
        weights = new double[] { 0.5 };
        
        for (int i = 0; i < 100; i++) {
            String value = MathUtils.randomValue(in, weights);
            if (value == null)
                value = "null";
            switch (value) {
            case "0":
            case "null":
                break;
            case "1":
            case "2":
            default:
                fail();
            }
        }
        
        weights = new double[] { 0 };
        
        for (int i = 0; i < 100; i++) {
            String value = MathUtils.randomValue(in, weights);
            if (value == null)
                value = "null";
            switch (value) {
            case "null":
                break;
            case "0":
            case "1":
            case "2":
            default:
                fail();
            }
        }
    }
    
    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testIRandomValueException() {
        assertNull(MathUtils.randomValue(new String[0], new double[1]));
    }
    
    /** */
    @Test
    public void testDRandomValue() {
        // Random seed so we have reproducible random test results
        final Random r = new Random(4327);
        
        assertNull(MathUtils.randomValue(new Integer[0], new int[0], r));
        assertNull(MathUtils.randomValue(new Integer[] { 0 }, new int[] { 0 }, r));
        assertNull(MathUtils.randomValue(new Integer[] { 0, 1 }, new int[] { 0, 0 }, r));
        
        Integer[] in = { 0, 1, 2 };
        int[] weights = { 2, 8, 0 };
        int[] out = { 15, 85, 0 };
        
        int[] counts = new int[in.length];
        for (int i = 0; i < 100; i++)
            counts[MathUtils.randomValue(in, weights, r)]++;
        assertArrayEquals(out, counts);
        
        weights = new int[] { 2, 0, 8 };
        out = new int[] { 24, 0, 76 };
        
        counts = new int[in.length];
        for (int i = 0; i < 100; i++)
            counts[MathUtils.randomValue(in, weights, r)]++;
        assertArrayEquals(out, counts);
        
        weights = new int[] { 1 };
        out = new int[] { 100, 0, 0 };
        
        counts = new int[in.length];
        for (int i = 0; i < 100; i++)
            counts[MathUtils.randomValue(in, weights, r)]++;
        assertArrayEquals(out, counts);
    }
    
    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testDRandomValueException() {
        assertNull(MathUtils.randomValue(new String[0], new int[1], new Random()));
    }
    
    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testDRandomValueException2() {
        assertNull(MathUtils.randomValue(new String[1], new int[] { -1 }, new Random()));
    }
    
}
