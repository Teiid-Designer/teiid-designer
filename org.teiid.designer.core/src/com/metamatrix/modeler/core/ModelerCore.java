/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.xsd.XSDPlugin;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.aspects.DeclarativeTransactionManager;
import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.interceptor.InvocationFactoryHelper;
import com.metamatrix.core.modeler.CoreModelerPlugin;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.core.util.PluginUtilImpl;
import com.metamatrix.core.util.Stopwatch;
import com.metamatrix.core.util.StringUtil;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.core.container.ResourceDescriptor;
import com.metamatrix.modeler.core.index.IndexSelectorFactory;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRegistry;
import com.metamatrix.modeler.core.metamodel.ResourceLoadOptionContributor;
import com.metamatrix.modeler.core.search.MetadataSearch;
import com.metamatrix.modeler.core.transaction.UnitOfWork;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.DatatypeManagerLifecycle;
import com.metamatrix.modeler.core.workspace.ModelProject;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.core.workspace.ModelWorkspace;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceException;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.Configuration;
import com.metamatrix.modeler.internal.core.EclipseConfigurationBuilder;
import com.metamatrix.modeler.internal.core.ExternalResourceLoader;
import com.metamatrix.modeler.internal.core.ModelEditorImpl;
import com.metamatrix.modeler.internal.core.TransformationPreferencesImpl;
import com.metamatrix.modeler.internal.core.ValidationPreferencesImpl;
import com.metamatrix.modeler.internal.core.container.ContainerImpl;
import com.metamatrix.modeler.internal.core.index.ModelWorkspaceIndexSelectorFactory;
import com.metamatrix.modeler.internal.core.metamodel.MetamodelRegistryImpl;
import com.metamatrix.modeler.internal.core.resource.EmfResource;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSet;
import com.metamatrix.modeler.internal.core.resource.EmfResourceSetImpl;
import com.metamatrix.modeler.internal.core.search.MetadataSearchImpl;
import com.metamatrix.modeler.internal.core.util.FlatRegistry;
import com.metamatrix.modeler.internal.core.util.StartupLogger;
import com.metamatrix.modeler.internal.core.util.WorkspaceUriPathConverter;
import com.metamatrix.modeler.internal.core.validation.ValidationRuleManager;
import com.metamatrix.modeler.internal.core.workspace.ModelStatusImpl;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManager;
import com.metamatrix.modeler.internal.core.workspace.ModelWorkspaceManagerSaveParticipant;

/**
 * The main plugin class to be used in the desktop.
 */
public class ModelerCore extends Plugin implements DeclarativeTransactionManager {

    // ============================================================================================================================
    // Constants

    public static boolean HEADLESS = false;

    /**
     * The bundle ID of the Designer feature.
     * 
     * @since 6.1.0
     */
    private static String FEATURE_ID = "org.teiid.designer"; //$NON-NLS-1$

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
     * @return the feature version
     * @since 6.1.0
     */
    static String getVersion() {
        if (Platform.isRunning()) {
            Bundle bundle = Platform.getBundle(FEATURE_ID);
            Object version = bundle.getHeaders().get("Bundle-Version"); //$NON-NLS-1$
            return version.toString();
        }

        // must be testing
        return "6.1.0"; //$NON-NLS-1$
    }

    /**
     * @return the feature name
     * @since 6.1.0
     */
    static String getProducerName() {
        if (Platform.isRunning()) {
            Bundle bundle = Platform.getBundle(FEATURE_ID);
            Object name = bundle.getHeaders().get("Bundle-Name"); //$NON-NLS-1$
            return name.toString();
        }

        // must be testing
        return "Teiid Designer"; //$NON-NLS-1$
    }

    private static final String MINIMUM_JAVA_VERSION = "1.6"; //$NON-NLS-1$

    /**
     * The plug-in identifier of the Modeler Resources support (value <code>"com.metamatrix.modeler.core"</code>).
     */
    public static final String PLUGIN_ID = "org.teiid.designer.core"; //$NON-NLS-1$

    public static final String PACKAGE_ID = ModelerCore.class.getPackage().getName();

    /**
     * The identifier for the model builder (value <code>"com.metamatrix.modeler.core.modelBuilder"</code>).
     */
    public static final String BUILDER_ID = PLUGIN_ID + ".modelBuilder"; //$NON-NLS-1$

    /**
     * The identifier for the Modeling nature (value <code>"com.metamatrix.modeler.core.modelnature"</code>). The presence of this
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

    private static final String[] RESERVED_PROJECT_NAMES = {USERFILES_FOLDERNAME, UDF_PROJECT_NAME, XML_EXTENSIONS_PROJECT_NAME,};

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
     * The property name to be used to lookup on a IMarker, an URI used in locating the physical EObject referenced by the
     * IMarker.
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
        } catch (Exception e) {
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
     * The {@link IResource#getSessionProperty(org.eclipse.core.runtime.QualifiedName) session property} key dictating this
     * resource as a duplicate of another model.
     */
    public final static QualifiedName DUPLICATE_MODEL_OF_IPATH_KEY = new QualifiedName(
                                                                                       "com.metamatrix.modeler.core", "pathToDuplicateModel"); //$NON-NLS-1$  //$NON-NLS-2$

    /**
     * Delimiter used by extension/extension point declarations
     */
    public static final String DELIMITER = "."; //$NON-NLS-1$

    /**
     * The identifiers for all ModelerCore extension points
     */
    public static class EXTENSION_POINT {

        /** Extension point for registering metamodels, point="com.metamatrix.modeler.core.metamodel" */
        public static class METAMODEL {
            public static final String ID = "metamodel"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

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
        }

