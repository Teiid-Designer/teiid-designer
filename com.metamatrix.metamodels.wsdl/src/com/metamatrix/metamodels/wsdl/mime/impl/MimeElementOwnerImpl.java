/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime.impl;

import com.metamatrix.metamodels.wsdl.mime.MimeElement;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Element Owner</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeElementOwnerImpl#getMimeElements <em>Mime Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MimeElementOwnerImpl extends EObjectImpl implements MimeElementOwner {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getMimeElements() <em>Mime Elements</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMimeElements()
	 * @generated
	 * @ordered
	 */
	protected EList mimeElements = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MimeElementOwnerImpl()
	{
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    protected EClass eStaticClass()
	{
		return MimePackage.eINSTANCE.getMimeElementOwner();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getMimeElements()
	{
		if (mimeElements == null) {
			mimeElements = new EObjectContainmentWithInverseEList(MimeElement.class, this, MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS, MimePackage.MIME_ELEMENT__MIME_ELEMENT_OWNER);
		}
		return mimeElements;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
	{
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS:
					return ((InternalEList)getMimeElements()).basicAdd(otherEnd, msgs);
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
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs)
	{
		if (featureID >= 0) {
			switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
				case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS:
					return ((InternalEList)getMimeElements()).basicRemove(otherEnd, msgs);
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
    public Object eGet(EStructuralFeature eFeature, boolean resolve)
	{
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS:
				return getMimeElements();
		}
		return eDynamicGet(eFeature, resolve);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
    public void eSet(EStructuralFeature eFeature, Object newValue)
	{
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS:
				getMimeElements().clear();
				getMimeElements().addAll((Collection)newValue);
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
    public void eUnset(EStructuralFeature eFeature)
	{
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS:
				getMimeElements().clear();
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
    public boolean eIsSet(EStructuralFeature eFeature)
	{
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS:
				return mimeElements != null && !mimeElements.isEmpty();
		}
		return eDynamicIsSet(eFeature);
	}

} //MimeElementOwnerImpl
