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
 * A representation of the model object '<em><b>Column Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.ColumnSet#getColumns <em>Columns</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumnSet()
 * @model abstract="true"
 * @generated
 */
public interface ColumnSet extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Columns</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Column}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Column#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Columns</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Columns</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getColumnSet_Columns()
     * @see com.metamatrix.metamodels.relational.Column#getOwner
     * @model type="com.metamatrix.metamodels.relational.Column" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getColumns();

} // ColumnSet
