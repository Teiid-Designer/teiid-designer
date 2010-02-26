/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * The <code>EditServerAction</code> runs a UI that allows {@link PersistedServer server} properties to be changed.
 */
public final class EditServerAction extends BaseSelectionListenerAction {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The selected server being edited.
     */
    private Server serverBeingEdited;

    /**
     * The server manager used to create and edit servers.
     */
    private final ServerManager serverManager;

    /**
     * The shell used to display the dialog that edits and creates servers.
     */
    private final Shell shell;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param shell the parent shell used to display the dialog
     * @param serverManager the server manager to use when creating and editing servers
     */
    public EditServerAction( Shell shell,
                             ServerManager serverManager ) {
        super(UTIL.getString("editServerActionText.text()"));
        setToolTipText(UTIL.getString("editServerActionToolTip.text()"));
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(EDIT_SERVER_IMAGE));
        setEnabled(false);

        this.shell = shell;
        this.serverManager = serverManager;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        ServerWizard wizard = new ServerWizard(this.serverManager, this.serverBeingEdited);
        WizardDialog dialog = new WizardDialog(this.shell, wizard) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.wizard.WizardDialog#configureShell(org.eclipse.swt.widgets.Shell)
             */
            @Override
            protected void configureShell( Shell newShell ) {
                super.configureShell(newShell);
                newShell.setImage(DqpUiPlugin.getDefault().getImage(ModeShape_IMAGE_16x));
            }
        };

        dialog.open();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        // disable if empty selection or multiple objects selected
        if (selection.isEmpty() || (selection.size() > 1)) {
            this.serverBeingEdited = null;
            return false;
        }

        Object obj = selection.getFirstElement();

        // enable if server is selected
        if (obj instanceof Server) {
            this.serverBeingEdited = (Server)obj;
            return true;
        }

        // disable if non-server is selected
        this.serverBeingEdited = null;
        return false;
    }

}
