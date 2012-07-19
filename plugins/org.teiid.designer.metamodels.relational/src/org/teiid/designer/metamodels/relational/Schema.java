/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Schema</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.Schema#getTables <em>Tables</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Schema#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Schema#getProcedures <em>Procedures</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Schema#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Schema#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getSchema()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface Schema extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Tables</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Table}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Table#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tables</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tables</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getSchema_Tables()
     * @see org.teiid.designer.metamodels.relational.Table#getSchema
     * @model type="org.teiid.designer.metamodels.relational.Table" opposite="schema" containment="true"
     * @generated
     */
    EList getTables();

    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Catalog#getSchemas <em>Schemas</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getSchema_Catalog()
     * @see org.teiid.designer.metamodels.relational.Catalog#getSchemas
     * @model opposite="schemas"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.Schema#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Procedures</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Procedure}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Procedure#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Procedures</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Procedures</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getSchema_Procedures()
     * @see org.teiid.designer.metamodels.relational.Procedure#getSchema
     * @model type="org.teiid.designer.metamodels.relational.Procedure" opposite="schema" containment="true"
     * @generated
     */
    EList getProcedures();

    /**
     * Returns the value of the '<em><b>Indexes</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Index}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Index#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Indexes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Indexes</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getSchema_Indexes()
     * @see org.teiid.designer.metamodels.relational.Index#getSchema
     * @model type="org.teiid.designer.metamodels.relational.Index" opposite="schema" containment="true"
     * @generated
     */
    EList getIndexes();

    /**
     * Returns the value of the '<em><b>Logical Relationships</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.LogicalRelationship}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Logical Relationships</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Logical Relationships</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getSchema_LogicalRelationships()
     * @see org.teiid.designer.metamodels.relational.LogicalRelationship#getSchema
     * @model type="org.teiid.designer.metamodels.relational.LogicalRelationship" opposite="schema" containment="true"
     * @generated
     */
    EList getLogicalRelationships();

} // Schema
