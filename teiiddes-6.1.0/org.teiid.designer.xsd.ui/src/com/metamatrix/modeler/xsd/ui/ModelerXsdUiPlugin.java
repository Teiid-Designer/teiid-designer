/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.xsd.ui;

import org.osgi.framework.BundleContext;

import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.CoreConstants.Debug;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * ModelerXsdUiPlugin
 */
public class ModelerXsdUiPlugin extends AbstractUiPlugin implements Debug, ModelerXsdUiConstants {

    //============================================================================================================================
    // Static Variables
    
    //The shared instance.
    private static ModelerXsdUiPlugin plugin;


    /**
     * Returns the shared instance.
     * @since 4.0
     */
    public static ModelerXsdUiPlugin getDefault() {
        return ModelerXsdUiPlugin.plugin;
    }


    /**
     * Construct an instance of XmlUiPlugin.
     */
    public ModelerXsdUiPlugin() {
        ModelerXsdUiPlugin.plugin = this;
    }

    //============================================================================================================================
    // AbstractUIPlugin Methods

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        Util.debug(PLUGIN_ACTIVATION, "Plug-in " + getClass().getName() + " activated."); //$NON-NLS-1$ //$NON-NLS-2$
        super.start(context);
        
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
        
    }

    //============================================================================================================================
    // AbstractUiPlugin Methods

    /**
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
