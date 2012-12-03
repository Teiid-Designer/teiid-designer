/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.views.content.ITeiidResourceNode;
import org.teiid.designer.runtime.ui.views.content.TeiidDataNode;

/**
 * Adapt a {@link ITeiidResourceNode}
 */
public class TeiidDataNodeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof TeiidDataNode))
            return null;
        
        TeiidDataNode teiidDataNode = (TeiidDataNode) adaptableObject;
        
        if (ITeiidResourceNode.class == adapterType)
            return adaptToTeiidResourceNode(teiidDataNode);
        
        if (ITeiidDataSource.class == adapterType ||
            ITeiidTranslator.class == adapterType ||
            ITeiidVdb.class == adapterType) {
        
            Object value = teiidDataNode.getValue();
        
            if (adapterType.isInstance(value))
                return value;
        }
        
        if (ITeiidServer.class == adapterType) {
            return adaptToTeiidServer(teiidDataNode);
        }
        
        return null;
    }

    /**
     * Try and adapt to a {@link ITeiidResourceNode}
     * 
     * @param teiidDataNode
     * @return
     */
    private Object adaptToTeiidResourceNode(TeiidDataNode teiidDataNode) {
        ITeiidResourceNode parent = teiidDataNode.getParent();
        return parent != null ? parent : null;
    }
    
    /**
     * Try and adapt to a {@link ITeiidServer}
     * 
     * @param teiidDataNode
     */
    private ITeiidServer adaptToTeiidServer(TeiidDataNode teiidDataNode) {
        return teiidDataNode.getTeiidServer();
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { ITeiidResourceNode.class, 
                                           ITeiidDataSource.class,
                                           ITeiidTranslator.class,
                                           ITeiidVdb.class,
                                           ITeiidServer.class };
    }

}
