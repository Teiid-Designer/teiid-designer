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

package com.metamatrix.modeler.relationship;

import com.metamatrix.core.MetaMatrixCoreException;

/**
 * NavigationContextBuilderException
 */
public class NavigationContextBuilderException extends MetaMatrixCoreException {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of NavigationContextBuilderException.
     * 
     */
    public NavigationContextBuilderException() {
        super();
    }


    /**
     * Construct an instance of NavigationContextBuilderException.
     * @param message
     */
    public NavigationContextBuilderException(String message) {
        super(message);
    }


    /**
     * Construct an instance of NavigationContextBuilderException.
     * @param e
     */
    public NavigationContextBuilderException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of NavigationContextBuilderException.
     * @param e
     * @param message
     */
    public NavigationContextBuilderException(Throwable e, String message) {
        super(e, message);
    }
}
