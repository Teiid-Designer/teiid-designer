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
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContentNode;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.views.content.TeiidResourceNode;
import org.teiid.designer.runtime.ui.views.content.TeiidServerContainerNode;

/**
 * Adapt a {@link TeiidResourceNode}
 */
public class TeiidResourceNodeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof TeiidResourceNode))
            return null;
        
        TeiidResourceNode teiidResourceNode = (TeiidResourceNode) adaptableObject;
        
        if (TeiidResourceNode.class == adapterType)
            return teiidResourceNode;
        
        if (TeiidServer.class == adapterType)
            return adaptToTeiidServer(teiidResourceNode);
        
        if (TeiidResourceNode.class == adapterType)
            return adaptToTeiidServerContainerNode(teiidResourceNode);
        
        return null;
    }

    /**
     * Adapt to a {@link TeiidServer}
     * 
     * @param adaptableObject
     */
    private TeiidServer adaptToTeiidServer(TeiidResourceNode teiidResourceNode) {
        return teiidResourceNode.getTeiidServer();
    }


    /**
     * Adapt to a {@link TeiidServerContainerNode}
     * 
     * @param adaptableObject
     * @return
     */
    private TeiidServerContainerNode adaptToTeiidServerContainerNode(TeiidResourceNode teiidResourceNode) {
        if (teiidResourceNode.hasChildren()) {
            List<? extends IContentNode<?>> children = teiidResourceNode.getChildren();
            return (TeiidServerContainerNode) children.get(0);
        }
        
        return null;
    }
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class, TeiidResourceNode.class, TeiidServerContainerNode.class };
    }

}
