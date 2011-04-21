/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
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
