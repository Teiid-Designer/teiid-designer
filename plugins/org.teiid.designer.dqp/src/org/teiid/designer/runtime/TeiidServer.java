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
import java.io.InputStream;
import java.sql.Driver;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterFactory;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.ExecutionConfigurationEvent;
import org.teiid.designer.runtime.spi.HostProvider;
import org.teiid.designer.runtime.spi.IExecutionAdmin;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidDataSource;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidTranslator;
import org.teiid.designer.runtime.spi.ITeiidVdb;
import org.teiid.designer.runtime.spi.TeiidPropertyDefinition;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion;


/**
 *
 *
 * @since 8.0
 */
public class TeiidServer implements ITeiidServer {

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    private ITeiidServerVersion serverVersion;

    protected IExecutionAdmin admin;

    /**
     * The object that will fire the events.
     */
    protected final EventManager eventManager;

    /**
     * The Teiid JDBC connection info object
     */
    private ITeiidJdbcInfo teiidJdbcInfo;

    /**
     * The Teiid Admin connection info object
     */
    private ITeiidAdminInfo teiidAdminInfo;

    private String connectionError;

    /**
     * An optional property that can be used for display purposes. May be <code>null</code>.
     */
    private String customLabel;

    /**
     * The unique id of this server
     */
    private final String id;

    /**
     * The parent {@link IServer} of this Teiid Instance
     * (never empty or <code>null</code>).
     */
    private final IServer parentServer;

    /**
     * Adapter factory providing adaption and teiid utilities
     */
    private TeiidServerAdapterFactory serverAdapterFactory;

    /**
     * Flag showing when server is in process of connecting
     */
    private boolean connecting = false;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================
    
    /**
     * Constructs on new <code>Server</code>.
     * 
     * @param serverVersion the version of the server
     * @param adminInfo the server admin connection properties (never <code>null</code>)
     * @param jdbcInfo the server JDBC connection properties (never <code>null</code>)
     * @param eventManager the event manager (never <code>null</code>)
     * @param parentServer the parent {@link IServer} (never <code>null</code>)
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    TeiidServer( ITeiidServerVersion serverVersion,
                   ITeiidAdminInfo adminInfo,
                   ITeiidJdbcInfo jdbcInfo,
                   EventManager eventManager,
                   IServer parentServer) {
        CoreArgCheck.isNotNull(serverVersion, "serverVersion"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(adminInfo, "adminInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jdbcInfo, "jdbcInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(eventManager, "eventManager"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(parentServer, "parentServer"); //$NON-NLS-1$
        CoreArgCheck.isTrue(! parentServer.getClass().getSimpleName().equals("ServerWorkingCopy"), "TeiidServer parent should not be a working copy");  //$NON-NLS-1$//$NON-NLS-2$
        
        this.serverVersion = serverVersion;
        this.eventManager = eventManager;
        this.parentServer = parentServer;

        /*
         * All fields must be set prior to calling setHostProvider
         * on TeiidConnectionInfo sub-classes since this calls
         * setPassword which relies on all facets of getUrl to be
         * complete.
         */

        this.teiidAdminInfo = adminInfo;
        this.teiidAdminInfo.setHostProvider(this);
        
        this.teiidJdbcInfo = jdbcInfo;
        this.teiidJdbcInfo.setHostProvider(this);

        this.id = getUrl() + "-" + getServerVersion() + "-" + getParent().getId();  //$NON-NLS-1$//$NON-NLS-2$
        
        if (parentServer.getServerState() != IServer.STATE_STARTED)
            disconnect();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    protected void setServerVersion(ITeiidServerVersion teiidVersion) {
        if (teiidVersion == null)
            return;

        this.serverVersion = teiidVersion;
    }

    @Override
    public ITeiidServerVersion getServerVersion() {
        return serverVersion;
    }
    
