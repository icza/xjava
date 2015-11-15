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

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of an intern pool for a specific {@link Internable} type.
 * 
 * @author Andras Belicza
 *
 * @param <T> type of the pool objects
 * 
 * @see Internable
 * @see BaseInternable
 */
public class InternPool<T extends Internable> {
    
    /** Internal map to store the interned/cached instances. */
    private final Map<T, T> map;
    
    /**
     * Creates a new {@link InternPool} with a default size hint.
     */
    public InternPool() {
        this(16);
    }
    
    /**
     * Creates a new {@link InternPool}.
     * 
     * @param sizeHint size hint for the number of different objects to be interned
     */
    public InternPool(final int sizeHint) {
        // Calculate initial capacity based on the default load factor
        // which is 0.75 = 3/4
        map = new HashMap<>(sizeHint / 3 * 4 + 2);
    }
    
    /**
     * Returns an interned/cached version of the specified entity.
     * 
     * <p>
     * If the entity is already interned/cached, the cached, shared instance is returned. Else it will first
     * be put into the internal pool, marked interned and then be returned.
     * </p>
     * 
     * @param entity entity to be interned
     * 
     * @return an interned/cached version of the specified entity
     */
    public T intern(final T entity) {
        if (entity.isInterned())
            return entity;
        
        final T cached = map.get(entity);
        if (cached != null)
            return cached;
        
        entity.markInterned();
        map.put(entity, entity);
        
        return entity;
    }
    
    /** A map of shared internal pools for each different {@link Class}es. */
    private static final Map<Class<? extends Internable>, InternPool<?>> CLASS_INTERN_POOL_MAP = new HashMap<>();
    
    /**
     * Returns the shared {@link InternPool} for the specified {@link Class}.
     * 
     * @param <T2> type whose intern pool to return
     * 
     * @param c {@link Class} to return the shared {@link InternPool} for
     * 
     * @return the shared {@link InternPool} for the specified {@link Class}
     */
    public static <T2 extends Internable> InternPool<T2> getClassInternPool(final Class<T2> c) {
        
        // Safe to suppress warning as this is the only method that adds entries to the map:
        @SuppressWarnings("unchecked")
        InternPool<T2> p = (InternPool<T2>) CLASS_INTERN_POOL_MAP.get(c);
        
        if (p == null)
            CLASS_INTERN_POOL_MAP.put(c, p = new InternPool<T2>());
        
        return p;
    }
    
}
