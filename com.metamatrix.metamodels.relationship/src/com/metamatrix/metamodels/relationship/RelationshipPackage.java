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

package com.metamatrix.metamodels.relationship;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see com.metamatrix.metamodels.relationship.RelationshipFactory
 * @generated
 */
public interface RelationshipPackage extends EPackage{
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
    String eNAME = "relationship"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Relationship"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "genrelat"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    RelationshipPackage eINSTANCE = com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.RelationshipEntityImpl <em>Entity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipEntityImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationshipEntity()
     * @generated
     */
    int RELATIONSHIP_ENTITY = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ENTITY__NAME = 0;

    /**
     * The number of structural features of the the '<em>Entity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ENTITY_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl <em>Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipTypeImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationshipType()
     * @generated
     */
    int RELATIONSHIP_TYPE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__NAME = RELATIONSHIP_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Directed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__DIRECTED = RELATIONSHIP_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Exclusive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__EXCLUSIVE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Cross Model</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__CROSS_MODEL = RELATIONSHIP_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__ABSTRACT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>User Defined</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__USER_DEFINED = RELATIONSHIP_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Status</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__STATUS = RELATIONSHIP_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__STEREOTYPE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__CONSTRAINT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__LABEL = RELATIONSHIP_ENTITY_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Opposite Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__OPPOSITE_LABEL = RELATIONSHIP_ENTITY_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Relationship Features</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__RELATIONSHIP_FEATURES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Super Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__SUPER_TYPE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Sub Type</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__SUB_TYPE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Roles</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__ROLES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE__OWNER = RELATIONSHIP_ENTITY_FEATURE_COUNT + 14;

    /**
     * The number of structural features of the the '<em>Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_TYPE_FEATURE_COUNT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 15;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.RelationshipImpl <em>Relationship</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationship()
     * @generated
     */
    int RELATIONSHIP = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__NAME = RELATIONSHIP_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Owned Relationships</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__OWNED_RELATIONSHIPS = RELATIONSHIP_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Feature Values</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__FEATURE_VALUES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Targets</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__TARGETS = RELATIONSHIP_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Sources</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__SOURCES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__TYPE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Relationship Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__RELATIONSHIP_CONTAINER = RELATIONSHIP_ENTITY_FEATURE_COUNT + 5;

    /**
     * The number of structural features of the the '<em>Relationship</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FEATURE_COUNT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 6;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.PlaceholderReferenceImpl <em>Placeholder Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.PlaceholderReferenceImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getPlaceholderReference()
     * @generated
     */
    int PLACEHOLDER_REFERENCE = 5;

    /**
     * The feature id for the '<em><b>Placeholder Reference Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER = 0;

    /**
     * The number of structural features of the the '<em>Placeholder Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PLACEHOLDER_REFERENCE_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.UriReferenceImpl <em>Uri Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.UriReferenceImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getUriReference()
     * @generated
     */
    int URI_REFERENCE = 7;

    /**
     * The feature id for the '<em><b>Placeholder Reference Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER = PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__NAME = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__URI = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Resolvable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__RESOLVABLE = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Encoding</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__ENCODING = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__ABSTRACT = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Keywords</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__KEYWORDS = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Related Uris</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__RELATED_URIS = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE__PROPERTIES = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the the '<em>Uri Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int URI_REFERENCE_FEATURE_COUNT = PLACEHOLDER_REFERENCE_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.FileReferenceImpl <em>File Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.FileReferenceImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getFileReference()
     * @generated
     */
    int FILE_REFERENCE = 3;

    /**
     * The feature id for the '<em><b>Placeholder Reference Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER = URI_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__NAME = URI_REFERENCE__NAME;

    /**
     * The feature id for the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__URI = URI_REFERENCE__URI;

    /**
     * The feature id for the '<em><b>Resolvable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__RESOLVABLE = URI_REFERENCE__RESOLVABLE;

    /**
     * The feature id for the '<em><b>Encoding</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__ENCODING = URI_REFERENCE__ENCODING;

    /**
     * The feature id for the '<em><b>Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__ABSTRACT = URI_REFERENCE__ABSTRACT;

    /**
     * The feature id for the '<em><b>Keywords</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__KEYWORDS = URI_REFERENCE__KEYWORDS;

    /**
     * The feature id for the '<em><b>Related Uris</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__RELATED_URIS = URI_REFERENCE__RELATED_URIS;

    /**
     * The feature id for the '<em><b>Properties</b></em>' map.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__PROPERTIES = URI_REFERENCE__PROPERTIES;

    /**
     * The feature id for the '<em><b>Tool Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__TOOL_NAME = URI_REFERENCE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Tool Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__TOOL_VERSION = URI_REFERENCE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Format Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__FORMAT_NAME = URI_REFERENCE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Format Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE__FORMAT_VERSION = URI_REFERENCE_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>File Reference</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILE_REFERENCE_FEATURE_COUNT = URI_REFERENCE_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl <em>Role</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipRoleImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationshipRole()
     * @generated
     */
    int RELATIONSHIP_ROLE = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__NAME = RELATIONSHIP_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__STEREOTYPE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Ordered</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__ORDERED = RELATIONSHIP_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Unique</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__UNIQUE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Navigable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__NAVIGABLE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Lower Bound</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__LOWER_BOUND = RELATIONSHIP_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Upper Bound</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__UPPER_BOUND = RELATIONSHIP_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__CONSTRAINT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Relationship Type</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__RELATIONSHIP_TYPE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Opposite Role</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__OPPOSITE_ROLE = RELATIONSHIP_ENTITY_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Include Types</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__INCLUDE_TYPES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Exclude Types</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE__EXCLUDE_TYPES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 10;

