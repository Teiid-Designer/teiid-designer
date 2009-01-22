/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.webservice.SampleFile;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sample File</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.SampleFileImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.SampleFileImpl#getUrl <em>Url</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.impl.SampleFileImpl#getSampleMessages <em>Sample Messages</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SampleFileImpl extends EObjectImpl implements SampleFile {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

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
     * The default value of the '{@link #getUrl() <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getUrl()
     * @generated
     * @ordered
     */
	protected static final String URL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getUrl()
     * @generated
     * @ordered
     */
	protected String url = URL_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected SampleFileImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getSampleFile();
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
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.SAMPLE_FILE__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getUrl() {
        return url;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setUrl(String newUrl) {
        String oldUrl = url;
        url = newUrl;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.SAMPLE_FILE__URL, oldUrl, url));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SampleMessages getSampleMessages() {
        if (eContainerFeatureID != WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES) return null;
        return (SampleMessages)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setSampleMessages(SampleMessages newSampleMessages) {
        if (newSampleMessages != eContainer || (eContainerFeatureID != WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES && newSampleMessages != null)) {
            if (EcoreUtil.isAncestor(this, newSampleMessages))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newSampleMessages != null)
                msgs = ((InternalEObject)newSampleMessages).eInverseAdd(this, WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES, SampleMessages.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newSampleMessages, WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES, newSampleMessages, newSampleMessages));
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
                case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES, msgs);
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
                case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                    return eBasicSetContainer(null, WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES, msgs);
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
                case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                    return eContainer.eInverseRemove(this, WebServicePackage.SAMPLE_MESSAGES__SAMPLE_FILES, SampleMessages.class, msgs);
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
            case WebServicePackage.SAMPLE_FILE__NAME:
                return getName();
            case WebServicePackage.SAMPLE_FILE__URL:
                return getUrl();
            case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                return getSampleMessages();
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
            case WebServicePackage.SAMPLE_FILE__NAME:
                setName((String)newValue);
                return;
            case WebServicePackage.SAMPLE_FILE__URL:
                setUrl((String)newValue);
                return;
            case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                setSampleMessages((SampleMessages)newValue);
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
            case WebServicePackage.SAMPLE_FILE__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WebServicePackage.SAMPLE_FILE__URL:
                setUrl(URL_EDEFAULT);
                return;
            case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                setSampleMessages((SampleMessages)null);
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
            case WebServicePackage.SAMPLE_FILE__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WebServicePackage.SAMPLE_FILE__URL:
                return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
            case WebServicePackage.SAMPLE_FILE__SAMPLE_MESSAGES:
                return getSampleMessages() != null;
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
        result.append(", url: "); //$NON-NLS-1$
        result.append(url);
        result.append(')');
        return result.toString();
    }

} //SampleFileImpl
