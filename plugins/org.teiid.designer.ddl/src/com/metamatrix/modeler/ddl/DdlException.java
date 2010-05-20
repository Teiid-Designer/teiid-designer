/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.ddl;

import org.teiid.core.TeiidException;

/**
 * DdlException
 */
public class DdlException extends TeiidException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public DdlException() {
        super();
    }
    
    /**
     * Construct an instance of DdlException.
     * @param message
     */
    public DdlException(String message) {
        super(message);
    }

    /**
     * Construct an instance of DdlException.
     * @param e
     */
    public DdlException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of DdlException.
     * @param e
     * @param message
     */
    public DdlException(Throwable e, String message) {
        super(e, message);
    }

}
