/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.Server;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * The <code>DeleteServerDialog</code> class provides a UI for deleting a {@link Server server}.
 */
public final class DeleteServerDialog extends MessageDialog {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * Collection of servers which will be deleted.
     */
    private final Collection<Server> serversBeingDeleted;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param parentShell the dialog parent
     * @param serversBeingDeleted the servers being deleted (never <code>null</code>)
     */
    public DeleteServerDialog( Shell parentShell,
                               Collection<Server> serversBeingDeleted ) {
        super(parentShell,
              UTIL.getString("deleteServerDialogTitle"), //$NON-NLS-1$
              DqpUiPlugin.getDefault().getImage(DqpUiPlugin.Images.DELETE_SERVER_ICON), null, MessageDialog.QUESTION,
              new String[] {IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL}, 0);

        CoreArgCheck.isNotNull(serversBeingDeleted, "serversBeingDeleted"); //$NON-NLS-1$
        this.serversBeingDeleted = serversBeingDeleted;

        // make sure dialog is resizable
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    @Override
    protected void configureShell( Shell shell ) {
        super.configureShell(shell);

        // now set message
        String msg;

        if (this.serversBeingDeleted.size() == 1) {
            Server server = this.serversBeingDeleted.iterator().next();
            msg = UTIL.getString("deleteServerDialogOneServerMsg", server.getUrl(), server.getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
        } else {
            msg = UTIL.getString("deleteServerDialogMultipleServersMsg", this.serversBeingDeleted.size()); //$NON-NLS-1$
        }

        this.message = msg;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createCustomArea( Composite parent ) {
        if (this.serversBeingDeleted.size() != 1) {
            List serverList = new List(parent, SWT.NONE);
            serverList.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
            gd.horizontalIndent = 40;
            serverList.setLayoutData(gd);

            for (Server server : this.serversBeingDeleted) {
                serverList.add(server.toString());
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.dialogs.Dialog#initializeBounds()
     */
    @Override
    protected void initializeBounds() {
        super.initializeBounds();
        // TODO position dialog
        // Utils.centerAndSizeShellRelativeToDisplay(getShell(), 75, 75);
    }

}
