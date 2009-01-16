/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

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
