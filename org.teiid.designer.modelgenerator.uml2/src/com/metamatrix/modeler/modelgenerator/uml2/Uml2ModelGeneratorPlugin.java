/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.uml2;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.modelgenerator.uml2.processor.Uml2RelationalGeneratorImpl;

/**
 * The main plugin class to be used in the desktop.
 */
public class Uml2ModelGeneratorPlugin extends Plugin {
    /**
     * The plug-in identifier of this plugin
     * (value <code>"com.metamatrix.modeler.modelgenerator.uml2"</code>).
     */
    public static final String PLUGIN_ID = "org.teiid.designer.modelgenerator.uml2"; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = Uml2ModelGeneratorPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));

    public static boolean DEBUG = false;


	//The shared instance.
	private static Uml2ModelGeneratorPlugin plugin;

	/**
	 * The constructor.
	 */
	public Uml2ModelGeneratorPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	@Override
    public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
    public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static Uml2ModelGeneratorPlugin getDefault() {
		return plugin;
	}

    /**
     * Create a new {@link Uml2RelationalGenerator} instance.
     * @return
     */
    public static Uml2RelationalGenerator createUml2RelationalGenerator() {
        return new Uml2RelationalGeneratorImpl();
    }

}
