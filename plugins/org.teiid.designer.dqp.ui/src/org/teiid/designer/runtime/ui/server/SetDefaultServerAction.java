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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;


/**
 * 
 *
 * @since 8.0
 */
public class SetDefaultServerAction extends BaseSelectionListenerAction {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The server manager used to create and edit servers.
     */
    private final TeiidServerManager teiidServerManager;

    /**
     * The servers being deleted (never <code>null</code>).
     */
    private TeiidServer selectedServer;

    /**
     * @param shell the parent shell used to display the dialog
     * @param teiidServerManager the server manager to use when creating and editing servers
     */
    public SetDefaultServerAction( TeiidServerManager teiidServerManager ) {
        super(UTIL.getString("setDefaultServerActionText")); //$NON-NLS-1$
        CoreArgCheck.isNotNull(teiidServerManager, "serverManager"); //$NON-NLS-1$

        if (Platform.isRunning()) {
            setToolTipText(UTIL.getString("setDefaultServerActionToolTip")); //$NON-NLS-1$
            setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SET_DEFAULT_SERVER_ICON));
        }

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
    	boolean disconnectOldDefault = false;
    	if( this.teiidServerManager.getDefaultServer().isConnected() ) {
	    	disconnectOldDefault = MessageDialog.openQuestion(getShell(), 
	    			UTIL.getString("setDefaultServerActionDisconnectOldTitle"),  //$NON-NLS-1$
	    			UTIL.getString("setDefaultServerActionDisconnectOldMessage", this.teiidServerManager.getDefaultServer().getUrl())); //$NON-NLS-1$
    	}
    	if( disconnectOldDefault ) {
    		this.teiidServerManager.getDefaultServer().disconnect();
    		
    	}
        this.teiidServerManager.setDefaultServer(this.selectedServer);
        if( !this.selectedServer.isConnected() ) {
        	final TeiidServer theNewDefaultServer = this.selectedServer;
            UiBusyIndicator.showWhile(Display.getDefault(), new Runnable() {

                @Override
                public void run() {
                    // Call disconnect() first to clear out Server & admin caches
                    theNewDefaultServer.reconnect();

                    if (theNewDefaultServer.getConnectionError() != null)
                        WidgetUtil.showError(theNewDefaultServer.getConnectionError());
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        // disable if empty selection
        if (selection.size() != 1) {
            return false;
        }
        
        // reset selected server collection
        TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);

        // enable only if selected object is server and not same server
        if (teiidServer != null) {
            this.selectedServer = teiidServer;
            if (this.teiidServerManager.getDefaultServer() != null
                && !this.selectedServer.equals(this.teiidServerManager.getDefaultServer())) {
                return true;
            }
        }

        return false;
    }
    
    private static Shell getShell() {
    	return DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }
}
