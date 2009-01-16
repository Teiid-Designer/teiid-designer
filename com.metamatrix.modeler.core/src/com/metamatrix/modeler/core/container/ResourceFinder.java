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

package com.metamatrix.modeler.core.container;

import org.eclipse.xsd.XSDSchemaDirective;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.core.id.ObjectID;
import com.metamatrix.core.id.UUID;
import com.metamatrix.metamodels.core.ModelImport;

/**
 * ResourceFinder. This object helps find resources in a Container.
 * @since 4.3
 */
public interface ResourceFinder {
    
    /** Length of a UUID string with protocol (e.g. "mmuuid:0b5fb081-1275-1eec-8518-c32201e76066") */
    public static final int UUID_STRING_LENGTH = 43;
    
    /** Delimiters used to separate the ObjectID protocol from it's value */
    public static final char STANDARD_UUID_PROTOCOL_DELIMITER  = ':';
    public static final char ALTERNATE_UUID_PROTOCOL_DELIMITER = '/';
    
    /** Possible UUID string prefixes */
    public static final String UUID_PROTOCOL_WITH_STANDARD_DELIMITER  = UUID.PROTOCOL + STANDARD_UUID_PROTOCOL_DELIMITER;
    public static final String UUID_PROTOCOL_WITH_ALTERNATE_DELIMITER = UUID.PROTOCOL + ALTERNATE_UUID_PROTOCOL_DELIMITER;

    /** Defines the expected suffix for any of the XSD global resources */
    public static final String SCHEMA_FOR_SCHEMA_URI_2001_SUFFIX = "www.w3.org/2001/XMLSchema.xsd"; //$NON-NLS-1$
    public static final String MAGIC_SCHEMA_URI_2001_SUFFIX      = "www.w3.org/2001/MagicXMLSchema.xsd"; //$NON-NLS-1$
    public static final String SCHEMA_INSTANCE_URI_2001_SUFFIX   = "www.w3.org/2001/XMLSchema-instance.xsd"; //$NON-NLS-1$
    
    /** Defines the expected URI for any of the XSD global resources */
    public static final URI MAGIC_SCHEMA_URI      = URI.createURI("http://www.w3.org/2001/MagicXMLSchema"); //$NON-NLS-1$
    public static final URI SCHEMA_FOR_SCHEMA_URI = URI.createURI(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001);
    public static final URI SCHEMA_INSTANCE_URI   = URI.createURI(XSDConstants.SCHEMA_INSTANCE_URI_2001);

    /** Defines MetaMatrix metamodel URI prefix */
    public static final String METAMATRIX_METAMODEL_PREFIX = "mtkplugin://"; //$NON-NLS-1$

    /** Defines IBM UML2 metamodel URI prefix */
    public static final String UML2_METAMODELS_PREFIX = "pathmap://UML2_METAMODELS/"; //$NON-NLS-1$
    
    public static final String VDB_WORKING_FOLDER_URI_PATH_SEGEMENT = ".metadata/.plugins/com.metamatrix.vdb.edit/vdbWorkingFolder"; //$NON-NLS-1$
    public static final String VDB_WORKING_FOLDER = "vdbWorkingFolder"; //$NON-NLS-1$
    
    /** 
     * Defines the expected name of the built-in datatype model and its built-in datatypes model URI. The values 
     * must be consistent with the values found in the com.metamatrix.modeler.sdt plugin.xml 
     */
    public static final String DATATYPES_MODEL_FILE_NAME = "builtInDataTypes.xsd"; //$NON-NLS-1$
    public static final String BUILTIN_DATATYPES_URI  = "http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"; //$NON-NLS-1$

    /** 
     * Defines the expected name of the built-in primitive types model and its primitive types internal URI.  The values 
     * must be consistent with the values found in the com.metamatrix.metamodels.uml2 plugin.xml
     */
    public static final String UML_PRIMITIVE_TYPES_MODEL_FILE_NAME = "primitiveTypes.xmi"; //$NON-NLS-1$
    public static final String UML_PRIMITIVE_TYPES_INTERNAL_URI = "http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"; //$NON-NLS-1$
        
