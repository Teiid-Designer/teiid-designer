/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.vdb.ui.actions.help;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import com.metamatrix.modeler.internal.ui.actions.help.HelpProjectAction;
import com.metamatrix.modeler.internal.vdb.ui.wizards.NewVdbWizard;
import com.metamatrix.modeler.ui.UiPlugin;
import com.metamatrix.modeler.vdb.ui.VdbUiConstants;
import com.metamatrix.modeler.vdb.ui.VdbUiPlugin;
import com.metamatrix.ui.internal.eventsupport.SelectionUtilities;

/**
 * NewVdbAction is a hook for the active help system to run the New VDB wizard.
 * The action is not exposed anywhere in the Modeler ui.
 */
public class NewVdbAction extends HelpProjectAction {
    private static final String TITLE = VdbUiConstants.Util.getString("NewVdbAction.noProjectTitle"); //$NON-NLS-1$
    private static final String NO_PROJECT_MESSAGE = VdbUiConstants.Util.getString("NewVdbAction.noProjectMessage"); //$NON-NLS-1$
    private static final String NO_OPEN_PROJECT_MESSAGE = VdbUiConstants.Util.getString("NewVdbAction.noOpenProjectMessage"); //$NON-NLS-1$
    /**
     * Construct an instance of NewVdbAction.
     */
    public NewVdbAction() {
        super(TITLE, NO_PROJECT_MESSAGE, NO_OPEN_PROJECT_MESSAGE);
    }


    /* (non-Javadoc)
     * @see org.eclipse.jface.action.IAction#run()
     */
    @Override
    public void run() {
        final IWorkbenchWindow iww = VdbUiPlugin.getDefault().getCurrentWorkbenchWindow();
        ISelection theSelection =  UiPlugin.getDefault().getPreviousViewSelection();

        Object obj = SelectionUtilities.getSelectedObject(theSelection);
            
        if ( obj instanceof IContainer ) {
            if ( obj instanceof IProject && ! ((IProject) obj).isOpen() ) {
                notifyNoOpenProject();
                return;
            }
                 
        } else {
            theSelection = getFirstOpenProject();
        }
        
        if( !theSelection.isEmpty() ) {
            try {
                NewVdbWizard wizard = new NewVdbWizard();
                wizard.init(iww.getWorkbench(), (IStructuredSelection) theSelection);
                WizardDialog dialog = new WizardDialog(iww.getShell(), wizard);
                dialog.open();
            } catch (Exception e) {
                VdbUiConstants.Util.log(IStatus.ERROR, e, e.getMessage());
            }
        }

    }
}
