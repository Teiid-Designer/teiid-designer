/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap;

import com.metamatrix.metamodels.wsdl.BindingParam;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Body</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam <em>Binding Param</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getEncodingStyles <em>Encoding Styles</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getParts <em>Parts</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBody()
 * @model
 * @generated
 */
public interface SoapBody extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBody_Use()
     * @model
     * @generated
     */
	SoapUseType getUse();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getUse <em>Use</em>}' attribute.
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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBody_Namespace()
     * @model
     * @generated
     */
	String getNamespace();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getNamespace <em>Namespace</em>}' attribute.
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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBody_EncodingStyles()
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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBody_Parts()
     * @model type="java.lang.String"
     * @generated
     */
	EList getParts();

    /**
     * Returns the value of the '<em><b>Binding Param</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody <em>Soap Body</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding Param</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding Param</em>' container reference.
     * @see #setBindingParam(BindingParam)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBody_BindingParam()
     * @see com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody
     * @model opposite="soapBody" required="true"
     * @generated
     */
	BindingParam getBindingParam();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam <em>Binding Param</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding Param</em>' container reference.
     * @see #getBindingParam()
     * @generated
     */
	void setBindingParam(BindingParam value);

} // SoapBody
