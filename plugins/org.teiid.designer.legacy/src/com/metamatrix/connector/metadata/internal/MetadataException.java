/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.connector.metadata.internal;

import org.teiid.core.TeiidComponentException;

public class MetadataException extends TeiidComponentException {

	/**
     */
    private static final long serialVersionUID = 1L;

    public MetadataException(Throwable t) {
		super(t);
	}
}
