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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.refactor.SaveModifiedResourcesDialog;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.wizards.CloneProjectWizard;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 5.0
 */
public class CloneProjectAction2 extends AbstractAction {
    private static final String TITLE = UiConstants.Util.getString("CloneProjectAction2.title"); //$NON-NLS-1$
    /**
     * Construct an instance of ImportMetadata.
     */
    public CloneProjectAction2() {
        super(UiPlugin.getDefault());
        this.setText(TITLE);
        this.setToolTipText(TITLE);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CLONE_PROJECT_ICON));
        setEnabled(true);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(IWorkbenchPart, ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        
        Object selectedObject = SelectionUtilities.getSelectedObject(theSelection);
        boolean enable = false;
        if ( selectedObject instanceof IResource && 
             selectedObject instanceof IProject && 
             ModelerCore.hasModelNature((IProject)selectedObject)) {
            enable=true;
        }
        setEnabled(enable);
    }

    @Override
    protected void doRun() {
        // Changed to use method that insures Object editor mode is on

        // cleanup modified files before starting this operation
        boolean bContinue = doResourceCleanup();
        
        if ( !bContinue ) { return; }
        
        final IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        try {
            CloneProjectWizard wizard = new CloneProjectWizard();
            ISelection theSelection =  UiPlugin.getDefault().getPreviousViewSelection();
            
            // Set the project value for the wizard so it knows what to clone
            IProject theProject = (IProject)SelectionUtilities.getSelectedObject(theSelection);
            wizard.setProject(theProject);
            
            wizard.init(iww.getWorkbench(), (IStructuredSelection) theSelection);
            WizardDialog dialog = new WizardDialog(iww.getShell(), wizard);
            dialog.open();
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        setEnabled(true);
    }
    
    
    protected boolean doResourceCleanup() {
        boolean bResult = false;
        
        if ( ModelEditorManager.getDirtyResources().size() > 0 ) {
        
            SaveModifiedResourcesDialog pnlSave = new SaveModifiedResourcesDialog( getShell() );
            pnlSave.open();
            
            bResult = ( pnlSave.getReturnCode() == Window.OK );        
        } else {
            bResult = true;
        }
        
        return bResult;
    }
    
    //
    // Utility methods:
    //
    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
}
