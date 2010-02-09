/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.List;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingPackageImpl;
import com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl;
import com.metamatrix.metamodels.core.impl.CorePackageImpl;
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
import com.metamatrix.metamodels.transformation.JoinType;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassObject;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.MappingClassSetContainer;
import com.metamatrix.metamodels.transformation.OperationNode;
import com.metamatrix.metamodels.transformation.OperationNodeGroup;
import com.metamatrix.metamodels.transformation.ProjectionNode;
import com.metamatrix.metamodels.transformation.RecursionErrorMode;
import com.metamatrix.metamodels.transformation.SortDirection;
import com.metamatrix.metamodels.transformation.SortNode;
import com.metamatrix.metamodels.transformation.SourceNode;
import com.metamatrix.metamodels.transformation.SqlAlias;
import com.metamatrix.metamodels.transformation.SqlNode;
import com.metamatrix.metamodels.transformation.SqlTransformation;
import com.metamatrix.metamodels.transformation.SqlTransformationMappingRoot;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TargetNode;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationFactory;
import com.metamatrix.metamodels.transformation.TransformationMapping;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;
import com.metamatrix.metamodels.transformation.TreeMappingRoot;
import com.metamatrix.metamodels.transformation.UnionNode;
import com.metamatrix.metamodels.transformation.XQueryTransformation;
import com.metamatrix.metamodels.transformation.XQueryTransformationMappingRoot;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class TransformationPackageImpl extends EPackageImpl implements TransformationPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass transformationContainerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sqlTransformationEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass transformationMappingRootEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass fragmentMappingRootEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass treeMappingRootEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass mappingClassEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass mappingClassColumnEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass mappingClassObjectEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass stagingTableEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass mappingClassSetEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass mappingClassSetContainerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass inputParameterEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass inputSetEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass inputBindingEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass dataFlowMappingRootEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass dataFlowNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass dataFlowLinkEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass expressionEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass targetNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sourceNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass abstractOperationNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass operationNodeGroupEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass operationNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass joinNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass unionNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass projectionNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass filterNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass groupingNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass dupRemovalNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sortNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sqlNodeEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass expressionOwnerEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass xQueryTransformationMappingRootEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass xQueryTransformationEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum recursionErrorModeEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum joinTypeEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EEnum sortDirectionEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType listEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass transformationMappingEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sqlAliasEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sqlTransformationMappingRootEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
     * EPackage.Registry} by the package package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package, if one already exists. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private TransformationPackageImpl() {
        super(eNS_URI, TransformationFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends. Simple
     * dependencies are satisfied by calling this method on all dependent packages before doing anything else. This method drives
     * initialization for interdependent packages directly, in parallel with this package, itself.
     * <p>
     * Of this package and its interdependencies, all packages which have not yet been registered by their URI values are first
     * created and registered. The packages are then initialized in two steps: meta-model objects for all of the packages are
     * created before any are initialized, since one package's meta-model objects may refer to those of another.
     * <p>
     * Invocation of this method will not affect any packages that have already been initialized. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static TransformationPackage init() {
        if (isInited) return (TransformationPackage)EPackage.Registry.INSTANCE.getEPackage(TransformationPackage.eNS_URI);

        // Obtain or create and register package
        TransformationPackageImpl theTransformationPackage = (TransformationPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof TransformationPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new TransformationPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        CorePackageImpl.init();
        EcorePackageImpl.init();
        ExtensionPackageImpl.init();
        MappingPackageImpl.init();

        // Create package meta-data objects
        theTransformationPackage.createPackageContents();

        // Initialize created meta-data
        theTransformationPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theTransformationPackage.freeze();

        return theTransformationPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getTransformationContainer() {
        return transformationContainerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getTransformationContainer_TransformationMappings() {
        return (EReference)transformationContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSqlTransformation() {
        return sqlTransformationEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_SelectSql() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_InsertSql() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_UpdateSql() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_DeleteSql() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_InsertAllowed() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_UpdateAllowed() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_DeleteAllowed() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_OutputLocked() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_InsertSqlDefault() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_UpdateSqlDefault() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlTransformation_DeleteSqlDefault() {
        return (EAttribute)sqlTransformationEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSqlTransformation_Aliases() {
        return (EReference)sqlTransformationEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getTransformationMappingRoot() {
        return transformationMappingRootEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getTransformationMappingRoot_Target() {
        return (EReference)transformationMappingRootEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getFragmentMappingRoot() {
        return fragmentMappingRootEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getTreeMappingRoot() {
        return treeMappingRootEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getMappingClass() {
        return mappingClassEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getMappingClass_Recursive() {
        return (EAttribute)mappingClassEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getMappingClass_RecursionAllowed() {
        return (EAttribute)mappingClassEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getMappingClass_RecursionCriteria() {
        return (EAttribute)mappingClassEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getMappingClass_RecursionLimit() {
        return (EAttribute)mappingClassEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getMappingClass_RecursionLimitErrorMode() {
        return (EAttribute)mappingClassEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClass_Columns() {
        return (EReference)mappingClassEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClass_MappingClassSet() {
        return (EReference)mappingClassEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClass_InputSet() {
        return (EReference)mappingClassEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getMappingClassColumn() {
        return mappingClassColumnEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClassColumn_Type() {
        return (EReference)mappingClassColumnEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClassColumn_MappingClass() {
        return (EReference)mappingClassColumnEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getMappingClassObject() {
        return mappingClassObjectEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getMappingClassObject_Name() {
        return (EAttribute)mappingClassObjectEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getStagingTable() {
        return stagingTableEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getMappingClassSet() {
        return mappingClassSetEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClassSet_MappingClasses() {
        return (EReference)mappingClassSetEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClassSet_Target() {
        return (EReference)mappingClassSetEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClassSet_InputBinding() {
        return (EReference)mappingClassSetEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getMappingClassSetContainer() {
        return mappingClassSetContainerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMappingClassSetContainer_MappingClassSets() {
        return (EReference)mappingClassSetContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getInputParameter() {
        return inputParameterEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getInputParameter_Name() {
        return (EAttribute)inputParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputParameter_InputSet() {
        return (EReference)inputParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputParameter_Type() {
        return (EReference)inputParameterEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getInputSet() {
        return inputSetEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputSet_MappingClass() {
        return (EReference)inputSetEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputSet_InputParameters() {
        return (EReference)inputSetEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getInputBinding() {
        return inputBindingEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputBinding_MappingClassSet() {
        return (EReference)inputBindingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputBinding_InputParameter() {
        return (EReference)inputBindingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInputBinding_MappingClassColumn() {
        return (EReference)inputBindingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getDataFlowMappingRoot() {
        return dataFlowMappingRootEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getDataFlowMappingRoot_AllowsOptimization() {
        return (EAttribute)dataFlowMappingRootEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowMappingRoot_Nodes() {
        return (EReference)dataFlowMappingRootEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowMappingRoot_Links() {
        return (EReference)dataFlowMappingRootEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getDataFlowNode() {
        return dataFlowNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getDataFlowNode_Name() {
        return (EAttribute)dataFlowNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowNode_Owner() {
        return (EReference)dataFlowNodeEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowNode_InputLinks() {
        return (EReference)dataFlowNodeEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowNode_OutputLinks() {
        return (EReference)dataFlowNodeEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getDataFlowLink() {
        return dataFlowLinkEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowLink_OutputNode() {
        return (EReference)dataFlowLinkEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowLink_InputNode() {
        return (EReference)dataFlowLinkEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getDataFlowLink_Owner() {
        return (EReference)dataFlowLinkEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getExpression() {
        return expressionEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getExpression_Value() {
        return (EAttribute)expressionEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getExpression_Owner() {
        return (EReference)expressionEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getTargetNode() {
        return targetNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getTargetNode_Target() {
        return (EReference)targetNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSourceNode() {
        return sourceNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSourceNode_Source() {
        return (EReference)sourceNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getAbstractOperationNode() {
        return abstractOperationNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getAbstractOperationNode_NodeGroup() {
        return (EReference)abstractOperationNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getOperationNodeGroup() {
        return operationNodeGroupEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getOperationNodeGroup_Contents() {
        return (EReference)operationNodeGroupEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getOperationNode() {
        return operationNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getJoinNode() {
        return joinNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getJoinNode_Type() {
        return (EAttribute)joinNodeEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getUnionNode() {
        return unionNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getProjectionNode() {
        return projectionNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getFilterNode() {
        return filterNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getGroupingNode() {
        return groupingNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getDupRemovalNode() {
        return dupRemovalNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSortNode() {
        return sortNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSqlNode() {
        return sqlNodeEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getExpressionOwner() {
        return expressionOwnerEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getExpressionOwner_Expressions() {
        return (EReference)expressionOwnerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getXQueryTransformationMappingRoot() {
        return xQueryTransformationMappingRootEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getXQueryTransformation() {
        return xQueryTransformationEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getXQueryTransformation_Expression() {
        return (EAttribute)xQueryTransformationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EEnum getRecursionErrorMode() {
        return recursionErrorModeEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EEnum getJoinType() {
        return joinTypeEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EEnum getSortDirection() {
        return sortDirectionEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EDataType getList() {
        return listEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getTransformationMapping() {
        return transformationMappingEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSqlAlias() {
        return sqlAliasEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSqlAlias_Alias() {
        return (EAttribute)sqlAliasEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSqlAlias_AliasedObject() {
        return (EReference)sqlAliasEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSqlAlias_SqlTransformation() {
        return (EReference)sqlAliasEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSqlTransformationMappingRoot() {
        return sqlTransformationMappingRootEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public TransformationFactory getTransformationFactory() {
        return (TransformationFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        transformationContainerEClass = createEClass(TRANSFORMATION_CONTAINER);
        createEReference(transformationContainerEClass, TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS);

        sqlTransformationEClass = createEClass(SQL_TRANSFORMATION);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__SELECT_SQL);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__INSERT_SQL);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__UPDATE_SQL);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__DELETE_SQL);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__INSERT_ALLOWED);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__UPDATE_ALLOWED);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__DELETE_ALLOWED);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__OUTPUT_LOCKED);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__INSERT_SQL_DEFAULT);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__UPDATE_SQL_DEFAULT);
        createEAttribute(sqlTransformationEClass, SQL_TRANSFORMATION__DELETE_SQL_DEFAULT);
        createEReference(sqlTransformationEClass, SQL_TRANSFORMATION__ALIASES);

        transformationMappingRootEClass = createEClass(TRANSFORMATION_MAPPING_ROOT);
        createEReference(transformationMappingRootEClass, TRANSFORMATION_MAPPING_ROOT__TARGET);

        transformationMappingEClass = createEClass(TRANSFORMATION_MAPPING);

        sqlAliasEClass = createEClass(SQL_ALIAS);
        createEAttribute(sqlAliasEClass, SQL_ALIAS__ALIAS);
        createEReference(sqlAliasEClass, SQL_ALIAS__ALIASED_OBJECT);
        createEReference(sqlAliasEClass, SQL_ALIAS__SQL_TRANSFORMATION);

        sqlTransformationMappingRootEClass = createEClass(SQL_TRANSFORMATION_MAPPING_ROOT);

        fragmentMappingRootEClass = createEClass(FRAGMENT_MAPPING_ROOT);

        treeMappingRootEClass = createEClass(TREE_MAPPING_ROOT);

        mappingClassEClass = createEClass(MAPPING_CLASS);
        createEAttribute(mappingClassEClass, MAPPING_CLASS__RECURSIVE);
        createEAttribute(mappingClassEClass, MAPPING_CLASS__RECURSION_ALLOWED);
        createEAttribute(mappingClassEClass, MAPPING_CLASS__RECURSION_CRITERIA);
        createEAttribute(mappingClassEClass, MAPPING_CLASS__RECURSION_LIMIT);
        createEAttribute(mappingClassEClass, MAPPING_CLASS__RECURSION_LIMIT_ERROR_MODE);
        createEReference(mappingClassEClass, MAPPING_CLASS__COLUMNS);
        createEReference(mappingClassEClass, MAPPING_CLASS__MAPPING_CLASS_SET);
        createEReference(mappingClassEClass, MAPPING_CLASS__INPUT_SET);

        mappingClassColumnEClass = createEClass(MAPPING_CLASS_COLUMN);
        createEReference(mappingClassColumnEClass, MAPPING_CLASS_COLUMN__MAPPING_CLASS);
        createEReference(mappingClassColumnEClass, MAPPING_CLASS_COLUMN__TYPE);

        mappingClassObjectEClass = createEClass(MAPPING_CLASS_OBJECT);
        createEAttribute(mappingClassObjectEClass, MAPPING_CLASS_OBJECT__NAME);

        stagingTableEClass = createEClass(STAGING_TABLE);

        mappingClassSetEClass = createEClass(MAPPING_CLASS_SET);
        createEReference(mappingClassSetEClass, MAPPING_CLASS_SET__MAPPING_CLASSES);
        createEReference(mappingClassSetEClass, MAPPING_CLASS_SET__TARGET);
        createEReference(mappingClassSetEClass, MAPPING_CLASS_SET__INPUT_BINDING);

        mappingClassSetContainerEClass = createEClass(MAPPING_CLASS_SET_CONTAINER);
        createEReference(mappingClassSetContainerEClass, MAPPING_CLASS_SET_CONTAINER__MAPPING_CLASS_SETS);

        inputParameterEClass = createEClass(INPUT_PARAMETER);
        createEAttribute(inputParameterEClass, INPUT_PARAMETER__NAME);
        createEReference(inputParameterEClass, INPUT_PARAMETER__INPUT_SET);
        createEReference(inputParameterEClass, INPUT_PARAMETER__TYPE);

        inputSetEClass = createEClass(INPUT_SET);
        createEReference(inputSetEClass, INPUT_SET__MAPPING_CLASS);
        createEReference(inputSetEClass, INPUT_SET__INPUT_PARAMETERS);

        inputBindingEClass = createEClass(INPUT_BINDING);
        createEReference(inputBindingEClass, INPUT_BINDING__MAPPING_CLASS_SET);
        createEReference(inputBindingEClass, INPUT_BINDING__INPUT_PARAMETER);
        createEReference(inputBindingEClass, INPUT_BINDING__MAPPING_CLASS_COLUMN);

        dataFlowMappingRootEClass = createEClass(DATA_FLOW_MAPPING_ROOT);
        createEAttribute(dataFlowMappingRootEClass, DATA_FLOW_MAPPING_ROOT__ALLOWS_OPTIMIZATION);
        createEReference(dataFlowMappingRootEClass, DATA_FLOW_MAPPING_ROOT__NODES);
        createEReference(dataFlowMappingRootEClass, DATA_FLOW_MAPPING_ROOT__LINKS);

        dataFlowNodeEClass = createEClass(DATA_FLOW_NODE);
        createEAttribute(dataFlowNodeEClass, DATA_FLOW_NODE__NAME);
        createEReference(dataFlowNodeEClass, DATA_FLOW_NODE__OWNER);
        createEReference(dataFlowNodeEClass, DATA_FLOW_NODE__INPUT_LINKS);
        createEReference(dataFlowNodeEClass, DATA_FLOW_NODE__OUTPUT_LINKS);

        dataFlowLinkEClass = createEClass(DATA_FLOW_LINK);
        createEReference(dataFlowLinkEClass, DATA_FLOW_LINK__OUTPUT_NODE);
        createEReference(dataFlowLinkEClass, DATA_FLOW_LINK__INPUT_NODE);
        createEReference(dataFlowLinkEClass, DATA_FLOW_LINK__OWNER);

        expressionEClass = createEClass(EXPRESSION);
        createEAttribute(expressionEClass, EXPRESSION__VALUE);
        createEReference(expressionEClass, EXPRESSION__OWNER);

        targetNodeEClass = createEClass(TARGET_NODE);
        createEReference(targetNodeEClass, TARGET_NODE__TARGET);

        sourceNodeEClass = createEClass(SOURCE_NODE);
        createEReference(sourceNodeEClass, SOURCE_NODE__SOURCE);

        abstractOperationNodeEClass = createEClass(ABSTRACT_OPERATION_NODE);
        createEReference(abstractOperationNodeEClass, ABSTRACT_OPERATION_NODE__NODE_GROUP);

        operationNodeGroupEClass = createEClass(OPERATION_NODE_GROUP);
        createEReference(operationNodeGroupEClass, OPERATION_NODE_GROUP__CONTENTS);

        operationNodeEClass = createEClass(OPERATION_NODE);

        joinNodeEClass = createEClass(JOIN_NODE);
        createEAttribute(joinNodeEClass, JOIN_NODE__TYPE);

        unionNodeEClass = createEClass(UNION_NODE);

        projectionNodeEClass = createEClass(PROJECTION_NODE);

        filterNodeEClass = createEClass(FILTER_NODE);

        groupingNodeEClass = createEClass(GROUPING_NODE);

        dupRemovalNodeEClass = createEClass(DUP_REMOVAL_NODE);

        sortNodeEClass = createEClass(SORT_NODE);

        sqlNodeEClass = createEClass(SQL_NODE);

        expressionOwnerEClass = createEClass(EXPRESSION_OWNER);
        createEReference(expressionOwnerEClass, EXPRESSION_OWNER__EXPRESSIONS);

        xQueryTransformationMappingRootEClass = createEClass(XQUERY_TRANSFORMATION_MAPPING_ROOT);

        xQueryTransformationEClass = createEClass(XQUERY_TRANSFORMATION);
        createEAttribute(xQueryTransformationEClass, XQUERY_TRANSFORMATION__EXPRESSION);

        // Create enums
        recursionErrorModeEEnum = createEEnum(RECURSION_ERROR_MODE);
        joinTypeEEnum = createEEnum(JOIN_TYPE);
        sortDirectionEEnum = createEEnum(SORT_DIRECTION);

        // Create data types
        listEDataType = createEDataType(LIST);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any invocation
     * but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        MappingPackageImpl theMappingPackage = (MappingPackageImpl)EPackage.Registry.INSTANCE.getEPackage(MappingPackage.eNS_URI);
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Add supertypes to classes
        sqlTransformationEClass.getESuperTypes().add(theMappingPackage.getMappingHelper());
        transformationMappingRootEClass.getESuperTypes().add(theMappingPackage.getMappingRoot());
        transformationMappingEClass.getESuperTypes().add(theMappingPackage.getMapping());
        sqlTransformationMappingRootEClass.getESuperTypes().add(this.getTransformationMappingRoot());
        fragmentMappingRootEClass.getESuperTypes().add(this.getTransformationMappingRoot());
        treeMappingRootEClass.getESuperTypes().add(this.getTransformationMappingRoot());
        mappingClassEClass.getESuperTypes().add(this.getMappingClassObject());
        mappingClassColumnEClass.getESuperTypes().add(this.getMappingClassObject());
        stagingTableEClass.getESuperTypes().add(this.getMappingClass());
        dataFlowMappingRootEClass.getESuperTypes().add(this.getTransformationMappingRoot());
        targetNodeEClass.getESuperTypes().add(this.getDataFlowNode());
        sourceNodeEClass.getESuperTypes().add(this.getDataFlowNode());
        abstractOperationNodeEClass.getESuperTypes().add(this.getDataFlowNode());
        abstractOperationNodeEClass.getESuperTypes().add(this.getExpressionOwner());
        operationNodeGroupEClass.getESuperTypes().add(this.getAbstractOperationNode());
        operationNodeEClass.getESuperTypes().add(this.getAbstractOperationNode());
        joinNodeEClass.getESuperTypes().add(this.getOperationNode());
        unionNodeEClass.getESuperTypes().add(this.getOperationNode());
        projectionNodeEClass.getESuperTypes().add(this.getOperationNode());
        filterNodeEClass.getESuperTypes().add(this.getOperationNode());
        groupingNodeEClass.getESuperTypes().add(this.getOperationNode());
        dupRemovalNodeEClass.getESuperTypes().add(this.getOperationNode());
        sortNodeEClass.getESuperTypes().add(this.getOperationNode());
        sqlNodeEClass.getESuperTypes().add(this.getOperationNode());
        xQueryTransformationMappingRootEClass.getESuperTypes().add(this.getTransformationMappingRoot());
        xQueryTransformationEClass.getESuperTypes().add(theMappingPackage.getMappingHelper());

        // Initialize classes and features; add operations and parameters
        initEClass(transformationContainerEClass,
                   TransformationContainer.class,
                   "TransformationContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getTransformationContainer_TransformationMappings(),
                       this.getTransformationMappingRoot(),
                       null,
                       "transformationMappings", null, 0, -1, TransformationContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(sqlTransformationEClass,
                   SqlTransformation.class,
                   "SqlTransformation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getSqlTransformation_SelectSql(),
                       ecorePackage.getEString(),
                       "selectSql", null, 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSqlTransformation_InsertSql(),
                       ecorePackage.getEString(),
                       "insertSql", null, 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSqlTransformation_UpdateSql(),
                       ecorePackage.getEString(),
                       "updateSql", null, 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSqlTransformation_DeleteSql(),
                       ecorePackage.getEString(),
                       "deleteSql", null, 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSqlTransformation_InsertAllowed(),
                       ecorePackage.getEBoolean(),
                       "insertAllowed", "true", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSqlTransformation_UpdateAllowed(),
                       ecorePackage.getEBoolean(),
                       "updateAllowed", "true", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSqlTransformation_DeleteAllowed(),
                       ecorePackage.getEBoolean(),
                       "deleteAllowed", "true", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSqlTransformation_OutputLocked(),
                       ecorePackage.getEBoolean(),
                       "outputLocked", "false", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSqlTransformation_InsertSqlDefault(),
                       ecorePackage.getEBoolean(),
                       "insertSqlDefault", "true", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSqlTransformation_UpdateSqlDefault(),
                       ecorePackage.getEBoolean(),
                       "updateSqlDefault", "true", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSqlTransformation_DeleteSqlDefault(),
                       ecorePackage.getEBoolean(),
                       "deleteSqlDefault", "true", 0, 1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getSqlTransformation_Aliases(),
                       this.getSqlAlias(),
                       this.getSqlAlias_SqlTransformation(),
                       "aliases", null, 0, -1, SqlTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(transformationMappingRootEClass,
                   TransformationMappingRoot.class,
                   "TransformationMappingRoot", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getTransformationMappingRoot_Target(),
                       theEcorePackage.getEObject(),
                       null,
                       "target", null, 0, 1, TransformationMappingRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(transformationMappingEClass,
                   TransformationMapping.class,
                   "TransformationMapping", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(sqlAliasEClass, SqlAlias.class, "SqlAlias", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getSqlAlias_Alias(),
                       ecorePackage.getEString(),
                       "alias", null, 0, 1, SqlAlias.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSqlAlias_AliasedObject(),
                       theEcorePackage.getEObject(),
                       null,
                       "aliasedObject", null, 1, 1, SqlAlias.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSqlAlias_SqlTransformation(),
                       this.getSqlTransformation(),
                       this.getSqlTransformation_Aliases(),
                       "sqlTransformation", null, 0, 1, SqlAlias.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(sqlTransformationMappingRootEClass,
                   SqlTransformationMappingRoot.class,
                   "SqlTransformationMappingRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(fragmentMappingRootEClass,
                   FragmentMappingRoot.class,
                   "FragmentMappingRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(treeMappingRootEClass,
                   TreeMappingRoot.class,
                   "TreeMappingRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(mappingClassEClass,
                   MappingClass.class,
                   "MappingClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getMappingClass_Recursive(),
                       ecorePackage.getEBoolean(),
                       "recursive", "false", 0, 1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getMappingClass_RecursionAllowed(),
                       ecorePackage.getEBoolean(),
                       "recursionAllowed", "false", 0, 1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getMappingClass_RecursionCriteria(),
                       ecorePackage.getEString(),
                       "recursionCriteria", null, 0, 1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getMappingClass_RecursionLimit(),
                       ecorePackage.getEInt(),
                       "recursionLimit", "5", 0, 1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getMappingClass_RecursionLimitErrorMode(),
                       this.getRecursionErrorMode(),
                       "recursionLimitErrorMode", "THROW", 0, 1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getMappingClass_Columns(),
                       this.getMappingClassColumn(),
                       this.getMappingClassColumn_MappingClass(),
                       "columns", null, 1, -1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMappingClass_MappingClassSet(),
                       this.getMappingClassSet(),
                       this.getMappingClassSet_MappingClasses(),
                       "mappingClassSet", null, 0, 1, MappingClass.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMappingClass_InputSet(),
                       this.getInputSet(),
                       this.getInputSet_MappingClass(),
                       "inputSet", null, 0, 1, MappingClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(mappingClassColumnEClass,
                   MappingClassColumn.class,
                   "MappingClassColumn", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMappingClassColumn_MappingClass(),
                       this.getMappingClass(),
                       this.getMappingClass_Columns(),
                       "mappingClass", null, 1, 1, MappingClassColumn.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMappingClassColumn_Type(),
                       theEcorePackage.getEObject(),
                       null,
                       "type", null, 1, 1, MappingClassColumn.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(mappingClassObjectEClass,
                   MappingClassObject.class,
                   "MappingClassObject", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getMappingClassObject_Name(),
                       ecorePackage.getEString(),
                       "name", null, 0, 1, MappingClassObject.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(stagingTableEClass,
                   StagingTable.class,
                   "StagingTable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(mappingClassSetEClass,
                   MappingClassSet.class,
                   "MappingClassSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMappingClassSet_MappingClasses(),
                       this.getMappingClass(),
                       this.getMappingClass_MappingClassSet(),
                       "mappingClasses", null, 0, -1, MappingClassSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMappingClassSet_Target(),
                       theEcorePackage.getEObject(),
                       null,
                       "target", null, 0, 1, MappingClassSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMappingClassSet_InputBinding(),
                       this.getInputBinding(),
                       this.getInputBinding_MappingClassSet(),
                       "inputBinding", null, 0, -1, MappingClassSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(mappingClassSetContainerEClass,
                   MappingClassSetContainer.class,
                   "MappingClassSetContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMappingClassSetContainer_MappingClassSets(),
                       this.getMappingClassSet(),
                       null,
                       "mappingClassSets", null, 0, -1, MappingClassSetContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(inputParameterEClass,
                   InputParameter.class,
                   "InputParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getInputParameter_Name(),
                       ecorePackage.getEString(),
                       "name", null, 0, 1, InputParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getInputParameter_InputSet(),
                       this.getInputSet(),
                       this.getInputSet_InputParameters(),
                       "inputSet", null, 1, 1, InputParameter.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getInputParameter_Type(),
                       theEcorePackage.getEObject(),
                       null,
                       "type", null, 1, 1, InputParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(inputSetEClass, InputSet.class, "InputSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getInputSet_MappingClass(),
                       this.getMappingClass(),
                       this.getMappingClass_InputSet(),
                       "mappingClass", null, 1, 1, InputSet.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getInputSet_InputParameters(),
                       this.getInputParameter(),
                       this.getInputParameter_InputSet(),
                       "inputParameters", null, 0, -1, InputSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(inputBindingEClass,
                   InputBinding.class,
                   "InputBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getInputBinding_MappingClassSet(),
                       this.getMappingClassSet(),
                       this.getMappingClassSet_InputBinding(),
                       "mappingClassSet", null, 1, 1, InputBinding.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getInputBinding_InputParameter(),
                       this.getInputParameter(),
                       null,
                       "inputParameter", null, 1, 1, InputBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getInputBinding_MappingClassColumn(),
                       this.getMappingClassColumn(),
                       null,
                       "mappingClassColumn", null, 1, 1, InputBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(dataFlowMappingRootEClass,
                   DataFlowMappingRoot.class,
                   "DataFlowMappingRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getDataFlowMappingRoot_AllowsOptimization(),
                       ecorePackage.getEBoolean(),
                       "allowsOptimization", "false", 0, 1, DataFlowMappingRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getDataFlowMappingRoot_Nodes(),
                       this.getDataFlowNode(),
                       this.getDataFlowNode_Owner(),
                       "nodes", null, 1, -1, DataFlowMappingRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDataFlowMappingRoot_Links(),
                       this.getDataFlowLink(),
                       this.getDataFlowLink_Owner(),
                       "links", null, 1, -1, DataFlowMappingRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(dataFlowMappingRootEClass, this.getList(), "getSourceNodes"); //$NON-NLS-1$

        addEOperation(dataFlowMappingRootEClass, this.getList(), "getTargetNodes"); //$NON-NLS-1$

        addEOperation(dataFlowMappingRootEClass, ecorePackage.getEString(), "getResultantSql"); //$NON-NLS-1$

        initEClass(dataFlowNodeEClass,
                   DataFlowNode.class,
                   "DataFlowNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getDataFlowNode_Name(),
                       ecorePackage.getEString(),
                       "name", null, 0, 1, DataFlowNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDataFlowNode_Owner(),
                       this.getDataFlowMappingRoot(),
                       this.getDataFlowMappingRoot_Nodes(),
                       "owner", null, 0, 1, DataFlowNode.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDataFlowNode_InputLinks(),
                       this.getDataFlowLink(),
                       this.getDataFlowLink_OutputNode(),
                       "inputLinks", null, 0, -1, DataFlowNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDataFlowNode_OutputLinks(),
                       this.getDataFlowLink(),
                       this.getDataFlowLink_InputNode(),
                       "outputLinks", null, 0, -1, DataFlowNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(dataFlowNodeEClass, this.getList(), "getInputNodes"); //$NON-NLS-1$

        addEOperation(dataFlowNodeEClass, this.getList(), "getOutputNodes"); //$NON-NLS-1$

        addEOperation(dataFlowNodeEClass, this.getList(), "getProjectedSymbols"); //$NON-NLS-1$

        addEOperation(dataFlowNodeEClass, ecorePackage.getEString(), "getSqlString"); //$NON-NLS-1$

        initEClass(dataFlowLinkEClass,
                   DataFlowLink.class,
                   "DataFlowLink", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getDataFlowLink_OutputNode(),
                       this.getDataFlowNode(),
                       this.getDataFlowNode_InputLinks(),
                       "outputNode", null, 1, 1, DataFlowLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDataFlowLink_InputNode(),
                       this.getDataFlowNode(),
                       this.getDataFlowNode_OutputLinks(),
                       "inputNode", null, 1, 1, DataFlowLink.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDataFlowLink_Owner(),
                       this.getDataFlowMappingRoot(),
                       this.getDataFlowMappingRoot_Links(),
                       "owner", null, 0, 1, DataFlowLink.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(expressionEClass, Expression.class, "Expression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getExpression_Value(),
                       ecorePackage.getEString(),
                       "value", null, 0, 1, Expression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getExpression_Owner(),
                       this.getExpressionOwner(),
                       this.getExpressionOwner_Expressions(),
                       "owner", null, 0, 1, Expression.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(targetNodeEClass, TargetNode.class, "TargetNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getTargetNode_Target(),
                       theEcorePackage.getEObject(),
                       null,
                       "target", null, 0, 1, TargetNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(sourceNodeEClass, SourceNode.class, "SourceNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSourceNode_Source(),
                       theEcorePackage.getEObject(),
                       null,
                       "source", null, 0, 1, SourceNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(abstractOperationNodeEClass,
                   AbstractOperationNode.class,
                   "AbstractOperationNode", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getAbstractOperationNode_NodeGroup(),
                       this.getOperationNodeGroup(),
                       this.getOperationNodeGroup_Contents(),
                       "nodeGroup", null, 0, 1, AbstractOperationNode.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(operationNodeGroupEClass,
                   OperationNodeGroup.class,
                   "OperationNodeGroup", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getOperationNodeGroup_Contents(),
                       this.getAbstractOperationNode(),
                       this.getAbstractOperationNode_NodeGroup(),
                       "contents", null, 0, -1, OperationNodeGroup.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(operationNodeGroupEClass, this.getList(), "getAllContents"); //$NON-NLS-1$

        initEClass(operationNodeEClass,
                   OperationNode.class,
                   "OperationNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(operationNodeEClass, ecorePackage.getEInt(), "getMinInputs"); //$NON-NLS-1$

        addEOperation(operationNodeEClass, ecorePackage.getEInt(), "getMaxInputs"); //$NON-NLS-1$

        addEOperation(operationNodeEClass, ecorePackage.getEInt(), "getMinOutputs"); //$NON-NLS-1$

        addEOperation(operationNodeEClass, ecorePackage.getEInt(), "getMaxOutputs"); //$NON-NLS-1$

        initEClass(joinNodeEClass, JoinNode.class, "JoinNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getJoinNode_Type(),
                       this.getJoinType(),
                       "type", null, 0, 1, JoinNode.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(joinNodeEClass, ecorePackage.getEString(), "getCriteria"); //$NON-NLS-1$

        initEClass(unionNodeEClass, UnionNode.class, "UnionNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(projectionNodeEClass,
                   ProjectionNode.class,
                   "ProjectionNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(filterNodeEClass, FilterNode.class, "FilterNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(filterNodeEClass, ecorePackage.getEString(), "getCriteria"); //$NON-NLS-1$

        initEClass(groupingNodeEClass,
                   GroupingNode.class,
                   "GroupingNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(groupingNodeEClass, ecorePackage.getEString(), "getGroupingColumns"); //$NON-NLS-1$

        initEClass(dupRemovalNodeEClass,
                   DupRemovalNode.class,
                   "DupRemovalNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(dupRemovalNodeEClass, ecorePackage.getEString(), "getDistinct"); //$NON-NLS-1$

        initEClass(sortNodeEClass, SortNode.class, "SortNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        addEOperation(sortNodeEClass, ecorePackage.getEString(), "getOrderBy"); //$NON-NLS-1$

        initEClass(sqlNodeEClass, SqlNode.class, "SqlNode", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(expressionOwnerEClass,
                   ExpressionOwner.class,
                   "ExpressionOwner", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getExpressionOwner_Expressions(),
                       this.getExpression(),
                       this.getExpression_Owner(),
                       "expressions", null, 1, -1, ExpressionOwner.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xQueryTransformationMappingRootEClass,
                   XQueryTransformationMappingRoot.class,
                   "XQueryTransformationMappingRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(xQueryTransformationEClass,
                   XQueryTransformation.class,
                   "XQueryTransformation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXQueryTransformation_Expression(),
                       ecorePackage.getEString(),
                       "expression", null, 0, 1, XQueryTransformation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(recursionErrorModeEEnum, RecursionErrorMode.class, "RecursionErrorMode"); //$NON-NLS-1$
        addEEnumLiteral(recursionErrorModeEEnum, RecursionErrorMode.THROW_LITERAL);
        addEEnumLiteral(recursionErrorModeEEnum, RecursionErrorMode.RECORD_LITERAL);
        addEEnumLiteral(recursionErrorModeEEnum, RecursionErrorMode.DISCARD_LITERAL);

        initEEnum(joinTypeEEnum, JoinType.class, "JoinType"); //$NON-NLS-1$
        addEEnumLiteral(joinTypeEEnum, JoinType.INNER_LITERAL);
        addEEnumLiteral(joinTypeEEnum, JoinType.LEFT_OUTER_LITERAL);
        addEEnumLiteral(joinTypeEEnum, JoinType.RIGHT_OUTER_LITERAL);
        addEEnumLiteral(joinTypeEEnum, JoinType.FULL_OUTER_LITERAL);
        addEEnumLiteral(joinTypeEEnum, JoinType.CROSS_LITERAL);

        initEEnum(sortDirectionEEnum, SortDirection.class, "SortDirection"); //$NON-NLS-1$
        addEEnumLiteral(sortDirectionEEnum, SortDirection.ASCENDING_LITERAL);
        addEEnumLiteral(sortDirectionEEnum, SortDirection.DESCENDING_LITERAL);

        // Initialize data types
        initEDataType(listEDataType, List.class, "List", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} // TransformationPackageImpl
