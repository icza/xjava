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
 * A modifiable <code>int</code> wrapper.
 * 
 * <p>
 * Useful for example when:
 * </p>
 * <ul>
 * <li>A counter is to be stored in a {@link Map}; so no new {@link Integer}s have to be allocated and put
 * back when counter value is changed.
 * <li>An anonymous class needs to refer to / change a counter defined outside of it (typical use case when
 * counting files with {@link Files#walkFileTree(java.nio.file.Path, java.nio.file.FileVisitor)}).
 * </ul>
 * 
 * @author Andras Belicza
 * 
 * @see LongInt
 */
public class Int implements Comparable<Int>, Cloneable, Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** The wrapped int value. */
    public int value;
    
    /**
     * Creates a new {@link Int}.
     */
    public Int() {
    }
    
    /**
     * Creates a new {@link Int}.
     * 
     * @param value initial value
     */
    public Int(final int value) {
        this.value = value;
    }
    
    /**
     * Returns the wrapped <code>int</code> value.
     * 
     * @return the wrapped <code>int</code> value
     */
    public int get() {
        return value;
    }
    
    /**
     * Sets the wrapped <code>int</code> value.
     * 
     * @param value the wrapped <code>int</code> value to be set
     */
    public void set(final int value) {
        this.value = value;
    }
    
    /**
     * Increments the wrapped <code>int</code> value by <code>1</code> and returns the new value.
     * 
     * @return the new value after the increment
     */
    public int incAndGet() {
        return ++value;
    }
    
    /**
     * Adds the specified amount to the wrapped <code>int</code> value and returns the new value.
     * 
     * @param amount to be added to the the wrapped <code>int</code> value
     * 
     * @return the new value after adding the specified amount
     */
    public int addAndGet(final int amount) {
        return value += amount;
    }
    
    /**
     * Returns <code>true</code> if both int wrappers have the same value.
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Int))
            return false;
        
        return value == ((Int) o).value;
    }
    
    @Override
    public int hashCode() {
        return value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(value);
    }
    
    @Override
    public int compareTo(final Int i) {
        return Integer.compare(value, i.value);
    }
    
    @Override
    public Int clone() {
        try {
            return (Int) super.clone();
        } catch (CloneNotSupportedException e) {
            // Never to happen as we implement Cloneable
            throw new InternalError(e);
        }
    }
    
}