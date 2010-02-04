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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.mapping.Mapping;
import org.eclipse.emf.mapping.MappingHelper;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingRootImpl;
import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Mapping Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.impl.TransformationMappingRootImpl#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class TransformationMappingRootImpl extends MappingRootImpl implements TransformationMappingRoot {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TransformationMappingRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getTransformationMappingRoot();
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
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET, oldTarget, target));
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
            eNotify(new ENotificationImpl(this, Notification.SET, TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET, oldTarget, target));
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
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER:
                    if (helper != null)
                        msgs = ((InternalEObject)helper).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER, null, msgs);
                    return basicSetHelper((MappingHelper)otherEnd, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED:
                    return ((InternalEList)getNested()).basicAdd(otherEnd, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN, msgs);
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
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER:
                    return basicSetHelper(null, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED:
                    return ((InternalEList)getNested()).basicRemove(otherEnd, msgs);
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                    return eBasicSetContainer(null, TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN, msgs);
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
                case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                    return eContainer.eInverseRemove(this, MappingPackage.MAPPING__NESTED, Mapping.class, msgs);
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
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER:
                return getHelper();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED:
                return getNested();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                return getNestedIn();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__INPUTS:
                return getInputs();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUTS:
                return getOutputs();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING:
                if (resolve) return getTypeMapping();
                return basicGetTypeMapping();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY:
                return isOutputReadOnly() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM:
                return isTopToBottom() ? Boolean.TRUE : Boolean.FALSE;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK:
                return getCommandStack();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET:
                if (resolve) return getTarget();
                return basicGetTarget();
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
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER:
                setHelper((MappingHelper)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED:
                getNested().clear();
                getNested().addAll((Collection)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                setNestedIn((Mapping)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__INPUTS:
                getInputs().clear();
                getInputs().addAll((Collection)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUTS:
                getOutputs().clear();
                getOutputs().addAll((Collection)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING:
                setTypeMapping((Mapping)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY:
                setOutputReadOnly(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM:
                setTopToBottom(((Boolean)newValue).booleanValue());
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK:
                setCommandStack((String)newValue);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET:
                setTarget((EObject)newValue);
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
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER:
                setHelper((MappingHelper)null);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED:
                getNested().clear();
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                setNestedIn((Mapping)null);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__INPUTS:
                getInputs().clear();
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUTS:
                getOutputs().clear();
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING:
                setTypeMapping((Mapping)null);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY:
                setOutputReadOnly(OUTPUT_READ_ONLY_EDEFAULT);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM:
                setTopToBottom(TOP_TO_BOTTOM_EDEFAULT);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK:
                setCommandStack(COMMAND_STACK_EDEFAULT);
                return;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET:
                setTarget((EObject)null);
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
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__HELPER:
                return helper != null;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED:
                return nested != null && !nested.isEmpty();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__NESTED_IN:
                return getNestedIn() != null;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__INPUTS:
                return inputs != null && !inputs.isEmpty();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUTS:
                return outputs != null && !outputs.isEmpty();
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TYPE_MAPPING:
                return typeMapping != null;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__OUTPUT_READ_ONLY:
                return outputReadOnly != OUTPUT_READ_ONLY_EDEFAULT;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TOP_TO_BOTTOM:
                return topToBottom != TOP_TO_BOTTOM_EDEFAULT;
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__COMMAND_STACK:
                return COMMAND_STACK_EDEFAULT == null ? commandStack != null : !COMMAND_STACK_EDEFAULT.equals(commandStack);
            case TransformationPackage.TRANSFORMATION_MAPPING_ROOT__TARGET:
                return target != null;
        }
        return eDynamicIsSet(eFeature);
    }

// GSG 5/19/05 - This has been commented out due to the regeneration of the transformation metamodel. These overridden methods might
// no longer be needed.
//  /**
//  * Overrides the MappingImpl implementation to fix defect 10694.
//  * @see org.eclipse.emf.mapping.MappingRoot#createMapping(java.util.Collection, java.util.Collection)
//  */
// public Mapping createMapping(Collection inputs, Collection outputs)
// {
//   Mapping newMapping = MappingPackage.eINSTANCE.getMappingFactory().createMapping();
//   newMapping.getInputs().addAll(inputs);
//   newMapping.getOutputs().addAll(outputs);
//
//   if (getTypeMappingRoot() != null)
//   {
//     Collection inputTypes = getTypeClassifiers(inputs);
//     if (!inputTypes.isEmpty())
//     {
//       Collection outputTypes = getTypeClassifiers(outputs);
//
//       Collection typeMappings = getTypeMappings(inputTypes, outputTypes);
//       if (!typeMappings.isEmpty())
//       {
//         newMapping.setTypeMapping((Mapping)typeMappings.iterator().next());
//       }
//     }
//   }
//   return newMapping;
// }
// 

     /**
      * Overrides the MappingImpl implementation to so that RemoveCommands on the MappingRoot objects work.
      * Default implementation for some reason seems to check that the root level object for every input/output
      * object on a MappingRoot also needs to be on the inputs/outputs for a command to ececute.
      * See defect 13015 (MappingDomains need to be set on MappingRoots; merging mapping calsses followed by 
      * undoing merge followed by remerging is causing an error when trying to execute a Compound command 
      * having a remove command. This is hapenning because canCreateMapping() is returning false on MappingRoot 
      * object. Overrided the method in TransformationMappingRoootImpl to always return true.
      * @see org.eclipse.emf.mapping.MappingRoot#canCreateMapping(java.util.Collection, java.util.Collection, org.eclipse.emf.mapping.Mapping)
      */
     @Override
    public boolean canCreateMapping(Collection inputs, Collection outputs, Mapping mapping) {
         return true;
     }
    
    
} //TransformationMappingRootImpl
