/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.teiid.designer.runtime.DqpPlugin;
import org.teiid.designer.runtime.HostProvider;
import org.teiid.designer.runtime.TeiidAdminInfo;
import org.teiid.designer.runtime.TeiidConnectionInfo;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;

/**
 * Adapter factory that can adapt an {@link IServer} to a {@link TeiidServer}
 * 
 * @since 8.0
 */
public class TeiidServerAdapterFactory implements IAdapterFactory {

    private Object lock = new Object();
    
    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class };
    }

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (TeiidServer.class != adapterType)
            return null;
        
        if (adaptableObject instanceof TeiidServer) {
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
    private TeiidServer adaptServer(final IServer server) throws Exception {
        
        if (server.getServerState() != IServer.STATE_STARTED)
            return null;
        
        TeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();

        // Only supports a jboss 7 server
        JBoss7Server jb7 = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
        if (jb7 == null)
            return null;
        
        // See if we already have registered this teiid server
        String serverUrl = TeiidConnectionInfo.MM + server.getHost() + ':' + jb7.getManagementPort();
        TeiidServer teiidServer = serverManager.getServer(serverUrl);
        if (teiidServer != null)
            return teiidServer;
        
        synchronized (lock) {
            // Check again in case the thread had to wait for the lock
            teiidServer = serverManager.getServer(serverUrl);
            if (teiidServer == null)
                teiidServer = createTeiidServer(jb7, serverManager);
        
            return teiidServer;
        }
    }
    
    private TeiidServer createTeiidServer(final JBoss7Server jb7, TeiidServerManager serverManager) throws Exception {
 
        if (! TeiidServerAdapterUtil.isJBossServerConnected(jb7.getServer()))
            return null;
        
        if (! TeiidServerAdapterUtil.isTeiidServer(jb7.getServer()))
            return null;
 
        String jdbcPort = TeiidServerAdapterUtil.getJdbcPort(jb7.getServer());
        
        TeiidAdminInfo teiidAdminInfo = new TeiidAdminInfo(new Integer(jb7.getManagementPort()).toString(),
                                                           jb7.getUsername(),
                                                           jb7.getPassword(),
                                                           true,
                                                           false);
        
        /*
         * Need to set a temporary host provider for this admin info
         * as the getUrl() method is dependent upon it, otherwise it
         * just makes the host 'localhost' so never finds the TeiidServer
         * in the TeiidServerManager.
         */
        teiidAdminInfo.setHostProvider(new HostProvider() {
            @Override
            public String getHost() {
                return jb7.getServer().getHost();
            }
        });
        
        TeiidJdbcInfo teiidJdbcInfo = new TeiidJdbcInfo(jdbcPort,
                                                                                TeiidJdbcInfo.DEFAULT_JDBC_USERNAME,
                                                                                TeiidJdbcInfo.DEFAULT_JDBC_PASSWORD,
                                                                                true,
                                                                                false);

        TeiidServer teiidServer = new TeiidServer(jb7.getHost(), teiidAdminInfo, teiidJdbcInfo, serverManager);
        
        // Initialise the ExecutionAdmin component of the teiid server
        teiidServer.getAdmin();
        
        serverManager.addServer(teiidServer);
        
        return teiidServer;
    }


}
