/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.vdb.edit.manifest.impl;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.modeler.core.validation.ProblemMarker;
import com.metamatrix.modeler.core.validation.ProblemMarkerContainer;
import com.metamatrix.modeler.core.validation.Severity;
import com.metamatrix.vdb.edit.manifest.ManifestPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Problem Marker Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerContainerImpl#getSeverity <em>Severity</em>}</li>
 *   <li>{@link com.metamatrix.vdb.edit.manifest.impl.ProblemMarkerContainerImpl#getMarkers <em>Markers</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ProblemMarkerContainerImpl extends EObjectImpl implements ProblemMarkerContainer {
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
     * The cached value of the '{@link #getMarkers() <em>Markers</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMarkers()
     * @generated
     * @ordered
     */
    protected EList markers = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProblemMarkerContainerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ManifestPackage.eINSTANCE.getProblemMarkerContainer();
    }

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated NOT
	 */
    public Severity getSeverity()
	{
        return calculateSeverity(Severity.OK_LITERAL);
		//return severity;
	}

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Severity getSeverityGen() {
        return severity;
    }

    protected Severity calculateSeverity( final Severity initialSeverity ) {
        Severity severity = initialSeverity;
        if ( this.markers != null ) {
            final Iterator iter = this.markers.iterator();
            while (iter.hasNext()) {
                final ProblemMarker marker = (ProblemMarker)iter.next();
                if ( marker.getSeverity().getValue() > severity.getValue() ) {
                    severity = marker.getSeverity();
                }
            }
        }
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
            eNotify(new ENotificationImpl(this, Notification.SET, ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY, oldSeverity, severity));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getMarkers() {
        if (markers == null) {
            markers = new EObjectContainmentWithInverseEList(ProblemMarker.class, this, ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS, ManifestPackage.PROBLEM_MARKER__MARKED);
        }
        return markers;
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
                case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS:
                    return ((InternalEList)getMarkers()).basicAdd(otherEnd, msgs);
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
                case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS:
                    return ((InternalEList)getMarkers()).basicRemove(otherEnd, msgs);
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
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY:
                return getSeverity();
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS:
                return getMarkers();
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
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY:
                setSeverity((Severity)newValue);
                return;
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS:
                getMarkers().clear();
                getMarkers().addAll((Collection)newValue);
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
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY:
                setSeverity(SEVERITY_EDEFAULT);
                return;
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS:
                getMarkers().clear();
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
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__SEVERITY:
                return severity != SEVERITY_EDEFAULT;
            case ManifestPackage.PROBLEM_MARKER_CONTAINER__MARKERS:
                return markers != null && !markers.isEmpty();
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
        result.append(')');
        return result.toString();
    }

} //ProblemMarkerContainerImpl
