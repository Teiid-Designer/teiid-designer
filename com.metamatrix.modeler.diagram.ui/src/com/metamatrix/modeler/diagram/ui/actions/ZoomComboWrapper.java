/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.diagram.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * ZoomInWrapper
 */
public class ZoomComboWrapper
    extends DiagramAction {
    
    /**
     * Construct an instance of ZoomInWrapper.
     * 
     */
    public ZoomComboWrapper() {
        super();
    }

    /**
     * Construct an instance of ZoomInWrapper.
     * @param theStyle
     */
    public ZoomComboWrapper(int theStyle) {
        super(theStyle);
    }


    @Override
    protected void doRun() {
    }
 

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart, ISelection theSelection) {
        // no action here...zooms do not care about selection        
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    public void setEnableState() {
    }
}
