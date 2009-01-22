/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.MessagePart;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
import com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapUseType;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Header Fault</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderFaultImpl#getMessagePart <em>Message Part</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderFaultImpl#getSoapHeader <em>Soap Header</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderFaultImpl#getParts <em>Parts</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderFaultImpl#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoapHeaderFaultImpl extends SoapFaultImpl implements SoapHeaderFault {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getMessagePart() <em>Message Part</em>}' reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMessagePart()
     * @generated
     * @ordered
     */
	protected EList messagePart = null;

    /**
     * The cached value of the '{@link #getParts() <em>Parts</em>}' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getParts()
     * @generated
     * @ordered
     */
	protected EList parts = null;

    /**
     * The default value of the '{@link #getMessage() <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMessage()
     * @generated
     * @ordered
     */
	protected static final String MESSAGE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMessage()
     * @generated
     * @ordered
     */
	protected String message = MESSAGE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SoapHeaderFaultImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return SoapPackage.eINSTANCE.getSoapHeaderFault();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getParts() {
        if (parts == null) {
            parts = new EDataTypeUniqueEList(String.class, this, SoapPackage.SOAP_HEADER_FAULT__PARTS);
        }
        return parts;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getMessage() {
        return message;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMessage(String newMessage) {
        String oldMessage = message;
        message = newMessage;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER_FAULT__MESSAGE, oldMessage, message));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getMessagePart() {
        if (messagePart == null) {
            messagePart = new EObjectResolvingEList(MessagePart.class, this, SoapPackage.SOAP_HEADER_FAULT__MESSAGE_PART);
        }
        return messagePart;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapHeader getSoapHeader() {
        if (eContainerFeatureID != SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER) return null;
        return (SoapHeader)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSoapHeader(SoapHeader newSoapHeader) {
        if (newSoapHeader != eContainer || (eContainerFeatureID != SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER && newSoapHeader != null)) {
            if (EcoreUtil.isAncestor(this, newSoapHeader))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSoapHeader != null)
                msgs = ((InternalEObject)newSoapHeader).eInverseAdd(this, SoapPackage.SOAP_HEADER__HEADER_FAULT, SoapHeader.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSoapHeader, SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER, newSoapHeader, newSoapHeader));
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
                case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT, msgs);
                case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER, msgs);
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
                case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                    return eBasicSetContainer(null, SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT, msgs);
                case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                    return eBasicSetContainer(null, SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER, msgs);
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
                case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING_FAULT__SOAP_FAULT, BindingFault.class, msgs);
                case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                    return eContainer.eInverseRemove(this, SoapPackage.SOAP_HEADER__HEADER_FAULT, SoapHeader.class, msgs);
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
            case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                return getBindingFault();
            case SoapPackage.SOAP_HEADER_FAULT__USE:
                return getUse();
            case SoapPackage.SOAP_HEADER_FAULT__NAMESPACE:
                return getNamespace();
            case SoapPackage.SOAP_HEADER_FAULT__ENCODING_STYLES:
                return getEncodingStyles();
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE_PART:
                return getMessagePart();
            case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                return getSoapHeader();
            case SoapPackage.SOAP_HEADER_FAULT__PARTS:
                return getParts();
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE:
                return getMessage();
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
            case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                setBindingFault((BindingFault)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__USE:
                setUse((SoapUseType)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__NAMESPACE:
                setNamespace((String)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__ENCODING_STYLES:
                getEncodingStyles().clear();
                getEncodingStyles().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE_PART:
                getMessagePart().clear();
                getMessagePart().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                setSoapHeader((SoapHeader)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__PARTS:
                getParts().clear();
                getParts().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE:
                setMessage((String)newValue);
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
            case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                setBindingFault((BindingFault)null);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__USE:
                setUse(USE_EDEFAULT);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__NAMESPACE:
                setNamespace(NAMESPACE_EDEFAULT);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__ENCODING_STYLES:
                getEncodingStyles().clear();
                return;
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE_PART:
                getMessagePart().clear();
                return;
            case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                setSoapHeader((SoapHeader)null);
                return;
            case SoapPackage.SOAP_HEADER_FAULT__PARTS:
                getParts().clear();
                return;
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE:
                setMessage(MESSAGE_EDEFAULT);
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
            case SoapPackage.SOAP_HEADER_FAULT__BINDING_FAULT:
                return getBindingFault() != null;
            case SoapPackage.SOAP_HEADER_FAULT__USE:
                return use != USE_EDEFAULT;
            case SoapPackage.SOAP_HEADER_FAULT__NAMESPACE:
                return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
            case SoapPackage.SOAP_HEADER_FAULT__ENCODING_STYLES:
                return encodingStyles != null && !encodingStyles.isEmpty();
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE_PART:
                return messagePart != null && !messagePart.isEmpty();
            case SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER:
                return getSoapHeader() != null;
            case SoapPackage.SOAP_HEADER_FAULT__PARTS:
                return parts != null && !parts.isEmpty();
            case SoapPackage.SOAP_HEADER_FAULT__MESSAGE:
                return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (parts: "); //$NON-NLS-1$
        result.append(parts);
        result.append(", message: "); //$NON-NLS-1$
        result.append(message);
        result.append(')');
        return result.toString();
    }

} //SoapHeaderFaultImpl
