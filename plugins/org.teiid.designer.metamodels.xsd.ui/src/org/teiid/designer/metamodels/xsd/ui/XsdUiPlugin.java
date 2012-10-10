/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xsd.ui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.designer.core.ModelerCore;
import org.teiid.designer.metamodels.xsd.ui.editor.XsdEditor;
import org.teiid.designer.ui.common.AbstractUiPlugin;
import org.teiid.designer.ui.common.actions.ActionService;


/**
 * RelationalPlugin
 *
 * @since 8.0
 */
public class XsdUiPlugin extends AbstractUiPlugin {
    public static final String PLUGIN_ID = "org.teiid.designer.metamodels.xsd.ui" ; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = XsdUiPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
        @Override
		public URL getBaseURL() {
            if ( INSTANCE != null ) {
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
        @Override
		public Object getImage(String key) {
            try {
                final URL baseUrl = getBaseURL();
                final URL url = new URL( baseUrl + "icons/" + key + ".gif");  //$NON-NLS-1$//$NON-NLS-2$
                InputStream inputStream = url.openStream();
                inputStream.close();
                return url;
            } catch (MalformedURLException exception) {
                throw new WrappedException(exception);
            } catch (IOException exception) {
                throw
                new MissingResourceException
                  (CommonPlugin.INSTANCE.getString("_UI_StringResourceNotFound_exception", new Object [] { key }), //$NON-NLS-1$
                   getClass().getName(),
                   key);
            }
        }
        @Override
		public String getString(String key) {
            return Util.getString(key);
        }

        @Override
		public String getString(String key, Object[] substitutions) {
            return Util.getString(key,substitutions);
        }

        @Override
		public String getString(final String key, final boolean translate) {
            return getString(key);
        }

        @Override
		public String getString(final String key, final Object[] substitutions, final boolean translate) {
            return getString(key, substitutions);
        }
    };
    /**
     * @return the EMF ResourceLocator used when run as a plugin
     */
    public static ResourceLocator getPluginResourceLocator() {
        return RESOURCE_LOCATOR;
    }

    public static boolean DEBUG = false;
    public static XsdUiPlugin INSTANCE;

    /**
     * Construct an instance of MetaMatrixPlugin.
     */
    public XsdUiPlugin() {
    }

    /**
     * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        XsdUiPlugin.INSTANCE = this;
        XsdEditor.setResourceSet(ModelerCore.getModelContainer() );
        ((PluginUtilImpl)Util).initializePlatformLogger(this);   // This must be called to initialize the platform logger!
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     */
    @Override
    protected ActionService createActionService(IWorkbenchPage page) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     */
    @Override
    public PluginUtil getPluginUtil() {
        return Util;
    }


}
