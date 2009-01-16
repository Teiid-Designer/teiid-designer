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
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.SampleFromXsd;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Message</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.MessageImpl#getContentElement <em>Content Element</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.MessageImpl#getSamples <em>Samples</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.MessageImpl#getContentComplexType <em>Content Complex Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.MessageImpl#getContentSimpleType <em>Content Simple Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class MessageImpl extends WebServiceComponentImpl implements Message {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

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
     * The cached value of the '{@link #getSamples() <em>Samples</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getSamples()
     * @generated
     * @ordered
     */
	protected SampleMessages samples = null;

    /**
     * The cached value of the '{@link #getContentComplexType() <em>Content Complex Type</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getContentComplexType()
     * @generated
     * @ordered
     */
	protected XSDComplexTypeDefinition contentComplexType = null;

    /**
     * The cached value of the '{@link #getContentSimpleType() <em>Content Simple Type</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getContentSimpleType()
     * @generated
     * @ordered
     */
	protected XSDSimpleTypeDefinition contentSimpleType = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected MessageImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getMessage();
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
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, WebServicePackage.MESSAGE__CONTENT_ELEMENT, oldContentElement, contentElement));
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
	 * @generated NOT
	 */
	public void setContentElement(XSDElementDeclaration newContentElement)
	{
		XSDElementDeclaration oldContentElement = contentElement;
        if ( oldContentElement != newContentElement ) {
            clearSampleFromXsd();
        }
		contentElement = newContentElement;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__CONTENT_ELEMENT, oldContentElement, contentElement));
	}
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setContentElementGen(XSDElementDeclaration newContentElement) {
        XSDElementDeclaration oldContentElement = contentElement;
        contentElement = newContentElement;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__CONTENT_ELEMENT, oldContentElement, contentElement));
    }

	/** 
     * 
     * @since 4.2
     */
    protected void clearSampleFromXsd() {
        if ( samples != null ) {
            final SampleFromXsd xsdSample = samples.getSampleFromXsd();
            if ( xsdSample != null ) {
                xsdSample.setSampleFragment(null);
            }
        }
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SampleMessages getSamples() {
        return samples;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetSamples(SampleMessages newSamples, NotificationChain msgs) {
        SampleMessages oldSamples = samples;
        samples = newSamples;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__SAMPLES, oldSamples, newSamples);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSamples(SampleMessages newSamples) {
        if (newSamples != samples) {
            NotificationChain msgs = null;
            if (samples != null)
                msgs = ((InternalEObject)samples).eInverseRemove(this, WebServicePackage.SAMPLE_MESSAGES__MESSAGE, SampleMessages.class, msgs);
            if (newSamples != null)
                msgs = ((InternalEObject)newSamples).eInverseAdd(this, WebServicePackage.SAMPLE_MESSAGES__MESSAGE, SampleMessages.class, msgs);
            msgs = basicSetSamples(newSamples, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__SAMPLES, newSamples, newSamples));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XSDComplexTypeDefinition getContentComplexType() {
        if (contentComplexType != null && contentComplexType.eIsProxy()) {
            XSDComplexTypeDefinition oldContentComplexType = contentComplexType;
            contentComplexType = (XSDComplexTypeDefinition)eResolveProxy((InternalEObject)contentComplexType);
            if (contentComplexType != oldContentComplexType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE, oldContentComplexType, contentComplexType));
            }
        }
        return contentComplexType;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XSDComplexTypeDefinition basicGetContentComplexType() {
        return contentComplexType;
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setContentComplexType(XSDComplexTypeDefinition newContentComplexType) {
		XSDComplexTypeDefinition oldContentComplexType = contentComplexType;
        if ( oldContentComplexType != newContentComplexType ) {
            clearSampleFromXsd();
        }
		contentComplexType = newContentComplexType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE, oldContentComplexType, contentComplexType));
	}

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setContentComplexTypeGen(XSDComplexTypeDefinition newContentComplexType) {
        XSDComplexTypeDefinition oldContentComplexType = contentComplexType;
        contentComplexType = newContentComplexType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE, oldContentComplexType, contentComplexType));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XSDSimpleTypeDefinition getContentSimpleType() {
        if (contentSimpleType != null && contentSimpleType.eIsProxy()) {
            XSDSimpleTypeDefinition oldContentSimpleType = contentSimpleType;
            contentSimpleType = (XSDSimpleTypeDefinition)eResolveProxy((InternalEObject)contentSimpleType);
            if (contentSimpleType != oldContentSimpleType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE, oldContentSimpleType, contentSimpleType));
            }
        }
        return contentSimpleType;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public XSDSimpleTypeDefinition basicGetContentSimpleType() {
        return contentSimpleType;
    }

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setContentSimpleType(XSDSimpleTypeDefinition newContentSimpleType) {
        XSDSimpleTypeDefinition oldContentSimpleType = contentSimpleType;
        if ( oldContentSimpleType != newContentSimpleType ) {
            clearSampleFromXsd();
        }
		contentSimpleType = newContentSimpleType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE, oldContentSimpleType, contentSimpleType));
	}
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setContentSimpleTypeGen(XSDSimpleTypeDefinition newContentSimpleType) {
        XSDSimpleTypeDefinition oldContentSimpleType = contentSimpleType;
        contentSimpleType = newContentSimpleType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE, oldContentSimpleType, contentSimpleType));
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
                case WebServicePackage.MESSAGE__SAMPLES:
                    if (samples != null)
                        msgs = ((InternalEObject)samples).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WebServicePackage.MESSAGE__SAMPLES, null, msgs);
                    return basicSetSamples((SampleMessages)otherEnd, msgs);
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
                case WebServicePackage.MESSAGE__SAMPLES:
                    return basicSetSamples(null, msgs);
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
            case WebServicePackage.MESSAGE__NAME:
                return getName();
            case WebServicePackage.MESSAGE__CONTENT_ELEMENT:
                if (resolve) return getContentElement();
                return basicGetContentElement();
            case WebServicePackage.MESSAGE__SAMPLES:
                return getSamples();
            case WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE:
                if (resolve) return getContentComplexType();
                return basicGetContentComplexType();
            case WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE:
                if (resolve) return getContentSimpleType();
                return basicGetContentSimpleType();
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
            case WebServicePackage.MESSAGE__NAME:
                setName((String)newValue);
                return;
            case WebServicePackage.MESSAGE__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)newValue);
                return;
            case WebServicePackage.MESSAGE__SAMPLES:
                setSamples((SampleMessages)newValue);
                return;
            case WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE:
                setContentComplexType((XSDComplexTypeDefinition)newValue);
                return;
            case WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE:
                setContentSimpleType((XSDSimpleTypeDefinition)newValue);
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
            case WebServicePackage.MESSAGE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WebServicePackage.MESSAGE__CONTENT_ELEMENT:
                setContentElement((XSDElementDeclaration)null);
                return;
            case WebServicePackage.MESSAGE__SAMPLES:
                setSamples((SampleMessages)null);
                return;
            case WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE:
                setContentComplexType((XSDComplexTypeDefinition)null);
                return;
            case WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE:
                setContentSimpleType((XSDSimpleTypeDefinition)null);
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
            case WebServicePackage.MESSAGE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WebServicePackage.MESSAGE__CONTENT_ELEMENT:
                return contentElement != null;
            case WebServicePackage.MESSAGE__SAMPLES:
                return samples != null;
            case WebServicePackage.MESSAGE__CONTENT_COMPLEX_TYPE:
                return contentComplexType != null;
            case WebServicePackage.MESSAGE__CONTENT_SIMPLE_TYPE:
                return contentSimpleType != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //MessageImpl
