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

package com.metamatrix.modeler.jdbc.util;

import com.metamatrix.modeler.jdbc.*;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.jdbc.JdbcPackage
 * @generated
 */
public class JdbcSwitch {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static JdbcPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcSwitch() {
        if (modelPackage == null) {
            modelPackage = JdbcPackage.eINSTANCE;
        }
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    public Object doSwitch(EObject theEObject) {
        return doSwitch(theEObject.eClass(), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(EClass theEClass, EObject theEObject) {
        if (theEClass.eContainer() == modelPackage) {
            return doSwitch(theEClass.getClassifierID(), theEObject);
        }
        List eSuperTypes = theEClass.getESuperTypes();
        return
            eSuperTypes.isEmpty() ?
                defaultCase(theEObject) :
                doSwitch((EClass)eSuperTypes.get(0), theEObject);
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    protected Object doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case JdbcPackage.JDBC_SOURCE_PROPERTY: {
                JdbcSourceProperty jdbcSourceProperty = (JdbcSourceProperty)theEObject;
                Object result = caseJdbcSourceProperty(jdbcSourceProperty);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JdbcPackage.JDBC_DRIVER: {
                JdbcDriver jdbcDriver = (JdbcDriver)theEObject;
                Object result = caseJdbcDriver(jdbcDriver);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JdbcPackage.JDBC_SOURCE: {
                JdbcSource jdbcSource = (JdbcSource)theEObject;
                Object result = caseJdbcSource(jdbcSource);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JdbcPackage.JDBC_DRIVER_CONTAINER: {
                JdbcDriverContainer jdbcDriverContainer = (JdbcDriverContainer)theEObject;
                Object result = caseJdbcDriverContainer(jdbcDriverContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JdbcPackage.JDBC_SOURCE_CONTAINER: {
                JdbcSourceContainer jdbcSourceContainer = (JdbcSourceContainer)theEObject;
                Object result = caseJdbcSourceContainer(jdbcSourceContainer);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JdbcPackage.JDBC_IMPORT_SETTINGS: {
                JdbcImportSettings jdbcImportSettings = (JdbcImportSettings)theEObject;
                Object result = caseJdbcImportSettings(jdbcImportSettings);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case JdbcPackage.JDBC_IMPORT_OPTIONS: {
                JdbcImportOptions jdbcImportOptions = (JdbcImportOptions)theEObject;
                Object result = caseJdbcImportOptions(jdbcImportOptions);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Source Property</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Source Property</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcSourceProperty(JdbcSourceProperty object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Driver</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Driver</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcDriver(JdbcDriver object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Source</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Source</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcSource(JdbcSource object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Driver Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Driver Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcDriverContainer(JdbcDriverContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Source Container</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Source Container</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcSourceContainer(JdbcSourceContainer object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Import Settings</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Import Settings</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcImportSettings(JdbcImportSettings object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>Import Options</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>Import Options</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public Object caseJdbcImportOptions(JdbcImportOptions object) {
        return null;
    }

    /**
     * Returns the result of interpretting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpretting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    public Object defaultCase(EObject object) {
        return null;
    }

} //JdbcSwitch
