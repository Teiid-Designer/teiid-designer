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
import org.teiid.designer.runtime.ui.views.content.TeiidResourceNode;
import org.teiid.designer.runtime.ui.views.content.TeiidServerContainerNode;

/**
 * Adapt a {@link TeiidServerContainerNode}
 */
public class TeiidServerContainerNodeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (TeiidServer.class == adapterType)
            return adaptToTeiidServer(adaptableObject);
        
        if (TeiidResourceNode.class == adapterType)
            return adaptToTeiidResourceNode(adaptableObject);
        
        return null;
    }

    /**
     * @param adaptableObject
     */
    private TeiidServer adaptToTeiidServer(Object adaptableObject) {
        if (adaptableObject instanceof TeiidServer) {
            return (TeiidServer) adaptableObject;
        }
        
        if (adaptableObject instanceof TeiidServerContainerNode) {
            return ((TeiidServerContainerNode)adaptableObject).getTeiidServer();
        }
        
        return null;
    }


    /**
     * @param adaptableObject
     * @return
     */
    private TeiidResourceNode adaptToTeiidResourceNode(Object adaptableObject) {
        if (adaptableObject instanceof TeiidResourceNode) {
            return (TeiidResourceNode) adaptableObject;
        }
        
        if (adaptableObject instanceof TeiidServerContainerNode) {
            return ((TeiidServerContainerNode)adaptableObject).getContainer();
        }
        
        return null;
    }
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class, TeiidResourceNode.class };
    }

}
