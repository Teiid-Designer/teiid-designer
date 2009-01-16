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
 * A representation of the model object '<em><b>Xml Fragment Use</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlFragmentUse#getFragment <em>Fragment</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlFragmentUse()
 * @model
 * @generated
 */
public interface XmlFragmentUse extends XmlBaseElement{
    /**
     * Returns the value of the '<em><b>Fragment</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Fragment</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Fragment</em>' reference.
     * @see #setFragment(XmlFragment)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlFragmentUse_Fragment()
     * @model required="true"
     * @generated
     */
    XmlFragment getFragment();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlFragmentUse#getFragment <em>Fragment</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Fragment</em>' reference.
     * @see #getFragment()
     * @generated
     */
    void setFragment(XmlFragment value);

} // XmlFragmentUse
