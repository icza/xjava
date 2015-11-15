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
 * A controlled thread with helper methods to pause/unpause or stop it.
 * 
 * <p>
 * Key methods to control a {@link CtrlThread}:
 * </p>
 * <ul>
 * <li>{@link #requestPause()} to pause it
 * <li>{@link #requestUnpause()} to unpause it
 * <li>{@link #requestCancel()} to cancel it
 * <li>{@link #waitToFinish()} to wait for the thread to end
 * <li>{@link #close()} to cancel the thread and wait for it to end
 * </ul>
 * 
 * <p>
 * 3 examples of proper {@link CtrlThread#customRun()} implementations in subclasses:
 * 
 * <pre>
 * <blockquote style='border:1px solid black'>
 * // The simplest and clearest:
 * public void customRun() {
 *     while (mayContinue() &amp;&amp; !done())
 *         doSomeWork();
 * }
 * 
 * // A lower lever usage/implementation:
 * public void customRun() {
 *     while (!cancelRequested &amp;&amp; !done()) {
 *         if (waitIfPaused())
 *             continue; // If execution was paused, &quot;continue&quot; so cancellation will be checked again first
 *         
 *         doSomeWork();
 *     }
 * }
 * 
 * // Usage/implementation when task is to walk a file tree (Files.walkFileTree()):
 * public void customRun() {
 *     try {
 *         Files.walkFileTree(folder, new SimpleFileVisitor&lt;Path&gt;() {
 *             &#064;Override
 *             public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
 *                 if (!mayContinue())
 *                     return FileVisitResult.TERMINATE;
 *                 
 *                 doSomethingWithFile(file);
 *                 
 *                 return FileVisitResult.CONTINUE;
 *             }
 *         } );
 *     } catch (IOException ie) {
 *         ie.printStackTrace();
 *     }
 * }
 * </blockquote>
 * </pre>
 * 
 * </p>
 * 
 * @author Andras Belicza
 */
public abstract class CtrlThread extends NormalThread {
    
    /** {@link Logger} used for logging. */
    private static final Logger LOGGER = Logger.getLogger(CtrlThread.class.getName());
    
    
    /**
     * Tells if a request has been made to cancel the execution of the thread.
     * 
     * <p>
     * The subclasses are responsible to periodically check this variable in their {@link #customRun()} method
     * whether they are allowed to continue their work or they have to return in order to end the thread.
     * </p>
     */
    protected volatile boolean cancelRequested;
    
    /**
     * Tells if a request has been made to pause the execution of the thread.
     * 
     * <p>
     * The subclasses are responsible to periodically check this variable in their {@link #customRun()} method
     * whether they are allowed to continue their work or they have to wait for either an unpause or cancel
     * request.
     * </p>
     */
    protected volatile boolean pauseRequested;
    
    /** Controlled state of the thread. */
    private volatile CtrlState ctrlState;
    
    /** Lock object to be owned for changing the controlled state. */
    private final Object STATE_LOCK = new Object();
    
    /** Execution start time. */
    protected long execStartTime;
    
    /** Execution end time. */
    protected long execEndTime;
    
    /** Time spent waiting in paused state. */
    protected long pausedTimeMs;
    
    /**
     * Creates a new {@link CtrlThread}.
     * 
     * @param name name of the thread
     */
    public CtrlThread(final String name) {
        super(name);
        
        ctrlState = CtrlState.NEW;
    }
    
    @Override
    public final void run() {
        synchronized (STATE_LOCK) {
            ctrlState = CtrlState.EXECUTING;
        }
        execStartTime = System.currentTimeMillis();
        
        try {
            customRun();
        } catch (final Throwable t) {
            LOGGER.log(Level.SEVERE, "Uncaught exception, prematurely ended thread: " + getName(), t);
        }
        
        execEndTime = System.currentTimeMillis();
        synchronized (STATE_LOCK) {
            ctrlState = CtrlState.ENDED;
        }
    }
    
    /**
     * Custom run method to do the work.
     */
    public abstract void customRun();
    
    /**
     * Requests canceling of the execution of the thread.
     */
    public void requestCancel() {
        // Volatile variables are synchronized internally, so no need external synchronization here.
        cancelRequested = true;
        
        updateCtrlState();
    }
    
    /**
     * Requests pausing of the execution of the thread.
     */
    public void requestPause() {
        if (pauseRequested)
            return; // Pause already requested
            
        // Volatile variables are synchronized internally, so no need external synchronization here.
        pauseRequested = true;
        
        updateCtrlState();
    }
    
    /**
     * Requests unpausing of the execution of the thread from a paused state.
     */
    public void requestUnpause() {
        if (!pauseRequested)
            return; // Pause not requested currently
            
        // Volatile variables are synchronized internally, so no need external synchronization here.
        pauseRequested = false;
        
        updateCtrlState();
    }
    
    /**
     * Updates the controlled state.
     */
    private void updateCtrlState() {
        synchronized (STATE_LOCK) {
            if (ctrlState == CtrlState.ENDED)
                return;
            
            if (cancelRequested) {
                ctrlState = CtrlState.EXECUTING_CANCEL_REQUESTED;
                return;
            }
            
            if (ctrlState != CtrlState.PAUSED && pauseRequested) {
                ctrlState = CtrlState.EXECUTING_PAUSE_REQUESTED;
                return;
            }
            
            ctrlState = CtrlState.EXECUTING;
        }
    }
    
    /**
     * Tells whether a cancel has been requested.
     * 
     * <p>
     * {@link CtrlThread} users are responsible to periodically check this whether they are allowed to
     * continue their work or they have to return in order to end the thread.
     * </p>
     * 
     * @return true if a cancel has been requested; false otherwise
     */
    public boolean isCancelRequested() {
        return cancelRequested;
    }
    
    /**
     * Tells whether a pause has been requested.
     * 
     * <p>
     * {@link CtrlThread} users are responsible to periodically check this whether they are allowed to
     * continue their work or they have to wait for either an unpause or cancel request.
     * </p>
     * 
     * @return true if a pause has been requested; false otherwise
     */
    public boolean isPauseRequested() {
        return cancelRequested;
    }
    
    /**
     * Returns the controlled state of the thread.
     * 
     * @return the controlled state of the thread
     */
    public CtrlState getCtrlState() {
        return ctrlState;
    }
    
    /**
     * If execution is paused, this method will block the thread until the execution is unpaused or cancelled.
     * 
     * <p>
     * This method should be called when a subclass wants to wait while the execution is paused, because this
     * method properly sets the controlled state to {@link CtrlState#PAUSED} while waiting, and also properly
     * handles execution times regarding wait time counting toward the paused time. Time waited inside this
     * method is properly excluded from execution time and is included in the paused time (
     * {@link #pausedTimeMs}).<br>
     * If a subclass implementation waits in some other way (e.g. using {@link Thread#sleep(long)}, those are
     * included in the execution time.
     * </p>
     * 
     * <p>
     * WARNING! This method can only be called from the job's executing thread!
     * </p>
     * 
     * @return true if the execution was paused and thread was blocked for some period of time; false
     *         otherwise
     * 
     * @see #mayContinue()
     * @see #guestMayContinue()
     */
    public boolean waitIfPaused() {
        if (!pauseRequested)
            return false;
        
        synchronized (STATE_LOCK) {
            ctrlState = CtrlState.PAUSED;
        }
        
        // Store waiting start time which will cause the execution time calculated properly,
        // because the time we wait here is only added at the end to the paused time,
        // but due to this it will properly be excluded from the execution time.
        execEndTime = System.currentTimeMillis();
        
        while (!cancelRequested && pauseRequested && checkedSleep(10) == null)
            ;
        
        pausedTimeMs += System.currentTimeMillis() - execEndTime;
        
        // Restore that execution has not yet been ended
        execEndTime = 0;
        
        synchronized (STATE_LOCK) {
            ctrlState = cancelRequested ? CtrlState.EXECUTING_CANCEL_REQUESTED : CtrlState.EXECUTING;
        }
        
        return true;
    }
    
    /**
     * Returns true if the thread is allowed to continue.
     * 
     * <p>
     * Besides checking and returning the inversion of {@link #cancelRequested} property this method also
     * waits if pause is requested (but returns false if cancel is requested while in the paused state).
     * </p>
     * 
     * <p>
     * WARNING! This method can only be called from the job's executing thread!
     * </p>
     * 
     * @return true if the thread is allowed to continue; false if the thread must terminate execution.
     * 
     * @see #waitIfPaused()
     * @see #guestMayContinue()
     */
    public boolean mayContinue() {
        // Cycle to wait out pause but abort if cancelled:
        while (!cancelRequested && waitIfPaused())
            ;
        
        return !cancelRequested;
    }
    
    /**
     * If job's execution is paused, this method will block the caller thread until the execution is unpaused
     * or cancelled.
     * 
     * <p>
     * This method should only be called from <i>guest</i> threads, meaning threads other than the job's
     * executing thread.
     * </p>
     * 
     * <p>
     * Purpose of this method is for example if the job spawns more threads and they want to respect the owner
     * job's paused state.
     * </p>
     * 
     * <p>
     * WARNING! This method is not for the job's executing thread!
     * </p>
     * 
     * @return true if the job's execution was paused and the caller thread was blocked for some period of
     *         time; false otherwise
     * 
     * @see #guestMayContinue()
     */
    public boolean guestWaitIfPaused() {
        if (!pauseRequested)
            return false;
        
        while (!cancelRequested && pauseRequested && checkedSleep(10) == null)
            ;
        
        return true;
    }
    
    /**
     * Returns true if the job's thread is allowed to continue.
     * 
     * <p>
     * Besides checking whether job was requested to be cancelled this method also waits if job pause is
     * requested (but returns false if cancel is requested while in the paused state).
     * </p>
     * 
     * <p>
     * Purpose of this method is for example if the job spawns more threads and they want to respect the owner
     * job's paused state.
     * </p>
     * 
     * <p>
     * WARNING! This method is not for the job's executing thread!
     * </p>
     * 
     * @return true if the job is allowed to continue; false if the job must terminate execution.
     * 
     * @see #guestWaitIfPaused()
     */
    public boolean guestMayContinue() {
        // Cycle to wait out pause but abort if cancelled:
        while (!cancelRequested && guestWaitIfPaused())
            ;
        
        return !cancelRequested;
    }
    
    /**
     * Returns the time spent waiting in paused state.
     * 
     * @return the time spent waiting in paused state
     */
    public long getPausedTimeMs() {
        return pausedTimeMs;
    }
    
    /**
     * Returns the execution time in ms.
     * 
     * @return the execution time in ms
     */
    public long getExecTimeMs() {
        final long end = execEndTime == 0 ? System.currentTimeMillis() : execEndTime;
        return end - execStartTime - pausedTimeMs;
    }
    
    /**
     * Properly closes this thread and waits for it to die.
     * 
     * <p>
     * First calls {@link #requestCancel()} and then waits for this thread to die by calling
     * {@link #waitToFinish()}.
     * </p>
     */
    public void close() {
        requestCancel();
        waitToFinish();
    }
    
}
