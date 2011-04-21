/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>XmlDocument</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocument#getEncoding <em>Encoding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocument#isFormatted <em>Formatted</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocument#getVersion <em>Version</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocument#isStandalone <em>Standalone</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlDocument#getSoapEncoding <em>Soap Encoding</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocument()
 * @model
 * @generated
 */
public interface XmlDocument extends XmlFragment{
    /**
     * Returns the value of the '<em><b>Encoding</b></em>' attribute.
     * The default value is <code>"UTF-8"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Encoding</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Encoding</em>' attribute.
     * @see #setEncoding(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocument_Encoding()
     * @model default="UTF-8"
     * @generated
     */
    String getEncoding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocument#getEncoding <em>Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Encoding</em>' attribute.
     * @see #getEncoding()
     * @generated
     */
    void setEncoding(String value);

    /**
     * Returns the value of the '<em><b>Formatted</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Formatted</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Formatted</em>' attribute.
     * @see #setFormatted(boolean)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocument_Formatted()
     * @model default="false"
     * @generated
     */
    boolean isFormatted();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocument#isFormatted <em>Formatted</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Formatted</em>' attribute.
     * @see #isFormatted()
     * @generated
     */
    void setFormatted(boolean value);

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * The default value is <code>"1.0"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Version</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocument_Version()
     * @model default="1.0"
     * @generated
     */
    String getVersion();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocument#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
    void setVersion(String value);

    /**
     * Returns the value of the '<em><b>Standalone</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Standalone</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Standalone</em>' attribute.
     * @see #setStandalone(boolean)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocument_Standalone()
     * @model default="false"
     * @generated
     */
    boolean isStandalone();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocument#isStandalone <em>Standalone</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Standalone</em>' attribute.
     * @see #isStandalone()
     * @generated
     */
    void setStandalone(boolean value);

    /**
     * Returns the value of the '<em><b>Soap Encoding</b></em>' attribute.
     * The default value is <code>"NONE"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.xml.SoapEncoding}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Soap Encoding</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Soap Encoding</em>' attribute.
     * @see com.metamatrix.metamodels.xml.SoapEncoding
     * @see #setSoapEncoding(SoapEncoding)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlDocument_SoapEncoding()
     * @model default="NONE"
     * @generated
     */
    SoapEncoding getSoapEncoding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlDocument#getSoapEncoding <em>Soap Encoding</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Soap Encoding</em>' attribute.
     * @see com.metamatrix.metamodels.xml.SoapEncoding
     * @see #getSoapEncoding()
     * @generated
     */
    void setSoapEncoding(SoapEncoding value);

} // XmlDocument
