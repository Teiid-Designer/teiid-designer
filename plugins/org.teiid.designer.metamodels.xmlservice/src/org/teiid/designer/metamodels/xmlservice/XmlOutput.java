/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xmlservice;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Output</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.xmlservice.XmlOutput#getOperation <em>Operation</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.xmlservice.XmlOutput#getResult <em>Result</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.xmlservice.XmlServicePackage#getXmlOutput()
 * @model
 * @generated
 */
public interface XmlOutput extends XmlMessage{
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.xmlservice.XmlOperation#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Operation</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Operation</em>' container reference.
     * @see #setOperation(XmlOperation)
     * @see org.teiid.designer.metamodels.xmlservice.XmlServicePackage#getXmlOutput_Operation()
     * @see org.teiid.designer.metamodels.xmlservice.XmlOperation#getOutput
     * @model opposite="output" required="true"
     * @generated
     */
    XmlOperation getOperation();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xmlservice.XmlOutput#getOperation <em>Operation</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation</em>' container reference.
     * @see #getOperation()
     * @generated
     */
    void setOperation(XmlOperation value);

    /**
     * Returns the value of the '<em><b>Result</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.xmlservice.XmlResult#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Result</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Result</em>' containment reference.
     * @see #setResult(XmlResult)
     * @see org.teiid.designer.metamodels.xmlservice.XmlServicePackage#getXmlOutput_Result()
     * @see org.teiid.designer.metamodels.xmlservice.XmlResult#getOutput
     * @model opposite="output" containment="true"
     * @generated
     */
    XmlResult getResult();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xmlservice.XmlOutput#getResult <em>Result</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Result</em>' containment reference.
     * @see #getResult()
     * @generated
     */
    void setResult(XmlResult value);

} // XmlOutput
