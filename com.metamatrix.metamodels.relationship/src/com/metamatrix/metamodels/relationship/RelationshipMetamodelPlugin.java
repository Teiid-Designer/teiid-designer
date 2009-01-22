/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.metamodels.relationship.util.RelationshipTypeManager;

/**
 * RelationalPlugin
 */
public class RelationshipMetamodelPlugin extends Plugin {

    public static final String BUILTIN_RELATIONSHIP_TYPES_URI = "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"; //$NON-NLS-1$

    public static final String PLUGIN_ID = "com.metamatrix.metamodels.relationship"; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, BUNDLE_NAME, ResourceBundle.getBundle(BUNDLE_NAME));

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
        public URL getBaseURL() {
            URL baseUrl = null;
            if (INSTANCE != null && INSTANCE.getBundle() != null) {
                try {
                    baseUrl = FileLocator.resolve(INSTANCE.getBundle().getEntry("/")); //$NON-NLS-1$
                } catch (final IOException err) {
                }
            }
            if (baseUrl == null) {
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
                                // If we can open an input stream, then the plugin.properties is there, and we have a good base
                                // URL.
                                InputStream inputStream = new URL(uri.appendSegment("plugin.properties").toString()).openStream(); //$NON-NLS-1$
                                inputStream.close();
                                baseUrl = new URL(uri.toString());
                            } catch (IOException exception) {
                                // If the plugin.properties isn't within the root of the archive,
                                // create a new URI for the folder location of the archive,
                                // so we can look in the folder that contains it.
                                uri = URI.createURI(uri.authority()).trimSegments(1);
                            }
                        }

                        // If we didn't find the plugin.properties in the usual place nor in the archive...
                        if (baseUrl == null) {
                            // Trim off the "bin" or "runtime" segement.
                            String lastSegment = uri.lastSegment();
                            if ("bin".equals(lastSegment) || "runtime".equals(lastSegment)) { //$NON-NLS-1$ //$NON-NLS-2$
                                uri = uri.trimSegments(1);
                            }
                            uri = uri.appendSegment("plugin.properties"); //$NON-NLS-1$
                            try {
                                // If we can open an input stream, then the plugin.properties is in the folder,
                                // and we have a good base URL.
                                InputStream inputStream = new URL(uri.toString()).openStream();
                                inputStream.close();
                                baseUrl = new URL(uri.trimSegments(1).toString() + "/"); //$NON-NLS-1$
                            } catch (IOException exception) {
                            }
                        }

                        // If we still don't have a good base URL, complain about it.
                        if (baseUrl == null) {
                            String resourceName = index == -1 ? "plugin.properties" : className.substring(0, index + 1).replace('.', '/') //$NON-NLS-1$
                                                                                      + "plugin.properties"; //$NON-NLS-1$
                            throw new MissingResourceException("Missing properties: " + resourceName, theClass.getName(), //$NON-NLS-1$
                                                               "plugin.properties"); //$NON-NLS-1$
                        }
                    } else {
                        baseUrl = new URL(URI.createURI(pluginPropertiesURL.toString()).trimSegments(1).toString() + "/"); //$NON-NLS-1$
                    }
                } catch (IOException exception) {
                    throw new WrappedException(exception);
                }
            }

            return baseUrl;
        }

        public Object getImage( String key ) {
            try {
                final URL baseUrl = getBaseURL();
                final URL url = new URL(baseUrl + "icons/" + key + ".gif"); //$NON-NLS-1$//$NON-NLS-2$
                InputStream inputStream = url.openStream();
                inputStream.close();
                return url;
            } catch (MalformedURLException exception) {
                throw new WrappedException(exception);
            } catch (IOException exception) {
                throw new MissingResourceException(
                                                   CommonPlugin.INSTANCE.getString("_UI_StringResourceNotFound_exception", new Object[] {key}), //$NON-NLS-1$
                                                   getClass().getName(), key);
            }
        }

        public String getString( String key ) {
            return Util.getString(key);
        }

        public String getString( String key,
                                 Object[] substitutions ) {
            return Util.getString(key, substitutions);
        }

        public String getString( final String key,
                                 final boolean translate ) {
            return getString(key);
        }

        public String getString( final String key,
                                 final Object[] substitutions,
                                 final boolean translate ) {
            return getString(key, substitutions);
        }
    };

    /**
     * Called by the {@link com.metamatrix.metamodels.transformation.provider.TransformationEditPlugin}
     * 
     * @return the EMF ResourceLocator used when run as a plugin
     */
    public static ResourceLocator getPluginResourceLocator() {
        return RESOURCE_LOCATOR;
    }

    /** Shared resource set containing the MetaMatrix built-in types resource */
    protected static ResourceSet globalResourceSet;

    /** Shared resource set containing the MetaMatrix built-in types resource */
    protected static Resource builtInTypesResource;

    // The shared instance.
    static RelationshipMetamodelPlugin INSTANCE = new RelationshipMetamodelPlugin();
    protected URL baseURL;

    public static boolean DEBUG = false;

    /**
     * Construct an instance of MetaMatrixPlugin.
     */
    public RelationshipMetamodelPlugin() {
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        RelationshipMetamodelPlugin.INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    }

    /**
     * Returns the shared instance.
     */
    public static RelationshipMetamodelPlugin getDefault() {
        return INSTANCE;
    }

    /**
     * Return a manager for built-in RelationshipType instances.
     * 
     * @return
     */
    public static RelationshipTypeManager getBuiltInRelationshipTypeManager() {
        return RelationshipTypeManager.getInstance();
    }

    /**
     * Return the shared resource set containing the MetaMatrix built-in types resource
     * 
     * @return
     * @since 4.3
     */
    public static synchronized ResourceSet getGlobalResourceSet() {
        if (globalResourceSet == null) {
            globalResourceSet = createResourceSet();

            loadBuiltInRelationshipTypesResource(globalResourceSet);
        }

        return globalResourceSet;
    }

    /**
     * Return the shared MetaMatrix built-in types resource
     * 
     * @return
     * @since 4.3
     */
    public static synchronized Resource getBuiltInRelationshipTypesResource() {
        if (builtInTypesResource == null) {
            URI uri = URI.createURI(BUILTIN_RELATIONSHIP_TYPES_URI);
            builtInTypesResource = getGlobalResourceSet().getResource(uri, true);
        }
        return builtInTypesResource;
    }

    protected static ResourceSet createResourceSet() {
        // TODO: Need to use EmfResourceSetImpl and MtkXmiResourceFactory to be able to read
        // in this model file. However, EmfResourceSetImpl and MtkXmiResourceFactory need
        // a Container instance and we remove this requirement
        ResourceSet result = new ResourceSetImpl();
        result.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl()); //$NON-NLS-1$
        return result;
    }

    protected static void loadBuiltInRelationshipTypesResource( final ResourceSet rs ) {
        try {
            String baseURL = getPluginResourceLocator().getBaseURL().toString();
            URI uri = URI.createURI(baseURL + "cache/www.metamatrix.com/relationships/builtInRelationshipTypes.xmi"); //$NON-NLS-1$
            Resource r = rs.getResource(uri, true);

            if (r != null) {
                URI logicalURI = URI.createURI(BUILTIN_RELATIONSHIP_TYPES_URI);
                URI physicalURI = r.getURI();
                rs.getURIConverter().getURIMap().put(logicalURI, physicalURI);
            } else {
                String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipMetamodelPlugin.error_loading_builtin_types_resource_from_url", uri); //$NON-NLS-1$
                RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, msg);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
