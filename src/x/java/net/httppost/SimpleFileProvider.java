/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.net.httppost;

import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A simple {@link FileProvider} which takes the provided file name or the file itself and last modified date
 * as constructor arguments.
 * 
 * @author Andras Belicza
 * 
 * @see FileProvider
 */
public class SimpleFileProvider implements FileProvider {
    
    /** The provided file. */
    protected Path file;
    
    /** Last modified date of the provided file. */
    protected Long lastModified;
    
    /**
     * Creates a new {@link SimpleFileProvider}.
     * 
     * @param fileName name (and path) of the provided file
     * @param lastModified last modified date of the provided file
     */
    public SimpleFileProvider(final String fileName, final Long lastModified) {
        this(Paths.get(fileName), lastModified);
    }
    
    /**
     * Creates a new {@link SimpleFileProvider}.
     * 
     * @param file the provided file
     * @param lastModified last modified date of the provided file
     */
    public SimpleFileProvider(final Path file, final Long lastModified) {
        this.file = file;
        this.lastModified = lastModified;
    }
    
    @Override
    public Path getFile(final HttpURLConnection httpUrlConnection) {
        return file;
    }
    
    @Override
    public Long getLastModified(final HttpURLConnection httpUrlConnection) {
        return lastModified;
    }
    
}