    /** 
     * Defines the expected name of the built-in relationship types model and its relationship types internal URI. The values
     * must be consistent with the values found in the com.metamatrix.metamodels.relationship plugin.xml
     */
    public static final String RELATIONSHIP_PRIMITIVE_TYPES_MODEL_FILE_NAME = "builtInRelationshipTypes.xmi"; //$NON-NLS-1$
    public static final String RELATIONSHIP_PRIMITIVE_TYPES_INTERNAL_URI = "http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"; //$NON-NLS-1$    
    
    /** 
     * Defines the expected name of the built-in system physical model and its internal URI. The values must be consistent 
     * with the values found in the com.metamatrix.modeler.core plugin.xml
     */
    public static final String SYSTEM_PHYSICAL_MODEL_FILE_NAME = "SystemPhysical.xmi"; //$NON-NLS-1$    
    public static final String SYSTEM_PHYSICAL_INTERNAL_URI = "http://www.metamatrix.com/models/SystemPhysical.xmi"; //$NON-NLS-1$    
    
    /** 
     * Defines the expected name of the built-in system virtual model and its internal URI. The values must be consistent 
     * with the value found in the com.metamatrix.modeler.core plugin.xml
     */
    public static final String SYSTEM_VIRTUAL_MODEL_FILE_NAME = "System.xmi"; //$NON-NLS-1$    
    public static final String SYSTEM_VIRTUAL_INTERNAL_URI = "http://www.metamatrix.com/models/System.xmi"; //$NON-NLS-1$    
    
    /**
     * Find the resource with the specified XSDSchemaDirective.  If the resource referenced by
     * this import cannot be found in the resource set then null is returned.  Note: invoking 
     * this method may cause the resultant resource (or other XSD resources) to be loaded.
     * @param theImport
     * @param searchExternal If true, any external resource sets registered with this
     * resource set will also be searched.
     * @return
     * @since 4.3
     */
    Resource findByImport(XSDSchemaDirective theImport, boolean searchExternal);
    
    /**
     * Find the resource with the specified XSDSchemaDirective.  If the resource referenced by
     * this import cannot be found in the resource array defining the scope of the search
     * then null is returned.  Note: invoking this method may cause the resultant 
     * resource (or other XSD resources) to be loaded.
     * @param theImport
     * @param scope The array of Resource instances to be searched; may not be null
     * @return
     * @since 4.3
     */
    Resource findByImport(XSDSchemaDirective theImport, Resource[] scope);

    /**
     * Find the resource with the specified ModelImport.  If the resource referenced by
     * this import cannot be found in the resource set then null is returned.  Note: invoking 
     * this method will not cause the resultant resource to be loaded.
     * @param theImport
     * @param searchExternal If true, any external resource sets registered with this
     * resource set will also be searched.
     * @return
     * @since 4.3
     */
    Resource findByImport(ModelImport theImport, boolean searchExternal);
    
    /**
     * Find the resource with the specified ModelImport.  If the resource referenced by
     * this import cannot be found in the resource array defining the scope of the search
     * then null is returned.  Note: invoking this method will not cause the resultant 
     * resource to be loaded.
     * @param theImport
     * @param scope The array of Resource instances to be searched; may not be null
     * @return
     * @since 4.3
     */
    Resource findByImport(ModelImport theImport, Resource[] scope);
    
    /**
     * Find the resource with the specified ObjectID.  If no resource with this UUID can
     * be found in the resource set then null is returned.  Note: invoking this method 
     * will not cause the resultant resource to be loaded.
     * @param uuid The UUID identifying the resource
     * @param searchExternal If true, any external resource sets registered with this
     * resource set will also be searched.
     * @return the resource identified by this UUID or null if one could not be found
     * @since 4.3
     */
    Resource findByUUID(ObjectID uuid, boolean searchExternal);
    
    /**
     * Find the resource with the specified ObjectID.  If no resource with this UUID can
     * be found in the resource array defining the the scope of the search then null is 
     * returned. Note: invoking this method will not cause the resultant resource to be loaded.
     * @param uuid The UUID identifying the resource
     * @param scope The array of Resource instances to be searched; may not be null
     * @return the resource identified by this UUID or null if one could not be found
     * @since 4.3
     */
    Resource findByUUID(ObjectID uuid, Resource[] scope);
    
