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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Message digest algorithm.
 * 
 * @author Andras Belicza
 */
public enum DigestAlg {
    
    /**
     * The MD2 message digest algorithm as defined in <a href="http://www.ietf.org/rfc/rfc1319.txt">RFC
     * 1319</a>.
     */
    MD2("MD2", false),
    
    /**
     * The MD5 message digest algorithm as defined in <a href="http://www.ietf.org/rfc/rfc1321.txt">RFC
     * 1321</a>.
     */
    MD5("MD5", true),
    
    /**
     * The SHA-1 hash algorithm defined in the <a
     * href="http://csrc.nist.gov/publications/fips/fips180-4/fips-180-4.pdf">FIPS PUB 180-4</a>.
     */
    SHA1("SHA-1", true),
    
    /**
     * The SHA-224 hash algorithm defined in the <a
     * href="http://csrc.nist.gov/publications/fips/fips180-4/fips-180-4.pdf">FIPS PUB 180-4</a>.
     */
    SHA224("SHA-224", false),
    
    /**
     * The SHA-256 hash algorithm defined in the <a
     * href="http://csrc.nist.gov/publications/fips/fips180-4/fips-180-4.pdf">FIPS PUB 180-4</a>.
     */
    SHA256("SHA-256", true),
    
    /**
     * The SHA-384 hash algorithm defined in the <a
     * href="http://csrc.nist.gov/publications/fips/fips180-4/fips-180-4.pdf">FIPS PUB 180-4</a>.
     */
    SHA384("SHA-384", false),
    
    /**
     * The SHA-512 hash algorithm defined in the <a
     * href="http://csrc.nist.gov/publications/fips/fips180-4/fips-180-4.pdf">FIPS PUB 180-4</a>.
     */
    SHA512("SHA-512", false);
    
    
    /** Name of the algorithm as expected by {@link MessageDigest#getInstance(String)}. */
    public final String algorithm;
    
    /**
     * Tells if the algorithm is standard and is required to be supported by every implementation of the Java
     * platform.
     */
    public final boolean standard;
    
    /**
     * Creates a new {@link DigestAlg}.
     * 
     * @param algorithm name of the algorithm as expected by {@link MessageDigest#getInstance(String)}
     * @param standard tells if the algorithm is standard and is required to be supported by every
     *            implementation of the Java platform
     */
    private DigestAlg(final String algorithm, final boolean standard) {
        this.algorithm = algorithm;
        this.standard = standard;
    }
    
    /**
     * Tells if the digest algorithm is supported by the current implementation of the Java platform.
     * 
     * @return <code>true</code> if the algorithm is supported; <code>false</code> otherwise
     */
    public boolean isSupported() {
        try {
            MessageDigest.getInstance(algorithm);
            return true;
        } catch (final NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    /**
     * Returns the {@link DigestAlg} for the specified algorithm.
     * 
     * @param algorithm algorithm name to return {@link DigestAlg} for
     * @return the {@link DigestAlg} for the specified algorithm or <code>null</code> if no {@link DigestAlg}
     *         found for the specified algorithm name
     */
    public static DigestAlg forAlgorithm(final String algorithm) {
        for (final DigestAlg da : VALUES)
            if (da.algorithm.equals(algorithm))
                return da;
        
        return null;
    }
    
    
    /** Cache of the values array. */
    private static final DigestAlg[] VALUES = DigestAlg.values();
    
}
