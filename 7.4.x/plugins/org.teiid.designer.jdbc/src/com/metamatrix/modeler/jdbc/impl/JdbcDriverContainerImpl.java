/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.InternalEList;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcDriverContainer;
import com.metamatrix.modeler.jdbc.JdbcPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Driver Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.impl.JdbcDriverContainerImpl#getJdbcDrivers <em>Jdbc Drivers</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JdbcDriverContainerImpl extends EObjectImpl implements JdbcDriverContainer {
    /**
     * The cached value of the '{@link #getJdbcDrivers() <em>Jdbc Drivers</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getJdbcDrivers()
     * @generated
     * @ordered
     */
    protected EList jdbcDrivers = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected JdbcDriverContainerImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return JdbcPackage.eINSTANCE.getJdbcDriverContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getJdbcDrivers() {
        if (jdbcDrivers == null) {
            jdbcDrivers = new EObjectContainmentWithInverseEList(JdbcDriver.class, this, JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS, JdbcPackage.JDBC_DRIVER__JDBC_DRIVER_CONTAINER);
        }
        return jdbcDrivers;
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
                case JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS:
                    return ((InternalEList)getJdbcDrivers()).basicAdd(otherEnd, msgs);
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
                case JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS:
                    return ((InternalEList)getJdbcDrivers()).basicRemove(otherEnd, msgs);
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
            case JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS:
                return getJdbcDrivers();
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
            case JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS:
                getJdbcDrivers().clear();
                getJdbcDrivers().addAll((Collection)newValue);
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
            case JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS:
                getJdbcDrivers().clear();
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
            case JdbcPackage.JDBC_DRIVER_CONTAINER__JDBC_DRIVERS:
                return jdbcDrivers != null && !jdbcDrivers.isEmpty();
        }
        return eDynamicIsSet(eFeature);
    }

} //JdbcDriverContainerImpl
