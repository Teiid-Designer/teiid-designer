/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Element#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Element#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Element#getTextContent <em>Text Content</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Element#getNamespaceUri <em>Namespace Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Element#getElementOwner <em>Element Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElement()
 * @model
 * @generated
 */
public interface Element extends AttributeOwner, ElementOwner{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElement_Name()
     * @model
     * @generated
     */
	String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Element#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
	void setName(String value);

    /**
     * Returns the value of the '<em><b>Prefix</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Prefix</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Prefix</em>' attribute.
     * @see #setPrefix(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElement_Prefix()
     * @model
     * @generated
     */
	String getPrefix();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Element#getPrefix <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Prefix</em>' attribute.
     * @see #getPrefix()
     * @generated
     */
	void setPrefix(String value);

    /**
     * Returns the value of the '<em><b>Text Content</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text Content</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Text Content</em>' attribute.
     * @see #setTextContent(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElement_TextContent()
     * @model
     * @generated
     */
	String getTextContent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Element#getTextContent <em>Text Content</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Text Content</em>' attribute.
     * @see #getTextContent()
     * @generated
     */
	void setTextContent(String value);

    /**
     * Returns the value of the '<em><b>Namespace Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Namespace Uri</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Namespace Uri</em>' attribute.
     * @see #setNamespaceUri(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElement_NamespaceUri()
     * @model
     * @generated
     */
	String getNamespaceUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Element#getNamespaceUri <em>Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Namespace Uri</em>' attribute.
     * @see #getNamespaceUri()
     * @generated
     */
	void setNamespaceUri(String value);

    /**
     * Returns the value of the '<em><b>Element Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.ElementOwner#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Element Owner</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Element Owner</em>' container reference.
     * @see #setElementOwner(ElementOwner)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElement_ElementOwner()
     * @see com.metamatrix.metamodels.wsdl.ElementOwner#getElements
     * @model opposite="elements"
     * @generated
     */
	ElementOwner getElementOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Element#getElementOwner <em>Element Owner</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Element Owner</em>' container reference.
     * @see #getElementOwner()
     * @generated
     */
	void setElementOwner(ElementOwner value);

} // Element
