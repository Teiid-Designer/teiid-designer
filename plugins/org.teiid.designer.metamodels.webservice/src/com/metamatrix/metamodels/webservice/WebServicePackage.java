/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see com.metamatrix.metamodels.webservice.WebServiceFactory
 * @generated
 */
public interface WebServicePackage extends EPackage{
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
	String eNAME = "webservice"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_URI = "http://www.metamatrix.com/metamodels/WebService"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	String eNS_PREFIX = "mmws"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	WebServicePackage eINSTANCE = com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.WebServiceComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.WebServiceComponentImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getWebServiceComponent()
     * @generated
     */
	int WEB_SERVICE_COMPONENT = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WEB_SERVICE_COMPONENT__NAME = 0;

    /**
     * The number of structural features of the the '<em>Component</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WEB_SERVICE_COMPONENT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.OperationImpl <em>Operation</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.OperationImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getOperation()
     * @generated
     */
	int OPERATION = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__NAME = WEB_SERVICE_COMPONENT__NAME;

    /**
     * The feature id for the '<em><b>Pattern</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__PATTERN = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Safe</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__SAFE = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Input</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__INPUT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Output</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__OUTPUT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Interface</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION__INTERFACE = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 4;
	
	/**
     * The feature id for the '<em><b>Update Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int OPERATION__UPDATE_COUNT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 5;

    /**
     * The number of structural features of the the '<em>Operation</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OPERATION_FEATURE_COUNT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 6;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.MessageImpl <em>Message</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.MessageImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getMessage()
     * @generated
     */
	int MESSAGE = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__NAME = WEB_SERVICE_COMPONENT__NAME;

    /**
     * The feature id for the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__CONTENT_ELEMENT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Samples</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__SAMPLES = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Content Complex Type</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__CONTENT_COMPLEX_TYPE = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Content Simple Type</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE__CONTENT_SIMPLE_TYPE = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the the '<em>Message</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int MESSAGE_FEATURE_COUNT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.InputImpl <em>Input</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.InputImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getInput()
     * @generated
     */
	int INPUT = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__NAME = MESSAGE__NAME;

    /**
     * The feature id for the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__CONTENT_ELEMENT = MESSAGE__CONTENT_ELEMENT;

    /**
     * The feature id for the '<em><b>Samples</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__SAMPLES = MESSAGE__SAMPLES;

    /**
     * The feature id for the '<em><b>Content Complex Type</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__CONTENT_COMPLEX_TYPE = MESSAGE__CONTENT_COMPLEX_TYPE;

    /**
     * The feature id for the '<em><b>Content Simple Type</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__CONTENT_SIMPLE_TYPE = MESSAGE__CONTENT_SIMPLE_TYPE;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT__OPERATION = MESSAGE_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Input</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INPUT_FEATURE_COUNT = MESSAGE_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.OutputImpl <em>Output</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.OutputImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getOutput()
     * @generated
     */
	int OUTPUT = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__NAME = MESSAGE__NAME;

    /**
     * The feature id for the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__CONTENT_ELEMENT = MESSAGE__CONTENT_ELEMENT;

    /**
     * The feature id for the '<em><b>Samples</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__SAMPLES = MESSAGE__SAMPLES;

    /**
     * The feature id for the '<em><b>Content Complex Type</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__CONTENT_COMPLEX_TYPE = MESSAGE__CONTENT_COMPLEX_TYPE;

    /**
     * The feature id for the '<em><b>Content Simple Type</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__CONTENT_SIMPLE_TYPE = MESSAGE__CONTENT_SIMPLE_TYPE;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__OPERATION = MESSAGE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Xml Document</b></em>' reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT__XML_DOCUMENT = MESSAGE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Output</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int OUTPUT_FEATURE_COUNT = MESSAGE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.InterfaceImpl <em>Interface</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.InterfaceImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getInterface()
     * @generated
     */
	int INTERFACE = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INTERFACE__NAME = WEB_SERVICE_COMPONENT__NAME;

    /**
     * The feature id for the '<em><b>Operations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INTERFACE__OPERATIONS = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Interface</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INTERFACE_FEATURE_COUNT = WEB_SERVICE_COMPONENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.SampleMessagesImpl <em>Sample Messages</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.SampleMessagesImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getSampleMessages()
     * @generated
     */
	int SAMPLE_MESSAGES = 6;

