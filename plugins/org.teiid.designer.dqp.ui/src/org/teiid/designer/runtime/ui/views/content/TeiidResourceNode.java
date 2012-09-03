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
 * @since 8.0
 */
public class TeiidResourceNode extends ContentNode<ITypeNode> implements IResourceNode {

    private ArrayList<IContentNode<? extends IContainerNode<?>>> children;
    private TeiidServerContentProvider provider;
    private TeiidServer teiidServer;

    private IErrorNode error;
    
    /**
     * Create a new instance
     * 
     * @param server
     * @param provider
     */
    public TeiidResourceNode(IServer server, TeiidServerContentProvider provider) {
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
            children = new ArrayList<IContentNode<? extends IContainerNode<?>>>();
            teiidServer = (TeiidServer) getServer().loadAdapter(TeiidServer.class, null);
            
            if (teiidServer != null) {
                if (teiidServer.isConnected())
                    children.add(new TeiidServerContainerNode(this, provider));
                else {
                    setError(new TeiidErrorNode(this, teiidServer, 
                                                    DqpUiConstants.UTIL.getString(getClass().getSimpleName() + ".labelNotConnected"))); //$NON-NLS-1$
                    return;
                }
            }
            
            clearError();
        } catch (Exception e) {
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
}
