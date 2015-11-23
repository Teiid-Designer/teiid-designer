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

import java.net.MalformedURLException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.runtime.spi.ITeiidConnectionInfo;

/**
 * 
 *
 * @since 8.0
 */
public abstract class TeiidConnectionInfo implements ITeiidConnectionInfo {

    protected static final int DEFAULT_PORT_NUMBER = 0;

    private ISecureStorageProvider secureStorageProvider;

    /*
     * A hash of the url and password. Generated when the password is initially set
     * then provides a unique reference to the password in the secure storage
     */
    private String passToken;
    
    private String passwordStorageKey;
    
    protected String url;

    private String host;
    private String port;
    private int portNumber = DEFAULT_PORT_NUMBER;
    private boolean secure;
    private String username;

    
    boolean settingAllInfo = false;

    /**
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param secureStorageProvider provider for storage of the password
     * @param password the connection password (can be <code>null</code> or empty)
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    protected TeiidConnectionInfo( String host,
    							   String port,
                                   String username,
                                   ISecureStorageProvider secureStorageProvider,
                                   String password,
                                   boolean secure) {
    	this.host = host;
        this.port = port;
        try {
			this.portNumber = Integer.parseInt(port);
		} catch (NumberFormatException ex) {
			this.portNumber = DEFAULT_PORT_NUMBER;
		}
        this.username = username;
        this.secureStorageProvider = secureStorageProvider;
        this.secure = secure;

    }

    /**
     * Used for initialising the password from constructors. Must be called by any
     * sub classes that add extra information to their urls since the password and
     * generated passToken are dependent on the url.
     *
     * Should password be null then no need to secure or set it.
     *
     * @param password
     */
    protected void initPassword(String password) {
        if (password == null)
            return;

        setPassword(password);
    }
    
