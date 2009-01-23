/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.http.impl;

import com.metamatrix.metamodels.wsdl.WsdlPackage;

import com.metamatrix.metamodels.wsdl.http.HttpAddress;
import com.metamatrix.metamodels.wsdl.http.HttpBinding;
import com.metamatrix.metamodels.wsdl.http.HttpFactory;
import com.metamatrix.metamodels.wsdl.http.HttpOperation;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;

import com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl;

import com.metamatrix.metamodels.wsdl.mime.MimePackage;

import com.metamatrix.metamodels.wsdl.mime.impl.MimePackageImpl;

import com.metamatrix.metamodels.wsdl.soap.SoapPackage;

import com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
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
public class HttpPackageImpl extends EPackageImpl implements HttpPackage {
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
	private EClass httpAddressEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass httpBindingEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass httpOperationEClass = null;

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
     * @see com.metamatrix.metamodels.wsdl.http.HttpPackage#eNS_URI
     * @see #init()
     * @generated
     */
	private HttpPackageImpl() {
        super(eNS_URI, HttpFactory.eINSTANCE);
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
	public static HttpPackage init() {
        if (isInited) return (HttpPackage)EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI);

        // Obtain or create and register package
        HttpPackageImpl theHttpPackage = (HttpPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof HttpPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new HttpPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        XSDPackageImpl.init();

        // Obtain or create and register interdependencies
        WsdlPackageImpl theWsdlPackage = (WsdlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI) instanceof WsdlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI) : WsdlPackage.eINSTANCE);
        SoapPackageImpl theSoapPackage = (SoapPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI) instanceof SoapPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI) : SoapPackage.eINSTANCE);
        MimePackageImpl theMimePackage = (MimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI) instanceof MimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI) : MimePackage.eINSTANCE);

        // Create package meta-data objects
        theHttpPackage.createPackageContents();
        theWsdlPackage.createPackageContents();
        theSoapPackage.createPackageContents();
        theMimePackage.createPackageContents();

        // Initialize created meta-data
        theHttpPackage.initializePackageContents();
        theWsdlPackage.initializePackageContents();
        theSoapPackage.initializePackageContents();
        theMimePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theHttpPackage.freeze();

        return theHttpPackage;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getHttpAddress() {
        return httpAddressEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getHttpAddress_Location() {
        return (EAttribute)httpAddressEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getHttpAddress_Port() {
        return (EReference)httpAddressEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getHttpBinding() {
        return httpBindingEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getHttpBinding_Verb() {
        return (EAttribute)httpBindingEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getHttpBinding_Binding() {
        return (EReference)httpBindingEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getHttpOperation() {
        return httpOperationEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getHttpOperation_Location() {
        return (EAttribute)httpOperationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getHttpOperation_BindingOperation() {
        return (EReference)httpOperationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public HttpFactory getHttpFactory() {
        return (HttpFactory)getEFactoryInstance();
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
        httpAddressEClass = createEClass(HTTP_ADDRESS);
        createEReference(httpAddressEClass, HTTP_ADDRESS__PORT);
        createEAttribute(httpAddressEClass, HTTP_ADDRESS__LOCATION);

        httpBindingEClass = createEClass(HTTP_BINDING);
        createEReference(httpBindingEClass, HTTP_BINDING__BINDING);
        createEAttribute(httpBindingEClass, HTTP_BINDING__VERB);

        httpOperationEClass = createEClass(HTTP_OPERATION);
        createEReference(httpOperationEClass, HTTP_OPERATION__BINDING_OPERATION);
        createEAttribute(httpOperationEClass, HTTP_OPERATION__LOCATION);
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

        // Initialize classes and features; add operations and parameters
        initEClass(httpAddressEClass, HttpAddress.class, "HttpAddress", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getHttpAddress_Port(), theWsdlPackage.getPort(), theWsdlPackage.getPort_HttpAddress(), "port", null, 1, 1, HttpAddress.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHttpAddress_Location(), ecorePackage.getEString(), "location", null, 0, 1, HttpAddress.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(httpBindingEClass, HttpBinding.class, "HttpBinding", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getHttpBinding_Binding(), theWsdlPackage.getBinding(), theWsdlPackage.getBinding_HttpBinding(), "binding", null, 1, 1, HttpBinding.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHttpBinding_Verb(), ecorePackage.getEString(), "verb", null, 0, 1, HttpBinding.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(httpOperationEClass, HttpOperation.class, "HttpOperation", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getHttpOperation_BindingOperation(), theWsdlPackage.getBindingOperation(), theWsdlPackage.getBindingOperation_HttpOperation(), "bindingOperation", null, 1, 1, HttpOperation.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getHttpOperation_Location(), ecorePackage.getEString(), "location", null, 0, 1, HttpOperation.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //HttpPackageImpl
