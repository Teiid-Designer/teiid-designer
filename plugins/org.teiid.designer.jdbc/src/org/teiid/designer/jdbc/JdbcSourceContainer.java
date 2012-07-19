/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Source Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.jdbc.JdbcSourceContainer#getJdbcSources <em>Jdbc Sources</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.jdbc.JdbcPackage#getJdbcSourceContainer()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface JdbcSourceContainer extends EObject{
    /**
     * Returns the value of the '<em><b>Jdbc Sources</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.jdbc.JdbcSource}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.jdbc.JdbcSource#getJdbcSourceContainer <em>Jdbc Source Container</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jdbc Sources</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jdbc Sources</em>' containment reference list.
     * @see org.teiid.designer.jdbc.JdbcPackage#getJdbcSourceContainer_JdbcSources()
     * @see org.teiid.designer.jdbc.JdbcSource#getJdbcSourceContainer
     * @model type="org.teiid.designer.jdbc.JdbcSource" opposite="jdbcSourceContainer" containment="true"
     * @generated
     */
    EList getJdbcSources();

} // JdbcSourceContainer
