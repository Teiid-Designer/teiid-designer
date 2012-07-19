/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.metamodels.xml;


import java.util.List;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Xml Choice</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.teiid.designer.metamodels.xml.XmlChoice#getDefaultErrorMode <em>Default Error Mode</em>}</li>
 *   <li>{@link org.teiid.designer.metamodels.xml.XmlChoice#getDefaultOption <em>Default Option</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlChoice()
 * @model
 * @generated
 *
 * @since 8.0
 */
public interface XmlChoice extends XmlContainerNode{
    /**
     * Returns the value of the '<em><b>Default Error Mode</b></em>' attribute.
     * The default value is <code>"THROW"</code>.
     * The literals are from the enumeration {@link org.teiid.designer.metamodels.xml.ChoiceErrorMode}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Error Mode</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Error Mode</em>' attribute.
     * @see org.teiid.designer.metamodels.xml.ChoiceErrorMode
     * @see #setDefaultErrorMode(ChoiceErrorMode)
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlChoice_DefaultErrorMode()
     * @model default="THROW"
     * @generated
     */
    ChoiceErrorMode getDefaultErrorMode();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xml.XmlChoice#getDefaultErrorMode <em>Default Error Mode</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Default Error Mode</em>' attribute.
     * @see org.teiid.designer.metamodels.xml.ChoiceErrorMode
     * @see #getDefaultErrorMode()
     * @generated
     */
    void setDefaultErrorMode(ChoiceErrorMode value);

    /**
     * Returns the value of the '<em><b>Default Option</b></em>' reference.
     * It is bidirectional and its opposite is '{@link org.teiid.designer.metamodels.xml.ChoiceOption#getDefaultFor <em>Default For</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Default Option</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Default Option</em>' reference.
     * @see #setDefaultOption(ChoiceOption)
     * @see org.teiid.designer.metamodels.xml.XmlDocumentPackage#getXmlChoice_DefaultOption()
     * @see org.teiid.designer.metamodels.xml.ChoiceOption#getDefaultFor
     * @model opposite="defaultFor"
     * @generated
     */
    ChoiceOption getDefaultOption();

    /**
     * Sets the value of the '{@link org.teiid.designer.metamodels.xml.XmlChoice#getDefaultOption <em>Default Option</em>}' reference.
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
     * @model dataType="org.teiid.designer.metamodels.xml.List" many="false" parameters=""
     * @generated
     */
	List getOrderedChoiceOptions();

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @model optionsDataType="org.teiid.designer.metamodels.xml.List" optionsMany="false"
     * @generated
     */
	void setOrderedChoiceOptions(List options);

} // XmlChoice