    /**
     * The feature id for the '<em><b>Message</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_MESSAGES__MESSAGE = 0;

    /**
     * The feature id for the '<em><b>Sample Files</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_MESSAGES__SAMPLE_FILES = 1;

    /**
     * The feature id for the '<em><b>Sample From Xsd</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_MESSAGES__SAMPLE_FROM_XSD = 2;

    /**
     * The number of structural features of the the '<em>Sample Messages</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_MESSAGES_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.SampleFileImpl <em>Sample File</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.SampleFileImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getSampleFile()
     * @generated
     */
	int SAMPLE_FILE = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FILE__NAME = 0;

    /**
     * The feature id for the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FILE__URL = 1;

    /**
     * The feature id for the '<em><b>Sample Messages</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FILE__SAMPLE_MESSAGES = 2;

    /**
     * The number of structural features of the the '<em>Sample File</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FILE_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.impl.SampleFromXsdImpl <em>Sample From Xsd</em>}' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.impl.SampleFromXsdImpl
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getSampleFromXsd()
     * @generated
     */
	int SAMPLE_FROM_XSD = 8;

    /**
     * The feature id for the '<em><b>Max Number Of Levels To Build</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD = 0;

    /**
     * The feature id for the '<em><b>Sample Fragment</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FROM_XSD__SAMPLE_FRAGMENT = 1;

    /**
     * The feature id for the '<em><b>Sample Messages</b></em>' container reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FROM_XSD__SAMPLE_MESSAGES = 2;

    /**
     * The number of structural features of the the '<em>Sample From Xsd</em>' class.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SAMPLE_FROM_XSD_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '<em>IStatus</em>' data type.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see org.eclipse.core.runtime.IStatus
     * @see com.metamatrix.metamodels.webservice.impl.WebServicePackageImpl#getIStatus()
     * @generated
     */
	int ISTATUS = 9;
		
    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.webservice.OperationUpdateCount <em>Operation Update Count</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.webservice.OperationUpdateCount
     * @see com.metamatrix.metamodels.webservice.impl.OperationPackageImpl#getOperationUpdateCount()
     * @generated
     */
    int OPERATION_UPDATE_COUNT = 10;

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.Operation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation
     * @generated
     */
	EClass getOperation();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.Operation#getPattern <em>Pattern</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Pattern</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation#getPattern()
     * @see #getOperation()
     * @generated
     */
	EAttribute getOperation_Pattern();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.Operation#isSafe <em>Safe</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Safe</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation#isSafe()
     * @see #getOperation()
     * @generated
     */
	EAttribute getOperation_Safe();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.webservice.Operation#getInput <em>Input</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Input</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation#getInput()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_Input();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.webservice.Operation#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Output</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation#getOutput()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_Output();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.webservice.Operation#getInterface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Interface</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation#getInterface()
     * @see #getOperation()
     * @generated
     */
	EReference getOperation_Interface();

