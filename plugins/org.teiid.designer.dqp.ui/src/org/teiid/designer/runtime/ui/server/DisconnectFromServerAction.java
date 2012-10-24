package org.teiid.designer.runtime.ui.server;

import static org.teiid.designer.runtime.ui.DqpUiConstants.UTIL;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;


/**
 * @since 8.0
 */
public class DisconnectFromServerAction extends BaseSelectionListenerAction {

    private final Display display;

    /**
     * @param display 
     */
    public DisconnectFromServerAction( Display display ) {
        super(UTIL.getString("serverDisconnectActionText")); //$NON-NLS-1$
        setToolTipText(UTIL.getString("serverDisconnectActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.SERVER_ERROR_ICON));
        setEnabled(false);

        this.display = display;
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
        UiBusyIndicator.showWhile(display, new Runnable() {

            @Override
            public void run() {
                try {
                	// Call disconnect() first to clear out Server & admin caches
                	teiidServer.disconnect();
                } catch (Exception e) {
                    UTIL.log(e);
                    String msg = UTIL.getString("serverReconnectErrorMsg", teiidServer); //$NON-NLS-1$
                    WidgetUtil.showError(msg);
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
        TeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);
        return ((selection.size() == 1) && (teiidServer != null));
    }

}

