package org.teiid.designer.datatools.profiles.modeshape;
// jdbc:jcr:http://localhost:8080/modeshape-rest/repository/default
public class ModeShapeJdbcUrl {

    private String host = ""; //$NON-NLS-1$

    private String port = ""; //$NON-NLS-1$

    private String protocol = ""; //$NON-NLS-1$

	private String path = "";

    /**
     * @param url
     */
    public ModeShapeJdbcUrl( String url ) {
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
    
    /**
     * @return Returns the path.
     */
    public String getPath() {
        return path;
    }

    private void parseURL( String url ) {
    	// jdbc:jcr:http://localhost:8080/modeshape-rest/repository/default
        try {
            String remainingURL = url.substring(url.indexOf(':') + 1);
            remainingURL = remainingURL.substring(url.indexOf(':'));
            this.protocol = remainingURL.substring(0, remainingURL.indexOf(':'));
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 3);
            this.host = remainingURL.substring(0, remainingURL.indexOf(':'));
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 1);
            this.port = remainingURL.substring(0, remainingURL.indexOf('/'));
            this.path = remainingURL.substring(remainingURL.indexOf('/'));
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
