/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import com.metamatrix.metamodels.xml.XmlDocument;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Output</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.webservice.Output#getOperation <em>Operation</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.webservice.Output#getXmlDocument <em>Xml Document</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOutput()
 * @model
 * @generated
 */
public interface Output extends Message{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Operation</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.webservice.Operation#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Operation</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Operation</em>' container reference.
     * @see #setOperation(Operation)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOutput_Operation()
     * @see com.metamatrix.metamodels.webservice.Operation#getOutput
     * @model opposite="output" required="true"
     * @generated
     */
	Operation getOperation();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Output#getOperation <em>Operation</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Operation</em>' container reference.
     * @see #getOperation()
     * @generated
     */
	void setOperation(Operation value);

    /**
     * Returns the value of the '<em><b>Xml Document</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Xml Document</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Xml Document</em>' reference.
     * @see #setXmlDocument(XmlDocument)
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#getOutput_XmlDocument()
     * @model required="true"
     * @generated
     */
	XmlDocument getXmlDocument();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.webservice.Output#getXmlDocument <em>Xml Document</em>}' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Xml Document</em>' reference.
     * @see #getXmlDocument()
     * @generated
     */
	void setXmlDocument(XmlDocument value);

} // Output
