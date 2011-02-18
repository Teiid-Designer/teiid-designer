/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.http;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.wsdl.BindingOperation;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Operation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.http.HttpOperation#getBindingOperation <em>Binding Operation</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.http.HttpOperation#getLocation <em>Location</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpOperation()
 * @model
 * @generated
 */
public interface HttpOperation extends EObject {

    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Location</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(String)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpOperation_Location()
     * @model
     * @generated
     */
    String getLocation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpOperation#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
    void setLocation( String value );

    /**
     * Returns the value of the '<em><b>Binding Operation</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.BindingOperation#getHttpOperation <em>Http Operation</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Binding Operation</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Binding Operation</em>' container reference.
     * @see #setBindingOperation(BindingOperation)
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#getHttpOperation_BindingOperation()
     * @see com.metamatrix.metamodels.wsdl.BindingOperation#getHttpOperation
     * @model opposite="httpOperation" required="true"
     * @generated
     */
    BindingOperation getBindingOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.http.HttpOperation#getBindingOperation
     * <em>Binding Operation</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Binding Operation</em>' container reference.
     * @see #getBindingOperation()
     * @generated
     */
    void setBindingOperation( BindingOperation value );

} // HttpOperation
