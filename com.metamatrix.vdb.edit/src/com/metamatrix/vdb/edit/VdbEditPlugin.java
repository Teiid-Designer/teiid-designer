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
package com.metamatrix.vdb.edit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.event.IChangeListener;
import com.metamatrix.core.event.IChangeNotifier;
import com.metamatrix.core.plugin.PluginUtilities;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.vdb.internal.edit.InternalVdbEditingContext;
import com.metamatrix.vdb.internal.edit.RuntimeIndexArtifactGenerator;
import com.metamatrix.vdb.internal.edit.SharedWsVdbContextEditor;
import com.metamatrix.vdb.internal.edit.SharedWsVdbContextValidator;
import com.metamatrix.vdb.internal.edit.VdbContextImpl;
import com.metamatrix.vdb.internal.edit.VdbEditingContextImpl;
import com.metamatrix.vdb.internal.edit.WsVdbInputResourceFinder;
import com.metamatrix.vdb.internal.edit.WsdlArtifactGenerator;

public class VdbEditPlugin extends Plugin {

    /** Name of the working folder used by the VdbEditingContext for locating temporary files and folders */
    public static final String VDB_WORKING_FOLDER_NAME = "vdbWorkingFolder"; //$NON-NLS-1$

    private static String producerVersion = ModelerCore.ILicense.VERSION;

    private static boolean autoBind = false;

