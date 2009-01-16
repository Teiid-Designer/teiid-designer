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

package com.metamatrix.metamodels.wsdl.http;

import com.metamatrix.metamodels.wsdl.Port;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Address</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getPort <em>Port</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getLocation <em>Location</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpAddress()
 * @model
 * @generated
 */
public interface HttpAddress extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(String)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpAddress_Location()
     * @model
     * @generated
     */
	String getLocation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
	void setLocation(String value);

    /**
     * Returns the value of the '<em><b>Port</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Port#getHttpAddress <em>Http Address</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Port</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Port</em>' container reference.
     * @see #setPort(Port)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpAddress_Port()
     * @see com.metamatrix.metamodels.wsdl.Port#getHttpAddress
     * @model opposite="httpAddress" required="true"
     * @generated
     */
	Port getPort();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getPort <em>Port</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Port</em>' container reference.
     * @see #getPort()
     * @generated
     */
	void setPort(Port value);

} // HttpAddress
