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

    private String serverUrl;

    /**
     * @param serverUrl
     */
    public TeiidServerEditorInput(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    /**
     * Returns the server url
     * @return unique url of this server
     */
    public String getServerUrl() {
        return serverUrl;
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
        
        return dqpPlugin.getServerManager().getServer(serverUrl);
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
        if (serverUrl == null) {
            if (other.serverUrl != null)
                return false;   
        } else if (!serverUrl.equals(other.serverUrl))
            return false;
        return true;
    }

    @Override
    public boolean exists() {
        if (serverUrl != null && getTeiidServer() == null)
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
        if (serverUrl != null) {
            ITeiidServer server = getTeiidServer();
            if (server != null)
                return server.getCustomLabel();
            
            return serverUrl;
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