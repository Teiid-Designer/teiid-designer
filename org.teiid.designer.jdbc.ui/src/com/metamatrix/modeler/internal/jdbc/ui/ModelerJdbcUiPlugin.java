/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.jdbc.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * The main plugin class to be used in the desktop.
 * @since 4.0
 */
public final class ModelerJdbcUiPlugin extends AbstractUiPlugin
implements InternalModelerJdbcUiPluginConstants {
    //============================================================================================================================
    // Static Variables
    
    //The shared instance.
    private static ModelerJdbcUiPlugin INSTANCE;

    //============================================================================================================================
    // Static Methods

    /**
     * Returns the shared instance.
     * @since 4.0
     */
    public static ModelerJdbcUiPlugin getDefault() {
        return ModelerJdbcUiPlugin.INSTANCE;
    }

    //============================================================================================================================
    // Constructors
        
    /**
     * The constructor.
     * @since 4.0
     */
    public ModelerJdbcUiPlugin() {
    	INSTANCE = this;
    }

    //============================================================================================================================
    // AbstractUIPlugin Methods

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    //============================================================================================================================
    // AbstractUiPlugin Methods

    /**<p>
	 * </p>
	 * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
	 * @since 4.0
	 */
	@Override
    protected ActionService createActionService(IWorkbenchPage page) {
		return null;
	}
    
    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }
}
