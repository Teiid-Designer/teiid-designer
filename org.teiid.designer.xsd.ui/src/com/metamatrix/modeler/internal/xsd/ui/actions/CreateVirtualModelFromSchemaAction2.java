/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
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
import com.metamatrix.modeler.internal.xsd.ui.wizards.CreateVirtualModelFromSchemaWizard;
import com.metamatrix.modeler.xsd.ui.ModelerXsdUiConstants;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

public class CreateVirtualModelFromSchemaAction2 extends ActionDelegate implements IWorkbenchWindowActionDelegate,
                                                                    IViewActionDelegate {

    private ISelection selection;
    private IWorkbench workbench;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public CreateVirtualModelFromSchemaAction2() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
    	CreateVirtualModelFromSchemaWizard wiz = new CreateVirtualModelFromSchemaWizard();
        wiz.init(workbench, (IStructuredSelection)selection);
        final WizardDialog dialog = new WizardDialog(wiz.getShell(), wiz);
        dialog.open();
        
        final MultiStatus result = wiz.getStatus();
        if(! result.isOK() ) {
            final int severity = result.getSeverity();
            switch (severity) {
                case IStatus.ERROR: {
                    final String errTitle = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaAction.errTitle"); //$NON-NLS-1$
                    final String err = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaAction.errFinish"); //$NON-NLS-1$
                    ErrorDialog.openError(wiz.getShell(), errTitle, err, result);                    
                    break;
                }
                case IStatus.WARNING: {
                    final String errTitle = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaAction.warnTitle"); //$NON-NLS-1$
                    final String err = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaAction.warnFinish"); //$NON-NLS-1$
                    ErrorDialog.openError(wiz.getShell(), errTitle, err, result);                    
                    break;                    
                }
                default: {
                    final String errTitle = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaAction.infoTitle"); //$NON-NLS-1$
                    final String err = ModelerXsdUiConstants.Util.getString("CreateVirtualModelFromSchemaAction.infoFinish"); //$NON-NLS-1$
                    ErrorDialog.openError(wiz.getShell(), errTitle, err, result);                                        
                    break;
                }
            }
        }
        
        ModelerXsdUiConstants.Util.log(result);
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
            if ( obj instanceof IFile && ModelUtil.isXsdFile( (IFile) obj) ) {
                enable = true;
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
