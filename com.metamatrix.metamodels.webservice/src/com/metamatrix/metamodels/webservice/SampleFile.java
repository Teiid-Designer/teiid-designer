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

package com.metamatrix.metamodels.webservice;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sample File</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.SampleFile#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.SampleFile#getUrl <em>Url</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.SampleFile#getSampleMessages <em>Sample Messages</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFile()
 * @model
 * @generated
 */
public interface SampleFile extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFile_Name()
     * @model
     * @generated
     */
	String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.SampleFile#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
	void setName(String value);

    /**
     * Returns the value of the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Url</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Url</em>' attribute.
     * @see #setUrl(String)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFile_Url()
     * @model
     * @generated
     */
	String getUrl();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.SampleFile#getUrl <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Url</em>' attribute.
     * @see #getUrl()
     * @generated
     */
	void setUrl(String value);

    /**
     * Returns the value of the '<em><b>Sample Messages</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.webservice.SampleMessages#getSampleFiles <em>Sample Files</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sample Messages</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Sample Messages</em>' container reference.
     * @see #setSampleMessages(SampleMessages)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getSampleFile_SampleMessages()
     * @see com.metamatrix.metamodels.webservice.SampleMessages#getSampleFiles
     * @model opposite="sampleFiles" required="true"
     * @generated
     */
	SampleMessages getSampleMessages();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.SampleFile#getSampleMessages <em>Sample Messages</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Sample Messages</em>' container reference.
     * @see #getSampleMessages()
     * @generated
     */
	void setSampleMessages(SampleMessages value);

} // SampleFile
