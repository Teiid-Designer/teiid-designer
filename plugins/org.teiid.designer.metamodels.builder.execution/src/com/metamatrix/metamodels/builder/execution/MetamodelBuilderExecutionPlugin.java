/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.builder.execution;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.metamodels.xsd.XsdResourceFactory;

/**
 * MetamodelBuilderExecutionPlugin
 */
public class MetamodelBuilderExecutionPlugin extends Plugin {

    /**
     * The plug-in identifier of this plug-in
     */
    public static final String PLUGIN_ID = "org.teiid.designer.metamodels.builder.execution"; //$NON-NLS-1$

    public static final String PACKAGE_ID = MetamodelBuilderExecutionPlugin.class.getPackage().getName();

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    /** Defines the expected name of the built-in datatype model file */
    public static final String DATATYPES_XSD_FILE_NAME = "builtInDataTypes.xsd"; //$NON-NLS-1$

    /** Defines the expected built-in datatypes model URI */
    public static final String BUILTIN_DATATYPES_URI_STRING = "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"; //$NON-NLS-1$

    /** Shared resource set containing the Teiid Designer built-in types resource */
    protected static ResourceSet globalResourceSet;

    /** Shared resource set containing the Teiid Designer built-in types resource */
    protected static Resource builtInTypesResource;

    // The shared instance.
    private static MetamodelBuilderExecutionPlugin plugin;
    protected URL baseURL;

    // ==================================================================================
    // C O N S T R U C T O R S
    // ==================================================================================

    /**
     * The constructor.
     */
    public MetamodelBuilderExecutionPlugin() {
        plugin = this;
    }

    // ==================================================================================
    // P U B L I C M E T H O D S
    // ==================================================================================

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);

        // This must be called to initialize the platform logger!
        ((PluginUtilImpl)Util).initializePlatformLogger(this);
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     */
    public static MetamodelBuilderExecutionPlugin getDefault() {
        return plugin;
    }

    /**
     * Return the shared resource set containing the Teiid Designer built-in types resource
     * 
     * @return
     * @since 4.3
     */
    public static synchronized ResourceSet getGlobalResourceSet() {
        if (globalResourceSet == null) {
            globalResourceSet = createResourceSet();

            Resource eResource = getBuiltInTypesResource();

            URI logicalURI = URI.createURI(BUILTIN_DATATYPES_URI_STRING);
            URI physicalURI = eResource.getURI();
            globalResourceSet.getURIConverter().getURIMap().put(logicalURI, physicalURI);
        }

        return globalResourceSet;
    }

    /**
     * Return the shared Teiid Designer built-in types resource
     * 
     * @return
     * @since 4.3
     */
    public static synchronized Resource getBuiltInTypesResource() {
        if (builtInTypesResource == null) {
            try {
                String baseURL = MetamodelBuilderExecutionPlugin.getDefault().getBaseURL().toString();
                URI uri = URI.createURI(baseURL + DATATYPES_XSD_FILE_NAME);
                builtInTypesResource = getGlobalResourceSet().getResource(uri, true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return builtInTypesResource;
    }

    // ==================================================================================
    // P R O T E C T E D M E T H O D S
    // ==================================================================================

    protected static ResourceSet createResourceSet() {
        ResourceSet result = new ResourceSetImpl();
        Map map = result.getResourceFactoryRegistry().getExtensionToFactoryMap();
        if (!map.containsKey("xsd")) { //$NON-NLS-1$
            map.put("xsd", new XsdResourceFactory()); //$NON-NLS-1$
        }
        return result;
    }

    protected URL getBaseURL() {
        if (baseURL == null) {
            try {
                // Determine the base URL by looking for the plugin.properties file in the standard way.
                Class theClass = getClass();
                URL pluginPropertiesURL = theClass.getResource("plugin.properties"); //$NON-NLS-1$
                if (pluginPropertiesURL == null) {
                    String className = theClass.getName();
                    int index = className.lastIndexOf("."); //$NON-NLS-1$
                    URL classURL = theClass.getResource((index == -1 ? className : className.substring(index + 1)) + ".class"); //$NON-NLS-1$
                    URI uri = URI.createURI(classURL.toString());

                    // Trim off the segements corresponding to the package nesting.
                    int count = 1;
                    for (int i = 0; (i = className.indexOf('.', i)) != -1; ++i) {
                        ++count;
                    }
                    uri = uri.trimSegments(count);

                    // For an archive URI, check for the plugin.properties in the archive.
                    if (URI.isArchiveScheme(uri.scheme())) {
                        try {
                            // If we can open an input stream, then the plugin.properties is there, and we have a good base URL.
                            InputStream inputStream = new URL(uri.appendSegment("plugin.properties").toString()).openStream(); //$NON-NLS-1$
                            inputStream.close();
                            baseURL = new URL(uri.toString());
                        } catch (IOException exception) {
                            // If the plugin.properties isn't within the root of the archive,
                            // create a new URI for the folder location of the archive,
                            // so we can look in the folder that contains it.
                            uri = URI.createURI(uri.authority()).trimSegments(1);
                        }
                    }

                    // If we didn't find the plugin.properties in the usual place nor in the archive...
                    if (baseURL == null) {
                        // Trim off the "bin" or "runtime" segement.
                        String lastSegment = uri.lastSegment();
                        if ("bin".equals(lastSegment) || "runtime".equals(lastSegment)) { //$NON-NLS-1$  //$NON-NLS-2$
                            uri = uri.trimSegments(1);
                        }
                        uri = uri.appendSegment("plugin.properties"); //$NON-NLS-1$
                        try {
                            // If we can open an input stream, then the plugin.properties is in the folder,
                            // and we have a good base URL.
                            InputStream inputStream = new URL(uri.toString()).openStream();
                            inputStream.close();
                            baseURL = new URL(uri.trimSegments(1).toString() + "/"); //$NON-NLS-1$
                        } catch (IOException exception) {
                        }
                    }

                    // If we still don't have a good base URL, complain about it.
                    if (baseURL == null) {
                        String resourceName = index == -1 ? "plugin.properties" : className.substring(0, index + 1) //$NON-NLS-1$
                        .replace('.', '/') + "plugin.properties"; //$NON-NLS-1$
                        throw new MissingResourceException("Missing properties: " + resourceName, theClass.getName(), //$NON-NLS-1$
                                                           "plugin.properties"); //$NON-NLS-1$
                    }
                } else {
                    baseURL = new URL(URI.createURI(pluginPropertiesURL.toString()).trimSegments(1).toString() + "/"); //$NON-NLS-1$
                }
            } catch (IOException exception) {
                throw new WrappedException(exception);
            }
        }

        return baseURL;
    }

}
