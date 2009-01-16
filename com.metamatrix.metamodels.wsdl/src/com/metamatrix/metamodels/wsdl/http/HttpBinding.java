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

import com.metamatrix.metamodels.wsdl.Binding;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getVerb <em>Verb</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpBinding()
 * @model
 * @generated
 */
public interface HttpBinding extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Verb</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Verb</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Verb</em>' attribute.
     * @see #setVerb(String)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpBinding_Verb()
     * @model
     * @generated
     */
	String getVerb();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getVerb <em>Verb</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Verb</em>' attribute.
     * @see #getVerb()
     * @generated
     */
	void setVerb(String value);

    /**
     * Returns the value of the '<em><b>Binding</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Binding#getHttpBinding <em>Http Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding</em>' container reference.
     * @see #setBinding(Binding)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpBinding_Binding()
     * @see com.metamatrix.metamodels.wsdl.Binding#getHttpBinding
     * @model opposite="httpBinding" required="true"
     * @generated
     */
	Binding getBinding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpBinding#getBinding <em>Binding</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding</em>' container reference.
     * @see #getBinding()
     * @generated
     */
	void setBinding(Binding value);

} // HttpBinding
