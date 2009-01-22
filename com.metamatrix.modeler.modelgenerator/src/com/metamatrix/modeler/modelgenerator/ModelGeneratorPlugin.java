/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator;

import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * ModelGeneratorPlugin
 */
public class ModelGeneratorPlugin extends Plugin {
    public static final String PLUGIN_ID = "com.metamatrix.modeler.modelgenerator" ; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,BUNDLE_NAME,ResourceBundle.getBundle(BUNDLE_NAME));

    public static boolean DEBUG = false;

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
        ((PluginUtilImpl)Util).initializePlatformLogger(this);   // This must be called to initialize the platform logger!
    }
}
