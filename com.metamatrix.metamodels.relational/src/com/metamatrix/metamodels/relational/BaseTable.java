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

package com.metamatrix.metamodels.relational;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Base Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.BaseTable#getForeignKeys <em>Foreign Keys</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.BaseTable#getPrimaryKey <em>Primary Key</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.BaseTable#getUniqueConstraints <em>Unique Constraints</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getBaseTable()
 * @model
 * @generated
 */
public interface BaseTable extends Table{
    /**
     * Returns the value of the '<em><b>Foreign Keys</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.ForeignKey}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.ForeignKey#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Foreign Keys</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Foreign Keys</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getBaseTable_ForeignKeys()
     * @see com.metamatrix.metamodels.relational.ForeignKey#getTable
     * @model type="com.metamatrix.metamodels.relational.ForeignKey" opposite="table" containment="true"
     * @generated
     */
    EList getForeignKeys();

    /**
     * Returns the value of the '<em><b>Primary Key</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.PrimaryKey#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Primary Key</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Primary Key</em>' containment reference.
     * @see #setPrimaryKey(PrimaryKey)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getBaseTable_PrimaryKey()
     * @see com.metamatrix.metamodels.relational.PrimaryKey#getTable
     * @model opposite="table" containment="true"
     * @generated
     */
    PrimaryKey getPrimaryKey();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.BaseTable#getPrimaryKey <em>Primary Key</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Primary Key</em>' containment reference.
     * @see #getPrimaryKey()
     * @generated
     */
    void setPrimaryKey(PrimaryKey value);

    /**
     * Returns the value of the '<em><b>Unique Constraints</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.UniqueConstraint}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.UniqueConstraint#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unique Constraints</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Unique Constraints</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getBaseTable_UniqueConstraints()
     * @see com.metamatrix.metamodels.relational.UniqueConstraint#getTable
     * @model type="com.metamatrix.metamodels.relational.UniqueConstraint" opposite="table" containment="true"
     * @generated
     */
    EList getUniqueConstraints();

} // BaseTable
