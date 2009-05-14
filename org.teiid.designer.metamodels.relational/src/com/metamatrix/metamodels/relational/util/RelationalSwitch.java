/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;


import java.util.List;

import org.eclipse.emf.ecore.EClass;
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
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.relational.RelationalPackage
 * @generated
 */
public class RelationalSwitch {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static RelationalPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RelationalSwitch() {
        if (modelPackage == null) {
            modelPackage = RelationalPackage.eINSTANCE;
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
            case RelationalPackage.TABLE: {
                Table table = (Table)theEObject;
                Object result = caseTable(table);
                if (result == null) result = caseColumnSet(table);
                if (result == null) result = caseRelationalEntity(table);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.COLUMN: {
                Column column = (Column)theEObject;
                Object result = caseColumn(column);
                if (result == null) result = caseRelationalEntity(column);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.SCHEMA: {
                Schema schema = (Schema)theEObject;
                Object result = caseSchema(schema);
                if (result == null) result = caseRelationalEntity(schema);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.PRIMARY_KEY: {
                PrimaryKey primaryKey = (PrimaryKey)theEObject;
                Object result = casePrimaryKey(primaryKey);
                if (result == null) result = caseUniqueKey(primaryKey);
                if (result == null) result = caseRelationalEntity(primaryKey);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.FOREIGN_KEY: {
                ForeignKey foreignKey = (ForeignKey)theEObject;
                Object result = caseForeignKey(foreignKey);
                if (result == null) result = caseRelationship(foreignKey);
                if (result == null) result = caseRelationalEntity(foreignKey);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.RELATIONAL_ENTITY: {
                RelationalEntity relationalEntity = (RelationalEntity)theEObject;
                Object result = caseRelationalEntity(relationalEntity);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.UNIQUE_KEY: {
                UniqueKey uniqueKey = (UniqueKey)theEObject;
                Object result = caseUniqueKey(uniqueKey);
                if (result == null) result = caseRelationalEntity(uniqueKey);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.VIEW: {
                View view = (View)theEObject;
                Object result = caseView(view);
                if (result == null) result = caseTable(view);
                if (result == null) result = caseColumnSet(view);
                if (result == null) result = caseRelationalEntity(view);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.CATALOG: {
                Catalog catalog = (Catalog)theEObject;
                Object result = caseCatalog(catalog);
                if (result == null) result = caseRelationalEntity(catalog);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.PROCEDURE: {
                Procedure procedure = (Procedure)theEObject;
                Object result = caseProcedure(procedure);
                if (result == null) result = caseRelationalEntity(procedure);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.INDEX: {
                Index index = (Index)theEObject;
                Object result = caseIndex(index);
                if (result == null) result = caseRelationalEntity(index);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.PROCEDURE_PARAMETER: {
                ProcedureParameter procedureParameter = (ProcedureParameter)theEObject;
                Object result = caseProcedureParameter(procedureParameter);
                if (result == null) result = caseRelationalEntity(procedureParameter);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.UNIQUE_CONSTRAINT: {
                UniqueConstraint uniqueConstraint = (UniqueConstraint)theEObject;
                Object result = caseUniqueConstraint(uniqueConstraint);
                if (result == null) result = caseUniqueKey(uniqueConstraint);
                if (result == null) result = caseRelationalEntity(uniqueConstraint);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.ACCESS_PATTERN: {
                AccessPattern accessPattern = (AccessPattern)theEObject;
                Object result = caseAccessPattern(accessPattern);
                if (result == null) result = caseRelationalEntity(accessPattern);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.RELATIONSHIP: {
                Relationship relationship = (Relationship)theEObject;
                Object result = caseRelationship(relationship);
                if (result == null) result = caseRelationalEntity(relationship);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.LOGICAL_RELATIONSHIP: {
                LogicalRelationship logicalRelationship = (LogicalRelationship)theEObject;
                Object result = caseLogicalRelationship(logicalRelationship);
                if (result == null) result = caseRelationship(logicalRelationship);
                if (result == null) result = caseRelationalEntity(logicalRelationship);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.LOGICAL_RELATIONSHIP_END: {
                LogicalRelationshipEnd logicalRelationshipEnd = (LogicalRelationshipEnd)theEObject;
                Object result = caseLogicalRelationshipEnd(logicalRelationshipEnd);
                if (result == null) result = caseRelationalEntity(logicalRelationshipEnd);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.BASE_TABLE: {
                BaseTable baseTable = (BaseTable)theEObject;
                Object result = caseBaseTable(baseTable);
                if (result == null) result = caseTable(baseTable);
                if (result == null) result = caseColumnSet(baseTable);
                if (result == null) result = caseRelationalEntity(baseTable);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.COLUMN_SET: {
                ColumnSet columnSet = (ColumnSet)theEObject;
                Object result = caseColumnSet(columnSet);
                if (result == null) result = caseRelationalEntity(columnSet);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case RelationalPackage.PROCEDURE_RESULT: {
                ProcedureResult procedureResult = (ProcedureResult)theEObject;
                Object result = caseProcedureResult(procedureResult);
                if (result == null) result = caseColumnSet(procedureResult);
                if (result == null) result = caseRelationalEntity(procedureResult);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Table</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Table</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseTable(Table object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Column</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Column</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseColumn(Column object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Schema</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Schema</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseSchema(Schema object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Primary Key</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Primary Key</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object casePrimaryKey(PrimaryKey object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Foreign Key</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Foreign Key</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseForeignKey(ForeignKey object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Entity</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Entity</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationalEntity(RelationalEntity object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Unique Key</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Unique Key</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseUniqueKey(UniqueKey object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>View</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>View</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseView(View object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Catalog</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Catalog</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseCatalog(Catalog object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Procedure</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Procedure</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcedure(Procedure object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Index</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Index</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseIndex(Index object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Procedure Parameter</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Procedure Parameter</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcedureParameter(ProcedureParameter object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Unique Constraint</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Unique Constraint</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseUniqueConstraint(UniqueConstraint object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Access Pattern</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Access Pattern</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseAccessPattern(AccessPattern object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Relationship</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Relationship</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseRelationship(Relationship object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Logical Relationship</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Logical Relationship</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseLogicalRelationship(LogicalRelationship object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Logical Relationship End</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Logical Relationship End</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseLogicalRelationshipEnd(LogicalRelationshipEnd object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Base Table</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Base Table</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseBaseTable(BaseTable object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Column Set</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Column Set</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseColumnSet(ColumnSet object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Procedure Result</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Procedure Result</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseProcedureResult(ProcedureResult object) {
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

} //RelationalSwitch