    public static final String PLUGIN_ID = "com.metamatrix.vdb.edit"; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, BUNDLE_NAME, ResourceBundle.getBundle(BUNDLE_NAME));

    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$

    /**
     * The identifiers for all ModelerCore extension points
     */
    public static class EXTENSION_POINT {

        /** Extension point for registering VDB artifact generators, point="com.metamatrix.vdb.edit.vdbArtifactGenerator" */
        public static class VDB_ARTIFACT_GENERATOR {

            public static final String ID = "vdbArtifactGenerator"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {

                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {

                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }
    }

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
        public URL getBaseURL() {
            if (INSTANCE != null) {
                URL baseUrl;
                try {
                    baseUrl = FileLocator.resolve(INSTANCE.getBundle().getEntry("/")); //$NON-NLS-1$
                } catch (final IOException err) {
                    baseUrl = null;
                }
                return baseUrl;
            }
            try {
                final URI uri = URI.createURI(getClass().getResource("plugin.properties").toString()); //$NON-NLS-1$
                final URL baseUrl = new URL(uri.trimSegments(1).toString() + "/"); //$NON-NLS-1$
                return baseUrl;
            } catch (IOException exception) {
                throw new WrappedException(exception);
            }
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

    /**
     * Constant that defines the replaceable token in WSDL generated in the VDB which represents the first part (VDB-independent)
     * of the URL. The general form of the URLs is as follows:
     * <p>
     * <code>  [URL ROOT][Path to Resource in VDB][URL Suffix]</code>
     * </p>
     * </p> In general, the URL root would be replaced in a particular server with the actual URL to the servlet used to obtain
     * the WSDL and XSD, and the URL suffix replaced with the servlet parameters. </p>
     * 
     * @see #URL_SUFFIX_FOR_VDB
     * @see #URL_FOR_DATA_WEBSERVICE
     */
    public static final String URL_ROOT_FOR_VDB = com.metamatrix.core.CoreConstants.URL_ROOT_FOR_VDB;

    /**
     * Constant that defines the replaceable token in WSDL generated in the VDB which represents the suffix part of the URL. The
     * general form of the URLs is as follows:
     * <p>
     * <code>  [URL ROOT][Path to Resource in VDB][URL Suffix]</code>
     * </p>
     * </p> In general, the URL root would be replaced in a particular server with the actual URL to the servlet used to obtain
     * the WSDL and XSD, and the URL suffix replaced with the servlet parameters. </p>
     * 
     * @see #URL_ROOT_FOR_VDB
     * @see #URL_FOR_DATA_WEBSERVICE
     */
    public static final String URL_SUFFIX_FOR_VDB = com.metamatrix.core.CoreConstants.URL_SUFFIX_FOR_VDB;

    /**
     * Constant that defines the replaceable token in WSDL generated in the VDB which represents the URL for the service binding.
     * 
     * @see #URL_SUFFIX_FOR_VDB
     * @see #URL_ROOT_FOR_VDB
     */
    public static final String URL_FOR_DATA_WEBSERVICE = com.metamatrix.core.CoreConstants.URL_FOR_DATA_WEBSERVICE;

    /**
     * Constant that defines the replaceable token in WSDL generated in the VDB which represents the Metamatrix Server properties
     * for connecting the to VDB.
     */
    public static final String ACTION_PREFIX_FOR_DATA_WEBSERVICE = com.metamatrix.core.CoreConstants.ACTION_PREFIX_FOR_DATA_WEBSERVICE;

    public static boolean DEBUG = false;

    static VdbEditPlugin INSTANCE = null;

    public static VdbEditPlugin getInstance() {
        return INSTANCE;
    }

    /** cache for all open VdbEditingContext objects. key = IPath.makeAbsolute().toString, value = VdbEditingContext */
    static final Map vdbEditingContextCache = new HashMap();

    /** cache for all open VdbContext objects. key = IPath.makeAbsolute().toString, value = VdbContext */
    private static final Map vdbContextCache = new HashMap();

    /** change listener to remove contexts from the map when they are closed */
    static final IChangeListener vdbContextChangeListener = new IChangeListener() {
        public void stateChanged( IChangeNotifier theSource ) {
            if (theSource instanceof VdbEditingContext) {
                VdbEditingContext context = (VdbEditingContext)theSource;
                if (!context.isOpen()) {
                    String key = ((InternalVdbEditingContext)context).getPathToVdb().makeAbsolute().toString();
                    vdbEditingContextCache.remove(key);
                    context.removeChangeListener(vdbContextChangeListener);
                }
            } else if (theSource instanceof VdbContextEditor) {
                VdbContextEditor context = (VdbContextEditor)theSource;
                if (!context.isOpen()) {
                    IPath pathToVdbFile = new Path(context.getVdbFile().getAbsolutePath());
                    String key = pathToVdbFile.makeAbsolute().toString();
                    vdbEditingContextCache.remove(key);
                    context.removeChangeListener(vdbContextChangeListener);
                }
            }
        }
    };

    /** list of listeners for creation of new VdbEditingContext objects */
    private static final ArrayList vdbContextCreationListeners = new ArrayList();

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!

        // Set the producer version ...
        final String version = context.getBundle().getHeaders().get(Constants.BUNDLE_VERSION).toString();
        if (version != null && version.trim().length() != 0) {
            producerVersion = version;
        }

        // Clean up all vdb working folder contents ...
        cleanVdbWorkingDirectory();
    }

    /**
     * Create a new editing context for the VDB given by the supplied path.
     * 
     * @param pathToVdbFile the path to the VDB file; may not be null
     * @return the new editing context
     */
    public static VdbEditingContext createVdbEditingContext( final IPath pathToVdbFile ) throws CoreException {
        ArgCheck.isNotNull(pathToVdbFile);
        String key = pathToVdbFile.makeAbsolute().toString();
        VdbEditingContext context = (VdbEditingContext)vdbEditingContextCache.get(key);
        if (context == null || !context.isOpen()) {
            context = new VdbEditingContextImpl(pathToVdbFile);
            vdbEditingContextCache.put(key, context);
            context.addChangeListener(vdbContextChangeListener);
            fireVdbEditingContextCreated(context);
        }

        return context;
    }

    /**
     * Create a new editing context for the VDB given by the supplied path.
     * 
     * @param pathToVdbFile the path to the VDB file; may not be null
     * @return the new editing context
     */
    public static VdbEditingContext createVdbEditingContext( final IPath pathToVdbFile,
                                                             final IPath vdbWorkingPath ) throws CoreException {
        ArgCheck.isNotNull(pathToVdbFile);

        String key = pathToVdbFile.makeAbsolute().toString();
        VdbEditingContext context = (VdbEditingContext)vdbEditingContextCache.get(key);
        if (context == null || !context.isOpen()) {
            context = new VdbEditingContextImpl(pathToVdbFile, vdbWorkingPath, new WsVdbInputResourceFinder());
            vdbEditingContextCache.put(key, context);
            context.addChangeListener(vdbContextChangeListener);
            fireVdbEditingContextCreated(context);
        }

        return context;
    }

    /**
     * Create a new editing context for the VDB given by the supplied path.
     * 
     * @param pathToVdbFile the path to the VDB file; may not be null
     * @return the new editing context
     */
    public static VdbContext createVdbContext( final IPath pathToVdbFile,
                                               final IPath vdbWorkingPath ) {
        ArgCheck.isNotNull(pathToVdbFile);

        String key = pathToVdbFile.makeAbsolute().toString();
        VdbContext context = (VdbContext)vdbContextCache.get(key);
        if (context == null || !context.isOpen()) {

            final File vdbFile = pathToVdbFile.toFile();
            final File vdbWorkingFolder = vdbWorkingPath.toFile();
            if (!vdbWorkingFolder.exists()) {
                vdbWorkingFolder.mkdirs();
            }
            context = new VdbContextImpl(vdbFile, vdbWorkingFolder);

            vdbContextCache.put(key, context);
            context.addChangeListener(vdbContextChangeListener);
            fireVdbContextCreated(context);
        }

        return context;
    }

    /**
     * Create a new editing context for the VDB given by the supplied path.
     * 
     * @param pathToVdbFile the path to the VDB file; may not be null
     * @return the new editing context
     */
    public static VdbContextEditor createSharedWsVdbContextEditor( final IPath pathToVdbFile,
                                                                   final IPath vdbWorkingPath ) throws CoreException {
        ArgCheck.isNotNull(pathToVdbFile);

        String key = pathToVdbFile.makeAbsolute().toString();
        VdbContextEditor context = (VdbContextEditor)vdbContextCache.get(key);
        if (context == null || !context.isOpen()) {

            final File vdbFile = pathToVdbFile.toFile();
            final File vdbWorkingFolder = vdbWorkingPath.toFile();
            if (!vdbWorkingFolder.exists()) {
                vdbWorkingFolder.mkdirs();
            }
            final ResourceSet sharedContainer = ModelerCore.getModelContainer();
            context = new SharedWsVdbContextEditor(vdbFile, vdbWorkingFolder, sharedContainer);

            // Add the VdbContextValidator instance to use
            context.setVdbContextValidator(new SharedWsVdbContextValidator());

            // Add any contributed artifact generators to the context
            final List contributedGenerators = getVdbArtifactGenerators();
            for (final Iterator i = contributedGenerators.iterator(); i.hasNext();) {
                final VdbArtifactGenerator generator = (VdbArtifactGenerator)i.next();
                context.addArtifactGenerator(generator);
            }

            // Add the necessary "internal" artifact generators to the context
            context.addArtifactGenerator(new WsdlArtifactGenerator());
            context.addArtifactGenerator(new RuntimeIndexArtifactGenerator());

            vdbContextCache.put(key, context);
            context.addChangeListener(vdbContextChangeListener);
            fireVdbContextCreated(context);
        }

        return context;
    }

    /**
     * Return the working directory used by the vdb editors
     * 
     * @return
     * @since 4.2
     */
    public static File getVdbWorkingDirectory() {
        final IPath pluginPath = getInstance().getStateLocation();
        final IPath vdbWorkingPath = pluginPath.append(VDB_WORKING_FOLDER_NAME);
        final String absolutePath = vdbWorkingPath.toOSString();
        final File vdbWorkingFolder = new File(absolutePath);
        if (!vdbWorkingFolder.exists()) {
            vdbWorkingFolder.mkdir();
        }
        return vdbWorkingFolder;
    }

    public static String getProducerVersion() {
        return producerVersion;
    }

    private static void cleanVdbWorkingDirectory() {
        File directory = getVdbWorkingDirectory();
        if (directory.exists()) {
            removeDirectoryAndChildren(directory);
        }
    }

    private static void removeDirectoryAndChildren( final File directory ) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    removeDirectoryAndChildren(file);
                } else {
                    if (!file.delete()) {
                        file.deleteOnExit();
                    }
                }
            }
        }
        if (!directory.delete()) {
            directory.deleteOnExit();
        }
    }

    public static List getVdbArtifactGenerators() {
        final List result = new ArrayList();
        if (INSTANCE != null) {
            // Then we know we're running in Eclipse and the extensions are available ...
            final IExtension[] extensions = PluginUtilities.getExtensions(VdbEditPlugin.EXTENSION_POINT.VDB_ARTIFACT_GENERATOR.UNIQUE_ID);
            for (int i = 0; i < extensions.length; i++) {
                final IExtension extension = extensions[i];
                final String classElement = VdbEditPlugin.EXTENSION_POINT.VDB_ARTIFACT_GENERATOR.ELEMENTS.CLASS;
                final String classNameAttribute = VdbEditPlugin.EXTENSION_POINT.VDB_ARTIFACT_GENERATOR.ATTRIBUTES.NAME;
                try {
                    Object obj = PluginUtilities.createExecutableExtension(extension, classElement, classNameAttribute);
                    if (obj instanceof VdbArtifactGenerator) {
                        result.add(obj);
                    }
                } catch (CoreException err) {
                    VdbEditPlugin.Util.log(err);
                } catch (Throwable err) {
                    VdbEditPlugin.Util.log(err);
                }
            }
        }
        return result;
    }

    /**
     * Add the specified {@link IChangeListener} to the list for notification when a new VdbEditingContext is created.
     * 
     * @since 5.0
     */
    public static void addVdbEditingContextCreationListener( IChangeListener listener ) {
        ArgCheck.isNotNull(listener);
        if (!vdbContextCreationListeners.contains(listener)) {
            vdbContextCreationListeners.add(listener);
        }
    }

    /**
     * Remove the specified {@link IChangeListener} from the list for notification when a new VdbEditingContext is created.
     * 
     * @since 5.0
     */
    public static void removeVdbEditingContextCreationListener( IChangeListener listener ) {
        ArgCheck.isNotNull(listener);
        vdbContextCreationListeners.remove(listener);
    }

    /**
     * Notify all registered {@link IChangeListener}s that the state has changed.
     * 
     * @since 5.0
     */
    private static void fireVdbEditingContextCreated( VdbEditingContext theContext ) {
        // obtain an array so the thing can't change underneath us:
        Object[] listeners = vdbContextCreationListeners.toArray();

        for (int i = 0; i < listeners.length; i++) {
            IChangeListener list = (IChangeListener)listeners[i];
            list.stateChanged(theContext);
        } // endfor
    }

    /**
     * Notify all registered {@link IChangeListener}s that the state has changed.
     * 
     * @since 5.0
     */
    private static void fireVdbContextCreated( VdbContext theContext ) {
        // obtain an array so the thing can't change underneath us:
        Object[] listeners = vdbContextCreationListeners.toArray();

        for (int i = 0; i < listeners.length; i++) {
            IChangeListener list = (IChangeListener)listeners[i];
            list.stateChanged(theContext);
        } // endfor
    }

    public static boolean shouldAutoBind() {
        return autoBind;
    }

    public static void setAutoBind( boolean value ) {
        autoBind = value;
    }
}
