/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.compare.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingHelperImpl;

import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.PropertyDifference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Difference Descriptor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceDescriptorImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceDescriptorImpl#isSkip <em>Skip</em>}</li>
 *   <li>{@link com.metamatrix.modeler.compare.impl.DifferenceDescriptorImpl#getPropertyDifferences <em>Property Differences</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DifferenceDescriptorImpl extends MappingHelperImpl implements DifferenceDescriptor {
    /**
     * The default value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected static final DifferenceType TYPE_EDEFAULT = DifferenceType.NO_CHANGE_LITERAL;

    /**
     * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getType()
     * @generated
     * @ordered
     */
    protected DifferenceType type = TYPE_EDEFAULT;

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
     * The cached value of the '{@link #getPropertyDifferences() <em>Property Differences</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPropertyDifferences()
     * @generated
     * @ordered
     */
    protected EList propertyDifferences = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DifferenceDescriptorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ComparePackage.eINSTANCE.getDifferenceDescriptor();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DifferenceType getType() {
        return type;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setType(DifferenceType newType) {
        DifferenceType oldType = type;
        type = newType == null ? TYPE_EDEFAULT : newType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_DESCRIPTOR__TYPE, oldType, type));
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
            eNotify(new ENotificationImpl(this, Notification.SET, ComparePackage.DIFFERENCE_DESCRIPTOR__SKIP, oldSkip, skip));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getPropertyDifferences() {
        if (propertyDifferences == null) {
            propertyDifferences = new EObjectContainmentWithInverseEList(PropertyDifference.class, this, ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES, ComparePackage.PROPERTY_DIFFERENCE__DESCRIPTOR);
        }
        return propertyDifferences;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isDeletion() {
        return DifferenceType.DELETION_LITERAL.equals(this.type);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isDeletionGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isAddition() {
        return DifferenceType.ADDITION_LITERAL.equals(this.type);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAdditionGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isChanged() {
        return DifferenceType.CHANGE_LITERAL.equals(this.type);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isChangedGen() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isChangedBelow() {
        return DifferenceType.CHANGE_BELOW_LITERAL.equals(this.type);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    public boolean isNoChange() {
        return DifferenceType.NO_CHANGE_LITERAL.equals(this.type);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isNoChangeGen() {
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
                case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED:
                    return ((InternalEList)getNested()).basicAdd(otherEnd, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
                    return ((InternalEList)getPropertyDifferences()).basicAdd(otherEnd, msgs);
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
                case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                    return eBasicSetContainer(null, ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                    return eBasicSetContainer(null, ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED:
                    return ((InternalEList)getNested()).basicRemove(otherEnd, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
                    return ((InternalEList)getPropertyDifferences()).basicRemove(otherEnd, msgs);
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
                case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING__HELPER, Mapping.class, msgs);
                case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING_HELPER__NESTED, MappingHelper.class, msgs);
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
            case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                return getMapper();
            case ComparePackage.DIFFERENCE_DESCRIPTOR__HELPED_OBJECT:
                if (resolve) return getHelpedObject();
                return basicGetHelpedObject();
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                return getNestedIn();
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED:
                return getNested();
            case ComparePackage.DIFFERENCE_DESCRIPTOR__TYPE:
                return getType();
            case ComparePackage.DIFFERENCE_DESCRIPTOR__SKIP:
                return isSkip() ? Boolean.TRUE : Boolean.FALSE;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
                return getPropertyDifferences();
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
            case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                setMapper((Mapping)newValue);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__HELPED_OBJECT:
                setHelpedObject((EObject)newValue);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                setNestedIn((MappingHelper)newValue);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED:
                getNested().clear();
                getNested().addAll((Collection)newValue);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__TYPE:
                setType((DifferenceType)newValue);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__SKIP:
                setSkip(((Boolean)newValue).booleanValue());
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
                getPropertyDifferences().clear();
                getPropertyDifferences().addAll((Collection)newValue);
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
            case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                setMapper((Mapping)null);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__HELPED_OBJECT:
                setHelpedObject((EObject)null);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                setNestedIn((MappingHelper)null);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED:
                getNested().clear();
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__TYPE:
                setType(TYPE_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__SKIP:
                setSkip(SKIP_EDEFAULT);
                return;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
                getPropertyDifferences().clear();
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
            case ComparePackage.DIFFERENCE_DESCRIPTOR__MAPPER:
                return getMapper() != null;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__HELPED_OBJECT:
                return helpedObject != null;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED_IN:
                return getNestedIn() != null;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__NESTED:
                return nested != null && !nested.isEmpty();
            case ComparePackage.DIFFERENCE_DESCRIPTOR__TYPE:
                return type != TYPE_EDEFAULT;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__SKIP:
                return skip != SKIP_EDEFAULT;
            case ComparePackage.DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES:
                return propertyDifferences != null && !propertyDifferences.isEmpty();
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
        result.append(" (type: "); //$NON-NLS-1$
        result.append(type);
        result.append(", skip: "); //$NON-NLS-1$
        result.append(skip);
        result.append(')');
        return result.toString();
    }

} //DifferenceDescriptorImpl
