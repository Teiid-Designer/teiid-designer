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
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Value Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlValueHolder#getValue <em>Value</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.xml.XmlValueHolder#getValueType <em>Value Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlValueHolder()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface XmlValueHolder extends EObject{
    /**
     * Returns the value of the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value</em>' attribute.
     * @see #setValue(String)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlValueHolder_Value()
     * @model
     * @generated
     */
    String getValue();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlValueHolder#getValue <em>Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value</em>' attribute.
     * @see #getValue()
     * @generated
     */
    void setValue(String value);

    /**
     * Returns the value of the '<em><b>Value Type</b></em>' attribute.
     * The default value is <code>"IGNORED"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.xml.ValueType}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Value Type</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Value Type</em>' attribute.
     * @see com.metamatrix.metamodels.xml.ValueType
     * @see #setValueType(ValueType)
     * @see com.metamatrix.metamodels.xml.XmlDocumentPackage#getXmlValueHolder_ValueType()
     * @model default="IGNORED"
     * @generated
     */
    ValueType getValueType();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.xml.XmlValueHolder#getValueType <em>Value Type</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Value Type</em>' attribute.
     * @see com.metamatrix.metamodels.xml.ValueType
     * @see #getValueType()
     * @generated
     */
    void setValueType(ValueType value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    boolean isValueFixed();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model parameters=""
     * @generated
     */
    boolean isValueDefault();

} // XmlValueHolder
