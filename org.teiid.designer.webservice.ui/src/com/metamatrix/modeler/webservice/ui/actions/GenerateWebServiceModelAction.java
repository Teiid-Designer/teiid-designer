/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.webservice.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.metamatrix.metamodels.xml.XmlDocument;
import com.metamatrix.metamodels.xml.XmlRoot;
import com.metamatrix.modeler.internal.webservice.ui.IInternalUiConstants;
import com.metamatrix.modeler.internal.webservice.ui.wizard.GenerateWebServiceModelWizard;
import com.metamatrix.modeler.webservice.ui.WebServiceUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * This action is specifically NOT extending ActionDelegate due to the fact
 * that it must operate on BOTH ModelObjects AND Resources.
 * 
 * This Action is added to both the Edit Menu and Context menus to drive
 * generation of XSD Schemas for usage as output documents for WebService deployments. 
 * @since 4.2
 */
public class GenerateWebServiceModelAction extends ActionDelegate implements
	IWorkbenchWindowActionDelegate, IViewActionDelegate, IInternalUiConstants {
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTANTS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    private ISelection selection;

	
	///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
    public GenerateWebServiceModelAction() {
    }
    
    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IAction action,
                                 ISelection theSelection) {
        this.selection = theSelection;
        action.setEnabled(isValidSelection());
    }

    @Override
    public void run(IAction action) {
        final GenerateWebServiceModelWizard wizard = new GenerateWebServiceModelWizard();
        wizard.init(WebServiceUiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), (IStructuredSelection)this.selection );
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        int rc = dialog.open();
        
        if( rc == Window.CANCEL ) {
            return;
        }
        
        final MultiStatus result = wizard.getResult();
        final int severity = result.getSeverity();
        if(severity == IStatus.ERROR) {
            final String errTitle = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.errTitle"); //$NON-NLS-1$
            final String err = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.errFinish"); //$NON-NLS-1$
            ErrorDialog.openError(wizard.getShell(), errTitle, err, result);
        }else if(severity == IStatus.WARNING) {
            final String warnTitle = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.warnTitle"); //$NON-NLS-1$
            final String warn = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.warnFinish"); //$NON-NLS-1$
            ErrorDialog.openError(wizard.getShell(), warnTitle, warn, result);            
        }else {
            final String okTitle = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.successTitle"); //$NON-NLS-1$
            final String ok = IInternalUiConstants.UTIL.getString("GenerateWebServiceModelAction.successFinish"); //$NON-NLS-1$

            List msgs = new ArrayList(result.getChildren().length);
            for( int i=0; i<result.getChildren().length; i++) {
                msgs.add(result.getChildren()[i].getMessage());
            }
            if(msgs.size()>0) {
            	ListMessageDialog.openInformation(wizard.getShell(), okTitle, null, ok, msgs , null); 
            } else {
            	MessageDialog.openInformation(wizard.getShell(),okTitle,ok);
            }
        }

    }
    
    /**
     * Valid selections include Relational Tables, Procedures or Relational Models.
     * The roots instance variable will populated with all Tables and Procedures contained
     * within the current selection. 
     * @return
     * @since 4.1
     */
    private boolean isValidSelection() {
        boolean isValid = true;
        if (SelectionUtilities.isEmptySelection(selection)) {
            isValid = false;
        }
        
        if ( isValid ) {
            final Collection objs = SelectionUtilities.getSelectedObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final Object next = selections.next();
                if (next instanceof XmlDocument || next instanceof XmlRoot) {
                    isValid = true;
                } else {
                    isValid = false;
                }
                
                // stop processing if no longer valid:
                if (!isValid) {
                    break;
                } // endif -- valid
            } // endwhile -- all selected
        } // endif -- is empty sel

        return isValid;
    }        

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
    }
    
}
