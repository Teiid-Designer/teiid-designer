/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.teiid.core.designer.util.Base64;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.core.util.KeyInValueHashMap;
import org.teiid.designer.core.util.KeyInValueHashMap.KeyFromValueAdapter;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.runtime.IServersProvider.IServersInitialiseListener;
import org.teiid.designer.runtime.importer.ImportManager;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.IExecutionConfigurationListener;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.spi.ITeiidServerVersionListener;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The <code>ServerManager</code> class manages the creation, deletion, and editing of servers hosting Teiid Instances.
 *
 * @since 8.0
 */
public final class TeiidServerManager implements ITeiidServerManager {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * Designer UI plugin id
     */
    private static final String DESIGNER_UI_PLUGIN_ID = "org.teiid.designer.ui"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's custom label. May not exist if server is not using a custom label.
     */
    public static final String CUSTOM_LABEL_ATTR = "customLabel"; //$NON-NLS-1$
    
    /**
     * The attribute indicating if the server is currently the preview server.
     */
    public static final String DEFAULT_ATTR = "default"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's login password.
     */
    public static final String PASSWORD_ATTR = "password"; //$NON-NLS-1$

    /**
     * The file name used when persisting the server registry.
     */
    public static final String REGISTRY_FILE = "serverRegistry.xml"; //$NON-NLS-1$

    /**
     * The tag used when persisting a server.
     */
    public static final String SERVER_TAG = "server"; //$NON-NLS-1$

    /**
     * The server collection tag used when persisting the server registry.
     */
    public static final String SERVERS_TAG = "servers"; //$NON-NLS-1$
    
    /**
     * The tag used when persisting admin connection info.
     */
    public static final String ADMIN_TAG = "admin"; //$NON-NLS-1$
    
    /**
     * The tag used when persisting jdbc connection info.
     */
    public static final String JDBC_TAG = "jdbc"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's login user.
     */
    public static final String USER_ATTR = "user"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's host value.
     */
    public static final String HOST_ATTR = "host"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's parent id
     */
    public static final String PARENT_SERVER_ID = "parentServerId"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's version value.
     */
    public static final String SERVER_VERSION = "version"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's port value.
     */
    public static final String PORT_ATTR = "port"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's secure value.
     */
    public static final String SECURE_ATTR = "secure"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's jdbc host value.
     */
    public static final String JDBC_HOST_ATTR = "jdbchost"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's jdbc port value.
     */
    public static final String JDBC_PORT_ATTR = "jdbcport"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's login user.
     */
    public static final String JDBC_USER_ATTR = "jdbcuser"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's login password.
     */
    public static final String JDBC_PASSWORD_ATTR = "jdbcpassword"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's secure value.
     */
    public static final String JDBC_SECURE_ATTR = "jdbcsecure"; //$NON-NLS-1$

    private class TeiidServerKeyValueAdapter implements KeyFromValueAdapter<String, ITeiidServer> {

        @Override
        public String getKey(ITeiidServer value) {
            return value.getId();
        }
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The listeners registered to receive {@link ExecutionConfigurationEvent server registry events}.
     */
    private final CopyOnWriteArrayList<IExecutionConfigurationListener> listeners;

    /**
     * The path where the server registry is persisted or <code>null</code> if not persisted.
     */
    private final String stateLocationPath;

    /**
     * The server registry.
     */
    private final KeyInValueHashMap<String, ITeiidServer> teiidServers;

    /**
     * The default teiid instance.
     */
    private ITeiidServer defaultServer;

    /**
     * Listener to the default teiid instance version
     */
    private Set<ITeiidServerVersionListener> teiidServerVersionListeners;

    /**
     * Lock used for when accessing the server registry.
     */
    private final ReadWriteLock serverLock = new ReentrantReadWriteLock();

    /**
     * The status of this manager.
     */
    private RuntimeState state = RuntimeState.INVALID;

    /**
     * The provider used for accessing the collection of available {@link IServer}s
     * rather than relying on {@link parentServersProvider} directly, which makes unit testing difficult
     */
    private IServersProvider parentServersProvider;

    /**
     * The provider used for storing passwords
     */
    private ISecureStorageProvider secureStorageProvider;

