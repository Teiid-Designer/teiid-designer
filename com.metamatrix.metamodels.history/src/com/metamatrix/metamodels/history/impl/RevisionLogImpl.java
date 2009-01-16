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

package com.metamatrix.metamodels.history.impl;

import com.metamatrix.metamodels.history.HistoryCriteria;
import com.metamatrix.metamodels.history.HistoryPackage;
import com.metamatrix.metamodels.history.Revision;
import com.metamatrix.metamodels.history.RevisionLog;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Revision Log</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionLogImpl#getFirstRevision <em>First Revision</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RevisionLogImpl extends HistoryLogImpl implements RevisionLog {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getFirstRevision() <em>First Revision</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstRevision()
     * @generated
     * @ordered
     */
    protected Revision firstRevision = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RevisionLogImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HistoryPackage.eINSTANCE.getRevisionLog();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Revision getFirstRevision() {
        return firstRevision;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetFirstRevision(Revision newFirstRevision, NotificationChain msgs) {
        Revision oldFirstRevision = firstRevision;
        firstRevision = newFirstRevision;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION_LOG__FIRST_REVISION, oldFirstRevision, newFirstRevision);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFirstRevision(Revision newFirstRevision) {
        if (newFirstRevision != firstRevision) {
            NotificationChain msgs = null;
            if (firstRevision != null)
                msgs = ((InternalEObject)firstRevision).eInverseRemove(this, HistoryPackage.REVISION__HISTORY_LOG, Revision.class, msgs);
            if (newFirstRevision != null)
                msgs = ((InternalEObject)newFirstRevision).eInverseAdd(this, HistoryPackage.REVISION__HISTORY_LOG, Revision.class, msgs);
            msgs = basicSetFirstRevision(newFirstRevision, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION_LOG__FIRST_REVISION, newFirstRevision, newFirstRevision));
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
                case HistoryPackage.REVISION_LOG__HISTORY_CRITERIA:
                    if (historyCriteria != null)
                        msgs = ((InternalEObject)historyCriteria).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HistoryPackage.REVISION_LOG__HISTORY_CRITERIA, null, msgs);
                    return basicSetHistoryCriteria((HistoryCriteria)otherEnd, msgs);
                case HistoryPackage.REVISION_LOG__FIRST_REVISION:
                    if (firstRevision != null)
                        msgs = ((InternalEObject)firstRevision).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HistoryPackage.REVISION_LOG__FIRST_REVISION, null, msgs);
                    return basicSetFirstRevision((Revision)otherEnd, msgs);
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
                case HistoryPackage.REVISION_LOG__HISTORY_CRITERIA:
                    return basicSetHistoryCriteria(null, msgs);
                case HistoryPackage.REVISION_LOG__FIRST_REVISION:
                    return basicSetFirstRevision(null, msgs);
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
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case HistoryPackage.REVISION_LOG__HISTORY_CRITERIA:
                return getHistoryCriteria();
            case HistoryPackage.REVISION_LOG__NAME:
                return getName();
            case HistoryPackage.REVISION_LOG__URI:
                return getUri();
            case HistoryPackage.REVISION_LOG__FIRST_REVISION:
                return getFirstRevision();
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
            case HistoryPackage.REVISION_LOG__HISTORY_CRITERIA:
                setHistoryCriteria((HistoryCriteria)newValue);
                return;
            case HistoryPackage.REVISION_LOG__NAME:
                setName((String)newValue);
                return;
            case HistoryPackage.REVISION_LOG__URI:
                setUri((String)newValue);
                return;
            case HistoryPackage.REVISION_LOG__FIRST_REVISION:
                setFirstRevision((Revision)newValue);
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
            case HistoryPackage.REVISION_LOG__HISTORY_CRITERIA:
                setHistoryCriteria((HistoryCriteria)null);
                return;
            case HistoryPackage.REVISION_LOG__NAME:
                setName(NAME_EDEFAULT);
                return;
            case HistoryPackage.REVISION_LOG__URI:
                setUri(URI_EDEFAULT);
                return;
            case HistoryPackage.REVISION_LOG__FIRST_REVISION:
                setFirstRevision((Revision)null);
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
            case HistoryPackage.REVISION_LOG__HISTORY_CRITERIA:
                return historyCriteria != null;
            case HistoryPackage.REVISION_LOG__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case HistoryPackage.REVISION_LOG__URI:
                return URI_EDEFAULT == null ? uri != null : !URI_EDEFAULT.equals(uri);
            case HistoryPackage.REVISION_LOG__FIRST_REVISION:
                return firstRevision != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //RevisionLogImpl
