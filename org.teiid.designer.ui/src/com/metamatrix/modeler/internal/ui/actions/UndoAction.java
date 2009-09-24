/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.core.util.Assertion;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.undo.IUndoManager;

/**
 * The <code>UndoAction</code> class is the action that handles the global undo.
 * 
 * @since 4.0
 */
public class UndoAction extends AbstractUndoRedoAction {

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    public UndoAction() {
        ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
        setDisabledImageDescriptor(images.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
        setAccelerator(SWT.CTRL | 'Z');
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
        Assertion.assertTrue((undoMgr != null) && undoMgr.canUndo());
        undoMgr.undo(monitor);
    }

    /**
     * @see com.metamatrix.modeler.internal.ui.actions.AbstractUndoRedoAction#updateState(com.metamatrix.modeler.ui.undo.IUndoManager)
     * @since 5.5
     */
    @Override
    protected void updateState(IUndoManager undoMgr) {
        boolean enable = false;
        String text = null;

        if ((undoMgr != null) && undoMgr.canUndo()) {
            enable = true;
            text = undoMgr.getUndoLabel();
        } else {
            text = UiConstants.Util.getString("com.metamatrix.modeler.internal.ui.actions.UndoAction.text"); //$NON-NLS-1$
        }

        setEnabled(enable);
        setText(text);
        setToolTipText(text);
    }
}
