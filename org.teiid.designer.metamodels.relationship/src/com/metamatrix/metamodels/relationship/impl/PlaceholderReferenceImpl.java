/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.relationship.PlaceholderReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.RelationshipPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Placeholder Reference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.PlaceholderReferenceImpl#getPlaceholderReferenceContainer <em>Placeholder Reference Container</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class PlaceholderReferenceImpl extends EObjectImpl implements PlaceholderReference {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected PlaceholderReferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getPlaceholderReference();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PlaceholderReferenceContainer getPlaceholderReferenceContainer() {
        if (eContainerFeatureID != RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER) return null;
        return (PlaceholderReferenceContainer)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPlaceholderReferenceContainer(PlaceholderReferenceContainer newPlaceholderReferenceContainer) {
        if (newPlaceholderReferenceContainer != eContainer || (eContainerFeatureID != RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER && newPlaceholderReferenceContainer != null)) {
            if (EcoreUtil.isAncestor(this, newPlaceholderReferenceContainer))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newPlaceholderReferenceContainer != null)
                msgs = ((InternalEObject)newPlaceholderReferenceContainer).eInverseAdd(this, RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS, PlaceholderReferenceContainer.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newPlaceholderReferenceContainer, RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, newPlaceholderReferenceContainer, newPlaceholderReferenceContainer));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public String getDisplayableName() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDisplayableNameGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
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
                case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
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
                case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    return eBasicSetContainer(null, RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER, msgs);
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
                case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                    return eContainer.eInverseRemove(this, RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS, PlaceholderReferenceContainer.class, msgs);
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                return getPlaceholderReferenceContainer();
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                setPlaceholderReferenceContainer((PlaceholderReferenceContainer)newValue);
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                setPlaceholderReferenceContainer((PlaceholderReferenceContainer)null);
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER:
                return getPlaceholderReferenceContainer() != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //PlaceholderReferenceImpl
