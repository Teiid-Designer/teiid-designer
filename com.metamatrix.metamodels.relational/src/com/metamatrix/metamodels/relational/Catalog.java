/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Catalog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.Catalog#getSchemas <em>Schemas</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Catalog#getProcedures <em>Procedures</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Catalog#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Catalog#getTables <em>Tables</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Catalog#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getCatalog()
 * @model
 * @generated
 */
public interface Catalog extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Schemas</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Schema}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Schema#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schemas</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schemas</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getCatalog_Schemas()
     * @see com.metamatrix.metamodels.relational.Schema#getCatalog
     * @model type="com.metamatrix.metamodels.relational.Schema" opposite="catalog" containment="true"
     * @generated
     */
    EList getSchemas();

    /**
     * Returns the value of the '<em><b>Procedures</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Procedure}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Procedures</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Procedures</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getCatalog_Procedures()
     * @see com.metamatrix.metamodels.relational.Procedure#getCatalog
     * @model type="com.metamatrix.metamodels.relational.Procedure" opposite="catalog" containment="true"
     * @generated
     */
    EList getProcedures();

    /**
     * Returns the value of the '<em><b>Indexes</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Index}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Index#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Indexes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Indexes</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getCatalog_Indexes()
     * @see com.metamatrix.metamodels.relational.Index#getCatalog
     * @model type="com.metamatrix.metamodels.relational.Index" opposite="catalog" containment="true"
     * @generated
     */
    EList getIndexes();

    /**
     * Returns the value of the '<em><b>Tables</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Table}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Table#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tables</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tables</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getCatalog_Tables()
     * @see com.metamatrix.metamodels.relational.Table#getCatalog
     * @model type="com.metamatrix.metamodels.relational.Table" opposite="catalog" containment="true"
     * @generated
     */
    EList getTables();

    /**
     * Returns the value of the '<em><b>Logical Relationships</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.LogicalRelationship}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.LogicalRelationship#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Logical Relationships</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Logical Relationships</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getCatalog_LogicalRelationships()
     * @see com.metamatrix.metamodels.relational.LogicalRelationship#getCatalog
     * @model type="com.metamatrix.metamodels.relational.LogicalRelationship" opposite="catalog" containment="true"
     * @generated
     */
    EList getLogicalRelationships();

} // Catalog
