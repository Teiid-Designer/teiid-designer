/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml;

import org.eclipse.ui.IWorkbenchPage;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.ui.AbstractUiPlugin;
import com.metamatrix.ui.actions.ActionService;

/**
 * The main plugin class to be used in the desktop.
 */
public class XmlImporterUiPlugin extends AbstractUiPlugin
// implements IInternalUiConstants {
{
    // The shared instance.
    private static XmlImporterUiPlugin plugin;
    // Resource bundle.

    public static final String C_threshold = "c_Threshold"; //$NON-NLS-1$
    public static final String P_threshold = "p_Threshold"; //$NON-NLS-1$
    public static final String F_threshold = "f_Threshold"; //$NON-NLS-1$
    public static final String requestTable = "requestTable"; //$NON-NLS-1$
    public static final String mergedChildSep = "mergedChildSep"; //$NON-NLS-1$
    public static final String xsdLibrary	 = "xsdLibrary"; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log, internationalized properties, and debugger.
     * 
     * @since 4.0
     */
    private Util util;

    /**
     * The constructor.
     */
    public XmlImporterUiPlugin() {
        // Even though we would like to call the single argument constructor of PluginUtilImpl,
        // since we are following the naming guideline, we can't because it is in another
        // classloader and so won't find the resource bundle.
        // We can't even use the two argument constructor because it appears to have a bug that
        // causes the bundle name to be i18n.i18n instead of <pluginid>.i18n
        // util = new PluginUtilImpl(IUiConstants.PLUGIN_ID, ResourceBundle.getBundle(IUiConstants.I18N_NAME));
        util = new Util();
        plugin = this;
    }

    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        // Initialize logging/i18n utility
        ((PluginUtilImpl)util).initializePlatformLogger(this);
    }

    /**
     * <p>
     * </p>
     * 
     * @see com.metamatrix.ui.AbstractUiPlugin#createActionService(org.eclipse.ui.IWorkbenchPage)
     * @since 4.0
     */
    @Override
    protected ActionService createActionService( IWorkbenchPage page ) {
        return null;
    }

    /**
     * @see com.metamatrix.ui.AbstractUiPlugin#getPluginUtil()
     * @since 4.0
     */
    @Override
    public PluginUtil getPluginUtil() {
        return util;
    }

    /**
     * Returns the shared instance.
     * 
     * @since 4.0
     */
    public static XmlImporterUiPlugin getDefault() {
        return plugin;
    }
}
