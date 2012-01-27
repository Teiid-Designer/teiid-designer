/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * The <code>DeleteServerAction</code> deletes one or more servers from the server registry.
 */
public final class DeleteServerAction extends BaseSelectionListenerAction {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The server manager used to delete servers.
     */
    private final ServerManager serverManager;

    /**
     * The servers being deleted (never <code>null</code>).
     */
    private final List<Server> serversToDelete;

    /**
     * The shell used to display the delete confirmation dialog.
     */
    private final Shell shell;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param shell the parent shell used to display the confirmation dialog
     * @param serverManager the server manager to use when deleting servers
     */
    public DeleteServerAction( Shell shell,
                               ServerManager serverManager ) {
        super(UTIL.getString("deleteServerActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("deleteServerActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiPlugin.Images.DELETE_SERVER_ICON));
        setEnabled(false);

        this.serversToDelete = new ArrayList<Server>(5);
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
        Dialog dialog = new DeleteServerDialog(this.shell, this.serversToDelete);

        if (dialog.open() == Window.OK) {
            boolean errorsOccurred = false;

            for (Server server : this.serversToDelete) {
                IStatus status = this.serverManager.removeServer(server);

                if (!status.isOK()) {
                    UTIL.log(status);

                    if (status.getSeverity() == IStatus.ERROR) {
                        errorsOccurred = true;
                    }
                }
            }

            if (errorsOccurred) {
                MessageDialog.openError(this.shell, UTIL.getString("errorDialogTitle.text()"), //$NON-NLS-1$
                                        UTIL.getString("deleteServerDialogErrorsOccurredMsg.text()")); //$NON-NLS-1$
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        // reset selected server collection
        this.serversToDelete.clear();

        // disable if empty selection
        if (selection.isEmpty()) {
            return false;
        }

        // disable if one non-server is found
        for (Object obj : selection.toArray()) {
            if (obj instanceof Server) {
                this.serversToDelete.add((Server)obj);
            } else {
                this.serversToDelete.clear();
                return false;
            }
        }

        // enable since all objects are servers
        return true;
    }

}
