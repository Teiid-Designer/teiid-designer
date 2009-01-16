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

import com.metamatrix.metamodels.xml.XmlDocument;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Output</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.Output#getOperation <em>Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.Output#getXmlDocument <em>Xml Document</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOutput()
 * @model
 * @generated
 */
public interface Output extends Message{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.webservice.Operation#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Operation</em>' container reference.
     * @see #setOperation(Operation)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOutput_Operation()
     * @see com.metamatrix.metamodels.webservice.Operation#getOutput
     * @model opposite="output" required="true"
     * @generated
     */
	Operation getOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Output#getOperation <em>Operation</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation</em>' container reference.
     * @see #getOperation()
     * @generated
     */
	void setOperation(Operation value);

    /**
     * Returns the value of the '<em><b>Xml Document</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Xml Document</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Xml Document</em>' reference.
     * @see #setXmlDocument(XmlDocument)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOutput_XmlDocument()
     * @model required="true"
     * @generated
     */
	XmlDocument getXmlDocument();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Output#getXmlDocument <em>Xml Document</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Xml Document</em>' reference.
     * @see #getXmlDocument()
     * @generated
     */
	void setXmlDocument(XmlDocument value);

} // Output
