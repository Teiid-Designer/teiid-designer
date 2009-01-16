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

package com.metamatrix.metamodels.wsdl.mime;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.MimeElement#getMimeElementOwner <em>Mime Element Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeElement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface MimeElement extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Mime Element Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.mime.MimeElementOwner#getMimeElements <em>Mime Elements</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mime Element Owner</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Mime Element Owner</em>' container reference.
     * @see #setMimeElementOwner(MimeElementOwner)
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeElement_MimeElementOwner()
     * @see com.metamatrix.metamodels.wsdl.mime.MimeElementOwner#getMimeElements
     * @model opposite="mimeElements" required="true"
     * @generated
     */
	MimeElementOwner getMimeElementOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.mime.MimeElement#getMimeElementOwner <em>Mime Element Owner</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Mime Element Owner</em>' container reference.
     * @see #getMimeElementOwner()
     * @generated
     */
	void setMimeElementOwner(MimeElementOwner value);

} // MimeElement
