/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.teiid.designer.metamodels.relational.AccessPattern;
import org.teiid.designer.metamodels.relational.BaseTable;
import org.teiid.designer.metamodels.relational.Catalog;
import org.teiid.designer.metamodels.relational.Column;
import org.teiid.designer.metamodels.relational.DirectionKind;
import org.teiid.designer.metamodels.relational.ForeignKey;
import org.teiid.designer.metamodels.relational.Index;
import org.teiid.designer.metamodels.relational.LogicalRelationship;
import org.teiid.designer.metamodels.relational.LogicalRelationshipEnd;
import org.teiid.designer.metamodels.relational.MultiplicityKind;
import org.teiid.designer.metamodels.relational.NullableType;
import org.teiid.designer.metamodels.relational.PrimaryKey;
import org.teiid.designer.metamodels.relational.Procedure;
import org.teiid.designer.metamodels.relational.ProcedureParameter;
import org.teiid.designer.metamodels.relational.ProcedureResult;
import org.teiid.designer.metamodels.relational.ProcedureUpdateCount;
import org.teiid.designer.metamodels.relational.RelationalFactory;
import org.teiid.designer.metamodels.relational.RelationalPackage;
import org.teiid.designer.metamodels.relational.Schema;
import org.teiid.designer.metamodels.relational.SearchabilityType;
import org.teiid.designer.metamodels.relational.UniqueConstraint;
import org.teiid.designer.metamodels.relational.View;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class RelationalFactoryImpl extends EFactoryImpl implements RelationalFactory {
    /**
     * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public RelationalFactoryImpl() {
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
            case RelationalPackage.COLUMN:
                return createColumn();
            case RelationalPackage.SCHEMA:
                return createSchema();
            case RelationalPackage.PRIMARY_KEY:
                return createPrimaryKey();
            case RelationalPackage.FOREIGN_KEY:
                return createForeignKey();
            case RelationalPackage.VIEW:
                return createView();
            case RelationalPackage.CATALOG:
                return createCatalog();
            case RelationalPackage.PROCEDURE:
                return createProcedure();
            case RelationalPackage.INDEX:
                return createIndex();
            case RelationalPackage.PROCEDURE_PARAMETER:
                return createProcedureParameter();
            case RelationalPackage.UNIQUE_CONSTRAINT:
                return createUniqueConstraint();
            case RelationalPackage.ACCESS_PATTERN:
                return createAccessPattern();
            case RelationalPackage.LOGICAL_RELATIONSHIP:
                return createLogicalRelationship();
            case RelationalPackage.LOGICAL_RELATIONSHIP_END:
                return createLogicalRelationshipEnd();
            case RelationalPackage.BASE_TABLE:
                return createBaseTable();
            case RelationalPackage.PROCEDURE_RESULT:
                return createProcedureResult();
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
            case RelationalPackage.NULLABLE_TYPE: {
                NullableType result = NullableType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case RelationalPackage.DIRECTION_KIND: {
                DirectionKind result = DirectionKind.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case RelationalPackage.MULTIPLICITY_KIND: {
                MultiplicityKind result = MultiplicityKind.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case RelationalPackage.SEARCHABILITY_TYPE: {
                SearchabilityType result = SearchabilityType.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
            case RelationalPackage.PROCEDURE_UPDATE_COUNT: {
                ProcedureUpdateCount result = ProcedureUpdateCount.get(initialValue);
                if (result == null) throw new IllegalArgumentException(
                                                                       "The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                return result;
            }
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
            case RelationalPackage.NULLABLE_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case RelationalPackage.DIRECTION_KIND:
                return instanceValue == null ? null : instanceValue.toString();
            case RelationalPackage.MULTIPLICITY_KIND:
                return instanceValue == null ? null : instanceValue.toString();
            case RelationalPackage.SEARCHABILITY_TYPE:
                return instanceValue == null ? null : instanceValue.toString();
            case RelationalPackage.PROCEDURE_UPDATE_COUNT:
                return instanceValue == null ? null : instanceValue.toString();
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
	public Column createColumn() {
        ColumnImpl column = new ColumnImpl();
        return column;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Schema createSchema() {
        SchemaImpl schema = new SchemaImpl();
        return schema;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public PrimaryKey createPrimaryKey() {
        PrimaryKeyImpl primaryKey = new PrimaryKeyImpl();
        return primaryKey;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ForeignKey createForeignKey() {
        ForeignKeyImpl foreignKey = new ForeignKeyImpl();
        return foreignKey;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public View createView() {
        ViewImpl view = new ViewImpl();
        return view;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Catalog createCatalog() {
        CatalogImpl catalog = new CatalogImpl();
        return catalog;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Procedure createProcedure() {
        ProcedureImpl procedure = new ProcedureImpl();
        return procedure;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public Index createIndex() {
        IndexImpl index = new IndexImpl();
        return index;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ProcedureParameter createProcedureParameter() {
        ProcedureParameterImpl procedureParameter = new ProcedureParameterImpl();
        return procedureParameter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public UniqueConstraint createUniqueConstraint() {
        UniqueConstraintImpl uniqueConstraint = new UniqueConstraintImpl();
        return uniqueConstraint;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public AccessPattern createAccessPattern() {
        AccessPatternImpl accessPattern = new AccessPatternImpl();
        return accessPattern;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public LogicalRelationship createLogicalRelationship() {
        LogicalRelationshipImpl logicalRelationship = new LogicalRelationshipImpl();
        return logicalRelationship;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public LogicalRelationshipEnd createLogicalRelationshipEnd() {
        LogicalRelationshipEndImpl logicalRelationshipEnd = new LogicalRelationshipEndImpl();
        return logicalRelationshipEnd;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public BaseTable createBaseTable() {
        BaseTableImpl baseTable = new BaseTableImpl();
        return baseTable;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public ProcedureResult createProcedureResult() {
        ProcedureResultImpl procedureResult = new ProcedureResultImpl();
        return procedureResult;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
	public RelationalPackage getRelationalPackage() {
        return (RelationalPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @deprecated
     * @generated
     */
    @Deprecated
    public static RelationalPackage getPackage() {
        return RelationalPackage.eINSTANCE;
    }

} // RelationalFactoryImpl
