/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.wsdl.soap;

import org.eclipse.emf.ecore.EObject;
import org.teiid.designer.metamodels.wsdl.Port;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.wsdl.soap.SoapAddress#getPort <em>Port</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.wsdl.soap.SoapAddress#getLocation <em>Location</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.wsdl.soap.SoapPackage#getSoapAddress()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface SoapAddress extends EObject {

    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Location</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(String)
     * @see org.teiid.designer.metamodels.wsdl.soap.SoapPackage#getSoapAddress_Location()
     * @model
     * @generated
     */
    String getLocation();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.soap.SoapAddress#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
    void setLocation( String value );

    /**
     * Returns the value of the '<em><b>Port</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.wsdl.Port#getSoapAddress <em>Soap Address</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Port</em>' container reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Port</em>' container reference.
     * @see #setPort(Port)
     * @see org.teiid.designer.metamodels.wsdl.soap.SoapPackage#getSoapAddress_Port()
     * @see org.teiid.designer.metamodels.wsdl.Port#getSoapAddress
     * @model opposite="soapAddress" required="true"
     * @generated
     */
    Port getPort();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.wsdl.soap.SoapAddress#getPort <em>Port</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Port</em>' container reference.
     * @see #getPort()
     * @generated
     */
    void setPort( Port value );

} // SoapAddress
