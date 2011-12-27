/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.designer.runtime.PreferenceConstants;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 * The <code>RuntimeAssistant<code> ensures that the preview preference is enabled and that a default Teiid server exists.
 */
public final class RuntimeAssistant {

    /**
     * The I18n properties prefix key used by this class.
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(RuntimeAssistant.class);

    /**
     * If the preview preference is disabled or a Teiid server does not exist, a dialog is shown asking the user if they want to
     * enable preview and create a server.
     * 
     * @param shell the shell used to display dialog if necessary
     * @return <code>true</code> if preview is enabled, a Teiid server exists, and a connection to the server has been made
     */
    public static boolean ensurePreviewEnabled( Shell shell ) {
        boolean previewEnabled = isPreviewEnabled();
        boolean previewServerExists = previewServerExists();

        // dialog message (null if preview preference enabled and server exists)
        String msg = null;

        if (!previewEnabled && !previewServerExists) {
            msg = UTIL.getString(PREFIX + "previewDisabledNoTeiidInstanceMsg"); //$NON-NLS-1$
        } else if (!previewEnabled) {
            msg = UTIL.getString(PREFIX + "previewDisabledMsg"); //$NON-NLS-1$
        } else if (!previewServerExists) {
            msg = UTIL.getString(PREFIX + "noTeiidInstanceMsg"); //$NON-NLS-1$
        }

        // if necessary open question dialog
        if ((msg != null) && MessageDialog.openQuestion(shell, UTIL.getString(PREFIX + "confirmEnablePreviewTitle"), msg)) { //$NON-NLS-1$
            // if necessary change preference
            if (!previewEnabled) {
                IEclipsePreferences prefs = DqpPlugin.getInstance().getPreferences();
                prefs.putBoolean(PreferenceConstants.PREVIEW_ENABLED, true);

                // save
                try {
                    prefs.flush();
                } catch (BackingStoreException e) {
                    UTIL.log(e);
                }
            }

            // if necessary create new server
            if (!previewServerExists) {
                runNewServerAction(shell);
            }
        }

        // if dialog was shown get values again
        if (msg != null) {
            previewEnabled = isPreviewEnabled();
            previewServerExists = previewServerExists();

            // if preview is not enabled or server does not exist then user canceled the dialog or the new server wizard
            if (!previewEnabled || !previewServerExists) {
                return false;
            }
        }

        // abort preview if server is not connected
        return serverConnectionExists(shell);
    }

    /**
     * If a Teiid server does not exist, a dialog is shown asking the user if they want to create a server.
     * 
     * @param shell the shell used to display dialog if necessary
     * @param dialogMessage the localized question to ask the user if they want to create a new Teiid instance in order to continue
     *            on with task
     * @return <code>true</code> if a Teiid server exists and can be connected to
     */
    public static boolean ensureServerConnection( Shell shell,
                                                  String dialogMessage ) {
        if (!previewServerExists()) {
            if (MessageDialog.openQuestion(shell, UTIL.getString(PREFIX + "confirmCreateTeiidInstanceTitle"), dialogMessage)) { //$NON-NLS-1$
                runNewServerAction(shell);
            }

            // if server does not exist then user canceled the dialog or the new server wizard
            if (!previewServerExists()) {
                return false;
            }
        }

        // abort preview if server is not connected
        return serverConnectionExists(shell);
    }

    /**
     * @return the default Teiid server (can be <code>null</code>)
     */
    private static Server getServer() {
        return getServerManager().getDefaultServer();
    }

    /**
     * @return the server manager (never <code>null</code>)
     */
    private static ServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    /**
     * @return <code>true</code> if the preview preference is enabled
     */
    private static boolean isPreviewEnabled() {
        return getServerManager().getPreviewManager().isPreviewEnabled();
    }

    /**
     * @return <code>true</code> if a default Teiid server exists
     */
    private static boolean previewServerExists() {
        return (getServerManager().getDefaultServer() != null);
    }

    /**
     * @param shell the shell used to run the new server wizard
     */
    private static void runNewServerAction( Shell shell ) {
        NewServerAction action = new NewServerAction(shell, getServerManager());
        action.setShowConnectionFailedDialog(false);
        action.run();
    }

    /**
     * @param shell the shell where the dialog is displayed if necessary
     * @return <code>true</code> if connection exists
     */
    private static boolean serverConnectionExists( Shell shell ) {
        assert (getServer() != null);

        if (!getServer().isConnected()) {
            MessageDialog.openError(shell, UTIL.getString(PREFIX + "teiidNotConnectedTitle"), //$NON-NLS-1$
                                    UTIL.getString(PREFIX + "teiidNotConnectedMsg", getServer().getHost())); //$NON-NLS-1$
            return false;
        }

        return true;
    }

    /**
     * Prevent instance construction.
     */
    private RuntimeAssistant() {
        // nothing to do
    }

}
