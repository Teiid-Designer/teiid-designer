/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.metamatrix.modeler.internal.ui.actions.workers.ImportActionUiWorker;
import com.metamatrix.modeler.ui.UiPlugin;

/**<p>
 * </p>
 * @since 4.0
 */
public final class ImportAction extends Action implements IWorkbenchWindowActionDelegate {
    //============================================================================================================================
    // Variables
    
    private IWorkbenchWindow wdw;
    private IStructuredSelection selection;
    
    //============================================================================================================================
    // Implemented Methods

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     * @since 4.0
     */
    public void dispose() {
    }

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     * @since 4.0
     */
    public void init(final IWorkbenchWindow window) {
        this.wdw = window;
    }

    /**<p>
     * </p>
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     * @since 4.0
     */
    public void run(final IAction action) {        
        run();
    }
    
    @Override
    public void run() {
        IStructuredSelection theSelection = this.selection;
        if (theSelection == null) {
            theSelection = StructuredSelection.EMPTY;
        } // endif
        
        IWorkbenchWindow ww = wdw;
        if (ww == null) {
            ww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        } // endif

        ImportActionUiWorker worker = new ImportActionUiWorker(theSelection, ww);
        notifyResult(worker.run());
    }

    /**<p>
     * Does nothing.
     * </p>
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     * @since 4.0
     */
    public void selectionChanged(final IAction action,
                                 final ISelection selection) {
        this.selection = (selection instanceof IStructuredSelection ? (IStructuredSelection)selection : null);
        action.setEnabled(true);
    }
}
