/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;

/**
 *
 */
public class TestServersProvider implements IServersProvider {
    
    private final String[] HOSTS = new String[] { "localhost", "myserver.com" };
    
    private final String[] PARENT_IDS = new String[] { "server1", "server2" };
    
    private List<IServer> servers = new ArrayList<IServer>();
    
    /**
     * Create new instance
     */
    public TestServersProvider() {
        
        for (String parentId : PARENT_IDS) {
            for (String host : HOSTS) {
                IServer mockServer = mock(IServer.class);
                when(mockServer.getHost()).thenReturn(host);
                when(mockServer.getId()).thenReturn(parentId);
            
                servers.add(mockServer);
            }
        }
    }

    @Override
    public boolean isInitialised() {
        return true;
    }

    @Override
    public void addServerInitialisedListener(IServersInitialiseListener listener) {
        // do nothing
    }

    @Override
    public void removeServerInitialisedListener(IServersInitialiseListener listener) {
        // do nothing
    }

    @Override
    public void removeServerLifecycleListener(IServerLifecycleListener serversListener) {
        // do nothing
    }
    
    @Override
    public IServer[] getServers() {
        return servers.toArray(new IServer[0]);
    }
    
    @Override
    public void addServerLifecycleListener(IServerLifecycleListener serversListener) {
        // do nothing
    }
    
    @Override
    public void addServerStateListener(IServerListener serverStateListener) {
     // do nothing
    }
    
    @Override
    public void removeServerStateListener(IServerListener serverStateListener) {
     // do nothing
    }
}
