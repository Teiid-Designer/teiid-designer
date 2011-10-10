/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.internal.ui.PluginConstants;
import com.metamatrix.modeler.internal.ui.refactor.SaveModifiedResourcesDialog;
import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.actions.ISelectionAction;
import com.metamatrix.modeler.ui.editors.ModelEditorManager;
import com.metamatrix.modeler.ui.wizards.CloneProjectWizard;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * @since 5.0
 */
public class CloneProjectAction2 extends Action implements ISelectionListener, Comparable, ISelectionAction {
    private static final String TITLE = UiConstants.Util.getString("CloneProjectAction2.title"); //$NON-NLS-1$

    private IProject selectedProject;

    /**
     * Construct an instance of ImportMetadata.
     */
    public CloneProjectAction2() {
        super();
        this.setText(TITLE);
        this.setToolTipText(TITLE);
        setImageDescriptor(UiPlugin.getDefault().getImageDescriptor(PluginConstants.Images.CLONE_PROJECT_ICON));
        setEnabled(true);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    // /////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 
     */
    public void selectionChanged( IWorkbenchPart part,
                                  ISelection selection ) {
        boolean enable = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IProject && ModelerCore.hasModelNature((IProject)obj)) {
                this.selectedProject = (IProject)obj;
                enable = true;
            }
        }
        setEnabled(enable);
    }

    public int compareTo( Object o ) {
        if (o instanceof String) {
            return getText().compareTo((String)o);
        }

        if (o instanceof Action) {
            return getText().compareTo(((Action)o).getText());
        }
        return 0;
    }

    public boolean isApplicable( ISelection selection ) {
        boolean result = false;
        if (!SelectionUtilities.isMultiSelection(selection)) {
            Object obj = SelectionUtilities.getSelectedObject(selection);
            if (obj instanceof IProject && ModelerCore.hasModelNature((IProject)obj)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public void run() {
        // Changed to use method that insures Object editor mode is on

        // cleanup modified files before starting this operation
        boolean bContinue = doResourceCleanup();

        if (!bContinue) {
            return;
        }

        final IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        try {
            CloneProjectWizard wizard = new CloneProjectWizard();
            // ISelection theSelection = UiPlugin.getDefault().getPreviousViewSelection();
            //
            // // Set the project value for the wizard so it knows what to clone
            // IProject theProject = (IProject)SelectionUtilities.getSelectedObject(theSelection);
            wizard.setProject(selectedProject);

            wizard.init(iww.getWorkbench(), new StructuredSelection(selectedProject));
            WizardDialog dialog = new WizardDialog(getShell(), wizard);
            dialog.open();
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }
        setEnabled(true);
    }

    protected boolean doResourceCleanup() {
        boolean bResult = false;

        if (ModelEditorManager.getDirtyResources().size() > 0) {

            SaveModifiedResourcesDialog pnlSave = new SaveModifiedResourcesDialog(getShell());
            pnlSave.open();

            bResult = (pnlSave.getReturnCode() == Window.OK);
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
