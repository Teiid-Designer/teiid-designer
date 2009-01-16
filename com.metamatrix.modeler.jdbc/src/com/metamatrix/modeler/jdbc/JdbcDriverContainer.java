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
 * A representation of the model object '<em><b>Driver Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.modeler.jdbc.JdbcDriverContainer#getJdbcDrivers <em>Jdbc Drivers</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriverContainer()
 * @model
 * @generated
 */
public interface JdbcDriverContainer extends EObject{
    /**
     * Returns the value of the '<em><b>Jdbc Drivers</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.modeler.jdbc.JdbcDriver}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.modeler.jdbc.JdbcDriver#getJdbcDriverContainer <em>Jdbc Driver Container</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jdbc Drivers</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jdbc Drivers</em>' containment reference list.
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#getJdbcDriverContainer_JdbcDrivers()
     * @see com.metamatrix.modeler.jdbc.JdbcDriver#getJdbcDriverContainer
     * @model type="com.metamatrix.modeler.jdbc.JdbcDriver" opposite="jdbcDriverContainer" containment="true"
     * @generated
     */
    EList getJdbcDrivers();

} // JdbcDriverContainer
