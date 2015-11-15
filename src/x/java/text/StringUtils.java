/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.text;

import java.security.SecureRandom;
import java.util.Random;

/**
 * String utilities.
 * 
 * @author Andras Belicza
 */
public class StringUtils {
    
    /** Digits used in the hexadecimal representation. */
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    
    
    /**
     * Converts the specified byte array to hex string.
     * 
     * <p>
     * Lower-cased letters are used to represent hex digits greater than <code>9</code>.
     * </p>
     * 
     * @param data data to be converted
     * 
     * @return the specified data converted to hex string
     * 
     * @see #toHexString(byte[], int, int)
     * @see #hexToBytes(String)
     */
    public static String toHexString(final byte[] data) {
        return toHexString(data, 0, data.length);
    }
    
    /**
     * Converts the specified byte array to hex string.
     * 
     * <p>
     * Lower-cased letters are used to represent hex digits greater than <code>9</code>.
     * </p>
     * 
     * @param data data to be converted
     * @param offset offset of the first byte to be convert
     * @param length number of bytes to be converted
     * 
     * @return the specified data converted to hex string
     * 
     * @see #toHexString(byte[])
     * @see #hexToBytes(String)
     */
    public static String toHexString(final byte[] data, int offset, int length) {
        final StringBuilder sb = new StringBuilder(length * 2);
        
        for (length += offset; offset < length; offset++) {
            final int b = data[offset] & 0xff;
            sb.append(HEX_DIGITS[b >> 4]).append(HEX_DIGITS[b & 0x0f]);
        }
        
        return sb.toString();
    }
    
    /**
     * Converts the specified hex string to bytes.
     * 
     * <p>
     * The hex string must be lower-cased, can only contain hex digits (<code>'0'..'9'</code> and
     * <code>'a'..'f'</code>) and spaces (which will be omitted), and its length (without spaces) must be
     * even.
     * </p>
     * 
     * @param hex hex string; a string containing hexadecimal numbers
     * 
     * @return the specified hex string converted to bytes; or <code>null</code> if <code>hex</code> is an
     *         invalid hex string
     * 
     * @see #toHexString(byte[])
     * @see #toHexString(byte[], int, int)
     */
    public static byte[] hexToBytes(String hex) {
        // Quick check if it contains spaces:
        if (hex.indexOf(' ') >= 0)
            hex = hex.replace(" ", ""); // Remove spaces
            
        // Check: length must be even, must contain only hex digits
        if ((hex.length() & 0x01) == 1 || !hex.matches("[\\da-f]*"))
            return null;
        
        final byte[] bytes = new byte[hex.length() / 2];
        for (int i = bytes.length - 1; i >= 0; i--) {
            final char upp = hex.charAt(i * 2);
            final char low = hex.charAt(i * 2 + 1);
            bytes[i] = (byte) (((upp - (upp < 'a' ? '0' : 'a' - 10)) << 4) | (low - (low < 'a' ? '0'
                    : 'a' - 10)));
        }
        
        return bytes;
    }
    
    /**
     * Converts the specified data to base64 encoded string.
     * 
     * <p>
     * This implementation uses the Standard 'base64' encoding charset (last 2 characters are <code>'+'</code>
     * and <code>'/'</code>).
     * </p>
     * 
     * @param data data to be converted
     * 
     * @return the specified data converted to base64 encoded string
     */
    public static String toBase64String(final byte[] data) {
        return javax.xml.bind.DatatypeConverter.printBase64Binary(data);
    }
    
    /**
     * Checks if the specified source text contains the specified other text, performing a case-insensitive
     * search.
     * 
     * <p>
     * This implementation does not create any temporary objects, and is about <b>4x faster</b> than
     * <code>src.toLowerCase().contains(what.toLowerCase())</code>.
     * </p>
     * 
     * @param src text to search in
     * @param what text to be searched
     * 
     * @return true if the specified text contains the specified search text, performing a case-insensitive
     *         search; false otherwise
     */
    public static boolean containsIgnoreCase(final String src, final String what) {
        if (src == null || what == null)
            return false;
        
        final int searchLength = what.length();
        if (searchLength == 0)
            return true; // Empty string is contained
            
        final char firstCharLow = Character.toLowerCase(what.charAt(0));
        final char firstCharUpp = Character.toUpperCase(what.charAt(0));
        
        for (int i = src.length() - searchLength; i >= 0; i--) {
            // A quick pre-check before calling the more expensive String.regionMatches() method:
            final char ch = src.charAt(i);
            if (ch != firstCharLow && ch != firstCharUpp)
                continue;
            
            if (src.regionMatches(true, i, what, 0, searchLength))
                return true;
        }
        
        return false;
    }
    
    /**
     * The character set of Standard 'base64url' with URL and Filename.<br>
     * These characters are safe to use in URLs, in cookie values and in file names.
     */
    private static final char[] BASE64URL_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_"
            .toCharArray();
    
    /** {@link SecureRandom} is expensive to instantiate, so cache it. */
    private static SecureRandom SECURE_RANDOM;
    
    /**
     * Returns the lazily initialized, cached {@link SecureRandom}.
     * 
     * @return the lazily initialized, cached {@link SecureRandom}
     */
    private static SecureRandom getSecureRandom() {
        if (SECURE_RANDOM == null) {
            SECURE_RANDOM = new SecureRandom();
        }
        
        return SECURE_RANDOM;
    }
    
    /**
     * Generates a random {@link String} with the specified length using the character set of Standard
     * 'base64url' with URL and Filename. Each character holds 6 bits of information.
     * 
     * <p>
     * The generated random {@link String} is URL-, cookie value- and filename safe.
     * </p>
     * 
     * <p>
     * A {@link SecureRandom} is used as the source of random data.
     * </p>
     * 
     * @param length length of the {@link String} to be generated
     * 
     * @return a random {@link String} with the specified length
     */
    public static String randomString(final int length) {
        return randomString(length, getSecureRandom());
    }
    
    /**
     * Generates a random {@link String} with the specified length using the character set of Standard
     * 'base64url' with URL and Filename. Each character holds 6 bits of information.
     * 
     * <p>
     * The generated random {@link String} is URL-, cookie value- and filename safe.
     * </p>
     * 
     * <p>
     * The specified {@link Random} object is used as the source of random data.
     * </p>
     * 
     * @param length length of the {@link String} to be generated
     * @param random {@link Random} object used as the source of random data
     * 
     * @return a random {@link String} with the specified length
     */
    public static String randomString(final int length, final Random random) {
        // Generate as many bytes as the specified length, and just use 6 bits from each.
        // This is much faster than to use Base64 encoding algorithm.
        // (Note it is only faster if base64 encoding is slower than generating the extra/unused bits.)
        final byte[] bytes = new byte[length];
        
        random.nextBytes(bytes);
        
        final char[] chars = new char[length];
        for (int i = bytes.length - 1; i >= 0; i--)
            chars[i] = BASE64URL_CHARS[bytes[i] & 0x3f];
        
        return new String(chars);
    }
    
}
