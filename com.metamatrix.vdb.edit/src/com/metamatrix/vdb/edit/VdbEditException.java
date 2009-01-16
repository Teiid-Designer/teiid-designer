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

package com.metamatrix.vdb.edit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.metamatrix.modeler.core.ModelerCoreException;

/**
 * VdbEditException
 */
public class VdbEditException extends ModelerCoreException {

    IStatus status;

    /**
     * No-arg constructor required by Externalizable semantics.
     */
    public VdbEditException() {
        super();
    }

    /**
     * Construct an instance of VdbEditException.
     * @param status
     */
    public VdbEditException(IStatus status) {
        this.status = status;
    }

    /**
     * Construct an instance of VdbEditException.
     * @param exception
     */
    public VdbEditException(CoreException exception) {
        super(exception);
    }

    /**
     * Construct an instance of VdbEditException.
     * @param message
     */
    public VdbEditException(String message) {
        super(message);
    }

    /**
     * Construct an instance of VdbEditException.
     * @param code
     * @param message
     */
    public VdbEditException(int code, String message) {
        this.status = new Status(IStatus.ERROR, "vdb.edit", code, message, null); //$NON-NLS-1$
    }

    /**
     * Construct an instance of VdbEditException.
     * @param e
     */
    public VdbEditException(Throwable e) {
        super(e);
    }

    /**
     * Construct an instance of VdbEditException.
     * @param e
     * @param message
     */
    public VdbEditException(Throwable e, String message) {
        this.status = new Status(IStatus.ERROR, "vdb.edit", 0, message, e); //$NON-NLS-1$
    }

    /**
     * Construct an instance of VdbEditException.
     * @param e
     * @param code
     */
    public VdbEditException(Throwable e, int code) {
        this.status = new Status(IStatus.ERROR, "vdb.edit", code, e.getMessage(), e); //$NON-NLS-1$
    }

    /**
     * Construct an instance of VdbEditException.
     * @param e
     * @param code
     * @param message
     */
    public VdbEditException(Throwable e, int code, String message) {
        this.status = new Status(IStatus.ERROR, "vdb.edit", code, message, e); //$NON-NLS-1$
    }
}
