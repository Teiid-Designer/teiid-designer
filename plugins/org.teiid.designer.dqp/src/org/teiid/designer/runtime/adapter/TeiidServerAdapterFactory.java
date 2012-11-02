/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.ITeiidAdminInfo;
import org.teiid.designer.runtime.ITeiidConnectionInfo;
import org.teiid.designer.runtime.ITeiidJdbcInfo;
import org.teiid.designer.runtime.ITeiidServer;
import org.teiid.designer.runtime.TeiidAdminInfo;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;

/**
 * Adapter factory that can adapt an {@link IServer} to a {@link ITeiidServer}
 * 
 * @since 8.0
 */
public class TeiidServerAdapterFactory implements IAdapterFactory {

    /**
     * Used by {@link TeiidServerAdapterFactory#createTeiidServer}
     * to determine whether the {@link ITeiidServer} should be added
     * to the {@link TeiidServerManager} after it is created.
     */
    public enum ServerOptions {
        /**
         * Add the {@link ITeiidServer} to the {@link TeiidServerManager}
         */
        ADD_TO_REGISTRY,
        
        /**
         * Connect the client to the teiid server
         */
        CONNECT
    }
    
    private Object lock = new Object();
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { ITeiidServer.class };
    }

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType.isInstance(ITeiidServer.class))
            return null;
        
        if (adaptableObject instanceof ITeiidServer) {
            return adaptableObject;
        }
        
        try {
            if (adaptableObject instanceof IServer) {
                return adaptServer((IServer) adaptableObject);
            }
        } catch (Exception ex) {
            DqpPlugin.Util.log(IStatus.ERROR, ex, "Failed to determine if server supports teiid"); //$NON-NLS-1$
        }
        
        return null;
    }
    
    /**
     * @param server
     * @return
     */
    private ITeiidServer adaptServer(final IServer server) throws Exception {
        
        if (server.getServerState() != IServer.STATE_STARTED)
            return null;
        
        TeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();

        // Only supports a jboss 7 server
        JBoss7Server jb7 = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
        if (jb7 == null)
            return null;
        
        // See if we already have registered this teiid server
        String serverUrl = ITeiidConnectionInfo.MM + server.getHost() + ':' + jb7.getManagementPort();
        ITeiidServer teiidServer = serverManager.getServer(serverUrl);
        if (teiidServer != null)
            return teiidServer;
        
        synchronized (lock) {
            if (! TeiidServerAdapterUtil.isJBossServerConnected(server))
                return null;
            
            if (! TeiidServerAdapterUtil.isTeiidServer(server))
                return null;

            // Check again in case the thread had to wait for the lock
            teiidServer = serverManager.getServer(serverUrl);
            if (teiidServer == null)
                teiidServer = createTeiidServer(jb7, server, serverManager, ServerOptions.ADD_TO_REGISTRY, ServerOptions.CONNECT);
        
            return teiidServer;
        }
    }
    
    /**
     * Create a new {@link ITeiidServer}.
     * 
     * @see ServerOptions for the options parameter.
     * 
     * @param jboss7Server
     * @param serverManager
     * @param options
     * 
     * @return new {@link ITeiidServer} or null if cannot connect to the {@link JBoss7Server}
     * 
     * @throws Exception
     */
    public ITeiidServer createTeiidServer(final JBoss7Server jboss7Server, final IServer server, TeiidServerManager serverManager, ServerOptions... options) throws Exception {
  
        List<ServerOptions> optionList = Collections.emptyList(); 
        if (options != null)
            optionList = Arrays.asList(options);
        
        String jdbcPort = TeiidServerAdapterUtil.getJdbcPort(server);
       
        ITeiidAdminInfo teiidAdminInfo = new TeiidAdminInfo(new Integer(jboss7Server.getManagementPort()).toString(),
                                                           jboss7Server.getUsername(),
                                                           serverManager.getSecureStorageProvider(),
                                                           jboss7Server.getPassword(),
                                                           false);
        
        ITeiidJdbcInfo teiidJdbcInfo = new TeiidJdbcInfo(jdbcPort,
                                                        ITeiidJdbcInfo.DEFAULT_JDBC_USERNAME,
                                                        serverManager.getSecureStorageProvider(),
                                                        ITeiidJdbcInfo.DEFAULT_JDBC_PASSWORD,
                                                        false);

        ITeiidServer teiidServer = new TeiidServer(jboss7Server.getHost(), teiidAdminInfo, teiidJdbcInfo, serverManager, server);
        
        if (optionList.contains(ServerOptions.CONNECT)) {
            // Connect this teiid server        
            teiidServer.connect();
        }
        
        if (optionList.contains(ServerOptions.ADD_TO_REGISTRY)) {
            serverManager.addServer(teiidServer);
        }
        
        return teiidServer;
    }
    
    public ITeiidServer createTeiidServer(IServer server, TeiidServerManager serverManager, ServerOptions... options) {
        JBoss7Server jboss7Server = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
        if (jboss7Server == null)
            return null;
        
        try {
            return createTeiidServer(jboss7Server, server, serverManager, options);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
