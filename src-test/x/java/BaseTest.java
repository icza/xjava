/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.io.Serializable;

import x.java.io.IOUtils;

/**
 * Base JUnit test with utility methods. All JUnit tests should extend this.
 * 
 * @author Andras Belicza
 */
public class BaseTest {
    
    /**
     * Tests if the specified object is cloneable.
     * 
     * <p>
     * A <code>public clone()</code> method will be looked for and called. The return value of this
     * <code>clone()</code> method will be compared to the input.
     * </p>
     * 
     * @param <T> type of the object
     * @param o object to be tested
     * 
     * @return the cloned object which can be further investigated
     */
    public <T> T testClone(final T o) {
        try {
            // The method clone() from the type Object is not visible (protected)
            // We have to use reflection
            
            @SuppressWarnings("unchecked")
            final T clone = (T) o.getClass().getMethod("clone").invoke(o);
            
            assertNotSame(clone, o);
            assertEquals(clone, o);
            
            return clone;
        } catch (Exception e) {
            fail(e.getClass().getName() + ": " + e.getMessage());
            
            // Return statement here is just semantics, fail() will terminate the JUnit test run
            return null;
        }
        
    }
    
    /**
     * Tests if the specified object is serializable/deserializable.
     * 
     * <p>
     * The object will be first serialized then deserialized. The deserialized instance will be compared to
     * the input.
     * </p>
     * 
     * @param <T> type of the object
     * @param o object to be tested
     * 
     * @return the deserialized instance which can be further investigated
     */
    public <T extends Serializable> T testSerialization(final T o) {
        try {
            final T deo = IOUtils.deserialize(IOUtils.serialize(o));
            
            assertEquals(o, deo);
            
            return deo;
        } catch (Exception e) {
            fail(e.getClass().getName() + ": " + e.getMessage());
            
            // Return statement here is just semantics, fail() will terminate the JUnit test run
            return null;
        }
    }
    
}
