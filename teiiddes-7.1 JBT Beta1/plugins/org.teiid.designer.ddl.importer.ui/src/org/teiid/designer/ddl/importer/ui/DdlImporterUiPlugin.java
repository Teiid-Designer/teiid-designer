package org.teiid.designer.ddl.importer.ui;

import java.util.ResourceBundle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class DdlImporterUiPlugin extends AbstractUIPlugin {

    /**
     * This plug-in's ID
     */
    public static final String ID = DdlImporterUiPlugin.class.getPackage().getName();

    private static final String I18N_NAME = ID + ".i18n"; //$NON-NLS-1$

    /**
     * This plug-in's utility for logging and internationalization
     */
    private static final PluginUtil UTIL = new PluginUtilImpl(ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /**
     * The singleton instance of this plug-in
     */
    private static DdlImporterUiPlugin singleton;

    /**
     * Not intended for use by any class other than {@link DdlImporterUiI18n}.
     * 
     * @param key
     * @return The i18n template associated with the supplied key
     */
    static String i18n( final String key ) {
        return UTIL.getString(key);
    }

    static ImageDescriptor imageDescriptor( final String name ) {
        return imageDescriptorFromPlugin(ID, "icons/" + name); //$NON-NLS-1$
    }

    /**
     * @return the singleton instance of this type
     */
    static DdlImporterUiPlugin singleton() {
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
