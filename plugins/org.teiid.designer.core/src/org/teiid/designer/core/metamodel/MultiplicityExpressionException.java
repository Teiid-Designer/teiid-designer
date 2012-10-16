/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core.metamodel;

import org.teiid.core.designer.TeiidDesignerException;

/**
 * @since 8.0
 */
public class MultiplicityExpressionException extends TeiidDesignerException {


    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public MultiplicityExpressionException() {
        super();
    }
    
    /**
     * Construct an instance of MultiplicityExpressionException.
     * @param message
     */
    public MultiplicityExpressionException(String message) {
        super(message);
    }

    /**
     * Construct an instance of MultiplicityExpressionException.
     * @param e
     */
    public MultiplicityExpressionException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of MultiplicityExpressionException.
     * @param e
     * @param message
     */
    public MultiplicityExpressionException(Throwable e, String message) {
        super(e, message);
    }
}
