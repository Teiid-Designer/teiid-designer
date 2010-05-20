/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import org.teiid.core.TeiidRuntimeException;

/**
 */
public class ModelerCoreRuntimeException extends TeiidRuntimeException {
    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * 
     */
    public ModelerCoreRuntimeException() {
        super();
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param message
     */
    public ModelerCoreRuntimeException(String message) {
        super(message);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param code
     * @param message
     */
    public ModelerCoreRuntimeException(int code, String message) {
        super(code, message);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param e
     */
    public ModelerCoreRuntimeException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param e
     * @param message
     */
    public ModelerCoreRuntimeException(Throwable e, String message) {
        super(e, message);
    }

    /**
     * Construct an instance of ModelerCoreRuntimeException.
     * @param e
     * @param code
     * @param message
     */
    public ModelerCoreRuntimeException(Throwable e, int code, String message) {
        super(e, code, message);
    }

}
