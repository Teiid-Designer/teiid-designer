/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.teiid.net;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.teiid.core.util.StringUtil;
import org.teiid.designer.annotation.Removed;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;
import org.teiid.runtime.client.Messages;


/**
 * Class defines the URL in the Teiid.
 * 
 * @since 4.2
 */
public class TeiidURL {

    /**
     * JDBC constants
     */
    public static interface JDBC {
	    /**
	     * Constant indicating Virtual database name
	     */
	    String VDB_NAME = "VirtualDatabaseName"; //$NON-NLS-1$
	    /**
	     * Constant indicating Virtual database version
	     */
	    String VDB_VERSION = "VirtualDatabaseVersion"; //$NON-NLS-1$
	    /**
	     * Constant for vdb version part of serverURL
	     */
	    String VERSION = "version"; //$NON-NLS-1$
	}

	/**
	 * Connection constants
	 */
	public static interface CONNECTION {
		/**
		 * Constant indicating client ip address
		 */
		String CLIENT_IP_ADDRESS = "clientIpAddress"; //$NON-NLS-1$
		/**
		 * Constant indicating client hostname
		 */
		String CLIENT_HOSTNAME = "clientHostName"; //$NON-NLS-1$
		/**
		 * Constant indicating client mac address
		 */
		String CLIENT_MAC = "clientMAC"; //$NON-NLS-1$
		/**
		 * If true, will automatically select a new server instance after a communication exception.
		 * @since 5.6
		 */
		String AUTO_FAILOVER = "autoFailover";  //$NON-NLS-1$
		/**
		 * A plugable discovery strategy for the client.  Defaults to using the AdminApi. 
		 */
		String DISCOVERY_STRATEGY = "discoveryStategy"; //$NON-NLS-1$
		/**
		 * Constant indicating server url
		 */
		String SERVER_URL = "serverURL"; //$NON-NLS-1$
		/**
		 * Non-secure Protocol.
		 */        
		String NON_SECURE_PROTOCOL = "mm"; //$NON-NLS-1$
		/**
		 * Secure Protocol.
		 */
		String SECURE_PROTOCOL = "mms"; //$NON-NLS-1$
		/**
		 * Default app name
		 */
		String DEFAULT_APP_NAME = "JDBC"; //$NON-NLS-1$
		/**
		 * Name of the application which is obtaining connection
		 */
		String APP_NAME = "ApplicationName"; //$NON-NLS-1$
		/**
		 * Constant for username part of url
		 */
		String USER_NAME = "user"; //$NON-NLS-1$
		/**
		 * Constant for password part of url
		 */
		String PASSWORD = "password"; //$NON-NLS-1$
		/**
		 * Constant for admin property
		 */
		@Removed(Version.TEIID_8_0)
		String ADMIN = "admin"; //$NON-NLS-1$
		/**
		 * Constant for passthrough authentication
		 */
		String PASSTHROUGH_AUTHENTICATION = "PassthroughAuthentication"; //$NON-NLS-1$
		/**
		 * Constant for JAAS name
		 */
		String JAAS_NAME = "jaasName"; //$NON-NLS-1$
		/**
		 * Constant for kerberos service name
		 */
		String KERBEROS_SERVICE_PRINCIPLE_NAME = "kerberosServicePrincipleName"; //$NON-NLS-1$;
		/**
		 * Constant for encrypting requests
		 */
		String ENCRYPT_REQUESTS = "encryptRequests"; //$NON-NLS-1$;
		/**
         * Constant for login timeout
         */
		String LOGIN_TIMEOUT = "loginTimeout"; //$NON-NLS-1$
	}

	private static final String COMMA_DELIMITER = ","; //$NON-NLS-1$

    private static final String COLON_DELIMITER = ":"; //$NON-NLS-1$
    private static final String DEFAULT_PROTOCOL= TeiidURL.CONNECTION.NON_SECURE_PROTOCOL + "://"; //$NON-NLS-1$
    private static final String SECURE_PROTOCOL= TeiidURL.CONNECTION.SECURE_PROTOCOL + "://"; //$NON-NLS-1$

    private static final String INVALID_FORMAT_SERVER = Messages.getString(Messages.TeiidURL.invalid_format); 
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
        if (StringUtil.startsWithIgnoreCase(serverURL, SECURE_PROTOCOL)) {
        	usingSSL = true;
        } else if (!StringUtil.startsWithIgnoreCase(serverURL, DEFAULT_PROTOCOL)) {
        	throw new MalformedURLException(INVALID_FORMAT_SERVER);
        }

        appServerURL = serverURL;
		parseServerURL(serverURL.substring(usingSSL?SECURE_PROTOCOL.length():DEFAULT_PROTOCOL.length()), INVALID_FORMAT_SERVER);
    }
    
    /**
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
     * @param serverURL  Expected format: mm[s]://server1:port1[,server2:port2]
     * @return true if valid server url
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
     * @return host info
     */
    public List<HostInfo> getHostInfo() {
        return hosts;
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
        			throw new MalformedURLException(Messages.getString(Messages.TeiidURL.invalid_ipv6_hostport, nextToken, exceptionMessage)); 
        		}
        		host = nextToken.substring(1, hostEnd);
        		port = nextToken.substring(hostEnd+2);
        	}
        	else {
        		int hostEnd = nextToken.indexOf(":"); //$NON-NLS-1$
        		if (hostEnd == -1) {
        			throw new MalformedURLException(Messages.getString(Messages.TeiidURL.invalid_hostport, nextToken, exceptionMessage)); 
        		}
        		host = nextToken.substring(0, hostEnd);
        		port = nextToken.substring(hostEnd+1);
        	}
        	host = host.trim();
        	port = port.trim();
            if (host.equals("") || port.equals("")) { //$NON-NLS-1$ //$NON-NLS-2$
                throw new MalformedURLException(Messages.getString(Messages.TeiidURL.invalid_hostport, nextToken, exceptionMessage)); 
            }
            int portNumber = validatePort(port);
            HostInfo hostInfo = new HostInfo(host, portNumber);
            hosts.add(hostInfo);
        }
    }

	/**
	 * @param port
	 * @return integer version of port string
	 * @throws MalformedURLException
	 */
	public static int validatePort(String port) throws MalformedURLException {
		int portNumber;
		try {
		    portNumber = Integer.parseInt(port);
		} catch (NumberFormatException nfe) {
		    throw new MalformedURLException(Messages.getString(Messages.TeiidURL.non_numeric_port, port)); 
		}
		String msg = validatePort(portNumber);
		if (msg != null) {
			throw new MalformedURLException(msg);
		}
		return portNumber;
	}

	/**
	 * @param portNumber
	 * @return null if port is valid, otherwise port out of range message
	 */
	public static String validatePort(int portNumber) {
		if (portNumber < 0 || portNumber > 0xFFFF) {
		    return Messages.getString(Messages.TeiidURL.port_out_of_range, portNumber); 
		}
		return null;
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
                sb.append(SECURE_PROTOCOL);
            } else {
                sb.append(DEFAULT_PROTOCOL);
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
	 * @return whether url is using ssl
	 */
	public boolean isUsingSSL() {
		return usingSSL;
	}

}
