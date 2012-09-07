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
        if (TeiidServer.class == adapterType)
            return adaptToTeiidServer(adaptableObject);
        
        if (TeiidResourceNode.class == adapterType)
            return adaptToTeiidServerContainerNode(adaptableObject);
        
        return null;
    }

    /**
     * @param adaptableObject
     */
    private TeiidServer adaptToTeiidServer(Object adaptableObject) {
        if (adaptableObject instanceof TeiidServer) {
            return (TeiidServer) adaptableObject;
        }
        
        if (adaptableObject instanceof TeiidResourceNode) {
            return ((TeiidResourceNode)adaptableObject).getTeiidServer();
        }
        
        return null;
    }


    /**
     * @param adaptableObject
     * @return
     */
    private TeiidServerContainerNode adaptToTeiidServerContainerNode(Object adaptableObject) {
        if (adaptableObject instanceof TeiidServerContainerNode) {
            return (TeiidServerContainerNode) adaptableObject;
        }
        
        if (adaptableObject instanceof TeiidResourceNode) {
            TeiidResourceNode node = (TeiidResourceNode) adaptableObject;
            if (node.hasChildren()) {
                List<? extends IContentNode<?>> children = node.getChildren();
                return (TeiidServerContainerNode) children.get(0);
            }
        }
        
        return null;
    }
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class, TeiidServerContainerNode.class };
    }

}
