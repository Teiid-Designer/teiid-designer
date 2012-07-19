/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Xml Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.teiid.designer.metamodels.xml.XmlElement#isRecursive <em>Recursive</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.xml.XmlElement#getAttributes <em>Attributes</em>}</li>
 * <li>{@link org.teiid.designer.metamodels.xml.XmlElement#getDeclaredNamespaces <em>Declared Namespaces</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlElement()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface XmlElement extends XmlBaseElement, XmlCommentHolder, ProcessingInstructionHolder, XmlEntityHolder, XmlValueHolder {
    /**
     * Returns the value of the '<em><b>Attributes</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.xml.XmlAttribute}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.xml.XmlAttribute#getElement <em>Element</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Attributes</em>' containment reference list.
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlElement_Attributes()
     * @see org.teiid.designer.metamodels.xml.XmlAttribute#getElement
     * @model type="org.teiid.designer.metamodels.xml.XmlAttribute" opposite="element" containment="true"
     * @generated
     */
    EList getAttributes();

    /**
     * Returns the value of the '<em><b>Declared Namespaces</b></em>' containment reference list. The list contents are of type
     * {@link org.teiid.designer.metamodels.xml.XmlNamespace}. It is bidirectional and its opposite is '
     * {@link org.teiid.designer.metamodels.xml.XmlNamespace#getElement <em>Element</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Declared Namespaces</em>' containment reference list isn't clear, there really should be more of a
     * description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Declared Namespaces</em>' containment reference list.
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlElement_DeclaredNamespaces()
     * @see org.teiid.designer.metamodels.xml.XmlNamespace#getElement
     * @model type="org.teiid.designer.metamodels.xml.XmlNamespace" opposite="element" containment="true"
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
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlElement_Recursive()
     * @model default="false"
     * @generated
     */
    boolean isRecursive();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xml.XmlElement#isRecursive <em>Recursive</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Recursive</em>' attribute.
     * @see #isRecursive()
     * @generated
     */
    void setRecursive( boolean value );
}
