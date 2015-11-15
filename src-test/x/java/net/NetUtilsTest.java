/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.net;

import java.net.URL;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link NetUtils}.
 * 
 * @author Andras Belicza
 */
public class NetUtilsTest extends BaseTest {
    
    /** */
    @Test
    public void testCreateUrl() {
        URL u = NetUtils.createUrl("http://google.com");
        NetUtils.createUrl(u, "robots.txt");
    }
    
    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUrlIAE() {
        NetUtils.createUrl("asdf://google.com");
    }
    
    /** */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUrlIAE2() {
        NetUtils.createUrl("/////");
    }
    
}
