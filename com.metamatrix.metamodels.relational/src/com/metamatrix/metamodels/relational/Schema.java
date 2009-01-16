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
 * A representation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.Schema#getTables <em>Tables</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Schema#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Schema#getProcedures <em>Procedures</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Schema#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Schema#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getSchema()
 * @model
 * @generated
 */
public interface Schema extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Tables</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Table}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Table#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tables</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tables</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getSchema_Tables()
     * @see com.metamatrix.metamodels.relational.Table#getSchema
     * @model type="com.metamatrix.metamodels.relational.Table" opposite="schema" containment="true"
     * @generated
     */
    EList getTables();

    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Catalog#getSchemas <em>Schemas</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getSchema_Catalog()
     * @see com.metamatrix.metamodels.relational.Catalog#getSchemas
     * @model opposite="schemas"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Schema#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Procedures</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Procedure}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Procedure#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Procedures</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Procedures</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getSchema_Procedures()
     * @see com.metamatrix.metamodels.relational.Procedure#getSchema
     * @model type="com.metamatrix.metamodels.relational.Procedure" opposite="schema" containment="true"
     * @generated
     */
    EList getProcedures();

    /**
     * Returns the value of the '<em><b>Indexes</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Index}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Index#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Indexes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Indexes</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getSchema_Indexes()
     * @see com.metamatrix.metamodels.relational.Index#getSchema
     * @model type="com.metamatrix.metamodels.relational.Index" opposite="schema" containment="true"
     * @generated
     */
    EList getIndexes();

    /**
     * Returns the value of the '<em><b>Logical Relationships</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.LogicalRelationship}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.LogicalRelationship#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Logical Relationships</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Logical Relationships</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getSchema_LogicalRelationships()
     * @see com.metamatrix.metamodels.relational.LogicalRelationship#getSchema
     * @model type="com.metamatrix.metamodels.relational.LogicalRelationship" opposite="schema" containment="true"
     * @generated
     */
    EList getLogicalRelationships();

} // Schema
