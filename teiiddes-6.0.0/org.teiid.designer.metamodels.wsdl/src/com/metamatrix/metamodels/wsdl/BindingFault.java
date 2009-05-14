/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import com.metamatrix.metamodels.wsdl.soap.SoapFault;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding Fault</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.BindingFault#getBindingOperation <em>Binding Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.BindingFault#getSoapFault <em>Soap Fault</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingFault()
 * @model
 * @generated
 */
public interface BindingFault extends ExtensibleDocumented, WsdlNameRequiredEntity{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Binding Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.BindingOperation#getBindingFaults <em>Binding Faults</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding Operation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding Operation</em>' container reference.
     * @see #setBindingOperation(BindingOperation)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingFault_BindingOperation()
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getBindingFaults
     * @model opposite="bindingFaults" required="true"
     * @generated
     */
	BindingOperation getBindingOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingFault#getBindingOperation <em>Binding Operation</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding Operation</em>' container reference.
     * @see #getBindingOperation()
     * @generated
     */
	void setBindingOperation(BindingOperation value);

    /**
     * Returns the value of the '<em><b>Soap Fault</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault#getBindingFault <em>Binding Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Fault</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Fault</em>' containment reference.
     * @see #setSoapFault(SoapFault)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingFault_SoapFault()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault#getBindingFault
     * @model opposite="bindingFault" containment="true" required="true"
     * @generated
     */
	SoapFault getSoapFault();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingFault#getSoapFault <em>Soap Fault</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Fault</em>' containment reference.
     * @see #getSoapFault()
     * @generated
     */
	void setSoapFault(SoapFault value);

} // BindingFault
