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
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.datatools.connectivity.ConnectivityUtil;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;
import org.teiid.designer.runtime.spi.HostProvider;
import org.teiid.designer.runtime.spi.ITeiidConnectionInfo;

/**
 * 
 *
 * @since 8.0
 */
public abstract class TeiidConnectionInfo implements ITeiidConnectionInfo {

    protected static final int DEFAULT_PORT_NUMBER = 0;

    private HostProvider hostProvider;
    private ISecureStorageProvider secureStorageProvider;

    /*
     * A hash of the url and password. Generated when the password is initially set
     * then provides a unique reference to the password in the secure storage
     */
    private String passToken;

    private String port;
    private int portNumber = DEFAULT_PORT_NUMBER;
    private boolean secure;
    private String username;

    /**
     * @param port the connection port (can be <code>null</code> or empty)
     * @param username the connection user name (can be <code>null</code> or empty)
     * @param secureStorageProvider provider for storage of the password
     * @param password the connection password (can be <code>null</code> or empty)
     * @param secure <code>true</code> if a secure connection should be used
     * @see #validate()
     */
    protected TeiidConnectionInfo( String port,
                                   String username,
                                   ISecureStorageProvider secureStorageProvider,
                                   String password,
                                   boolean secure) {
        this.port = port;
        try {
			this.portNumber = Integer.parseInt(port);
		} catch (NumberFormatException ex) {
			this.portNumber = DEFAULT_PORT_NUMBER;
		}
        this.username = username;
        this.secureStorageProvider = secureStorageProvider;
        this.secure = secure;

        /*
         * Password must be set last since it relies on getUrl() which is built from
         * the other properties.
         *
         * When restoring from TeiidServerManager, the new TeiidConnectionInfo
         * will have a pass token for newer models and password for older models
         * so diverge at this point to ensure both situations are handled.
         *
         */
        initPassword(password);
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

        if (ConnectivityUtil.isPasswordToken(password))
            setPassToken(password);
        else
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
        String thisHost = getHostProvider().getHost();
        String thatHost = thatInfo.getHostProvider().getHost();

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
    public HostProvider getHostProvider() {
        if (this.hostProvider == null) {
            return HostProvider.DEFAULT_HOST_PROVIDER;
        }

        return this.hostProvider;
    }
    
    @Override
    public ISecureStorageProvider getSecureStorageProvider() {
        return this.secureStorageProvider;
    }

    /**
     * Get the key to be used for storing properties against for this connection.
     *
     * @return provider key used as reference to secure storage
     */
    private String getProviderKey() {
        String key;
        if (passToken == null)
            key = ConnectivityUtil.buildSecureStorageKey(getClass(), getUrl());
        else
            key = ConnectivityUtil.buildSecureStorageKey(getClass(), getUrl(), passToken);

        return key;
    }

    private boolean passwordExists() {
        try {
            String providerKey = getProviderKey();
            boolean exists = secureStorageProvider.existsInSecureStorage(providerKey, getPasswordKey());
            return exists;
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            return false;
        }
    }

    private String retrievePassword() {
        try {
            String providerKey = getProviderKey();
            return secureStorageProvider.getFromSecureStorage(providerKey, getPasswordKey());
        } catch (Exception ex) {
            DqpPlugin.Util.log(ex);
            return null;
        }
    }

    private void generatePasswordToken(String password) {
        try {
            this.passToken = ConnectivityUtil.generateHashToken(getUrl(), password);
            String providerKey = getProviderKey();
            secureStorageProvider.storeInSecureStorage(providerKey, getPasswordKey(), password);
        } catch (Exception e) {
            DqpPlugin.Util.log(e);
        }
    }

    /**
     * @return the password (can be <code>null</code> or empty)
     */
    @Override
    public String getPassword() {
        String password = retrievePassword();

        if (passToken == null && password != null) {
            // Password is pre-tokenisation algorithm so generate now and setup
            generatePasswordToken(password);
        }

        return password;
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

    /**
     * @return the URL (never <code>null</code>)
     */
    @Override
    public String getUrl() {
        // mm<s>://host:port
        StringBuilder sb = new StringBuilder();
        sb.append(isSecure() ? MMS : MM);
        sb.append(getHostProvider().getHost());
        sb.append(':');
        sb.append(getPort());

        return sb.toString();
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
                                     getHostProvider().getHost(),
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
        setHostProvider(info.getHostProvider(), true);
        setPort(info.getPort());
        setUsername(info.getUsername());
        setSecure(info.isSecure());

        /* Do these last since the url is required for creation of the pass token */
        setPassToken(info.getPassToken());
        setPassword(info.getPassword());
    }

    /**
     * @param hostProvider the new value for host provider (never <code>null</code>)
     * @param loadPasswords load passwords
     * @throws IllegalArgumentException if hostProvider is <code>null</code>
     */
    @Override
    public void setHostProvider( HostProvider hostProvider, boolean loadPasswords) {
        CoreArgCheck.isNotNull(hostProvider, "hostProvider"); //$NON-NLS-1$

        
        this.hostProvider = hostProvider;

        if( loadPasswords ) {
	        /* 
	         * Before changing:
	         * Retrieve password from secure storage using old url if one has been set
	         */
	        boolean passwordExists = passwordExists();
	        String myPassword = null;
	        if (passwordExists)
	            myPassword = retrievePassword();
	
	        if (passwordExists)
	            setPassword(myPassword);
    	}

    }

    /**
     * @return the passToken
     */
    @Override
    public String getPassToken() {
        return this.passToken;
    }

    @Override
    public void setPassToken(String passToken) {
        /*
         * Use the password token to fetch the password from
         * secure storage.
         */
        this.passToken = passToken;
    }

    /**
     * Note. Password can be set to null and this will be stored as the value in secure storage
     *
     * @param password the new value for password (can be empty or <code>null</code>)
     */
    @Override
    public void setPassword( String password ) {
        /*
         * Real password being passed into this method so generate a token
         * and use it to store the real password in secure storage
         */
        generatePasswordToken(password);
    }

    /**
     * @param port the new value for port (never empty or <code>null</code>)
     * @see #validate()
     */
    @Override
    public void setPort( String port ) {
        /* 
         * Before changing:
         * Retrieve password from secure storage using old url if one has been set
         */
        boolean passwordExists = passwordExists();
        String myPassword = null;
        if (passwordExists)
            myPassword = retrievePassword();

        this.port = port;
        try {
            this.portNumber = Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            this.portNumber = DEFAULT_PORT_NUMBER;
        }

        if (passwordExists)
            setPassword(myPassword);
    }

    /**
     * @param secure the new value for if a secure connection protocol should be used
     */
    @Override
    public void setSecure( boolean secure ) {
        /* 
         * Before changing:
         * Retrieve password from secure storage using old url if one has been set
         */
        boolean passwordExists = passwordExists();
        String myPassword = null;
        if (passwordExists)
            myPassword = retrievePassword();

        this.secure = secure;

        if (passwordExists)
            setPassword(myPassword);
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
                                        getHostProvider().getHost(),
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
