/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core;

import org.teiid.core.TeiidRuntimeException;

import com.metamatrix.core.modeler.CoreModelerPlugin;

/**
 * UserCancelledException
 */
public class UserCancelledException extends TeiidRuntimeException {

    /**
     */
    private static final long serialVersionUID = 1L;
    private static final String msg = CoreModelerPlugin.Util.getString("UserCancelledException.User_cancelled_operation_msg"); //$NON-NLS-1$

    /**
     * Construct an instance of UserCancelledException.
     */
    public UserCancelledException() {
        super(msg);
    }
}
