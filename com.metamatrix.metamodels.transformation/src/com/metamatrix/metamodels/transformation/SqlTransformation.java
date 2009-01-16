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

package com.metamatrix.metamodels.transformation;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.mapping.MappingHelper;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sql Transformation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#getSelectSql <em>Select Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#getInsertSql <em>Insert Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#getUpdateSql <em>Update Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#getDeleteSql <em>Delete Sql</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isInsertAllowed <em>Insert Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateAllowed <em>Update Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteAllowed <em>Delete Allowed</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isOutputLocked <em>Output Locked</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isInsertSqlDefault <em>Insert Sql Default</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateSqlDefault <em>Update Sql Default</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteSqlDefault <em>Delete Sql Default</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.transformation.SqlTransformation#getAliases <em>Aliases</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation()
 * @model
 * @generated
 */
public interface SqlTransformation extends MappingHelper{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Select Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Select Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Select Sql</em>' attribute.
     * @see #setSelectSql(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_SelectSql()
     * @model
     * @generated
     */
    String getSelectSql();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getSelectSql <em>Select Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Select Sql</em>' attribute.
     * @see #getSelectSql()
     * @generated
     */
    void setSelectSql(String value);

    /**
     * Returns the value of the '<em><b>Insert Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Insert Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Insert Sql</em>' attribute.
     * @see #setInsertSql(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_InsertSql()
     * @model
     * @generated
     */
    String getInsertSql();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getInsertSql <em>Insert Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Insert Sql</em>' attribute.
     * @see #getInsertSql()
     * @generated
     */
    void setInsertSql(String value);

    /**
     * Returns the value of the '<em><b>Update Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Sql</em>' attribute.
     * @see #setUpdateSql(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_UpdateSql()
     * @model
     * @generated
     */
    String getUpdateSql();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getUpdateSql <em>Update Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Sql</em>' attribute.
     * @see #getUpdateSql()
     * @generated
     */
    void setUpdateSql(String value);

    /**
     * Returns the value of the '<em><b>Delete Sql</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Sql</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Delete Sql</em>' attribute.
     * @see #setDeleteSql(String)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_DeleteSql()
     * @model
     * @generated
     */
    String getDeleteSql();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#getDeleteSql <em>Delete Sql</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Delete Sql</em>' attribute.
     * @see #getDeleteSql()
     * @generated
     */
    void setDeleteSql(String value);

    /**
     * Returns the value of the '<em><b>Insert Allowed</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Insert Allowed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Insert Allowed</em>' attribute.
     * @see #setInsertAllowed(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_InsertAllowed()
     * @model default="true"
     * @generated
     */
    boolean isInsertAllowed();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isInsertAllowed <em>Insert Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Insert Allowed</em>' attribute.
     * @see #isInsertAllowed()
     * @generated
     */
    void setInsertAllowed(boolean value);

    /**
     * Returns the value of the '<em><b>Update Allowed</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Allowed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Allowed</em>' attribute.
     * @see #setUpdateAllowed(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_UpdateAllowed()
     * @model default="true"
     * @generated
     */
    boolean isUpdateAllowed();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateAllowed <em>Update Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Allowed</em>' attribute.
     * @see #isUpdateAllowed()
     * @generated
     */
    void setUpdateAllowed(boolean value);

    /**
     * Returns the value of the '<em><b>Delete Allowed</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Allowed</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Delete Allowed</em>' attribute.
     * @see #setDeleteAllowed(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_DeleteAllowed()
     * @model default="true"
     * @generated
     */
    boolean isDeleteAllowed();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteAllowed <em>Delete Allowed</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Delete Allowed</em>' attribute.
     * @see #isDeleteAllowed()
     * @generated
     */
    void setDeleteAllowed(boolean value);

    /**
     * Returns the value of the '<em><b>Output Locked</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output Locked</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Output Locked</em>' attribute.
     * @see #setOutputLocked(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_OutputLocked()
     * @model default="false"
     * @generated
     */
    boolean isOutputLocked();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isOutputLocked <em>Output Locked</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Output Locked</em>' attribute.
     * @see #isOutputLocked()
     * @generated
     */
    void setOutputLocked(boolean value);

    /**
     * Returns the value of the '<em><b>Insert Sql Default</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Insert Sql Default</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Insert Sql Default</em>' attribute.
     * @see #setInsertSqlDefault(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_InsertSqlDefault()
     * @model default="true"
     * @generated
     */
    boolean isInsertSqlDefault();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isInsertSqlDefault <em>Insert Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Insert Sql Default</em>' attribute.
     * @see #isInsertSqlDefault()
     * @generated
     */
    void setInsertSqlDefault(boolean value);

    /**
     * Returns the value of the '<em><b>Update Sql Default</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Sql Default</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Sql Default</em>' attribute.
     * @see #setUpdateSqlDefault(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_UpdateSqlDefault()
     * @model default="true"
     * @generated
     */
    boolean isUpdateSqlDefault();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isUpdateSqlDefault <em>Update Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Sql Default</em>' attribute.
     * @see #isUpdateSqlDefault()
     * @generated
     */
    void setUpdateSqlDefault(boolean value);

    /**
     * Returns the value of the '<em><b>Delete Sql Default</b></em>' attribute.
     * The default value is <code>"true"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Delete Sql Default</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Delete Sql Default</em>' attribute.
     * @see #setDeleteSqlDefault(boolean)
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_DeleteSqlDefault()
     * @model default="true"
     * @generated
     */
    boolean isDeleteSqlDefault();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.transformation.SqlTransformation#isDeleteSqlDefault <em>Delete Sql Default</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Delete Sql Default</em>' attribute.
     * @see #isDeleteSqlDefault()
     * @generated
     */
    void setDeleteSqlDefault(boolean value);

    /**
     * Returns the value of the '<em><b>Aliases</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.transformation.SqlAlias}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.transformation.SqlAlias#getSqlTransformation <em>Sql Transformation</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Aliases</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Aliases</em>' containment reference list.
     * @see com.metamatrix.metamodels.transformation.TransformationPackage#getSqlTransformation_Aliases()
     * @see com.metamatrix.metamodels.transformation.SqlAlias#getSqlTransformation
     * @model type="com.metamatrix.metamodels.transformation.SqlAlias" opposite="sqlTransformation" containment="true"
     * @generated
     */
    EList getAliases();

} // SqlTransformation
