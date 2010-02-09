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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import com.metamatrix.metamodels.transformation.InputBinding;
import com.metamatrix.metamodels.transformation.InputParameter;
import com.metamatrix.metamodels.transformation.MappingClassColumn;
import com.metamatrix.metamodels.transformation.MappingClassSet;
import com.metamatrix.metamodels.transformation.TransformationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Input Binding</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.transformation.impl.InputBindingImpl#getMappingClassSet <em>Mapping Class Set</em>}</li>
 * <li>{@link com.metamatrix.metamodels.transformation.impl.InputBindingImpl#getInputParameter <em>Input Parameter</em>}</li>
 * <li>{@link com.metamatrix.metamodels.transformation.impl.InputBindingImpl#getMappingClassColumn <em>Mapping Class Column</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class InputBindingImpl extends EObjectImpl implements InputBinding {

    /**
     * The cached value of the '{@link #getInputParameter() <em>Input Parameter</em>}' reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getInputParameter()
     * @generated
     * @ordered
     */
    protected InputParameter inputParameter = null;

    /**
     * The cached value of the '{@link #getMappingClassColumn() <em>Mapping Class Column</em>}' reference. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getMappingClassColumn()
     * @generated
     * @ordered
     */
    protected MappingClassColumn mappingClassColumn = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected InputBindingImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return TransformationPackage.eINSTANCE.getInputBinding();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MappingClassSet getMappingClassSet() {
        if (eContainerFeatureID != TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET) return null;
        return (MappingClassSet)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMappingClassSet( MappingClassSet newMappingClassSet ) {
        if (newMappingClassSet != eContainer
            || (eContainerFeatureID != TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET && newMappingClassSet != null)) {
            if (EcoreUtil.isAncestor(this, newMappingClassSet)) throw new IllegalArgumentException(
                                                                                                   "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newMappingClassSet != null) msgs = ((InternalEObject)newMappingClassSet).eInverseAdd(this,
                                                                                                     TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING,
                                                                                                     MappingClassSet.class,
                                                                                                     msgs);
            msgs = eBasicSetContainer((InternalEObject)newMappingClassSet,
                                      TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET,
                                      msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET,
                                                                          newMappingClassSet, newMappingClassSet));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public InputParameter getInputParameter() {
        if (inputParameter != null && inputParameter.eIsProxy()) {
            InputParameter oldInputParameter = inputParameter;
            inputParameter = (InputParameter)eResolveProxy((InternalEObject)inputParameter);
            if (inputParameter != oldInputParameter) {
                if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.RESOLVE,
                                                                           TransformationPackage.INPUT_BINDING__INPUT_PARAMETER,
                                                                           oldInputParameter, inputParameter));
            }
        }
        return inputParameter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public InputParameter basicGetInputParameter() {
        return inputParameter;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setInputParameter( InputParameter newInputParameter ) {
        InputParameter oldInputParameter = inputParameter;
        inputParameter = newInputParameter;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   TransformationPackage.INPUT_BINDING__INPUT_PARAMETER,
                                                                   oldInputParameter, inputParameter));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MappingClassColumn getMappingClassColumn() {
        if (mappingClassColumn != null && mappingClassColumn.eIsProxy()) {
            MappingClassColumn oldMappingClassColumn = mappingClassColumn;
            mappingClassColumn = (MappingClassColumn)eResolveProxy((InternalEObject)mappingClassColumn);
            if (mappingClassColumn != oldMappingClassColumn) {
                if (eNotificationRequired()) eNotify(new ENotificationImpl(
                                                                           this,
                                                                           Notification.RESOLVE,
                                                                           TransformationPackage.INPUT_BINDING__MAPPING_CLASS_COLUMN,
                                                                           oldMappingClassColumn, mappingClassColumn));
            }
        }
        return mappingClassColumn;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public MappingClassColumn basicGetMappingClassColumn() {
        return mappingClassColumn;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setMappingClassColumn( MappingClassColumn newMappingClassColumn ) {
        MappingClassColumn oldMappingClassColumn = mappingClassColumn;
        mappingClassColumn = newMappingClassColumn;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                   TransformationPackage.INPUT_BINDING__MAPPING_CLASS_COLUMN,
                                                                   oldMappingClassColumn, mappingClassColumn));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd( InternalEObject otherEnd,
                                          int featureID,
                                          Class baseClass,
                                          NotificationChain msgs ) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET, msgs);
                default:
                    return eDynamicInverseAdd(otherEnd, featureID, baseClass, msgs);
            }
        }
        if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
        return eBasicSetContainer(otherEnd, featureID, msgs);
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
                case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                    return eBasicSetContainer(null, TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET, msgs);
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
    public NotificationChain eBasicRemoveFromContainer( NotificationChain msgs ) {
        if (eContainerFeatureID >= 0) {
            switch (eContainerFeatureID) {
                case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                    return eContainer.eInverseRemove(this,
                                                     TransformationPackage.MAPPING_CLASS_SET__INPUT_BINDING,
                                                     MappingClassSet.class,
                                                     msgs);
                default:
                    return eDynamicBasicRemoveFromContainer(msgs);
            }
        }
        return eContainer.eInverseRemove(this, EOPPOSITE_FEATURE_BASE - eContainerFeatureID, null, msgs);
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
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                return getMappingClassSet();
            case TransformationPackage.INPUT_BINDING__INPUT_PARAMETER:
                if (resolve) return getInputParameter();
                return basicGetInputParameter();
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_COLUMN:
                if (resolve) return getMappingClassColumn();
                return basicGetMappingClassColumn();
        }
        return eDynamicGet(eFeature, resolve);
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
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                setMappingClassSet((MappingClassSet)newValue);
                return;
            case TransformationPackage.INPUT_BINDING__INPUT_PARAMETER:
                setInputParameter((InputParameter)newValue);
                return;
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_COLUMN:
                setMappingClassColumn((MappingClassColumn)newValue);
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
    public void eUnset( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                setMappingClassSet((MappingClassSet)null);
                return;
            case TransformationPackage.INPUT_BINDING__INPUT_PARAMETER:
                setInputParameter((InputParameter)null);
                return;
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_COLUMN:
                setMappingClassColumn((MappingClassColumn)null);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet( EStructuralFeature eFeature ) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_SET:
                return getMappingClassSet() != null;
            case TransformationPackage.INPUT_BINDING__INPUT_PARAMETER:
                return inputParameter != null;
            case TransformationPackage.INPUT_BINDING__MAPPING_CLASS_COLUMN:
                return mappingClassColumn != null;
        }
        return eDynamicIsSet(eFeature);
    }

} // InputBindingImpl