	/**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.Operation#getUpdateCount() <em>UpdateCount</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>UpdateCount</em>'.
     * @see com.metamatrix.metamodels.webservice.Operation#getUpdateCount()
     * @see #getOperation()
     * @generated
     */
    EAttribute getOperation_UpdateCount();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.Message <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Message</em>'.
     * @see com.metamatrix.metamodels.webservice.Message
     * @generated
     */
	EClass getMessage();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.webservice.Message#getContentElement <em>Content Element</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Content Element</em>'.
     * @see com.metamatrix.metamodels.webservice.Message#getContentElement()
     * @see #getMessage()
     * @generated
     */
	EReference getMessage_ContentElement();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.webservice.Message#getSamples <em>Samples</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Samples</em>'.
     * @see com.metamatrix.metamodels.webservice.Message#getSamples()
     * @see #getMessage()
     * @generated
     */
	EReference getMessage_Samples();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.webservice.Message#getContentComplexType <em>Content Complex Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Content Complex Type</em>'.
     * @see com.metamatrix.metamodels.webservice.Message#getContentComplexType()
     * @see #getMessage()
     * @generated
     */
	EReference getMessage_ContentComplexType();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.webservice.Message#getContentSimpleType <em>Content Simple Type</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Content Simple Type</em>'.
     * @see com.metamatrix.metamodels.webservice.Message#getContentSimpleType()
     * @see #getMessage()
     * @generated
     */
	EReference getMessage_ContentSimpleType();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.WebServiceComponent <em>Component</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Component</em>'.
     * @see com.metamatrix.metamodels.webservice.WebServiceComponent
     * @generated
     */
	EClass getWebServiceComponent();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.WebServiceComponent#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.webservice.WebServiceComponent#getName()
     * @see #getWebServiceComponent()
     * @generated
     */
	EAttribute getWebServiceComponent_Name();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.Input <em>Input</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Input</em>'.
     * @see com.metamatrix.metamodels.webservice.Input
     * @generated
     */
	EClass getInput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.webservice.Input#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.webservice.Input#getOperation()
     * @see #getInput()
     * @generated
     */
	EReference getInput_Operation();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.Output <em>Output</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Output</em>'.
     * @see com.metamatrix.metamodels.webservice.Output
     * @generated
     */
	EClass getOutput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.webservice.Output#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.webservice.Output#getOperation()
     * @see #getOutput()
     * @generated
     */
	EReference getOutput_Operation();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.webservice.Output#getXmlDocument <em>Xml Document</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Xml Document</em>'.
     * @see com.metamatrix.metamodels.webservice.Output#getXmlDocument()
     * @see #getOutput()
     * @generated
     */
	EReference getOutput_XmlDocument();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.Interface <em>Interface</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Interface</em>'.
     * @see com.metamatrix.metamodels.webservice.Interface
     * @generated
     */
	EClass getInterface();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.webservice.Interface#getOperations <em>Operations</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Operations</em>'.
     * @see com.metamatrix.metamodels.webservice.Interface#getOperations()
     * @see #getInterface()
     * @generated
     */
	EReference getInterface_Operations();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.SampleMessages <em>Sample Messages</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sample Messages</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleMessages
     * @generated
     */
	EClass getSampleMessages();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.webservice.SampleMessages#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Message</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleMessages#getMessage()
     * @see #getSampleMessages()
     * @generated
     */
	EReference getSampleMessages_Message();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.webservice.SampleMessages#getSampleFiles <em>Sample Files</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Sample Files</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleMessages#getSampleFiles()
     * @see #getSampleMessages()
     * @generated
     */
	EReference getSampleMessages_SampleFiles();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.webservice.SampleMessages#getSampleFromXsd <em>Sample From Xsd</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Sample From Xsd</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleMessages#getSampleFromXsd()
     * @see #getSampleMessages()
     * @generated
     */
	EReference getSampleMessages_SampleFromXsd();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.SampleFile <em>Sample File</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sample File</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFile
     * @generated
     */
	EClass getSampleFile();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.SampleFile#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFile#getName()
     * @see #getSampleFile()
     * @generated
     */
	EAttribute getSampleFile_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.SampleFile#getUrl <em>Url</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Url</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFile#getUrl()
     * @see #getSampleFile()
     * @generated
     */
	EAttribute getSampleFile_Url();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.webservice.SampleFile#getSampleMessages <em>Sample Messages</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Sample Messages</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFile#getSampleMessages()
     * @see #getSampleFile()
     * @generated
     */
	EReference getSampleFile_SampleMessages();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.webservice.SampleFromXsd <em>Sample From Xsd</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sample From Xsd</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFromXsd
     * @generated
     */
	EClass getSampleFromXsd();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getMaxNumberOfLevelsToBuild <em>Max Number Of Levels To Build</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Max Number Of Levels To Build</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFromXsd#getMaxNumberOfLevelsToBuild()
     * @see #getSampleFromXsd()
     * @generated
     */
	EAttribute getSampleFromXsd_MaxNumberOfLevelsToBuild();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleFragment <em>Sample Fragment</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Sample Fragment</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleFragment()
     * @see #getSampleFromXsd()
     * @generated
     */
	EReference getSampleFromXsd_SampleFragment();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleMessages <em>Sample Messages</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Sample Messages</em>'.
     * @see com.metamatrix.metamodels.webservice.SampleFromXsd#getSampleMessages()
     * @see #getSampleFromXsd()
     * @generated
     */
	EReference getSampleFromXsd_SampleMessages();

    /**
     * Returns the meta object for data type '{@link org.eclipse.core.runtime.IStatus <em>IStatus</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for data type '<em>IStatus</em>'.
     * @see org.eclipse.core.runtime.IStatus
     * @model instanceClass="org.eclipse.core.runtime.IStatus"
     * @generated
     */
	EDataType getIStatus();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
	WebServiceFactory getWebServiceFactory();

} //WebServicePackage
