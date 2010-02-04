/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship.ui.search;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.metamatrix.modeler.relationship.ui.UiConstants;

/**
 * OpenRelationshipSearchPageAction
 */
public class OpenRelationshipSearchPageAction implements IWorkbenchWindowActionDelegate,
                                                         UiConstants,
                                                         UiConstants.Extensions.SearchPage {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // FIELDS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private IWorkbenchWindow window;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////

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
            String msg = Util.getString("OpenRelationshipSearchPageAction.msg.nullWindow"); //$NON-NLS-1$
            Util.log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, msg, null));
            return;
        }

        NewSearchUI.openSearchDialog(this.window, ID); 
    }

    /**
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction theAction, ISelection theSelection) {
        // do nothing since the action isn't selection dependent.
    }

}
