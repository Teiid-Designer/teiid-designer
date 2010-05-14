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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.relational.Catalog;
import com.metamatrix.metamodels.relational.RelationalPackage;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.View;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>View</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ViewImpl extends TableImpl implements View {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ViewImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getView();
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
                case RelationalPackage.VIEW__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.VIEW__SCHEMA:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.VIEW__SCHEMA, msgs);
                case RelationalPackage.VIEW__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicAdd(otherEnd, msgs);
                case RelationalPackage.VIEW__CATALOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.VIEW__CATALOG, msgs);
                case RelationalPackage.VIEW__LOGICAL_RELATIONSHIPS:
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
                case RelationalPackage.VIEW__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.VIEW__SCHEMA:
                    return eBasicSetContainer(null, RelationalPackage.VIEW__SCHEMA, msgs);
                case RelationalPackage.VIEW__ACCESS_PATTERNS:
                    return ((InternalEList)getAccessPatterns()).basicRemove(otherEnd, msgs);
                case RelationalPackage.VIEW__CATALOG:
                    return eBasicSetContainer(null, RelationalPackage.VIEW__CATALOG, msgs);
                case RelationalPackage.VIEW__LOGICAL_RELATIONSHIPS:
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
    public NotificationChain eBasicRemoveFromContainer(NotificationChain msgs) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case RelationalPackage.VIEW__SCHEMA:
                    return eContainer.eInverseRemove(this, RelationalPackage.SCHEMA__TABLES, Schema.class, msgs);
                case RelationalPackage.VIEW__CATALOG:
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
            case RelationalPackage.VIEW__NAME:
                return getName();
            case RelationalPackage.VIEW__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.VIEW__COLUMNS:
                return getColumns();
            case RelationalPackage.VIEW__SYSTEM:
                return isSystem() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.VIEW__CARDINALITY:
                return new Integer(getCardinality());
            case RelationalPackage.VIEW__SUPPORTS_UPDATE:
                return isSupportsUpdate() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.VIEW__MATERIALIZED:
                return isMaterialized() ? Boolean.TRUE : Boolean.FALSE;
            case RelationalPackage.VIEW__SCHEMA:
                return getSchema();
            case RelationalPackage.VIEW__ACCESS_PATTERNS:
                return getAccessPatterns();
            case RelationalPackage.VIEW__CATALOG:
                return getCatalog();
            case RelationalPackage.VIEW__LOGICAL_RELATIONSHIPS:
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
            case RelationalPackage.VIEW__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.VIEW__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.VIEW__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case RelationalPackage.VIEW__SYSTEM:
                setSystem(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.VIEW__CARDINALITY:
                setCardinality(((Integer)newValue).intValue());
                return;
            case RelationalPackage.VIEW__SUPPORTS_UPDATE:
                setSupportsUpdate(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.VIEW__MATERIALIZED:
                setMaterialized(((Boolean)newValue).booleanValue());
                return;
            case RelationalPackage.VIEW__SCHEMA:
                setSchema((Schema)newValue);
                return;
            case RelationalPackage.VIEW__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                getAccessPatterns().addAll((Collection)newValue);
                return;
            case RelationalPackage.VIEW__CATALOG:
                setCatalog((Catalog)newValue);
                return;
            case RelationalPackage.VIEW__LOGICAL_RELATIONSHIPS:
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
            case RelationalPackage.VIEW__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.VIEW__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.VIEW__COLUMNS:
                getColumns().clear();
                return;
            case RelationalPackage.VIEW__SYSTEM:
                setSystem(SYSTEM_EDEFAULT);
                return;
            case RelationalPackage.VIEW__CARDINALITY:
                setCardinality(CARDINALITY_EDEFAULT);
                return;
            case RelationalPackage.VIEW__SUPPORTS_UPDATE:
                setSupportsUpdate(SUPPORTS_UPDATE_EDEFAULT);
                return;
            case RelationalPackage.VIEW__MATERIALIZED:
                setMaterialized(MATERIALIZED_EDEFAULT);
                return;
            case RelationalPackage.VIEW__SCHEMA:
                setSchema((Schema)null);
                return;
            case RelationalPackage.VIEW__ACCESS_PATTERNS:
                getAccessPatterns().clear();
                return;
            case RelationalPackage.VIEW__CATALOG:
                setCatalog((Catalog)null);
                return;
            case RelationalPackage.VIEW__LOGICAL_RELATIONSHIPS:
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
            case RelationalPackage.VIEW__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.VIEW__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.VIEW__COLUMNS:
                return columns != null && !columns.isEmpty();
            case RelationalPackage.VIEW__SYSTEM:
                return system != SYSTEM_EDEFAULT;
            case RelationalPackage.VIEW__CARDINALITY:
                return cardinality != CARDINALITY_EDEFAULT;
            case RelationalPackage.VIEW__SUPPORTS_UPDATE:
                return supportsUpdate != SUPPORTS_UPDATE_EDEFAULT;
            case RelationalPackage.VIEW__MATERIALIZED:
                return materialized != MATERIALIZED_EDEFAULT;
            case RelationalPackage.VIEW__SCHEMA:
                return getSchema() != null;
            case RelationalPackage.VIEW__ACCESS_PATTERNS:
                return accessPatterns != null && !accessPatterns.isEmpty();
            case RelationalPackage.VIEW__CATALOG:
                return getCatalog() != null;
            case RelationalPackage.VIEW__LOGICAL_RELATIONSHIPS:
                return logicalRelationships != null && !logicalRelationships.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //ViewImpl
