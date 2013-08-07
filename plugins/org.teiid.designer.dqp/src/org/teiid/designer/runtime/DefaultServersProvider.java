/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.runtime;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ResourceManager;
import org.teiid.designer.core.ModelerCore;

/**
 * Default implementation of {@link IServersProvider} that utilises
 * the {@link ServerCore}.
 *
 * This implementation is designed to check for and wait on the initialisation of {@link ServerCore}'s
 * server collection. This is actually initialised by its internal ResoourceManager, which
 * fails to identify when its in the process of initialisation itself.
 *
 * Should a call be made to {@link ServerCore#getServers()} while the ResourceManager
 * is initialisation then an incorrect empty collection is returned. To mitigate this scenario,
 * a wait thread is provided that will notify all interested listeners once the ResourceManager
 * has been correctly initialised and the servers can be fetched correctly.
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

    private class ObserverThread extends Thread {

        public ObserverThread() {
            super(DefaultServersProvider.this + "." + ObserverThread.class.getSimpleName()); //$NON-NLS-1$
            setDaemon(true);
        }

        @Override
        public void run() {
            while(! isServerCoreInitialised()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ModelerCore.Util.log(ex);
                }
            }

            synchronized(listenerLock) {
                Iterator<IServersInitialiseListener> iterator = listeners.iterator();
                while(iterator.hasNext()) {
                    IServersInitialiseListener listener = iterator.next();
                    listener.serversInitialised();
                    iterator.remove();
                }
            }
        }
    }

    private static final String INIT_FIELD_NAME = "initialized"; //$NON-NLS-1$

    private static Field resourceManagerInitField;

    private Object listenerLock = new Object();

    private Collection<IServersInitialiseListener> listeners = new ArrayList<IServersInitialiseListener>();

    /**
     * Create a new instance
     */
    public DefaultServersProvider() {
        try {
            resourceManagerInitField = ResourceManager.class.getDeclaredField(INIT_FIELD_NAME);
            resourceManagerInitField.setAccessible(true);

            ObserverThread thread = new ObserverThread();
            thread.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Check if ServerCore is properly initialised
     */
    private boolean isServerCoreInitialised() {
        try {
            return (Boolean) resourceManagerInitField.get(ResourceManager.class);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void checkServerCoreInitialised() {
        if (! isServerCoreInitialised())
            throw new RuntimeException(ERROR_MSG);
    }

    @Override
    public boolean isInitialised() {
        return isServerCoreInitialised();
    }

    @Override
    public void addServerInitialisedListener(IServersInitialiseListener listener) {
        if (isInitialised()) {
            // No need to listen since everything is initialised
            listener.serversInitialised();
            return;
        }

        synchronized(listenerLock) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeServerInitialisedListener(IServersInitialiseListener listener) {
        synchronized(listenerLock) {
            listeners.remove(listener);
        }
    }

    @Override
    public void addServerLifecycleListener(IServerLifecycleListener listener) {
        checkServerCoreInitialised();

        ServerCore.addServerLifecycleListener(listener);
    }

    @Override
    public void removeServerLifecycleListener(IServerLifecycleListener listener) {
        checkServerCoreInitialised();

        ServerCore.removeServerLifecycleListener(listener);
    }

    @Override
    public IServer[] getServers() {
        checkServerCoreInitialised();

        return ServerCore.getServers();
    }
    
    @Override
    public void addServerStateListener(IServerListener serverStateListener) {
        checkServerCoreInitialised();

        for (IServer server : getServers()) {
            server.addServerListener(serverStateListener);
        }
    }
    
    @Override
    public void removeServerStateListener(IServerListener serverStateListener) {
        checkServerCoreInitialised();

        for (IServer server : getServers()) {
            server.removeServerListener(serverStateListener);
        }
    }

}
