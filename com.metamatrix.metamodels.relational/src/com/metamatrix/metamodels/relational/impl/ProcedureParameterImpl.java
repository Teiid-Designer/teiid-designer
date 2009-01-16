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

package com.metamatrix.metamodels.relational.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.relational.DirectionKind;
import com.metamatrix.metamodels.relational.NullableType;
import com.metamatrix.metamodels.relational.Procedure;
import com.metamatrix.metamodels.relational.ProcedureParameter;
import com.metamatrix.metamodels.relational.RelationalPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Procedure Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getDirection <em>Direction</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getDefaultValue <em>Default Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getNativeType <em>Native Type</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getLength <em>Length</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getPrecision <em>Precision</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getScale <em>Scale</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getNullable <em>Nullable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getRadix <em>Radix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getProcedure <em>Procedure</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProcedureParameterImpl extends RelationalEntityImpl implements ProcedureParameter {
    /**
     * The default value of the '{@link #getDirection() <em>Direction</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDirection()
     * @generated
     * @ordered
     */
    protected static final DirectionKind DIRECTION_EDEFAULT = DirectionKind.IN_LITERAL;

    /**
     * The cached value of the '{@link #getDirection() <em>Direction</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDirection()
     * @generated
     * @ordered
     */
    protected DirectionKind direction = DIRECTION_EDEFAULT;

    /**
     * The default value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultValue()
     * @generated
     * @ordered
     */
    protected static final String DEFAULT_VALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDefaultValue() <em>Default Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDefaultValue()
     * @generated
     * @ordered
     */
    protected String defaultValue = DEFAULT_VALUE_EDEFAULT;

    /**
     * The default value of the '{@link #getNativeType() <em>Native Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNativeType()
     * @generated
     * @ordered
     */
    protected static final String NATIVE_TYPE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getNativeType() <em>Native Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNativeType()
     * @generated
     * @ordered
     */
    protected String nativeType = NATIVE_TYPE_EDEFAULT;

    /**
     * The default value of the '{@link #getLength() <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLength()
     * @generated
     * @ordered
     */
    protected static final int LENGTH_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getLength() <em>Length</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLength()
     * @generated
     * @ordered
     */
    protected int length = LENGTH_EDEFAULT;

    /**
     * The default value of the '{@link #getPrecision() <em>Precision</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrecision()
     * @generated
     * @ordered
     */
    protected static final int PRECISION_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getPrecision() <em>Precision</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPrecision()
     * @generated
     * @ordered
     */
    protected int precision = PRECISION_EDEFAULT;

    /**
     * The default value of the '{@link #getScale() <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScale()
     * @generated
     * @ordered
     */
    protected static final int SCALE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getScale() <em>Scale</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getScale()
     * @generated
     * @ordered
     */
    protected int scale = SCALE_EDEFAULT;

    /**
     * The default value of the '{@link #getNullable() <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNullable()
     * @generated
     * @ordered
     */
    protected static final NullableType NULLABLE_EDEFAULT = NullableType.NO_NULLS_LITERAL;

    /**
     * The cached value of the '{@link #getNullable() <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNullable()
     * @generated
     * @ordered
     */
    protected NullableType nullable = NULLABLE_EDEFAULT;

    /**
     * The default value of the '{@link #getRadix() <em>Radix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRadix()
     * @generated
     * @ordered
     */
    protected static final int RADIX_EDEFAULT = 10;

    /**
     * The cached value of the '{@link #getRadix() <em>Radix</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRadix()
     * @generated
     * @ordered
     */
    protected int radix = RADIX_EDEFAULT;

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
     * This is true if the Type reference has been set.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    protected boolean typeESet = false;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProcedureParameterImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.eINSTANCE.getProcedureParameter();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DirectionKind getDirection() {
        return direction;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDirection(DirectionKind newDirection) {
        DirectionKind oldDirection = direction;
        direction = newDirection == null ? DIRECTION_EDEFAULT : newDirection;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__DIRECTION, oldDirection, direction));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDefaultValue(String newDefaultValue) {
        String oldDefaultValue = defaultValue;
        defaultValue = newDefaultValue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__DEFAULT_VALUE, oldDefaultValue, defaultValue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getNativeType() {
        return nativeType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNativeType(String newNativeType) {
        String oldNativeType = nativeType;
        nativeType = newNativeType;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__NATIVE_TYPE, oldNativeType, nativeType));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getLength() {
        return length;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLength(int newLength) {
        int oldLength = length;
        length = newLength;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__LENGTH, oldLength, length));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPrecision(int newPrecision) {
        int oldPrecision = precision;
        precision = newPrecision;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__PRECISION, oldPrecision, precision));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getScale() {
        return scale;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setScale(int newScale) {
        int oldScale = scale;
        scale = newScale;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__SCALE, oldScale, scale));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NullableType getNullable() {
        return nullable;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNullable(NullableType newNullable) {
        NullableType oldNullable = nullable;
        nullable = newNullable == null ? NULLABLE_EDEFAULT : newNullable;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__NULLABLE, oldNullable, nullable));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getRadix() {
        return radix;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRadix(int newRadix) {
        int oldRadix = radix;
        radix = newRadix;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__RADIX, oldRadix, radix));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Procedure getProcedure() {
        if (eContainerFeatureID != RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE) return null;
        return (Procedure)eContainer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setProcedure(Procedure newProcedure) {
        if (newProcedure != eContainer || (eContainerFeatureID != RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE && newProcedure != null)) {
            if (EcoreUtil.isAncestor(this, newProcedure))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newProcedure != null)
                msgs = ((InternalEObject)newProcedure).eInverseAdd(this, RelationalPackage.PROCEDURE__PARAMETERS, Procedure.class, msgs);
            msgs = eBasicSetContainer((InternalEObject)newProcedure, RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE, newProcedure, newProcedure));
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
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, RelationalPackage.PROCEDURE_PARAMETER__TYPE, oldType, type));
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
        boolean oldTypeESet = typeESet;
        typeESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, RelationalPackage.PROCEDURE_PARAMETER__TYPE, oldType, type, !oldTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void unsetType() {
        EObject oldType = type;
        boolean oldTypeESet = typeESet;
        type = null;
        typeESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, RelationalPackage.PROCEDURE_PARAMETER__TYPE, oldType, null, oldTypeESet));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isSetType() {
        return typeESet;
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
                case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                    if (eContainer != null)
                        msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE, msgs);
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
                case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                    return eBasicSetContainer(null, RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE, msgs);
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
                case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                    return eContainer.eInverseRemove(this, RelationalPackage.PROCEDURE__PARAMETERS, Procedure.class, msgs);
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
            case RelationalPackage.PROCEDURE_PARAMETER__NAME:
                return getName();
            case RelationalPackage.PROCEDURE_PARAMETER__NAME_IN_SOURCE:
                return getNameInSource();
            case RelationalPackage.PROCEDURE_PARAMETER__DIRECTION:
                return getDirection();
            case RelationalPackage.PROCEDURE_PARAMETER__DEFAULT_VALUE:
                return getDefaultValue();
            case RelationalPackage.PROCEDURE_PARAMETER__NATIVE_TYPE:
                return getNativeType();
            case RelationalPackage.PROCEDURE_PARAMETER__LENGTH:
                return new Integer(getLength());
            case RelationalPackage.PROCEDURE_PARAMETER__PRECISION:
                return new Integer(getPrecision());
            case RelationalPackage.PROCEDURE_PARAMETER__SCALE:
                return new Integer(getScale());
            case RelationalPackage.PROCEDURE_PARAMETER__NULLABLE:
                return getNullable();
            case RelationalPackage.PROCEDURE_PARAMETER__RADIX:
                return new Integer(getRadix());
            case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                return getProcedure();
            case RelationalPackage.PROCEDURE_PARAMETER__TYPE:
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
            case RelationalPackage.PROCEDURE_PARAMETER__NAME:
                setName((String)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__DIRECTION:
                setDirection((DirectionKind)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__DEFAULT_VALUE:
                setDefaultValue((String)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__NATIVE_TYPE:
                setNativeType((String)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__LENGTH:
                setLength(((Integer)newValue).intValue());
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__PRECISION:
                setPrecision(((Integer)newValue).intValue());
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__SCALE:
                setScale(((Integer)newValue).intValue());
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__NULLABLE:
                setNullable((NullableType)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__RADIX:
                setRadix(((Integer)newValue).intValue());
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                setProcedure((Procedure)newValue);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__TYPE:
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
            case RelationalPackage.PROCEDURE_PARAMETER__NAME:
                setName(NAME_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__DIRECTION:
                setDirection(DIRECTION_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__DEFAULT_VALUE:
                setDefaultValue(DEFAULT_VALUE_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__NATIVE_TYPE:
                setNativeType(NATIVE_TYPE_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__LENGTH:
                setLength(LENGTH_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__PRECISION:
                setPrecision(PRECISION_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__SCALE:
                setScale(SCALE_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__NULLABLE:
                setNullable(NULLABLE_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__RADIX:
                setRadix(RADIX_EDEFAULT);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                setProcedure((Procedure)null);
                return;
            case RelationalPackage.PROCEDURE_PARAMETER__TYPE:
                unsetType();
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
            case RelationalPackage.PROCEDURE_PARAMETER__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case RelationalPackage.PROCEDURE_PARAMETER__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case RelationalPackage.PROCEDURE_PARAMETER__DIRECTION:
                return direction != DIRECTION_EDEFAULT;
            case RelationalPackage.PROCEDURE_PARAMETER__DEFAULT_VALUE:
                return DEFAULT_VALUE_EDEFAULT == null ? defaultValue != null : !DEFAULT_VALUE_EDEFAULT.equals(defaultValue);
            case RelationalPackage.PROCEDURE_PARAMETER__NATIVE_TYPE:
                return NATIVE_TYPE_EDEFAULT == null ? nativeType != null : !NATIVE_TYPE_EDEFAULT.equals(nativeType);
            case RelationalPackage.PROCEDURE_PARAMETER__LENGTH:
                return length != LENGTH_EDEFAULT;
            case RelationalPackage.PROCEDURE_PARAMETER__PRECISION:
                return precision != PRECISION_EDEFAULT;
            case RelationalPackage.PROCEDURE_PARAMETER__SCALE:
                return scale != SCALE_EDEFAULT;
            case RelationalPackage.PROCEDURE_PARAMETER__NULLABLE:
                return nullable != NULLABLE_EDEFAULT;
            case RelationalPackage.PROCEDURE_PARAMETER__RADIX:
                return radix != RADIX_EDEFAULT;
            case RelationalPackage.PROCEDURE_PARAMETER__PROCEDURE:
                return getProcedure() != null;
            case RelationalPackage.PROCEDURE_PARAMETER__TYPE:
                return isSetType();
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
        result.append(" (direction: "); //$NON-NLS-1$
        result.append(direction);
        result.append(", defaultValue: "); //$NON-NLS-1$
        result.append(defaultValue);
        result.append(", nativeType: "); //$NON-NLS-1$
        result.append(nativeType);
        result.append(", length: "); //$NON-NLS-1$
        result.append(length);
        result.append(", precision: "); //$NON-NLS-1$
        result.append(precision);
        result.append(", scale: "); //$NON-NLS-1$
        result.append(scale);
        result.append(", nullable: "); //$NON-NLS-1$
        result.append(nullable);
        result.append(", radix: "); //$NON-NLS-1$
        result.append(radix);
        result.append(')');
        return result.toString();
    }

} //ProcedureParameterImpl
