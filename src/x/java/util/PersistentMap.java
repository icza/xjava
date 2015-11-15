/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import x.java.io.IOUtils;

/**
 * A file-persisted map-like utility.
 * 
 * <p>
 * {@link PersistentMap} allows to store values of type <code>byte[]</code> associated with keys of type
 * {@link String}. Values already stored cannot be modified, only retrieved. <code>null</code> values are not
 * permitted for keys nor for values.
 * </p>
 * 
 * <p>
 * There are also 2 more methods provided for convenience: {@link #putObj(String, Serializable)} and
 * {@link #getObj(String)}. The first one serializes the object to a <code>byte[]</code> and then calls
 * {@link #put(String, byte[])}, the second one first calls {@link #get(String)} and then deserializes the
 * result.
 * </p>
 * 
 * <p>
 * Typical use-case of this class is to provide persistent data or object cache functionality. You can also
 * register {@link PropertyChangeListener}s which will be notified of changes performed on this map.
 * </p>
 * 
 * <p>
 * A {@link PersistentMap} is identified by its <i>root folder</i> in which its persisting files will be
 * placed. The values stored in the map are kept only persisted in the hard drive, they don't consume memory.
 * The mappings of a {@link PersistentMap} are kept when the map is closed or when the application is
 * restarted. Only one {@link PersistentMap} can exists for a given root folder. Attempts to create more
 * {@link PersistentMap}s with the same root folder before an existing one is closed will result in
 * {@link IOException} thrown by the constructor.
 * </p>
 * 
 * <p>
 * A {@link PersistentMap} has a version of type {@link String} which is also written to the persisting files.
 * This version has to be passed to the constructor. If the persisting files have a different version, their
 * content will be discarded / cleared automatically when the {@link PersistentMap} is created.
 * </p>
 * 
 * <p>
 * The implementation is thread-safe.<br>
 * The implementation is also close-state-tolerant: every method can be called after a
 * {@link PersistentMap#close()}, but they will not return any valid results.
 * </p>
 * 
 * <p>
 * Theoretical limits of the current {@link PersistentMap} implementation are:
 * </p>
 * <ul>
 * <li>Maximum number of entries: {@link Integer#MAX_VALUE}
 * <li>Maximum size of the value in an entry: {@link Integer#MAX_VALUE}
 * <li>TODO make ValueInfo.pos long, so there won't be other limits
 *     maybe do some bit kung-fu so won't be needing long+int in valueinfo
 *     TODO make a keySet() method
 * </ul>
 * 
 * @author Andras Belicza
 */
public class PersistentMap implements AutoCloseable {
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(PersistentMap.class.getName());
    
    /** Name of the index file. */
    private static final String FILE_NAME_INDEX = "index";
    
    /** Name of the data file. */
    private static final String FILE_NAME_DATA = "data";
    
    
    /**
     * Deletes the persisting files of a {@link PersistentMap}.
     * 
     * <p>
     * The persisting files of a {@link PersistentMap} can only be deleted if no live {@link PersistentMap}
     * exists. If one were created, it must be closed first.
     * </p>
     * 
     * @param rootFolder root folder of the persistent map to delete
     *
     * @return <code>true</code> if the persisting files did not exist or were deleted successfully;
     *         <code>false</code> otherwise
     */
    public static boolean delete(final Path rootFolder) {
        Path p = null;
        try {
            Files.deleteIfExists(p = rootFolder.resolve(FILE_NAME_INDEX));
            Files.deleteIfExists(p = rootFolder.resolve(FILE_NAME_DATA));
            return true;
        } catch (IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to delete file: " + p, ie);
            return false;
        }
    }
    
    
    /** Root folder of the persisting files. */
    private final Path rootFolder;
    
    /** Index file. */
    private final RandomAccessFile indexFile;
    
    /** Data file. */
    private final RandomAccessFile dataFile;
    
    /** The version of the data stored in the persistent map. */
    private final String version;
    
    /** Registry of change listeners. */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    
    /**
     * Info about a value in the persistent map.
     * 
     * @author Andras Belicza
     */
    private static class ValueInfo {
        /** Byte position of the value. */
        public final int pos;
        
        /** Size of the value in bytes. */
        public final int size;
        
        /**
         * Creates a new {@link ValueInfo}.
         * 
         * @param pos byte position of the value
         * @param size size of the value in bytes
         */
        public ValueInfo(final int pos, final int size) {
            this.pos = pos;
            this.size = size;
        }
    }
    
    
    /**
     * Index map: map of the persistent map keys and value info set.<br>
     * This is an in-memory cache of the content of the index file ({@link #indexFile}).
     */
    private final Map<String, ValueInfo> indexMap = new HashMap<>();
    
    /** Tells if the persistent map has been closed. */
    private boolean closed;
    
