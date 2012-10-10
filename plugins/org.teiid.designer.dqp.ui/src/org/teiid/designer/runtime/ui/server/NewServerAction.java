/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;


/**
 * The <code>NewServerAction</code> runs a UI that allows the user to create a new {@link TeiidServer server}.
 *
 * @since 8.0
 */
public class NewServerAction extends Action {

    /**
     * The server manager used to create and edit servers.
     */
    private final TeiidServerManager teiidServerManager;

    /**
     * The shell used to display the dialog that edits and creates servers.
     */
    private final Shell shell;

    /**
     * @param shell the parent shell used to display the dialog
     * @param teiidServerManager the server manager to use when creating and editing servers
     */
    public NewServerAction( Shell shell,
                            TeiidServerManager teiidServerManager ) {
        super(UTIL.getString("newServerActionText")); //$NON-NLS-1$
        CoreArgCheck.isNotNull(teiidServerManager, "serverManager"); //$NON-NLS-1$

        if (Platform.isRunning()) {
            setToolTipText(UTIL.getString("newServerActionToolTip")); //$NON-NLS-1$
            setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.NEW_SERVER_ICON));
        }

        this.shell = shell;
        this.teiidServerManager = teiidServerManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        ServerWizard wizard = new ServerWizard(this.teiidServerManager);      
        WizardDialog dialog = new WizardDialog(this.shell, wizard) {
            /**
             * {@inheritDoc}
             * 
             * @see org.eclipse.jface.wizard.WizardDialog#configureShell(org.eclipse.swt.widgets.Shell)
             */
            @Override
            protected void configureShell( Shell newShell ) {
                super.configureShell(newShell);
                if (Platform.isRunning()) {
                    newShell.setImage(DqpUiPlugin.getDefault().getImage(DqpUiConstants.Images.NEW_SERVER_ICON));
                }
            }
        };
        
        dialog.open();
    }
}
