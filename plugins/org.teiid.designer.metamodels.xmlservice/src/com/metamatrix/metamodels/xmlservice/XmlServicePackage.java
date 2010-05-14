/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice;

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
 * @see com.metamatrix.metamodels.xmlservice.XmlServiceFactory
 * @model kind="package"
 * @generated
 */
public interface XmlServicePackage extends EPackage{
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
    String eNAME = "xmlservice"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/XmlService"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "mmxs"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    XmlServicePackage eINSTANCE = com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.impl.XmlServiceComponentImpl <em>Component</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServiceComponentImpl
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlServiceComponent()
     * @generated
     */
    int XML_SERVICE_COMPONENT = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SERVICE_COMPONENT__NAME = 0;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SERVICE_COMPONENT__NAME_IN_SOURCE = 1;

    /**
     * The number of structural features of the the '<em>Component</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_SERVICE_COMPONENT_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.impl.XmlOperationImpl <em>Xml Operation</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlOperationImpl
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlOperation()
     * @generated
     */
    int XML_OPERATION = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OPERATION__NAME = XML_SERVICE_COMPONENT__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OPERATION__NAME_IN_SOURCE = XML_SERVICE_COMPONENT__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Inputs</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OPERATION__INPUTS = XML_SERVICE_COMPONENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Output</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OPERATION__OUTPUT = XML_SERVICE_COMPONENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Update Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OPERATION__UPDATE_COUNT = XML_SERVICE_COMPONENT_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Xml Operation</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OPERATION_FEATURE_COUNT = XML_SERVICE_COMPONENT_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.impl.XmlMessageImpl <em>Xml Message</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlMessageImpl
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlMessage()
     * @generated
     */
    int XML_MESSAGE = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_MESSAGE__NAME = XML_SERVICE_COMPONENT__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_MESSAGE__NAME_IN_SOURCE = XML_SERVICE_COMPONENT__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_MESSAGE__CONTENT_ELEMENT = XML_SERVICE_COMPONENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Xml Message</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_MESSAGE_FEATURE_COUNT = XML_SERVICE_COMPONENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.impl.XmlInputImpl <em>Xml Input</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlInputImpl
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlInput()
     * @generated
     */
    int XML_INPUT = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_INPUT__NAME = XML_MESSAGE__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_INPUT__NAME_IN_SOURCE = XML_MESSAGE__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_INPUT__CONTENT_ELEMENT = XML_MESSAGE__CONTENT_ELEMENT;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_INPUT__OPERATION = XML_MESSAGE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_INPUT__TYPE = XML_MESSAGE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Xml Input</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_INPUT_FEATURE_COUNT = XML_MESSAGE_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.impl.XmlOutputImpl <em>Xml Output</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlOutputImpl
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlOutput()
     * @generated
     */
    int XML_OUTPUT = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OUTPUT__NAME = XML_MESSAGE__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OUTPUT__NAME_IN_SOURCE = XML_MESSAGE__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Content Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OUTPUT__CONTENT_ELEMENT = XML_MESSAGE__CONTENT_ELEMENT;

    /**
     * The feature id for the '<em><b>Operation</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OUTPUT__OPERATION = XML_MESSAGE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Result</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OUTPUT__RESULT = XML_MESSAGE_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Xml Output</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_OUTPUT_FEATURE_COUNT = XML_MESSAGE_FEATURE_COUNT + 2;


    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.impl.XmlResultImpl <em>Xml Result</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlResultImpl
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlResult()
     * @generated
     */
    int XML_RESULT = 5;

    /**
     * The feature id for the '<em><b>Output</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_RESULT__OUTPUT = 0;

    /**
     * The number of structural features of the the '<em>Xml Result</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int XML_RESULT_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.xmlservice.OperationUpdateCount <em>Operation Update Count</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.xmlservice.OperationUpdateCount
     * @see com.metamatrix.metamodels.xmlservice.impl.XmlServicePackageImpl#getXmlOperationUpdateCount()
     * @generated
     */
    int OPERATION_UPDATE_COUNT = 6;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xmlservice.XmlOperation <em>Xml Operation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Operation</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOperation
     * @generated
     */
    EClass getXmlOperation();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getInputs <em>Inputs</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Inputs</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOperation#getInputs()
     * @see #getXmlOperation()
     * @generated
     */
    EReference getXmlOperation_Inputs();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Output</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOperation#getOutput()
     * @see #getXmlOperation()
     * @generated
     */
    EReference getXmlOperation_Output();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xmlservice.XmlOperation#getUpdateCount <em>Update Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Update Count</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOperation#getUpdateCount()
     * @see #getXmlOperation()
     * @generated
     */
    EAttribute getXmlOperation_UpdateCount();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xmlservice.XmlInput <em>Xml Input</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Input</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlInput
     * @generated
     */
    EClass getXmlInput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xmlservice.XmlInput#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlInput#getOperation()
     * @see #getXmlInput()
     * @generated
     */
    EReference getXmlInput_Operation();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xmlservice.XmlInput#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlInput#getType()
     * @see #getXmlInput()
     * @generated
     */
    EReference getXmlInput_Type();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent <em>Component</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Component</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlServiceComponent
     * @generated
     */
    EClass getXmlServiceComponent();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getName()
     * @see #getXmlServiceComponent()
     * @generated
     */
    EAttribute getXmlServiceComponent_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getNameInSource <em>Name In Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name In Source</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlServiceComponent#getNameInSource()
     * @see #getXmlServiceComponent()
     * @generated
     */
    EAttribute getXmlServiceComponent_NameInSource();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xmlservice.XmlOutput <em>Xml Output</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Output</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOutput
     * @generated
     */
    EClass getXmlOutput();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xmlservice.XmlOutput#getOperation <em>Operation</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Operation</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOutput#getOperation()
     * @see #getXmlOutput()
     * @generated
     */
    EReference getXmlOutput_Operation();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.xmlservice.XmlOutput#getResult <em>Result</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Result</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlOutput#getResult()
     * @see #getXmlOutput()
     * @generated
     */
    EReference getXmlOutput_Result();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xmlservice.XmlMessage <em>Xml Message</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Message</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlMessage
     * @generated
     */
    EClass getXmlMessage();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.xmlservice.XmlMessage#getContentElement <em>Content Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Content Element</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlMessage#getContentElement()
     * @see #getXmlMessage()
     * @generated
     */
    EReference getXmlMessage_ContentElement();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.xmlservice.XmlResult <em>Xml Result</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Xml Result</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlResult
     * @generated
     */
    EClass getXmlResult();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.xmlservice.XmlResult#getOutput <em>Output</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Output</em>'.
     * @see com.metamatrix.metamodels.xmlservice.XmlResult#getOutput()
     * @see #getXmlResult()
     * @generated
     */
    EReference getXmlResult_Output();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.xmlservice.OperationUpdateCount <em>Operation Update Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Operation Update Count</em>'.
     * @see com.metamatrix.metamodels.xmlservice.OperationUpdateCount
     * @generated
     */
    EEnum getOperationUpdateCount();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    XmlServiceFactory getXmlServiceFactory();

} //XmlServicePackage
