/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.GuardedBy;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.core.IServer;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.core.util.HashCodeUtil;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;
import org.teiid.designer.runtime.ui.DqpUiConstants;


/**
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * 
 * @since 8.0
 */
public class TeiidServerContentProvider implements ITreeContentProvider {

    static final String VDBS_FOLDER_NAME = DqpUiConstants.UTIL.getString("TeiidViewTreeProvider.vdbsFolder.label"); //$NON-NLS-1$
    static final String DATA_SOURCES_FOLDER_NAME = DqpUiConstants.UTIL.getString("TeiidViewTreeProvider.dataSourcesFolder.label"); //$NON-NLS-1$
    static final String TRANSLATORS_FOLDER_NAME = DqpUiConstants.UTIL.getString("TeiidViewTreeProvider.translatorsFolder.label"); //$NON-NLS-1$

    private boolean showVDBs = true;
    private boolean showDataSources = true;
    private boolean showTranslators = true;

    /**
     * Servers that a connection can't be established. Value is the last time establishing a connection was tried.
     */
    @GuardedBy( "offlineServersLock" )
    private final Map<TeiidServer, Long> offlineServerMap = new HashMap<TeiidServer, Long>();

    /**
     * Content will include VDBs, translators, and data sources.
     * @since 5.0
     */
    public TeiidServerContentProvider() {
        super();
    }

    /**
     * @param showVDBs 
     * @param showTranslators 
     * @param showDataSources 
     * 
     * @since 5.0
     */
    public TeiidServerContentProvider( boolean showVDBs,
                                  boolean showTranslators,
                                  boolean showDataSources ) {
        super();
        this.showVDBs = showVDBs;
        this.showTranslators = showTranslators;
        this.showDataSources = showDataSources;
    }
    
    /**
     * Set show data sources flag
     * 
     * @param show
     */
    public void setShowDataSources(boolean show) {
        this.showDataSources = show;
    }
            
    /**
     * Set show translators flag
     * 
     * @param show
     */
    public void setShowTranslators(boolean show) {
        this.showTranslators = show;
    }
            
