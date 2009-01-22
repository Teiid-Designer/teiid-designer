/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice;

import org.eclipse.xsd.XSDElementDeclaration;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Message</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlMessage#getContentElement <em>Content Element</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlMessage()
 * @model
 * @generated
 */
public interface XmlMessage extends XmlServiceComponent {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Content Element</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Content Element</em>' reference.
     * @see #setContentElement(XSDElementDeclaration)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlMessage_ContentElement()
     * @model
     * @generated
     */
    XSDElementDeclaration getContentElement();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlMessage#getContentElement <em>Content Element</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Content Element</em>' reference.
     * @see #getContentElement()
     * @generated
     */
    void setContentElement(XSDElementDeclaration value);

} // XmlMessage
