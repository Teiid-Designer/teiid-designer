/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;


import org.eclipse.xsd.XSDAttributeUseCategory;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlAttribute#getUse <em>Use</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlAttribute#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlAttribute()
 * @model
 * @generated
 */
public interface XmlAttribute extends XmlDocumentNode, XmlValueHolder{
    /**
     * Returns the value of the '<em><b>Use</b></em>' attribute.
     * The literals are from the enumeration {@link org.eclipse.xsd.XSDAttributeUseCategory}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Use</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Use</em>' attribute.
     * @see org.eclipse.xsd.XSDAttributeUseCategory
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlAttribute_Use()
     * @model transient="true" changeable="false" volatile="true"
     * @generated
     */
    XSDAttributeUseCategory getUse();

    /**
     * Returns the value of the '<em><b>Element</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlElement#getAttributes <em>Attributes</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Element</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Element</em>' container reference.
     * @see #setElement(XmlElement)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlAttribute_Element()
     * @see com.metamatrix.metamodels.xml.XmlElement#getAttributes
     * @model opposite="attributes" required="true"
     * @generated
     */
    XmlElement getElement();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlAttribute#getElement <em>Element</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Element</em>' container reference.
     * @see #getElement()
     * @generated
     */
    void setElement(XmlElement value);

} // XmlAttribute
