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
 * A representation of the model object '<em><b>Xml Fragment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlFragment#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlFragment#getRoot <em>Root</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlFragment()
 * @model
 * @generated
 */
public interface XmlFragment extends XmlDocumentEntity, XmlCommentHolder, ProcessingInstructionHolder{
    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlFragment_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlFragment#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Root</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlRoot#getFragment <em>Fragment</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Root</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Root</em>' containment reference.
     * @see #setRoot(XmlRoot)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlFragment_Root()
     * @see com.metamatrix.metamodels.xml.XmlRoot#getFragment
     * @model opposite="fragment" containment="true" required="true"
     * @generated
     */
    XmlRoot getRoot();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlFragment#getRoot <em>Root</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Root</em>' containment reference.
     * @see #getRoot()
     * @generated
     */
    void setRoot(XmlRoot value);

} // XmlFragment
