/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import org.teiid.core.TeiidException;

/**
 * NavigationContextException
 */
public class NavigationContextException extends TeiidException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of NavigationContextException.
     * 
     */
    public NavigationContextException() {
        super();
    }


    /**
     * Construct an instance of NavigationContextException.
     * @param message
     */
    public NavigationContextException(String message) {
        super(message);
    }

    /**
     * Construct an instance of NavigationContextException.
     * @param e
     */
    public NavigationContextException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of NavigationContextException.
     * @param e
     * @param message
     */
    public NavigationContextException(Throwable e, String message) {
        super(e, message);
    }
}
