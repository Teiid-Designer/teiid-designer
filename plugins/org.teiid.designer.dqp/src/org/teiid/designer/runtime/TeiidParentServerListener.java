/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import static org.teiid.designer.runtime.DqpPlugin.PLUGIN_ID;
import static org.teiid.designer.runtime.DqpPlugin.Util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.jboss.ide.eclipse.as.core.server.internal.v7.JBoss7Server;
import org.jboss.ide.eclipse.as.management.core.JBoss7ManangerException;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.runtime.TeiidServerFactory.ServerOptions;
import org.teiid.designer.runtime.adapter.JBoss7ServerUtil;
import org.teiid.designer.runtime.adapter.TeiidServerAdapterFactory;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.version.spi.TeiidServerVersion.Version;

/**
 * Singleton listener for monitoring both the {@link IServer}s'
 * life-cycle and their state.
 */
public class TeiidParentServerListener implements IServerLifecycleListener, IServerListener {
    
    private static TeiidParentServerListener instance;
    
    /*
     * The following exception is being caught during server start.
     * Could not execute "read-children-names" for undefined. Failure was "JBAS013493: System boot is in process; execution of remote management operations is not currently available".
     */
    private static String JBAS013493_CODE = "JBAS013493";  //$NON-NLS-1$

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

    private Thread startTeiidServerThread = null;

    private TeiidParentServerListener() {}

    @Override
    public void serverAdded(IServer server) {
        if (sleep) return;
        
        // Initialise the Teiid Instance manager is not already initialised
        DqpPlugin.getInstance().getServerManager();

        server.addServerListener(this);

        // New server added so add a teiid instance, even though it is not currently connected
        try {
            factory.adaptServer(server, ServerOptions.NO_CHECK_CONNECTION, ServerOptions.ADD_TO_REGISTRY);
        } catch (final Exception ex) {
            DqpPlugin.handleException(ex);
        }
    }

    @Override
    public void serverChanged(IServer server) {
        if (sleep) return;
        
        ITeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();

        try {
            for (ITeiidServer teiidServer : serverManager.getServers()) {
                if (! server.equals(teiidServer.getParent()))
                    continue;
            
                /*
                 * Cannot use updateServer as it replaces rather than modifies the existing server
                 * and references in editor will thus hang on to the old defunct version.
                 *
                 * Only update the settings which may have been queried from the server.
                 *
                 * The admin settings were changed in version 8+ to use the admin connection
                 * of the jboss parent server. Thus, version 7 should not try and change these
                 * while version 8+ should.
                 */

                if (teiidServer.getServerVersion().isGreaterThan(Version.TEIID_7_7.get())) {
                    teiidServer.getTeiidAdminInfo().setAll(
                    		teiidServer.getTeiidAdminInfo().getHost(), 
                    		teiidServer.getTeiidAdminInfo().getPort(), 
                    		teiidServer.getTeiidAdminInfo().getUsername(), 
                    		teiidServer.getTeiidAdminInfo().getPassword(), 
                    		teiidServer.getTeiidAdminInfo().isSecure());
                }
                String portNumber = serverManager.getJdbcPort(teiidServer, true);
                if( StringUtilities.isEmpty(portNumber) ) {
                    JBoss7Server jb7 = (JBoss7Server) server.loadAdapter(JBoss7Server.class, null);
                    if (jb7 != null) {
                    	portNumber = JBoss7ServerUtil.getJdbcPort(server, jb7);
                    	teiidServer.getTeiidJdbcInfo().setPort(portNumber);
                    }
                }

                teiidServer.notifyRefresh();

                return;
            }
        
            /*
             * We have a parent server with no Teiid Instance attached
             * This may be intentional if the parent server is not teiid
             * enabled but should check just in case.
             */
            factory.adaptServer(server, ServerOptions.ADD_TO_REGISTRY);
        } catch (Exception ex) {
            DqpPlugin.handleException(ex);
        }
    }
   
    @Override
    public void serverRemoved(IServer server) {
        if (sleep) return;
        
        server.removeServerListener(this);
        
        ITeiidServerManager serverManager = DqpPlugin.getInstance().getServerManager();
        
        // Tidy up the server manager by removing the related Teiid Instance
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

        try {
            if (state == IServer.STATE_STOPPING || state == IServer.STATE_STOPPED) {
                ITeiidServer teiidServer = factory.adaptServer(parentServer);
                if (teiidServer != null)
                    teiidServer.disconnect();
            
            } else if (state == IServer.STATE_STARTED) {

                teiidServerStarted(parentServer);
            }
        } catch (Exception ex) {
            DqpPlugin.handleException(ex);
        }
    }

