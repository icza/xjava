/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.text;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link StringUtils}.
 * 
 * @author Andras Belicza
 */
public class StringUtilsTest extends BaseTest {
    
    /** */
    @Test
    public void testToHexString() {
        byte[] in = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        
        assertEquals("000102030405060708090a0b0c0d0e0f10", StringUtils.toHexString(in));
        assertEquals("0f10", StringUtils.toHexString(in, 15, 2));
    }
    
    /** */
    @Test
    public void testHexToBytes() {
        byte[] expected = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        
        byte[] out = StringUtils.hexToBytes("000102030405060708090a0b0c0d0e0f10");
        
        assertArrayEquals(expected, out);
        
        out = StringUtils.hexToBytes("0 001 0    20304050  60 7 08090a0b0c0d0e0f10");
        
        assertArrayEquals(expected, out);
        
        // Test invalid hex digits:
        assertNull(StringUtils.hexToBytes("rt"));
        
        // Test odd hex string length:
        assertNull(StringUtils.hexToBytes("012"));
    }
    
    /**
     * @throws UnsupportedEncodingException a
     */
    @Test
    public void testToBase64String() throws UnsupportedEncodingException {
        byte[] data = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
        
        assertEquals("AAECAwQFBgcICQoLDA0ODxA=", StringUtils.toBase64String(data));
        
        String[] in = { "Sample text file with some data.", "a", "ab", "abc", "abcd" };
        String[] out = { "U2FtcGxlIHRleHQgZmlsZSB3aXRoIHNvbWUgZGF0YS4=", "YQ==", "YWI=", "YWJj", "YWJjZA==" };
        
        for (int i = 0; i < in.length; i++)
            assertEquals(out[i], StringUtils.toBase64String(in[i].getBytes("UTF-8")));
        
        byte[] inData = new byte[256];
        String outString = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8gISIjJCUmJygpKissLS4vMDEyMzQ1Njc4OTo7PD0+P0BBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWltcXV5fYGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6e3x9fn+AgYKDhIWGh4iJiouMjY6PkJGSk5SVlpeYmZqbnJ2en6ChoqOkpaanqKmqq6ytrq+wsbKztLW2t7i5uru8vb6/wMHCw8TFxsfIycrLzM3Oz9DR0tPU1dbX2Nna29zd3t/g4eLj5OXm5+jp6uvs7e7v8PHy8/T19vf4+fr7/P3+/w==";
        for (int i = 0; i < inData.length; i++)
            inData[i] = (byte) i;
        assertEquals(outString, StringUtils.toBase64String(inData));
    }
    
    /** */
    @Test
    public void testContainsIgnoreCase() {
        String[] srcs = { null, "", null, "xJava" };
        String[] whats = { null, null, "", " xJava" };
        
        for (int i = 0; i < srcs.length; i++)
            assertFalse(StringUtils.containsIgnoreCase(srcs[i], whats[i]));
        
        srcs = new String[] { "", "abc", "xJava", "xJava", "xJava", "xJava" };
        whats = new String[] { "", "", "xJ", "XJ", "jA", "xJava" };
        
        for (int i = 0; i < srcs.length; i++)
            assertTrue(StringUtils.containsIgnoreCase(srcs[i], whats[i]));
    }
    
    /** */
    @Test
    public void testRandomString() {
        String out = StringUtils.randomString(10);
        assertEquals(10, out.length());
    }
    
    /** */
    @Test
    public void testRandomString2() {
        // Random seed so we have reproducible random test results
        final Random r = new Random(4327);
        
        String[] out = { "", "S", "00", "-Xn", "ssON", "5EQfH", "uombu6", "2iDwt3O", "-PlW-Rhg", "icR2hk5W0" };
        
        for (int i = 0; i < 10; i++)
            assertEquals(out[i], StringUtils.randomString(i, r));
    }
    
}
