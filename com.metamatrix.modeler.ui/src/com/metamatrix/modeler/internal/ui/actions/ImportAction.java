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
