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
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.AdminFactory;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.HashCodeUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterUtil;
import org.teiid.jdbc.TeiidDriver;


/**
 *
 *
 * @since 8.0
 */
public class TeiidServer implements HostProvider {

    // ===========================================================================================================================
    // Class Methods
    // ===========================================================================================================================

    /**
     * @param thisObj an object being compared (may be <code>null</code>)
     * @param thatObj the other object being compared (may be <code>null</code>)
     * @return <code>true</code> if both objects are <code>null</code> or both are not <code>null</code> and equal
     */
    private static boolean equivalent( Object thisObj,
                                       Object thatObj ) {
        // true if both objects are null
        if (thisObj == null) {
            return (thatObj == null);
        }

        if (thatObj == null) return false;
        return thisObj.equals(thatObj);
    }

    // ===========================================================================================================================
    // Fields
    // ===========================================================================================================================

    protected ExecutionAdmin admin;

    /**
     * The object that will fire the events.
     */
    protected final EventManager eventManager;

    private ExecutionManager executionManager;

    /**
     * The Teiid JDBC connection info object
     */
    private TeiidJdbcInfo teiidJdbcInfo;

    /**
     * The Teiid Admin connection info object
     */
    private TeiidAdminInfo teiidAdminInfo;

    private String connectionError;

    /**
     * An optional property that can be used for display purposes. May be <code>null</code>.
     */
    private String customLabel;

    /**
     * The host this server connects to (never empty or <code>null</code>).
     */
    private String host;

    /**
     * The parent {@link IServer} of this teiid server
     */
    private IServer parentServer;

    private IServerListener serverListener = new IServerListener() {

        @Override
        public void serverChanged(ServerEvent event) {
            if (event == null)
                return;
            
            int eventKind = event.getKind();
            if ((eventKind & ServerEvent.SERVER_CHANGE) == 0)
                return;
            
            // server change event
            if ((eventKind & ServerEvent.STATE_CHANGE) == 0)
                return;
            
            int state = event.getState();
            
            if (state == IServer.STATE_STOPPING || state == IServer.STATE_STOPPED) {
                disconnect();
                notifyRefresh();
            } else if (state == IServer.STATE_STARTED && TeiidServerAdapterUtil.isJBossServerConnected(event.getServer())) {
                try {
                    getAdmin();
                } catch (Exception ex) {
                    Util.log(ex);
                } finally {
                    notifyRefresh();
                }
            }
        }
    };

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================

    /**
     * Constructs on new <code>Server</code>.
     * 
     * @param host the server host (<code>null</code> or empty if default host should be used)
     * @param adminInfo the server admin connection properties (never <code>null</code>)
     * @param jdbcInfo the server JDBC connection properties (never <code>null</code>)
     * @param eventManager the event manager (never <code>null</code>)
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public TeiidServer( String host,
                   TeiidAdminInfo adminInfo,
                   TeiidJdbcInfo jdbcInfo,
                   EventManager eventManager ) {
        CoreArgCheck.isNotNull(adminInfo, "adminInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jdbcInfo, "jdbcInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(eventManager, "eventManager"); //$NON-NLS-1$

        this.host = host;
        
        this.teiidAdminInfo = adminInfo;
        this.teiidAdminInfo.setHostProvider(this);
        
        this.teiidJdbcInfo = jdbcInfo;
        this.teiidJdbcInfo.setHostProvider(this);
        
        this.eventManager = eventManager;
        
        initParent();
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    private void initParent() {
        IServer[] servers = ServerCore.getServers();
        for (IServer server : servers) {
            if (! getHost().equals(server.getHost()))
                continue;
            
            JBoss7Server jb7 = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
            if (jb7 == null)
                continue;
            
            if (getTeiidAdminInfo().getPortNumber() != jb7.getManagementPort())
                continue;
            
            // The host and admin port match so must be the same server
            parentServer = server;
            parentServer.addServerListener(serverListener);
            
            return;
        }
        
        DqpPlugin.Util.log(IStatus.WARNING, DqpPlugin.Util.getString("parentServerFailureMessage", this)); //$NON-NLS-1$
    }
    
    /**
     * Perform cleanup
     */
    public void close() {
        if (this.admin != null) {
            Admin adminApi = this.admin.getAdminApi();
            if (adminApi != null) {
                adminApi.close();
            }
            this.admin = null;
        }
        // System.out.println(" >>>> Server.close() CLOSED  Server = " + getUrl());
    }

