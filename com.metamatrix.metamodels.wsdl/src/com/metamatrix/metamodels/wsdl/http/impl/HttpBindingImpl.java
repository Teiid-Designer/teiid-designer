/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.http.impl;

import com.metamatrix.metamodels.wsdl.Binding;
import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.http.HttpBinding;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;

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
 * An implementation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.http.impl.HttpBindingImpl#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.http.impl.HttpBindingImpl#getVerb <em>Verb</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class HttpBindingImpl extends EObjectImpl implements HttpBinding {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getVerb() <em>Verb</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getVerb()
     * @generated
     * @ordered
     */
	protected static final String VERB_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVerb() <em>Verb</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getVerb()
     * @generated
     * @ordered
     */
	protected String verb = VERB_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected HttpBindingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return HttpPackage.eINSTANCE.getHttpBinding();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getVerb() {
        return verb;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setVerb(String newVerb) {
        String oldVerb = verb;
        verb = newVerb;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HttpPackage.HTTP_BINDING__VERB, oldVerb, verb));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Binding getBinding() {
        if (eContainerFeatureID != HttpPackage.HTTP_BINDING__BINDING) return null;
        return (Binding)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setBinding(Binding newBinding) {
        if (newBinding != eContainer || (eContainerFeatureID != HttpPackage.HTTP_BINDING__BINDING && newBinding != null)) {
            if (EcoreUtil.isAncestor(this, newBinding))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newBinding != null)
                msgs = ((InternalEObject)newBinding).eInverseAdd(this, WsdlPackage.BINDING__HTTP_BINDING, Binding.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newBinding, HttpPackage.HTTP_BINDING__BINDING, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HttpPackage.HTTP_BINDING__BINDING, newBinding, newBinding));
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
                case HttpPackage.HTTP_BINDING__BINDING:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HttpPackage.HTTP_BINDING__BINDING, msgs);
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
                case HttpPackage.HTTP_BINDING__BINDING:
                    return eBasicSetContainer(null, HttpPackage.HTTP_BINDING__BINDING, msgs);
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
                case HttpPackage.HTTP_BINDING__BINDING:
                    return eContainer.eInverseRemove(this, WsdlPackage.BINDING__HTTP_BINDING, Binding.class, msgs);
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
            case HttpPackage.HTTP_BINDING__BINDING:
                return getBinding();
            case HttpPackage.HTTP_BINDING__VERB:
                return getVerb();
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
            case HttpPackage.HTTP_BINDING__BINDING:
                setBinding((Binding)newValue);
                return;
            case HttpPackage.HTTP_BINDING__VERB:
                setVerb((String)newValue);
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
            case HttpPackage.HTTP_BINDING__BINDING:
                setBinding((Binding)null);
                return;
            case HttpPackage.HTTP_BINDING__VERB:
                setVerb(VERB_EDEFAULT);
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
            case HttpPackage.HTTP_BINDING__BINDING:
                return getBinding() != null;
            case HttpPackage.HTTP_BINDING__VERB:
                return VERB_EDEFAULT == null ? verb != null : !VERB_EDEFAULT.equals(verb);
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
        result.append(" (verb: "); //$NON-NLS-1$
        result.append(verb);
        result.append(')');
        return result.toString();
    }

} //HttpBindingImpl
