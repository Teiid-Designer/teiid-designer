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

package com.metamatrix.metamodels.relationship.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.core.util.ArgCheck;
import com.metamatrix.metamodels.relationship.RelationshipMetamodelPlugin;
import com.metamatrix.metamodels.relationship.RelationshipType;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.Container;
import com.metamatrix.modeler.internal.core.ExternalResourceDescriptorImpl;
import com.metamatrix.modeler.internal.core.ExternalResourceLoader;

/**
 * RelationshipTypeManager is a singleton used for managing built-in Relationship Types.
 * A built-in relationship type is available to all Relationship models.
 */
public class RelationshipTypeManager {

    /** Defines the expected primitive types internal URI */
    public static final String BUILTIN_RELATIONSHIP_TYPES_INTERNAL_URI = "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"; //$NON-NLS-1$

    /** Defines the URI for the primitive types model */
    public static final URI BUILTIN_RELATIONSHIP_TYPES_URI = URI.createURI(BUILTIN_RELATIONSHIP_TYPES_INTERNAL_URI);

    /** Defines the expected name of the primitive types model file */
    public static final String BUILTIN_RELATIONSHIP_TYPES_MODEL_FILE_NAME = "builtInRelationshipTypes.xmi"; //$NON-NLS-1$

    /** Defines the expected name of theprimitive types archive file */
    public static final String BUILTIN_RELATIONSHIP_TYPES_ZIP_FILE_NAME = "builtInRelationshipTypes.zip"; //$NON-NLS-1$

    public static final String ANY_TYPE_NAME = Names.ANY;

    public static final String ANY_TYPE_URI_FRAGMENT = "mmuuid/954c4b03-4200-1f04-9a10-e53b219c8f76"; //$NON-NLS-1$

    private final URI uri;

    /** Reference to the EMF resource for the primitive types model */
//    private final Resource resource;

    /** Map of primitive type name to PrimitiveType instance */
    private final Map nameToType = new HashMap();

    private final Map lowercaseNameToType = new HashMap();

    private final List allTypes = new ArrayList();

    private final List unmodifiableAllTypes = Collections.unmodifiableList(allTypes);

    private static RelationshipTypeManager INSTANCE;
    private static final Object LOCK = new Object();

    public static class Names {
        public static final String ANY              = "Any"; //$NON-NLS-1$
        public static final String GENERIC          = "Generic"; //$NON-NLS-1$
        public static final String COMPOSITION      = "Composition"; //$NON-NLS-1$
        public static final String GENERALIZATION   = "Generalization"; //$NON-NLS-1$
        public static final String DEPENDENCY       = "Dependency"; //$NON-NLS-1$
        public static final String CLASSIFICATION   = "Classification"; //$NON-NLS-1$
        public static final String CAUSALITY        = "Causality"; //$NON-NLS-1$
        public static final String PROCESS          = "Process"; //$NON-NLS-1$
        public static final String REALIZATION      = "Realization"; //$NON-NLS-1$
        public static final String USAGE            = "Usage"; //$NON-NLS-1$
        public static final String REFINEMENT       = "Refinement"; //$NON-NLS-1$
        public static final String TRANSFORMATION   = "Transformation"; //$NON-NLS-1$
        public static final String MANIFESTATION    = "Manifestation"; //$NON-NLS-1$
        public static final String CONSUMPTION      = "Consumption"; //$NON-NLS-1$
        public static final String COMPLIANCE       = "Compliance"; //$NON-NLS-1$
    }

    /**
     * Construct an instance of RelationshipTypeManager.
     */
    public RelationshipTypeManager( final URI uri ) {
        super();
        ArgCheck.isNotNull(uri);
        this.uri = uri;
//        this.resource =
            doLoad(this.uri);
    }

    /**
     * Construct an instance of RelationshipTypeManager.
     */
    public RelationshipTypeManager( final Resource resource ) {
        super();
        ArgCheck.isNotNull(resource);
        this.uri = resource.getURI();
//        this.resource = resource;
    }

    /**
     * Obtain the shared instance.
     * @return
     */
    public static RelationshipTypeManager getInstance() {
        if ( INSTANCE == null ) {
            synchronized(LOCK) {
                INSTANCE = new RelationshipTypeManager(BUILTIN_RELATIONSHIP_TYPES_URI);
            }
        }
        return INSTANCE;
    }

    /**
     * Return the {@link ANY_TYPE_NAME Any} built-in {@link RelationshipType} instance.
     * @return the reference to the Any built-in type
     */
    public RelationshipType getAnyRelationshipType() {
        return getBuiltInRelationshipType(ANY_TYPE_NAME);
    }

