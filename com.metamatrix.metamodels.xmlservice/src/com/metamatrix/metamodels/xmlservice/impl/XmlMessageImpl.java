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

package com.metamatrix.metamodels.xmlservice.impl;

import com.metamatrix.metamodels.xmlservice.XmlMessage;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.xsd.XSDElementDeclaration;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Message</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlMessageImpl#getContentElement <em>Content Element</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlMessageImpl extends XmlServiceComponentImpl implements XmlMessage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getContentElement() <em>Content Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getContentElement()
     * @generated
     * @ordered
     */
    protected XSDElementDeclaration contentElement = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlMessageImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlServicePackage.eINSTANCE.getXmlMessage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDElementDeclaration getContentElement() {
        if (contentElement != null && contentElement.eIsProxy()) {
            XSDElementDeclaration oldContentElement = contentElement;
            contentElement = (XSDElementDeclaration)eResolveProxy((InternalEObject)contentElement);
            if (contentElement != oldContentElement) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, XmlServicePackage.XML_MESSAGE__CONTENT_ELEMENT, oldContentElement, contentElement));
            }
        }
        return contentElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XSDElementDeclaration basicGetContentElement() {
        return contentElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setContentElement(XSDElementDeclaration newContentElement) {
        XSDElementDeclaration oldContentElement = contentElement;
        contentElement = newContentElement;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_MESSAGE__CONTENT_ELEMENT, oldContentElement, contentElement));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case XmlServicePackage.XML_MESSAGE__NAME:
                return getName();
            case XmlServicePackage.XML_MESSAGE__NAME_IN_SOURCE:
                return getNameInSource();
            case XmlServicePackage.XML_MESSAGE__CONTENT_ELEMENT:
                if (resolve) return getContentElement();
                return basicGetContentElement();
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
            case XmlServicePackage.XML_MESSAGE__NAME:
                setName((String)newValue);
                return;
            case XmlServicePackage.XML_MESSAGE__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case XmlServicePackage.XML_MESSAGE__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)newValue);
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
            case XmlServicePackage.XML_MESSAGE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlServicePackage.XML_MESSAGE__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case XmlServicePackage.XML_MESSAGE__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)null);
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
            case XmlServicePackage.XML_MESSAGE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlServicePackage.XML_MESSAGE__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case XmlServicePackage.XML_MESSAGE__CONTENT_ELEMENT:
                return contentElement != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //XmlMessageImpl
