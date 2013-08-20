/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.core.loading;

import java.util.Properties;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.runtime.spi.ITeiidServerManager;

/**
 * Singleton to manager the loading of editors implementing the {@link IManagedLoading} interface.
 *
 * Specifically, such editors are dependent upon the full loading of the {@link ITeiidServerManager} either
 * directly or indirectly. As such, it is imperative that these editors await the loading of their contents
 * until the latter has completed its restoration.
 */
public class ComponentLoadingManager {

    private class LoadingThread extends Thread {

        private final IManagedLoading component;
        private final Properties args;

        public LoadingThread(final IManagedLoading component, Properties args) {
            super(component.getClass().getSimpleName() + "." + LoadingThread.class.getSimpleName()); //$NON-NLS-1$
            this.component = component;
            this.args = args;
            setDaemon(true);
        }

        @Override
        public void run() {
            ITeiidServerManager serverManager = ModelerCore.getTeiidServerManager();
            while(! serverManager.isStarted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ModelerCore.Util.log(ex);
                }
            }

            component.manageLoad(args);
        }
    }

    private static ComponentLoadingManager instance;

    /**
     * @return singleton instance of this manager
     */
    public static ComponentLoadingManager getInstance() {
        if (instance == null) {
            instance = new ComponentLoadingManager();
        }

        return instance;
    }

    /**
     * Begin a managed loading of the given editor where its {@link IManagedLoading#manageLoad(Properties)} will
     * be delayed until the {@link ITeiidServerManager} instance has been fully loaded.
     *
     * @param component
     * @param args
     */
    public void manageLoading(final IManagedLoading component, Properties args) {
        if (ModelerCore.getTeiidServerManager().isStarted()) {
            component.manageLoad(args);
            return;
        }

        LoadingThread thread = new LoadingThread(component, args);
        thread.start();
    }

    /**
     * Begin a managed loading of the given component where its {@link IManagedLoading#manageLoad(Properties)} will
     * be delayed until the {@link ITeiidServerManager} instance has been fully loaded.
     *
     * @param component
     */
    public void manageLoading(final IManagedLoading component) {
        manageLoading(component, new Properties());
    }
}
