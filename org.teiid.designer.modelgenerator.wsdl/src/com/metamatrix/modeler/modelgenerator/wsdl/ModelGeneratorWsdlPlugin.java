/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.wsdl;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

/**
 * The main plugin class.
 */
public class ModelGeneratorWsdlPlugin extends Plugin {

    /**
     * The plug-in identifier of this plugin
     */
    public static final String PLUGIN_ID = "org.teiid.modelgenerator.wsdl"; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = ModelGeneratorWsdlPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    public static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    // The shared instance.
    private static ModelGeneratorWsdlPlugin INSTANCE;

    public static boolean DEBUG = false;

    /**
     * Construct an instance of ModelGeneratorWsdlPlugin.
     */
    public ModelGeneratorWsdlPlugin() {
    	INSTANCE = this;
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        ModelGeneratorWsdlPlugin.INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    }

    /**
     * Returns the shared instance.
     */
    public static ModelGeneratorWsdlPlugin getDefault() {
        return INSTANCE;
    }

}
