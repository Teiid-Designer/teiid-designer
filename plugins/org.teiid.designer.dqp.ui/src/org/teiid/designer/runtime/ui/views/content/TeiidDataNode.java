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
import org.teiid.designer.runtime.connection.SourceConnectionBinding;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
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
    private ITeiidServer teiidServer;
    
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
     * Get the {@link ITeiidServer} that this folder belongs to
     * 
     * @return teiidServer
     */
    public ITeiidServer getTeiidServer() {
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
        if (value instanceof ITeiidDataSource) {
        	String nodeName = null;
            if (((ITeiidDataSource) value).getDisplayName() != null) {
            	nodeName = ((ITeiidDataSource) value).getDisplayName();
            }
            nodeName = ((ITeiidDataSource) value).getName();
            
            String jndiName = ((ITeiidDataSource)value).getPropertyValue("jndi-name"); //$NON-NLS-1$
            if(jndiName!=null && !jndiName.isEmpty()) {
            	nodeName += " [JNDI: " + jndiName + "]";  //$NON-NLS-1$ //$NON-NLS-2$
            } else {
            	nodeName += " [JNDI: java:/" + nodeName + "]";  //$NON-NLS-1$ //$NON-NLS-2$
            }
            return nodeName;
        }
        
        if (value instanceof ITeiidTranslator) {
            return ((ITeiidTranslator) value).getName();
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
        if (value instanceof ITeiidDataSource) {
            if (((ITeiidDataSource) value).getDisplayName() != null) {
                return ((ITeiidDataSource) value).getDisplayName();
            }
            return ((ITeiidDataSource) value).getName();
        }
        
        if (value instanceof ITeiidTranslator) {
            return ((ITeiidTranslator) value).getName();
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
            int version = ((ITeiidVdb)value).getVersion();
            builder.append("\nVersion:").append(version); //$NON-NLS-1$
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
        if (value instanceof ITeiidTranslator) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }

        if (value instanceof ITeiidDataSource) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTION_SOURCE_ICON);
        }

        if (value instanceof ITeiidVdb) {
            if (((ITeiidVdb) value).isActive()) {
            	if (((ITeiidVdb) value).getValidityErrors().isEmpty()) {
            		return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.DEPLOY_VDB);
            	} else {
            		return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.ACTIVE_VDB_WITH_ERRORS);
            	}
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
