/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.query.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import org.teiid.core.CoreConstants.Debug;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.query.ui.actions.QueryActionService;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.PreferenceKeyAndDefaultValue;
import com.metamatrix.ui.actions.ActionService;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @since 4.0
 */
public final class UiPlugin extends AbstractUiPlugin implements Debug, UiConstants {

    // The shared instance.
    private static UiPlugin plugin;

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static UiPlugin getDefault() {
        return UiPlugin.plugin;
    }

    /**
     * The constructor.
     * 
     * @since 4.0
     */
    public UiPlugin() {
        UiPlugin.plugin = this;
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        storeDefaultPreferenceValues();
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    /**
     * SqlEditor doesn't utilize an ActionService internally. Just return AbstractActionService instance.
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return new QueryActionService(page);
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

    private void storeDefaultPreferenceValues() {
        // Store default values of preferences. Needs to be done once. Does not change current
        // values of preferences if any are already stored.
        IPreferenceStore preferenceStore = UiPlugin.getDefault().getPreferenceStore();
        for (int i = 0; i < UiConstants.Prefs.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, UiConstants.Prefs.PREFERENCES[i]);
        }
        UiPlugin.getDefault().savePluginPreferences();
    }
}