    /**
     * Creates a new {@link PersistentMap}.
     * 
     * <p>
     * If a {@link PersistentMap} at the specified root folder already exists and has the same
     * <code>version</code>, it will be used. If its <code>version</code> is different, it will be cleared. If
     * no {@link PersistentMap} exist at the specified root folder, a new empty one will be created.
     * </p>
     * 
     * <p>
     * If <code>rootFolder</code> does not exist, an attempt will be made to create it including non-existing
     * parent folder.
     * </p>
     * 
     * @param rootFolder root folder of the persisting files
     * @param version tells the version of the data stored in the persistent map; if it does not equal to the
     *            version of the persistent file, it will be cleared automatically
     * 
     * @throws IOException if the persistent map could not be initialized
     */
    public PersistentMap(final Path rootFolder, final String version) throws IOException {
        this.rootFolder = rootFolder;
        
        if (!Files.exists(rootFolder))
            Files.createDirectories(rootFolder);
        
        final Path indexPath = rootFolder.resolve(FILE_NAME_INDEX);
        indexFile = new RandomAccessFile(indexPath.toFile(), "rw");
        IOException lockException = null;
        try {
            if (indexFile.getChannel().tryLock() == null)
                lockException = new IOException("Index file is already in use by another program: "
                        + indexPath);
        } catch (final OverlappingFileLockException ofe) {
            lockException = new IOException("Index file is already in use: " + indexPath, ofe);
        }
        if (lockException != null) {
            // Failed to lock, must close the file!
            close();
            throw lockException;
        }
        
        final Path dataPath = rootFolder.resolve(FILE_NAME_DATA);
        dataFile = new RandomAccessFile(dataPath.toFile(), "rw");
        lockException = null;
        try {
            if (dataFile.getChannel().tryLock() == null)
                lockException = new IOException("Data file is already in use by another program: " + dataPath);
        } catch (final OverlappingFileLockException ofe) {
            lockException = new IOException("Data file is already in use: " + dataPath);
        }
        if (lockException != null) {
            // Failed to lock, must close the files (both)!
            close();
            throw lockException;
        }
        
        this.version = version;
        
        final long indexSize = indexFile.length();
        // New file or old version?
        final String oldVersion = indexSize == 0 ? null : indexFile.readUTF();
        if (!version.equals(oldVersion)) {
            if (oldVersion != null && LOGGER.isLoggable(Level.FINER))
                LOGGER.finer("Persistent map content outdated (old version: " + oldVersion
                        + ", new version: " + version + "): " + rootFolder);
            clear();
        } else {
            // Read the index file into memory
            String key;
            while (indexFile.getFilePointer() < indexSize) {
                key = indexFile.readUTF();
                final int pos = indexFile.readInt();
                final int size = indexFile.readInt();
                indexMap.put(key, new ValueInfo(pos, size));
            }
            if (LOGGER.isLoggable(Level.FINER))
                LOGGER.finer("Loaded " + indexMap.size() + " entries from persistent map: " + rootFolder);
        }
    }
    
    /**
     * Puts a new entry into the persistent map.
     * 
     * @param key key of the new entry
     * @param value value of the new entry
     * 
     * @throws IllegalArgumentException if the <code>key</code> or <code>value</code> is <code>null</code>
     * 
     * @see #putObj(String, Serializable)
     */
    public synchronized void put(final String key, final byte[] value) throws IllegalArgumentException {
        if (key == null || value == null)
            throw new IllegalArgumentException("key and value cannot be null!");
        
        if (closed || indexMap.containsKey(key))
            return;
        
        try {
            final ValueInfo valueInfo = new ValueInfo((int) dataFile.length(), value.length);
            
            dataFile.setLength(valueInfo.pos + value.length);
            dataFile.seek(valueInfo.pos);
            dataFile.write(value);
            
            // Index file's pointer is always at the end, no need to seek
            indexFile.writeUTF(key);
            indexFile.writeInt(valueInfo.pos);
            indexFile.writeInt(valueInfo.size);
            
            indexMap.put(key, valueInfo);
            
            pcs.firePropertyChange(key, null, value);
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Error adding new value to persistent map: " + rootFolder, ie);
        }
    }
    
    /**
     * Puts a new entry into the persistent map.
     * 
     * <p>
     * This method will serialize the <code>obj</code> object using standard Java serialization and puts the
     * resulting <code>byte</code> array into the map.
     * </p>
     * 
     * <p>
     * Note: this method allows <code>null</code> reference for <code>obj</code>.
     * </p>
     * 
     * @param key key of the new entry
     * @param obj object to store for the new entry
     * 
     * @throws IllegalArgumentException if <code>key</code> is <code>null</code>, or <code>value</code> is not
     *             serializable
     * 
     * @see #put(String, byte[])
     */
    public void putObj(final String key, final Serializable obj) throws IllegalArgumentException {
        final byte[] value = IOUtils.serialize(obj);
        if (value == null)
            throw new IllegalArgumentException("value is not serializable!");
        
        put(key, value);
    }
    
