/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interface</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.Interface#getOperations <em>Operations</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getInterface()
 * @model
 * @generated
 */
public interface Interface extends WebServiceComponent{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "Copyright (c) 2000-2004 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Operations</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.webservice.Operation}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.webservice.Operation#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operations</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Operations</em>' containment reference list.
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getInterface_Operations()
     * @see com.metamatrix.metamodels.webservice.Operation#getInterface
     * @model type="com.metamatrix.metamodels.webservice.Operation" opposite="interface" containment="true"
     * @generated
     */
	EList getOperations();

} // Interface
