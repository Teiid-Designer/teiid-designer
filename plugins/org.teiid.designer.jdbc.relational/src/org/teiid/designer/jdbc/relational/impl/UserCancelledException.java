/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.relational.impl;


/**
 * UserCancelledException
 *
 * @since 8.0
 */
public class UserCancelledException extends RuntimeException {

    /**
     */
    private static final long serialVersionUID = 1L;
    private static final String msg = org.teiid.designer.jdbc.relational.ModelerJdbcRelationalConstants.Util.getString("UserCancelledException.User_cancelled_operation"); //$NON-NLS-1$

    /**
     * Construct an instance of UserCancelledException.
     * 
     */
    public UserCancelledException() {
        super(msg);
    }

    /**
     * Construct an instance of UserCancelledException.
     * @param message
     */
    public UserCancelledException(String message) {
        super(message);
    }

    /**
     * Construct an instance of UserCancelledException.
     * @param e
     */
    public UserCancelledException(Throwable e) {
        super(e);
    }

}