    /**
     * The number of structural features of the the '<em>Role</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_ROLE_FEATURE_COUNT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 11;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.PlaceholderReferenceContainerImpl <em>Placeholder Reference Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.PlaceholderReferenceContainerImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getPlaceholderReferenceContainer()
     * @generated
     */
    int PLACEHOLDER_REFERENCE_CONTAINER = 6;

    /**
     * The feature id for the '<em><b>Placeholders</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS = 0;

    /**
     * The number of structural features of the the '<em>Placeholder Reference Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PLACEHOLDER_REFERENCE_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl <em>Folder</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipFolderImpl
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationshipFolder()
     * @generated
     */
    int RELATIONSHIP_FOLDER = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER__NAME = RELATIONSHIP_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Placeholders</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER__PLACEHOLDERS = RELATIONSHIP_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Owned Relationships</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER__OWNED_RELATIONSHIPS = RELATIONSHIP_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Owned Relationship Types</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_TYPES = RELATIONSHIP_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Owned Relationship Folders</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER__OWNED_RELATIONSHIP_FOLDERS = RELATIONSHIP_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER__OWNER = RELATIONSHIP_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Folder</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FOLDER_FEATURE_COUNT = RELATIONSHIP_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.RelationshipContainer <em>Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.RelationshipContainer
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationshipContainer()
     * @generated
     */
    int RELATIONSHIP_CONTAINER = 9;

    /**
     * The feature id for the '<em><b>Owned Relationships</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_CONTAINER__OWNED_RELATIONSHIPS = 0;

    /**
     * The number of structural features of the the '<em>Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relationship.RelationshipTypeStatus <em>Type Status</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relationship.RelationshipTypeStatus
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getRelationshipTypeStatus()
     * @generated
     */
    int RELATIONSHIP_TYPE_STATUS = 10;

    /**
     * The meta object id for the '<em>IStatus</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IStatus
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getIStatus()
     * @generated
     */
    int ISTATUS = 11;

    /**
     * The meta object id for the '<em>List</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.List
     * @see com.metamatrix.metamodels.relationship.impl.RelationshipPackageImpl#getList()
     * @generated
     */
    int LIST = 12;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.RelationshipType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Type</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType
     * @generated
     */
    EClass getRelationshipType();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#isDirected <em>Directed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Directed</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#isDirected()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Directed();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#isExclusive <em>Exclusive</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Exclusive</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#isExclusive()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Exclusive();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#isCrossModel <em>Cross Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Cross Model</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#isCrossModel()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_CrossModel();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#isAbstract <em>Abstract</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Abstract</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#isAbstract()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Abstract();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#isUserDefined <em>User Defined</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>User Defined</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#isUserDefined()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_UserDefined();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#getStatus <em>Status</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Status</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getStatus()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Status();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#getStereotype <em>Stereotype</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Stereotype</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getStereotype()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Stereotype();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#getConstraint <em>Constraint</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Constraint</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getConstraint()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Constraint();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#getLabel <em>Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Label</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getLabel()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_Label();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipType#getOppositeLabel <em>Opposite Label</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Opposite Label</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getOppositeLabel()
     * @see #getRelationshipType()
     * @generated
     */
    EAttribute getRelationshipType_OppositeLabel();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relationship.RelationshipType#getRelationshipFeatures <em>Relationship Features</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Relationship Features</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getRelationshipFeatures()
     * @see #getRelationshipType()
     * @generated
     */
    EReference getRelationshipType_RelationshipFeatures();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relationship.RelationshipType#getSuperType <em>Super Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Super Type</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getSuperType()
     * @see #getRelationshipType()
     * @generated
     */
    EReference getRelationshipType_SuperType();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relationship.RelationshipType#getSubType <em>Sub Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Sub Type</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getSubType()
     * @see #getRelationshipType()
     * @generated
     */
    EReference getRelationshipType_SubType();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relationship.RelationshipType#getRoles <em>Roles</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Roles</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getRoles()
     * @see #getRelationshipType()
     * @generated
     */
    EReference getRelationshipType_Roles();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relationship.RelationshipType#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipType#getOwner()
     * @see #getRelationshipType()
     * @generated
     */
    EReference getRelationshipType_Owner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.RelationshipEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Entity</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipEntity
     * @generated
     */
    EClass getRelationshipEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipEntity#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipEntity#getName()
     * @see #getRelationshipEntity()
     * @generated
     */
    EAttribute getRelationshipEntity_Name();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.Relationship <em>Relationship</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Relationship</em>'.
     * @see com.metamatrix.metamodels.relationship.Relationship
     * @generated
     */
    EClass getRelationship();

