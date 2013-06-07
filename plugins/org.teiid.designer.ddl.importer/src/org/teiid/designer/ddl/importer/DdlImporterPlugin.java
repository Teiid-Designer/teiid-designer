package org.teiid.designer.ddl.importer;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.PluginUtilImpl;


/**
 * The activator class controls the plug-in life cycle
 *
 * @since 8.0
 */
public class DdlImporterPlugin extends Plugin {

    /**
     * This plug-in's ID
     */
    public static final String ID = DdlImporterPlugin.class.getPackage().getName();

    private static final String I18N_NAME = ID + ".i18n"; //$NON-NLS-1$

    /**
     * This plug-in's utility for logging and internationalization
     */
    public static final PluginUtil UTIL = new PluginUtilImpl(ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /**
     * The singleton instance of this plug-in
     */
    private static DdlImporterPlugin singleton;

    /**
     * Not intended for use by any class other than {@link DdlImporterI18n}.
     * 
     * @param key
     * @return The i18n template associated with the supplied key
     */
    static String i18n( final String key ) {
        return UTIL.getString(key);
    }

    /**
     * Not intended for use by any class other than {@link DdlImporterI18n}.
     * 
     * @param key
     * @param parameters
     * @return The i18n template associated with the supplied key
     */
    static String i18n( final String key, Object... parameters) {
        return UTIL.getString(key, parameters);
    }

    /**
     * @return the singleton instance of this type
     */
    public static DdlImporterPlugin singleton() {
        return singleton;
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        singleton = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        singleton = null;
        super.stop(context);
    }
}
