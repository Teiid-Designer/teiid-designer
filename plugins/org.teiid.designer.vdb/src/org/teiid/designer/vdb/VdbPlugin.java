package org.teiid.designer.vdb;

import java.util.ResourceBundle;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;


/**
 * The activator class controls the plug-in life cycle
 *
 * @since 8.0
 */
public class VdbPlugin extends Plugin implements VdbConstants {

    private static final String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

    /**
     * This plug-in's utility for logging and internationalization
     */
    public static final PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /**
     * The singleton instance of this plug-in
     */
    private static VdbPlugin singleton;

    /**
     * @return the singleton instance of this type
     */
    public static VdbPlugin singleton() {
        return singleton;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        singleton = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        singleton = null;
        super.stop(context);
    }

    /**
     * Obtains the current plugin preferences values.
     *
     * @return the preferences (never <code>null</code>)
     */
    public IEclipsePreferences getPreferences() {
        return this.getPreferences(PLUGIN_ID);
    }

    /**
     * Obtains the current plugin preferences values for the given plugin id
     *
     * @param pluginId
     *
     * @return the preferences (never <code>null</code>) for the given plugin id
     */
    public IEclipsePreferences getPreferences(String pluginId) {
        return InstanceScope.INSTANCE.getNode(pluginId);
    }

    /**
     * @return whether a vdb conversion is currently taking place
     */
    public boolean conversionInProgress() {
        return getPreferences().getBoolean(VdbConstants.CONVERSION_IN_PROGRESS, Boolean.FALSE);
    }

    /**
     * @param value
     * @throws Exception
     */
    public void setConversionInProgress(boolean value) throws Exception {
        try {
            IWorkspace workspace = ModelerCore.getWorkspace();
            if (workspace == null)
                return;

            IWorkspaceDescription description = workspace.getDescription();
            if (description == null)
                return;

            description.setAutoBuilding(! value);
            workspace.setDescription(description);
        } finally {
            getPreferences().putBoolean(VdbConstants.CONVERSION_IN_PROGRESS, value);
        }
    }
}
