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
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.ContainerNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContainerNode;
import org.jboss.ide.eclipse.as.ui.views.as7.management.content.IContentNode;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.ui.views.TeiidServerContentProvider;

/**
 * @since 8.0
 */
public class TeiidServerContainerNode extends ContainerNode<TeiidResourceNode> {

    private ArrayList<IContentNode<? extends IContainerNode<?>>> children;
    private TeiidServer teiidServer;
    private TeiidServerContentProvider provider;
    
    /**
     * @param server
     */
    protected TeiidServerContainerNode(TeiidResourceNode parent, TeiidServerContentProvider provider) {
        super(parent, parent.getTeiidServer().getDisplayName());
        this.teiidServer = parent.getTeiidServer();
        this.provider = provider;
    }
    
    /**
     * Does this node have any children
     * 
     * @return true if there are children.
     */
    public boolean hasChildren() {
        return children != null && ! children.isEmpty();
    }

    @Override
    protected List<? extends IContentNode<?>> delegateGetChildren() {
        return children;
    }

    @Override
    protected void delegateClearChildren() {
        if (children != null) {
            for (IContentNode<? extends IContainerNode<?>> child : children) {
                child.dispose();
            }
            children.clear();
            children = null;
        }
    }

    @Override
    protected void delegateLoad() throws Exception {
        if (!teiidServer.isConnected()) {
            return;
        }
        
        children = new ArrayList<IContentNode<? extends IContainerNode<?>>>();

        try {
            // hide Data Sources related variables from other local variables
            DATA_SOURCES: {
                Collection<TeiidDataSource> dataSources;

                if (provider.isShowDataSources()) {
                    dataSources = new ArrayList(teiidServer.getAdmin().getDataSources());

                    if (!dataSources.isEmpty()) {
                        children.add(new DataSourcesFolder(this, dataSources.toArray()));
                    }
                } else {
                    dataSources = Collections.emptyList();
                }
                
                break DATA_SOURCES;
            }

            // hide VDBs related variables from other local variables
            VDBS: {
                Collection<TeiidVdb> vdbs;

                if (provider.isShowVDBs()) {
                    vdbs = new ArrayList<TeiidVdb>(teiidServer.getAdmin().getVdbs());

                    if (!vdbs.isEmpty()) {
                        children.add(new VdbsFolder(this, vdbs.toArray()));
                    }
                } else {
                    vdbs = Collections.emptyList();
                }
                
                break VDBS;
            }

            // hide translators related variables from other local variables
            TRANSLATORS: {
                Collection<TeiidTranslator> translators;

                if (provider.isShowTranslators()) {
                    translators = teiidServer.getAdmin().getTranslators();

                    if (!translators.isEmpty()) {
                        children.add(new TranslatorsFolder(this, translators.toArray()));
                    }
                } else {
                    translators = Collections.emptyList();
                }
                
                break TRANSLATORS;
            }
        } catch (Exception e) {
            // Want to log the exception as well as throw it since the 
            // tree viewer doesnt provide details of the whole exception
            DqpPlugin.Util.log(e);
            throw e;
        }
    }

    /**
     * Get this container's teiid server
     * 
     * @return {@link TeiidServer}
     */
    public TeiidServer getTeiidServer() {
        return teiidServer;
    }
    
}
