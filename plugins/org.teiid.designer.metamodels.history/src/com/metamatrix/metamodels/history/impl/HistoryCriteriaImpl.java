/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.history.HistoryCriteria;
import com.metamatrix.metamodels.history.HistoryLog;
import com.metamatrix.metamodels.history.HistoryPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Criteria</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl#getHistoryLog <em>History Log</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl#getFromDate <em>From Date</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl#getToDate <em>To Date</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl#getUser <em>User</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl#isIncludeLabels <em>Include Labels</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.impl.HistoryCriteriaImpl#isOnlyLabels <em>Only Labels</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class HistoryCriteriaImpl extends EObjectImpl implements HistoryCriteria {

    /**
     * The default value of the '{@link #getFromDate() <em>From Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getFromDate()
     * @generated
     * @ordered
     */
    protected static final String FROM_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFromDate() <em>From Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getFromDate()
     * @generated
     * @ordered
     */
    protected String fromDate = FROM_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getToDate() <em>To Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getToDate()
     * @generated
     * @ordered
     */
    protected static final String TO_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getToDate() <em>To Date</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getToDate()
     * @generated
     * @ordered
     */
    protected String toDate = TO_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getUser() <em>User</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected static final String USER_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUser() <em>User</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getUser()
     * @generated
     * @ordered
     */
    protected String user = USER_EDEFAULT;

    /**
     * The default value of the '{@link #isIncludeLabels() <em>Include Labels</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isIncludeLabels()
     * @generated
     * @ordered
     */
    protected static final boolean INCLUDE_LABELS_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIncludeLabels() <em>Include Labels</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isIncludeLabels()
     * @generated
     * @ordered
     */
    protected boolean includeLabels = INCLUDE_LABELS_EDEFAULT;

    /**
     * The default value of the '{@link #isOnlyLabels() <em>Only Labels</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #isOnlyLabels()
     * @generated
     * @ordered
     */
    protected static final boolean ONLY_LABELS_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isOnlyLabels() <em>Only Labels</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #isOnlyLabels()
     * @generated
     * @ordered
     */
    protected boolean onlyLabels = ONLY_LABELS_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected HistoryCriteriaImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HistoryPackage.eINSTANCE.getHistoryCriteria();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getFromDate() {
        return fromDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setFromDate( String newFromDate ) {
        String oldFromDate = fromDate;
        fromDate = newFromDate;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   HistoryPackage.HISTORY_CRITERIA__FROM_DATE, oldFromDate,
                                                                   fromDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getToDate() {
        return toDate;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setToDate( String newToDate ) {
        String oldToDate = toDate;
        toDate = newToDate;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   HistoryPackage.HISTORY_CRITERIA__TO_DATE, oldToDate, toDate));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getUser() {
        return user;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setUser( String newUser ) {
        String oldUser = user;
        user = newUser;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.HISTORY_CRITERIA__USER,
                                                                   oldUser, user));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isIncludeLabels() {
        return includeLabels;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setIncludeLabels( boolean newIncludeLabels ) {
        boolean oldIncludeLabels = includeLabels;
        includeLabels = newIncludeLabels;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   HistoryPackage.HISTORY_CRITERIA__INCLUDE_LABELS,
                                                                   oldIncludeLabels, includeLabels));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isOnlyLabels() {
        return onlyLabels;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setOnlyLabels( boolean newOnlyLabels ) {
        boolean oldOnlyLabels = onlyLabels;
        onlyLabels = newOnlyLabels;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   HistoryPackage.HISTORY_CRITERIA__ONLY_LABELS, oldOnlyLabels,
                                                                   onlyLabels));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public HistoryLog getHistoryLog() {
        if (eContainerFeatureID != HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG) return null;
        return (HistoryLog)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setHistoryLog( HistoryLog newHistoryLog ) {
        if (newHistoryLog != eContainer
            || (eContainerFeatureID != HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG && newHistoryLog != null)) {
            if (EcoreUtil.isAncestor(this, newHistoryLog)) throw new IllegalArgumentException(
                                                                                              "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newHistoryLog != null) msgs = ((InternalEObject)newHistoryLog).eInverseAdd(this,
                                                                                           HistoryPackage.HISTORY_LOG__HISTORY_CRITERIA,
                                                                                           HistoryLog.class,
                                                                                           msgs);
            msgs = eBasicSetContainer((InternalEObject)newHistoryLog, HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG,
                                                                          newHistoryLog, newHistoryLog));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd,
                                          int featureID,
                                          Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove( InternalEObject otherEnd,
                                             int featureID,
                                             Class baseClass,
                                             NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                    return eBasicSetContainer(null, HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                    return eContainer.eInverseRemove(this, HistoryPackage.HISTORY_LOG__HISTORY_CRITERIA, HistoryLog.class, msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet( EStructuralFeature eFeature,
                        boolean resolve ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                return getHistoryLog();
            case HistoryPackage.HISTORY_CRITERIA__FROM_DATE:
                return getFromDate();
            case HistoryPackage.HISTORY_CRITERIA__TO_DATE:
                return getToDate();
            case HistoryPackage.HISTORY_CRITERIA__USER:
                return getUser();
            case HistoryPackage.HISTORY_CRITERIA__INCLUDE_LABELS:
                return isIncludeLabels() ? Boolean.TRUE : Boolean.FALSE;
            case HistoryPackage.HISTORY_CRITERIA__ONLY_LABELS:
                return isOnlyLabels() ? Boolean.TRUE : Boolean.FALSE;
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet( EStructuralFeature eFeature,
                      Object newValue ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                setHistoryLog((HistoryLog)newValue);
                return;
            case HistoryPackage.HISTORY_CRITERIA__FROM_DATE:
                setFromDate((String)newValue);
                return;
            case HistoryPackage.HISTORY_CRITERIA__TO_DATE:
                setToDate((String)newValue);
                return;
            case HistoryPackage.HISTORY_CRITERIA__USER:
                setUser((String)newValue);
                return;
            case HistoryPackage.HISTORY_CRITERIA__INCLUDE_LABELS:
                setIncludeLabels(((Boolean)newValue).booleanValue());
                return;
            case HistoryPackage.HISTORY_CRITERIA__ONLY_LABELS:
                setOnlyLabels(((Boolean)newValue).booleanValue());
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                setHistoryLog((HistoryLog)null);
                return;
            case HistoryPackage.HISTORY_CRITERIA__FROM_DATE:
                setFromDate(FROM_DATE_EDEFAULT);
                return;
            case HistoryPackage.HISTORY_CRITERIA__TO_DATE:
                setToDate(TO_DATE_EDEFAULT);
                return;
            case HistoryPackage.HISTORY_CRITERIA__USER:
                setUser(USER_EDEFAULT);
                return;
            case HistoryPackage.HISTORY_CRITERIA__INCLUDE_LABELS:
                setIncludeLabels(INCLUDE_LABELS_EDEFAULT);
                return;
            case HistoryPackage.HISTORY_CRITERIA__ONLY_LABELS:
                setOnlyLabels(ONLY_LABELS_EDEFAULT);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case HistoryPackage.HISTORY_CRITERIA__HISTORY_LOG:
                return getHistoryLog() != null;
            case HistoryPackage.HISTORY_CRITERIA__FROM_DATE:
                return FROM_DATE_EDEFAULT == null ? fromDate != null : !FROM_DATE_EDEFAULT.equals(fromDate);
            case HistoryPackage.HISTORY_CRITERIA__TO_DATE:
                return TO_DATE_EDEFAULT == null ? toDate != null : !TO_DATE_EDEFAULT.equals(toDate);
            case HistoryPackage.HISTORY_CRITERIA__USER:
                return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals(user);
            case HistoryPackage.HISTORY_CRITERIA__INCLUDE_LABELS:
                return includeLabels != INCLUDE_LABELS_EDEFAULT;
            case HistoryPackage.HISTORY_CRITERIA__ONLY_LABELS:
                return onlyLabels != ONLY_LABELS_EDEFAULT;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (fromDate: "); //$NON-NLS-1$
        result.append(fromDate);
        result.append(", toDate: "); //$NON-NLS-1$
        result.append(toDate);
        result.append(", user: "); //$NON-NLS-1$
        result.append(user);
        result.append(", includeLabels: "); //$NON-NLS-1$
        result.append(includeLabels);
        result.append(", onlyLabels: "); //$NON-NLS-1$
        result.append(onlyLabels);
        result.append(')');
        return result.toString();
    }

} // HistoryCriteriaImpl
