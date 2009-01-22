/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
