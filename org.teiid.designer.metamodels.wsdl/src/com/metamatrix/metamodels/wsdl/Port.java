/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import com.metamatrix.metamodels.wsdl.http.HttpAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapAddress;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Port</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Port#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Port#getService <em>Service</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Port#getSoapAddress <em>Soap Address</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.Port#getHttpAddress <em>Http Address</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPort()
 * @model
 * @generated
 */
public interface Port extends WsdlNameRequiredEntity, ExtensibleDocumented{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Binding</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding</em>' attribute.
     * @see #setBinding(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPort_Binding()
     * @model
     * @generated
     */
	String getBinding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Port#getBinding <em>Binding</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding</em>' attribute.
     * @see #getBinding()
     * @generated
     */
	void setBinding(String value);

    /**
     * Returns the value of the '<em><b>Service</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Service#getPorts <em>Ports</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Service</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Service</em>' container reference.
     * @see #setService(Service)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPort_Service()
     * @see com.metamatrix.metamodels.wsdl.Service#getPorts
     * @model opposite="ports" required="true"
     * @generated
     */
	Service getService();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Port#getService <em>Service</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Service</em>' container reference.
     * @see #getService()
     * @generated
     */
	void setService(Service value);

    /**
     * Returns the value of the '<em><b>Soap Address</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.soap.SoapAddress#getPort <em>Port</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Soap Address</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Address</em>' containment reference.
     * @see #setSoapAddress(SoapAddress)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPort_SoapAddress()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapAddress#getPort
     * @model opposite="port" containment="true"
     * @generated
     */
	SoapAddress getSoapAddress();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Port#getSoapAddress <em>Soap Address</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Address</em>' containment reference.
     * @see #getSoapAddress()
     * @generated
     */
	void setSoapAddress(SoapAddress value);

    /**
     * Returns the value of the '<em><b>Http Address</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.http.HttpAddress#getPort <em>Port</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Http Address</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Http Address</em>' containment reference.
     * @see #setHttpAddress(HttpAddress)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getPort_HttpAddress()
     * @see com.metamatrix.metamodels.wsdl.http.HttpAddress#getPort
     * @model opposite="port" containment="true"
     * @generated
     */
	HttpAddress getHttpAddress();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.Port#getHttpAddress <em>Http Address</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Http Address</em>' containment reference.
     * @see #getHttpAddress()
     * @generated
     */
	void setHttpAddress(HttpAddress value);

} // Port
