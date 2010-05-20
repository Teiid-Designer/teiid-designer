/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.wizards;

import org.teiid.core.TeiidException;

/**
 * StructuralCopyException
 */
public class StructuralCopyException extends TeiidException {

    private static final long serialVersionUID = 1L;

    public StructuralCopyException(String message) {
		super(message);
	}
}
