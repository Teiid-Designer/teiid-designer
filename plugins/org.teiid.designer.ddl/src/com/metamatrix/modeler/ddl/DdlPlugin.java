/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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

    public static final String PLUGIN_ID = "org.teiid.designer.ddl" ; //$NON-NLS-1$
    
    public static final String PACKAGE_ID = DdlPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID,I18N_NAME,ResourceBundle.getBundle(I18N_NAME));

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

            public static class ELEMENTS {
                public static final String DESCRIPTION    = "description"; //$NON-NLS-1$
                public static final String XSLT_PATH      = "xsltPath"; //$NON-NLS-1$
            }
        }
    }


    static DdlPlugin INSTANCE = null;
    
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
        
        INSTANCE = this;
        
        // populate the style registry from the extensions
        loadStyleRegistryFromExtensions(STYLE_REGISTRY);
    }
    
    /**
     * @return DqpPlugin
     * @since 4.3
     */
    public static DdlPlugin getInstance() {
        return INSTANCE;
    }

    /**
     * Create a new DDL writer instance.
     * @return
     */
    public DdlWriter createDdlWriter() {
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
        final IExtensionPoint ddlStyleXP = Platform.getExtensionRegistry().getExtensionPoint(DdlPlugin.PLUGIN_ID, EXTENSION_POINT.DDL_STYLE.ID);

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
