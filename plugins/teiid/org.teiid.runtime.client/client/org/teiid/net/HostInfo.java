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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.teiid.core.util.ArgCheck;


/**
 * Defines the hostname/port or {@link InetAddress} to connect to a host.
 * 
 * Similar to an {@link InetSocketAddress} except that it can be constructed
 * fully resolved, with an {@link InetAddress} and a hostname.
 * 
 * @since 4.2
 */
public class HostInfo {
    // Host Name and Port Number
    private String hostName;
    private int portNumber = 0;
    private InetAddress inetAddress;
    private boolean ssl;
    
    /**
     * Construct a fully resolved {@link HostInfo}.
     * @param hostName
     * @param addr
     */
    public HostInfo(String hostName, InetSocketAddress addr) {
    	this.hostName = hostName;
    	this.portNumber = addr.getPort();
    	this.inetAddress = addr.getAddress();
    }
    
    /**
     * Construct a {@link HostInfo} that can resolve each
     * time an {@link InetAddress} is asked for.
     * @param host
     * @param port
     */
    public HostInfo (String host, int port) {
    	ArgCheck.isNotNull(host);
		this.hostName = host.toLowerCase();
    	this.portNumber = port;
    	
    	//only cache inetaddresses if they represent the ip. 
    	try {
			InetAddress addr = InetAddress.getByName(this.hostName);
			if (addr.getHostAddress().equalsIgnoreCase(this.hostName)) {
				this.inetAddress = addr;
			}
		} catch (UnknownHostException e) {
		}
    }
    
    /**
     * @return inet address
     * @throws UnknownHostException
     */
    public InetAddress getInetAddress() throws UnknownHostException {
    	if (inetAddress != null) {
    		return inetAddress;
    	}
    	return InetAddress.getByName(this.hostName);
    }
    
    /**
     * @return host name
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @return port number
     */
    public int getPortNumber() {
        return portNumber;
    }
    
	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(hostName).append(":").append(portNumber); //$NON-NLS-1$
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object obj) {
    	if (obj == this) {
            return true;
    	}
    	if (!(obj instanceof HostInfo)) {
            return false;
    	}
        HostInfo hostInfo = (HostInfo) obj;
        if (portNumber != hostInfo.getPortNumber()) {
            return false;
        }
        if (ssl != hostInfo.ssl) {
                return false;
        }
        if (inetAddress != null && hostInfo.inetAddress != null) {
        	return inetAddress.equals(hostInfo.inetAddress);
        }
        return hostName.equals(hostInfo.getHostName());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.hostName == null) ? 0 : this.hostName.hashCode());
        result = prime * result + this.portNumber;
        return result;
    }

    /**
     * @return address is resolver or not
     */
    public boolean isResolved() {
    	return this.inetAddress != null;
    }
    
    /**
     * @return is ssl
     */
    public boolean isSsl() {
		return ssl;
	}
    
    /**
     * @param ssl
     */
    public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

}
