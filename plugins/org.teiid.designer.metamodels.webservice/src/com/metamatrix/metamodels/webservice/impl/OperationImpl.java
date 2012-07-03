/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.OperationUpdateCount;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.WebServicePackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Operation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.OperationImpl#getPattern <em>Pattern</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.OperationImpl#isSafe <em>Safe</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.OperationImpl#getInput <em>Input</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.OperationImpl#getOutput <em>Output</em>}</li>
 * <li>{@link com.metamatrix.metamodels.webservice.impl.OperationImpl#getInterface <em>Interface</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class OperationImpl extends WebServiceComponentImpl implements Operation {

    /**
     * The default value of the '{@link #getPattern() <em>Pattern</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getPattern()
     * @generated
     * @ordered
     */
    protected static final String PATTERN_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPattern() <em>Pattern</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getPattern()
     * @generated
     * @ordered
     */
    protected String pattern = PATTERN_EDEFAULT;

    /**
     * The default value of the '{@link #isSafe() <em>Safe</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isSafe()
     * @generated
     * @ordered
     */
    protected static final boolean SAFE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isSafe() <em>Safe</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #isSafe()
     * @generated
     * @ordered
     */
    protected boolean safe = SAFE_EDEFAULT;

    /**
     * The cached value of the '{@link #getInput() <em>Input</em>}' containment reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getInput()
     * @generated
     * @ordered
     */
    protected Input input = null;

    /**
     * The cached value of the '{@link #getOutput() <em>Output</em>}' containment reference. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getOutput()
     * @generated
     * @ordered
     */
    protected Output output = null;
    
    /**
     * The default value of the '{@link #getUpdateCount() <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateCount()
     * @generated
     * @ordered
     */
    protected static final OperationUpdateCount UPDATE_COUNT_EDEFAULT = OperationUpdateCount.AUTO_LITERAL;
    
    /**
     * The cached value of the '{@link #getUpdateCount() <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getUpdateCount()
     * @generated
     * @ordered
     */
    protected OperationUpdateCount updateCount = UPDATE_COUNT_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected OperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return WebServicePackage.eINSTANCE.getOperation();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setPattern( String newPattern ) {
        String oldPattern = pattern;
        pattern = newPattern;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.OPERATION__PATTERN,
                                                                   oldPattern, pattern));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public boolean isSafe() {
        return safe;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setSafe( boolean newSafe ) {
        boolean oldSafe = safe;
        safe = newSafe;
        if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.OPERATION__SAFE,
                                                                   oldSafe, safe));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Input getInput() {
        return input;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetInput( Input newInput,
                                            NotificationChain msgs ) {
        Input oldInput = input;
        input = newInput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WebServicePackage.OPERATION__INPUT,
                                                                   oldInput, newInput);
            if (msgs == null) msgs = notification;
            else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setInput( Input newInput ) {
        if (newInput != input) {
            NotificationChain msgs = null;
            if (input != null) msgs = ((InternalEObject)input).eInverseRemove(this,
                                                                              WebServicePackage.INPUT__OPERATION,
                                                                              Input.class,
                                                                              msgs);
            if (newInput != null) msgs = ((InternalEObject)newInput).eInverseAdd(this,
                                                                                 WebServicePackage.INPUT__OPERATION,
                                                                                 Input.class,
                                                                                 msgs);
            msgs = basicSetInput(newInput, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          WebServicePackage.OPERATION__INPUT, newInput, newInput));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Output getOutput() {
        return output;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public NotificationChain basicSetOutput( Output newOutput,
                                             NotificationChain msgs ) {
        Output oldOutput = output;
        output = newOutput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WebServicePackage.OPERATION__OUTPUT,
                                                                   oldOutput, newOutput);
            if (msgs == null) msgs = notification;
            else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setOutput( Output newOutput ) {
        if (newOutput != output) {
            NotificationChain msgs = null;
            if (output != null) msgs = ((InternalEObject)output).eInverseRemove(this,
                                                                                WebServicePackage.OUTPUT__OPERATION,
                                                                                Output.class,
                                                                                msgs);
            if (newOutput != null) msgs = ((InternalEObject)newOutput).eInverseAdd(this,
                                                                                   WebServicePackage.OUTPUT__OPERATION,
                                                                                   Output.class,
                                                                                   msgs);
            msgs = basicSetOutput(newOutput, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          WebServicePackage.OPERATION__OUTPUT, newOutput,
                                                                          newOutput));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Interface getInterface() {
        if (eContainerFeatureID != WebServicePackage.OPERATION__INTERFACE) return null;
        return (Interface)eContainer;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setInterface( Interface newInterface ) {
        if (newInterface != eContainer || (eContainerFeatureID != WebServicePackage.OPERATION__INTERFACE && newInterface != null)) {
            if (EcoreUtil.isAncestor(this, newInterface)) throw new IllegalArgumentException(
                                                                                             "Recursive containment not allowed for " + toString()); //$NON-NLS-1$
            NotificationChain msgs = null;
            if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
            if (newInterface != null) msgs = ((InternalEObject)newInterface).eInverseAdd(this,
                                                                                         WebServicePackage.INTERFACE__OPERATIONS,
                                                                                         Interface.class,
                                                                                         msgs);
            msgs = eBasicSetContainer((InternalEObject)newInterface, WebServicePackage.OPERATION__INTERFACE, msgs);
            if (msgs != null) msgs.dispatch();
        } else if (eNotificationRequired()) eNotify(new ENotificationImpl(this, Notification.SET,
                                                                          WebServicePackage.OPERATION__INTERFACE, newInterface,
                                                                          newInterface));
    }
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OperationUpdateCount getUpdateCount() {
        return updateCount;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setUpdateCount(OperationUpdateCount newUpdateCount) {
        OperationUpdateCount oldUpdateCount = updateCount;
        updateCount = newUpdateCount == null ? UPDATE_COUNT_EDEFAULT : newUpdateCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, WebServicePackage.OPERATION__UPDATE_COUNT, oldUpdateCount, updateCount));
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
                case WebServicePackage.OPERATION__INPUT:
                    if (input != null) msgs = ((InternalEObject)input).eInverseRemove(this,
                                                                                      EOPPOSITE_FEATURE_BASE
                                                                                      - WebServicePackage.OPERATION__INPUT,
                                                                                      null,
                                                                                      msgs);
                    return basicSetInput((Input)otherEnd, msgs);
                case WebServicePackage.OPERATION__OUTPUT:
                    if (output != null) msgs = ((InternalEObject)output).eInverseRemove(this,
                                                                                        EOPPOSITE_FEATURE_BASE
                                                                                        - WebServicePackage.OPERATION__OUTPUT,
                                                                                        null,
                                                                                        msgs);
                    return basicSetOutput((Output)otherEnd, msgs);
                case WebServicePackage.OPERATION__INTERFACE:
                    if (eContainer != null) msgs = eBasicRemoveFromContainer(msgs);
                    return eBasicSetContainer(otherEnd, WebServicePackage.OPERATION__INTERFACE, msgs);
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
                case WebServicePackage.OPERATION__INPUT:
                    return basicSetInput(null, msgs);
                case WebServicePackage.OPERATION__OUTPUT:
                    return basicSetOutput(null, msgs);
                case WebServicePackage.OPERATION__INTERFACE:
                    return eBasicSetContainer(null, WebServicePackage.OPERATION__INTERFACE, msgs);
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
                case WebServicePackage.OPERATION__INTERFACE:
                    return eContainer.eInverseRemove(this, WebServicePackage.INTERFACE__OPERATIONS, Interface.class, msgs);
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
            case WebServicePackage.OPERATION__NAME:
                return getName();
            case WebServicePackage.OPERATION__PATTERN:
                return getPattern();
            case WebServicePackage.OPERATION__SAFE:
                return isSafe() ? Boolean.TRUE : Boolean.FALSE;
            case WebServicePackage.OPERATION__INPUT:
                return getInput();
            case WebServicePackage.OPERATION__OUTPUT:
                return getOutput();
            case WebServicePackage.OPERATION__INTERFACE:
                return getInterface();
            case WebServicePackage.OPERATION__UPDATE_COUNT:
                return getUpdateCount();
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
            case WebServicePackage.OPERATION__NAME:
                setName((String)newValue);
                return;
            case WebServicePackage.OPERATION__PATTERN:
                setPattern((String)newValue);
                return;
            case WebServicePackage.OPERATION__SAFE:
                setSafe(((Boolean)newValue).booleanValue());
                return;
            case WebServicePackage.OPERATION__INPUT:
                setInput((Input)newValue);
                return;
            case WebServicePackage.OPERATION__OUTPUT:
                setOutput((Output)newValue);
                return;
            case WebServicePackage.OPERATION__INTERFACE:
                setInterface((Interface)newValue);
                return;
            case WebServicePackage.OPERATION__UPDATE_COUNT:
                setUpdateCount((OperationUpdateCount)newValue);
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
            case WebServicePackage.OPERATION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case WebServicePackage.OPERATION__PATTERN:
                setPattern(PATTERN_EDEFAULT);
                return;
            case WebServicePackage.OPERATION__SAFE:
                setSafe(SAFE_EDEFAULT);
                return;
            case WebServicePackage.OPERATION__INPUT:
                setInput((Input)null);
                return;
            case WebServicePackage.OPERATION__OUTPUT:
                setOutput((Output)null);
                return;
            case WebServicePackage.OPERATION__INTERFACE:
                setInterface((Interface)null);
                return;
            case WebServicePackage.OPERATION__UPDATE_COUNT:
                setUpdateCount(UPDATE_COUNT_EDEFAULT);
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
            case WebServicePackage.OPERATION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case WebServicePackage.OPERATION__PATTERN:
                return PATTERN_EDEFAULT == null ? pattern != null : !PATTERN_EDEFAULT.equals(pattern);
            case WebServicePackage.OPERATION__SAFE:
                return safe != SAFE_EDEFAULT;
            case WebServicePackage.OPERATION__INPUT:
                return input != null;
            case WebServicePackage.OPERATION__OUTPUT:
                return output != null;
            case WebServicePackage.OPERATION__INTERFACE:
                return getInterface() != null;
            case WebServicePackage.OPERATION__UPDATE_COUNT:
                return updateCount != UPDATE_COUNT_EDEFAULT;
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (pattern: "); //$NON-NLS-1$
        result.append(pattern);
        result.append(", safe: "); //$NON-NLS-1$
        result.append(safe);
        result.append(", updateCount: "); //$NON-NLS-1$
        result.append(updateCount);
        result.append(')');
        return result.toString();
    }

} // OperationImpl
