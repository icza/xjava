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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link URLBuilder}.
 * 
 * @author Andras Belicza
 */
public class URLBuilderTest extends BaseTest {
    
    /** */
    @Test
    public void testAddParams() {
        URLBuilder u = new URLBuilder("http://foo.com/");
        
        assertEquals("http://foo.com/", u.toURL().toString());
        
        u.add("one", "first");
        assertEquals("http://foo.com/?one=first", u.toURL().toString());
        
        u.add("two", "second");
        assertEquals("http://foo.com/?one=first&two=second", u.toURL().toString());
        
        
        u = new URLBuilder("http://foo.com/yo");
        
        assertEquals("http://foo.com/yo", u.toURL().toString());
        
        u.add("one", "first");
        assertEquals("http://foo.com/yo?one=first", u.toURL().toString());
        
        u.add("two", "second");
        assertEquals("http://foo.com/yo?one=first&two=second", u.toURL().toString());
        
        
        u = new URLBuilder("http://foo.com/yo?three=third");
        
        assertEquals("http://foo.com/yo?three=third", u.toURL().toString());
        
        u.add("one", "first");
        assertEquals("http://foo.com/yo?three=third&one=first", u.toURL().toString());
        
        u.add("two", "second");
        assertEquals("http://foo.com/yo?three=third&one=first&two=second", u.toURL().toString());
    }
    
}
