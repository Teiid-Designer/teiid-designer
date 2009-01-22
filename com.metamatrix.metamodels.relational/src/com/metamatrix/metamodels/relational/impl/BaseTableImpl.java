/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.UniqueConstraint;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Base Table</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.BaseTableImpl#getForeignKeys <em>Foreign Keys</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.BaseTableImpl#getPrimaryKey <em>Primary Key</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.BaseTableImpl#getUniqueConstraints <em>Unique Constraints</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BaseTableImpl extends TableImpl implements BaseTable {
    /**
     * The cached value of the '{@link #getForeignKeys() <em>Foreign Keys</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getForeignKeys()
     * @generated
     * @ordered
     */
    protected EList foreignKeys = null;

    /**
     * The cached value of the '{@link #getPrimaryKey() <em>Primary Key</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrimaryKey()
     * @generated
     * @ordered
     */
    protected PrimaryKey primaryKey = null;

    /**
     * The cached value of the '{@link #getUniqueConstraints() <em>Unique Constraints</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUniqueConstraints()
     * @generated
     * @ordered
     */
    protected EList uniqueConstraints = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected BaseTableImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getBaseTable();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getForeignKeys() {
        if (foreignKeys == null) {
            foreignKeys = new EObjectContainmentWithInverseEList(ForeignKey.class, this, RelationalPackage.BASE_TABLE__FOREIGN_KEYS, RelationalPackage.FOREIGN_KEY__TABLE);
        }
        return foreignKeys;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetPrimaryKey(PrimaryKey newPrimaryKey, NotificationChain msgs) {
        PrimaryKey oldPrimaryKey = primaryKey;
        primaryKey = newPrimaryKey;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, RelationalPackage.BASE_TABLE__PRIMARY_KEY, oldPrimaryKey, newPrimaryKey);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPrimaryKey(PrimaryKey newPrimaryKey) {
        if (newPrimaryKey != primaryKey) {
            NotificationChain msgs = null;
            if (primaryKey != null)
                msgs = ((InternalEObject)primaryKey).eInverseRemove(this, RelationalPackage.PRIMARY_KEY__TABLE, PrimaryKey.class, msgs);
            if (newPrimaryKey != null)
                msgs = ((InternalEObject)newPrimaryKey).eInverseAdd(this, RelationalPackage.PRIMARY_KEY__TABLE, PrimaryKey.class, msgs);
            msgs = basicSetPrimaryKey(newPrimaryKey, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.BASE_TABLE__PRIMARY_KEY, newPrimaryKey, newPrimaryKey));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getUniqueConstraints() {
        if (uniqueConstraints == null) {
            uniqueConstraints = new EObjectContainmentWithInverseEList(UniqueConstraint.class, this, RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS, RelationalPackage.UNIQUE_CONSTRAINT__TABLE);
        }
        return uniqueConstraints;
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
                case RelationalPackage.BASE_TABLE__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__SCHEMA:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.BASE_TABLE__SCHEMA, msgs);
                case RelationalPackage.BASE_TABLE__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.BASE_TABLE__CATALOG, msgs);
                case RelationalPackage.BASE_TABLE__LOGICAL_RELATIONSHIPS:
                    return ((InternalEList)getLogicalRelationships()).basicAdd(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicAdd(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__PRIMARY_KEY:
                    if (primaryKey != null)
                        msgs = ((InternalEObject)primaryKey).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - RelationalPackage.BASE_TABLE__PRIMARY_KEY, null, msgs);
                    return basicSetPrimaryKey((PrimaryKey)otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS:
                    return ((InternalEList)getUniqueConstraints()).basicAdd(otherEnd, msgs);
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
                case RelationalPackage.BASE_TABLE__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__SCHEMA:
                    return eBasicSetContainer(null, RelationalPackage.BASE_TABLE__SCHEMA, msgs);
                case RelationalPackage.BASE_TABLE__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.BASE_TABLE__CATALOG, msgs);
                case RelationalPackage.BASE_TABLE__LOGICAL_RELATIONSHIPS:
                    return ((InternalEList)getLogicalRelationships()).basicRemove(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicRemove(otherEnd, msgs);
                case RelationalPackage.BASE_TABLE__PRIMARY_KEY:
                    return basicSetPrimaryKey(null, msgs);
                case RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS:
                    return ((InternalEList)getUniqueConstraints()).basicRemove(otherEnd, msgs);
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
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case RelationalPackage.BASE_TABLE__SCHEMA:
                    return eContainer.eInverseRemove(this, RelationalPackage.SCHEMA__TABLES, Schema.class, msgs);
                case RelationalPackage.BASE_TABLE__CATALOG:
                    return eContainer.eInverseRemove(this, RelationalPackage.CATALOG__TABLES, Catalog.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case RelationalPackage.BASE_TABLE__NAME:
                return getName();
            case RelationalPackage.BASE_TABLE__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.BASE_TABLE__COLUMNS:
                return getColumns();
            case RelationalPackage.BASE_TABLE__SYSTEM:
                return isSystem() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.BASE_TABLE__CARDINALITY:
                return new Integer(getCardinality());
            case RelationalPackage.BASE_TABLE__SUPPORTS_UPDATE:
                return isSupportsUpdate() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.BASE_TABLE__MATERIALIZED:
                return isMaterialized() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.BASE_TABLE__SCHEMA:
                return getSchema();
            case RelationalPackage.BASE_TABLE__ACCESS_PATTERNS:
                return getAccessPatterns();
            case RelationalPackage.BASE_TABLE__CATALOG:
                return getCatalog();
            case RelationalPackage.BASE_TABLE__LOGICAL_RELATIONSHIPS:
                return getLogicalRelationships();
            case RelationalPackage.BASE_TABLE__FOREIGN_KEYS:
                return getForeignKeys();
            case RelationalPackage.BASE_TABLE__PRIMARY_KEY:
                return getPrimaryKey();
            case RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS:
                return getUniqueConstraints();
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
            case RelationalPackage.BASE_TABLE__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.BASE_TABLE__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.BASE_TABLE__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.BASE_TABLE__SYSTEM:
                setSystem(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.BASE_TABLE__CARDINALITY:
                setCardinality(((Integer)newValue).intValue());
                return;
            case RelationalPackage.BASE_TABLE__SUPPORTS_UPDATE:
                setSupportsUpdate(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.BASE_TABLE__MATERIALIZED:
                setMaterialized(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.BASE_TABLE__SCHEMA:
                setSchema((Schema)newValue);
                return;
            case RelationalPackage.BASE_TABLE__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                getAccessPatterns().addAll((Collection)newValue);
                return;
            case RelationalPackage.BASE_TABLE__CATALOG:
                setCatalog((Catalog)newValue);
                return;
            case RelationalPackage.BASE_TABLE__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                getLogicalRelationships().addAll((Collection)newValue);
                return;
            case RelationalPackage.BASE_TABLE__FOREIGN_KEYS:
                getForeignKeys().clear();
                getForeignKeys().addAll((Collection)newValue);
                return;
            case RelationalPackage.BASE_TABLE__PRIMARY_KEY:
                setPrimaryKey((PrimaryKey)newValue);
                return;
            case RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS:
                getUniqueConstraints().clear();
                getUniqueConstraints().addAll((Collection)newValue);
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
            case RelationalPackage.BASE_TABLE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.BASE_TABLE__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.BASE_TABLE__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.BASE_TABLE__SYSTEM:
                setSystem(SYSTEM_EDEFAULT);
                return;
            case RelationalPackage.BASE_TABLE__CARDINALITY:
                setCardinality(CARDINALITY_EDEFAULT);
                return;
            case RelationalPackage.BASE_TABLE__SUPPORTS_UPDATE:
                setSupportsUpdate(SUPPORTS_UPDATE_EDEFAULT);
                return;
            case RelationalPackage.BASE_TABLE__MATERIALIZED:
                setMaterialized(MATERIALIZED_EDEFAULT);
                return;
            case RelationalPackage.BASE_TABLE__SCHEMA:
                setSchema((Schema)null);
                return;
            case RelationalPackage.BASE_TABLE__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                return;
            case RelationalPackage.BASE_TABLE__CATALOG:
                setCatalog((Catalog)null);
                return;
            case RelationalPackage.BASE_TABLE__LOGICAL_RELATIONSHIPS:
                getLogicalRelationships().clear();
                return;
            case RelationalPackage.BASE_TABLE__FOREIGN_KEYS:
                getForeignKeys().clear();
                return;
            case RelationalPackage.BASE_TABLE__PRIMARY_KEY:
                setPrimaryKey((PrimaryKey)null);
                return;
            case RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS:
                getUniqueConstraints().clear();
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
            case RelationalPackage.BASE_TABLE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.BASE_TABLE__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.BASE_TABLE__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.BASE_TABLE__SYSTEM:
                return system != SYSTEM_EDEFAULT;
            case RelationalPackage.BASE_TABLE__CARDINALITY:
                return cardinality != CARDINALITY_EDEFAULT;
            case RelationalPackage.BASE_TABLE__SUPPORTS_UPDATE:
                return supportsUpdate != SUPPORTS_UPDATE_EDEFAULT;
            case RelationalPackage.BASE_TABLE__MATERIALIZED:
                return materialized != MATERIALIZED_EDEFAULT;
            case RelationalPackage.BASE_TABLE__SCHEMA:
                return getSchema() != null;
            case RelationalPackage.BASE_TABLE__ACCESS_PATTERNS:
                return accessPatterns != null && !accessPatterns.isEmpty();
            case RelationalPackage.BASE_TABLE__CATALOG:
                return getCatalog() != null;
            case RelationalPackage.BASE_TABLE__LOGICAL_RELATIONSHIPS:
                return logicalRelationships != null && !logicalRelationships.isEmpty();
            case RelationalPackage.BASE_TABLE__FOREIGN_KEYS:
                return foreignKeys != null && !foreignKeys.isEmpty();
            case RelationalPackage.BASE_TABLE__PRIMARY_KEY:
                return primaryKey != null;
            case RelationalPackage.BASE_TABLE__UNIQUE_CONSTRAINTS:
                return uniqueConstraints != null && !uniqueConstraints.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //BaseTableImpl
