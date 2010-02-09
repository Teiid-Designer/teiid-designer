/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Message Part</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.MessagePart#getType <em>Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.MessagePart#getElement <em>Element</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.MessagePart#getMessage <em>Message</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getMessagePart()
 * @model
 * @generated
 */
public interface MessagePart extends ExtensibleAttributesDocumented, WsdlNameOptionalEntity {

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getMessagePart_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.MessagePart#getType <em>Type</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType( String value );

    /**
     * Returns the value of the '<em><b>Element</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Element</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Element</em>' attribute.
     * @see #setElement(String)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getMessagePart_Element()
     * @model
     * @generated
     */
    String getElement();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.MessagePart#getElement <em>Element</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Element</em>' attribute.
     * @see #getElement()
     * @generated
     */
    void setElement( String value );

    /**
     * Returns the value of the '<em><b>Message</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.wsdl.Message#getParts <em>Parts</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Message</em>' container reference isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Message</em>' container reference.
     * @see #setMessage(Message)
     * @see com.metamatrix.metamodels.wsdl.WsdlPackage#getMessagePart_Message()
     * @see com.metamatrix.metamodels.wsdl.Message#getParts
     * @model opposite="parts" required="true"
     * @generated
     */
    Message getMessage();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.MessagePart#getMessage <em>Message</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Message</em>' container reference.
     * @see #getMessage()
     * @generated
     */
    void setMessage( Message value );

} // MessagePart
