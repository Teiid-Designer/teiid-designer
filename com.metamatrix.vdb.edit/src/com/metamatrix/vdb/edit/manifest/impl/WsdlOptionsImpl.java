/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.VirtualDatabase;
import com.metamatrix.vdb.edit.manifest.WsdlOptions;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Wsdl Options</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.WsdlOptionsImpl#getTargetNamespaceUri <em>Target Namespace Uri</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.WsdlOptionsImpl#getDefaultNamespaceUri <em>Default Namespace Uri</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.WsdlOptionsImpl#getVirtualDatabase <em>Virtual Database</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WsdlOptionsImpl extends EObjectImpl implements WsdlOptions {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright (c) 2000-2005 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getTargetNamespaceUri() <em>Target Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTargetNamespaceUri()
     * @generated
     * @ordered
     */
	protected static final String TARGET_NAMESPACE_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTargetNamespaceUri() <em>Target Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getTargetNamespaceUri()
     * @generated
     * @ordered
     */
	protected String targetNamespaceUri = TARGET_NAMESPACE_URI_EDEFAULT;

    /**
     * The default value of the '{@link #getDefaultNamespaceUri() <em>Default Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getDefaultNamespaceUri()
     * @generated
     * @ordered
     */
	protected static final String DEFAULT_NAMESPACE_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDefaultNamespaceUri() <em>Default Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getDefaultNamespaceUri()
     * @generated
     * @ordered
     */
	protected String defaultNamespaceUri = DEFAULT_NAMESPACE_URI_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected WsdlOptionsImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return ManifestPackage.eINSTANCE.getWsdlOptions();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getTargetNamespaceUri() {
        return targetNamespaceUri;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setTargetNamespaceUri(String newTargetNamespaceUri) {
        String oldTargetNamespaceUri = targetNamespaceUri;
        targetNamespaceUri = newTargetNamespaceUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.WSDL_OPTIONS__TARGET_NAMESPACE_URI, oldTargetNamespaceUri, targetNamespaceUri));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getDefaultNamespaceUri() {
        return defaultNamespaceUri;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setDefaultNamespaceUri(String newDefaultNamespaceUri) {
        String oldDefaultNamespaceUri = defaultNamespaceUri;
        defaultNamespaceUri = newDefaultNamespaceUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.WSDL_OPTIONS__DEFAULT_NAMESPACE_URI, oldDefaultNamespaceUri, defaultNamespaceUri));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public VirtualDatabase getVirtualDatabase() {
        if (eContainerFeatureID != ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE) return null;
        return (VirtualDatabase)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setVirtualDatabase(VirtualDatabase newVirtualDatabase) {
        if (newVirtualDatabase != eContainer || (eContainerFeatureID != ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE && newVirtualDatabase != null)) {
            if (EcoreUtil.isAncestor(this, newVirtualDatabase))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newVirtualDatabase != null)
                msgs = ((InternalEObject)newVirtualDatabase).eInverseAdd(this, ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS, VirtualDatabase.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newVirtualDatabase, ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE, newVirtualDatabase, newVirtualDatabase));
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
                case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE, msgs);
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
                case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                    return eBasicSetContainer(null, ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE, msgs);
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
                case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                    return eContainer.eInverseRemove(this, ManifestPackage.VIRTUAL_DATABASE__WSDL_OPTIONS, VirtualDatabase.class, msgs);
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
            case ManifestPackage.WSDL_OPTIONS__TARGET_NAMESPACE_URI:
                return getTargetNamespaceUri();
            case ManifestPackage.WSDL_OPTIONS__DEFAULT_NAMESPACE_URI:
                return getDefaultNamespaceUri();
            case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                return getVirtualDatabase();
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
            case ManifestPackage.WSDL_OPTIONS__TARGET_NAMESPACE_URI:
                setTargetNamespaceUri((String)newValue);
                return;
            case ManifestPackage.WSDL_OPTIONS__DEFAULT_NAMESPACE_URI:
                setDefaultNamespaceUri((String)newValue);
                return;
            case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                setVirtualDatabase((VirtualDatabase)newValue);
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
            case ManifestPackage.WSDL_OPTIONS__TARGET_NAMESPACE_URI:
                setTargetNamespaceUri(TARGET_NAMESPACE_URI_EDEFAULT);
                return;
            case ManifestPackage.WSDL_OPTIONS__DEFAULT_NAMESPACE_URI:
                setDefaultNamespaceUri(DEFAULT_NAMESPACE_URI_EDEFAULT);
                return;
            case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                setVirtualDatabase((VirtualDatabase)null);
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
            case ManifestPackage.WSDL_OPTIONS__TARGET_NAMESPACE_URI:
                return TARGET_NAMESPACE_URI_EDEFAULT == null ? targetNamespaceUri != null : !TARGET_NAMESPACE_URI_EDEFAULT.equals(targetNamespaceUri);
            case ManifestPackage.WSDL_OPTIONS__DEFAULT_NAMESPACE_URI:
                return DEFAULT_NAMESPACE_URI_EDEFAULT == null ? defaultNamespaceUri != null : !DEFAULT_NAMESPACE_URI_EDEFAULT.equals(defaultNamespaceUri);
            case ManifestPackage.WSDL_OPTIONS__VIRTUAL_DATABASE:
                return getVirtualDatabase() != null;
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
        result.append(" (targetNamespaceUri: "); //$NON-NLS-1$
        result.append(targetNamespaceUri);
        result.append(", defaultNamespaceUri: "); //$NON-NLS-1$
        result.append(defaultNamespaceUri);
        result.append(')');
        return result.toString();
    }

} //WsdlOptionsImpl
