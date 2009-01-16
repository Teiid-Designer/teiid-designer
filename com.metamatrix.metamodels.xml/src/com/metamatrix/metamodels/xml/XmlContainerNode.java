/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.xml;

import org.eclipse.xsd.XSDComponent;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Container Node</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#isExcludeFromDocument <em>Exclude From Document</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getMinOccurs <em>Min Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getMaxOccurs <em>Max Occurs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getXsdComponent <em>Xsd Component</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlContainerNode#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode()
 * @model abstract="true"
 * @generated
 */
public interface XmlContainerNode extends XmlDocumentEntity, XmlElementHolder, XmlContainerHolder, ChoiceOption, XmlBuildable {
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
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_ExcludeFromDocument()
     * @model default="false"
     * @generated
     */
    boolean isExcludeFromDocument();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlContainerNode#isExcludeFromDocument <em>Exclude From Document</em>}' attribute.
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
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_XsdComponent()
     * @model
     * @generated
     */
    XSDComponent getXsdComponent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getXsdComponent <em>Xsd Component</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Xsd Component</em>' reference.
     * @see #getXsdComponent()
     * @generated
     */
    void setXsdComponent(XSDComponent value);

    /**
     * Returns the value of the '<em><b>Parent</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlContainerHolder#getContainers <em>Containers</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Parent</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Parent</em>' container reference.
     * @see #setParent(XmlContainerHolder)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_Parent()
     * @see com.metamatrix.metamodels.xml.XmlContainerHolder#getContainers
     * @model opposite="containers" required="true"
     * @generated
     */
    XmlContainerHolder getParent();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlContainerNode#getParent <em>Parent</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Parent</em>' container reference.
     * @see #getParent()
     * @generated
     */
    void setParent(XmlContainerHolder value);

    /**
     * Returns the value of the '<em><b>Min Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Min Occurs</em>' attribute.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_MinOccurs()
     * @model unique="false" transient="true" changeable="false" volatile="true"
     * @generated
     */
    int getMinOccurs();

    /**
     * Returns the value of the '<em><b>Max Occurs</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the value of the '<em>Max Occurs</em>' attribute.
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlContainerNode_MaxOccurs()
     * @model unique="false" transient="true" changeable="false" volatile="true"
     * @generated
     */
    int getMaxOccurs();

} // XmlContainerNode
