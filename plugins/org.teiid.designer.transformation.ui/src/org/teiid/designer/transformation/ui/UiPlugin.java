/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.transformation.ui;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.diagram.ui.DiagramUiPlugin;
import org.teiid.designer.transformation.TransformationPlugin;
import org.teiid.designer.transformation.ui.util.TransformationNotificationListener;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.PreferenceKeyAndDefaultValue;
import org.teiid.designer.ui.common.actions.ActionService;
import org.teiid.designer.ui.viewsupport.ModelUtilities;


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
    }

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static UiPlugin getDefault() {
        return UiPlugin.plugin;
    }

    private TransformationNotificationListener notificationListener = new TransformationNotificationListener();

    private void init() {
        TransformationPlugin.getDefault();
        ModelUtilities.addNotifyChangedListener(notificationListener);
    }

    public void setIgnoreTransformationNotifications( boolean ignoreNotifications ) {
        if (this.notificationListener != null) {
            this.notificationListener.setIgnoreNotifications(ignoreNotifications);
        }
    }

    // ============================================================================================================================
    // AbstractUiPlugin Methods

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)Util).initializePlatformLogger(this);

        storeDefaultPreferenceValues();
        init();
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

    // =========================================================================================================
    // Instance methods
    private void storeDefaultPreferenceValues() {
        // -----------------------------------------------------------
        // Diagram-related prefs stored in diagram.ui plugin prefs
        // -----------------------------------------------------------
        // Store default values of preferences. Needs to be done once. Does not change current
        // values of preferences if any are already stored.
        IPreferenceStore preferenceStore = DiagramUiPlugin.getDefault().getPreferenceStore();

        for (int i = 0; i < PluginConstants.Prefs.Appearance.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, PluginConstants.Prefs.Appearance.PREFERENCES[i]);
        }

        DiagramUiPlugin.getDefault().savePreferences();

        // -----------------------------------------------------------
        // prefs for this plugin
        // -----------------------------------------------------------
        // Store default values of preferences. Needs to be done once. Does not change current
        // values of preferences if any are already stored.
        preferenceStore = getDefault().getPreferenceStore();

        for (int i = 0; i < PluginConstants.Prefs.Reconciler.PREFERENCES.length; i++) {
            PreferenceKeyAndDefaultValue.storePreferenceDefault(preferenceStore, PluginConstants.Prefs.Reconciler.PREFERENCES[i]);
        }

        getDefault().savePreferences();
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
        ModelUtilities.removeNotifyChangedListener(notificationListener);
        super.stop(context);
    }
    
    public String getString(final String prefix, final String id) {
        return Util.getString(prefix + id);
    }

}
