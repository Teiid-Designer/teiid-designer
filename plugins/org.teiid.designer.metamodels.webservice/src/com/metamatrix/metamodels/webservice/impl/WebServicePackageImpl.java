/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.webservice.impl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.impl.XSDPackageImpl;

import com.metamatrix.metamodels.webservice.Input;
import com.metamatrix.metamodels.webservice.Interface;
import com.metamatrix.metamodels.webservice.Message;
import com.metamatrix.metamodels.webservice.Operation;
import com.metamatrix.metamodels.webservice.OperationUpdateCount;
import com.metamatrix.metamodels.webservice.Output;
import com.metamatrix.metamodels.webservice.SampleFile;
import com.metamatrix.metamodels.webservice.SampleFromXsd;
import com.metamatrix.metamodels.webservice.SampleMessages;
import com.metamatrix.metamodels.webservice.WebServiceComponent;
import com.metamatrix.metamodels.webservice.WebServiceFactory;
import com.metamatrix.metamodels.webservice.WebServicePackage;
import com.metamatrix.metamodels.xml.XmlDocumentPackage;
import com.metamatrix.metamodels.xml.impl.XmlDocumentPackageImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * 
 * @generated
 */
public class WebServicePackageImpl extends EPackageImpl implements WebServicePackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass operationEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass messageEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass webServiceComponentEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass inputEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass outputEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass interfaceEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sampleMessagesEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sampleFileEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EClass sampleFromXsdEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private EDataType iStatusEDataType = null;
    
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum operationUpdateCountEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
     * EPackage.Registry} by the package package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package, if one already exists. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.webservice.WebServicePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private WebServicePackageImpl() {
        super(eNS_URI, WebServiceFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends. Simple
     * dependencies are satisfied by calling this method on all dependent packages before doing anything else. This method drives
     * initialization for interdependent packages directly, in parallel with this package, itself.
     * <p>
     * Of this package and its interdependencies, all packages which have not yet been registered by their URI values are first
     * created and registered. The packages are then initialized in two steps: meta-model objects for all of the packages are
     * created before any are initialized, since one package's meta-model objects may refer to those of another.
     * <p>
     * Invocation of this method will not affect any packages that have already been initialized. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static WebServicePackage init() {
        if (isInited) return (WebServicePackage)EPackage.Registry.INSTANCE.getEPackage(WebServicePackage.eNS_URI);

        // Obtain or create and register package
        WebServicePackageImpl theWebServicePackage = (WebServicePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof WebServicePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new WebServicePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        XmlDocumentPackageImpl.init();
        XSDPackageImpl.init();

        // Create package meta-data objects
        theWebServicePackage.createPackageContents();

        // Initialize created meta-data
        theWebServicePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theWebServicePackage.freeze();

        return theWebServicePackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getOperation() {
        return operationEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getOperation_Pattern() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getOperation_Safe() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getOperation_Input() {
        return (EReference)operationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getOperation_Output() {
        return (EReference)operationEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getOperation_Interface() {
        return (EReference)operationEClass.getEStructuralFeatures().get(4);
    }
    
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getOperation_UpdateCount() {
        return (EAttribute)operationEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getMessage() {
        return messageEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMessage_ContentElement() {
        return (EReference)messageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMessage_Samples() {
        return (EReference)messageEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMessage_ContentComplexType() {
        return (EReference)messageEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getMessage_ContentSimpleType() {
        return (EReference)messageEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getWebServiceComponent() {
        return webServiceComponentEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getWebServiceComponent_Name() {
        return (EAttribute)webServiceComponentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getInput() {
        return inputEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInput_Operation() {
        return (EReference)inputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getOutput() {
        return outputEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getOutput_Operation() {
        return (EReference)outputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getOutput_XmlDocument() {
        return (EReference)outputEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getInterface() {
        return interfaceEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getInterface_Operations() {
        return (EReference)interfaceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSampleMessages() {
        return sampleMessagesEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSampleMessages_Message() {
        return (EReference)sampleMessagesEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSampleMessages_SampleFiles() {
        return (EReference)sampleMessagesEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSampleMessages_SampleFromXsd() {
        return (EReference)sampleMessagesEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSampleFile() {
        return sampleFileEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSampleFile_Name() {
        return (EAttribute)sampleFileEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSampleFile_Url() {
        return (EAttribute)sampleFileEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSampleFile_SampleMessages() {
        return (EReference)sampleFileEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EClass getSampleFromXsd() {
        return sampleFromXsdEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EAttribute getSampleFromXsd_MaxNumberOfLevelsToBuild() {
        return (EAttribute)sampleFromXsdEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSampleFromXsd_SampleFragment() {
        return (EReference)sampleFromXsdEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EReference getSampleFromXsd_SampleMessages() {
        return (EReference)sampleFromXsdEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getOperationUpdateCount() {
        return operationUpdateCountEEnum;
    }
    
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EDataType getIStatus() {
        return iStatusEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public WebServiceFactory getWebServiceFactory() {
        return (WebServiceFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package. This method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        operationEClass = createEClass(OPERATION);
        createEAttribute(operationEClass, OPERATION__PATTERN);
        createEAttribute(operationEClass, OPERATION__SAFE);
        createEReference(operationEClass, OPERATION__INPUT);
        createEReference(operationEClass, OPERATION__OUTPUT);
        createEReference(operationEClass, OPERATION__INTERFACE);
        createEAttribute(operationEClass, OPERATION__UPDATE_COUNT);

        messageEClass = createEClass(MESSAGE);
        createEReference(messageEClass, MESSAGE__CONTENT_ELEMENT);
        createEReference(messageEClass, MESSAGE__SAMPLES);
        createEReference(messageEClass, MESSAGE__CONTENT_COMPLEX_TYPE);
        createEReference(messageEClass, MESSAGE__CONTENT_SIMPLE_TYPE);

        webServiceComponentEClass = createEClass(WEB_SERVICE_COMPONENT);
        createEAttribute(webServiceComponentEClass, WEB_SERVICE_COMPONENT__NAME);

        inputEClass = createEClass(INPUT);
        createEReference(inputEClass, INPUT__OPERATION);

        outputEClass = createEClass(OUTPUT);
        createEReference(outputEClass, OUTPUT__OPERATION);
        createEReference(outputEClass, OUTPUT__XML_DOCUMENT);

        interfaceEClass = createEClass(INTERFACE);
        createEReference(interfaceEClass, INTERFACE__OPERATIONS);

        sampleMessagesEClass = createEClass(SAMPLE_MESSAGES);
        createEReference(sampleMessagesEClass, SAMPLE_MESSAGES__MESSAGE);
        createEReference(sampleMessagesEClass, SAMPLE_MESSAGES__SAMPLE_FILES);
        createEReference(sampleMessagesEClass, SAMPLE_MESSAGES__SAMPLE_FROM_XSD);

        sampleFileEClass = createEClass(SAMPLE_FILE);
        createEAttribute(sampleFileEClass, SAMPLE_FILE__NAME);
        createEAttribute(sampleFileEClass, SAMPLE_FILE__URL);
        createEReference(sampleFileEClass, SAMPLE_FILE__SAMPLE_MESSAGES);

        sampleFromXsdEClass = createEClass(SAMPLE_FROM_XSD);
        createEAttribute(sampleFromXsdEClass, SAMPLE_FROM_XSD__MAX_NUMBER_OF_LEVELS_TO_BUILD);
        createEReference(sampleFromXsdEClass, SAMPLE_FROM_XSD__SAMPLE_FRAGMENT);
        createEReference(sampleFromXsdEClass, SAMPLE_FROM_XSD__SAMPLE_MESSAGES);

        // Create data types
        iStatusEDataType = createEDataType(ISTATUS);
        
        // Create enums
        operationUpdateCountEEnum = createEEnum(OPERATION_UPDATE_COUNT);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model. This method is guarded to have no affect on any invocation
     * but its first. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        XSDPackageImpl theXSDPackage = (XSDPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XSDPackage.eNS_URI);
        XmlDocumentPackageImpl theXmlDocumentPackage = (XmlDocumentPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XmlDocumentPackage.eNS_URI);

        // Add supertypes to classes
        operationEClass.getESuperTypes().add(this.getWebServiceComponent());
        messageEClass.getESuperTypes().add(this.getWebServiceComponent());
        inputEClass.getESuperTypes().add(this.getMessage());
        outputEClass.getESuperTypes().add(this.getMessage());
        interfaceEClass.getESuperTypes().add(this.getWebServiceComponent());

        // Initialize classes and features; add operations and parameters
        initEClass(operationEClass, Operation.class, "Operation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getOperation_Pattern(),
                       ecorePackage.getEString(),
                       "pattern", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getOperation_Safe(),
                       ecorePackage.getEBoolean(),
                       "safe", "false", 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getOperation_Input(),
                       this.getInput(),
                       this.getInput_Operation(),
                       "input", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOperation_Output(),
                       this.getOutput(),
                       this.getOutput_Operation(),
                       "output", null, 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOperation_Interface(),
                       this.getInterface(),
                       this.getInterface_Operations(),
                       "interface", null, 1, 1, Operation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getOperation_UpdateCount(), 
                this.getOperationUpdateCount(), 
                "updateCount", "AUTO", 0, 1, Operation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(messageEClass, Message.class, "Message", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMessage_ContentElement(),
                       theXSDPackage.getXSDElementDeclaration(),
                       null,
                       "contentElement", null, 0, 1, Message.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMessage_Samples(),
                       this.getSampleMessages(),
                       this.getSampleMessages_Message(),
                       "samples", null, 0, 1, Message.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMessage_ContentComplexType(),
                       theXSDPackage.getXSDComplexTypeDefinition(),
                       null,
                       "contentComplexType", null, 0, 1, Message.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getMessage_ContentSimpleType(),
                       theXSDPackage.getXSDSimpleTypeDefinition(),
                       null,
                       "contentSimpleType", null, 0, 1, Message.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(webServiceComponentEClass,
                   WebServiceComponent.class,
                   "WebServiceComponent", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getWebServiceComponent_Name(),
                       ecorePackage.getEString(),
                       "name", null, 0, 1, WebServiceComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(webServiceComponentEClass, this.getIStatus(), "isValid"); //$NON-NLS-1$

        initEClass(inputEClass, Input.class, "Input", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getInput_Operation(),
                       this.getOperation(),
                       this.getOperation_Input(),
                       "operation", null, 1, 1, Input.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(outputEClass, Output.class, "Output", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getOutput_Operation(),
                       this.getOperation(),
                       this.getOperation_Output(),
                       "operation", null, 1, 1, Output.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getOutput_XmlDocument(),
                       theXmlDocumentPackage.getXmlDocument(),
                       null,
                       "xmlDocument", null, 1, 1, Output.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(interfaceEClass, Interface.class, "Interface", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getInterface_Operations(),
                       this.getOperation(),
                       this.getOperation_Interface(),
                       "operations", null, 0, -1, Interface.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(sampleMessagesEClass,
                   SampleMessages.class,
                   "SampleMessages", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSampleMessages_Message(),
                       this.getMessage(),
                       this.getMessage_Samples(),
                       "message", null, 1, 1, SampleMessages.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSampleMessages_SampleFiles(),
                       this.getSampleFile(),
                       this.getSampleFile_SampleMessages(),
                       "sampleFiles", null, 0, -1, SampleMessages.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSampleMessages_SampleFromXsd(),
                       this.getSampleFromXsd(),
                       this.getSampleFromXsd_SampleMessages(),
                       "sampleFromXsd", null, 0, 1, SampleMessages.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(sampleFileEClass, SampleFile.class, "SampleFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getSampleFile_Name(),
                       ecorePackage.getEString(),
                       "name", null, 0, 1, SampleFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSampleFile_Url(),
                       ecorePackage.getEString(),
                       "url", null, 0, 1, SampleFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSampleFile_SampleMessages(),
                       this.getSampleMessages(),
                       this.getSampleMessages_SampleFiles(),
                       "sampleMessages", null, 1, 1, SampleFile.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(sampleFromXsdEClass,
                   SampleFromXsd.class,
                   "SampleFromXsd", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getSampleFromXsd_MaxNumberOfLevelsToBuild(),
                       ecorePackage.getEInt(),
                       "maxNumberOfLevelsToBuild", "30", 0, 1, SampleFromXsd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getSampleFromXsd_SampleFragment(),
                       theXmlDocumentPackage.getXmlElement(),
                       null,
                       "sampleFragment", null, 0, 1, SampleFromXsd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSampleFromXsd_SampleMessages(),
                       this.getSampleMessages(),
                       this.getSampleMessages_SampleFromXsd(),
                       "sampleMessages", null, 1, 1, SampleFromXsd.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize data types
        initEDataType(iStatusEDataType, IStatus.class, "IStatus", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(operationUpdateCountEEnum, OperationUpdateCount.class, "OperationUpdateCount"); //$NON-NLS-1$
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.AUTO_LITERAL);
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.ZERO_LITERAL);
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.ONE_LITERAL);
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.MULTIPLE_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} // WebServicePackageImpl
