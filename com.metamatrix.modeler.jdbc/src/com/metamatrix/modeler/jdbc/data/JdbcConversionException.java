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
