/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.core.designer.exception;

import org.teiid.core.designer.CoreI18n;
import org.teiid.core.designer.I18n;

/**
 * Thrown when a method argument is <code>null</code> or empty.
 *
 * @since 8.0
 */
public class EmptyArgumentException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    /**
     * @param name the name of the argument
     */
    public EmptyArgumentException( final String name ) {
        super(I18n.format(CoreI18n.EMPTY_ARGUMENT_MSG, name));
    }
}
