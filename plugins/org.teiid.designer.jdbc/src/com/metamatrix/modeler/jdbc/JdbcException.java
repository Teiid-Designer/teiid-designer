/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import org.eclipse.core.runtime.IStatus;
import com.metamatrix.modeler.core.ModelerCoreException;


public class JdbcException extends ModelerCoreException {

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public JdbcException() {
        super();
    }
    
    /**
     * Construct an instance of JdbcException.
     * @param e
     * @param code
     */
    public JdbcException(Throwable e, int code) {
        super(e, code);
    }


    /**
     * Construct an instance of JdbcException.
     * @param status
     */
    public JdbcException(IStatus status) {
        super(status);
    }

    /**
     * Construct an instance of JdbcException.
     * @param message
     */
    public JdbcException(String message) {
        super(message);
    }

    /**
     * Construct an instance of JdbcException.
     * @param e
     */
    public JdbcException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of JdbcException.
     * @param e
     * @param message
     */
    public JdbcException(Throwable e, String message) {
        super(e, message);
    }

    /**
     * Subclasses may override this method, which is used by {@link #toString()} to obtain the "type"
     * for the exception class.
     * @return the type; defaults to "Modeler Core Exception"
     */
    @Override
    protected String getToStringType() {
        return "Modeler Core Exception"; //$NON-NLS-1$
    }
}
