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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.teiid.designer.runtime.ITeiidServer;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;
import org.teiid.designer.runtime.ui.views.content.ITeiidResourceNode;
import org.teiid.designer.ui.common.util.WidgetUtil;
import org.teiid.designer.ui.common.viewsupport.UiBusyIndicator;


/**
 * The <code>RefreshServerAction</code> tries to refresh a connection to a selected server.
 *
 * @since 8.0
 */
public final class RefreshServerAction extends BaseSelectionListenerAction {

    private Display display;

    /**
     * @param display 
     */
    public RefreshServerAction(Display display) {
        super(UTIL.getString("serverRefreshActionText")); //$NON-NLS-1$
        this.display = display;
        setToolTipText(UTIL.getString("serverRefreshActionToolTip")); //$NON-NLS-1$
        setImageDescriptor(DqpUiPlugin.getDefault().getImageDescriptor(DqpUiConstants.Images.REFRESH_ICON));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IStructuredSelection sselection = getStructuredSelection();
        final ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(sselection);
        if (teiidServer == null)
            return;
        
        UiBusyIndicator.showWhile(display, new Runnable() {

            @Override
            public void run() {
                teiidServer.reconnect();
                
                if (teiidServer.getConnectionError() != null)
                    WidgetUtil.showError(teiidServer.getConnectionError());
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
        
        ITeiidServer teiidServer = RuntimeAssistant.getServerFromSelection(selection);
        if (teiidServer != null && teiidServer.isParentConnected())
            return true;
        
        Object element = selection.getFirstElement();
        if (RuntimeAssistant.adapt(element, ITeiidResourceNode.class) != null)
            return true;
        
        return false;
    }

}
