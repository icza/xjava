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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Network utilities.
 * 
 * @author Andras Belicza
 */
public class NetUtils {
    
    /**
     * Creates a new {@link URL} from the specified URL spec.
     * 
     * <p>
     * Primary goal of this factory method is to suppress the {@link MalformedURLException} that comes with
     * the {@link URL}'s constructor, it is thrown in a "shadowed" manner, wrapped in an
     * {@link IllegalArgumentException} (which is a {@link RuntimeException}).
     * </p>
     * 
     * <p>
     * This method is useful for example if you want to create a {@link URL} from a {@link String} which
     * exists at compile time (so we have guarantee it's well-formed) and using <code>try-catch</code> block
     * would just make code less readable.
     * </p>
     * 
     * @param spec URL spec to create a {@link URL} from
     * 
     * @return a new {@link URL} from the specified URL spec
     * 
     * @throws IllegalArgumentException if the specified URL spec is a malformed URL
     * 
     * @see #createUrl(URL, String)
     * @see URL#URL(String)
     */
    public static URL createUrl(final String spec) throws IllegalArgumentException {
        return createUrl(null, spec);
    }
    
    /**
     * Creates a new {@link URL} from the specified URL context and spec.
     * 
     * <p>
     * Primary goal of this factory method is to suppress the {@link MalformedURLException} that comes with
     * the {@link URL}'s constructor, it is thrown in a "shadowed" manner, wrapped in an
     * {@link IllegalArgumentException} (which is a {@link RuntimeException}).
     * </p>
     * 
     * <p>
     * This method is useful for example if you want to create a {@link URL} from a {@link String} which
     * exists at compile time (so we have guarantee it's well-formed) and using <code>try-catch</code> block
     * would just make code less readable.
     * </p>
     * 
     * @param context URL context in which to interpret the URL spec
     * @param spec URL spec to create a {@link URL} from
     * 
     * @return a new {@link URL} from the specified URL spec
     * 
     * @throws IllegalArgumentException if the specified URL spec is a malformed URL
     * 
     * @see #createUrl(String)
     * @see URL#URL(URL, String)
     */
    public static URL createUrl(final URL context, final String spec) throws IllegalArgumentException {
        try {
            return new URL(context, spec);
        } catch (final MalformedURLException mue) {
            throw new IllegalArgumentException("Malformed URL: " + spec, mue);
        }
    }
    
}
