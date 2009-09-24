/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.metamatrix.metamodels.wsdl.soap.SoapFactory
 * @generated
 */
public interface SoapPackage extends EPackage{
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * The package name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNAME = "soap"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_URI = "http://schemas.xmlsoap.org/wsdl/soap/"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_PREFIX = "soap"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	SoapPackage eINSTANCE = com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapAddressImpl <em>Address</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapAddressImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapAddress()
     * @generated
     */
	int SOAP_ADDRESS = 0;

    /**
     * The feature id for the '<em><b>Port</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_ADDRESS__PORT = 0;

    /**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_ADDRESS__LOCATION = 1;

    /**
     * The number of structural features of the the '<em>Address</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_ADDRESS_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapFaultImpl <em>Fault</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapFaultImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapFault()
     * @generated
     */
	int SOAP_FAULT = 2;

    /**
     * The feature id for the '<em><b>Binding Fault</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_FAULT__BINDING_FAULT = 0;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_FAULT__USE = 1;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_FAULT__NAMESPACE = 2;

    /**
     * The feature id for the '<em><b>Encoding Styles</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_FAULT__ENCODING_STYLES = 3;

    /**
     * The number of structural features of the the '<em>Fault</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_FAULT_FEATURE_COUNT = 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderFaultImpl <em>Header Fault</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderFaultImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapHeaderFault()
     * @generated
     */
	int SOAP_HEADER_FAULT = 1;

    /**
     * The feature id for the '<em><b>Binding Fault</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__BINDING_FAULT = SOAP_FAULT__BINDING_FAULT;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__USE = SOAP_FAULT__USE;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__NAMESPACE = SOAP_FAULT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Encoding Styles</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__ENCODING_STYLES = SOAP_FAULT__ENCODING_STYLES;

    /**
     * The feature id for the '<em><b>Message Part</b></em>' reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__MESSAGE_PART = SOAP_FAULT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Soap Header</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__SOAP_HEADER = SOAP_FAULT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Parts</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__PARTS = SOAP_FAULT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT__MESSAGE = SOAP_FAULT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Header Fault</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FAULT_FEATURE_COUNT = SOAP_FAULT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl <em>Header</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapHeaderImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapHeader()
     * @generated
     */
	int SOAP_HEADER = 3;

    /**
     * The feature id for the '<em><b>Binding Param</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__BINDING_PARAM = 0;

    /**
     * The feature id for the '<em><b>Message Part</b></em>' reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__MESSAGE_PART = 1;

    /**
     * The feature id for the '<em><b>Header Fault</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__HEADER_FAULT = 2;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__USE = 3;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__NAMESPACE = 4;

    /**
     * The feature id for the '<em><b>Encoding Styles</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__ENCODING_STYLES = 5;

    /**
     * The feature id for the '<em><b>Parts</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__PARTS = 6;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER__MESSAGE = 7;

    /**
     * The number of structural features of the the '<em>Header</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_HEADER_FEATURE_COUNT = 8;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl <em>Body</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapBodyImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapBody()
     * @generated
     */
	int SOAP_BODY = 4;

    /**
     * The feature id for the '<em><b>Binding Param</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BODY__BINDING_PARAM = 0;

    /**
     * The feature id for the '<em><b>Use</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BODY__USE = 1;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BODY__NAMESPACE = 2;

    /**
     * The feature id for the '<em><b>Encoding Styles</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BODY__ENCODING_STYLES = 3;

    /**
     * The feature id for the '<em><b>Parts</b></em>' attribute list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BODY__PARTS = 4;

    /**
     * The number of structural features of the the '<em>Body</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BODY_FEATURE_COUNT = 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapOperationImpl <em>Operation</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapOperationImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapOperation()
     * @generated
     */
	int SOAP_OPERATION = 5;

    /**
     * The feature id for the '<em><b>Binding Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_OPERATION__BINDING_OPERATION = 0;

    /**
     * The feature id for the '<em><b>Style</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_OPERATION__STYLE = 1;

    /**
     * The feature id for the '<em><b>Action</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_OPERATION__ACTION = 2;

    /**
     * The number of structural features of the the '<em>Operation</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_OPERATION_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.impl.SoapBindingImpl <em>Binding</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapBindingImpl
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapBinding()
     * @generated
     */
	int SOAP_BINDING = 6;

