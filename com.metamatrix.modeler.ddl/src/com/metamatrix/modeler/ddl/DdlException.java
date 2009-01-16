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

package com.metamatrix.modeler.ddl;

import com.metamatrix.core.MetaMatrixCoreException;

/**
 * DdlException
 */
public class DdlException extends MetaMatrixCoreException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public DdlException() {
        super();
    }
    
    /**
     * Construct an instance of DdlException.
     * @param message
     */
    public DdlException(String message) {
        super(message);
    }

    /**
     * Construct an instance of DdlException.
     * @param e
     */
    public DdlException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of DdlException.
     * @param e
     * @param message
     */
    public DdlException(Throwable e, String message) {
        super(e, message);
    }

}