        /** Extension point for registering EMF resource factories, point="com.metamatrix.modeler.core.resourceFactory" */
        public static class RESOURCE_FACTORY {
            public static final String ID = "resourceFactory"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
                public static final String PROTOCOL = "protocol"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for the model validation service implementation */
        public static class VALIDATION {
            public static final String ID = "modelValidation"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String PREFERENCE = "preference"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
                public static final String LABEL = "label"; //$NON-NLS-1$
                public static final String TOOL_TIP = "toolTip"; //$NON-NLS-1$
                public static final String CATEGORY = "category"; //$NON-NLS-1$
                public static final String DEFAULT = "default"; //$NON-NLS-1$
            }
        }

        /** Extension point for the StreamReader implementation */
        public static class STREAM_READER {
            public static final String ID = "streamReader"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for the StreamWriter implementation */
        public static class STREAM_WRITER {
            public static final String ID = "streamWriter"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /**
         * Extension point for registering AssociationProvider implementations,
         * point="com.metamatrix.modeler.core.associationProvider"
         */
        public static class ASSOCIATION_PROVIDER {
            public static final String ID = "associationProvider"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String PROVIDER_CLASS = "providerClass"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
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

            public static class ELEMENTS {
                public static final String METAMODEL_REF_ID = "metamodelExtensionID"; //$NON-NLS-1$
                public static final String FACTORY_CLASS = "factoryClass"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }

            public static String[] ASPECT_IDS = new String[] {UML_DIAGRAM_ID, ITEM_PROVIDER_ID, DEPENDENCY_ID, VALIDATION_ID,
                SQL_ID, RELATIONSHIP_ID, FEATURE_CONSTRAINT_ID, IMPORT_ID};
        }

        /** Extension point for the UML diagram MetamodelAspect */
        public static class UML_DIAGRAM_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = UML_DIAGRAM_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the item provider MetamodelAspect */
        public static class ITEM_PROVIDER_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = ITEM_PROVIDER_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the dependency MetamodelAspect */
        public static class DEPENDENCY_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = DEPENDENCY_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the validation MetamodelAspect */
        public static class VALIDATION_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = VALIDATION_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the sql MetamodelAspect */
        public static class SQL_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = SQL_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
        }

        /** Extension point for the relationship MetamodelAspect */
        public static class RELATIONSHIP_ASPECT extends METAMODEL_ASPECT {
            public static final String ID = RELATIONSHIP_ID;
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;
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

        /** Extension point for the factories of {@link com.metamatrix.modeler.core.compare.EObjectMatcher}s */
        public static class EOBJECT_MATCHER_FACTORY {
            public static final String ID = "eobjectMatcherFactory"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for the datatype manager */
        public static class DATATYPE_MANAGER {
            public static final String ID = "datatypeManager"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for external resources */
        public static class EXTERNAL_RESOURCE {
            public static final String ID = "externalResource"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String RESOURCE_NAME = "resourceName"; //$NON-NLS-1$
                public static final String RESOURCE_URL = "resourceUrl"; //$NON-NLS-1$
                public static final String INTERNAL_URI = "internalUri"; //$NON-NLS-1$
                public static final String PRIORITY = "priority"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String LOAD_IMMEDIATELY = "loadImmediately"; //$NON-NLS-1$
            }
        }

        /** Extension point for external resource sets */
        public static class EXTERNAL_RESOURCE_SET {
            public static final String ID = "externalResourceSet"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for resource validators */
        public static class RESOURCE_VALIDATOR {
            public static final String ID = "resourceValidator"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for resource indexers */
        public static class RESOURCE_INDEXER {
            public static final String ID = "resourceIndexer"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for the InvocationFactoryHelper instances */
        public static class INVOCATION_FACTORY_HELPER {
            public static final String ID = "invocationFactoryHelper"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for the OPTION_XML_MAP loading options when reading an EMF resource */
        public static class RESOURCE_LOAD_OPTIONS {
            public static final String ID = "resourceLoadOptions"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
        }

        /** Extension point for resource reference updator */
        public static class REFERENCE_UPDATOR {
            public static final String ID = "referenceUpdator"; //$NON-NLS-1$
            public static final String UNIQUE_ID = PLUGIN_ID + DELIMITER + ID;

            public static class ELEMENTS {
                public static final String CLASS = "class"; //$NON-NLS-1$
                public static final String PROPERTIES = "properties"; //$NON-NLS-1$
            }

            public static class ATTRIBUTES {
                public static final String NAME = "name"; //$NON-NLS-1$
            }
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
     * Provides access to the plugin's log and to it's resources.
     */
    private static final String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$
    public static final PluginUtil Util = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));

    private static Plugin MODELER_CORE_PLUGIN = null;

    // Model container
    private static Container defaultModelContainer;
    private static final Object CONTAINER_LOCK = new Object();
    private static final String DEFAULT_CONTAINER_NAME = "Modeler Container"; //$NON-NLS-1$

    // External resource container
    private static Container externalResourceContainer;
    private static final String EXTERNAL_RESOURCE_CONTAINER_NAME = "External Resource Container"; //$NON-NLS-1$

    // External Resource Sets
    private static ResourceSet[] externalResourceSets;

    // InvocationFactoryHelper instances
    private static InvocationFactoryHelper[] invocationFactoryHelpers;

    // Model editor
    private static ModelEditor modelEditor;

    // Model editor
    private static ValidationPreferences validationPreferences;

    private static TransformationPreferences transformationPreferences;

    // Registry
    private static Registry registry;

    private static DatatypeManager dTypeManager;

