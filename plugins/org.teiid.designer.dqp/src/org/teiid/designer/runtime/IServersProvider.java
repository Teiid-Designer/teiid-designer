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

/**
 * Abstracts dependencies on {@link ServerCore}. Default implementation
 * will directly employ {@link ServerCore} but it is possible for other 
 * implementations to be utilised that do not.
 * @since 8.0
 */
public interface IServersProvider {

    /**
     * Interface for listening for completed initialisation of
     * this servers provider
     */
    public static interface IServersInitialiseListener {

        /**
         * Called when the servers have been initialised
         */
        void serversInitialised();
    }

    /**
     * Is this server provider initialised
     *
     * @return true if initialiased
     */
    boolean isInitialised();

    /**
     * Add a listener that will be notified when the server provider is initialised
     *
     * @param listener
     */
    void addServerInitialisedListener(IServersInitialiseListener listener);

    /**
     * Remove a server initialised listener
     *
     * @param listener
     */
    void removeServerInitialisedListener(IServersInitialiseListener listener);

    /**
     * @param serversListener
     */
    void addServerLifecycleListener(IServerLifecycleListener serversListener);

    /**
     * @param serversListener
     */
    void removeServerLifecycleListener(IServerLifecycleListener serversListener);

    /**
     * @return collection of {@link IServer}s
     */
    IServer[] getServers();

    /**
     * @param serverStateListener
     */
    void addServerStateListener(IServerListener serverStateListener);

    /**
     * @param serverStateListener
     */
    void removeServerStateListener(IServerListener serverStateListener);
}
