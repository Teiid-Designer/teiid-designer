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

package com.metamatrix.modeler.transformation.ui.search;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.metamatrix.modeler.transformation.ui.UiConstants;

/**
 * @since 5.0
 */

public class OpenTransformationSearchPageAction implements
                                               IWorkbenchWindowActionDelegate,
                                               UiConstants {

    
    private static final String DIALOG_TITLE = UiConstants.Util.getString("OpenTransformationSearchPageAction.dialog.title"); //$NON-NLS-1$
    
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    private IWorkbenchWindow window;
    
    private Shell shell;


    /** 
     * 
     * @since 5.0
     */
    public OpenTransformationSearchPageAction() {
        super();
    }
    
    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        this.window = null;
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow theWindow) {
        this.window = theWindow;
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction theAction) {
        if (this.window == null || this.window.getActivePage() == null) {
            String msg = Util.getString("OpenTransformationSearchPageAction.msg.nullWindow"); //$NON-NLS-1$
            Util.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg, null));
            return;
        }

        Dialog dialog = new TransformationSearchDialog(this.window.getShell(), DIALOG_TITLE);
        dialog.open();

    }
    
    public void setShell(Shell theShell) {
        shell = theShell;
    }
    
    /**
     * 
     */
    public void run() {
        if (shell == null) {
            String msg = Util.getString("OpenTransformationSearchPageAction.msg.nullWindow"); //$NON-NLS-1$
            Util.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg, null));
            return;
        }

        Dialog dialog = new TransformationSearchDialog(shell, DIALOG_TITLE);
        dialog.open();

    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction theAction,
                                 ISelection theSelection) {
        // do nothing since the action isn't selection dependent.
    }


}
