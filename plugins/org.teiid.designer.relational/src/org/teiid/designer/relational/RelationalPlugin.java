package org.teiid.designer.relational;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * The activator class controls the plug-in life cycle
 */
public class RelationalPlugin  extends Plugin {

    // =========================================================
    // Static

    public static RelationalPlugin INSTANCE;

    public static RelationalPlugin getDefault() {
        return INSTANCE;
    }
    
    // =========================================================
    // Constructor

    /**
     * Construct an instance of RelationshipPlugin.
     */
    public RelationalPlugin() {
        INSTANCE = this;
    }
    
    /**
     * The plug-in identifier of this plugin
     */
    public static final String PLUGIN_ID = "org.teiid.designer.relational"; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = RelationalPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));

    public static final boolean DEBUG = false;


     /**
     * <p>
     * {@inheritDoc}
     * </p>
     *
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }
}