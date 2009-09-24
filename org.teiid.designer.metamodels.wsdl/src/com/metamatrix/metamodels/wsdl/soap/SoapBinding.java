/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap;

import com.metamatrix.metamodels.wsdl.Binding;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binding</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getBinding <em>Binding</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getTransport <em>Transport</em>}</li>
 *   <li>{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getStyle <em>Style</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBinding()
 * @model
 * @generated
 */
public interface SoapBinding extends EObject{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * Returns the value of the '<em><b>Transport</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transport</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Transport</em>' attribute.
     * @see #setTransport(String)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBinding_Transport()
     * @model
     * @generated
     */
	String getTransport();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getTransport <em>Transport</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Transport</em>' attribute.
     * @see #getTransport()
     * @generated
     */
	void setTransport(String value);

    /**
     * Returns the value of the '<em><b>Style</b></em>' attribute.
     * The default value is <code>"DOCUMENT"</code>.
     * The literals are from the enumeration {@link com.metamatrix.metamodels.wsdl.soap.SoapStyleType}.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Style</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Style</em>' attribute.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapStyleType
     * @see #setStyle(SoapStyleType)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBinding_Style()
     * @model default="DOCUMENT"
     * @generated
     */
	SoapStyleType getStyle();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getStyle <em>Style</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Style</em>' attribute.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapStyleType
     * @see #getStyle()
     * @generated
     */
	void setStyle(SoapStyleType value);

    /**
     * Returns the value of the '<em><b>Binding</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link com.metamatrix.metamodels.wsdl.Binding#getSoapBinding <em>Soap Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Binding</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Binding</em>' container reference.
     * @see #setBinding(Binding)
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#getSoapBinding_Binding()
     * @see com.metamatrix.metamodels.wsdl.Binding#getSoapBinding
     * @model opposite="soapBinding" required="true"
     * @generated
     */
	Binding getBinding();

    /**
     * Sets the value of the '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getBinding <em>Binding</em>}' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Binding</em>' container reference.
     * @see #getBinding()
     * @generated
     */
	void setBinding(Binding value);

} // SoapBinding
