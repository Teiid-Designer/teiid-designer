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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.handlers.HandlerUtil;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;


/**
 * The <code>RefreshServerAction</code> tries to refresh a connection to a selected server.
 *
 * @since 8.0
 */
public final class RefreshServerAction extends BaseSelectionListenerAction implements IHandler {

    /**
     * The currently selected server
     */
    private ITeiidServer selectedServer;

    /**
     * Create new instance
     */
    public RefreshServerAction() {
        super(UTIL.getString("serverRefreshActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("serverRefreshActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.REFRESH_ICON));
        setId("org.teiid.designer.server.refresh"); //$NON-NLS-1$
    }

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
        if(this.selectedServer == null && !RuntimeAssistant.hasAvailableServers()) {
            String title = UTIL.getString("noServerAvailableTitle"); //$NON-NLS-1$
            String message = UTIL.getString("noServerAvailableMessage"); //$NON-NLS-1$
            MessageDialog.openError(getShell(), title, message);
            return;
        }
        else if (this.selectedServer == null) {
            this.selectedServer = RuntimeAssistant.selectServer(getShell(), false);
            if(RuntimeAssistant.selectServerWasCancelled()) return;
        }

        if (selectedServer == null) {
            String title = UTIL.getString("noServerAvailableTitle"); //$NON-NLS-1$
            String message = UTIL.getString("serverRefreshNoServer"); //$NON-NLS-1$
            MessageDialog.openInformation(getShell(), title, message);
            return;
        }

        UiBusyIndicator.showWhile(getShell().getDisplay(), new Runnable() {

            @Override
            public void run() {
                if (! selectedServer.isParentConnected()) {
                    WidgetUtil.showError(UTIL.getString("serverRefreshParentNotConnected", selectedServer.getParent())); //$NON-NLS-1$
                    return;
                }

                selectedServer.reconnect();

                if (selectedServer.getConnectionError() != null)
                    WidgetUtil.showError(selectedServer.getConnectionError());
                else
                    WidgetUtil.showNotification(UTIL.getString("serverRefreshServerSuccessful")); //$NON-NLS-1$
            }
        });
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
        
     // disable if empty selection
        if (selection.size() != 1) {
            return false;
        }
        
        // reset selected server collection
        ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);

        // enable only if selected object is server
        if (teiidServer == null)
            return false;
        
        this.selectedServer = teiidServer;

        return true;
    }

    @Override
    public Object execute(ExecutionEvent event) {
        ISelection currentSelection = HandlerUtil.getCurrentSelection(event);
        this.selectedServer = RuntimeAssistant.getServerFromSelection(currentSelection);
        run();
        return null;
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
    public void removeHandlerListener(IHandlerListener handlerListener) {
        // Not required
    }

}
