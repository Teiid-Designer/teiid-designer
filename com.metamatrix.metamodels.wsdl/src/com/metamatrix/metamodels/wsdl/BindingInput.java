/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding Input</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.BindingInput#getBindingOperation <em>Binding Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingInput()
 * @model
 * @generated
 */
public interface BindingInput extends BindingParam{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Binding Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBindingInput <em>Binding Input</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding Operation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding Operation</em>' container reference.
     * @see #setBindingOperation(BindingOperation)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingInput_BindingOperation()
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBindingInput
     * @model opposite="bindingInput" required="true"
     * @generated
     */
	BindingOperation getBindingOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingInput#getBindingOperation <em>Binding Operation</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding Operation</em>' container reference.
     * @see #getBindingOperation()
     * @generated
     */
	void setBindingOperation(BindingOperation value);

} // BindingInput
