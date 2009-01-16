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


import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Choice</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultErrorMode <em>Default Error Mode</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultOption <em>Default Option</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlChoice()
 * @model
 * @generated
 */
public interface XmlChoice extends XmlContainerNode{
    /**
     * Returns the value of the '<em><b>Default Error Mode</b></em>' attribute.
     * The default value is <code>"THROW"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.xml.ChoiceErrorMode}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Error Mode</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Error Mode</em>' attribute.
     * @see com.metamatrix.metamodels.xml.ChoiceErrorMode
     * @see #setDefaultErrorMode(ChoiceErrorMode)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlChoice_DefaultErrorMode()
     * @model default="THROW"
     * @generated
     */
    ChoiceErrorMode getDefaultErrorMode();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultErrorMode <em>Default Error Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Error Mode</em>' attribute.
     * @see com.metamatrix.metamodels.xml.ChoiceErrorMode
     * @see #getDefaultErrorMode()
     * @generated
     */
    void setDefaultErrorMode(ChoiceErrorMode value);

    /**
     * Returns the value of the '<em><b>Default Option</b></em>' reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.ChoiceOption#getDefaultFor <em>Default For</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Option</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Option</em>' reference.
     * @see #setDefaultOption(ChoiceOption)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlChoice_DefaultOption()
     * @see com.metamatrix.metamodels.xml.ChoiceOption#getDefaultFor
     * @model opposite="defaultFor"
     * @generated
     */
    ChoiceOption getDefaultOption();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultOption <em>Default Option</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Option</em>' reference.
     * @see #getDefaultOption()
     * @generated
     */
    void setDefaultOption(ChoiceOption value);

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @model dataType="com.metamatrix.metamodels.xml.List" many="false" parameters=""
     * @generated
     */
	List getOrderedChoiceOptions();

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @model optionsDataType="com.metamatrix.metamodels.xml.List" optionsMany="false"
     * @generated
     */
	void setOrderedChoiceOptions(List options);

} // XmlChoice
