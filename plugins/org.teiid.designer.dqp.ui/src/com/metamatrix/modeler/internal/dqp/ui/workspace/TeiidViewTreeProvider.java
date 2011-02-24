/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.dqp.ui.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jcip.annotations.GuardedBy;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.teiid.adminapi.AdminComponentException;
import org.teiid.core.util.HashCodeUtil;
import org.teiid.designer.runtime.ExecutionAdmin;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ServerManager;
import org.teiid.designer.runtime.TeiidDataSource;
import org.teiid.designer.runtime.TeiidTranslator;
import org.teiid.designer.runtime.TeiidVdb;
import org.teiid.designer.runtime.connection.SourceConnectionBinding;

import com.metamatrix.core.util.I18nUtil;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.dqp.ui.DqpUiConstants;
import com.metamatrix.modeler.dqp.ui.DqpUiPlugin;

/**
 * Class provides content and label information for ConnectorBindings and ModelInfos in ConnectorsView
 * 
 * @since 5.0
 */
public class TeiidViewTreeProvider extends ColumnLabelProvider implements ILightweightLabelDecorator, ITreeContentProvider {

    /**
     * If a server connection cannot be established, wait this amount of time before trying again.
     */
    private static final long RETRY_DURATION = 2000;

    static final String VDBS_FOLDER_NAME = DqpUiConstants.UTIL.getString("TeiidViewTreeProvider.vdbsFolder.label"); //$NON-NLS-1$
    static final String DATA_SOURCES_FOLDER_NAME = DqpUiConstants.UTIL.getString("TeiidViewTreeProvider.dataSourcesFolder.label"); //$NON-NLS-1$
    static final String TRANSLATORS_FOLDER_NAME = DqpUiConstants.UTIL.getString("TeiidViewTreeProvider.translatorsFolder.label"); //$NON-NLS-1$

    private ServerManager serverMgr;

    private boolean showVDBs = true;
    private boolean showDataSources = true;
    private boolean showTranslators = true;

    /**
     * Servers that a connection can't be established. Value is the last time establishing a connection was tried.
     */
    @GuardedBy( "offlineServersLock" )
    private final Map<Server, Long> offlineServerMap = new HashMap<Server, Long>();

    /**
     * Lock used for when accessing the offline server map. The map will be accessed in different threads as the decorator runs in
     * its own thread (not the UI thread).
     */
    private final ReadWriteLock offlineServersLock = new ReentrantReadWriteLock();

    /**
     * Content will include VDBs, translators, and data sources.
     * @since 5.0
     */
    public TeiidViewTreeProvider() {
        super();
    }

    /**
     * @since 5.0
     */
    public TeiidViewTreeProvider( boolean showVDBs,
                                  boolean showTranslators,
                                  boolean showDataSources ) {
        super();
        this.showVDBs = showVDBs;
        this.showTranslators = showTranslators;
        this.showDataSources = showDataSources;
    }

    /**
     * @param server the server that is offline
     */
    private void addOfflineServer( Server server ) {
        try {
            this.offlineServersLock.writeLock().lock();
            this.offlineServerMap.put(server, System.currentTimeMillis());
        } finally {
            this.offlineServersLock.writeLock().unlock();
        }
    }

