/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.impl;

import com.metamatrix.metamodels.history.HistoryLogEntry;
import com.metamatrix.metamodels.history.HistoryPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Log Entry</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.impl.HistoryLogEntryImpl#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.HistoryLogEntryImpl#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class HistoryLogEntryImpl extends EObjectImpl implements HistoryLogEntry {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimestamp()
     * @generated
     * @ordered
     */
    protected static final String TIMESTAMP_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTimestamp() <em>Timestamp</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTimestamp()
     * @generated
     * @ordered
     */
    protected String timestamp = TIMESTAMP_EDEFAULT;

    /**
     * The default value of the '{@link #getUser() <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected static final String USER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUser() <em>User</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected String user = USER_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected HistoryLogEntryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HistoryPackage.eINSTANCE.getHistoryLogEntry();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTimestamp(String newTimestamp) {
        String oldTimestamp = timestamp;
        timestamp = newTimestamp;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.HISTORY_LOG_ENTRY__TIMESTAMP, oldTimestamp, timestamp));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getUser() {
        return user;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUser(String newUser) {
        String oldUser = user;
        user = newUser;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.HISTORY_LOG_ENTRY__USER, oldUser, user));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case HistoryPackage.HISTORY_LOG_ENTRY__TIMESTAMP:
                return getTimestamp();
            case HistoryPackage.HISTORY_LOG_ENTRY__USER:
                return getUser();
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
            case HistoryPackage.HISTORY_LOG_ENTRY__TIMESTAMP:
                setTimestamp((String)newValue);
                return;
            case HistoryPackage.HISTORY_LOG_ENTRY__USER:
                setUser((String)newValue);
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
            case HistoryPackage.HISTORY_LOG_ENTRY__TIMESTAMP:
                setTimestamp(TIMESTAMP_EDEFAULT);
                return;
            case HistoryPackage.HISTORY_LOG_ENTRY__USER:
                setUser(USER_EDEFAULT);
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
            case HistoryPackage.HISTORY_LOG_ENTRY__TIMESTAMP:
                return TIMESTAMP_EDEFAULT == null ? timestamp != null : !TIMESTAMP_EDEFAULT.equals(timestamp);
            case HistoryPackage.HISTORY_LOG_ENTRY__USER:
                return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals(user);
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
        result.append(" (timestamp: "); //$NON-NLS-1$
        result.append(timestamp);
        result.append(", user: "); //$NON-NLS-1$
        result.append(user);
        result.append(')');
        return result.toString();
    }

} //HistoryLogEntryImpl
