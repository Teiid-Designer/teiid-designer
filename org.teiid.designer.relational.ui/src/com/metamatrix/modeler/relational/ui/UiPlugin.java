/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relational.ui;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * The main plugin class to be used in the desktop.
 */
public class UiPlugin extends AbstractUiPlugin implements UiConstants {
	//The shared instance.
	private static UiPlugin plugin;
    
	/**
	 * The constructor.
	 */
	public UiPlugin() {
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 * @since 4.0
	 */
	public static UiPlugin getDefault() {
		return UiPlugin.plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
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
        storeDefaultPreferenceValues();
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
    
//	/* (non-Javadoc)
//	 * @see org.eclipse.core.runtime.Plugin#shutdown()
//	 */
//	public void shutdown() throws CoreException {
//		super.shutdown();
//	}
    
	//=========================================================================================================
	// Instance methods
	private void storeDefaultPreferenceValues() {
		// no work yet
	}
}
