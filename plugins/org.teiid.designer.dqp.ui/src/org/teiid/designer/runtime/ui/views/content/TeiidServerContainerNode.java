/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.views.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.ui.DqpUiConstants;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider;

/**
 * @param <T> 
 * @since 8.0
 */
public class TeiidServerContainerNode<T extends ITeiidResourceNode> extends TeiidContentNode<T> implements ITeiidContainerNode<T> {

    private List<ITeiidContentNode<TeiidServerContainerNode>> children;
    private ITeiidServer teiidServer;
    private TeiidServerContentProvider provider;
    private TeiidErrorNode error;
    private ITeiidServerManager serverManager;
    
    /**
     * @param server
     */
    protected TeiidServerContainerNode(T parent, TeiidServerContentProvider provider) {
        super(parent, parent.getTeiidServer().getDisplayName());
        this.teiidServer = parent.getTeiidServer();
        this.provider = provider;
        this.serverManager = DqpPlugin.getInstance().getServerManager();
    }
    
    private void clearError() {
        if (error != null) {
            error.dispose();
            error = null;
        }
    }
    
    @Override
    public T getContainer() {
        return super.getContainer();
    }
    
    @Override
    public boolean hasChildren() {
        return children != null && ! children.isEmpty();
    }

    @Override
    public final List<? extends ITeiidContentNode<?>> getChildren() {
        if (error != null) {
            return Collections.singletonList(error);
        }
        return children;
    }
    
    private void clearChildren() {
        clearError();
        
        if (children != null) {
            for (ITeiidContentNode<TeiidServerContainerNode> child : children) {
                child.dispose();
            }
            children.clear();
            children = null;
        }
    }
    
    protected void setError(TeiidErrorNode error) {
        clearError();
        this.error = error;
    }

    @Override
    public void dispose() {
        clearChildren();
        super.dispose();
    }

    @Override
    public final void load() {
        clearChildren();
        
        if (getServer().getServerState() != IServer.STATE_STARTED) {
            setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(TeiidServerContainerNode.class.getSimpleName() + ".ServerContentLabelNotConnected"))); //$NON-NLS-1$
            return;
        }
        
        if (!teiidServer.isConnected()) {
            return;
        }
        
        children = new ArrayList<ITeiidContentNode<TeiidServerContainerNode>>();
        
        try {
            // hide Data Sources related variables from other local variables
            DATA_SOURCES: {
                Collection<ITeiidDataSource> dataSources = teiidServer.getDataSources();

                if (provider.isShowDataSources() && ! dataSources.isEmpty()) {
                    List<ITeiidDataSource> filteredDataSources = new ArrayList<ITeiidDataSource>(dataSources.size());

                    for (ITeiidDataSource dataSource : dataSources) {

                        // Either we are showing preview data source or dataSource is not a preview
                        // Check if preview data source name contains this workspace UUID
                        if( dataSource.isPreview()) {
                        	if( dataSource.getName().contains(ModelerCore.workspaceUuid().toString())) {
                        		filteredDataSources.add(dataSource);
                        	}
                        } else {
                        	filteredDataSources.add(dataSource);
                        }
                    }

                    if (!filteredDataSources.isEmpty()) {
                        children.add(new DataSourcesFolder(this, filteredDataSources));
                    }
                }
                
                break DATA_SOURCES;
            }

            // hide VDBs related variables from other local variables
            VDBS: {
                Collection<ITeiidVdb> vdbs = teiidServer.getVdbs();

                if (provider.isShowVDBs() && ! vdbs.isEmpty()) {
                    List<ITeiidVdb> filteredVdbs = new ArrayList<ITeiidVdb>(vdbs.size());

                    for (ITeiidVdb vdb : vdbs) {

                        // Either we are showing preview vdb or VDB is not a preview
                        // Check if preview VDB's name contains this workspace UUID
                        if( vdb.isPreviewVdb() ) { 
                        	if( vdb.getName().contains(ModelerCore.workspaceUuid().toString())) {
                        		filteredVdbs.add(vdb);
                        	}
                        } else {
                        	filteredVdbs.add(vdb);
                        }
                        
                    }

                    if (!filteredVdbs.isEmpty()) {
                        children.add(new VdbsFolder(this, filteredVdbs));
                    }
                }
                break VDBS;
            }

            // hide translators related variables from other local variables
            TRANSLATORS: {
                Collection<ITeiidTranslator> translators;

                if (provider.isShowTranslators()) {
                    translators = new ArrayList<ITeiidTranslator>(teiidServer.getTranslators());

                    if (!translators.isEmpty()) {
                        children.add(new TranslatorsFolder(this, translators));
                    }
                } else {
                    translators = Collections.emptyList();
                }

                break TRANSLATORS;
            }
            clearError();
        } catch (Exception e) {
            setError(new TeiidErrorNode(this, teiidServer, DqpUiConstants.UTIL.getString(TeiidServerContainerNode.class.getSimpleName() + ".ServerContentLabelNotConnected"))); //$NON-NLS-1$
            DqpPlugin.Util.log(e);
        }
    }

    /**
     * Get this container's Teiid Instance
     * 
     * @return {@link ITeiidServer}
     */
    public ITeiidServer getTeiidServer() {
        return teiidServer;
    }
    
    @Override
    public String getName() {
        ITeiidServer defaultServer = serverManager.getDefaultServer();
        int serverCount = serverManager.getServers().size();
        
        String name = super.getName();
        if (getTeiidServer() != null && serverCount > 1 && getTeiidServer() == defaultServer) {
            name = name + "  " + DqpUiConstants.UTIL.getString(TeiidServerContainerNode.class.getSimpleName() + ".ServerContentLabelDefault"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        return name;
    }
    
    @Override
    public String toString() {
        if (teiidServer == null)
            return DqpUiConstants.UTIL.getString(TeiidResourceNode.class.getSimpleName() + ".labelNotConnected"); //$NON-NLS-1$
        
        String ttip = teiidServer.toString();
        if( teiidServer.getConnectionError() != null ) {
            ttip = ttip + "\n\n" + teiidServer.getConnectionError(); //$NON-NLS-1$
        }
        
        return ttip;
    }
}
