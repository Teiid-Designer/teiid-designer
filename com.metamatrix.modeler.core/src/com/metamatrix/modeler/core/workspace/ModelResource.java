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

package com.metamatrix.modeler.core.workspace;

import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.emf.ecore.resource.Resource;

import com.metamatrix.metamodels.core.ModelAnnotation;
import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.core.metamodel.MetamodelDescriptor;

/**
 * A ModelResource is an {@link org.eclipse.core.resources.IResource Eclipse resource}
 * that are models that can be understood by the Modeler.
 * <p>
 * If a model resource cannot be parsed, its structure remains unknown.  Use
 * {@link ModelWorkspaceItem#isStructureKnown()} to determine whether this is the
 * case.
 * </p>
 * <p>
 * Model resource items need to be opened before they can be navigated or manipulated.
 * The children are of type {@link ModelImports},
 * {@link MetamodelImports} and {@link org.eclipse.emf.ecore.EObject},
 * and appear in the order in which they are declared in the source.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface ModelResource extends ModelWorkspaceItem, Openable {

	/**
	 * Constants indicating if the ModelResource is indexed.
	 */
	// ModelResource is not indexed
	int NOT_INDEXED = 0;

	// only metadata indexes have been created for the model Resource
	int METADATA_INDEXED = 1;

	// only search indexes have been created for the model Resource
	int SEARCH_INDEXED = 2;

	// ModelResource is compleately indexed
	int INDEXED = 3;

    /**
     * Return for the primary metamodel in this model
     * @return the primary metamodel URI string; may be null if 
     * the primary metamodel is not registered
     */
    String getPrimaryMetamodelUri() throws ModelWorkspaceException;

    /**
     * Return the {@link com.metamatrix.metamodels.core.ModelType} for this
     * resource.  The model type information is obtained by either reading
     * the model file header information or by examining the model annotation 
     * node depending on whether the resource has been opened. If the type 
     * cannot be determined then ModelType.PHYSICAL will be returned as the default.
     * @return
     */
    ModelType getModelType() throws ModelWorkspaceException;
    
    /**
     * Return the description string defined for this resource or null
     * if one does not exist. The description information is obtained by either
     * reading the model file header information or by examining the model 
     * annotation node depending on whether the resource has been opened. 
     * @return
     */
    String getDescription() throws ModelWorkspaceException;
    
    /**
     * Return the UUID string defined for this resource or null
     * if one does not exist. The UUID is obtained by either
     * reading the model file header information or by examining the model 
     * annotation node depending on whether the resource has been opened. 
     * @return
     */
    String getUuid() throws ModelWorkspaceException;
    
    /**
     * Return the {@link ModelAnnotations model annotation} for this
     * resource.  
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * @return the annotation for the model resource itself; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    ModelAnnotation getModelAnnotation() throws ModelWorkspaceException;

    /**
     * Return the {@link ModelAnnotations model annotation} for this
     * resource.  
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * @param force param used to determine whether to create underlying java.io.File for resource
     * if it does not exist or to throw RuntimeException if force is false and File does not exist.
     * @return the annotation for the model resource itself; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
//    ModelAnnotation getModelAnnotation(final boolean force) throws ModelWorkspaceException;

    /**
     * Return the {@link ModelDiagrams model diagrams} for this
     * resource.  All ModelResource instances have a {@link ModelDiagrams}
     * object.  However, since not all resources have diagrams, the
     * resulting {@link ModelDiagrams} may be empty.
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * @return the container of the diagrams in this resource; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    ModelDiagrams getModelDiagrams() throws ModelWorkspaceException;
  
    /**
     * Return the {@link ModelTransformations object transformations} for this
     * resource.  All ModelResource instances have a {@link ModelTransformations}
     * object.  However, since not all resources have transformations, the
     * resulting {@link ModelTransformations} may be empty.
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * @return the container of the transformations in this resource; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    ModelTransformations getModelTransformations() throws ModelWorkspaceException;
  
    /**
     * Return the {@link ModelMappingClassSets mapping class sets} for this
     * resource.  All ModelResource instances have a {@link ModelMappingClassSets}
     * object.  However, since not all resources have mapping classe sets, the
     * resulting {@link ModelMappingClassSets} may be empty.
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * @return the container of the {@link com.metamatrix.metamodels.transformation.MappingClassSet}
     * objects in this resource; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    ModelMappingClassSets getModelMappingClassSets() throws ModelWorkspaceException;
  
    /**
     * Return true if an open buffer exists for this ModelResource
     */
    public boolean isLoaded();
    
    /**
     * Unload the model and lose any changes that have been made so far.
     */
    public void unload();

    /**
     * Return whether this resource has errors upon opening.  Calling this method will result in the
     * model being loaded.  If there are no errors, this method returns <code>false</code>. 
     * @return true if this resource has errors that occurred during opening
     * @since 4.2
     * @see #getErrors()
     */
    public boolean hasErrors();
    
    /**
     * Return any errors that occurred upon opening.  Calling this method will result in the
     * model being loaded.  If there are no errors, this method returns an {@link IStatus#OK OK status}.
     * @return the errors as an {@link IStatus}
     * @since 4.2
     * @see #hasErrors()
     */
    public IStatus getErrors();
    
    /**
     * Return the index type for this model resource. The index type is one among
     * constants defined on the ModleResource. It can be one among the types
     * <LI>ModelResource.NOT_INDEXED<LI>
     * <LI>ModelResource.METADATA_INDEXED<LI>
     * <LI>ModelResource.SEARCH_INDEXED<LI>
     * <LI>ModelResource.INDEXED<LI>
     * The index type can be used to determine if the resource needs to be indexed,
     * and what type of index needs to be created for the modleResource.
     * @return int indicating the type of indexes created for the resource.  
     */
    public int getIndexType();

    /**
     * Set the index type on this model resource. The index type is one among
     * constants defined on the ModleResource. It can be one among the types
     * <LI>ModelResource.NOT_INDEXED<LI>
     * <LI>ModelResource.METADATA_INDEXED<LI>
     * <LI>ModelResource.SEARCH_INDEXED<LI>
     * <LI>ModelResource.INDEXED<LI>
     * The index type can be used to determine if the resource needs to be indexed,
     * and what type of index needs to be created for the modleResource.
     * @param indexType indicating the type of indexes created for the resource.  
     */    
    public void setIndexType(int indexType);
    
    /**
     * Refresh the index type for this model resource. The index type is one among
     * constants defined on the ModleResource. It can be one among the types
     * <LI>ModelResource.NOT_INDEXED<LI>
     * <LI>ModelResource.METADATA_INDEXED<LI>
     * <LI>ModelResource.SEARCH_INDEXED<LI>
     * <LI>ModelResource.INDEXED<LI>
     * The index type can be used to determine if the resource needs to be indexed,
     * and what type of index needs to be created for the modleResource.
     * @return int indicating the type of indexes created for the resource.  
     */
    void refreshIndexType();

    /**
     * Return the {@link ModelObjectAnnotations object annotations} for this
     * resource.  All ModelResource instances have a {@link ModelObjectAnnotations}
     * object.  However, annotations on objects are only created when requested, so it is possible
     * that if there are not yet any annotated objects, the resulting resulting {@link ModelObjectAnnotations}
     * may be empty.
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * @return the container of the annotations in this resource; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    ModelObjectAnnotations getAnnotations() throws ModelWorkspaceException;

//  /**
//   * Return the {@link ModelImports model import container} for this
//   * resource.
//   * <p>
//   * This method requires opening and parsing the resource and materializing
//   * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
//   * being created for this model resource if one does not already exist.
//   * </p>
//   * <p>
//   * The returned object is also returned in the result of the {@link #getChildren()} method.
//   * </p>
//   * @return the container of the model imports in this resource; never null
//   * @throws ModelWorkspaceException if this element does not exist or if an
//   *      exception occurs while accessing its corresponding resource
//   */
//  ModelImports getModelImportContainer() throws ModelWorkspaceException;
//
//    /**
//     * Return the {@link MetamodelImports metamodel import container} for this
//     * resource.
//     * <p>
//     * This method requires opening and parsing the resource and materializing
//     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
//     * being created for this model resource if one does not already exist.
//     * </p>
//     * <p>
//     * The returned object is also returned in the result of the {@link #getChildren()} method.
//     * </p>
//     * @return the container of the metamodel imports in this resource; never null
//     * @throws ModelWorkspaceException if this element does not exist or if an
//     *      exception occurs while accessing its corresponding resource
//     */
//    MetamodelImports getMetamodelImportContainer() throws ModelWorkspaceException;

    /**
     * Return the {@link org.eclipse.emf.ecore.EObject model objects} that are
     * considered the root model objects in this resource, excluding the "hidden" model objects
     * such as the {@link com.metamatrix.metamodels.diagram.DiagramContainer DiagramContainer}.
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * <p>
     * This list is a copy of the underlying list, so changes will not be reflected in the underlying
     * resource.
     * </p>
     * @return the container of the metamodel imports in this resource; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getEObjects() throws ModelWorkspaceException;
    
    /**
     * Return the {@link org.eclipse.emf.ecore.EObject model objects} that are
     * considered the root model objects in this resource.
     * <p>
     * This method requires opening and parsing the resource and materializing
     * the objects within the model, and will result in an {@link #getEmfResource(boolean) EMF resource}
     * being created for this model resource if one does not already exist.
     * </p>
     * <p>
     * This is the mutable {@link EList} returned from the {@link Resource#getContents()}.
     * </p>
     * @return the container of the metamodel imports in this resource; never null
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getAllRootEObjects() throws ModelWorkspaceException;
    
    /**
     * Return the {@link Resource EMF resource} that represents this model resource.
     * An EMF resource is used as the location into which the model contents are
     * materialized.  Calling this method will cause an EMF resource to be created
     * if one does not currently exist for this resource or if one does exist it
     * will cause the EMF resource to be loaded.
     * @return the EMF resource, or <code>null</code> if the model resource has not
     * yet been parsed and materialized.
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    Resource getEmfResource() throws ModelWorkspaceException;

    /**
     * Return the MetamodelDescriptor for the primary metamodel in this model
     * @return the MetamodelDescriptor; may be null if the primary metamodel is not
     * registered
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    MetamodelDescriptor getPrimaryMetamodelDescriptor() throws ModelWorkspaceException;
 
    /**
     * Return the list of MetamodelDescriptor instances for those metamodels used by
     * this model.  
     * @return the MetamodelDescriptor instances, with the primary metamodel
     * descriptor being first.
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getAllMetamodelDescriptors() throws ModelWorkspaceException;
 
    /**
     * Return the list of ModelImport instances for those models used by
     * this model.  Each ModelImport instance will contain, at minimum, 
     * the absolute path to the model and the ModelType information.
     * @return the ModelImport instances.  
     * @throws ModelWorkspaceException if this element does not exist or if an
     *      exception occurs while accessing its corresponding resource
     */
    List getModelImports() throws ModelWorkspaceException;
    
    /**
     * Return true if the ModelResource represents an XSD Resource
     */    
    boolean isXsd();
    
    /**
     * Returns the value of the '<em><b>Target Namespace</b></em>' attribute
     * if the ModelResource represents an XSD resource otherwise null is 
     * returned.
     * @return the value of the '<em>Target Namespace</em>' attribute.
     * @throws ModelWorkspaceException if an exception occurs while accessing 
     *      its corresponding resource
     */
    String getTargetNamespace() throws ModelWorkspaceException;
   
}
