/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.Index;
import com.metamatrix.metamodels.relational.LogicalRelationship;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Catalog</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.CatalogImpl#getSchemas <em>Schemas</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.CatalogImpl#getProcedures <em>Procedures</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.CatalogImpl#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.CatalogImpl#getTables <em>Tables</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.CatalogImpl#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class CatalogImpl extends RelationalEntityImpl implements Catalog {
    /**
     * The cached value of the '{@link #getSchemas() <em>Schemas</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSchemas()
     * @generated
     * @ordered
     */
    protected EList schemas = null;

    /**
     * The cached value of the '{@link #getProcedures() <em>Procedures</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProcedures()
     * @generated
     * @ordered
     */
    protected EList procedures = null;

    /**
     * The cached value of the '{@link #getIndexes() <em>Indexes</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getIndexes()
     * @generated
     * @ordered
     */
    protected EList indexes = null;

    /**
     * The cached value of the '{@link #getTables() <em>Tables</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTables()
     * @generated
     * @ordered
     */
    protected EList tables = null;

    /**
     * The cached value of the '{@link #getLogicalRelationships() <em>Logical Relationships</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLogicalRelationships()
     * @generated
     * @ordered
     */
    protected EList logicalRelationships = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected CatalogImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getCatalog();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getSchemas() {
        if (schemas == null) {
            schemas = new EObjectContainmentWithInverseEList(Schema.class, this, RelationalPackage.CATALOG__SCHEMAS, RelationalPackage.SCHEMA__CATALOG);
        }
        return schemas;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getProcedures() {
        if (procedures == null) {
            procedures = new EObjectContainmentWithInverseEList(Procedure.class, this, RelationalPackage.CATALOG__PROCEDURES, RelationalPackage.PROCEDURE__CATALOG);
        }
        return procedures;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getIndexes() {
        if (indexes == null) {
            indexes = new EObjectContainmentWithInverseEList(Index.class, this, RelationalPackage.CATALOG__INDEXES, RelationalPackage.INDEX__CATALOG);
        }
        return indexes;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getTables() {
        if (tables == null) {
            tables = new EObjectContainmentWithInverseEList(Table.class, this, RelationalPackage.CATALOG__TABLES, RelationalPackage.TABLE__CATALOG);
        }
        return tables;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getLogicalRelationships() {
        if (logicalRelationships == null) {
            logicalRelationships = new EObjectContainmentWithInverseEList(LogicalRelationship.class, this, RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS, RelationalPackage.LOGICAL_RELATIONSHIP__CATALOG);
        }
        return logicalRelationships;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.CATALOG__SCHEMAS:
                    return ((InternalEList)getSchemas()).basicAdd(otherEnd, msgs);
                case RelationalPackage.CATALOG__PROCEDURES:
                    return ((InternalEList)getProcedures()).basicAdd(otherEnd, msgs);
                case RelationalPackage.CATALOG__INDEXES:
                    return ((InternalEList)getIndexes()).basicAdd(otherEnd, msgs);
                case RelationalPackage.CATALOG__TABLES:
                    return ((InternalEList)getTables()).basicAdd(otherEnd, msgs);
                case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
                    return ((InternalEList)getLogicalRelationships()).basicAdd(otherEnd, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null)
            msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case RelationalPackage.CATALOG__SCHEMAS:
                    return ((InternalEList)getSchemas()).basicRemove(otherEnd, msgs);
                case RelationalPackage.CATALOG__PROCEDURES:
                    return ((InternalEList)getProcedures()).basicRemove(otherEnd, msgs);
                case RelationalPackage.CATALOG__INDEXES:
                    return ((InternalEList)getIndexes()).basicRemove(otherEnd, msgs);
                case RelationalPackage.CATALOG__TABLES:
                    return ((InternalEList)getTables()).basicRemove(otherEnd, msgs);
                case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
                    return ((InternalEList)getLogicalRelationships()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.CATALOG__NAME:
                return getName();
            case RelationalPackage.CATALOG__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.CATALOG__SCHEMAS:
                return getSchemas();
            case RelationalPackage.CATALOG__PROCEDURES:
                return getProcedures();
            case RelationalPackage.CATALOG__INDEXES:
                return getIndexes();
            case RelationalPackage.CATALOG__TABLES:
                return getTables();
            case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
                return getLogicalRelationships();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.CATALOG__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.CATALOG__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.CATALOG__SCHEMAS:
                getSchemas().clear();
                getSchemas().addAll((Collection)newValue);
                return;
            case RelationalPackage.CATALOG__PROCEDURES:
                getProcedures().clear();
                getProcedures().addAll((Collection)newValue);
                return;
            case RelationalPackage.CATALOG__INDEXES:
                getIndexes().clear();
                getIndexes().addAll((Collection)newValue);
                return;
            case RelationalPackage.CATALOG__TABLES:
                getTables().clear();
                getTables().addAll((Collection)newValue);
                return;
            case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                getLogicalRelationships().addAll((Collection)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.CATALOG__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.CATALOG__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.CATALOG__SCHEMAS:
                getSchemas().clear();
                return;
            case RelationalPackage.CATALOG__PROCEDURES:
                getProcedures().clear();
                return;
            case RelationalPackage.CATALOG__INDEXES:
                getIndexes().clear();
                return;
            case RelationalPackage.CATALOG__TABLES:
                getTables().clear();
                return;
            case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.CATALOG__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.CATALOG__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.CATALOG__SCHEMAS:
                return schemas != null && !schemas.isEmpty();
            case RelationalPackage.CATALOG__PROCEDURES:
                return procedures != null && !procedures.isEmpty();
            case RelationalPackage.CATALOG__INDEXES:
                return indexes != null && !indexes.isEmpty();
            case RelationalPackage.CATALOG__TABLES:
                return tables != null && !tables.isEmpty();
            case RelationalPackage.CATALOG__LOGICAL_RELATIONSHIPS:
                return logicalRelationships != null && !logicalRelationships.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //CatalogImpl
