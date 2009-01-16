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

package com.metamatrix.modeler.core.metamodel;

import com.metamatrix.core.MetaMatrixCoreException;

public class MultiplicityExpressionException extends MetaMatrixCoreException {


    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * No-arg costructor required by Externalizable semantics
     */
    public MultiplicityExpressionException() {
        super();
    }
    
    /**
     * Construct an instance of MultiplicityExpressionException.
     * @param message
     */
    public MultiplicityExpressionException(String message) {
        super(message);
    }

    /**
     * Construct an instance of MultiplicityExpressionException.
     * @param e
     */
    public MultiplicityExpressionException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of MultiplicityExpressionException.
     * @param e
     * @param message
     */
    public MultiplicityExpressionException(Throwable e, String message) {
        super(e, message);
    }
}
