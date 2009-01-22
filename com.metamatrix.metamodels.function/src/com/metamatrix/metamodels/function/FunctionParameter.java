/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.function;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.function.FunctionParameter#getFunction <em>Function</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.FunctionParameter#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.function.FunctionParameter#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.function.FunctionPackage#getFunctionParameter()
 * @model
 * @generated
 */
public interface FunctionParameter extends EObject{
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getFunctionParameter_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.FunctionParameter#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getFunctionParameter_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.FunctionParameter#getType <em>Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType(String value);

    /**
     * Returns the value of the '<em><b>Function</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.function.ScalarFunction#getInputParameters <em>Input Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Function</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Function</em>' container reference.
     * @see #setFunction(ScalarFunction)
     * @see com.metamatrix.metamodels.function.FunctionPackage#getFunctionParameter_Function()
     * @see com.metamatrix.metamodels.function.ScalarFunction#getInputParameters
     * @model opposite="inputParameters" required="true"
     * @generated
     */
    ScalarFunction getFunction();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.function.FunctionParameter#getFunction <em>Function</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Function</em>' container reference.
     * @see #getFunction()
     * @generated
     */
    void setFunction(ScalarFunction value);

} // FunctionParameter
