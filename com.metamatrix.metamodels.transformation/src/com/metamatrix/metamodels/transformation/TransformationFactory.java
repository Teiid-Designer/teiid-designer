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

package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.transformation.TransformationPackage
 * @generated
 */
public interface TransformationFactory extends EFactory{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     */
    TransformationFactory eINSTANCE = new com.metamatrix.metamodels.transformation.impl.TransformationFactoryImpl();

    /**
     * Returns a new object of class '<em>Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Container</em>'.
     * @generated
     */
    TransformationContainer createTransformationContainer();

    /**
     * Returns a new object of class '<em>Sql Transformation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Sql Transformation</em>'.
     * @generated
     */
    SqlTransformation createSqlTransformation();

    /**
     * Returns a new object of class '<em>Fragment Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Fragment Mapping Root</em>'.
     * @generated
     */
    FragmentMappingRoot createFragmentMappingRoot();

    /**
     * Returns a new object of class '<em>Tree Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Tree Mapping Root</em>'.
     * @generated
     */
    TreeMappingRoot createTreeMappingRoot();

    /**
     * Returns a new object of class '<em>Mapping Class</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mapping Class</em>'.
     * @generated
     */
    MappingClass createMappingClass();

    /**
     * Returns a new object of class '<em>Mapping Class Column</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mapping Class Column</em>'.
     * @generated
     */
    MappingClassColumn createMappingClassColumn();

    /**
     * Returns a new object of class '<em>Staging Table</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Staging Table</em>'.
     * @generated
     */
    StagingTable createStagingTable();

    /**
     * Returns a new object of class '<em>Mapping Class Set</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mapping Class Set</em>'.
     * @generated
     */
    MappingClassSet createMappingClassSet();

    /**
     * Returns a new object of class '<em>Mapping Class Set Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mapping Class Set Container</em>'.
     * @generated
     */
    MappingClassSetContainer createMappingClassSetContainer();

    /**
     * Returns a new object of class '<em>Input Parameter</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Input Parameter</em>'.
     * @generated
     */
    InputParameter createInputParameter();

    /**
     * Returns a new object of class '<em>Input Set</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Input Set</em>'.
     * @generated
     */
    InputSet createInputSet();

    /**
     * Returns a new object of class '<em>Input Binding</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Input Binding</em>'.
     * @generated
     */
    InputBinding createInputBinding();

    /**
     * Returns a new object of class '<em>Data Flow Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Data Flow Mapping Root</em>'.
     * @generated
     */
    DataFlowMappingRoot createDataFlowMappingRoot();

    /**
     * Returns a new object of class '<em>Data Flow Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Data Flow Node</em>'.
     * @generated
     */
    DataFlowNode createDataFlowNode();

    /**
     * Returns a new object of class '<em>Data Flow Link</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Data Flow Link</em>'.
     * @generated
     */
    DataFlowLink createDataFlowLink();

    /**
     * Returns a new object of class '<em>Expression</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Expression</em>'.
     * @generated
     */
    Expression createExpression();

    /**
     * Returns a new object of class '<em>Target Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Target Node</em>'.
     * @generated
     */
    TargetNode createTargetNode();

    /**
     * Returns a new object of class '<em>Source Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Source Node</em>'.
     * @generated
     */
    SourceNode createSourceNode();

    /**
     * Returns a new object of class '<em>Operation Node Group</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Operation Node Group</em>'.
     * @generated
     */
    OperationNodeGroup createOperationNodeGroup();

    /**
     * Returns a new object of class '<em>Operation Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Operation Node</em>'.
     * @generated
     */
    OperationNode createOperationNode();

    /**
     * Returns a new object of class '<em>Join Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Join Node</em>'.
     * @generated
     */
    JoinNode createJoinNode();

    /**
     * Returns a new object of class '<em>Union Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Union Node</em>'.
     * @generated
     */
    UnionNode createUnionNode();

    /**
     * Returns a new object of class '<em>Projection Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Projection Node</em>'.
     * @generated
     */
    ProjectionNode createProjectionNode();

    /**
     * Returns a new object of class '<em>Filter Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Filter Node</em>'.
     * @generated
     */
    FilterNode createFilterNode();

    /**
     * Returns a new object of class '<em>Grouping Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Grouping Node</em>'.
     * @generated
     */
    GroupingNode createGroupingNode();

    /**
     * Returns a new object of class '<em>Dup Removal Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Dup Removal Node</em>'.
     * @generated
     */
    DupRemovalNode createDupRemovalNode();

    /**
     * Returns a new object of class '<em>Sort Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Sort Node</em>'.
     * @generated
     */
    SortNode createSortNode();

    /**
     * Returns a new object of class '<em>Sql Node</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Sql Node</em>'.
     * @generated
     */
    SqlNode createSqlNode();

    /**
     * Returns a new object of class '<em>XQuery Transformation Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>XQuery Transformation Mapping Root</em>'.
     * @generated
     */
    XQueryTransformationMappingRoot createXQueryTransformationMappingRoot();

    /**
     * Returns a new object of class '<em>XQuery Transformation</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>XQuery Transformation</em>'.
     * @generated
     */
    XQueryTransformation createXQueryTransformation();

    /**
     * Returns a new object of class '<em>Mapping</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mapping</em>'.
     * @generated
     */
    TransformationMapping createTransformationMapping();

    /**
     * Returns a new object of class '<em>Sql Alias</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Sql Alias</em>'.
     * @generated
     */
    SqlAlias createSqlAlias();

    /**
     * Returns a new object of class '<em>Sql Transformation Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Sql Transformation Mapping Root</em>'.
     * @generated
     */
    SqlTransformationMappingRoot createSqlTransformationMappingRoot();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    TransformationPackage getTransformationPackage();

} //TransformationFactory
