/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.soap.impl;

import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.http.HttpPackage;

import com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl;

import com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl;

import com.metamatrix.metamodels.wsdl.mime.MimePackage;

import com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl;

import com.metamatrix.metamodels.wsdl.soap.SoapAddress;
import com.metamatrix.metamodels.wsdl.soap.SoapBinding;
import com.metamatrix.metamodels.wsdl.soap.SoapBody;
import com.metamatrix.metamodels.wsdl.soap.SoapFactory;
import com.metamatrix.metamodels.wsdl.soap.SoapFault;
import com.metamatrix.metamodels.wsdl.soap.SoapHeader;
import com.metamatrix.metamodels.wsdl.soap.SoapHeaderFault;
import com.metamatrix.metamodels.wsdl.soap.SoapOperation;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.SoapStyleType;
import com.metamatrix.metamodels.wsdl.soap.SoapUseType;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;

import org.eclipse.xsd.impl.XSDPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SoapPackageImpl extends EPackageImpl implements SoapPackage {
    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public static final String copyright = "See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing."; //$NON-NLS-1$

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapAddressEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapHeaderFaultEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapFaultEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapHeaderEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapBodyEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapOperationEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass soapBindingEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EEnum soapStyleTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EEnum soapUseTypeEEnum = null;

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
     * @see com.metamatrix.metamodels.wsdl.soap.SoapPackage#eNS_URI
     * @see #init()
     * @generated
     */
	private SoapPackageImpl() {
        super(eNS_URI, SoapFactory.eINSTANCE);
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
	public static SoapPackage init() {
        if (isInited) return (SoapPackage)EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI);

        // Obtain or create and register package
        SoapPackageImpl theSoapPackage = (SoapPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof SoapPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new SoapPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        XSDPackageImpl.init();

        // Obtain or create and register interdependencies
        WsdlPackageImpl theWsdlPackage = (WsdlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI) instanceof WsdlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI) : WsdlPackage.eINSTANCE);
        HttpPackageImpl theHttpPackage = (HttpPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI) instanceof HttpPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI) : HttpPackage.eINSTANCE);
        MimePackageImpl theMimePackage = (MimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI) instanceof MimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI) : MimePackage.eINSTANCE);

        // Create package meta-data objects
        theSoapPackage.createPackageContents();
        theWsdlPackage.createPackageContents();
        theHttpPackage.createPackageContents();
        theMimePackage.createPackageContents();

        // Initialize created meta-data
        theSoapPackage.initializePackageContents();
        theWsdlPackage.initializePackageContents();
        theHttpPackage.initializePackageContents();
        theMimePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theSoapPackage.freeze();

        return theSoapPackage;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapAddress() {
        return soapAddressEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapAddress_Location() {
        return (EAttribute)soapAddressEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapAddress_Port() {
        return (EReference)soapAddressEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapHeaderFault() {
        return soapHeaderFaultEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeaderFault_Parts() {
        return (EAttribute)soapHeaderFaultEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeaderFault_Message() {
        return (EAttribute)soapHeaderFaultEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapHeaderFault_MessagePart() {
        return (EReference)soapHeaderFaultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapHeaderFault_SoapHeader() {
        return (EReference)soapHeaderFaultEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapFault() {
        return soapFaultEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapFault_Use() {
        return (EAttribute)soapFaultEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapFault_Namespace() {
        return (EAttribute)soapFaultEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapFault_EncodingStyles() {
        return (EAttribute)soapFaultEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapFault_BindingFault() {
        return (EReference)soapFaultEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapHeader() {
        return soapHeaderEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeader_Use() {
        return (EAttribute)soapHeaderEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeader_Namespace() {
        return (EAttribute)soapHeaderEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeader_EncodingStyles() {
        return (EAttribute)soapHeaderEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeader_Parts() {
        return (EAttribute)soapHeaderEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapHeader_Message() {
        return (EAttribute)soapHeaderEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapHeader_BindingParam() {
        return (EReference)soapHeaderEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapHeader_MessagePart() {
        return (EReference)soapHeaderEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapHeader_HeaderFault() {
        return (EReference)soapHeaderEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapBody() {
        return soapBodyEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapBody_Use() {
        return (EAttribute)soapBodyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapBody_Namespace() {
        return (EAttribute)soapBodyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapBody_EncodingStyles() {
        return (EAttribute)soapBodyEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapBody_Parts() {
        return (EAttribute)soapBodyEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapBody_BindingParam() {
        return (EReference)soapBodyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapOperation() {
        return soapOperationEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapOperation_Style() {
        return (EAttribute)soapOperationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapOperation_Action() {
        return (EAttribute)soapOperationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapOperation_BindingOperation() {
        return (EReference)soapOperationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getSoapBinding() {
        return soapBindingEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapBinding_Transport() {
        return (EAttribute)soapBindingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getSoapBinding_Style() {
        return (EAttribute)soapBindingEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getSoapBinding_Binding() {
        return (EReference)soapBindingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EEnum getSoapStyleType() {
        return soapStyleTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EEnum getSoapUseType() {
        return soapUseTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public SoapFactory getSoapFactory() {
        return (SoapFactory)getEFactoryInstance();
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
        soapAddressEClass = createEClass(SOAP_ADDRESS);
        createEReference(soapAddressEClass, SOAP_ADDRESS__PORT);
        createEAttribute(soapAddressEClass, SOAP_ADDRESS__LOCATION);

        soapHeaderFaultEClass = createEClass(SOAP_HEADER_FAULT);
        createEReference(soapHeaderFaultEClass, SOAP_HEADER_FAULT__MESSAGE_PART);
        createEReference(soapHeaderFaultEClass, SOAP_HEADER_FAULT__SOAP_HEADER);
        createEAttribute(soapHeaderFaultEClass, SOAP_HEADER_FAULT__PARTS);
        createEAttribute(soapHeaderFaultEClass, SOAP_HEADER_FAULT__MESSAGE);

        soapFaultEClass = createEClass(SOAP_FAULT);
        createEReference(soapFaultEClass, SOAP_FAULT__BINDING_FAULT);
        createEAttribute(soapFaultEClass, SOAP_FAULT__USE);
        createEAttribute(soapFaultEClass, SOAP_FAULT__NAMESPACE);
        createEAttribute(soapFaultEClass, SOAP_FAULT__ENCODING_STYLES);

        soapHeaderEClass = createEClass(SOAP_HEADER);
        createEReference(soapHeaderEClass, SOAP_HEADER__BINDING_PARAM);
        createEReference(soapHeaderEClass, SOAP_HEADER__MESSAGE_PART);
        createEReference(soapHeaderEClass, SOAP_HEADER__HEADER_FAULT);
        createEAttribute(soapHeaderEClass, SOAP_HEADER__USE);
        createEAttribute(soapHeaderEClass, SOAP_HEADER__NAMESPACE);
        createEAttribute(soapHeaderEClass, SOAP_HEADER__ENCODING_STYLES);
        createEAttribute(soapHeaderEClass, SOAP_HEADER__PARTS);
        createEAttribute(soapHeaderEClass, SOAP_HEADER__MESSAGE);

        soapBodyEClass = createEClass(SOAP_BODY);
        createEReference(soapBodyEClass, SOAP_BODY__BINDING_PARAM);
        createEAttribute(soapBodyEClass, SOAP_BODY__USE);
        createEAttribute(soapBodyEClass, SOAP_BODY__NAMESPACE);
        createEAttribute(soapBodyEClass, SOAP_BODY__ENCODING_STYLES);
        createEAttribute(soapBodyEClass, SOAP_BODY__PARTS);

        soapOperationEClass = createEClass(SOAP_OPERATION);
        createEReference(soapOperationEClass, SOAP_OPERATION__BINDING_OPERATION);
        createEAttribute(soapOperationEClass, SOAP_OPERATION__STYLE);
        createEAttribute(soapOperationEClass, SOAP_OPERATION__ACTION);

        soapBindingEClass = createEClass(SOAP_BINDING);
        createEReference(soapBindingEClass, SOAP_BINDING__BINDING);
        createEAttribute(soapBindingEClass, SOAP_BINDING__TRANSPORT);
        createEAttribute(soapBindingEClass, SOAP_BINDING__STYLE);

        // Create enums
        soapStyleTypeEEnum = createEEnum(SOAP_STYLE_TYPE);
        soapUseTypeEEnum = createEEnum(SOAP_USE_TYPE);
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
        WsdlPackageImpl theWsdlPackage = (WsdlPackageImpl)EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI);

        // Add supertypes to classes
        soapHeaderFaultEClass.getESuperTypes().add(this.getSoapFault());

        // Initialize classes and features; add operations and parameters
        initEClass(soapAddressEClass, SoapAddress.class, "SoapAddress", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapAddress_Port(), theWsdlPackage.getPort(), theWsdlPackage.getPort_SoapAddress(), "port", null, 1, 1, SoapAddress.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapAddress_Location(), ecorePackage.getEString(), "location", null, 0, 1, SoapAddress.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(soapHeaderFaultEClass, SoapHeaderFault.class, "SoapHeaderFault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapHeaderFault_MessagePart(), theWsdlPackage.getMessagePart(), null, "messagePart", null, 0, -1, SoapHeaderFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSoapHeaderFault_SoapHeader(), this.getSoapHeader(), this.getSoapHeader_HeaderFault(), "soapHeader", null, 1, 1, SoapHeaderFault.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeaderFault_Parts(), ecorePackage.getEString(), "parts", null, 0, -1, SoapHeaderFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeaderFault_Message(), ecorePackage.getEString(), "message", null, 0, 1, SoapHeaderFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(soapFaultEClass, SoapFault.class, "SoapFault", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapFault_BindingFault(), theWsdlPackage.getBindingFault(), theWsdlPackage.getBindingFault_SoapFault(), "bindingFault", null, 0, 1, SoapFault.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapFault_Use(), this.getSoapUseType(), "use", null, 0, 1, SoapFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapFault_Namespace(), ecorePackage.getEString(), "namespace", null, 0, 1, SoapFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapFault_EncodingStyles(), ecorePackage.getEString(), "encodingStyles", null, 0, -1, SoapFault.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(soapHeaderEClass, SoapHeader.class, "SoapHeader", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapHeader_BindingParam(), theWsdlPackage.getBindingParam(), theWsdlPackage.getBindingParam_SoapHeader(), "bindingParam", null, 1, 1, SoapHeader.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSoapHeader_MessagePart(), theWsdlPackage.getMessagePart(), null, "messagePart", null, 0, -1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getSoapHeader_HeaderFault(), this.getSoapHeaderFault(), this.getSoapHeaderFault_SoapHeader(), "headerFault", null, 0, 1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeader_Use(), this.getSoapUseType(), "use", null, 0, 1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeader_Namespace(), ecorePackage.getEString(), "namespace", null, 0, 1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeader_EncodingStyles(), ecorePackage.getEString(), "encodingStyles", null, 0, -1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeader_Parts(), ecorePackage.getEString(), "parts", null, 0, -1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapHeader_Message(), ecorePackage.getEString(), "message", null, 0, 1, SoapHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(soapBodyEClass, SoapBody.class, "SoapBody", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapBody_BindingParam(), theWsdlPackage.getBindingParam(), theWsdlPackage.getBindingParam_SoapBody(), "bindingParam", null, 1, 1, SoapBody.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapBody_Use(), this.getSoapUseType(), "use", null, 0, 1, SoapBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapBody_Namespace(), ecorePackage.getEString(), "namespace", null, 0, 1, SoapBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapBody_EncodingStyles(), ecorePackage.getEString(), "encodingStyles", null, 0, -1, SoapBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapBody_Parts(), ecorePackage.getEString(), "parts", null, 0, -1, SoapBody.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(soapOperationEClass, SoapOperation.class, "SoapOperation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapOperation_BindingOperation(), theWsdlPackage.getBindingOperation(), theWsdlPackage.getBindingOperation_SoapOperation(), "bindingOperation", null, 1, 1, SoapOperation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapOperation_Style(), this.getSoapStyleType(), "style", "DOCUMENT", 0, 1, SoapOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getSoapOperation_Action(), ecorePackage.getEString(), "action", null, 0, 1, SoapOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(soapBindingEClass, SoapBinding.class, "SoapBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getSoapBinding_Binding(), theWsdlPackage.getBinding(), theWsdlPackage.getBinding_SoapBinding(), "binding", null, 1, 1, SoapBinding.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapBinding_Transport(), ecorePackage.getEString(), "transport", null, 0, 1, SoapBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getSoapBinding_Style(), this.getSoapStyleType(), "style", "DOCUMENT", 0, 1, SoapBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        // Initialize enums and add enum literals
        initEEnum(soapStyleTypeEEnum, SoapStyleType.class, "SoapStyleType"); //$NON-NLS-1$
        addEEnumLiteral(soapStyleTypeEEnum, SoapStyleType.RPC_LITERAL);
        addEEnumLiteral(soapStyleTypeEEnum, SoapStyleType.DOCUMENT_LITERAL);

        initEEnum(soapUseTypeEEnum, SoapUseType.class, "SoapUseType"); //$NON-NLS-1$
        addEEnumLiteral(soapUseTypeEEnum, SoapUseType.LITERAL_LITERAL);
        addEEnumLiteral(soapUseTypeEEnum, SoapUseType.ENCODED_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} //SoapPackageImpl