    /**
     * Set show vdbs flag
     * 
     * @param show
     */
    public void setShowVdbs(boolean show) {
        this.showVDBs = show;
    }
    
    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
//    @SuppressWarnings("unused")
    @Override
    public Object[] getChildren( Object parentElement ) {
        if (parentElement == null)
            return new Object[0];
        
        if (parentElement instanceof IServer) {
            Object teiidServer = ((IServer) parentElement).loadAdapter(TeiidServer.class, null);
            if (teiidServer != null) {
                return new Object[] { teiidServer };
            } else {
                return new String[] { DqpPlugin.Util.getString("jbossServerNotStartedMessage") }; //$NON-NLS-1$
            }
        }
        else if ((parentElement instanceof TeiidServer)) {
            Collection<Object> allObjects = new ArrayList<Object>();
            TeiidServer teiidServer = (TeiidServer)parentElement;

            if (!teiidServer.isConnected()) {
                return new Object[0];
            }

            try {
                // hide Data Sources related variables from other local variables
                DATA_SOURCES: {
                    Collection<TeiidDataSource> dataSources;

                    if (this.showDataSources) {
                        dataSources = new ArrayList(teiidServer.getAdmin().getDataSources());

                        if (!dataSources.isEmpty()) {
                            allObjects.add(new DataSourcesFolder(teiidServer, dataSources.toArray()));
                        }
                    } else {
                        dataSources = Collections.emptyList();
                    }
                    
                    break DATA_SOURCES;
                }

                // hide VDBs related variables from other local variables
                VDBS: {
                    Collection<TeiidVdb> vdbs;

                    if (this.showVDBs) {
                        vdbs = new ArrayList<TeiidVdb>(teiidServer.getAdmin().getVdbs());

                        if (!vdbs.isEmpty()) {
                            allObjects.add(new VdbsFolder(teiidServer, vdbs.toArray()));
                        }
                    } else {
                        vdbs = Collections.emptyList();
                    }
                    
                    break VDBS;
                }

                // hide translators related variables from other local variables
                TRANSLATORS: {
                    Collection<TeiidTranslator> translators;

                    if (this.showTranslators) {
                        translators = teiidServer.getAdmin().getTranslators();

                        if (!translators.isEmpty()) {
                            allObjects.add(new TranslatorsFolder(teiidServer, translators.toArray()));
                        }
                    } else {
                        translators = Collections.emptyList();
                    }
                    
                    break TRANSLATORS;
                }

                return allObjects.toArray();
            } catch (AdminComponentException ace) {
                return new Object[0];
            } catch (Exception e) {
                DqpPlugin.Util.log(e);
                return new Object[0];
            }
        } else if (parentElement instanceof TeiidFolder) {
            return ((TeiidFolder)parentElement).getChildren();
        } else if (parentElement instanceof SourceConnectionBinding) {
            return new Object[0];
        }

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object[] getElements( Object inputElement ) {
        return getChildren(inputElement);
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getParent( Object element ) {
        if (element instanceof IServer)
            return null;
        else if (element instanceof TeiidServer)
            return ((TeiidServer)element).getParent();
        else if (element instanceof TeiidFolder)
            return ((TeiidFolder)element).getServer();
        
        return null;
    }

    /**
     * @param server the server whose Data Source folder is being requested
     * @return the folder
     */
    public Object getDataSourceFolder(Object server) {
        for (Object child : getChildren(server)) {
            if (child instanceof DataSourcesFolder) {
                return child;
            }
        }
        
        return null;
    }
    
    /**
     * @param server the server whose Translators folder is being requested
     * @return the folder
     */
    public Object getTranslatorFolder(Object server) {
        for (Object child : getChildren(server)) {
            if (child instanceof TranslatorsFolder) {
                return child;
            }
        }
        
        return null;
    }
    
    /**
     * @param server the server whose VDBs folder is being requested
     * @return the folder
     */
    public Object getVdbFolder(Object server) {
        for (Object child : getChildren(server)) {
            if (child instanceof VdbsFolder) {
                return child;
            }
        }
        
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean hasChildren( Object element ) {
        if (element instanceof IServer)
            return true;
        else if (element instanceof TeiidServer)
            return ((TeiidServer)element).isConnected();
        else {
            return getChildren(element).length > 0;
        }
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.2
     */
    @Override
    public void inputChanged( Viewer viewer, Object oldInput, Object newInput ) {
        // do nothing
    }
    

    @Override
    public void dispose() {
        // nothing to do
    }

    class TeiidFolder {
        Object[] theValues;
        TeiidServer teiidServer;

        public TeiidFolder(TeiidServer teiidServer, Object[] values ) {
        	this.teiidServer = teiidServer;
            theValues = values;
        }
        
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals( Object obj ) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!getClass().equals(obj.getClass())) return false;

            TeiidFolder folder = (TeiidFolder)obj;
            return getServer().equals(folder.getServer());
        }

        public Object[] getChildren() {
            return theValues;
        }

        protected String getName() {
            return null;
        }
        
        public ExecutionAdmin getAdmin() {
        	ExecutionAdmin admin = null;
        	
        	try {
				admin = teiidServer.getAdmin();
			} catch (Exception e) {
				DqpUiConstants.UTIL.log(IStatus.ERROR, e, e.getMessage());
			}
        	return admin;
        }
        
        private TeiidServer getServer() {
            return this.teiidServer;
        }
        
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return HashCodeUtil.hashCode(this.teiidServer.hashCode(), getClass().hashCode());
        }
    }

    class DataSourcesFolder extends TeiidFolder {
        public DataSourcesFolder(TeiidServer teiidServer, Object[] values ) {
            super(teiidServer, values);
        }

        @Override
        protected String getName() {
            return DATA_SOURCES_FOLDER_NAME;
        }
    }

    class VdbsFolder extends TeiidFolder {
        public VdbsFolder(TeiidServer teiidServer, Object[] values ) {
            super(teiidServer, values);
        }

        @Override
        protected String getName() {
            return VDBS_FOLDER_NAME;
        }
    }

    class TranslatorsFolder extends TeiidFolder {
        public TranslatorsFolder(TeiidServer teiidServer, Object[] values ) {
            super(teiidServer, values);
        }

        @Override
        protected String getName() {
            return TRANSLATORS_FOLDER_NAME;
        }
    }
}
