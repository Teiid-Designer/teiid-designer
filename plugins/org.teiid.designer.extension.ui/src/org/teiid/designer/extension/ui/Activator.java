/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension.ui;

import static org.teiid.designer.extension.ui.UiConstants.UTIL;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * 
 */
public final class Activator extends AbstractUiPlugin {

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

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return null;
    }

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
        ((LoggingUtil)UTIL).initializePlatformLogger(this);
    }

}
