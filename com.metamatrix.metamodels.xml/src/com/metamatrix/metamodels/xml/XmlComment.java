/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Comment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlComment#getText <em>Text</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlComment#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlComment()
 * @model
 * @generated
 */
public interface XmlComment extends XmlDocumentEntity{
    /**
     * Returns the value of the '<em><b>Text</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Text</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Text</em>' attribute.
     * @see #setText(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlComment_Text()
     * @model
     * @generated
     */
    String getText();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlComment#getText <em>Text</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Text</em>' attribute.
     * @see #getText()
     * @generated
     */
    void setText(String value);

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlCommentHolder#getComments <em>Comments</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(XmlCommentHolder)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlComment_Parent()
     * @see com.metamatrix.metamodels.xml.XmlCommentHolder#getComments
     * @model opposite="comments" required="true"
     * @generated
     */
    XmlCommentHolder getParent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlComment#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent(XmlCommentHolder value);

} // XmlComment
