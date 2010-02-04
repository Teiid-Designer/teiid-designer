/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.relationship.PlaceholderReference;
import com.metamatrix.metamodels.relationship.PlaceholderReferenceContainer;
import com.metamatrix.metamodels.relationship.RelationshipPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Placeholder Reference Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relationship.impl.PlaceholderReferenceContainerImpl#getPlaceholders <em>Placeholders</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PlaceholderReferenceContainerImpl extends EObjectImpl implements PlaceholderReferenceContainer {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getPlaceholders() <em>Placeholders</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPlaceholders()
     * @generated
     * @ordered
     */
    protected EList placeholders = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected PlaceholderReferenceContainerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationshipPackage.eINSTANCE.getPlaceholderReferenceContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getPlaceholders() {
        if (placeholders == null) {
            placeholders = new EObjectContainmentWithInverseEList(PlaceholderReference.class, this, RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS, RelationshipPackage.PLACEHOLDER_REFERENCE__PLACEHOLDER_REFERENCE_CONTAINER);
        }
        return placeholders;
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
                case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS:
                    return ((InternalEList)getPlaceholders()).basicAdd(otherEnd, msgs);
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
                case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS:
                    return ((InternalEList)getPlaceholders()).basicRemove(otherEnd, msgs);
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS:
                return getPlaceholders();
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS:
                getPlaceholders().clear();
                getPlaceholders().addAll((Collection)newValue);
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS:
                getPlaceholders().clear();
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
            case RelationshipPackage.PLACEHOLDER_REFERENCE_CONTAINER__PLACEHOLDERS:
                return placeholders != null && !placeholders.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //PlaceholderReferenceContainerImpl
