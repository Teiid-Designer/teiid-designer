/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.metamatrix.metamodels.function.Function;
import com.metamatrix.metamodels.function.FunctionPackage;

import com.metamatrix.metamodels.function.PushDownType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Function</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.function.impl.FunctionImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.impl.FunctionImpl#getCategory <em>Category</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.impl.FunctionImpl#getPushDown <em>Push Down</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class FunctionImpl extends EObjectImpl implements Function {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getCategory() <em>Category</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCategory()
     * @generated
     * @ordered
     */
    protected static final String CATEGORY_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCategory() <em>Category</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCategory()
     * @generated
     * @ordered
     */
    protected String category = CATEGORY_EDEFAULT;

    /**
     * The default value of the '{@link #getPushDown() <em>Push Down</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPushDown()
     * @generated
     * @ordered
     */
    protected static final PushDownType PUSH_DOWN_EDEFAULT = PushDownType.ALLOWED_LITERAL;

    /**
     * The cached value of the '{@link #getPushDown() <em>Push Down</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPushDown()
     * @generated
     * @ordered
     */
    protected PushDownType pushDown = PUSH_DOWN_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected FunctionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return FunctionPackage.eINSTANCE.getFunction();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getCategory() {
        return category;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCategory(String newCategory) {
        String oldCategory = category;
        category = newCategory;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__CATEGORY, oldCategory, category));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PushDownType getPushDown() {
        return pushDown;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPushDown(PushDownType newPushDown) {
        PushDownType oldPushDown = pushDown;
        pushDown = newPushDown == null ? PUSH_DOWN_EDEFAULT : newPushDown;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.FUNCTION__PUSH_DOWN, oldPushDown, pushDown));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case FunctionPackage.FUNCTION__NAME:
                return getName();
            case FunctionPackage.FUNCTION__CATEGORY:
                return getCategory();
            case FunctionPackage.FUNCTION__PUSH_DOWN:
                return getPushDown();
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
            case FunctionPackage.FUNCTION__NAME:
                setName((String)newValue);
                return;
            case FunctionPackage.FUNCTION__CATEGORY:
                setCategory((String)newValue);
                return;
            case FunctionPackage.FUNCTION__PUSH_DOWN:
                setPushDown((PushDownType)newValue);
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
            case FunctionPackage.FUNCTION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case FunctionPackage.FUNCTION__CATEGORY:
                setCategory(CATEGORY_EDEFAULT);
                return;
            case FunctionPackage.FUNCTION__PUSH_DOWN:
                setPushDown(PUSH_DOWN_EDEFAULT);
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
            case FunctionPackage.FUNCTION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case FunctionPackage.FUNCTION__CATEGORY:
                return CATEGORY_EDEFAULT == null ? category != null : !CATEGORY_EDEFAULT.equals(category);
            case FunctionPackage.FUNCTION__PUSH_DOWN:
                return pushDown != PUSH_DOWN_EDEFAULT;
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
        result.append(" (name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", category: "); //$NON-NLS-1$
        result.append(category);
        result.append(", pushDown: "); //$NON-NLS-1$
        result.append(pushDown);
        result.append(')');
        return result.toString();
    }

} //FunctionImpl
