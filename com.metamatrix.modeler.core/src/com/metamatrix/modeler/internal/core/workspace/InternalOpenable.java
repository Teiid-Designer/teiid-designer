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

package com.metamatrix.modeler.internal.core.workspace;

import org.eclipse.core.runtime.IProgressMonitor;

import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;

/**
 * InternalOpenable
 */
public interface InternalOpenable {

    /**
     * Open an <code>Openable</code> that is known to be closed (no check for <code>isOpen()</code>).
     */
    public void openWhenClosed(IProgressMonitor pm) throws ModelWorkspaceException;
    
//    /**
//     * Open an <code>Openable</code> that is known to be closed (no check for <code>isOpen()</code>).
//     * the force flag is used to determine whether to create underlying java.io.File or throw a runtime
//     * exception if it is not present.
//     */
//    public void openWhenClosed(IProgressMonitor pm, boolean force) throws ModelWorkspaceException;

}
