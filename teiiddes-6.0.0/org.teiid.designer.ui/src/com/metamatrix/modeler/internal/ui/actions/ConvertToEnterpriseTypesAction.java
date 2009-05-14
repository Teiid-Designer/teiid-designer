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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.metamatrix.modeler.internal.core.workspace.ModelUtil;
import com.metamatrix.modeler.internal.ui.wizards.ConvertToEnterpriseTypesWizard;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * ModelStatisticsAction
 */
public class ConvertToEnterpriseTypesAction extends Action implements ISelectionListener, Comparable, ISelectionAction{

    private ISelection selection;

    /**
     * Construct an instance of RebuildImportsAction.
     */
    public ConvertToEnterpriseTypesAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run() {
        ConvertToEnterpriseTypesWizard wiz = new ConvertToEnterpriseTypesWizard();
        wiz.init(null, (IStructuredSelection)selection);
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
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        this.selection = selection;
        boolean enable = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IResource && ModelUtil.isXsdFile( (IResource) obj) ) {
                enable = ! ModelUtil.isIResourceReadOnly((IResource)obj);
            }
        }
        setEnabled(enable);
    }
    
    public int compareTo(Object o) {
        if( o instanceof String) {
            return getText().compareTo((String)o);
        }
        
        if( o instanceof Action ) {
            return getText().compareTo( ((Action)o).getText() );
        }
        return 0;
    }
    
    public boolean isApplicable(ISelection selection) {
        this.selection = selection;
        boolean enable = false;
        if ( ! SelectionUtilities.isMultiSelection(selection) ) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if ( obj instanceof IResource && ModelUtil.isXsdFile( (IResource) obj) ) {
                enable = ! ModelUtil.isIResourceReadOnly((IResource)obj);
            }
        }
        
        return enable;
    }

}
