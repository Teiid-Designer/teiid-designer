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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Choice Option</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.ChoiceOption#getChoiceCriteria <em>Choice Criteria</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.ChoiceOption#getChoiceOrder <em>Choice Order</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.ChoiceOption#getDefaultFor <em>Default For</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getChoiceOption()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface ChoiceOption extends EObject{
    /**
     * Returns the value of the '<em><b>Choice Criteria</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Choice Criteria</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Choice Criteria</em>' attribute.
     * @see #setChoiceCriteria(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getChoiceOption_ChoiceCriteria()
     * @model
     * @generated
     */
    String getChoiceCriteria();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.ChoiceOption#getChoiceCriteria <em>Choice Criteria</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Choice Criteria</em>' attribute.
     * @see #getChoiceCriteria()
     * @generated
     */
    void setChoiceCriteria(String value);

    /**
     * Returns the value of the '<em><b>Choice Order</b></em>' attribute.
     * The default value is <code>"-1"</code>.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Choice Order</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * Represents the order that this choice option appears in the choice, with a default value of -1 if the choice value hasn't been set (or doesn't apply).
     * <!-- end-model-doc -->
     * @return the value of the '<em>Choice Order</em>' attribute.
     * @see #setChoiceOrder(int)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getChoiceOption_ChoiceOrder()
     * @model default="-1"
     * @generated
     */
	int getChoiceOrder();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.ChoiceOption#getChoiceOrder <em>Choice Order</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Choice Order</em>' attribute.
     * @see #getChoiceOrder()
     * @generated
     */
	void setChoiceOrder(int value);

    /**
     * Returns the value of the '<em><b>Default For</b></em>' reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xml.XmlChoice#getDefaultOption <em>Default Option</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default For</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default For</em>' reference.
     * @see #setDefaultFor(XmlChoice)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getChoiceOption_DefaultFor()
     * @see com.metamatrix.metamodels.xml.XmlChoice#getDefaultOption
     * @model opposite="defaultOption"
     * @generated
     */
    XmlChoice getDefaultFor();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.ChoiceOption#getDefaultFor <em>Default For</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default For</em>' reference.
     * @see #getDefaultFor()
     * @generated
     */
    void setDefaultFor(XmlChoice value);

} // ChoiceOption
