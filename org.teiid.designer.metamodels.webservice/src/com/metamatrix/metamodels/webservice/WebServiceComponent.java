/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Component</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.webservice.WebServiceComponent#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getWebServiceComponent()
 * @model abstract="true"
 * @generated
 */
public interface WebServiceComponent extends EObject {

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getWebServiceComponent_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.WebServiceComponent#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName( String value );

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc --> <!-- begin-model-doc --> Method to determine whether this relationship entity
     * is considered valid. The result is an IStatus that contains a message that can be displayed to the user, as well as a
     * status code designating "OK", "WARNING", or "ERROR". <!-- end-model-doc -->
     * 
     * @model dataType="com.metamatrix.metamodels.webservice.IStatus"
     * @generated
     */
    IStatus isValid(); // NO_UCD

} // WebServiceComponent
