/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.metamatrix.modeler.jdbc.JdbcPackage
 * @generated
 */
public interface JdbcFactory extends EFactory{
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated NOT
     */
    JdbcFactory eINSTANCE = new com.metamatrix.modeler.jdbc.impl.JdbcFactoryImpl();

    /**
     * Returns a new object of class '<em>Source Property</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Source Property</em>'.
     * @generated
     */
    JdbcSourceProperty createJdbcSourceProperty();

    /**
     * Returns a new object of class '<em>Driver</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Driver</em>'.
     * @generated
     */
    JdbcDriver createJdbcDriver();

    /**
     * Returns a new object of class '<em>Source</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Source</em>'.
     * @generated
     */
    JdbcSource createJdbcSource();

    /**
     * Returns a new object of class '<em>Driver Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Driver Container</em>'.
     * @generated
     */
    JdbcDriverContainer createJdbcDriverContainer();

    /**
     * Returns a new object of class '<em>Source Container</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Source Container</em>'.
     * @generated
     */
    JdbcSourceContainer createJdbcSourceContainer();

    /**
     * Returns a new object of class '<em>Import Settings</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Import Settings</em>'.
     * @generated
     */
    JdbcImportSettings createJdbcImportSettings();

    /**
     * Returns a new object of class '<em>Import Options</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Import Options</em>'.
     * @generated
     */
    JdbcImportOptions createJdbcImportOptions();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    JdbcPackage getJdbcPackage();

} //JdbcFactory
