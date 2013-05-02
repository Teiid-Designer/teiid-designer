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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiPlugin;


/**
 * The <code>EditServerAction</code> runs a UI that allows {@link ITeiidServer server} properties to be changed.
 *
 * @since 8.0
 */
public final class EditServerAction extends BaseSelectionListenerAction implements IHandler {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * Flag determines if default is being edited, or supplied
     */
	ITeiidServer serverBeingEdited = null;
    private boolean editDefault = false;

    /**
     * The server manager used to create and edit servers.
     */
    private Shell shell;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Default Constructor
     */
    public EditServerAction( ) {
        super(UTIL.getString("editServerActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("editServerActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiPlugin.Images.EDIT_SERVER_ICON));
        setEnabled(true);
        this.shell = getShell();
        this.editDefault = true;
    }
    
    /**
     * @param shell the parent shell used to display the dialog
     * @param editDefault 'true' will edit the current default, 'false' will show a chooser dialog
     */
    public EditServerAction( Shell shell, boolean editDefault ) {
        this();
        setEnabled(false);
        this.shell = shell;
        this.editDefault = editDefault;
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
    	// Edit the Default server
    	if(this.editDefault) { 
    		serverBeingEdited = ModelerCore.getDefaultServer();
    	// Choose Server to Edit
    	} else {
    		serverBeingEdited = RuntimeAssistant.selectServer(this.shell,false);
    	}
    	
    	if(RuntimeAssistant.selectServerWasCancelled()) return;
    	
    	if( serverBeingEdited == null ) {
    	    String title = UTIL.getString("noServerAvailableTitle"); //$NON-NLS-1$
    	    String message = UTIL.getString("noServerAvailableMessage"); //$NON-NLS-1$
    	    MessageDialog.openError(this.shell, title, message);
    	    return;
    	}
    	    
    	DqpUiPlugin.editTeiidServer(serverBeingEdited);
    }

    private static Shell getShell() {
    	return DqpUiPlugin.getDefault().getCurrentWorkbenchWindow().getShell();
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

        ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);

        // enable if server is selected
        if (teiidServer != null) {
            this.serverBeingEdited = teiidServer;
            return true;
        }

        // disable if non-server is selected
        this.serverBeingEdited = null;
        return false;
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
