/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.Util;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.teiid.designer.runtime.TeiidServerFactory.ServerOptions;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterFactory;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;

/**
 * Singleton listener for monitoring both the {@link IServer}s'
 * life-cycle and their state.
 */
public class TeiidParentServerListener implements IServerLifecycleListener, IServerListener {
    
    private static TeiidParentServerListener instance;
    
    /**
     * Get the singleton instance of of this class
     * 
     * @return instance
     */
    public static TeiidParentServerListener getInstance() {
        if (instance == null)
            instance = new TeiidParentServerListener();
        
        return instance;
    }
    
    private TeiidServerAdapterFactory factory = new TeiidServerAdapterFactory();
    
    private boolean sleep;
    
    private TeiidParentServerListener() {}
    
    @Override
    public void serverAdded(IServer server) {
        if (sleep) return;
        
        // Initialise the teiid server manager is not already initialised
        DqpPlugin.getInstance().getServerManager();
        
        server.addServerListener(this);
        factory.adaptServer(server, ServerOptions.ADD_TO_REGISTRY);
    }
    
    @Override
    public void serverChanged(IServer server) {
        if (sleep) return;
        
        ITeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();
        
        for (ITeiidServer teiidServer : serverManager.getServers()) {
            if (! server.equals(teiidServer.getParent()))
                continue;
            
            ITeiidServer newTeiidServer = factory.adaptServer(server, ServerOptions.NO_CHECK_SERVER_REGISTRY, ServerOptions.NO_CHECK_CONNECTION);
            
            // Cannot use updateServer as it replaces rather than modified the existing server
            // and references in editor will thus hang on to the old defunct version
            teiidServer.update(newTeiidServer);
            teiidServer.notifyRefresh();
            
            return;
        }
        
        /*
         * We have a parent server with no teiid server attached
         * This may be intentional if the parent server is not teiid
         * enabled but should check just in case.
         */
        factory.adaptServer(server, ServerOptions.ADD_TO_REGISTRY);
    }
   
    @Override
    public void serverRemoved(IServer server) {
        if (sleep) return;
        
        server.removeServerListener(this);
        
        ITeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();
        
        // Tidy up the server manager by removing the related teiid server
        for (ITeiidServer teiidServer : serverManager.getServers()) {
            if (server.equals(teiidServer.getParent())) {
                serverManager.removeServer(teiidServer);
                break;
            }
        }
    }
    
    @Override
    public void serverChanged(ServerEvent event) {
        if (sleep) return;
        
        if (event == null) return;

        int eventKind = event.getKind();
        if ((eventKind & ServerEvent.SERVER_CHANGE) == 0) return;

        // server change event
        if ((eventKind & ServerEvent.STATE_CHANGE) == 0) return;

        int state = event.getState();
        IServer parentServer = event.getServer();

        if (state == IServer.STATE_STOPPING || state == IServer.STATE_STOPPED) {
            ITeiidServer teiidServer = factory.adaptServer(parentServer);
            if (teiidServer != null)
                teiidServer.disconnect();
            
        } else if (state == IServer.STATE_STARTED) {

            ITeiidServer teiidServer = factory.adaptServer(parentServer, ServerOptions.ADD_TO_REGISTRY);
            if (teiidServer != null && teiidServer.isParentConnected()) {
                /*
                * Update all the settings since the server has been started and a 
                * proper set of queries can take place.
                */
                ITeiidServer queryServer = factory.adaptServer(parentServer,
                                                               ServerOptions.NO_CHECK_SERVER_REGISTRY);
                
                teiidServer.update(queryServer);

                try {
                    teiidServer.reconnect();
                } catch (Exception ex) {
                    Util.log(ex);
                }
                
            }
        }
    }

    /**
     * Deafen this listener
     */
    public void sleep() {
        sleep = true;
    }

    /**
     * Awaken this listener
     */
    public void wake() {
        sleep = false;
    }
}