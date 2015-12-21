/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.extension;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.LoggingUtil;
import org.teiid.designer.extension.definition.ExtendableMetaclassNameProvider;
import org.teiid.designer.extension.definition.ModelExtensionAssistant;
import org.teiid.designer.extension.definition.ModelExtensionDefinition;
import org.teiid.designer.extension.definition.ModelExtensionDefinitionParser;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistant;
import org.teiid.designer.extension.definition.ModelObjectExtensionAssistantFactory;
import org.teiid.designer.extension.registry.ModelExtensionRegistry;


/**
 * @since 8.0
 */
public class ExtensionPlugin extends Plugin implements ExtensionConstants {

    /**
     * Visitor that collects mxd resources
     */
    public static class MxdResourceCollectorVisitor implements IResourceVisitor {

        List<IFile> resources = new ArrayList<IFile>();

        @Override
        public boolean visit(IResource resource) {
            /*
             * Always needs to return true since the search will not recurse into
             * projects and folders!
             *
             * However, we only add the resource if its an mxd.
             */
            if (! resource.exists() || resource.getType() != IResource.FILE)
                return true;

            if (MED_EXTENSION.equalsIgnoreCase(resource.getFileExtension()))
                resources.add((IFile) resource);

            return true;
        }

        /**
         * Get the found file resource collection
         *
         * @return collection of {@link IFile} resources
         */
        public Collection<IFile> getFileResources() {
            return Collections.unmodifiableCollection(resources);
        }
    }

    /**
     * The shared instance.
     */
    private static ExtensionPlugin plugin;

    /**
     * Logging Utility instance
     */
    public static PluginUtil Util = new LoggingUtil(PLUGIN_ID);

    /**
     * @return singleton instance
     */
    public static ExtensionPlugin getInstance() {
        return plugin;
    }

    private static IPath runtimePath;

    private ModelExtensionAssistantAggregator assistantAggregator;
    private Map<String, ExtendableMetaclassNameProvider> metaclassNameProvidersMap;

    /**
     * A factory that creates model object extension assistants.
     */
    private ModelObjectExtensionAssistantFactory modelObjectAssistantFactory;

    private ModelExtensionRegistry registry;

    private File schemaFile;

    /**
     * @return the assistant (never <code>null</code>)
     */
    public ModelObjectExtensionAssistant createDefaultModelObjectExtensionAssistant() {
        if (this.modelObjectAssistantFactory == null) {
            loadModelObjectExtensionAssistantFactories();

            // should always have at least one factory
            if (this.modelObjectAssistantFactory == null) {
                // should not happen
                this.modelObjectAssistantFactory = new ModelObjectExtensionAssistantFactory() {

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistantFactory#getModelObjectType()
                     */
                    @Override
                    public String getModelObjectType() {
                        return "NO MODEL OBJECT ASSISTANT FACTORY FOUND"; //$NON-NLS-1$
                    }

                    /**
                     * {@inheritDoc}
                     * 
                     * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistantFactory#createAssistant()
                     */
                    @Override
                    public ModelObjectExtensionAssistant createAssistant() {
                        Util.log(IStatus.ERROR, Messages.modelObjectExtensionAssistantFactoryNotFound);
                        return new ModelObjectExtensionAssistantAdapter();
                    }
                };
            }
        }

        return this.modelObjectAssistantFactory.createAssistant();
    }

    /**
     * @param namespacePrefix the namespace prefix to assign the assistant (cannot be <code>null</code> or empty)
     * @return the assistant (never <code>null</code>)
     */
    public ModelObjectExtensionAssistant createDefaultModelObjectExtensionAssistant( String namespacePrefix ) {
        CoreArgCheck.isNotEmpty(namespacePrefix, "namespacePrefix is empty"); //$NON-NLS-1$
        ModelObjectExtensionAssistant assistant = createDefaultModelObjectExtensionAssistant();
        assistant.createModelExtensionDefinition(namespacePrefix, null, null, null, null, null);
        return assistant;
    }

