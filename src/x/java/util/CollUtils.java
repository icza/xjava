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

import java.util.Comparator;

import x.java.lang.NullAwareComparable;

/**
 * Collection utilities.
 * 
 * @author Andras Belicza
 */
public class CollUtils {
    
    /**
     * Makes the specified {@link Comparator} null-aware.
     * 
     * <p>
     * The returned {@link Comparator} defines the same order as <code>c</code> for non-null objects and for
     * <code>null</code> "objects" defines the following order:
     * <ul>
     * <li>if both objects are <code>null</code>s, <code>compareTo()</code> returns <code>0</code>
     * <li><code>null</code> "objects" are smaller than non-nulls
     * <li>if both objects are non-nulls, their <code>compareTo()</code> will be called
     * </ul>
     * 
     * @param <T> type of the comparable elements
     * @param c comparator to make null-aware
     * 
     * @return a {@link Comparator} that defines the same order as <code>c</code> but also handles
     *         <code>null</code>s
     * 
     * @see NullAwareComparable
     */
    public static <T> Comparator<T> nullAwareComparator(final Comparator<T> c) {
        return new Comparator<T>() {
            @Override
            public int compare(final T o1, final T o2) {
                // First check this as this might be the most frequent case
                if (o1 != null && o2 != null)
                    return c.compare(o1, o2);
                
                if (o1 == null && o2 == null)
                    return 0;
                
                return o1 == null ? -1 : 1;
            }
        };
    }
    
}
