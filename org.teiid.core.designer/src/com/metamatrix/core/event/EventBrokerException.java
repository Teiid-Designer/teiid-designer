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
public class EventBrokerException extends MetaMatrixCoreException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public EventBrokerException() {
        super();
    }

    /**
     * Construct an instance of EventBrokerException.
     * 
     * @param message
     */
    public EventBrokerException( String message ) {
        super(message);
    }

    /**
     * Construct an instance of EventBrokerException.
     * 
     * @param e
     */
    public EventBrokerException( Throwable e ) {
        super(e);
    }

    /**
     * Construct an instance of EventBrokerException.
     * 
     * @param e
     * @param message
     */
    public EventBrokerException( Throwable e,
                                 String message ) {
        super(e, message);
    }
}
