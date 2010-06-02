/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Xml Base Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link com.metamatrix.metamodels.xml.XmlHolderEntity#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlBaseElement()
 * @model abstract="true"
 * @generated
 */
public interface XmlHolderEntity extends EObject {

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference. It is bidirectional and its opposite is '
     * {@link com.metamatrix.metamodels.xml.XmlEntityHolder#getEntities <em>Entities</em>}'. <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * 
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(XmlEntityHolder)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlHolderEntity_Parent()
     * @see com.metamatrix.metamodels.xml.XmlEntityHolder#getEntities
     * @model opposite="entities" required="true"
     * @generated
     */
    XmlEntityHolder getParent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlHolderEntity#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent( XmlEntityHolder value );
}
