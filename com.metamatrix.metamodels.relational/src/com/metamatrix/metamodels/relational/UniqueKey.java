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
 * A representation of the model object '<em><b>Unique Key</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.UniqueKey#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.UniqueKey#getForeignKeys <em>Foreign Keys</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getUniqueKey()
 * @model abstract="true"
 * @generated
 */
public interface UniqueKey extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Columns</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Column}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Column#getUniqueKeys <em>Unique Keys</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Columns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Columns</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getUniqueKey_Columns()
     * @see com.metamatrix.metamodels.relational.Column#getUniqueKeys
     * @model type="com.metamatrix.metamodels.relational.Column" opposite="uniqueKeys" required="true"
     * @generated
     */
    EList getColumns();

    /**
     * Returns the value of the '<em><b>Foreign Keys</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.ForeignKey}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.ForeignKey#getUniqueKey <em>Unique Key</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Foreign Keys</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Foreign Keys</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getUniqueKey_ForeignKeys()
     * @see com.metamatrix.metamodels.relational.ForeignKey#getUniqueKey
     * @model type="com.metamatrix.metamodels.relational.ForeignKey" opposite="uniqueKey"
     * @generated
     */
    EList getForeignKeys();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    BaseTable getTable();

} // UniqueKey
