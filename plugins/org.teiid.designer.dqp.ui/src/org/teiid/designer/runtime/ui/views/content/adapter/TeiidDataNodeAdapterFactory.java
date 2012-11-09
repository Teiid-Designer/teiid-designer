/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
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
        
        if (TeiidDataSource.class == adapterType ||
            TeiidTranslator.class == adapterType ||
            TeiidVdb.class == adapterType) {
        
            Object value = teiidDataNode.getValue();
        
            if (adapterType.isInstance(value))
                return value;
        }
        
        return null;
    }

    /**
     * Try and adapt to a {@link ITeiidResourceNode}
     * 
     * @param teiidDataNode
     * @param adapterType
     * @return
     */
    private Object adaptToTeiidResourceNode(TeiidDataNode teiidDataNode) {
        ITeiidResourceNode parent = teiidDataNode.getParent();
        return parent != null ? parent : null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { ITeiidResourceNode.class, 
                                           TeiidDataSource.class,
                                           TeiidTranslator.class,
                                           TeiidVdb.class };
    }

}
