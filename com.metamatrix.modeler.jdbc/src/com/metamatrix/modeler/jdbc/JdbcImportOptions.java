/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Import Options</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getImportSettings <em>Import Settings</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportOptions()
 * @model
 * @generated
 */
public interface JdbcImportOptions extends EObject{
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportOptions_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportOptions_Value()
     * @model
     * @generated
     */
    String getValue();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
    void setValue(String value);

    /**
     * Returns the value of the '<em><b>Import Settings</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getOptions <em>Options</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Import Settings</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Import Settings</em>' container reference.
     * @see #setImportSettings(JdbcImportSettings)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcImportOptions_ImportSettings()
     * @see com.metamatrix.modeler.jdbc.JdbcImportSettings#getOptions
     * @model opposite="options" required="true"
     * @generated
     */
    JdbcImportSettings getImportSettings();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcImportOptions#getImportSettings <em>Import Settings</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Import Settings</em>' container reference.
     * @see #getImportSettings()
     * @generated
     */
    void setImportSettings(JdbcImportSettings value);

} // JdbcImportOptions
