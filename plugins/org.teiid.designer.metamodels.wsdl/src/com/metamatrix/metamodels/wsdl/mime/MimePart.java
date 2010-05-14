/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Part</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated <em>Mime Multipart Related</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimePart()
 * @model
 * @generated
 */
public interface MimePart extends MimeElementOwner {

    /**
     * Returns the value of the '<em><b>Mime Multipart Related</b></em>' container reference. It is bidirectional and its opposite
     * is '{@link com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated#getMimeParts <em>Mime Parts</em>}'. <!-- begin-user-doc
     * -->
     * <p>
     * If the meaning of the '<em>Mime Multipart Related</em>' container reference isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Mime Multipart Related</em>' container reference.
     * @see #setMimeMultipartRelated(MimeMultipartRelated)
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#getMimePart_MimeMultipartRelated()
     * @see com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated#getMimeParts
     * @model opposite="mimeParts" required="true"
     * @generated
     */
    MimeMultipartRelated getMimeMultipartRelated();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.mime.MimePart#getMimeMultipartRelated
     * <em>Mime Multipart Related</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Mime Multipart Related</em>' container reference.
     * @see #getMimeMultipartRelated()
     * @generated
     */
    void setMimeMultipartRelated( MimeMultipartRelated value );

} // MimePart
