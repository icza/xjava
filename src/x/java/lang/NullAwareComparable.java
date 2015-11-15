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

import x.java.util.CollUtils;

/**
 * The {@link NullAwareComparable} allows any of the comparable objects to be <code>null</code> with the
 * following behavior:
 * <ul>
 * <li>if both objects are <code>null</code>s, <code>compareTo()</code> returns <code>0</code>
 * <li><code>null</code> "objects" are smaller than non-nulls
 * <li>if both objects are non-nulls, their <code>compareTo()</code> will be called
 * </ul>
 * 
 * @author Andras Belicza
 * 
 * @param <T> type of the comparable objects
 * 
 * @see CollUtils#nullAwareComparator(java.util.Comparator)
 */
public class NullAwareComparable<T extends Comparable<T>> implements Comparable<NullAwareComparable<T>>,
        Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** Reference to the comparable object. */
    public final T value;
    
    /**
     * Creates a new {@link NullAwareComparable}.
     * 
     * @param value reference to the comparable object
     */
    public NullAwareComparable(final T value) {
        this.value = value;
    }
    
    @Override
    public int compareTo(final NullAwareComparable<T> other) {
        final T value = this.value;
        final T value2 = other.value;
        
        // First check this as this might be the most frequent case
        if (value != null && value2 != null)
            return value.compareTo(value2);
        
        if (value == null && value2 == null)
            return 0;
        
        return value == null ? -1 : 1;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof NullAwareComparable))
            return false;
        return Objects.equals(value, ((NullAwareComparable<?>) obj).value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    /**
     * Returns an empty string if value is <code>null</code>, <code>value.toString()</code> otherwise.
     */
    @Override
    public String toString() {
        return value == null ? "" : value.toString();
    }
    
    /**
     * Returns a {@link NullAwareComparable} whose {@link #toString()} formats the value as percent.
     * 
     * <p>
     * If the specified value is <code>null</code>, <code>"-"</code> will be returned by {@link #toString()}.
     * </p>
     * 
     * @param percent the percent value; might be <code>null</code>
     * @return a {@link NullAwareComparable} whose {@link #toString()} formats the value as percent
     */
    public static NullAwareComparable<Double> getPercent(final Double percent) {
        return new NullAwareComparable<Double>(percent) {
            /** */
            private static final long serialVersionUID = 1L;
            
            @Override
            public String toString() {
                return value == null ? "-" : String.format("%.2f%%", value);
            }
        };
    }
    
}
