/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.transformation.TransformationContainer;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Container</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.impl.TransformationContainerImpl#getTransformationMappings <em>
 * Transformation Mappings</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TransformationContainerImpl extends EObjectImpl implements TransformationContainer {

    /**
     * The cached value of the '{@link #getTransformationMappings() <em>Transformation Mappings</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getTransformationMappings()
     * @generated
     * @ordered
     */
    protected EList transformationMappings = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected TransformationContainerImpl() {
        super();
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
            case TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS:
                return getTransformationMappings();
        }
        return eDynamicGet(eFeature, resolve);
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
                case TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS:
                    return ((InternalEList)getTransformationMappings()).basicRemove(otherEnd, msgs);
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
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS:
                return transformationMappings != null && !transformationMappings.isEmpty();
        }
        return eDynamicIsSet(eFeature);
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
            case TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS:
                getTransformationMappings().clear();
                getTransformationMappings().addAll((Collection)newValue);
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
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getTransformationContainer();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS:
                getTransformationMappings().clear();
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList getTransformationMappings() {
        if (transformationMappings == null) {
            transformationMappings = new EObjectContainmentEList(
                                                                 TransformationMappingRoot.class,
                                                                 this,
                                                                 TransformationPackage.TRANSFORMATION_CONTAINER__TRANSFORMATION_MAPPINGS);
        }
        return transformationMappings;
    }

} // TransformationContainerImpl
