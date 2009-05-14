/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

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

import com.metamatrix.vdb.edit.manifest.ManifestPackage;
import com.metamatrix.vdb.edit.manifest.ProblemMarker;
import com.metamatrix.vdb.edit.manifest.ProblemMarkerContainer;
import com.metamatrix.vdb.edit.manifest.Severity;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Problem Marker</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getSeverity <em>Severity</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getMessage <em>Message</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getTargetUri <em>Target Uri</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getCode <em>Code</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getStackTrace <em>Stack Trace</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getMarked <em>Marked</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerImpl#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProblemMarkerImpl extends EObjectImpl implements ProblemMarker {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The default value of the '{@link #getSeverity() <em>Severity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSeverity()
     * @generated
     * @ordered
     */
    protected static final Severity SEVERITY_EDEFAULT = Severity.OK_LITERAL;

    /**
     * The cached value of the '{@link #getSeverity() <em>Severity</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSeverity()
     * @generated
     * @ordered
     */
    protected Severity severity = SEVERITY_EDEFAULT;

    /**
     * The default value of the '{@link #getMessage() <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMessage()
     * @generated
     * @ordered
     */
    protected static final String MESSAGE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMessage() <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMessage()
     * @generated
     * @ordered
     */
    protected String message = MESSAGE_EDEFAULT;

    /**
     * The default value of the '{@link #getTarget() <em>Target</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected static final String TARGET_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTarget() <em>Target</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected String target = TARGET_EDEFAULT;

    /**
     * The default value of the '{@link #getTargetUri() <em>Target Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTargetUri()
     * @generated
     * @ordered
     */
    protected static final String TARGET_URI_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getTargetUri() <em>Target Uri</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTargetUri()
     * @generated
     * @ordered
     */
    protected String targetUri = TARGET_URI_EDEFAULT;

    /**
     * The default value of the '{@link #getCode() <em>Code</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCode()
     * @generated
     * @ordered
     */
    protected static final int CODE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getCode() <em>Code</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCode()
     * @generated
     * @ordered
     */
    protected int code = CODE_EDEFAULT;

    /**
     * The default value of the '{@link #getStackTrace() <em>Stack Trace</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStackTrace()
     * @generated
     * @ordered
     */
    protected static final String STACK_TRACE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getStackTrace() <em>Stack Trace</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStackTrace()
     * @generated
     * @ordered
     */
    protected String stackTrace = STACK_TRACE_EDEFAULT;

    /**
     * The cached value of the '{@link #getChildren() <em>Children</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChildren()
     * @generated
     * @ordered
     */
    protected EList children = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProblemMarkerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ManifestPackage.eINSTANCE.getProblemMarker();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Severity getSeverity() {
        return severity;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSeverity(Severity newSeverity) {
        Severity oldSeverity = severity;
        severity = newSeverity == null ? SEVERITY_EDEFAULT : newSeverity;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__SEVERITY, oldSeverity, severity));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getMessage() {
        return message;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMessage(String newMessage) {
        String oldMessage = message;
        message = newMessage;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__MESSAGE, oldMessage, message));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTarget() {
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTarget(String newTarget) {
        String oldTarget = target;
        target = newTarget;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__TARGET, oldTarget, target));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getTargetUri() {
        return targetUri;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTargetUri(String newTargetUri) {
        String oldTargetUri = targetUri;
        targetUri = newTargetUri;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__TARGET_URI, oldTargetUri, targetUri));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getCode() {
        return code;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCode(int newCode) {
        int oldCode = code;
        code = newCode;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__CODE, oldCode, code));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStackTrace(String newStackTrace) {
        String oldStackTrace = stackTrace;
        stackTrace = newStackTrace;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__STACK_TRACE, oldStackTrace, stackTrace));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProblemMarkerContainer getMarked() {
        if (eContainerFeatureID != ManifestPackage.PROBLEM_MARKER__MARKED) return null;
        return (ProblemMarkerContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMarked(ProblemMarkerContainer newMarked) {
        if (newMarked != eContainer || (eContainerFeatureID != ManifestPackage.PROBLEM_MARKER__MARKED && newMarked != null)) {
            if (EcoreUtil.isAncestor(this, newMarked))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMarked != null)
                msgs = ((InternalEObject)newMarked).eInverseAdd(this, ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS, ProblemMarkerContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMarked, ManifestPackage.PROBLEM_MARKER__MARKED, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__MARKED, newMarked, newMarked));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getChildren() {
        if (children == null) {
            children = new EObjectContainmentWithInverseEList(ProblemMarker.class, this, ManifestPackage.PROBLEM_MARKER__CHILDREN, ManifestPackage.PROBLEM_MARKER__PARENT);
        }
        return children;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ProblemMarker getParent() {
        if (eContainerFeatureID != ManifestPackage.PROBLEM_MARKER__PARENT) return null;
        return (ProblemMarker)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setParent(ProblemMarker newParent) {
        if (newParent != eContainer || (eContainerFeatureID != ManifestPackage.PROBLEM_MARKER__PARENT && newParent != null)) {
            if (EcoreUtil.isAncestor(this, newParent))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newParent != null)
                msgs = ((InternalEObject)newParent).eInverseAdd(this, ManifestPackage.PROBLEM_MARKER__CHILDREN, ProblemMarker.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newParent, ManifestPackage.PROBLEM_MARKER__PARENT, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER__PARENT, newParent, newParent));
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
                case ManifestPackage.PROBLEM_MARKER__MARKED:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ManifestPackage.PROBLEM_MARKER__MARKED, msgs);
                case ManifestPackage.PROBLEM_MARKER__CHILDREN:
                    return ((InternalEList)getChildren()).basicAdd(otherEnd, msgs);
                case ManifestPackage.PROBLEM_MARKER__PARENT:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ManifestPackage.PROBLEM_MARKER__PARENT, msgs);
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
                case ManifestPackage.PROBLEM_MARKER__MARKED:
                    return eBasicSetContainer(null, ManifestPackage.PROBLEM_MARKER__MARKED, msgs);
                case ManifestPackage.PROBLEM_MARKER__CHILDREN:
                    return ((InternalEList)getChildren()).basicRemove(otherEnd, msgs);
                case ManifestPackage.PROBLEM_MARKER__PARENT:
                    return eBasicSetContainer(null, ManifestPackage.PROBLEM_MARKER__PARENT, msgs);
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
                case ManifestPackage.PROBLEM_MARKER__MARKED:
                    return eContainer.eInverseRemove(this, ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS, ProblemMarkerContainer.class, msgs);
                case ManifestPackage.PROBLEM_MARKER__PARENT:
                    return eContainer.eInverseRemove(this, ManifestPackage.PROBLEM_MARKER__CHILDREN, ProblemMarker.class, msgs);
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
            case ManifestPackage.PROBLEM_MARKER__SEVERITY:
                return getSeverity();
            case ManifestPackage.PROBLEM_MARKER__MESSAGE:
                return getMessage();
            case ManifestPackage.PROBLEM_MARKER__TARGET:
                return getTarget();
            case ManifestPackage.PROBLEM_MARKER__TARGET_URI:
                return getTargetUri();
            case ManifestPackage.PROBLEM_MARKER__CODE:
                return new Integer(getCode());
            case ManifestPackage.PROBLEM_MARKER__STACK_TRACE:
                return getStackTrace();
            case ManifestPackage.PROBLEM_MARKER__MARKED:
                return getMarked();
            case ManifestPackage.PROBLEM_MARKER__CHILDREN:
                return getChildren();
            case ManifestPackage.PROBLEM_MARKER__PARENT:
                return getParent();
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
            case ManifestPackage.PROBLEM_MARKER__SEVERITY:
                setSeverity((Severity)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__MESSAGE:
                setMessage((String)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__TARGET:
                setTarget((String)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__TARGET_URI:
                setTargetUri((String)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__CODE:
                setCode(((Integer)newValue).intValue());
                return;
            case ManifestPackage.PROBLEM_MARKER__STACK_TRACE:
                setStackTrace((String)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__MARKED:
                setMarked((ProblemMarkerContainer)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__CHILDREN:
                getChildren().clear();
                getChildren().addAll((Collection)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER__PARENT:
                setParent((ProblemMarker)newValue);
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
            case ManifestPackage.PROBLEM_MARKER__SEVERITY:
                setSeverity(SEVERITY_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER__MESSAGE:
                setMessage(MESSAGE_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER__TARGET:
                setTarget(TARGET_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER__TARGET_URI:
                setTargetUri(TARGET_URI_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER__CODE:
                setCode(CODE_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER__STACK_TRACE:
                setStackTrace(STACK_TRACE_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER__MARKED:
                setMarked((ProblemMarkerContainer)null);
                return;
            case ManifestPackage.PROBLEM_MARKER__CHILDREN:
                getChildren().clear();
                return;
            case ManifestPackage.PROBLEM_MARKER__PARENT:
                setParent((ProblemMarker)null);
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
            case ManifestPackage.PROBLEM_MARKER__SEVERITY:
                return severity != SEVERITY_EDEFAULT;
            case ManifestPackage.PROBLEM_MARKER__MESSAGE:
                return MESSAGE_EDEFAULT == null ? message != null : !MESSAGE_EDEFAULT.equals(message);
            case ManifestPackage.PROBLEM_MARKER__TARGET:
                return TARGET_EDEFAULT == null ? target != null : !TARGET_EDEFAULT.equals(target);
            case ManifestPackage.PROBLEM_MARKER__TARGET_URI:
                return TARGET_URI_EDEFAULT == null ? targetUri != null : !TARGET_URI_EDEFAULT.equals(targetUri);
            case ManifestPackage.PROBLEM_MARKER__CODE:
                return code != CODE_EDEFAULT;
            case ManifestPackage.PROBLEM_MARKER__STACK_TRACE:
                return STACK_TRACE_EDEFAULT == null ? stackTrace != null : !STACK_TRACE_EDEFAULT.equals(stackTrace);
            case ManifestPackage.PROBLEM_MARKER__MARKED:
                return getMarked() != null;
            case ManifestPackage.PROBLEM_MARKER__CHILDREN:
                return children != null && !children.isEmpty();
            case ManifestPackage.PROBLEM_MARKER__PARENT:
                return getParent() != null;
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
        result.append(" (severity: "); //$NON-NLS-1$
        result.append(severity);
        result.append(", message: "); //$NON-NLS-1$
        result.append(message);
        result.append(", target: "); //$NON-NLS-1$
        result.append(target);
        result.append(", targetUri: "); //$NON-NLS-1$
        result.append(targetUri);
        result.append(", code: "); //$NON-NLS-1$
        result.append(code);
        result.append(", stackTrace: "); //$NON-NLS-1$
        result.append(stackTrace);
        result.append(')');
        return result.toString();
    }

} //ProblemMarkerImpl
