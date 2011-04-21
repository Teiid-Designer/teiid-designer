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
 * NavigationContextBuilderException
 */
public class NavigationContextBuilderException extends TeiidException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of NavigationContextBuilderException.
     * 
     */
    public NavigationContextBuilderException() {
        super();
    }


    /**
     * Construct an instance of NavigationContextBuilderException.
     * @param message
     */
    public NavigationContextBuilderException(String message) {
        super(message);
    }


    /**
     * Construct an instance of NavigationContextBuilderException.
     * @param e
     */
    public NavigationContextBuilderException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of NavigationContextBuilderException.
     * @param e
     * @param message
     */
    public NavigationContextBuilderException(Throwable e, String message) {
        super(e, message);
    }
}
