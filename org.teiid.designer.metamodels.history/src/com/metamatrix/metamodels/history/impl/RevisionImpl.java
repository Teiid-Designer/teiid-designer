/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.history.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.history.Branch;
import com.metamatrix.metamodels.history.HistoryPackage;
import com.metamatrix.metamodels.history.Label;
import com.metamatrix.metamodels.history.Revision;
import com.metamatrix.metamodels.history.RevisionLog;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Revision</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getHistoryLog <em>History Log</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getBranch <em>Branch</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getNextRevision <em>Next Revision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getPreviousRevision <em>Previous Revision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.history.impl.RevisionImpl#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RevisionImpl extends HistoryLogEntryImpl implements Revision {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getBranch() <em>Branch</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getBranch()
     * @generated
     * @ordered
     */
    protected EList branch = null;

    /**
     * The cached value of the '{@link #getLabel() <em>Label</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected EList label = null;

    /**
     * The cached value of the '{@link #getNextRevision() <em>Next Revision</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNextRevision()
     * @generated
     * @ordered
     */
    protected Revision nextRevision = null;

    /**
     * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getComment()
     * @generated
     * @ordered
     */
    protected static final String COMMENT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getComment() <em>Comment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getComment()
     * @generated
     * @ordered
     */
    protected String comment = COMMENT_EDEFAULT;

    /**
     * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected static final String VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected String version = VERSION_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RevisionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return HistoryPackage.eINSTANCE.getRevision();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getComment() {
        return comment;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setComment(String newComment) {
        String oldComment = comment;
        comment = newComment;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION__COMMENT, oldComment, comment));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getVersion() {
        return version;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVersion(String newVersion) {
        String oldVersion = version;
        version = newVersion;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION__VERSION, oldVersion, version));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RevisionLog getHistoryLog() {
        if (eContainerFeatureID != HistoryPackage.REVISION__HISTORY_LOG) return null;
        return (RevisionLog)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setHistoryLog(RevisionLog newHistoryLog) {
        if (newHistoryLog != eContainer || (eContainerFeatureID != HistoryPackage.REVISION__HISTORY_LOG && newHistoryLog != null)) {
            if (EcoreUtil.isAncestor(this, newHistoryLog))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newHistoryLog != null)
                msgs = ((InternalEObject)newHistoryLog).eInverseAdd(this, HistoryPackage.REVISION_LOG__FIRST_REVISION, RevisionLog.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newHistoryLog, HistoryPackage.REVISION__HISTORY_LOG, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION__HISTORY_LOG, newHistoryLog, newHistoryLog));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getBranch() {
        if (branch == null) {
            branch = new EObjectContainmentWithInverseEList(Branch.class, this, HistoryPackage.REVISION__BRANCH, HistoryPackage.BRANCH__PREVIOUS_REVISION);
        }
        return branch;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getLabel() {
        if (label == null) {
            label = new EObjectContainmentWithInverseEList(Label.class, this, HistoryPackage.REVISION__LABEL, HistoryPackage.LABEL__REVISION);
        }
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Revision getNextRevision() {
        return nextRevision;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetNextRevision(Revision newNextRevision, NotificationChain msgs) {
        Revision oldNextRevision = nextRevision;
        nextRevision = newNextRevision;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION__NEXT_REVISION, oldNextRevision, newNextRevision);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNextRevision(Revision newNextRevision) {
        if (newNextRevision != nextRevision) {
            NotificationChain msgs = null;
            if (nextRevision != null)
                msgs = ((InternalEObject)nextRevision).eInverseRemove(this, HistoryPackage.REVISION__PREVIOUS_REVISION, Revision.class, msgs);
            if (newNextRevision != null)
                msgs = ((InternalEObject)newNextRevision).eInverseAdd(this, HistoryPackage.REVISION__PREVIOUS_REVISION, Revision.class, msgs);
            msgs = basicSetNextRevision(newNextRevision, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION__NEXT_REVISION, newNextRevision, newNextRevision));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Revision getPreviousRevision() {
        if (eContainerFeatureID != HistoryPackage.REVISION__PREVIOUS_REVISION) return null;
        return (Revision)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPreviousRevision(Revision newPreviousRevision) {
        if (newPreviousRevision != eContainer || (eContainerFeatureID != HistoryPackage.REVISION__PREVIOUS_REVISION && newPreviousRevision != null)) {
            if (EcoreUtil.isAncestor(this, newPreviousRevision))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newPreviousRevision != null)
                msgs = ((InternalEObject)newPreviousRevision).eInverseAdd(this, HistoryPackage.REVISION__NEXT_REVISION, Revision.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newPreviousRevision, HistoryPackage.REVISION__PREVIOUS_REVISION, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, HistoryPackage.REVISION__PREVIOUS_REVISION, newPreviousRevision, newPreviousRevision));
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
                case HistoryPackage.REVISION__HISTORY_LOG:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HistoryPackage.REVISION__HISTORY_LOG, msgs);
                case HistoryPackage.REVISION__BRANCH:
                    return ((InternalEList)getBranch()).basicAdd(otherEnd, msgs);
                case HistoryPackage.REVISION__LABEL:
                    return ((InternalEList)getLabel()).basicAdd(otherEnd, msgs);
                case HistoryPackage.REVISION__NEXT_REVISION:
                    if (nextRevision != null)
                        msgs = ((InternalEObject)nextRevision).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - HistoryPackage.REVISION__NEXT_REVISION, null, msgs);
                    return basicSetNextRevision((Revision)otherEnd, msgs);
                case HistoryPackage.REVISION__PREVIOUS_REVISION:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, HistoryPackage.REVISION__PREVIOUS_REVISION, msgs);
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
                case HistoryPackage.REVISION__HISTORY_LOG:
                    return eBasicSetContainer(null, HistoryPackage.REVISION__HISTORY_LOG, msgs);
                case HistoryPackage.REVISION__BRANCH:
                    return ((InternalEList)getBranch()).basicRemove(otherEnd, msgs);
                case HistoryPackage.REVISION__LABEL:
                    return ((InternalEList)getLabel()).basicRemove(otherEnd, msgs);
                case HistoryPackage.REVISION__NEXT_REVISION:
                    return basicSetNextRevision(null, msgs);
                case HistoryPackage.REVISION__PREVIOUS_REVISION:
                    return eBasicSetContainer(null, HistoryPackage.REVISION__PREVIOUS_REVISION, msgs);
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
                case HistoryPackage.REVISION__HISTORY_LOG:
                    return eContainer.eInverseRemove(this, HistoryPackage.REVISION_LOG__FIRST_REVISION, RevisionLog.class, msgs);
                case HistoryPackage.REVISION__PREVIOUS_REVISION:
                    return eContainer.eInverseRemove(this, HistoryPackage.REVISION__NEXT_REVISION, Revision.class, msgs);
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
            case HistoryPackage.REVISION__TIMESTAMP:
                return getTimestamp();
            case HistoryPackage.REVISION__USER:
                return getUser();
            case HistoryPackage.REVISION__HISTORY_LOG:
                return getHistoryLog();
            case HistoryPackage.REVISION__BRANCH:
                return getBranch();
            case HistoryPackage.REVISION__LABEL:
                return getLabel();
            case HistoryPackage.REVISION__NEXT_REVISION:
                return getNextRevision();
            case HistoryPackage.REVISION__PREVIOUS_REVISION:
                return getPreviousRevision();
            case HistoryPackage.REVISION__COMMENT:
                return getComment();
            case HistoryPackage.REVISION__VERSION:
                return getVersion();
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
            case HistoryPackage.REVISION__TIMESTAMP:
                setTimestamp((String)newValue);
                return;
            case HistoryPackage.REVISION__USER:
                setUser((String)newValue);
                return;
            case HistoryPackage.REVISION__HISTORY_LOG:
                setHistoryLog((RevisionLog)newValue);
                return;
            case HistoryPackage.REVISION__BRANCH:
                getBranch().clear();
                getBranch().addAll((Collection)newValue);
                return;
            case HistoryPackage.REVISION__LABEL:
                getLabel().clear();
                getLabel().addAll((Collection)newValue);
                return;
            case HistoryPackage.REVISION__NEXT_REVISION:
                setNextRevision((Revision)newValue);
                return;
            case HistoryPackage.REVISION__PREVIOUS_REVISION:
                setPreviousRevision((Revision)newValue);
                return;
            case HistoryPackage.REVISION__COMMENT:
                setComment((String)newValue);
                return;
            case HistoryPackage.REVISION__VERSION:
                setVersion((String)newValue);
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
            case HistoryPackage.REVISION__TIMESTAMP:
                setTimestamp(TIMESTAMP_EDEFAULT);
                return;
            case HistoryPackage.REVISION__USER:
                setUser(USER_EDEFAULT);
                return;
            case HistoryPackage.REVISION__HISTORY_LOG:
                setHistoryLog((RevisionLog)null);
                return;
            case HistoryPackage.REVISION__BRANCH:
                getBranch().clear();
                return;
            case HistoryPackage.REVISION__LABEL:
                getLabel().clear();
                return;
            case HistoryPackage.REVISION__NEXT_REVISION:
                setNextRevision((Revision)null);
                return;
            case HistoryPackage.REVISION__PREVIOUS_REVISION:
                setPreviousRevision((Revision)null);
                return;
            case HistoryPackage.REVISION__COMMENT:
                setComment(COMMENT_EDEFAULT);
                return;
            case HistoryPackage.REVISION__VERSION:
                setVersion(VERSION_EDEFAULT);
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
            case HistoryPackage.REVISION__TIMESTAMP:
                return TIMESTAMP_EDEFAULT == null ? timestamp != null : !TIMESTAMP_EDEFAULT.equals(timestamp);
            case HistoryPackage.REVISION__USER:
                return USER_EDEFAULT == null ? user != null : !USER_EDEFAULT.equals(user);
            case HistoryPackage.REVISION__HISTORY_LOG:
                return getHistoryLog() != null;
            case HistoryPackage.REVISION__BRANCH:
                return branch != null && !branch.isEmpty();
            case HistoryPackage.REVISION__LABEL:
                return label != null && !label.isEmpty();
            case HistoryPackage.REVISION__NEXT_REVISION:
                return nextRevision != null;
            case HistoryPackage.REVISION__PREVIOUS_REVISION:
                return getPreviousRevision() != null;
            case HistoryPackage.REVISION__COMMENT:
                return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
            case HistoryPackage.REVISION__VERSION:
                return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
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
        result.append(" (comment: "); //$NON-NLS-1$
        result.append(comment);
        result.append(", version: "); //$NON-NLS-1$
        result.append(version);
        result.append(')');
        return result.toString();
    }

} //RevisionImpl
