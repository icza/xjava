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

/**
 * State of a {@link CtrlThread}.
 * 
 * @author Andras Belicza
 */
public enum CtrlState {
    
    /** Created but not yet started. */
    NEW("New"),
    
    /** Executing. */
    EXECUTING("Executing"),
    
    /** Pause requested but still executing. */
    EXECUTING_PAUSE_REQUESTED("Executing (Pause requested)"),
    
    /** Cancel requested but still executing. */
    EXECUTING_CANCEL_REQUESTED("Executing (Cancel requested)"),
    
    /** Paused. */
    PAUSED("Paused"),
    
    /** Ended. */
    ENDED("Ended");
    
    
    /** String value of the state. */
    public final String stringValue;
    
    
    /**
     * Creates a new {@link CtrlState}.
     * 
     * @param stringValue string value of the state
     */
    private CtrlState(final String stringValue) {
        this.stringValue = stringValue;
    }
    
    @Override
    public String toString() {
        return stringValue;
    }
    
}
