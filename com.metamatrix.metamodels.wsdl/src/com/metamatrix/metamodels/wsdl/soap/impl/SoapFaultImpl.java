/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import com.metamatrix.metamodels.wsdl.BindingFault;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.soap.SoapFault;
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
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fault</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapFaultImpl#getBindingFault <em>Binding Fault</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapFaultImpl#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapFaultImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapFaultImpl#getEncodingStyles <em>Encoding Styles</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoapFaultImpl extends EObjectImpl implements SoapFault {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

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
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SoapFaultImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return SoapPackage.eINSTANCE.getSoapFault();
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
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_FAULT__USE, oldUse, use));
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
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_FAULT__NAMESPACE, oldNamespace, namespace));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getEncodingStyles() {
        if (encodingStyles == null) {
            encodingStyles = new EDataTypeUniqueEList(String.class, this, SoapPackage.SOAP_FAULT__ENCODING_STYLES);
        }
        return encodingStyles;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingFault getBindingFault() {
        if (eContainerFeatureID != SoapPackage.SOAP_FAULT__BINDING_FAULT) return null;
        return (BindingFault)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingFault(BindingFault newBindingFault) {
        if (newBindingFault != eContainer || (eContainerFeatureID != SoapPackage.SOAP_FAULT__BINDING_FAULT && newBindingFault != null)) {
            if (EcoreUtil.isAncestor(this, newBindingFault))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBindingFault != null)
                msgs = ((InternalEObject)newBindingFault).eInverseAdd(this, WsdlPackage.BINDING_FAULT__SOAP_FAULT, BindingFault.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBindingFault, SoapPackage.SOAP_FAULT__BINDING_FAULT, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_FAULT__BINDING_FAULT, newBindingFault, newBindingFault));
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
                case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_FAULT__BINDING_FAULT, msgs);
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
                case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                    return eBasicSetContainer(null, SoapPackage.SOAP_FAULT__BINDING_FAULT, msgs);
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
                case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING_FAULT__SOAP_FAULT, BindingFault.class, msgs);
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
            case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                return getBindingFault();
            case SoapPackage.SOAP_FAULT__USE:
                return getUse();
            case SoapPackage.SOAP_FAULT__NAMESPACE:
                return getNamespace();
            case SoapPackage.SOAP_FAULT__ENCODING_STYLES:
                return getEncodingStyles();
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
            case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                setBindingFault((BindingFault)newValue);
                return;
            case SoapPackage.SOAP_FAULT__USE:
                setUse((SoapUseType)newValue);
                return;
            case SoapPackage.SOAP_FAULT__NAMESPACE:
                setNamespace((String)newValue);
                return;
            case SoapPackage.SOAP_FAULT__ENCODING_STYLES:
                getEncodingStyles().clear();
                getEncodingStyles().addAll((Collection)newValue);
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
            case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                setBindingFault((BindingFault)null);
                return;
            case SoapPackage.SOAP_FAULT__USE:
                setUse(USE_EDEFAULT);
                return;
            case SoapPackage.SOAP_FAULT__NAMESPACE:
                setNamespace(NAMESPACE_EDEFAULT);
                return;
            case SoapPackage.SOAP_FAULT__ENCODING_STYLES:
                getEncodingStyles().clear();
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
            case SoapPackage.SOAP_FAULT__BINDING_FAULT:
                return getBindingFault() != null;
            case SoapPackage.SOAP_FAULT__USE:
                return use != USE_EDEFAULT;
            case SoapPackage.SOAP_FAULT__NAMESPACE:
                return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
            case SoapPackage.SOAP_FAULT__ENCODING_STYLES:
                return encodingStyles != null && !encodingStyles.isEmpty();
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
        result.append(')');
        return result.toString();
    }

} //SoapFaultImpl
