/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.api;

import org.teiid.core.TeiidProcessingException;
/**
 * The base exception from which all Runtime Metadata Exceptions extend.
 */
public class VirtualDatabaseException extends TeiidProcessingException {

    public static final String NO_MODELS = "1"; //$NON-NLS-1$
    public static final String MODEL_NON_DEPLOYABLE_STATE = "2";  //$NON-NLS-1$
    public static final String VDB_NON_DEPLOYABLE_STATE = "3";  //$NON-NLS-1$

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public VirtualDatabaseException() {
        super();
    }
    
    /**
     * Construct an instance with the message specified.
     *
     * @param message A message describing the exception
     */
    public VirtualDatabaseException( String message ) {
        super( message );
    }

    /**
     * Construct an instance with the message and error code specified.
     *
     * @param message A message describing the exception
     * @param code The error code
     */
    public VirtualDatabaseException( String code, String message ) {
        super( code, message );
    }

    /**
     * Construct an instance from an exception to chain to this one.
     *
     * @param e An exception to nest within this one
     */
    public VirtualDatabaseException(Exception e) {
        super(e);
    }    
    /**
     * Construct an instance from a message and an exception to chain to this one.
     *
     * @param code A code denoting the exception
     * @param e An exception to nest within this one
     */
    public VirtualDatabaseException( Exception e, String message ) {
        super( e, message );
    }

    /**
     * Construct an instance from a message and a code and an exception to
     * chain to this one.
     *
     * @param e An exception to nest within this one
     * @param message A message describing the exception
     * @param code A code denoting the exception
     */
    public VirtualDatabaseException( Exception e, String code, String message ) {
        super( e, code, message );
    }
}

