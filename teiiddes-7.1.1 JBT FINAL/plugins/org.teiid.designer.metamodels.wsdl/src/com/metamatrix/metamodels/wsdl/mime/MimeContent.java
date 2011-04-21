/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime;

import com.metamatrix.metamodels.wsdl.MessagePart;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Content</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#getMessagePart <em>Message Part</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#getType <em>Type</em>}</li>
 * <li>{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#isXml <em>Xml</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeContent()
 * @model
 * @generated
 */
public interface MimeContent extends MimeElement {

    /**
     * Returns the value of the '<em><b>Type</b></em>' attribute. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Type</em>' attribute.
     * @see #setType(String)
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeContent_Type()
     * @model
     * @generated
     */
    String getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#getType <em>Type</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Type</em>' attribute.
     * @see #getType()
     * @generated
     */
    void setType( String value );

    /**
     * Returns the value of the '<em><b>Xml</b></em>' attribute. The default value is <code>"false"</code>. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Xml</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Xml</em>' attribute.
     * @see #setXml(boolean)
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeContent_Xml()
     * @model default="false"
     * @generated
     */
    boolean isXml();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#isXml <em>Xml</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Xml</em>' attribute.
     * @see #isXml()
     * @generated
     */
    void setXml( boolean value );

    /**
     * Returns the value of the '<em><b>Message Part</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Message Part</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Message Part</em>' reference.
     * @see #setMessagePart(MessagePart)
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimeContent_MessagePart()
     * @model
     * @generated
     */
    MessagePart getMessagePart();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.mime.MimeContent#getMessagePart <em>Message Part</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Message Part</em>' reference.
     * @see #getMessagePart()
     * @generated
     */
    void setMessagePart( MessagePart value );

} // MimeContent
