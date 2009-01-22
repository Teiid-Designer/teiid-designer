/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.mapping.MappingPackage;

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
 * @see com.metamatrix.metamodels.transformation.TransformationFactory
 * @model kind="package"
 * @generated
 */
public interface TransformationPackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "transformation"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Transformation"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "transformation"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    TransformationPackage eINSTANCE = com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.TransformationContainerImpl <em>Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.TransformationContainerImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getTransformationContainer()
     * @generated
     */
    int TRANSFORMATION_CONTAINER = 0;

    /**
     * The feature id for the '<em><b>Transformation Mappings</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS = 0;

    /**
     * The number of structural features of the the '<em>Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl <em>Sql Transformation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.SqlTransformationImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSqlTransformation()
     * @generated
     */
    int SQL_TRANSFORMATION = 1;

    /**
     * The feature id for the '<em><b>Mapper</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__MAPPER = MappingPackage.MAPPING_HELPER__MAPPER;

    /**
     * The feature id for the '<em><b>Helped Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__HELPED_OBJECT = MappingPackage.MAPPING_HELPER__HELPED_OBJECT;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__NESTED_IN = MappingPackage.MAPPING_HELPER__NESTED_IN;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__NESTED = MappingPackage.MAPPING_HELPER__NESTED;

    /**
     * The feature id for the '<em><b>Select Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__SELECT_SQL = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Insert Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__INSERT_SQL = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Update Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__UPDATE_SQL = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Delete Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__DELETE_SQL = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Insert Allowed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__INSERT_ALLOWED = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Update Allowed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__UPDATE_ALLOWED = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Delete Allowed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__DELETE_ALLOWED = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Output Locked</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__OUTPUT_LOCKED = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Insert Sql Default</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__INSERT_SQL_DEFAULT = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Update Sql Default</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Delete Sql Default</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__DELETE_SQL_DEFAULT = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Aliases</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION__ALIASES = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 11;

    /**
     * The number of structural features of the the '<em>Sql Transformation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_FEATURE_COUNT = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 12;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.TransformationMappingRootImpl <em>Mapping Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.TransformationMappingRootImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getTransformationMappingRoot()
     * @generated
     */
    int TRANSFORMATION_MAPPING_ROOT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.FragmentMappingRootImpl <em>Fragment Mapping Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.FragmentMappingRootImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getFragmentMappingRoot()
     * @generated
     */
    int FRAGMENT_MAPPING_ROOT = 6;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.TreeMappingRootImpl <em>Tree Mapping Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.TreeMappingRootImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getTreeMappingRoot()
     * @generated
     */
    int TREE_MAPPING_ROOT = 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.MappingClassObjectImpl <em>Mapping Class Object</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.MappingClassObjectImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getMappingClassObject()
     * @generated
     */
    int MAPPING_CLASS_OBJECT = 10;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.MappingClassImpl <em>Mapping Class</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.MappingClassImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getMappingClass()
     * @generated
     */
    int MAPPING_CLASS = 8;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.MappingClassColumnImpl <em>Mapping Class Column</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.MappingClassColumnImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getMappingClassColumn()
     * @generated
     */
    int MAPPING_CLASS_COLUMN = 9;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.TransformationMappingImpl <em>Mapping</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.TransformationMappingImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getTransformationMapping()
     * @generated
     */
    int TRANSFORMATION_MAPPING = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.SqlAliasImpl <em>Sql Alias</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.SqlAliasImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSqlAlias()
     * @generated
     */
    int SQL_ALIAS = 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.SqlTransformationMappingRootImpl <em>Sql Transformation Mapping Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.SqlTransformationMappingRootImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSqlTransformationMappingRoot()
     * @generated
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT = 5;

    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__HELPER = MappingPackage.MAPPING_ROOT__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__NESTED = MappingPackage.MAPPING_ROOT__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__NESTED_IN = MappingPackage.MAPPING_ROOT__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__INPUTS = MappingPackage.MAPPING_ROOT__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__OUTPUTS = MappingPackage.MAPPING_ROOT__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING = MappingPackage.MAPPING_ROOT__TYPE_MAPPING;

    /**
     * The feature id for the '<em><b>Output Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY = MappingPackage.MAPPING_ROOT__OUTPUT_READ_ONLY;

    /**
     * The feature id for the '<em><b>Top To Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM = MappingPackage.MAPPING_ROOT__TOP_TO_BOTTOM;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK = MappingPackage.MAPPING_ROOT__COMMAND_STACK;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT__TARGET = MappingPackage.MAPPING_ROOT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Mapping Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT = MappingPackage.MAPPING_ROOT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING__HELPER = MappingPackage.MAPPING__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING__NESTED = MappingPackage.MAPPING__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING__NESTED_IN = MappingPackage.MAPPING__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING__INPUTS = MappingPackage.MAPPING__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING__OUTPUTS = MappingPackage.MAPPING__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING__TYPE_MAPPING = MappingPackage.MAPPING__TYPE_MAPPING;

    /**
     * The number of structural features of the the '<em>Mapping</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TRANSFORMATION_MAPPING_FEATURE_COUNT = MappingPackage.MAPPING_FEATURE_COUNT + 0;


    /**
     * The feature id for the '<em><b>Alias</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_ALIAS__ALIAS = 0;

    /**
     * The feature id for the '<em><b>Aliased Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_ALIAS__ALIASED_OBJECT = 1;

    /**
     * The feature id for the '<em><b>Sql Transformation</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_ALIAS__SQL_TRANSFORMATION = 2;

    /**
     * The number of structural features of the the '<em>Sql Alias</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_ALIAS_FEATURE_COUNT = 3;


    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__HELPER = TRANSFORMATION_MAPPING_ROOT__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__NESTED = TRANSFORMATION_MAPPING_ROOT__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__NESTED_IN = TRANSFORMATION_MAPPING_ROOT__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__INPUTS = TRANSFORMATION_MAPPING_ROOT__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__OUTPUTS = TRANSFORMATION_MAPPING_ROOT__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING = TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING;

    /**
     * The feature id for the '<em><b>Output Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY = TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY;

    /**
     * The feature id for the '<em><b>Top To Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM = TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK = TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT__TARGET = TRANSFORMATION_MAPPING_ROOT__TARGET;

    /**
     * The number of structural features of the the '<em>Sql Transformation Mapping Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__HELPER = TRANSFORMATION_MAPPING_ROOT__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__NESTED = TRANSFORMATION_MAPPING_ROOT__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__NESTED_IN = TRANSFORMATION_MAPPING_ROOT__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__INPUTS = TRANSFORMATION_MAPPING_ROOT__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__OUTPUTS = TRANSFORMATION_MAPPING_ROOT__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__TYPE_MAPPING = TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING;

    /**
     * The feature id for the '<em><b>Output Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__OUTPUT_READ_ONLY = TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY;

    /**
     * The feature id for the '<em><b>Top To Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__TOP_TO_BOTTOM = TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__COMMAND_STACK = TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT__TARGET = TRANSFORMATION_MAPPING_ROOT__TARGET;

    /**
     * The number of structural features of the the '<em>Fragment Mapping Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FRAGMENT_MAPPING_ROOT_FEATURE_COUNT = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__HELPER = TRANSFORMATION_MAPPING_ROOT__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__NESTED = TRANSFORMATION_MAPPING_ROOT__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__NESTED_IN = TRANSFORMATION_MAPPING_ROOT__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__INPUTS = TRANSFORMATION_MAPPING_ROOT__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__OUTPUTS = TRANSFORMATION_MAPPING_ROOT__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__TYPE_MAPPING = TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING;

    /**
     * The feature id for the '<em><b>Output Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__OUTPUT_READ_ONLY = TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY;

    /**
     * The feature id for the '<em><b>Top To Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__TOP_TO_BOTTOM = TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__COMMAND_STACK = TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT__TARGET = TRANSFORMATION_MAPPING_ROOT__TARGET;

    /**
     * The number of structural features of the the '<em>Tree Mapping Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TREE_MAPPING_ROOT_FEATURE_COUNT = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_OBJECT__NAME = 0;

    /**
     * The number of structural features of the the '<em>Mapping Class Object</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_OBJECT_FEATURE_COUNT = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__NAME = MAPPING_CLASS_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Recursive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__RECURSIVE = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Recursion Allowed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__RECURSION_ALLOWED = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Recursion Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__RECURSION_CRITERIA = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Recursion Limit</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__RECURSION_LIMIT = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Recursion Limit Error Mode</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__COLUMNS = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Mapping Class Set</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__MAPPING_CLASS_SET = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Input Set</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS__INPUT_SET = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the the '<em>Mapping Class</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_FEATURE_COUNT = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_COLUMN__NAME = MAPPING_CLASS_OBJECT__NAME;

    /**
     * The feature id for the '<em><b>Mapping Class</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_COLUMN__MAPPING_CLASS = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_COLUMN__TYPE = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Mapping Class Column</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_COLUMN_FEATURE_COUNT = MAPPING_CLASS_OBJECT_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.StagingTableImpl <em>Staging Table</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.StagingTableImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getStagingTable()
     * @generated
     */
    int STAGING_TABLE = 11;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__NAME = MAPPING_CLASS__NAME;

    /**
     * The feature id for the '<em><b>Recursive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__RECURSIVE = MAPPING_CLASS__RECURSIVE;

    /**
     * The feature id for the '<em><b>Recursion Allowed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__RECURSION_ALLOWED = MAPPING_CLASS__RECURSION_ALLOWED;

    /**
     * The feature id for the '<em><b>Recursion Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__RECURSION_CRITERIA = MAPPING_CLASS__RECURSION_CRITERIA;

    /**
     * The feature id for the '<em><b>Recursion Limit</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__RECURSION_LIMIT = MAPPING_CLASS__RECURSION_LIMIT;

    /**
     * The feature id for the '<em><b>Recursion Limit Error Mode</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__RECURSION_LIMIT_ERROR_MODE = MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__COLUMNS = MAPPING_CLASS__COLUMNS;

    /**
     * The feature id for the '<em><b>Mapping Class Set</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__MAPPING_CLASS_SET = MAPPING_CLASS__MAPPING_CLASS_SET;

    /**
     * The feature id for the '<em><b>Input Set</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE__INPUT_SET = MAPPING_CLASS__INPUT_SET;

    /**
     * The number of structural features of the the '<em>Staging Table</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STAGING_TABLE_FEATURE_COUNT = MAPPING_CLASS_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.MappingClassSetImpl <em>Mapping Class Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.MappingClassSetImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getMappingClassSet()
     * @generated
     */
    int MAPPING_CLASS_SET = 12;

    /**
     * The feature id for the '<em><b>Mapping Classes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_SET__MAPPING_CLASSES = 0;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_SET__TARGET = 1;

    /**
     * The feature id for the '<em><b>Input Binding</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_SET__INPUT_BINDING = 2;

    /**
     * The number of structural features of the the '<em>Mapping Class Set</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_SET_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.MappingClassSetContainerImpl <em>Mapping Class Set Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.MappingClassSetContainerImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getMappingClassSetContainer()
     * @generated
     */
    int MAPPING_CLASS_SET_CONTAINER = 13;

    /**
     * The feature id for the '<em><b>Mapping Class Sets</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_SET_CONTAINER__MAPPING_CLASS_SETS = 0;

    /**
     * The number of structural features of the the '<em>Mapping Class Set Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MAPPING_CLASS_SET_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.InputParameterImpl <em>Input Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.InputParameterImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getInputParameter()
     * @generated
     */
    int INPUT_PARAMETER = 14;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_PARAMETER__NAME = 0;

    /**
     * The feature id for the '<em><b>Input Set</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_PARAMETER__INPUT_SET = 1;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_PARAMETER__TYPE = 2;

    /**
     * The number of structural features of the the '<em>Input Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_PARAMETER_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.InputSetImpl <em>Input Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.InputSetImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getInputSet()
     * @generated
     */
    int INPUT_SET = 15;

    /**
     * The feature id for the '<em><b>Mapping Class</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_SET__MAPPING_CLASS = 0;

    /**
     * The feature id for the '<em><b>Input Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_SET__INPUT_PARAMETERS = 1;

    /**
     * The number of structural features of the the '<em>Input Set</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_SET_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.InputBindingImpl <em>Input Binding</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.InputBindingImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getInputBinding()
     * @generated
     */
    int INPUT_BINDING = 16;

    /**
     * The feature id for the '<em><b>Mapping Class Set</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_BINDING__MAPPING_CLASS_SET = 0;

    /**
     * The feature id for the '<em><b>Input Parameter</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_BINDING__INPUT_PARAMETER = 1;

    /**
     * The feature id for the '<em><b>Mapping Class Column</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_BINDING__MAPPING_CLASS_COLUMN = 2;

    /**
     * The number of structural features of the the '<em>Input Binding</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INPUT_BINDING_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.DataFlowMappingRootImpl <em>Data Flow Mapping Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.DataFlowMappingRootImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getDataFlowMappingRoot()
     * @generated
     */
    int DATA_FLOW_MAPPING_ROOT = 17;

    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__HELPER = TRANSFORMATION_MAPPING_ROOT__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__NESTED = TRANSFORMATION_MAPPING_ROOT__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__NESTED_IN = TRANSFORMATION_MAPPING_ROOT__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__INPUTS = TRANSFORMATION_MAPPING_ROOT__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__OUTPUTS = TRANSFORMATION_MAPPING_ROOT__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__TYPE_MAPPING = TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING;

    /**
     * The feature id for the '<em><b>Output Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__OUTPUT_READ_ONLY = TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY;

    /**
     * The feature id for the '<em><b>Top To Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__TOP_TO_BOTTOM = TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__COMMAND_STACK = TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__TARGET = TRANSFORMATION_MAPPING_ROOT__TARGET;

    /**
     * The feature id for the '<em><b>Allows Optimization</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Nodes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__NODES = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Links</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT__LINKS = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Data Flow Mapping Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_MAPPING_ROOT_FEATURE_COUNT = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.DataFlowNodeImpl <em>Data Flow Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.DataFlowNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getDataFlowNode()
     * @generated
     */
    int DATA_FLOW_NODE = 18;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_NODE__NAME = 0;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_NODE__OWNER = 1;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_NODE__INPUT_LINKS = 2;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_NODE__OUTPUT_LINKS = 3;

    /**
     * The number of structural features of the the '<em>Data Flow Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_NODE_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.DataFlowLinkImpl <em>Data Flow Link</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.DataFlowLinkImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getDataFlowLink()
     * @generated
     */
    int DATA_FLOW_LINK = 19;

    /**
     * The feature id for the '<em><b>Output Node</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_LINK__OUTPUT_NODE = 0;

    /**
     * The feature id for the '<em><b>Input Node</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_LINK__INPUT_NODE = 1;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_LINK__OWNER = 2;

    /**
     * The number of structural features of the the '<em>Data Flow Link</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DATA_FLOW_LINK_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.ExpressionImpl <em>Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.ExpressionImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getExpression()
     * @generated
     */
    int EXPRESSION = 20;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXPRESSION__VALUE = 0;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXPRESSION__OWNER = 1;

    /**
     * The number of structural features of the the '<em>Expression</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXPRESSION_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.TargetNodeImpl <em>Target Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.TargetNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getTargetNode()
     * @generated
     */
    int TARGET_NODE = 21;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TARGET_NODE__NAME = DATA_FLOW_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TARGET_NODE__OWNER = DATA_FLOW_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TARGET_NODE__INPUT_LINKS = DATA_FLOW_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TARGET_NODE__OUTPUT_LINKS = DATA_FLOW_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TARGET_NODE__TARGET = DATA_FLOW_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Target Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TARGET_NODE_FEATURE_COUNT = DATA_FLOW_NODE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.SourceNodeImpl <em>Source Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.SourceNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSourceNode()
     * @generated
     */
    int SOURCE_NODE = 22;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOURCE_NODE__NAME = DATA_FLOW_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOURCE_NODE__OWNER = DATA_FLOW_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOURCE_NODE__INPUT_LINKS = DATA_FLOW_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOURCE_NODE__OUTPUT_LINKS = DATA_FLOW_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Source</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOURCE_NODE__SOURCE = DATA_FLOW_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Source Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SOURCE_NODE_FEATURE_COUNT = DATA_FLOW_NODE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.AbstractOperationNodeImpl <em>Abstract Operation Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.AbstractOperationNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getAbstractOperationNode()
     * @generated
     */
    int ABSTRACT_OPERATION_NODE = 23;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE__NAME = DATA_FLOW_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE__OWNER = DATA_FLOW_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE__INPUT_LINKS = DATA_FLOW_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE__OUTPUT_LINKS = DATA_FLOW_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE__EXPRESSIONS = DATA_FLOW_NODE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE__NODE_GROUP = DATA_FLOW_NODE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Abstract Operation Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ABSTRACT_OPERATION_NODE_FEATURE_COUNT = DATA_FLOW_NODE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.OperationNodeGroupImpl <em>Operation Node Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.OperationNodeGroupImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getOperationNodeGroup()
     * @generated
     */
    int OPERATION_NODE_GROUP = 24;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__NAME = ABSTRACT_OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__OWNER = ABSTRACT_OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__INPUT_LINKS = ABSTRACT_OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__OUTPUT_LINKS = ABSTRACT_OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__EXPRESSIONS = ABSTRACT_OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__NODE_GROUP = ABSTRACT_OPERATION_NODE__NODE_GROUP;

    /**
     * The feature id for the '<em><b>Contents</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP__CONTENTS = ABSTRACT_OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Operation Node Group</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_GROUP_FEATURE_COUNT = ABSTRACT_OPERATION_NODE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.OperationNodeImpl <em>Operation Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.OperationNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getOperationNode()
     * @generated
     */
    int OPERATION_NODE = 25;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE__NAME = ABSTRACT_OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE__OWNER = ABSTRACT_OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE__INPUT_LINKS = ABSTRACT_OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE__OUTPUT_LINKS = ABSTRACT_OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE__EXPRESSIONS = ABSTRACT_OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE__NODE_GROUP = ABSTRACT_OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Operation Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION_NODE_FEATURE_COUNT = ABSTRACT_OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.JoinNodeImpl <em>Join Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.JoinNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getJoinNode()
     * @generated
     */
    int JOIN_NODE = 26;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE__TYPE = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Join Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.UnionNodeImpl <em>Union Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.UnionNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getUnionNode()
     * @generated
     */
    int UNION_NODE = 27;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Union Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNION_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.ProjectionNodeImpl <em>Projection Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.ProjectionNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getProjectionNode()
     * @generated
     */
    int PROJECTION_NODE = 28;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Projection Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROJECTION_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.FilterNodeImpl <em>Filter Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.FilterNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getFilterNode()
     * @generated
     */
    int FILTER_NODE = 29;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Filter Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FILTER_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.GroupingNodeImpl <em>Grouping Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.GroupingNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getGroupingNode()
     * @generated
     */
    int GROUPING_NODE = 30;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Grouping Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int GROUPING_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.DupRemovalNodeImpl <em>Dup Removal Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.DupRemovalNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getDupRemovalNode()
     * @generated
     */
    int DUP_REMOVAL_NODE = 31;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Dup Removal Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DUP_REMOVAL_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.SortNodeImpl <em>Sort Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.SortNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSortNode()
     * @generated
     */
    int SORT_NODE = 32;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Sort Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SORT_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.SqlNodeImpl <em>Sql Node</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.SqlNodeImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSqlNode()
     * @generated
     */
    int SQL_NODE = 33;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE__NAME = OPERATION_NODE__NAME;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE__OWNER = OPERATION_NODE__OWNER;

    /**
     * The feature id for the '<em><b>Input Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE__INPUT_LINKS = OPERATION_NODE__INPUT_LINKS;

    /**
     * The feature id for the '<em><b>Output Links</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE__OUTPUT_LINKS = OPERATION_NODE__OUTPUT_LINKS;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE__EXPRESSIONS = OPERATION_NODE__EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Node Group</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE__NODE_GROUP = OPERATION_NODE__NODE_GROUP;

    /**
     * The number of structural features of the the '<em>Sql Node</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SQL_NODE_FEATURE_COUNT = OPERATION_NODE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.ExpressionOwner <em>Expression Owner</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.ExpressionOwner
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getExpressionOwner()
     * @generated
     */
    int EXPRESSION_OWNER = 34;

    /**
     * The feature id for the '<em><b>Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXPRESSION_OWNER__EXPRESSIONS = 0;

    /**
     * The number of structural features of the the '<em>Expression Owner</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int EXPRESSION_OWNER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.XQueryTransformationMappingRootImpl <em>XQuery Transformation Mapping Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.XQueryTransformationMappingRootImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getXQueryTransformationMappingRoot()
     * @generated
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT = 35;

    /**
     * The feature id for the '<em><b>Helper</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__HELPER = TRANSFORMATION_MAPPING_ROOT__HELPER;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__NESTED = TRANSFORMATION_MAPPING_ROOT__NESTED;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__NESTED_IN = TRANSFORMATION_MAPPING_ROOT__NESTED_IN;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__INPUTS = TRANSFORMATION_MAPPING_ROOT__INPUTS;

    /**
     * The feature id for the '<em><b>Outputs</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__OUTPUTS = TRANSFORMATION_MAPPING_ROOT__OUTPUTS;

    /**
     * The feature id for the '<em><b>Type Mapping</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING = TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING;

    /**
     * The feature id for the '<em><b>Output Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY = TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY;

    /**
     * The feature id for the '<em><b>Top To Bottom</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM = TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM;

    /**
     * The feature id for the '<em><b>Command Stack</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK = TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK;

    /**
     * The feature id for the '<em><b>Target</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT__TARGET = TRANSFORMATION_MAPPING_ROOT__TARGET;

    /**
     * The number of structural features of the the '<em>XQuery Transformation Mapping Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT = TRANSFORMATION_MAPPING_ROOT_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.impl.XQueryTransformationImpl <em>XQuery Transformation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.impl.XQueryTransformationImpl
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getXQueryTransformation()
     * @generated
     */
    int XQUERY_TRANSFORMATION = 36;

    /**
     * The feature id for the '<em><b>Mapper</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION__MAPPER = MappingPackage.MAPPING_HELPER__MAPPER;

    /**
     * The feature id for the '<em><b>Helped Object</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION__HELPED_OBJECT = MappingPackage.MAPPING_HELPER__HELPED_OBJECT;

    /**
     * The feature id for the '<em><b>Nested In</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION__NESTED_IN = MappingPackage.MAPPING_HELPER__NESTED_IN;

    /**
     * The feature id for the '<em><b>Nested</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION__NESTED = MappingPackage.MAPPING_HELPER__NESTED;

    /**
     * The feature id for the '<em><b>Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION__EXPRESSION = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>XQuery Transformation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XQUERY_TRANSFORMATION_FEATURE_COUNT = MappingPackage.MAPPING_HELPER_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.RecursionErrorMode <em>Recursion Error Mode</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.RecursionErrorMode
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getRecursionErrorMode()
     * @generated
     */
    int RECURSION_ERROR_MODE = 37;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.JoinType <em>Join Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.JoinType
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getJoinType()
     * @generated
     */
    int JOIN_TYPE = 38;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.transformation.SortDirection <em>Sort Direction</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.transformation.SortDirection
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getSortDirection()
     * @generated
     */
    int SORT_DIRECTION = 39;

    /**
     * The meta object id for the '<em>List</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.List
     * @see com.metamatrix.metamodels.transformation.impl.TransformationPackageImpl#getList()
     * @generated
     */
    int LIST = 40;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.TransformationContainer <em>Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Container</em>'.
     * @see com.metamatrix.metamodels.transformation.TransformationContainer
     * @generated
     */
    EClass getTransformationContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.TransformationContainer#getTransformationMappings <em>Transformation Mappings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Transformation Mappings</em>'.
     * @see com.metamatrix.metamodels.transformation.TransformationContainer#getTransformationMappings()
     * @see #getTransformationContainer()
     * @generated
     */
    EReference getTransformationContainer_TransformationMappings();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.SqlTransformation <em>Sql Transformation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sql Transformation</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation
     * @generated
     */
    EClass getSqlTransformation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getSelectSql <em>Select Sql</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Select Sql</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#getSelectSql()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_SelectSql();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getInsertSql <em>Insert Sql</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Insert Sql</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#getInsertSql()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_InsertSql();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getUpdateSql <em>Update Sql</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Update Sql</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#getUpdateSql()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_UpdateSql();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getDeleteSql <em>Delete Sql</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Delete Sql</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#getDeleteSql()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_DeleteSql();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isInsertAllowed <em>Insert Allowed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Insert Allowed</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isInsertAllowed()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_InsertAllowed();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateAllowed <em>Update Allowed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Update Allowed</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateAllowed()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_UpdateAllowed();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteAllowed <em>Delete Allowed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Delete Allowed</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteAllowed()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_DeleteAllowed();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isOutputLocked <em>Output Locked</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Output Locked</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isOutputLocked()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_OutputLocked();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isInsertSqlDefault <em>Insert Sql Default</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Insert Sql Default</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isInsertSqlDefault()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_InsertSqlDefault();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateSqlDefault <em>Update Sql Default</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Update Sql Default</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateSqlDefault()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_UpdateSqlDefault();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteSqlDefault <em>Delete Sql Default</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Delete Sql Default</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteSqlDefault()
     * @see #getSqlTransformation()
     * @generated
     */
    EAttribute getSqlTransformation_DeleteSqlDefault();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getAliases <em>Aliases</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Aliases</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation#getAliases()
     * @see #getSqlTransformation()
     * @generated
     */
    EReference getSqlTransformation_Aliases();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.TransformationMappingRoot <em>Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Root</em>'.
     * @see com.metamatrix.metamodels.transformation.TransformationMappingRoot
     * @generated
     */
    EClass getTransformationMappingRoot();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.TransformationMappingRoot#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see com.metamatrix.metamodels.transformation.TransformationMappingRoot#getTarget()
     * @see #getTransformationMappingRoot()
     * @generated
     */
    EReference getTransformationMappingRoot_Target();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.FragmentMappingRoot <em>Fragment Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Fragment Mapping Root</em>'.
     * @see com.metamatrix.metamodels.transformation.FragmentMappingRoot
     * @generated
     */
    EClass getFragmentMappingRoot();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.TreeMappingRoot <em>Tree Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tree Mapping Root</em>'.
     * @see com.metamatrix.metamodels.transformation.TreeMappingRoot
     * @generated
     */
    EClass getTreeMappingRoot();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.MappingClass <em>Mapping Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Class</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass
     * @generated
     */
    EClass getMappingClass();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.MappingClass#isRecursive <em>Recursive</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Recursive</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#isRecursive()
     * @see #getMappingClass()
     * @generated
     */
    EAttribute getMappingClass_Recursive();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.MappingClass#isRecursionAllowed <em>Recursion Allowed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Recursion Allowed</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#isRecursionAllowed()
     * @see #getMappingClass()
     * @generated
     */
    EAttribute getMappingClass_RecursionAllowed();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionCriteria <em>Recursion Criteria</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Recursion Criteria</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#getRecursionCriteria()
     * @see #getMappingClass()
     * @generated
     */
    EAttribute getMappingClass_RecursionCriteria();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimit <em>Recursion Limit</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Recursion Limit</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimit()
     * @see #getMappingClass()
     * @generated
     */
    EAttribute getMappingClass_RecursionLimit();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimitErrorMode <em>Recursion Limit Error Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Recursion Limit Error Mode</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#getRecursionLimitErrorMode()
     * @see #getMappingClass()
     * @generated
     */
    EAttribute getMappingClass_RecursionLimitErrorMode();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.MappingClass#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Columns</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#getColumns()
     * @see #getMappingClass()
     * @generated
     */
    EReference getMappingClass_Columns();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.MappingClass#getMappingClassSet <em>Mapping Class Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Mapping Class Set</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#getMappingClassSet()
     * @see #getMappingClass()
     * @generated
     */
    EReference getMappingClass_MappingClassSet();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.transformation.MappingClass#getInputSet <em>Input Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Input Set</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClass#getInputSet()
     * @see #getMappingClass()
     * @generated
     */
    EReference getMappingClass_InputSet();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.MappingClassColumn <em>Mapping Class Column</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Class Column</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassColumn
     * @generated
     */
    EClass getMappingClassColumn();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassColumn#getType()
     * @see #getMappingClassColumn()
     * @generated
     */
    EReference getMappingClassColumn_Type();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.MappingClassColumn#getMappingClass <em>Mapping Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Mapping Class</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassColumn#getMappingClass()
     * @see #getMappingClassColumn()
     * @generated
     */
    EReference getMappingClassColumn_MappingClass();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.MappingClassObject <em>Mapping Class Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Class Object</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassObject
     * @generated
     */
    EClass getMappingClassObject();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.MappingClassObject#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassObject#getName()
     * @see #getMappingClassObject()
     * @generated
     */
    EAttribute getMappingClassObject_Name();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.StagingTable <em>Staging Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Staging Table</em>'.
     * @see com.metamatrix.metamodels.transformation.StagingTable
     * @generated
     */
    EClass getStagingTable();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.MappingClassSet <em>Mapping Class Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Class Set</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassSet
     * @generated
     */
    EClass getMappingClassSet();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.MappingClassSet#getMappingClasses <em>Mapping Classes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Mapping Classes</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassSet#getMappingClasses()
     * @see #getMappingClassSet()
     * @generated
     */
    EReference getMappingClassSet_MappingClasses();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.MappingClassSet#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassSet#getTarget()
     * @see #getMappingClassSet()
     * @generated
     */
    EReference getMappingClassSet_Target();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.MappingClassSet#getInputBinding <em>Input Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Input Binding</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassSet#getInputBinding()
     * @see #getMappingClassSet()
     * @generated
     */
    EReference getMappingClassSet_InputBinding();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.MappingClassSetContainer <em>Mapping Class Set Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping Class Set Container</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassSetContainer
     * @generated
     */
    EClass getMappingClassSetContainer();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.MappingClassSetContainer#getMappingClassSets <em>Mapping Class Sets</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Mapping Class Sets</em>'.
     * @see com.metamatrix.metamodels.transformation.MappingClassSetContainer#getMappingClassSets()
     * @see #getMappingClassSetContainer()
     * @generated
     */
    EReference getMappingClassSetContainer_MappingClassSets();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.InputParameter <em>Input Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Input Parameter</em>'.
     * @see com.metamatrix.metamodels.transformation.InputParameter
     * @generated
     */
    EClass getInputParameter();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.InputParameter#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.transformation.InputParameter#getName()
     * @see #getInputParameter()
     * @generated
     */
    EAttribute getInputParameter_Name();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.InputParameter#getInputSet <em>Input Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Input Set</em>'.
     * @see com.metamatrix.metamodels.transformation.InputParameter#getInputSet()
     * @see #getInputParameter()
     * @generated
     */
    EReference getInputParameter_InputSet();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.InputParameter#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see com.metamatrix.metamodels.transformation.InputParameter#getType()
     * @see #getInputParameter()
     * @generated
     */
    EReference getInputParameter_Type();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.InputSet <em>Input Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Input Set</em>'.
     * @see com.metamatrix.metamodels.transformation.InputSet
     * @generated
     */
    EClass getInputSet();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.InputSet#getMappingClass <em>Mapping Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Mapping Class</em>'.
     * @see com.metamatrix.metamodels.transformation.InputSet#getMappingClass()
     * @see #getInputSet()
     * @generated
     */
    EReference getInputSet_MappingClass();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.InputSet#getInputParameters <em>Input Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Input Parameters</em>'.
     * @see com.metamatrix.metamodels.transformation.InputSet#getInputParameters()
     * @see #getInputSet()
     * @generated
     */
    EReference getInputSet_InputParameters();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.InputBinding <em>Input Binding</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Input Binding</em>'.
     * @see com.metamatrix.metamodels.transformation.InputBinding
     * @generated
     */
    EClass getInputBinding();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassSet <em>Mapping Class Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Mapping Class Set</em>'.
     * @see com.metamatrix.metamodels.transformation.InputBinding#getMappingClassSet()
     * @see #getInputBinding()
     * @generated
     */
    EReference getInputBinding_MappingClassSet();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.InputBinding#getInputParameter <em>Input Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Input Parameter</em>'.
     * @see com.metamatrix.metamodels.transformation.InputBinding#getInputParameter()
     * @see #getInputBinding()
     * @generated
     */
    EReference getInputBinding_InputParameter();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.InputBinding#getMappingClassColumn <em>Mapping Class Column</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Mapping Class Column</em>'.
     * @see com.metamatrix.metamodels.transformation.InputBinding#getMappingClassColumn()
     * @see #getInputBinding()
     * @generated
     */
    EReference getInputBinding_MappingClassColumn();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot <em>Data Flow Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Data Flow Mapping Root</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot
     * @generated
     */
    EClass getDataFlowMappingRoot();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#isAllowsOptimization <em>Allows Optimization</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Allows Optimization</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot#isAllowsOptimization()
     * @see #getDataFlowMappingRoot()
     * @generated
     */
    EAttribute getDataFlowMappingRoot_AllowsOptimization();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getNodes <em>Nodes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Nodes</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getNodes()
     * @see #getDataFlowMappingRoot()
     * @generated
     */
    EReference getDataFlowMappingRoot_Nodes();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks <em>Links</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Links</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot#getLinks()
     * @see #getDataFlowMappingRoot()
     * @generated
     */
    EReference getDataFlowMappingRoot_Links();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.DataFlowNode <em>Data Flow Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Data Flow Node</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowNode
     * @generated
     */
    EClass getDataFlowNode();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getName()
     * @see #getDataFlowNode()
     * @generated
     */
    EAttribute getDataFlowNode_Name();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getOwner()
     * @see #getDataFlowNode()
     * @generated
     */
    EReference getDataFlowNode_Owner();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks <em>Input Links</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Input Links</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getInputLinks()
     * @see #getDataFlowNode()
     * @generated
     */
    EReference getDataFlowNode_InputLinks();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks <em>Output Links</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Output Links</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowNode#getOutputLinks()
     * @see #getDataFlowNode()
     * @generated
     */
    EReference getDataFlowNode_OutputLinks();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.DataFlowLink <em>Data Flow Link</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Data Flow Link</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowLink
     * @generated
     */
    EClass getDataFlowLink();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode <em>Output Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Output Node</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowLink#getOutputNode()
     * @see #getDataFlowLink()
     * @generated
     */
    EReference getDataFlowLink_OutputNode();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode <em>Input Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Input Node</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowLink#getInputNode()
     * @see #getDataFlowLink()
     * @generated
     */
    EReference getDataFlowLink_InputNode();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.DataFlowLink#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.transformation.DataFlowLink#getOwner()
     * @see #getDataFlowLink()
     * @generated
     */
    EReference getDataFlowLink_Owner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.Expression <em>Expression</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Expression</em>'.
     * @see com.metamatrix.metamodels.transformation.Expression
     * @generated
     */
    EClass getExpression();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.Expression#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see com.metamatrix.metamodels.transformation.Expression#getValue()
     * @see #getExpression()
     * @generated
     */
    EAttribute getExpression_Value();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.Expression#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.transformation.Expression#getOwner()
     * @see #getExpression()
     * @generated
     */
    EReference getExpression_Owner();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.TargetNode <em>Target Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Target Node</em>'.
     * @see com.metamatrix.metamodels.transformation.TargetNode
     * @generated
     */
    EClass getTargetNode();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.TargetNode#getTarget <em>Target</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Target</em>'.
     * @see com.metamatrix.metamodels.transformation.TargetNode#getTarget()
     * @see #getTargetNode()
     * @generated
     */
    EReference getTargetNode_Target();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.SourceNode <em>Source Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Source Node</em>'.
     * @see com.metamatrix.metamodels.transformation.SourceNode
     * @generated
     */
    EClass getSourceNode();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.SourceNode#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Source</em>'.
     * @see com.metamatrix.metamodels.transformation.SourceNode#getSource()
     * @see #getSourceNode()
     * @generated
     */
    EReference getSourceNode_Source();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.AbstractOperationNode <em>Abstract Operation Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Abstract Operation Node</em>'.
     * @see com.metamatrix.metamodels.transformation.AbstractOperationNode
     * @generated
     */
    EClass getAbstractOperationNode();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.AbstractOperationNode#getNodeGroup <em>Node Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Node Group</em>'.
     * @see com.metamatrix.metamodels.transformation.AbstractOperationNode#getNodeGroup()
     * @see #getAbstractOperationNode()
     * @generated
     */
    EReference getAbstractOperationNode_NodeGroup();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.OperationNodeGroup <em>Operation Node Group</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation Node Group</em>'.
     * @see com.metamatrix.metamodels.transformation.OperationNodeGroup
     * @generated
     */
    EClass getOperationNodeGroup();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.OperationNodeGroup#getContents <em>Contents</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Contents</em>'.
     * @see com.metamatrix.metamodels.transformation.OperationNodeGroup#getContents()
     * @see #getOperationNodeGroup()
     * @generated
     */
    EReference getOperationNodeGroup_Contents();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.OperationNode <em>Operation Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation Node</em>'.
     * @see com.metamatrix.metamodels.transformation.OperationNode
     * @generated
     */
    EClass getOperationNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.JoinNode <em>Join Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Join Node</em>'.
     * @see com.metamatrix.metamodels.transformation.JoinNode
     * @generated
     */
    EClass getJoinNode();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.JoinNode#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Type</em>'.
     * @see com.metamatrix.metamodels.transformation.JoinNode#getType()
     * @see #getJoinNode()
     * @generated
     */
    EAttribute getJoinNode_Type();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.UnionNode <em>Union Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Union Node</em>'.
     * @see com.metamatrix.metamodels.transformation.UnionNode
     * @generated
     */
    EClass getUnionNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.ProjectionNode <em>Projection Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Projection Node</em>'.
     * @see com.metamatrix.metamodels.transformation.ProjectionNode
     * @generated
     */
    EClass getProjectionNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.FilterNode <em>Filter Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Filter Node</em>'.
     * @see com.metamatrix.metamodels.transformation.FilterNode
     * @generated
     */
    EClass getFilterNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.GroupingNode <em>Grouping Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Grouping Node</em>'.
     * @see com.metamatrix.metamodels.transformation.GroupingNode
     * @generated
     */
    EClass getGroupingNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.DupRemovalNode <em>Dup Removal Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Dup Removal Node</em>'.
     * @see com.metamatrix.metamodels.transformation.DupRemovalNode
     * @generated
     */
    EClass getDupRemovalNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.SortNode <em>Sort Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sort Node</em>'.
     * @see com.metamatrix.metamodels.transformation.SortNode
     * @generated
     */
    EClass getSortNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.SqlNode <em>Sql Node</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sql Node</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlNode
     * @generated
     */
    EClass getSqlNode();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.ExpressionOwner <em>Expression Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Expression Owner</em>'.
     * @see com.metamatrix.metamodels.transformation.ExpressionOwner
     * @generated
     */
    EClass getExpressionOwner();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.transformation.ExpressionOwner#getExpressions <em>Expressions</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Expressions</em>'.
     * @see com.metamatrix.metamodels.transformation.ExpressionOwner#getExpressions()
     * @see #getExpressionOwner()
     * @generated
     */
    EReference getExpressionOwner_Expressions();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot <em>XQuery Transformation Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>XQuery Transformation Mapping Root</em>'.
     * @see com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot
     * @generated
     */
    EClass getXQueryTransformationMappingRoot();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.XQueryTransformation <em>XQuery Transformation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>XQuery Transformation</em>'.
     * @see com.metamatrix.metamodels.transformation.XQueryTransformation
     * @generated
     */
    EClass getXQueryTransformation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.XQueryTransformation#getExpression <em>Expression</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Expression</em>'.
     * @see com.metamatrix.metamodels.transformation.XQueryTransformation#getExpression()
     * @see #getXQueryTransformation()
     * @generated
     */
    EAttribute getXQueryTransformation_Expression();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.transformation.RecursionErrorMode <em>Recursion Error Mode</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Recursion Error Mode</em>'.
     * @see com.metamatrix.metamodels.transformation.RecursionErrorMode
     * @generated
     */
    EEnum getRecursionErrorMode();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.transformation.JoinType <em>Join Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Join Type</em>'.
     * @see com.metamatrix.metamodels.transformation.JoinType
     * @generated
     */
    EEnum getJoinType();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.transformation.SortDirection <em>Sort Direction</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Sort Direction</em>'.
     * @see com.metamatrix.metamodels.transformation.SortDirection
     * @generated
     */
    EEnum getSortDirection();

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
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.TransformationMapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mapping</em>'.
     * @see com.metamatrix.metamodels.transformation.TransformationMapping
     * @generated
     */
    EClass getTransformationMapping();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.SqlAlias <em>Sql Alias</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sql Alias</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlAlias
     * @generated
     */
    EClass getSqlAlias();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.transformation.SqlAlias#getAlias <em>Alias</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Alias</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlAlias#getAlias()
     * @see #getSqlAlias()
     * @generated
     */
    EAttribute getSqlAlias_Alias();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.transformation.SqlAlias#getAliasedObject <em>Aliased Object</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Aliased Object</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlAlias#getAliasedObject()
     * @see #getSqlAlias()
     * @generated
     */
    EReference getSqlAlias_AliasedObject();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.transformation.SqlAlias#getSqlTransformation <em>Sql Transformation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Sql Transformation</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlAlias#getSqlTransformation()
     * @see #getSqlAlias()
     * @generated
     */
    EReference getSqlAlias_SqlTransformation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot <em>Sql Transformation Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sql Transformation Mapping Root</em>'.
     * @see com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot
     * @generated
     */
    EClass getSqlTransformationMappingRoot();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    TransformationFactory getTransformationFactory();

} //TransformationPackage
