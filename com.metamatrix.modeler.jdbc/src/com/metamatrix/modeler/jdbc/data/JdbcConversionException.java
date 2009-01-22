/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import com.metamatrix.modeler.jdbc.JdbcException;

/**
 * JdbcConversionException
 */
public class JdbcConversionException extends JdbcException {

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public JdbcConversionException() {
        super();
    }
    
    /**
     * Construct an instance of JdbcConversionException.
     * @param e
     * @param code
     */
    public JdbcConversionException(Throwable e, int code) {
        super(e, code);
    }

    /**
     * Construct an instance of JdbcConversionException.
     * @param message
     */
    public JdbcConversionException(String message) {
        super(message);
    }

    /**
     * Construct an instance of JdbcConversionException.
     * @param e
     */
    public JdbcConversionException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of JdbcConversionException.
     * @param e
     * @param message
     */
    public JdbcConversionException(Throwable e, String message) {
        super(e, message);
    }

}
