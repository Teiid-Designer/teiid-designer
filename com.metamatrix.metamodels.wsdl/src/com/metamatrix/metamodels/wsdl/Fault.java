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
 * A representation of the model object '<em><b>Fault</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Fault#getMessage <em>Message</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Fault#getOperation <em>Operation</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getFault()
 * @model
 * @generated
 */
public interface Fault extends WsdlNameRequiredEntity, ExtensibleAttributesDocumented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Message</em>' attribute.
     * @see #setMessage(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getFault_Message()
     * @model
     * @generated
     */
	String getMessage();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Fault#getMessage <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Message</em>' attribute.
     * @see #getMessage()
     * @generated
     */
	void setMessage(String value);

    /**
     * Returns the value of the '<em><b>Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Operation#getFaults <em>Faults</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Operation</em>' container reference.
     * @see #setOperation(Operation)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getFault_Operation()
     * @see com.metamatrix.metamodels.wsdl.Operation#getFaults
     * @model opposite="faults" required="true"
     * @generated
     */
	Operation getOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Fault#getOperation <em>Operation</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation</em>' container reference.
     * @see #getOperation()
     * @generated
     */
	void setOperation(Operation value);

} // Fault
