/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
/**
 * @since 8.0
 *
 */
public interface ITeiidServer extends IExecutionAdmin, HostProvider {

    /**
     * @return the version information of this server
     */
    ITeiidServerVersion getServerVersion();

    /**
     * Disconnect then connect to this server. This is preferable to 
     * calling {@link #disconnect()} and {@link #connect()} separately
     * since it only notifies at the end of the reconnection.
     */
    void reconnect();
    
    /**
     * @return TeiidAdminInfo
     */
    ITeiidAdminInfo getTeiidAdminInfo();

    /**
     * @return TeiidJdbcInfo
     */
    ITeiidJdbcInfo getTeiidJdbcInfo();
    
    /**
     * An appropriate name for this Teiid Instance
     * 
     * @return {@link #getCustomLabel()} if available otherwise {@link #getUrl()}
     */
    String getDisplayName();
    
    /**
     * @return object managing notifications for this server
     */
    EventManager getEventManager();

    /**
     * @return the host URL (never <code>null</code>)
     */
    String getUrl();

    /**
     * @return the unique identifier of this server
     */
    String getId();

    /**
     * @return the parentServer
     */
    IServer getParent();

    /**
     * Get the parent server name
     * @return the parent serverName
     */
    String getParentName();

    /**
     * @return <code>true</code> if a connection to this server exists and is working
     */
    boolean isConnected();

    /**
     * Return whether parent server is connected.
     * 
     * @return true is started, otherwise false
     */
    boolean isParentConnected();

    /**
     * Attempts to establish communication with the server.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    IStatus ping();
    
    /**
     * Notify clients of a refresh
     */
    void notifyRefresh();

    /**
     * @return the custom label or <code>null</code> if not being used
     */
    String getCustomLabel();
    
    /**
     * @return the connection error message if the connection to the server failed
     */
    String getConnectionError();
    
    /**
     * @param customLabel the new custom label or <code>null</code> or empty if the custom label is not being used
     */
    void setCustomLabel(String customLabel);

    /**
     * Attempts to establish communication with the specified server for testing purposes only.
     * 
     * This results in the connection being closed.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    IStatus testPing();

    /**
     * Test the jdbc connection
     * 
     * @param host
     * @param port
     * @param username
     * @param password
     * 
     * @return status as to the ping's success
     */
    IStatus testJDBCPing(String host,
                                         String port,
                                         String username,
                                         String password);
    
    /**
     * Construct a vdb data source
     * 
     * @param vdbName
     * @param displayName
     * @param jndiName
     * 
     * @return IStatus as to whether it succeeded
     */
    IStatus createVdbDataSource(String vdbName, String displayName, String jndiName);

    /**
     * Update this server with the properties of the given server
     * 
     * @param otherServer
     */
    void update(ITeiidServer otherServer);

    /**
     * @return the request execution timeout value for the parent server
     */
    int getParentRequestTimeout();
}
