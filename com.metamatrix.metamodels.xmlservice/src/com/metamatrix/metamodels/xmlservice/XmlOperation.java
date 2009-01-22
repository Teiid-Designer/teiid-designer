/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice;


import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Operation</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getInputs <em>Inputs</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getOutput <em>Output</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlOperation()
 * @model
 * @generated
 */
public interface XmlOperation extends XmlServiceComponent{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Inputs</b></em>' containment reference list.
     * The list contents are of type {@link com.metamatrix.metamodels.xmlservice.XmlInput}.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xmlservice.XmlInput#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Inputs</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Inputs</em>' containment reference list.
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlOperation_Inputs()
     * @see com.metamatrix.metamodels.xmlservice.XmlInput#getOperation
     * @model type="com.metamatrix.metamodels.xmlservice.XmlInput" opposite="operation" containment="true"
     * @generated
     */
    EList getInputs();

    /**
     * Returns the value of the '<em><b>Output</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.xmlservice.XmlOutput#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Output</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Output</em>' containment reference.
     * @see #setOutput(XmlOutput)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlOperation_Output()
     * @see com.metamatrix.metamodels.xmlservice.XmlOutput#getOperation
     * @model opposite="operation" containment="true"
     * @generated
     */
    XmlOutput getOutput();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getOutput <em>Output</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Output</em>' containment reference.
     * @see #getOutput()
     * @generated
     */
    void setOutput(XmlOutput value);

    /**
     * Returns the value of the '<em><b>Update Count</b></em>' attribute.
     * The default value is <code>"AUTO"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.xmlservice.OperationUpdateCount}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Update Count</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Update Count</em>' attribute.
     * @see com.metamatrix.metamodels.xmlservice.OperationUpdateCount
     * @see #setUpdateCount(OperationUpdateCount)
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#getXmlOperation_UpdateCount()
     * @model default="AUTO"
     * @generated
     */
    OperationUpdateCount getUpdateCount();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getUpdateCount <em>Update Count</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Update Count</em>' attribute.
     * @see com.metamatrix.metamodels.xmlservice.OperationUpdateCount
     * @see #getUpdateCount()
     * @generated
     */
    void setUpdateCount(OperationUpdateCount value);

} // XmlOperation