    /**
     * Returns the meta object for the map '{@link com.metamatrix.metamodels.relationship.Relationship#getFeatureValues <em>Feature Values</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Feature Values</em>'.
     * @see com.metamatrix.metamodels.relationship.Relationship#getFeatureValues()
     * @see #getRelationship()
     * @generated
     */
    EReference getRelationship_FeatureValues();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relationship.Relationship#getTargets <em>Targets</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Targets</em>'.
     * @see com.metamatrix.metamodels.relationship.Relationship#getTargets()
     * @see #getRelationship()
     * @generated
     */
    EReference getRelationship_Targets();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relationship.Relationship#getSources <em>Sources</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Sources</em>'.
     * @see com.metamatrix.metamodels.relationship.Relationship#getSources()
     * @see #getRelationship()
     * @generated
     */
    EReference getRelationship_Sources();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relationship.Relationship#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see com.metamatrix.metamodels.relationship.Relationship#getType()
     * @see #getRelationship()
     * @generated
     */
    EReference getRelationship_Type();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relationship.Relationship#getRelationshipContainer <em>Relationship Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Relationship Container</em>'.
     * @see com.metamatrix.metamodels.relationship.Relationship#getRelationshipContainer()
     * @see #getRelationship()
     * @generated
     */
    EReference getRelationship_RelationshipContainer();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.FileReference <em>File Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>File Reference</em>'.
     * @see com.metamatrix.metamodels.relationship.FileReference
     * @generated
     */
    EClass getFileReference();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.FileReference#getToolName <em>Tool Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Tool Name</em>'.
     * @see com.metamatrix.metamodels.relationship.FileReference#getToolName()
     * @see #getFileReference()
     * @generated
     */
    EAttribute getFileReference_ToolName();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.FileReference#getToolVersion <em>Tool Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Tool Version</em>'.
     * @see com.metamatrix.metamodels.relationship.FileReference#getToolVersion()
     * @see #getFileReference()
     * @generated
     */
    EAttribute getFileReference_ToolVersion();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.FileReference#getFormatName <em>Format Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Format Name</em>'.
     * @see com.metamatrix.metamodels.relationship.FileReference#getFormatName()
     * @see #getFileReference()
     * @generated
     */
    EAttribute getFileReference_FormatName();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.FileReference#getFormatVersion <em>Format Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Format Version</em>'.
     * @see com.metamatrix.metamodels.relationship.FileReference#getFormatVersion()
     * @see #getFileReference()
     * @generated
     */
    EAttribute getFileReference_FormatVersion();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.RelationshipRole <em>Role</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Role</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole
     * @generated
     */
    EClass getRelationshipRole();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getStereotype <em>Stereotype</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Stereotype</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getStereotype()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_Stereotype();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#isOrdered <em>Ordered</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Ordered</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#isOrdered()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_Ordered();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#isUnique <em>Unique</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Unique</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#isUnique()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_Unique();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#isNavigable <em>Navigable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Navigable</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#isNavigable()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_Navigable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getLowerBound <em>Lower Bound</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Lower Bound</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getLowerBound()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_LowerBound();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getUpperBound <em>Upper Bound</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Upper Bound</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getUpperBound()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_UpperBound();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getConstraint <em>Constraint</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Constraint</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getConstraint()
     * @see #getRelationshipRole()
     * @generated
     */
    EAttribute getRelationshipRole_Constraint();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getRelationshipType <em>Relationship Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Relationship Type</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getRelationshipType()
     * @see #getRelationshipRole()
     * @generated
     */
    EReference getRelationshipRole_RelationshipType();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getOppositeRole <em>Opposite Role</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Opposite Role</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getOppositeRole()
     * @see #getRelationshipRole()
     * @generated
     */
    EReference getRelationshipRole_OppositeRole();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getIncludeTypes <em>Include Types</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Include Types</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getIncludeTypes()
     * @see #getRelationshipRole()
     * @generated
     */
    EReference getRelationshipRole_IncludeTypes();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relationship.RelationshipRole#getExcludeTypes <em>Exclude Types</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Exclude Types</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipRole#getExcludeTypes()
     * @see #getRelationshipRole()
     * @generated
     */
    EReference getRelationshipRole_ExcludeTypes();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.PlaceholderReference <em>Placeholder Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Placeholder Reference</em>'.
     * @see com.metamatrix.metamodels.relationship.PlaceholderReference
     * @generated
     */
    EClass getPlaceholderReference();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer <em>Placeholder Reference Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Placeholder Reference Container</em>'.
     * @see com.metamatrix.metamodels.relationship.PlaceholderReference#getPlaceholderReferenceContainer()
     * @see #getPlaceholderReference()
     * @generated
     */
    EReference getPlaceholderReference_PlaceholderReferenceContainer();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer <em>Placeholder Reference Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Placeholder Reference Container</em>'.
     * @see com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer
     * @generated
     */
    EClass getPlaceholderReferenceContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer#getPlaceholders <em>Placeholders</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Placeholders</em>'.
     * @see com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer#getPlaceholders()
     * @see #getPlaceholderReferenceContainer()
     * @generated
     */
    EReference getPlaceholderReferenceContainer_Placeholders();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.UriReference <em>Uri Reference</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Uri Reference</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference
     * @generated
     */
    EClass getUriReference();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.UriReference#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getName()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.UriReference#getUri <em>Uri</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Uri</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getUri()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_Uri();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.UriReference#isResolvable <em>Resolvable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Resolvable</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#isResolvable()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_Resolvable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.UriReference#getEncoding <em>Encoding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Encoding</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getEncoding()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_Encoding();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.UriReference#getAbstract <em>Abstract</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Abstract</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getAbstract()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_Abstract();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relationship.UriReference#getKeywords <em>Keywords</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Keywords</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getKeywords()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_Keywords();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.relationship.UriReference#getRelatedUris <em>Related Uris</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Related Uris</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getRelatedUris()
     * @see #getUriReference()
     * @generated
     */
    EAttribute getUriReference_RelatedUris();

