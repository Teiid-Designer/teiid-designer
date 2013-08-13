/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;


/**
 * 
 *
 * @since 8.0
 */
public class SetDefaultServerAction extends BaseSelectionListenerAction implements IHandler {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The currently selected server
     */
    private ITeiidServer selectedServer;

    /**
     * Default Constructor
     */
    public SetDefaultServerAction() {
        super(UTIL.getString("setDefaultServerActionText")); //$NON-NLS-1$

        if (Platform.isRunning()) {
            setToolTipText(UTIL.getString("setDefaultServerActionToolTip")); //$NON-NLS-1$
            setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SET_DEFAULT_SERVER_ICON));
        }
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
        
        /*
         * Since this action can now be run from the RuntimeAssistant then
         * selectedServer may be null. In which case, need to ask the user
         * which server to be selected
         */
        if (this.selectedServer == null && !RuntimeAssistant.hasAvailableServers()) {
            String title = UTIL.getString("noServerAvailableTitle"); //$NON-NLS-1$
            String message = UTIL.getString("noServerAvailableMessage"); //$NON-NLS-1$
            MessageDialog.openError(getShell(), title, message);
            return;
        }
        else if (this.selectedServer == null) {
            this.selectedServer = RuntimeAssistant.selectServer(getShell(), true);
            if (RuntimeAssistant.selectServerWasCancelled()) return;
        }

        if (selectedServer == null) {
            getServerManager().setDefaultServer(null);
            String title = UTIL.getString("defaultServerChangedTitle"); //$NON-NLS-1$
            String message = UTIL.getString("defaultServerChangedMessage", "No Default"); //$NON-NLS-1$ //$NON-NLS-2$
            MessageDialog.openInformation(getShell(), title, message);
        	return;
        }
        
        ITeiidServer currentDefaultServer = this.getServerManager().getDefaultServer();
        
        /*
         * If a server version change is occurring then tell the user and ask them if its
         * alright to continue since this will close any open editors.
         */
        boolean continueChangingServer = false;
        if (changeOfServerVersion() && hasOpenEditors()) {
            continueChangingServer = MessageDialog.openQuestion(getShell(), 
                                       UTIL.getString("setDefaultServerActionVersionChangeTitle"),  //$NON-NLS-1$
                                       UTIL.getString("setDefaultServerActionVersionChangeMessage")); //$NON-NLS-1$
            
            if (! continueChangingServer)
                return;
        }
        
        /*
         * If old default teiid instance is connected, ask user if they wish to disconnect it.
         */
        boolean disconnectOldDefault = false;
        if( currentDefaultServer!=null && currentDefaultServer.isConnected() ) {
	    	disconnectOldDefault = MessageDialog.openQuestion(getShell(), 
	    			UTIL.getString("setDefaultServerActionDisconnectOldTitle"),  //$NON-NLS-1$
	    			UTIL.getString("setDefaultServerActionDisconnectOldMessage", currentDefaultServer.getDisplayName())); //$NON-NLS-1$
    	}
    	if( disconnectOldDefault ) {
    		currentDefaultServer.disconnect();
    	}
    	
    	/*
    	 * Set the default Teiid Instance
    	 */
        this.getServerManager().setDefaultServer(this.selectedServer);
        
        /*
         * If the new default teiid instance is connected then reconnect it to 
         * clear out any caches.
         */
        if( this.selectedServer.isConnected() ) {
        	final ITeiidServer theNewDefaultServer = this.selectedServer;
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
        
        String title = UTIL.getString("defaultServerChangedTitle"); //$NON-NLS-1$
        String message = UTIL.getString("defaultServerChangedMessage", selectedServer.getDisplayName()); //$NON-NLS-1$
        MessageDialog.openInformation(getShell(), title, message);
    }

    /**
     * @return the server manager
     */
    private ITeiidServerManager getServerManager() {
        return DqpPlugin.getInstance().getServerManager();
    }

    private boolean hasOpenEditors() {
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
            for (IWorkbenchPage page : window.getPages()) {
                if (page.getEditorReferences().length > 0)
                    return true;
            }
        }
        return false;
    }
    
    private boolean changeOfServerVersion() {
        ITeiidServer currentDefaultServer = this.getServerManager().getDefaultServer();
        if (currentDefaultServer == null)
            return true;
        
        return ! currentDefaultServer.getServerVersion().equals(selectedServer.getServerVersion());
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
        ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);

        // enable only if selected object is server and not same server
        if (teiidServer == null)
            return false;
        
        this.selectedServer = teiidServer;
        
        // No default teiid instance selected so display the action to allow it
        if (getServerManager().getDefaultServer() == null)
            return true;
        
        if (! this.selectedServer.equals(this.getServerManager().getDefaultServer()))
            return true;

        return false;
    }
    
    private static Shell getShell() {
    	return DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
    }

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
        // Not required
	}

	@Override
	public void dispose() {
        // Not required
	}

	@Override
	public Object execute(ExecutionEvent event) {
        run();
        return null;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
        // Not required
	}
}
