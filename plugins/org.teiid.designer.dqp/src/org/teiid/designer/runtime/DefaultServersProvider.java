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
import org.eclipse.wst.server.core.ServerCore;

/**
 * Default implementation of {@link IServersProvider} that utilises
 * the {@link ServerCore}
 * 
 * @since 8.0
 *
 */
public class DefaultServersProvider implements IServersProvider {

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

}
