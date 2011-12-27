/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import static com.metamatrix.modeler.dqp.DqpPlugin.PLUGIN_ID;
import static com.metamatrix.modeler.dqp.DqpPlugin.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.util.Base64;
import org.teiid.designer.runtime.preview.PreviewManager;
import org.teiid.net.TeiidURL;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;

/**
 * The <code>ServerManager</code> class manages the creation, deletion, and editing of servers hosting Teiid servers.
 */
public final class ServerManager implements EventManager {

    private enum RuntimeState {
        INVALID,
        STARTED,
        SHUTTING_DOWN,
        SHUTDOWN
    }

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * The attribute used to persist a server's custom label. May not exist if server is not using a custom label.
     */
    private static final String CUSTOM_LABEL_ATTR = "customLabel"; //$NON-NLS-1$
    
    /**
     * The attribute indicating if the server is currently the preview server.
     */
    private static final String DEFAULT_ATTR = "default"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's login password.
     */
    private static final String PASSWORD_ATTR = "password"; //$NON-NLS-1$

    /**
     * The file name used when persisting the server registry.
     */
    private static final String REGISTRY_FILE = "serverRegistry.xml"; //$NON-NLS-1$

    /**
     * The tag used when persisting a server.
     */
    private static final String SERVER_TAG = "server"; //$NON-NLS-1$

    /**
     * The server collection tag used when persisting the server registry.
     */
    private static final String SERVERS_TAG = "servers"; //$NON-NLS-1$
    
    /**
     * The tag used when persisting admin connection info.
     */
    private static final String ADMIN_TAG = "admin"; //$NON-NLS-1$
    
    /**
     * The tag used when persisting jdbc connection info.
     */
    private static final String JDBC_TAG = "jdbc"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's URL.
     */
    private static final String URL_ATTR = "url"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's login user.
     */
    private static final String USER_ATTR = "user"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's host value.
     */
    private static final String HOST_ATTR = "host"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's port value.
     */
    private static final String PORT_ATTR = "port"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's secure value.
     */
    private static final String SECURE_ATTR = "secure"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's jdbc host value.
     */
    private static final String JDBC_HOST_ATTR = "jdbchost"; //$NON-NLS-1$

    /**
     * The attribute used to persist a server's jdbc port value.
     */
    private static final String JDBC_PORT_ATTR = "jdbcport"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's login user.
     */
    private static final String JDBC_USER_ATTR = "jdbcuser"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's login password.
     */
    private static final String JDBC_PASSWORD_ATTR = "jdbcpassword"; //$NON-NLS-1$
    
    /**
     * The attribute used to persist a server's secure value.
     */
    private static final String JDBC_SECURE_ATTR = "jdbcsecure"; //$NON-NLS-1$
    
    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    /**
     * The listeners registered to receive {@link ExecutionConfigurationEvent server registry events}.
     */
    private final CopyOnWriteArrayList<IExecutionConfigurationListener> listeners;

    /**
     * The manager responsible for the maintenace of the Preview VDBs,
     */
    private final PreviewManager previewManager;

    /**
     * The path where the server registry is persisted or <code>null</code> if not persisted.
     */
    private final String stateLocationPath;

    /**
     * The server registry.
     */
    private final List<Server> servers;

    /**
     * The server registry.
     */
    private Server defaultServer;

    /**
     * Lock used for when accessing the server registry.
     */
    private final ReadWriteLock serverLock = new ReentrantReadWriteLock();

