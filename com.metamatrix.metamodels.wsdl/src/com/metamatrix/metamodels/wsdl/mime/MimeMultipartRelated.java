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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multipart Related</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated#getMimeParts <em>Mime Parts</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeMultipartRelated()
 * @model
 * @generated
 */
public interface MimeMultipartRelated extends MimeElement{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Mime Parts</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.mime.MimePart}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated <em>Mime Multipart Related</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mime Parts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Mime Parts</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeMultipartRelated_MimeParts()
     * @see com.metamatrix.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated
     * @model type="com.metamatrix.metamodels.wsdl.mime.MimePart" opposite="mimeMultipartRelated" containment="true"
     * @generated
     */
	EList getMimeParts();

} // MimeMultipartRelated
