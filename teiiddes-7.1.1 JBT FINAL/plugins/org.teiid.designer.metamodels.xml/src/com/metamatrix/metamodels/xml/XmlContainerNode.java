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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Xml Container Node</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#isExcludeFromDocument <em>Exclude From Document</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getMinOccurs <em>Min Occurs</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getMaxOccurs <em>Max Occurs</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getXsdComponent <em>Xsd Component</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode()
 * @model abstract="true"
 * @generated
 */
public interface XmlContainerNode extends XmlDocumentEntity, XmlEntityHolder, ChoiceOption, XmlBuildable, XmlHolderEntity {

    /**
     * Returns the value of the '<em><b>Max Occurs</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Max Occurs</em>' attribute.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_MaxOccurs()
     * @model unique="false" transient="true" changeable="false" volatile="true"
     * @generated
     */
    int getMaxOccurs();

    /**
     * Returns the value of the '<em><b>Min Occurs</b></em>' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Min Occurs</em>' attribute.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_MinOccurs()
     * @model unique="false" transient="true" changeable="false" volatile="true"
     * @generated
     */
    int getMinOccurs();

    /**
     * Returns the value of the '<em><b>Xsd Component</b></em>' reference. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Xsd Component</em>' reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Xsd Component</em>' reference.
     * @see #setXsdComponent(XSDComponent)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_XsdComponent()
     * @model
     * @generated
     */
    XSDComponent getXsdComponent();

    /**
     * Returns the value of the '<em><b>Exclude From Document</b></em>' attribute. The default value is <code>"false"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Exclude From Document</em>' attribute isn't clear, there really should be more of a description
     * here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Exclude From Document</em>' attribute.
     * @see #setExcludeFromDocument(boolean)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_ExcludeFromDocument()
     * @model default="false"
     * @generated
     */
    boolean isExcludeFromDocument();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlContainerNode#isExcludeFromDocument
     * <em>Exclude From Document</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Exclude From Document</em>' attribute.
     * @see #isExcludeFromDocument()
     * @generated
     */
    void setExcludeFromDocument( boolean value );

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getXsdComponent <em>Xsd Component</em>}'
     * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Xsd Component</em>' reference.
     * @see #getXsdComponent()
     * @generated
     */
    void setXsdComponent( XSDComponent value );
}
