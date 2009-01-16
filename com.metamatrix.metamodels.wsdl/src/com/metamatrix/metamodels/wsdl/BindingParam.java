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

package com.metamatrix.metamodels.wsdl;

import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;

import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding Param</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader <em>Soap Header</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody <em>Soap Body</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingParam()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface BindingParam extends ExtensibleDocumented, MimeElementOwner, WsdlNameOptionalEntity{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Soap Header</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam <em>Binding Param</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Header</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Header</em>' containment reference.
     * @see #setSoapHeader(SoapHeader)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingParam_SoapHeader()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam
     * @model opposite="bindingParam" containment="true"
     * @generated
     */
	SoapHeader getSoapHeader();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader <em>Soap Header</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Header</em>' containment reference.
     * @see #getSoapHeader()
     * @generated
     */
	void setSoapHeader(SoapHeader value);

    /**
     * Returns the value of the '<em><b>Soap Body</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam <em>Binding Param</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Body</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Body</em>' containment reference.
     * @see #setSoapBody(SoapBody)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingParam_SoapBody()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam
     * @model opposite="bindingParam" containment="true"
     * @generated
     */
	SoapBody getSoapBody();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody <em>Soap Body</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Body</em>' containment reference.
     * @see #getSoapBody()
     * @generated
     */
	void setSoapBody(SoapBody value);

} // BindingParam
