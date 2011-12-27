/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.PropertyDifference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Property Difference</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl#getNewValue <em>New Value</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl#getOldValue <em>Old Value</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl#isSkip <em>Skip</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl#getAffectedFeature <em>Affected Feature</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.PropertyDifferenceImpl#getDescriptor <em>Descriptor</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PropertyDifferenceImpl extends EObjectImpl implements PropertyDifference {
    /**
     * The default value of the '{@link #getNewValue() <em>New Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNewValue()
     * @generated
     * @ordered
     */
    protected static final Object NEW_VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNewValue() <em>New Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNewValue()
     * @generated
     * @ordered
     */
    protected Object newValue = NEW_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getOldValue() <em>Old Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOldValue()
     * @generated
     * @ordered
     */
    protected static final Object OLD_VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getOldValue() <em>Old Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOldValue()
     * @generated
     * @ordered
     */
    protected Object oldValue = OLD_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #isSkip() <em>Skip</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSkip()
     * @generated
     * @ordered
     */
    protected static final boolean SKIP_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isSkip() <em>Skip</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isSkip()
     * @generated
     * @ordered
     */
    protected boolean skip = SKIP_EDEFAULT;

    /**
     * The cached value of the '{@link #getAffectedFeature() <em>Affected Feature</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAffectedFeature()
     * @generated
     * @ordered
     */
    protected EStructuralFeature affectedFeature = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected PropertyDifferenceImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ComparePackage.eINSTANCE.getPropertyDifference();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getNewValue() {
        return newValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNewValue(Object newNewValue) {
        Object oldNewValue = newValue;
        newValue = newNewValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.PROPERTY_DIFFERENCE__NEW_VALUE, oldNewValue, newValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object getOldValue() {
        return oldValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOldValue(Object newOldValue) {
        Object oldOldValue = oldValue;
        oldValue = newOldValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.PROPERTY_DIFFERENCE__OLD_VALUE, oldOldValue, oldValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSkip() {
        return skip;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSkip(boolean newSkip) {
        boolean oldSkip = skip;
        skip = newSkip;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.PROPERTY_DIFFERENCE__SKIP, oldSkip, skip));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EStructuralFeature getAffectedFeature() {
        if (affectedFeature != null && affectedFeature.eIsProxy()) {
            EStructuralFeature oldAffectedFeature = affectedFeature;
            affectedFeature = (EStructuralFeature)eResolveProxy((InternalEObject)affectedFeature);
            if (affectedFeature != oldAffectedFeature) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ComparePackage.PROPERTY_DIFFERENCE__AFFECTED_FEATURE, oldAffectedFeature, affectedFeature));
            }
        }
        return affectedFeature;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EStructuralFeature basicGetAffectedFeature() {
        return affectedFeature;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAffectedFeature(EStructuralFeature newAffectedFeature) {
        EStructuralFeature oldAffectedFeature = affectedFeature;
        affectedFeature = newAffectedFeature;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.PROPERTY_DIFFERENCE__AFFECTED_FEATURE, oldAffectedFeature, affectedFeature));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DifferenceDescriptor getDescriptor() {
        if (eContainerFeatureID != ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR) return null;
        return (DifferenceDescriptor)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescriptor(DifferenceDescriptor newDescriptor) {
        if (newDescriptor != eContainer || (eContainerFeatureID != ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR && newDescriptor != null)) {
            if (EcoreUtil.isAncestor(this, newDescriptor))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newDescriptor != null)
                msgs = ((InternalEObject)newDescriptor).eInverseAdd(this, ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES, DifferenceDescriptor.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newDescriptor, ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR, newDescriptor, newDescriptor));
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
                case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR, msgs);
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
                case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                    return eBasicSetContainer(null, ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR, msgs);
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
                case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                    return eContainer.eInverseRemove(this, ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES, DifferenceDescriptor.class, msgs);
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
            case ComparePackage.PROPERTY_DIFFERENCE__NEW_VALUE:
                return getNewValue();
            case ComparePackage.PROPERTY_DIFFERENCE__OLD_VALUE:
                return getOldValue();
            case ComparePackage.PROPERTY_DIFFERENCE__SKIP:
                return isSkip() ? Boolean.TRUE : Boolean.FALSE;
            case ComparePackage.PROPERTY_DIFFERENCE__AFFECTED_FEATURE:
                if (resolve) return getAffectedFeature();
                return basicGetAffectedFeature();
            case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                return getDescriptor();
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
            case ComparePackage.PROPERTY_DIFFERENCE__NEW_VALUE:
                setNewValue(newValue);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__OLD_VALUE:
                setOldValue(newValue);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__SKIP:
                setSkip(((Boolean)newValue).booleanValue());
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__AFFECTED_FEATURE:
                setAffectedFeature((EStructuralFeature)newValue);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                setDescriptor((DifferenceDescriptor)newValue);
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
            case ComparePackage.PROPERTY_DIFFERENCE__NEW_VALUE:
                setNewValue(NEW_VALUE_EDEFAULT);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__OLD_VALUE:
                setOldValue(OLD_VALUE_EDEFAULT);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__SKIP:
                setSkip(SKIP_EDEFAULT);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__AFFECTED_FEATURE:
                setAffectedFeature((EStructuralFeature)null);
                return;
            case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                setDescriptor((DifferenceDescriptor)null);
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
            case ComparePackage.PROPERTY_DIFFERENCE__NEW_VALUE:
                return NEW_VALUE_EDEFAULT == null ? newValue != null : !NEW_VALUE_EDEFAULT.equals(newValue);
            case ComparePackage.PROPERTY_DIFFERENCE__OLD_VALUE:
                return OLD_VALUE_EDEFAULT == null ? oldValue != null : !OLD_VALUE_EDEFAULT.equals(oldValue);
            case ComparePackage.PROPERTY_DIFFERENCE__SKIP:
                return skip != SKIP_EDEFAULT;
            case ComparePackage.PROPERTY_DIFFERENCE__AFFECTED_FEATURE:
                return affectedFeature != null;
            case ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR:
                return getDescriptor() != null;
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
        result.append(" (newValue: "); //$NON-NLS-1$
        result.append(newValue);
        result.append(", oldValue: "); //$NON-NLS-1$
        result.append(oldValue);
        result.append(", skip: "); //$NON-NLS-1$
        result.append(skip);
        result.append(')');
        return result.toString();
    }

} //PropertyDifferenceImpl