    /**
     * The feature id for the '<em><b>Binding</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BINDING__BINDING = 0;

    /**
     * The feature id for the '<em><b>Transport</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BINDING__TRANSPORT = 1;

    /**
     * The feature id for the '<em><b>Style</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BINDING__STYLE = 2;

    /**
     * The number of structural features of the the '<em>Binding</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SOAP_BINDING_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.SoapStyleType <em>Style Type</em>}' enum.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.SoapStyleType
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapStyleType()
     * @generated
     */
	int SOAP_STYLE_TYPE = 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.wsdl.soap.SoapUseType <em>Use Type</em>}' enum.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.wsdl.soap.SoapUseType
     * @see com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl#getSoapUseType()
     * @generated
     */
	int SOAP_USE_TYPE = 8;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapAddress <em>Address</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Address</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapAddress
     * @generated
     */
	EClass getSoapAddress();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapAddress#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Location</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapAddress#getLocation()
     * @see #getSoapAddress()
     * @generated
     */
	EAttribute getSoapAddress_Location();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapAddress#getPort <em>Port</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Port</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapAddress#getPort()
     * @see #getSoapAddress()
     * @generated
     */
	EReference getSoapAddress_Port();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault <em>Header Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Header Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault
     * @generated
     */
	EClass getSoapHeaderFault();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getParts <em>Parts</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Parts</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getParts()
     * @see #getSoapHeaderFault()
     * @generated
     */
	EAttribute getSoapHeaderFault_Parts();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessage()
     * @see #getSoapHeaderFault()
     * @generated
     */
	EAttribute getSoapHeaderFault_Message();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessagePart <em>Message Part</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Message Part</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getMessagePart()
     * @see #getSoapHeaderFault()
     * @generated
     */
	EReference getSoapHeaderFault_MessagePart();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getSoapHeader <em>Soap Header</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Soap Header</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault#getSoapHeader()
     * @see #getSoapHeaderFault()
     * @generated
     */
	EReference getSoapHeaderFault_SoapHeader();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault <em>Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault
     * @generated
     */
	EClass getSoapFault();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault#getUse <em>Use</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault#getUse()
     * @see #getSoapFault()
     * @generated
     */
	EAttribute getSoapFault_Use();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault#getNamespace <em>Namespace</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault#getNamespace()
     * @see #getSoapFault()
     * @generated
     */
	EAttribute getSoapFault_Namespace();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault#getEncodingStyles <em>Encoding Styles</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Encoding Styles</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault#getEncodingStyles()
     * @see #getSoapFault()
     * @generated
     */
	EAttribute getSoapFault_EncodingStyles();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapFault#getBindingFault <em>Binding Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapFault#getBindingFault()
     * @see #getSoapFault()
     * @generated
     */
	EReference getSoapFault_BindingFault();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader <em>Header</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Header</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader
     * @generated
     */
	EClass getSoapHeader();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getUse <em>Use</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getUse()
     * @see #getSoapHeader()
     * @generated
     */
	EAttribute getSoapHeader_Use();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getNamespace <em>Namespace</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getNamespace()
     * @see #getSoapHeader()
     * @generated
     */
	EAttribute getSoapHeader_Namespace();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getEncodingStyles <em>Encoding Styles</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Encoding Styles</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getEncodingStyles()
     * @see #getSoapHeader()
     * @generated
     */
	EAttribute getSoapHeader_EncodingStyles();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getParts <em>Parts</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Parts</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getParts()
     * @see #getSoapHeader()
     * @generated
     */
	EAttribute getSoapHeader_Parts();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessage()
     * @see #getSoapHeader()
     * @generated
     */
	EAttribute getSoapHeader_Message();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam <em>Binding Param</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Param</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getBindingParam()
     * @see #getSoapHeader()
     * @generated
     */
	EReference getSoapHeader_BindingParam();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessagePart <em>Message Part</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Message Part</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getMessagePart()
     * @see #getSoapHeader()
     * @generated
     */
	EReference getSoapHeader_MessagePart();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapHeader#getHeaderFault <em>Header Fault</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Header Fault</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapHeader#getHeaderFault()
     * @see #getSoapHeader()
     * @generated
     */
	EReference getSoapHeader_HeaderFault();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody <em>Body</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Body</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody
     * @generated
     */
	EClass getSoapBody();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getUse <em>Use</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Use</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getUse()
     * @see #getSoapBody()
     * @generated
     */
	EAttribute getSoapBody_Use();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getNamespace <em>Namespace</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Namespace</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getNamespace()
     * @see #getSoapBody()
     * @generated
     */
	EAttribute getSoapBody_Namespace();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getEncodingStyles <em>Encoding Styles</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Encoding Styles</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getEncodingStyles()
     * @see #getSoapBody()
     * @generated
     */
	EAttribute getSoapBody_EncodingStyles();

    /**
     * Returns the meta object for the attribute list '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getParts <em>Parts</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Parts</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getParts()
     * @see #getSoapBody()
     * @generated
     */
	EAttribute getSoapBody_Parts();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam <em>Binding Param</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Param</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBody#getBindingParam()
     * @see #getSoapBody()
     * @generated
     */
	EReference getSoapBody_BindingParam();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapOperation
     * @generated
     */
	EClass getSoapOperation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapOperation#getStyle <em>Style</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Style</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapOperation#getStyle()
     * @see #getSoapOperation()
     * @generated
     */
	EAttribute getSoapOperation_Style();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapOperation#getAction <em>Action</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Action</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapOperation#getAction()
     * @see #getSoapOperation()
     * @generated
     */
	EAttribute getSoapOperation_Action();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapOperation#getBindingOperation <em>Binding Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding Operation</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapOperation#getBindingOperation()
     * @see #getSoapOperation()
     * @generated
     */
	EReference getSoapOperation_BindingOperation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBinding
     * @generated
     */
	EClass getSoapBinding();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getTransport <em>Transport</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Transport</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBinding#getTransport()
     * @see #getSoapBinding()
     * @generated
     */
	EAttribute getSoapBinding_Transport();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getStyle <em>Style</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Style</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBinding#getStyle()
     * @see #getSoapBinding()
     * @generated
     */
	EAttribute getSoapBinding_Style();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.wsdl.soap.SoapBinding#getBinding <em>Binding</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Binding</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapBinding#getBinding()
     * @see #getSoapBinding()
     * @generated
     */
	EReference getSoapBinding_Binding();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.wsdl.soap.SoapStyleType <em>Style Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Style Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapStyleType
     * @generated
     */
	EEnum getSoapStyleType();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.wsdl.soap.SoapUseType <em>Use Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Use Type</em>'.
     * @see com.metamatrix.metamodels.wsdl.soap.SoapUseType
     * @generated
     */
	EEnum getSoapUseType();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	SoapFactory getSoapFactory();

} //SoapPackage
