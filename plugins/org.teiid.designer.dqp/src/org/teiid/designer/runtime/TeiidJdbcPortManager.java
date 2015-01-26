/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;

public final class TeiidJdbcPortManager {

    
    /**
     * Map containing applicable JDBC PORT value overrides.
     * 
     * This is required to allow basic teiid jdbc port override value
     * 
     * key == Teiid Server ID
     * value == PORT number (i.e. 31000, etc..)
     */
    private Map<String, String> overrideMap;
    
    private Map<String, String> defaultsMap;

	public TeiidJdbcPortManager() {
		init();
	}
	
	private void init() {
		overrideMap = new HashMap<String, String>(10);
		defaultsMap = new HashMap<String, String>(10);
	}
	
	public void setPort(ITeiidServer server, int port, boolean isOverride) {
		if( isOverride ) {
			if( port < 1 ) {
				overrideMap.remove(server);
			} else {
				overrideMap.put(server.getId(), Integer.toString(port));
			}
		} else {
			if( port < 1 ) {
				defaultsMap.remove(server);
			} else {
				defaultsMap.put(server.getId(), Integer.toString(port));
			}
		}
	}
	
	public String getPort(ITeiidServer server, boolean isOverride) {
		if( isOverride ) {
			return overrideMap.get(server.getId());
		} else {
			return defaultsMap.get(server.getId());
		}
	}
	
	public void cleanPorts(ITeiidServerManager manager) {
		List<String> serverIDs = new ArrayList<String>(10);

		for( ITeiidServer server : manager.getServers() ) {
			serverIDs.add(server.getId());
		}
		
		List<String> staleOverrideServerIDs = new ArrayList<String>(10);
		
		for( String serverID : overrideMap.keySet()) {
			if( ! serverIDs.contains(serverID) ) {
				staleOverrideServerIDs.add(serverID);
			}
		}
		
		for( String staleID : staleOverrideServerIDs ) {
			overrideMap.remove(staleID);
		}
	}
}
