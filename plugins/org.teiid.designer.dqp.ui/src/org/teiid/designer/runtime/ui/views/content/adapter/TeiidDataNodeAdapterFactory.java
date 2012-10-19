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
        
        Object value = ((TeiidDataNode) adaptableObject).getValue();
        
        if (adapterType.isInstance(value))
            return value;
        
        return null;
    }

    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidDataSource.class, TeiidTranslator.class, TeiidVdb.class };
    }

}
