package org.teiid.designer.datatools.profiles.modeshape;
// jdbc:jcr:http://localhost:8080/modeshape-rest/repository/
public class ModeShapeJdbcUrl {

    private String host = ""; //$NON-NLS-1$

    private String port = ""; //$NON-NLS-1$

    private String protocol = ""; //$NON-NLS-1$

	private String repos = ""; //$NON-NLS-1$
	
	private boolean teiidMetadata;

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
    public String getRepos() {
        return repos;
    }

    private void parseURL( String url ) {
    	// jdbc:jcr:http://localhost:8080/modeshape-rest/repository
        try {
            String remainingURL = url.substring(url.indexOf(':') + 1);
            remainingURL = remainingURL.substring(url.indexOf(':'));
            this.protocol = remainingURL.substring(0, remainingURL.indexOf(':'));
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 3);
            this.host = remainingURL.substring(0, remainingURL.indexOf(':'));
            remainingURL = remainingURL.substring(remainingURL.indexOf(':') + 1);
            this.port = remainingURL.substring(0, remainingURL.indexOf('/'));
            //snip off port
            remainingURL = remainingURL.substring(remainingURL.indexOf('/') + 1);
            //snip off modeshape-rest
            if(remainingURL.indexOf('?') == -1) {
            	this.repos =  remainingURL.substring(remainingURL.indexOf('/') + 1);
            } else {
            	this.repos =  remainingURL.substring(remainingURL.indexOf('/') + 1, remainingURL.indexOf('?'));
            	this.teiidMetadata = true;
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

	public boolean getTeiidMetadata() {
		return teiidMetadata;
	}

}
