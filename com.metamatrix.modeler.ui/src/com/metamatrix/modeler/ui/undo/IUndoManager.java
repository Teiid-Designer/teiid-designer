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

package com.metamatrix.modeler.ui.undo;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @since 5.5.3
 */
public interface IUndoManager {

    /**
     * @return <code>true</code> if there is anything to redo
     * @since 5.5.3
     */
    boolean canRedo();

    /**
     * @return <code>true</code> if there is anything to undo
     * @since 5.5.3
     */
    boolean canUndo();

    /**
     * @return the top most redo name or <code>null</code> if there are no changes to redo
     * @since 5.5.3
     */
    String getRedoLabel();

    /**
     * @return the the top most undo name or <code>null</code> if there are no changes to undo
     * @since 5.5.3
     */
    String getUndoLabel();

    /**
     * Redo the top most change.
     * 
     * @param monitor the progress monitor to report progress while performing the redo change
     * @since 5.5.3
     */
    void redo(IProgressMonitor monitor);

    /**
     * Undo the top most change.
     * 
     * @param monitor the progress monitor to report progress while performing the undo change
     * @since 5.5.3
     */
    void undo(IProgressMonitor monitor);

}
