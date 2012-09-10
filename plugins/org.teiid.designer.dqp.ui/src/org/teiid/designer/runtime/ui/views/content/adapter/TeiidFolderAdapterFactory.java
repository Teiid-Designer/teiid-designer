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
import org.teiid.designer.runtime.ui.views.content.TeiidFolder;
import org.teiid.designer.runtime.ui.views.content.TeiidResourceNode;

/**
 * Adapt a {@link TeiidResourceNode}
 */
public class TeiidFolderAdapterFactory implements IAdapterFactory {

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adaptableObject instanceof TeiidServer) {
            return adaptableObject;
        }
        
        if (adaptableObject instanceof TeiidFolder) {
            return ((TeiidFolder) adaptableObject).getTeiidServer();
        }
        
        return null;
    }
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class };
    }

}
