/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server.editor;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidServer;

/**
 *
 */
public class TeiidServerEditorInput implements IEditorInput, IPersistableElement {

    private String serverId;

    /**
     * @param serverId
     */
    public TeiidServerEditorInput(String serverId) {
        this.serverId = serverId;
    }
    
    /**
     * Returns the server id
     * @return unique id of this server
     */
    public String getServerId() {
        return serverId;
    }
    
    /**
     * Get the {@link ITeiidServer} referenced by this input
     * 
     * @return the {@link ITeiidServer} or null
     */
    public ITeiidServer getTeiidServer() {
        DqpPlugin dqpPlugin = DqpPlugin.getInstance();
        if (dqpPlugin == null) 
            return null;
        
        return dqpPlugin.getServerManager().getServer(serverId);
    }

    /**
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof TeiidServerEditorInput))
            return false;
        TeiidServerEditorInput other = (TeiidServerEditorInput) obj;
        if (serverId == null) {
            if (other.serverId != null)
                return false;   
        } else if (!serverId.equals(other.serverId))
            return false;
        return true;
    }

    @Override
    public boolean exists() {
        if (serverId != null && getTeiidServer() == null)
            return false;
        
        return true;
    }

    @Override
    public Object getAdapter(Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public String getFactoryId() {
        return TeiidServerEditorInputFactory.FACTORY_ID;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }
    
    @Override
    public String getName() {
        if (serverId != null) {
            ITeiidServer server = getTeiidServer();
            if (server != null)
                return server.getCustomLabel();
            
            return serverId;
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public IPersistableElement getPersistable() {
        return this;
    }

    @Override
    public String getToolTipText() {
        return getName();
    }

    @Override
    public void saveState(IMemento memento) {
        TeiidServerEditorInputFactory.saveState(memento, this);
    }
}