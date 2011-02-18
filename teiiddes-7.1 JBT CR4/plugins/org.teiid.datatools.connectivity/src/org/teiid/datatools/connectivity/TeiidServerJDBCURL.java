/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity;

/**
 * 
 */
public class TeiidServerJDBCURL {

    private static final String PROTOCOL_SSL_TRUE_TEXT = "mms"; //$NON-NLS-1$

	private String node = ""; //$NON-NLS-1$

    private String port = ""; //$NON-NLS-1$

    private String vdbName = ""; //$NON-NLS-1$

    private String properties = ""; //$NON-NLS-1$

	private boolean isSecureProtocol;

    /**
     * @param url
     */
    public TeiidServerJDBCURL( String url ) {
        if (url != null) {
            parseURL(url);
        }
    }

    /**
     * @return Returns the databaseName.
     */
    public String getDatabaseName() {
        return vdbName;
    }

    /**
     * @return Returns the node.
     */
    public String getNode() {
        return node;
    }

    private void parseURL( String url ) {
        // jdbc:teiid:<vdb-name>@mm[s]://<host>:<port>;[prop-name=prop-value;]*
        try {
            String remainingURL = url.substring(url.indexOf(':') + 1);
            remainingURL = remainingURL.substring(url.indexOf(':') + 2);
            this.vdbName = remainingURL.substring(0, remainingURL.indexOf('@'));
            remainingURL = remainingURL.substring(remainingURL.indexOf('@') + 1);
            String protocol = remainingURL.substring(0, remainingURL.indexOf(':'));
            isSecureProtocol = protocol.equalsIgnoreCase(PROTOCOL_SSL_TRUE_TEXT);
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 3);
            this.node = remainingURL.substring(0, remainingURL.indexOf(':'));
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 1);

            if (remainingURL.indexOf(';') > -1) {
                // there are connection properties
                // TODO: how do we want to handle these? As checkboxes or test fields?
                this.node = remainingURL.substring(0, remainingURL.indexOf(';'));
                remainingURL = remainingURL.substring(remainingURL.indexOf(';') + 1);
                this.properties = remainingURL;

            } else {
                this.port = remainingURL;
            }
        } catch (Exception e) {
        }
    }

    /**
     * @return Returns the port.
     */
    public String getPort() {
        return port;
    }

    /**
     * @return Returns the properties.
     */
    public String getProperties() {
        return properties;
    }

	public boolean isSecureProtocol() {
		return isSecureProtocol;
	}
}
