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

/**
 * {@link HttpPost} connection/communication state.
 * 
 * @author Andras Belicza
 */
public enum State {
    
    /** Not connected. */
    NOT_CONNECTED,
    
    /** Successfully connected. */
    CONNECTED,
    
    /** Connect failed. */
    CONNECT_FAILED,
    
    /** Request sent successfully. */
    REQUEST_SENT,
    
    /** Sending request failed. */
    SENDING_REQUEST_FAILED,
    
    /** Response processed successfully. */
    RESPONSE_PROCESSED,
    
    /** Processing response failed. */
    PROCESSING_RESPONSE_FAILED,
    
    /** Closed. */
    CLOSED;
    
}
