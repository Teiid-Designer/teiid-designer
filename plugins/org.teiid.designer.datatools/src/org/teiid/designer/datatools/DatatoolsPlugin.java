package org.teiid.designer.datatools;

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
public class DatatoolsPlugin extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.teiid.designer.datatools"; //$NON-NLS-1$

    /**
     * The package identifier.
     */
    public static final String PACKAGE_ID = DatatoolsPlugin.class.getPackage().getName();
    
    /**
     * The name of the I18n properties file.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    
    /**
     * Provides access to the plugin's log and to it's resources.
     * 
     * @since 4.2.1
     */
    public static PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));
    
	// The shared instance
	private static DatatoolsPlugin plugin;
	
	/**
	 * The constructor
	 */
	public DatatoolsPlugin() {
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
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
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

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DatatoolsPlugin getDefault() {
		return plugin;
	}

}
