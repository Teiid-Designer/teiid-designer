/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.transformation.impl;

import java.util.List;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.mapping.domain.AdapterFactoryMappingDomain;
import org.eclipse.emf.mapping.domain.MappingDomain;
import org.eclipse.emf.mapping.domain.PluginAdapterFactoryMappingDomain;
import org.eclipse.emf.mapping.provider.MappingItemProviderAdapterFactory;
import org.teiid.designer.metamodels.transformation.DataFlowLink;
import org.teiid.designer.metamodels.transformation.DataFlowMappingRoot;
import org.teiid.designer.metamodels.transformation.DataFlowNode;
import org.teiid.designer.metamodels.transformation.DupRemovalNode;
import org.teiid.designer.metamodels.transformation.Expression;
import org.teiid.designer.metamodels.transformation.FilterNode;
import org.teiid.designer.metamodels.transformation.FragmentMappingRoot;
import org.teiid.designer.metamodels.transformation.GroupingNode;
import org.teiid.designer.metamodels.transformation.InputBinding;
import org.teiid.designer.metamodels.transformation.InputParameter;
import org.teiid.designer.metamodels.transformation.InputSet;
import org.teiid.designer.metamodels.transformation.JoinNode;
import org.teiid.designer.metamodels.transformation.JoinType;
import org.teiid.designer.metamodels.transformation.MappingClass;
import org.teiid.designer.metamodels.transformation.MappingClassColumn;
import org.teiid.designer.metamodels.transformation.MappingClassSet;
import org.teiid.designer.metamodels.transformation.MappingClassSetContainer;
import org.teiid.designer.metamodels.transformation.OperationNode;
import org.teiid.designer.metamodels.transformation.OperationNodeGroup;
import org.teiid.designer.metamodels.transformation.ProjectionNode;
import org.teiid.designer.metamodels.transformation.RecursionErrorMode;
import org.teiid.designer.metamodels.transformation.SortDirection;
import org.teiid.designer.metamodels.transformation.SortNode;
import org.teiid.designer.metamodels.transformation.SourceNode;
import org.teiid.designer.metamodels.transformation.SqlAlias;
import org.teiid.designer.metamodels.transformation.SqlNode;
import org.teiid.designer.metamodels.transformation.SqlTransformation;
import org.teiid.designer.metamodels.transformation.SqlTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.StagingTable;
import org.teiid.designer.metamodels.transformation.TargetNode;
import org.teiid.designer.metamodels.transformation.TransformationContainer;
import org.teiid.designer.metamodels.transformation.TransformationFactory;
import org.teiid.designer.metamodels.transformation.TransformationMapping;
import org.teiid.designer.metamodels.transformation.TransformationPackage;
import org.teiid.designer.metamodels.transformation.TreeMappingRoot;
import org.teiid.designer.metamodels.transformation.UnionNode;
import org.teiid.designer.metamodels.transformation.XQueryTransformation;
import org.teiid.designer.metamodels.transformation.XQueryTransformationMappingRoot;
import org.teiid.designer.metamodels.transformation.provider.TransformationItemProviderAdapterFactory;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class TransformationFactoryImpl extends EFactoryImpl implements TransformationFactory {

    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public TransformationFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public EObject create( EClass eClass ) {
        switch (eClass.getClassifierID()) {
            case TransformationPackage.TRANSFORMATION_CONTAINER:
                return createTransformationContainer();
            case TransformationPackage.SQL_TRANSFORMATION:
                return createSqlTransformation();
            case TransformationPackage.TRANSFORMATION_MAPPING:
                return createTransformationMapping();
            case TransformationPackage.SQL_ALIAS:
                return createSqlAlias();
            case TransformationPackage.SQL_TRANSFORMATION_MAPPING_ROOT:
                return createSqlTransformationMappingRoot();
            case TransformationPackage.FRAGMENT_MAPPING_ROOT:
                return createFragmentMappingRoot();
            case TransformationPackage.TREE_MAPPING_ROOT:
                return createTreeMappingRoot();
            case TransformationPackage.MAPPING_CLASS:
                return createMappingClass();
            case TransformationPackage.MAPPING_CLASS_COLUMN:
                return createMappingClassColumn();
            case TransformationPackage.STAGING_TABLE:
                return createStagingTable();
            case TransformationPackage.MAPPING_CLASS_SET:
                return createMappingClassSet();
            case TransformationPackage.MAPPING_CLASS_SET_CONTAINER:
                return createMappingClassSetContainer();
            case TransformationPackage.INPUT_PARAMETER:
                return createInputParameter();
            case TransformationPackage.INPUT_SET:
                return createInputSet();
            case TransformationPackage.INPUT_BINDING:
                return createInputBinding();
            case TransformationPackage.DATA_FLOW_MAPPING_ROOT:
                return createDataFlowMappingRoot();
            case TransformationPackage.DATA_FLOW_NODE:
                return createDataFlowNode();
            case TransformationPackage.DATA_FLOW_LINK:
                return createDataFlowLink();
            case TransformationPackage.EXPRESSION:
                return createExpression();
            case TransformationPackage.TARGET_NODE:
                return createTargetNode();
            case TransformationPackage.SOURCE_NODE:
                return createSourceNode();
            case TransformationPackage.OPERATION_NODE_GROUP:
                return createOperationNodeGroup();
            case TransformationPackage.OPERATION_NODE:
                return createOperationNode();
            case TransformationPackage.JOIN_NODE:
                return createJoinNode();
            case TransformationPackage.UNION_NODE:
                return createUnionNode();
            case TransformationPackage.PROJECTION_NODE:
                return createProjectionNode();
            case TransformationPackage.FILTER_NODE:
                return createFilterNode();
            case TransformationPackage.GROUPING_NODE:
                return createGroupingNode();
            case TransformationPackage.DUP_REMOVAL_NODE:
                return createDupRemovalNode();
            case TransformationPackage.SORT_NODE:
                return createSortNode();
            case TransformationPackage.SQL_NODE:
                return createSqlNode();
            case TransformationPackage.XQUERY_TRANSFORMATION_MAPPING_ROOT:
                return createXQueryTransformationMappingRoot();
            case TransformationPackage.XQUERY_TRANSFORMATION:
                return createXQueryTransformation();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object createFromString( EDataType eDataType,
                                    String initialValue ) {
        switch (eDataType.getClassifierID()) {
            case TransformationPackage.RECURSION_ERROR_MODE: {
                RecursionErrorMode result = RecursionErrorMode.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case TransformationPackage.JOIN_TYPE: {
                JoinType result = JoinType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case TransformationPackage.SORT_DIRECTION: {
                SortDirection result = SortDirection.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case TransformationPackage.LIST:
                return createListFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String convertToString( EDataType eDataType,
                                   Object instanceValue ) {
        switch (eDataType.getClassifierID()) {
            case TransformationPackage.RECURSION_ERROR_MODE:
                return instanceValue == null ? null : instanceValue.toString();
            case TransformationPackage.JOIN_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case TransformationPackage.SORT_DIRECTION:
                return instanceValue == null ? null : instanceValue.toString();
            case TransformationPackage.LIST:
                return convertListToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public TransformationContainer createTransformationContainer() {
        TransformationContainerImpl transformationContainer = new TransformationContainerImpl();
        return transformationContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SqlTransformation createSqlTransformation() {
        SqlTransformationImpl sqlTransformation = new SqlTransformationImpl();
        return sqlTransformation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
	public FragmentMappingRoot createFragmentMappingRoot() {
        FragmentMappingRootImpl fragmentMappingRoot = new FragmentMappingRootImpl();
        fragmentMappingRoot.setDomain(createDefaultMappingDomain());
        return fragmentMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public FragmentMappingRoot createFragmentMappingRootGen() { // NO_UCD
        FragmentMappingRootImpl fragmentMappingRoot = new FragmentMappingRootImpl();
        return fragmentMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
	public TreeMappingRoot createTreeMappingRoot() {
        TreeMappingRootImpl treeMappingRoot = new TreeMappingRootImpl();
        treeMappingRoot.setDomain(createDefaultMappingDomain());
        return treeMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public TreeMappingRoot createTreeMappingRootGen() { // NO_UCD
        TreeMappingRootImpl treeMappingRoot = new TreeMappingRootImpl();
        return treeMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public MappingClass createMappingClass() {
        MappingClassImpl mappingClass = new MappingClassImpl();
        return mappingClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public MappingClassColumn createMappingClassColumn() {
        MappingClassColumnImpl mappingClassColumn = new MappingClassColumnImpl();
        return mappingClassColumn;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public StagingTable createStagingTable() {
        StagingTableImpl stagingTable = new StagingTableImpl();
        return stagingTable;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public MappingClassSet createMappingClassSet() {
        MappingClassSetImpl mappingClassSet = new MappingClassSetImpl();
        return mappingClassSet;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public MappingClassSetContainer createMappingClassSetContainer() {
        MappingClassSetContainerImpl mappingClassSetContainer = new MappingClassSetContainerImpl();
        return mappingClassSetContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public InputParameter createInputParameter() {
        InputParameterImpl inputParameter = new InputParameterImpl();
        return inputParameter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public InputSet createInputSet() {
        InputSetImpl inputSet = new InputSetImpl();
        return inputSet;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public InputBinding createInputBinding() {
        InputBindingImpl inputBinding = new InputBindingImpl();
        return inputBinding;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public DataFlowMappingRoot createDataFlowMappingRoot() {
        DataFlowMappingRootImpl dataFlowMappingRoot = new DataFlowMappingRootImpl();
        return dataFlowMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public DataFlowNode createDataFlowNode() {
        DataFlowNodeImpl dataFlowNode = new DataFlowNodeImpl();
        return dataFlowNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public DataFlowLink createDataFlowLink() {
        DataFlowLinkImpl dataFlowLink = new DataFlowLinkImpl();
        return dataFlowLink;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Expression createExpression() {
        ExpressionImpl expression = new ExpressionImpl();
        return expression;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public TargetNode createTargetNode() {
        TargetNodeImpl targetNode = new TargetNodeImpl();
        return targetNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SourceNode createSourceNode() {
        SourceNodeImpl sourceNode = new SourceNodeImpl();
        return sourceNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public OperationNodeGroup createOperationNodeGroup() {
        OperationNodeGroupImpl operationNodeGroup = new OperationNodeGroupImpl();
        return operationNodeGroup;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public OperationNode createOperationNode() {
        OperationNodeImpl operationNode = new OperationNodeImpl();
        return operationNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public JoinNode createJoinNode() {
        JoinNodeImpl joinNode = new JoinNodeImpl();
        return joinNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public UnionNode createUnionNode() {
        UnionNodeImpl unionNode = new UnionNodeImpl();
        return unionNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ProjectionNode createProjectionNode() {
        ProjectionNodeImpl projectionNode = new ProjectionNodeImpl();
        return projectionNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public FilterNode createFilterNode() {
        FilterNodeImpl filterNode = new FilterNodeImpl();
        return filterNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public GroupingNode createGroupingNode() {
        GroupingNodeImpl groupingNode = new GroupingNodeImpl();
        return groupingNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public DupRemovalNode createDupRemovalNode() {
        DupRemovalNodeImpl dupRemovalNode = new DupRemovalNodeImpl();
        return dupRemovalNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SortNode createSortNode() {
        SortNodeImpl sortNode = new SortNodeImpl();
        return sortNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SqlNode createSqlNode() {
        SqlNodeImpl sqlNode = new SqlNodeImpl();
        return sqlNode;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XQueryTransformationMappingRoot createXQueryTransformationMappingRoot() {
        XQueryTransformationMappingRootImpl xQueryTransformationMappingRoot = new XQueryTransformationMappingRootImpl();
        return xQueryTransformationMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public XQueryTransformation createXQueryTransformation() {
        XQueryTransformationImpl xQueryTransformation = new XQueryTransformationImpl();
        return xQueryTransformation;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List createListFromString( EDataType eDataType,
                                      String initialValue ) {
        return (List)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String convertListToString( EDataType eDataType,
                                       Object instanceValue ) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public TransformationMapping createTransformationMapping() {
        TransformationMappingImpl transformationMapping = new TransformationMappingImpl();
        return transformationMapping;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public SqlAlias createSqlAlias() {
        SqlAliasImpl sqlAlias = new SqlAliasImpl();
        return sqlAlias;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
	public SqlTransformationMappingRoot createSqlTransformationMappingRoot() {
        SqlTransformationMappingRootImpl sqlTransformationMappingRoot = new SqlTransformationMappingRootImpl();
        sqlTransformationMappingRoot.setDomain(createDefaultMappingDomain());
        return sqlTransformationMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public SqlTransformationMappingRoot createSqlTransformationMappingRootGen() { // NO_UCD
        SqlTransformationMappingRootImpl sqlTransformationMappingRoot = new SqlTransformationMappingRootImpl();
        return sqlTransformationMappingRoot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public TransformationPackage getTransformationPackage() {
        return (TransformationPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static TransformationPackage getPackage() { // NO_UCD
        return TransformationPackage.eINSTANCE;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    protected MappingDomain createDefaultMappingDomain() {
        // BML 5/9/07 - We were newing up Two of these darned things, the second one is for a "test case???"
        // So let's just new ONE up here and use for the ComposedAdapterFactory AND the MappingDomain
        TransformationItemProviderAdapterFactory tFactory = new TransformationItemProviderAdapterFactory();

        AdapterFactory mappingAdapterFactory = new ComposedAdapterFactory(new AdapterFactory[] {
            new ResourceItemProviderAdapterFactory(), new MappingItemProviderAdapterFactory(), tFactory});

        // Create the command stack
        CommandStack commandStack = new BasicCommandStack();

        // This is a test case for cross domain code.
        // It creates two instances of the factory.
        AdapterFactoryMappingDomain mappingDomain = new PluginAdapterFactoryMappingDomain(mappingAdapterFactory, tFactory,
                                                                                          new EcoreItemProviderAdapterFactory(),
                                                                                          commandStack, null);
        // enable all flags
        mappingDomain.setMappingEnablementFlags(MappingDomain.ENABLE_ALL);

        return mappingDomain;
    }

} // TransformationFactoryImpl
