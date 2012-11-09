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
import org.teiid.designer.runtime.TeiidServer;

/**
 * @param <V> actual business objects this folder has as children
 * 
 * @since 8.0
 */
public abstract class AbstractTeiidFolder<V> implements ITeiidContainerNode<TeiidServerContainerNode> {
    
    private TeiidServerContainerNode parentNode;
    private Collection<V> theValues;
    private TeiidServer teiidServer;
    
    private List<ITeiidContentNode<? extends ITeiidContainerNode<?>>> children;
    
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
    public ITeiidResourceNode getParent() {
        return parentNode != null ? parentNode.getParent() : null;
    }

    @Override
    public TeiidServerContainerNode getContainer() {
        return parentNode;
    }
    
    @Override
    public void load() {
        if (getTeiidServer() == null || !getTeiidServer().isConnected()) {
            return;
        }
        
        children = new ArrayList<ITeiidContentNode<? extends ITeiidContainerNode<?>>>();
        for (V value : theValues) {
            TeiidDataNode dataNode = new TeiidDataNode(this, value);
            children.add(dataNode);
        }
    }
    
    @Override
    public List<? extends ITeiidContentNode<?>> getChildren() {
        return children;
    }
    
    @Override
    public boolean hasChildren() {
        return children != null && ! children.isEmpty();
    }
    
    @Override
    public void clearChildren() {
        if (children != null) {
            for (ITeiidContentNode<? extends ITeiidContainerNode<?>> child : children) {
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
