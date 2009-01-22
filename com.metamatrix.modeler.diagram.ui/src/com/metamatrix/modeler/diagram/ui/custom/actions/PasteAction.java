/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.custom.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import com.metamatrix.modeler.diagram.ui.DiagramUiConstants;
import com.metamatrix.modeler.diagram.ui.actions.DiagramAction;

/**
 * PasteAction
 */
public class PasteAction 
     extends DiagramAction
  implements DiagramUiConstants  
 {
    //============================================================================================================================
    // Constants
    
    //============================================================================================================================
    // Constructors


    /**
     * Construct an instance of PasteAction.
     * 
     */
    public PasteAction() {
        super();
        final ISharedImages imgs = PlatformUI.getWorkbench().getSharedImages();
        setImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
        setDisabledImageDescriptor(imgs.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
    }

    //============================================================================================================================
    // ISelectionListener Methods
    
    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     * @since 4.0
     */
    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
        // sample code:
        super.selectionChanged(part, selection);
        
        setEnabled(false);
    }

    //============================================================================================================================
    // Action Methods

    /**
     * @see org.eclipse.jface.action.Action#run()
     * @since 4.0
     */
    @Override
    protected void doRun() {

        setEnableState();
    }
    /**
     * @since 4.0
     */
    private void setEnableState() {

    }
}
