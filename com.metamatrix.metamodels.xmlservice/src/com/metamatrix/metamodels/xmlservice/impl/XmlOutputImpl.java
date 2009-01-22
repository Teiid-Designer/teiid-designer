/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.impl;

import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlOutput;
import com.metamatrix.metamodels.xmlservice.XmlResult;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.xsd.XSDElementDeclaration;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Output</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlOutputImpl#getOperation <em>Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlOutputImpl#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlOutputImpl extends XmlMessageImpl implements XmlOutput {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getResult() <em>Result</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResult()
     * @generated
     * @ordered
     */
    protected XmlResult result = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlOutputImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlServicePackage.eINSTANCE.getXmlOutput();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlOperation getOperation() {
        if (eContainerFeatureID != XmlServicePackage.XML_OUTPUT__OPERATION) return null;
        return (XmlOperation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOperation(XmlOperation newOperation) {
        if (newOperation != eContainer || (eContainerFeatureID != XmlServicePackage.XML_OUTPUT__OPERATION && newOperation != null)) {
            if (EcoreUtil.isAncestor(this, newOperation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOperation != null)
                msgs = ((InternalEObject)newOperation).eInverseAdd(this, XmlServicePackage.XML_OPERATION__OUTPUT, XmlOperation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOperation, XmlServicePackage.XML_OUTPUT__OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_OUTPUT__OPERATION, newOperation, newOperation));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlResult getResult() {
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetResult(XmlResult newResult, NotificationChain msgs) {
        XmlResult oldResult = result;
        result = newResult;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_OUTPUT__RESULT, oldResult, newResult);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setResult(XmlResult newResult) {
        if (newResult != result) {
            NotificationChain msgs = null;
            if (result != null)
                msgs = ((InternalEObject)result).eInverseRemove(this, XmlServicePackage.XML_RESULT__OUTPUT, XmlResult.class, msgs);
            if (newResult != null)
                msgs = ((InternalEObject)newResult).eInverseAdd(this, XmlServicePackage.XML_RESULT__OUTPUT, XmlResult.class, msgs);
            msgs = basicSetResult(newResult, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_OUTPUT__RESULT, newResult, newResult));
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
                case XmlServicePackage.XML_OUTPUT__OPERATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, XmlServicePackage.XML_OUTPUT__OPERATION, msgs);
                case XmlServicePackage.XML_OUTPUT__RESULT:
                    if (result != null)
                        msgs = ((InternalEObject)result).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - XmlServicePackage.XML_OUTPUT__RESULT, null, msgs);
                    return basicSetResult((XmlResult)otherEnd, msgs);
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
                case XmlServicePackage.XML_OUTPUT__OPERATION:
                    return eBasicSetContainer(null, XmlServicePackage.XML_OUTPUT__OPERATION, msgs);
                case XmlServicePackage.XML_OUTPUT__RESULT:
                    return basicSetResult(null, msgs);
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
                case XmlServicePackage.XML_OUTPUT__OPERATION:
                    return eContainer.eInverseRemove(this, XmlServicePackage.XML_OPERATION__OUTPUT, XmlOperation.class, msgs);
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
            case XmlServicePackage.XML_OUTPUT__NAME:
                return getName();
            case XmlServicePackage.XML_OUTPUT__NAME_IN_SOURCE:
                return getNameInSource();
            case XmlServicePackage.XML_OUTPUT__CONTENT_ELEMENT:
                if (resolve) return getContentElement();
                return basicGetContentElement();
            case XmlServicePackage.XML_OUTPUT__OPERATION:
                return getOperation();
            case XmlServicePackage.XML_OUTPUT__RESULT:
                return getResult();
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
            case XmlServicePackage.XML_OUTPUT__NAME:
                setName((String)newValue);
                return;
            case XmlServicePackage.XML_OUTPUT__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case XmlServicePackage.XML_OUTPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)newValue);
                return;
            case XmlServicePackage.XML_OUTPUT__OPERATION:
                setOperation((XmlOperation)newValue);
                return;
            case XmlServicePackage.XML_OUTPUT__RESULT:
                setResult((XmlResult)newValue);
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
            case XmlServicePackage.XML_OUTPUT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlServicePackage.XML_OUTPUT__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case XmlServicePackage.XML_OUTPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)null);
                return;
            case XmlServicePackage.XML_OUTPUT__OPERATION:
                setOperation((XmlOperation)null);
                return;
            case XmlServicePackage.XML_OUTPUT__RESULT:
                setResult((XmlResult)null);
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
            case XmlServicePackage.XML_OUTPUT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlServicePackage.XML_OUTPUT__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case XmlServicePackage.XML_OUTPUT__CONTENT_ELEMENT:
                return contentElement != null;
            case XmlServicePackage.XML_OUTPUT__OPERATION:
                return getOperation() != null;
            case XmlServicePackage.XML_OUTPUT__RESULT:
                return result != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //XmlOutputImpl
