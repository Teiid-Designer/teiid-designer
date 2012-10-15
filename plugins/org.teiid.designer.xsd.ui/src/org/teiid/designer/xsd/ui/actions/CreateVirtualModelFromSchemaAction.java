/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.xsd.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.teiid.designer.core.workspace.ModelUtil;
import org.teiid.designer.ui.actions.ISelectionAction;
import org.teiid.designer.ui.common.eventsupport.SelectionUtilities;
import org.teiid.designer.xsd.ui.ModelerXsdUiConstants;
import org.teiid.designer.xsd.ui.ModelerXsdUiPlugin;
import org.teiid.designer.xsd.ui.wizards.CreateVirtualModelFromSchemaWizard;


/**
 * @since 8.0
 */
public class CreateVirtualModelFromSchemaAction extends Action implements ISelectionListener, Comparable, ISelectionAction{

    private ISelection selection;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public CreateVirtualModelFromSchemaAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
    	CreateVirtualModelFromSchemaWizard wiz = new CreateVirtualModelFromSchemaWizard();
        wiz.init(ModelerXsdUiPlugin.getDefault().getWorkbench(), (IStructuredSelection)selection);
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
            
            ModelerXsdUiConstants.Util.log(result);
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        this.selection = selection;
        boolean enable = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile && ModelUtil.isXsdFile( (IFile) obj) ) {
                enable = true;
            }
        }
        setEnabled(enable);
    }
    
    @Override
	public int compareTo(Object o) {
        if( o instanceof String) {
            return getText().compareTo((String)o);
        }
        
        if( o instanceof Action ) {
            return getText().compareTo( ((Action)o).getText() );
        }
        return 0;
    }
    
    @Override
	public boolean isApplicable(ISelection selection) {
        this.selection = selection;
        boolean enable = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IFile && ModelUtil.isXsdFile( (IFile) obj) ) {
                enable = true;
            }
        }
        
        return enable;
    }
}