    /**
     * Return the built-in {@link RelationshipType} instance with the specified case-insensitive name. Only
     * the MetaMatrix predefined built-in types are available through this manager. This method will return
     * null for any name that is not one of these predefined types.
     *
     * @param name
     * @return
     */
    public RelationshipType getBuiltInRelationshipType( final String name ) {
        return getBuiltInRelationshipType(name,false);
    }

    /**
     * Return the built-in {@link RelationshipType} instance with the specified name. Only
     * the MetaMatrix predefined built-in types are available through this manager. This method will return
     * null for any name that is not one of these predefined types.
     *
     * @param name
     * @return
     */
    public RelationshipType getBuiltInRelationshipType( final String name, final boolean caseSensitive ) {
        ArgCheck.isNotZeroLength(name);
        if ( caseSensitive ) {
            return (RelationshipType)this.nameToType.get(name);
        }
        return (RelationshipType)this.lowercaseNameToType.get(name.toLowerCase());
    }

    public List getAllBuiltInRelationshipTypes() {
        return this.unmodifiableAllTypes;
    }

    /**
     * Determine whether a built-in {@link RelationshipType} instance exists with the specified case-insensitive name.
     * @param name
     * @return return true if a built-in RelationshipType instance exists with the specified name, or false otherwise.
     */
    public boolean hasBuiltInRelationshipType(final String name) {
        return hasBuiltInRelationshipType(name,false);
    }

    /**
     * Determine whether a built-in {@link RelationshipType} instance exists with the specified name.
     * @param name
     * @return return true if a built-in RelationshipType instance exists with the specified name, or false otherwise.
     */
    public boolean hasBuiltInRelationshipType(final String name, final boolean caseSensitive) {
        ArgCheck.isNotZeroLength(name);
        if ( caseSensitive ) {
            return this.nameToType.containsKey(name);
        }
        return this.lowercaseNameToType.containsKey(name.toLowerCase());
    }

    /**
     * Return true if the supplied relationship type is one of the built-in types exposed by
     * this manager.
     * @param type the type; may not be null
     * @return
     */
    public boolean isBuiltInRelationshipType( final RelationshipType type ) {
        ArgCheck.isNotNull(type);
        final Resource resource = type.eResource();
        if ( resource != null ) {
            final URI typeResourceUri = resource.getURI();
            if ( this.uri.equals(typeResourceUri) ) {
                return true;
            }
        }
        return false;
    }

    public boolean isBuiltInAnyRelationshipType( final RelationshipType type ) {
        ArgCheck.isNotNull(type);
        final String name = type.getName();
        if ( ANY_TYPE_NAME.equals(name) ) {
            if ( isBuiltInRelationshipType(type) ) {
                return true;
            }
            final Resource resource = type.eResource();
            if ( resource != null ) {
                final String uriFragment = resource.getURIFragment(type);
                if ( ANY_TYPE_URI_FRAGMENT.equals(uriFragment) ) {
                    return true;        // the built-in model must be in the workspace as a normal model
                }
            }
        }
        return false;
    }

    // ==================================================================================
    //                    P R O T E C T E D       M E T H O D S
    // ==================================================================================

    protected URI getUri() {
        return this.uri;
    }

    protected Resource doLoad( final URI modelUri ) {
        final Container container = getModelContainer();

        // Load the external resource into the container
        Resource theResource = null;
        if (container.getResource(modelUri, false) != null) {
            theResource = container.getResource(modelUri, false);
            //System.out.println("Obtaining previously loaded resource " + modelUri);
        } else {
            // Create a descriptor defining the primitive types external resource
            final ExternalResourceDescriptor descriptor = getExternalResourceDescriptor();

            //System.out.println("Loading resource " + modelUri);
            theResource = loadContainer(descriptor, container);
        }

        // Populate the HashMap with the primitive types found in the external resource
        if (theResource != null) {
            for (Iterator iter = theResource.getAllContents(); iter.hasNext();) {
                EObject eObject = (EObject)iter.next();
                if (eObject instanceof RelationshipType) {
                    RelationshipType rt = (RelationshipType)eObject;
                    this.nameToType.put(rt.getName(), rt);
                    this.lowercaseNameToType.put(rt.getName().toLowerCase(), rt);
                    this.allTypes.add(rt);
                }
            }
        }
        return theResource;
    }

