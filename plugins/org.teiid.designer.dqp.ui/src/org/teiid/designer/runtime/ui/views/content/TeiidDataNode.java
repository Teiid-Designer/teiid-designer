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
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.designer.runtime.ITeiidVdb;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.DqpUiPlugin;

/**
 * @param <V> 
 * @since 8.0
 */
public class TeiidDataNode<V> implements ITeiidContentNode<AbstractTeiidFolder> {
    
    /**
     * Prefix for language NLS properties
     */
    private static final String PREFIX = I18nUtil.getPropertyPrefix(TeiidDataNode.class);
    
    private static final String ACTIVE_VDB = DqpUiConstants.UTIL.getString(PREFIX + "activeVdb"); //$NON-NLS-1$
    
    private static final String INACTIVE_VDB = DqpUiConstants.UTIL.getString(PREFIX + "inactiveVdb"); //$NON-NLS-1$
    
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
    public ITeiidResourceNode getParent() {
        return parentNode != null ? parentNode.getParent() : null;
    }

    @Override
    public AbstractTeiidFolder getContainer() {
        return parentNode;
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

        if (value instanceof ITeiidVdb) {
            return ((ITeiidVdb) value).getName();
        }

        if (value instanceof SourceConnectionBinding) {
            SourceConnectionBinding binding = (SourceConnectionBinding) value;
            return binding.getModelName();
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        if (value instanceof TeiidDataSource) {
            if (((TeiidDataSource) value).getDisplayName() != null) {
                return ((TeiidDataSource) value).getDisplayName();
            }
            return ((TeiidDataSource) value).getName();
        }
        
        if (value instanceof TeiidTranslator) {
            return ((TeiidTranslator) value).getName();
        }

        if (value instanceof ITeiidVdb) {
            ITeiidVdb vdb = (ITeiidVdb) value;
            StringBuilder builder = new StringBuilder();
            builder.append("VDB:\t\t").append(vdb.getName()).append("\nState:\t"); //$NON-NLS-1$ //$NON-NLS-2$
            if (vdb.isActive()) {
                builder.append(ACTIVE_VDB);
            } else {
                builder.append(INACTIVE_VDB);
                for (String error : vdb.getValidityErrors()) {
                    builder.append("\nERROR:\t").append(error); //$NON-NLS-1$
                }
            }

            builder.append("\nModels:"); //$NON-NLS-1$
            for (String modelName : vdb.getModelNames()) {
                builder.append("\n\t   ").append(modelName); //$NON-NLS-1$
            }
            
            return builder.toString();
        }

        if (value instanceof SourceConnectionBinding) {
            SourceConnectionBinding binding = (SourceConnectionBinding) value;
            return binding.getModelName();
        }
        
        return super.toString();   
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

        if (value instanceof ITeiidVdb) {
            if (((ITeiidVdb) value).isActive()) {
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
     * @return real value of this data node
     */
    public V getValue() {
        return value;
    }
}
