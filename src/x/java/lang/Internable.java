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
 * Interface of a <i>self-aware</i> internable object.
 * 
 * <p>
 * Internable objects must be immutable.
 * </p>
 * 
 * <p>
 * Methods defined in this interface are intended for the {@link InternPool} class.
 * </p>
 * 
 * @author Andras Belicza
 * 
 * @see InternPool
 * @see BaseInternable
 */
public interface Internable {
    
    /**
     * Tells if the instance is an interned instance (is from the intern pool).
     * 
     * @return <code>true</code> if the instance is interned (is from the pool), <code>false</code> otherwise
     */
    boolean isInterned();
    
    /**
     * Marks the current instance interned (that it is placed in the intern pool).
     */
    void markInterned();
    
}
