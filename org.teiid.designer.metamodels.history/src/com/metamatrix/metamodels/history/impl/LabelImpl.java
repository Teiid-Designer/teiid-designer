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
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.history.HistoryPackage;
import com.metamatrix.metamodels.history.Label;
import com.metamatrix.metamodels.history.LabelLog;
import com.metamatrix.metamodels.history.Revision;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Label</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.impl.LabelImpl#getRevision <em>Revision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.LabelImpl#getHistoryLog <em>History Log</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.LabelImpl#getTag <em>Tag</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LabelImpl extends HistoryLogEntryImpl implements Label {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getTag() <em>Tag</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTag()
     * @generated
     * @ordered
     */
    protected static final String TAG_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTag() <em>Tag</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTag()
     * @generated
     * @ordered
     */
    protected String tag = TAG_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected LabelImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HistoryPackage.eINSTANCE.getLabel();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTag() {
        return tag;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTag(String newTag) {
        String oldTag = tag;
        tag = newTag;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.LABEL__TAG, oldTag, tag));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Revision getRevision() {
        if (eContainerFeatureID != HistoryPackage.LABEL__REVISION) return null;
        return (Revision)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRevision(Revision newRevision) {
        if (newRevision != eContainer || (eContainerFeatureID != HistoryPackage.LABEL__REVISION && newRevision != null)) {
            if (EcoreUtil.isAncestor(this, newRevision))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newRevision != null)
                msgs = ((InternalEObject)newRevision).eInverseAdd(this, HistoryPackage.REVISION__LABEL, Revision.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newRevision, HistoryPackage.LABEL__REVISION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.LABEL__REVISION, newRevision, newRevision));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public LabelLog getHistoryLog() {
        if (eContainerFeatureID != HistoryPackage.LABEL__HISTORY_LOG) return null;
        return (LabelLog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setHistoryLog(LabelLog newHistoryLog) {
        if (newHistoryLog != eContainer || (eContainerFeatureID != HistoryPackage.LABEL__HISTORY_LOG && newHistoryLog != null)) {
            if (EcoreUtil.isAncestor(this, newHistoryLog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newHistoryLog != null)
                msgs = ((InternalEObject)newHistoryLog).eInverseAdd(this, HistoryPackage.LABEL_LOG__LABELS, LabelLog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newHistoryLog, HistoryPackage.LABEL__HISTORY_LOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.LABEL__HISTORY_LOG, newHistoryLog, newHistoryLog));
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
                case HistoryPackage.LABEL__REVISION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HistoryPackage.LABEL__REVISION, msgs);
                case HistoryPackage.LABEL__HISTORY_LOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HistoryPackage.LABEL__HISTORY_LOG, msgs);
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
                case HistoryPackage.LABEL__REVISION:
                    return eBasicSetContainer(null, HistoryPackage.LABEL__REVISION, msgs);
                case HistoryPackage.LABEL__HISTORY_LOG:
                    return eBasicSetContainer(null, HistoryPackage.LABEL__HISTORY_LOG, msgs);
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
                case HistoryPackage.LABEL__REVISION:
                    return eContainer.eInverseRemove(this, HistoryPackage.REVISION__LABEL, Revision.class, msgs);
                case HistoryPackage.LABEL__HISTORY_LOG:
                    return eContainer.eInverseRemove(this, HistoryPackage.LABEL_LOG__LABELS, LabelLog.class, msgs);
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
            case HistoryPackage.LABEL__TIMESTAMP:
                return getTimestamp();
            case HistoryPackage.LABEL__USER:
                return getUser();
            case HistoryPackage.LABEL__REVISION:
                return getRevision();
            case HistoryPackage.LABEL__HISTORY_LOG:
                return getHistoryLog();
            case HistoryPackage.LABEL__TAG:
                return getTag();
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
            case HistoryPackage.LABEL__TIMESTAMP:
                setTimestamp((String)newValue);
                return;
            case HistoryPackage.LABEL__USER:
                setUser((String)newValue);
                return;
            case HistoryPackage.LABEL__REVISION:
                setRevision((Revision)newValue);
                return;
            case HistoryPackage.LABEL__HISTORY_LOG:
                setHistoryLog((LabelLog)newValue);
                return;
            case HistoryPackage.LABEL__TAG:
                setTag((String)newValue);
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
            case HistoryPackage.LABEL__TIMESTAMP:
                setTimestamp(TIMESTAMP_EDEFAULT);
                return;
            case HistoryPackage.LABEL__USER:
                setUser(USER_EDEFAULT);
                return;
            case HistoryPackage.LABEL__REVISION:
                setRevision((Revision)null);
                return;
            case HistoryPackage.LABEL__HISTORY_LOG:
                setHistoryLog((LabelLog)null);
                return;
            case HistoryPackage.LABEL__TAG:
                setTag(TAG_EDEFAULT);
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
            case HistoryPackage.LABEL__TIMESTAMP:
                return TIMESTAMP_EDEFAULT == null ? timestamp != null : !TIMESTAMP_EDEFAULT.equals(timestamp);
            case HistoryPackage.LABEL__USER:
                return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals(user);
            case HistoryPackage.LABEL__REVISION:
                return getRevision() != null;
            case HistoryPackage.LABEL__HISTORY_LOG:
                return getHistoryLog() != null;
            case HistoryPackage.LABEL__TAG:
                return TAG_EDEFAULT == null ? tag != null : !TAG_EDEFAULT.equals(tag);
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
        result.append(" (tag: "); //$NON-NLS-1$
        result.append(tag);
        result.append(')');
        return result.toString();
    }

} //LabelImpl
