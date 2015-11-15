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
 * A pair of generic-type objects.
 * 
 * <p>
 * Implementation is immutable if the type parameters are immutable.
 * </p>
 * 
 * @param <T1> type of the first object
 * @param <T2> type of the second object
 * 
 * @author Andras Belicza
 */
public class Pair<T1, T2> implements Cloneable, Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** The first object. */
    public final T1 value1;
    
    /** The second object. */
    public final T2 value2;
    
    /**
     * Creates a new {@link Pair}.
     * 
     * @param value1 the first object
     * @param value2 the second object
     */
    public Pair(final T1 value1, final T2 value2) {
        this.value1 = value1;
        this.value2 = value2;
    }
    
    /**
     * Returns the first object.
     * 
     * @return the first object
     */
    public T1 getValue1() {
        return value1;
    }
    
    /**
     * Returns the second object.
     * 
     * @return the second object
     */
    public T2 getValue2() {
        return value2;
    }
    
    /**
     * Returns <code>true</code> if both values of the pairs in this object and in the other one are equal.
     * 
     * <p>
     * If a value is <code>null</code>, then the value in the other pair must also be <code>null</code> in
     * order to be equal.
     * </p>
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Pair))
            return false;
        
        final Pair<?, ?> pair2 = (Pair<?, ?>) o;
        
        return Objects.equals(value1, pair2.value1) && Objects.equals(value2, pair2.value2);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value1, value2);
    }
    
    @Override
    public Pair<T1, T2> clone() {
        try {
            @SuppressWarnings("unchecked")
            final Pair<T1, T2> cloned = (Pair<T1, T2>) super.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            // Never to happen as we implement Cloneable
            throw new InternalError(e);
        }
    }
    
}
