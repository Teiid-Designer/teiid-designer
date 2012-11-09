/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.views.content.ITeiidResourceNode;
import org.teiid.designer.runtime.ui.views.content.TeiidServerContainerNode;

/**
 * Adapt a {@link TeiidServerContainerNode}
 */
public class TeiidServerContainerNodeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof TeiidServerContainerNode))
            return null;
        
        TeiidServerContainerNode serverContainerNode = (TeiidServerContainerNode) adaptableObject;
        
        if (TeiidServer.class == adapterType)
            return adaptToTeiidServer(serverContainerNode);
        
        if (ITeiidResourceNode.class == adapterType)
            return adaptToTeiidResourceNode(serverContainerNode);
        
        return null;
    }

    /**
     * Adapt to a {@link TeiidServer}
     * 
     * @param serverContainerNode
     */
    private TeiidServer adaptToTeiidServer(TeiidServerContainerNode serverContainerNode) {
        return serverContainerNode.getTeiidServer();
    }

    /**
     * Adapt to {@link ITeiidResourceNode}
     * 
     * @param serverContainerNode
     * @return
     */
    private ITeiidResourceNode adaptToTeiidResourceNode(TeiidServerContainerNode serverContainerNode) {
        return serverContainerNode.getContainer();
    }
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class, ITeiidResourceNode.class };
    }

}
