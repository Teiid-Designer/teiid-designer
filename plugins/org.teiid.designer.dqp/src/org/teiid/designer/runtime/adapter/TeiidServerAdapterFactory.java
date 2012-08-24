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
import org.teiid.designer.runtime.TeiidAdminInfo;
import org.teiid.designer.runtime.TeiidJdbcInfo;
import org.teiid.designer.runtime.TeiidServer;
import org.teiid.designer.runtime.TeiidServerManager;

public class TeiidServerAdapterFactory implements IAdapterFactory {

    @Override
    public Class[] getAdapterList() {
        return new Class[] { TeiidServer.class };
    }

    @Override
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (TeiidServer.class != adapterType)
            return null;
        
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
    private TeiidServer adaptServer(IServer server) throws Exception {
        if (server.getServerState() != IServer.STATE_STARTED)
            return null;

        JBoss7Server jb7 = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
        if (jb7 == null)
            return null;
        
        if (! TeiidServerAdapterUtil.isTeiidServer(server))
            return null;
        
        // Only supports a jboss 7 server
        TeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();
        
        TeiidAdminInfo teiidAdminInfo = new TeiidAdminInfo(new Integer(jb7.getManagementPort()).toString(),
                                                           jb7.getUsername(),
                                                           jb7.getPassword(),
                                                           false,
                                                           false);
        
        TeiidServer teiidServer = serverManager.getServer(teiidAdminInfo.getUrl());
        if (teiidServer == null) {
            // No registered teiid server
            teiidServer = createTeiidServer(jb7, serverManager, teiidAdminInfo);
        }
        
        return teiidServer;
    }
    
    private TeiidServer createTeiidServer(JBoss7Server jb7, TeiidServerManager serverManager, TeiidAdminInfo teiidAdminInfo) throws Exception {
 
        String jdbcPort = TeiidServerAdapterUtil.getJdbcPort(jb7.getServer());
        
        TeiidJdbcInfo teiidJdbcInfo = new TeiidJdbcInfo(jdbcPort,
                                                                                TeiidJdbcInfo.DEFAULT_JDBC_USERNAME,
                                                                                TeiidJdbcInfo.DEFAULT_JDBC_PASSWORD,
                                                                                false,
                                                                                false);

        TeiidServer teiidServer = new TeiidServer(jb7.getHost(), teiidAdminInfo, teiidJdbcInfo, serverManager);
        
        // Initialise the ExecutionAdmin component of the teiid server
        teiidServer.getAdmin();
        
        serverManager.addServer(teiidServer);
        
        return teiidServer;
    }


}
