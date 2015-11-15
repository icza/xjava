/*
 * Project XJava
 * 
 * Copyright (c) 2014 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package x.java.lang;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A normal {@link Thread} with some utility methods.
 * 
 * <p>
 * Instances do not take the inherited priority but instead set {@link Thread#NORM_PRIORITY} and install an
 * {@link java.lang.Thread.UncaughtExceptionHandler} which simply logs any uncaught exceptions.
 * </p>
 * 
 * <p>
 * Example usage of {@link NormalThread}:
 * 
 * <pre>
 * <blockquote style='border:1px solid black'>
 * final NormalThread nt = new NormalThread(&quot;Example&quot;) {
 *     public void run() {
 *         checkedSleep( 500 );
 *         System.out.println(&quot;I am a NormalThread.&quot;);
 *     }
 * });
 * 
 * // Start the normal thread:
 * nt.start();
 * 
 * System.out.println(&quot;I am not a NormalThread.&quot;);
 * // Wait for it to finish:
 * nt.waitToFinish();
 * </blockquote>
 * </pre>
 * 
 * </p>
 * 
 * @author Andras Belicza
 * 
 * @see CtrlThread
 */
public class NormalThread extends Thread {
    
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(NormalThread.class.getName());
    
    /**
     * A shared {@link java.lang.Thread.UncaughtExceptionHandler UncaughtExceptionHandler} implementation
     * which simply logs uncaught exceptions using <code>java.util.logging</code>.
     */
    public static final UncaughtExceptionHandler UNCAUGHT_EXCEPTION_HANDLER = new UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(final Thread t, final Throwable e) {
            LOGGER.log(Level.SEVERE, "Uncaught exception in thread: " + t.getName(), t);
        }
    };
    
    /**
     * Creates a new {@link NormalThread}.
     * 
     * @param name name of the thread
     */
    public NormalThread(final String name) {
        super(name);
        
        setPriority(NORM_PRIORITY);
        
        setUncaughtExceptionHandler(UNCAUGHT_EXCEPTION_HANDLER);
    }
    
    /**
     * Waits for this thread to finish.
     * 
     * @return true if thread finished properly; false otherwise (interrupted)
     */
    public boolean waitToFinish() {
        try {
            join();
            return true;
        } catch (final InterruptedException ie) {
            LOGGER.log(Level.WARNING, "Thread interrupted: " + getName(), ie);
            return false;
        }
    }
    
    /**
     * Sleeps for the specified amount of milliseconds.
     * 
     * @param ms milliseconds to sleep
     * @return the exception that was thrown if sleep was interrupted; <code>null</code> otherwise
     */
    public InterruptedException checkedSleep(final long ms) {
        try {
            sleep(ms);
            return null;
        } catch (final InterruptedException ie) {
            LOGGER.log(Level.WARNING, "Thread interrupted: " + getName(), ie);
            return ie;
        }
    }
    
}