    /**
     * Find the resources with the specified name.  If no resources with this name can
     * be found in the resource set then null is returned.  Note: invoking this method will 
     * not cause the resultant resources to be loaded.
     * @param theName The name of the resource
     * @param caseSensitive If true names will be compared in a case-sensitive manner, otherwise,
     * case is ignored.
     * @param searchExternal If true, any external resource sets registered with this
     * resource set will also be searched.
     * @return an array of resources matching the criteria or an empty array if none are found
     * @since 4.3
     */
    Resource[] findByName(String theName, boolean caseSensitive, boolean searchExternal);
    
    /**
     * Find the resource with the specified URI. If no resource with this URI can be found in the
     * resource set then null is returned. Note: invoking this method will not cause the resultant 
     * resources to be loaded.
     * @param theUri The uri of the resource
     * @param searchExternal If true, any external resource sets registered with this
     * resource set will also be searched.
     * @return an array of resources matching the criteria or an empty array if none are found
     * @since 4.3
     */
    Resource findByURI(URI theUri, boolean searchExternal);
            
    /**
     * Return a reference to the resource containing this EObject.  If the EObject is an orphan
     * and does not exists in a resource then null will be returned.  If the EObject is an eProxy
     * the resource referenced by the eProxy URI will be returned (if it can be found in the
     * resource set), otherwise null will be returned. Note: invoking this method on an eProxy will not 
     * cause the resource to be loaded.
     * @param eObject
     * @return the resource containing the EObject or null if the EObject is not owned by a resource
     * or the EObject is an eProxy for which the reosurce cannot be found.
     * @since 4.3
     */
    Resource findByEObject(EObject eObject);
    
    /**
     * Find the resource with the specified URI. If no resource with this URI can be found in the
     * resource set then null is returned. Note: invoking this method will not cause the resultant 
     * resources to be loaded.
     * @param theUri The uri of the target resource
     * @param Resource The source resource
     * @return the resource identified by theUri
     * @since 5.0
     */
    Resource findByWorkspaceUri(URI theUri, Resource eResource);
    
    /**
     * Returns true if the specified URI identifies a resource that exists in
     * one of the external resources sets associated with this resource set,
     * otherwise false is returned.
     * @param uri the URI; may not be null
     * @return true if the URI identifies one of the global/shared resources
     */
    boolean isExternalResource(URI theUri);
    
    /**
     * Returns true if the specified resource exists in one of the external 
     * resources sets associated with this resource set, otherwise false is returned.
     * @param theResource The EMF resource to use; may not be null
     * @return true if the reosurce is one of the external resources
     */
    boolean isExternalResource(Resource theResource);
    
    /**
     * Returns true if the specified URI identifies one of the well-known 
     * MetaMatrix/EMF built-in shared resources such as 
     * <p>
     * <li>"http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"</li>
     * <li>"http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"</li>
     * <li>"http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"</li>
     * <li>"http://www.w3.org/2001/XMLSchema"</li>
     * <li>"http://www.w3.org/2001/MagicXMLSchema"</li>
     * <li>"http://www.w3.org/2001/XMLSchema-instance"</li>
     * </p>
     * otherwise false is returned.
     * @param uri the URI; may not be null
     * @return true if the URI identifies one of the global/shared resources
     */
    boolean isBuiltInResource(URI theUri);
    
    /**
     * Returns true if the specified resource is one of the well-known 
     * MetaMatrix/EMF resources built-in shared resources such as 
      * <p>
     * <li>"http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"</li>
     * <li>"http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"</li>
     * <li>"http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"</li>
     * <li>"http://www.w3.org/2001/XMLSchema"</li>
     * <li>"http://www.w3.org/2001/MagicXMLSchema"</li>
     * <li>"http://www.w3.org/2001/XMLSchema-instance"</li>
     * <li>"http://www.metamatrix.com/models/System.xmi"</li>
     * <li>"http://www.metamatrix.com/models/SystemPhysical.xmi"</li>
     * </p>
     * otherwise false is returned.
     * @param theResource The EMF resource to use; may not be null
     * @return true if the reosurce is one of the external resources
     */
    boolean isBuiltInResource(Resource theResource);
    