    /**
     * Returns the meta object for the map '{@link com.metamatrix.metamodels.relationship.UriReference#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the map '<em>Properties</em>'.
     * @see com.metamatrix.metamodels.relationship.UriReference#getProperties()
     * @see #getUriReference()
     * @generated
     */
    EReference getUriReference_Properties();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.RelationshipFolder <em>Folder</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Folder</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder
     * @generated
     */
    EClass getRelationshipFolder();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipTypes <em>Owned Relationship Types</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Owned Relationship Types</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipTypes()
     * @see #getRelationshipFolder()
     * @generated
     */
    EReference getRelationshipFolder_OwnedRelationshipTypes();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipFolders <em>Owned Relationship Folders</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Owned Relationship Folders</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder#getOwnedRelationshipFolders()
     * @see #getRelationshipFolder()
     * @generated
     */
    EReference getRelationshipFolder_OwnedRelationshipFolders();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relationship.RelationshipFolder#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipFolder#getOwner()
     * @see #getRelationshipFolder()
     * @generated
     */
    EReference getRelationshipFolder_Owner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relationship.RelationshipContainer <em>Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Container</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipContainer
     * @generated
     */
    EClass getRelationshipContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relationship.RelationshipContainer#getOwnedRelationships <em>Owned Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Owned Relationships</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipContainer#getOwnedRelationships()
     * @see #getRelationshipContainer()
     * @generated
     */
    EReference getRelationshipContainer_OwnedRelationships();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.relationship.RelationshipTypeStatus <em>Type Status</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Type Status</em>'.
     * @see com.metamatrix.metamodels.relationship.RelationshipTypeStatus
     * @generated
     */
    EEnum getRelationshipTypeStatus();

    /**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IStatus <em>IStatus</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IStatus</em>'.
     * @see org.eclipse.core.runtime.IStatus
     * @model instanceClass="org.eclipse.core.runtime.IStatus"
     * @generated
     */
    EDataType getIStatus();

    /**
     * Returns the meta object for data type '{@link java.util.List <em>List</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>List</em>'.
     * @see java.util.List
     * @model instanceClass="java.util.List"
     * @generated
     */
    EDataType getList();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    RelationshipFactory getRelationshipFactory();

} //RelationshipPackage
