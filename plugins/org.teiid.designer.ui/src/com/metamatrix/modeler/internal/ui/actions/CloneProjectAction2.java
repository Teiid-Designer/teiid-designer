/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveableFilter;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.Saveable;

import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.wizards.CloneProjectWizard;
import com.metamatrix.ui.actions.AbstractAction;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;
import com.metamatrix.ui.internal.util.UiUtil;


/** 
 * @since 5.0
 */
public class CloneProjectAction2 extends AbstractAction {

    private static final String TITLE = UiConstants.Util.getString("CloneProjectAction2.title"); //$NON-NLS-1$

    /**
     * The selected project or <code>null</code>.
     */
    private IProject project;

    /**
     * A filter to save only models in the selected project. The selected project cannot be <code>null</code>.
     */
    private ISaveableFilter filter = new ISaveableFilter() {

        /**
         * {@inheritDoc}
         * 
         * @see org.eclipse.ui.ISaveableFilter#select(org.eclipse.ui.Saveable, org.eclipse.ui.IWorkbenchPart[])
         */
        @Override
        public boolean select( Saveable saveable,
                               IWorkbenchPart[] containingParts ) {
            // make sure first part is an editor
            if ((containingParts != null) && (containingParts.length != 0) && (containingParts[0] instanceof IEditorPart)) {
                IEditorInput input = ((IEditorPart)containingParts[0]).getEditorInput();

                if (input instanceof IFileEditorInput) {
                    IFile file = ((IFileEditorInput)input).getFile();

                    // if not a model editor don't force save as only models are cloned
                    if (getProject().equals(file.getProject())) {
                        return (ModelUtilities.getModelResourceForIFile(file, false) != null);
                    }
                }
            }

            // if no part or part is not an editor don't force save
            return false;
        }
    };

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

    /**
     * @return the selected project or <code>null</code>
     */
    protected IProject getProject() {
        return this.project;
    }
    
    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.ui.actions.AbstractAction#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    @Override
    public void selectionChanged(IWorkbenchPart thePart,
                                 ISelection theSelection) {
        super.selectionChanged(thePart, theSelection);
        
        this.project = null;
        Object selectedObject = SelectionUtilities.getSelectedObject(theSelection);
        boolean enable = false;

        if ( selectedObject instanceof IResource && 
             selectedObject instanceof IProject && 
             ModelerCore.hasModelNature((IProject)selectedObject)) {
            this.project = (IProject)selectedObject;
            enable=true;
        }
        setEnabled(enable);
    }

    /**
     * {@inheritDoc}
     *
     * @see com.metamatrix.ui.actions.AbstractAction#doRun()
     */
    @Override
    protected void doRun() {
        // Changed to use method that insures Object editor mode is on

        // cleanup modified files before starting this operation
        boolean bContinue = UiUtil.saveDirtyEditors(null, this.filter, true);
        
        if (!bContinue) {
            MessageDialog.openInformation(getShell(),
                                          UiConstants.Util.getString("CloneProjectAction.operationCanceledDialogTitle"), //$NON-NLS-1$
                                          UiConstants.Util.getString("CloneProjectAction.operationCanceledDialogMessage")); //$NON-NLS-1$
            return;
        }
        
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
    
    //
    // Utility methods:
    //
    protected Shell getShell() {
        return UiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
}
