package org.teiid.designer.datatools.profiles.teiidadmin;

public class TeiidAdminUrl {

    private String host = ""; //$NON-NLS-1$

    private String port = ""; //$NON-NLS-1$

    private String protocol = ""; //$NON-NLS-1$

    /**
     * @param url
     */
    public TeiidAdminUrl( String url ) {
        if (url != null) {
            parseURL(url);
        }
    }

    /**
     * @return Returns the databaseName.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @return Returns the hostname.
     */
    public String getHost() {
        return host;
    }
    
    private void parseURL( String url ) {
    	// mm[s]://localhost:31443
        try {
            String remainingURL = url;
            this.protocol = remainingURL.substring(0, remainingURL.indexOf(':'));
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 3);
            this.host = remainingURL.substring(0, remainingURL.indexOf(':'));
            this.port = remainingURL.substring(remainingURL.indexOf(':') + 1);
            } catch (Exception e) {
        }
    }

    /**
     * @return Returns the port.
     */
    public String getPort() {
        return port;
    }

}
