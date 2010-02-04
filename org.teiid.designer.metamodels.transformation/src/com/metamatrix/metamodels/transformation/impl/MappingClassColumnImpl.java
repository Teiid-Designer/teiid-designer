/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping Class Column</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassColumnImpl#getMappingClass <em>Mapping Class</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassColumnImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MappingClassColumnImpl extends MappingClassObjectImpl implements MappingClassColumn {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected EObject type = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MappingClassColumnImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getMappingClassColumn();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getType() {
        if (type != null && type.eIsProxy()) {
            EObject oldType = type;
            type = eResolveProxy((InternalEObject)type);
            if (type != oldType) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TransformationPackage.MAPPING_CLASS_COLUMN__TYPE, oldType, type));
            }
        }
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(EObject newType) {
        EObject oldType = type;
        type = newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS_COLUMN__TYPE, oldType, type));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MappingClass getMappingClass() {
        if (eContainerFeatureID != TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS) return null;
        return (MappingClass)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMappingClass(MappingClass newMappingClass) {
        if (newMappingClass != eContainer || (eContainerFeatureID != TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS && newMappingClass != null)) {
            if (EcoreUtil.isAncestor(this, newMappingClass))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newMappingClass != null)
                msgs = ((InternalEObject)newMappingClass).eInverseAdd(this, TransformationPackage.MAPPING_CLASS__COLUMNS, MappingClass.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newMappingClass, TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS, newMappingClass, newMappingClass));
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
                case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS, msgs);
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
                case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                    return eBasicSetContainer(null, TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS, msgs);
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
                case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                    return eContainer.eInverseRemove(this, TransformationPackage.MAPPING_CLASS__COLUMNS, MappingClass.class, msgs);
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
            case TransformationPackage.MAPPING_CLASS_COLUMN__NAME:
                return getName();
            case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                return getMappingClass();
            case TransformationPackage.MAPPING_CLASS_COLUMN__TYPE:
                if (resolve) return getType();
                return basicGetType();
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
            case TransformationPackage.MAPPING_CLASS_COLUMN__NAME:
                setName((String)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                setMappingClass((MappingClass)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS_COLUMN__TYPE:
                setType((EObject)newValue);
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
            case TransformationPackage.MAPPING_CLASS_COLUMN__NAME:
                setName(NAME_EDEFAULT);
                return;
            case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                setMappingClass((MappingClass)null);
                return;
            case TransformationPackage.MAPPING_CLASS_COLUMN__TYPE:
                setType((EObject)null);
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
            case TransformationPackage.MAPPING_CLASS_COLUMN__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case TransformationPackage.MAPPING_CLASS_COLUMN__MAPPING_CLASS:
                return getMappingClass() != null;
            case TransformationPackage.MAPPING_CLASS_COLUMN__TYPE:
                return type != null;
        }
        return eDynamicIsSet(eFeature);
    }

} //MappingClassColumnImpl