    /**
     * Basically closes the connection and admin and nulls out the admin reference so next call to connect will
     * reconstruct the Teiid connection from scratch.
     */
    public void disconnect() {
        close();

        if (this.admin != null) {
            this.admin.disconnect();
            this.admin = null;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object obj ) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        TeiidServer otherServer = (TeiidServer) obj;

        if (!getTeiidAdminInfo().equals(otherServer.getTeiidAdminInfo())) {
            return false;
        }

        if (!getTeiidJdbcInfo().equals(otherServer.getTeiidJdbcInfo())) {
            return false;
        }

        return equivalent(getHost(), otherServer.getHost()) && equivalent(getCustomLabel(), otherServer.getCustomLabel())
               && getTeiidAdminInfo().equals(otherServer.getTeiidAdminInfo())
               && getTeiidJdbcInfo().equals(otherServer.getTeiidJdbcInfo());
    }

    public ExecutionAdmin getAdmin() throws Exception {
        if (this.admin == null) {
            char[] pwd = null;
            if (getTeiidAdminInfo().getPassword() != null) {
                pwd = getTeiidAdminInfo().getPassword().toCharArray();
            }
            this.admin = new ExecutionAdmin(AdminFactory.getInstance().createAdmin(getHost(), getTeiidAdminInfo().getPortNumber(), getTeiidAdminInfo().getUsername(), pwd),
                                            this,
                                            this.eventManager);
            this.admin.load();
        }

        return this.admin;
    }

    public TeiidAdminInfo getTeiidAdminInfo() {
        return teiidAdminInfo;
    }

    public TeiidJdbcInfo getTeiidJdbcInfo() {
        return teiidJdbcInfo;
    }

    /**
     * @return the host URL (never <code>null</code>)
     */
    public String getUrl() {
        return getTeiidAdminInfo().getUrl();
    }

    /**
     * @return the custom label or <code>null</code> if not being used
     */
    public String getCustomLabel() {
        return this.customLabel;
    }

    public ExecutionManager getExecutionManager() {
        if (this.executionManager == null) {
            this.executionManager = new ExecutionManager();
        }

        return this.executionManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.runtime.HostProvider#getHost()
     */
    @Override
    public String getHost() {
        if (this.host == null) {
            return HostProvider.DEFAULT_HOST;
        }

        return this.host;
    }
    
    /**
     * @return the parentServer
     */
    public IServer getParent() {
        return this.parentServer;
    }


    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(0, getHost(), getCustomLabel(), getTeiidAdminInfo(), getTeiidJdbcInfo());
    }

    /**
     * A server has the same identifying properties if their URL and user matches.
     * 
     * @param otherServer the server whose key is being compared (never <code>null</code>)
     * @return <code>true</code> if the servers have the same key
     * @throws IllegalArgumentException if the argument is <code>null</code>
     */
    public boolean hasSameKey( TeiidServer otherServer ) {
        CoreArgCheck.isNotNull(otherServer, "otherServer"); //$NON-NLS-1$
        return (equivalent(getUrl(), otherServer.getUrl()) && equivalent(getTeiidAdminInfo().getUsername(),
                                                                         otherServer.getTeiidAdminInfo().getUsername()));
    }

    /**
     * @return <code>true</code> if a connection to this server exists and is working
     */
    public boolean isConnected() {
        if (this.admin == null) {
            return false;
        }
        return ping().isOK();
    }

