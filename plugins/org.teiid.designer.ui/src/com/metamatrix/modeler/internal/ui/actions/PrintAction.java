/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.actions.AbstractAction;

/**
 * PrintAction
 */
public class PrintAction extends AbstractAction {

    /*
     * This is the Modeler's default print action.  Real implementations 
     * must be contributed so as to replace this one.  That is why this
     * one defaults to disabled.
     */

    // ====================================================================
    // CONSTRUCTORS
    // ====================================================================
    
    public PrintAction() {
        super(UiPlugin.getDefault());
 
    }
    
    // ====================================================================
    // METHODS
    // ====================================================================
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
                                     
        super.selectionChanged(thePart, theSelection);
        setEnabled( false );
    }

	@Override
    protected void doRun() {
//        System.out.println("[PrintAction.doRun] TOP"); //$NON-NLS-1$
	}
}
