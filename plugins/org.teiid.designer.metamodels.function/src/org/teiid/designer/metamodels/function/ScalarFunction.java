/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.function;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Scalar Function</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.function.ScalarFunction#getInputParameters <em>Input Parameters</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.function.ScalarFunction#getReturnParameter <em>Return Parameter</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.function.ScalarFunction#getInvocationClass <em>Invocation Class</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.function.ScalarFunction#getInvocationMethod <em>Invocation Method</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.function.ScalarFunction#isDeterministic <em>Deterministic</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.function.FunctionPackage#getScalarFunction()
 * @model
 * @generated
 *
 * @since 8.0
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
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getScalarFunction_InvocationClass()
     * @model
     * @generated
     */
    String getInvocationClass();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.function.ScalarFunction#getInvocationClass <em>Invocation Class</em>}' attribute.
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
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getScalarFunction_InvocationMethod()
     * @model
     * @generated
     */
    String getInvocationMethod();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.function.ScalarFunction#getInvocationMethod <em>Invocation Method</em>}' attribute.
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
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getScalarFunction_Deterministic()
     * @model default="false"
     * @generated
     */
    boolean isDeterministic();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.function.ScalarFunction#isDeterministic <em>Deterministic</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Deterministic</em>' attribute.
     * @see #isDeterministic()
     * @generated
     */
    void setDeterministic(boolean value);

    /**
     * Returns the value of the '<em><b>Input Parameters</b></em>' containment reference list.
     * The list contents are of type {@link org.teiid.designer.metamodels.function.FunctionParameter}.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.function.FunctionParameter#getFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Input Parameters</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Input Parameters</em>' containment reference list.
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getScalarFunction_InputParameters()
     * @see org.teiid.designer.metamodels.function.FunctionParameter#getFunction
     * @model type="org.teiid.designer.metamodels.function.FunctionParameter" opposite="function" containment="true"
     * @generated
     */
    EList getInputParameters();

    /**
     * Returns the value of the '<em><b>Return Parameter</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.function.ReturnParameter#getFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Return Parameter</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Return Parameter</em>' containment reference.
     * @see #setReturnParameter(ReturnParameter)
     * @see org.teiid.designer.metamodels.function.FunctionPackage#getScalarFunction_ReturnParameter()
     * @see org.teiid.designer.metamodels.function.ReturnParameter#getFunction
     * @model opposite="function" containment="true" required="true"
     * @generated
     */
    ReturnParameter getReturnParameter();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.function.ScalarFunction#getReturnParameter <em>Return Parameter</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Return Parameter</em>' containment reference.
     * @see #getReturnParameter()
     * @generated
     */
    void setReturnParameter(ReturnParameter value);

} // ScalarFunction
