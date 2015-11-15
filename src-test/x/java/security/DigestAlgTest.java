/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.security;

import static org.junit.Assert.assertSame;

import org.junit.Test;

import x.java.BaseTest;

/**
 * JUnit test of {@link DigestAlg}.
 * 
 * @author Andras Belicza
 */
public class DigestAlgTest extends BaseTest {
    
    /** */
    @Test
    public void testForAlgorithm() {
        String[] in = { "MD5", "SHA-1", "SHA-256", "SHA-512", "unknownAlg" };
        DigestAlg[] out = { DigestAlg.MD5, DigestAlg.SHA1, DigestAlg.SHA256, DigestAlg.SHA512, null };
        
        for (int i = 0; i < in.length; i++)
            assertSame(out[i], DigestAlg.forAlgorithm(in[i]));
    }
    
}
