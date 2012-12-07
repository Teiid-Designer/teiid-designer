/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content.adapter;

import java.util.List;
import org.eclipse.core.runtime.IAdapterFactory;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.views.content.ITeiidContentNode;
import org.teiid.designer.runtime.ui.views.content.ITeiidResourceNode;
import org.teiid.designer.runtime.ui.views.content.TeiidServerContainerNode;

/**
 * Adapt a {@link ITeiidResourceNode}
 */
public class TeiidResourceNodeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof ITeiidResourceNode))
            return null;
        
        ITeiidResourceNode teiidResourceNode = (ITeiidResourceNode) adaptableObject;
        
        if (ITeiidResourceNode.class == adapterType)
            return teiidResourceNode;
        
        if (TeiidServer.class == adapterType)
            return adaptToTeiidServer(teiidResourceNode);
        
        if (TeiidServerContainerNode.class == adapterType)
            return adaptToTeiidServerContainerNode(teiidResourceNode);
        
        return null;
    }

    /**
     * Adapt to a {@link TeiidServer}
     * 
     * @param adaptableObject
     */
    private TeiidServer adaptToTeiidServer(ITeiidResourceNode teiidResourceNode) {
        return teiidResourceNode.getTeiidServer();
    }


    /**
     * Adapt to a {@link TeiidServerContainerNode}
     * 
     * @param adaptableObject
     * @return
     */
    private TeiidServerContainerNode adaptToTeiidServerContainerNode(ITeiidResourceNode teiidResourceNode) {
        if (teiidResourceNode.hasChildren()) {
            List<? extends ITeiidContentNode<?>> children = teiidResourceNode.getChildren();
            ITeiidContentNode<?> child = children.get(0);
            if (child instanceof TeiidServerContainerNode)
                return (TeiidServerContainerNode) child;
        }
        
        return null;
    }
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class, ITeiidResourceNode.class, TeiidServerContainerNode.class };
    }

}
