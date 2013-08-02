/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ResourceManager;

/**
 * This class is designed to check for and wait on the initialisation of {@link ServerCore}'s
 * server collection. This is actually initialised by its internal ResoourceManager, which 
 * fails to identify when its in the process of initialisation itself.
 *
 * Should a call be made to {@link ServerCore#getServers()} while the ResourceManager
 * is initialisation then an incorrect empty collection is returned. To mitigate this scenario,
 * a wait thread is provided that will notify all interested listeners once the ResourceManager
 * has been correctly initialised and the servers can be fetched correctly.
 */
public class ParentServerMonitor {

    /**
     * Interface for listening for completed initialisation of
     * the {@link ServerCore}.
     */
    public static interface IParentServerMonitorListener {

        /**
         * Called when the servers have been initialised
         */
        void serversInitialised();
    }

    private static final String INIT_FIELD_NAME = "initialized"; //$NON-NLS-1$

    private static ParentServerMonitor instance;

    private static Field resourceManagerInitField;

    private Collection<IParentServerMonitorListener> listeners = new ArrayList<IParentServerMonitorListener>();

    private class ObserverThread extends Thread {

        public ObserverThread() {
            super(ParentServerMonitor.this + "." + ObserverThread.class.getSimpleName()); //$NON-NLS-1$
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

            for (IParentServerMonitorListener listener : listeners) {
                listener.serversInitialised();
            }
        }
    }

    /**
     * @return singleton instance
     */
    public static ParentServerMonitor getInstance() {
        if (instance == null) {
            instance = new ParentServerMonitor();

            try {
                resourceManagerInitField = ResourceManager.class.getDeclaredField(INIT_FIELD_NAME);
                resourceManagerInitField.setAccessible(true);

                ObserverThread thread = instance.createObserverThread();
                thread.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        return instance;
    }

    private ObserverThread createObserverThread() {
        return new ObserverThread();
    }

    /**
     * Has {@link ServerCore} been properly initialised
     *
     * @return true / false depending on initialisation state
     */
    public boolean isServerCoreInitialised() {
        try {
            return (Boolean) resourceManagerInitField.get(ResourceManager.class); 
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Add an initialisation listener
     *
     * @param listener
     */
    public void addParentServerListener(IParentServerMonitorListener listener) {
        if (isServerCoreInitialised()) {
            // No need to listen since everything is initialised
            listener.serversInitialised();
            return;
        }

        listeners.add(listener);
    }

    /**
     * Remove an initialisation listener
     *
     * @param listener
     */
    public void removeParentServerListener(IParentServerMonitorListener listener) {
        listeners.remove(listener);
    }
}
