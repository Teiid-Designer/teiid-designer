/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
