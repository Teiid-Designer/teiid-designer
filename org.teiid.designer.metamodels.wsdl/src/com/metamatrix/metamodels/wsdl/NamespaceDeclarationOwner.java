/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Namespace Declaration Owner</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.NamespaceDeclarationOwner#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getNamespaceDeclarationOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface NamespaceDeclarationOwner extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Declared Namespaces</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Declared Namespaces</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Declared Namespaces</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getNamespaceDeclarationOwner_DeclaredNamespaces()
     * @see com.metamatrix.metamodels.wsdl.NamespaceDeclaration#getOwner
     * @model type="com.metamatrix.metamodels.wsdl.NamespaceDeclaration" opposite="owner" containment="true"
     * @generated
     */
	EList getDeclaredNamespaces();

} // NamespaceDeclarationOwner
