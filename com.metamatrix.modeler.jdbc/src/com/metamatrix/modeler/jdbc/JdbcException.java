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
