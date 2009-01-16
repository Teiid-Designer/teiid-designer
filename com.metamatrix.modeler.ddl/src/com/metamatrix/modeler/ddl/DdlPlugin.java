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

package com.metamatrix.modeler.ddl;

import java.net.URL;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.core.xslt.Style;
import com.metamatrix.core.xslt.StyleFromResource;
import com.metamatrix.core.xslt.StyleFromUrlStream;
import com.metamatrix.core.xslt.StyleRegistry;
import com.metamatrix.core.xslt.impl.StyleRegistryImpl;
import com.metamatrix.modeler.internal.ddl.DdlWriterImpl;

/**
 * DdlPlugin
 */
public class DdlPlugin extends Plugin {

    public static final String PLUGIN_ID = "com.metamatrix.modeler.ddl" ; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,BUNDLE_NAME,ResourceBundle.getBundle(BUNDLE_NAME));

    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$

    /**
     * The identifiers for all DDL Plugin extension points
     */
    public static class EXTENSION_POINT {
        /** Extension point for registering EMF resource factories, point="com.metamatrix.modeler.ddl.ddlStyle" */
        public static class DDL_STYLE {
            public static final String ID = "style"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
            public static class ELEMENTS {
                public static final String DESCRIPTION    = "description"; //$NON-NLS-1$
                public static final String XSLT_PATH      = "xsltPath"; //$NON-NLS-1$
            }
        }
    }


    public static boolean DEBUG = false;

    /** The style registry */
    private static final StyleRegistry STYLE_REGISTRY = new StyleRegistryImpl();

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

        // populate the style registry from the extensions
        loadStyleRegistryFromExtensions(STYLE_REGISTRY);
    }

    /**
     * Create a new DDL writer instance.
     * @return
     */
    public static DdlWriter createDdlWriter() {
        return new DdlWriterImpl();
    }

    /**
     * Return the shared registry of {@link Style} instances that have been registered
     * with this plugin or that were found in Eclipse extensions.
     * @return the StyleRegistry; never null
     */
    public static StyleRegistry getStyleRegistry() {
        return STYLE_REGISTRY;
    }

    protected static void loadStyleRegistryFromExtensions( final StyleRegistry registry ) {
        // Find the extension point in the plugin registry ...
        final IExtensionPoint ddlStyleXP = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT.DDL_STYLE.UNIQUE_ID);

        // Get all of the extension ...
        final IExtension[] extensions = ddlStyleXP.getExtensions();
        for (int i = 0; i < extensions.length; ++i) {
            final IExtension extension = extensions[i];
            final String extensionName = extension.getLabel();

            // Get the information ...
            String desc = null;
            String xsltPath = null;
            final IConfigurationElement[] elements = extension.getConfigurationElements();
            for (int j = 0; j < elements.length; ++j) {
                final IConfigurationElement element = elements[j];
                final String elementName = element.getName();
                if ( EXTENSION_POINT.DDL_STYLE.ELEMENTS.DESCRIPTION.equals(elementName) ) {
                    desc = element.getValue();
                } else if ( EXTENSION_POINT.DDL_STYLE.ELEMENTS.XSLT_PATH.equals(elementName) ) {
                    xsltPath = element.getValue();
                }
            }


            // Create the Style ...
            if ( extensionName != null && xsltPath != null ) {
                // Determine what type of style implementation to create ...
                Style ddlStyle = null;
                try {
                    final URL url = new URL(xsltPath);
                    final String protocol = url.getProtocol();
                    if ( protocol != null && protocol.trim().length() != 0 ) {
                        ddlStyle = new StyleFromUrlStream(extensionName,xsltPath,desc);
                    }
                } catch ( Throwable t ) {
                    // It must not be a URL
                }
                if ( ddlStyle == null ) {
                    // Get the class loader ...
                    final Bundle bundle = Platform.getBundle(extension.getNamespaceIdentifier());
					if (bundle != null) {
                        ddlStyle = new StyleFromResource(bundle.getResource(xsltPath), extensionName, desc);
                    }
                }
                if ( ddlStyle != null ) {
                    registry.getStyles().add(ddlStyle);
                }
            }
        }
    }

}
