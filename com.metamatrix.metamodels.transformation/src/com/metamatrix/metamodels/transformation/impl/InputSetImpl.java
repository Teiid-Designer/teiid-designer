/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.InputSet;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Input Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.InputSetImpl#getMappingClass <em>Mapping Class</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.InputSetImpl#getInputParameters <em>Input Parameters</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InputSetImpl extends EObjectImpl implements InputSet {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getInputParameters() <em>Input Parameters</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputParameters()
     * @generated
     * @ordered
     */
    protected EList inputParameters = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected InputSetImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getInputSet();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MappingClass getMappingClass() {
        if (eContainerFeatureID != TransformationPackage.INPUT_SET__MAPPING_CLASS) return null;
        return (MappingClass)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMappingClass(MappingClass newMappingClass) {
        if (newMappingClass != eContainer || (eContainerFeatureID != TransformationPackage.INPUT_SET__MAPPING_CLASS && newMappingClass != null)) {
            if (EcoreUtil.isAncestor(this, newMappingClass))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMappingClass != null)
                msgs = ((InternalEObject)newMappingClass).eInverseAdd(this, TransformationPackage.MAPPING_CLASS__INPUT_SET, MappingClass.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMappingClass, TransformationPackage.INPUT_SET__MAPPING_CLASS, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.INPUT_SET__MAPPING_CLASS, newMappingClass, newMappingClass));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getInputParameters() {
        if (inputParameters == null) {
            inputParameters = new EObjectContainmentWithInverseEList(InputParameter.class, this, TransformationPackage.INPUT_SET__INPUT_PARAMETERS, TransformationPackage.INPUT_PARAMETER__INPUT_SET);
        }
        return inputParameters;
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
                case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.INPUT_SET__MAPPING_CLASS, msgs);
                case TransformationPackage.INPUT_SET__INPUT_PARAMETERS:
                    return ((InternalEList)getInputParameters()).basicAdd(otherEnd, msgs);
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
                case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                    return eBasicSetContainer(null, TransformationPackage.INPUT_SET__MAPPING_CLASS, msgs);
                case TransformationPackage.INPUT_SET__INPUT_PARAMETERS:
                    return ((InternalEList)getInputParameters()).basicRemove(otherEnd, msgs);
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
                case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                    return eContainer.eInverseRemove(this, TransformationPackage.MAPPING_CLASS__INPUT_SET, MappingClass.class, msgs);
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
            case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                return getMappingClass();
            case TransformationPackage.INPUT_SET__INPUT_PARAMETERS:
                return getInputParameters();
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
            case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                setMappingClass((MappingClass)newValue);
                return;
            case TransformationPackage.INPUT_SET__INPUT_PARAMETERS:
                getInputParameters().clear();
                getInputParameters().addAll((Collection)newValue);
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
            case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                setMappingClass((MappingClass)null);
                return;
            case TransformationPackage.INPUT_SET__INPUT_PARAMETERS:
                getInputParameters().clear();
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
            case TransformationPackage.INPUT_SET__MAPPING_CLASS:
                return getMappingClass() != null;
            case TransformationPackage.INPUT_SET__INPUT_PARAMETERS:
                return inputParameters != null && !inputParameters.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //InputSetImpl
