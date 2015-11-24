/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.ui.connection;

import java.util.Properties;

import org.teiid.designer.runtime.spi.ITeiidServer;


/**
 *
 */
public class VdbDataSourceInfo {
    
	private static final String PASS_THROUGH_KEY = "PassthroughAuthentication"; //$NON-NLS-1$
	private static final String PASSWORD_KEY = "password"; //$NON-NLS-1$
	private static final String USERNAME_KEY = "user-name"; //$NON-NLS-1$
	private static final String CONNECTION_URL_KEY = "connection-url"; //$NON-NLS-1$
	private static final String DRIVER_CLASS_KEY = "driver-class"; //$NON-NLS-1$
	private static final String TEIID_DRIVER_CLASS_NAME = "org.teiid.jdbc.TeiidDriver"; //$NON-NLS-1$
	private static final String TEIID_JDBC_URL_PREFIX = "jdbc:teiid:"; //$NON-NLS-1$
	private static final char SEMI_COLON = ';';
	private static final String PROTOCOL = "@mm";
	private static final String DELIMITER = "//";
	private static final String SECURE_PROTOCOL = "@mms";
	private static final char COLON = ':';
	private static final char EQUALS = '=';
	private static final String TRUE_VALUE = "\"true\""; //$NON-NLS-1$
	private String vdbName;
    private String displayName;
    private String jndiName;
    private String username;
    private String password;
    private ITeiidServer teiidServer;
    private boolean passThroughAuthentication;

    public VdbDataSourceInfo() {

    }

    /**
     * @param displayName
     * @param jndiName
     * @param properties
     * @param connectionInfoProvider
     * @param requiresPassword
     */
    public VdbDataSourceInfo(   String vdbName,
    							String displayName,
                                String jndiName,
                                ITeiidServer teiidServer) {
        super();
        this.vdbName = vdbName;
        this.displayName = displayName;
        this.jndiName = jndiName;
        this.teiidServer = teiidServer;
    }

    /**
     * @return vdbName
     */
    public String getVdbName() {
        return vdbName;
    }
    
    /**
     * @return displayName
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @param displayName Sets displayName to the specified value.
     */
    public void setDisplayName( String displayName ) {
        this.displayName = displayName;
    }

    /**
     * @return jndiName
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * @param jndiName Sets jndiName to the specified value.
     */
    public void setJndiName( String jndiName ) {
        this.jndiName = jndiName;
    }

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the passThroughAuthentication
	 */
	public boolean isPassThroughAuthentication() {
		return this.passThroughAuthentication;
	}

	/**
	 * @param passThroughAuthentication the passThroughAuthentication to set
	 */
	public void setPassThroughAuthentication(boolean passThroughAuthentication) {
		this.passThroughAuthentication = passThroughAuthentication;
	}
    
    public String getUrl() {
    	StringBuilder sb = new StringBuilder();
    	sb.append(TEIID_JDBC_URL_PREFIX).append(this.vdbName);
    	String host = this.teiidServer.getTeiidJdbcInfo().getHost();
		String port = this.teiidServer.getTeiidJdbcInfo().getPort();
		String protocol = this.teiidServer.getTeiidJdbcInfo().isSecure() ? SECURE_PROTOCOL : PROTOCOL;
		sb.append(protocol).append(COLON).append(DELIMITER).append(host).append(COLON).append(port);
    	if( isPassThroughAuthentication() ) {
    		sb.append(SEMI_COLON).append(PASS_THROUGH_KEY).append(EQUALS).append(TRUE_VALUE).append(SEMI_COLON);
    	}
    	
    	return sb.toString();
    }
    
    public Properties getProperties() {
    	Properties props = new Properties();
    	if( this.username != null ) {
    		props.put(USERNAME_KEY, this.username);
    	}
    	if( this.password != null ) {
    		props.put(PASSWORD_KEY, this.password);
    	}
    	props.put(DRIVER_CLASS_KEY, TEIID_DRIVER_CLASS_NAME);
    	props.put(CONNECTION_URL_KEY, getUrl());
    	return props;
    }
}
