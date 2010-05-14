/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.wizards.ConvertToEnterpriseTypesWizard;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ModelStatisticsAction
 */
public class ConvertToEnterpriseDatatypesAction extends ActionDelegate implements IWorkbenchWindowActionDelegate,
                                                                    IViewActionDelegate {

    private ISelection selection;
    private IWorkbench workbench;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public ConvertToEnterpriseDatatypesAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
        ConvertToEnterpriseTypesWizard wiz = new ConvertToEnterpriseTypesWizard();
        wiz.init(workbench, (IStructuredSelection)selection);
        final WizardDialog dialog = new WizardDialog(wiz.getShell(), wiz);
        dialog.open();
        
        final StringBuffer result = wiz.getMessages();
        if(result.length() > 0) {
            final String errTitle = UiConstants.Util.getString("ConvertToEnterpriseTypesAction.errTitle"); //$NON-NLS-1$
            final String err = UiConstants.Util.getString("ConvertToEnterpriseTypesAction.errFinish"); //$NON-NLS-1$
            final String errDetails = UiConstants.Util.getString("ConvertToEnterpriseTypesAction.errDetails"); //$NON-NLS-1$
            final Status status = new Status(IStatus.ERROR, UiConstants.PLUGIN_ID, -1, errDetails, null);
            ErrorDialog.openError(wiz.getShell(), errTitle, err, status);
        }    
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
        boolean enable = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IResource && ModelUtil.isXsdFile( (IResource) obj) ) {
                enable = !ModelUtil.isIResourceReadOnly((IResource)obj);
            }
        }
        action.setEnabled(enable);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
        this.workbench = window.getWorkbench();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
    }

}
