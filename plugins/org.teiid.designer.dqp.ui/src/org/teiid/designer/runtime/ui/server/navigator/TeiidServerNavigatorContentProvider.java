/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.server.navigator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.core.IServer;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.ui.views.TeiidViewTreeProvider;

/**
 * Provider ???
 *
 * @since 8.0
 */
public class TeiidServerNavigatorContentProvider implements ITreeContentProvider {

    private TeiidViewTreeProvider delegate;

    /**
     * 
     */
    public TeiidServerNavigatorContentProvider() {
        super();
    }
    
    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement == null)
            return new Object[0];
        
        if (parentElement instanceof IServer) {
            
            if (((IServer) parentElement).getServerState() != IServer.STATE_STARTED)
                return new Object[0];
            
            Object teiidServer = ((IServer) parentElement).loadAdapter(TeiidServer.class, null);
            if (teiidServer != null) {
                return new Object[] { teiidServer };
            }
        }
        else if (parentElement instanceof TeiidServer) {
            delegate = new TeiidViewTreeProvider();
            return delegate.getChildren(parentElement);
        } else if (delegate != null)
            return delegate.getChildren(parentElement);
        
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof IServer)
            return null;
        else if (element instanceof TeiidServer)
            return ((TeiidServer)element).getParent();
        else if (delegate != null)
            return delegate.getParent(element);
        
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        if (element instanceof IServer)
            return true;
        else if (element instanceof TeiidServer)
            return true;
        else if (delegate != null)
            return delegate.hasChildren(element);
        
        return false;
    }
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }


}
