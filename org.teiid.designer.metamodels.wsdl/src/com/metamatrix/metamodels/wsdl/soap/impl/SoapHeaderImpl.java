/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import com.metamatrix.metamodels.wsdl.BindingParam;
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
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Header</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getBindingParam <em>Binding Param</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getMessagePart <em>Message Part</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getHeaderFault <em>Header Fault</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getEncodingStyles <em>Encoding Styles</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getParts <em>Parts</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoapHeaderImpl extends EObjectImpl implements SoapHeader {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * The cached value of the '{@link #getHeaderFault() <em>Header Fault</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getHeaderFault()
     * @generated
     * @ordered
     */
	protected SoapHeaderFault headerFault = null;

    /**
     * The default value of the '{@link #getUse() <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getUse()
     * @generated
     * @ordered
     */
	protected static final SoapUseType USE_EDEFAULT = SoapUseType.LITERAL_LITERAL;

    /**
     * The cached value of the '{@link #getUse() <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getUse()
     * @generated
     * @ordered
     */
	protected SoapUseType use = USE_EDEFAULT;

    /**
     * The default value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getNamespace()
     * @generated
     * @ordered
     */
	protected static final String NAMESPACE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNamespace() <em>Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getNamespace()
     * @generated
     * @ordered
     */
	protected String namespace = NAMESPACE_EDEFAULT;

    /**
     * The cached value of the '{@link #getEncodingStyles() <em>Encoding Styles</em>}' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getEncodingStyles()
     * @generated
     * @ordered
     */
	protected EList encodingStyles = null;

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
	protected SoapHeaderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return SoapPackage.eINSTANCE.getSoapHeader();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapUseType getUse() {
        return use;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setUse(SoapUseType newUse) {
        SoapUseType oldUse = use;
        use = newUse == null ? USE_EDEFAULT : newUse;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER__USE, oldUse, use));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getNamespace() {
        return namespace;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setNamespace(String newNamespace) {
        String oldNamespace = namespace;
        namespace = newNamespace;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER__NAMESPACE, oldNamespace, namespace));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getEncodingStyles() {
        if (encodingStyles == null) {
            encodingStyles = new EDataTypeUniqueEList(String.class, this, SoapPackage.SOAP_HEADER__ENCODING_STYLES);
        }
        return encodingStyles;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getParts() {
        if (parts == null) {
            parts = new EDataTypeUniqueEList(String.class, this, SoapPackage.SOAP_HEADER__PARTS);
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
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER__MESSAGE, oldMessage, message));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingParam getBindingParam() {
        if (eContainerFeatureID != SoapPackage.SOAP_HEADER__BINDING_PARAM) return null;
        return (BindingParam)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingParam(BindingParam newBindingParam) {
        if (newBindingParam != eContainer || (eContainerFeatureID != SoapPackage.SOAP_HEADER__BINDING_PARAM && newBindingParam != null)) {
            if (EcoreUtil.isAncestor(this, newBindingParam))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBindingParam != null)
                msgs = ((InternalEObject)newBindingParam).eInverseAdd(this, WsdlPackage.BINDING_PARAM__SOAP_HEADER, BindingParam.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBindingParam, SoapPackage.SOAP_HEADER__BINDING_PARAM, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER__BINDING_PARAM, newBindingParam, newBindingParam));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getMessagePart() {
        if (messagePart == null) {
            messagePart = new EObjectResolvingEList(MessagePart.class, this, SoapPackage.SOAP_HEADER__MESSAGE_PART);
        }
        return messagePart;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapHeaderFault getHeaderFault() {
        return headerFault;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain basicSetHeaderFault(SoapHeaderFault newHeaderFault, NotificationChain msgs) {
        SoapHeaderFault oldHeaderFault = headerFault;
        headerFault = newHeaderFault;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER__HEADER_FAULT, oldHeaderFault, newHeaderFault);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setHeaderFault(SoapHeaderFault newHeaderFault) {
        if (newHeaderFault != headerFault) {
            NotificationChain msgs = null;
            if (headerFault != null)
                msgs = ((InternalEObject)headerFault).eInverseRemove(this, SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER, SoapHeaderFault.class, msgs);
            if (newHeaderFault != null)
                msgs = ((InternalEObject)newHeaderFault).eInverseAdd(this, SoapPackage.SOAP_HEADER_FAULT__SOAP_HEADER, SoapHeaderFault.class, msgs);
            msgs = basicSetHeaderFault(newHeaderFault, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_HEADER__HEADER_FAULT, newHeaderFault, newHeaderFault));
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
                case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_HEADER__BINDING_PARAM, msgs);
                case SoapPackage.SOAP_HEADER__HEADER_FAULT:
                    if (headerFault != null)
                        msgs = ((InternalEObject)headerFault).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - SoapPackage.SOAP_HEADER__HEADER_FAULT, null, msgs);
                    return basicSetHeaderFault((SoapHeaderFault)otherEnd, msgs);
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
                case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                    return eBasicSetContainer(null, SoapPackage.SOAP_HEADER__BINDING_PARAM, msgs);
                case SoapPackage.SOAP_HEADER__HEADER_FAULT:
                    return basicSetHeaderFault(null, msgs);
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
                case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING_PARAM__SOAP_HEADER, BindingParam.class, msgs);
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
            case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                return getBindingParam();
            case SoapPackage.SOAP_HEADER__MESSAGE_PART:
                return getMessagePart();
            case SoapPackage.SOAP_HEADER__HEADER_FAULT:
                return getHeaderFault();
            case SoapPackage.SOAP_HEADER__USE:
                return getUse();
            case SoapPackage.SOAP_HEADER__NAMESPACE:
                return getNamespace();
            case SoapPackage.SOAP_HEADER__ENCODING_STYLES:
                return getEncodingStyles();
            case SoapPackage.SOAP_HEADER__PARTS:
                return getParts();
            case SoapPackage.SOAP_HEADER__MESSAGE:
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
            case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                setBindingParam((BindingParam)newValue);
                return;
            case SoapPackage.SOAP_HEADER__MESSAGE_PART:
                getMessagePart().clear();
                getMessagePart().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_HEADER__HEADER_FAULT:
                setHeaderFault((SoapHeaderFault)newValue);
                return;
            case SoapPackage.SOAP_HEADER__USE:
                setUse((SoapUseType)newValue);
                return;
            case SoapPackage.SOAP_HEADER__NAMESPACE:
                setNamespace((String)newValue);
                return;
            case SoapPackage.SOAP_HEADER__ENCODING_STYLES:
                getEncodingStyles().clear();
                getEncodingStyles().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_HEADER__PARTS:
                getParts().clear();
                getParts().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_HEADER__MESSAGE:
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
            case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                setBindingParam((BindingParam)null);
                return;
            case SoapPackage.SOAP_HEADER__MESSAGE_PART:
                getMessagePart().clear();
                return;
            case SoapPackage.SOAP_HEADER__HEADER_FAULT:
                setHeaderFault((SoapHeaderFault)null);
                return;
            case SoapPackage.SOAP_HEADER__USE:
                setUse(USE_EDEFAULT);
                return;
            case SoapPackage.SOAP_HEADER__NAMESPACE:
                setNamespace(NAMESPACE_EDEFAULT);
                return;
            case SoapPackage.SOAP_HEADER__ENCODING_STYLES:
                getEncodingStyles().clear();
                return;
            case SoapPackage.SOAP_HEADER__PARTS:
                getParts().clear();
                return;
            case SoapPackage.SOAP_HEADER__MESSAGE:
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
            case SoapPackage.SOAP_HEADER__BINDING_PARAM:
                return getBindingParam() != null;
            case SoapPackage.SOAP_HEADER__MESSAGE_PART:
                return messagePart != null && !messagePart.isEmpty();
            case SoapPackage.SOAP_HEADER__HEADER_FAULT:
                return headerFault != null;
            case SoapPackage.SOAP_HEADER__USE:
                return use != USE_EDEFAULT;
            case SoapPackage.SOAP_HEADER__NAMESPACE:
                return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
            case SoapPackage.SOAP_HEADER__ENCODING_STYLES:
                return encodingStyles != null && !encodingStyles.isEmpty();
            case SoapPackage.SOAP_HEADER__PARTS:
                return parts != null && !parts.isEmpty();
            case SoapPackage.SOAP_HEADER__MESSAGE:
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
        result.append(" (use: "); //$NON-NLS-1$
        result.append(use);
        result.append(", namespace: "); //$NON-NLS-1$
        result.append(namespace);
        result.append(", encodingStyles: "); //$NON-NLS-1$
        result.append(encodingStyles);
        result.append(", parts: "); //$NON-NLS-1$
        result.append(parts);
        result.append(", message: "); //$NON-NLS-1$
        result.append(message);
        result.append(')');
        return result.toString();
    }

} //SoapHeaderImpl
