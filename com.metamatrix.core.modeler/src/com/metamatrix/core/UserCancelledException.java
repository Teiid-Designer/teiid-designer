/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core;

import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * UserCancelledException
 */
public class UserCancelledException extends MetaMatrixRuntimeException {

    /**
     */
    private static final long serialVersionUID = 1L;
    private static final String msg = CoreModelerPlugin.Util.getString("UserCancelledException.User_cancelled_operation_msg"); //$NON-NLS-1$

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
     * @param code
     * @param message
     */
    public UserCancelledException(int code, String message) {
        super(code, message);
    }

    /**
     * Construct an instance of UserCancelledException.
     * @param e
     */
    public UserCancelledException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of UserCancelledException.
     * @param e
     * @param message
     */
    public UserCancelledException(Throwable e, String message) {
        super(e, message);
    }

    /**
     * Construct an instance of UserCancelledException.
     * @param e
     * @param code
     * @param message
     */
    public UserCancelledException(Throwable e, int code, String message) {
        super(e, code, message);
    }

}
