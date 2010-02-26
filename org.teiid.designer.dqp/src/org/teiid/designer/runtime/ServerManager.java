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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.metamatrix.core.modeler.util.ArgCheck;
import com.metamatrix.core.util.Base64;

/**
 * The <code>ServerManager</code> class manages the creation, deletion, and editing of servers hosting Teiid servers.
 */
public final class ServerManager implements EventManager {

    // ===========================================================================================================================
    // Constants
    // ===========================================================================================================================

    /**
     * The tag used to persist a server's login password.
     */
    private static final String PASSWORD_TAG = "password"; //$NON-NLS-1$

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
     * The tag used to persist a server's URL.
     */
    private static final String URL_TAG = "url"; //$NON-NLS-1$

    /**
     * The tag used to persist a server's login user.
     */
    private static final String USER_TAG = "user"; //$NON-NLS-1$

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
    private final List<Server> servers;

    /**
     * Lock used for when accessing the server registry.
     */
    private final ReadWriteLock serverLock = new ReentrantReadWriteLock();

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
        ArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.addIfAbsent(listener);
    }

    /**
     * Registers the specified <code>PersistedServer</code>.
     * 
     * @param server the server being added (never <code>null</code>)
     * @return a status indicating if the server was added to the registry
     */
    public IStatus addServer( Server server ) {
        ArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
        return internalAddServer(server, true);
    }

    /**
     * @param url the URL of the server being requested (never <code>null</code> )
     * @return the requested server or <code>null</code> if not found in the registry
     */
    public Server getServer( String url ) {
        ArgCheck.isNotNull(url, "url"); //$NON-NLS-1$

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

        try {
            this.serverLock.writeLock().lock();

            if (!isRegistered(server)) {
                added = this.servers.add(server);
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        if (added) {
            if (notifyListeners) {
                notifyListeners(ExecutionConfigurationEvent.createAddServerEvent(server));
            }

            return Status.OK_STATUS;
        }

        // server already exists
        return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("serverExistsMsg.text(server.getShortDescription()"));
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
        return new Status(IStatus.ERROR, PLUGIN_ID,
                          Util.getString("serverManagerRegistryRemoveUnexpectedError.text(server.getShortDescription())"));
    }

    /**
     * @param server the server being tested (never <code>null</code>)
     * @return <code>true</code> if the server has been registered
     */
    public boolean isRegistered( Server server ) {
        ArgCheck.isNotNull(server, "server"); //$NON-NLS-1$

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
                Util.log(IStatus.WARNING, e, Util.getString("unexpectedErrorInExecutionConfigurationListener")); // TODO i18n this
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
        ArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.listeners.remove(listener);
    }

    /**
     * @param server the server being removed (never <code>null</code>)
     * @return a status indicating if the specified server was removed from the registry (never <code>null</code>)
     */
    public IStatus removeServer( Server server ) {
        ArgCheck.isNotNull(server, "server"); //$NON-NLS-1$
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
                        Node server = servers.item(i);

                        if (server.getNodeType() != Node.TEXT_NODE) {
                            NamedNodeMap attributeMap = server.getAttributes();

                            if (attributeMap == null) continue;

                            Node urlNode = attributeMap.getNamedItem(URL_TAG);
                            Node userNode = attributeMap.getNamedItem(USER_TAG);
                            Node passwordNode = attributeMap.getNamedItem(PASSWORD_TAG);
                            String pswd = ((passwordNode == null) ? null : new String(Base64.decode(passwordNode.getNodeValue()),
                                                                                      "UTF-8")); //$NON-NLS-1$

                            // add server to registry
                            addServer(new Server(urlNode.getNodeValue(), userNode.getNodeValue(), pswd, (pswd != null), this));
                        }
                    }
                } catch (Exception e) {
                    return new Status(IStatus.ERROR, PLUGIN_ID,
                                      Util.getString("errorRestoringServerRegistry.text(getStateFileName())"));
                }
            }
        }

        // do nothing of there is no save location or state file does not exist
        return Status.OK_STATUS;
    }

    /**
     * Saves the {@link Server} registry to the file system.
     * 
     * @return a status indicating if the registry was successfully saved
     */
    public IStatus saveState() {
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

                    serverElement.setAttribute(URL_TAG, server.getUrl());
                    serverElement.setAttribute(USER_TAG, server.getUser());
                    serverElement.setAttribute(PASSWORD_TAG, Base64.encodeBytes(server.getPassword().getBytes()));
                }

                DOMSource source = new DOMSource(doc);
                StreamResult resultXML = new StreamResult(new FileOutputStream(getStateFileName()));
                TransformerFactory transFactory = TransformerFactory.newInstance();
                Transformer transformer = transFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2"); //$NON-NLS-1$
                transformer.transform(source, resultXML);
            } catch (Exception e) {
                return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("errorSavingServerRegistry.text(getStateFileName())"));
            }
        } else if ((this.stateLocationPath != null) && stateFileExists()) {
            // delete current registry file since all servers have been deleted
            try {
                new File(getStateFileName()).delete();
            } catch (Exception e) {
                return new Status(IStatus.ERROR, PLUGIN_ID,
                                  Util.getString("errorDeletingServerRegistryFile.text(getStateFileName())"));
            }
        }

        return Status.OK_STATUS;
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
        ArgCheck.isNotNull(replacedServer, "previousServerVersion"); //$NON-NLS-1$
        ArgCheck.isNotNull(updatedServer, "newServerVersion"); //$NON-NLS-1$

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

                // unexpected problem adding new version of server to registry
                // TODO add previousServerVerson back into registry???
                return new Status(IStatus.ERROR, PLUGIN_ID,
                                  Util.getString("serverManagerRegistryUpdateAddError.text(status.getMessage())"));
            }
        } finally {
            this.serverLock.writeLock().unlock();
        }

        // unexpected problem removing server from registry
        return new Status(IStatus.ERROR, PLUGIN_ID,
                          Util.getString("serverManagerRegistryUpdateRemoveError.text(status.getMessage())"));
    }

}
