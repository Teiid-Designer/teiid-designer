/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.transformation.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.MappingClass;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping Class Set</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassSetImpl#getMappingClasses <em>Mapping Classes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassSetImpl#getTarget <em>Target</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.MappingClassSetImpl#getInputBinding <em>Input Binding</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MappingClassSetImpl extends EObjectImpl implements MappingClassSet {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getMappingClasses() <em>Mapping Classes</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMappingClasses()
     * @generated
     * @ordered
     */
    protected EList mappingClasses = null;

    /**
     * The cached value of the '{@link #getTarget() <em>Target</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTarget()
     * @generated
     * @ordered
     */
    protected EObject target = null;

    /**
     * The cached value of the '{@link #getInputBinding() <em>Input Binding</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputBinding()
     * @generated
     * @ordered
     */
    protected EList inputBinding = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected MappingClassSetImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getMappingClassSet();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getMappingClasses() {
        if (mappingClasses == null) {
            mappingClasses = new EObjectContainmentWithInverseEList(MappingClass.class, this, TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES, TransformationPackage.MAPPING_CLASS__MAPPING_CLASS_SET);
        }
        return mappingClasses;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject getTarget() {
        if (target != null && target.eIsProxy()) {
            EObject oldTarget = target;
            target = eResolveProxy((InternalEObject)target);
            if (target != oldTarget) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TransformationPackage.MAPPING_CLASS_SET__TARGET, oldTarget, target));
            }
        }
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EObject basicGetTarget() {
        return target;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setTarget(EObject newTarget) {
        EObject oldTarget = target;
        target = newTarget;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.MAPPING_CLASS_SET__TARGET, oldTarget, target));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getInputBinding() {
        if (inputBinding == null) {
            inputBinding = new EObjectContainmentWithInverseEList(InputBinding.class, this, TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING, TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET);
        }
        return inputBinding;
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
                case TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES:
                    return ((InternalEList)getMappingClasses()).basicAdd(otherEnd, msgs);
                case TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING:
                    return ((InternalEList)getInputBinding()).basicAdd(otherEnd, msgs);
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
                case TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES:
                    return ((InternalEList)getMappingClasses()).basicRemove(otherEnd, msgs);
                case TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING:
                    return ((InternalEList)getInputBinding()).basicRemove(otherEnd, msgs);
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
            case TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES:
                return getMappingClasses();
            case TransformationPackage.MAPPING_CLASS_SET__TARGET:
                if (resolve) return getTarget();
                return basicGetTarget();
            case TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING:
                return getInputBinding();
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
            case TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES:
                getMappingClasses().clear();
                getMappingClasses().addAll((Collection)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS_SET__TARGET:
                setTarget((EObject)newValue);
                return;
            case TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING:
                getInputBinding().clear();
                getInputBinding().addAll((Collection)newValue);
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
            case TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES:
                getMappingClasses().clear();
                return;
            case TransformationPackage.MAPPING_CLASS_SET__TARGET:
                setTarget((EObject)null);
                return;
            case TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING:
                getInputBinding().clear();
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
            case TransformationPackage.MAPPING_CLASS_SET__MAPPING_CLASSES:
                return mappingClasses != null && !mappingClasses.isEmpty();
            case TransformationPackage.MAPPING_CLASS_SET__TARGET:
                return target != null;
            case TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING:
                return inputBinding != null && !inputBinding.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //MappingClassSetImpl
