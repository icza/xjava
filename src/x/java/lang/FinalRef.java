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
import java.util.Objects;

/**
 * This is a simple non-modifiable wrapper class for a generic type.
 * 
 * @author Andras Belicza
 *
 * @param <T> type of the wrapped value
 * 
 * @see Ref
 */
public class FinalRef<T> implements Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** Reference to the wrapped value. */
    public final T value;
    
    /**
     * Creates a new {@link FinalRef} with the specified value.
     * 
     * @param value value to be wrapped
     */
    public FinalRef(final T value) {
        this.value = value;
    }
    
    /**
     * Returns the wrapped value.
     * 
     * @return the wrapped value
     */
    public T getValue() {
        return value;
    }
    
    /**
     * Returns <code>true</code> if both wrappers have the same value.
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FinalRef))
            return false;
        
        return Objects.equals(value, ((FinalRef<?>) o).value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
}
