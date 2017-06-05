/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.dataservices.ui;

import static org.teiid.designer.dataservices.ui.UiConstants.UTIL;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.LoggingUtil;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;


/**
 * The Plugin activator
 *
 * @since 8.1
 */
public class UiPlugin extends AbstractUiPlugin {
    /**
     * The shared instance.
     */
    private static UiPlugin _plugin;

    private static IPath runtimePath;

    /**
     * @return the shared instance or <code>null</code> if the Eclipse platform is not running
     */
    public static UiPlugin getDefault() {
        return _plugin;
    }

    // ============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        _plugin = this;

        // initialize logger first so that other methods can use logger
        ((LoggingUtil)UTIL).initializePlatformLogger(this);  
    }
    
    /**
     * {@inheritDoc}
     * <p>
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        super.stop(context);
    }

    /**
     * @return the <code>designer.importer.ui</code> plugin's runtime workspace path or the test runtime path
     */
    public IPath getRuntimePath() {
        if (runtimePath == null) {
            runtimePath = getDefault().getStateLocation();
        }

        return (IPath)runtimePath.clone();
    }

    // ============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return null;
    }
    
}
