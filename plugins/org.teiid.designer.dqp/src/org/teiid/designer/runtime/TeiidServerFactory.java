/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.wst.server.core.IServer;
import org.teiid.designer.runtime.spi.ITeiidAdminInfo;
import org.teiid.designer.runtime.spi.ITeiidJdbcInfo;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;

/**
 * @since 8.0
 */
public class TeiidServerFactory {

    /**
     * Determine whether the {@link ITeiidServer} should be added
     * to the {@link TeiidServerManager} after it is created and whether
     * it should be connected.
     */
    public enum ServerOptions {
        /**
         * Add the {@link ITeiidServer} to the {@link TeiidServerManager}
         */
        ADD_TO_REGISTRY,
        
        /**
         * Connect the client to the Teiid Instance
         */
        CONNECT,
        
        /**
         * Do NOT query the {@link TeiidServerManager}'s registry for an existing
         * {@link ITeiidServer}
         */
        NO_CHECK_SERVER_REGISTRY, 
        
        
        /**
         * Do NOT query whether the parent {@link IServer} is connected
         */
        NO_CHECK_CONNECTION,
        
        /**
         * Server uses secure connections for admin requests
         */
        ADMIN_SECURE_CONNECTION,
        
        /**
         * Server uses secure connections for jdbc requests
         */
        JDBC_SECURE_CONNECTION
    }
    
    /**
     * @param teiidServer
     * @param serverManager
     * @param options
     */
    private void processOptions(ITeiidServer teiidServer, ITeiidServerManager serverManager, ServerOptions[] options) {
        List<ServerOptions> optionList = Collections.emptyList(); 
        if (options != null)
            optionList = Arrays.asList(options);
        
        if (optionList.contains(ServerOptions.ADMIN_SECURE_CONNECTION)) {
            teiidServer.getTeiidAdminInfo().setSecure(true);
        }
        
        if (optionList.contains(ServerOptions.JDBC_SECURE_CONNECTION)) {
            teiidServer.getTeiidJdbcInfo().setSecure(true);
        }
        
        if (optionList.contains(ServerOptions.CONNECT)) {
            // Connect this Teiid Instance
            try {
                teiidServer.connect();
            } catch (Exception ex) {
                DqpPlugin.Util.log(ex);
            }
        }
        
        if (optionList.contains(ServerOptions.ADD_TO_REGISTRY)) {
            serverManager.addServer(teiidServer);
        }
    }
    
    /**
     * @param serverVersion 
     * @param serverManager
     * @param parentServer
     * @param adminPort
     * @param adminUserName
     * @param adminPassword
     * @param jdbcPort
     * @param jdbcUserName
     * @param jdbcPassword
     * @param options
     * 
     * @return new {@link ITeiidServer}
     */
    public ITeiidServer createTeiidServer(ITeiidServerVersion serverVersion, 
                                                               ITeiidServerManager serverManager,
                                                               IServer parentServer,
                                                               String adminPort,
                                                               String adminUserName,
                                                               String adminPassword,
                                                               String jdbcPort,
                                                               String jdbcUserName,
                                                               String jdbcPassword,
                                                               ServerOptions... options) {
        
        ITeiidAdminInfo teiidAdminInfo = new TeiidAdminInfo(adminPort,
                                                            adminUserName,
                                                            serverManager.getSecureStorageProvider(),
                                                            adminPassword,
                                                            false);
         
         ITeiidJdbcInfo teiidJdbcInfo = new TeiidJdbcInfo(jdbcPort,
                                                         jdbcUserName,
                                                         serverManager.getSecureStorageProvider(),
                                                         jdbcPassword,
                                                         false);

         ITeiidServer teiidServer = new TeiidServer(serverVersion, teiidAdminInfo, teiidJdbcInfo, serverManager, parentServer);
         
         processOptions(teiidServer, serverManager, options);
         
         return teiidServer;
    }

    /**
     * @param serverVersion 
     * @param teiidAdminInfo 
     * @param teiidJdbcInfo 
     * @param serverManager 
     * @param parentServer 
     * @param options 
     * 
     * @return instance of {@link ITeiidServer}
     */
    public ITeiidServer createTeiidServer(ITeiidServerVersion serverVersion,
                                                               ITeiidAdminInfo teiidAdminInfo,
                                                               ITeiidJdbcInfo teiidJdbcInfo,
                                                               TeiidServerManager serverManager,
                                                               IServer parentServer,
                                                               ServerOptions... options) {

        ITeiidServer teiidServer = new TeiidServer(serverVersion, teiidAdminInfo, teiidJdbcInfo, serverManager, parentServer);
        processOptions(teiidServer, serverManager, options);
        
        return teiidServer;
    }
}