    /**
     * Returns true if the specified URI identifies one of the well-known MetaMatrix
     * system catalog shared resources available through one of the external resource 
     * sets.  Those resources are identified are
     * <p>
     * <li>"http://www.metamatrix.com/models/System.xmi"</li>
     * <li>"http://www.metamatrix.com/models/SystemPhysical.xmi"</li>
     * </p>
     * otherwise false is returned.
     * @param uri the URI; may not be null
     * @return true if the resource is one of the system resources
     */
    boolean isBuiltInSystemResource(URI theUri);
    
    /**
     * Returns true if the specified resource is one of the well-known MetaMatrix
     * system catalog shared resources available through one of the external resource 
     * sets.  Those resources are identified are
     * <p>
     * <li>"http://www.metamatrix.com/models/System.xmi"</li>
     * <li>"http://www.metamatrix.com/models/SystemPhysical.xmi"</li>
     * </p>
     * otherwise false is returned.
     * @param theResource The EMF resource to use; may not be null
     * @return true if the resource is one of the system resources
     */
    boolean isBuiltInSystemResource(Resource theResource);

    /**
     * Find the resources that this resource references.  The resultant array 
     * represents dependent resources that the specified resource requires to resolve 
     * its cross references.  If the recurse argument is true, then this method will
     * be recursely called on every dependent resource until the complete dependency
     * hierarchy is completed.  If the recurse argument is false, then only the immediate
     * dependencies are returned.  If the includeExternal argument is true, then references 
     * to resources in one of the resource set's external resource sets will be included 
     * in the result.  The external resource sets are used to store the well-known MetaMatrix 
     * and EMF global resource instances shared across all resources, such as
     * <p>
     * <li>"http://www.metamatrix.com/metamodels/SimpleDatatypes-instance"</li>
     * <li>"http://www.metamatrix.com/metamodels/UmlPrimitiveTypes-instance"</li>
     * <li>"http://www.metamatrix.com/relationships/BuiltInRelationshipTypes-instance"</li>
     * <li>"http://www.w3.org/2001/XMLSchema"</li>
     * <li>"http://www.w3.org/2001/MagicXMLSchema"</li>
     * <li>"http://www.w3.org/2001/XMLSchema-instance"</li>
     * </p>
     * If this resource does not have external references to other resources then an empty 
     * array is returned.  Note: invoking this method <b>will</b> force the specified resource
     * to be loaded along with the external resources that this resource references.
     * @param theResource The resource to check for references from; may not be null
     * @param recurse If true, the result will include all direct and indirect dependent resources otherwise
     * only the direct dependencies are returned.
     * @param includeExternal If true, external resource references will be included in the
     * resulant array, otherwise they will be excluded from the result.
     * @return an array of resources that this resource references.
     * @since 4.3
     */
    Resource[] findReferencesFrom(Resource theResource, boolean recurse, boolean includeExternal);

    /**
     * Find the resources that have references to this resource.  The resultant array represents
     * resources that have cross references to this specified resource.  If no other resources 
     * have references to this resource then an empty array is returned.  If the recurse argument
     * is true, then this method will be recursely check all direct or indirect references to the
     * specified resource.  If the recurse argument is false, then only those resources that directly
     * reference the specified resource are returned in the result.  Note: invoking this method <b>will</b> 
     * force the specified resource to be loaded along with any other unloaded resources in the resource set.
     * @param theResource The resource to check for references to; may not be null
     * @param recurse If true, the result will include all resources that directly or indirectly reference
     * the specified resource, otherwise only resources that directly reference this resource are returned.
     * @return an array of resources that reference the given resource.
     * @since 4.3
     */
    Resource[] findReferencesTo(Resource theResource, boolean recurse);
    
    /**
     * Return an array of strings representing any schema or model locations in this resource
     * that cannot be resolved within the associated resouce set. These locations, within
     * this resource, are external references to resources that cannot be resolved.  If all 
     * external references can be resolvable then an empty array is returned.
     * @param theResource
     * @return the array of schema or model location strings for any references that cannot
     * be resolved in this resource
     * @since 4.3
     */
    String[] findUnresolvedResourceLocations(Resource theResource);
    
