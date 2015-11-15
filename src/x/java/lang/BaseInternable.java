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

/**
 * A very efficient base implementation of {@link Internable}.
 * 
 * <p>
 * The implementation overrides {@link Object#equals(Object)} and {@link Object#hashCode()} as
 * <code>final</code> so these implementations cannot be changed, {@link #customEquals(BaseInternable)} and
 * {@link #customHashCode()} are provided for the functionality.
 * </p>
 * 
 * @param <T> the dynamic type of object
 * 
 * @author Andras Belicza
 * 
 * @see Internable
 * @see InternPool
 */
public abstract class BaseInternable<T extends BaseInternable<T>> implements Internable {
    
    /**
     * Returns an interned/cached version of this {@link BaseInternable}.
     * 
     * @return an interned/cached version of this {@link BaseInternable}
     */
    public T intern() {
        @SuppressWarnings("unchecked")
        final T interned = InternPool.getClassInternPool((Class<T>) getClass()).intern((T) this);
        return interned;
    }
    
    /** Stores the interned state of the instance. */
    private transient boolean interned;
    
    @Override
    public boolean isInterned() {
        return interned;
    }
    
    @Override
    public void markInterned() {
        interned = true;
    }
    
    /**
     * This method throws {@link CloneNotSupportedException}.<br>
     * Internable instances must be immutable and no need to clone them. On the contrary: interned instances
     * should be used/enforced!
     */
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("BaseInternable must be immutable and no need to clone them."
                + " On the contrary: interned instances should be used/enforced!");
    }
    
    /** Cached hash code. */
    private int cachedHashCode = -1;
    
    /**
     * Tests whether this object is equal to the specified other object.
     * 
     * <p>
     * Takes advantage of being a <i>self-aware</i> {@link Internable} and if possible, equality is decided
     * based on the interned state.<br>
     * If interned state is not sufficient to decide equality, first a hashcode check is performed because
     * hash code returned by {@link #customHashCode()} is cached.<br>
     * Only if necessary will {@link #customEquals(BaseInternable)} be called.
     * </p>
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        
        final BaseInternable<?> other = (BaseInternable<?>) obj;
        if (interned && other.interned) {
            // Both interned, and this != obj => not equal
            return false;
        }
        
        // At least one of them is not interned.
        // First do a fast check on hash codes (which is/will be cached).
        if (hashCode() != other.hashCode())
            return false;
        
        // At this point we have to perform a "full" equality check:
        @SuppressWarnings("unchecked")
        final T otherKey = (T) other;
        return customEquals(otherKey);
    }
    
    @Override
    public final int hashCode() {
        if (cachedHashCode != -1)
            return cachedHashCode;
        
        // We only go down once on this path.
        
        // If by a once-in-a-billion chance custom hash code would be -1, make sure it isn't.
        if ((cachedHashCode = customHashCode()) == -1)
            cachedHashCode = 4327; // My magic number
            
        return cachedHashCode;
    }
    
    /**
     * Returns a "custom" hash code based on the attributes of the subclass.
     * 
     * <p>
     * The returned hash code will be cached and this method will never be called again.
     * </p>
     * 
     * @return a "custom" hash code based on the attributes of the subclass
     */
    protected abstract int customHashCode();
    
    /**
     * Performs a "custom" equality check on the attributes of the subclass.
     * 
     * <p>
     * This method is only called if the dynamic type of <code>obj</code> matches our dynamic type (this
     * implies <code>obj</code> cannot be <code>null</code>).
     * </p>
     * 
     * @param obj the other key object to check equality with
     * 
     * @return <code>true</code> if the 2 instances are considered equal, <code>false</code> otherwise
     */
    protected abstract boolean customEquals(T obj);
    
}
