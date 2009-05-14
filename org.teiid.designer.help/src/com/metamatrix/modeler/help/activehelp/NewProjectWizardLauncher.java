/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.help.activehelp;

import org.eclipse.help.ILiveHelpAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import com.metamatrix.modeler.ui.wizards.NewModelProjectWizard;

/**
 * @since 4.0
 */
public class NewProjectWizardLauncher extends Action implements ILiveHelpAction {
    private Shell shTheShell;

    public NewProjectWizardLauncher() {
    }

    public void setInitializationString( String sData ) {
        MessageDialog.openInformation(null, "[NewProjectWizardLauncher.setInitializationString] ", "TOP"); //$NON-NLS-1$ //$NON-NLS-2$

        // org.eclipse.platform.doc.isv.activeHelp.ActiveHelpOpenDialogAction

    }

    @Override
    public void run() {
        System.err.println("run"); //$NON-NLS-1$
        MessageDialog.openInformation(null, "[NewProjectWizardLauncher.run] ", "TOP"); //$NON-NLS-1$ //$NON-NLS-2$

        // Active help does not run on the UI thread, so we must use syncExec
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                if (window != null) {
                    // Bring the Workbench window to the top of other windows;
                    // On some Windows systems, it will only flash the Workbench
                    // icon on the task bar
                    Shell shell = window.getShell();
                    shell.setMinimized(false);
                    shell.forceActive();

                    IWorkbenchWizard wizard = new NewModelProjectWizard();
                    IStructuredSelection issSelection = null;

                    wizard.init(PlatformUI.getWorkbench(), issSelection);
                    WizardDialog dialog = new WizardDialog(getShell(), wizard);
                    dialog.open();
                }
            }
        });

        MessageDialog.openInformation(null, "[NewProjectWizardLauncher.run] ", "BOT"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    Shell getShell() {
        if (shTheShell == null) {
            IWorkbenchWindow iwwWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (iwwWindow != null) {
                shTheShell = iwwWindow.getShell();
            }
        }
        return shTheShell;
    }
}
