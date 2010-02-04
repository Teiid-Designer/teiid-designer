/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.diagram.ui.DiagramUiPlugin;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;
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
		init();
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
	
	private void init() {

	}
    
	//============================================================================================================================
	// AbstractUiPlugin Methods

    /** 
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start(BundleContext context) throws Exception {
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
	 * @see com.metamatrix.ui.AbstractUiPlugin#getActionService(org.eclipse.ui.IWorkbenchPage)
	 * @since 4.0
	 */
	@Override
    public ActionService getActionService(IWorkbenchPage page) {
		// This plugin uses the DiagramActionService
		return DiagramUiPlugin.getDefault().getActionService(page);
	}
    
	/**
	 * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
	 * @since 4.0
	 */
	@Override
    public PluginUtil getPluginUtil() {
		return Util;
	}
    
	//=========================================================================================================
	// Instance methods
	private void storeDefaultPreferenceValues() {
		//Store default values of preferences.  Needs to be done once.  Does not change current
		//values of preferences if any are already stored.
		IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();

		for (int i = 0; i < PluginConstants.Prefs.Appearance.PREFERENCES.length; i++) {
			PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore,
					PluginConstants.Prefs.Appearance.PREFERENCES[i]);
		}

		DiagramUiPlugin.getDefault().savePluginPreferences();
	}
}
