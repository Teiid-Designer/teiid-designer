/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDPlugin;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.teiid.core.designer.CoreModelerPlugin;
import org.teiid.core.designer.ModelerCoreException;
import org.teiid.core.designer.ModelerCoreRuntimeException;
import org.teiid.core.designer.PluginUtil;
import org.teiid.core.designer.aspects.DeclarativeTransactionManager;
import org.teiid.core.designer.id.ObjectID;
import org.teiid.core.designer.util.CoreArgCheck;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.I18nUtil;
import org.teiid.core.designer.util.PluginUtilImpl;
import org.teiid.core.designer.util.Stopwatch;
import org.teiid.designer.ExtensionRegistryUtils;
import org.teiid.designer.IExtensionRegistryCallback;
import org.teiid.designer.WorkspaceUUIDService;
import org.teiid.designer.core.ModelerCore.EXTENSION_POINT.EOBJECT_MATCHER_FACTORY;
import org.teiid.designer.core.container.Container;
import org.teiid.designer.core.container.ContainerImpl;
import org.teiid.designer.core.container.ResourceDescriptor;
import org.teiid.designer.core.index.IndexSelectorFactory;
import org.teiid.designer.core.index.ModelWorkspaceIndexSelectorFactory;
import org.teiid.designer.core.metamodel.MetamodelDescriptor;
import org.teiid.designer.core.metamodel.MetamodelRegistry;
import org.teiid.designer.core.metamodel.MetamodelRegistryImpl;
import org.teiid.designer.core.metamodel.ResourceLoadOptionContributor;
import org.teiid.designer.core.refactor.IRefactorResourceListener;
import org.teiid.designer.core.refactor.RefactorResourceEvent;
import org.teiid.designer.core.resource.EmfResource;
import org.teiid.designer.core.resource.EmfResourceSet;
import org.teiid.designer.core.resource.EmfResourceSetImpl;
import org.teiid.designer.core.search.MetadataSearch;
import org.teiid.designer.core.search.MetadataSearchImpl;
import org.teiid.designer.core.spi.RegistrySPI;
import org.teiid.designer.core.transaction.UnitOfWork;
import org.teiid.designer.core.types.DatatypeManager;
import org.teiid.designer.core.types.DatatypeManagerLifecycle;
import org.teiid.designer.core.util.FlatRegistry;
import org.teiid.designer.core.util.InvocationFactoryHelper;
import org.teiid.designer.core.util.StartupLogger;
import org.teiid.designer.core.util.WorkspaceUriPathConverter;
import org.teiid.designer.core.validation.ValidationRuleManager;
import org.teiid.designer.core.workspace.DotProjectUtils;
import org.teiid.designer.core.workspace.ModelProject;
import org.teiid.designer.core.workspace.ModelResource;
import org.teiid.designer.core.workspace.ModelStatusImpl;
import org.teiid.designer.core.workspace.ModelWorkspace;
import org.teiid.designer.core.workspace.ModelWorkspaceException;
import org.teiid.designer.core.workspace.ModelWorkspaceItem;
import org.teiid.designer.core.workspace.ModelWorkspaceManager;
import org.teiid.designer.core.workspace.ModelWorkspaceManagerSaveParticipant;
import org.teiid.designer.query.IQueryService;
import org.teiid.designer.runtime.registry.TeiidRuntimeRegistry;
import org.teiid.designer.runtime.spi.EventManager;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory;
import org.teiid.designer.runtime.spi.IExecutionAdminFactory.SupportLevel;
import org.teiid.designer.runtime.spi.ITeiidServer;
import org.teiid.designer.runtime.spi.ITeiidServerManager;
import org.teiid.designer.runtime.spi.ITeiidServerVersionListener;
import org.teiid.designer.runtime.version.spi.ITeiidServerVersion;
import org.teiid.designer.type.IDataTypeManagerService;

/**
 * The main plugin class to be used in the desktop.
 *
 * @since 8.0
 */
public class ModelerCore extends Plugin implements DeclarativeTransactionManager {

    /**
	 * Options for whether to register an item with this class' registry
	 */
	public static enum RegistryOption {
		/**
		 * Register the given item in this class' registry
		 */
		REGISTER, 
		
		/**
		 * Do No register the given item in this class' registry
		 */
		NO_REGISTER;
	}

    /**
     * The bundle ID of the Designer feature.
     * 
     * @since 6.1.0
     */
    private static final String FEATURE_ID = "org.teiid.designer"; //$NON-NLS-1$

    /**
     * The name of the file containing the {{@link #workspaceUuid workspace UUID} for the current workspace: {@value}
     */
    public static final String WORKSPACE_UUID_FILE = "workspace.uuid"; //$NON-NLS-1$

    private static final String MINIMUM_JAVA_VERSION = "1.6"; //$NON-NLS-1$

    /**
     * The plug-in identifier of the Modeler Resources support (value <code>"org.teiid.designer.core"</code>).
     */
    public static final String PLUGIN_ID = "org.teiid.designer.core"; //$NON-NLS-1$

    public static final String PACKAGE_ID = ModelerCore.class.getPackage().getName();

    /**
     * The identifier for the model builder (value <code>"org.teiid.designer.core.modelBuilder"</code>).
     */
    public static final String BUILDER_ID = PLUGIN_ID + ".modelBuilder"; //$NON-NLS-1$
    
    public static final String VDB_BUILDER_ID = "org.teiid.designer.vdb.ui.vdbBuilder"; //$NON-NLS-1$

    /**
     * The identifier for the Modeling nature (value <code>"org.teiid.designer.core.modelnature"</code>). The presence of this
     * nature on a project indicates that it is modeling-capable.
     * 
     * @see org.eclipse.core.resources.IProject#hasNature(java.lang.String)
     */
    public static final String NATURE_ID = PLUGIN_ID + ".modelNature"; //$NON-NLS-1$

    /**
     * The project nature that identifies a project as one that should be hidden in the workspace.
     * 
     * @since 5.5.3
     */
    public static final String HIDDEN_PROJECT_NATURE_ID = PLUGIN_ID + ".hiddenProjectNature"; //$NON-NLS-1$

    public static final String[] NATURES = new String[] {NATURE_ID};

    private static final String USERFILES_FOLDERNAME = "user-files"; //$NON-NLS-1$

    public static final String UDF_MODEL_NAME = "FunctionDefinitions.xmi"; //$NON-NLS-1$

    public static final String UDF_PROJECT_NAME = "org.teiid.designer.udf"; //$NON-NLS-1$

    private static final String XML_EXTENSIONS_PROJECT_NAME = "XMLExtensionsProject"; //$NON-NLS-1$

    private static final String BUILTIN_MEDS_PROJECT_NAME = "BuiltInMedsProject"; //$NON-NLS-1$

    private static final String[] RESERVED_PROJECT_NAMES = {USERFILES_FOLDERNAME, UDF_PROJECT_NAME, XML_EXTENSIONS_PROJECT_NAME, BUILTIN_MEDS_PROJECT_NAME};
    /**
     * <p>
     * The file extension of Teiid Designer model files.
     * </p>
     * 
     * @since 4.0
     */
    public static final String MODEL_FILE_EXTENSION = ".xmi"; //$NON-NLS-1$

    public static final String DECLATIVE_TXN = "Declarative Transaction"; //$NON-NLS-1$

    /**
     * <p>
     * The file extension of Xml Schema model files.
     * </p>
     * 
     * @since 4.0
     */
    public static final String XSD_FILE_EXTENSION = ".xsd"; //$NON-NLS-1$

    /**
     * <p>
     * The file extension of Virtual Database (VDB) files.
     * </p>
     * 
     * @since 4.0
     */
    public static final String VDB_FILE_EXTENSION = ".vdb"; //$NON-NLS-1$

    /**
     * The file extension of Model Extension Definition (MED) files.
     * 
     * @since 7.6
     */
    public static final String MED_FILE_EXTENSION = ".mxd"; //$NON-NLS-1$

    /**
     * The file extension of the Eclipse <code>.project</code> file.
     * 
     * @since 6.0.0
     */
    public static final String DOT_PROJECT_EXTENSION = "project"; //$NON-NLS-1$

    /**
     * The attribute name to be used to lookup on a IMarker the text to display when decorating the resource in Model Explorer.
     */
    public static final String MARKER_PROBLEM_DECORATOR_TEXT = PLUGIN_ID + ".problemDecoratorText"; //$NON-NLS-1$

    /**
     * The property name to be used to lookup on a IMarker, an URI used in locating the logical EObject referenced by the IMarker.
     */
    public static final String MARKER_URI_PROPERTY = PLUGIN_ID + ".markerURI"; //$NON-NLS-1$

    /**
     * The property name to be used to lookup on a IMarker, an URI used in locating the physical EObject referenced by the IMarker.
     */
    public static final String TARGET_MARKER_URI_PROPERTY = PLUGIN_ID + ".targetMarkerURI"; //$NON-NLS-1$

    /** The URI used for all external references to the XMLSchema models */
    public static final String XML_SCHEMA_GENERAL_URI = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$

    public static final String XML_MAGIC_SCHEMA_GENERAL_URI = "http://www.w3.org/2001/MagicXMLSchema"; //$NON-NLS-1$

    public static final String XML_SCHEMA_INSTANCE_GENERAL_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$

    public static final String XML_XSD_GENERAL_URI = "http://www.w3.org/2001/xml"; //$NON-NLS-1$
    /** The eclipse installation specific URI for the XMLSchema models */
    public static String XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX = "platform:/plugin/org.eclipse.xsd_2.1.0/"; //$NON-NLS-1$

