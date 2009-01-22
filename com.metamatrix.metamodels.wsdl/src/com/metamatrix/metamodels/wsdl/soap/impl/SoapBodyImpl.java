/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import com.metamatrix.metamodels.wsdl.BindingParam;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.soap.SoapBody;
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
 * An implementation of the model object '<em><b>Body</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl#getBindingParam <em>Binding Param</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl#getEncodingStyles <em>Encoding Styles</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl#getParts <em>Parts</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SoapBodyImpl extends EObjectImpl implements SoapBody {
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
     * The cached value of the '{@link #getParts() <em>Parts</em>}' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getParts()
     * @generated
     * @ordered
     */
	protected EList parts = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SoapBodyImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return SoapPackage.eINSTANCE.getSoapBody();
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
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_BODY__USE, oldUse, use));
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
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_BODY__NAMESPACE, oldNamespace, namespace));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getEncodingStyles() {
        if (encodingStyles == null) {
            encodingStyles = new EDataTypeUniqueEList(String.class, this, SoapPackage.SOAP_BODY__ENCODING_STYLES);
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
            parts = new EDataTypeUniqueEList(String.class, this, SoapPackage.SOAP_BODY__PARTS);
        }
        return parts;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public BindingParam getBindingParam() {
        if (eContainerFeatureID != SoapPackage.SOAP_BODY__BINDING_PARAM) return null;
        return (BindingParam)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBindingParam(BindingParam newBindingParam) {
        if (newBindingParam != eContainer || (eContainerFeatureID != SoapPackage.SOAP_BODY__BINDING_PARAM && newBindingParam != null)) {
            if (EcoreUtil.isAncestor(this, newBindingParam))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBindingParam != null)
                msgs = ((InternalEObject)newBindingParam).eInverseAdd(this, WsdlPackage.BINDING_PARAM__SOAP_BODY, BindingParam.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBindingParam, SoapPackage.SOAP_BODY__BINDING_PARAM, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoapPackage.SOAP_BODY__BINDING_PARAM, newBindingParam, newBindingParam));
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
                case SoapPackage.SOAP_BODY__BINDING_PARAM:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, SoapPackage.SOAP_BODY__BINDING_PARAM, msgs);
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
                case SoapPackage.SOAP_BODY__BINDING_PARAM:
                    return eBasicSetContainer(null, SoapPackage.SOAP_BODY__BINDING_PARAM, msgs);
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
                case SoapPackage.SOAP_BODY__BINDING_PARAM:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING_PARAM__SOAP_BODY, BindingParam.class, msgs);
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
            case SoapPackage.SOAP_BODY__BINDING_PARAM:
                return getBindingParam();
            case SoapPackage.SOAP_BODY__USE:
                return getUse();
            case SoapPackage.SOAP_BODY__NAMESPACE:
                return getNamespace();
            case SoapPackage.SOAP_BODY__ENCODING_STYLES:
                return getEncodingStyles();
            case SoapPackage.SOAP_BODY__PARTS:
                return getParts();
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
            case SoapPackage.SOAP_BODY__BINDING_PARAM:
                setBindingParam((BindingParam)newValue);
                return;
            case SoapPackage.SOAP_BODY__USE:
                setUse((SoapUseType)newValue);
                return;
            case SoapPackage.SOAP_BODY__NAMESPACE:
                setNamespace((String)newValue);
                return;
            case SoapPackage.SOAP_BODY__ENCODING_STYLES:
                getEncodingStyles().clear();
                getEncodingStyles().addAll((Collection)newValue);
                return;
            case SoapPackage.SOAP_BODY__PARTS:
                getParts().clear();
                getParts().addAll((Collection)newValue);
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
            case SoapPackage.SOAP_BODY__BINDING_PARAM:
                setBindingParam((BindingParam)null);
                return;
            case SoapPackage.SOAP_BODY__USE:
                setUse(USE_EDEFAULT);
                return;
            case SoapPackage.SOAP_BODY__NAMESPACE:
                setNamespace(NAMESPACE_EDEFAULT);
                return;
            case SoapPackage.SOAP_BODY__ENCODING_STYLES:
                getEncodingStyles().clear();
                return;
            case SoapPackage.SOAP_BODY__PARTS:
                getParts().clear();
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
            case SoapPackage.SOAP_BODY__BINDING_PARAM:
                return getBindingParam() != null;
            case SoapPackage.SOAP_BODY__USE:
                return use != USE_EDEFAULT;
            case SoapPackage.SOAP_BODY__NAMESPACE:
                return NAMESPACE_EDEFAULT == null ? namespace != null : !NAMESPACE_EDEFAULT.equals(namespace);
            case SoapPackage.SOAP_BODY__ENCODING_STYLES:
                return encodingStyles != null && !encodingStyles.isEmpty();
            case SoapPackage.SOAP_BODY__PARTS:
                return parts != null && !parts.isEmpty();
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
        result.append(')');
        return result.toString();
    }

} //SoapBodyImpl
