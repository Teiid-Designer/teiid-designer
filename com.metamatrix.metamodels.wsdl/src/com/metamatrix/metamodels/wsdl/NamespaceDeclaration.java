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
 * A representation of the model object '<em><b>Namespace Declaration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getUri <em>Uri</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getPrefix <em>Prefix</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getOwner <em>Owner</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getNamespaceDeclaration()
 * @model
 * @generated
 */
public interface NamespaceDeclaration extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Uri</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Uri</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Uri</em>' attribute.
     * @see #setUri(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getNamespaceDeclaration_Uri()
     * @model
     * @generated
     */
	String getUri();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getUri <em>Uri</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Uri</em>' attribute.
     * @see #getUri()
     * @generated
     */
	void setUri(String value);

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
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getNamespaceDeclaration_Prefix()
     * @model
     * @generated
     */
	String getPrefix();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getPrefix <em>Prefix</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Prefix</em>' attribute.
     * @see #getPrefix()
     * @generated
     */
	void setPrefix(String value);

    /**
     * Returns the value of the '<em><b>Owner</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner#getDeclaredNamespaces <em>Declared Namespaces</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Owner</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Owner</em>' container reference.
     * @see #setOwner(NamespaceDeclarationOwner)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getNamespaceDeclaration_Owner()
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner#getDeclaredNamespaces
     * @model opposite="declaredNamespaces" required="true"
     * @generated
     */
	NamespaceDeclarationOwner getOwner();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getOwner <em>Owner</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Owner</em>' container reference.
     * @see #getOwner()
     * @generated
     */
	void setOwner(NamespaceDeclarationOwner value);

} // NamespaceDeclaration
