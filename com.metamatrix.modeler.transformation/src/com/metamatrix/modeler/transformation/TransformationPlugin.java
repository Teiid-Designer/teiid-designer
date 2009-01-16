/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