    @Override
    public void disconnect() {
        if (this.admin != null) {
            this.admin.disconnect();
            this.admin = null;
        }
        
        notifyRefresh();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        TeiidServer other = (TeiidServer)obj;
        if (this.id == null) {
            if (other.id != null) return false;
        } else if (!this.id.equals(other.id)) return false;
        if (this.parentServer == null) {
            if (other.parentServer != null) return false;
        } else if (!this.parentServer.equals(other.parentServer)) return false;
        if (this.teiidAdminInfo == null) {
            if (other.teiidAdminInfo != null) return false;
        } else if (!this.teiidAdminInfo.equals(other.teiidAdminInfo)) return false;
        if (this.teiidJdbcInfo == null) {
            if (other.teiidJdbcInfo != null) return false;
        } else if (!this.teiidJdbcInfo.equals(other.teiidJdbcInfo)) return false;
        return true;
    }

    @Override
    public void connect() throws Exception {
        if (! isParentConnected()) {
            throw new Exception(DqpPlugin.Util.getString("jbossServerNotStartedMessage")); //$NON-NLS-1$
        }

        if (this.admin == null) {
            /*
             * The version should be determined prior to getting an admin
             * instance and 'connect' (which is in fact nothing but a refresh),
             * since both stash the version and use it during init and refresh
             * of translators etc...
             */
            try {
                setServerVersion(getAdapterFactory().getTeiidRuntimeVersion(parentServer));
            } catch (Exception ex) {
                DqpPlugin.Util.log(ex);
            }

            try {
                /*
                 * By the time this has been called the teiid version should be correct
                 * for the given server hence use of the version should not produce
                 * any results against an incorrect version.
                 */
                this.admin = TeiidRuntimeRegistry.getInstance().getExecutionAdmin(this);

                if (admin != null) {
                    /*
                     * Avoid the refresh listener being fired prematurely by the admin client.
                     * Want to fire the refresh ourselves using {#notifyRefresh} at the end
                     * of this function.
                     */
                    getEventManager().permitListeners(false);

                    this.admin.connect();
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                getEventManager().permitListeners(true);
                this.connecting = false;
            }

            getEventManager().notifyListeners(ExecutionConfigurationEvent.createServerConnectedEvent(this));
            notifyRefresh();
        }
    }

    /**
     * @return server adapter factory
     */
    private TeiidServerAdapterFactory getAdapterFactory() {
        if (serverAdapterFactory == null)
            serverAdapterFactory = new TeiidServerAdapterFactory();

        return serverAdapterFactory;
    }

    @Override
    public void reconnect() {
        try {
            // Call disconnect() first to clear out Server & admin caches
            getEventManager().permitListeners(false);
            try {
                disconnect();
            } catch (Exception ex) {
                throw ex;
            } finally {
                getEventManager().permitListeners(true);
            }
            
            if (isParentConnected()) {
                // Refresh is implied in the getting of the admin object since it will
                // automatically load and refresh.
                connect();
            } else {
                throw new Exception(DqpPlugin.Util.getString("serverParentNotConnectedErrorMsg")); //$NON-NLS-1$
            }

            setConnectionError(null);
        } catch (IllegalArgumentException e) {
            DqpPlugin.Util.log(e);
            String msg = DqpPlugin.Util.getString("serverReconnectErrorMsg", this) + "\n" +  //$NON-NLS-1$ //$NON-NLS-2$
                                        DqpPlugin.Util.getString("serverAdminInitError"); //$NON-NLS-1$
            setConnectionError(msg);
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
            String msg = DqpPlugin.Util.getString("serverReconnectErrorMsg", this) + "\n" + e.getLocalizedMessage(); //$NON-NLS-1$ //$NON-NLS-2$
            setConnectionError(msg);
        }
    }

    @Override
    public ITeiidAdminInfo getTeiidAdminInfo() {
        return teiidAdminInfo;
    }

    @Override
    public ITeiidJdbcInfo getTeiidJdbcInfo() {
        return teiidJdbcInfo;
    }
    
    @Override
    public EventManager getEventManager() {
        return eventManager;
    }
    
    @Override
    public String getDisplayName() {
        return getCustomLabel() != null ? getCustomLabel() : getUrl();
    }

    /**
     * @return the host URL (never <code>null</code>)
     */
    @Override
    public String getUrl() {
        return getTeiidAdminInfo().getUrl();
    }

    @Override
    public String getCustomLabel() {
        return this.customLabel;
    }

    @Override
    public String getHost() {
        String host = this.parentServer.getHost();

        if (host == null) {
            host = HostProvider.DEFAULT_HOST;
        }

        return host;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * @return the parentServer
     */
    @Override
    public IServer getParent() {
        return this.parentServer;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.admin == null) ? 0 : this.admin.hashCode());
        result = prime * result + ((this.eventManager == null) ? 0 : this.eventManager.hashCode());
        result = prime * result + ((this.parentServer == null) ? 0 : this.parentServer.hashCode());
        result = prime * result + ((this.teiidAdminInfo == null) ? 0 : this.teiidAdminInfo.hashCode());
        result = prime * result + ((this.teiidJdbcInfo == null) ? 0 : this.teiidJdbcInfo.hashCode());
        return result;
    }

