/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.impl;

import com.metamatrix.metamodels.xmlservice.OperationUpdateCount;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlOutput;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;

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

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Xml Operation</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlOperationImpl#getInputs <em>Inputs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.impl.XmlOperationImpl#getOutput <em>Output</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class XmlOperationImpl extends XmlServiceComponentImpl implements XmlOperation {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The cached value of the '{@link #getInputs() <em>Inputs</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInputs()
     * @generated
     * @ordered
     */
    protected EList inputs = null;

    /**
     * The cached value of the '{@link #getOutput() <em>Output</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOutput()
     * @generated
     * @ordered
     */
    protected XmlOutput output = null;

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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected XmlOperationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return XmlServicePackage.eINSTANCE.getXmlOperation();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getInputs() {
        if (inputs == null) {
            inputs = new EObjectContainmentWithInverseEList(XmlInput.class, this, XmlServicePackage.XML_OPERATION__INPUTS, XmlServicePackage.XML_INPUT__OPERATION);
        }
        return inputs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlOutput getOutput() {
        return output;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetOutput(XmlOutput newOutput, NotificationChain msgs) {
        XmlOutput oldOutput = output;
        output = newOutput;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_OPERATION__OUTPUT, oldOutput, newOutput);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOutput(XmlOutput newOutput) {
        if (newOutput != output) {
            NotificationChain msgs = null;
            if (output != null)
                msgs = ((InternalEObject)output).eInverseRemove(this, XmlServicePackage.XML_OUTPUT__OPERATION, XmlOutput.class, msgs);
            if (newOutput != null)
                msgs = ((InternalEObject)newOutput).eInverseAdd(this, XmlServicePackage.XML_OUTPUT__OPERATION, XmlOutput.class, msgs);
            msgs = basicSetOutput(newOutput, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_OPERATION__OUTPUT, newOutput, newOutput));
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
            eNotify(new ENotificationImpl(this, Notification.SET, XmlServicePackage.XML_OPERATION__UPDATE_COUNT, oldUpdateCount, updateCount));
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
                case XmlServicePackage.XML_OPERATION__INPUTS:
                    return ((InternalEList)getInputs()).basicAdd(otherEnd, msgs);
                case XmlServicePackage.XML_OPERATION__OUTPUT:
                    if (output != null)
                        msgs = ((InternalEObject)output).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - XmlServicePackage.XML_OPERATION__OUTPUT, null, msgs);
                    return basicSetOutput((XmlOutput)otherEnd, msgs);
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
                case XmlServicePackage.XML_OPERATION__INPUTS:
                    return ((InternalEList)getInputs()).basicRemove(otherEnd, msgs);
                case XmlServicePackage.XML_OPERATION__OUTPUT:
                    return basicSetOutput(null, msgs);
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
            case XmlServicePackage.XML_OPERATION__NAME:
                return getName();
            case XmlServicePackage.XML_OPERATION__NAME_IN_SOURCE:
                return getNameInSource();
            case XmlServicePackage.XML_OPERATION__INPUTS:
                return getInputs();
            case XmlServicePackage.XML_OPERATION__OUTPUT:
                return getOutput();
            case XmlServicePackage.XML_OPERATION__UPDATE_COUNT:
                return getUpdateCount();
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
            case XmlServicePackage.XML_OPERATION__NAME:
                setName((String)newValue);
                return;
            case XmlServicePackage.XML_OPERATION__NAME_IN_SOURCE:
                setNameInSource((String)newValue);
                return;
            case XmlServicePackage.XML_OPERATION__INPUTS:
                getInputs().clear();
                getInputs().addAll((Collection)newValue);
                return;
            case XmlServicePackage.XML_OPERATION__OUTPUT:
                setOutput((XmlOutput)newValue);
                return;
            case XmlServicePackage.XML_OPERATION__UPDATE_COUNT:
                setUpdateCount((OperationUpdateCount)newValue);
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
            case XmlServicePackage.XML_OPERATION__NAME:
                setName(NAME_EDEFAULT);
                return;
            case XmlServicePackage.XML_OPERATION__NAME_IN_SOURCE:
                setNameInSource(NAME_IN_SOURCE_EDEFAULT);
                return;
            case XmlServicePackage.XML_OPERATION__INPUTS:
                getInputs().clear();
                return;
            case XmlServicePackage.XML_OPERATION__OUTPUT:
                setOutput((XmlOutput)null);
                return;
            case XmlServicePackage.XML_OPERATION__UPDATE_COUNT:
                setUpdateCount(UPDATE_COUNT_EDEFAULT);
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
            case XmlServicePackage.XML_OPERATION__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case XmlServicePackage.XML_OPERATION__NAME_IN_SOURCE:
                return NAME_IN_SOURCE_EDEFAULT == null ? nameInSource != null : !NAME_IN_SOURCE_EDEFAULT.equals(nameInSource);
            case XmlServicePackage.XML_OPERATION__INPUTS:
                return inputs != null && !inputs.isEmpty();
            case XmlServicePackage.XML_OPERATION__OUTPUT:
                return output != null;
            case XmlServicePackage.XML_OPERATION__UPDATE_COUNT:
                return updateCount != UPDATE_COUNT_EDEFAULT;
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
        result.append(" (updateCount: "); //$NON-NLS-1$
        result.append(updateCount);
        result.append(')');
        return result.toString();
    }

} //XmlOperationImpl
