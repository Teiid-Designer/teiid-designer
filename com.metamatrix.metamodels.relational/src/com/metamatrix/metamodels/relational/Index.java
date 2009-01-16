/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.relational;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Index</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#getFilterCondition <em>Filter Condition</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#isNullable <em>Nullable</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#isAutoUpdate <em>Auto Update</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#isUnique <em>Unique</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#getColumns <em>Columns</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Index#getCatalog <em>Catalog</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex()
 * @model
 * @generated
 */
public interface Index extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Filter Condition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Filter Condition</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Filter Condition</em>' attribute.
     * @see #setFilterCondition(String)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_FilterCondition()
     * @model
     * @generated
     */
    String getFilterCondition();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Index#getFilterCondition <em>Filter Condition</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Filter Condition</em>' attribute.
     * @see #getFilterCondition()
     * @generated
     */
    void setFilterCondition(String value);

    /**
     * Returns the value of the '<em><b>Nullable</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Nullable</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Nullable</em>' attribute.
     * @see #setNullable(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_Nullable()
     * @model default="true"
     * @generated
     */
    boolean isNullable();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Index#isNullable <em>Nullable</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Nullable</em>' attribute.
     * @see #isNullable()
     * @generated
     */
    void setNullable(boolean value);

    /**
     * Returns the value of the '<em><b>Auto Update</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Auto Update</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Auto Update</em>' attribute.
     * @see #setAutoUpdate(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_AutoUpdate()
     * @model
     * @generated
     */
    boolean isAutoUpdate();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Index#isAutoUpdate <em>Auto Update</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Auto Update</em>' attribute.
     * @see #isAutoUpdate()
     * @generated
     */
    void setAutoUpdate(boolean value);

    /**
     * Returns the value of the '<em><b>Unique</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Unique</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Unique</em>' attribute.
     * @see #setUnique(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_Unique()
     * @model
     * @generated
     */
    boolean isUnique();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Index#isUnique <em>Unique</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Unique</em>' attribute.
     * @see #isUnique()
     * @generated
     */
    void setUnique(boolean value);

    /**
     * Returns the value of the '<em><b>Schema</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Schema#getIndexes <em>Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schema</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schema</em>' container reference.
     * @see #setSchema(Schema)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_Schema()
     * @see com.metamatrix.metamodels.relational.Schema#getIndexes
     * @model opposite="indexes"
     * @generated
     */
    Schema getSchema();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Index#getSchema <em>Schema</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Schema</em>' container reference.
     * @see #getSchema()
     * @generated
     */
    void setSchema(Schema value);

    /**
     * Returns the value of the '<em><b>Columns</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.Column}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Column#getIndexes <em>Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Columns</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Columns</em>' reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_Columns()
     * @see com.metamatrix.metamodels.relational.Column#getIndexes
     * @model type="com.metamatrix.metamodels.relational.Column" opposite="indexes" required="true"
     * @generated
     */
    EList getColumns();

    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Catalog#getIndexes <em>Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getIndex_Catalog()
     * @see com.metamatrix.metamodels.relational.Catalog#getIndexes
     * @model opposite="indexes"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Index#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

} // Index