    @Override
    public boolean isConnecting() {
        return connecting ;
    }

    @Override
    public void startConnecting() {
        this.connecting = true;
        getEventManager().notifyListeners(ExecutionConfigurationEvent.createServerConnectingEvent(this));
    }

    /**
     * @return <code>true</code> if a connection to this server exists and is working
     */
    @Override
    public boolean isConnected() {
        if (! isParentConnected() || this.admin == null) {
            return false;
        }
        return ping().isOK();
    }

    /**
     * Return whether parent server is connected.
     * 
     * @return true is started, otherwise false
     */
    @Override
    public boolean isParentConnected() {
        if(this.parentServer == null || this.parentServer.getServerState() != IServer.STATE_STARTED)
            return false;
        
        try {
            return getAdapterFactory().isParentServerConnected(parentServer);
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            return false;
        }
    }
    
    /**
     * @return <code>true</code> if a server is connected and loaded
     */
    @Override
    public boolean isRefreshing() {
	    if( isConnected() ) {
	        try {
	        	return this.admin.isRefreshing();
	        } catch (Exception ex) {
	            DqpPlugin.Util.log(ex);
	            return false;
	        }
	    	
	    }
	    
	    return false;
    }
    
    /**
     * Attempts to establish communication with the specified server.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    @Override
    public IStatus ping() {
        String msg = Util.getString("cannotConnectToServer", getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
        
        try {
            if (! isParentConnected() || this.admin == null)
                throw new Exception(msg);
            
            admin.ping(PingType.ADMIN);
        } catch (Exception e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }

        return Status.OK_STATUS;
    }

    @Override
    public void notifyRefresh() {
        if (this.admin != null) {
            getEventManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
        } else {
            DqpPlugin.getInstance().getServerManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
        }
    }

    @Override
    public String getConnectionError() {
        return connectionError;
    }

    private void setConnectionError( String connectionError ) {
        this.connectionError = connectionError;
    }

    /**
     * @param customLabel the new custom label or <code>null</code> or empty if the custom label is not being used
     */
    @Override
    public void setCustomLabel( String customLabel ) {
        this.customLabel = StringUtilities.isEmpty(customLabel) ? null : customLabel;
    }

