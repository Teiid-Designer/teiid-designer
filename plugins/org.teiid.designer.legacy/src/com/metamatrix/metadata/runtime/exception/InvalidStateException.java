/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.exception;

import com.metamatrix.metadata.runtime.api.VirtualDatabaseException;

/**
 * Thrown when an update is requested on the VirtualDatabase and the VirtualDatabase is not in the proper state to allow it.
 */
public class InvalidStateException extends VirtualDatabaseException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public InvalidStateException() {
        super();
    }

    /**
     * Construct an instance with the message specified.
     * 
     * @param message A message describing the exception
     */
    public InvalidStateException( String message ) {
        super(message);
    }

    /**
     * Construct an instance with the message and error code specified.
     * 
     * @param message A message describing the exception
     * @param code The error code
     */
    public InvalidStateException( String code,
                                  String message ) {
        super(code, message);
    }

    /**
     * Construct an instance from a message and an exception to chain to this one.
     * 
     * @param code A code denoting the exception
     * @param e An exception to nest within this one
     */
    public InvalidStateException( Exception e,
                                  String message ) {
        super(e, message);
    }

    /**
     * Construct an instance from a message and a code and an exception to chain to this one.
     * 
     * @param e An exception to nest within this one
     * @param message A message describing the exception
     * @param code A code denoting the exception
     */
    public InvalidStateException( Exception e,
                                  String code,
                                  String message ) {
        super(e, code, message);
    }
}
