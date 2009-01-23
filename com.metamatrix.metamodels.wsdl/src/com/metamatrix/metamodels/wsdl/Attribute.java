/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Attribute#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Attribute#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Attribute#getValue <em>Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Attribute#getNamespaceUri <em>Namespace Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Attribute#getAttributeOwner <em>Attribute Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttribute()
 * @model
 * @generated
 */
public interface Attribute extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttribute_Name()
     * @model
     * @generated
     */
	String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Attribute#getName <em>Name</em>}' attribute.
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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttribute_Prefix()
     * @model
     * @generated
     */
	String getPrefix();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Attribute#getPrefix <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Prefix</em>' attribute.
     * @see #getPrefix()
     * @generated
     */
	void setPrefix(String value);

    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttribute_Value()
     * @model
     * @generated
     */
	String getValue();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Attribute#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
	void setValue(String value);

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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttribute_NamespaceUri()
     * @model
     * @generated
     */
	String getNamespaceUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Attribute#getNamespaceUri <em>Namespace Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Namespace Uri</em>' attribute.
     * @see #getNamespaceUri()
     * @generated
     */
	void setNamespaceUri(String value);

    /**
     * Returns the value of the '<em><b>Attribute Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.AttributeOwner#getAttributes <em>Attributes</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Owner</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Attribute Owner</em>' container reference.
     * @see #setAttributeOwner(AttributeOwner)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getAttribute_AttributeOwner()
     * @see com.metamatrix.metamodels.wsdl.AttributeOwner#getAttributes
     * @model opposite="attributes"
     * @generated
     */
	AttributeOwner getAttributeOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Attribute#getAttributeOwner <em>Attribute Owner</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Attribute Owner</em>' container reference.
     * @see #getAttributeOwner()
     * @generated
     */
	void setAttributeOwner(AttributeOwner value);

} // Attribute
