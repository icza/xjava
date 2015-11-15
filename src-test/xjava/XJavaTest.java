/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package xjava;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link XJava}.
 * 
 * @author Andras Belicza
 */
public class XJavaTest extends BaseTest {
    
    /** */
    private XJava xj = XJava.INSTANCE;
    
    /** */
    @Test
    public void testProperties() {
        assertNotNull(xj.getProjectName());
        assertNotNull(xj.getProjectVersion());
        assertNotNull(xj.getProjectFullName());
        assertNotNull(xj.getCopyrightYears());
        assertNotNull(xj.getAuthorName());
        assertTrue(xj.getBuildNumber() > 0);
        assertTrue(xj.getBuildTimeStamp() > 0);
        
        assertEquals("XJava", xj.getProjectName());
        assertEquals("Andras Belicza", xj.getAuthorName());
    }
    
    /** */
    @Test
    public void testVersion() {
        String[] parts = xj.getProjectVersion().split("\\.");
        
        assertTrue(parts.length > 0);
        
        // Each part must be a non-negative integer
        for (int i = 0; i < parts.length; i++) {
            String spart = parts[i];
            
            assertFalse(spart.isEmpty());
            
            if (spart.length() > 1)
                assertNotEquals('0', spart.charAt(0));
            
            int part = Integer.parseInt(parts[i]);
            assertTrue(part >= 0);
        }
    }
    
    /** */
    @Test
    public void testCopyrightYears() {
        String cy = xj.getCopyrightYears();
        
        // Must be either a year
        // or a range of years in the format of "xxxx-yyyy"
        
        if (cy.indexOf('-') < 0) {
            int year = Integer.parseInt(cy);
            assertTrue(year >= 2010);
            assertTrue(year <= 2100);
        } else {
            String[] parts = cy.split("-");
            assertEquals(2, parts.length);
            
            int year1 = Integer.parseInt(parts[0]);
            int year2 = Integer.parseInt(parts[1]);
            
            assertTrue(year1 >= 2010);
            assertTrue(year2 <= 2100);
            assertTrue(year1 < year2);
        }
    }
    
}
