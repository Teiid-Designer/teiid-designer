/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Xml Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.xml.XmlElement#isRecursive <em>Recursive</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.XmlElement#getAttributes <em>Attributes</em>}</li>
 * <li>{@link com.metamatrix.metamodels.xml.XmlElement#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlElement()
 * @model
 * @generated
 */
public interface XmlElement extends XmlBaseElement, XmlCommentHolder, ProcessingInstructionHolder, XmlEntityHolder, XmlValueHolder {
    /**
     * Returns the value of the '<em><b>Attributes</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.xml.XmlAttribute}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.xml.XmlAttribute#getElement <em>Element</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Attributes</em>' containment reference list.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlElement_Attributes()
     * @see com.metamatrix.metamodels.xml.XmlAttribute#getElement
     * @model type="com.metamatrix.metamodels.xml.XmlAttribute" opposite="element" containment="true"
     * @generated
     */
    EList getAttributes();

    /**
     * Returns the value of the '<em><b>Declared Namespaces</b></em>' containment reference list. The list contents are of type
     * {@link com.metamatrix.metamodels.xml.XmlNamespace}. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.xml.XmlNamespace#getElement <em>Element</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Declared Namespaces</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Declared Namespaces</em>' containment reference list.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlElement_DeclaredNamespaces()
     * @see com.metamatrix.metamodels.xml.XmlNamespace#getElement
     * @model type="com.metamatrix.metamodels.xml.XmlNamespace" opposite="element" containment="true"
     * @generated
     */
    EList getDeclaredNamespaces();

    /**
     * Returns the value of the '<em><b>Recursive</b></em>' attribute. The default value is <code>"false"</code>. <!--
     * begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Recursive</em>' attribute isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Recursive</em>' attribute.
     * @see #setRecursive(boolean)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlElement_Recursive()
     * @model default="false"
     * @generated
     */
    boolean isRecursive();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlElement#isRecursive <em>Recursive</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Recursive</em>' attribute.
     * @see #isRecursive()
     * @generated
     */
    void setRecursive( boolean value );
}
