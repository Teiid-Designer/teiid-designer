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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlRoot#getFragment <em>Fragment</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlRoot()
 * @model
 * @generated
 */
public interface XmlRoot extends XmlElement{
    /**
     * Returns the value of the '<em><b>Fragment</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlFragment#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Fragment</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Fragment</em>' container reference.
     * @see #setFragment(XmlFragment)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlRoot_Fragment()
     * @see com.metamatrix.metamodels.xml.XmlFragment#getRoot
     * @model opposite="root" required="true"
     * @generated
     */
    XmlFragment getFragment();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlRoot#getFragment <em>Fragment</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Fragment</em>' container reference.
     * @see #getFragment()
     * @generated
     */
    void setFragment(XmlFragment value);

} // XmlRoot