    /**
     * @return the assistant aggregator
     */
    public ModelExtensionAssistantAggregator getModelExtensionAssistantAggregator() {
    	if(this.assistantAggregator==null) {
    		initializeMedRegistry();
    	}
        return this.assistantAggregator;
    }

    /**
     * @return the model extension registry
     */
    public ModelExtensionRegistry getRegistry() {
        if (this.registry == null) {
            initializeMedRegistry();
        }
        return this.registry;
    }
    
    /*
     * Create and load the ModelExtensionRegistry
     */
    private void initializeMedRegistry() {
        try {
            this.registry = new ModelExtensionRegistry(getMedSchema());
            loadExtensibleMetamodelUriClassnameMap();
            this.registry.setMetamodelUris(this.metaclassNameProvidersMap.keySet());
            this.assistantAggregator = new ModelExtensionAssistantAggregator(this.registry);

            // Load Built-In MEDs into the Registry
            loadBuiltInMeds();

            // Load Imported and custom MEDs into the Registry
            loadDefinedMeds();

        } catch (Exception e) {
            Util.log(e);
        }
    }

    /**
     * @param metaclassUri
     * @return meta class name provider for given uri
     */
    public ExtendableMetaclassNameProvider getMetaclassNameProvider( String metaclassUri ) {
    	// if metaclassNameProvidersMap is null, MED registry has not yet been initialized
    	if(this.metaclassNameProvidersMap==null) {
    		initializeMedRegistry();
    	}
        return this.metaclassNameProvidersMap.get(metaclassUri);
    }

    /**
     * @param modelObject the model object being checked (cannot be <code>null</code>)
     * @return <code>true</code> if the model object is related to a model extension definition
     */
    public boolean isModelExtensionDefinitionRelated( Object modelObject ) {
        for (String namespacePrefix : this.registry.getAllNamespacePrefixes()) {
            ModelExtensionAssistant assistant = this.registry.getModelExtensionAssistant(namespacePrefix);

            try {
                if ((assistant instanceof ModelObjectExtensionAssistant)
                        && ((ModelObjectExtensionAssistant)assistant).isModelExtensionDefinitionRelated(modelObject)) {
                    return true;
                }
            } catch (Exception e) {
                Util.log(e);
            }
        }

        return false;
    }

    /**
     * @return the MED schema file
     * @throws Exception it there is a problem obtaining the schema file
     */
    public File getMedSchema() throws Exception {
        if (this.schemaFile == null) {
            Bundle bundle = Platform.getBundle(PLUGIN_ID);
            URL url = bundle.getEntry(ExtensionConstants.SCHEMA_FILENAME);

            if (url == null) {
                throw new Exception(NLS.bind(Messages.definitionSchemaFileNotFoundInWorkspace, PLUGIN_ID));
            }

            this.schemaFile = new File(FileLocator.toFileURL(url).getFile());

            if (!this.schemaFile.exists()) {
                throw new Exception(NLS.bind(Messages.definitionSchemaFileNotFoundInFilesystem, PLUGIN_ID));
            }
        }

        return this.schemaFile;
    }

    /**
     * Loads the extension point contributors which indicate the valid extensible metamodel URIs, plus the extendable classname
     * provider which correspond to each.
     */
    private void loadExtensibleMetamodelUriClassnameMap() {
        final String EXT_PT = PLUGIN_ID + ".extensibleMetamodelProvider"; //$NON-NLS-1$
        final String METAMODEL_URI_ELEMENT = "definition"; //$NON-NLS-1$
        final String METAMODEL_URI_ATTR = "metamodelUri"; //$NON-NLS-1$
        final String METACLASS_PROVIDER_ATTR = "metaclassProviderClass"; //$NON-NLS-1$

        this.metaclassNameProvidersMap = new HashMap();
        IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_PT);

