/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.teiid.designer.runtime.ServerManager;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * The <code>NewServerAction</code> runs a UI that allows the user to create a new {@link PersistedServer server}.
 */
public class NewServerAction extends Action {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

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
    public NewServerAction( Shell shell,
                            ServerManager serverManager ) {
        super(UTIL.getString("newServerActionText")); //$NON-NLS-1$
        CoreArgCheck.isNotNull(serverManager, "serverManager"); //$NON-NLS-1$

        if (Platform.isRunning()) {
            setToolTipText(UTIL.getString("newServerActionToolTip")); //$NON-NLS-1$
            setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.NEW_SERVER_ICON));
        }

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
        ServerWizard wizard = new ServerWizard(this.serverManager);
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

        int result = dialog.open();
        
        if( result == Dialog.OK) {
	        
	        if( wizard.shouldAutoConnect() ) {
	            	try {
	    				wizard.getServer().getAdmin();
	    			} catch (Exception e) {
	    				MessageDialog.openError(this.shell, UTIL.getString("newServerActionAutoConnectProblemTitle"), //$NON-NLS-1$
	                            UTIL.getString("serverWizardNewServerAutoConnectError")); //$NON-NLS-1$
	    			}

	        }
        }
    }

}
