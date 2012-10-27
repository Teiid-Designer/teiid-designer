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
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.ui.views.content.TeiidDataNode;
import org.teiid.designer.runtime.ui.views.content.TeiidResourceNode;

/**
 * Adapt a {@link TeiidResourceNode}
 */
public class TeiidDataNodeAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (! (adaptableObject instanceof TeiidDataNode))
            return null;
        
        TeiidDataNode teiidDataNode = (TeiidDataNode) adaptableObject;
        
        if (TeiidResourceNode.class == adapterType)
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
     * Try and adapt to a {@link TeiidResourceNode}
     * 
     * @param teiidDataNode
     * @param adapterType
     * @return
     */
    private Object adaptToTeiidResourceNode(TeiidDataNode teiidDataNode) {
        IResourceNode parent = teiidDataNode.getParent();
        return parent instanceof TeiidResourceNode ? parent : null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidResourceNode.class, 
                                           TeiidDataSource.class,
                                           TeiidTranslator.class,
                                           TeiidVdb.class };
    }

}
