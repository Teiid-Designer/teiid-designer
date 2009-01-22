/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import org.eclipse.xsd.XSDComponent;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getName <em>Name</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocumentNode#isExcludeFromDocument <em>Exclude From Document</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getMinOccurs <em>Min Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getMaxOccurs <em>Max Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getXsdComponent <em>Xsd Component</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getNamespace <em>Namespace</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode()
 * @model abstract="true"
 * @generated
 */
public interface XmlDocumentNode extends XmlDocumentEntity, XmlBuildable{
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
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Exclude From Document</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Exclude From Document</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Exclude From Document</em>' attribute.
     * @see #setExcludeFromDocument(boolean)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode_ExcludeFromDocument()
     * @model default="false"
     * @generated
     */
    boolean isExcludeFromDocument();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#isExcludeFromDocument <em>Exclude From Document</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Exclude From Document</em>' attribute.
     * @see #isExcludeFromDocument()
     * @generated
     */
    void setExcludeFromDocument(boolean value);

    /**
     * Returns the value of the '<em><b>Xsd Component</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Xsd Component</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Xsd Component</em>' reference.
     * @see #setXsdComponent(XSDComponent)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode_XsdComponent()
     * @model
     * @generated
     */
    XSDComponent getXsdComponent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getXsdComponent <em>Xsd Component</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Xsd Component</em>' reference.
     * @see #getXsdComponent()
     * @generated
     */
    void setXsdComponent(XSDComponent value);

    /**
     * Returns the value of the '<em><b>Namespace</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Namespace</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Namespace</em>' reference.
     * @see #setNamespace(XmlNamespace)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode_Namespace()
     * @model
     * @generated
     */
    XmlNamespace getNamespace();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocumentNode#getNamespace <em>Namespace</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Namespace</em>' reference.
     * @see #getNamespace()
     * @generated
     */
    void setNamespace(XmlNamespace value);

    /**
     * Returns the value of the '<em><b>Min Occurs</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Min Occurs</em>' attribute.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode_MinOccurs()
     * @model default="1" unique="false" transient="true" changeable="false" volatile="true"
     * @generated
     */
    int getMinOccurs();

    /**
     * Returns the value of the '<em><b>Max Occurs</b></em>' attribute.
     * The default value is <code>"1"</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Occurs</em>' attribute.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentNode_MaxOccurs()
     * @model default="1" unique="false" transient="true" changeable="false" volatile="true"
     * @generated
     */
    int getMaxOccurs();

} // XmlDocumentNode
