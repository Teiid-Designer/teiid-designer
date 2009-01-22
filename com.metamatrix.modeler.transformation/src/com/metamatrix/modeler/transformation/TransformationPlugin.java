/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.transformation.udf.UdfManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class TransformationPlugin extends Plugin {

    /**
     * The plug-in identifier of this plugin (value <code>"com.metamatrix.modeler.transformation"</code>).
     */
    public static final String PLUGIN_ID = "com.metamatrix.modeler.transformation"; //$NON-NLS-1$

    public static final String FUNCTION_DEFS = "FunctionDefinitions.xmi"; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtilImpl Util = new PluginUtilImpl(PLUGIN_ID, BUNDLE_NAME, ResourceBundle.getBundle(BUNDLE_NAME));

    public static final boolean DEBUG = false;

    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$

    /**
     * The identifiers for all TransformationPlugin extension points
     */
    public static class EXTENSION_POINT {
        /** Extension point for external resource sets */
        public static class USER_DEFINED_FUNCTION_MODEL {
            public static final String ID = "userDefinedFunctionModel"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String MODEL = "model"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String URL = "url"; //$NON-NLS-1$
            }
        }
    }

    // The shared instance.
    private static TransformationPlugin TRANSFORMATION_PLUGIN;

    /**
     * The constructor.
     */
    public TransformationPlugin() {
        TRANSFORMATION_PLUGIN = this;
    }

    /**
     * Returns the single instance of the Transformation plug-in runtime class.
     * 
     * @return the single instance of the Transformation plug-in runtime class
     */
    public static TransformationPlugin getDefault() {
        return TRANSFORMATION_PLUGIN;
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        Util.initializePlatformLogger(this);

        // initialize the use of user-defined functions
        UdfManager.INSTANCE.initialize();
    }

    @Override
    public void stop( BundleContext context ) throws Exception {
        UdfManager.INSTANCE.shutdown();
        super.stop(context);
    }

    protected File getFile( final URL url ) {
        final String path = url.getPath();
        final File result = new File(path);
        return result;
    }
}
