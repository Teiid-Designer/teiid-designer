/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.modeler.compare.impl.ComparePackageImpl;
import com.metamatrix.modeler.compare.processor.DifferenceProcessorImpl;
import com.metamatrix.modeler.compare.processor.MergeProcessorImpl;
import com.metamatrix.modeler.compare.selector.EmfResourceSelector;
import com.metamatrix.modeler.compare.selector.InputStreamModelSelector;
import com.metamatrix.modeler.compare.selector.ModelResourceSelector;
import com.metamatrix.modeler.compare.selector.ModelSelector;
import com.metamatrix.modeler.compare.selector.URIModelSelector;
import com.metamatrix.modeler.core.MappingAdapterDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.compare.EObjectMatcherFactory;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.internal.core.workspace.ModelUtil;

/**
 * ModelerComparePlugin
 */
public class ModelerComparePlugin extends Plugin {

    private static final String MISSING_RESOURCE = "<Missing message for key"; //$NON-NLS-1$

    public static final String PLUGIN_ID = "com.metamatrix.modeler.compare"; //$NON-NLS-1$

    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String BUNDLE_NAME = PLUGIN_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, BUNDLE_NAME, ResourceBundle.getBundle(BUNDLE_NAME));

    private static final ResourceLocator RESOURCE_LOCATOR = new ResourceLocator() {
        private ResourceLocator delegate = EcorePlugin.INSTANCE.getPluginResourceLocator();

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
                return delegate.getImage(key);
            }
        }

        public String getString( String key ) {
            String result = Util.getString(key);
            if (result.startsWith(MISSING_RESOURCE)) {
                result = delegate.getString(key);
            }
            return result;
        }

        public String getString( String key,
                                 Object[] substitutions ) {
            String result = Util.getString(key);
            if (result.startsWith(MISSING_RESOURCE)) {
                result = delegate.getString(key, substitutions);
            }
            return result;
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

    public static boolean DEBUG = false;
    static ModelerComparePlugin INSTANCE = null;

    static {
        ModelerCore.getMetamodelRegistry();

        ComparePackageImpl.init();
    }

    /**
     * Construct an instance of MetaMatrixPlugin.
     */
    public ModelerComparePlugin() {
    }

    /**
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     * @since 4.3.2
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        INSTANCE = this;
        ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
    }

    /**
     * Create a difference processor that computes the differences between the current (unsaved) state of a model and it's saved
     * state (on disk).
     * <p>
     * The differences returned by the processor describe the actions taken to change the <i>saved</i> state into the <i>unsaved
     * (current)</i> state.
     * </p>
     * 
     * @param modelResource the {@link ModelResource} for which the difference between the saved and current state is to be
     *        performed; may not be null
     * @return the difference processor; never null
     * @throws ModelWorkspaceException if there is a problem with the supplied resources
     */
    public static DifferenceProcessor createDifferenceProcessor( final ModelResource modelResource )
        throws ModelWorkspaceException {
        ArgCheck.isNotNull(modelResource);
        final ModelSelector currentSelector = new ModelResourceSelector(modelResource);
        currentSelector.setLabel(modelResource.getPath().toString());
        DifferenceProcessor processor = null;
        if (!modelResource.hasUnsavedChanges()) {
            // There are no unsaved changes, so nothing to compute ...
            processor = new DifferenceProcessorImpl(currentSelector);
        } else {
            // There are unsaved changes, so create a selector to open the saved model ...
            final URI uri = URI.createFileURI(modelResource.getResource().getLocation().toFile().getAbsolutePath());
            final ModelSelector savedSelector = new URIModelSelector(uri);
            final Object[] params = new Object[] {modelResource.getPath().toString()};
            savedSelector.setLabel(ModelerComparePlugin.Util.getString("ModelerComparePlugin.SavedModel", params)); //$NON-NLS-1$
            currentSelector.setLabel(ModelerComparePlugin.Util.getString("ModelerComparePlugin.OpenedModel", params)); //$NON-NLS-1$
            processor = new DifferenceProcessorImpl(savedSelector, currentSelector);
        }

        final List mappingAdapters = createEObjectMatcherFactories();
        processor.addEObjectMatcherFactories(mappingAdapters);
        return processor;
    }

    /**
     * Create a difference processor that computes the differences between the supplied model resources.
     * <p>
     * The differences returned by the processor describe the actions taken to change the <code>startingResource</code> into the
     * <code>endingResource</code>.
     * </p>
     * 
     * @param startingResource the {@link ModelResource} containing the original (starting) state; may not be null
     * @param endingResource the {@link ModelResource} containing the final (ending) state; may not be null
     * @return the difference processor; never null
     * @throws ModelWorkspaceException if there is a problem with the supplied resources
     */
    public static DifferenceProcessor createDifferenceProcessor( final ModelResource startingResource,
                                                                 final ModelResource endingResource ) {
        ArgCheck.isNotNull(startingResource);
        ArgCheck.isNotNull(endingResource);
        final ModelSelector startingSelector = new ModelResourceSelector(startingResource);
        final ModelSelector endingSelector = new ModelResourceSelector(endingResource);
        endingSelector.setLabel(startingResource.getPath().toString());
        endingSelector.setLabel(endingResource.getPath().toString());
        final DifferenceProcessor processor = new DifferenceProcessorImpl(startingSelector, endingSelector);
        final List mappingAdapters = createEObjectMatcherFactories();
        processor.addEObjectMatcherFactories(mappingAdapters);
        return processor;
    }

    /**
     * Create a difference processor that computes the differences between model in the supplied stream and the supplied model
     * resource.
     * <p>
     * The differences returned by the processor describe the actions taken to change the <code>startingResource</code> into the
     * <code>endingResource</code>.
     * </p>
     * 
     * @param startingResource the {@link InputStream} containing the model in it's original (starting) state; may not be null
     * @param startingResourcePath the {@link IPath} to the resource in the workspace or repository.
     * @param endingResource the {@link ModelResource} containing the final (ending) state; may not be null
     * @return the difference processor; never null
     * @throws ModelWorkspaceException if there is a problem with the supplied resources
     */
    public static DifferenceProcessor createDifferenceProcessor( final InputStream startingResource,
                                                                 final IPath startingResourcePath,
                                                                 final ModelResource endingResource,
                                                                 final String startingResourceDesc )
        throws ModelWorkspaceException {
        ArgCheck.isNotNull(startingResource);
        ArgCheck.isNotNull(startingResourcePath);
        ArgCheck.isNotNull(endingResource);

        // assume that the starting resource and ending resource are of same type
        IResource resource = endingResource.getCorrespondingResource();
        URI temporayURI = null;
        if (ModelUtil.isXsdFile(resource)) {
            temporayURI = InputStreamModelSelector.XSD_URI;
        } else if (ModelUtil.isModelFile(resource)) {
            temporayURI = InputStreamModelSelector.XMI_URI;
        } else if (ModelUtil.isVdbArchiveFile(resource)) {
            temporayURI = InputStreamModelSelector.VDB_URI;
        }

        // dont process....this is not a model file
        if (temporayURI == null) {
            return null;
        }
        final ModelSelector startingSelector = new InputStreamModelSelector(startingResource, temporayURI);
        final ModelSelector endingSelector = new ModelResourceSelector(endingResource);
        startingSelector.setLabel(startingResourceDesc);
        endingSelector.setLabel(endingResource.getPath().toString());
        final DifferenceProcessor processor = new DifferenceProcessorImpl(startingSelector, endingSelector);
        final List mappingAdapters = createEObjectMatcherFactories();
        processor.addEObjectMatcherFactories(mappingAdapters);
        return processor;
    }

    /**
     * Create a difference processor that computes the differences between the supplied resources.
     * <p>
     * The differences returned by the processor describe the actions taken to change the <code>startingResource</code> into the
     * <code>endingResource</code>.
     * </p>
     * 
     * @param startingResource the EMF resource containing the original (starting) state; may not be null
     * @param endingResource the EMF resource containing the final (ending) state; may not be null
     * @return the difference processor; never null
     * @throws ModelWorkspaceException if there is a problem with the supplied resources
     */
    public static DifferenceProcessor createDifferenceProcessor( final Resource startingResource,
                                                                 final Resource endingResource ) {
        ArgCheck.isNotNull(startingResource);
        ArgCheck.isNotNull(endingResource);
        final ModelSelector startingSelector = new EmfResourceSelector(startingResource);
        final ModelSelector endingSelector = new EmfResourceSelector(endingResource);
        startingSelector.setLabel(URI.decode(startingResource.getURI().toString()));
        endingSelector.setLabel(URI.decode(endingResource.getURI().toString()));
        final DifferenceProcessor processor = new DifferenceProcessorImpl(startingSelector, endingSelector);
        final List mappingAdapters = createEObjectMatcherFactories();
        processor.addEObjectMatcherFactories(mappingAdapters);
        return processor;
    }

    /**
     * Create a difference processor that computes the differences between the supplied resources. Sets the inputToOutput map so
     * that previous model mappings may be used.
     * <p>
     * The differences returned by the processor describe the actions taken to change the <code>startingResource</code> into the
     * <code>endingResource</code>.
     * </p>
     * 
     * @param startingResource the EMF resource containing the original (starting) state; may not be null
     * @param endingResource the EMF resource containing the final (ending) state; may not be null
     * @return the difference processor; never null
     * @throws ModelWorkspaceException if there is a problem with the supplied resources
     */
    public static DifferenceProcessor createDifferenceProcessor( final Resource startingResource,
                                                                 final Resource endingResource,
                                                                 final HashMap mappings ) {
        ArgCheck.isNotNull(startingResource);
        ArgCheck.isNotNull(endingResource);
        final ModelSelector startingSelector = new EmfResourceSelector(startingResource);
        final ModelSelector endingSelector = new EmfResourceSelector(endingResource);
        startingSelector.setLabel(URI.decode(startingResource.getURI().toString()));
        endingSelector.setLabel(URI.decode(endingResource.getURI().toString()));
        final DifferenceProcessor processor = new DifferenceProcessorImpl(startingSelector, endingSelector, mappings);
        final List mappingAdapters = createEObjectMatcherFactories();
        processor.addEObjectMatcherFactories(mappingAdapters);
        return processor;
    }

    /**
     * Create a merge processor that merges the differences computed by the supplied processor. The result of executing the merge
     * processor is to convert the original (starting) state of the difference report into the final (ending) state.
     * 
     * @param difference the difference report that specifies those differences that should be merged
     * @return the processor that can be used to execute the merge
     */
    public static MergeProcessor createMergeProcessor( final DifferenceProcessor difference ) {
        ArgCheck.isInstanceOf(DifferenceProcessorImpl.class, difference);
        return new MergeProcessorImpl((DifferenceProcessorImpl)difference);
    }

    /**
     * Create a merge processor that merges the differences computed by the supplied processor. The result of executing the merge
     * processor is to convert the original (starting) state of the difference report into the final (ending) state.
     * 
     * @param difference the difference report that specifies those differences that should be merged
     * @param externalReferences the array of EObjects that are referenceable by either the starting or ending resource but are
     *        external to both resource.
     * @return the processor that can be used to execute the merge
     */
    public static MergeProcessor createMergeProcessor( final DifferenceProcessor difference,
                                                       final EObject[] externalReferences ) {
        ArgCheck.isInstanceOf(DifferenceProcessorImpl.class, difference);
        return new MergeProcessorImpl((DifferenceProcessorImpl)difference, externalReferences);
    }

    /**
     * Create a merge processor that merges the differences computed by the supplied processor. The result of executing the merge
     * processor is to convert the original (starting) state of the difference report into the final (ending) state.
     * 
     * @param difference the difference report that specifies those differences that should be merged
     * @param externalReferences the array of EObjects that are referenceable by either the starting or ending resource but are
     *        external to both resource.
     * @param moveAddsRatherThanCopy true if objects that are considered "adds" should be <i>moved</i> rather than copied (will be
     *        removed from the source model), or false the source model should be left unchanged and any "adds" be copied into the
     *        output model
     * @return the processor that can be used to execute the merge
     */
    public static MergeProcessor createMergeProcessor( final DifferenceProcessor difference,
                                                       final EObject[] externalReferences,
                                                       final boolean moveAddsRatherThanCopy ) {
        ArgCheck.isInstanceOf(DifferenceProcessorImpl.class, difference);
        return new MergeProcessorImpl((DifferenceProcessorImpl)difference, externalReferences, moveAddsRatherThanCopy);
    }

    /**
     * Utility method to create {@link EObjectMappingAdapter mapping adapter} instances by using the
     * {@link ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY} extensions
     * 
     * @return a list of {@link EObjectMatcherFactory} instances defined via extensions
     * @see ModelerCore#getMappingAdapterDescriptors()
     */
    public static List createEObjectMatcherFactories() {
        final List factories = new LinkedList();

        // Look for registered mapping adapters ...
        final List adapterDescriptors = ModelerCore.getMappingAdapterDescriptors();
        final Iterator iter = adapterDescriptors.iterator();
        while (iter.hasNext()) {
            final MappingAdapterDescriptor desc = (MappingAdapterDescriptor)iter.next();
            if (desc != null) {
                // Instantiate the descriptor ...
                final EObjectMatcherFactory factory = (EObjectMatcherFactory)desc.getExtensionClassInstance();
                if (factory != null) {
                    factories.add(factory);
                }
            }
        }
        return factories;
    }
}