    /**
     * The status of this manager.
     */
    private RuntimeState state = RuntimeState.INVALID;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * @param stateLocationPath the directory where the {@link Server} registry} is persisted (may be <code>null</code> if
     *        persistence is not desired)
     */
    public ServerManager( String stateLocationPath ) {
        this.servers = new ArrayList<Server>();
        this.stateLocationPath = stateLocationPath;
        this.listeners = new CopyOnWriteArrayList<IExecutionConfigurationListener>();

        // construct Preview VDB Manager
        PreviewManager tempPreviewManager = null;

        if (stateLocationPath != null) {
            try {
                tempPreviewManager = new PreviewManager();
                ResourcesPlugin.getWorkspace().addResourceChangeListener(tempPreviewManager);
                addListener(tempPreviewManager);
            } catch (Exception e) {
                Util.log(IStatus.ERROR, e, Util.getString("serverManagerErrorConstructingPreviewManager")); //$NON-NLS-1$
            }
        }

        this.previewManager = tempPreviewManager;
        this.state = RuntimeState.STARTED;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.EventManager#addListener(org.teiid.designer.runtime.IExecutionConfigurationListener)
     */
    @Override
    public boolean addListener( IExecutionConfigurationListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(listener);
    }

    /**
     * Registers the specified <code>PersistedServer</code>.
     * 
     * @param server the server being added (never <code>null</code>)
     * @return a status indicating if the server was added to the registry
     */
    public IStatus addServer( Server server ) {
        CoreArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
        return internalAddServer(server, true);
    }

    /**
     * @return defaultServer
     */
    public Server getDefaultServer() {
        return defaultServer;
    }

    /**
     * @return the preview VDB manager (may be <code>null</code> if preview not enabled because of no state space folder)
     */
    public PreviewManager getPreviewManager() {
        return this.previewManager;
    }

    /**
     * @param url the URL of the server being requested (never <code>null</code> )
     * @return the requested server or <code>null</code> if not found in the registry
     */
    public Server getServer( String url ) {
        CoreArgCheck.isNotNull(url, "url"); //$NON-NLS-1$

        for (Server server : getServers()) {
            if (url.equals(server.getUrl())) {
                return server;
            }
        }

        return null;
    }

    /**
     * @return an unmodifiable collection of registered servers (never <code>null</code>)
     */
    public Collection<Server> getServers() {
        try {
            this.serverLock.readLock().lock();
            return Collections.unmodifiableCollection(new ArrayList<Server>(this.servers));
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * @return the name of the state file that the server registry is persisted to or <code>null</code>
     */
    private String getStateFileName() {
        String name = this.stateLocationPath;

        if (this.stateLocationPath != null) {
            name += File.separatorChar + REGISTRY_FILE;
        }

        return name;
    }

    /**
     * Registers the specified <code>Server</code>.
     * 
     * @param server the server being added
     * @param notifyListeners indicates if registry listeners should be notified
     * @return a status indicating if the server was added to the registry
     */
    private IStatus internalAddServer( Server server,
                                       boolean notifyListeners ) {
        boolean added = false;
        Server defaultServer = null;

        try {
            this.serverLock.writeLock().lock();

            if (!isRegistered(server)) {
                if (servers.isEmpty()) {
                    defaultServer = server;
                }
                added = this.servers.add(server);
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (added) {
            if (notifyListeners) {
                notifyListeners(ExecutionConfigurationEvent.createAddServerEvent(server));
            }
            if( defaultServer != null ) {
            	setDefaultServer(defaultServer);
            }
            return Status.OK_STATUS;
        }

        // server already exists
        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("serverExistsMsg", server)); //$NON-NLS-1$
    }

    /**
     * @param server the server being removed
     * @param notifyListeners indicates if registry listeners should be notified
     * @return a status indicating if the specified server was removed from the registry
     */
    private IStatus internalRemoveServer( Server server,
                                          boolean notifyListeners ) {
        boolean removed = false;

        try {
            this.serverLock.writeLock().lock();

            // see if registered server has the same key
            for (Server registeredServer : this.servers) {
                if (registeredServer.hasSameKey(server)) {
                    removed = this.servers.remove(registeredServer);
                    if (removed) {
                        // If no servers left, set defaultServer to null
                        if (this.servers.isEmpty()) {
                            setDefaultServer(null);
                        }
                        // Check if removed server is default, then set to first server
                        if (server.equals(getDefaultServer())) {
                            setDefaultServer(this.servers.get(0));
                        }
                    }
                    break;
                }
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (removed) {
            if (notifyListeners) {
                notifyListeners(ExecutionConfigurationEvent.createRemoveServerEvent(server));
            }

            return Status.OK_STATUS;
        }

        // server could not be removed
        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("serverManagerRegistryRemoveUnexpectedError", server)); //$NON-NLS-1$
    }

    public boolean isDefaultServer( Server server ) {
        CoreArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
        if (this.defaultServer == null) {
            return false;
        }
        return this.defaultServer.equals(server);
    }

    /**
     * @param server the server being tested (never <code>null</code>)
     * @return <code>true</code> if the server has been registered
     */
    public boolean isRegistered( Server server ) {
        CoreArgCheck.isNotNull(server, "server"); //$NON-NLS-1$

        try {
            this.serverLock.readLock().lock();

            // check to make sure no other registered server has the same key
            for (Server registeredServer : this.servers) {
                if (registeredServer.hasSameKey(server)) {
                    return true;
                }
            }

            return false;
        } finally {
            this.serverLock.readLock().unlock();
        }
    }
    
    /**
     * @param url the url being tested (never <code>null</code>)
     * @return <code>true</code> if a server with the url has been registered
     */
    public boolean isRegisteredUrl( String url ) {
        CoreArgCheck.isNotEmpty(url, "url"); //$NON-NLS-1$

        try {
            this.serverLock.readLock().lock();

            // check to make sure no other registered server has the same key
            for (Server registeredServer : this.servers) {
                if (registeredServer.getUrl().equals(url)) {
                    return true;
                }
            }

            return false;
        } finally {
            this.serverLock.readLock().unlock();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.EventManager#notifyListeners(org.teiid.designer.runtime.ExecutionConfigurationEvent)
     */
    @Override
    public void notifyListeners( ExecutionConfigurationEvent event ) {
        for (IExecutionConfigurationListener l : this.listeners) {
            try {
                l.configurationChanged(event);
            } catch (Exception e) {
                removeListener(l);
                Util.log(IStatus.WARNING, e, Util.getString("unexpectedErrorInExecutionConfigurationListener", l)); //$NON-NLS-1$
            }
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.EventManager#removeListener(org.teiid.designer.runtime.IExecutionConfigurationListener)
     */
    @Override
    public boolean removeListener( IExecutionConfigurationListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * @param server the server being removed (never <code>null</code>)
     * @return a status indicating if the specified segetUrlrver was removed from the registry (never <code>null</code>)
     */
    public IStatus removeServer( Server server ) {
        CoreArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
        return internalRemoveServer(server, true);
    }

    /**
     * @return a status indicating if the previous session state was restored successfully
     */
    public IStatus restoreState() {
        if (this.stateLocationPath != null) {
            if (stateFileExists()) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    Document doc = docBuilder.parse(new File(getStateFileName()));
                    Node root = doc.getDocumentElement();
                    NodeList servers = root.getChildNodes();

                    for (int size = servers.getLength(), i = 0; i < size; ++i) {
                        TeiidAdminInfo teiidAdminInfo = null;
                        TeiidJdbcInfo teiidJdbcInfo = null;
                        Node serverNode = servers.item(i);

                        // server attributes (host, custom label, default server)
                        NamedNodeMap serverAttributeMap = serverNode.getAttributes();

                        if (serverAttributeMap == null) {
                            continue;
                        }

                        String host = null;
                        String customLabel = null;
                        boolean previewServer = false;

                        // host attribute
                        Node hostNode = serverAttributeMap.getNamedItem(HOST_ATTR);

                        if (hostNode != null) {
                            host = hostNode.getNodeValue();
                        }
                        
                        // custom label attribute
                        Node customLabelNode = serverAttributeMap.getNamedItem(CUSTOM_LABEL_ATTR);

                        if (customLabelNode != null) {
                            customLabel = customLabelNode.getNodeValue();
                        }

                        // default server attribute
                        Node defaultServerNode = serverAttributeMap.getNamedItem(DEFAULT_ATTR);
                        
                        if (defaultServerNode != null) {
                            previewServer = Boolean.parseBoolean(defaultServerNode.getNodeValue());
                        }
                        
                        // Check for newer XML structure where server contains child nodes (admin & jdbc elements)
                        NodeList connectionNodes = serverNode.getChildNodes();

                        if( connectionNodes.getLength() > 0 ) {
                            for (int connSize = connectionNodes.getLength(), j = 0; j < connSize; ++j) {
                                Node connNode = connectionNodes.item(j);
                                if (connNode.getNodeType() != Node.TEXT_NODE) {
                                    if (connNode.getNodeName().equalsIgnoreCase(ADMIN_TAG)) {
                                        NamedNodeMap attributeMap = connNode.getAttributes();
                                        if (attributeMap == null)
                                            continue;

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
                                        String adminPassword = ((passwordNode == null) ? null
                                                                                      : new String(Base64.decode(passwordNode.getNodeValue()),
                                                                                                   "UTF-8")); //$NON-NLS-1$
                                        Node adminSecureNode = attributeMap.getNamedItem(SECURE_ATTR);
                                        String adminSecureStr = ((adminSecureNode == null) ? Boolean.FALSE.toString()
                                                                                          : adminSecureNode.getNodeValue());

                                        teiidAdminInfo = new TeiidAdminInfo(adminPort,
                                                                            adminUsername,
                                                                            adminPassword,
                                                                            (adminPassword != null),
                                                                            Boolean.parseBoolean(adminSecureStr));
                                    } else if (connNode.getNodeName().equalsIgnoreCase(JDBC_TAG)) {
                                        NamedNodeMap attributeMap = connNode.getAttributes();
                                        if (attributeMap == null)
                                            continue;

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
                                        String jdbcPassword = ((jdbcPasswordNode == null) ? null
                                                                                         : new String(Base64.decode(jdbcPasswordNode.getNodeValue()),
                                                                                                      "UTF-8")); //$NON-NLS-1$
                                        Node jdbcSecureNode = attributeMap.getNamedItem(JDBC_SECURE_ATTR);
                                        String jdbcSecureStr = ((jdbcSecureNode == null) ? Boolean.FALSE.toString()
                                                                                        : jdbcSecureNode.getNodeValue());
                                        teiidJdbcInfo = new TeiidJdbcInfo(jdbcPort,
                                                                          jdbcUsername,
                                                                          jdbcPassword,
                                                                          (jdbcPassword != null),
                                                                          Boolean.parseBoolean(jdbcSecureStr));
                                    }
                                }
                            }
                        } else {
                            // OLD xml structure where there were only attributes for each server node
                            Node urlNode = serverAttributeMap.getNamedItem(URL_ATTR);

                            // if bad registry file just continue since error will be corrected when registry is saved
                            if (urlNode == null) {
                                continue;
                            }

                            TeiidURL jdbcURL = new TeiidURL(urlNode.getNodeValue());
                            Node userNode = serverAttributeMap.getNamedItem(USER_ATTR);
                            Node passwordNode = serverAttributeMap.getNamedItem(PASSWORD_ATTR);
                            String pswd = ((passwordNode == null) ? null : new String(Base64.decode(passwordNode.getNodeValue()),
                                                                                      "UTF-8")); //$NON-NLS-1$

                            host = jdbcURL.getHosts();
                            teiidAdminInfo = new TeiidAdminInfo(jdbcURL.getPorts(),
                                                                userNode.getNodeValue(),
                                                                pswd,
                                                                (pswd != null),
                                                                jdbcURL.isUsingSSL());

                            teiidJdbcInfo = new TeiidJdbcInfo(teiidAdminInfo.getPort(),
                                                              teiidAdminInfo.getUsername(),
                                                              null,
                                                              TeiidJdbcInfo.DEFAULT_PERSIST_PASSWORD,
                                                              TeiidJdbcInfo.DEFAULT_SECURE);
                        }

                        // add server to registry
                        Server server = new Server(host, teiidAdminInfo, teiidJdbcInfo, this);
                        server.setCustomLabel(customLabel);
                        teiidAdminInfo.setHostProvider(server);
                        teiidJdbcInfo.setHostProvider(server);
                        addServer(server);

                        if (previewServer) {
                            setDefaultServer(server);
                            server.ping();
                        }
                    }
                } catch (Exception e) {
                    return new Status(IStatus.ERROR, PLUGIN_ID,
                                      Util.getString("errorRestoringServerRegistry", getStateFileName())); //$NON-NLS-1$
                }
            }
        }

        // do nothing of there is no save location or state file does not exist
        return Status.OK_STATUS;
    }

    /**
     * @param server Sets defaultServer to the specified value. May be null.
     */
    public void setDefaultServer( Server server ) {
        boolean notify = false;
        if (server != null) {
            notify = !server.equals(this.defaultServer);
        } else {
            notify = defaultServer != null;
        }

        Server oldDefaultServer = this.defaultServer;
        this.defaultServer = server;

        if (notify) {
            notifyListeners(ExecutionConfigurationEvent.createSetDefaultServerEvent(oldDefaultServer, this.defaultServer));
        }
    }

    /**
     * Saves the {@link Server} registry to the file system, shuts down the {@link PreviewManager}, and performs any other tasks
     * needed to shutdown. Shutdown may take a bit of time so it is advised to pass in a monitor and, if needed, show the user a
     * dialog that blocks until the monitor is finished.
     * 
     * @param monitor the progress monitor (may be <code>null</code>)
     */
    public void shutdown( IProgressMonitor monitor ) throws Exception {
        // return if already being shutdown
        if ((this.state == RuntimeState.SHUTTING_DOWN) || (this.state == RuntimeState.SHUTDOWN)) return;

        try {
            this.state = RuntimeState.SHUTTING_DOWN;

            if (monitor != null) monitor.subTask(Util.getString("serverManagerSavingServerRegistryTask")); //$NON-NLS-1$

            if ((this.stateLocationPath != null) && !getServers().isEmpty()) {
                try {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = factory.newDocumentBuilder();
                    Document doc = docBuilder.newDocument();

                    // create root element
                    Element root = doc.createElement(SERVERS_TAG);
                    doc.appendChild(root);

                    for (Server server : getServers()) {
                        Element serverElement = doc.createElement(SERVER_TAG);
                        root.appendChild(serverElement);
                        
                        { // Host
                            serverElement.setAttribute(HOST_ATTR, server.getHost());
                        }
                        
                        { // CUSTOM LABEL
                            if (!StringUtilities.isEmpty(server.getCustomLabel())) {
                                serverElement.setAttribute(CUSTOM_LABEL_ATTR, server.getCustomLabel());
                            }
                        }
                        
                        { // ADMIN CONNECTION INFO
	                        Element adminElement = doc.createElement(ADMIN_TAG);
	                        serverElement.appendChild(adminElement);
	
	                        adminElement.setAttribute(PORT_ATTR, server.getTeiidAdminInfo().getPort());
	                        adminElement.setAttribute(USER_ATTR, server.getTeiidAdminInfo().getUsername());
	                        if( server.getTeiidAdminInfo().getPassword() != null && server.getTeiidAdminInfo().isPasswordBeingPersisted()) {
	                        	adminElement.setAttribute(PASSWORD_ATTR, Base64.encodeBytes(server.getTeiidAdminInfo().getPassword().getBytes()));
	                        }
	                        adminElement.setAttribute(SECURE_ATTR, Boolean.toString(server.getTeiidAdminInfo().isSecure()));
                        }
                        
                        { // JDBC CONNECTION INFO
	                        Element jdbcElement = doc.createElement(JDBC_TAG);
	                        serverElement.appendChild(jdbcElement);
	
	                        jdbcElement.setAttribute(JDBC_PORT_ATTR, server.getTeiidJdbcInfo().getPort());
	                        jdbcElement.setAttribute(JDBC_USER_ATTR, server.getTeiidJdbcInfo().getUsername());
	                        if( server.getTeiidJdbcInfo().getPassword() != null && server.getTeiidJdbcInfo().isPasswordBeingPersisted() ) {
	                        	jdbcElement.setAttribute(JDBC_PASSWORD_ATTR, Base64.encodeBytes(server.getTeiidJdbcInfo().getPassword().getBytes()));
	                        }
	                        jdbcElement.setAttribute(JDBC_SECURE_ATTR, Boolean.toString(server.getTeiidJdbcInfo().isSecure()));
                        }
                        
                        if ((getDefaultServer() != null) && (getDefaultServer().equals(server))) {
                            serverElement.setAttribute(DEFAULT_ATTR, Boolean.toString(true));
                        }
                    }

                    DOMSource source = new DOMSource(doc);
                    StreamResult resultXML = new StreamResult(new FileOutputStream(getStateFileName()));
                    TransformerFactory transFactory = TransformerFactory.newInstance();
                    Transformer transformer = transFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$ //$NON-NLS-2$ 
                    transformer.transform(source, resultXML);
                } catch (Exception e) {
                    IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
                                                Util.getString("errorSavingServerRegistry", getStateFileName())); //$NON-NLS-1$
                    Util.log(status);
                }
            } else if ((this.stateLocationPath != null) && stateFileExists()) {
                // delete current registry file since all servers have been deleted
                try {
                    new File(getStateFileName()).delete();
                } catch (Exception e) {
                    IStatus status = new Status(IStatus.ERROR, PLUGIN_ID,
                                                Util.getString("errorDeletingServerRegistryFile", getStateFileName())); //$NON-NLS-1$
                    Util.log(status);
                }
            }

            // shutdown PreviewManage
            if (this.previewManager != null) {
                ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.previewManager);

                try {
                    this.previewManager.shutdown(monitor);
                } catch (Exception e) {
                    IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("errorOnPreviewManagerShutdown"), e); //$NON-NLS-1$
                    Util.log(status);
                }
            }
        } finally {
            this.state = RuntimeState.SHUTDOWN;
        }
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
    public IStatus updateServer( Server replacedServer,
                                 Server updatedServer ) {
        CoreArgCheck.isNotNull(replacedServer, "previousServerVersion"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(updatedServer, "newServerVersion"); //$NON-NLS-1$

        IStatus status = null;

        try {
            this.serverLock.writeLock().lock();
            status = internalRemoveServer(replacedServer, false);

            if (status.isOK()) {
                status = internalAddServer(updatedServer, false);

                if (status.isOK()) {
                    // all good so notify listeners
                    notifyListeners(ExecutionConfigurationEvent.createUpdateServerEvent(replacedServer, updatedServer));
                    return status;
                }

                // unexpected problem adding new version of server to registry so add old one back
                IStatus undoRemoveServerStatus = internalAddServer(replacedServer, false);

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

}
