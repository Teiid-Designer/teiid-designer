/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContainerNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContentNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IResourceNode;
import org.teiid.designer.runtime.TeiidServer;

/**
 * @since 8.0
 */
public abstract class AbstractTeiidFolder<V> implements IContainerNode<TeiidServerContainerNode> {
    
    private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$
    
    private TeiidServerContainerNode parentNode;
    private Collection<V> theValues;
    private TeiidServer teiidServer;
    
    private List<IContentNode<? extends IContainerNode<?>>> children;
    
    /**
     * Create new instance
     * 
     * @param parentNode 
     * @param theValues
     */
    public AbstractTeiidFolder(TeiidServerContainerNode parentNode, Collection<V> theValues) {
        this.parentNode = parentNode;
        this.teiidServer = parentNode.getTeiidServer();
        this.theValues = theValues;
    }
    
    @Override
    public IServer getServer() {
       return teiidServer != null ? teiidServer.getParent() : null;
    }
    
    /**
     * Get the {@link TeiidServer} that this folder belongs to
     * 
     * @return teiidServer
     */
    public TeiidServer getTeiidServer() {
        return teiidServer;
    }

    @Override
    public IResourceNode getParent() {
        return parentNode != null ? parentNode.getParent() : null;
    }

    @Override
    public TeiidServerContainerNode getContainer() {
        return parentNode;
    }

    @Override
    public String getAddress() {
        if (getParent() == null)
            return null;
        
        return getParent().getAddress() + PATH_SEPARATOR + getName();
    }
    
    @Override
    public void load() {
        if (getTeiidServer() == null || !getTeiidServer().isConnected()) {
            return;
        }
        
        children = new ArrayList<IContentNode<? extends IContainerNode<?>>>();
        for (V value : theValues) {
            TeiidDataNode dataNode = new TeiidDataNode(this, value);
            children.add(dataNode);
        }
    }
    
    @Override
    public List<? extends IContentNode<?>> getChildren() {
        return children;
    }
    
    @Override
    public void clearChildren() {
        if (children != null) {
            for (IContentNode<? extends IContainerNode<?>> child : children) {
                child.dispose();
            }
            children.clear();
            children = null;
        }
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public void dispose() {
        this.parentNode = null;
        this.teiidServer = null;
        this.theValues = null;
    }
}
