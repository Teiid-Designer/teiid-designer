/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * The main plugin class to be used in the desktop.
 */
public class ToolsPlugin extends Plugin {

	// The shared instance.
	private static ToolsPlugin plugin;

    /**
     * The plug-in identifier of this plugin
     * (value <code>"com.metamatrix.modeler.schema.tools"</code>).
     */
    public static final String PLUGIN_ID = "org.teiid.designer.schema.tools" ; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = ToolsPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));
    
	/**
	 * The constructor.
	 */
	public ToolsPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
		// This must be called to initialize the platform logger!
		((PluginUtilImpl)Util).initializePlatformLogger(this);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ToolsPlugin getDefault() {
		return plugin;
	}

}
