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

package com.metamatrix.modeler.core.container;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.modeler.core.ModelerCoreRuntimeException;


/** 
 * This exception signifies that a Resource could not be loaded into a Container because one
 * already existed (at a different URI).
 * @since 4.2
 */
public class DuplicateResourceException extends ModelerCoreRuntimeException {

    /**
     */
    private static final long serialVersionUID = 1L;
    private Resource duplicateOfModel;
    private IPath duplicateOfModelPath;
    
    /**
     * Construct an instance of DuplicateResourceException.
     * 
     */
    public DuplicateResourceException() {
        super();
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
    
    
    /**
     * Construct an instance of DuplicateResourceException.
     * @param message
     */
    public DuplicateResourceException(final Resource duplicateOfModel, final IPath duplicateOfModelPath, String message) {
        super(message);
        this.duplicateOfModel = duplicateOfModel;
        this.duplicateOfModelPath = duplicateOfModelPath;
    }
    
    
    
    /**
     * Construct an instance of DuplicateResourceException.
     * @param code
     * @param message
     */
    public DuplicateResourceException(int code, String message) {
        super(code, message);
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param e
     */
    public DuplicateResourceException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param e
     * @param message
     */
    public DuplicateResourceException(Throwable e, String message) {
        super(e, message);
    }

    /**
     * Construct an instance of DuplicateResourceException.
     * @param e
     * @param code
     * @param message
     */
    public DuplicateResourceException(Throwable e, int code, String message) {
        super(e, code, message);
    }
    
    public Resource getDuplicateOfModel() {
        return duplicateOfModel;
    }
    
    public IPath getDuplicateOfModelPath() {
        return duplicateOfModelPath;
    }
    
}
