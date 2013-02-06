/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.TeiidServerManager;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiPlugin;


/**
 * The <code>EditServerAction</code> runs a UI that allows {@link ITeiidServer server} properties to be changed.
 *
 * @since 8.0
 */
public final class EditServerAction extends BaseSelectionListenerAction {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The selected server being edited.
     */
    private ITeiidServer serverBeingEdited;

    /**
     * The server manager used to create and edit servers.
     */
    private final TeiidServerManager teiidServerManager;

    /**
     * The shell used to display the dialog that edits and creates servers.
     */
    private final Shell shell;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param shell the parent shell used to display the dialog
     * @param teiidServerManager the server manager to use when creating and editing servers
     */
    public EditServerAction( Shell shell,
                             TeiidServerManager teiidServerManager ) {
        super(UTIL.getString("editServerActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("editServerActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiPlugin.Images.EDIT_SERVER_ICON));
        setEnabled(false);

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
    	// if serverBeingEdited == NULL need to query user to select one in order to continue
    	
    	if( this.serverBeingEdited == null) {
    	    this.serverBeingEdited = RuntimeAssistant.selectServer(shell);
    	}
    	
    	if( this.serverBeingEdited == null ) return;
    	    
    	DqpUiPlugin.editTeiidServer(serverBeingEdited);
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

}
