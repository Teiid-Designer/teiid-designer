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

package com.metamatrix.metamodels.xmlservice;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Result</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlResult#getOutput <em>Output</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlResult()
 * @model
 * @generated
 */
public interface XmlResult extends EObject{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Output</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xmlservice.XmlOutput#getResult <em>Result</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Output</em>' container reference.
     * @see #setOutput(XmlOutput)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlResult_Output()
     * @see com.metamatrix.metamodels.xmlservice.XmlOutput#getResult
     * @model opposite="result" required="true"
     * @generated
     */
    XmlOutput getOutput();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlResult#getOutput <em>Output</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Output</em>' container reference.
     * @see #getOutput()
     * @generated
     */
    void setOutput(XmlOutput value);

} // XmlResult
