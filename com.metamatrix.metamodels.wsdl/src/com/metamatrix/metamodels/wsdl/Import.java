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
 * A representation of the model object '<em><b>Import</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Import#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Import#getLocation <em>Location</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Import#getDefinitions <em>Definitions</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getImport()
 * @model
 * @generated
 */
public interface Import extends ExtensibleAttributesDocumented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Namespace</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Namespace</em>' attribute.
     * @see #setNamespace(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getImport_Namespace()
     * @model
     * @generated
     */
	String getNamespace();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Import#getNamespace <em>Namespace</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Namespace</em>' attribute.
     * @see #getNamespace()
     * @generated
     */
	void setNamespace(String value);

    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Location</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getImport_Location()
     * @model
     * @generated
     */
	String getLocation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Import#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
	void setLocation(String value);

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Definitions#getImports <em>Imports</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Definitions</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getImport_Definitions()
     * @see com.metamatrix.metamodels.wsdl.Definitions#getImports
     * @model opposite="imports" required="true"
     * @generated
     */
	Definitions getDefinitions();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Import#getDefinitions <em>Definitions</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
	void setDefinitions(Definitions value);

} // Import
