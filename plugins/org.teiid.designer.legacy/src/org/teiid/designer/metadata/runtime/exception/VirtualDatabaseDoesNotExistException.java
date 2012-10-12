/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package org.teiid.designer.metadata.runtime.exception;

import org.teiid.designer.metadata.runtime.api.VirtualDatabaseException;

/**
 * Thrown when the VirtualDatabase is not found.
 *
 * @since 8.0
 */
public class VirtualDatabaseDoesNotExistException extends VirtualDatabaseException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public VirtualDatabaseDoesNotExistException() {
        super();
    }

    /**
     * Construct an instance with the message specified.
     * 
     * @param message A message describing the exception
     */
    public VirtualDatabaseDoesNotExistException( String message ) {
        super(message);
    }

    /**
     * Construct an instance from a message and an exception to chain to this one.
     * 
     * @param code A code denoting the exception
     * @param e An exception to nest within this one
     */
    public VirtualDatabaseDoesNotExistException( Exception e,
                                                 String message ) {
        super(e, message);
    }
}
