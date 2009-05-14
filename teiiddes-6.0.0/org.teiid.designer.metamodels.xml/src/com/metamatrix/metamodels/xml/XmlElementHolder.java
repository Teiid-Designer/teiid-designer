/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Element Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlElementHolder#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlElementHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlElementHolder extends EObject{
    /**
     * Returns the value of the '<em><b>Elements</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.xml.XmlBaseElement}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlBaseElement#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Elements</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Elements</em>' containment reference list.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlElementHolder_Elements()
     * @see com.metamatrix.metamodels.xml.XmlBaseElement#getParent
     * @model type="com.metamatrix.metamodels.xml.XmlBaseElement" opposite="parent" containment="true"
     * @generated
     */
    EList getElements();

} // XmlElementHolder
