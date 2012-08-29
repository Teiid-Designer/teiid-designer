/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiPlugin;

/**
 * The <code>DeleteServerAction</code> deletes one or more servers from the server registry.
 *
 * @since 8.0
 */
public final class DeleteServerAction extends BaseSelectionListenerAction {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The server manager used to delete servers.
     */
    private final TeiidServerManager teiidServerManager;

    /**
     * The servers being deleted (never <code>null</code>).
     */
    private final List<TeiidServer> serversToDelete;

    /**
     * The shell used to display the delete confirmation dialog.
     */
    private final Shell shell;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param shell the parent shell used to display the confirmation dialog
     * @param teiidServerManager the server manager to use when deleting servers
     */
    public DeleteServerAction( Shell shell,
                               TeiidServerManager teiidServerManager ) {
        super(UTIL.getString("deleteServerActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("deleteServerActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiPlugin.Images.DELETE_SERVER_ICON));
        setEnabled(false);

        this.serversToDelete = new ArrayList<TeiidServer>(5);
        this.shell = shell;
        this.teiidServerManager = teiidServerManager;
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

            for (TeiidServer teiidServer : this.serversToDelete) {
                IStatus status = this.teiidServerManager.removeServer(teiidServer);

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
            if (obj instanceof TeiidServer) {
                this.serversToDelete.add((TeiidServer)obj);
            } else {
                this.serversToDelete.clear();
                return false;
            }
        }

        // enable since all objects are servers
        return true;
    }

}
