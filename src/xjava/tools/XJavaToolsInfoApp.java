/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package xjava.tools;

import xjava.XJava;

/**
 * A runnable application which is a registry of other tools bundled in the project.
 * 
 * @author Andras Belicza
 */
public class XJavaToolsInfoApp {
    
    /** List of available XJava tools. */
    private static final Class<?>[] TOOLS = {
    //
    XJavaToolsInfoApp.class, //
    };
    
    /**
     * Entry point of the application.
     * 
     * @param args arguments specified by the running environment
     */
    public static void main(final String[] args) {
        System.out.println(XJavaToolsInfoApp.class.getSimpleName());
        
        final XJava info = XJava.INSTANCE;
        
        System.out.println("\tLib  : " + info.getProjectFullName());
        System.out.printf("\tBuild: %d (%2$tY-%2$tm-%2$td)\n", info.getBuildNumber(),
                info.getBuildTimeStamp());
        System.out.printf("\tCopyright (c) %s, %s\n", info.getAuthorName(), info.getCopyrightYears());
        
        System.out.println("Available tools:");
        int maxLength = 0;
        for (final Class<?> c : TOOLS)
            maxLength = Math.max(maxLength, c.getSimpleName().length());
        
        for (final Class<?> c : TOOLS)
            System.out.printf("\t%-" + maxLength + "s - %s\n", c.getSimpleName(), c.getName());
    }
    
}
