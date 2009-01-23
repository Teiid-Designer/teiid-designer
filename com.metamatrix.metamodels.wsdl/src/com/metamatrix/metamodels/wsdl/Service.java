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
 * A representation of the model object '<em><b>Service</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Service#getPorts <em>Ports</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getService()
 * @model
 * @generated
 */
public interface Service extends WsdlNameRequiredEntity, ExtensibleDocumented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Definitions</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Definitions#getServices <em>Services</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Definitions</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Definitions</em>' container reference.
     * @see #setDefinitions(Definitions)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getService_Definitions()
     * @see com.metamatrix.metamodels.wsdl.Definitions#getServices
     * @model opposite="services" required="true"
     * @generated
     */
	Definitions getDefinitions();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Service#getDefinitions <em>Definitions</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Definitions</em>' container reference.
     * @see #getDefinitions()
     * @generated
     */
	void setDefinitions(Definitions value);

    /**
     * Returns the value of the '<em><b>Ports</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.wsdl.Port}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Port#getService <em>Service</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ports</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Ports</em>' containment reference list.
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getService_Ports()
     * @see com.metamatrix.metamodels.wsdl.Port#getService
     * @model type="com.metamatrix.metamodels.wsdl.Port" opposite="service" containment="true"
     * @generated
     */
	EList getPorts();

} // Service
