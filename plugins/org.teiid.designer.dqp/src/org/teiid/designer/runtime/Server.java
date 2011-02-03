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
import com.metamatrix.core.util.StringUtilities;
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
    
    /**
     * An optional property that can be used for display purposes. May be <code>null</code>.
     */
    private String customLabel;

    // ===========================================================================================================================
    // Constructors
    // ===========================================================================================================================
    
	/**
     * Constructs on new <code>Server</code>.
     * 
     * @param adminInfo the server admin connection properties (never <code>null</code>)
     * @param jdbcInfo the server JDBC connection properties (never <code>null</code>)
     * @param eventManager the event manager (never <code>null</code>)
     * @throws IllegalArgumentException if any of the parameters are <code>null</code>
     */
    public Server( TeiidAdminInfo adminInfo,
                   TeiidJdbcInfo jdbcInfo,
                   EventManager eventManager ) {
        CoreArgCheck.isNotNull(adminInfo, "adminInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jdbcInfo, "jdbcInfo"); //$NON-NLS-1$
        CoreArgCheck.isNotNull(jdbcInfo, "eventManager"); //$NON-NLS-1$

        this.teiidAdminInfo = adminInfo;
        this.teiidJdbcInfo = jdbcInfo;
        this.eventManager = eventManager;
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
		return equivalent(getUrl(), otherServer.getUrl()) && equivalent(getTeiidAdminInfo().getUsername(),
																		otherServer.getTeiidAdminInfo().getUsername())
				&& equivalent(	getTeiidAdminInfo().getPassword(),
								otherServer.getTeiidAdminInfo().getPassword())
				&& (isPasswordBeingPersisted() == otherServer.isPasswordBeingPersisted() && equivalent(	this.customLabel,
																										otherServer.customLabel));
    }

    public ExecutionAdmin getAdmin() throws Exception {
        if (this.admin == null) {
            char[] pwd = null;
            if (getTeiidAdminInfo().getPassword() != null) {
                pwd = getTeiidAdminInfo().getPassword().toCharArray();
            }
            this.admin = new ExecutionAdmin(AdminFactory.getInstance().createAdmin(getTeiidAdminInfo().getUsername(), pwd, getUrl()), this,
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
        return getTeiidAdminInfo().getURL();
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(0, getUrl(), getTeiidAdminInfo().getUsername(), getTeiidAdminInfo().getPassword());
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
        return (equivalent(getUrl(), otherServer.getUrl()) && equivalent(getTeiidAdminInfo().getUsername(), otherServer.getTeiidAdminInfo().getUsername()));
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
	
	/**
	 * @param customLabel the new custom label or <code>null</code> or empty if the custom label is not being used
	 */
    public void setCustomLabel( String customLabel ) {
        this.customLabel = StringUtilities.isEmpty(customLabel) ? null : customLabel;
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
            String msg = Util.getString("cannotConnectToServer", this); //$NON-NLS-1$
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
		StringBuilder txt = new StringBuilder();

		// add URL
		txt.append(getUrl());

		// add custom label if it exists
		if (this.customLabel != null) {
			txt.append(" (").append(this.customLabel).append(')'); //$NON-NLS-1$
		}

		// add user
		txt.append("::"); //$NON-NLS-1$
		txt.append(getTeiidAdminInfo().getUsername());

		return txt.toString();
	}

}