    /**
     * Flag indicating whether other open editors should be closed when the default server
     * is changed. Will be set to true when the manager is fully started and AFTER the default
     * server initially set to avoid closing editors from the previous session.
     */
    private boolean closeEditorsOnDefaultServerChange = false;

    /**
     * Internal flag to stop signals being sent to listeners.
     * Should always be called in pairs,
     * ie. turn off -> do work -> turn on
     */
    private boolean notifyListeners = true;

    /* Listen for changes to the default teiid instance version preference */
    private IPreferenceChangeListener preferenceChangeListener = new IPreferenceChangeListener() {
        @Override
        public void preferenceChange(PreferenceChangeEvent event) {
            if (! ITeiidServerManager.DEFAULT_TEIID_SERVER_VERSION_ID.equals(event.getKey()))
                return;

            if (getDefaultServer() != null) {
                // Default teiid instance exists so this preference is superceded by this server
                return;
            }

           if (getDefaultServerVersion().equals(event.getNewValue()))
               return;

           // Server version change has occurred so close all editors and notify server version listeners
           closeEditors();

           if (teiidServerVersionListeners == null)
               return;

           for (ITeiidServerVersionListener listener : teiidServerVersionListeners) {
               listener.versionChanged(getDefaultServerVersion());
           }
        }
    };

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Create default instance
     */
    public TeiidServerManager() {
        this(DqpPlugin.getInstance().getRuntimePath().toFile().getAbsolutePath(),
                DqpPlugin.getInstance().getServersProvider(),
                ConnectivityUtil.getSecureStorageProvider());
    }

