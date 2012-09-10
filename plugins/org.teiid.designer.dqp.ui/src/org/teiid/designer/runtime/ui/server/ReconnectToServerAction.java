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
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;


/**
 * The <code>ReconnectToServerAction</code> tries to reconnect to a selected server.
 *
 * @since 8.0
 */
public final class ReconnectToServerAction extends BaseSelectionListenerAction {

    /**
     * The server view tree viewer.
     */
    private final TreeViewer viewer;

    /**
     * @param viewer the server view tree viewer
     */
    public ReconnectToServerAction( TreeViewer viewer ) {
        super(UTIL.getString("serverReconnectActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("serverReconnectActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.REFRESH_ICON));
        setEnabled(false);

        this.viewer = viewer;
    }

    /**
     * @return the view's tree viewer
     */
    StructuredViewer getViewer() {
        return this.viewer;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IStructuredSelection sselection = getStructuredSelection();
        final TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(sselection);
        BusyIndicator.showWhile(getViewer().getControl().getDisplay(), new Runnable() {

            @Override
            public void run() {
                try {
                	// Call disconnect() first to clear out Server & admin caches
                	teiidServer.disconnect();
                    teiidServer.getAdmin().refresh();
                    teiidServer.setConnectionError(null);
                } catch (Exception e) {
                    UTIL.log(e);
                    String msg = UTIL.getString("serverReconnectErrorMsg", teiidServer); //$NON-NLS-1$
                    WidgetUtil.showError(msg);
                    teiidServer.setConnectionError(msg);
                } finally {
                    getViewer().refresh();
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.actions.BaseSelectionListenerAction#updateSelection(org.eclipse.jface.viewers.IStructuredSelection)
     */
    @Override
    protected boolean updateSelection( IStructuredSelection selection ) {
        if (selection.size() != 1)
            return false;
        
        TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);
        if (teiidServer != null)
            return true;
        
        return false;
    }

}
