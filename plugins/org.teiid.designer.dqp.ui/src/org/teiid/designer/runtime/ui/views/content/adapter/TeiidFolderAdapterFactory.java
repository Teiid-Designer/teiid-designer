/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IResourceNode;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.views.content.AbstractTeiidFolder;
import org.teiid.designer.runtime.ui.views.content.TeiidResourceNode;

/**
 * Adapt a {@link TeiidResourceNode}
 */
public class TeiidFolderAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof AbstractTeiidFolder))
            return null;

        AbstractTeiidFolder<?> abstractTeiidFolder = (AbstractTeiidFolder<?>) adaptableObject;

        if (TeiidResourceNode.class == adapterType)
            return adaptToTeiidResourceNode(abstractTeiidFolder);
        else if (TeiidServer.class == adapterType)
            return adaptToTeiidServer(abstractTeiidFolder);
        
        return null;
    }

    /**
     * Try and adapt to a {@link TeiidResourceNode}
     * 
     * @param teiidDataNode
     * @param adapterType
     * @return
     */
    private Object adaptToTeiidResourceNode(AbstractTeiidFolder<?> teiidDataNode) {
        IResourceNode parent = teiidDataNode.getParent();
        return parent instanceof TeiidResourceNode ? parent : null;
    }

    /**
     * Try and adapt to a {@link TeiidServer}
     * 
     * @param adaptableObject
     */
    private TeiidServer adaptToTeiidServer(AbstractTeiidFolder<?> abstractTeiidFolder) {
        return abstractTeiidFolder.getTeiidServer();
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] {TeiidResourceNode.class, TeiidServer.class};
    }

}
