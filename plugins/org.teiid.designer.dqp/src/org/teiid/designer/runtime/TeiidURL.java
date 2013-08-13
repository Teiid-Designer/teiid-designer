/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Class defines the URL in the Teiid Instance.
 * 
 * @since 8.0
 */
public class TeiidURL {

    /**
     * Non-secure Protocol.
     */
    private static final String NON_SECURE_PROTOCOL = "mm"; //$NON-NLS-1$
    
    /**
     * Secure Protocol.
     */
    private static final String SECURE_PROTOCOL = "mms"; //$NON-NLS-1$
    
    private static final String DOUBLE_SLASH_DELIMITER = "//"; //$NON-NLS-1$
    
    private static final String COMMA_DELIMITER = ","; //$NON-NLS-1$

    private static final String COLON_DELIMITER = ":"; //$NON-NLS-1$
    
    private static final String DEFAULT_PREFIX= NON_SECURE_PROTOCOL + COLON_DELIMITER + DOUBLE_SLASH_DELIMITER;
    
    private static final String SECURE_PREFIX= SECURE_PROTOCOL + COLON_DELIMITER + DOUBLE_SLASH_DELIMITER;

    private static final String INVALID_FORMAT_SERVER = DqpPlugin.Util.getString("TeiidURL.Invalid_format"); //$NON-NLS-1$
    
    private class HostInfo {
        
        private String host;
        private int port;
        
        public HostInfo(String host, int port) {
            this.host = host;
            this.port = port;
        }
        
        /**
         * @return the host name
         */
        public String getHostName() {
            return host;
        }
        
        /**
         * @return the port number
         */
        public int getPortNumber() {
            return port;
        }
    }
    
    /*
     * appserver URL
     */
    private String appServerURL;
    /*
     * List of <code> HostData </code> in a cluster
     */
    private List<HostInfo> hosts = new ArrayList<HostInfo>();

    private boolean usingSSL = false;
    
    /**
     * Create an MMURL from the server URL.  For use by the server-side.
     * @param serverURL   Expected format: mm[s]://server1:port1[,server2:port2]
     * @throws MalformedURLException 
     * @since 4.2
     */
    public TeiidURL(String serverURL) throws MalformedURLException {
        if (serverURL == null) {
            throw new MalformedURLException(INVALID_FORMAT_SERVER);
        } 
        if (serverURL.toLowerCase().startsWith(SECURE_PREFIX)) {
            usingSSL = true;
        } else if (!serverURL.toLowerCase().startsWith(DEFAULT_PREFIX)) {
            throw new MalformedURLException(INVALID_FORMAT_SERVER);
        }

        appServerURL = serverURL;
        parseServerURL(serverURL.substring(usingSSL?SECURE_PREFIX.length():DEFAULT_PREFIX.length()), INVALID_FORMAT_SERVER);
    }
    
    /**
     * Create new instance with given parameters
     * 
     * @param host
     * @param port
     * @param secure
     */
    public TeiidURL(String host, int port, boolean secure) {
        usingSSL = secure;
        if(host.startsWith("[")) { //$NON-NLS-1$
            host = host.substring(1, host.indexOf(']'));
        }
        hosts.add(new HostInfo(host, port));
    }
    
    /**
     * Validates that a server URL is in the correct format.
     * 
     * @param serverURL  Expected format: mm[s]://server1:port1[,server2:port2]
     * @return true is URL is valid
     * 
     * @since 4.2
     */
    public static boolean isValidServerURL(String serverURL) {
        boolean valid = true;
        try {
            new TeiidURL(serverURL);            
        } catch (Exception e) {
            valid = false;
        }
        
        return valid;
    }
    
    /**
     * Get a list of hosts
     *  
     * @return string of host separated by commas
     * @since 4.2
     */
    public String getHosts() {
        StringBuffer hostList = new StringBuffer("");  //$NON-NLS-1$
        if( hosts != null) {
            Iterator<HostInfo> iterator = hosts.iterator();
            while (iterator.hasNext()) {
                HostInfo element = iterator.next();
                hostList.append(element.getHostName());
                if( iterator.hasNext()) { 
                    hostList.append(COMMA_DELIMITER); 
                }
            }
        }
        return hostList.toString();
    }
    
