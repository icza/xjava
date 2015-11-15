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

import java.text.DecimalFormat;

/**
 * Representation formats of a size (capacity) value.
 * 
 * <p>
 * This implementation ({@link #format(long, int)}) is thread-safe.
 * </p>
 * 
 * @author Andras Belicza
 */
public enum SizeFormat {
    
    /** Auto, depends on size value. */
    AUTO("<Auto>", -1L, -1.0),
    
    /** Bytes. */
    BYTES("Bytes", 999L, 1.0),
    
    /** KB. */
    KB("KB", 1_024_000L, 1_024.0),
    
    /** MB. */
    MB("MB", 1_073_741_824L, 1_048_576.0),
    
    /** GB. */
    GB("GB", 1_099_511_627_776L, 1_073_741_824.0),
    
    /** TB. */
    TB("TB", Long.MAX_VALUE, 1_099_511_627_776.0);
    
    /** Text value of the size format. */
    public final String text;
    
    /** Max limit in bytes to qualify for this format in case of {@link #AUTO}. */
    public final long autoByteLimit;
    
    /** Divider to divide a size value with given in bytes to get value in this size format. */
    public final double divider;
    
    /** String representation of the size format prepended with a space. */
    private final String preSpaceString;
    
    /**
     * Creates a new {@link SizeFormat}.
     * 
     * @param text text value of the size format
     * @param autoByteLimit max limit in bytes to qualify for this format in case of {@link #AUTO}
     * @param divider Divider to divide a size value with given in bytes to get in this size format
     */
    private SizeFormat(final String text, final long autoByteLimit, final double divider) {
        this.text = text;
        this.autoByteLimit = autoByteLimit;
        this.divider = divider;
        preSpaceString = " " + text;
    }
    
    @Override
    public String toString() {
        return text;
    }
    
    /**
     * Returns a formatted size value.
     * 
     * @param size size to be formatted
     * @param fractionDigits number of fraction digits if output is not in bytes
     * 
     * @return the formatted size value
     */
    public String format(final long size, final int fractionDigits) {
        SizeFormat sf = this;
        if (sf == AUTO) {
            for (SizeFormat SF : NON_AUTOS)
                if (size <= SF.autoByteLimit) {
                    sf = SF;
                    break;
                }
        }
        
        final DecimalFormat df = new DecimalFormat();
        if (sf == BYTES)
            return df.format(size) + BYTES.preSpaceString;
        
        df.setMinimumFractionDigits(fractionDigits);
        df.setMaximumFractionDigits(fractionDigits);
        
        return df.format(size / sf.divider) + sf.preSpaceString;
    }
    
    
    /** Cached array of the non-auto size formats. */
    private static final SizeFormat[] NON_AUTOS = { BYTES, KB, MB, GB, TB };
    
}
