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
import com.metamatrix.metamodels.history.Branch;
import com.metamatrix.metamodels.history.HistoryPackage;
import com.metamatrix.metamodels.history.Revision;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Branch</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.history.impl.BranchImpl#getPreviousRevision <em>Previous Revision</em>}</li>
 * <li>{@link com.metamatrix.metamodels.history.impl.BranchImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class BranchImpl extends HistoryLogEntryImpl implements Branch {

    /**
     * The default value of the '{@link #getVersion() <em>Version</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected static final String VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected String version = VERSION_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected BranchImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HistoryPackage.eINSTANCE.getBranch();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getVersion() {
        return version;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setVersion( String newVersion ) {
        String oldVersion = version;
        version = newVersion;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.BRANCH__VERSION,
                                                                   oldVersion, version));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Revision getPreviousRevision() {
        if (eContainerFeatureID != HistoryPackage.BRANCH__PREVIOUS_REVISION) return null;
        return (Revision)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setPreviousRevision( Revision newPreviousRevision ) {
        if (newPreviousRevision != eContainer
            || (eContainerFeatureID != HistoryPackage.BRANCH__PREVIOUS_REVISION && newPreviousRevision != null)) {
            if (EcoreUtil.isAncestor(this, newPreviousRevision)) throw new IllegalArgumentException(
                                                                                                    "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newPreviousRevision != null) msgs = ((InternalEObject)newPreviousRevision).eInverseAdd(this,
                                                                                                       HistoryPackage.REVISION__BRANCH,
                                                                                                       Revision.class,
                                                                                                       msgs);
            msgs = eBasicSetContainer((InternalEObject)newPreviousRevision, HistoryPackage.BRANCH__PREVIOUS_REVISION, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          HistoryPackage.BRANCH__PREVIOUS_REVISION,
                                                                          newPreviousRevision, newPreviousRevision));
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
                case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HistoryPackage.BRANCH__PREVIOUS_REVISION, msgs);
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
                case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                    return eBasicSetContainer(null, HistoryPackage.BRANCH__PREVIOUS_REVISION, msgs);
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
                case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                    return eContainer.eInverseRemove(this, HistoryPackage.REVISION__BRANCH, Revision.class, msgs);
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
            case HistoryPackage.BRANCH__TIMESTAMP:
                return getTimestamp();
            case HistoryPackage.BRANCH__USER:
                return getUser();
            case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                return getPreviousRevision();
            case HistoryPackage.BRANCH__VERSION:
                return getVersion();
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
            case HistoryPackage.BRANCH__TIMESTAMP:
                setTimestamp((String)newValue);
                return;
            case HistoryPackage.BRANCH__USER:
                setUser((String)newValue);
                return;
            case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                setPreviousRevision((Revision)newValue);
                return;
            case HistoryPackage.BRANCH__VERSION:
                setVersion((String)newValue);
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
            case HistoryPackage.BRANCH__TIMESTAMP:
                setTimestamp(TIMESTAMP_EDEFAULT);
                return;
            case HistoryPackage.BRANCH__USER:
                setUser(USER_EDEFAULT);
                return;
            case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                setPreviousRevision((Revision)null);
                return;
            case HistoryPackage.BRANCH__VERSION:
                setVersion(VERSION_EDEFAULT);
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
            case HistoryPackage.BRANCH__TIMESTAMP:
                return TIMESTAMP_EDEFAULT == null ? timestamp != null : !TIMESTAMP_EDEFAULT.equals(timestamp);
            case HistoryPackage.BRANCH__USER:
                return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals(user);
            case HistoryPackage.BRANCH__PREVIOUS_REVISION:
                return getPreviousRevision() != null;
            case HistoryPackage.BRANCH__VERSION:
                return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
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
        result.append(" (version: "); //$NON-NLS-1$
        result.append(version);
        result.append(')');
        return result.toString();
    }

} // BranchImpl
