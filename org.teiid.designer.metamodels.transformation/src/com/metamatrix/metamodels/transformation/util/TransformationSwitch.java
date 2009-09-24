/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.util;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingRoot;

import com.metamatrix.metamodels.transformation.AbstractOperationNode;
import com.metamatrix.metamodels.transformation.DataFlowLink;
import com.metamatrix.metamodels.transformation.DataFlowMappingRoot;
import com.metamatrix.metamodels.transformation.DataFlowNode;
import com.metamatrix.metamodels.transformation.DupRemovalNode;
import com.metamatrix.metamodels.transformation.Expression;
import com.metamatrix.metamodels.transformation.ExpressionOwner;
import com.metamatrix.metamodels.transformation.FilterNode;
import com.metamatrix.metamodels.transformation.FragmentMappingRoot;
import com.metamatrix.metamodels.transformation.GroupingNode;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.JoinNode;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassObject;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.OperationNode;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.ProjectionNode;
import com.metamatrix.metamodels.transformation.SortNode;
import com.metamatrix.metamodels.transformation.SourceNode;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlNode;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TargetNode;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationMapping;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.transformation.UnionNode;
import com.metamatrix.metamodels.transformation.XQueryTransformation;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.transformation.TransformationPackage
 * @generated
 */
public class TransformationSwitch {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static TransformationPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TransformationSwitch() {
        if (modelPackage == null) {
            modelPackage = TransformationPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return
            eSuperTypes.isEmpty() ?
                defaultCase(theEObject) :
                doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case TransformationPackage.TRANSFORMATION_CONTAINER: {
                TransformationContainer transformationContainer = (TransformationContainer)theEObject;
                Object result = caseTransformationContainer(transformationContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.SQL_TRANSFORMATION: {
                SqlTransformation sqlTransformation = (SqlTransformation)theEObject;
                Object result = caseSqlTransformation(sqlTransformation);
                if (result == null) result = caseMappingHelper(sqlTransformation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT: {
                TransformationMappingRoot transformationMappingRoot = (TransformationMappingRoot)theEObject;
                Object result = caseTransformationMappingRoot(transformationMappingRoot);
                if (result == null) result = caseMappingRoot(transformationMappingRoot);
                if (result == null) result = caseMapping(transformationMappingRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.TRANSFORMATION_MAPPING: {
                TransformationMapping transformationMapping = (TransformationMapping)theEObject;
                Object result = caseTransformationMapping(transformationMapping);
                if (result == null) result = caseMapping(transformationMapping);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.SQL_ALIAS: {
                SqlAlias sqlAlias = (SqlAlias)theEObject;
                Object result = caseSqlAlias(sqlAlias);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.SQL_TRANSFORMATION_MAPPING_ROOT: {
                SqlTransformationMappingRoot sqlTransformationMappingRoot = (SqlTransformationMappingRoot)theEObject;
                Object result = caseSqlTransformationMappingRoot(sqlTransformationMappingRoot);
                if (result == null) result = caseTransformationMappingRoot(sqlTransformationMappingRoot);
                if (result == null) result = caseMappingRoot(sqlTransformationMappingRoot);
                if (result == null) result = caseMapping(sqlTransformationMappingRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.FRAGMENT_MAPPING_ROOT: {
                FragmentMappingRoot fragmentMappingRoot = (FragmentMappingRoot)theEObject;
                Object result = caseFragmentMappingRoot(fragmentMappingRoot);
                if (result == null) result = caseTransformationMappingRoot(fragmentMappingRoot);
                if (result == null) result = caseMappingRoot(fragmentMappingRoot);
                if (result == null) result = caseMapping(fragmentMappingRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.TREE_MAPPING_ROOT: {
                TreeMappingRoot treeMappingRoot = (TreeMappingRoot)theEObject;
                Object result = caseTreeMappingRoot(treeMappingRoot);
                if (result == null) result = caseTransformationMappingRoot(treeMappingRoot);
                if (result == null) result = caseMappingRoot(treeMappingRoot);
                if (result == null) result = caseMapping(treeMappingRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.MAPPING_CLASS: {
                MappingClass mappingClass = (MappingClass)theEObject;
                Object result = caseMappingClass(mappingClass);
                if (result == null) result = caseMappingClassObject(mappingClass);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.MAPPING_CLASS_COLUMN: {
                MappingClassColumn mappingClassColumn = (MappingClassColumn)theEObject;
                Object result = caseMappingClassColumn(mappingClassColumn);
                if (result == null) result = caseMappingClassObject(mappingClassColumn);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.MAPPING_CLASS_OBJECT: {
                MappingClassObject mappingClassObject = (MappingClassObject)theEObject;
                Object result = caseMappingClassObject(mappingClassObject);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.STAGING_TABLE: {
                StagingTable stagingTable = (StagingTable)theEObject;
                Object result = caseStagingTable(stagingTable);
                if (result == null) result = caseMappingClass(stagingTable);
                if (result == null) result = caseMappingClassObject(stagingTable);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.MAPPING_CLASS_SET: {
                MappingClassSet mappingClassSet = (MappingClassSet)theEObject;
                Object result = caseMappingClassSet(mappingClassSet);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.MAPPING_CLASS_SET_CONTAINER: {
                MappingClassSetContainer mappingClassSetContainer = (MappingClassSetContainer)theEObject;
                Object result = caseMappingClassSetContainer(mappingClassSetContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.INPUT_PARAMETER: {
                InputParameter inputParameter = (InputParameter)theEObject;
                Object result = caseInputParameter(inputParameter);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.INPUT_SET: {
                InputSet inputSet = (InputSet)theEObject;
                Object result = caseInputSet(inputSet);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.INPUT_BINDING: {
                InputBinding inputBinding = (InputBinding)theEObject;
                Object result = caseInputBinding(inputBinding);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT: {
                DataFlowMappingRoot dataFlowMappingRoot = (DataFlowMappingRoot)theEObject;
                Object result = caseDataFlowMappingRoot(dataFlowMappingRoot);
                if (result == null) result = caseTransformationMappingRoot(dataFlowMappingRoot);
                if (result == null) result = caseMappingRoot(dataFlowMappingRoot);
                if (result == null) result = caseMapping(dataFlowMappingRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.DATA_FLOW_NODE: {
                DataFlowNode dataFlowNode = (DataFlowNode)theEObject;
                Object result = caseDataFlowNode(dataFlowNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.DATA_FLOW_LINK: {
                DataFlowLink dataFlowLink = (DataFlowLink)theEObject;
                Object result = caseDataFlowLink(dataFlowLink);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.EXPRESSION: {
                Expression expression = (Expression)theEObject;
                Object result = caseExpression(expression);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.TARGET_NODE: {
                TargetNode targetNode = (TargetNode)theEObject;
                Object result = caseTargetNode(targetNode);
                if (result == null) result = caseDataFlowNode(targetNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.SOURCE_NODE: {
                SourceNode sourceNode = (SourceNode)theEObject;
                Object result = caseSourceNode(sourceNode);
                if (result == null) result = caseDataFlowNode(sourceNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.ABSTRACT_OPERATION_NODE: {
                AbstractOperationNode abstractOperationNode = (AbstractOperationNode)theEObject;
                Object result = caseAbstractOperationNode(abstractOperationNode);
                if (result == null) result = caseDataFlowNode(abstractOperationNode);
                if (result == null) result = caseExpressionOwner(abstractOperationNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.OPERATION_NODE_GROUP: {
                OperationNodeGroup operationNodeGroup = (OperationNodeGroup)theEObject;
                Object result = caseOperationNodeGroup(operationNodeGroup);
                if (result == null) result = caseAbstractOperationNode(operationNodeGroup);
                if (result == null) result = caseDataFlowNode(operationNodeGroup);
                if (result == null) result = caseExpressionOwner(operationNodeGroup);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.OPERATION_NODE: {
                OperationNode operationNode = (OperationNode)theEObject;
                Object result = caseOperationNode(operationNode);
                if (result == null) result = caseAbstractOperationNode(operationNode);
                if (result == null) result = caseDataFlowNode(operationNode);
                if (result == null) result = caseExpressionOwner(operationNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.JOIN_NODE: {
                JoinNode joinNode = (JoinNode)theEObject;
                Object result = caseJoinNode(joinNode);
                if (result == null) result = caseOperationNode(joinNode);
                if (result == null) result = caseAbstractOperationNode(joinNode);
                if (result == null) result = caseDataFlowNode(joinNode);
                if (result == null) result = caseExpressionOwner(joinNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.UNION_NODE: {
                UnionNode unionNode = (UnionNode)theEObject;
                Object result = caseUnionNode(unionNode);
                if (result == null) result = caseOperationNode(unionNode);
                if (result == null) result = caseAbstractOperationNode(unionNode);
                if (result == null) result = caseDataFlowNode(unionNode);
                if (result == null) result = caseExpressionOwner(unionNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.PROJECTION_NODE: {
                ProjectionNode projectionNode = (ProjectionNode)theEObject;
                Object result = caseProjectionNode(projectionNode);
                if (result == null) result = caseOperationNode(projectionNode);
                if (result == null) result = caseAbstractOperationNode(projectionNode);
                if (result == null) result = caseDataFlowNode(projectionNode);
                if (result == null) result = caseExpressionOwner(projectionNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.FILTER_NODE: {
                FilterNode filterNode = (FilterNode)theEObject;
                Object result = caseFilterNode(filterNode);
                if (result == null) result = caseOperationNode(filterNode);
                if (result == null) result = caseAbstractOperationNode(filterNode);
                if (result == null) result = caseDataFlowNode(filterNode);
                if (result == null) result = caseExpressionOwner(filterNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.GROUPING_NODE: {
                GroupingNode groupingNode = (GroupingNode)theEObject;
                Object result = caseGroupingNode(groupingNode);
                if (result == null) result = caseOperationNode(groupingNode);
                if (result == null) result = caseAbstractOperationNode(groupingNode);
                if (result == null) result = caseDataFlowNode(groupingNode);
                if (result == null) result = caseExpressionOwner(groupingNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.DUP_REMOVAL_NODE: {
                DupRemovalNode dupRemovalNode = (DupRemovalNode)theEObject;
                Object result = caseDupRemovalNode(dupRemovalNode);
                if (result == null) result = caseOperationNode(dupRemovalNode);
                if (result == null) result = caseAbstractOperationNode(dupRemovalNode);
                if (result == null) result = caseDataFlowNode(dupRemovalNode);
                if (result == null) result = caseExpressionOwner(dupRemovalNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.SORT_NODE: {
                SortNode sortNode = (SortNode)theEObject;
                Object result = caseSortNode(sortNode);
                if (result == null) result = caseOperationNode(sortNode);
                if (result == null) result = caseAbstractOperationNode(sortNode);
                if (result == null) result = caseDataFlowNode(sortNode);
                if (result == null) result = caseExpressionOwner(sortNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.SQL_NODE: {
                SqlNode sqlNode = (SqlNode)theEObject;
                Object result = caseSqlNode(sqlNode);
                if (result == null) result = caseOperationNode(sqlNode);
                if (result == null) result = caseAbstractOperationNode(sqlNode);
                if (result == null) result = caseDataFlowNode(sqlNode);
                if (result == null) result = caseExpressionOwner(sqlNode);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.EXPRESSION_OWNER: {
                ExpressionOwner expressionOwner = (ExpressionOwner)theEObject;
                Object result = caseExpressionOwner(expressionOwner);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.XQUERY_TRANSFORMATION_MAPPING_ROOT: {
                XQueryTransformationMappingRoot xQueryTransformationMappingRoot = (XQueryTransformationMappingRoot)theEObject;
                Object result = caseXQueryTransformationMappingRoot(xQueryTransformationMappingRoot);
                if (result == null) result = caseTransformationMappingRoot(xQueryTransformationMappingRoot);
                if (result == null) result = caseMappingRoot(xQueryTransformationMappingRoot);
                if (result == null) result = caseMapping(xQueryTransformationMappingRoot);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case TransformationPackage.XQUERY_TRANSFORMATION: {
                XQueryTransformation xQueryTransformation = (XQueryTransformation)theEObject;
                Object result = caseXQueryTransformation(xQueryTransformation);
                if (result == null) result = caseMappingHelper(xQueryTransformation);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseTransformationContainer(TransformationContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sql Transformation</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sql Transformation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSqlTransformation(SqlTransformation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseTransformationMappingRoot(TransformationMappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Fragment Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Fragment Mapping Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseFragmentMappingRoot(FragmentMappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Tree Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Tree Mapping Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseTreeMappingRoot(TreeMappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping Class</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping Class</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingClass(MappingClass object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping Class Column</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping Class Column</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingClassColumn(MappingClassColumn object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping Class Object</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping Class Object</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingClassObject(MappingClassObject object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Staging Table</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Staging Table</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseStagingTable(StagingTable object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping Class Set</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping Class Set</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingClassSet(MappingClassSet object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping Class Set Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping Class Set Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingClassSetContainer(MappingClassSetContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Input Parameter</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Input Parameter</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseInputParameter(InputParameter object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Input Set</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Input Set</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseInputSet(InputSet object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Input Binding</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Input Binding</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseInputBinding(InputBinding object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Data Flow Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Data Flow Mapping Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseDataFlowMappingRoot(DataFlowMappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Data Flow Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Data Flow Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseDataFlowNode(DataFlowNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Data Flow Link</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Data Flow Link</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseDataFlowLink(DataFlowLink object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Expression</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Expression</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseExpression(Expression object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Target Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Target Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseTargetNode(TargetNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Source Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Source Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSourceNode(SourceNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Abstract Operation Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Abstract Operation Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseAbstractOperationNode(AbstractOperationNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Operation Node Group</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Operation Node Group</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseOperationNodeGroup(OperationNodeGroup object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Operation Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Operation Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseOperationNode(OperationNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Join Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Join Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJoinNode(JoinNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Union Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Union Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseUnionNode(UnionNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Projection Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Projection Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProjectionNode(ProjectionNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Filter Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Filter Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseFilterNode(FilterNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Grouping Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Grouping Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseGroupingNode(GroupingNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Dup Removal Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Dup Removal Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseDupRemovalNode(DupRemovalNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sort Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sort Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSortNode(SortNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sql Node</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sql Node</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSqlNode(SqlNode object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Expression Owner</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Expression Owner</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseExpressionOwner(ExpressionOwner object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>XQuery Transformation Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>XQuery Transformation Mapping Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXQueryTransformationMappingRoot(XQueryTransformationMappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>XQuery Transformation</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>XQuery Transformation</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseXQueryTransformation(XQueryTransformation object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseTransformationMapping(TransformationMapping object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sql Alias</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sql Alias</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSqlAlias(SqlAlias object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Sql Transformation Mapping Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Sql Transformation Mapping Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSqlTransformationMappingRoot(SqlTransformationMappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Helper</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Helper</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingHelper(MappingHelper object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Mapping</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Mapping</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMapping(Mapping object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Root</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Root</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseMappingRoot(MappingRoot object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase(EObject object) {
        return null;
    }

} //TransformationSwitch
