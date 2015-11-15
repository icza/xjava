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

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import x.java.net.httppost.HttpPost;

/**
 * Helper class to build {@link URL}s.
 * 
 * <p>
 * Main goal of this class is to help adding query parameters to a URL. This simple implementation just
 * appends query parameters and their values encoded and connected with proper characters, but does not check
 * if the same parameter is added multiple times (they will be appended multiple times to the result URL
 * string).
 * </p>
 * 
 * <p>
 * Example usage:<br>
 * 
 * <pre>
 * URL u = new URLBuilder(&quot;http://foo.com/bar&quot;).add(&quot;one&quot;, &quot;first&quot;).add(&quot;two&quot;, &quot;second&quot;).toURL();
 * </pre>
 * 
 * Result:
 * 
 * <pre>
 * http://foo.com/bar?one=first&amp;two=second
 * </pre>
 * 
 * </p>
 * 
 * @author Andras Belicza
 * 
 * @see HttpPost
 */
public class URLBuilder {
    
    /** URL-safe (meaning no need to URL-encode) timestamp chars to use. */
    private static final char[] TIMESTAMP_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.-"
            .toCharArray();
    
    
    /** String builder used to construct the string URL. */
    protected final StringBuilder sb;
    
    /** Tells if there are parameters added. */
    protected boolean paramsAdded = false;
    
    /**
     * Creates a new {@link URLBuilder}.
     * 
     * @param spec base URL specification, may contain a query part but must not contain a reference part
     * 
     * @throws IllegalArgumentException if the spec is a malformed URL spec or it contains a reference part
     */
    public URLBuilder(final String spec) throws IllegalArgumentException {
        this(NetUtils.createUrl(spec));
    }
    
    /**
     * Creates a new {@link URLBuilder}.
     * 
     * @param url base URL to extend, may contain a query part but must not contain a reference part
     * 
     * @throws IllegalArgumentException if the specified URL contains a reference part
     */
    public URLBuilder(final URL url) throws IllegalArgumentException {
        if (url.getRef() != null)
            throw new IllegalArgumentException("URL contains a reference part: " + url);
        
        paramsAdded = url.getQuery() != null;
        
        sb = new StringBuilder(url.toString());
    }
    
    /**
     * Adds a new query parameter.
     * 
     * @param name name of the parameter to be added
     * @param value value of the parameter to be added (<code>value.toString()</code> will be used)
     * 
     * @return <code>this</code> for chaining
     */
    public URLBuilder add(final String name, final Object value) {
        prepareAddParam();
        
        try {
            sb.append(name).append('=')
                    .append(URLEncoder.encode(value == null ? "null" : value.toString(), "UTF-8"));
        } catch (final UnsupportedEncodingException uee) {
            // Never to happen
            throw new RuntimeException(uee);
        }
        
        return this;
    }
    
    /**
     * Adds a timestamp query parameter ensuring that the URL will be unique and therefore its response won't
     * be cached.
     * 
     * <p>
     * The appended timestamp parameter name is <code>"t"</code> and its value is the value returned by
     * {@link System#currentTimeMillis()} in radix 64, reversed.
     * </p>
     * 
     * @return <code>this</code> for chaining
     * 
     * @see #addTimestamp(String)
     */
    public URLBuilder addTimestamp() {
        return addTimestamp("t");
    }
    
    /**
     * Adds a timestamp query parameter ensuring that the URL will be unique and therefore its response won't
     * be cached.
     * 
     * <p>
     * The appended timestamp parameter name is <code>"t"</code> and its value is the value returned by
     * {@link System#currentTimeMillis()} in radix 64, reversed.
     * </p>
     * 
     * @param paramName timestamp parameter name to be used
     * 
     * @return <code>this</code> for chaining
     * 
     * @see #addTimestamp()
     */
    public URLBuilder addTimestamp(final String paramName) {
        prepareAddParam();
        
        sb.append(paramName).append('=');
        
        // The next cycle would not write anything in case of 0 time,
        // but time is always greater than 0, so no need to check.
        for (long t = System.currentTimeMillis(); t > 0; t >>= 6)
            sb.append(TIMESTAMP_CHARS[(int) (t & 0x3f)]);
        
        return this;
    }
    
    /**
     * Prepares adding a param, so after this a param can simply be appended.
     */
    private void prepareAddParam() {
        if (paramsAdded)
            sb.append('&');
        else {
            sb.append('?');
            paramsAdded = true;
        }
    }
    
    /**
     * Builds and returns the {@link URL}.
     * 
     * @return the built {@link URL}
     */
    public URL toURL() {
        try {
            return new URL(sb.toString());
        } catch (final MalformedURLException mue) {
            // Never to happen since we check URL spec in the constructor
            // and we only add query parameters, properly encoded
            throw new RuntimeException("Malformed URL: " + sb.toString(), mue);
        }
    }
    
}
