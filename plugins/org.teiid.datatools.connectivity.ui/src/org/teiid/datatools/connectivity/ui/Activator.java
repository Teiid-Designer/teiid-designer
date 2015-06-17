/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.connectivity.ui;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @since 8.0
 */
public class Activator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "org.teiid.datatools.connectivity.ui"; //$NON-NLS-1$

	public static final String CLOSED_FOLDER_ID = "icons/closedFolder.gif"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
        
        // initialize preferences
        initializeDefaultPreferences();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    /**
     * Retrieves the image descriptor associated with the specified key from the {@link org.eclipse.jface.resource.ImageRegistry
     * image registry}, creating the descriptor and registering it if it doesn't already exist. A null key will cause the
     * descriptor for the "No image" image to be returned.
     * 
     * @param key The key associated with the image descriptor to retrieve. This must be in the form of the path to the image file
     *        relative to this plug-in's folder; may be null.
     * @return The image descriptor associated with the specified key.
     * @since 4.0
     */
    public final ImageDescriptor getImageDescriptor( final String key ) {
        final ImageRegistry registry = getImageRegistry();
        final ImageDescriptor descriptor = registry.getDescriptor(key);
        if (descriptor != null) {
            return descriptor;
        }
        return createImageDescriptor(key);
    }

    /**
     * @since 4.0
     */
    private ImageDescriptor createImageDescriptor( final String key ) {
        try {
            final URL url = new URL(getBundle().getEntry("/").toString() + key); //$NON-NLS-1$
            final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
            final ImageRegistry registry = getImageRegistry();
            registry.put(key, descriptor);
            return descriptor;
        } catch (final MalformedURLException err) {
            plugin.log(err);
            return null;
        }
    }

    @Override
	protected void initializeImageRegistry(ImageRegistry registry) {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath path = new Path("icons/closedFolder.gif"); //$NON-NLS-1$
        URL url = Platform.find(bundle, path);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        registry.put(CLOSED_FOLDER_ID, desc);
     }
    

    /**
     * Log the given exception 
     * 
     * @param exception
     */
    public static void log(Throwable exception) {
        IStatus status = new Status(
                                    IStatus.ERROR, 
                                    PLUGIN_ID,
                                    PLUGIN_ID,
                                    exception);
        
        plugin.getLog().log(status);
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
    
    private void initializeDefaultPreferences() {
        IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(getDefault().getBundle().getSymbolicName());

        // initialize the Teiid cleanup enabled preference
        prefs.putBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED, PreferenceConstants.TEIID_QUERYPLANS_ENABLED_DEFAULT);

        // initialize the Teiid cleanup enabled preference
        prefs.putBoolean(PreferenceConstants.TEIID_QUERYPLANS_ENABLED,
                         PreferenceConstants.TEIID_QUERYPLANS_ENABLED_DEFAULT);

    }

}
