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

package com.metamatrix.metamodels.wsdl.mime.impl;

import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.mime.MimePart;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Multipart Related</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeMultipartRelatedImpl#getMimeElementOwner <em>Mime Element Owner</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.impl.MimeMultipartRelatedImpl#getMimeParts <em>Mime Parts</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MimeMultipartRelatedImpl extends EObjectImpl implements MimeMultipartRelated {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getMimeParts() <em>Mime Parts</em>}' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getMimeParts()
     * @generated
     * @ordered
     */
	protected EList mimeParts = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected MimeMultipartRelatedImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	@Override
    protected EClass eStaticClass() {
        return MimePackage.eINSTANCE.getMimeMultipartRelated();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public MimeElementOwner getMimeElementOwner() {
        if (eContainerFeatureID != MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER) return null;
        return (MimeElementOwner)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setMimeElementOwner(MimeElementOwner newMimeElementOwner) {
        if (newMimeElementOwner != eContainer || (eContainerFeatureID != MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER && newMimeElementOwner != null)) {
            if (EcoreUtil.isAncestor(this, newMimeElementOwner))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMimeElementOwner != null)
                msgs = ((InternalEObject)newMimeElementOwner).eInverseAdd(this, MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS, MimeElementOwner.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMimeElementOwner, MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER, newMimeElementOwner, newMimeElementOwner));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EList getMimeParts() {
        if (mimeParts == null) {
            mimeParts = new EObjectContainmentWithInverseEList(MimePart.class, this, MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS, MimePackage.MIME_PART__MIME_MULTIPART_RELATED);
        }
        return mimeParts;
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
                case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER, msgs);
                case MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS:
                    return ((InternalEList)getMimeParts()).basicAdd(otherEnd, msgs);
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
                case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                    return eBasicSetContainer(null, MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER, msgs);
                case MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS:
                    return ((InternalEList)getMimeParts()).basicRemove(otherEnd, msgs);
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
                case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                    return eContainer.eInverseRemove(this, MimePackage.MIME_ELEMENT_OWNER__MIME_ELEMENTS, MimeElementOwner.class, msgs);
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
            case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                return getMimeElementOwner();
            case MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS:
                return getMimeParts();
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
            case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                setMimeElementOwner((MimeElementOwner)newValue);
                return;
            case MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS:
                getMimeParts().clear();
                getMimeParts().addAll((Collection)newValue);
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
            case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                setMimeElementOwner((MimeElementOwner)null);
                return;
            case MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS:
                getMimeParts().clear();
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
            case MimePackage.MIME_MULTIPART_RELATED__MIME_ELEMENT_OWNER:
                return getMimeElementOwner() != null;
            case MimePackage.MIME_MULTIPART_RELATED__MIME_PARTS:
                return mimeParts != null && !mimeParts.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //MimeMultipartRelatedImpl
