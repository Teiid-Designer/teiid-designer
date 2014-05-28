/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
*/
package org.jboss.ide.eclipse.as.storage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * Activator class for this plugin
 */
public class StoragePlugin extends Plugin {

    /**
     * The plug-in identifier of this plugin
     */
    public static final String PLUGIN_ID = StoragePlugin.class.getPackage().getName();

    // The shared instance
    private static StoragePlugin plugin;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static StoragePlugin getDefault() {
        return plugin;
    }


    /**
     * @param status
     */
    public static void log(IStatus status) {
        getDefault().getLog().log(status);
    }

    /**
     * @param severity
     * @param message
     * @param e
     */
    public static void log(int severity, String message, Throwable e) {
        log(new Status(severity, PLUGIN_ID, 0, message, e));
    }
    

    /**
     * @param e
     */
    public static void log(Throwable e) {
        log(e.getMessage(), e);
    }

    /**
     * @param message
     * @param e
     */
    public static void log(String message, Throwable e) {
        log(IStatus.ERROR, message, e);
    }

}