    protected static Container getModelContainer() {
        Container container = null;
        try {
            container = ModelerCore.getModelContainer();
        } catch (Throwable t) {
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Error_retrieving_model_container_reference_1"); //$NON-NLS-1$
            RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, t, msg);
        }
        if (container == null) {
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Failed_to_retrieve_model_container_reference_2"); //$NON-NLS-1$
            RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, msg);
        }
        return container;
    }

    protected static Container createContainer(final String containerName) {
        Container container = null;
        try {
            container = ModelerCore.createEmptyContainer(containerName);
        } catch (Throwable t) {
            final Object[] params = new Object[]{containerName,BUILTIN_RELATIONSHIP_TYPES_MODEL_FILE_NAME};
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Error_creating_model_container_{0}_when_loading_{1}_3",params); //$NON-NLS-1$
            RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, t,msg);
        }
        if (container == null) {
            final Object[] params = new Object[]{containerName,BUILTIN_RELATIONSHIP_TYPES_MODEL_FILE_NAME};
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Failed_to_create_model_container_{0}_when_loading_{1}_4",params); //$NON-NLS-1$
            RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, msg);
        }
        return container;
    }

    protected static Resource loadContainer(final ExternalResourceDescriptor descriptor,
                                            final Container container) {
        Resource resource = null;
        if (container != null) {
            final ExternalResourceLoader loader = new ExternalResourceLoader();
            try {
                resource = loader.load(descriptor, container);
            } catch (Throwable t) {
                final Object[] params = new Object[]{descriptor.getResourceName(),container.getName()};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Error_loading_external_resource_{0}_into_model_container_{1}_5",params); //$NON-NLS-1$
                RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, t,msg);
            }
        }
        return resource;
    }

    protected static ExternalResourceDescriptor getExternalResourceDescriptor() {
        final ExternalResourceDescriptorImpl descriptor = new ExternalResourceDescriptorImpl();

        // Set the plugin and extension IDs
        descriptor.setPluginID(RelationshipMetamodelPlugin.PLUGIN_ID);
        descriptor.setExtensionID(ModelerCore.EXTENSION_POINT.EXTERNAL_RESOURCE.ID);

        // Define the name of the resource to load
        descriptor.setResourceName(BUILTIN_RELATIONSHIP_TYPES_MODEL_FILE_NAME);

        // Define the internal URI to use when retrieving this resource,
        descriptor.setInternalUri(BUILTIN_RELATIONSHIP_TYPES_INTERNAL_URI);

        // Define the resource location in terms of the declaring plugin location on the a file system
        String resourceURL = null;
        try {
            final URL installURL = FileLocator.resolve(RelationshipMetamodelPlugin.getDefault().getBundle().getEntry("/")); //$NON-NLS-1$
            resourceURL = FileLocator.toFileURL(new URL(installURL, BUILTIN_RELATIONSHIP_TYPES_ZIP_FILE_NAME)).getFile();
            if (resourceURL == null || resourceURL.trim().length() == 0) {
                final Object[] params = new Object[]{BUILTIN_RELATIONSHIP_TYPES_ZIP_FILE_NAME};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Unable_to_create_an_absolute_path_to_the_resource_{0}_6",params); //$NON-NLS-1$
                RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, msg);
            } else {
                descriptor.setResourceUrl(resourceURL);
            }
        } catch (Throwable t) {
            final Object[] params = new Object[]{BUILTIN_RELATIONSHIP_TYPES_ZIP_FILE_NAME};
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Error_creating_local_URL_for_{0}_7",params); //$NON-NLS-1$
            RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, t,msg);
        }

        // Define the temporary working directory location in terms of the declaring plugin location on the a file system
        String tempDirPath = null;
        try {
            tempDirPath = RelationshipMetamodelPlugin.getDefault().getStateLocation().toOSString();
            if (tempDirPath == null || tempDirPath.trim().length() == 0) {
                final Object[] params = new Object[]{descriptor.getResourceUrl()};
                final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Unable_to_create_an_absolute_path_to_the_data_directory_for_{0}_8",params); //$NON-NLS-1$
                RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, msg);
            } else {
                descriptor.setTempDirectoryPath(tempDirPath);
            }
        } catch (Throwable t) {
            final Object[] params = new Object[]{BUILTIN_RELATIONSHIP_TYPES_ZIP_FILE_NAME};
            final String msg = RelationshipMetamodelPlugin.Util.getString("RelationshipTypeManager.Error_creating_the_absolute_path_to_the_data_directory_for_{0}_9",params); //$NON-NLS-1$
            RelationshipMetamodelPlugin.Util.log(IStatus.ERROR, t,msg);
        }

        return descriptor;
    }

}
