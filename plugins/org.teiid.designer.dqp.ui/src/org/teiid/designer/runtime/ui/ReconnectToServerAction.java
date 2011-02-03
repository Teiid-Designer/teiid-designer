/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.Server;

import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * The <code>ReconnectToServerAction</code> tries to reconnect to a selected server.
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
        final Server server = (Server)getStructuredSelection().getFirstElement();
        BusyIndicator.showWhile(getViewer().getControl().getDisplay(), new Runnable() {

            @Override
            public void run() {
                try {
                	// Call disconnect() first to clear out Server & admin caches
                	server.disconnect();
                    server.getAdmin().refresh();
                    server.setConnectionError(null);
                } catch (Exception e) {
                    UTIL.log(e);
                    String msg = UTIL.getString("serverReconnectErrorMsg", server); //$NON-NLS-1$
                    WidgetUtil.showError(msg);
                    server.setConnectionError(msg);
                    getViewer().refresh(server);
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
        return ((selection.size() == 1) && (selection.getFirstElement() instanceof Server));
    }

}
