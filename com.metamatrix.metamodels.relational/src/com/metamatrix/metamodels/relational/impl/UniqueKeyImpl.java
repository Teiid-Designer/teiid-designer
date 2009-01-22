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
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.UniqueKey;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Unique Key</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.UniqueKeyImpl#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.UniqueKeyImpl#getForeignKeys <em>Foreign Keys</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class UniqueKeyImpl extends RelationalEntityImpl implements UniqueKey {
    /**
     * The cached value of the '{@link #getColumns() <em>Columns</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getColumns()
     * @generated
     * @ordered
     */
    protected EList columns = null;

    /**
     * The cached value of the '{@link #getForeignKeys() <em>Foreign Keys</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getForeignKeys()
     * @generated
     * @ordered
     */
    protected EList foreignKeys = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected UniqueKeyImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getUniqueKey();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getColumns() {
        if (columns == null) {
            columns = new EObjectWithInverseResolvingEList.ManyInverse(Column.class, this, RelationalPackage.UNIQUE_KEY__COLUMNS, RelationalPackage.COLUMN__UNIQUE_KEYS);
        }
        return columns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getForeignKeys() {
        if (foreignKeys == null) {
            foreignKeys = new EObjectWithInverseResolvingEList(ForeignKey.class, this, RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS, RelationalPackage.FOREIGN_KEY__UNIQUE_KEY);
        }
        return foreignKeys;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public BaseTable getTable() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
                case RelationalPackage.UNIQUE_KEY__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicAdd(otherEnd, msgs);
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
                case RelationalPackage.UNIQUE_KEY__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS:
                    return ((InternalEList)getForeignKeys()).basicRemove(otherEnd, msgs);
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
            case RelationalPackage.UNIQUE_KEY__NAME:
                return getName();
            case RelationalPackage.UNIQUE_KEY__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.UNIQUE_KEY__COLUMNS:
                return getColumns();
            case RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS:
                return getForeignKeys();
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
            case RelationalPackage.UNIQUE_KEY__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.UNIQUE_KEY__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.UNIQUE_KEY__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS:
                getForeignKeys().clear();
                getForeignKeys().addAll((Collection)newValue);
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
            case RelationalPackage.UNIQUE_KEY__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.UNIQUE_KEY__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.UNIQUE_KEY__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS:
                getForeignKeys().clear();
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
            case RelationalPackage.UNIQUE_KEY__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.UNIQUE_KEY__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.UNIQUE_KEY__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.UNIQUE_KEY__FOREIGN_KEYS:
                return foreignKeys != null && !foreignKeys.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //UniqueKeyImpl
