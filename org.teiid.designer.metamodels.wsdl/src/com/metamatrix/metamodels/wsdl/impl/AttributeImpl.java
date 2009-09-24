/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.impl;

import com.metamatrix.metamodels.wsdl.Attribute;
import com.metamatrix.metamodels.wsdl.AttributeOwner;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.AttributeImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.AttributeImpl#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.AttributeImpl#getValue <em>Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.AttributeImpl#getNamespaceUri <em>Namespace Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.impl.AttributeImpl#getAttributeOwner <em>Attribute Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AttributeImpl extends EObjectImpl implements Attribute {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
	protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
	protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getPrefix()
     * @generated
     * @ordered
     */
	protected static final String PREFIX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPrefix() <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getPrefix()
     * @generated
     * @ordered
     */
	protected String prefix = PREFIX_EDEFAULT;

    /**
     * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
	protected static final String VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getValue()
     * @generated
     * @ordered
     */
	protected String value = VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getNamespaceUri() <em>Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getNamespaceUri()
     * @generated
     * @ordered
     */
	protected static final String NAMESPACE_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNamespaceUri() <em>Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getNamespaceUri()
     * @generated
     * @ordered
     */
	protected String namespaceUri = NAMESPACE_URI_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected AttributeImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WsdlPackage.eINSTANCE.getAttribute();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.ATTRIBUTE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getPrefix() {
        return prefix;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setPrefix(String newPrefix) {
        String oldPrefix = prefix;
        prefix = newPrefix;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.ATTRIBUTE__PREFIX, oldPrefix, prefix));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getValue() {
        return value;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setValue(String newValue) {
        String oldValue = value;
        value = newValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.ATTRIBUTE__VALUE, oldValue, value));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getNamespaceUri() {
        return namespaceUri;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setNamespaceUri(String newNamespaceUri) {
        String oldNamespaceUri = namespaceUri;
        namespaceUri = newNamespaceUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.ATTRIBUTE__NAMESPACE_URI, oldNamespaceUri, namespaceUri));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public AttributeOwner getAttributeOwner() {
        if (eContainerFeatureID != WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER) return null;
        return (AttributeOwner)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setAttributeOwner(AttributeOwner newAttributeOwner) {
        if (newAttributeOwner != eContainer || (eContainerFeatureID != WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER && newAttributeOwner != null)) {
            if (EcoreUtil.isAncestor(this, newAttributeOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newAttributeOwner != null)
                msgs = ((InternalEObject)newAttributeOwner).eInverseAdd(this, WsdlPackage.ATTRIBUTE_OWNER__ATTRIBUTES, AttributeOwner.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newAttributeOwner, WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER, newAttributeOwner, newAttributeOwner));
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
                case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER, msgs);
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
                case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                    return eBasicSetContainer(null, WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER, msgs);
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
                case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                    return eContainer.eInverseRemove(this, WsdlPackage.ATTRIBUTE_OWNER__ATTRIBUTES, AttributeOwner.class, msgs);
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
            case WsdlPackage.ATTRIBUTE__NAME:
                return getName();
            case WsdlPackage.ATTRIBUTE__PREFIX:
                return getPrefix();
            case WsdlPackage.ATTRIBUTE__VALUE:
                return getValue();
            case WsdlPackage.ATTRIBUTE__NAMESPACE_URI:
                return getNamespaceUri();
            case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                return getAttributeOwner();
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
            case WsdlPackage.ATTRIBUTE__NAME:
                setName((String)newValue);
                return;
            case WsdlPackage.ATTRIBUTE__PREFIX:
                setPrefix((String)newValue);
                return;
            case WsdlPackage.ATTRIBUTE__VALUE:
                setValue((String)newValue);
                return;
            case WsdlPackage.ATTRIBUTE__NAMESPACE_URI:
                setNamespaceUri((String)newValue);
                return;
            case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                setAttributeOwner((AttributeOwner)newValue);
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
            case WsdlPackage.ATTRIBUTE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WsdlPackage.ATTRIBUTE__PREFIX:
                setPrefix(PREFIX_EDEFAULT);
                return;
            case WsdlPackage.ATTRIBUTE__VALUE:
                setValue(VALUE_EDEFAULT);
                return;
            case WsdlPackage.ATTRIBUTE__NAMESPACE_URI:
                setNamespaceUri(NAMESPACE_URI_EDEFAULT);
                return;
            case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                setAttributeOwner((AttributeOwner)null);
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
            case WsdlPackage.ATTRIBUTE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WsdlPackage.ATTRIBUTE__PREFIX:
                return PREFIX_EDEFAULT == null ? prefix != null : !PREFIX_EDEFAULT.equals(prefix);
            case WsdlPackage.ATTRIBUTE__VALUE:
                return VALUE_EDEFAULT == null ? value != null : !VALUE_EDEFAULT.equals(value);
            case WsdlPackage.ATTRIBUTE__NAMESPACE_URI:
                return NAMESPACE_URI_EDEFAULT == null ? namespaceUri != null : !NAMESPACE_URI_EDEFAULT.equals(namespaceUri);
            case WsdlPackage.ATTRIBUTE__ATTRIBUTE_OWNER:
                return getAttributeOwner() != null;
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
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", prefix: "); //$NON-NLS-1$
        result.append(prefix);
        result.append(", value: "); //$NON-NLS-1$
        result.append(value);
        result.append(", namespaceUri: "); //$NON-NLS-1$
        result.append(namespaceUri);
        result.append(')');
        return result.toString();
    }

} //AttributeImpl
