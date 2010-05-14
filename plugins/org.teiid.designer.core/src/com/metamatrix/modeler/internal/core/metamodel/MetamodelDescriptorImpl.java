/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.metamodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
import org.osgi.framework.Bundle;
import com.metamatrix.core.MetaMatrixRuntimeException;
import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.CoreStringUtil;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.ModelInitializer;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;
import com.metamatrix.modeler.core.metamodel.MetamodelRootClassDescriptor;
import com.metamatrix.modeler.core.metamodel.aspect.DependencyAspect;
import com.metamatrix.modeler.core.metamodel.aspect.FeatureConstraintAspect;
import com.metamatrix.modeler.core.metamodel.aspect.ImportsAspect;
import com.metamatrix.modeler.core.metamodel.aspect.MetamodelAspectFactory;
import com.metamatrix.modeler.core.metamodel.aspect.ValidationAspect;
import com.metamatrix.modeler.core.metamodel.aspect.relationship.RelationshipMetamodelAspect;
import com.metamatrix.modeler.core.metamodel.aspect.sql.SqlAspect;
import com.metamatrix.modeler.core.metamodel.aspect.uml.UmlDiagramAspect;

/**
 * @author dfuglsang
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MetamodelDescriptorImpl implements MetamodelDescriptor {

    private static final ModelType[] EMPTY_MODEL_TYPE_ARRAY = new ModelType[0];
    private static final MetamodelRootClassDescriptor[] EMPTY_ROOT_CLASS_DESCRIPTOR_ARRAY = new MetamodelRootClassDescriptor[0];

    private static final String TRUE  = Boolean.TRUE.toString();
    private static final String FALSE = Boolean.FALSE.toString();
    private static final boolean IS_PRIMARY_DEFAULT_VALUE         = true;
    private static final boolean SUPPORTS_NEW_MODEL_DEFAULT_VALUE = IS_PRIMARY_DEFAULT_VALUE;
    private static final boolean SUPPORTS_DIAGRAMS_DEFAULT_VALUE  = false;
    private static final boolean SUPPORTS_EXTENSION_DEFAULT_VALUE = false;

    // Mappings between the plugin.xml extension ID for the MetamodelAspectFactory and the aspect type produced by that factory
    private static final Map EXTENSION_ID_TO_ASPECT_INTERFACE_MAP = new HashMap(7);
    static {
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.SQL_ASPECT.ID,                SqlAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.UML_DIAGRAM_ASPECT.ID,        UmlDiagramAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.VALIDATION_ASPECT.ID,         ValidationAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.DEPENDENCY_ASPECT.ID,         DependencyAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.FEATURE_CONSTRAINT_ASPECT.ID, FeatureConstraintAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.IMPORT_ASPECT.ID,             ImportsAspect.class);
        EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.put(ModelerCore.EXTENSION_POINT.RELATIONSHIP_ASPECT.ID,       RelationshipMetamodelAspect.class);
    }

    private String extensionID;
    private String displayName;
    private String namespaceURI;
    private Properties properties;
    private List alternateNamespaceURIs;
    private List allowableModelTypes;
    private String fileExtension;

    private DescriptorClassLoader ePackageClassLoader;
    private List adapterFactories;
    private List adapterFactoryDescriptorClassLoaders;
    private Map aspectFactoryMap;
    private Map aspectFactoryDescriptorClassLoaderMap;
    private List rootClassDescriptors;

    private final Map modelInitializerDescriptorClassLoadersByName;
    private final Map modelInitializerDescriptionsByName;
    private final List modelInitializerNames;
    private final List readOnlyModelInitializerNames;

    // ==================================================================================
    //                        C O N S T R U C T O R S
    // ==================================================================================

    /**
     *
     * @since 4.3
     */
    public MetamodelDescriptorImpl(final String theNamespaceURI,
                                   final String ePackageClassName,
                                   final Bundle bundle ) {
        CoreArgCheck.isNotZeroLength(theNamespaceURI);
        CoreArgCheck.isNotZeroLength(ePackageClassName);
        CoreArgCheck.isNotNull(bundle);

        this.namespaceURI = theNamespaceURI;
        this.ePackageClassLoader = new DescriptorClassLoader(ePackageClassName, bundle);

        this.extensionID = null;
        this.displayName = null;
        this.fileExtension = null;
        this.properties = new Properties();
        this.alternateNamespaceURIs = new ArrayList(11);
        this.allowableModelTypes = new ArrayList(11);

        this.adapterFactoryDescriptorClassLoaders = new ArrayList(11);
        this.adapterFactories = new ArrayList(11);
        this.aspectFactoryDescriptorClassLoaderMap = new HashMap(11);
        this.aspectFactoryMap = new HashMap(11);
        this.rootClassDescriptors = new ArrayList(11);

        this.modelInitializerDescriptorClassLoadersByName = new HashMap();
        this.modelInitializerDescriptionsByName = new HashMap();
        this.modelInitializerNames = new ArrayList();
        this.readOnlyModelInitializerNames = Collections.unmodifiableList(this.modelInitializerNames);
    }

    //==================================================================================
    //                     I N T E R F A C E   M E T H O D S
    //==================================================================================

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelDescriptor#getExtensionID()
     * @since 5.0
     */
    public String getExtensionID() {
        return this.extensionID;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelDescriptor#getDisplayName()
     * @since 5.0
     */
    public String getDisplayName() {
        return (this.displayName != null ? this.displayName : getName());
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getName()
     * @since 4.3
     */
    public String getName() {
        if (getEPackage() != null) {
            return getEPackage().getName();
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getNamespacePrefix()
     * @since 4.3
     */
    public String getNamespacePrefix() {
        if (getEPackage() != null) {
            return getEPackage().getNsPrefix();
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getNamespaceURI()
     * @since 4.3
     */
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.MetamodelDescriptor#getFileExtension()
     * @since 5.0
     */
    public String getFileExtension() {
        if (this.fileExtension == null) {
            this.fileExtension = ModelerCore.MODEL_FILE_EXTENSION;
        }
        return this.fileExtension;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getEPackage()
     * @since 4.3
     */
    public EPackage getEPackage() {
        return getPackageForURI( getNamespaceURI() );
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getAllowableModelTypes()
     * @since 4.3
     */
    public ModelType[] getAllowableModelTypes() {
        if (this.allowableModelTypes == null || this.allowableModelTypes.isEmpty()) {
            return EMPTY_MODEL_TYPE_ARRAY;
        }
        ModelType[] result = new ModelType[this.allowableModelTypes.size()];
        return (ModelType[])this.allowableModelTypes.toArray(result);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getAlternateNamespaceURIs()
     * @since 4.3
     */
    public String[] getAlternateNamespaceURIs() {
        if (this.alternateNamespaceURIs == null || this.alternateNamespaceURIs.isEmpty()) {
            return CoreStringUtil.Constants.EMPTY_STRING_ARRAY;
        }
        String[] result = new String[this.alternateNamespaceURIs.size()];
        return (String[])this.alternateNamespaceURIs.toArray(result);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#isPrimary()
     * @since 4.3
     */
    public boolean isPrimary() {
        boolean isPrimary = IS_PRIMARY_DEFAULT_VALUE;
        if (this.properties != null) {
            String value = (String)this.properties.get(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.PARTICIPATORY_ONLY);
            if (value != null && value.equalsIgnoreCase(TRUE)) {
                isPrimary = false;
            }
        }
        return isPrimary;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#supportsDiagrams()
     * @since 4.3
     */
    public boolean supportsDiagrams() {
        boolean supportsDiagrams = SUPPORTS_DIAGRAMS_DEFAULT_VALUE;
        if (this.properties != null) {
            String value = (String)this.properties.get(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.SUPPORTS_DIAGRAMS);
            if (value != null) {
                supportsDiagrams = Boolean.valueOf(value).booleanValue();
            }
        }
        return supportsDiagrams;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#supportsExtension()
     * @since 4.3
     */
    public boolean supportsExtension() {
        boolean supportsExtension = SUPPORTS_EXTENSION_DEFAULT_VALUE;
        if (this.properties != null) {
            String value = (String)this.properties.get(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.SUPPORTS_EXTENSION);
            if (value != null) {
                supportsExtension = Boolean.valueOf(value).booleanValue();
            }
        }
        return supportsExtension;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#supportsNewModel()
     * @since 4.3
     */
    public boolean supportsNewModel() {
        boolean supportsNewModel = SUPPORTS_NEW_MODEL_DEFAULT_VALUE;
        if (this.properties != null) {
            String value = (String)this.properties.get(ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.CREATE_AS_NEW_MODEL);
            if (value != null) {
                supportsNewModel = Boolean.valueOf(value).booleanValue();
            }
        }
        return supportsNewModel;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getModelInitializer(java.lang.String)
     * @since 4.3
     */
    public ModelInitializer getModelInitializer(final String name) {
        final DescriptorClassLoader loader = (DescriptorClassLoader)this.modelInitializerDescriptorClassLoadersByName.get(name);
        if ( loader != null ) {
            final Object instance = loader.getClassInstance();
            if ( instance instanceof ModelInitializer ) {
                return (ModelInitializer)instance;
            }
        }
        return null;
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getModelInitializerDescription(java.lang.String)
     * @since 4.3
     */
    public String getModelInitializerDescription(final String name) {
        return (String) this.modelInitializerDescriptionsByName.get(name);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getModelInitializerNames()
     * @since 4.3
     */
    public List getModelInitializerNames() {
        return this.readOnlyModelInitializerNames;
    }
    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getRootClassDescriptors()
     * @since 5.0
     */
    public MetamodelRootClassDescriptor[] getRootClassDescriptors() {
        if (this.rootClassDescriptors == null || this.rootClassDescriptors.isEmpty()) {
            return EMPTY_ROOT_CLASS_DESCRIPTOR_ARRAY;
        }
        MetamodelRootClassDescriptor[] result = new MetamodelRootClassDescriptor[this.rootClassDescriptors.size()];
        return (MetamodelRootClassDescriptor[]) this.rootClassDescriptors.toArray(result);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getAdapterFactories()
     * @since 5.0
     */
    public AdapterFactory[] getAdapterFactories() {
        if (this.adapterFactories.isEmpty()) {
            initializeAdapterFactoryList();
        }
        AdapterFactory[] result = new AdapterFactory[this.adapterFactories.size()];
        return (AdapterFactory[])this.adapterFactories.toArray(result);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getAspectFactories()
     * @since 5.0
     */
    public MetamodelAspectFactory[] getAspectFactories() {
        if (this.aspectFactoryMap.isEmpty()) {
            initializeAspectFactoryMap();
        }
        MetamodelAspectFactory[] result = new MetamodelAspectFactory[this.aspectFactoryMap.values().size()];
        return (MetamodelAspectFactory[])this.aspectFactoryMap.values().toArray(result);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getAspectFactory(java.lang.Class)
     * @since 5.0
     */
    public MetamodelAspectFactory getAspectFactory(final Class theType) {
        CoreArgCheck.isNotNull(theType);
        if (this.aspectFactoryMap.isEmpty()) {
            initializeAspectFactoryMap();
        }
        return (MetamodelAspectFactory)this.aspectFactoryMap.get(theType);
    }

    /**
     * @see com.metamatrix.modeler.core.metamodel.ProtoMetamodelDescriptor#getAspectFactory(java.lang.String)
     * @since 5.0
     */
    public MetamodelAspectFactory getAspectFactory(final String extensionID) {
        CoreArgCheck.isNotZeroLength(extensionID);
        final Class type = (Class)EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.get(extensionID);
        if (type == null) {
            final String msg = ModelerCore.Util.getString("MetamodelDescriptorImpl.Extension_ID_does_not_match_any_metamodel_aspect_class",extensionID); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }
        return getAspectFactory(type);
    }

    // ==================================================================================
    //                      P U B L I C   M E T H O D S
    // ==================================================================================

    public void setExtensionID(final String theExtensionID) {
        this.extensionID = theExtensionID;
    }

    public void setDisplayName(final String theDisplayName) {
        this.displayName = theDisplayName;
    }

    public void setFileExtension(final String string) {
        CoreArgCheck.isNotZeroLength(string);
        final String extension = string.trim();
        this.fileExtension = (extension.charAt(0) == '.' ? extension : "."+extension); //$NON-NLS-1$
    }

    public Properties getProperties() {
        return this.properties;
    }

    public void setProperties(final Properties properties) {
        CoreArgCheck.isNotNull(properties);
        this.properties = properties;

        addDefaultPropertyValues(this.properties);
    }

    public void addAlternateNamespaceURI(final String alternateNamespaceURI) {
        CoreArgCheck.isNotZeroLength(alternateNamespaceURI);

        if ( !this.alternateNamespaceURIs.contains(alternateNamespaceURI) ) {
            this.alternateNamespaceURIs.add(alternateNamespaceURI);
        }
    }

    public void addAllowableModelType(final String modelTypeName) {
        CoreArgCheck.isNotZeroLength(modelTypeName);

        ModelType type = ModelType.get(modelTypeName);
        if (type != null && !this.allowableModelTypes.contains(type)) {
            this.allowableModelTypes.add(type);
        }
    }

    public void addRootClassDescriptor(final MetamodelRootClassDescriptor descriptor) {
        CoreArgCheck.isNotNull(descriptor);

        for (Iterator i = this.rootClassDescriptors.iterator(); i.hasNext();) {
            final MetamodelRootClassDescriptor d = (MetamodelRootClassDescriptor)i.next();
            if (descriptor.getClassName() != null && descriptor.getClassName().equals(d.getClassName())) {
                return;
            }
        }
        this.rootClassDescriptors.add(descriptor);
    }

    public void addAdapterFactoryBundle( final String className,
	                                     final Bundle bundle ) {
		DescriptorClassLoader newDescriptor = new DescriptorClassLoader(className, bundle);

        if ( !this.adapterFactoryDescriptorClassLoaders.contains(newDescriptor)) {
            this.adapterFactoryDescriptorClassLoaders.add(newDescriptor);
        }
    }

    public void addAspectFactoryBundle( final String extensionPointID,
	                                    final String className,
	                                    final Bundle bundle ) {
        CoreArgCheck.isNotZeroLength(extensionPointID);

        // Map the extensionPointID to the metamodel aspect that it represents
        final String extensionID = CoreStringUtil.getLastToken(extensionPointID,ModelerCore.DELIMITER);
        final Class type = (Class)EXTENSION_ID_TO_ASPECT_INTERFACE_MAP.get(extensionID);
        if (type == null) {
            final String msg = ModelerCore.Util.getString("MetamodelDescriptorImpl.Extension_ID_does_not_match_any_metamodel_aspect_class",extensionID); //$NON-NLS-1$
            throw new IllegalArgumentException(msg);
        }

        // Remove any existing MetamodelAspectFactory with this ID
        if (this.aspectFactoryDescriptorClassLoaderMap.containsKey(type)) {
            this.aspectFactoryDescriptorClassLoaderMap.remove(type);
        }

        DescriptorClassLoader newDescriptor = new DescriptorClassLoader(className, bundle);
        this.aspectFactoryDescriptorClassLoaderMap.put(type, newDescriptor);
    }

    public void addModelInitializer( final String name,
	                                 final String desc,
	                                 final String className,
	                                 final Bundle bundle ) {
		final DescriptorClassLoader loader = new DescriptorClassLoader(className, bundle);
        try {
            this.modelInitializerDescriptorClassLoadersByName.put(name,loader);
            this.modelInitializerDescriptionsByName.put(name,desc);
            this.modelInitializerNames.add(name);
        } catch (Throwable e) {
            // If there was an exception adding one of the two entries, make sure to clean them both up
            try {
                this.modelInitializerDescriptorClassLoadersByName.remove(name);
                this.modelInitializerDescriptionsByName.remove(name);
            } catch (RuntimeException e1) {
                ModelerCore.Util.log(e1);
            }
        }
    }

    public void dispose() {
        this.properties = null;
        this.alternateNamespaceURIs.clear();
        this.allowableModelTypes.clear();

        this.ePackageClassLoader = null;
        this.adapterFactories.clear();
        this.adapterFactoryDescriptorClassLoaders.clear();
        this.aspectFactoryMap.clear();
        this.aspectFactoryDescriptorClassLoaderMap.clear();
        this.rootClassDescriptors.clear();

        this.modelInitializerDescriptorClassLoadersByName.clear();
        this.modelInitializerDescriptionsByName.clear();
        this.modelInitializerNames.clear();
    }

    // ==================================================================================
    //                    P R O T E C T E D   M E T H O D S
    // ==================================================================================

    protected EPackage.Registry getEPackageRegistry() {
        return EPackage.Registry.INSTANCE;
    }

    /**
     * Attempt to get the namespace for the given prefix, then return
     * ERegister.getPackage() or null.
     */
    protected EPackage getPackageForURI(final String uriString) {
        if (CoreStringUtil.isEmpty(uriString)) {
            return null;
        }

        EPackage ePackage = getEPackageRegistry().getEPackage(uriString);
        if (ePackage != null && ePackage.eIsProxy()) {
            getEPackageRegistry().remove(uriString);
            ePackage = null;
        }

        // Create the EPackage instance if it is not found in the EPackage registry
        if ( ePackage == null && this.ePackageClassLoader != null) {

            // Expecting the java Class to be the EPackage interface for the specific
            // metamodel (e.g. "com.metamatrix.metamodels.relational.RelationalPackage")
            Class javaClass = this.ePackageClassLoader.getLoadedClass();
            if (javaClass != null) {

                // Initialize the EPackage contents
                if (javaClass.isInterface()) {
                    try {
                        Field field = javaClass.getField("eINSTANCE"); //$NON-NLS-1$
                        ePackage = (EPackage)field.get(null);
                    } catch (Exception e) {
                        throw new MetaMatrixRuntimeException(e);
                    }
                } else {
                    try {
                        Class[] interfaces = javaClass.getInterfaces();
                        for (int i = 0; i < interfaces.length; i++) {
                            Field field = interfaces[i].getField("eINSTANCE"); //$NON-NLS-1$
                            ePackage = (EPackage)field.get(null);
                        }
                    } catch (Exception e) {
                        throw new MetaMatrixRuntimeException(e);
                    }
                }

                if (ePackage != null) {
                    // Register the EPackage ...
                    getEPackageRegistry().put(uriString, ePackage);

                    // If the eNS_URI defined in the metamodel specific EPackage is different
                    // than the one associated with this descriptor then register both
                    if (!uriString.equals(ePackage.getNsURI())) {
                        getEPackageRegistry().put(ePackage.getNsURI(), ePackage);
                    }
                }
            }
        }

        // If there are alternate namespace URIs for this EPackage then register them also.
        if (ePackage != null && this.alternateNamespaceURIs != null && !this.alternateNamespaceURIs.isEmpty()) {
            EPackage.Registry ePkgRegistry = getEPackageRegistry();
            for (Iterator iter = this.alternateNamespaceURIs.iterator(); iter.hasNext();) {
                String nsUri = (String)iter.next();
                if (ePkgRegistry.getEPackage(nsUri) == null) {
                    ePkgRegistry.put(nsUri, ePackage);
                }
            }
        }

        return ePackage;
    }

    /**
     * For any property values that are not defined add its default value
     * @param props
     */
    protected void addDefaultPropertyValues(final Properties props) {
        String key = ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.PARTICIPATORY_ONLY;
        String val = props.getProperty(key);
        boolean participatoryOnly;
        if (val == null) {
            participatoryOnly = !IS_PRIMARY_DEFAULT_VALUE;
            props.setProperty(key,Boolean.toString(!IS_PRIMARY_DEFAULT_VALUE));
        } else {
            participatoryOnly = Boolean.valueOf(val).booleanValue();
        }

        key = ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.SUPPORTS_DIAGRAMS;
        if (props.getProperty(key) == null) {
            props.setProperty(key,Boolean.toString(SUPPORTS_DIAGRAMS_DEFAULT_VALUE));
        }

        key = ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.SUPPORTS_EXTENSION;
        if (props.getProperty(key) == null) {
            props.setProperty(key,Boolean.toString(SUPPORTS_EXTENSION_DEFAULT_VALUE));
        }

        key = ModelerCore.EXTENSION_POINT.METAMODEL.ATTRIBUTES.CREATE_AS_NEW_MODEL;
        if (participatoryOnly) {
            props.setProperty(key, FALSE);
        } else if (props.getProperty(key) == null) {
            props.setProperty(key, Boolean.toString(SUPPORTS_NEW_MODEL_DEFAULT_VALUE));
        }
    }

    protected void initializeAdapterFactoryList() {
        if (this.adapterFactoryDescriptorClassLoaders != null && !this.adapterFactoryDescriptorClassLoaders.isEmpty()) {
            for (Iterator i = this.adapterFactoryDescriptorClassLoaders.iterator(); i.hasNext();) {
                DescriptorClassLoader d = (DescriptorClassLoader)i.next();
                AdapterFactory factory = (AdapterFactory)d.getClassInstance();
                if (factory != null && !this.adapterFactories.contains(factory)) {
                    this.adapterFactories.add(factory);
                }
            }
        }
    }

    protected void initializeAspectFactoryMap() {
        if (this.aspectFactoryDescriptorClassLoaderMap != null && !this.aspectFactoryDescriptorClassLoaderMap.isEmpty()) {
            for (Iterator i = this.aspectFactoryDescriptorClassLoaderMap.entrySet().iterator(); i.hasNext();) {
                Map.Entry entry = (Map.Entry)i.next();
                Class type = (Class)entry.getKey();
                DescriptorClassLoader d = (DescriptorClassLoader)entry.getValue();
                MetamodelAspectFactory factory = (MetamodelAspectFactory)d.getClassInstance();
                if (factory != null) {
                    this.aspectFactoryMap.put(type, factory);
                }
            }
        }
    }

    // ==================================================================================
    //                        I N N E R   C L A S S
    // ==================================================================================

    private class DescriptorClassLoader {
        private final String className;
        private final Bundle bundle;
        private boolean loadClassFailure;
        private boolean newInstanceFailure;
        private Class loadedClass;
        private Object classInstance;

        public DescriptorClassLoader( final String className,
		                              final Bundle bundle ) {
            CoreArgCheck.isNotZeroLength(className);
            CoreArgCheck.isNotNull(bundle);
            this.className          = className;
            this.bundle = bundle;
            this.loadClassFailure   = false;
            this.newInstanceFailure = false;
            this.loadedClass        = null;
            this.classInstance      = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof DescriptorClassLoader)) {
                return false;
            }
            DescriptorClassLoader that = (DescriptorClassLoader)obj;
            if (this.className.equals(that.className) && this.bundle.equals(that.bundle)) {
                return true;
            }
            return super.equals(obj);
        }

        /**
         * Return the loaded class associated with this instance
         * @return Class
         */
        public Class getLoadedClass() {
            if (!loadClassFailure && loadedClass == null) {
                try {
                    loadedClass = bundle.loadClass(className);
                    loadClassFailure = false;
                } catch (Throwable e) {
                    String msg = ModelerCore.Util.getString("MetamodelDescriptorImpl.Unable_to_load_class_using_bundle", className, bundle); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR,msg);
                    loadedClass = null;
                    loadClassFailure = true;
                }
            }
            return loadedClass;
        }

        /**
         * Get instance of the class
         * @return Object
         */
        public Object getClassInstance() {
            if (!newInstanceFailure && classInstance == null && getLoadedClass() != null) {
                try {
                    classInstance = getLoadedClass().newInstance();
                    newInstanceFailure = false;
                } catch (InstantiationException e) {
                    String msg = ModelerCore.Util.getString("MetamodelDescriptorImpl.Unable_to_create_instance",className); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR,e,msg);
                    classInstance = null;
                    newInstanceFailure = true;
                } catch (IllegalAccessException e) {
                    String msg = ModelerCore.Util.getString("MetamodelDescriptorImpl.Error_creating_instance",className); //$NON-NLS-1$
                    ModelerCore.Util.log(IStatus.ERROR,e,msg);
                    classInstance = null;
                    newInstanceFailure = true;
                }
            }
            return classInstance;
        }

        @Override
        public String toString() {
            final Object[] params = new Object[] {className, bundle, new Boolean(loadedClass != null)};
            return ModelerCore.Util.getString("MetamodelDescriptorImpl.descriptor_info",params); //$NON-NLS-1$
        }
    }

}
