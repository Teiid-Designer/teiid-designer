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
 * A representation of the model object '<em><b>Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#isSystem <em>System</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#getCardinality <em>Cardinality</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#isSupportsUpdate <em>Supports Update</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#isMaterialized <em>Materialized</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#getAccessPatterns <em>Access Patterns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Table#getLogicalRelationships <em>Logical Relationships</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable()
 * @model abstract="true"
 * @generated
 */
public interface Table extends ColumnSet{
    /**
     * Returns the value of the '<em><b>System</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>System</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>System</em>' attribute.
     * @see #setSystem(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_System()
     * @model default="false"
     * @generated
     */
    boolean isSystem();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Table#isSystem <em>System</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>System</em>' attribute.
     * @see #isSystem()
     * @generated
     */
    void setSystem(boolean value);

    /**
     * Returns the value of the '<em><b>Cardinality</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Cardinality</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Cardinality</em>' attribute.
     * @see #setCardinality(int)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_Cardinality()
     * @model
     * @generated
     */
    int getCardinality();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Table#getCardinality <em>Cardinality</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Cardinality</em>' attribute.
     * @see #getCardinality()
     * @generated
     */
    void setCardinality(int value);

    /**
     * Returns the value of the '<em><b>Supports Update</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Supports Update</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Supports Update</em>' attribute.
     * @see #setSupportsUpdate(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_SupportsUpdate()
     * @model default="true"
     * @generated
     */
    boolean isSupportsUpdate();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Table#isSupportsUpdate <em>Supports Update</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Supports Update</em>' attribute.
     * @see #isSupportsUpdate()
     * @generated
     */
    void setSupportsUpdate(boolean value);

    /**
     * Returns the value of the '<em><b>Materialized</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Materialized</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Materialized</em>' attribute.
     * @see #setMaterialized(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_Materialized()
     * @model default="false"
     * @generated
     */
	boolean isMaterialized();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Table#isMaterialized <em>Materialized</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Materialized</em>' attribute.
     * @see #isMaterialized()
     * @generated
     */
	void setMaterialized(boolean value);

    /**
     * Returns the value of the '<em><b>Schema</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Schema#getTables <em>Tables</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schema</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schema</em>' container reference.
     * @see #setSchema(Schema)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_Schema()
     * @see com.metamatrix.metamodels.relational.Schema#getTables
     * @model opposite="tables"
     * @generated
     */
    Schema getSchema();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Table#getSchema <em>Schema</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Schema</em>' container reference.
     * @see #getSchema()
     * @generated
     */
    void setSchema(Schema value);

    /**
     * Returns the value of the '<em><b>Access Patterns</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.AccessPattern}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.AccessPattern#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Access Patterns</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Access Patterns</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_AccessPatterns()
     * @see com.metamatrix.metamodels.relational.AccessPattern#getTable
     * @model type="com.metamatrix.metamodels.relational.AccessPattern" opposite="table" containment="true"
     * @generated
     */
    EList getAccessPatterns();

    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Catalog#getTables <em>Tables</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_Catalog()
     * @see com.metamatrix.metamodels.relational.Catalog#getTables
     * @model opposite="tables"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Table#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Logical Relationships</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Logical Relationships</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Logical Relationships</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getTable_LogicalRelationships()
     * @see com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getTable
     * @model type="com.metamatrix.metamodels.relational.LogicalRelationshipEnd" opposite="table"
     * @generated
     */
    EList getLogicalRelationships();

} // Table
