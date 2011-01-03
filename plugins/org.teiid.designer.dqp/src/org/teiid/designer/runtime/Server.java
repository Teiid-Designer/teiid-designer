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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminFactory;
import org.teiid.core.util.HashCodeUtil;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.modeler.dqp.DqpPlugin;

/**
 *
 */
public class Server {

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

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================
    
	/**
     * Constructs on new <code>Server</code>.
     * 
     * @param url the server URL (never <code>null</code>)
     * @param user the server user (may be <code>null</code>)
     * @param password the server password (may be <code>null</code>)
     * @throws IllegalArgumentException if the URL or user arguments are <code>null</code>
     */
    public Server( TeiidAdminInfo adminInfo,
                   TeiidJdbcInfo jdbcInfo,
                   EventManager eventManager ) {
        CoreArgCheck.isNotNull(adminInfo, "adminInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jdbcInfo, "jdbcInfo"); //$NON-NLS-1$

        this.eventManager = eventManager;
        
        this.teiidAdminInfo = adminInfo;
        this.teiidJdbcInfo = jdbcInfo;
    }

    // ===========================================================================================================================
    // Methods
    // ===========================================================================================================================

    /**
     * Perform cleanup
     */
    public void close() {
    	if( this.admin != null ) {
        	Admin adminApi = this.admin.getAdminApi();
        	if( adminApi != null) {
        		adminApi.close();
        	}
        	this.admin = null;
    	}
    	//System.out.println(" >>>> Server.close() CLOSED  Server = " + getUrl());
    }
    
    /**
     * Basically closes the connection and admin and nulls out the admin reference so next call to connect will
     * reconstruct the Teiid connection from scratch.
     */
    public void disconnect() {
    	close();
    	
    	if( this.admin != null ) {
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
        if (this == obj) return true;
        if ((obj == null) || (getClass() != obj.getClass())) return false;

        Server otherServer = (Server)obj;
        return equivalent(getTeiidAdminInfo().getURL(), otherServer.getTeiidAdminInfo().getURL()) && equivalent(getTeiidAdminInfo().getUsername(), otherServer.getTeiidAdminInfo().getUsername())
               && equivalent(getTeiidAdminInfo().getPassword(), otherServer.getTeiidAdminInfo().getPassword()) && (isPasswordBeingPersisted() == otherServer.isPasswordBeingPersisted());
    }

    public ExecutionAdmin getAdmin() throws Exception {
        if (this.admin == null) {
            char[] pwd = null;
            if (getTeiidAdminInfo().getPassword() != null) {
                pwd = getTeiidAdminInfo().getPassword().toCharArray();
            }
            this.admin = new ExecutionAdmin(AdminFactory.getInstance().createAdmin(getTeiidAdminInfo().getUsername(), pwd, getTeiidAdminInfo().getURL()), this,
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

    public ExecutionManager getExecutionManager() {
        if (this.executionManager == null) {
            this.executionManager = new ExecutionManager();
        }

        return this.executionManager;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(0, getTeiidAdminInfo().getURL(), getTeiidAdminInfo().getUsername(), getTeiidAdminInfo().getPassword());
    }

    /**
     * A server has the same identifying properties if their URL and user matches.
     * 
     * @param otherServer the server whose key is being compared (never <code>null</code>)
     * @return <code>true</code> if the servers have the same key
     * @throws IllegalArgumentException if the argument is <code>null</code>
     */
    public boolean hasSameKey( Server otherServer ) {
        CoreArgCheck.isNotNull(otherServer, "otherServer"); //$NON-NLS-1$
        return (equivalent(getTeiidAdminInfo().getURL(), otherServer.getTeiidAdminInfo().getURL()) && equivalent(getTeiidAdminInfo().getUsername(), otherServer.getTeiidAdminInfo().getUsername()));
    }

    /**
     * @return <code>true</code> if a connection to this server exists and is working
     */
    public boolean isConnected() {
    	if( this.admin == null ) {
    		return false;
    	}
        return ping().isOK();
    }

    /**
     * @return persistPassword <code>true</code> if the password is being persisted
     */
    public boolean isPasswordBeingPersisted() {
        return this.teiidAdminInfo.isPasswordBeingPersisted();
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
    	if( this.admin != null ) {
    		this.admin.getEventManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
    	} else {
    		DqpPlugin.getInstance().getServerManager().notifyListeners(ExecutionConfigurationEvent.createServerRefreshEvent(this));
    	}
    }
  
	public String getConnectionError() {
		return connectionError;
	}

	public void setConnectionError(String connectionError) {
		this.connectionError = connectionError;
	}

	public void setTeiidAdminInfo(TeiidAdminInfo adminInfo) {
		this.teiidAdminInfo = adminInfo;
	}
    
	public void setTeiidJdbcInfo(TeiidJdbcInfo jdbcInfo) {
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
            String msg = Util.getString("cannotConnectToServer", getTeiidAdminInfo().getURL()); //$NON-NLS-1$
            return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
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
        return getTeiidAdminInfo().getURL() + "::" + getTeiidAdminInfo().getUsername(); //$NON-NLS-1$
    }

}