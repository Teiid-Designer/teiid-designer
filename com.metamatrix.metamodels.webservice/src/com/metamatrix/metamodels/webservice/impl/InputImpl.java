/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Input</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.InputImpl#getOperation <em>Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InputImpl extends MessageImpl implements Input {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected InputImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getInput();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Operation getOperation() {
        if (eContainerFeatureID != WebServicePackage.INPUT__OPERATION) return null;
        return (Operation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setOperation(Operation newOperation) {
        if (newOperation != eContainer || (eContainerFeatureID != WebServicePackage.INPUT__OPERATION && newOperation != null)) {
            if (EcoreUtil.isAncestor(this, newOperation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOperation != null)
                msgs = ((InternalEObject)newOperation).eInverseAdd(this, WebServicePackage.OPERATION__INPUT, Operation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOperation, WebServicePackage.INPUT__OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.INPUT__OPERATION, newOperation, newOperation));
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
                case WebServicePackage.INPUT__SAMPLES:
                    if (samples != null)
                        msgs = ((InternalEObject)samples).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WebServicePackage.INPUT__SAMPLES, null, msgs);
                    return basicSetSamples((SampleMessages)otherEnd, msgs);
                case WebServicePackage.INPUT__OPERATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WebServicePackage.INPUT__OPERATION, msgs);
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
                case WebServicePackage.INPUT__SAMPLES:
                    return basicSetSamples(null, msgs);
                case WebServicePackage.INPUT__OPERATION:
                    return eBasicSetContainer(null, WebServicePackage.INPUT__OPERATION, msgs);
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
                case WebServicePackage.INPUT__OPERATION:
                    return eContainer.eInverseRemove(this, WebServicePackage.OPERATION__INPUT, Operation.class, msgs);
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
            case WebServicePackage.INPUT__NAME:
                return getName();
            case WebServicePackage.INPUT__CONTENT_ELEMENT:
                if (resolve) return getContentElement();
                return basicGetContentElement();
            case WebServicePackage.INPUT__SAMPLES:
                return getSamples();
            case WebServicePackage.INPUT__CONTENT_COMPLEX_TYPE:
                if (resolve) return getContentComplexType();
                return basicGetContentComplexType();
            case WebServicePackage.INPUT__CONTENT_SIMPLE_TYPE:
                if (resolve) return getContentSimpleType();
                return basicGetContentSimpleType();
            case WebServicePackage.INPUT__OPERATION:
                return getOperation();
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
            case WebServicePackage.INPUT__NAME:
                setName((String)newValue);
                return;
            case WebServicePackage.INPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)newValue);
                return;
            case WebServicePackage.INPUT__SAMPLES:
                setSamples((SampleMessages)newValue);
                return;
            case WebServicePackage.INPUT__CONTENT_COMPLEX_TYPE:
                setContentComplexType((XSDComplexTypeDefinition)newValue);
                return;
            case WebServicePackage.INPUT__CONTENT_SIMPLE_TYPE:
                setContentSimpleType((XSDSimpleTypeDefinition)newValue);
                return;
            case WebServicePackage.INPUT__OPERATION:
                setOperation((Operation)newValue);
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
            case WebServicePackage.INPUT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WebServicePackage.INPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)null);
                return;
            case WebServicePackage.INPUT__SAMPLES:
                setSamples((SampleMessages)null);
                return;
            case WebServicePackage.INPUT__CONTENT_COMPLEX_TYPE:
                setContentComplexType((XSDComplexTypeDefinition)null);
                return;
            case WebServicePackage.INPUT__CONTENT_SIMPLE_TYPE:
                setContentSimpleType((XSDSimpleTypeDefinition)null);
                return;
            case WebServicePackage.INPUT__OPERATION:
                setOperation((Operation)null);
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
            case WebServicePackage.INPUT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WebServicePackage.INPUT__CONTENT_ELEMENT:
                return contentElement != null;
            case WebServicePackage.INPUT__SAMPLES:
                return samples != null;
            case WebServicePackage.INPUT__CONTENT_COMPLEX_TYPE:
                return contentComplexType != null;
            case WebServicePackage.INPUT__CONTENT_SIMPLE_TYPE:
                return contentSimpleType != null;
            case WebServicePackage.INPUT__OPERATION:
                return getOperation() != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //InputImpl
