/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.tools.textimport.ui;

import org.osgi.framework.BundleContext;

import org.eclipse.ui.IWorkbenchPage;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;


/**
 * The main plugin class to be used in the desktop.
 */
/**
 * The main plugin class to be used in the desktop.
 */
public class TextImportPlugin extends AbstractUiPlugin implements UiConstants {
    //The shared instance.
    private static TextImportPlugin plugin;
    
    /**
     * The constructor.
     */
    public TextImportPlugin() {
        plugin = this;
    }

    /**
     * Returns the shared instance.
     */
    public static TextImportPlugin getDefault() {
        return TextImportPlugin.plugin;
    }

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
        
        init();
    }
    
    private void init() {
        // does nothing yet
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
     * @see com.metamatrix.ui.AbstractUiPlugin#getActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    public ActionService getActionService(IWorkbenchPage page) {
        return TextImportPlugin.getDefault().getActionService(page);
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
