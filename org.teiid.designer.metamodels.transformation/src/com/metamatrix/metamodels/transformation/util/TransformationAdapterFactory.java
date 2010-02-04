/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
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
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.transformation.TransformationPackage
 * @generated
 */
public class TransformationAdapterFactory extends AdapterFactoryImpl {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static TransformationPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TransformationAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = TransformationPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch the delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TransformationSwitch modelSwitch =
        new TransformationSwitch() {
            @Override
            public Object caseTransformationContainer(TransformationContainer object) {
                return createTransformationContainerAdapter();
            }
            @Override
            public Object caseSqlTransformation(SqlTransformation object) {
                return createSqlTransformationAdapter();
            }
            @Override
            public Object caseTransformationMappingRoot(TransformationMappingRoot object) {
                return createTransformationMappingRootAdapter();
            }
            @Override
            public Object caseTransformationMapping(TransformationMapping object) {
                return createTransformationMappingAdapter();
            }
            @Override
            public Object caseSqlAlias(SqlAlias object) {
                return createSqlAliasAdapter();
            }
            @Override
            public Object caseSqlTransformationMappingRoot(SqlTransformationMappingRoot object) {
                return createSqlTransformationMappingRootAdapter();
            }
            @Override
            public Object caseFragmentMappingRoot(FragmentMappingRoot object) {
                return createFragmentMappingRootAdapter();
            }
            @Override
            public Object caseTreeMappingRoot(TreeMappingRoot object) {
                return createTreeMappingRootAdapter();
            }
            @Override
            public Object caseMappingClass(MappingClass object) {
                return createMappingClassAdapter();
            }
            @Override
            public Object caseMappingClassColumn(MappingClassColumn object) {
                return createMappingClassColumnAdapter();
            }
            @Override
            public Object caseMappingClassObject(MappingClassObject object) {
                return createMappingClassObjectAdapter();
            }
            @Override
            public Object caseStagingTable(StagingTable object) {
                return createStagingTableAdapter();
            }
            @Override
            public Object caseMappingClassSet(MappingClassSet object) {
                return createMappingClassSetAdapter();
            }
            @Override
            public Object caseMappingClassSetContainer(MappingClassSetContainer object) {
                return createMappingClassSetContainerAdapter();
            }
            @Override
            public Object caseInputParameter(InputParameter object) {
                return createInputParameterAdapter();
            }
            @Override
            public Object caseInputSet(InputSet object) {
                return createInputSetAdapter();
            }
            @Override
            public Object caseInputBinding(InputBinding object) {
                return createInputBindingAdapter();
            }
            @Override
            public Object caseDataFlowMappingRoot(DataFlowMappingRoot object) {
                return createDataFlowMappingRootAdapter();
            }
            @Override
            public Object caseDataFlowNode(DataFlowNode object) {
                return createDataFlowNodeAdapter();
            }
            @Override
            public Object caseDataFlowLink(DataFlowLink object) {
                return createDataFlowLinkAdapter();
            }
            @Override
            public Object caseExpression(Expression object) {
                return createExpressionAdapter();
            }
            @Override
            public Object caseTargetNode(TargetNode object) {
                return createTargetNodeAdapter();
            }
            @Override
            public Object caseSourceNode(SourceNode object) {
                return createSourceNodeAdapter();
            }
            @Override
            public Object caseAbstractOperationNode(AbstractOperationNode object) {
                return createAbstractOperationNodeAdapter();
            }
            @Override
            public Object caseOperationNodeGroup(OperationNodeGroup object) {
                return createOperationNodeGroupAdapter();
            }
            @Override
            public Object caseOperationNode(OperationNode object) {
                return createOperationNodeAdapter();
            }
            @Override
            public Object caseJoinNode(JoinNode object) {
                return createJoinNodeAdapter();
            }
            @Override
            public Object caseUnionNode(UnionNode object) {
                return createUnionNodeAdapter();
            }
            @Override
            public Object caseProjectionNode(ProjectionNode object) {
                return createProjectionNodeAdapter();
            }
            @Override
            public Object caseFilterNode(FilterNode object) {
                return createFilterNodeAdapter();
            }
            @Override
            public Object caseGroupingNode(GroupingNode object) {
                return createGroupingNodeAdapter();
            }
            @Override
            public Object caseDupRemovalNode(DupRemovalNode object) {
                return createDupRemovalNodeAdapter();
            }
            @Override
            public Object caseSortNode(SortNode object) {
                return createSortNodeAdapter();
            }
            @Override
            public Object caseSqlNode(SqlNode object) {
                return createSqlNodeAdapter();
            }
            @Override
            public Object caseExpressionOwner(ExpressionOwner object) {
                return createExpressionOwnerAdapter();
            }
            @Override
            public Object caseXQueryTransformationMappingRoot(XQueryTransformationMappingRoot object) {
                return createXQueryTransformationMappingRootAdapter();
            }
            @Override
            public Object caseXQueryTransformation(XQueryTransformation object) {
                return createXQueryTransformationAdapter();
            }
            @Override
            public Object caseMappingHelper(MappingHelper object) {
                return createMappingHelperAdapter();
            }
            @Override
            public Object caseMapping(Mapping object) {
                return createMappingAdapter();
            }
            @Override
            public Object caseMappingRoot(MappingRoot object) {
                return createMappingRootAdapter();
            }
            @Override
            public Object defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return (Adapter)modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.TransformationContainer <em>Container</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.TransformationContainer
     * @generated
     */
    public Adapter createTransformationContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.SqlTransformation <em>Sql Transformation</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.SqlTransformation
     * @generated
     */
    public Adapter createSqlTransformationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.TransformationMappingRoot <em>Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.TransformationMappingRoot
     * @generated
     */
    public Adapter createTransformationMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.FragmentMappingRoot <em>Fragment Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.FragmentMappingRoot
     * @generated
     */
    public Adapter createFragmentMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.TreeMappingRoot <em>Tree Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.TreeMappingRoot
     * @generated
     */
    public Adapter createTreeMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.MappingClass <em>Mapping Class</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.MappingClass
     * @generated
     */
    public Adapter createMappingClassAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.MappingClassColumn <em>Mapping Class Column</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.MappingClassColumn
     * @generated
     */
    public Adapter createMappingClassColumnAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.MappingClassObject <em>Mapping Class Object</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.MappingClassObject
     * @generated
     */
    public Adapter createMappingClassObjectAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.StagingTable <em>Staging Table</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.StagingTable
     * @generated
     */
    public Adapter createStagingTableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.MappingClassSet <em>Mapping Class Set</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.MappingClassSet
     * @generated
     */
    public Adapter createMappingClassSetAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.MappingClassSetContainer <em>Mapping Class Set Container</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.MappingClassSetContainer
     * @generated
     */
    public Adapter createMappingClassSetContainerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.InputParameter <em>Input Parameter</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.InputParameter
     * @generated
     */
    public Adapter createInputParameterAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.InputSet <em>Input Set</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.InputSet
     * @generated
     */
    public Adapter createInputSetAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.InputBinding <em>Input Binding</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.InputBinding
     * @generated
     */
    public Adapter createInputBindingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.DataFlowMappingRoot <em>Data Flow Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.DataFlowMappingRoot
     * @generated
     */
    public Adapter createDataFlowMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.DataFlowNode <em>Data Flow Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.DataFlowNode
     * @generated
     */
    public Adapter createDataFlowNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.DataFlowLink <em>Data Flow Link</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.DataFlowLink
     * @generated
     */
    public Adapter createDataFlowLinkAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.Expression <em>Expression</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.Expression
     * @generated
     */
    public Adapter createExpressionAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.TargetNode <em>Target Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.TargetNode
     * @generated
     */
    public Adapter createTargetNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.SourceNode <em>Source Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.SourceNode
     * @generated
     */
    public Adapter createSourceNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.AbstractOperationNode <em>Abstract Operation Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.AbstractOperationNode
     * @generated
     */
    public Adapter createAbstractOperationNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.OperationNodeGroup <em>Operation Node Group</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.OperationNodeGroup
     * @generated
     */
    public Adapter createOperationNodeGroupAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.OperationNode <em>Operation Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.OperationNode
     * @generated
     */
    public Adapter createOperationNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.JoinNode <em>Join Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.JoinNode
     * @generated
     */
    public Adapter createJoinNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.UnionNode <em>Union Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.UnionNode
     * @generated
     */
    public Adapter createUnionNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.ProjectionNode <em>Projection Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.ProjectionNode
     * @generated
     */
    public Adapter createProjectionNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.FilterNode <em>Filter Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.FilterNode
     * @generated
     */
    public Adapter createFilterNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.GroupingNode <em>Grouping Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.GroupingNode
     * @generated
     */
    public Adapter createGroupingNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.DupRemovalNode <em>Dup Removal Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.DupRemovalNode
     * @generated
     */
    public Adapter createDupRemovalNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.SortNode <em>Sort Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.SortNode
     * @generated
     */
    public Adapter createSortNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.SqlNode <em>Sql Node</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.SqlNode
     * @generated
     */
    public Adapter createSqlNodeAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.ExpressionOwner <em>Expression Owner</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.ExpressionOwner
     * @generated
     */
    public Adapter createExpressionOwnerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot <em>XQuery Transformation Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot
     * @generated
     */
    public Adapter createXQueryTransformationMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.XQueryTransformation <em>XQuery Transformation</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.XQueryTransformation
     * @generated
     */
    public Adapter createXQueryTransformationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.TransformationMapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.TransformationMapping
     * @generated
     */
    public Adapter createTransformationMappingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.SqlAlias <em>Sql Alias</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.SqlAlias
     * @generated
     */
    public Adapter createSqlAliasAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot <em>Sql Transformation Mapping Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot
     * @generated
     */
    public Adapter createSqlTransformationMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.mapping.MappingHelper <em>Helper</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.mapping.MappingHelper
     * @generated
     */
    public Adapter createMappingHelperAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.mapping.Mapping <em>Mapping</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.mapping.Mapping
     * @generated
     */
    public Adapter createMappingAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.emf.mapping.MappingRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.emf.mapping.MappingRoot
     * @generated
     */
    public Adapter createMappingRootAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //TransformationAdapterFactory
