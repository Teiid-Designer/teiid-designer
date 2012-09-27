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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.teiid.core.util.CoreArgCheck;
import org.teiid.core.util.HashCodeUtil;
import org.teiid.designer.core.util.StringUtilities;
import org.teiid.designer.runtime.security.ISecureStorageProvider;

/**
 * 
 *
 * @since 8.0
 */
public abstract class TeiidConnectionInfo {

    /**
     * Protocol address prefix for secure server connections
     */
    public static final String MMS = "mms://"; //$NON-NLS-1$
    
    /**
     * Protocol address prefix for server connections
     */
    public static final String MM = "mm://"; //$NON-NLS-1$
    
    /**
     * Base key for the secure storage node used for holding passwords
     */
    protected static final String PREFERENCES_BASEKEY = PLUGIN_ID.replace('.', IPath.SEPARATOR);
    
    protected static final int DEFAULT_PORT_NUMBER = 0;

    private HostProvider hostProvider;
    private ISecureStorageProvider secureStorageProvider;
    private String password;
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
        setPassword(password);
        this.secure = secure;
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

    /**
     * @return the host provider (never <code>null</code>)
     */
    protected HostProvider getHostProvider() {
        if (this.hostProvider == null) {
            return HostProvider.DEFAULT_HOST_PROVIDER;
        }

        return this.hostProvider;
    }
    
    /**
     * @return the secureStorageProvider
     */
    protected ISecureStorageProvider getSecureStorageProvider() {
        return this.secureStorageProvider;
    }

    /**
     * @return the password (can be <code>null</code> or empty)
     */
    public String getPassword() {
        if (password != null)
            return password;
        
        password = secureStorageProvider.getFromSecureStorage(getProviderKey(), getPasswordKey());
        
        return password;
    }

    /**
     * @return the port (can be <code>null</code> or empty)
     */
    public String getPort() {
        return this.port;
    }
    
    /**
     * @return the port number
     */
    
    public int getPortNumber() {
    	return portNumber;
    }

    /**
     * @return the connection type (never <code>null</code>)
     */
    public abstract String getType();

    /**
     * @return the URL (never <code>null</code>)
     */
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
                                     getUsername(),
                                     getPassword());
    }
    
    /**
     * @return <code>true</code> if a secure connection protocol is being used
     */
    public boolean isSecure() {
        return this.secure;
    }

    /**
     * The port, password, user name, persisting password, secure protocol, and host provider are set.
     * 
     * @param info the connection properties whose values are being used to update state
     */
    public void setAll( TeiidConnectionInfo info ) {
        setHostProvider(info.getHostProvider());
        setPort(info.getPort());
        setPassword(info.getPassword());
        setUsername(info.getUsername());
        setSecure(info.isSecure());
    }

    /**
     * @param hostProvider the new value for host provider (never <code>null</code>)
     * @throws IllegalArgumentException if hostProvider is <code>null</code>
     */
    public void setHostProvider( HostProvider hostProvider ) {
        CoreArgCheck.isNotNull(hostProvider, "hostProvider"); //$NON-NLS-1$
        this.hostProvider = hostProvider;
    }

    /**
     * @param password the new value for password (can be empty or <code>null</code>)
     */
    public void setPassword( String password ) {
        if (password != null) {
            // Only store non-null values
            try {
                secureStorageProvider.storeInSecureStorage(getProviderKey(), getPasswordKey(), password);
            } catch (Exception e) {
                DqpPlugin.Util.log(e);
            }
        }
        
        this.password = password;
    }

    /**
     * @param port the new value for port (never empty or <code>null</code>)
     * @see #validate()
     */
    public void setPort( String port ) {
        this.port = port;
        try {
			this.portNumber = Integer.parseInt(port);
		} catch (NumberFormatException ex) {
			this.portNumber = DEFAULT_PORT_NUMBER;
		}
    }

    /**
     * @param secure the new value for if a secure connection protocol should be used
     */
    public void setSecure( boolean secure ) {
        this.secure = secure;
    }

    /**
     * @param username the new value for user name
     * @see #validate()
     */
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
    
    /**
     * Get the key to be used for storing properties against for this connection.
     * 
     * @return
     */
    private String getProviderKey() {
        String secureKey = new StringBuilder(PREFERENCES_BASEKEY)
        .append(getClass().getSimpleName())
        .append(getUrl())
        .append(IPath.SEPARATOR).toString();
        
        return secureKey;
    }
}
