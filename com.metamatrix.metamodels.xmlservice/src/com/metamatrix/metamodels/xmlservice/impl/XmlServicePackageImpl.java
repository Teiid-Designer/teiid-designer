/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.xmlservice.impl;

import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.impl.XSDPackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;

import com.metamatrix.metamodels.xmlservice.OperationUpdateCount;
import com.metamatrix.metamodels.xmlservice.XmlInput;
import com.metamatrix.metamodels.xmlservice.XmlMessage;
import com.metamatrix.metamodels.xmlservice.XmlOperation;
import com.metamatrix.metamodels.xmlservice.XmlOutput;
import com.metamatrix.metamodels.xmlservice.XmlResult;
import com.metamatrix.metamodels.xmlservice.XmlServiceComponent;
import com.metamatrix.metamodels.xmlservice.XmlServiceFactory;
import com.metamatrix.metamodels.xmlservice.XmlServicePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XmlServicePackageImpl extends EPackageImpl implements XmlServicePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final String copyright = "Copyright (c) 2000-2006 MetaMatrix Corporation. All rights reserved."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlOperationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlInputEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlServiceComponentEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlOutputEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlMessageEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass xmlResultEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum operationUpdateCountEEnum = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see com.metamatrix.metamodels.xmlservice.XmlServicePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private XmlServicePackageImpl() {
        super(eNS_URI, XmlServiceFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static XmlServicePackage init() {
        if (isInited) return (XmlServicePackage)EPackage.Registry.INSTANCE.getEPackage(XmlServicePackage.eNS_URI);

        // Obtain or create and register package
        XmlServicePackageImpl theXmlServicePackage = (XmlServicePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof XmlServicePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new XmlServicePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        XSDPackageImpl.init();

        // Create package meta-data objects
        theXmlServicePackage.createPackageContents();

        // Initialize created meta-data
        theXmlServicePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theXmlServicePackage.freeze();

        return theXmlServicePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlOperation() {
        return xmlOperationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlOperation_Inputs() {
        return (EReference)xmlOperationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlOperation_Output() {
        return (EReference)xmlOperationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlOperation_UpdateCount() {
        return (EAttribute)xmlOperationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlInput() {
        return xmlInputEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlInput_Operation() {
        return (EReference)xmlInputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlInput_Type() {
        return (EReference)xmlInputEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlServiceComponent() {
        return xmlServiceComponentEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlServiceComponent_Name() {
        return (EAttribute)xmlServiceComponentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getXmlServiceComponent_NameInSource() {
        return (EAttribute)xmlServiceComponentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlOutput() {
        return xmlOutputEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlOutput_Operation() {
        return (EReference)xmlOutputEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlOutput_Result() {
        return (EReference)xmlOutputEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlMessage() {
        return xmlMessageEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlMessage_ContentElement() {
        return (EReference)xmlMessageEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getXmlResult() {
        return xmlResultEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getXmlResult_Output() {
        return (EReference)xmlResultEClass.getEStructuralFeatures().get(0);
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
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlServiceFactory getXmlServiceFactory() {
        return (XmlServiceFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        xmlOperationEClass = createEClass(XML_OPERATION);
        createEReference(xmlOperationEClass, XML_OPERATION__INPUTS);
        createEReference(xmlOperationEClass, XML_OPERATION__OUTPUT);
        createEAttribute(xmlOperationEClass, XML_OPERATION__UPDATE_COUNT);

        xmlInputEClass = createEClass(XML_INPUT);
        createEReference(xmlInputEClass, XML_INPUT__OPERATION);
        createEReference(xmlInputEClass, XML_INPUT__TYPE);

        xmlServiceComponentEClass = createEClass(XML_SERVICE_COMPONENT);
        createEAttribute(xmlServiceComponentEClass, XML_SERVICE_COMPONENT__NAME);
        createEAttribute(xmlServiceComponentEClass, XML_SERVICE_COMPONENT__NAME_IN_SOURCE);

        xmlOutputEClass = createEClass(XML_OUTPUT);
        createEReference(xmlOutputEClass, XML_OUTPUT__OPERATION);
        createEReference(xmlOutputEClass, XML_OUTPUT__RESULT);

        xmlMessageEClass = createEClass(XML_MESSAGE);
        createEReference(xmlMessageEClass, XML_MESSAGE__CONTENT_ELEMENT);

        xmlResultEClass = createEClass(XML_RESULT);
        createEReference(xmlResultEClass, XML_RESULT__OUTPUT);

        // Create enums
        operationUpdateCountEEnum = createEEnum(OPERATION_UPDATE_COUNT);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
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
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
        XSDPackageImpl theXSDPackage = (XSDPackageImpl)EPackage.Registry.INSTANCE.getEPackage(XSDPackage.eNS_URI);

        // Add supertypes to classes
        xmlOperationEClass.getESuperTypes().add(this.getXmlServiceComponent());
        xmlInputEClass.getESuperTypes().add(this.getXmlMessage());
        xmlInputEClass.getESuperTypes().add(this.getXmlServiceComponent());
        xmlOutputEClass.getESuperTypes().add(this.getXmlMessage());
        xmlOutputEClass.getESuperTypes().add(this.getXmlServiceComponent());
        xmlMessageEClass.getESuperTypes().add(this.getXmlServiceComponent());

        // Initialize classes and features; add operations and parameters
        initEClass(xmlOperationEClass, XmlOperation.class, "XmlOperation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlOperation_Inputs(), this.getXmlInput(), this.getXmlInput_Operation(), "inputs", null, 0, -1, XmlOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlOperation_Output(), this.getXmlOutput(), this.getXmlOutput_Operation(), "output", null, 0, 1, XmlOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getXmlOperation_UpdateCount(), this.getOperationUpdateCount(), "updateCount", "AUTO", 0, 1, XmlOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(xmlInputEClass, XmlInput.class, "XmlInput", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlInput_Operation(), this.getXmlOperation(), this.getXmlOperation_Inputs(), "operation", null, 1, 1, XmlInput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlInput_Type(), theEcorePackage.getEObject(), null, "type", null, 1, 1, XmlInput.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlServiceComponentEClass, XmlServiceComponent.class, "XmlServiceComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getXmlServiceComponent_Name(), ecorePackage.getEString(), "name", null, 0, 1, XmlServiceComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getXmlServiceComponent_NameInSource(), ecorePackage.getEString(), "nameInSource", null, 0, 1, XmlServiceComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlOutputEClass, XmlOutput.class, "XmlOutput", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlOutput_Operation(), this.getXmlOperation(), this.getXmlOperation_Output(), "operation", null, 1, 1, XmlOutput.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getXmlOutput_Result(), this.getXmlResult(), this.getXmlResult_Output(), "result", null, 0, 1, XmlOutput.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlMessageEClass, XmlMessage.class, "XmlMessage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlMessage_ContentElement(), theXSDPackage.getXSDElementDeclaration(), null, "contentElement", null, 0, 1, XmlMessage.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(xmlResultEClass, XmlResult.class, "XmlResult", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getXmlResult_Output(), this.getXmlOutput(), this.getXmlOutput_Result(), "output", null, 1, 1, XmlResult.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(operationUpdateCountEEnum, OperationUpdateCount.class, "OperationUpdateCount"); //$NON-NLS-1$
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.AUTO_LITERAL);
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.ZERO_LITERAL);
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.ONE_LITERAL);
        addEEnumLiteral(operationUpdateCountEEnum, OperationUpdateCount.MULTIPLE_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} //XmlServicePackageImpl
