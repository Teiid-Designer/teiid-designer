/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.ContentNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContainerNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContentNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IErrorNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IResourceNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.ITypeNode;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider;

/**
 * Root node of the {@link TeiidServer} tree view.
 * 
 * The node is cached against its {@link TeiidServerContentProvider} so that the same
 * node can be returned, allowing calls to method like setExpandedState(node, true) to
 * still work.
 * 
 * @since 8.0
 */
public class TeiidResourceNode extends ContentNode<ITypeNode> implements IResourceNode {
    
    private static Map<TeiidServerContentProvider, TeiidResourceNode> nodeCache = new WeakHashMap<TeiidServerContentProvider, TeiidResourceNode>();
    
    private ArrayList<IContentNode<? extends IContainerNode<?>>> children;
    private TeiidServerContentProvider provider;
    private TeiidServer teiidServer;

    private IErrorNode error;
    
    /**
     * @param server
     * @param provider
     * 
     * @return new or cached {@link TeiidResourceNode}
     */
    public static TeiidResourceNode getInstance(IServer server, TeiidServerContentProvider provider) {
        TeiidResourceNode node = nodeCache.get(provider);
        if (node == null) {
            node = new TeiidResourceNode(server, provider);
            nodeCache.put(provider, node); 
        } else {
            // Existing node but children may be out of date
            // Remove children so they are refreshed
            node.clearChildren();
        }
        
        return node; 
    }
    
    /**
     * Create a new instance
     * 
     * @param server
     * @param provider
     */
    private TeiidResourceNode(IServer server, TeiidServerContentProvider provider) {
        super(server, DqpUiConstants.UTIL.getString(TeiidResourceNode.class.getSimpleName() + ".label")); //$NON-NLS-1$
        this.provider = provider;
    }

    @Override
    public final List<? extends IContentNode<?>> getChildren() {
        if (error != null) {
            return Collections.singletonList(error);
        }
        
        return children;
    }
    
    @Override
    public final void load() {
        if (getServer().getServerState() != IServer.STATE_STARTED) {
            setError(new TeiidErrorNode(this, null, DqpUiConstants.UTIL.getString(getClass().getSimpleName() + ".labelNotConnected"))); //$NON-NLS-1$
            return;
        }
        
        try {
            teiidServer = (TeiidServer) getServer().loadAdapter(TeiidServer.class, null);

            if (teiidServer != null && teiidServer.isConnected()) {
                if (children == null)
                    children = new ArrayList<IContentNode<? extends IContainerNode<?>>>();
                    
                children.add(new TeiidServerContainerNode(this, provider));
            } else {
                setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(getClass().getSimpleName() + ".labelNotConnected"))); //$NON-NLS-1$
                return;
            }

            clearError();
            
        } catch (Exception e) {
            DqpUiConstants.UTIL.log(e);
            setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(getClass().getSimpleName() + ".labelRetrievalError"))); //$NON-NLS-1$
        }
    }
    
    @Override
    public final void clearChildren() {
        clearError();
        if (children != null) {
            for (IContentNode<? extends IContainerNode<?>> child : children) {
                child.dispose();
            }
            
            children.clear();
            children = null;
        }
    }
    
    private void clearError() {
        if (error != null) {
            error.dispose();
            error = null;
        }
    }
    
    protected void setError(IErrorNode error) {
        clearError();
        this.error = error;
    }
    
    @Override
    public void dispose() {
        clearChildren();
        super.dispose();
    }

    /**
     * @return the teiidServer
     */
    public TeiidServer getTeiidServer() {
        return this.teiidServer;
    }
    
    @Override
    public String getAddress() {
        if (getParent() == null) {
            // special handling for root node
            return ""; //$NON-NLS-1$
        }
        return getParent().getAddress() + PATH_SEPARATOR + getContainer().getName() + "=" + getName(); //$NON-NLS-1$
    }

    /**
     * Does this node have any children
     * 
     * @return true if there are children.
     */
    public boolean hasChildren() {
        return children != null && ! children.isEmpty();
    }
}