        for (IConfigurationElement configElement : configElements) {
            String sElementName = configElement.getName();
            final String pluginId = configElement.getNamespaceIdentifier();

            if (METAMODEL_URI_ELEMENT.equals(sElementName)) {
                String metamodelUri = configElement.getAttribute(METAMODEL_URI_ATTR);

                if ((metamodelUri != null) && (metamodelUri.trim().length() != 0)) {
                    // Init the map entry with the metamodelUri
                    this.metaclassNameProvidersMap.put(metamodelUri, null);

                    // must have an extendable metaclass name provider
                    Object provider = null;

                    try {
                        provider = configElement.createExecutableExtension(METACLASS_PROVIDER_ATTR);
                    } catch (Exception e) {
                        // attribute CLASS_NAME could be missing or a no-arg constructor in was not found
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.problemConstructingMetaclassNameProviderClass,
                                          ModelExtensionAssistant.class.getSimpleName(), pluginId));
                        continue;
                    }

                    final Object metaClassnameProvider = provider;

                    if ((metaClassnameProvider != null) && !(metaClassnameProvider instanceof ExtendableMetaclassNameProvider)) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.incorrectMetaclassNameProviderClass,
                                                         metaClassnameProvider.getClass().getName(), pluginId));
                        continue;
                    }
                    // Get the MetamodelDescriptor for the name provider
                    this.metaclassNameProvidersMap.put(metamodelUri, (ExtendableMetaclassNameProvider)metaClassnameProvider);
                }
            }
        }
    }

    /*
     * Load the Built In MEDS (which are contributed from extension point) into the Registry.
     */
    private void loadBuiltInMeds() throws Exception {
        final String EXT_PT = PLUGIN_ID + ".modelExtensionProvider"; //$NON-NLS-1$
        final String PATH = "path"; //$NON-NLS-1$
        final String CLASS_NAME = "className"; //$NON-NLS-1$

        try {
            IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_PT);

            for (IConfigurationElement configElement : configElements) {
                final String pluginId = configElement.getNamespaceIdentifier();

                try {
                    // loop over model definitions
                    String tempPath = configElement.getAttribute(PATH);

                    // make sure path attribute exists
                    if (CoreStringUtil.isEmpty(tempPath)) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.missingDefinitionPath, pluginId, PATH));
                        continue;
                    }

                    // make sure path represents a file in workspace and on the filesystem
                    final Bundle bundle = Platform.getBundle(pluginId);
                    final IPath path = new Path(tempPath);
                    final URL url = FileLocator.find(bundle, path, null);

                    if (url == null) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.definitionFileNotFoundInWorkspace, path, pluginId));
                        continue;
                    }

                    final File defnFile = new File(FileLocator.toFileURL(url).getFile());

                    if (!defnFile.isFile() || !defnFile.exists()) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.definitionFileNotFoundInFilesystem, path, pluginId));
                        continue;
                    }

                    // must have a model extension assistant
                    Object tempAssistant = null;

                    try {
                        tempAssistant = configElement.createExecutableExtension(CLASS_NAME);
                    } catch (Exception e) {
                        // attribute CLASS_NAME could be missing or a no-arg constructor in was not found
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.problemConstructingModelExtensionAssistantClass,
                                          ModelExtensionAssistant.class.getSimpleName(), pluginId));
                        continue;
                    }

                    final Object assistant = tempAssistant;

                    if ((assistant != null) && !(assistant instanceof ModelExtensionAssistant)) {
                        Util.log(IStatus.ERROR,
                                 NLS.bind(Messages.incorrectModelExtensionAssistantClass, assistant.getClass().getName(), pluginId));
                        continue;
                    }

                    ModelExtensionDefinition definition;
					try {
						definition = getRegistry().addDefinition(new FileInputStream(defnFile),(ModelExtensionAssistant)assistant);
					} catch (Exception ex) {
                        Util.log(IStatus.ERROR, ex, NLS.bind(Messages.errorProcessingDefinitionFile, path, pluginId));
                        throw ex;
					}
					if(definition!=null) {
						definition.markAsBuiltIn();
					}
                    
                } catch (Exception e) {
                    Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorProcessingModelExtension, pluginId));
                }
            }
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorProcessingExtensionPoint, EXT_PT));
            throw e;
        }
    }

    /**
     * Loads the extensions for the factories that creatE model object assistants.
     */
    private void loadModelObjectExtensionAssistantFactories() {
        final String EXT_PT = PLUGIN_ID + ".modelObjectExtensionAssistantFactory"; //$NON-NLS-1$
        //        final String TYPE = "modelObjectType"; //$NON-NLS-1$
        final String CLASS_NAME = "className"; //$NON-NLS-1$

        try {
            IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(EXT_PT);

            for (IConfigurationElement configElement : configElements) {
                final String pluginId = configElement.getNamespaceIdentifier();

                try {
                    // loop over factories
                    // String type = configElement.getAttribute(TYPE);

                    // must have a factory class
                    Object tempFactory = null;

                    try {
                        tempFactory = configElement.createExecutableExtension(CLASS_NAME);
                    } catch (Exception e) {
                        // attribute CLASS_NAME could be missing or a no-arg constructor in was not found
                        Util.log(IStatus.ERROR, NLS.bind(Messages.problemConstructingModelExtensionAssistantFactoryClass,
                                                         ModelObjectExtensionAssistantFactory.class.getSimpleName(), pluginId));
                        continue;
                    }

                    final Object factory = tempFactory;

                    if ((factory != null) && !(factory instanceof ModelObjectExtensionAssistantFactory)) {
                        Util.log(IStatus.ERROR, NLS.bind(Messages.incorrectModelExtensionAssistantFactoryClass, factory.getClass()
                                                                                                                       .getName(),
                                                         pluginId));
                        continue;
                    }

                    this.modelObjectAssistantFactory = (ModelObjectExtensionAssistantFactory)factory;
                    break; // currently allow only one factory
                } catch (Exception e) {
                    Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorProcessingModelExtensionAssistantFactory, pluginId));
                }
            }
        } catch (Exception e) {
            Util.log(IStatus.ERROR, e, NLS.bind(Messages.errorProcessingExtensionPoint, EXT_PT));
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        plugin = this;

        // initialize logger first so that other methods can use logger
        ((LoggingUtil)Util).initializePlatformLogger(this);

        // initialized the Med schema
        try {
        	this.schemaFile = getMedSchema();
        } catch (Exception e) {
        	Util.log(e);
        	throw e;
        }

    }

    /**
     * {@inheritDoc}
     * <p>
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        try {
            // Save the used-defined and imported MEDs that are currently
            // in the registry (not necessary to save Built Ins)
            saveDefinedMeds();
        } finally {
            super.stop(context);
        }
    }

    /**
     * Stream needs to be closed by caller.
     * 
     * @param medFileStream the input stream for the model extension definition file being parsed (never <code>null</code>)
     * @return the model extension definition (never <code>null</code>)
     * @throws Exception if there is a problem parsing the input stream
     */
    public ModelExtensionDefinition parse( InputStream medFileStream ) throws Exception {
        ModelExtensionDefinitionParser parser = new ModelExtensionDefinitionParser(getMedSchema());
        return parser.parse(medFileStream, createDefaultModelObjectExtensionAssistant());
    }

    /*
     * Save the defined MEDS in the Registry to the defined file system location.
     */
    private void saveDefinedMeds() {
        if (this.registry != null) {
            // Save the registry User-Defined Definitions to the specified location
            this.registry.saveDefinedDefinitions(getUserDefinitionsPath());
        }
    }

    /*
     * Load the User-Defined MEDS into the Registry. These are the MEDS which were previously registered and saved at the last
     * shutdown.
     */
    private void loadDefinedMeds() throws CoreException {
        if (this.registry != null) {
            // Restore the User-Defined Definitions into the Registry
            final IStatus status = this.registry.restoreDefinedDefinitions(getUserDefinitionsPath());

            if (!status.isOK()) {
                Util.log(status);
            }

            if (status.getSeverity() == IStatus.ERROR) {
                throw new CoreException(status);
            }
        }
    }

    /**
     * @return the path string to the directory containing the persisted user definitions.
     */
    public String getUserDefinitionsPath() {
        return getRuntimePath().toFile().getAbsolutePath();
    }

    /**
     * @return the <code>designer.extension</code> plugin's runtime workspace path or the test runtime path
     * @since 6.0.0
     */
    public IPath getRuntimePath() {
        if (runtimePath == null) {
            runtimePath = ExtensionPlugin.getInstance().getStateLocation();
        }

        return (IPath)runtimePath.clone();
    }

    protected static class ModelObjectExtensionAssistantAdapter extends ModelObjectExtensionAssistant {
        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#cleanupModelResourceMEDs(java.lang.Object)
         * @throws Exception if error trying to detect and remove incompatible MEDs from model resource
         */
        @Override
    	public void cleanupModelResourceMEDs(Object modelResource) throws Exception {
        	// nothing to do
        }
        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getModelExtensionDefinition(java.lang.Object)
         */
        @Override
        public ModelExtensionDefinition getModelExtensionDefinition( Object modelObject ) throws Exception {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getOverriddenValue(java.lang.Object, java.lang.String)
         */
        @Override
        public String getOverriddenValue( Object modelObject,
                                          String propId ) throws Exception {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getOverriddenValues(java.lang.Object)
         */
        @Override
        public Properties getOverriddenValues( Object modelObject ) throws Exception {
            return new Properties();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getPropertyValue(java.lang.Object, java.lang.String)
         */
        @Override
        public String getPropertyValue( Object modelObject,
                                        String propId ) throws Exception {
            return null;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getPropertyValues(java.lang.Object)
         */
        @Override
        public Properties getPropertyValues( Object modelObject ) throws Exception {
            return new Properties();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#getSupportedNamespaces(java.lang.Object)
         */
        @Override
        public Collection<String> getSupportedNamespaces( Object modelObject ) throws Exception {
            return Collections.emptyList();
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#hasExtensionProperties(java.io.File)
         */
        @Override
        public boolean hasExtensionProperties( File file ) throws Exception {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#hasExtensionProperties(java.lang.Object)
         */
        @Override
        public boolean hasExtensionProperties( Object modelObject ) throws Exception {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#isModelExtensionDefinitionRelated(java.lang.Object)
         */
        @Override
        public boolean isModelExtensionDefinitionRelated( Object modelObject ) throws Exception {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#removeModelExtensionDefinition(java.lang.Object)
         */
        @Override
        public void removeModelExtensionDefinition( Object modelObject ) throws Exception {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#removeProperty(java.lang.Object, java.lang.String)
         */
        @Override
        public void removeProperty( Object modelObject,
                                    String propId ) throws Exception {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#saveModelExtensionDefinition(java.lang.Object)
         */
        @Override
        public void saveModelExtensionDefinition( Object modelObject ) throws Exception {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#setPropertyValue(java.lang.Object, java.lang.String, java.lang.String)
         */
        @Override
        public void setPropertyValue( Object modelObject,
                                      String propId,
                                      String newValue ) throws Exception {
            // nothing to do
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelExtensionAssistant#supportsMedOperation(java.lang.String, java.lang.Object)
         */
        @Override
        public boolean supportsMedOperation( String proposedOperationName,
                                             Object context ) {
            return false;
        }

        /**
         * {@inheritDoc}
         * 
         * @see org.teiid.designer.extension.definition.ModelObjectExtensionAssistant#supportsMyNamespace(java.lang.Object)
         */
        @Override
        public boolean supportsMyNamespace( Object modelObject ) throws Exception {
            return false;
        }
        
        /**
        * {@inheritDoc}
        * 
        */
		@Override
		public boolean supportsProperty(Object modelObject, String propId)
				throws Exception {
			// TODO Auto-generated method stub
			return false;
		}

    }

}
