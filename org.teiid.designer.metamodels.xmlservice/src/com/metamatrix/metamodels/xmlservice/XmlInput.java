/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice;


import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Input</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlInput#getOperation <em>Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlInput#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlInput()
 * @model
 * @generated
 */
public interface XmlInput extends XmlMessage, XmlServiceComponent{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getInputs <em>Inputs</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operation</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operation</em>' container reference.
     * @see #setOperation(XmlOperation)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlInput_Operation()
     * @see com.metamatrix.metamodels.xmlservice.XmlOperation#getInputs
     * @model opposite="inputs" required="true"
     * @generated
     */
    XmlOperation getOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlInput#getOperation <em>Operation</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation</em>' container reference.
     * @see #getOperation()
     * @generated
     */
    void setOperation(XmlOperation value);

    /**
     * Returns the value of the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Type</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Type</em>' reference.
     * @see #setType(EObject)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlInput_Type()
     * @model required="true"
     * @generated
     */
    EObject getType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlInput#getType <em>Type</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Type</em>' reference.
     * @see #getType()
     * @generated
     */
    void setType(EObject value);

} // XmlInput
