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

package com.metamatrix.metamodels.core;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.core.CoreFactory
 * @model kind="package"
 * @generated
 */
public interface CorePackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "core"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Core"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "mmcore"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    CorePackage eINSTANCE = com.metamatrix.metamodels.core.impl.CorePackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.impl.AnnotationImpl <em>Annotation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.impl.AnnotationImpl
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getAnnotation()
     * @generated
     */
    int ANNOTATION = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION__DESCRIPTION = 0;

    /**
     * The feature id for the '<em><b>Keywords</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION__KEYWORDS = 1;

    /**
     * The feature id for the '<em><b>Tags</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION__TAGS = 2;

    /**
     * The feature id for the '<em><b>Annotation Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION__ANNOTATION_CONTAINER = 3;

    /**
     * The feature id for the '<em><b>Annotated Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION__ANNOTATED_OBJECT = 4;

    /**
     * The feature id for the '<em><b>Extension Object</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION__EXTENSION_OBJECT = 5;

    /**
     * The number of structural features of the the '<em>Annotation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.impl.AnnotationContainerImpl <em>Annotation Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.impl.AnnotationContainerImpl
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getAnnotationContainer()
     * @generated
     */
    int ANNOTATION_CONTAINER = 1;

    /**
     * The feature id for the '<em><b>Annotations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION_CONTAINER__ANNOTATIONS = 0;

    /**
     * The number of structural features of the the '<em>Annotation Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ANNOTATION_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.impl.ModelAnnotationImpl <em>Model Annotation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.impl.ModelAnnotationImpl
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getModelAnnotation()
     * @generated
     */
    int MODEL_ANNOTATION = 2;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__DESCRIPTION = 0;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__NAME_IN_SOURCE = 1;

    /**
     * The feature id for the '<em><b>Primary Metamodel Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__PRIMARY_METAMODEL_URI = 2;

    /**
     * The feature id for the '<em><b>Model Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__MODEL_TYPE = 3;

    /**
     * The feature id for the '<em><b>Max Set Size</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__MAX_SET_SIZE = 4;

    /**
     * The feature id for the '<em><b>Visible</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__VISIBLE = 5;

    /**
     * The feature id for the '<em><b>Supports Distinct</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__SUPPORTS_DISTINCT = 6;

    /**
     * The feature id for the '<em><b>Supports Join</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__SUPPORTS_JOIN = 7;

    /**
     * The feature id for the '<em><b>Supports Order By</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__SUPPORTS_ORDER_BY = 8;

    /**
     * The feature id for the '<em><b>Supports Outer Join</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__SUPPORTS_OUTER_JOIN = 9;

    /**
     * The feature id for the '<em><b>Supports Where All</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__SUPPORTS_WHERE_ALL = 10;

    /**
     * The feature id for the '<em><b>Tags</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__TAGS = 11;

    /**
     * The feature id for the '<em><b>Namespace Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__NAMESPACE_URI = 12;

    /**
     * The feature id for the '<em><b>Producer Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__PRODUCER_NAME = 13;

    /**
     * The feature id for the '<em><b>Producer Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__PRODUCER_VERSION = 14;

    /**
     * The feature id for the '<em><b>Model Imports</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__MODEL_IMPORTS = 15;

    /**
     * The feature id for the '<em><b>Extension Package</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION__EXTENSION_PACKAGE = 16;

    /**
     * The number of structural features of the the '<em>Model Annotation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ANNOTATION_FEATURE_COUNT = 17;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.impl.LinkImpl <em>Link</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.impl.LinkImpl
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getLink()
     * @generated
     */
    int LINK = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK__NAME = 0;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK__DESCRIPTION = 1;

    /**
     * The feature id for the '<em><b>References</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK__REFERENCES = 2;

    /**
     * The feature id for the '<em><b>Linked Objects</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK__LINKED_OBJECTS = 3;

    /**
     * The feature id for the '<em><b>Link Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK__LINK_CONTAINER = 4;

    /**
     * The number of structural features of the the '<em>Link</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.impl.LinkContainerImpl <em>Link Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.impl.LinkContainerImpl
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getLinkContainer()
     * @generated
     */
    int LINK_CONTAINER = 4;

    /**
     * The feature id for the '<em><b>Links</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK_CONTAINER__LINKS = 0;

    /**
     * The number of structural features of the the '<em>Link Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LINK_CONTAINER_FEATURE_COUNT = 1;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.Identifiable <em>Identifiable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.Identifiable
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getIdentifiable()
     * @generated
     */
    int IDENTIFIABLE = 6;

    /**
     * The feature id for the '<em><b>Uuid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTIFIABLE__UUID = 0;

    /**
     * The number of structural features of the the '<em>Identifiable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int IDENTIFIABLE_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.Datatype <em>Datatype</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.Datatype
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getDatatype()
     * @generated
     */
    int DATATYPE = 5;

    /**
     * The feature id for the '<em><b>Uuid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATATYPE__UUID = IDENTIFIABLE__UUID;

    /**
     * The number of structural features of the the '<em>Datatype</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATATYPE_FEATURE_COUNT = IDENTIFIABLE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.impl.ModelImportImpl <em>Model Import</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.impl.ModelImportImpl
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getModelImport()
     * @generated
     */
    int MODEL_IMPORT = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__NAME = 0;

    /**
     * The feature id for the '<em><b>Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__PATH = 1;

    /**
     * The feature id for the '<em><b>Model Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__MODEL_LOCATION = 2;

    /**
     * The feature id for the '<em><b>Uuid</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__UUID = 3;

    /**
     * The feature id for the '<em><b>Model Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__MODEL_TYPE = 4;

    /**
     * The feature id for the '<em><b>Primary Metamodel Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__PRIMARY_METAMODEL_URI = 5;

    /**
     * The feature id for the '<em><b>Model</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT__MODEL = 6;

    /**
     * The number of structural features of the the '<em>Model Import</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_IMPORT_FEATURE_COUNT = 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.core.ModelType <em>Model Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.core.ModelType
     * @see com.metamatrix.metamodels.core.impl.CorePackageImpl#getModelType()
     * @generated
     */
    int MODEL_TYPE = 8;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.Annotation <em>Annotation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Annotation</em>'.
     * @see com.metamatrix.metamodels.core.Annotation
     * @generated
     */
    EClass getAnnotation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.Annotation#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see com.metamatrix.metamodels.core.Annotation#getDescription()
     * @see #getAnnotation()
     * @generated
     */
    EAttribute getAnnotation_Description();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.core.Annotation#getKeywords <em>Keywords</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Keywords</em>'.
     * @see com.metamatrix.metamodels.core.Annotation#getKeywords()
     * @see #getAnnotation()
     * @generated
     */
    EAttribute getAnnotation_Keywords();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.core.Annotation#getAnnotatedObject <em>Annotated Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Annotated Object</em>'.
     * @see com.metamatrix.metamodels.core.Annotation#getAnnotatedObject()
     * @see #getAnnotation()
     * @generated
     */
    EReference getAnnotation_AnnotatedObject();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.core.Annotation#getExtensionObject <em>Extension Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Extension Object</em>'.
     * @see com.metamatrix.metamodels.core.Annotation#getExtensionObject()
     * @see #getAnnotation()
     * @generated
     */
    EReference getAnnotation_ExtensionObject();

    /**
     * Returns the meta object for the map '{@link com.metamatrix.metamodels.core.Annotation#getTags <em>Tags</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Tags</em>'.
     * @see com.metamatrix.metamodels.core.Annotation#getTags()
     * @see #getAnnotation()
     * @generated
     */
    EReference getAnnotation_Tags();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.core.Annotation#getAnnotationContainer <em>Annotation Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Annotation Container</em>'.
     * @see com.metamatrix.metamodels.core.Annotation#getAnnotationContainer()
     * @see #getAnnotation()
     * @generated
     */
    EReference getAnnotation_AnnotationContainer();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.AnnotationContainer <em>Annotation Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Annotation Container</em>'.
     * @see com.metamatrix.metamodels.core.AnnotationContainer
     * @generated
     */
    EClass getAnnotationContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.core.AnnotationContainer#getAnnotations <em>Annotations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Annotations</em>'.
     * @see com.metamatrix.metamodels.core.AnnotationContainer#getAnnotations()
     * @see #getAnnotationContainer()
     * @generated
     */
    EReference getAnnotationContainer_Annotations();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.ModelAnnotation <em>Model Annotation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Annotation</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation
     * @generated
     */
    EClass getModelAnnotation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getDescription()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_Description();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getNameInSource <em>Name In Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name In Source</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getNameInSource()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_NameInSource();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Primary Metamodel Uri</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getPrimaryMetamodelUri()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_PrimaryMetamodelUri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getModelType <em>Model Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Model Type</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getModelType()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_ModelType();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getMaxSetSize <em>Max Set Size</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Set Size</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getMaxSetSize()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_MaxSetSize();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#isVisible <em>Visible</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Visible</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#isVisible()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_Visible();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsDistinct <em>Supports Distinct</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Supports Distinct</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#isSupportsDistinct()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_SupportsDistinct();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsJoin <em>Supports Join</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Supports Join</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#isSupportsJoin()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_SupportsJoin();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOrderBy <em>Supports Order By</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Supports Order By</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOrderBy()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_SupportsOrderBy();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOuterJoin <em>Supports Outer Join</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Supports Outer Join</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#isSupportsOuterJoin()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_SupportsOuterJoin();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#isSupportsWhereAll <em>Supports Where All</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Supports Where All</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#isSupportsWhereAll()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_SupportsWhereAll();

    /**
     * Returns the meta object for the map '{@link com.metamatrix.metamodels.core.ModelAnnotation#getTags <em>Tags</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Tags</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getTags()
     * @see #getModelAnnotation()
     * @generated
     */
    EReference getModelAnnotation_Tags();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getNamespaceUri <em>Namespace Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace Uri</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getNamespaceUri()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_NamespaceUri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getProducerName <em>Producer Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Producer Name</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getProducerName()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_ProducerName();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelAnnotation#getProducerVersion <em>Producer Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Producer Version</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getProducerVersion()
     * @see #getModelAnnotation()
     * @generated
     */
    EAttribute getModelAnnotation_ProducerVersion();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.core.ModelAnnotation#getModelImports <em>Model Imports</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Model Imports</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getModelImports()
     * @see #getModelAnnotation()
     * @generated
     */
    EReference getModelAnnotation_ModelImports();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.core.ModelAnnotation#getExtensionPackage <em>Extension Package</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Extension Package</em>'.
     * @see com.metamatrix.metamodels.core.ModelAnnotation#getExtensionPackage()
     * @see #getModelAnnotation()
     * @generated
     */
    EReference getModelAnnotation_ExtensionPackage();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.Link <em>Link</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Link</em>'.
     * @see com.metamatrix.metamodels.core.Link
     * @generated
     */
    EClass getLink();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.Link#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.core.Link#getName()
     * @see #getLink()
     * @generated
     */
    EAttribute getLink_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.Link#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see com.metamatrix.metamodels.core.Link#getDescription()
     * @see #getLink()
     * @generated
     */
    EAttribute getLink_Description();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.core.Link#getReferences <em>References</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>References</em>'.
     * @see com.metamatrix.metamodels.core.Link#getReferences()
     * @see #getLink()
     * @generated
     */
    EAttribute getLink_References();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.core.Link#getLinkedObjects <em>Linked Objects</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Linked Objects</em>'.
     * @see com.metamatrix.metamodels.core.Link#getLinkedObjects()
     * @see #getLink()
     * @generated
     */
    EReference getLink_LinkedObjects();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.core.Link#getLinkContainer <em>Link Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Link Container</em>'.
     * @see com.metamatrix.metamodels.core.Link#getLinkContainer()
     * @see #getLink()
     * @generated
     */
    EReference getLink_LinkContainer();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.LinkContainer <em>Link Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Link Container</em>'.
     * @see com.metamatrix.metamodels.core.LinkContainer
     * @generated
     */
    EClass getLinkContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.core.LinkContainer#getLinks <em>Links</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Links</em>'.
     * @see com.metamatrix.metamodels.core.LinkContainer#getLinks()
     * @see #getLinkContainer()
     * @generated
     */
    EReference getLinkContainer_Links();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.Datatype <em>Datatype</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Datatype</em>'.
     * @see com.metamatrix.metamodels.core.Datatype
     * @generated
     */
    EClass getDatatype();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.Identifiable <em>Identifiable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Identifiable</em>'.
     * @see com.metamatrix.metamodels.core.Identifiable
     * @generated
     */
    EClass getIdentifiable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.Identifiable#getUuid <em>Uuid</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uuid</em>'.
     * @see com.metamatrix.metamodels.core.Identifiable#getUuid()
     * @see #getIdentifiable()
     * @generated
     */
    EAttribute getIdentifiable_Uuid();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.core.ModelImport <em>Model Import</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Import</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport
     * @generated
     */
    EClass getModelImport();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelImport#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getName()
     * @see #getModelImport()
     * @generated
     */
    EAttribute getModelImport_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelImport#getPath <em>Path</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Path</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getPath()
     * @see #getModelImport()
     * @generated
     */
    EAttribute getModelImport_Path();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelImport#getModelLocation <em>Model Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Model Location</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getModelLocation()
     * @see #getModelImport()
     * @generated
     */
    EAttribute getModelImport_ModelLocation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelImport#getUuid <em>Uuid</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uuid</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getUuid()
     * @see #getModelImport()
     * @generated
     */
    EAttribute getModelImport_Uuid();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelImport#getModelType <em>Model Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Model Type</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getModelType()
     * @see #getModelImport()
     * @generated
     */
    EAttribute getModelImport_ModelType();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.core.ModelImport#getPrimaryMetamodelUri <em>Primary Metamodel Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Primary Metamodel Uri</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getPrimaryMetamodelUri()
     * @see #getModelImport()
     * @generated
     */
    EAttribute getModelImport_PrimaryMetamodelUri();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.core.ModelImport#getModel <em>Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Model</em>'.
     * @see com.metamatrix.metamodels.core.ModelImport#getModel()
     * @see #getModelImport()
     * @generated
     */
    EReference getModelImport_Model();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.core.ModelType <em>Model Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Model Type</em>'.
     * @see com.metamatrix.metamodels.core.ModelType
     * @generated
     */
    EEnum getModelType();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    CoreFactory getCoreFactory();

} //CorePackage
