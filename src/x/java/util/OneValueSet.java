/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A super-fast, unmodifiable {@link Set} implementation holding exactly 1 value.<br>
 * Thread safe, of course :)
 * 
 * @param <E> type of the element
 * 
 * @author Andras Belicza
 */
public class OneValueSet<E> implements Set<E>, Cloneable, Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** The one and only element. */
    private final E value;
    
    /**
     * Creates a new {@link OneValueSet}.
     * 
     * @param value the one and only value
     */
    public OneValueSet(final E value) {
        this.value = value;
    }
    
    @Override
    public int size() {
        return 1;
    }
    
    @Override
    public boolean isEmpty() {
        return false;
    }
    
    @Override
    public boolean contains(final Object o) {
        return value.equals(o);
    }
    
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            boolean notReturned = true; // Tells if the one and only element has not yet been returned
            
            @Override
            public boolean hasNext() {
                return notReturned;
            }
            
            @Override
            public E next() {
                if (notReturned) {
                    notReturned = false;
                    return value;
                }
                throw new NoSuchElementException();
            }
            
            /**
             * Throws {@link UnsupportedOperationException}.
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public Object[] toArray() {
        return new Object[] { value };
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        @SuppressWarnings("unchecked")
        final T[] result = a.length > 0 ? a : (T[]) java.lang.reflect.Array.newInstance(a.getClass()
                .getComponentType(), 1);
        
        @SuppressWarnings("unchecked")
        final T typedValue = (T) value;
        result[0] = typedValue;
        
        // Quoting from javadoc of Set.toArray(T[]):
        // If this set fits in the specified array with room to spare (i.e., the array has more elements than
        // this set), the element in the array immediately following the end of the set is set to null.
        if (result.length > 1)
            result[1] = null;
        
        return result;
    }
    
    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean add(final E e) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c)
            if (!value.equals(o))
                return false;
        return true;
    }
    
    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Throws {@link UnsupportedOperationException}.
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks if the specified object is equal to this set.
     * 
     * <p>
     * Will return <code>true</code> if the specified object:
     * </p>
     * 
     * <ul>
     * <li>is a {@link Set}
     * <li>has a size of <code>1</code>
     * <li>and contains our one and only value
     * </ul>
     *
     * @param obj object to be checked for equality
     * 
     * @return <code>true</code> the specified object is equal, <code>false</code> otherwise
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;
        
        if (!(obj instanceof Set))
            return false;
        
        final Set<?> set = (Set<?>) obj;
        return set.size() == 1 && set.contains(value);
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
    
    @Override
    public OneValueSet<E> clone() {
        try {
            @SuppressWarnings("unchecked")
            final OneValueSet<E> set = (OneValueSet<E>) super.clone();
            return set;
        } catch (final CloneNotSupportedException e) {
            // Never to happen as we implement Cloneable
            throw new InternalError(e);
        }
    }
    
}
