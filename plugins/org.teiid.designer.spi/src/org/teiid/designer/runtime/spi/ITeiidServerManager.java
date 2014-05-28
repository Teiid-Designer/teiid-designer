/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import java.io.File;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.DesignerSPIPlugin;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 *
 */
public interface ITeiidServerManager extends EventManager {

    /**
     * State of the server manager
     */
    public enum RuntimeState {
        /**
         * State when the instance is first constructed
         */
        INVALID,

        /**
         * State when the instance is fully restored and ready to be used
         */
        STARTED,

        /**
         * State when the instance is restoring server configurations
         */
        RESTORING,

        /**
         * State when the instance is in the process of shutting down
         */
        SHUTTING_DOWN,

        /**
         * State when the instance has fully shutdown
         */
        SHUTDOWN
    }

    /**
     * Default teiid instance version property id
     */
    String DEFAULT_TEIID_SERVER_VERSION_ID = "defaultTeiidServerVersion"; //$NON-NLS-1$

    /**
     * Extension Point ID
     */
    String TEIID_SERVER_MANAGER_EXTENSION_POINT_ID = DesignerSPIPlugin.PLUGIN_ID + ".teiidServerManager"; //$NON-NLS-1$

    /**
     * Extension Point Element ID
     */
    String TEIID_SERVER_MANAGER_ELEMENT_ID = "serverManager"; //$NON-NLS-1$

    /**
     * Get the implementation of the {@link ISecureStorageProvider} used by
     * this server manager.
     * 
     * @return implementation of {@link ISecureStorageProvider}
     */
    ISecureStorageProvider getSecureStorageProvider();

    /**
     * Registers the specified <code>PersistedServer</code>.
     * 
     * @param teiidServer the server being added (never <code>null</code>)
     * @return a status indicating if the server was added to the registry
     */
    IStatus addServer(ITeiidServer teiidServer);

    /**
     * @return defaultServer
     */
    ITeiidServer getDefaultServer();

    /**
     * @param id the id of the server being requested (never <code>null</code> )
     * @return the requested server or <code>null</code> if not found in the registry
     */
    ITeiidServer getServer(String id);

    /**
     * @param parentServer the parent server of the requested Teiid Instance
     * @return the requested server or <code>null</code> if not found in the registry
     */
    ITeiidServer getServer(IServer parentServer);

    /**
     * @return an unmodifiable collection of registered servers (never <code>null</code>)
     */
    Collection<ITeiidServer> getServers();

    /**
     * @return the state
     */
    RuntimeState getState();

    /**
     * @return true if manager is started
     */
    boolean isStarted();

    /**
     * Get the targeted Teiid Instance version
     *
     * @return Teiid Instance version
     */
    ITeiidServerVersion getDefaultServerVersion();

    /**
     * Is this server the default
     * 
     * @param teiidServer
     * 
     * @return true if this server is the default, false otherwise.
     */
    boolean isDefaultServer(ITeiidServer teiidServer);

    /**
     * @param teiidServer the server being tested (never <code>null</code>)
     * @return <code>true</code> if the server has been registered
     */
    boolean isRegistered(ITeiidServer teiidServer);

    /**
     * @param teiidServer the server being removed (never <code>null</code>)
     * @return a status indicating if the specified segetUrlrver was removed from the registry (never <code>null</code>)
     */
    IStatus removeServer(ITeiidServer teiidServer);

    /**
     * @param teiidServer Sets defaultServer to the specified value. May be null.
     */
    void setDefaultServer(ITeiidServer teiidServer);

    /**
     * Load Teiid instances from the given file
     *
     * @param serverFile
     * @return the default teiid instance if one has been set in the file
     * @throws Exception
     */
    public ITeiidServer loadServers(File serverFile) throws Exception;

    /**
     * Try and restore the manager's prior state
     */
    void restoreState();

    /**
     * Save the state of the manager to the given file
     *
     * @param stateFilePath
     */
    void saveState(String stateFilePath);

    /**
     * Saves the {@link ITeiidServer} registry to the file system and performs any other tasks
     * needed to shutdown. Shutdown may take a bit of time so it is advised to pass in a monitor
     * and, if needed, show the user a dialog that blocks until the monitor is finished.
     * 
     * @param monitor the progress monitor (may be <code>null</code>)
     * @throws Exception 
     */
    void shutdown( IProgressMonitor monitor ) throws Exception;

    /**
     * Add a listener to be notified in the event the default teiid instance
     * version is changed
     * 
     * @param listener
     */
    void addTeiidServerVersionListener(ITeiidServerVersionListener listener);

    /**
     * Remove a listener no longer interested in listening
     * to changes is server version
     * 
     * @param listener
     */
    void removeTeiidServerVersionListener(ITeiidServerVersionListener listener);

}
