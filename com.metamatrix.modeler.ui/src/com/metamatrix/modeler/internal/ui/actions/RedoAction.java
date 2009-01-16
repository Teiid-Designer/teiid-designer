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

package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.undo.IUndoManager;

/**
 * The <code>RedoAction</code> class is the action that handles the global redo.
 * 
 * @since 4.0
 */
public class RedoAction extends AbstractUndoRedoAction {

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    public RedoAction() {
        ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
        setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_REDO_DISABLED));
        setAccelerator(SWT.CTRL | 'Y');
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * @see com.metamatrix.modeler.internal.ui.actions.AbstractUndoRedoAction#performAction(com.metamatrix.modeler.ui.undo.IUndoManager,
     *      org.eclipse.core.runtime.IProgressMonitor)
     * @since 5.5
     */
    @Override
    protected void performAction(IUndoManager undoMgr,
                                 IProgressMonitor monitor) {
        Assertion.assertTrue((undoMgr != null) && undoMgr.canRedo());
        undoMgr.redo(monitor);
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.actions.AbstractUndoRedoAction#updateState(com.metamatrix.modeler.ui.undo.IUndoManager)
     * @since 5.5
     */
    @Override
    protected void updateState(IUndoManager undoMgr) {
        boolean enable = false;
        String text = null;

        if ((undoMgr != null) && undoMgr.canRedo()) {
            enable = true;
            text = undoMgr.getRedoLabel();
        } else {
            text = UiConstants.Util.getString("com.metamatrix.modeler.internal.ui.actions.RedoAction.text"); //$NON-NLS-1$
        }

        setEnabled(enable);
        setText(text);
        setToolTipText(text);
    }
}
