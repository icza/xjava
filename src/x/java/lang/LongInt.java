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

import java.io.Serializable;
import java.nio.file.Files;
import java.util.Map;

/**
 * A modifiable <code>long</code> wrapper.
 * 
 * <p>
 * Useful for example when:
 * </p>
 * <ul>
 * <li>A counter is to be stored in a {@link Map}; so no new {@link Long}s have to be allocated and put back
 * when counter value is changed.
 * <li>An anonymous class needs to refer to / change a <code>long</code> counter defined outside of it
 * (typical use case when counting file size with
 * {@link Files#walkFileTree(java.nio.file.Path, java.nio.file.FileVisitor)}).
 * </ul>
 * 
 * @author Andras Belicza
 * 
 * @see Int
 */
public class LongInt implements Comparable<LongInt>, Cloneable, Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** The wrapped long value. */
    public long value;
    
    /**
     * Creates a new {@link LongInt}.
     */
    public LongInt() {
    }
    
    /**
     * Creates a new {@link LongInt}.
     * 
     * @param value initial value
     */
    public LongInt(final long value) {
        this.value = value;
    }
    
    /**
     * Returns the wrapped <code>long</code> value.
     * 
     * @return the wrapped <code>long</code> value
     */
    public long get() {
        return value;
    }
    
    /**
     * Sets the wrapped <code>long</code> value.
     * 
     * @param value the wrapped <code>long</code> value to be set
     */
    public void set(final long value) {
        this.value = value;
    }
    
    /**
     * Increments the wrapped <code>long</code> value by <code>1</code> and returns the new value.
     * 
     * @return the new value after the increment
     */
    public long incAndGet() {
        return ++value;
    }
    
    /**
     * Adds the specified amount to the wrapped <code>long</code> value and returns the new value.
     * 
     * @param amount to be added to the the wrapped <code>long</code> value
     * 
     * @return the new value after adding the specified amount
     */
    public long addAndGet(final long amount) {
        return value += amount;
    }
    
    /**
     * Returns <code>true</code> if both long wrappers have the same value.
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof LongInt))
            return false;
        
        return value == ((LongInt) o).value;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public String toString() {
        return Long.toString(value);
    }
    
    @Override
    public int compareTo(final LongInt i) {
        return Long.compare(value, i.value);
    }
    
    @Override
    public LongInt clone() {
        try {
            return (LongInt) super.clone();
        } catch (CloneNotSupportedException e) {
            // Never to happen as we implement Cloneable
            throw new InternalError(e);
        }
    }
    
}