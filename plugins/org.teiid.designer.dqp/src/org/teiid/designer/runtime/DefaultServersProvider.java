/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;
import org.teiid.designer.core.ParentServerMonitor;

/**
 * Default implementation of {@link IServersProvider} that utilises
 * the {@link ServerCore}.
 *
 * @since 8.0
 *
 */
public class DefaultServersProvider implements IServersProvider {

    private static final String ERROR_MSG = "Programming Error: The DefaultServersProvider is dependent upon " + //$NON-NLS-1$
                                                                            "the org.eclipse.wst.server.core.ServerCore for providing the parent " + //$NON-NLS-1$
                                                                            "servers configured in the Server View. The ServerCore needs to be" + //$NON-NLS-1$
                                                                            "initialised through a Job and as such is not available until the IDE has " + //$NON-NLS-1$
                                                                            "been fully loaded. See the DefaultServersProvider class for ways of " + //$NON-NLS-1$
                                                                            "avoiding this error."; //$NON-NLS-1$

    /**
     * Create a new instance but check that {@link ServerCore} has
     * been properly initialised.
     */
    public DefaultServersProvider() {
        if (! ParentServerMonitor.getInstance().isServerCoreInitialised())
            throw new RuntimeException(ERROR_MSG);
    }

    @Override
    public void addServerLifecycleListener(IServerLifecycleListener listener) {
        ServerCore.addServerLifecycleListener(listener);
    }

    @Override
    public void removeServerLifecycleListener(IServerLifecycleListener listener) {
        ServerCore.removeServerLifecycleListener(listener);
    }

    @Override
    public IServer[] getServers() {
        return ServerCore.getServers();
    }
    
    @Override
    public void addServerStateListener(IServerListener serverStateListener) {
        for (IServer server : getServers()) {
            server.addServerListener(serverStateListener);
        }
    }
    
    @Override
    public void removeServerStateListener(IServerListener serverStateListener) {
        for (IServer server : getServers()) {
            server.removeServerListener(serverStateListener);
        }
    }

}