    /**
     * Get a list of ports  
     * 
     * @return string of ports seperated by commas
     * @since 4.2
     */
    public String getPorts() {
        StringBuffer portList = new StringBuffer("");  //$NON-NLS-1$
        if( hosts != null) {
            Iterator<HostInfo> iterator = hosts.iterator();
            while (iterator.hasNext()) {
                HostInfo element = iterator.next();
                portList.append(element.getPortNumber());
                if( iterator.hasNext()) { 
                    portList.append(COMMA_DELIMITER); 
                }
            }
        }
        return portList.toString();
    }

    /**
     * @param url
     * @throws MalformedURLException 
     * @since 4.2
     */
    private void parseServerURL(String serverURL, String exceptionMessage) throws MalformedURLException {
        StringTokenizer st = new StringTokenizer(serverURL, COMMA_DELIMITER); 
        if (!st.hasMoreTokens()) {
            throw new MalformedURLException(exceptionMessage);
        }
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            nextToken = nextToken.trim();
            String host = ""; //$NON-NLS-1$
            String port = ""; //$NON-NLS-1$
            if (nextToken.startsWith("[")) { //$NON-NLS-1$
                int hostEnd = nextToken.indexOf("]:"); //$NON-NLS-1$
                if (hostEnd == -1) {
                    throw new MalformedURLException(DqpPlugin.Util.getString("TeiidURL.invalid_ipv6_hostport", nextToken, exceptionMessage)); //$NON-NLS-1$
                }
                host = nextToken.substring(1, hostEnd);
                port = nextToken.substring(hostEnd+2);
            }
            else {
                int hostEnd = nextToken.indexOf(":"); //$NON-NLS-1$
                if (hostEnd == -1) {
                    throw new MalformedURLException(DqpPlugin.Util.getString("TeiidURL.invalid_hostport", nextToken, exceptionMessage)); //$NON-NLS-1$
                }
                host = nextToken.substring(0, hostEnd);
                port = nextToken.substring(hostEnd+1);
            }
            host = host.trim();
            port = port.trim();
            if (host.equals("") || port.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
                throw new MalformedURLException(DqpPlugin.Util.getString("TeiidURL.invalid_hostport", nextToken, exceptionMessage)); //$NON-NLS-1$
            }
            int portNumber = validatePort(port);
            HostInfo hostInfo = new HostInfo(host, portNumber);
            hosts.add(hostInfo);
        }
    }

    private int validatePort(String port) throws MalformedURLException {
        int portNumber;
        try {
            portNumber = Integer.parseInt(port);
        } catch (NumberFormatException nfe) {
            throw new MalformedURLException(DqpPlugin.Util.getString("TeiidURL.non_numeric_port", port)); //$NON-NLS-1$
        }
        
        if (portNumber < 0 || portNumber > 0xFFFF) {
            String msg = DqpPlugin.Util.getString("TeiidURL.port_out_of_range", portNumber); //$NON-NLS-1$
            throw new MalformedURLException(msg);
        }
        return portNumber;
    }

    /**
     * Get the Application Server URL
     * 
     * @return String for connection to the Server
     * @since 4.2
     */
    public String getAppServerURL() {
        if (appServerURL == null) {
            StringBuffer sb = new StringBuffer();
            if (usingSSL) {
                sb.append(SECURE_PREFIX);
            } else {
                sb.append(DEFAULT_PREFIX);
            }
            Iterator<HostInfo> iter = hosts.iterator();
            while (iter.hasNext()) {
                HostInfo host = iter.next();
                
                boolean ipv6HostName = host.getHostName().indexOf(':') != -1;
                if (ipv6HostName) {
                    sb.append('[');
                }
                sb.append(host.getHostName());
                if (ipv6HostName) {
                    sb.append(']');
                }                
                sb.append(COLON_DELIMITER); 
                sb.append(host.getPortNumber());
                if (iter.hasNext()) {
                    sb.append(COMMA_DELIMITER);
                }
            }
            appServerURL = sb.toString();
        }
        return appServerURL;
    }

    /**
     * @see java.lang.Object#toString()
     * @since 4.2
     */
    @Override
    public String toString() {
        return getAppServerURL(); 
    }

    /** 
     * @see java.lang.Object#equals(java.lang.Object)
     * @since 4.2
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } 
        if (!(obj instanceof TeiidURL)) {
            return false;
        } 
        TeiidURL url = (TeiidURL)obj;
        return (appServerURL.equals(url.getAppServerURL()));
    }
    
    /** 
     * @see java.lang.Object#hashCode()
     * @since 4.2
     */
    @Override
    public int hashCode() {
        return appServerURL.hashCode();
    }

    /**
     * @return whether url is using SSL
     */
    public boolean isUsingSSL() {
        return usingSSL;
    }

}

