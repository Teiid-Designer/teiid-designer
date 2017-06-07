/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.datasources.ui.sources;

import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.part.PluginDropAdapter;
import org.teiid.designer.core.ModelerCore;

public class DataSourcesDropAdapter extends PluginDropAdapter {
    /**
     * The current transfer data, or <code>null</code> if none.
     */
    private TransferData currentTransfer;
    private ConnectionProfilesPanel panel;
    
	public DataSourcesDropAdapter( StructuredViewer viewer, ConnectionProfilesPanel panel) {
        super(viewer);
        this.panel = panel;
    }
	
    @Override
    public void drop(DropTargetEvent event) {
        Object data = event.data;
        
        if( !panel.getManager().isServerAvailable() ) return;
        
        try {
            if (data instanceof IConnectionProfile) {
            	IConnectionProfile prof = (IConnectionProfile)data;
        		CreateDataSourceAction action = new CreateDataSourceAction(prof);
        		action.setTeiidServer(ModelerCore.getTeiidServerManager().getDefaultServer());
        		action.run();
        		panel.refresh();
            }
        } catch (Exception ce) {
            event.detail = DND.DROP_NONE;
        }

    }

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
        currentTransfer = transferType;
        if (currentTransfer != null && ConnectionProfileTransfer.getInstance().isSupportedType(currentTransfer)) {
            return true;
        }
        return false;
	}
}
