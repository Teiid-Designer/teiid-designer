/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;

import com.metamatrix.metamodels.core.extension.impl.ExtensionPackageImpl;
import com.metamatrix.metamodels.core.impl.CorePackageImpl;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.MultiplicityKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.ProcedureUpdateCount;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Relationship;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.SearchabilityType;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.View;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class RelationalPackageImpl extends EPackageImpl implements RelationalPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass tableEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass columnEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass schemaEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass primaryKeyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass foreignKeyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationalEntityEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass uniqueKeyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass viewEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass catalogEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass procedureEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass indexEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass procedureParameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass uniqueConstraintEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass accessPatternEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass relationshipEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass logicalRelationshipEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass logicalRelationshipEndEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass baseTableEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass columnSetEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass procedureResultEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum nullableTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum directionKindEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum multiplicityKindEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum searchabilityTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum procedureUpdateCountEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.relational.RelationalPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private RelationalPackageImpl() {
        super(eNS_URI, RelationalFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static RelationalPackage init() {
        if (isInited) return (RelationalPackage)EPackage.Registry.INSTANCE.getEPackage(RelationalPackage.eNS_URI);

        // Obtain or create and register package
        RelationalPackageImpl theRelationalPackage = (RelationalPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof RelationalPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new RelationalPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        CorePackageImpl.init();
        EcorePackageImpl.init();
        ExtensionPackageImpl.init();

        // Create package meta-data objects
        theRelationalPackage.createPackageContents();

        // Initialize created meta-data
        theRelationalPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theRelationalPackage.freeze();

        return theRelationalPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTable() {
        return tableEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTable_System() {
        return (EAttribute)tableEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTable_Cardinality() {
        return (EAttribute)tableEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTable_SupportsUpdate() {
        return (EAttribute)tableEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getTable_Materialized() {
        return (EAttribute)tableEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTable_Schema() {
        return (EReference)tableEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTable_AccessPatterns() {
        return (EReference)tableEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTable_Catalog() {
        return (EReference)tableEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTable_LogicalRelationships() {
        return (EReference)tableEClass.getEStructuralFeatures().get(7);
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTable_MaterializedTable() {
        return (EReference)tableEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getColumn() {
        return columnEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_NativeType() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumn_Type() {
        return (EReference)columnEClass.getEStructuralFeatures().get(27);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Nullable() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_AutoIncremented() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_DefaultValue() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_MinimumValue() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_MaximumValue() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Format() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Length() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_FixedLength() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Scale() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Precision() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_CharacterSetName() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_CollationName() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Selectable() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Updateable() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(14);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_CaseSensitive() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(15);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Searchability() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(16);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Currency() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(17);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Radix() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(18);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_Signed() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(19);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_DistinctValueCount() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(20);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumn_NullValueCount() {
        return (EAttribute)columnEClass.getEStructuralFeatures().get(21);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumn_UniqueKeys() {
        return (EReference)columnEClass.getEStructuralFeatures().get(22);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumn_Indexes() {
        return (EReference)columnEClass.getEStructuralFeatures().get(23);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumn_ForeignKeys() {
        return (EReference)columnEClass.getEStructuralFeatures().get(24);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumn_AccessPatterns() {
        return (EReference)columnEClass.getEStructuralFeatures().get(25);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumn_Owner() {
        return (EReference)columnEClass.getEStructuralFeatures().get(26);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getSchema() {
        return schemaEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSchema_Tables() {
        return (EReference)schemaEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSchema_Catalog() {
        return (EReference)schemaEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSchema_Procedures() {
        return (EReference)schemaEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSchema_Indexes() {
        return (EReference)schemaEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSchema_LogicalRelationships() {
        return (EReference)schemaEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPrimaryKey() {
        return primaryKeyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getPrimaryKey_Table() {
        return (EReference)primaryKeyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getForeignKey() {
        return foreignKeyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getForeignKey_ForeignKeyMultiplicity() {
        return (EAttribute)foreignKeyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getForeignKey_PrimaryKeyMultiplicity() {
        return (EAttribute)foreignKeyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getForeignKey_Table() {
        return (EReference)foreignKeyEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getForeignKey_Columns() {
        return (EReference)foreignKeyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getForeignKey_UniqueKey() {
        return (EReference)foreignKeyEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationalEntity() {
        return relationalEntityEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationalEntity_Name() {
        return (EAttribute)relationalEntityEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRelationalEntity_NameInSource() {
        return (EAttribute)relationalEntityEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getUniqueKey() {
        return uniqueKeyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUniqueKey_Columns() {
        return (EReference)uniqueKeyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUniqueKey_ForeignKeys() {
        return (EReference)uniqueKeyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getView() {
        return viewEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCatalog() {
        return catalogEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCatalog_Schemas() {
        return (EReference)catalogEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCatalog_Procedures() {
        return (EReference)catalogEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCatalog_Indexes() {
        return (EReference)catalogEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCatalog_Tables() {
        return (EReference)catalogEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCatalog_LogicalRelationships() {
        return (EReference)catalogEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProcedure() {
        return procedureEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedure_Function() {
        return (EAttribute)procedureEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedure_Schema() {
        return (EReference)procedureEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedure_Parameters() {
        return (EReference)procedureEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedure_Catalog() {
        return (EReference)procedureEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedure_Result() {
        return (EReference)procedureEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedure_UpdateCount() {
        return (EAttribute)procedureEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getIndex() {
        return indexEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndex_FilterCondition() {
        return (EAttribute)indexEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndex_Nullable() {
        return (EAttribute)indexEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndex_AutoUpdate() {
        return (EAttribute)indexEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndex_Unique() {
        return (EAttribute)indexEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndex_Schema() {
        return (EReference)indexEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndex_Columns() {
        return (EReference)indexEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndex_Catalog() {
        return (EReference)indexEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProcedureParameter() {
        return procedureParameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_Direction() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_DefaultValue() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_NativeType() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_Length() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_Precision() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_Scale() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_Nullable() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getProcedureParameter_Radix() {
        return (EAttribute)procedureParameterEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedureParameter_Type() {
        return (EReference)procedureParameterEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedureParameter_Procedure() {
        return (EReference)procedureParameterEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getUniqueConstraint() {
        return uniqueConstraintEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getUniqueConstraint_Table() {
        return (EReference)uniqueConstraintEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAccessPattern() {
        return accessPatternEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAccessPattern_Columns() {
        return (EReference)accessPatternEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAccessPattern_Table() {
        return (EReference)accessPatternEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRelationship() {
        return relationshipEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLogicalRelationship() {
        return logicalRelationshipEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalRelationship_Catalog() {
        return (EReference)logicalRelationshipEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalRelationship_Schema() {
        return (EReference)logicalRelationshipEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalRelationship_Ends() {
        return (EReference)logicalRelationshipEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLogicalRelationshipEnd() {
        return logicalRelationshipEndEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getLogicalRelationshipEnd_Multiplicity() {
        return (EAttribute)logicalRelationshipEndEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalRelationshipEnd_Table() {
        return (EReference)logicalRelationshipEndEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getLogicalRelationshipEnd_Relationship() {
        return (EReference)logicalRelationshipEndEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getBaseTable() {
        return baseTableEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getBaseTable_ForeignKeys() {
        return (EReference)baseTableEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getBaseTable_PrimaryKey() {
        return (EReference)baseTableEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getBaseTable_UniqueConstraints() {
        return (EReference)baseTableEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getColumnSet() {
        return columnSetEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnSet_Columns() {
        return (EReference)columnSetEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getProcedureResult() {
        return procedureResultEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getProcedureResult_Procedure() {
        return (EReference)procedureResultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getNullableType() {
        return nullableTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getDirectionKind() {
        return directionKindEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getMultiplicityKind() {
        return multiplicityKindEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getSearchabilityType() {
        return searchabilityTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getProcedureUpdateCount() {
        return procedureUpdateCountEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationalFactory getRelationalFactory() {
        return (RelationalFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        tableEClass = createEClass(TABLE);
        createEAttribute(tableEClass, TABLE__SYSTEM);
        createEAttribute(tableEClass, TABLE__CARDINALITY);
        createEAttribute(tableEClass, TABLE__SUPPORTS_UPDATE);
        createEAttribute(tableEClass, TABLE__MATERIALIZED);
        createEReference(tableEClass, TABLE__SCHEMA);
        createEReference(tableEClass, TABLE__ACCESS_PATTERNS);
        createEReference(tableEClass, TABLE__CATALOG);
        createEReference(tableEClass, TABLE__LOGICAL_RELATIONSHIPS);
        createEReference(tableEClass, TABLE__MATERIALIZED_TABLE);

        columnEClass = createEClass(COLUMN);
        createEAttribute(columnEClass, COLUMN__NATIVE_TYPE);
        createEAttribute(columnEClass, COLUMN__LENGTH);
        createEAttribute(columnEClass, COLUMN__FIXED_LENGTH);
        createEAttribute(columnEClass, COLUMN__PRECISION);
        createEAttribute(columnEClass, COLUMN__SCALE);
        createEAttribute(columnEClass, COLUMN__NULLABLE);
        createEAttribute(columnEClass, COLUMN__AUTO_INCREMENTED);
        createEAttribute(columnEClass, COLUMN__DEFAULT_VALUE);
        createEAttribute(columnEClass, COLUMN__MINIMUM_VALUE);
        createEAttribute(columnEClass, COLUMN__MAXIMUM_VALUE);
        createEAttribute(columnEClass, COLUMN__FORMAT);
        createEAttribute(columnEClass, COLUMN__CHARACTER_SET_NAME);
        createEAttribute(columnEClass, COLUMN__COLLATION_NAME);
        createEAttribute(columnEClass, COLUMN__SELECTABLE);
        createEAttribute(columnEClass, COLUMN__UPDATEABLE);
        createEAttribute(columnEClass, COLUMN__CASE_SENSITIVE);
        createEAttribute(columnEClass, COLUMN__SEARCHABILITY);
        createEAttribute(columnEClass, COLUMN__CURRENCY);
        createEAttribute(columnEClass, COLUMN__RADIX);
        createEAttribute(columnEClass, COLUMN__SIGNED);
        createEAttribute(columnEClass, COLUMN__DISTINCT_VALUE_COUNT);
        createEAttribute(columnEClass, COLUMN__NULL_VALUE_COUNT);
        createEReference(columnEClass, COLUMN__UNIQUE_KEYS);
        createEReference(columnEClass, COLUMN__INDEXES);
        createEReference(columnEClass, COLUMN__FOREIGN_KEYS);
        createEReference(columnEClass, COLUMN__ACCESS_PATTERNS);
        createEReference(columnEClass, COLUMN__OWNER);
        createEReference(columnEClass, COLUMN__TYPE);

        schemaEClass = createEClass(SCHEMA);
        createEReference(schemaEClass, SCHEMA__TABLES);
        createEReference(schemaEClass, SCHEMA__CATALOG);
        createEReference(schemaEClass, SCHEMA__PROCEDURES);
        createEReference(schemaEClass, SCHEMA__INDEXES);
        createEReference(schemaEClass, SCHEMA__LOGICAL_RELATIONSHIPS);

        primaryKeyEClass = createEClass(PRIMARY_KEY);
        createEReference(primaryKeyEClass, PRIMARY_KEY__TABLE);

        foreignKeyEClass = createEClass(FOREIGN_KEY);
        createEAttribute(foreignKeyEClass, FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY);
        createEAttribute(foreignKeyEClass, FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY);
        createEReference(foreignKeyEClass, FOREIGN_KEY__COLUMNS);
        createEReference(foreignKeyEClass, FOREIGN_KEY__UNIQUE_KEY);
        createEReference(foreignKeyEClass, FOREIGN_KEY__TABLE);

        relationalEntityEClass = createEClass(RELATIONAL_ENTITY);
        createEAttribute(relationalEntityEClass, RELATIONAL_ENTITY__NAME);
        createEAttribute(relationalEntityEClass, RELATIONAL_ENTITY__NAME_IN_SOURCE);

        uniqueKeyEClass = createEClass(UNIQUE_KEY);
        createEReference(uniqueKeyEClass, UNIQUE_KEY__COLUMNS);
        createEReference(uniqueKeyEClass, UNIQUE_KEY__FOREIGN_KEYS);

        viewEClass = createEClass(VIEW);

        catalogEClass = createEClass(CATALOG);
        createEReference(catalogEClass, CATALOG__SCHEMAS);
        createEReference(catalogEClass, CATALOG__PROCEDURES);
        createEReference(catalogEClass, CATALOG__INDEXES);
        createEReference(catalogEClass, CATALOG__TABLES);
        createEReference(catalogEClass, CATALOG__LOGICAL_RELATIONSHIPS);

        procedureEClass = createEClass(PROCEDURE);
        createEAttribute(procedureEClass, PROCEDURE__FUNCTION);
        createEReference(procedureEClass, PROCEDURE__SCHEMA);
        createEReference(procedureEClass, PROCEDURE__PARAMETERS);
        createEReference(procedureEClass, PROCEDURE__CATALOG);
        createEReference(procedureEClass, PROCEDURE__RESULT);
        createEAttribute(procedureEClass, PROCEDURE__UPDATE_COUNT);

        indexEClass = createEClass(INDEX);
        createEAttribute(indexEClass, INDEX__FILTER_CONDITION);
        createEAttribute(indexEClass, INDEX__NULLABLE);
        createEAttribute(indexEClass, INDEX__AUTO_UPDATE);
        createEAttribute(indexEClass, INDEX__UNIQUE);
        createEReference(indexEClass, INDEX__SCHEMA);
        createEReference(indexEClass, INDEX__COLUMNS);
        createEReference(indexEClass, INDEX__CATALOG);

        procedureParameterEClass = createEClass(PROCEDURE_PARAMETER);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__DIRECTION);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__DEFAULT_VALUE);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__NATIVE_TYPE);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__LENGTH);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__PRECISION);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__SCALE);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__NULLABLE);
        createEAttribute(procedureParameterEClass, PROCEDURE_PARAMETER__RADIX);
        createEReference(procedureParameterEClass, PROCEDURE_PARAMETER__PROCEDURE);
        createEReference(procedureParameterEClass, PROCEDURE_PARAMETER__TYPE);

        uniqueConstraintEClass = createEClass(UNIQUE_CONSTRAINT);
        createEReference(uniqueConstraintEClass, UNIQUE_CONSTRAINT__TABLE);

        accessPatternEClass = createEClass(ACCESS_PATTERN);
        createEReference(accessPatternEClass, ACCESS_PATTERN__COLUMNS);
        createEReference(accessPatternEClass, ACCESS_PATTERN__TABLE);

        relationshipEClass = createEClass(RELATIONSHIP);

        logicalRelationshipEClass = createEClass(LOGICAL_RELATIONSHIP);
        createEReference(logicalRelationshipEClass, LOGICAL_RELATIONSHIP__CATALOG);
        createEReference(logicalRelationshipEClass, LOGICAL_RELATIONSHIP__SCHEMA);
        createEReference(logicalRelationshipEClass, LOGICAL_RELATIONSHIP__ENDS);

        logicalRelationshipEndEClass = createEClass(LOGICAL_RELATIONSHIP_END);
        createEAttribute(logicalRelationshipEndEClass, LOGICAL_RELATIONSHIP_END__MULTIPLICITY);
        createEReference(logicalRelationshipEndEClass, LOGICAL_RELATIONSHIP_END__TABLE);
        createEReference(logicalRelationshipEndEClass, LOGICAL_RELATIONSHIP_END__RELATIONSHIP);

        baseTableEClass = createEClass(BASE_TABLE);
        createEReference(baseTableEClass, BASE_TABLE__FOREIGN_KEYS);
        createEReference(baseTableEClass, BASE_TABLE__PRIMARY_KEY);
        createEReference(baseTableEClass, BASE_TABLE__UNIQUE_CONSTRAINTS);

        columnSetEClass = createEClass(COLUMN_SET);
        createEReference(columnSetEClass, COLUMN_SET__COLUMNS);

        procedureResultEClass = createEClass(PROCEDURE_RESULT);
        createEReference(procedureResultEClass, PROCEDURE_RESULT__PROCEDURE);

        // Create enums
        nullableTypeEEnum = createEEnum(NULLABLE_TYPE);
        directionKindEEnum = createEEnum(DIRECTION_KIND);
        multiplicityKindEEnum = createEEnum(MULTIPLICITY_KIND);
        searchabilityTypeEEnum = createEEnum(SEARCHABILITY_TYPE);
        procedureUpdateCountEEnum = createEEnum(PROCEDURE_UPDATE_COUNT);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Add supertypes to classes
        tableEClass.getESuperTypes().add(this.getColumnSet());
        columnEClass.getESuperTypes().add(this.getRelationalEntity());
        schemaEClass.getESuperTypes().add(this.getRelationalEntity());
        primaryKeyEClass.getESuperTypes().add(this.getUniqueKey());
        foreignKeyEClass.getESuperTypes().add(this.getRelationship());
        uniqueKeyEClass.getESuperTypes().add(this.getRelationalEntity());
        viewEClass.getESuperTypes().add(this.getTable());
        catalogEClass.getESuperTypes().add(this.getRelationalEntity());
        procedureEClass.getESuperTypes().add(this.getRelationalEntity());
        indexEClass.getESuperTypes().add(this.getRelationalEntity());
        procedureParameterEClass.getESuperTypes().add(this.getRelationalEntity());
        uniqueConstraintEClass.getESuperTypes().add(this.getUniqueKey());
        accessPatternEClass.getESuperTypes().add(this.getRelationalEntity());
        relationshipEClass.getESuperTypes().add(this.getRelationalEntity());
        logicalRelationshipEClass.getESuperTypes().add(this.getRelationship());
        logicalRelationshipEndEClass.getESuperTypes().add(this.getRelationalEntity());
        baseTableEClass.getESuperTypes().add(this.getTable());
        columnSetEClass.getESuperTypes().add(this.getRelationalEntity());
        procedureResultEClass.getESuperTypes().add(this.getColumnSet());

        // Initialize classes and features; add operations and parameters
        initEClass(tableEClass, Table.class, "Table", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getTable_System(), ecorePackage.getEBoolean(), "system", "false", 0, 1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getTable_Cardinality(), ecorePackage.getEInt(), "cardinality", null, 0, 1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getTable_SupportsUpdate(), ecorePackage.getEBoolean(), "supportsUpdate", "true", 0, 1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getTable_Materialized(), ecorePackage.getEBoolean(), "materialized", "false", 0, 1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getTable_Schema(), this.getSchema(), this.getSchema_Tables(), "schema", null, 0, 1, Table.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getTable_AccessPatterns(), this.getAccessPattern(), this.getAccessPattern_Table(), "accessPatterns", null, 0, -1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getTable_Catalog(), this.getCatalog(), this.getCatalog_Tables(), "catalog", null, 0, 1, Table.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getTable_LogicalRelationships(), this.getLogicalRelationshipEnd(), this.getLogicalRelationshipEnd_Table(), "logicalRelationships", null, 0, -1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getTable_MaterializedTable(), this.getTable(), null, "materializedTable", null, 0, 1, Table.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        
        initEClass(columnEClass, Column.class, "Column", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getColumn_NativeType(), ecorePackage.getEString(), "nativeType", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Length(), ecorePackage.getEInt(), "length", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_FixedLength(), ecorePackage.getEBoolean(), "fixedLength", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Precision(), ecorePackage.getEInt(), "precision", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Scale(), ecorePackage.getEInt(), "scale", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Nullable(), this.getNullableType(), "nullable", "NULLABLE", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_AutoIncremented(), ecorePackage.getEBoolean(), "autoIncremented", "false", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_MinimumValue(), ecorePackage.getEString(), "minimumValue", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_MaximumValue(), ecorePackage.getEString(), "maximumValue", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Format(), ecorePackage.getEString(), "format", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_CharacterSetName(), ecorePackage.getEString(), "characterSetName", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_CollationName(), ecorePackage.getEString(), "collationName", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Selectable(), ecorePackage.getEBoolean(), "selectable", "true", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_Updateable(), ecorePackage.getEBoolean(), "updateable", "true", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_CaseSensitive(), ecorePackage.getEBoolean(), "caseSensitive", "true", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_Searchability(), this.getSearchabilityType(), "searchability", "SEARCHABLE", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_Currency(), ecorePackage.getEBoolean(), "currency", null, 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getColumn_Radix(), ecorePackage.getEInt(), "radix", "10", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_Signed(), ecorePackage.getEBoolean(), "signed", "true", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_DistinctValueCount(), ecorePackage.getEInt(), "distinctValueCount", "-1", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getColumn_NullValueCount(), ecorePackage.getEInt(), "nullValueCount", "-1", 0, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getColumn_UniqueKeys(), this.getUniqueKey(), this.getUniqueKey_Columns(), "uniqueKeys", null, 0, -1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getColumn_Indexes(), this.getIndex(), this.getIndex_Columns(), "indexes", null, 0, -1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getColumn_ForeignKeys(), this.getForeignKey(), this.getForeignKey_Columns(), "foreignKeys", null, 0, -1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getColumn_AccessPatterns(), this.getAccessPattern(), this.getAccessPattern_Columns(), "accessPatterns", null, 0, -1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getColumn_Owner(), this.getColumnSet(), this.getColumnSet_Columns(), "owner", null, 0, 1, Column.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getColumn_Type(), theEcorePackage.getEObject(), null, "type", null, 1, 1, Column.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(schemaEClass, Schema.class, "Schema", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSchema_Tables(), this.getTable(), this.getTable_Schema(), "tables", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSchema_Catalog(), this.getCatalog(), this.getCatalog_Schemas(), "catalog", null, 0, 1, Schema.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSchema_Procedures(), this.getProcedure(), this.getProcedure_Schema(), "procedures", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSchema_Indexes(), this.getIndex(), this.getIndex_Schema(), "indexes", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSchema_LogicalRelationships(), this.getLogicalRelationship(), this.getLogicalRelationship_Schema(), "logicalRelationships", null, 0, -1, Schema.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(primaryKeyEClass, PrimaryKey.class, "PrimaryKey", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getPrimaryKey_Table(), this.getBaseTable(), this.getBaseTable_PrimaryKey(), "table", null, 0, 1, PrimaryKey.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(foreignKeyEClass, ForeignKey.class, "ForeignKey", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getForeignKey_ForeignKeyMultiplicity(), this.getMultiplicityKind(), "foreignKeyMultiplicity", "ZERO_TO_MANY", 0, 1, ForeignKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getForeignKey_PrimaryKeyMultiplicity(), this.getMultiplicityKind(), "primaryKeyMultiplicity", "ONE", 0, 1, ForeignKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getForeignKey_Columns(), this.getColumn(), this.getColumn_ForeignKeys(), "columns", null, 1, -1, ForeignKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getForeignKey_UniqueKey(), this.getUniqueKey(), this.getUniqueKey_ForeignKeys(), "uniqueKey", null, 1, 1, ForeignKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getForeignKey_Table(), this.getBaseTable(), this.getBaseTable_ForeignKeys(), "table", null, 0, 1, ForeignKey.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(relationalEntityEClass, RelationalEntity.class, "RelationalEntity", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getRelationalEntity_Name(), ecorePackage.getEString(), "name", null, 0, 1, RelationalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getRelationalEntity_NameInSource(), ecorePackage.getEString(), "nameInSource", null, 0, 1, RelationalEntity.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(uniqueKeyEClass, UniqueKey.class, "UniqueKey", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getUniqueKey_Columns(), this.getColumn(), this.getColumn_UniqueKeys(), "columns", null, 1, -1, UniqueKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getUniqueKey_ForeignKeys(), this.getForeignKey(), this.getForeignKey_UniqueKey(), "foreignKeys", null, 0, -1, UniqueKey.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(uniqueKeyEClass, this.getBaseTable(), "getTable"); //$NON-NLS-1$

        initEClass(viewEClass, View.class, "View", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(catalogEClass, Catalog.class, "Catalog", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getCatalog_Schemas(), this.getSchema(), this.getSchema_Catalog(), "schemas", null, 0, -1, Catalog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getCatalog_Procedures(), this.getProcedure(), this.getProcedure_Catalog(), "procedures", null, 0, -1, Catalog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getCatalog_Indexes(), this.getIndex(), this.getIndex_Catalog(), "indexes", null, 0, -1, Catalog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getCatalog_Tables(), this.getTable(), this.getTable_Catalog(), "tables", null, 0, -1, Catalog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getCatalog_LogicalRelationships(), this.getLogicalRelationship(), this.getLogicalRelationship_Catalog(), "logicalRelationships", null, 0, -1, Catalog.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(procedureEClass, Procedure.class, "Procedure", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getProcedure_Function(), ecorePackage.getEBoolean(), "function", null, 0, 1, Procedure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProcedure_Schema(), this.getSchema(), this.getSchema_Procedures(), "schema", null, 0, 1, Procedure.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProcedure_Parameters(), this.getProcedureParameter(), this.getProcedureParameter_Procedure(), "parameters", null, 0, -1, Procedure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProcedure_Catalog(), this.getCatalog(), this.getCatalog_Procedures(), "catalog", null, 0, 1, Procedure.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProcedure_Result(), this.getProcedureResult(), this.getProcedureResult_Procedure(), "result", null, 0, 1, Procedure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedure_UpdateCount(), this.getProcedureUpdateCount(), "updateCount", "AUTO", 0, 1, Procedure.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(indexEClass, Index.class, "Index", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getIndex_FilterCondition(), ecorePackage.getEString(), "filterCondition", null, 0, 1, Index.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getIndex_Nullable(), ecorePackage.getEBoolean(), "nullable", "true", 0, 1, Index.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getIndex_AutoUpdate(), ecorePackage.getEBoolean(), "autoUpdate", null, 0, 1, Index.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getIndex_Unique(), ecorePackage.getEBoolean(), "unique", null, 0, 1, Index.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getIndex_Schema(), this.getSchema(), this.getSchema_Indexes(), "schema", null, 0, 1, Index.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getIndex_Columns(), this.getColumn(), this.getColumn_Indexes(), "columns", null, 1, -1, Index.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getIndex_Catalog(), this.getCatalog(), this.getCatalog_Indexes(), "catalog", null, 0, 1, Index.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(procedureParameterEClass, ProcedureParameter.class, "ProcedureParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_Direction(), this.getDirectionKind(), "direction", null, 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_DefaultValue(), ecorePackage.getEString(), "defaultValue", null, 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_NativeType(), ecorePackage.getEString(), "nativeType", null, 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_Length(), ecorePackage.getEInt(), "length", null, 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_Precision(), ecorePackage.getEInt(), "precision", null, 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_Scale(), ecorePackage.getEInt(), "scale", null, 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getProcedureParameter_Nullable(), this.getNullableType(), "nullable", "NO_NULLS", 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getProcedureParameter_Radix(), ecorePackage.getEInt(), "radix", "10", 0, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getProcedureParameter_Procedure(), this.getProcedure(), this.getProcedure_Parameters(), "procedure", null, 0, 1, ProcedureParameter.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getProcedureParameter_Type(), theEcorePackage.getEObject(), null, "type", null, 1, 1, ProcedureParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(uniqueConstraintEClass, UniqueConstraint.class, "UniqueConstraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getUniqueConstraint_Table(), this.getBaseTable(), this.getBaseTable_UniqueConstraints(), "table", null, 0, 1, UniqueConstraint.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(accessPatternEClass, AccessPattern.class, "AccessPattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getAccessPattern_Columns(), this.getColumn(), this.getColumn_AccessPatterns(), "columns", null, 1, -1, AccessPattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getAccessPattern_Table(), this.getTable(), this.getTable_AccessPatterns(), "table", null, 0, 1, AccessPattern.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(relationshipEClass, Relationship.class, "Relationship", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        initEClass(logicalRelationshipEClass, LogicalRelationship.class, "LogicalRelationship", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getLogicalRelationship_Catalog(), this.getCatalog(), this.getCatalog_LogicalRelationships(), "catalog", null, 0, 1, LogicalRelationship.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLogicalRelationship_Schema(), this.getSchema(), this.getSchema_LogicalRelationships(), "schema", null, 0, 1, LogicalRelationship.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLogicalRelationship_Ends(), this.getLogicalRelationshipEnd(), this.getLogicalRelationshipEnd_Relationship(), "ends", null, 2, -1, LogicalRelationship.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(logicalRelationshipEndEClass, LogicalRelationshipEnd.class, "LogicalRelationshipEnd", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getLogicalRelationshipEnd_Multiplicity(), this.getMultiplicityKind(), "multiplicity", null, 0, 1, LogicalRelationshipEnd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLogicalRelationshipEnd_Table(), this.getTable(), this.getTable_LogicalRelationships(), "table", null, 1, 1, LogicalRelationshipEnd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getLogicalRelationshipEnd_Relationship(), this.getLogicalRelationship(), this.getLogicalRelationship_Ends(), "relationship", null, 1, 1, LogicalRelationshipEnd.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(baseTableEClass, BaseTable.class, "BaseTable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getBaseTable_ForeignKeys(), this.getForeignKey(), this.getForeignKey_Table(), "foreignKeys", null, 0, -1, BaseTable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBaseTable_PrimaryKey(), this.getPrimaryKey(), this.getPrimaryKey_Table(), "primaryKey", null, 0, 1, BaseTable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getBaseTable_UniqueConstraints(), this.getUniqueConstraint(), this.getUniqueConstraint_Table(), "uniqueConstraints", null, 0, -1, BaseTable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(columnSetEClass, ColumnSet.class, "ColumnSet", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getColumnSet_Columns(), this.getColumn(), this.getColumn_Owner(), "columns", null, 1, -1, ColumnSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(procedureResultEClass, ProcedureResult.class, "ProcedureResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getProcedureResult_Procedure(), this.getProcedure(), this.getProcedure_Result(), "procedure", null, 0, 1, ProcedureResult.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(nullableTypeEEnum, NullableType.class, "NullableType"); //$NON-NLS-1$
        addEEnumLiteral(nullableTypeEEnum, NullableType.NO_NULLS_LITERAL);
        addEEnumLiteral(nullableTypeEEnum, NullableType.NULLABLE_LITERAL);
        addEEnumLiteral(nullableTypeEEnum, NullableType.NULLABLE_UNKNOWN_LITERAL);

        initEEnum(directionKindEEnum, DirectionKind.class, "DirectionKind"); //$NON-NLS-1$
        addEEnumLiteral(directionKindEEnum, DirectionKind.IN_LITERAL);
        addEEnumLiteral(directionKindEEnum, DirectionKind.OUT_LITERAL);
        addEEnumLiteral(directionKindEEnum, DirectionKind.INOUT_LITERAL);
        addEEnumLiteral(directionKindEEnum, DirectionKind.RETURN_LITERAL);
        addEEnumLiteral(directionKindEEnum, DirectionKind.UNKNOWN_LITERAL);

        initEEnum(multiplicityKindEEnum, MultiplicityKind.class, "MultiplicityKind"); //$NON-NLS-1$
        addEEnumLiteral(multiplicityKindEEnum, MultiplicityKind.ONE_LITERAL);
        addEEnumLiteral(multiplicityKindEEnum, MultiplicityKind.MANY_LITERAL);
        addEEnumLiteral(multiplicityKindEEnum, MultiplicityKind.ZERO_TO_ONE_LITERAL);
        addEEnumLiteral(multiplicityKindEEnum, MultiplicityKind.ZERO_TO_MANY_LITERAL);
        addEEnumLiteral(multiplicityKindEEnum, MultiplicityKind.UNSPECIFIED_LITERAL);

        initEEnum(searchabilityTypeEEnum, SearchabilityType.class, "SearchabilityType"); //$NON-NLS-1$
        addEEnumLiteral(searchabilityTypeEEnum, SearchabilityType.SEARCHABLE_LITERAL);
        addEEnumLiteral(searchabilityTypeEEnum, SearchabilityType.ALL_EXCEPT_LIKE_LITERAL);
        addEEnumLiteral(searchabilityTypeEEnum, SearchabilityType.LIKE_ONLY_LITERAL);
        addEEnumLiteral(searchabilityTypeEEnum, SearchabilityType.UNSEARCHABLE_LITERAL);

        initEEnum(procedureUpdateCountEEnum, ProcedureUpdateCount.class, "ProcedureUpdateCount"); //$NON-NLS-1$
        addEEnumLiteral(procedureUpdateCountEEnum, ProcedureUpdateCount.AUTO_LITERAL);
        addEEnumLiteral(procedureUpdateCountEEnum, ProcedureUpdateCount.ZERO_LITERAL);
        addEEnumLiteral(procedureUpdateCountEEnum, ProcedureUpdateCount.ONE_LITERAL);
        addEEnumLiteral(procedureUpdateCountEEnum, ProcedureUpdateCount.MULTIPLE_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} //RelationalPackageImpl
