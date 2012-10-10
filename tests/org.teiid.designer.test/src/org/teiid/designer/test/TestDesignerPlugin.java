/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.test;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.ui.common.UiConstants;

public class TestDesignerPlugin extends Plugin {
    
    private static String ID = "org.teiid.designer.test"; //$NON-NLS-1$
    
    private static TestDesignerPlugin instance;
    
    private BundleContext bundleContext;

    public static TestDesignerPlugin getPlugin() {
        return instance;
    }
    
    /**
     * @return bundleContext
     */
    public BundleContext getBundleContext() {
        return bundleContext;
    }
    
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        instance = this;
        bundleContext = context;
        ((PluginUtilImpl)CoreModelerPlugin.Util).initializePlatformLogger(instance);
        ((PluginUtilImpl)UiConstants.Util).initializePlatformLogger(instance);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        instance = null;
        bundleContext = null;
        super.stop(context);
    }

    /**
     * @param msg
     */
    public static void logInfo(String msg) {
        IStatus status = new Status(IStatus.INFO, ID, msg);
        getPlugin().getLog().log(status);
    }

    /**
     * @param msg
     */
    public static void logWarning(String msg) {
        IStatus status = new Status(IStatus.WARNING, ID, msg);
        getPlugin().getLog().log(status);
    }

    /**
     * @param ex
     */
    public static void logException(Exception ex) {
        IStatus status = new Status(IStatus.ERROR, ID, "Plugin " + ID + " threw an exception", ex); //$NON-NLS-1$ //$NON-NLS-2$
        getPlugin().getLog().log(status);
    }
}