    /**
     * @param parentServer
     * @throws Exception
     */
    private void teiidServerStarted(final IServer parentServer) {

        if (startTeiidServerThread != null && startTeiidServerThread.isAlive())
            return;

        Runnable serverStartRunnable = new Runnable() {

            private boolean connected = false;

            @Override
            public void run() {

                    try {
                        tryConnecting(parentServer);
                    } catch (Exception ex) {
                        DqpPlugin.handleException(ex);

                    }
            }

            /**
             * @param parentServer
             * @throws Exception
             */
            private void tryConnecting(final IServer parentServer) throws Exception {
            	int waitTimeInMS = getTimeoutPrefSecs() * 1000;
            	
                ITeiidServer teiidServer = factory.adaptServer(parentServer, ServerOptions.ADD_TO_REGISTRY);
                if (teiidServer != null) {
                    // Places the teiid server is a connecting state which
                    // can be detected by the UI
                    teiidServer.startConnecting();
                }

                boolean parentConnected = false; 
                /*
                 * Update all the settings since the server has been started and a
                 * proper set of queries can take place.
                 */
                ITeiidServer queryServer = null;
                int attempts = 0;

                // Loop will try to connect the teiid server 10 times after receiving a server
                // start signal from the server framework. Given the thread sleeps for 5 seconds
                // then the server has 60 seconds to finish starting if not already started.
                
                Exception logThisException = null;
                
                while ( (!parentConnected || queryServer == null ) && attempts < 10) {
                    try {
                        attempts++;
                        parentConnected = teiidServer != null && teiidServer.isParentConnected() && adaptServerOK(parentServer);
                        if( parentConnected ) {
                        	queryServer = factory.adaptServer(parentServer, ServerOptions.NO_CHECK_SERVER_REGISTRY);
                        }                  
                        Thread.sleep(waitTimeInMS);
                    } catch (Exception ex) {
                    	logThisException = ex;
                    }
                }
                
                if( queryServer != null ) {

	                /*
	                 * Updates those settings that may have been successfully queried from the
	                 * contacted server.
	                 */
	                teiidServer.getTeiidAdminInfo().setAll(queryServer.getTeiidAdminInfo());
	                teiidServer.getTeiidJdbcInfo().setPort(queryServer.getTeiidJdbcInfo().getPort());
	                
	                teiidServer.reconnect();
	                
	                // Cache the default Teiid JDBC port that was discovered on connection
	                try {
						int defaultPort = Integer.parseInt(queryServer.getTeiidJdbcInfo().getPort());
						DqpPlugin.getInstance().getServerManager().setJdbcPort(teiidServer, defaultPort, false);
						// If there is no override port cached, set it the same value as the default so they start in sync
						if( DqpPlugin.getInstance().getServerManager().getJdbcPort(teiidServer, true) == null ) {
							DqpPlugin.getInstance().getServerManager().setJdbcPort(teiidServer, defaultPort, true);
						}
					} catch (Exception e) {
						// DO NOTHING
					}

                } else if( teiidServer != null ) {
                    // If the query server is null then this is not a Teiid-enabled JBoss Server but
                    // a TeiidServer was cached in the registry, presumably due to an adaption
                    // being made while the server was not started. Since we now know better, we
                    // can correct the registry.
                    DqpPlugin.getInstance().getServerManager().removeServer(teiidServer);
                    if( logThisException != null ) {
                    	DqpPlugin.handleException(logThisException);
                    }
                    return;
                } else {
                    IStatus status = new Status(IStatus.WARNING, PLUGIN_ID,
                            Util.getString("warningServerNotFullyStarted_RefreshServer", parentServer.getName())); //$NON-NLS-1$
                    Util.log(status);
                }

                return;
            }
        };

        startTeiidServerThread = new Thread(serverStartRunnable, "Teiid Server Starting Thread"); //$NON-NLS-1$
        startTeiidServerThread.start();
    }
    
    private int getTimeoutPrefSecs() {
        return DqpPlugin.getInstance().getPreferences().getInt(PreferenceConstants.TEIID_SERVER_STARTUP_TIMEOUT_SEC, PreferenceConstants.TEIID_SERVER_STARTUP_TIMEOUT_SEC_DEFAULT);
    }
    
    private boolean adaptServerOK(final IServer parentServer) throws Exception {
    	try {
			factory.adaptServer(parentServer, ServerOptions.NO_CHECK_SERVER_REGISTRY);
		} catch (JBoss7ManangerException e) {
			if( e.getMessage().contains(JBAS013493_CODE)) {
				return false;
			}
			throw new Exception(e);
		} catch (Exception e) {
			throw new Exception(e);
		}
    	
    	return true;
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