/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.mapping.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.mapping.ui.util.MappingNotificationListener;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;
import org.teiid.designer.ui.common.actions.ActionService;


/**
 * The main plugin class to be used in the desktop.
 *
 * @since 8.0
 */
public class UiPlugin extends AbstractUiPlugin implements UiConstants {
    // The shared instance.
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
     * 
     * @since 4.0
     */
    public static UiPlugin getDefault() {
        return UiPlugin.plugin;
    }

    private MappingNotificationListener notificationListener = new MappingNotificationListener();

    private void init() {
        try {
            ModelerCore.getModelContainer().getChangeNotifier().addListener(notificationListener);
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    // ============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 5.0
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        storeDefaultPreferenceValues();
    }

    // ============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    public ActionService getActionService( IWorkbenchPage page ) {
        // This plugin uses the DiagramActionService
        return DiagramUiPlugin.getDefault().getActionService(page);
    }

    /**
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.teiid.designer.ui.common.AbstractUiPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        ModelerCore.getModelContainer().getChangeNotifier().removeListener(notificationListener);
        super.stop(context);
    }

    // =========================================================================================================
    // Instance methods
    private void storeDefaultPreferenceValues() {
        // Store default values of preferences. Needs to be done once. Does not change current
        // values of preferences if any are already stored.
        IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();

        for (int i = 0; i < PluginConstants.Prefs.Appearance.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, PluginConstants.Prefs.Appearance.PREFERENCES[i]);
        }

        for (int i = 0; i < PluginConstants.Prefs.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, PluginConstants.Prefs.PREFERENCES[i]);
        }

        DiagramUiPlugin.getDefault().savePreferences();

    }

    @Override
    public IPreferenceStore getPreferenceStore() {
        return DiagramUiPlugin.getDefault().getPreferenceStore();
    }
}
