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
 * A representation of the model object '<em><b>Param Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.ParamType#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getParamType()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ParamType extends WsdlNameOptionalEntity, ExtensibleAttributesDocumented{
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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getParamType_Message()
     * @model
     * @generated
     */
	String getMessage();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.ParamType#getMessage <em>Message</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Message</em>' attribute.
     * @see #getMessage()
     * @generated
     */
	void setMessage(String value);

} // ParamType
