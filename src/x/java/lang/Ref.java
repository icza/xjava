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
 * This is a simple modifiable wrapper class for a generic type.
 * 
 * @author Andras Belicza
 *
 * @param <T> type of the wrapped value
 * 
 * @see FinalRef
 */
public class Ref<T> implements Cloneable, Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** Reference to the wrapped value. */
    public T value;
    
    /**
     * Creates a new {@link Ref}. Default no-arg constructor.
     */
    public Ref() {
    }
    
    /**
     * Creates a new {@link Ref} with the specified initial value.
     * 
     * @param value initial value
     */
    public Ref(final T value) {
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
     * Sets the wrapped value.
     * 
     * @param value the wrapped value to be set
     */
    public void setValue(final T value) {
        this.value = value;
    }
    
    /**
     * Returns <code>true</code> if both wrappers have the same value.
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Ref))
            return false;
        
        return Objects.equals(value, ((Ref<?>) o).value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    /**
     * Returns a clone with the same referenced value.
     */
    @Override
    public Ref<T> clone() {
        try {
            @SuppressWarnings("unchecked")
            final Ref<T> cloned = (Ref<T>) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            // Never to happen as we implement Cloneable
            throw new InternalError(e);
        }
    }
    
}
