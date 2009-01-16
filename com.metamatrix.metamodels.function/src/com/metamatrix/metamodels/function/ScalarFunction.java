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

package com.metamatrix.metamodels.function;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Scalar Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.function.ScalarFunction#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.ScalarFunction#getReturnParameter <em>Return Parameter</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.ScalarFunction#getInvocationClass <em>Invocation Class</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.ScalarFunction#getInvocationMethod <em>Invocation Method</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.ScalarFunction#isDeterministic <em>Deterministic</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.function.FunctionPackage#getScalarFunction()
 * @model
 * @generated
 */
public interface ScalarFunction extends Function{
    /**
     * Returns the value of the '<em><b>Invocation Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Invocation Class</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Invocation Class</em>' attribute.
     * @see #setInvocationClass(String)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getScalarFunction_InvocationClass()
     * @model
     * @generated
     */
    String getInvocationClass();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.ScalarFunction#getInvocationClass <em>Invocation Class</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Invocation Class</em>' attribute.
     * @see #getInvocationClass()
     * @generated
     */
    void setInvocationClass(String value);

    /**
     * Returns the value of the '<em><b>Invocation Method</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Invocation Method</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Invocation Method</em>' attribute.
     * @see #setInvocationMethod(String)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getScalarFunction_InvocationMethod()
     * @model
     * @generated
     */
    String getInvocationMethod();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.ScalarFunction#getInvocationMethod <em>Invocation Method</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Invocation Method</em>' attribute.
     * @see #getInvocationMethod()
     * @generated
     */
    void setInvocationMethod(String value);

    /**
     * Returns the value of the '<em><b>Deterministic</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Deterministic</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Deterministic</em>' attribute.
     * @see #setDeterministic(boolean)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getScalarFunction_Deterministic()
     * @model default="false"
     * @generated
     */
    boolean isDeterministic();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.ScalarFunction#isDeterministic <em>Deterministic</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Deterministic</em>' attribute.
     * @see #isDeterministic()
     * @generated
     */
    void setDeterministic(boolean value);

    /**
     * Returns the value of the '<em><b>Input Parameters</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.function.FunctionParameter}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.function.FunctionParameter#getFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Parameters</em>' containment reference list.
     * @see com.metamatrix.metamodels.function.FunctionPackage#getScalarFunction_InputParameters()
     * @see com.metamatrix.metamodels.function.FunctionParameter#getFunction
     * @model type="com.metamatrix.metamodels.function.FunctionParameter" opposite="function" containment="true"
     * @generated
     */
    EList getInputParameters();

    /**
     * Returns the value of the '<em><b>Return Parameter</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.function.ReturnParameter#getFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Parameter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Parameter</em>' containment reference.
     * @see #setReturnParameter(ReturnParameter)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getScalarFunction_ReturnParameter()
     * @see com.metamatrix.metamodels.function.ReturnParameter#getFunction
     * @model opposite="function" containment="true" required="true"
     * @generated
     */
    ReturnParameter getReturnParameter();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.ScalarFunction#getReturnParameter <em>Return Parameter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Parameter</em>' containment reference.
     * @see #getReturnParameter()
     * @generated
     */
    void setReturnParameter(ReturnParameter value);

} // ScalarFunction
