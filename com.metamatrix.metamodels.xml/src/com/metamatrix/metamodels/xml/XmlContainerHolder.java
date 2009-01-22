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
 * A representation of the model object '<em><b>Xml Container Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerHolder#getContainers <em>Containers</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlContainerHolder extends EObject{
    /**
     * Returns the value of the '<em><b>Containers</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.xml.XmlContainerNode}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getParent <em>Parent</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Containers</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Containers</em>' containment reference list.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerHolder_Containers()
     * @see com.metamatrix.metamodels.xml.XmlContainerNode#getParent
     * @model type="com.metamatrix.metamodels.xml.XmlContainerNode" opposite="parent" containment="true"
     * @generated
     */
    EList getContainers();

} // XmlContainerHolder