    /**
     * Return an array of strings representing any schema or model locations in this resource
     * that are referenced but do not have a corresponding ModelImport contained in the ModelAnnotation.
     * These locations, within this resource, are external references to resources that may or may not be resolved.
     * @param theResource
     * @return the array of schema or model location strings for any references in this resource
     * @since 4.3
     */
    String[] findMissingImportLocations(Resource theResource);
    
//    /**
//     * Find the resources that the given resource references.
//     * @param resourceIdentifier The object identifying the a resource could be any of 
//     * {@link org.eclipse.emf.ecore.resource.Resource} , {@link org.eclipse.emf.common.util.URI} or a string
//     * representation of an URI.
//     * @return an array of resources that the given resource references.
//     * @since 4.3
//     */
//    Resource[] findResourcesReferencedFrom(Object resourceIdentifier);
//
//    /**
//     * Find the resources that reference the given resource.
//     * @param resourceIdentifier The object identifying the a resource could be any of 
//     * {@link org.eclipse.emf.ecore.resource.Resource} , {@link org.eclipse.emf.common.util.URI} or a string
//     * representation of an URI.
//     * @return an array of resources that reference the given resource.
//     * @since 4.3
//     */
//    Resource[] findResourcesReferencedTo(Object resourceIdentifier);
//    
//    /**
//     * Find the resource with the specified ObjectID. 
//     * @param uuid The UUID identifying the resource
//     * @param searchExternalResourceSets If true, any external resource sets registered with this
//     * container will also be searched.
//     * @return the resource identified by this UUID or null if one could not be found
//     * @since 4.3
//     */
//    Resource findResourceByUUID(ObjectID uuid, boolean searchExternalResourceSets);
//    
//    /**
//     * Find the resource with the specified ObjectID. 
//     * @param uuid The UUID identifying the resource
//     * @param scope The array of Resource instances to be searched; may not be null
//     * @return the resource identified by this UUID or null if one could not be found
//     * @since 4.3
//     */
//    Resource findResourceByUUID(ObjectID uuid, Resource[] scope);
//    
//    /**
//     * Find the resources with the specified name.
//     * @param name The name of the resource
//     * @param caseSensitive If true names will be compared in a case-sensitive manner, otherwise,
//     * case is ignored.
//     * @param searchExternalResourceSets If true, any external resource sets registered with this
//     * container will also be searched.
//     * @return an array of resources matching the criteria or an empty array if none are found
//     * @since 4.3
//     */
//    Resource[] findResourcesByName(String name, boolean caseSensitive, boolean searchExternalResourceSets);
//    
////    /**
////     * Find the resources with URIs that match the specified path.  The path may be a relative
////     * path in which case the resource URI must end with those path segments or the path may
////     * be absolute in which case the file URI must match exactly. 
////     * @param path The path that the resource URIs must match
////     * @param caseSensitive If true, the paths will be compared in a case-sensitive manner otherwise,
////     * if false, case is ignored.
////     * @param searchExternalResourceSets If true, any external resource sets registered with this
////     * container will also be searched.
////     * @return an array of resources matching the criteria or an empty array if none are found
////     * @since 4.3
////     */
////    Resource[] findResourcesByPath(IPath path, boolean caseSensitive, boolean searchExternalResourceSets);
//    
//    /**
//     * Find the resource with the specified URI.
//     * @param uri The uri of the resource
//     * @param searchExternalResourceSets If true, any external resource sets registered with this
//     * container will also be searched.
//     * @return the resource identified by this uri or null if one could not be found
//     * @since 4.3
//     */
//    Resource findResourceByURI(URI uri, boolean searchExternalResourceSets);
//    
//    /**
//     * Find the resource with the specified ModelImport.  If the resource referenced by
//     * this import cannot be found in the resource set then null is returned.  Note: invoking 
//     * this method will not cause the resultant resource to be loaded.
//     * @param theImport
//     * @param searchExternal If true, any external resource sets registered with this
//     * resource set will also be searched.
//     * @return
//     * @since 4.3
//     */
//    Resource findResourceByImport(ModelImport theImport, boolean searchExternal);

}
