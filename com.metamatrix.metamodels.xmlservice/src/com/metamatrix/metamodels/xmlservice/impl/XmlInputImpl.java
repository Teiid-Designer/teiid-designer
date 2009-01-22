/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.impl;

import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.xsd.XSDElementDeclaration;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Input</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlInputImpl#getOperation <em>Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlInputImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlInputImpl extends XmlMessageImpl implements XmlInput {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected EObject type = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlInputImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlServicePackage.eINSTANCE.getXmlInput();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlOperation getOperation() {
        if (eContainerFeatureID != XmlServicePackage.XML_INPUT__OPERATION) return null;
        return (XmlOperation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOperation(XmlOperation newOperation) {
        if (newOperation != eContainer || (eContainerFeatureID != XmlServicePackage.XML_INPUT__OPERATION && newOperation != null)) {
            if (EcoreUtil.isAncestor(this, newOperation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOperation != null)
                msgs = ((InternalEObject)newOperation).eInverseAdd(this, XmlServicePackage.XML_OPERATION__INPUTS, XmlOperation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOperation, XmlServicePackage.XML_INPUT__OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_INPUT__OPERATION, newOperation, newOperation));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getType() {
        if (type != null && type.eIsProxy()) {
            EObject oldType = type;
            type = eResolveProxy((InternalEObject)type);
            if (type != oldType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XmlServicePackage.XML_INPUT__TYPE, oldType, type));
            }
        }
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(EObject newType) {
        EObject oldType = type;
        type = newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_INPUT__TYPE, oldType, type));
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
                case XmlServicePackage.XML_INPUT__OPERATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlServicePackage.XML_INPUT__OPERATION, msgs);
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
                case XmlServicePackage.XML_INPUT__OPERATION:
                    return eBasicSetContainer(null, XmlServicePackage.XML_INPUT__OPERATION, msgs);
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
                case XmlServicePackage.XML_INPUT__OPERATION:
                    return eContainer.eInverseRemove(this, XmlServicePackage.XML_OPERATION__INPUTS, XmlOperation.class, msgs);
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
            case XmlServicePackage.XML_INPUT__NAME:
                return getName();
            case XmlServicePackage.XML_INPUT__NAME_IN_SOURCE:
                return getNameInSource();
            case XmlServicePackage.XML_INPUT__CONTENT_ELEMENT:
                if (resolve) return getContentElement();
                return basicGetContentElement();
            case XmlServicePackage.XML_INPUT__OPERATION:
                return getOperation();
            case XmlServicePackage.XML_INPUT__TYPE:
                if (resolve) return getType();
                return basicGetType();
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
            case XmlServicePackage.XML_INPUT__NAME:
                setName((String)newValue);
                return;
            case XmlServicePackage.XML_INPUT__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case XmlServicePackage.XML_INPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)newValue);
                return;
            case XmlServicePackage.XML_INPUT__OPERATION:
                setOperation((XmlOperation)newValue);
                return;
            case XmlServicePackage.XML_INPUT__TYPE:
                setType((EObject)newValue);
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
            case XmlServicePackage.XML_INPUT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlServicePackage.XML_INPUT__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case XmlServicePackage.XML_INPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)null);
                return;
            case XmlServicePackage.XML_INPUT__OPERATION:
                setOperation((XmlOperation)null);
                return;
            case XmlServicePackage.XML_INPUT__TYPE:
                setType((EObject)null);
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
            case XmlServicePackage.XML_INPUT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlServicePackage.XML_INPUT__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case XmlServicePackage.XML_INPUT__CONTENT_ELEMENT:
                return contentElement != null;
            case XmlServicePackage.XML_INPUT__OPERATION:
                return getOperation() != null;
            case XmlServicePackage.XML_INPUT__TYPE:
                return type != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //XmlInputImpl
