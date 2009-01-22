/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.diagram.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.diagram.DiagramLink;
import com.metamatrix.metamodels.diagram.DiagramPackage;
import com.metamatrix.metamodels.diagram.DiagramPosition;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Position</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramPositionImpl#getXPosition <em>XPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramPositionImpl#getYPosition <em>YPosition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.diagram.impl.DiagramPositionImpl#getDiagramLink <em>Diagram Link</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DiagramPositionImpl extends EObjectImpl implements DiagramPosition {

    /**
     * The default value of the '{@link #getXPosition() <em>XPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getXPosition()
     * @generated
     * @ordered
     */
    protected static final int XPOSITION_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getXPosition() <em>XPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getXPosition()
     * @generated
     * @ordered
     */
    protected int xPosition = XPOSITION_EDEFAULT;

    /**
     * The default value of the '{@link #getYPosition() <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getYPosition()
     * @generated
     * @ordered
     */
    protected static final int YPOSITION_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getYPosition() <em>YPosition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getYPosition()
     * @generated
     * @ordered
     */
    protected int yPosition = YPOSITION_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DiagramPositionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return DiagramPackage.eINSTANCE.getDiagramPosition();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getXPosition() {
        return xPosition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setXPosition(int newXPosition) {
        int oldXPosition = xPosition;
        xPosition = newXPosition;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_POSITION__XPOSITION, oldXPosition, xPosition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getYPosition() {
        return yPosition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setYPosition(int newYPosition) {
        int oldYPosition = yPosition;
        yPosition = newYPosition;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_POSITION__YPOSITION, oldYPosition, yPosition));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DiagramLink getDiagramLink() {
        if (eContainerFeatureID != DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK) return null;
        return (DiagramLink)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDiagramLink(DiagramLink newDiagramLink) {
        if (newDiagramLink != eContainer || (eContainerFeatureID != DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK && newDiagramLink != null)) {
            if (EcoreUtil.isAncestor(this, newDiagramLink))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newDiagramLink != null)
                msgs = ((InternalEObject)newDiagramLink).eInverseAdd(this, DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS, DiagramLink.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newDiagramLink, DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK, newDiagramLink, newDiagramLink));
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
                case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK, msgs);
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
                case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                    return eBasicSetContainer(null, DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK, msgs);
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
                case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                    return eContainer.eInverseRemove(this, DiagramPackage.DIAGRAM_LINK__ROUTE_POINTS, DiagramLink.class, msgs);
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
            case DiagramPackage.DIAGRAM_POSITION__XPOSITION:
                return new Integer(getXPosition());
            case DiagramPackage.DIAGRAM_POSITION__YPOSITION:
                return new Integer(getYPosition());
            case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                return getDiagramLink();
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
            case DiagramPackage.DIAGRAM_POSITION__XPOSITION:
                setXPosition(((Integer)newValue).intValue());
                return;
            case DiagramPackage.DIAGRAM_POSITION__YPOSITION:
                setYPosition(((Integer)newValue).intValue());
                return;
            case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                setDiagramLink((DiagramLink)newValue);
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
            case DiagramPackage.DIAGRAM_POSITION__XPOSITION:
                setXPosition(XPOSITION_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_POSITION__YPOSITION:
                setYPosition(YPOSITION_EDEFAULT);
                return;
            case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                setDiagramLink((DiagramLink)null);
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
            case DiagramPackage.DIAGRAM_POSITION__XPOSITION:
                return xPosition != XPOSITION_EDEFAULT;
            case DiagramPackage.DIAGRAM_POSITION__YPOSITION:
                return yPosition != YPOSITION_EDEFAULT;
            case DiagramPackage.DIAGRAM_POSITION__DIAGRAM_LINK:
                return getDiagramLink() != null;
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
        result.append(" (xPosition: "); //$NON-NLS-1$
        result.append(xPosition);
        result.append(", yPosition: "); //$NON-NLS-1$
        result.append(yPosition);
        result.append(')');
        return result.toString();
    }

} //DiagramPositionImpl
