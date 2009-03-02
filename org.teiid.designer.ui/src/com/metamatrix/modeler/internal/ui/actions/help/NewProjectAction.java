/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.ui.actions.help;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;

import com.metamatrix.modeler.ui.UiConstants;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.ui.wizards.NewModelProjectWizard;

/**
 * NewProjectAction is a hook for the active help system to run the New Model Project wizard.
 * The action is not exposed anywhere in the Modeler ui.
 */
public class NewProjectAction extends Action {

    /**
     * Construct an instance of NewProjectAction.
     */
    public NewProjectAction() {
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        final IWorkbenchWindow iww = UiPlugin.getDefault().getCurrentWorkbenchWindow();
        try {
            NewModelProjectWizard wizard = new NewModelProjectWizard();
            ISelection theSelection =  UiPlugin.getDefault().getPreviousViewSelection();
            wizard.init(iww.getWorkbench(), (IStructuredSelection) theSelection);
            WizardDialog dialog = new WizardDialog(iww.getShell(), wizard);
            dialog.open();
        } catch (Exception e) {
            UiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
        }

    }

}
