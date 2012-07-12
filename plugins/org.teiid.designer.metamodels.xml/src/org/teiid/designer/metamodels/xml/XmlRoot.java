/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.xml.XmlRoot#getFragment <em>Fragment</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlRoot()
 * @model
 * @generated
 */
public interface XmlRoot extends XmlElement{
    /**
     * Returns the value of the '<em><b>Fragment</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.xml.XmlFragment#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Fragment</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Fragment</em>' container reference.
     * @see #setFragment(XmlFragment)
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlRoot_Fragment()
     * @see org.teiid.designer.metamodels.xml.XmlFragment#getRoot
     * @model opposite="root" required="true"
     * @generated
     */
    XmlFragment getFragment();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xml.XmlRoot#getFragment <em>Fragment</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Fragment</em>' container reference.
     * @see #getFragment()
     * @generated
     */
    void setFragment(XmlFragment value);

} // XmlRoot
