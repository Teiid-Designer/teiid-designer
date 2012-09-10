/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.Arrays;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContentNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IResourceNode;
import org.teiid.designer.runtime.TeiidServer;

/**
 * @since 8.0
 */
public abstract class TeiidFolder implements IContentNode<TeiidServerContainerNode> {
    
    private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$
    
    private TeiidServerContainerNode parentNode;
    private Object[] theValues;
    private TeiidServer teiidServer;
    
    /**
     * Create new instance
     * 
     * @param parentNode 
     * @param values
     */
    public TeiidFolder(TeiidServerContainerNode parentNode, Object[] values ) {
        this.parentNode = parentNode;
        this.teiidServer = parentNode.getTeiidServer();
        theValues = values;
    }

    /**
     * Get the children of this folder
     * 
     * @return object array of the children in this folder
     */
    public Object[] getChildren() {
        return theValues;
    }
    
    @Override
    public IServer getServer() {
       return teiidServer.getParent();
    }
    
    /**
     * Get the {@link TeiidServer} that this folder belongs to
     * 
     * @return teiidServer
     */
    public TeiidServer getTeiidServer() {
        return teiidServer;
    }

    @Override
    public IResourceNode getParent() {
        return parentNode.getParent();
    }

    @Override
    public TeiidServerContainerNode getContainer() {
        return parentNode;
    }

    @Override
    public String getAddress() {
        return getParent().getAddress() + PATH_SEPARATOR + getName();
    }

    @Override
    public void dispose() {
        this.parentNode = null;
        this.teiidServer = null;
        this.theValues = null;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.teiidServer == null) ? 0 : this.teiidServer.hashCode());
        result = prime * result + Arrays.hashCode(this.theValues);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TeiidFolder other = (TeiidFolder)obj;
        if (this.teiidServer == null) {
            if (other.teiidServer != null) return false;
        } else if (!this.teiidServer.equals(other.teiidServer)) return false;
        if (!Arrays.equals(this.theValues, other.theValues)) return false;
        return true;
    }    
}