    /**
     * Indicates if at least one binding is loaded in the configuration.
     * 
     * @return <code>true</code> if configuration contains at least one binding; <code>false</code>.
     * @since 4.3
     */
    public boolean containsBindings() {
        boolean result = false;

        if (getServerManager() != null) {
            Object[] types = getElements(this.serverMgr);

            if ((types != null) && (types.length != 0)) {
                for (int i = 0; i < types.length; ++i) {
                    if (hasChildren(types[i])) {
                        result = true;
                        break;
                    }
                }
            }
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
     */
    @Override
    public void decorate( Object element,
                          IDecoration decoration ) {
        final Display display = Display.getDefault();

        if (display.isDisposed()) {
            return;
        }

        // the decorator framework actually constructs another instance of this provider and the server manager will not be set by
        // the getElements method
        if (getServerManager() != null) {
            assert (element instanceof Server) : "element is not a server (check plugin.xml enablement)"; //$NON-NLS-1$
            ImageDescriptor overlay = null;
            Server server = (Server)element;

            if (isOkToConnect(server)) {
                // decorate server if can't connect
                if (!server.isConnected()) {
                    addOfflineServer(server);
                    //ISharedImages images = DqpUiPlugin.getDefault().getWorkbench().getSharedImages();
                    //overlay = images.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_ERROR);
                }
            } else {
            	if( server.isConnected() ) {
            		overlay = null;
            	}
            }

            if (overlay != null) {
                decoration.addOverlay(overlay);
            }
        }
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     * @since 4.2
     */
//    @SuppressWarnings("unused")
    @Override
    public Object[] getChildren( Object parentElement ) {
        if ((parentElement instanceof Server)) {
            Collection<Object> allObjects = new ArrayList<Object>();
            Server server = (Server)parentElement;

            if (!server.isConnected()) {
                return new Object[0];
            }

            try {
                // hide Data Sources related variables from other local variables
                DATA_SOURCES: {
                    Collection<TeiidDataSource> dataSources;

                    if (this.showDataSources) {
                        dataSources = new ArrayList(server.getAdmin().getDataSources());

                        if (!dataSources.isEmpty()) {
                            allObjects.add(new DataSourcesFolder(server, dataSources.toArray()));
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
                        vdbs = new ArrayList<TeiidVdb>(server.getAdmin().getVdbs());

                        if (!vdbs.isEmpty()) {
                            allObjects.add(new VdbsFolder(server, vdbs.toArray()));
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
                        translators = server.getAdmin().getTranslators();

                        if (!translators.isEmpty()) {
                            allObjects.add(new TranslatorsFolder(server, translators.toArray()));
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
        if (inputElement instanceof ServerManager) {
            serverMgr = (ServerManager)inputElement;
            return serverMgr.getServers().toArray();
        }

        return new Object[0];
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Image getImage( Object element ) {
        if (element instanceof Server) {
        	Server server = (Server)element;
        	boolean isOKtoConnect = isOkToConnect(server);
        	//System.out.println(" >>>> TVTP.getImage() IS CONNECTED = " + server.isConnected() + " IS OK TO CONNECT = " + isOKtoConnect + "  Server = " + server.getUrl());
        	boolean isError = false;
            if (isOKtoConnect) {
                // decorate server if can't connect
                if (!server.isConnected()) {
                    addOfflineServer(server);
                    isError = true;
                }
            } else {
            	if( !server.isConnected() ) {
                	isError = true;
            	}
            }
            //isError = false;
            if (getServerManager() != null) {
                if (this.serverMgr.isDefaultServer((Server)element)) {
                	if( isError )
                		return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ERROR_ICON);
                    return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ICON);
                }
            }
            if( isError) {
            	if( server.getConnectionError() != null )
            	return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ERROR_ICON);
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SET_DEFAULT_SERVER_ERROR_ICON);
            }
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SERVER_ICON);
        }

        if (element instanceof TeiidFolder) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.FOLDER_OBJ);
        }

        if (element instanceof TeiidTranslator) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTOR_BINDING_ICON);
        }

        if (element instanceof TeiidDataSource) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.CONNECTION_SOURCE_ICON);
        }

        if (element instanceof TeiidVdb) {
            if (((TeiidVdb)element).isActive()) {
                return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.DEPLOY_VDB);
            }
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.INACTIVE_DEPLOYED_VDB);
        }

        if (element instanceof SourceConnectionBinding) {
            return DqpUiPlugin.getDefault().getAnImage(DqpUiConstants.Images.SOURCE_CONNECTOR_BINDING_ICON);
        }
        return null;
    }

    /**
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     * @since 4.2
     */
    @Override
    public Object getParent( Object element ) {
        return null;
    }

    /**
     * @return the server manager (never <code>null</code>)
     */
    private ServerManager getServerManager() {
        if (this.serverMgr == null) {
            this.serverMgr = DqpPlugin.getInstance().getServerManager();
        }

        return this.serverMgr;
    }

    /**
     * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
     * @since 4.2
     */
    @Override
    public String getText( Object element ) {
        if (element instanceof Server) {
            Server server = (Server)element;
            
            if (server.getCustomLabel() == null) {
                return server.getUrl();
            }

            return server.getCustomLabel();
        }

        if (element instanceof TeiidFolder) {
            return ((TeiidFolder)element).getName();
        }

        if (element instanceof TeiidTranslator) {
            return ((TeiidTranslator)element).getName();
        }

        if (element instanceof TeiidDataSource) {
            if (((TeiidDataSource)element).getDisplayName() != null) {
                return ((TeiidDataSource)element).getDisplayName();
            }
            return ((TeiidDataSource)element).getName();
        }

        if (element instanceof TeiidVdb) {
            // if( !((TeiidVdb)element).isActive() ) {
            // return INACTIVE_VDB_PREFIX + ((TeiidVdb)element).getName();
            // }
            return ((TeiidVdb)element).getName();
        }

        if (element instanceof SourceConnectionBinding) {
            SourceConnectionBinding binding = (SourceConnectionBinding)element;
            return binding.getModelName();
        }
        if (element instanceof String) {
            return (String)element;
        }
        return DqpUiConstants.UTIL.getString(I18nUtil.getPropertyPrefix(TeiidViewTreeProvider.class), new Object[] {
            element.toString(), element.getClass().getName()});
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
        return getChildren(element).length > 0;
    }

    /**
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     *      java.lang.Object)
     * @since 4.2
     */
    @Override
    public void inputChanged( Viewer viewer,
                              Object oldInput,
                              Object newInput ) {
        // nothing to do
    }

    /**
     * Determines if a try to connect to a server should be done based on the last time a try was done and failed.
     * 
     * @param server the server being checked
     * @return <code>true</code> if it is OK to try and connect
     */
    private boolean isOkToConnect( Server server ) {
        boolean check = false; // check map for time

        try {
            this.offlineServersLock.readLock().lock();
            check = this.offlineServerMap.containsKey(server);
        } finally {
            this.offlineServersLock.readLock().unlock();
        }

        if (check) {
            try {
                this.offlineServersLock.writeLock().lock();

                if (this.offlineServerMap.containsKey(server)) {
                    long checkTime = this.offlineServerMap.get(server);

                    // OK to try and connect if last failed attempt was too long ago
                    if ((System.currentTimeMillis() - checkTime) > RETRY_DURATION) {
                        this.offlineServerMap.remove(server);
                        return true;
                    }

                    // don't try and connect because we just tried and failed
                    return false;
                }
            } finally {
                this.offlineServersLock.writeLock().unlock();
            }
        }

        // OK to try and connect
        return true;
    }
    
    public void setShowDataSources(boolean show) {
        this.showDataSources = show;
    }
    
    public void setShowTranslators(boolean show) {
        this.showTranslators = show;
    }
    
    public void setShowVdbs(boolean show) {
        this.showVDBs = show;
    }

    class TeiidFolder {
        Object[] theValues;
        Server server;

        public TeiidFolder(Server server, Object[] values ) {
        	this.server = server;
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
				admin = server.getAdmin();
			} catch (Exception e) {
				DqpUiConstants.UTIL.log(IStatus.ERROR, e, e.getMessage());
			}
        	return admin;
        }
        
        private Server getServer() {
            return this.server;
        }
        
        /**
         * {@inheritDoc}
         *
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            return HashCodeUtil.hashCode(this.server.hashCode(), getClass().hashCode());
        }
    }

    class DataSourcesFolder extends TeiidFolder {
        public DataSourcesFolder(Server server, Object[] values ) {
            super(server, values);
        }

        @Override
        protected String getName() {
            return DATA_SOURCES_FOLDER_NAME;
        }
    }

    class VdbsFolder extends TeiidFolder {
        public VdbsFolder(Server server, Object[] values ) {
            super(server, values);
        }

        @Override
        protected String getName() {
            return VDBS_FOLDER_NAME;
        }
    }

    class TranslatorsFolder extends TeiidFolder {
        public TranslatorsFolder(Server server, Object[] values ) {
            super(server, values);
        }

        @Override
        protected String getName() {
            return TRANSLATORS_FOLDER_NAME;
        }
    }

}
