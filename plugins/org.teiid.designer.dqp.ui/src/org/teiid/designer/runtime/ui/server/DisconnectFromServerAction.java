package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.ui.common.util.WidgetUtil;


/**
 * @since 8.0
 */
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
        final TeiidServer teiidServer = (TeiidServer)getStructuredSelection().getFirstElement();
        BusyIndicator.showWhile(getViewer().getControl().getDisplay(), new Runnable() {

            @Override
            public void run() {
                try {
                	// Call disconnect() first to clear out Server & admin caches
                	teiidServer.disconnect();
                } catch (Exception e) {
                    UTIL.log(e);
                    String msg = UTIL.getString("serverReconnectErrorMsg", teiidServer); //$NON-NLS-1$
                    WidgetUtil.showError(msg);
                } finally {
                	getViewer().refresh(teiidServer);
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
        return ((selection.size() == 1) && (selection.getFirstElement() instanceof TeiidServer));
    }

}

