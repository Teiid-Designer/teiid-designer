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
 * A representation of the model object '<em><b>Column Set</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.relational.ColumnSet#getColumns <em>Columns</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.relational.RelationalPackage#getColumnSet()
 * @model abstract="true"
 * @generated
 *
 * @since 8.0
 */
public interface ColumnSet extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Columns</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.relational.Column}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.relational.Column#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Columns</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Columns</em>' containment reference list.
     * @see org.teiid.designer.metamodels.relational.RelationalPackage#getColumnSet_Columns()
     * @see org.teiid.designer.metamodels.relational.Column#getOwner
     * @model type="org.teiid.designer.metamodels.relational.Column" opposite="owner" containment="true" required="true"
     * @generated
     */
    EList getColumns();

} // ColumnSet
