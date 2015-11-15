/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.nio.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

import x.java.lang.LongInt;
import x.java.lang.Pair;

/**
 * Path utilities.
 * 
 * @author Andras Belicza
 */
public class PathUtils {
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(PathUtils.class.getName());
    
    
    /**
     * Returns the file name of the specified file without its extension.
     * 
     * <p>
     * If the name of the specified file does not have an extension (dot is not found in its name at a
     * positive index), the file name is returned. This also means a dot at position <code>0</code> is not
     * considered an extension separator.
     * </p>
     * 
     * @param file file whose name without extension to be returned
     * 
     * @return the file name of the specified file without its extension
     */
    public static String getFileNameWithoutExt(final Path file) {
        final String fileName = file.getFileName().toString();
        
        final int dotIdx = fileName.lastIndexOf('.');
        return dotIdx > 0 ? fileName.substring(0, dotIdx) : fileName;
    }
    
    /**
     * Returns the file name and extension parts of the name of the specified file.
     * 
     * <p>
     * If the name of the specified file does not have an extension (dot is not found in its name at a
     * positive index), the second value of the returned {@link Pair} will be <code>null</code>. This also
     * means a dot at position <code>0</code> is not considered an extension separator.
     * </p>
     * 
     * @param file file whose name and extension to be returned
     * 
     * @return the file name and extension parts of the name of the specified file
     */
    public static Pair<String, String> getFileNameAndExt(final Path file) {
        final String fileName = file.getFileName().toString();
        
        final int dotIdx = fileName.lastIndexOf('.');
        return dotIdx > 0 ? new Pair<>(fileName.substring(0, dotIdx), fileName.substring(dotIdx + 1))
                : new Pair<>(fileName, (String) null);
    }
    
    /**
     * Generates and returns a unique {@link Path} for the specified file that does not yet exist.
     * 
     * <p>
     * If the provided file does not exist, it is returned.<br>
     * If it exists, an incrementing counter starting at 2 will be appended to the file name will be tried
     * (the extension will not be modified). (e.g. <code>" (2)"</code>, <code>" (3)"</code>, etc.).
     * </p>
     * 
     * @param file file to return a unique {@link Path} for
     * 
     * @return a unique file path that does not yet exist
     */
    public static Path uniqueFile(Path file) {
        if (!Files.exists(file))
            return file;
        
        final Path folder = file.getParent();
        
        final Pair<String, String> nameExt = getFileNameAndExt(file);
        final StringBuilder sb = new StringBuilder(nameExt.value1).append(" (");
        final String ext = nameExt.value2 == null ? null : '.' + nameExt.value2;
        
        final int nameLength = sb.length(); // name length without the counter value
        
        for (int counter = 2; true; counter++) {
            sb.append(counter).append(')');
            if (ext != null)
                sb.append(ext);
            
            file = folder.resolve(sb.toString());
            
            if (!Files.exists(file))
                return file;
            
            // Reset name builder
            sb.setLength(nameLength);
        }
    }
    
    /**
     * Deletes the specified path, recursively.
     * 
     * <p>
     * If the specified path does not exists as claimed by
     * {@link Files#exists(Path, java.nio.file.LinkOption...)}, this method returns <code>true</code>.
     * </p>
     * 
     * @param path path to be deleted
     * 
     * @return <code>true</code> if deletion was successful; <code>false</code> otherwise
     */
    public static boolean deletePath(final Path path) {
        if (!Files.exists(path))
            return true;
        
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                        throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(final Path dir, final IOException exc)
                        throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to delete file or folder!", ie);
            return false;
        }
        
        return true;
    }
    
    /**
     * Calculates the size of the specified folder (or file), recursively.
     * 
     * <p>
     * If the specified path does not exists as claimed by
     * {@link Files#exists(Path, java.nio.file.LinkOption...)}, this method returns <code>-1L</code>.
     * </p>
     * 
     * @param path path whose size to be calculated
     * 
     * @return the size of the specified path or <code>-1L</code> if the specified path does not exist or an
     *         error occurs
     */
    public static long pathSize(final Path path) {
        if (!Files.exists(path))
            return -1L;
        
        final LongInt size = new LongInt();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
                        throws IOException {
                    size.addAndGet(Files.size(file));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (final IOException ie) {
            LOGGER.log(Level.WARNING, "Failed to calculate folder size: " + path, ie);
            return -1L;
        }
        
        return size.value;
    }
    
    /**
     * Ensures that the specified folder exists recursively (and is a folder) on return.
     * 
     * @param folder folder to make sure exists
     * 
     * @return <code>false</code> if the folder already existed, <code>true</code> if the folder did not exist
     *         but was created successfully
     * 
     * @throws RuntimeException if the specified folder cannot be created or it exists but it is not a
     *             directory
     */
    public static boolean ensureFolderExists(final Path folder) {
        if (Files.isDirectory(folder))
            return false;
        
        try {
            Files.createDirectories(folder);
            return true;
        } catch (final IOException ie) {
            LOGGER.log(Level.SEVERE, "Failed to create folder: " + folder, ie);
            throw new RuntimeException(ie);
        }
    }
    
}
