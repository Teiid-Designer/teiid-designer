/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.RecursionErrorMode;
import com.metamatrix.metamodels.transformation.StagingTable;
import com.metamatrix.metamodels.transformation.TransformationPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Staging Table</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class StagingTableImpl extends MappingClassImpl implements StagingTable {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StagingTableImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getStagingTable();
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
                case TransformationPackage.STAGING_TABLE__COLUMNS:
                    return ((InternalEList)getColumns()).basicAdd(otherEnd, msgs);
                case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET, msgs);
                case TransformationPackage.STAGING_TABLE__INPUT_SET:
                    if (inputSet != null)
                        msgs = ((InternalEObject)inputSet).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TransformationPackage.STAGING_TABLE__INPUT_SET, null, msgs);
                    return basicSetInputSet((InputSet)otherEnd, msgs);
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
                case TransformationPackage.STAGING_TABLE__COLUMNS:
                    return ((InternalEList)getColumns()).basicRemove(otherEnd, msgs);
                case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                    return eBasicSetContainer(null, TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET, msgs);
                case TransformationPackage.STAGING_TABLE__INPUT_SET:
                    return basicSetInputSet(null, msgs);
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
                case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                    return eContainer.eInverseRemove(this, TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES, MappingClassSet.class, msgs);
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
            case TransformationPackage.STAGING_TABLE__NAME:
                return getName();
            case TransformationPackage.STAGING_TABLE__RECURSIVE:
                return isRecursive() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.STAGING_TABLE__RECURSION_ALLOWED:
                return isRecursionAllowed() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.STAGING_TABLE__RECURSION_CRITERIA:
                return getRecursionCriteria();
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT:
                return new Integer(getRecursionLimit());
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT_ERROR_MODE:
                return getRecursionLimitErrorMode();
            case TransformationPackage.STAGING_TABLE__COLUMNS:
                return getColumns();
            case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                return getMappingClassSet();
            case TransformationPackage.STAGING_TABLE__INPUT_SET:
                return getInputSet();
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
            case TransformationPackage.STAGING_TABLE__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSIVE:
                setRecursive(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_ALLOWED:
                setRecursionAllowed(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_CRITERIA:
                setRecursionCriteria((String)newValue);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT:
                setRecursionLimit(((Integer)newValue).intValue());
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT_ERROR_MODE:
                setRecursionLimitErrorMode((RecursionErrorMode)newValue);
                return;
            case TransformationPackage.STAGING_TABLE__COLUMNS:
                getColumns().clear();
                getColumns().addAll((Collection)newValue);
                return;
            case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                setMappingClassSet((MappingClassSet)newValue);
                return;
            case TransformationPackage.STAGING_TABLE__INPUT_SET:
                setInputSet((InputSet)newValue);
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
            case TransformationPackage.STAGING_TABLE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSIVE:
                setRecursive(RECURSIVE_EDEFAULT);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_ALLOWED:
                setRecursionAllowed(RECURSION_ALLOWED_EDEFAULT);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_CRITERIA:
                setRecursionCriteria(RECURSION_CRITERIA_EDEFAULT);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT:
                setRecursionLimit(RECURSION_LIMIT_EDEFAULT);
                return;
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT_ERROR_MODE:
                setRecursionLimitErrorMode(RECURSION_LIMIT_ERROR_MODE_EDEFAULT);
                return;
            case TransformationPackage.STAGING_TABLE__COLUMNS:
                getColumns().clear();
                return;
            case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                setMappingClassSet((MappingClassSet)null);
                return;
            case TransformationPackage.STAGING_TABLE__INPUT_SET:
                setInputSet((InputSet)null);
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
            case TransformationPackage.STAGING_TABLE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.STAGING_TABLE__RECURSIVE:
                return recursive != RECURSIVE_EDEFAULT;
            case TransformationPackage.STAGING_TABLE__RECURSION_ALLOWED:
                return recursionAllowed != RECURSION_ALLOWED_EDEFAULT;
            case TransformationPackage.STAGING_TABLE__RECURSION_CRITERIA:
                return RECURSION_CRITERIA_EDEFAULT == null ? recursionCriteria != null : !RECURSION_CRITERIA_EDEFAULT.equals(recursionCriteria);
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT:
                return recursionLimit != RECURSION_LIMIT_EDEFAULT;
            case TransformationPackage.STAGING_TABLE__RECURSION_LIMIT_ERROR_MODE:
                return recursionLimitErrorMode != RECURSION_LIMIT_ERROR_MODE_EDEFAULT;
            case TransformationPackage.STAGING_TABLE__COLUMNS:
                return columns != null && !columns.isEmpty();
            case TransformationPackage.STAGING_TABLE__MAPPING_CLASS_SET:
                return getMappingClassSet() != null;
            case TransformationPackage.STAGING_TABLE__INPUT_SET:
                return inputSet != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //StagingTableImpl
