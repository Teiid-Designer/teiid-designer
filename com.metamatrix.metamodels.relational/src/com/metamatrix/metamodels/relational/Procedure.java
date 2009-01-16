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
 * A representation of the model object '<em><b>Procedure</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.relational.Procedure#isFunction <em>Function</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Procedure#getSchema <em>Schema</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Procedure#getParameters <em>Parameters</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.relational.Procedure#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure()
 * @model
 * @generated
 */
public interface Procedure extends RelationalEntity{
    /**
     * Returns the value of the '<em><b>Function</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Function</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Function</em>' attribute.
     * @see #setFunction(boolean)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure_Function()
     * @model
     * @generated
     */
    boolean isFunction();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Procedure#isFunction <em>Function</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Function</em>' attribute.
     * @see #isFunction()
     * @generated
     */
    void setFunction(boolean value);

    /**
     * Returns the value of the '<em><b>Schema</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Schema#getProcedures <em>Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Schema</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Schema</em>' container reference.
     * @see #setSchema(Schema)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure_Schema()
     * @see com.metamatrix.metamodels.relational.Schema#getProcedures
     * @model opposite="procedures"
     * @generated
     */
    Schema getSchema();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Procedure#getSchema <em>Schema</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Schema</em>' container reference.
     * @see #getSchema()
     * @generated
     */
    void setSchema(Schema value);

    /**
     * Returns the value of the '<em><b>Parameters</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.relational.ProcedureParameter}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parameters</em>' containment reference list.
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure_Parameters()
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getProcedure
     * @model type="com.metamatrix.metamodels.relational.ProcedureParameter" opposite="procedure" containment="true"
     * @generated
     */
    EList getParameters();

    /**
     * Returns the value of the '<em><b>Catalog</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.Catalog#getProcedures <em>Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Catalog</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Catalog</em>' container reference.
     * @see #setCatalog(Catalog)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure_Catalog()
     * @see com.metamatrix.metamodels.relational.Catalog#getProcedures
     * @model opposite="procedures"
     * @generated
     */
    Catalog getCatalog();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Catalog</em>' container reference.
     * @see #getCatalog()
     * @generated
     */
    void setCatalog(Catalog value);

    /**
     * Returns the value of the '<em><b>Result</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.relational.ProcedureResult#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Result</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Result</em>' containment reference.
     * @see #setResult(ProcedureResult)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure_Result()
     * @see com.metamatrix.metamodels.relational.ProcedureResult#getProcedure
     * @model opposite="procedure" containment="true"
     * @generated
     */
    ProcedureResult getResult();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Procedure#getResult <em>Result</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Result</em>' containment reference.
     * @see #getResult()
     * @generated
     */
    void setResult(ProcedureResult value);

    /**
     * Returns the value of the '<em><b>Update Count</b></em>' attribute.
     * The default value is <code>"AUTO"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.relational.ProcedureUpdateCount}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Count</em>' attribute.
     * @see com.metamatrix.metamodels.relational.ProcedureUpdateCount
     * @see #setUpdateCount(ProcedureUpdateCount)
     * @see com.metamatrix.metamodels.relational.RelationalPackage#getProcedure_UpdateCount()
     * @model default="AUTO"
     * @generated
     */
    ProcedureUpdateCount getUpdateCount();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.relational.Procedure#getUpdateCount <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Count</em>' attribute.
     * @see com.metamatrix.metamodels.relational.ProcedureUpdateCount
     * @see #getUpdateCount()
     * @generated
     */
    void setUpdateCount(ProcedureUpdateCount value);

} // Procedure
