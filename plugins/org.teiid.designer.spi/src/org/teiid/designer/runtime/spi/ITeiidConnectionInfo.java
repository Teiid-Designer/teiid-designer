/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.spi;

import org.eclipse.core.runtime.IStatus;
import org.teiid.datatools.connectivity.spi.ISecureStorageProvider;

/**
 * @since 8.0
 */
public interface ITeiidConnectionInfo {

    /**
     * Protocol address prefix for secure server connections
     */
    public static final String MMS = "mms://"; //$NON-NLS-1$
    /**
     * Protocol address prefix for server connections
     */
    public static final String MM = "mm://"; //$NON-NLS-1$
    
    /**
     * The default connection host. Value is {@value}.
     */
    String DEFAULT_HOST = "localhost"; //$NON-NLS-1$

    /**
     * @return the password (can be <code>null</code> or empty)
     */
    String getPassword();

    /**
     * @return the host (can be <code>null</code> or empty)
     */
    String getHost();
    
    /**
     * @return the port (can be <code>null</code> or empty)
     */
    String getPort();

    /**
     * @return the port number
     */

    int getPortNumber();

    /**
     * @return the connection type (never <code>null</code>)
     */
    String getType();
    
    /**
     * @return the secureStorageProvider
     */
    ISecureStorageProvider getSecureStorageProvider();

    /**
     * @return the URL (never <code>null</code>)
     */
    String getUrl();

    /**
     * @return the user name (can be <code>null</code> or empty)
     */
    String getUsername();

    /**
     * @return <code>true</code> if a secure connection protocol is being used
     */
    boolean isSecure();

    /**
     * The port, password, user name, persisting password, secure protocol, and host provider are set.
     * 
     * @param info the connection properties whose values are being used to update state
     */
    void setAll(ITeiidConnectionInfo info);
    
    /**
     * The port, password, user name, persisting password, secure protocol, and host provider are set.
     * 
     * @param info the connection properties whose values are being used to update state
     */
    void setAll(String host, String port, String username, String password, boolean isSecure );
    
    /**
     * @param host the new value for host (never <code>null</code>)
     * @param loadPasswords load passwords
     * @throws IllegalArgumentException if hostProvider is <code>null</code>
     */
    void setHost(String host);

    /**
     * @param password the new value for password (can be empty or <code>null</code>)
     */
    void setPassword(String password);

    /**
     * @param port the new value for port (never empty or <code>null</code>)
     * @see #validate()
     */
    void setPort(String port);

    /**
     * @param secure the new value for if a secure connection protocol should be used
     */
    void setSecure(boolean secure);

    /**
     * @param username the new value for user name
     * @see #validate()
     */
    void setUsername(String username);

    /**
     * @return a status indicating if the connection info is in a validate state (never <code>null</code>)
     */
    IStatus validate();

    /**
     * @return the id referencing the password in secure storage
     */
    String getPassToken();
}
