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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Entity</b></em>'. <!-- end-user-doc -->
 * 
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocumentEntity()
 * @model abstract="true"
 * @generated
 */
public interface XmlDocumentEntity extends EObject {

    /**
     * <!-- begin-user-doc --> Obtain the path from the root of the document to this container node. The path will contain segments
     * for {@link XmlSequence sequence}, {@link XmlChoice choice}, and {@link XmlAll all} components.
     * 
     * @see #getXPath() <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getPathInDocument();

    /**
     * <!-- begin-user-doc --> Obtain the XPath-like string from the root of the document to this document node. Like true XPaths,
     * the returned path will not contain segments for {@link XmlSequence sequence}, {@link XmlChoice choice}, or {@link XmlAll all}
     * components. However, it will also not contain position information, since the XML Document model is not exactly reflective of
     * an actual XML document.
     * 
     * @see #getPathInDocument() <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    String getXPath();
}
