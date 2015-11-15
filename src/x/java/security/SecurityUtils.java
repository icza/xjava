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

import java.io.EOFException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.logging.Level;
import java.util.logging.Logger;

import x.java.lang.Ref;
import x.java.text.StringUtils;

/**
 * Security utilities.
 * 
 * @author Andras Belicza
 */
public class SecurityUtils {
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(SecurityUtils.class.getName());
    
    
    /**
     * Calculates the specified digest of a file.
     * 
     * @param algorithm digest algorithm to use
     * @param file file whose digest to be calculated
     * @param exception optional parameter, if provided, {@link Exception} cause will be stored here if digest
     *            cannot be calculated (in which case empty string is returned)
     * 
     * @return the calculated digest of the file; or an empty string if the file cannot be read or or
     *         <code>algorithm</code> is unknown
     * 
     * @see DigestAlg
     */
    @SafeVarargs
    public static String fileDigest(final String algorithm, final Path file,
            final Ref<Exception>... exception) {
        try (final InputStream input = Files.newInputStream(file)) {
            
            return streamDigest(algorithm, input, Files.size(file), exception);
            
        } catch (final Exception e) {
            LOGGER.log(Level.FINE, "Failed to read file: " + file, e);
            
            if (exception.length > 0)
                exception[0].value = e;
            
            return "";
        }
    }
    
    /**
     * Calculates the specified digest of data read from a stream.
     * 
     * <p>
     * A <code>size = -1</code> indicates that all the data available from the stream has to be read and
     * included in digest calculation. This means that if <code>size = -1</code> data will be read from the
     * stream until end of stream is reached.
     * </p>
     * 
     * @param algorithm digest algorithm to use
     * @param is input stream to read data from
     * @param size number of bytes to read and calculate digest from; <code>-1</code> means to read until end
     *            of stream is reached
     * @param exception optional parameter, if provided, {@link Exception} cause will be stored here if digest
     *            cannot be calculated (in which case empty string is returned)
     * 
     * @return the calculated digest of the data; or an empty string if the specified number of bytes cannot
     *         be read from the stream or <code>algorithm</code> is unknown
     * 
     * @see DigestAlg
     */
    @SafeVarargs
    public static String streamDigest(final String algorithm, final InputStream is, final long size,
            final Ref<Exception>... exception) {
        try {
            long remaining = size == -1 ? Long.MAX_VALUE : size;
            final MessageDigest md = MessageDigest.getInstance(algorithm);
            
            final byte[] buffer = new byte[(int) Math.min(16 * 1024, remaining)];
            
            while (remaining > 0) {
                final int read = is.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                
                if (read == -1) {
                    // End of stream reached
                    if (size == -1)
                        break;
                    
                    throw new EOFException("Not enough data, still need " + remaining + " bytes!");
                }
                
                remaining -= read;
                md.update(buffer, 0, read);
            }
            
            return StringUtils.toHexString(md.digest());
            
        } catch (final Exception e) {
            LOGGER.log(Level.INFO, "Failed to calculate " + algorithm + " digest", e);
            
            if (exception.length > 0)
                exception[0].value = e;
            
            return "";
        }
    }
    
}
