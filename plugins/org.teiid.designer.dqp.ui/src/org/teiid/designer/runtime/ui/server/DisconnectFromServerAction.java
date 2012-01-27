package org.teiid.designer.runtime.ui.server;

import static com.metamatrix.modeler.dqp.ui.DqpUiConstants.UTIL;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.Server;

import com.metamatrix.ui.internal.util.WidgetUtil;

public class DisconnectFromServerAction extends BaseSelectionListenerAction {

    /**
     * The server view tree viewer.
     */
    private final TreeViewer viewer;

    /**
     * @param viewer the server view tree viewer
     */
    public DisconnectFromServerAction( TreeViewer viewer ) {
        super(UTIL.getString("serverDisconnectActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("serverDisconnectActionToolTip")); //$NON-NLS-1$
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
                } catch (Exception e) {
                    UTIL.log(e);
                    String msg = UTIL.getString("serverReconnectErrorMsg", server); //$NON-NLS-1$
                    WidgetUtil.showError(msg);
                } finally {
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

