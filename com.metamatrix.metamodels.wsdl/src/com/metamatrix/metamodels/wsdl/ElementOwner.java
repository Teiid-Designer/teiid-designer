/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Element Owner</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.ElementOwner#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElementOwner()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ElementOwner extends NamespaceDeclarationOwner, Documented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.Element}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Element#getElementOwner <em>Element Owner</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Elements</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getElementOwner_Elements()
     * @see com.metamatrix.metamodels.wsdl.Element#getElementOwner
     * @model type="com.metamatrix.metamodels.wsdl.Element" opposite="elementOwner" containment="true"
     * @generated
     */
	EList getElements();

} // ElementOwner
