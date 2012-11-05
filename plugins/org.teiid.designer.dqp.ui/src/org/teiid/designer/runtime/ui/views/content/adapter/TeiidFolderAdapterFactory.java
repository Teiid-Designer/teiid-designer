/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.teiid.designer.runtime.ITeiidServer;
import org.teiid.designer.runtime.ui.views.content.AbstractTeiidFolder;
import org.teiid.designer.runtime.ui.views.content.ITeiidResourceNode;

/**
 * Adapt a {@link ITeiidResourceNode}
 */
public class TeiidFolderAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof AbstractTeiidFolder))
            return null;

        AbstractTeiidFolder<?> abstractTeiidFolder = (AbstractTeiidFolder<?>) adaptableObject;

        if (ITeiidResourceNode.class == adapterType)
            return adaptToTeiidResourceNode(abstractTeiidFolder);
        else if (adapterType.isInstance(ITeiidServer.class))
            return adaptToTeiidServer(abstractTeiidFolder);
        
        return null;
    }

    /**
     * Try and adapt to a {@link ITeiidResourceNode}
     * 
     * @param teiidDataNode
     * @param adapterType
     * @return
     */
    private Object adaptToTeiidResourceNode(AbstractTeiidFolder<?> teiidDataNode) {
        ITeiidResourceNode parent = teiidDataNode.getParent();
        return parent != null ? parent : null;
    }

    /**
     * Try and adapt to a {@link ITeiidServer}
     * 
     * @param adaptableObject
     */
    private ITeiidServer adaptToTeiidServer(AbstractTeiidFolder<?> abstractTeiidFolder) {
        return abstractTeiidFolder.getTeiidServer();
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] {ITeiidResourceNode.class, ITeiidServer.class};
    }

}