    /**
     * Key that the password is stored against under secure storage
     * 
     * @return
     */
    protected abstract String getPasswordKey();

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object object ) {
        if (this == object) {
            return true;
        }

        if (object == null) {
            return false;
        }

        if (getClass() != object.getClass()) {
            return false;
        }

        TeiidConnectionInfo thatInfo = (TeiidConnectionInfo) object;

        // host
        String thisHost = getHost();
        String thatHost = thatInfo.getHost();

        if (thisHost == null) {
            if (thatHost != null) {
                return false;
            }
        } else {
            if (thatHost == null) {
                return false;
            }

            if (!thisHost.equals(thatHost)) {
                return false;
            }
        }

        // port
        if (getPort() == null) {
            if (thatInfo.getPort() != null) {
                return false;
            }
        } else {
            if (thatInfo.getPort() == null) {
                return false;
            }

            if (!getPort().equals(thatInfo.getPort())) {
                return false;
            }
        }

        // secure
        if (isSecure() != thatInfo.isSecure()) {
            return false;
        }

        // user
        if (getUsername() == null) {
            if (thatInfo.getUsername() != null) {
                return false;
            }
        } else {
            if (thatInfo.getUsername() == null) {
                return false;
            }

            if (!getUsername().equals(thatInfo.getUsername())) {
                return false;
            }
        }

        // password
        if (getPassword() == null) {
            if (thatInfo.getPassword() != null) {
                return false;
            }
        } else {
            if (thatInfo.getPassword() == null) {
                return false;
            }

            if (!getPassword().equals(thatInfo.getPassword())) {
                return false;
            }
        }

        return true;
    }
    
    @Override
    public ISecureStorageProvider getSecureStorageProvider() {
        return this.secureStorageProvider;
    }

    /**
     * Get the key to be used for storing properties against for this connection.
     *
     * @return provider key used as reference to secure storage
     * @throws Exception 
     */
    private void generateProviderKey() throws Exception {
        if( this.passwordStorageKey == null ) {
	        if (passToken == null) {
	            throw new Exception("password token is NULL");
	        } else {
	            this.passwordStorageKey = ConnectivityUtil.buildSecureStorageKey(getClass(), getUrl(), passToken);
	        }
        }
    }

    private boolean passwordExists() {
        try {
            boolean exists = secureStorageProvider.existsInSecureStorage(this.passwordStorageKey, getPasswordKey());
            return exists;
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            return false;
        }
    }

    private String retrievePassword() {
    	if( this.passwordStorageKey == null ) return null;
    	
        try {
            return secureStorageProvider.getFromSecureStorage(this.passwordStorageKey, getPasswordKey());
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            return null;
        }
    }

    private void generatePasswordToken(String password) {
        try {
            this.passToken = ConnectivityUtil.generateHashToken(getUrl(), password);
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
        }
    }
    
    private void storePassword(String password) {
    	boolean restoring = false;
        try {
        	 if (passToken == null ) {
        		 generateUrl();
        		 if( ConnectivityUtil.isPasswordToken(password)) {
        			 passToken = password;
        			 restoring = true;
        		 } else {
        			 generatePasswordToken(password);
        		 }
        		 generateProviderKey();
        	 }
        	 if( !restoring && !passwordExists() ) {
        		 secureStorageProvider.storeInSecureStorage(this.passwordStorageKey, getPasswordKey(), password);
        	 }
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
        } finally {
        	restoring = false;
        }
    }
    
    private void resetPassword() {
    	if( settingAllInfo ) return;
    	
    	// If HOST or PORT changes, this method needs to be called to do the following
    	
    	// 1) get the current password from storage with current passwordStorageKey
    	String currentPassword = retrievePassword();
    	
    	// 2) remove the node from storage
        try {
	    	if( this.passwordStorageKey != null ) {
	        	// if passToken != null, need to remove old stored password so new one can be stored instead?
	    		secureStorageProvider.removeFromSecureStorage(passwordStorageKey);
	    	}
	    } catch (Exception e) {
	        DqpPlugin.Util.log(e);
	    }
        
    	// 3) regenerate passToken
    	// 4) regenerate passwordStorageKey
    	// 5) restore password
        if( currentPassword != null ) {
        	setPassword(currentPassword);
        }
    }

    /**
     * @return the password (can be <code>null</code> or empty)
     */
    @Override
    public String getPassword() {
        String password = retrievePassword();

        return password;
    }
    
    /**
     * @return the host (can be <code>null</code> or empty)
     */
    @Override
    public String getHost() {
        return this.host;
    }

    /**
     * @return the port (can be <code>null</code> or empty)
     */
    @Override
    public String getPort() {
        return this.port;
    }
    
    /**
     * @return the port number
     */
    
    @Override
    public int getPortNumber() {
    	return portNumber;
    }

    /**
     * @return the connection type (never <code>null</code>)
     */
    @Override
    public abstract String getType();
    
    protected void generateUrl() {
        // mm<s>://host:port
        StringBuilder sb = new StringBuilder();
        sb.append(isSecure() ? MMS : MM);
        sb.append(getHost());
        sb.append(':');
        sb.append(getPort());

    	this.url = sb.toString();
    }

    /**
     * @return the URL (never <code>null</code>)
     */
    @Override
    public String getUrl() {
    	return this.url;
    }

    /**
     * @return the user name (can be <code>null</code> or empty)
     */
    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return HashCodeUtil.hashCode(0,
                                     getHost(),
                                     getPort(),
                                     isSecure(),
                                     getUsername());
    }
    
    /**
     * @return <code>true</code> if a secure connection protocol is being used
     */
    @Override
    public boolean isSecure() {
        return this.secure;
    }

    /**
     * The port, password, user name, persisting password, secure protocol, and host provider are set.
     * 
     * @param info the connection properties whose values are being used to update state
     */
    @Override
    public void setAll( ITeiidConnectionInfo info ) {
    	settingAllInfo = true;
        setHost(info.getHost());
        setPort(info.getPort());
        setUsername(info.getUsername());
        setSecure(info.isSecure());

        settingAllInfo = false;

        setPassword(info.getPassword());
    }
    
    /**
     * The port, password, user name, persisting password, secure protocol, and host provider are set.
     * 
     * @param info the connection properties whose values are being used to update state
     */
    @Override
    public void setAll(String host, String port, String username, String password, boolean isSecure ) {
    	settingAllInfo = true;
        setHost(host);
        setPort(port);
        setUsername(username);
        setSecure(isSecure);

        settingAllInfo = false;

        setPassword(password);
    }

    /**
     * @return the passToken
     */
    @Override
    public String getPassToken() {
        return this.passToken;
    }


    /**
     * Note. Password can be set to null and this will be stored as the value in secure storage
     *
     * @param password the new value for password (can be empty or <code>null</code>)
     */
    @Override
    public void setPassword( String password ) {
    	if( settingAllInfo ) return;  // setAll() will be setting host/port/username/isSecure. Don't want to setPassword it's complete

        /*
         * Real password being passed into this method so generate a token
         * and use it to store the real password in secure storage
         */
    	this.url = null;
    	this.passToken = null;
    	this.passwordStorageKey = null;
    	
    	if( password != null ) {
    		storePassword(password);
    	}
    }
    
    /**
     * @param port the new value for host (never empty or <code>null</code>)
     * @see #validate()
     */
    @Override
    public void setHost( String host ) {
    	this.host = host;
    	
    	resetPassword();
    }

    /**
     * @param port the new value for port (never empty or <code>null</code>)
     * @see #validate()
     */
    @Override
    public void setPort( String port ) {

        this.port = port;
        try {
            this.portNumber = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            this.portNumber = DEFAULT_PORT_NUMBER;
        }

        resetPassword();
    }

    /**
     * @param secure the new value for if a secure connection protocol should be used
     */
    @Override
    public void setSecure( boolean secure ) {

        this.secure = secure;

        resetPassword();
    }

    /**
     * @param username the new value for user name
     * @see #validate()
     */
    @Override
    public void setUsername( String username ) {
        this.username = username;
    }

    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return DqpPlugin.Util.getString("teiidConnectionInfoProperties", //$NON-NLS-1$
                                        getType(),
                                        getHost(),
                                        getPort(),
                                        getUsername(),
                                        getPassword(),
                                        isSecure());
    }

    /**
     * @return a status indicating if the connection info is in a validate state (never <code>null</code>)
     */
    @Override
    public IStatus validate() {
        IStatus status = validateUrl();

        if (!status.isOK()) {
            return status;
        }

        return validateUsername();
    }

    /**
     * @return a status indicating if the URL is valid (never <code>null</code>)
     */
    protected IStatus validateUrl() {
        // validate URL (protocol, host, port)
        try {
            TeiidServerUtils.validateServerUrl(getUrl());
        } catch (MalformedURLException e) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("invalidServerUrl", getType(), e.getMessage()), e); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    /**
     * @return a status indicating if the user name is valida (never <code>null</code>)
     */
    protected IStatus validateUsername() {
        // must have a user name
        if (StringUtilities.isEmpty(this.username)) {
            return new Status(IStatus.ERROR, PLUGIN_ID, Util.getString("connectionUsernameIsEmpty", getType())); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }
}
