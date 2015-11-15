/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package xjava;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import xjava.res.Res;

/**
 * XJava project info class.
 * 
 * @author Andras Belicza
 */
public enum XJava {
    
    /** Singleton instance. */
    INSTANCE;
    
    
    /** Project name. */
    private final String projectName = "XJava";
    
    /** Project version. */
    private final String projectVersion = "0.1";
    
    /** Project full name (name + version). */
    private final String projectFullName = getProjectName() + " " + projectVersion;
    
    /** Project copyright years. */
    private final String copyrightYears = "2014";
    
    /** Project Author name. */
    private final String authorName = "Andras Belicza";
    
    /** Release build number. */
    private final int buildNumber;
    
    /** Release build time stamp (the number of milliseconds since January 1, 1970, 00:00:00 GMT). */
    private final long buildTimeStamp;
    
    
    /**
     * Creates a new {@link XJava}.
     */
    private XJava() {
        final Logger LOGGER = Logger.getLogger(XJava.class.getName());
        
        final Properties p = new Properties();
        try (InputStream is = Res.class.getResourceAsStream("build.properties")) {
            p.load(is);
            buildNumber = Integer.parseInt(p.getProperty("build.number"));
            buildTimeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(
                    p.getProperty("build.timestamp")).getTime();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load build properties!", e);
            throw new RuntimeException(e);
        }
        LOGGER.fine(String.format("Version: %s (build: %d)", projectFullName, buildNumber));
    }
    
    
    /**
     * Returns the project name.
     * 
     * @return the project name
     * 
     * @see #getProjectFullName()
     */
    public String getProjectName() {
        return projectName;
    }
    
    /**
     * Returns the project version.
     * 
     * @return the project version
     */
    public String getProjectVersion() {
        return projectVersion;
    }
    
    /**
     * Returns the project full name (name + version).
     * 
     * @return the project full name (name + version)
     */
    public String getProjectFullName() {
        return projectFullName;
    }
    
    /**
     * Returns the project copyright years.
     * 
     * @return the project copyright years
     */
    public String getCopyrightYears() {
        return copyrightYears;
    }
    
    /**
     * Returns the project author name.
     * 
     * @return the project author name
     */
    public String getAuthorName() {
        return authorName;
    }
    
    /**
     * Returns the release build number.
     * 
     * @return the release build number
     */
    public int getBuildNumber() {
        return buildNumber;
    }
    
    /**
     * Returns the release build time stamp, the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     * 
     * @return the release build time stamp
     */
    public long getBuildTimeStamp() {
        return buildTimeStamp;
    }
    
}
