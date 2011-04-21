/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.roles;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

public class RolesPlugin extends Plugin {

    public static final String PLUGIN_ID = RolesPlugin.class.getPackage().getName();

    private static final String I18N_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$

    static final PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static RolesPlugin plugin;

    public static RolesPlugin getInstance() {
        return RolesPlugin.plugin;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;
        ((PluginUtilImpl)UTIL).initializePlatformLogger(this);
        // Initialize the use of user-defined functions
    }

}
