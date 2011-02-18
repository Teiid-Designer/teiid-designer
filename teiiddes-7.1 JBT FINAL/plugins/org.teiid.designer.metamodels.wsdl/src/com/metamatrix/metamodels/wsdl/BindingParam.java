/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Binding Param</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader <em>Soap Header</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody <em>Soap Body</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingParam()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface BindingParam extends ExtensibleDocumented, MimeElementOwner, WsdlNameOptionalEntity {

    /**
     * Returns the value of the '<em><b>Soap Header</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam <em>Binding Param</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Soap Header</em>' containment reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Soap Header</em>' containment reference.
     * @see #setSoapHeader(SoapHeader)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingParam_SoapHeader()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam
     * @model opposite="bindingParam" containment="true"
     * @generated
     */
    SoapHeader getSoapHeader();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapHeader <em>Soap Header</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Soap Header</em>' containment reference.
     * @see #getSoapHeader()
     * @generated
     */
    void setSoapHeader( SoapHeader value );

    /**
     * Returns the value of the '<em><b>Soap Body</b></em>' containment reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam <em>Binding Param</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Soap Body</em>' containment reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Soap Body</em>' containment reference.
     * @see #setSoapBody(SoapBody)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getBindingParam_SoapBody()
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam
     * @model opposite="bindingParam" containment="true"
     * @generated
     */
    SoapBody getSoapBody();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.BindingParam#getSoapBody <em>Soap Body</em>}' containment
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Soap Body</em>' containment reference.
     * @see #getSoapBody()
     * @generated
     */
    void setSoapBody( SoapBody value );

} // BindingParam
