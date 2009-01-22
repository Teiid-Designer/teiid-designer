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
 * A representation of the model object '<em><b>Documented</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Documented#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDocumented()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Documented extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright � 2000-2005 MetaMatrix, Inc.  All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Documentation</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Documentation#getDocumented <em>Documented</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Documentation</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Documentation</em>' containment reference.
     * @see #setDocumentation(Documentation)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getDocumented_Documentation()
     * @see com.metamatrix.metamodels.wsdl.Documentation#getDocumented
     * @model opposite="documented" containment="true"
     * @generated
     */
	Documentation getDocumentation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Documented#getDocumentation <em>Documentation</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Documentation</em>' containment reference.
     * @see #getDocumentation()
     * @generated
     */
	void setDocumentation(Documentation value);

} // Documented
