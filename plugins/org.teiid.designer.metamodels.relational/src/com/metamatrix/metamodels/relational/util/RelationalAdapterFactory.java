/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.AccessPattern;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ColumnSet;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.LogicalRelationshipEnd;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.ProcedureResult;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Relationship;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.metamodels.relational.UniqueConstraint;
import com.metamatrix.metamodels.relational.UniqueKey;
import com.metamatrix.metamodels.relational.View;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage
 * @generated
 */
public class RelationalAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static RelationalPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationalAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = RelationalPackage.eINSTANCE;
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
    protected RelationalSwitch modelSwitch =
        new RelationalSwitch() {
            @Override
            public Object caseTable(Table object) {
                return createTableAdapter();
            }
            @Override
            public Object caseColumn(Column object) {
                return createColumnAdapter();
            }
            @Override
            public Object caseSchema(Schema object) {
                return createSchemaAdapter();
            }
            @Override
            public Object casePrimaryKey(PrimaryKey object) {
                return createPrimaryKeyAdapter();
            }
            @Override
            public Object caseForeignKey(ForeignKey object) {
                return createForeignKeyAdapter();
            }
            @Override
            public Object caseRelationalEntity(RelationalEntity object) {
                return createRelationalEntityAdapter();
            }
            @Override
            public Object caseUniqueKey(UniqueKey object) {
                return createUniqueKeyAdapter();
            }
            @Override
            public Object caseView(View object) {
                return createViewAdapter();
            }
            @Override
            public Object caseCatalog(Catalog object) {
                return createCatalogAdapter();
            }
            @Override
            public Object caseProcedure(Procedure object) {
                return createProcedureAdapter();
            }
            @Override
            public Object caseIndex(Index object) {
                return createIndexAdapter();
            }
            @Override
            public Object caseProcedureParameter(ProcedureParameter object) {
                return createProcedureParameterAdapter();
            }
            @Override
            public Object caseUniqueConstraint(UniqueConstraint object) {
                return createUniqueConstraintAdapter();
            }
            @Override
            public Object caseAccessPattern(AccessPattern object) {
                return createAccessPatternAdapter();
            }
            @Override
            public Object caseRelationship(Relationship object) {
                return createRelationshipAdapter();
            }
            @Override
            public Object caseLogicalRelationship(LogicalRelationship object) {
                return createLogicalRelationshipAdapter();
            }
            @Override
            public Object caseLogicalRelationshipEnd(LogicalRelationshipEnd object) {
                return createLogicalRelationshipEndAdapter();
            }
            @Override
            public Object caseBaseTable(BaseTable object) {
                return createBaseTableAdapter();
            }
            @Override
            public Object caseColumnSet(ColumnSet object) {
                return createColumnSetAdapter();
            }
            @Override
            public Object caseProcedureResult(ProcedureResult object) {
                return createProcedureResultAdapter();
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
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Table <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Table
     * @generated
     */
    public Adapter createTableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Column <em>Column</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Column
     * @generated
     */
    public Adapter createColumnAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Schema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Schema
     * @generated
     */
    public Adapter createSchemaAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.PrimaryKey <em>Primary Key</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.PrimaryKey
     * @generated
     */
    public Adapter createPrimaryKeyAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.ForeignKey <em>Foreign Key</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.ForeignKey
     * @generated
     */
    public Adapter createForeignKeyAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.RelationalEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.RelationalEntity
     * @generated
     */
    public Adapter createRelationalEntityAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.UniqueKey <em>Unique Key</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.UniqueKey
     * @generated
     */
    public Adapter createUniqueKeyAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.View <em>View</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.View
     * @generated
     */
    public Adapter createViewAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Catalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Catalog
     * @generated
     */
    public Adapter createCatalogAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Procedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Procedure
     * @generated
     */
    public Adapter createProcedureAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Index <em>Index</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Index
     * @generated
     */
    public Adapter createIndexAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.ProcedureParameter <em>Procedure Parameter</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter
     * @generated
     */
    public Adapter createProcedureParameterAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.UniqueConstraint <em>Unique Constraint</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.UniqueConstraint
     * @generated
     */
    public Adapter createUniqueConstraintAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.AccessPattern <em>Access Pattern</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.AccessPattern
     * @generated
     */
    public Adapter createAccessPatternAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.Relationship <em>Relationship</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.Relationship
     * @generated
     */
    public Adapter createRelationshipAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.LogicalRelationship <em>Logical Relationship</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.LogicalRelationship
     * @generated
     */
    public Adapter createLogicalRelationshipAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd <em>Logical Relationship End</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.LogicalRelationshipEnd
     * @generated
     */
    public Adapter createLogicalRelationshipEndAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.BaseTable <em>Base Table</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.BaseTable
     * @generated
     */
    public Adapter createBaseTableAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.ColumnSet <em>Column Set</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.ColumnSet
     * @generated
     */
    public Adapter createColumnSetAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link com.metamatrix.metamodels.relational.ProcedureResult <em>Procedure Result</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see com.metamatrix.metamodels.relational.ProcedureResult
     * @generated
     */
    public Adapter createProcedureResultAdapter() {
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

} //RelationalAdapterFactory
