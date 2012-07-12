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
 * A representation of the model object '<em><b>Catalog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.Catalog#getSchemas <em>Schemas</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Catalog#getProcedures <em>Procedures</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Catalog#getIndexes <em>Indexes</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Catalog#getTables <em>Tables</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.Catalog#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getCatalog()
 * @model
 * @generated
 */
public interface Catalog extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Schemas</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Schema}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Schema#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schemas</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schemas</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getCatalog_Schemas()
     * @see org.teiid.designer.metamodels.relational.Schema#getCatalog
     * @model type="org.teiid.designer.metamodels.relational.Schema" opposite="catalog" containment="true"
     * @generated
     */
    EList getSchemas();

    /**
     * Returns the value of the '<em><b>Procedures</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Procedure}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Procedures</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Procedures</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getCatalog_Procedures()
     * @see org.teiid.designer.metamodels.relational.Procedure#getCatalog
     * @model type="org.teiid.designer.metamodels.relational.Procedure" opposite="catalog" containment="true"
     * @generated
     */
    EList getProcedures();

    /**
     * Returns the value of the '<em><b>Indexes</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Index}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Index#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Indexes</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Indexes</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getCatalog_Indexes()
     * @see org.teiid.designer.metamodels.relational.Index#getCatalog
     * @model type="org.teiid.designer.metamodels.relational.Index" opposite="catalog" containment="true"
     * @generated
     */
    EList getIndexes();

    /**
     * Returns the value of the '<em><b>Tables</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Table}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Table#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Tables</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Tables</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getCatalog_Tables()
     * @see org.teiid.designer.metamodels.relational.Table#getCatalog
     * @model type="org.teiid.designer.metamodels.relational.Table" opposite="catalog" containment="true"
     * @generated
     */
    EList getTables();

    /**
     * Returns the value of the '<em><b>Logical Relationships</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.LogicalRelationship}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.LogicalRelationship#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Logical Relationships</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Logical Relationships</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getCatalog_LogicalRelationships()
     * @see org.teiid.designer.metamodels.relational.LogicalRelationship#getCatalog
     * @model type="org.teiid.designer.metamodels.relational.LogicalRelationship" opposite="catalog" containment="true"
     * @generated
     */
    EList getLogicalRelationships();

} // Catalog
