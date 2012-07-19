/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.relational;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Primary Key</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.PrimaryKey#getTable <em>Table</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getPrimaryKey()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface PrimaryKey extends UniqueKey{
    /**
     * Returns the value of the '<em><b>Table</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.BaseTable#getPrimaryKey <em>Primary Key</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Table</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Table</em>' container reference.
     * @see #setTable(BaseTable)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getPrimaryKey_Table()
     * @see org.teiid.designer.metamodels.relational.BaseTable#getPrimaryKey
     * @model opposite="primaryKey"
     * @generated
     */
    @Override
	BaseTable getTable();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.PrimaryKey#getTable <em>Table</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Table</em>' container reference.
     * @see #getTable()
     * @generated
     */
    void setTable(BaseTable value);

} // PrimaryKey
