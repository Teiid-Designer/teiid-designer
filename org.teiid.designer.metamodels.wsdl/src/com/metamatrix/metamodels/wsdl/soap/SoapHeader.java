/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.wsdl.BindingParam;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Header</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam <em>Binding Param</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessagePart <em>Message Part</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getHeaderFault <em>Header Fault</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getEncodingStyles <em>Encoding Styles</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getParts <em>Parts</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader()
 * @model
 * @generated
 */
public interface SoapHeader extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Use</b></em>' attribute.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.wsdl.soap.SoapUseType}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Use</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Use</em>' attribute.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapUseType
     * @see #setUse(SoapUseType)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_Use()
     * @model
     * @generated
     */
	SoapUseType getUse();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getUse <em>Use</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Use</em>' attribute.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapUseType
     * @see #getUse()
     * @generated
     */
	void setUse(SoapUseType value);

    /**
     * Returns the value of the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Namespace</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Namespace</em>' attribute.
     * @see #setNamespace(String)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_Namespace()
     * @model
     * @generated
     */
	String getNamespace();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getNamespace <em>Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Namespace</em>' attribute.
     * @see #getNamespace()
     * @generated
     */
	void setNamespace(String value);

    /**
     * Returns the value of the '<em><b>Encoding Styles</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Encoding Styles</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Encoding Styles</em>' attribute list.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_EncodingStyles()
     * @model type="java.lang.String"
     * @generated
     */
	EList getEncodingStyles();

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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_Parts()
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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_Message()
     * @model
     * @generated
     */
	String getMessage();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessage <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Message</em>' attribute.
     * @see #getMessage()
     * @generated
     */
	void setMessage(String value);

    /**
     * Returns the value of the '<em><b>Binding Param</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader <em>Soap Header</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding Param</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding Param</em>' container reference.
     * @see #setBindingParam(BindingParam)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_BindingParam()
     * @see com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader
     * @model opposite="soapHeader" required="true"
     * @generated
     */
	BindingParam getBindingParam();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam <em>Binding Param</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding Param</em>' container reference.
     * @see #getBindingParam()
     * @generated
     */
	void setBindingParam(BindingParam value);

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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_MessagePart()
     * @model type="com.metamatrix.metamodels.wsdl.MessagePart"
     * @generated
     */
	EList getMessagePart();

    /**
     * Returns the value of the '<em><b>Header Fault</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getSoapHeader <em>Soap Header</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Header Fault</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Header Fault</em>' containment reference.
     * @see #setHeaderFault(SoapHeaderFault)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapHeader_HeaderFault()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getSoapHeader
     * @model opposite="soapHeader" containment="true"
     * @generated
     */
	SoapHeaderFault getHeaderFault();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getHeaderFault <em>Header Fault</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Header Fault</em>' containment reference.
     * @see #getHeaderFault()
     * @generated
     */
	void setHeaderFault(SoapHeaderFault value);

} // SoapHeader
