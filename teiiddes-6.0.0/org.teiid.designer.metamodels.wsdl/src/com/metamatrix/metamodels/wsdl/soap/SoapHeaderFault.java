/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Header Fault</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessagePart <em>Message Part</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getSoapHeader <em>Soap Header</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getParts <em>Parts</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeaderFault()
 * @model
 * @generated
 */
public interface SoapHeaderFault extends SoapFault{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Parts</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parts</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Parts</em>' attribute list.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeaderFault_Parts()
     * @model type="java.lang.String"
     * @generated
     */
	EList getParts();

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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeaderFault_Message()
     * @model
     * @generated
     */
	String getMessage();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessage <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Message</em>' attribute.
     * @see #getMessage()
     * @generated
     */
	void setMessage(String value);

    /**
     * Returns the value of the '<em><b>Message Part</b></em>' reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.MessagePart}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Message Part</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Message Part</em>' reference list.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeaderFault_MessagePart()
     * @model type="com.metamatrix.metamodels.wsdl.MessagePart"
     * @generated
     */
	EList getMessagePart();

    /**
     * Returns the value of the '<em><b>Soap Header</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getHeaderFault <em>Header Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Header</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Header</em>' container reference.
     * @see #setSoapHeader(SoapHeader)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeaderFault_SoapHeader()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getHeaderFault
     * @model opposite="headerFault" required="true"
     * @generated
     */
	SoapHeader getSoapHeader();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getSoapHeader <em>Soap Header</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Header</em>' container reference.
     * @see #getSoapHeader()
     * @generated
     */
	void setSoapHeader(SoapHeader value);

} // SoapHeaderFault