    /**
     * @param stateLocationPath the directory where the {@link ITeiidServer} registry} is persisted (may be <code>null</code> if
     *        persistence is not desired)
     * @param parentServersProvider 
     * @param secureStorageProvider 
     */
    public TeiidServerManager( String stateLocationPath, IServersProvider parentServersProvider,
                                                     ISecureStorageProvider secureStorageProvider ) {
        CoreArgCheck.isNotNull(stateLocationPath);
        CoreArgCheck.isNotNull(parentServersProvider);
        CoreArgCheck.isNotNull(secureStorageProvider);

        this.teiidServers = new KeyInValueHashMap<String, ITeiidServer>(new TeiidServerKeyValueAdapter());
        this.stateLocationPath = stateLocationPath;
        this.parentServersProvider = parentServersProvider;
        this.secureStorageProvider = secureStorageProvider;
        this.listeners = new CopyOnWriteArrayList<IExecutionConfigurationListener>();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private void checkStarted() {
        if (! RuntimeState.STARTED.equals(state))
            throw new RuntimeException(
                                       "Programming error: The TeiidServerManager is being used before it has been initialised"); //$NON-NLS-1$
    }

    /**
     * Get the implementation of the {@link ISecureStorageProvider} used by
     * this server manager.
     * 
     * @return implementation of {@link ISecureStorageProvider}
     */
    @Override
    public ISecureStorageProvider getSecureStorageProvider() {
        checkStarted();

        return secureStorageProvider;
    }

    /**
     * @return defaultServer
     */
    @Override
    public ITeiidServer getDefaultServer() {
        checkStarted();

        return defaultServer;
    }

    /**
     * @param id the id of the server being requested (never <code>null</code> )
     * @return the requested server or <code>null</code> if not found in the registry
     */
    @Override
    public ITeiidServer getServer( String id ) {
        checkStarted();
        CoreArgCheck.isNotNull(id, "id"); //$NON-NLS-1$

        return teiidServers.get(id);
    }

    /**
     * @param parentServer the parent server of the requested Teiid Instance
     * @return the requested server or <code>null</code> if not found in the registry
     */
    @Override
    public ITeiidServer getServer( IServer parentServer) {
        checkStarted();
        CoreArgCheck.isNotNull(parentServer, "parentServer"); //$NON-NLS-1$

        for (ITeiidServer teiidServer : getServers()) {
            if (parentServer.equals(teiidServer.getParent())) {
                return teiidServer;
            }
        }

        return null;
    }
    
    /**
     * @return an unmodifiable collection of registered servers (never <code>null</code>)
     */
    @Override
    public Collection<ITeiidServer> getServers() {
        checkStarted();

        try {
            this.serverLock.readLock().lock();
            return Collections.unmodifiableCollection(this.teiidServers.values());
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    private ITeiidServerVersion getDefaultServerVersionInternal() {
        if (this.defaultServer == null) {
            // Preference is stored in the ui plugin which we do not have a dependency on so have to
            // get at the preference in this way. Not great but alternative is to try and save the property
            // in this plugin's properties and not really worth it.
            IEclipsePreferences preferences = DqpPlugin.getInstance().getPreferences(DESIGNER_UI_PLUGIN_ID);
            ITeiidServerVersion defaultVersion = TeiidServerVersion.deriveUltimateDefaultServerVersion();
            String versionString = preferences.get(DEFAULT_TEIID_SERVER_VERSION_ID, defaultVersion.toString());
            return new TeiidServerVersion(versionString);
        }

        return defaultServer.getServerVersion();
    }

    /**
     * Get the targeted Teiid Instance version
     *
     * @return Teiid Instance version
     */
    @Override
    public ITeiidServerVersion getDefaultServerVersion() {
        checkStarted();
        return getDefaultServerVersionInternal();
    }

    @Override
    public RuntimeState getState() {
        return this.state;
    }

    @Override
    public boolean isStarted() {
        return RuntimeState.STARTED.equals(this.state);
    }

    /**
     * @return the name of the state file that the server registry is persisted to or <code>null</code>
     */
    public String getStateFileName() {
        String name = this.stateLocationPath;

        if (this.stateLocationPath != null) {
            name += File.separatorChar + REGISTRY_FILE;
        }

        return name;
    }

    /**
     * Registers the specified <code>Server</code>.
     * 
     * @param teiidServer the server being added
     * @param notifyListeners indicates if registry listeners should be notified
     * @return a status indicating if the server was added to the registry
     */
    private IStatus addServerInternal( ITeiidServer teiidServer, boolean notifyListeners ) {
        CoreArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$

        boolean added = false;
        ITeiidServer defaultServer = null;

        try {
            this.serverLock.writeLock().lock();

            if (!isRegisteredInternal(teiidServer)) {
                if (teiidServers.isEmpty()) {
                    defaultServer = teiidServer;
                }
                added = this.teiidServers.add(teiidServer);
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (added) {
            if (notifyListeners) {
                notifyListeners(ExecutionConfigurationEvent.createAddServerEvent(teiidServer));
            }
            if( defaultServer != null ) {
                setDefaultServerInternal(defaultServer);
            }
            return Status.OK_STATUS;
        }

        // server already exists
        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("serverExistsMsg", teiidServer)); //$NON-NLS-1$
    }

    /**
     * Registers the specified <code>PersistedServer</code>.
     *
     * @param teiidServer the server being added (never <code>null</code>)
     * @return a status indicating if the server was added to the registry
     */
    @Override
    public IStatus addServer( ITeiidServer teiidServer ) {
        checkStarted();

        return addServerInternal(teiidServer, true);
    }

    /**
     * @param teiidServer the server being removed
     * @param notifyListeners indicates if registry listeners should be notified
     * @return a status indicating if the specified server was removed from the registry
     */
    private IStatus removeServerInternal( ITeiidServer teiidServer, boolean notifyListeners ) {
        ITeiidServer removed = null;

        try {
            this.serverLock.writeLock().lock();

            removed = this.teiidServers.remove(teiidServer);

            // Check if removed server is default, then set to first server
            if (teiidServer.equals(getDefaultServer())) {
                // If no servers left, set defaultServer to null
                if (this.teiidServers.isEmpty())
                    setDefaultServerInternal(null);
                else
                    setDefaultServerInternal(this.teiidServers.values().iterator().next());
            }

        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (removed != null) {
            if (notifyListeners) {
                notifyListeners(ExecutionConfigurationEvent.createRemoveServerEvent(teiidServer));
            }

            return Status.OK_STATUS;
        }

        // server could not be removed
        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("serverManagerRegistryRemoveUnexpectedError", teiidServer)); //$NON-NLS-1$
    }

    /**
     * @param teiidServer the server being removed (never <code>null</code>)
     * @return a status indicating if the specified segetUrlrver was removed from the registry (never <code>null</code>)
     */
    @Override
    public IStatus removeServer( ITeiidServer teiidServer ) {
        checkStarted();
        CoreArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$

        return removeServerInternal(teiidServer, true);
    }

    /**
     * Is this server the default
     * 
     * @param teiidServer
     * 
     * @return true if this server is the default, false otherwise.
     */
    @Override
    public boolean isDefaultServer( ITeiidServer teiidServer ) {
        checkStarted();
        CoreArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$

        if (this.defaultServer == null) {
            return false;
        }
        return this.defaultServer.equals(teiidServer);
    }

    private boolean isRegisteredInternal(ITeiidServer teiidServer) {
        try {
            this.serverLock.readLock().lock();
            return this.teiidServers.containsValue(teiidServer);
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * @param teiidServer the server being tested (never <code>null</code>)
     * @return <code>true</code> if the server has been registered
     */
    @Override
    public boolean isRegistered( ITeiidServer teiidServer ) {
        checkStarted();
        CoreArgCheck.isNotNull(teiidServer, "server"); //$NON-NLS-1$
        return isRegisteredInternal(teiidServer);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.runtime.spi.EventManager#notifyListeners(org.teiid.designer.runtime.spi.ExecutionConfigurationEvent)
     */
    @Override
    public void permitListeners(boolean enable) {
        this.notifyListeners = enable;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.spi.EventManager#notifyListeners(org.teiid.designer.runtime.spi.ExecutionConfigurationEvent)
     */
    @Override
    public void notifyListeners( ExecutionConfigurationEvent event ) {
        if (!notifyListeners)
            return;

        if (RuntimeState.SHUTTING_DOWN.equals(getState()) || RuntimeState.SHUTDOWN.equals(getState())) {
            // Since we are shutting down then everything is being saved and no reason to notify UI
            return;
        }

        for (IExecutionConfigurationListener l : this.listeners) {
            try {
                l.configurationChanged(event);
            } catch (Exception e) {
                removeListener(l);
                Util.log(IStatus.WARNING, e, Util.getString("unexpectedErrorInExecutionConfigurationListener", l)); //$NON-NLS-1$
            }
        }
    }

    private IServer findParentServer(String host, String parentServerId, ITeiidAdminInfo teiidAdminInfo) throws OrphanedTeiidServerException {
        IServer[] servers = parentServersProvider.getServers();
        for (IServer server : servers) {
            if (! host.equals(server.getHost()))
                continue;

            if (parentServerId != null && ! server.getId().equals(parentServerId)) {
                // Double checks against the parent server id only if a parent server id was
                // save. In the case of the old registry format, this was not possible so host
                // comparison is sufficient
                continue;
            }

            return server;
        }

        throw new OrphanedTeiidServerException(teiidAdminInfo);
    }

    /**
     * Initialise those managers and listeners under the control of the server manager
     * that may well rely on it.
     */
    private void initialiseManagers() {
        PreviewManager previewManager = PreviewManager.getInstance();
        addListener(previewManager);

        addListener(ImportManager.getInstance());

        IEclipsePreferences preferences = DqpPlugin.getInstance().getPreferences(DESIGNER_UI_PLUGIN_ID);
        preferences.addPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    public ITeiidServer loadServers(File serverFile) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(serverFile);
        Node root = doc.getDocumentElement();
        NodeList servers = root.getChildNodes();

        ITeiidServer defaultServer = null;

        for (int size = servers.getLength(), i = 0; i < size; ++i) {
            ITeiidAdminInfo teiidAdminInfo = null;
            ITeiidJdbcInfo teiidJdbcInfo = null;
            Node serverNode = servers.item(i);

            // server attributes (host, custom label, default teiid instance)
            NamedNodeMap serverAttributeMap = serverNode.getAttributes();

            if (serverAttributeMap == null) {
                continue;
            }

            String host = null;
            String parentServerId = null;
            String customLabel = null;
            boolean previewServer = false;

            // version attribute
            ITeiidServerVersion teiidServerVersion = TeiidServerVersion.DEFAULT_TEIID_SERVER;
            Node versionNode = serverAttributeMap.getNamedItem(SERVER_VERSION);
            if (versionNode != null) teiidServerVersion = new TeiidServerVersion(versionNode.getNodeValue());

            // host attribute
            Node hostNode = serverAttributeMap.getNamedItem(HOST_ATTR);

            if (hostNode != null) {
                host = hostNode.getNodeValue();
            }

            Node parentServerNode = serverAttributeMap.getNamedItem(PARENT_SERVER_ID);

            if (parentServerNode != null) {
                parentServerId = parentServerNode.getNodeValue();
            }

            // custom label attribute
            Node customLabelNode = serverAttributeMap.getNamedItem(CUSTOM_LABEL_ATTR);

            if (customLabelNode != null) {
                customLabel = customLabelNode.getNodeValue();
            }

            // default teiid instance attribute
            Node defaultServerNode = serverAttributeMap.getNamedItem(DEFAULT_ATTR);

            if (defaultServerNode != null) {
                previewServer = Boolean.parseBoolean(defaultServerNode.getNodeValue());
            }

            // Check for newer XML structure where server contains child nodes (admin & jdbc elements)
            NodeList connectionNodes = serverNode.getChildNodes();

            if (connectionNodes.getLength() > 0) {
                for (int connSize = connectionNodes.getLength(), j = 0; j < connSize; ++j) {
                    Node connNode = connectionNodes.item(j);
                    if (connNode.getNodeType() != Node.TEXT_NODE) {
                        if (connNode.getNodeName().equalsIgnoreCase(ADMIN_TAG)) {
                            NamedNodeMap attributeMap = connNode.getAttributes();
                            if (attributeMap == null) continue;

                            // if host is null than an older registry xml file is being used
                            if (host == null) {
                                Node adminHostNode = attributeMap.getNamedItem(HOST_ATTR);
                                assert (adminHostNode != null);
                                host = adminHostNode.getNodeValue();
                            }

                            // port must be non-null/not empty to be valid server
                            Node adminPortNode = attributeMap.getNamedItem(PORT_ATTR); // should always have one
                            assert (adminPortNode != null);
                            String adminPort = adminPortNode.getNodeValue();

                            // username must be non-null/not empty to be valid server
                            Node userNode = attributeMap.getNamedItem(USER_ATTR); // should always have one
                            assert (userNode != null);
                            String adminUsername = userNode.getNodeValue();
                            Node passwordNode = attributeMap.getNamedItem(PASSWORD_ATTR);
                            String adminPassword = ((passwordNode == null) ? null : new String(
                                                                                               Base64.decode(passwordNode.getNodeValue()),
                                                                                               "UTF-8")); //$NON-NLS-1$
                            Node adminSecureNode = attributeMap.getNamedItem(SECURE_ATTR);
                            String adminSecureStr = ((adminSecureNode == null) ? Boolean.FALSE.toString() : adminSecureNode.getNodeValue());

                            teiidAdminInfo = new TeiidAdminInfo(adminPort, adminUsername, secureStorageProvider,
                                                                adminPassword, Boolean.parseBoolean(adminSecureStr));
                        } else if (connNode.getNodeName().equalsIgnoreCase(JDBC_TAG)) {
                            NamedNodeMap attributeMap = connNode.getAttributes();
                            if (attributeMap == null) continue;

                            // if host is null than an older registry xml file is being used
                            if (host == null) {
                                Node jdbcHostNode = attributeMap.getNamedItem(JDBC_HOST_ATTR);
                                assert (jdbcHostNode != null);
                                host = jdbcHostNode.getNodeValue();
                            }

                            // port must be non-null/not empty to be valid server
                            Node jdbcPortNode = attributeMap.getNamedItem(JDBC_PORT_ATTR);
                            assert (jdbcPortNode != null);
                            String jdbcPort = jdbcPortNode.getNodeValue();

                            // username must be non-null/not empty to be valid server
                            Node jdbcUserNode = attributeMap.getNamedItem(JDBC_USER_ATTR);
                            assert (jdbcUserNode != null);
                            String jdbcUsername = jdbcUserNode.getNodeValue();

                            Node jdbcPasswordNode = attributeMap.getNamedItem(JDBC_PASSWORD_ATTR);
                            String jdbcPassword = ((jdbcPasswordNode == null) ? null : new String(
                                                                                                  Base64.decode(jdbcPasswordNode.getNodeValue()),
                                                                                                  "UTF-8")); //$NON-NLS-1$
                            Node jdbcSecureNode = attributeMap.getNamedItem(JDBC_SECURE_ATTR);
                            String jdbcSecureStr = ((jdbcSecureNode == null) ? Boolean.FALSE.toString() : jdbcSecureNode.getNodeValue());
                            teiidJdbcInfo = new TeiidJdbcInfo(jdbcPort, jdbcUsername, secureStorageProvider, jdbcPassword,
                                                              Boolean.parseBoolean(jdbcSecureStr));
                        }
                    }
                }
            }

            // add server to registry
            IServer parentServer = null;
            try {
                parentServer = findParentServer(host, parentServerId, teiidAdminInfo);
            } catch (OrphanedTeiidServerException ex) {
                // Cannot add the Teiid Instance since it has no parent
                continue;
            }

            TeiidServerFactory teiidServerFactory = new TeiidServerFactory();
            ITeiidServer teiidServer = teiidServerFactory.createTeiidServer(teiidServerVersion,
                                                                            teiidAdminInfo,
                                                                            teiidJdbcInfo,
                                                                            this,
                                                                            parentServer);
            teiidServer.setCustomLabel(customLabel);

           addServerInternal(teiidServer, true);

            if (previewServer) {
                defaultServer = teiidServer;
            }
        }

        return defaultServer;
    }

    private void restoreStateInternal() {

        // Initialize teiid parent server state listener
        parentServersProvider.addServerStateListener(TeiidParentServerListener.getInstance());
        parentServersProvider.addServerLifecycleListener(TeiidParentServerListener.getInstance());

        ITeiidServer defaultServer = null;

        try {
            this.notifyListeners = false;

            if (this.stateLocationPath == null || ! stateFileExists()) {
                // Started will be called from the finally clause.
                return;
            }

            defaultServer = loadServers(new File(getStateFileName()));

        } catch (Exception e) {
            Util.log(e);
        } finally {
            this.notifyListeners = true;
            this.state = RuntimeState.STARTED;
            initialiseManagers();

            // Set the default teiid instance. Doing this here will allow the managers to detect the change
            // and initialise accordingly
            setDefaultServerInternal(defaultServer);

            // Set the default server for the first time so set the close editors flag to true, ensuring that
            // editors will be subsequently be closed when the default server version is changed.
            closeEditorsOnDefaultServerChange = true;
        }
    }

    @Override
    public void restoreState() {
        this.state = RuntimeState.RESTORING;

        if (parentServersProvider.isInitialised()) {
            restoreStateInternal();
            return;
        }

        // Not yet initialised so install a listener to await the initialisation
        IServersInitialiseListener listener = new IServersInitialiseListener() {

            @Override
            public void serversInitialised() {
                restoreStateInternal();
            }
        };

        parentServersProvider.addServerInitialisedListener(listener);
    }

    private void setDefaultServerInternal(ITeiidServer teiidServer) {
        if (this.defaultServer == null && teiidServer == null) {
            // Both are null so no point in continuing
            return;
        }

        ITeiidServer oldDefaultServer = this.defaultServer;
        this.defaultServer = teiidServer;

        if (teiidServerVersionListeners != null) {
            // Notify the server version listeners that the server has changed
            for (ITeiidServerVersionListener listener : teiidServerVersionListeners) {
                listener.serverChanged(defaultServer);
            }
        }

        /*
         * Compare the versions of the server.
         *
         * Only if the server version has changed should the model editors be closed
         * and server version listeners notified.
         */
        ITeiidServerVersion oldServerVersion = oldDefaultServer != null ? oldDefaultServer.getServerVersion() : null;

        if (teiidServerVersionListeners != null && ! getDefaultServerVersionInternal().equals(oldServerVersion)) {
            // Server version change has occurred so close all editors and notify server version listeners
            closeEditors();

            for (ITeiidServerVersionListener listener : teiidServerVersionListeners) {
                listener.versionChanged(getDefaultServerVersionInternal());
            }
        }

        notifyListeners(ExecutionConfigurationEvent.createSetDefaultServerEvent(oldDefaultServer, this.defaultServer));
    }

    /**
     * @param teiidServer Sets defaultServer to the specified value. May be null.
     */
    @Override
    public void setDefaultServer( ITeiidServer teiidServer ) {
        checkStarted();
        setDefaultServerInternal(teiidServer);
    }

    /**
     * Close editors associated with modelling projects
     */
    private void closeEditors() {
        if (RuntimeState.RESTORING == state || ! closeEditorsOnDefaultServerChange) {
            // Avoid closing editors on startup since the default teiid instance is simply being assigned
            return;
        }

        Display display = PlatformUI.getWorkbench().getDisplay();
        if (display == null)
            return;

        // Ensure that this is performed on the UI thread
        display.asyncExec(new Runnable() {
            @Override
            public void run() {

                for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
                    for (IWorkbenchPage page : window.getPages()) {
                        IEditorReference[] editorReferences = page.getEditorReferences();
                        for (IEditorReference editorRef : editorReferences) {
                            IEditorInput input;
                            try {
                                input = editorRef.getEditorInput();
                                IResource resource = (IResource)input.getAdapter(IResource.class);

                                // Only close those editors associated with modelling projects
                                // rather than blindly closing all editors
                                if (resource != null && DotProjectUtils.isModelerProject(resource.getProject())) {
                                    page.closeEditor(editorRef.getEditor(true), true);
                                }
                            } catch (PartInitException ex) {
                                DqpPlugin.Util.log(ex);
                            }
                        }
                    }
                }
            }
        });
    }

    @Override
    public void shutdown( IProgressMonitor monitor ) throws Exception {
        // return if already being shutdown
        if ((this.state == RuntimeState.SHUTTING_DOWN) || (this.state == RuntimeState.SHUTDOWN)) return;

        try {
            this.state = RuntimeState.SHUTTING_DOWN;

            if (monitor != null) monitor.subTask(Util.getString("serverManagerSavingServerRegistryTask")); //$NON-NLS-1$

            IEclipsePreferences preferences = DqpPlugin.getInstance().getPreferences(DESIGNER_UI_PLUGIN_ID);
            if (preferences != null)
                preferences.removePreferenceChangeListener(preferenceChangeListener);

            saveState();
        } finally {
            this.state = RuntimeState.SHUTDOWN;
        }
    }

    @Override
    public void saveState(String stateFilePath) {
        FileOutputStream fileOutputStream = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // create root element
            Element root = doc.createElement(SERVERS_TAG);
            doc.appendChild(root);

            Collection<ITeiidServer> servers = teiidServers.values();
            for (ITeiidServer teiidServer : servers) {
                Element serverElement = doc.createElement(SERVER_TAG);
                root.appendChild(serverElement);
                    
                { // Server Version
                    serverElement.setAttribute(SERVER_VERSION, teiidServer.getServerVersion().toString());
                }
                    
                { // Host
                    serverElement.setAttribute(HOST_ATTR, teiidServer.getHost());
                }
                    
                { // Parent Server Id
                    serverElement.setAttribute(PARENT_SERVER_ID, teiidServer.getParent().getId());
                }
                    
                { // CUSTOM LABEL
                    if (!StringUtilities.isEmpty(teiidServer.getCustomLabel())) {
                        serverElement.setAttribute(CUSTOM_LABEL_ATTR, teiidServer.getCustomLabel());
                    }
                }
                    
                { // ADMIN CONNECTION INFO
                    Element adminElement = doc.createElement(ADMIN_TAG);
                    serverElement.appendChild(adminElement);

                    adminElement.setAttribute(PORT_ATTR, teiidServer.getTeiidAdminInfo().getPort());
                    adminElement.setAttribute(USER_ATTR, teiidServer.getTeiidAdminInfo().getUsername());
                        
                    /* password is saved in the eclipse secure storage */
                        
//                 if( teiidServer.getTeiidAdminInfo().getPassword() != null) {
//                     	adminElement.setAttribute(PASSWORD_ATTR, Base64.encodeBytes(teiidServer.getTeiidAdminInfo().getPassword().getBytes()));
//                 }
                        
                    adminElement.setAttribute(SECURE_ATTR, Boolean.toString(teiidServer.getTeiidAdminInfo().isSecure()));
                }
                    
                { // JDBC CONNECTION INFO
                    Element jdbcElement = doc.createElement(JDBC_TAG);
                    serverElement.appendChild(jdbcElement);

                    jdbcElement.setAttribute(JDBC_PORT_ATTR, teiidServer.getTeiidJdbcInfo().getPort());
                    jdbcElement.setAttribute(JDBC_USER_ATTR, teiidServer.getTeiidJdbcInfo().getUsername());
                        
                    /* password is saved in the eclipse secure storage */
                        
//                 if( teiidServer.getTeiidJdbcInfo().getPassword() != null) {
//                     	jdbcElement.setAttribute(JDBC_PASSWORD_ATTR, Base64.encodeBytes(teiidServer.getTeiidJdbcInfo().getPassword().getBytes()));
//                 }
                        
                    jdbcElement.setAttribute(JDBC_SECURE_ATTR, Boolean.toString(teiidServer.getTeiidJdbcInfo().isSecure()));
                }
                    
                if ((defaultServer != null) && (defaultServer.equals(teiidServer))) {
                    serverElement.setAttribute(DEFAULT_ATTR, Boolean.toString(true));
                }
            }

            DOMSource source = new DOMSource(doc);
            fileOutputStream = new FileOutputStream(stateFilePath);
            StreamResult resultXML = new StreamResult(fileOutputStream);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$ 
            transformer.transform(source, resultXML);
        } catch (Exception e) {
            IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
                                        Util.getString("errorSavingServerRegistry", getStateFileName())); //$NON-NLS-1$
            Util.log(status);
        } finally {
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException ex) {
                    Util.log(ex);
                }
        }
    }

    private void saveState() throws TransformerFactoryConfigurationError {
        if (this.stateLocationPath == null)
            return; // no persistence

        if (teiidServers.isEmpty()) {
            if (stateFileExists()) {
                // delete current registry file since all servers have been deleted
                try {
                    new File(getStateFileName()).delete();
                } catch (Exception e) {
                    IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
                                                Util.getString("errorDeletingServerRegistryFile", getStateFileName())); //$NON-NLS-1$
                    Util.log(status);
                }
            }

            return;
        }

        saveState(getStateFileName());
    }

    /**
     * @return <code>true</code> if the state file already exists
     */
    private boolean stateFileExists() {
        return new File(getStateFileName()).exists();
    }

    /**
     * Updates the server registry with a new version of a server.
     * 
     * @param replacedServer the version of the server being replaced (never <code>null</code>)
     * @param updatedServer the new version of the server being put in the server registry (never <code>null</code>)
     * @return a status indicating if the server was updated in the registry (never <code>null</code>)
     */
    public IStatus updateServer( ITeiidServer replacedServer, ITeiidServer updatedServer ) {
        checkStarted();
        CoreArgCheck.isNotNull(replacedServer, "previousServerVersion"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(updatedServer, "newServerVersion"); //$NON-NLS-1$

        IStatus status = null;

        try {
            this.serverLock.writeLock().lock();
            status = removeServerInternal(replacedServer, false);

            if (status.isOK()) {
                status = addServerInternal(updatedServer, false);

                if (status.isOK()) {
                    // all good so notify listeners
                    notifyListeners(ExecutionConfigurationEvent.createUpdateServerEvent(replacedServer, updatedServer));
                    return status;
                }

                // unexpected problem adding new version of server to registry so add old one back
                IStatus undoRemoveServerStatus = addServerInternal(replacedServer, false);

                if (undoRemoveServerStatus.getSeverity() == IStatus.ERROR) {
                    Util.log(undoRemoveServerStatus);
                }

                return new Status(IStatus.ERROR, PLUGIN_ID,
                                  Util.getString("serverManagerRegistryUpdateAddError", status.getMessage())); //$NON-NLS-1$
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        // unexpected problem removing server from registry
        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("serverManagerRegistryUpdateRemoveError", status.getMessage())); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.runtime.spi.EventManager#addListener(org.teiid.designer.runtime.spi.IExecutionConfigurationListener)
     */
    @Override
    public boolean addListener( IExecutionConfigurationListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.teiid.designer.runtime.spi.EventManager#removeListener(org.teiid.designer.runtime.spi.IExecutionConfigurationListener)
     */
    @Override
    public boolean removeListener( IExecutionConfigurationListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    @Override
    public void addTeiidServerVersionListener(ITeiidServerVersionListener listener) {
        if (teiidServerVersionListeners == null)
            teiidServerVersionListeners = new HashSet<ITeiidServerVersionListener>();

        teiidServerVersionListeners.add(listener);
    }

    @Override
    public void removeTeiidServerVersionListener(ITeiidServerVersionListener listener) {
        if (teiidServerVersionListeners == null)
            return;

        teiidServerVersionListeners.remove(listener);
    }
}
