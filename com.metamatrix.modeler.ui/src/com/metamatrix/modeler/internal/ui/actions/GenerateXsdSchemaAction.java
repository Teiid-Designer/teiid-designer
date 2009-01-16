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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;

import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.internal.ui.wizards.GenerateXsdWizard;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.product.ProductCustomizerMgr;
import com.metamatrix.ui.internal.widget.ListMessageDialog;

/**
 * This action is specifically NOT extending ActionDelegate due to the fact
 * that it must operate on BOTH ModelObjects AND Resources.
 * 
 * This Action is added to both the Edit Menu and Context menus to drive
 * generation of XSD Schemas for usage as output documents for WebService deployments. 
 * @since 4.2
 */
public class GenerateXsdSchemaAction extends ActionDelegate implements
	IWorkbenchWindowActionDelegate, IViewActionDelegate {
    
    private ISelection selection;

	
	///////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    ///////////////////////////////////////////////////////////////////////////////////////////////
	
    public GenerateXsdSchemaAction() {
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
        final GenerateXsdWizard wizard = new GenerateXsdWizard();
        wizard.init(UiPlugin.getDefault().getCurrentWorkbenchWindow().getWorkbench(), (IStructuredSelection)this.selection );
        final WizardDialog dialog = new WizardDialog(wizard.getShell(), wizard);
        int rc = dialog.open();
        
        if(rc!=Window.CANCEL) {
            final MultiStatus result = wizard.getResult();
            final int severity = result.getSeverity();
            if(severity == IStatus.ERROR) {
                final String errTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.errTitle"); //$NON-NLS-1$
                final String err = UiConstants.Util.getString("GenerateXsdSchemaAction.errFinish"); //$NON-NLS-1$
                ErrorDialog.openError(wizard.getShell(), errTitle, err, result);
            }else if(severity == IStatus.WARNING) {
                final String warnTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.warnTitle"); //$NON-NLS-1$
                final String warn = UiConstants.Util.getString("GenerateXsdSchemaAction.warnFinish"); //$NON-NLS-1$
                ErrorDialog.openError(wizard.getShell(), warnTitle, warn, result);            
            }else {
                final String okTitle = UiConstants.Util.getString("GenerateXsdSchemaAction.successTitle"); //$NON-NLS-1$
                final String ok = UiConstants.Util.getString("GenerateXsdSchemaAction.successFinish"); //$NON-NLS-1$
    
                List msgs = new ArrayList(result.getChildren().length);
                for( int i=0; i<result.getChildren().length; i++) {
                    msgs.add(result.getChildren()[i].getMessage());
                }
                // Defect 20589 - Thread off this dialog, so it shows up AFTER auto-build and other jobs which are 
                // causing more Progress monitors to appear (lots of flashing).
                final List messgs = msgs;
                Display.getCurrent().asyncExec(new Runnable() {  
                    public void run() {   
                            ListMessageDialog.openInformation(wizard.getShell(), okTitle, null, ok, messgs , null);
                    }
                });                          
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
        } else if (ProductCustomizerMgr.getInstance() != null && Platform.getProduct() != null) {
            if (ProductCustomizerMgr.getInstance().getProductId() != null) {
                if ( ! ProductCustomizerMgr.getInstance().getProductId().equals(Platform.getProduct().getId())) {
                   isValid = false;
                }
            }
        }
        
        if ( isValid ) {
            final Collection objs = SelectionUtilities.getSelectedObjects(selection);
            final Iterator selections = objs.iterator();
            while (selections.hasNext() && isValid) {
                final Object next = selections.next();
                if (next instanceof Table) {
                    isValid = true;
                }else if(next instanceof Procedure) {
                    isValid = true;
                }else if (next instanceof IFile) {
                    final ModelResource modelResource = ModelerCore.getModelWorkspace().findModelResource((IFile) next);
                    if (modelResource != null) {
                        try {
                            // defect 19183 - do not load models on selection:
                            isValid = ModelUtilities.isRelationalModel(modelResource);
                        } catch (ModelWorkspaceException err) {
                            UiConstants.Util.log(err);
                            isValid = false;
                        }
                    } else {
                        isValid = false;
                    }
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
