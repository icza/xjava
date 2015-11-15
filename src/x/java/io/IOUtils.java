/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I/O utilities.
 * 
 * @author Andras Belicza
 */
public class IOUtils {
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(IOUtils.class.getName());
    
    
    /**
     * Serializes the specified object using the standard Java serialization process.
     * 
     * @param obj object to be serialized
     * 
     * @return the byte array of the serialized object; or <code>null</code> if <code>obj</code> cannot be
     *         serialized
     * 
     * @see #deserialize(byte[])
     * @see Serializable
     * @see ObjectOutputStream
     */
    public static byte[] serialize(final Serializable obj) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (final ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to serialize object!", ie);
            return null;
        }
        
        return baos.toByteArray();
    }
    
    /**
     * Deserializes an object from the specified byte array using the standard Java serialization process.
     * 
     * @param <T> type of the object to be deserialized
     * 
     * @param array byte array of the serialized object
     * 
     * @return the object deserialized from the specified byte array; or <code>null</code> if an object cannot
     *         be deserialized
     * 
     * @see #serialize(Serializable)
     * @see Serializable
     * @see ObjectInputStream
     */
    public static <T extends Serializable> T deserialize(final byte[] array) {
        try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(array))) {
            @SuppressWarnings("unchecked")
            final T obj = (T) ois.readObject();
            return obj;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to deserialize object!", e);
            return null;
        }
    }
    
    /**
     * Tries to reads a full byte array from the specified input stream.
     * 
     * <p>
     * This method will attempt to read up to <code>buffer.length</code> bytes and store them into the
     * <code>buffer</code> array. Only returns if <code>buffer</code> is fully populated, or end of stream is
     * reached. In either case the return value will tell now many bytes were read and stored in the
     * <code>buffer</code>).
     * </p>
     * 
     * @param is input stream to read from
     * @param buffer array to be read
     * 
     * @return the number of bytes read
     * 
     * @throws IOException if reading from the stream throws {@link IOException}
     * 
     * @see #readFully(InputStream, byte[])
     * @see #readAllBytes(InputStream)
     */
    public static int tryReadFully(final InputStream is, final byte[] buffer) throws IOException {
        final int size = buffer.length;
        
        for (int off = 0; off < size;) {
            int read = is.read(buffer, off, size - off);
            if (read == -1)
                return off;
            off += read;
        }
        
        return size;
    }
    
    /**
     * Reads a full byte array from the specified input stream.
     * 
     * <p>
     * The problem with {@link InputStream#read(byte[])} is that it does not guarantee that the passed array
     * will be "fully populated" even if there are enough data in the input stream (the returned number of
     * bytes might be smaller than the array length). This method does guarantee it.
     * </p>
     * 
     * @param is input stream to read from
     * @param buffer array to be read
     * 
     * @return the buffer
     * 
     * @throws IOException if reading from the stream throws {@link IOException} or end of stream is reached
     * 
     * @see #tryReadFully(InputStream, byte[])
     * @see #readAllBytes(InputStream)
     */
    public static byte[] readFully(final InputStream is, final byte[] buffer) throws IOException {
        final int read = tryReadFully(is, buffer);
        
        if (read != buffer.length)
            throw new EOFException("End of Stream reached before buffer could be fully read: " + read
                    + " bytes read out of " + buffer.length);
        
        return buffer;
    }
    
    /**
     * Reads all bytes from the specified input stream until end of stream is reached.
     * 
     * @param is input stream to read from
     * 
     * @return the read bytes
     * 
     * @throws IOException if reading from the stream throws {@link IOException}
     * 
     * @see #tryReadFully(InputStream, byte[])
     * @see #readFully(InputStream, byte[])
     */
    public static byte[] readAllBytes(final InputStream is) throws IOException {
        final List<byte[]> chunkList = new ArrayList<>();
        
        final int CHUNK_SIZE = 16 * 1024;
        
        byte[] buffer;
        int read;
        
        while (true) {
            buffer = new byte[CHUNK_SIZE];
            read = tryReadFully(is, buffer);
            
            if (read < CHUNK_SIZE) {
                // End of stream reached, assemble final array and return it
                final byte[] res = new byte[chunkList.size() * CHUNK_SIZE + read];
                
                int off = 0;
                for (final byte[] chunk : chunkList) {
                    System.arraycopy(chunk, 0, res, off, CHUNK_SIZE);
                    off += CHUNK_SIZE;
                }
                
                // And the last part:
                System.arraycopy(buffer, 0, res, off, read);
                
                return res;
            }
            
            chunkList.add(buffer);
        }
    }
    
}
