/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContentNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IResourceNode;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;

/**
 * @param <V> 
 * @since 8.0
 */
public class TeiidDataNode<V> implements IContentNode<AbstractTeiidFolder> {
    
    private static final String PATH_SEPARATOR = "/"; //$NON-NLS-1$
    
    private AbstractTeiidFolder parentNode;
    private V value;
    private TeiidServer teiidServer;
    
    /**
     * Create new instance
     * 
     * @param parentNode 
     * @param value
     */
    public TeiidDataNode(AbstractTeiidFolder parentNode, V value ) {
        this.parentNode = parentNode;
        this.teiidServer = parentNode.getTeiidServer();
        this.value = value;
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
        return parentNode != null ? parentNode.getParent() : null;
    }

    @Override
    public AbstractTeiidFolder getContainer() {
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
    }
    
    @Override
    public String getName() {
        if (value instanceof TeiidDataSource) {
            if (((TeiidDataSource) value).getDisplayName() != null) {
                return ((TeiidDataSource) value).getDisplayName();
            }
            return ((TeiidDataSource) value).getName();
        }
        
        if (value instanceof TeiidTranslator) {
            return ((TeiidTranslator) value).getName();
        }

        if (value instanceof TeiidVdb) {
            return ((TeiidVdb) value).getName();
        }

        if (value instanceof SourceConnectionBinding) {
            SourceConnectionBinding binding = (SourceConnectionBinding) value;
            return binding.getModelName();
        }
        
        return null;
    }
    
    /**
     * Get the image associated with this data node
     * 
     * @return {@link Image}
     */
    public Image getImage() {
        if (value instanceof TeiidTranslator) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }

        if (value instanceof TeiidDataSource) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTION_SOURCE_ICON);
        }

        if (value instanceof TeiidVdb) {
            if (((TeiidVdb) value).isActive()) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.DEPLOY_VDB);
            }
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.INACTIVE_DEPLOYED_VDB);
        }

        if (value instanceof SourceConnectionBinding) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_CONNECTOR_BINDING_ICON);
        }
        
        return null;
    }

    /**
     * @return
     */
    public V getValue() {
        return value;
    }
}
