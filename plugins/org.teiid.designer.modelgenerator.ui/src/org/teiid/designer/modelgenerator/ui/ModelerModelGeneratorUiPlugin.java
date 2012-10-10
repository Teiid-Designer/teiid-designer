/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.modelgenerator.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;
import org.teiid.designer.ui.common.actions.ActionService;

/**
 * The main plugin class to be used in the desktop.
 *
 * @since 8.0
 */
public class ModelerModelGeneratorUiPlugin extends AbstractUiPlugin implements ModelGeneratorUiConstants{
	//The shared instance.
	private static ModelerModelGeneratorUiPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public ModelerModelGeneratorUiPlugin() {
		plugin = this;
	}

    /**
     * Returns the shared instance.
     * @since 4.0
     */
    public static ModelerModelGeneratorUiPlugin getDefault() {
        return ModelerModelGeneratorUiPlugin.plugin;
    }

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ModelerCore.getWorkspace();
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
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return null;
    }
    
    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }
    
    
    //=========================================================================================================
    // Instance methods
    private void storeDefaultPreferenceValues() {
        //-----------------------------------------------------------
        // prefs for this plugin 
        //-----------------------------------------------------------
        //Store default values of preferences.  Needs to be done once.  Does not change current
        //values of preferences if any are already stored.
        IPreferenceStore preferenceStore = getDefault().getPreferenceStore();

        for (int i = 0; i < PluginConstants.Prefs.ModelGenerator.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore,
                    PluginConstants.Prefs.ModelGenerator.PREFERENCES[i]);
        }

        getDefault().savePreferences();
    }
}
