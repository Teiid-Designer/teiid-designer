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

import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocument;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Output</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.OutputImpl#getOperation <em>Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.OutputImpl#getXmlDocument <em>Xml Document</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class OutputImpl extends MessageImpl implements Output {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getXmlDocument() <em>Xml Document</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getXmlDocument()
     * @generated
     * @ordered
     */
	protected XmlDocument xmlDocument = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected OutputImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getOutput();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Operation getOperation() {
        if (eContainerFeatureID != WebServicePackage.OUTPUT__OPERATION) return null;
        return (Operation)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setOperation(Operation newOperation) {
        if (newOperation != eContainer || (eContainerFeatureID != WebServicePackage.OUTPUT__OPERATION && newOperation != null)) {
            if (EcoreUtil.isAncestor(this, newOperation))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newOperation != null)
                msgs = ((InternalEObject)newOperation).eInverseAdd(this, WebServicePackage.OPERATION__OUTPUT, Operation.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newOperation, WebServicePackage.OUTPUT__OPERATION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.OUTPUT__OPERATION, newOperation, newOperation));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XmlDocument getXmlDocument() {
        if (xmlDocument != null && xmlDocument.eIsProxy()) {
            XmlDocument oldXmlDocument = xmlDocument;
            xmlDocument = (XmlDocument)eResolveProxy((InternalEObject)xmlDocument);
            if (xmlDocument != oldXmlDocument) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, WebServicePackage.OUTPUT__XML_DOCUMENT, oldXmlDocument, xmlDocument));
            }
        }
        return xmlDocument;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XmlDocument basicGetXmlDocument() {
        return xmlDocument;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setXmlDocument(XmlDocument newXmlDocument) {
        XmlDocument oldXmlDocument = xmlDocument;
        xmlDocument = newXmlDocument;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.OUTPUT__XML_DOCUMENT, oldXmlDocument, xmlDocument));
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
                case WebServicePackage.OUTPUT__SAMPLES:
                    if (samples != null)
                        msgs = ((InternalEObject)samples).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WebServicePackage.OUTPUT__SAMPLES, null, msgs);
                    return basicSetSamples((SampleMessages)otherEnd, msgs);
                case WebServicePackage.OUTPUT__OPERATION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WebServicePackage.OUTPUT__OPERATION, msgs);
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
                case WebServicePackage.OUTPUT__SAMPLES:
                    return basicSetSamples(null, msgs);
                case WebServicePackage.OUTPUT__OPERATION:
                    return eBasicSetContainer(null, WebServicePackage.OUTPUT__OPERATION, msgs);
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
                case WebServicePackage.OUTPUT__OPERATION:
                    return eContainer.eInverseRemove(this, WebServicePackage.OPERATION__OUTPUT, Operation.class, msgs);
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
            case WebServicePackage.OUTPUT__NAME:
                return getName();
            case WebServicePackage.OUTPUT__CONTENT_ELEMENT:
                if (resolve) return getContentElement();
                return basicGetContentElement();
            case WebServicePackage.OUTPUT__SAMPLES:
                return getSamples();
            case WebServicePackage.OUTPUT__CONTENT_COMPLEX_TYPE:
                if (resolve) return getContentComplexType();
                return basicGetContentComplexType();
            case WebServicePackage.OUTPUT__CONTENT_SIMPLE_TYPE:
                if (resolve) return getContentSimpleType();
                return basicGetContentSimpleType();
            case WebServicePackage.OUTPUT__OPERATION:
                return getOperation();
            case WebServicePackage.OUTPUT__XML_DOCUMENT:
                if (resolve) return getXmlDocument();
                return basicGetXmlDocument();
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
            case WebServicePackage.OUTPUT__NAME:
                setName((String)newValue);
                return;
            case WebServicePackage.OUTPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)newValue);
                return;
            case WebServicePackage.OUTPUT__SAMPLES:
                setSamples((SampleMessages)newValue);
                return;
            case WebServicePackage.OUTPUT__CONTENT_COMPLEX_TYPE:
                setContentComplexType((XSDComplexTypeDefinition)newValue);
                return;
            case WebServicePackage.OUTPUT__CONTENT_SIMPLE_TYPE:
                setContentSimpleType((XSDSimpleTypeDefinition)newValue);
                return;
            case WebServicePackage.OUTPUT__OPERATION:
                setOperation((Operation)newValue);
                return;
            case WebServicePackage.OUTPUT__XML_DOCUMENT:
                setXmlDocument((XmlDocument)newValue);
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
            case WebServicePackage.OUTPUT__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WebServicePackage.OUTPUT__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)null);
                return;
            case WebServicePackage.OUTPUT__SAMPLES:
                setSamples((SampleMessages)null);
                return;
            case WebServicePackage.OUTPUT__CONTENT_COMPLEX_TYPE:
                setContentComplexType((XSDComplexTypeDefinition)null);
                return;
            case WebServicePackage.OUTPUT__CONTENT_SIMPLE_TYPE:
                setContentSimpleType((XSDSimpleTypeDefinition)null);
                return;
            case WebServicePackage.OUTPUT__OPERATION:
                setOperation((Operation)null);
                return;
            case WebServicePackage.OUTPUT__XML_DOCUMENT:
                setXmlDocument((XmlDocument)null);
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
            case WebServicePackage.OUTPUT__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WebServicePackage.OUTPUT__CONTENT_ELEMENT:
                return contentElement != null;
            case WebServicePackage.OUTPUT__SAMPLES:
                return samples != null;
            case WebServicePackage.OUTPUT__CONTENT_COMPLEX_TYPE:
                return contentComplexType != null;
            case WebServicePackage.OUTPUT__CONTENT_SIMPLE_TYPE:
                return contentSimpleType != null;
            case WebServicePackage.OUTPUT__OPERATION:
                return getOperation() != null;
            case WebServicePackage.OUTPUT__XML_DOCUMENT:
                return xmlDocument != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //OutputImpl
