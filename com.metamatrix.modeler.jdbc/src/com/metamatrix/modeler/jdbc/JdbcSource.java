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

package com.metamatrix.modeler.jdbc;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Source</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getJdbcDriver <em>Jdbc Driver</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getProperties <em>Properties</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getJdbcSourceContainer <em>Jdbc Source Container</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getImportSettings <em>Import Settings</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getDriverName <em>Driver Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getDriverClass <em>Driver Class</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getUsername <em>Username</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcSource#getUrl <em>Url</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource()
 * @model
 * @generated
 */
public interface JdbcSource extends EObject{
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
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Driver Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Driver Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Driver Name</em>' attribute.
     * @see #setDriverName(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_DriverName()
     * @model
     * @generated
     */
    String getDriverName();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getDriverName <em>Driver Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Driver Name</em>' attribute.
     * @see #getDriverName()
     * @generated
     */
    void setDriverName(String value);

    /**
     * Returns the value of the '<em><b>Driver Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Driver Class</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Driver Class</em>' attribute.
     * @see #setDriverClass(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_DriverClass()
     * @model
     * @generated
     */
    String getDriverClass();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getDriverClass <em>Driver Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Driver Class</em>' attribute.
     * @see #getDriverClass()
     * @generated
     */
    void setDriverClass(String value);

    /**
     * Returns the value of the '<em><b>Username</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Username</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Username</em>' attribute.
     * @see #setUsername(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_Username()
     * @model
     * @generated
     */
    String getUsername();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getUsername <em>Username</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Username</em>' attribute.
     * @see #getUsername()
     * @generated
     */
    void setUsername(String value);

    /**
     * Returns the value of the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Url</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Url</em>' attribute.
     * @see #setUrl(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_Url()
     * @model
     * @generated
     */
    String getUrl();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getUrl <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Url</em>' attribute.
     * @see #getUrl()
     * @generated
     */
    void setUrl(String value);

    /**
     * Returns the value of the '<em><b>Jdbc Driver</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jdbc Driver</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jdbc Driver</em>' reference.
     * @see #setJdbcDriver(JdbcDriver)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_JdbcDriver()
     * @model
     * @generated
     */
    JdbcDriver getJdbcDriver();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getJdbcDriver <em>Jdbc Driver</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Jdbc Driver</em>' reference.
     * @see #getJdbcDriver()
     * @generated
     */
    void setJdbcDriver(JdbcDriver value);

    /**
     * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.modeler.jdbc.JdbcSourceProperty}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcSourceProperty#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Properties</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Properties</em>' containment reference list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_Properties()
     * @see com.metamatrix.modeler.jdbc.JdbcSourceProperty#getSource
     * @model type="com.metamatrix.modeler.jdbc.JdbcSourceProperty" opposite="source" containment="true"
     * @generated
     */
    EList getProperties();

    /**
     * Returns the value of the '<em><b>Jdbc Source Container</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcSourceContainer#getJdbcSources <em>Jdbc Sources</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jdbc Source Container</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jdbc Source Container</em>' container reference.
     * @see #setJdbcSourceContainer(JdbcSourceContainer)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_JdbcSourceContainer()
     * @see com.metamatrix.modeler.jdbc.JdbcSourceContainer#getJdbcSources
     * @model opposite="jdbcSources"
     * @generated
     */
    JdbcSourceContainer getJdbcSourceContainer();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getJdbcSourceContainer <em>Jdbc Source Container</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Jdbc Source Container</em>' container reference.
     * @see #getJdbcSourceContainer()
     * @generated
     */
    void setJdbcSourceContainer(JdbcSourceContainer value);

    /**
     * Returns the value of the '<em><b>Import Settings</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcImportSettings#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Import Settings</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Import Settings</em>' containment reference.
     * @see #setImportSettings(JdbcImportSettings)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcSource_ImportSettings()
     * @see com.metamatrix.modeler.jdbc.JdbcImportSettings#getSource
     * @model opposite="source" containment="true"
     * @generated
     */
    JdbcImportSettings getImportSettings();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcSource#getImportSettings <em>Import Settings</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Import Settings</em>' containment reference.
     * @see #getImportSettings()
     * @generated
     */
    void setImportSettings(JdbcImportSettings value);

} // JdbcSource
