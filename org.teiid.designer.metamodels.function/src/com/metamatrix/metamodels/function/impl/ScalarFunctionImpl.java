/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;

import com.metamatrix.metamodels.function.FunctionPackage;
import com.metamatrix.metamodels.function.FunctionParameter;
import com.metamatrix.metamodels.function.PushDownType;
import com.metamatrix.metamodels.function.ReturnParameter;
import com.metamatrix.metamodels.function.ScalarFunction;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Scalar Function</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.function.impl.ScalarFunctionImpl#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.impl.ScalarFunctionImpl#getReturnParameter <em>Return Parameter</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.impl.ScalarFunctionImpl#getInvocationClass <em>Invocation Class</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.impl.ScalarFunctionImpl#getInvocationMethod <em>Invocation Method</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.impl.ScalarFunctionImpl#isDeterministic <em>Deterministic</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ScalarFunctionImpl extends FunctionImpl implements ScalarFunction {
    /**
     * The cached value of the '{@link #getInputParameters() <em>Input Parameters</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputParameters()
     * @generated
     * @ordered
     */
    protected EList inputParameters = null;

    /**
     * The cached value of the '{@link #getReturnParameter() <em>Return Parameter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getReturnParameter()
     * @generated
     * @ordered
     */
    protected ReturnParameter returnParameter = null;

    /**
     * The default value of the '{@link #getInvocationClass() <em>Invocation Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInvocationClass()
     * @generated
     * @ordered
     */
    protected static final String INVOCATION_CLASS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInvocationClass() <em>Invocation Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInvocationClass()
     * @generated
     * @ordered
     */
    protected String invocationClass = INVOCATION_CLASS_EDEFAULT;

    /**
     * The default value of the '{@link #getInvocationMethod() <em>Invocation Method</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInvocationMethod()
     * @generated
     * @ordered
     */
    protected static final String INVOCATION_METHOD_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getInvocationMethod() <em>Invocation Method</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInvocationMethod()
     * @generated
     * @ordered
     */
    protected String invocationMethod = INVOCATION_METHOD_EDEFAULT;

    /**
     * The default value of the '{@link #isDeterministic() <em>Deterministic</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeterministic()
     * @generated
     * @ordered
     */
    protected static final boolean DETERMINISTIC_EDEFAULT = true;

    /**
     * The cached value of the '{@link #isDeterministic() <em>Deterministic</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeterministic()
     * @generated
     * @ordered
     */
    protected boolean deterministic = DETERMINISTIC_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ScalarFunctionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return FunctionPackage.eINSTANCE.getScalarFunction();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getInvocationClass() {
        return invocationClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInvocationClass(String newInvocationClass) {
        String oldInvocationClass = invocationClass;
        invocationClass = newInvocationClass;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.SCALAR_FUNCTION__INVOCATION_CLASS, oldInvocationClass, invocationClass));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getInvocationMethod() {
        return invocationMethod;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setInvocationMethod(String newInvocationMethod) {
        String oldInvocationMethod = invocationMethod;
        invocationMethod = newInvocationMethod;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.SCALAR_FUNCTION__INVOCATION_METHOD, oldInvocationMethod, invocationMethod));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isDeterministic() {
        return deterministic;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeterministic(boolean newDeterministic) {
        boolean oldDeterministic = deterministic;
        deterministic = newDeterministic;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.SCALAR_FUNCTION__DETERMINISTIC, oldDeterministic, deterministic));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getInputParameters() {
        if (inputParameters == null) {
            inputParameters = new EObjectContainmentWithInverseEList(FunctionParameter.class, this, FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS, FunctionPackage.FUNCTION_PARAMETER__FUNCTION);
        }
        return inputParameters;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ReturnParameter getReturnParameter() {
        return returnParameter;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetReturnParameter(ReturnParameter newReturnParameter, NotificationChain msgs) {
        ReturnParameter oldReturnParameter = returnParameter;
        returnParameter = newReturnParameter;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER, oldReturnParameter, newReturnParameter);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setReturnParameter(ReturnParameter newReturnParameter) {
        if (newReturnParameter != returnParameter) {
            NotificationChain msgs = null;
            if (returnParameter != null)
                msgs = ((InternalEObject)returnParameter).eInverseRemove(this, FunctionPackage.RETURN_PARAMETER__FUNCTION, ReturnParameter.class, msgs);
            if (newReturnParameter != null)
                msgs = ((InternalEObject)newReturnParameter).eInverseAdd(this, FunctionPackage.RETURN_PARAMETER__FUNCTION, ReturnParameter.class, msgs);
            msgs = basicSetReturnParameter(newReturnParameter, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER, newReturnParameter, newReturnParameter));
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
                case FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS:
                    return ((InternalEList)getInputParameters()).basicAdd(otherEnd, msgs);
                case FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER:
                    if (returnParameter != null)
                        msgs = ((InternalEObject)returnParameter).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER, null, msgs);
                    return basicSetReturnParameter((ReturnParameter)otherEnd, msgs);
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
                case FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS:
                    return ((InternalEList)getInputParameters()).basicRemove(otherEnd, msgs);
                case FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER:
                    return basicSetReturnParameter(null, msgs);
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
            case FunctionPackage.SCALAR_FUNCTION__NAME:
                return getName();
            case FunctionPackage.SCALAR_FUNCTION__CATEGORY:
                return getCategory();
            case FunctionPackage.SCALAR_FUNCTION__PUSH_DOWN:
                return getPushDown();
            case FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS:
                return getInputParameters();
            case FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER:
                return getReturnParameter();
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_CLASS:
                return getInvocationClass();
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_METHOD:
                return getInvocationMethod();
            case FunctionPackage.SCALAR_FUNCTION__DETERMINISTIC:
                return isDeterministic() ? Boolean.TRUE : Boolean.FALSE;
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
            case FunctionPackage.SCALAR_FUNCTION__NAME:
                setName((String)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__CATEGORY:
                setCategory((String)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__PUSH_DOWN:
                setPushDown((PushDownType)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS:
                getInputParameters().clear();
                getInputParameters().addAll((Collection)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER:
                setReturnParameter((ReturnParameter)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_CLASS:
                setInvocationClass((String)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_METHOD:
                setInvocationMethod((String)newValue);
                return;
            case FunctionPackage.SCALAR_FUNCTION__DETERMINISTIC:
                setDeterministic(((Boolean)newValue).booleanValue());
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
            case FunctionPackage.SCALAR_FUNCTION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case FunctionPackage.SCALAR_FUNCTION__CATEGORY:
                setCategory(CATEGORY_EDEFAULT);
                return;
            case FunctionPackage.SCALAR_FUNCTION__PUSH_DOWN:
                setPushDown(PUSH_DOWN_EDEFAULT);
                return;
            case FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS:
                getInputParameters().clear();
                return;
            case FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER:
                setReturnParameter((ReturnParameter)null);
                return;
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_CLASS:
                setInvocationClass(INVOCATION_CLASS_EDEFAULT);
                return;
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_METHOD:
                setInvocationMethod(INVOCATION_METHOD_EDEFAULT);
                return;
            case FunctionPackage.SCALAR_FUNCTION__DETERMINISTIC:
                setDeterministic(DETERMINISTIC_EDEFAULT);
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
            case FunctionPackage.SCALAR_FUNCTION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case FunctionPackage.SCALAR_FUNCTION__CATEGORY:
                return CATEGORY_EDEFAULT == null ? category != null : !CATEGORY_EDEFAULT.equals(category);
            case FunctionPackage.SCALAR_FUNCTION__PUSH_DOWN:
                return pushDown != PUSH_DOWN_EDEFAULT;
            case FunctionPackage.SCALAR_FUNCTION__INPUT_PARAMETERS:
                return inputParameters != null && !inputParameters.isEmpty();
            case FunctionPackage.SCALAR_FUNCTION__RETURN_PARAMETER:
                return returnParameter != null;
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_CLASS:
                return INVOCATION_CLASS_EDEFAULT == null ? invocationClass != null : !INVOCATION_CLASS_EDEFAULT.equals(invocationClass);
            case FunctionPackage.SCALAR_FUNCTION__INVOCATION_METHOD:
                return INVOCATION_METHOD_EDEFAULT == null ? invocationMethod != null : !INVOCATION_METHOD_EDEFAULT.equals(invocationMethod);
            case FunctionPackage.SCALAR_FUNCTION__DETERMINISTIC:
                return deterministic != DETERMINISTIC_EDEFAULT;
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
        result.append(" (invocationClass: "); //$NON-NLS-1$
        result.append(invocationClass);
        result.append(", invocationMethod: "); //$NON-NLS-1$
        result.append(invocationMethod);
        result.append(", deterministic: "); //$NON-NLS-1$
        result.append(deterministic);
        result.append(')');
        return result.toString();
    }

} //ScalarFunctionImpl
