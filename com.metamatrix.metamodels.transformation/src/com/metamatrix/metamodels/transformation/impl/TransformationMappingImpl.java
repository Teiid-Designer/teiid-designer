/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingImpl;

import com.metamatrix.metamodels.transformation.TransformationMapping;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class TransformationMappingImpl extends MappingImpl implements TransformationMapping {
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
    protected TransformationMappingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getTransformationMapping();
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
                case TransformationPackage.TRANSFORMATION_MAPPING__HELPER:
                    if (helper != null)
                        msgs = ((InternalEObject)helper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TransformationPackage.TRANSFORMATION_MAPPING__HELPER, null, msgs);
                    return basicSetHelper((MappingHelper)otherEnd, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING__NESTED:
                    return ((InternalEList)getNested()).basicAdd(otherEnd, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN, msgs);
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
                case TransformationPackage.TRANSFORMATION_MAPPING__HELPER:
                    return basicSetHelper(null, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING__NESTED:
                    return ((InternalEList)getNested()).basicRemove(otherEnd, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                    return eBasicSetContainer(null, TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN, msgs);
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
                case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING__NESTED, Mapping.class, msgs);
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
            case TransformationPackage.TRANSFORMATION_MAPPING__HELPER:
                return getHelper();
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED:
                return getNested();
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                return getNestedIn();
            case TransformationPackage.TRANSFORMATION_MAPPING__INPUTS:
                return getInputs();
            case TransformationPackage.TRANSFORMATION_MAPPING__OUTPUTS:
                return getOutputs();
            case TransformationPackage.TRANSFORMATION_MAPPING__TYPE_MAPPING:
                if (resolve) return getTypeMapping();
                return basicGetTypeMapping();
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
            case TransformationPackage.TRANSFORMATION_MAPPING__HELPER:
                setHelper((MappingHelper)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED:
                getNested().clear();
                getNested().addAll((Collection)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                setNestedIn((Mapping)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__INPUTS:
                getInputs().clear();
                getInputs().addAll((Collection)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__OUTPUTS:
                getOutputs().clear();
                getOutputs().addAll((Collection)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__TYPE_MAPPING:
                setTypeMapping((Mapping)newValue);
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
            case TransformationPackage.TRANSFORMATION_MAPPING__HELPER:
                setHelper((MappingHelper)null);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED:
                getNested().clear();
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                setNestedIn((Mapping)null);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__INPUTS:
                getInputs().clear();
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__OUTPUTS:
                getOutputs().clear();
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING__TYPE_MAPPING:
                setTypeMapping((Mapping)null);
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
            case TransformationPackage.TRANSFORMATION_MAPPING__HELPER:
                return helper != null;
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED:
                return nested != null && !nested.isEmpty();
            case TransformationPackage.TRANSFORMATION_MAPPING__NESTED_IN:
                return getNestedIn() != null;
            case TransformationPackage.TRANSFORMATION_MAPPING__INPUTS:
                return inputs != null && !inputs.isEmpty();
            case TransformationPackage.TRANSFORMATION_MAPPING__OUTPUTS:
                return outputs != null && !outputs.isEmpty();
            case TransformationPackage.TRANSFORMATION_MAPPING__TYPE_MAPPING:
                return typeMapping != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //TransformationMappingImpl
