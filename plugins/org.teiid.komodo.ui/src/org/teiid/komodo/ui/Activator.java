/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.komodo.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.komodo.ui.common.AbstractUiPlugin;
import org.teiid.komodo.ui.common.ActionService;


/**
 * The main plugin class to be used in the desktop.
 * 
 * @since 1.0
 */
public final class Activator extends AbstractUiPlugin implements UiConstants {

    /**
     * The shared instance.
     */
    private static Activator _plugin;

    /**
     * @return the shared instance or <code>null</code> if the Eclipse platform is not running
     */
    public static Activator getDefault() {
        return _plugin;
    }

    /* (non-Javadoc)
     * @see org.teiid.komodo.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        // TODO Auto-generated method stub
        return null;
    }
//    /**
//     * {@inheritDoc}
//     * 
//     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
//     */
//    @Override
//    public PluginUtil getPluginUtil() {
//        return null;
//    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        _plugin = this;

        // initialize logger first so that other methods can use logger
        //((LoggingUtil)UTIL).initializePlatformLogger(this);
    }

}