    /**
     * Attempts to establish communication with the specified server.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    public IStatus ping() {
        try {
            getAdmin().getAdminApi().getSessions();
        } catch (Exception e) {
            String msg = Util.getString("cannotConnectToServer", getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }

        return Status.OK_STATUS;
    }

    public void notifyRefresh() {
        if (this.admin != null) {
            this.admin.getEventManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
        } else {
            DqpPlugin.getInstance().getServerManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
        }
    }

    public String getConnectionError() {
        return connectionError;
    }

    public void setConnectionError( String connectionError ) {
        this.connectionError = connectionError;
    }

    /**
     * @param customLabel the new custom label or <code>null</code> or empty if the custom label is not being used
     */
    public void setCustomLabel( String customLabel ) {
        this.customLabel = StringUtilities.isEmpty(customLabel) ? null : customLabel;
    }

    /**
     * @param host the new host value (<code>null</code> if default host should be used)
     */
    public void setHost( String host ) {
        this.host = host;
    }

    public void setTeiidAdminInfo( TeiidAdminInfo adminInfo ) {
        this.teiidAdminInfo = adminInfo;
    }

    public void setTeiidJdbcInfo( TeiidJdbcInfo jdbcInfo ) {
        this.teiidJdbcInfo = jdbcInfo;
    }

    /**
     * Attempts to establish communication with the specified server for testing purposes only.
     * 
     * This results in the connection being closed.
     * 
     * @return a status if the server connection can be established (never <code>null</code>)
     */
    public IStatus testPing() {
        try {
            Admin adminApi = getAdmin().getAdminApi();
            adminApi.close();
            this.admin = null;
        } catch (Exception e) {
            String msg = Util.getString("cannotConnectToServer", this); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
        }

        return Status.OK_STATUS;
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
    public IStatus testJDBCPing(String host, String port, String username, String password) {
    	Connection teiidJdbcConnection = null;
    	String url = "jdbc:teiid:ping@mm://"+host+':'+port; //$NON-NLS-1$
        
		try {
			Admin adminApi = getAdmin().getAdminApi();
			adminApi.deploy("ping-vdb.xml", new ByteArrayInputStream(TeiidServerUtils.TEST_VDB.getBytes())); //$NON-NLS-1$
			
			try{
				String urlAndCredentials = url + ";user="+username+";password="+password+';';  //$NON-NLS-1$ //$NON-NLS-2$				
				teiidJdbcConnection = TeiidDriver.getInstance().connect(urlAndCredentials, null);
			   //pass
			} catch(SQLException ex){
				String msg = Util.getString("serverDeployUndeployProblemPingingTeiidJdbc", url); //$NON-NLS-1$
	            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
			} finally {
				adminApi.undeploy("ping"); //$NON-NLS-1$
				
				if( teiidJdbcConnection != null ) {
					teiidJdbcConnection.close();
				}
		        adminApi.close();
		        this.admin = null;
			}
		} catch (AdminException ex) {
			String msg = Util.getString("serverDeployUndeployProblemPingingTeiidJdbc", url); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
		} catch (Exception ex) {
			String msg = Util.getString("serverDeployUndeployProblemPingingTeiidJdbc", url); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
		}
		
		return Status.OK_STATUS;
    }
    
    public String getVdbDataSourceConnectionUrl(String vdbName) {
    	String host = this.teiidAdminInfo.getHostProvider().getHost();
		String port = this.teiidAdminInfo.getPort();
		return "jdbc:teiid:" + vdbName + "@mm://"+host+':'+port;  //$NON-NLS-1$ //$NON-NLS-2$
    }
    
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
			getAdmin().getOrCreateDataSource(displayName, jndiName, "connector-jdbc", props); //$NON-NLS-1$
		} catch (Exception ex) {
			String msg = "Error creating data source for VDB " + vdbName; //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, ex);
		}
		
		return Status.OK_STATUS;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String txt = Util.getString("serverToStringWithNoCustomLabel", getUrl(), getTeiidAdminInfo().getUsername()); //$NON-NLS-1$
        
        if (this.customLabel != null) {
            txt = Util.getString("serverToStringWithCustomLabel", this.customLabel, txt); //$NON-NLS-1$
        }

        return txt;
    }
}