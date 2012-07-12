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
 * A representation of the model object '<em><b>Access Pattern</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.AccessPattern#getColumns <em>Columns</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.relational.AccessPattern#getTable <em>Table</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getAccessPattern()
 * @model
 * @generated
 */
public interface AccessPattern extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Columns</b></em>' reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Column}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Column#getAccessPatterns <em>Access Patterns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Columns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Columns</em>' reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getAccessPattern_Columns()
     * @see org.teiid.designer.metamodels.relational.Column#getAccessPatterns
     * @model type="org.teiid.designer.metamodels.relational.Column" opposite="accessPatterns" required="true"
     * @generated
     */
    EList getColumns();

    /**
     * Returns the value of the '<em><b>Table</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Table#getAccessPatterns <em>Access Patterns</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Table</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Table</em>' container reference.
     * @see #setTable(Table)
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getAccessPattern_Table()
     * @see org.teiid.designer.metamodels.relational.Table#getAccessPatterns
     * @model opposite="accessPatterns"
     * @generated
     */
    Table getTable();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.relational.AccessPattern#getTable <em>Table</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Table</em>' container reference.
     * @see #getTable()
     * @generated
     */
    void setTable(Table value);

} // AccessPattern
