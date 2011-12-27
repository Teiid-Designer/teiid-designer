/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.LoggingUtil;

public class ExtensionPlugin extends Plugin {

    /**
     * This plugin's identifier.
     */
    public static final String PLUGIN_ID = "org.teiid.designer.extension"; //$NON-NLS-1$

    /**
     * The package identifier.
     */
    public static final String PACKAGE_ID = ExtensionPlugin.class.getPackage().getName();
    
    private static IPath runtimePath;

    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.2.1
     */
    public static PluginUtil Util = new LoggingUtil(PLUGIN_ID);

    /**
     * The shared instance.
     */
    private static ExtensionPlugin plugin;

    /**
     * @return DqpPlugin
     * @since 4.3
     */
    public static ExtensionPlugin getInstance() {
        return plugin;
    }


    /**
     * Obtains the current plubin preferences values. <strong>This method should be used instead of
     * {@link Plugin#getPluginPreferences()}.</strong>
     * 
     * @return the preferences (never <code>null</code>)
     */
    public IEclipsePreferences getPreferences() {
        return new InstanceScope().getNode(PLUGIN_ID);
    }
    
    /**
     * @return the <code>designer.dqp</code> plugin's runtime workspace path or the test runtime path
     * @throws IOException if an error occurs obtaining the path
     * @since 6.0.0
     */
    public IPath getRuntimePath() {
        if (runtimePath == null) {
            runtimePath = ExtensionPlugin.getInstance().getStateLocation();
        }

        return (IPath)runtimePath.clone();
    }

    private void initializeDefaultPreferences() {
//        IEclipsePreferences prefs = new DefaultScope().getNode(DqpPlugin.getInstance().getBundle().getSymbolicName());
//
//        // initialize the Teiid cleanup enabled preference
//        prefs.putBoolean(PreferenceConstants.PREVIEW_ENABLED, PreferenceConstants.PREVIEW_ENABLED_DEFAULT);
//
//        // initialize the Teiid cleanup enabled preference
//        prefs.putBoolean(PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED,
//                         PreferenceConstants.PREVIEW_TEIID_CLEANUP_ENABLED_DEFAULT);

    }

    /**
     * Option names can be found in the <code>.debug</code> file and in {@link DebugConstants}.
     * 
     * @param option the option being checked
     * @return <code>true</code> if in debugging mode and the debug option is enabled
     */
    public boolean isDebugOptionEnabled( String option ) {
        return (isDebugging() && Boolean.toString(true).equals(Platform.getDebugOption(option)));
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;

        // initialize logger first so that other methods can use logger
        ((LoggingUtil)Util).initializePlatformLogger(this);

        // initialize preferences
        initializeDefaultPreferences();

    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        super.stop(context);
    }

}
