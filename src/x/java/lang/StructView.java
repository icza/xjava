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
import java.util.Map;

/**
 * A class which has a source a source key-value structure ({@link Map}) where keys are {@link String}s, and
 * provides easy access to its content.
 * 
 * @author Andras Belicza
 */
public class StructView implements Serializable {
    
    /** */
    private static final long serialVersionUID = 1L;
    
    /** Source data structure. */
    protected final Map<String, Object> struct;
    
    /**
     * Creates a new {@link StructView}.
     * 
     * @param struct source data structure
     */
    public StructView(final Map<String, Object> struct) {
        this.struct = struct;
    }
    
    /**
     * Returns the value of the field specified by its name.
     * 
     * @param <T> (expected) type of the value
     * @param name name of the field
     * @return the value of the field specified by its name
     */
    public <T> T get(final String name) {
        @SuppressWarnings("unchecked")
        final T value = (T) struct.get(name);
        
        return value;
    }
    
    /**
     * Returns the value for the field specified by its path.
     * 
     * <p>
     * This method returns a value acquired in <code>n</code> steps (where <code>n</code> is the number of
     * path elements).<br>
     * The first step is executed on <code>this</code>, and the step result is the value returned by
     * {@link #get(String)} for the first path element.<br>
     * The result of the <code>i</code><sup>th</sup> step is the value returned by {@link #get(String)} called
     * on the result of the <code>i-1</code><sup>th</sup> step which must be of type {@link StructView}.<br>
     * The return value of the method is the result of the <code>n</code><sup>th</sup> step.<br>
     * <br>
     * If the result of any steps is <code>null</code>, then <code>null</code> is returned.
     * </p>
     * 
     * @param <T> (expected) type of the value
     * @param path path to the field
     * @return the value of the field specified by its path
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final String... path) {
        // Last name index
        final int lastNameIdx = path.length - 1;
        
        Map<String, Object> struct2 = struct;
        for (int i = 0; i < lastNameIdx; i++)
            if ((struct2 = (Map<String, Object>) struct2.get(path[i])) == null)
                return null;
        
        return struct2 == null ? null : (T) struct2.get(path[lastNameIdx]);
    }
    
    /**
     * Returns the source structure.
     * 
     * @return the source structure
     */
    public Map<String, Object> getStruct() {
        return struct;
    }
    
    /**
     * Checks if the specified object is equal to this {@link StructView}.
     * 
     * <p>
     * Will return <code>true</code> if the specified object:
     * </p>
     * 
     * <ul>
     * <li>is a {@link StructView}
     * <li>the source data structure maps are equal
     * </ul>
     *
     * @param obj object to be checked for equality
     * 
     * @return <code>true</code> if the specified object is equal, <code>false</code> otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        
        if (!(obj instanceof StructView))
            return false;
        
        return struct.equals(((StructView) obj).struct);
    }
    
    @Override
    public int hashCode() {
        return struct.hashCode();
    }
    
}