    // Metamodel registry
    private static MetamodelRegistry metamodelRegistry;
    private static final Object METAMODEL_REGISTRY_LOCK = new Object();

    // Configuration information
    private static final Configuration CONFIG = new Configuration();

    // Validation rule manager
    private static ValidationRuleManager validationRuleMgr;

    // Eclipse IWorkspace that can be set and used for headless testing.
    private static Workspace WORKSPACE;

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
    // validation severity, otherwise, if false the buid process will ignore
    // user preferences and use default severity levels for all validation
    // rules.
    private static boolean IGNORE_VALIDATION_PREFERNCES_ON_BUILD = false;

    private ISaveParticipant saveParticipant;

    private final HashMap<Method, Boolean> writableInvocationMap = new HashMap<Method, Boolean>();

    /**
     * The constructor.
     */
    public ModelerCore() {
        MODELER_CORE_PLUGIN = this;
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
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    @Override
    public void start( BundleContext context ) throws Exception {
        super.start(context);
        start();
    }

    public void start() throws Exception {
        Stopwatch watch = new Stopwatch();
        watch.start();
        StartupLogger.log(" ModelerCore.startup() STARTED"); //$NON-NLS-1$

        // Set this as the txn manager for the aspect plugin
        CoreModelerPlugin.setTransactionManager(this);

        try {
            ((PluginUtilImpl)Util).initializePlatformLogger(this); // This must be called to initialize the platform logger!
        } catch (Throwable t) {
            Util.log(IStatus.ERROR,
                     ModelerCore.Util.getString("ModelerCore.Error_encountered_initializing_the_platform_logger_1", //$NON-NLS-1$
                                                t.getMessage()));
        }

        Util.checkJre(MINIMUM_JAVA_VERSION);

        // Change the factory to use the extension mechanism ...
        try {
            EclipseConfigurationBuilder.build(CONFIG);
        } catch (Throwable t) {
            Util.log(IStatus.ERROR,
                     ModelerCore.Util.getString("ModelerCore.Error_encountered_building_the_Eclipse_configuration_2", //$NON-NLS-1$
                                                t.getMessage()));
        }

        final IWorkspace workspace;
        try {
            workspace = ResourcesPlugin.getWorkspace();
            this.saveParticipant = new ModelWorkspaceManagerSaveParticipant();
            workspace.addSaveParticipant(this, this.saveParticipant);
        } catch (Throwable t) {
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

        watch.stop();
        StartupLogger.log(" ModelerCore.startup() FINISHED", watch.getTotalDuration()); //$NON-NLS-1$
    }

    /**
     * Set whether validation preference settings will be adhered to during the validation step of a workspace build operation. If
     * set to true, preference settings will be ignored and default severity levels will be used for all validation rules. If
     * false, severity levels defined in the modeler's user preferences will be used.
     * 
     * @param serverValidation
     * @since 4.3
     */
    public static void setIgnoreValidationPreferencesOnBuild( final boolean ignorePrefs ) {
        IGNORE_VALIDATION_PREFERNCES_ON_BUILD = ignorePrefs;
    }

    /**
     * Returns whether validation preference settings will be adhered to during the validation step of a workspace build
     * operation.
     * 
     * @since 4.3
     */
    public static boolean ignoreValidationPreferencesOnBuild() {
        return IGNORE_VALIDATION_PREFERNCES_ON_BUILD;
    }

    /**
     * Returns the workspace instance.
     */
    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the interface used to work with model objects.
     * 
     * @return the editor for working with models
     */
    public static ModelEditor getModelEditor() {
        if (modelEditor == null) {
            modelEditor = new ModelEditorImpl();
        }
        return modelEditor;
    }

    /**
     * Returns the interface used to get validation preference info
     * 
     * @return the preferences used for validating resources
     */
    public static ValidationPreferences getValidationPreferences() {
        if (validationPreferences == null) {
            validationPreferences = new ValidationPreferencesImpl();
        }
        return validationPreferences;
    }

    /**
     * Returns the interface used to get tranformation preference info
     * 
     * @return the preferences used for transformation defaults
     */
    public static TransformationPreferences getTransformationPreferences() {
        if (transformationPreferences == null) {
            transformationPreferences = new TransformationPreferencesImpl();
        }
        return transformationPreferences;
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
        } catch (CoreException e) {
            throw new ModelerCoreRuntimeException(
                                                  e,
                                                  ModelerCore.Util.getString("ModelerCore.Error_creating_the_DatatypeManager_instance_1")); //$NON-NLS-1$
        }
        return datatypeManager;
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

    /**
     * Returns the DatatypeManager instance associated with the container where the supplied object exists.
     * 
     * @return the DatatypeManager for the object's container, or null if the object is not associated with a container.
     */
    public static void setDatatypeManager( final DatatypeManager mgr ) {
        dTypeManager = mgr;
    }

    /**
     * Returns the DatatypeManager instance associated with the container where the supplied object exists.
     * 
     * @return the DatatypeManager for the object's container, or null if the object is not associated with a container.
     */
    public static DatatypeManager getDatatypeManager() {
        return dTypeManager;
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
        ArgCheck.isNotNull(object);

        // if DatatypeManager is set (thru UnitTest, use it)
        if (dTypeManager != null) return dTypeManager;

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
     * Construct a new DatatypeManager instance.
     * 
     * @return the DatatypeManager; null if no extension could be found
     */
    protected static DatatypeManager createDatatypeManager() {

        // Ensure that the metamodel registry is initialized
        getMetamodelRegistry();

        // Instantiate the first DatatypeManager extension found in the configuration.
        // This instance is used to resolving built-in datatypes
        final Collection desc = CONFIG.getDatatypeManagerDescriptors();
        if (desc.isEmpty()) {
            Util.log(IStatus.ERROR,
                     ModelerCore.Util.getString("ModelerCore.Error,_no_DatatypeManager_extensions_were_found_in_the_plugin_registry_1")); //$NON-NLS-1$
        }

        DatatypeManager dtMgr = null;
        Iterator iter = desc.iterator();
        if (iter.hasNext()) {
            ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
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
                return defaultModelContainer;
            }
        }
        return null;
    }

    /**
     * Returns the default ModelWorkspace.
     * 
     * @return the object graph representing the modeling workspace
     */
    public static ModelWorkspace getModelWorkspace() {
        return ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
    }

    /**
     * Returns the default ModelWorkspace.
     * 
     * @return the object graph representing the modeling workspace
     */
    public static IWorkspace getEclispeWorkspace() {
        if (HEADLESS) {
            return WORKSPACE;
        }

        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Returns the default ModelWorkspace.
     * 
     * @return the object graph representing the modeling workspace
     */
    public static void setEcliseWorkspace( Workspace ws ) {
        WORKSPACE = ws;
    }

    /**
     * Returns the default ValidationRuleManager.
     * 
     * @return the ValidationRuleManager
     */
    public static synchronized ValidationRuleManager getValidationRuleManager() {
        if (validationRuleMgr == null) {
            validationRuleMgr = new ValidationRuleManager();
        }
        return validationRuleMgr;
    }

    /**
     * Return a list of {@link com.metamatrix.modeler.core.MappingAdapterDescriptor}instances for all extensions of the
     * ModelerCore.EXTENSION_POINT.MODEL_OBJECT_RESOLVER.
     * 
     * @return
     */
    public static List getMappingAdapterDescriptors() {
        return CONFIG.getMappingAdapterDescriptors();
    }

    // /**
    // * Determine the {@link Container}in which the supplied model object is loaded.
    // *
    // * @param modelObject
    // * the model object; may not be null
    // * @return the Container in which the model object is loaded, or null if the model object is not loaded in a Container.
    // */
    // public static Container getContainer(final EObject modelObject) {
    // ArgCheck.isNotNull(modelObject);
    // final Resource rsrc = modelObject.eResource();
    // if(rsrc )
    // return ProxyUtilities.getContainer(modelObject);
    // }

    /**
     * Determine the {@link Container}in which the supplied resource is loaded.
     * 
     * @param resource the resource; may not be null
     * @return the Container in which the resource is loaded, or null if the resource is not loaded in a Container.
     */
    public static Container getContainer( final Resource resource ) {
        ArgCheck.isNotNull(resource);
        if (resource instanceof EmfResource) {
            return ((EmfResource)resource).getContainer();
        } else if (resource instanceof XSDResourceImpl) {
            ResourceSet rs = resource.getResourceSet();
            if (rs instanceof EmfResourceSet) {
                return ((EmfResourceSet)rs).getContainer();
            }
        }
        return null;
    }

    /**
     * Returns the default container into which models are .
     * 
     * @return the object graph representing the modeling workspace
     */
    public static Container getModelContainer() throws CoreException {
        if (defaultModelContainer == null && HEADLESS) {
            loadModelContainer();
            defaultModelContainer.start();
        }

        return defaultModelContainer;
    }

    /**
     * Sets the default container into which models are .
     * 
     * @return the object graph representing the modeling workspace
     */
    public static void setModelContainer( final Container container ) {
        defaultModelContainer = container;
    }

    /**
     * Load the default container into which models are .
     */
    private static void loadModelContainer() throws CoreException {
        if (defaultModelContainer == null) {
            synchronized (CONTAINER_LOCK) {
                // If some other thread initialized it ...
                if (defaultModelContainer != null) {
                    return;
                }
                defaultModelContainer = createContainer(DEFAULT_CONTAINER_NAME);
                // // run model container initializers (if any) that have been added via the configuration
                // initializeModelContainer();
            }
        }
    }

    // private static void initializeModelContainer() {
    // for (Iterator it = CONFIG.getModelContainerInitializerDescriptors().iterator(); it.hasNext();) {
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
     * This method should only be used for unit tests.
     * 
     * @throws CoreException
     * @since 4.3
     */
    public static void testLoadModelContainer() throws CoreException {
        loadModelContainer();
    }

    /**
     * Create a new {@link com.metamatrix.modeler.core.container.Container}instance. The new container will be initialized with a
     * reference to the metamodel registry along with references to the resource factories needed to create new resource
     * instances. The container will be loaded with all external resources found through the "externalResource" extension point
     * which includes the "built-in" datatypes resource.
     * 
     * @param containerName
     * @return @throws CoreException
     */
    public static Container createContainer( final String containerName ) throws CoreException {
        final Container container = createEmptyContainer(containerName);

        // Add the external rsesource sets to the container as delegate resource sets
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
     * Return the default configuration options for a container.
     * 
     * @return
     * @since 4.3
     */
    private static Map getDefaultContainerOptions() {
        final Map options = new HashMap();

        // Check if are running in an Eclipse runtime environment ...
        if (ModelerCore.getPlugin() != null && CONFIG != null) {

            // Create a map of the XML loading options and add all contributions by the extensions
            final XMLResource.XMLMap xmlMap = new XMLMapImpl();
            for (Iterator iter = CONFIG.getResourceLoadOptions().iterator(); iter.hasNext();) {
                ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
                ResourceLoadOptionContributor optionContributor = (ResourceLoadOptionContributor)descriptor.getExtensionClassInstance();
                if (optionContributor != null) {
                    optionContributor.addMappings(xmlMap);
                }
            }

            // Add the XML loading options map to the container's options map. This will overwrite
            options.put(XMLResource.OPTION_XML_MAP, xmlMap);
        }
        return options;
    }

    private static void initializeXsdGlobalResourceSet() {
        StartupLogger.log(" ModelerCore - initializeXsdGlobalResourceSet() STARTED"); //$NON-NLS-1$
        Stopwatch watch = new Stopwatch();
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

    /**
     * Return the {@link com.metamatrix.modeler.core.container.Container}instance used to hold all external resources. This
     * container is used as an external resource set for all other containers created in the modeler thereby allowing those
     * containers access to the same global resources. The external resource container will be loaded with all external resources
     * found through the "externalResource" extension point which includes the "built-in" datatypes resource.
     * 
     * @param containerName
     * @return @throws CoreException
     */
    public synchronized static Container getExternalResourceContainer() throws CoreException {
        if (externalResourceContainer == null) {
            externalResourceContainer = createEmptyContainer(EXTERNAL_RESOURCE_CONTAINER_NAME);

            // Load the container with all external resources ...
            loadExternalResources(externalResourceContainer);
        }

        return externalResourceContainer;
    }

    /**
     * Load all model resources known through the EXTERNAL_RESOURCE extension into the container
     * 
     * @param container
     */
    public static void loadExternalResources( final Container container ) {
        // Load all external model resources into the container
        final ExternalResourceLoader loader = new ExternalResourceLoader();
        final Collection desc = CONFIG.getExternalResourceDescriptors();
        for (Iterator iter = desc.iterator(); iter.hasNext();) {
            ExternalResourceDescriptor descriptor = (ExternalResourceDescriptor)iter.next();
            try {
                loader.load(descriptor, container);
            } catch (ModelerCoreException e) {
                Util.log(IStatus.ERROR, e, ModelerCore.Util.getString("ModelerCore.Error_loading_external_resource_1")); //$NON-NLS-1$
            }
        }
    }

    /**
     * Returns an array of {@link org.eclipse.emf.ecore.resource.Resource} instances representing System.vdb resources loaded as
     * external resources upon startup
     * 
     * @return
     * @since 4.3
     */
    public synchronized static Resource[] getSystemVdbResources() {
        ArrayList tmp = new ArrayList();

        try {
            ResourceSet rsrcSet = getExternalResourceContainer();
            if (rsrcSet != null) {
                for (Iterator iter = rsrcSet.getResources().iterator(); iter.hasNext();) {
                    Resource rsrc = (Resource)iter.next();
                    if (rsrc != null && rsrc.getURI().lastSegment().indexOf("System") != -1) { //$NON-NLS-1$
                        tmp.add(rsrc);
                    }
                }
            }
        } catch (CoreException e) {
            ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
        }

        // Create a Resource[] to return
        Resource[] systemResources = new Resource[tmp.size()];
        tmp.toArray(systemResources);
        return systemResources;

    }

    /**
     * Load all model resource sets known through the EXTERNAL_RESOURCE_SET extension into the container
     * 
     * @param container
     */
    public synchronized static ResourceSet[] getExternalResourceSets() {
        if (externalResourceSets == null) {
            // Instantiate each external resource set extension found in the configuration
            ArrayList tmp = new ArrayList();

            // Add the external resource container holding all shared external resources
            // to be shared by containers throughout the modeler
            try {
                ResourceSet rsrcSet = getExternalResourceContainer();
                if (rsrcSet != null) {
                    tmp.add(rsrcSet);
                }
            } catch (CoreException e) {
                ModelerCore.Util.log(IStatus.ERROR, e, e.getMessage());
            }

            // Add in all other resource sets known through the EXTERNAL_RESOURCE_SET extension
            final Collection desc = CONFIG.getExternalResourceSetDescriptors();
            for (Iterator iter = desc.iterator(); iter.hasNext();) {
                ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
                ExternalResourceSet extRsrcSet = (ExternalResourceSet)descriptor.getExtensionClassInstance();
                if (extRsrcSet != null) {
                    ResourceSet rsrcSet = extRsrcSet.getResourceSet();
                    if (rsrcSet != null) {
                        tmp.add(rsrcSet);
                    }
                }
            }

            // Create a ResourceSet[] to return
            externalResourceSets = new ResourceSet[tmp.size()];
            tmp.toArray(externalResourceSets);
        }
        return externalResourceSets;
    }

    /**
     * Setter for UnitTesting
     * 
     * @param externalSets
     */
    public synchronized static void setExternalResourceSets( ResourceSet[] externalSets ) {
        externalResourceSets = externalSets;
    }

    /**
     * Load all InvocationFactoryHelper instances known through the INVOCATION_FACTORY_HELPER extension into the container
     * 
     * @param container
     */
    public static InvocationFactoryHelper[] getInvocationFactoryHelpers() {
        if (invocationFactoryHelpers == null || invocationFactoryHelpers.length == 0) {
            // Instantiate each invocation factory helper extension found in the configuration
            ArrayList tmp = new ArrayList();
            final Collection desc = CONFIG.getInvocationFactoryHelpers();
            for (Iterator iter = desc.iterator(); iter.hasNext();) {
                ExtensionDescriptor descriptor = (ExtensionDescriptor)iter.next();
                InvocationFactoryHelper helper = (InvocationFactoryHelper)descriptor.getExtensionClassInstance();
                if (helper != null) {
                    tmp.add(helper);
                }
            }
            invocationFactoryHelpers = new InvocationFactoryHelper[tmp.size()];
            tmp.toArray(invocationFactoryHelpers);
        }
        return invocationFactoryHelpers;
    }

    /**
     * Add all model resource sets known through the EXTERNAL_RESOURCE_SET extension to the specified container
     * 
     * @param container
     */
    public static void addExternalResourceSets( final Container container ) {
        // Add each external resource set found in the configuration
        // to the container as a delegate resource set
        ResourceSet[] extRsrcSets = getExternalResourceSets();
        for (int i = 0; i < extRsrcSets.length; i++) {
            ResourceSet rsrcSet = extRsrcSets[i];
            container.addExternalResourceSet(rsrcSet);
            if (DEBUG) {
                Util.log(IStatus.INFO,
                         ModelerCore.Util.getString("ModelerCore.DEBUG.Added_external_resource_set_to_the_container._1", rsrcSet)); //$NON-NLS-1$
            }
        }
    }

    /**
     * Create a new empty {@link com.metamatrix.modeler.core.container.Container}instance. The new container will be initialized
     * with a reference to the metamodel registry along with references to the resource factories needed to create new resource
     * instances. The container will <b>not </b> be loaded or have access to any of the external resources including the
     * "built-in" datatypes resource.
     * 
     * @param containerName
     * @return @throws CoreException
     */
    public static Container createEmptyContainer( final String containerName ) throws CoreException {
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
            if (name != null && name.length() != 0) {
                ModelerCore.getRegistry().register(name, container);
            }
            container.start();

            // Set the EmfUriHelper to use when resolving EObject URIs. The
            // EmfUriHelper is used by the EmfResource.getEObject(URI,boolean)
            // method to convert workspace relative URIs of the form
            // "/Project/.../Resource" into absolute file URIs. Workspace relative
            // URIs are sometimes encountered in cross model references, hrefs in
            // the xmi files, and are not resolved using the normal URI resolution
            // logic found in EMF.
            ResourceSet resourceSet = ((ContainerImpl)container).getResourceSet();
            if (resourceSet instanceof EmfResourceSetImpl) {
                ((EmfResourceSetImpl)resourceSet).setUriPathConverter(new WorkspaceUriPathConverter());
            }

        } catch (ModelerCoreException e) {
            throw new CoreException(
                                    new Status(
                                               IStatus.ERROR,
                                               ModelerCore.PLUGIN_ID,
                                               1,
                                               ModelerCore.Util.getString("ModelerCore.Error_adding_resource_descriptors_to_the_container"), e)); //$NON-NLS-1$
        }

        // Register the known resource descriptors
        final Iterator iter = CONFIG.getResourceDescriptors().iterator();
        final List errors = new ArrayList();
        while (iter.hasNext()) {
            final ResourceDescriptor resourceDescriptor = (ResourceDescriptor)iter.next();
            try {
                container.addResourceDescriptor(resourceDescriptor);
            } catch (ModelerCoreException e) {
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

    public static Configuration getConfiguration() {
        return CONFIG;
    }

    public synchronized static Registry getRegistry() {
        if (registry == null) {
            registry = new FlatRegistry();
        }
        return registry;
    }

    public static boolean isModelContainer( final Container container ) {
        return container == defaultModelContainer;
    }

    public static void setMetamodelRegistry( MetamodelRegistry registry ) {
        synchronized (METAMODEL_REGISTRY_LOCK) {
            metamodelRegistry = registry;
        }
    }

    /**
     * Return the {@link MetamodelRegistry}.
     * 
     * @return the MetamodelRegistry instance.
     */
    public static MetamodelRegistry getMetamodelRegistry() {
        // Create the MetamodelRegistry instance ...
        synchronized (METAMODEL_REGISTRY_LOCK) {
            if (metamodelRegistry == null) {
                StartupLogger.log(" ModelerCore - getMetamodelRegistry() Create Registry STARTED"); //$NON-NLS-1$
                Stopwatch watch = new Stopwatch();
                watch.start(true);
                // If some other thread initialized it ...
                if (metamodelRegistry != null) {
                    return metamodelRegistry;
                }

                metamodelRegistry = new MetamodelRegistryImpl();

                // Register all of the metamodel descriptors ...
                if (DEBUG_METAMODEL) {
                    Util.log(IStatus.INFO,
                             ModelerCore.Util.getString("ModelerCore.Registering_metamodel_descriptor(s)_in_the_MetamodelRegistry_1", //$NON-NLS-1$
                                                        CONFIG.getMetamodelDescriptors().size()));
                }
                final Iterator iter = CONFIG.getMetamodelDescriptors().iterator();
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
        }
        return metamodelRegistry;
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
     * Get the current UnitOfWork. If it is not already started, start it and set the significant and description attributes. NOTE
     * : UoW attributes will not be updated if this call does not result in a new UoW.
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
     * @param containeredObject - used to derive the current container
     * @param source for this txn
     * @return true if a new txn was started by this call, else false.
     */
    public static boolean startTxn( final String description,
                                    final Object source ) {
        return startTxn(true, true, description, source);
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
        } catch (ModelerCoreException e) {
            return false;
        }

        return false;
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
        } catch (ModelerCoreException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
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
        } catch (ModelerCoreException e) {
            Util.log(IStatus.ERROR, e, e.getMessage());
        }
    }

    /**
     * @return the current UoW for the defaultModelContainer
     */
    public static UnitOfWork getCurrentUoW() {
        if (defaultModelContainer != null) {
            try {
                return getModelContainer().getEmfTransactionProvider().getCurrent();
            } catch (CoreException e) {
                Util.log(IStatus.ERROR, e, e.getMessage());
                return null;
            }
        }
        return null;
    }

    /**
     * <p>
     * {@inheritDoc}
     * </p>
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
     */
    @Override
    public void stop( BundleContext context ) throws Exception {
        super.stop(context);

        // Null the reference to the model container ...
        defaultModelContainer = null;

        // Null the reference to the registry ...
        registry = null;

        // Shut down the metamodel registry
        metamodelRegistry.dispose();
        metamodelRegistry = null;

        // Remove this as a save participant ...
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.removeSaveParticipant(this);

        // Shut down the model workspace manager ...
        IStatus wsMgrProblem = null;
        try {
            ModelWorkspaceManager.shutdown();
        } catch (CoreException e) {
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
     * Returns the {@link ModelWorkspaceItem}corresponding to the given handle identifier generated by
     * {@link ModelWorkspaceItem#getHandleIdentifier()}, or <code>null</code> if unable to create the associated item.
     */
    public static ModelWorkspaceItem create( String handleIdentifier ) {
        if (handleIdentifier == null) {
            return null;
        }
        return ModelWorkspaceManager.getModelWorkspaceManager().getHandleFromMemento(handleIdentifier);
    }

    /**
     * Returns the {@link ModelWorkspaceItem}corresponding to the given file, or <code>null</code> if unable to associate the
     * given file with a {@link ModelWorkspaceItem}.
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
     * @return the {@link ModelWorkspaceItem}corresponding to the given file, or <code>null</code> if unable to associate the
     *         given file with a {@link ModelWorkspaceItem workspace item}
     */
    public static ModelResource create( IFile file ) {
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
    public static ModelProject create( IProject project ) {
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
    public static ModelWorkspaceItem create( IResource resource ) {
        return ModelWorkspaceManager.create(resource, null);
    }

    /**
     * Returns the {@link ModelWorkspace}
     * 
     * @param root the given root
     * @return the ModelWorkspace, or <code>null</code> if the root is null
     */
    public static ModelWorkspace create( IWorkspaceRoot root ) {
        if (root == null) {
            return null;
        }
        return ModelWorkspaceManager.getModelWorkspaceManager().getModelWorkspace();
    }

    /**
     * Returns true if the given project is accessible and it has a java nature, otherwise false.
     * 
     * @since 4.0
     */
    public static boolean hasModelNature( IProject project ) {
        ArgCheck.isNotNull(project);
        try {
            return project.hasNature(NATURE_ID);
        } catch (CoreException e) {
            // project does not exist or is not open
        }
        return false;
    }

    /**
     * Adds a nature to the specified project that allows it to be considered hidden. Does nothing if the nature already exists.
     * 
     * @param project the project the hidden project nature is being added to
     * @return <code>true</code> if the nature was added
     * @since 5.5.3
     */
    public static boolean makeHidden( IProject project ) {
        boolean success = true;

        try {
            if (!project.hasNature(ModelerCore.HIDDEN_PROJECT_NATURE_ID)) {
                IProjectDescription description = project.getDescription();
                String[] natures = description.getNatureIds();
                String[] newNatures = new String[natures.length + 1];
                System.arraycopy(natures, 0, newNatures, 0, natures.length);
                newNatures[natures.length] = ModelerCore.HIDDEN_PROJECT_NATURE_ID;
                description.setNatureIds(newNatures);
                project.setDescription(description, null);
            }
        } catch (CoreException e) {
            Util.log(e);
            success = false;
        }

        return success;
    }

    /**
     * @param proposedName the name being checked
     * @return <code>true</code> if the name is a reserved project name
     * @since 6.0.0
     */
    public static boolean isReservedProjectName( String proposedName ) {
        if (StringUtil.isEmpty(proposedName)) {
            return false;
        }

        for (String reservedProjectName : RESERVED_PROJECT_NAMES) {
            if (reservedProjectName.equalsIgnoreCase(proposedName)) {
                return true;
            }
        }

        return false;
    }

    public static ObjectID getObjectId( EObject object ) {
        return getModelEditor().getObjectID(object);
    }

    public static String getObjectIdString( EObject object ) {
        return getModelEditor().getObjectIdString(object);
    }

    public static void setObjectId( EObject object,
                                    ObjectID objectId ) {
        getModelEditor().setObjectID(object, objectId);
    }

    public static void setObjectId( EObject object,
                                    String objectId ) {
        getModelEditor().setObjectID(object, objectId);
    }

    // ============================================================================================================================
    // // DeclarativeTransactionManager implementation
    // public InvocationFactoryHelper[] getInvocationFactoryHelperArray() {
    // return ModelerCore.getInvocationFactoryHelpers();
    // }
    //
    public boolean isWritable( final Method method ) {
        if (this.writableInvocationMap.get(method) == null) {
            final InvocationFactoryHelper[] helpers = ModelerCore.getInvocationFactoryHelpers();
            for (int i = 0; i < helpers.length; i++) {
                InvocationFactoryHelper next = helpers[i];
                final int writable = next.isWrite(method);
                if (writable == InvocationFactoryHelper.READ_ONLY) {
                    this.writableInvocationMap.put(method, Boolean.FALSE);
                    return false;
                } else if (writable == InvocationFactoryHelper.WRITABLE) {
                    this.writableInvocationMap.put(method, Boolean.TRUE);
                    return true;
                }
            }
        } else {
            return this.writableInvocationMap.get(method);
        }

        return false;
    }

    // /**
    // * @see com.metamatrix.core.aspects.DeclarativeTransactionManager#beginTxn(java.lang.String, java.lang.Object)
    // * @since 4.1
    // */
    // public boolean beginTxn(String description,
    // Object source) {
    // return ModelerCore.startTxn(description, source);
    // }
    //
    // /**
    // * @see com.metamatrix.core.aspects.DeclarativeTransactionManager#endTxn()
    // * @since 4.1
    // */
    // public void endTxn() {
    // ModelerCore.commitTxn();
    // }
    // /**
    // * @see com.metamatrix.core.aspects.DeclarativeTransactionManager#rollbackDeclarativeTxn()
    // * @since 4.1
    // */
    // public void rollbackDeclarativeTxn() {
    // ModelerCore.rollbackTxn();
    // }

    public Object executeInTransaction( final Method method,
                                        final Object target,
                                        final Object[] params ) throws RuntimeException {
        ArgCheck.isNotNull(method);
        ArgCheck.isNotNull(params);
        ArgCheck.isNotNull(target);
        final boolean startedTxn = ModelerCore.startTxn(null, this);
        boolean success = false;
        try {
            CommandParameter param = createCommandParameter(method, target, params);

            // if (param != null && param.getFeature() != null && target instanceof EObject && param.getFeature() instanceof
            // EStructuralFeature) {
            // Object oldValue = ((EObject) target).eGet((EStructuralFeature) param.getFeature());
            // Object newValue = params.length == 2 ? params[1] : params[0];
            //                System.out.println("ModelerCore:executeInTransaction - Old value: " + oldValue + " *** New value: " + newValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            // }

            if (param != null && param.getFeature() != null) {
                EStructuralFeature sf = (EStructuralFeature)param.getFeature();
                EClassifier eType = sf.getEType();
                if (eType instanceof EEnum && sf.isMany()) {
                    Object newValue = (params.length == 2 ? params[1] : params[0]);
                    if (newValue instanceof String) {
                        param = null;
                    }
                }
            }

            final ContainerImpl cntr = (ContainerImpl)defaultModelContainer;
            final Command cmd = (param == null || cntr == null) ? null : cntr.getEditingDomain().createCommand(SetCommand.class,
                                                                                                               param);

            // If we couldn't create a command param, just execute the method within a txn.
            if (cmd == null || param == null) {
                Object result = null;
                try {
                    result = method.invoke(target, params);
                    success = true;
                    return result;
                } catch (Exception e) {
                    final String msg = ModelerCore.Util.getString("ModelerCore.unexpected_declarative_txn_exception"); //$NON-NLS-1$
                    Util.log(IStatus.ERROR, e, msg);
                    success = false;
                    return result;
                }
            }

            // Else, execute the command via the ModelerEditor so that it is undoable.
            ModelerCore.getModelEditor().executeCommand((EObject)target, cmd);

            final Collection result = cmd.getResult();
            if (result != null && result.size() == 1) {
                success = true;
                return result.iterator().next();
            }

            success = true;
            return result;
        } catch (Exception e) {
            final String msg = ModelerCore.Util.getString("ModelerCore.unexpected_declarative_txn_exception"); //$NON-NLS-1$
            Util.log(IStatus.ERROR, e, msg);
            return null;
        } finally {
            if (startedTxn) {
                if (success) {
                    commitTxn();
                } else {
                    rollbackTxn();
                }
            }
        }

    }

    /**
     * Create an object that performs searches for model object instances.
     * 
     * @return the search object; never null
     */
    private CommandParameter createCommandParameter( final Method method,
                                                     final Object target,
                                                     final Object[] params ) {
        if (!(target instanceof EObject)) {
            return null;
        }

        final EObject eTarget = (EObject)target;
        EStructuralFeature sf = null;

        // First check the invocation parameters to see if one of the params is a SF...
        for (int i = 0; i < params.length; i++) {
            final Object temp = params[i];
            if (temp instanceof EStructuralFeature) {
                sf = (EStructuralFeature)temp;

                if (params.length == 2) {
                    // Assume params consist of sf and value
                    return new CommandParameter(target, sf, params[1]);
                }
            }
        }

        // Currently we are assuming that all commands will be sets
        final String sfName = method.getName().substring(3);

        // Lower case the first char and try to look-up the SF
        final String tempName = StringUtilities.lowerCaseFirstChar(sfName);
        sf = eTarget.eClass().getEStructuralFeature(tempName);

        // Try to get the SF with the feature name as-is (First char will probably be uppercase)
        if (sf == null) {
            sf = eTarget.eClass().getEStructuralFeature(sfName);
        }

        // Final fall-through... if sf is still null, try using substrings of the method name
        // looking at the last upper case char, and moving back from there.
        if (sf == null) {
            sf = complexSFSearch(sfName, eTarget);
        }

        // If we can't find a SF assume this should be a read txn
        if (sf == null) {
            return null;
        }

        return new CommandParameter(eTarget, sf, params[0]);
    }

    /**
     * Unable to locate SF via assuming just "setXXX". Now search by substring from the last upper case char backwards.
     * 
     * @return EStructuralFeature
     */
    private EStructuralFeature complexSFSearch( String sfName,
                                                EObject target ) {
        String tmp = StringUtilities.getLastUpperCharToken(sfName);
        EStructuralFeature result = target.eClass().getEStructuralFeature(tmp);

        // if result still null try lower casing first char
        if (result == null) {
            result = target.eClass().getEStructuralFeature(StringUtilities.lowerCaseFirstChar(tmp));
        }

        while (!sfName.equals(tmp) && result == null) {
            tmp = StringUtilities.getLastUpperCharToken(sfName, tmp);
            result = target.eClass().getEStructuralFeature(tmp);

            // if result stull null try lower casing first char
            if (result == null) {
                result = target.eClass().getEStructuralFeature(StringUtilities.lowerCaseFirstChar(tmp));
            }
        }

        return result;
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
}
