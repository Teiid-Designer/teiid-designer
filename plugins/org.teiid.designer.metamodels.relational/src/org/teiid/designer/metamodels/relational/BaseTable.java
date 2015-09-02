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
 * A representation of the model object '<em><b>Base Table</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.BaseTable#getForeignKeys <em>Foreign Keys</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.BaseTable#getPrimaryKey <em>Primary Key</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.BaseTable#getUniqueConstraints <em>Unique Constraints</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getBaseTable()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface BaseTable extends Table{
    /**
     * Returns the value of the '<em><b>Foreign Keys</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.ForeignKey}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.ForeignKey#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Foreign Keys</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Foreign Keys</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getBaseTable_ForeignKeys()
     * @see org.teiid.designer.metamodels.relational.ForeignKey#getTable
     * @model type="org.teiid.designer.metamodels.relational.ForeignKey" opposite="table" containment="true"
     * @generated
     */
    EList getForeignKeys();

    /**
     * Returns the value of the '<em><b>Primary Key</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.PrimaryKey#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Primary Key</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Primary Key</em>' containment reference.
     * @see #setPrimaryKey(PrimaryKey)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getBaseTable_PrimaryKey()
     * @see org.teiid.designer.metamodels.relational.PrimaryKey#getTable
     * @model opposite="table" containment="true"
     * @generated
     */
    PrimaryKey getPrimaryKey();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.BaseTable#getPrimaryKey <em>Primary Key</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Primary Key</em>' containment reference.
     * @see #getPrimaryKey()
     * @generated
     */
    void setPrimaryKey(PrimaryKey value);

    /**
     * Returns the value of the '<em><b>Unique Constraints</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.UniqueConstraint}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.UniqueConstraint#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unique Constraints</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Unique Constraints</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getBaseTable_UniqueConstraints()
     * @see org.teiid.designer.metamodels.relational.UniqueConstraint#getTable
     * @model type="org.teiid.designer.metamodels.relational.UniqueConstraint" opposite="table" containment="true"
     * @generated
     */
    EList<UniqueConstraint> getUniqueConstraints();

} // BaseTable