    static {
        try {
            // Try to reset the base URL assuming Eclipse runtime environment. If this fails
            // then fall back on hardcoded base URL
            XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX = XSDPlugin.INSTANCE.getBaseURL().toString();
        } catch (final Exception e) {
            // do nothing
        }
    }

    public static final String XML_SCHEMA_ECLIPSE_PLATFORM_URI = XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX
                                                                 + "cache/www.w3.org/2001/XMLSchema.xsd"; //$NON-NLS-1$

    public static final String XML_MAGIC_SCHEMA_ECLIPSE_PLATFORM_URI = XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX
                                                                       + "cache/www.w3.org/2001/MagicXMLSchema.xsd"; //$NON-NLS-1$
    public static final String XML_SCHEMA_INSTANCE_ECLIPSE_PLATFORM_URI = XML_SCHEMA_ECLIPSE_PLATFORM_URI_PREFIX
                                                                          + "cache/www.w3.org/2001/XMLSchema-instance.xsd"; //$NON-NLS-1$
    
    /**
     * The {@link IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName) session property} key dictating this resource
     * as a duplicate of another model.
     */
    public final static QualifiedName DUPLICATE_MODEL_OF_IPATH_KEY = new QualifiedName(
                                                                                       "org.teiid.designer.core", "pathToDuplicateModel"); //$NON-NLS-1$  //$NON-NLS-2$
    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$
    /**
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

    private static final String I18N_PREFIX = I18nUtil.getPropertyPrefix(ModelerCore.class);

    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static Plugin MODELER_CORE_PLUGIN = null;

    private static final Object CONTAINER_LOCK = new Object();

    public static final String DEFAULT_CONTAINER_KEY = "Modeler Container"; //$NON-NLS-1$
    
    public static final String EXTERNAL_RESOURCE_CONTAINER_KEY = "External Resource Container"; //$NON-NLS-1$
    
    public static final String EXTERNAL_RESOURCE_SETS_KEY = "External Resource Sets"; //$NON-NLS-1$
    
    public static final String INVOCATION_FACTORY_HELPERS_KEY = "Invocation Factory Helpers"; //$NON-NLS-1$

    public static final String CONFIGURATION_KEY = "Configuration"; //$NON-NLS-1$
    
    public static final String DEFAULT_DATATYPE_MANAGER_KEY = "Default DataType Manager"; //$NON-NLS-1$
    
    public static final String METAMODEL_REGISTRY_KEY = "Default Metamodel Registry";  //$NON-NLS-1$
    
    public static final String MODEL_EDITOR_KEY = "Model Editor";  //$NON-NLS-1$
    
    public static final String TRANSFORMATION_PREFERENCES_KEY = "Transformation Preferences"; //$NON-NLS-1$
    
    public static final String VALIDATION_PREFERENCES_KEY = "Validation Preferences"; //$NON-NLS-1$
    
    public static final String VALIDATION_RULE_MANAGER_KEY = "Validation Rule Manager"; //$NON-NLS-1$
    
    public static final String UUID_KEY = "Workspace UUID"; //$NON-NLS-1$
    
    public static final String WORKSPACE_KEY = "Workspace"; //$NON-NLS-1$
    
    // Registry
    private static RegistrySPI registry = new FlatRegistry();

    private static final Object METAMODEL_REGISTRY_LOCK = new Object();

    public static boolean DEBUG = false;
    public static boolean DEBUG_MODELER_CORE_INIT = false;

    public static boolean DEBUG_METAMODEL = false;

    public static boolean DEBUG_VALIDATION = false;

    public static boolean DEBUG_TRANSACTION = false;

    public static boolean DEBUG_TRANSACTION_INVOCATIONS = false;
    public static boolean DEBUG_MODEL_WORKSPACE = false;
    public static boolean DEBUG_MODEL_WORKSPACE_EVENT = false;
    public static boolean DEBUG_MODEL_WORKSPACE_SAVE = false;
    public static boolean DEBUG_PROJECT_BUILDER = false;
    public static boolean DEBUG_NOTIFICATIONS = false;
    public static boolean DEBUG_TEAM = false;
    public static boolean DEBUG_BRIDGE = false;
    public static boolean DEBUG_XML = false;
    public static boolean DEBUG_GEMINI = false;
    public static boolean DEBUG_QUERY_RESOLUTION = false;
    public static boolean DEBUG_VDB_EDITING_CONTEXT = false;
    // If true the workspace build process will use preference settings for
    // validation severity, otherwise, if false the build process will ignore
    // user preferences and use default severity levels for all validation
    // rules.
    private static boolean IGNORE_VALIDATION_PREFERNCES_ON_BUILD = false;

    private static ITeiidServerManager teiidServerManager = null;

    /**
     * mutex lock for initialisation of the TeiidServerManager
     */
    private static Object mutexObject = new Object();

