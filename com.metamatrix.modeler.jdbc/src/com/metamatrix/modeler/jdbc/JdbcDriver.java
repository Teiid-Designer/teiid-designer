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
 * A representation of the model object '<em><b>Driver</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriver#getJdbcDriverContainer <em>Jdbc Driver Container</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriver#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriver#getUrlSyntax <em>Url Syntax</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriver#getJarFileUris <em>Jar File Uris</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriver#getAvailableDriverClassNames <em>Available Driver Class Names</em>}</li>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriver#getPreferredDriverClassName <em>Preferred Driver Class Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver()
 * @model
 * @generated
 */
public interface JdbcDriver extends EObject{
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
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcDriver#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Url Syntax</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Url Syntax</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Url Syntax</em>' attribute.
     * @see #setUrlSyntax(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver_UrlSyntax()
     * @model
     * @generated
     */
    String getUrlSyntax();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcDriver#getUrlSyntax <em>Url Syntax</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Url Syntax</em>' attribute.
     * @see #getUrlSyntax()
     * @generated
     */
    void setUrlSyntax(String value);

    /**
     * Returns the value of the '<em><b>Jar File Uris</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jar File Uris</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jar File Uris</em>' attribute list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver_JarFileUris()
     * @model type="java.lang.String"
     * @generated
     */
    EList getJarFileUris();

    /**
     * Returns the value of the '<em><b>Available Driver Class Names</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Available Driver Class Names</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Available Driver Class Names</em>' attribute list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver_AvailableDriverClassNames()
     * @model type="java.lang.String"
     * @generated
     */
    EList getAvailableDriverClassNames();

    /**
     * Returns the value of the '<em><b>Preferred Driver Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Preferred Driver Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Preferred Driver Class Name</em>' attribute.
     * @see #setPreferredDriverClassName(String)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver_PreferredDriverClassName()
     * @model
     * @generated
     */
    String getPreferredDriverClassName();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcDriver#getPreferredDriverClassName <em>Preferred Driver Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Preferred Driver Class Name</em>' attribute.
     * @see #getPreferredDriverClassName()
     * @generated
     */
    void setPreferredDriverClassName(String value);

    /**
     * Returns the value of the '<em><b>Jdbc Driver Container</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcDriverContainer#getJdbcDrivers <em>Jdbc Drivers</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jdbc Driver Container</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jdbc Driver Container</em>' container reference.
     * @see #setJdbcDriverContainer(JdbcDriverContainer)
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriver_JdbcDriverContainer()
     * @see com.metamatrix.modeler.jdbc.JdbcDriverContainer#getJdbcDrivers
     * @model opposite="jdbcDrivers"
     * @generated
     */
    JdbcDriverContainer getJdbcDriverContainer();

    /**
     * Sets the value of the '{@link com.metamatrix.modeler.jdbc.JdbcDriver#getJdbcDriverContainer <em>Jdbc Driver Container</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Jdbc Driver Container</em>' container reference.
     * @see #getJdbcDriverContainer()
     * @generated
     */
    void setJdbcDriverContainer(JdbcDriverContainer value);

} // JdbcDriver
