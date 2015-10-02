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
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider;

/**
 * Root node of the {@link ITeiidServer} tree view.
 * 
 * The node is cached against its {@link TeiidServerContentProvider} so that the same
 * node can be returned, allowing calls to method like setExpandedState(node, true) to
 * still work.
 * 
 * @since 8.0
 */
public class TeiidResourceNode extends TeiidContentNode implements ITeiidResourceNode {
    
    private static Map<String, ITeiidResourceNode> nodeCache = new WeakHashMap<String, ITeiidResourceNode>();
    
    private ArrayList<ITeiidContentNode<? extends ITeiidContainerNode<?>>> children;
    private TeiidServerContentProvider provider;
    private ITeiidServer teiidServer;

    private TeiidErrorNode error;

    private boolean dirty = true;
    
    /**
     * @param server
     * @param provider
     * 
     * @return new or cached {@link ITeiidResourceNode}
     */
    public static ITeiidResourceNode getInstance(IServer server, TeiidServerContentProvider provider) {
        String key = server.toString() + provider.toString();
        ITeiidResourceNode node = nodeCache.get(key);
        if (node == null) {
            node = new TeiidResourceNode(server, provider);
            nodeCache.put(key, node); 
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
    public void setDirty() {
        dirty = true;
    }

    @Override
    public final List<? extends ITeiidContentNode<?>> getChildren() {
        if (dirty) {
            /* 
             * node flagged as dirty so the children are out-of-date. Avoid
             * returning them thereby making the content provider reload 
             * them.
             */
            return null;
        }
        
        if (error != null) {
            return Collections.singletonList(error);
        }
        
        return children;
    }
    
    @Override
    public final void load() {
        clearChildren();
        
        if (getServer().getServerState() != IServer.STATE_STARTED) {
            setError(new TeiidErrorNode(this, null, DqpUiConstants.UTIL.getString(getClass().getSimpleName() + ".labelNotConnected"))); //$NON-NLS-1$
            dirty = false;
            return;
        }
        
        synchronized(provider) {
            try {
                teiidServer = (ITeiidServer)getServer().loadAdapter(ITeiidServer.class, null);
                if (teiidServer == null) {
                    setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(getClass().getSimpleName()
                                                                                                 + ".labelNotConnected"))); //$NON-NLS-1$
                    return;
                }
                if (teiidServer.isConnecting() && ! teiidServer.isConnected()) {
                    setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(getClass().getSimpleName()
                                                                                                 + ".labelConnecting"))); //$NON-NLS-1$
                    return;
                }

                if (teiidServer.isConnected()) {
                    if (children == null)
                        children = new ArrayList<ITeiidContentNode<? extends ITeiidContainerNode<?>>>();

                    children.add(new TeiidServerContainerNode(this, provider));

                    clearError();
                } else {
                    setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(getClass().getSimpleName()
                                                                                                 + ".labelNotConnected"))); //$NON-NLS-1$
                }

            } catch (Exception e) {
                DqpUiConstants.UTIL.log(e);
                setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(getClass().getSimpleName()
                                                                                             + ".labelRetrievalError"))); //$NON-NLS-1$
            } finally {
                dirty = false;
            }
        }
    }
    
    private void clearChildren() {
        synchronized (provider) {
            clearError();
            if (children != null) {
                for (ITeiidContentNode<? extends ITeiidContainerNode<?>> child : children) {
                    child.dispose();
                }

                children.clear();
                children = null;
            }

            teiidServer = null;
        }
    }
    
    private void clearError() {
        if (error != null) {
            error.dispose();
            error = null;
        }
    }
    
    protected void setError(TeiidErrorNode error) {
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
    @Override
    public ITeiidServer getTeiidServer() {
        return this.teiidServer;
    }

    @Override
    public boolean hasChildren() {
        if (dirty)
            return false;
        
        return children != null && ! children.isEmpty();
    }
    
    @Override
    public String toString() {
        if (teiidServer == null)
            return DqpUiConstants.UTIL.getString(TeiidResourceNode.class.getSimpleName() + ".labelNotConnected"); //$NON-NLS-1$
        
        String ttip = teiidServer.toString();
        if( teiidServer.getConnectionError() != null ) {
            ttip = ttip + "\n\n" + teiidServer.getConnectionError(); //$NON-NLS-1$
        }
        
        return ttip;
    }
}