    /**
     * Reads a value from the persistent map.
     * 
     * @param key key whose associated value to be read
     * 
     * @return the value associated with the specified key; or <code>null</code> if there is no value
     *         associated with the specified key
     * 
     * @throws IllegalArgumentException if the <code>key</code> is <code>null</code>
     * 
     * @see #getObj(String)
     */
    public synchronized byte[] get(final String key) {
        if (key == null)
            throw new IllegalArgumentException("key cannot be null!");
        
        if (closed)
            return null;
        
        final ValueInfo valueInfo = indexMap.get(key);
        
        if (valueInfo != null)
            try {
                dataFile.seek(valueInfo.pos);
                
                final byte[] value = new byte[valueInfo.size];
                dataFile.readFully(value);
                
                return value;
            } catch (final IOException ie) {
                LOGGER.log(Level.SEVERE, "Error reading value from persistent map: " + rootFolder, ie);
            }
        
        return null;
    }
    
    /**
     * Reads an object from the persistent map.
     * 
     * <p>
     * This method will get the <code>byte</code> array value associated with the specified key and will
     * deserialize an object using standard Java serialization.
     * </p>
     * 
     * <p>
     * Note: If the object stored for the specified key was the <code>null</code> reference, this method will
     * return <code>null</code>. To tell the difference between this case and the case where the key is not in
     * the map {@link #contains(String)} can be used.
     * </p>
     * 
     * @param <T> type of the object to deserialize from the value associated with the specified key
     * 
     * @param key key whose associated value to be read and deserialized to an object
     * 
     * @return the object deserialized from the value associated with the specified key; or <code>null</code>
     *         if there is no value associated with the specified key or an object cannot be deserialized
     * 
     * @throws IllegalArgumentException if the <code>key</code> is <code>null</code>
     * 
     * @see #get(String)
     */
    public <T extends Serializable> T getObj(final String key) throws IllegalArgumentException {
        final byte[] value = get(key);
        if (value == null)
            return null;
        
        // deserialize() returns null if an object cannot be deserialized
        return IOUtils.deserialize(value);
    }
    
    /**
     * Tells if the specified key is part of the persistent map without reading and returning its value.
     * 
     * @param key key to check
     * 
     * @return {@link Boolean#TRUE} if the specified key has been put into the map; {@link Boolean#TRUE} if
     *         the key is not found and <code>null</code> if this {@link PersistentMap} has already been
     *         closed
     */
    public synchronized Boolean contains(final String key) {
        if (closed)
            return null;
        
        return indexMap.containsKey(key);
    }
    
    /**
     * Returns the size (number of entries) of the persistent map.
     * 
     * @return the size (number of entries) of the persistent map; or <code>null</code> if the persistent map
     *         is closed
     */
    public synchronized Integer size() {
        if (closed)
            return null;
        
        return indexMap.size();
    }
    
    /**
     * Clears the persistent map.
     */
    public synchronized void clear() {
        if (closed)
            return;
        
        try {
            if (LOGGER.isLoggable(Level.FINER))
                LOGGER.finer((indexFile.length() == 0 ? "Initializing" : "Clearing") + " persistent map: "
                        + rootFolder);
            
            indexFile.setLength(0L);
            indexFile.writeUTF(version.toString());
            dataFile.setLength(0L);
            indexMap.clear();
            
            pcs.firePropertyChange(null, null, null);
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Error clearing persistent map: " + rootFolder, ie);
        }
    }
    
    /**
     * Closes the persistent map.
     */
    public synchronized void close() {
        if (closed)
            return;
        
        closed = true;
        
        if (indexFile != null)
            try {
                indexFile.close();
            } catch (final IOException ie) {
                // Silently ignore.
            }
        
        if (dataFile != null)
            try {
                dataFile.close();
            } catch (final IOException ie) {
                // Silently ignore.
            }
    }
    
    /**
     * Tells if the persistent map has been closed.
     * 
     * @return true if the persistent map has been closed; false otherwise
     */
    public synchronized boolean isClosed() {
        return closed;
    }
    
    /**
     * Adds a change listener which will be called when the persistent map changes.
     * 
     * <p>
     * {@link PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)} will be called when the
     * map is cleared or a new value is put into it.<br>
     * If a new value is put into the map, the {@link PropertyChangeEvent#getPropertyName()} and
     * {@link PropertyChangeEvent#getNewValue()} will be the key and value respectively.<br>
     * If the map is cleared, property name and new value will be <code>null</code>.
     * </p>
     * 
     * @param listener listener to be added
     * 
     * @see #removeListener(PropertyChangeListener)
     */
    public void addListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }
    
    /**
     * Removes a change listener.
     * 
     * @param listener listener to be removed
     * 
     * @see #addListener(PropertyChangeListener)
     */
    public void removeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }
    
}
