/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * The main plugin class to be used in the desktop.
 */
public class WebServiceMetamodelPlugin extends Plugin {
    public static final String PLUGIN_ID = "org.teiid.designer.metamodels.webservice"; //$NON-NLS-1$

    public static final String PACKAGE_ID = WebServiceMetamodelPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
        public URL getBaseURL() {
            if (INSTANCE != null) {
                URL baseUrl;
                try {
                    baseUrl = FileLocator.resolve(INSTANCE.getBundle().getEntry("/")); //$NON-NLS-1$
                } catch (final IOException err) {
                    baseUrl = null;
                }
                return baseUrl;
            }
            try {
                final URI uri = URI.createURI(getClass().getResource("plugin.properties").toString()); //$NON-NLS-1$
                final URL baseUrl = new URL(uri.trimSegments(1).toString() + "/"); //$NON-NLS-1$
                return baseUrl;
            } catch (IOException exception) {
                throw new WrappedException(exception);
            }
        }

        public Object getImage( String key ) {
            try {
                final URL baseUrl = getBaseURL();
                final URL url = new URL(baseUrl + "icons/" + key + ".gif"); //$NON-NLS-1$//$NON-NLS-2$
                InputStream inputStream = url.openStream();
                inputStream.close();
                return url;
            } catch (MalformedURLException exception) {
                throw new WrappedException(exception);
            } catch (IOException exception) {
                throw new MissingResourceException(
                                                   CommonPlugin.INSTANCE.getString("_UI_StringResourceNotFound_exception", new Object[] {key}), //$NON-NLS-1$
                                                   getClass().getName(), key);
            }
        }

        public String getString( String key ) {
            return Util.getString(key);
        }

        public String getString( String key,
                                 Object[] substitutions ) {
            return Util.getString(key, substitutions);
        }

        public String getString( final String key,
                                 final boolean translate ) {
            return getString(key);
        }

        public String getString( final String key,
                                 final Object[] substitutions,
                                 final boolean translate ) {
            return getString(key, substitutions);
        }
    };

    /**
     * Called by the {@link com.metamatrix.metamodels.transformation.provider.TransformationEditPlugin}
     * 
     * @return the EMF ResourceLocator used when run as a plugin
     */
    public static ResourceLocator getPluginResourceLocator() {
        return RESOURCE_LOCATOR;
    }

    static WebServiceMetamodelPlugin INSTANCE = null;

    /**
     * Construct an instance of MetaMatrixPlugin.
     */
    public WebServiceMetamodelPlugin() {
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        WebServiceMetamodelPlugin.INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    }
}
