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

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link SizeFormat}.
 * 
 * @author Andras Belicza
 */
public class SizeFormatTest extends BaseTest {
    
    /**
     * @throws Exception a
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Locale.setDefault(Locale.US);
    }
    
    /** */
    private static long[] IN = { 0L, 100L, 1_000L, 2_000L, 1_000_000L, 1_000_000_000L, 1_000_000_000_000L,
            1_000_000_000_000_000L };
    
    /** */
    @Test
    public void testBYTE() {
        String[] out = { "0 Bytes", "100 Bytes", "1,000 Bytes", "2,000 Bytes", "1,000,000 Bytes",
                "1,000,000,000 Bytes", "1,000,000,000,000 Bytes", "1,000,000,000,000,000 Bytes" };
        
        for (int i = 0; i < IN.length; i++)
            assertEquals(out[i], SizeFormat.BYTES.format(IN[i], 0));
        
        // Also test no fraction part:
        assertEquals("0 Bytes", SizeFormat.BYTES.format(0, 1));
    }
    
    /** */
    @Test
    public void testFraction() {
        String[] out = { "0 KB", "0.0 KB", "0.00 KB" };
        
        for (int i = 0; i < out.length; i++)
            assertEquals(out[i], SizeFormat.KB.format(0, i));
    }
    
    /** */
    @Test
    public void testKB() {
        String[] out = { "0.00 KB", "0.10 KB", "0.98 KB", "1.95 KB", "976.56 KB", "976,562.50 KB",
                "976,562,500.00 KB", "976,562,500,000.00 KB" };
        
        for (int i = 0; i < IN.length; i++)
            assertEquals(out[i], SizeFormat.KB.format(IN[i], 2));
    }
    
    /** */
    @Test
    public void testMB() {
        String[] out = { "0.00 MB", "0.00 MB", "0.00 MB", "0.00 MB", "0.95 MB", "953.67 MB", "953,674.32 MB",
                "953,674,316.41 MB" };
        
        for (int i = 0; i < IN.length; i++)
            assertEquals(out[i], SizeFormat.MB.format(IN[i], 2));
    }
    
    /** */
    @Test
    public void testGB() {
        String[] out = { "0.00 GB", "0.00 GB", "0.00 GB", "0.00 GB", "0.00 GB", "0.93 GB", "931.32 GB",
                "931,322.57 GB" };
        
        for (int i = 0; i < IN.length; i++)
            assertEquals(out[i], SizeFormat.GB.format(IN[i], 2));
    }
    
    /** */
    @Test
    public void testTB() {
        String[] out = { "0.00 TB", "0.00 TB", "0.00 TB", "0.00 TB", "0.00 TB", "0.00 TB", "0.91 TB",
                "909.49 TB" };
        
        for (int i = 0; i < IN.length; i++)
            assertEquals(out[i], SizeFormat.TB.format(IN[i], 2));
    }
    
    /** */
    @Test
    public void testAUTO() {
        String[] out = { "0 Bytes", "100 Bytes", "0.98 KB", "1.95 KB", "976.56 KB", "953.67 MB", "931.32 GB",
                "909.49 TB" };
        
        for (int i = 0; i < IN.length; i++)
            assertEquals(out[i], SizeFormat.AUTO.format(IN[i], 2));
    }
    
}
