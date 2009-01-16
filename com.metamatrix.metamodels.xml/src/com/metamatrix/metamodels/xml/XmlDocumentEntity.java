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

package com.metamatrix.metamodels.xml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entity</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentEntity()
 * @model abstract="true"
 * @generated
 */
public interface XmlDocumentEntity extends EObject{

    /**
     * <!-- begin-user-doc -->
     * Obtain the path from the root of the document to this container node.  The path
     * will contain segments for {@link XmlSequence sequence}, {@link XmlChoice choice}, 
     * and {@link XmlAll all} components. 
     * @see #getXPath()
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getPathInDocument();

    /**
     * <!-- begin-user-doc -->
     * Obtain the XPath-like string from the root of the document to this document node.  Like true
     * XPaths, the returned path will not contain segments for {@link XmlSequence sequence}, 
     * {@link XmlChoice choice}, or {@link XmlAll all} components.  However, it will also not contain
     * position information, since the XML Document model is not exactly reflective of an actual
     * XML document.
     * @see #getPathInDocument()
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getXPath();

} // XmlDocumentEntity