    /**
     * Attempts to establish communication with the specified server for testing purposes only.
     * 
     * This results in the connection being closed.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    @Override
    public IStatus testPing() {
        try {
            boolean testCausesConnect = false;
            
            if (admin == null) {
                connect();
                testCausesConnect = true;
            }
            
            IStatus status = ping();

            // Only disconnect if this test ping caused
            // the connect
            if (testCausesConnect) {
                disconnect();
            }

            return status;

        } catch (Exception e) {
            String msg = Util.getString("cannotConnectToServer", this); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }
    }
    
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
    @Override
    public IStatus testJDBCPing(String host, String port, String username, String password) {
        try {
            boolean testCausesConnect = false;
            
            if (admin == null) {
                connect();
                testCausesConnect = true;
            }
            
            IStatus status = admin.ping(PingType.JDBC);

            // Only disconnect if this test ping caused
            // the connect
            if (testCausesConnect) {
                disconnect();
            }

            return status;

        } catch (Exception e) {
            String msg = Util.getString("cannotConnectToServer", this); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }
    }
    
    private String getVdbDataSourceConnectionUrl(String vdbName) {
    	String host = this.teiidJdbcInfo.getHostProvider().getHost();
		String port = this.teiidJdbcInfo.getPort();
		String protocol = this.teiidJdbcInfo.isSecure() ? "@mms" : "@mm"; //$NON-NLS-1$ //$NON-NLS-2$
		return "jdbc:teiid:" + vdbName + protocol+"://"+host+":"+port;  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
    
    @Override
    public IStatus createVdbDataSource(String vdbName, String displayName, String jndiName) {
    	Properties props = new Properties();
		String username = this.teiidJdbcInfo.getUsername();
		String password = this.teiidJdbcInfo.getPassword();
		if( username != null ) {
			props.put("user-name", username); //$NON-NLS-1$
		}
		if( password != null ) {
			props.put("password", password); //$NON-NLS-1$
		}
		
		props.put("driver-class", "org.teiid.jdbc.TeiidDriver"); //$NON-NLS-1$ //$NON-NLS-2$
		props.put("connection-url", getVdbDataSourceConnectionUrl(vdbName)); //$NON-NLS-1$
    	
    	try {
			connect();
			admin.getOrCreateDataSource(displayName, jndiName, "connector-jdbc", props); //$NON-NLS-1$
		} catch (Exception ex) {
			String msg = "Error creating data source for VDB " + vdbName; //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
		}
		
		return Status.OK_STATUS;
    }

    @Override
	public String getParentName() {
    	return this.parentServer.getName();
    }

    @Override
    public int getParentRequestTimeout() {
        return getAdapterFactory().getParentRequestExecutionTimeout(parentServer);
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String txt = Util.getString("serverToStringWithNoCustomLabel", //$NON-NLS-1$
                                    parentServer.getName());

        return txt;
    }
    
    @Override
    public boolean dataSourceExists(String name) throws Exception {
        connect();
        return admin.dataSourceExists(name);
    }

    @Override
    public void deleteDataSource(String jndiName) throws Exception {
        connect();
        admin.deleteDataSource(jndiName);
    }

    @Override
    public void deployVdb(IFile vdbFile) throws Exception {
        connect();
        admin.deployVdb(vdbFile);
    }
    
    @Override
    public void deployVdb(IFile vdbFile, int version) throws Exception {
        connect();
        admin.deployVdb(vdbFile, version);
    }

    @Override
    public ITeiidDataSource getDataSource(String name) throws Exception {
        connect();
        return admin.getDataSource(name);
    }

    @Override
    public Collection<ITeiidDataSource> getDataSources() throws Exception {
        connect();
        return admin.getDataSources();
    }

    @Override
    public Set<String> getDataSourceTypeNames() throws Exception {
        connect();
        return admin.getDataSourceTypeNames();
    }

    @Override
    public ITeiidDataSource getOrCreateDataSource(String displayName,
                                                 String jndiName,
                                                 String typeName,
                                                 Properties properties) throws Exception {
        connect();
        CoreArgCheck.isNotNull(displayName, "displayName"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jndiName, "jndiName"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(typeName, "typeName"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(properties, "properties"); //$NON-NLS-1$

        for (Entry<Object, Object> entry : properties.entrySet()) {
            Object value = entry.getValue();
            String errorMsg = "No value for the connection property '" + entry.getKey() + "'"; //$NON-NLS-1$ //$NON-NLS-2$
            CoreArgCheck.isNotNull(value, errorMsg);
            CoreArgCheck.isNotEmpty(value.toString(), errorMsg);
        }

        return admin.getOrCreateDataSource(displayName, jndiName, typeName, properties);
    }

    @Override
    public ITeiidTranslator getTranslator(String name) throws Exception {
        connect();
        return admin.getTranslator(name);
    }

    @Override
    public Collection<ITeiidTranslator> getTranslators() throws Exception {
        connect();
        return admin.getTranslators();
    }

    @Override
    public Collection<ITeiidVdb> getVdbs() throws Exception {
        connect();
        return admin.getVdbs();
    }
    
    @Override
    public ITeiidVdb getVdb(String name) throws Exception {
        connect();
        return admin.getVdb(name);
    }
    
    @Override
    public boolean hasVdb(String name) throws Exception {
        connect();
        return admin.hasVdb(name);
    }

    @Override
    public boolean isVdbActive(String vdbName) throws Exception {
        connect();
        return admin.isVdbActive(vdbName);
    }

    @Override
    public boolean isVdbLoading(String vdbName) throws Exception {
        connect();
        return admin.isVdbLoading(vdbName);
    }

    @Override
    public boolean hasVdbFailed(String vdbName) throws Exception {
        connect();
        return admin.hasVdbFailed(vdbName);
    }

    @Override
    public boolean wasVdbRemoved(String vdbName) throws Exception {
        connect();
        return admin.wasVdbRemoved(vdbName);
    }
    
    @Override
    public List<String> retrieveVdbValidityErrors(String vdbName) throws Exception {
        connect();
        return admin.retrieveVdbValidityErrors(vdbName);
    }

    @Override
    public void undeployVdb(String vdbName) throws Exception {
        connect();
        admin.undeployVdb(vdbName);
    }

    @Override
    public IStatus ping(PingType pingType) throws Exception {
        connect();
        return admin.ping(pingType);
    }
    
    @Override
    public String getAdminDriverPath() throws Exception {
        connect();
        return admin.getAdminDriverPath();
    }
    
    @Override
    public Driver getTeiidDriver(String driverClass) throws Exception {
        connect();
        return admin.getTeiidDriver(driverClass);
    }
    
    @Override
    public void update(ITeiidServer otherServer) {
        CoreArgCheck.isNotNull(otherServer);

        serverVersion = new TeiidServerVersion(otherServer.getServerVersion().toString());

        getTeiidAdminInfo().setAll(otherServer.getTeiidAdminInfo());
        getTeiidJdbcInfo().setAll(otherServer.getTeiidJdbcInfo());  
    }
    
    @Override
    public void deployDynamicVdb(String vdbDeploymentName, InputStream inStream) throws Exception {
        connect();
        admin.deployDynamicVdb(vdbDeploymentName,inStream);
    }
    
    @Override
    public void deployDriver(File file) throws Exception {
        connect();
        admin.deployDriver(file);
    }

    @Override
    public void undeployDynamicVdb(String vdbName) throws Exception {
        connect();
        admin.undeployDynamicVdb(vdbName);
    }

    @Override
    public Set<String> getDataSourceTemplateNames() throws Exception {
        connect();
        return admin.getDataSourceTemplateNames();
    }
   
    @Override
    public Collection<TeiidPropertyDefinition> getTemplatePropertyDefns(String templateName) throws Exception {
        connect();
        return admin.getTemplatePropertyDefns(templateName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.spi.IExecutionAdmin#getSchema(java.lang.String, int, java.lang.String)
     */
    @Override
    public String getSchema(String vdbName,
                            int vdbVersion,
                            String modelName) throws Exception {
        connect();
        return admin.getSchema(vdbName, vdbVersion, modelName);
    }

    /* (non-Javadoc)
     * @see org.teiid.designer.runtime.spi.IExecutionAdmin#getDataSourceProperties(java.lang.String)
     */
    @Override
    public Properties getDataSourceProperties(String name) throws Exception {
        connect();
        return admin.getDataSourceProperties(name);
    }

    @Deprecated
    @Override
    public void mergeVdbs( String sourceVdbName, int sourceVdbVersion, 
                    String targetVdbName, int targetVdbVersion ) throws Exception {
        connect();
        admin.mergeVdbs(sourceVdbName, sourceVdbVersion, targetVdbName, targetVdbVersion);
    }
}
