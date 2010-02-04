/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.refactor.SaveModifiedResourcesDialog;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.wizards.CloneProjectWizard;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;


/** 
 * @since 5.0
 */
public class CloneProjectAction extends ActionDelegate implements IWorkbenchWindowActionDelegate,
IViewActionDelegate {

    /** 
     * 
     * @since 5.0
     */
    public CloneProjectAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    @Override
    public void run(IAction action) {
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
    }
    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        Object selectedObject = SelectionUtilities.getSelectedObject(selection);
        boolean enable = false;
        if ( selectedObject instanceof IResource && 
             selectedObject instanceof IProject && 
             ModelerCore.hasModelNature((IProject)selectedObject)) {
            enable=true;
        }
        action.setEnabled(enable);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window) {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view) {
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
