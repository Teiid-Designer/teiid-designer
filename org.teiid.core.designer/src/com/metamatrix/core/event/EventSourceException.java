/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.core.event;

import com.metamatrix.core.MetaMatrixCoreException;

/**
 * Subclasses of this exception typically only need to implement whatever constructors they need.
 * <p>
 */
public class EventSourceException extends MetaMatrixCoreException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public EventSourceException() {
        super();
    }

    /**
     * Construct an instance of EventSourceException.
     * 
     * @param message
     */
    public EventSourceException( String message ) {
        super(message);
    }

    /**
     * Construct an instance of EventSourceException.
     * 
     * @param e
     */
    public EventSourceException( Throwable e ) {
        super(e);
    }

    /**
     * Construct an instance of EventSourceException.
     * 
     * @param e
     * @param message
     */
    public EventSourceException( Throwable e,
                                 String message ) {
        super(e, message);
    }
}
