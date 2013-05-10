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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.handlers.IHandlerService;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.UiUtil;


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
	private ITeiidServer serverBeingEdited = null;

    /**
     * The shell used to display the dialog that edits and creates servers.
     */
    private final Shell shell;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Default Constructor
     *
     * Used when called by the {@link IHandlerService}. Calls to the accompanying command should
     * provide an {@link Event} when executing the command and reference an {@link ITeiidServer}
     * in the event's data. Otherwise, the dialog for choosing a server will be displayed.
     */
    public EditServerAction() {
        super(UTIL.getString("editServerActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("editServerActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiPlugin.Images.EDIT_SERVER_ICON));
        setEnabled(true);

        // Will throw a RuntimeException if there is no workbench window and thus no shell
        this.shell = UiUtil.getWorkbenchShellOnlyIfUiThread();
    }
    
    /**
     * @param shell the parent shell used to display the dialog
     */
    public EditServerAction(Shell shell) {
        super(UTIL.getString("editServerActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("editServerActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiPlugin.Images.EDIT_SERVER_ICON));
        setEnabled(false);
        this.shell = shell;
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
        // Server may have already been selected by the action
        // having its updateSelection called. If it hasn't then find
        // a server to edit accordingly
        if(this.serverBeingEdited == null) {
            // Choose Server to Edit
            serverBeingEdited = RuntimeAssistant.selectServer(this.shell, false);
            if(RuntimeAssistant.selectServerWasCancelled())
                return;
        }

        if( serverBeingEdited == null ) {
            String title = UTIL.getString("noServerAvailableTitle"); //$NON-NLS-1$
            String message = UTIL.getString("noServerAvailableMessage"); //$NON-NLS-1$
            MessageDialog.openError(this.shell, title, message);
            return;
        }

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
	    // Determine whether the execution event carries an
	    // SWT event with it and whether its loaded wth a
	    // ITeiidServer reference.
	    Object object = event.getTrigger();
	    if (object instanceof Event) {
	        Event swtEvent = (Event) object;
	        Object data = swtEvent.data;
	        if (data instanceof ITeiidServer)
	            serverBeingEdited = (ITeiidServer) data;
	    }

        run();
        return null;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
        // Not required
	}

}