    /**
     * Add all model resource sets known through the EXTERNAL_RESOURCE_SET extension to the specified container
     * 
     * @param container
     */
    public static void addExternalResourceSets( final Container container ) {
        // Add each external resource set found in the configuration
        // to the container as a delegate resource set
        final List<ResourceSet> extRsrcSets = getExternalResourceSets();
        for (final ResourceSet rsrcSet : extRsrcSets) {
            container.addExternalResourceSet(rsrcSet);
            if (DEBUG) {
                Util.log(IStatus.INFO,
                         ModelerCore.Util.getString("ModelerCore.DEBUG.Added_external_resource_set_to_the_container._1", rsrcSet)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Commit the current UnitOfWork for the model container
     */
    public static void commitTxn() {
        try {
            final UnitOfWork uow = getCurrentUoW();
            if (uow != null) {
                uow.commit();
            }
        } catch (final ModelerCoreException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * Returns the {@link ModelWorkspaceItem}corresponding to the given file, or <code>null</code> if unable to associate the given
     * file with a {@link ModelWorkspaceItem}.
     * <p>
     * The file must be one of:
     * <ul>
     * <li>a <code>.mmm</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * <li>a <code>.xml</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * </ul>
     * <p>
     * Creating a {@link ModelWorkspaceItem}has the side effect of creating and opening all of the item's parents if they are not
     * yet open.
     * 
     * @param the given file
     * @return the {@link ModelWorkspaceItem}corresponding to the given file, or <code>null</code> if unable to associate the given
     *         file with a {@link ModelWorkspaceItem workspace item}
     */
    public static ModelResource create( final IFile file ) {
        return ModelWorkspaceManager.create(file, null);
    }

    /**
     * Returns the {@link ModelProject}corresponding to the given project.
     * <p>
     * Creating a {@link ModelProject}has the side effect of creating and opening all of the project's parents if they are not yet
     * open.
     * <p>
     * Note that no check is done at this time on the existence or the modeling nature of this project.
     * 
     * @param project the given project
     * @return the {@link ModelProject}corresponding to the given project, null if the given project is null
     */
    public static ModelProject create( final IProject project ) {
        if (project == null || !hasModelNature(project)) {
            return null;
        }
        final ModelWorkspace modelWorkspace = ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
        return modelWorkspace.getModelProject(project);
    }

    /**
     * Returns the {@link ModelWorkspaceItem}corresponding to the given resource, or <code>null</code> if unable to associate the
     * given resource with a {@link ModelWorkspaceItem workspace item}.
     * <p>
     * The resource must be one of:
     * <ul>
     * <li>a project - the element returned is the corresponding {@link ModelProject}</li>
     * <li>a <code>.xmi</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * <li>a <code>.xsd</code> file - the item returned is the corresponding {@link ModelResource}</li>
     * <li>the workspace root resource - the element returned is the {@link ModelWorkspace}</li>
     * </ul>
     * <p>
     * Creating a {@link ModelWorkspaceItem}has the side effect of creating and opening all of the item's parents if they are not
     * yet open.
     * 
     * @param resource the given resource
     * @return the {@link ModelWorkspaceItem}corresponding to the given resource, or <code>null</code> if unable to associate the
     *         given resource with a {@link ModelWorkspaceItem workspace item}
     */
    public static ModelWorkspaceItem create( final IResource resource ) {
        return ModelWorkspaceManager.create(resource, null);
    }

    /**
     * Returns the {@link ModelWorkspace}
     * 
     * @param root the given root
     * @return the ModelWorkspace, or <code>null</code> if the root is null
     */
    public static ModelWorkspace create( final IWorkspaceRoot root ) {
        if (root == null) {
            return null;
        }
        return ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
    }

    /**
     * Returns the {@link ModelWorkspaceItem}corresponding to the given handle identifier generated by
     * {@link ModelWorkspaceItem#getHandleIdentifier()}, or <code>null</code> if unable to create the associated item.
     */
    public static ModelWorkspaceItem create( final String handleIdentifier ) {
        if (handleIdentifier == null) {
            return null;
        }
        return ModelWorkspaceManager.getModelWorkspaceManager().getHandleFromMemento(handleIdentifier);
    }

    /**
     * Create a new {@link org.teiid.designer.core.container.Container}instance. The new container will be initialized with a
     * reference to the metamodel registry along with references to the resource factories needed to create new resource instances.
     * The container will be loaded with all external resources found through the "externalResource" extension point which includes
     * the "built-in" datatypes resource.
     *
     * Assumes the container should be registered with {@link ModelerCore}'s registry.
     * 
     * @param containerName name of the new container
     * @return the new container 
     * 
     * @throws CoreException
     */
    public static Container createContainer( final String containerName ) throws CoreException {
    	return createContainer(containerName, RegistryOption.REGISTER);
    }
    
    /**
     * Create a new {@link Container}instance. The new container will be initialized with a
     * reference to the metamodel registry along with references to the resource factories needed to create new resource instances.
     * The container will be loaded with all external resources found through the "externalResource" extension point which includes
     * the "built-in" datatypes resource.
     * 
     * @param containerName name of the new container
     * @param registryOption whether the created container should be registerd
     * @return @throws CoreException
     */
    public static Container createContainer( final String containerName, RegistryOption registryOption ) throws CoreException {
        final Container container = createEmptyContainer(containerName);

        // Add the external resource sets to the container as delegate resource sets
        // This must be done before the workspace's datatype manager is initialized
        // since it requires finding the built-in datatypes model in one of the external
        // resource sets.
       
        addExternalResourceSets(container);
       
        // Do this only if we are in the Eclipse plugin environment ...
        if (MODELER_CORE_PLUGIN != null) {
            // RMH 10/19/04 - Might be able to do this even outside the Eclipse environment if
            // we hard-code to load the WorkspaceDatatypeManager reflectively
            if (container instanceof ContainerImpl) {
                // Each container gets its own datatype manager. The datatype manager used is a
                // WorkspaceDatatypeManager instance but this manager only uses information
                // from the workspace when it is associated with the workspace model container
                // and is running in an Eclipse runtime environment. For all other containers,
                // the WorkspaceDatatypeManager only searches the models in its container.
                final ContainerImpl ci = (ContainerImpl)container;
                final DatatypeManager datatypeManager = createDatatypeManager();
                if (datatypeManager != null) {
                    ci.setDatatypeManager(datatypeManager);
                    if (datatypeManager instanceof DatatypeManagerLifecycle) {
                        // Initialize
                        ((DatatypeManagerLifecycle)datatypeManager).initialize(ci);
                    }
                }
            }
        }

        // Set the default container options
        container.setOptions(getDefaultContainerOptions());

        return container;
    }

    /**
     * Construct a new DatatypeManager instance.
     * 
     * @return the DatatypeManager; null if no extension could be found
     */
    protected static DatatypeManager createDatatypeManager() {

        // Ensure that the metamodel registry is initialized
        getMetamodelRegistry();

        // Instantiate the first DatatypeManager extension found in the configuration.
        // This instance is used to resolving built-in datatypes
        final Collection desc = getConfiguration().getDatatypeManagerDescriptors();
        if (desc.isEmpty()) {
            Util.log(IStatus.ERROR,
                     ModelerCore.Util.getString("ModelerCore.Error,_no_DatatypeManager_extensions_were_found_in_the_plugin_registry_1")); //$NON-NLS-1$
        }

        DatatypeManager dtMgr = null;
        final Iterator iter = desc.iterator();
        if (iter.hasNext()) {
            final ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
            dtMgr = (DatatypeManager)descriptor.getNewExtensionClassInstance();
        }

        if (dtMgr == null) {
            Util.log(IStatus.ERROR, ModelerCore.Util.getString("ModelerCore.Error_creating_new_instance_of_a_DatatypeManager_2")); //$NON-NLS-1$
            throw new ModelerCoreRuntimeException(
                                                  ModelerCore.Util.getString("ModelerCore.Error_creating_the_DatatypeManager_instance_1")); //$NON-NLS-1$
        }

        // Register all of the metamodel descriptors ...
        if (DEBUG) {
            Util.log(IStatus.INFO, ModelerCore.Util.getString("ModelerCore.DEBUG.Created_new_instance_of_a_DatatypeManager_1")); //$NON-NLS-1$
        }
        return dtMgr;
    }

    /**
     * Create a new empty {@link org.teiid.designer.core.container.Container}instance. The new container will be initialized
     * with a reference to the metamodel registry along with references to the resource factories needed to create new resource
     * instances. The container will <b>not </b> be loaded or have access to any of the external resources including the "built-in"
     * datatypes resource.
     * 
     * Assumes the new container should be registered with the {@link ModelerCore} registry.
     * 
     * @param containerName name of the empty container
     * @return @throws CoreException
     */
    public static Container createEmptyContainer( final String containerName ) throws CoreException {
    	return createEmptyContainer(containerName, RegistryOption.REGISTER);
    }
  
    /**
     * Create a new empty {@link Container}instance. The new container will be initialized
     * with a reference to the metamodel registry along with references to the resource factories needed to create new resource
     * instances. The container will <b>not </b> be loaded or have access to any of the external resources including the "built-in"
     * datatypes resource.
     * 
     * @param containerName name of the empty container
     * @param registryOption of whether the container should be registered with {@link ModelerCore}. Temporary
     *                 containers may prefer not to be registered.
     * @return @throws CoreException
     */
    public static Container createEmptyContainer( final String containerName, RegistryOption registryOption ) throws CoreException {
        Container container = null;
        try {
            if (DEBUG_MODEL_WORKSPACE) {
                Util.log(IStatus.INFO, ModelerCore.Util.getString("ModelWorkspaceManager.DEBUG.Creating_the_model_container")); //$NON-NLS-1$
            }

            // Create the container
            container = new ContainerImpl();

            // Set the name ...
            container.setName(containerName);

            // Set the metamodel registry reference ...
            container.setMetamodelRegistry(getMetamodelRegistry());

            // Ensure the container is registered with its name ...
            final String name = container.getName();
            if (name != null && name.length() != 0 && RegistryOption.REGISTER.equals(registryOption)) {
                registry.register(name, container, Container.CONTAINER_NAME_PROPERTY);
            }
            container.start();

            // Set the EmfUriHelper to use when resolving EObject URIs. The
            // EmfUriHelper is used by the EmfResource.getEObject(URI,boolean)
            // method to convert workspace relative URIs of the form
            // "/Project/.../Resource" into absolute file URIs. Workspace relative
            // URIs are sometimes encountered in cross model references, hrefs in
            // the xmi files, and are not resolved using the normal URI resolution
            // logic found in EMF.
            final ResourceSet resourceSet = ((ContainerImpl)container).getResourceSet();
            if (resourceSet instanceof EmfResourceSetImpl) {
                ((EmfResourceSetImpl)resourceSet).setUriPathConverter(new WorkspaceUriPathConverter());
            }

        } catch (final ModelerCoreException e) {
            throw new CoreException(
                                    new Status(
                                               IStatus.ERROR,
                                               ModelerCore.PLUGIN_ID,
                                               1,
                                               ModelerCore.Util.getString("ModelerCore.Error_adding_resource_descriptors_to_the_container"), e)); //$NON-NLS-1$
        }

        // Register the known resource descriptors
        final Iterator iter = getConfiguration().getResourceDescriptors().iterator();
        final List errors = new ArrayList();
        while (iter.hasNext()) {
            final ResourceDescriptor resourceDescriptor = (ResourceDescriptor)iter.next();
            try {
                container.addResourceDescriptor(resourceDescriptor);
            } catch (final ModelerCoreException e) {
                final IStatus status = e.getStatus();
                errors.add(status);
            }
        }

        if (!errors.isEmpty()) {
            final IStatus[] statuses = (IStatus[])errors.toArray(new IStatus[errors.size()]);
            throw new CoreException(
                                    new MultiStatus(
                                                    ModelerCore.PLUGIN_ID,
                                                    0,
                                                    statuses,
                                                    ModelerCore.Util.getString("ModelerCore.Error_adding_resource_descriptors_to_the_container"), //$NON-NLS-1$
                                                    null));
        }

        return container;
    }

    /**
     * Create an object that performs searches for model object instances.
     * 
     * @return the search object; never null
     */
    public static MetadataSearch createMetadataSearch() {
        final IndexSelectorFactory factory = new ModelWorkspaceIndexSelectorFactory();
        final ModelWorkspace workspace = ModelerCore.getModelWorkspace();
        return new MetadataSearchImpl(workspace, factory);
    }

    /**
     * Returns the DatatypeManager instance associated built-in datatypes only
     * 
     * @return the DatatypeManager for built-in types
     */
    public static DatatypeManager getBuiltInTypesManager() {
        DatatypeManager builtInTypesManager = null;
        // Get the workspace datatype manager ...
        final DatatypeManager wsDatatypeManager = getWorkspaceDatatypeManager();
        if (wsDatatypeManager != null) {
            builtInTypesManager = wsDatatypeManager.getBuiltInTypeManager();
        }
        return builtInTypesManager;
    }

    public static Configuration getConfiguration() {
        Configuration configuration = registry.lookup(CONFIGURATION_KEY, Configuration.class);
        if (configuration == null) {
            configuration = registry.register(CONFIGURATION_KEY, new Configuration());
        }
        
        return configuration;
    }

    /**
     * Return the Container in which the supplied object exists. <i>Note: if at all possible, do not pass in a built-in datatypes,
     * since all built-ins are equivalent and shared among all containers. In such cases, the {@link #getModelContainer() model
     * container} is returned.</i>
     * 
     * @param obj the EObject.
     * @return
     * @since 4.2
     */
    public static Container getContainer( final EObject obj ) {
        if (obj == null) {
            return null;
        }
        final Resource objResource = obj.eResource();
        if (objResource != null) {
            final ResourceSet resourceSet = objResource.getResourceSet();
            if (resourceSet instanceof EmfResourceSet) {
                final EmfResourceSet emfResourceSet = (EmfResourceSet)resourceSet;
                final Container container = emfResourceSet.getContainer();
                return container;
            }
            if (resourceSet instanceof Container) {
                return (Container)resourceSet;
            }
            if (resourceSet == XSDSchemaImpl.getGlobalResourceSet()) {
                // 'obj' is a built-in type within the Schema of Schemas, so we will
                // find it through the model container ...
                return registry.lookup(DEFAULT_CONTAINER_KEY, Container.class);
            }
        }
        return null;
    }

    /**
     * Determine the {@link Container}in which the supplied resource is loaded.
     * 
     * @param resource the resource; may not be null
     * @return the Container in which the resource is loaded, or null if the resource is not loaded in a Container.
     */
    public static Container getContainer( final Resource resource ) {
        CoreArgCheck.isNotNull(resource);
        if (resource instanceof EmfResource) {
            return ((EmfResource)resource).getContainer();
        } else if (resource instanceof XSDResourceImpl) {
            final ResourceSet rs = resource.getResourceSet();
            if (rs instanceof EmfResourceSet) {
                return ((EmfResourceSet)rs).getContainer();
            }
        }
        return null;
    }

    /**
     * @return the current UoW for the defaultModelContainer
     */
    public static UnitOfWork getCurrentUoW() {
        if (registry.lookup(DEFAULT_CONTAINER_KEY, Container.class) != null) {
            try {
                return getModelContainer().getEmfTransactionProvider().getCurrent();
            } catch (final CoreException e) {
                Util.log(IStatus.ERROR, e, e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the DatatypeManager instance associated with the container where the supplied object exists.
     * 
     * @return the DatatypeManager for the object's container, or null if the object is not associated with a container.
     */
    public static DatatypeManager getDatatypeManager() {
        DatatypeManager manager = registry.lookup(DEFAULT_DATATYPE_MANAGER_KEY, DatatypeManager.class);
        if (manager == null) {
            manager = registry.register(DEFAULT_DATATYPE_MANAGER_KEY, createDatatypeManager());
        }
        return manager;
    }

    /**
     * Returns the DatatypeManager instance associated with the container where the supplied object exists.
     * 
     * @return the DatatypeManager for the object's container, or null if the object is not associated with a container.
     */
    public static DatatypeManager getDatatypeManager( final EObject object ) {
        return getDatatypeManager(object, false);
    }

    /**
     * Returns the DatatypeManager instance associated with the container where the supplied object exists.
     * 
     * @return the DatatypeManager for the object's container, or the {@link #getWorkspaceDatatypeManager() workspace datatype
     *         manager} if the object is not associated with a container.
     */
    public static DatatypeManager getDatatypeManager( final EObject object,
                                                      final boolean defaultToWorkspaceMgr ) {
        if (defaultToWorkspaceMgr && object == null) {
            // Short circuit so that when 'object' is null be we will accept the workspace, just return the workspace ...
            return ModelerCore.getWorkspaceDatatypeManager();
        }
        CoreArgCheck.isNotNull(object);

        // if DatatypeManager is set (thru UnitTest, use it)
        DatatypeManager datatypeManager = registry.lookup(DEFAULT_DATATYPE_MANAGER_KEY, DatatypeManager.class);
        if (datatypeManager != null) {
            return datatypeManager;
        }

        // Look up the object's container ...
        DatatypeManager result = null;

        final Container container = getContainer(object);
        if (container != null) {
            result = container.getDatatypeManager();
        }

        if (defaultToWorkspaceMgr && result == null) {
            result = ModelerCore.getWorkspaceDatatypeManager();
        }

        return result;
    }

    /**
     * Return the default configuration options for a container.
     * 
     * @return
     * @since 4.3
     */
    private static Map getDefaultContainerOptions() {
        final Map options = new HashMap();

        // Check if running in an Eclipse runtime environment ...
        Configuration configuration = registry.lookup(CONFIGURATION_KEY, Configuration.class);
        if (getPlugin() != null && configuration != null) {

            // Create a map of the XML loading options and add all contributions by the extensions
            final XMLResource.XMLMap xmlMap = new XMLMapImpl();
            for (final Iterator iter = configuration.getResourceLoadOptions().iterator(); iter.hasNext();) {
                final ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
                final ResourceLoadOptionContributor optionContributor = (ResourceLoadOptionContributor)descriptor.getExtensionClassInstance();
                if (optionContributor != null) {
                    optionContributor.addMappings(xmlMap);
                }
            }

            // Add the XML loading options map to the container's options map. This will overwrite
            options.put(XMLResource.OPTION_XML_MAP, xmlMap);
        }
        return options;
    }

    /**
     * Obtains the default preferences values of the plugin with the specified identifier. <strong>This method should be used
     * instead of {@link Plugin#getPluginPreferences()} to obtain default values.</strong>
     * 
     * @param pluginId the plugin ID (may not be <code>null</code>)
     * @return the preferences (never <code>null</code>)
     */
    public static IEclipsePreferences getDefaultPreferences( final String pluginId ) {
        CoreArgCheck.isNotNull(pluginId, "pluginId"); //$NON-NLS-1$
        return new DefaultScope().getNode(pluginId);
    }

    /**
     * Return the {@link org.teiid.designer.core.container.Container}instance used to hold all external resources. This
     * container is used as an external resource set for all other containers created in the modeler thereby allowing those
     * containers access to the same global resources. The external resource container will be loaded with all external resources
     * found through the "externalResource" extension point which includes the "built-in" datatypes resource.
     * 
     * @param containerName
     * @return @throws CoreException
     */
    public synchronized static Container getExternalResourceContainer() throws CoreException {
        Container externalResourceContainer = registry.lookup(EXTERNAL_RESOURCE_CONTAINER_KEY, Container.class);
        
        if (externalResourceContainer == null) {
            externalResourceContainer = createEmptyContainer(EXTERNAL_RESOURCE_CONTAINER_KEY);
            registry.register(EXTERNAL_RESOURCE_CONTAINER_KEY, externalResourceContainer);
            
            // Load the container with all external resources ...
            loadExternalResources(externalResourceContainer);
            
        }

        return externalResourceContainer;
    }

    /**
     * Load all model resource sets known through the EXTERNAL_RESOURCE_SET extension into the container
     * 
     * @param container
     */
    public synchronized static List<ResourceSet> getExternalResourceSets() {
        List<ResourceSet> externalResourceSets = registry.lookup(EXTERNAL_RESOURCE_SETS_KEY, List.class);
        
        if (externalResourceSets == null) {
            // Instantiate each external resource set extension found in the configuration
            externalResourceSets = registry.register(EXTERNAL_RESOURCE_SETS_KEY, new ArrayList<ResourceSet>());

            // Add the external resource container holding all shared external resources
            // to be shared by containers throughout the modeler
            try {
                final ResourceSet rsrcSet = getExternalResourceContainer();
                if (rsrcSet != null) {
                    externalResourceSets.add(rsrcSet);
                }
            } catch (final CoreException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }

            // Add in all other resource sets known through the EXTERNAL_RESOURCE_SET extension
            final Collection desc = getConfiguration().getExternalResourceSetDescriptors();
            for (final Iterator iter = desc.iterator(); iter.hasNext();) {
                final ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
                final ExternalResourceSet extRsrcSet = (ExternalResourceSet)descriptor.getExtensionClassInstance();
                if (extRsrcSet != null) {
                    final ResourceSet rsrcSet = extRsrcSet.getResourceSet();
                    if (rsrcSet != null) {
                        externalResourceSets.add(rsrcSet);
                    }
                }
            }
        }
        
        return externalResourceSets;
    }
    
    /**
     * Determine whether the given resource is part of one of the 
     * external resource sets
     * 
     * @param resource
     * @return
     */
    public static boolean isResourceInExternalResourceSet(Resource resource) {
        if (resource == null) {
            return false;
        }
        
        List<ResourceSet> sets = getExternalResourceSets();
        if (sets == null || sets.isEmpty()) {
            return false;
        }
        
        for (ResourceSet set : sets) {
            if (resource.getResourceSet() == set) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Load all InvocationFactoryHelper instances known through the INVOCATION_FACTORY_HELPER extension into the container
     * 
     * @param container
     */
    public static List<InvocationFactoryHelper> getInvocationFactoryHelpers() {
        List<InvocationFactoryHelper> invocationFactoryHelpers = registry.lookup(INVOCATION_FACTORY_HELPERS_KEY, List.class);
        
        if (invocationFactoryHelpers == null) {
            // Instantiate each invocation factory helper extension found in the configuration
            invocationFactoryHelpers = registry.register(INVOCATION_FACTORY_HELPERS_KEY, new ArrayList<InvocationFactoryHelper>());
            
            final Collection desc = getConfiguration().getInvocationFactoryHelpers();
            for (final Iterator iter = desc.iterator(); iter.hasNext();) {
                final ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
                final InvocationFactoryHelper helper = (InvocationFactoryHelper)descriptor.getExtensionClassInstance();
                if (helper != null) {
                    invocationFactoryHelpers.add(helper);
                }
            }
        }
        return invocationFactoryHelpers;
    }

    /**
     * Return a list of {@link org.teiid.designer.core.MappingAdapterDescriptor}instances for all extensions of the
     * {@link EOBJECT_MATCHER_FACTORY}
     * 
     * @return
     */
    public static List getMappingAdapterDescriptors() {
        return getConfiguration().getMappingAdapterDescriptors();
    }

    /**
     * Return the {@link MetamodelRegistry}.
     * 
     * @return the MetamodelRegistry instance.
     */
    public static MetamodelRegistry getMetamodelRegistry() {
        MetamodelRegistry metamodelRegistry = registry.lookup(METAMODEL_REGISTRY_KEY, MetamodelRegistry.class);
        if (metamodelRegistry != null) {
            return metamodelRegistry;
        }
        
        synchronized (METAMODEL_REGISTRY_LOCK) {
            // Create the MetamodelRegistry instance ...
                
            StartupLogger.log(" ModelerCore - getMetamodelRegistry() Create Registry STARTED"); //$NON-NLS-1$
            final Stopwatch watch = new Stopwatch();
            watch.start(true);
            
            // If some other thread initialized it ...
            metamodelRegistry = registry.lookup(METAMODEL_REGISTRY_KEY, MetamodelRegistry.class);
            if (metamodelRegistry != null) {
                return metamodelRegistry;
            }

            metamodelRegistry = registry.register(METAMODEL_REGISTRY_KEY, new MetamodelRegistryImpl());

            // Register all of the metamodel descriptors ...
            if (DEBUG_METAMODEL) {
                Util.log(IStatus.INFO,
                        ModelerCore.Util.getString("ModelerCore.Registering_metamodel_descriptor(s)_in_the_MetamodelRegistry_1", //$NON-NLS-1$
                                getConfiguration().getMetamodelDescriptors().size()));
            }
            final Iterator iter = getConfiguration().getMetamodelDescriptors().iterator();
            while (iter.hasNext()) {
                final MetamodelDescriptor descriptor = (MetamodelDescriptor)iter.next();
                if (DEBUG_METAMODEL) {
                    Util.log(IStatus.INFO, ModelerCore.Util.getString("ModelerCore.Registering_metamodel", descriptor)); //$NON-NLS-1$
                }
                metamodelRegistry.register(descriptor);
            }

            watch.stop();
            StartupLogger.log(" ModelerCore - getMetamodelRegistry() Create Registry Finished", watch.getTotalDuration()); //$NON-NLS-1$
        }
        
        return metamodelRegistry;
    }

    /**
     * Returns the default container into which models are .
     * 
     * @return the object graph representing the modeling workspace
     */
    public static Container getModelContainer() throws CoreException {
        Container defaultModelContainer = registry.lookup(DEFAULT_CONTAINER_KEY, Container.class);
        
        if (defaultModelContainer == null) {
            defaultModelContainer = loadModelContainer();
            defaultModelContainer.start();
        }

        return defaultModelContainer;
    }

    /**
     * Returns the interface used to work with model objects.
     * 
     * @return the editor for working with models
     */
    public static ModelEditor getModelEditor() {
        ModelEditor modelEditor = registry.lookup(MODEL_EDITOR_KEY, ModelEditor.class);
        
        if (modelEditor == null) {
            modelEditor = registry.register(MODEL_EDITOR_KEY, new ModelEditorImpl());
        }
        
        return modelEditor;
    }

    /**
     * Returns the default ModelWorkspace.
     * 
     * @return the object graph representing the modeling workspace
     */
    public static ModelWorkspace getModelWorkspace() {
        return ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
    }

    public static ObjectID getObjectId( final EObject object ) {
        return getModelEditor().getObjectID(object);
    }

    public static String getObjectIdString( final EObject object ) {
        return getModelEditor().getObjectIdString(object);
    }

    /**
     * Returns the single instance of the Modeler core plug-in runtime class.
     * 
     * @return the single instance of the Modeler core plug-in runtime class
     */
    public static Plugin getPlugin() {
        return MODELER_CORE_PLUGIN;
    }

    /**
     * Obtains the current preferences values for the plugin with the specified identifier. <strong>This method should be used
     * instead of {@link Plugin#getPluginPreferences()}.</strong>
     * 
     * @param pluginId the plugin ID (may not be <code>null</code>)
     * @return the preferences (never <code>null</code>)
     */
    public static IEclipsePreferences getPreferences( final String pluginId ) {
        CoreArgCheck.isNotNull(pluginId, "pluginId"); //$NON-NLS-1$
        return new InstanceScope().getNode(pluginId);
    }

    /**
     * @return the feature name
     * @since 6.1.0
     */
    static String getProducerName() {
        if (Platform.isRunning()) {
            final Bundle bundle = Platform.getBundle(FEATURE_ID);
            final Object name = bundle.getHeaders().get("Bundle-Name"); //$NON-NLS-1$
            return name.toString();
        }

        // must be testing
        return "Teiid Designer"; //$NON-NLS-1$
    }
    
    /**
     * Return the {@link ModelerCore}'s registry
     * 
     * @return A {@link Registry} that allows looking up but not registering. Only in specific 
     *                circumstances, like testing, should the registry be cast to allow registering / unregistering.
     */
    public synchronized static Registry getRegistry() {
    	return registry;
    }

    // /**
    // * Determine the {@link Container}in which the supplied model object is loaded.
    // *
    // * @param modelObject
    // * the model object; may not be null
    // * @return the Container in which the model object is loaded, or null if the model object is not loaded in a Container.
    // */
    // public static Container getContainer(final EObject modelObject) {
    // CoreArgCheck.isNotNull(modelObject);
    // final Resource rsrc = modelObject.eResource();
    // if(rsrc )
    // return ProxyUtilities.getContainer(modelObject);
    // }

    /**
     * Returns an array of {@link org.eclipse.emf.ecore.resource.Resource} instances representing System.vdb resources loaded as
     * external resources upon startup
     * 
     * @return
     * @since 4.3
     */
    public synchronized static Resource[] getSystemVdbResources() {
        final ArrayList tmp = new ArrayList();

        try {
            final ResourceSet rsrcSet = getExternalResourceContainer();
            if (rsrcSet != null) {
                for (final Object element : rsrcSet.getResources()) {
                    final Resource rsrc = (Resource)element;
                    if (rsrc != null && rsrc.getURI().lastSegment().indexOf("SYS") != -1) { //$NON-NLS-1$
                        tmp.add(rsrc);
                    }
                }
            }
        } catch (final CoreException e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        // Create a Resource[] to return
        final Resource[] systemResources = new Resource[tmp.size()];
        tmp.toArray(systemResources);
        return systemResources;

    }

    /**
     * Returns the interface used to get tranformation preference info
     * 
     * @return the preferences used for transformation defaults
     */
    public static TransformationPreferences getTransformationPreferences() {
        TransformationPreferences transformationPreferences = registry.lookup(TRANSFORMATION_PREFERENCES_KEY, TransformationPreferences.class);
        if (transformationPreferences == null) {
            transformationPreferences = registry.register(TRANSFORMATION_PREFERENCES_KEY, new TransformationPreferencesImpl());
        }
        
        return transformationPreferences;
    }

    /**
     * Returns the interface used to get validation preference info
     * 
     * @return the preferences used for validating resources
     */
    public static ValidationPreferences getValidationPreferences() {
        ValidationPreferences validationPreferences = registry.lookup(VALIDATION_PREFERENCES_KEY, ValidationPreferences.class);

        if (validationPreferences == null) {
            validationPreferences = registry.register(VALIDATION_PREFERENCES_KEY, new ValidationPreferencesImpl());
        }
        return validationPreferences;
    }

    /**
     * Returns the default ValidationRuleManager.
     * 
     * @return the ValidationRuleManager
     */
    public static synchronized ValidationRuleManager getValidationRuleManager() {
        ValidationRuleManager validationRuleMgr = registry.lookup(VALIDATION_RULE_MANAGER_KEY, ValidationRuleManager.class);

        if (validationRuleMgr == null) {
            validationRuleMgr = registry.register(VALIDATION_RULE_MANAGER_KEY, new ValidationRuleManager());
        }
        return validationRuleMgr;
    }

    // private static void initializeModelContainer() {
    // for (Iterator it = getConfiguration().getModelContainerInitializerDescriptors().iterator(); it.hasNext();) {
    // Object o1 = it.next();
    // if (o1 instanceof ExtensionDescriptor) {
    // ExtensionDescriptor descriptor = (ExtensionDescriptor) o1;
    // Object o2 = descriptor.getExtensionClassInstance();
    // if (o2 instanceof ModelContainerInitializer) {
    // ((ModelContainerInitializer) o2).execute();
    // }
    // }
    // }
    // }

    /**
     * @return the feature version
     * @since 6.1.0
     */
    static String getVersion() {
        if (Platform.isRunning()) {
            final Bundle bundle = Platform.getBundle(FEATURE_ID);
            final Object version = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
            String versionStr = version.toString();
            if( versionStr.endsWith(".qualifier")) { //$NON-NLS-1$
            	versionStr = versionStr.substring(0, versionStr.length()-10);
            }
            return versionStr;
        }

        // must be testing
        return "7.7.0"; //$NON-NLS-1$
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        IWorkspace workspace = registry.lookup(WORKSPACE_KEY, IWorkspace.class);
        if (workspace == null) {
            workspace = registry.register(WORKSPACE_KEY, ResourcesPlugin.getWorkspace());
        }
        
        return workspace;
    }

    /**
     * Returns the DatatypeManager instance associated with the {@link #getModelContainer() model container}
     * 
     * @return the DatatypeManager for the model workspace container
     */
    public static DatatypeManager getWorkspaceDatatypeManager() {
        DatatypeManager datatypeManager = null;
        try {
            // Get the ModelContainer's datatype manager ...
            final Container modelContainer = getModelContainer();
            if (modelContainer != null) {
                datatypeManager = modelContainer.getDatatypeManager();
            }
        } catch (final CoreException e) {
            throw new ModelerCoreRuntimeException(
                                                  e,
                                                  ModelerCore.Util.getString("ModelerCore.Error_creating_the_DatatypeManager_instance_1")); //$NON-NLS-1$
        }
        return datatypeManager;
    }

    /**
     * Returns true if the given project is accessible and it has a java nature, otherwise false.
     * 
     * @since 4.0
     */
    public static boolean hasModelNature( final IProject project ) {
        return DotProjectUtils.isModelerProject(project);
    }

    /**
     * Returns whether validation preference settings will be adhered to during the validation step of a workspace build operation.
     * 
     * @since 4.3
     */
    public static boolean ignoreValidationPreferencesOnBuild() {
        return IGNORE_VALIDATION_PREFERNCES_ON_BUILD;
    }

    private static void initializeXsdGlobalResourceSet() {
        StartupLogger.log(" ModelerCore - initializeXsdGlobalResourceSet() STARTED"); //$NON-NLS-1$
        final Stopwatch watch = new Stopwatch();
        watch.start(true);
        XSDSchemaImpl.getMagicSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        watch.stop();
        StartupLogger.log(" ModelerCore - initializeXsdGlobalResourceSet() calling XSDSchemaImpl.getMagicSchemaForSchema()", watch.getTotalDuration()); //$NON-NLS-1$
        watch.start(true);
        XSDSchemaImpl.getSchemaForSchema(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
        watch.stop();
        StartupLogger.log(" ModelerCore - initializeXsdGlobalResourceSet() calling XSDSchemaImpl.getSchemaForSchema()", watch.getTotalDuration()); //$NON-NLS-1$
        watch.start(true);
        XSDSchemaImpl.getSchemaInstance(XSDConstants.SCHEMA_INSTANCE_URI_2001);
        watch.stop();
        StartupLogger.log(" ModelerCore - initializeXsdGlobalResourceSet() calling XSDSchemaImpl.getSchemaInstance()", watch.getTotalDuration()); //$NON-NLS-1$
        StartupLogger.log(" ModelerCore - initializeXsdGlobalResourceSet() FINISHED"); //$NON-NLS-1$
    }

    public static boolean isModelContainer( final Container container ) {
        return container == registry.lookup(DEFAULT_CONTAINER_KEY, Container.class);
    }

    /**
     * @param proposedName the name being checked
     * @return <code>true</code> if the name is a reserved project name
     * @since 6.0.0
     */
    public static boolean isReservedProjectName( final String proposedName ) {
        if (CoreStringUtil.isEmpty(proposedName)) {
            return false;
        }

        for (final String reservedProjectName : RESERVED_PROJECT_NAMES) {
            if (reservedProjectName.equalsIgnoreCase(proposedName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Load all model resources known through the EXTERNAL_RESOURCE extension into the container
     * 
     * @param container
     */
    private static void loadExternalResources( final Container container ) {
        // Load all external model resources into the container
        final ExternalResourceLoader loader = new ExternalResourceLoader();
        final Collection desc = getConfiguration().getExternalResourceDescriptors();
        for (final Iterator iter = desc.iterator(); iter.hasNext();) {
            final ExternalResourceDescriptor descriptor = (ExternalResourceDescriptor)iter.next();
            try {
                loader.load(descriptor, container);
            } catch (final ModelerCoreException e) {
                Util.log(IStatus.ERROR, e, ModelerCore.Util.getString("ModelerCore.Error_loading_external_resource_1")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Load the default container into which models are .
     */
    private static Container loadModelContainer() throws CoreException {
        Container defaultContainer = registry.lookup(DEFAULT_CONTAINER_KEY, Container.class);
        if (defaultContainer != null) {
            return defaultContainer;
        }
        
        synchronized (CONTAINER_LOCK) {
            // If some other thread initialized it ...
            defaultContainer = registry.lookup(DEFAULT_CONTAINER_KEY, Container.class);
            
            if (defaultContainer == null) {
                defaultContainer = createContainer(DEFAULT_CONTAINER_KEY, RegistryOption.REGISTER);
            }
            
            return defaultContainer;
        }
    }

    /**
     * Adds a nature to the specified project that allows it to be considered hidden. Does nothing if the nature already exists.
     * 
     * @param project the project the hidden project nature is being added to
     * @return <code>true</code> if the nature was added
     * @since 5.5.3
     */
    public static boolean makeHidden( final IProject project ) {
        boolean success = true;

        try {
            if (!project.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)) {
                final IProjectDescription description = project.getDescription();
                final String[] natures = description.getNatureIds();
                final String[] newNatures = new String[natures.length + 1];
                System.arraycopy(natures, 0, newNatures, 0, natures.length);
                newNatures[natures.length] = ModelerCore.HIDDEN_PROJECT_NATURE_ID;
                description.setNatureIds(newNatures);
                project.setDescription(description, null);
            }
        } catch (final CoreException e) {
            Util.log(e);
            success = false;
        }

        return success;
    }

    /**
     * Rollback the current UnitOfWork for the model container
     */
    public static void rollbackTxn() {
        try {
            final UnitOfWork uow = getCurrentUoW();
            if (uow != null) {
                uow.rollback();
            }
        } catch (final ModelerCoreException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * Persists preferences for the plugin with the specified identifier. <strong>This method should be used instead of
     * {@link Plugin#savePluginPreferences()}.</strong>
     * 
     * @param pluginId the plugin ID (may not be <code>null</code>)
     * @throws BackingStoreException
     */
    public static void savePreferences( final String pluginId ) throws BackingStoreException {
        CoreArgCheck.isNotNull(pluginId, "pluginId"); //$NON-NLS-1$
        new DefaultScope().getNode(pluginId).flush(); // save defaults
        new InstanceScope().getNode(pluginId).flush(); // save current values
    }

    /**
     * Set whether validation preference settings will be adhered to during the validation step of a workspace build operation. If
     * set to true, preference settings will be ignored and default severity levels will be used for all validation rules. If false,
     * severity levels defined in the modeler's user preferences will be used.
     * 
     * @param serverValidation
     * @since 4.3
     */
    public static void setIgnoreValidationPreferencesOnBuild( final boolean ignorePrefs ) {
        IGNORE_VALIDATION_PREFERNCES_ON_BUILD = ignorePrefs;
    }

    public static void setObjectId( final EObject object,
                                    final ObjectID objectId ) {
        getModelEditor().setObjectID(object, objectId);
    }

    public static void setObjectId( final EObject object,
                                    final String objectId ) {
        getModelEditor().setObjectID(object, objectId);
    }

    /**
     * Get the current UnitOfWork. If it is not already started, start it and set the significant, description and source
     * attributes. NOTE : UoW attributes will not be updated if this call does not result in a new UoW.
     * 
     * @param isSignificant
     * @param containeredObject - used to derive the current container
     * @param source for this txn
     * @return true if a new txn was started by this call, else false.
     */
    public static boolean startTxn( final boolean isSignificant,
                                    final boolean isUndoable,
                                    final String description,
                                    final Object source ) {
        try {
            final UnitOfWork uow = getCurrentUoW();
            if (uow != null && uow.requiresStart()) {
                uow.begin();
                uow.setSignificant(isSignificant);
                uow.setDescription(description);
                uow.setSource(source);
                uow.setUndoable(isUndoable);
                return true;
            }
        } catch (final ModelerCoreException e) {
            return false;
        }

        return false;
    }

    /**
     * Get the current UnitOfWork. If it is not already started, start it and set the significant and description attributes. NOTE :
     * UoW attributes will not be updated if this call does not result in a new UoW.
     * 
     * @deprecated - Use methods that take a source parameter instead
     * @param isSignificant
     * @param containeredObject - used to derive the current container
     * @return true if a new txn was started by this call, else false.
     */
    @Deprecated
    public static boolean startTxn( final boolean isSignificant,
                                    final String description ) {
        return startTxn(isSignificant, description, null);
    }

    /**
     * Get the current UnitOfWork. If it is not already started, start it and set the significant, description and source
     * attributes. NOTE : UoW attributes will not be updated if this call does not result in a new UoW.
     * 
     * @param isSignificant
     * @param containeredObject - used to derive the current container
     * @param source for this txn
     * @return true if a new txn was started by this call, else false.
     */
    public static boolean startTxn( final boolean isSignificant,
                                    final String description,
                                    final Object source ) {
        return startTxn(isSignificant, true, description, source);
    }

    /**
     * Get the current UnitOfWork. If it is not already started, start it and set the significant, description and source
     * attributes. NOTE : UoW attributes will not be updated if this call does not result in a new UoW.
     * 
     * @param containeredObject - used to derive the current container
     * @param source for this txn
     * @return true if a new txn was started by this call, else false.
     */
    public static boolean startTxn( final String description,
                                    final Object source ) {
        return startTxn(true, true, description, source);
    }

    /**
     * This method should only be used for unit tests.
     * 
     * @throws CoreException
     * @since 4.3
     */
    public static void testLoadModelContainer() throws CoreException {
        loadModelContainer();
    }

    /**
     * @return the UUID of the current workspace
     */
    public static UUID workspaceUuid() {
        return registry.lookup(UUID_KEY, UUID.class);
    }

    private ISaveParticipant saveParticipant;

    private final CopyOnWriteArrayList<IRefactorResourceListener> refactorListeners;

    /**
     * The constructor.
     */
    public ModelerCore() {
        MODELER_CORE_PLUGIN = this;
        this.refactorListeners = new CopyOnWriteArrayList<IRefactorResourceListener>();
    }

    /**
     * Listeners already registered will not be added again.
     * 
     * @param listener the listener being registered to receive events (never <code>null</code>)
     * @return <code>true</code> if listener was added
     */
    public boolean addRefactorResourceListener( final IRefactorResourceListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.refactorListeners.addIfAbsent(listener);
    }

    /**
     * @param event the event being broadcast to registered {@link IRefactorResourceListener}s
     */
    public void notifyRefactored( final RefactorResourceEvent event ) {
        for (final IRefactorResourceListener listener : this.refactorListeners) {
            try {
                listener.notifyRefactored(event);
            } catch (final Exception e) {
                Util.log(e);
            }
        }
    }

    /**
     * @param listener the listener being unregistered and will no longer receive events (never <code>null</code>)
     * @return <code>true</code> if listener was removed
     */
    public boolean removeRefactorResourceListener( final IRefactorResourceListener listener ) {
        CoreArgCheck.isNotNull(listener, "listener"); //$NON-NLS-1$
        return this.refactorListeners.remove(listener);
    }

    public void start() throws Exception {
        final Stopwatch watch = new Stopwatch();
        watch.start();
        StartupLogger.log(" ModelerCore.startup() STARTED"); //$NON-NLS-1$

        // Set this as the txn manager for the aspect plugin
        CoreModelerPlugin.setTransactionManager(this);

        try {
            ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
        } catch (final Throwable t) {
            Util.log(IStatus.ERROR, ModelerCore.Util.getString("ModelerCore.Error_encountered_initializing_the_platform_logger_1", //$NON-NLS-1$
                                                               t.getMessage()));
        }

        Util.checkJre(MINIMUM_JAVA_VERSION);

        // Change the factory to use the extension mechanism ...
        try {
            EclipseConfigurationBuilder.build(getConfiguration());
        } catch (final Throwable t) {
            Util.log(IStatus.ERROR,
                     ModelerCore.Util.getString("ModelerCore.Error_encountered_building_the_Eclipse_configuration_2", //$NON-NLS-1$
                                                t.getMessage()));
        }

        final IWorkspace workspace;
        try {
            workspace = ResourcesPlugin.getWorkspace();
            this.saveParticipant = new ModelWorkspaceManagerSaveParticipant();
            workspace.addSaveParticipant(this, this.saveParticipant);
        } catch (final Throwable t) {
            Util.log(IStatus.ERROR, ModelerCore.Util.getString("ModelerCore.Error_encountered_starting_ModelWorkspaceManager")); //$NON-NLS-1$
        }

        // Initialize the XSD global resource set containing the resources:
        // "http://www.w3.org/2001/XMLSchema"
        // "http://www.w3.org/2001/MagicXMLSchema"
        // "http://www.w3.org/2001/XMLSchema-instance"
        initializeXsdGlobalResourceSet();

        // initialize the workspace model container
        loadModelContainer();

        // cause the validation preferences to get loaded so that they are available for this first build
        getValidationPreferences();

        final File file = getStateLocation().append(WORKSPACE_UUID_FILE).toFile();
        WorkspaceUUIDService.getInstance().setRestoreLocation(file);
        
        registry.register(UUID_KEY, WorkspaceUUIDService.getInstance().getUUID());

        watch.stop();
        StartupLogger.log(" ModelerCore.startup() FINISHED", watch.getTotalDuration()); //$NON-NLS-1$
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( final BundleContext context ) throws Exception {
        super.start(context);
        start();
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( final BundleContext context ) throws Exception {
        super.stop(context);

        // Null the reference to the model container ...
        registry.unregister(DEFAULT_CONTAINER_KEY);
        
        // Shut down the metamodel registry
        MetamodelRegistry metamodelRegistry = registry.lookup(METAMODEL_REGISTRY_KEY, MetamodelRegistry.class);
        if (metamodelRegistry != null) {
        	metamodelRegistry.dispose();
        	registry.unregister(METAMODEL_REGISTRY_KEY);
        }

        // Remove this as a save participant ...
        final IWorkspace workspace = getWorkspace();
        workspace.removeSaveParticipant(this);

        // Shut down the model workspace manager ...
        IStatus wsMgrProblem = null;
        try {
            ModelWorkspaceManager.shutdown();
        } catch (final CoreException e) {
            wsMgrProblem = e.getStatus();
        }

        // Throw an exception with all of the problems ...
        if (wsMgrProblem != null) {
            final String msg = Util.getString("ModelerCore.One_or_more_errors_shutting_down_ModelerCore_1"); //$NON-NLS-1$
            final ModelStatusImpl multiStat = new ModelStatusImpl(1, msg);
            multiStat.add(wsMgrProblem);
            throw new ModelWorkspaceException(multiStat);
        }
    }

    /**
     * The identifiers for all extensions referenced within ModelerCore
     */
    public static class EXTENSION {

        public static final String METAMODEL_SERVICE_ID = PLUGIN_ID + DELIMITER + "metamodelService"; //$NON-NLS-1$
        public static final String XMI_STREAM_READER_ID = PLUGIN_ID + DELIMITER + "xmiStreamReader"; //$NON-NLS-1$
        public static final String XMI_STREAM_WRITER_ID = PLUGIN_ID + DELIMITER + "xmiStreamWriter"; //$NON-NLS-1$
        public static final String XMI_RESOURCE_FACTORY_ID = PLUGIN_ID + DELIMITER + "xmiResourceFactory"; //$NON-NLS-1$
        public static final String BUILDER_ID = PLUGIN_ID + DELIMITER + "modelBuilder"; //$NON-NLS-1$
        public static final String NATURE_ID = PLUGIN_ID + DELIMITER + "modelNature"; //$NON-NLS-1$
    }

    /**
     * The identifiers for all ModelerCore extension points
     */
    public static class EXTENSION_POINT {

        /**
         * Extension point for registering AssociationProvider implementations,
         * point="org.teiid.designer.core.associationProvider"
         */
        public static class ASSOCIATION_PROVIDER {
            public static final String ID = "associationProvider"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String PROVIDER_CLASS = "providerClass"; //$NON-NLS-1$
            }
        }

        /** Extension point for the datatype manager */
        public static class DATATYPE_MANAGER {
            public static final String ID = "datatypeManager"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }
        }

        /** Extension point for the dependency MetamodelAspect */
        public static class DEPENDENCY_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = DEPENDENCY_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the factories of {@link org.teiid.designer.core.compare.EObjectMatcher}s */
        public static class EOBJECT_MATCHER_FACTORY {
            public static final String ID = "eobjectMatcherFactory"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }
        }

        /** Extension point for external resources */
        public static class EXTERNAL_RESOURCE {
            public static final String ID = "externalResource"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String LOAD_IMMEDIATELY = "loadImmediately"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String RESOURCE_NAME = "resourceName"; //$NON-NLS-1$
                public static final String RESOURCE_URL = "resourceUrl"; //$NON-NLS-1$
                public static final String INTERNAL_URI = "internalUri"; //$NON-NLS-1$
                public static final String PRIORITY = "priority"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }
        }

        /** Extension point for external resource sets */
        public static class EXTERNAL_RESOURCE_SET {
            public static final String ID = "externalResourceSet"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }
        }

        /** Extension point for the feature constraint MetamodelAspect */
        public static class FEATURE_CONSTRAINT_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = FEATURE_CONSTRAINT_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the import MetamodelAspect */
        public static class IMPORT_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = IMPORT_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the InvocationFactoryHelper instances */
        public static class INVOCATION_FACTORY_HELPER {
            public static final String ID = "invocationFactoryHelper"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }
        }

        /** Extension point for the item provider MetamodelAspect */
        public static class ITEM_PROVIDER_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = ITEM_PROVIDER_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for registering metamodels, point="org.teiid.designer.core.metamodel" */
        public static class METAMODEL {
            public static final String ID = "metamodel"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String DESCRIPTION = "description"; //$NON-NLS-1$
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String CREATE_AS_PHYSICAL = "createAsPhysical"; //$NON-NLS-1$
                public static final String CREATE_AS_VIRTUAL = "createAsVirtual"; //$NON-NLS-1$
                public static final String CREATE_AS_NEW_MODEL = "createAsNewModel"; //$NON-NLS-1$
                public static final String PARTICIPATORY_ONLY = "participatoryOnly"; //$NON-NLS-1$
                public static final String REQUIRES_PROXIES = "requiresProxies"; //$NON-NLS-1$
                public static final String ROOT_ENTITY_MAX_OCCURS = "maxOccurs"; //$NON-NLS-1$
                public static final String SUPPORTS_EXTENSION = "supportsExtension"; //$NON-NLS-1$
                public static final String SUPPORTS_DIAGRAMS = "supportsDiagrams"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String URI = "uri"; //$NON-NLS-1$
                public static final String DISPLAY_NAME = "displayName"; //$NON-NLS-1$
                public static final String ALTERNATE_URI = "alternateUri"; //$NON-NLS-1$
                public static final String PACKAGE_CLASS = "packageClass"; //$NON-NLS-1$
                public static final String ADAPTER_CLASS = "adapterClass"; //$NON-NLS-1$
                public static final String ROOT_ENTITY_CLASS = "rootEntityClass"; //$NON-NLS-1$
                public static final String ALLOWABLE_MODEL_TYPE = "allowableModelType"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
                public static final String INITIALIZERS = "initializers"; //$NON-NLS-1$
                public static final String INITIALIZER = "initializer"; //$NON-NLS-1$
                public static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
            }
        }

        /** Elements and attribute definitions common to all MetamodelAspect extensions */
        public static class METAMODEL_ASPECT {
            protected static final String UML_DIAGRAM_ID = "umlDiagramAspect"; //$NON-NLS-1$
            protected static final String ITEM_PROVIDER_ID = "itemProviderAspect"; //$NON-NLS-1$
            protected static final String DEPENDENCY_ID = "dependencyAspect"; //$NON-NLS-1$
            protected static final String VALIDATION_ID = "validationAspect"; //$NON-NLS-1$
            protected static final String SQL_ID = "sqlAspect"; //$NON-NLS-1$
            protected static final String RELATIONSHIP_ID = "relationshipAspect"; //$NON-NLS-1$
            protected static final String FEATURE_CONSTRAINT_ID = "featureConstraintAspect"; //$NON-NLS-1$
            protected static final String IMPORT_ID = "importAspect"; //$NON-NLS-1$

            public static String[] ASPECT_IDS = new String[] {UML_DIAGRAM_ID, ITEM_PROVIDER_ID, DEPENDENCY_ID, VALIDATION_ID,
                SQL_ID, RELATIONSHIP_ID, FEATURE_CONSTRAINT_ID, IMPORT_ID};

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String METAMODEL_REF_ID = "metamodelExtensionID"; //$NON-NLS-1$
                public static final String FACTORY_CLASS = "factoryClass"; //$NON-NLS-1$
            }
        }

        /** Extension point for resource reference updator */
        public static class REFERENCE_UPDATOR {
            public static final String ID = "referenceUpdator"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }
        }

        /** Extension point for the relationship MetamodelAspect */
        public static class RELATIONSHIP_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = RELATIONSHIP_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for registering EMF resource factories, point="org.teiid.designer.core.resourceFactory" */
        public static class RESOURCE_FACTORY {
            public static final String ID = "resourceFactory"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
                public static final String PROTOCOL = "protocol"; //$NON-NLS-1$
            }
        }

        /** Extension point for resource indexers */
        public static class RESOURCE_INDEXER {
            public static final String ID = "resourceIndexer"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }
        }

        /** Extension point for the OPTION_XML_MAP loading options when reading an EMF resource */
        public static class RESOURCE_LOAD_OPTIONS {
            public static final String ID = "resourceLoadOptions"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }
        }

        /** Extension point for resource validators */
        public static class RESOURCE_VALIDATOR {
            public static final String ID = "resourceValidator"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }
        }

        /** Extension point for the sql MetamodelAspect */
        public static class SQL_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = SQL_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the StreamReader implementation */
        public static class STREAM_READER {
            public static final String ID = "streamReader"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }
        }

        /** Extension point for the StreamWriter implementation */
        public static class STREAM_WRITER {
            public static final String ID = "streamWriter"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }
        }

        /** Extension point for the UML diagram MetamodelAspect */
        public static class UML_DIAGRAM_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = UML_DIAGRAM_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the model validation service implementation */
        public static class VALIDATION {
            public static final String ID = "modelValidation"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String LABEL = "label"; //$NON-NLS-1$
                public static final String TOOL_TIP = "toolTip"; //$NON-NLS-1$
                public static final String CATEGORY = "category"; //$NON-NLS-1$
                public static final String DEFAULT = "default"; //$NON-NLS-1$
            }

            public static class ELEMENTS {
                public static final String PREFERENCE = "preference"; //$NON-NLS-1$
            }
        }

        /** Extension point for the validation MetamodelAspect */
        public static class VALIDATION_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = VALIDATION_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }
    }

    /**
     * Product license constants.
     * 
     * @since 4.1
     */
    public static interface ILicense {
        String PRODUCER_NAME = getProducerName();
        String VERSION = getVersion();
    }

    /**
     * Has a default teiid instance been set
     *
     * @return
     */
    public static boolean hasDefaultTeiidServer() {
        return getTeiidServerManager().getDefaultServer() != null;
    }

    /**
     * Get the targeted Teiid Instance name
     * 
     * @return Teiid Instance name
     */
    public static String getDefaultServerName() {
        ITeiidServer defaultTeiidServer = getTeiidServerManager().getDefaultServer();
        if (defaultTeiidServer == null) {
            return Util.getString(I18N_PREFIX + "noDefaultServer"); //$NON-NLS-1$
        }
        return defaultTeiidServer.getParentName();
    }

    /**
     * Determine if the default teiid instance is connected
     * @return 'true' if the teiid instance is connected.
     */
    public static boolean isDefaultTeiidConnected() {
        ITeiidServer defaultTeiidServer = getTeiidServerManager().getDefaultServer();
        if (defaultTeiidServer == null) {
            return false;
        }
        return defaultTeiidServer.isConnected();
    }
    
    /**
     * Determine if the default parent server is connected
     * @return 'true' if the parent server is connected.
     */
    public static boolean isDefaultParentConnected() {
        ITeiidServer defaultTeiidServer = getTeiidServerManager().getDefaultServer();
        if (defaultTeiidServer == null) {
            return false;
        }
        return defaultTeiidServer.isParentConnected();
    }

    /**
     * Get the {@link EventManager} associated with the default
     * server or null if there is no default teiid instance set.
     *
     * @return event manager
     */
    public static EventManager getDefaultServerEventManager() {
        return getTeiidServerManager();
    }

    /**
     * Create an SWT {@link Event} that encapsulates the
     * default Teiid Instance as its data object
     *
     * @return an event
     */
    public static Event createDefaultTeiidServerEvent() {
        Event event = new Event();
        event.data = getTeiidServerManager().getDefaultServer();

        return event;
    }

    /**
     * Get the targeted Teiid Instance version
     *
     * @return Teiid Instance version
     */
    public static ITeiidServerVersion getTeiidServerVersion() {
        return getTeiidServerManager().getDefaultServerVersion();
    }

    /**
     * Get the default teiid runtime client's plugin path
     *
     * @return string of OS specific path
     */
    public static String getTeiidRuntimePath() throws Exception {
        ITeiidServerVersion version = getTeiidServerVersion();

        IExecutionAdminFactory adminFactory = TeiidRuntimeRegistry.getInstance().getExecutionAdminFactory(version);
        if (adminFactory == null)
            throw new Exception(Util.getString("ModelerCore.cannotFindTeiidClientRuntimePath"));

        return adminFactory.getRuntimePluginPath();
    }

    /**
     * Add a listener to be notified in the event the default teiid instance
     * version is changed
     * 
     * @param listener
     */
    public static void addTeiidServerVersionListener(ITeiidServerVersionListener listener) {
        if (getTeiidServerManager() == null)
            throw new IllegalStateException();

        getTeiidServerManager().addTeiidServerVersionListener(listener);
    }
    
    /**
     * Remove a listener no longer interested in listening
     * to changes is server version
     * 
     * @param listener
     */
    public static void removeTeiidServerVersionListener(ITeiidServerVersionListener listener) {
        if (getTeiidServerManager() == null)
            throw new IllegalStateException();

        getTeiidServerManager().removeTeiidServerVersionListener(listener);
    }

    /**
     * Get the teiid data type manager service for the
     * targeted Teiid Instance. The targeted Teiid Instance
     * can be changed using {@link #setDefaultServer(ITeiidServerVersion)}
     *  
     * @return
     */
    public static IDataTypeManagerService getTeiidDataTypeManagerService() {
        try {
            return TeiidRuntimeRegistry.getInstance().getDataTypeManagerService(getTeiidServerManager().getDefaultServerVersion());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Get the teiid query service for the
     * targeted Teiid Instance. The targeted Teiid Instance
     * can be changed using {@link #setDefaultServer(ITeiidServerVersion)}
     * 
     * @return
     */
    public static IQueryService getTeiidQueryService() {
        try {
            return TeiidRuntimeRegistry.getInstance().getQueryService(getTeiidServerManager().getDefaultServerVersion());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Get the level of support for the given {@link ITeiidServerVersion}
     * 
     * @return support level
     */
    public static SupportLevel getTeiidSupportLevel(ITeiidServerVersion teiidVersion) {
        try {
            IExecutionAdminFactory factory = TeiidRuntimeRegistry.getInstance().getExecutionAdminFactory(teiidVersion);
            return factory.supports(teiidVersion);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method will generate a ModelResource based on IPath and and name
	 * @param location
	 * @param modelName
	 * @return
     * @since 8.2
	 */
	public static ModelResource createModelResource(IPath location, String modelName) {
		IResource resource;
		IProject project;
		resource = ModelerCore.getWorkspace().getRoot().findMember(location);
        
        if( location.segmentCount() == 1 ) {
        	//We already have the project
        	project = (IProject)resource;
        } else {
        	//Get the project for a folder(s)
        	project = resource.getProject();
        }
        	
        IPath relativeModelPath = resource.getFullPath().removeFirstSegments(1).append(modelName);
        final IFile modelFile = project.getFile( relativeModelPath );
        final ModelResource resrc = ModelerCore.create( modelFile );
		return resrc;
	}

    /**
     * Get (and if necessary initialise) the {@link ITeiidServerManager}
     *
     * @return the teiidServerManager
     */
    public static ITeiidServerManager getTeiidServerManager() {

        /*
         * Several threads at startup can be calling this not least to wait on its
         * restoration and its possible that more than 1 thread could try to
         * create a new instance. Needs to be synchronized.
         *
         * Need to watch for request on the UI thread that may cause deadlocks.
         */
        synchronized (mutexObject) {

            if (teiidServerManager == null) {

                IExtensionRegistryCallback<ITeiidServerManager> callback = new IExtensionRegistryCallback<ITeiidServerManager>() {

                    @Override
                    public String getExtensionPointId() {
                        return ITeiidServerManager.TEIID_SERVER_MANAGER_EXTENSION_POINT_ID;
                    }

                    @Override
                    public String getElementId() {
                        return ITeiidServerManager.TEIID_SERVER_MANAGER_ELEMENT_ID;
                    }

                    @Override
                    public String getAttributeId() {
                        return CLASS_ATTRIBUTE_ID;
                    }

                    @Override
                    public boolean isSingle() {
                        return true;
                    }

                    @Override
                    public void process(ITeiidServerManager instance, IConfigurationElement element) {
                        CoreArgCheck.isNotNull(instance);

                        if (teiidServerManager != null) {
                            /*
                             * Programming error since the Teiid Instance manager extension should only be
                             * implemented once.
                             */
                            throw new IllegalStateException();
                        }

                        /*
                         * Need to assign the new instance to the singleton here prior to
                         * its restoration since the saved server instances use it,
                         * eg. notifying listeners. This scenario is not protected by the
                         * synchronized mutexObject since it would be the same thread.
                         */
                        teiidServerManager = instance;
                        instance.restoreState();
                    }
                };

                try {
                    ExtensionRegistryUtils.createExtensionInstances(callback);
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }
        }
        return teiidServerManager;
    }

    /**
     * This should only be called in rare circumstances such as unit testing
     * where the extension point is not available
     *
     * @param teiidServerManager the teiidServerManager to set
     */
    public static void setTeiidServerManager(ITeiidServerManager teiidServerManager) {
        ModelerCore.teiidServerManager = teiidServerManager;
    }
}
