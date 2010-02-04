/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.wsdl.mime.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.xsd.impl.XSDPackageImpl;
import com.metamatrix.metamodels.wsdl.WsdlPackage;
import com.metamatrix.metamodels.wsdl.http.HttpPackage;
import com.metamatrix.metamodels.wsdl.http.impl.HttpPackageImpl;
import com.metamatrix.metamodels.wsdl.impl.WsdlPackageImpl;
import com.metamatrix.metamodels.wsdl.mime.MimeContent;
import com.metamatrix.metamodels.wsdl.mime.MimeElement;
import com.metamatrix.metamodels.wsdl.mime.MimeElementOwner;
import com.metamatrix.metamodels.wsdl.mime.MimeFactory;
import com.metamatrix.metamodels.wsdl.mime.MimeMultipartRelated;
import com.metamatrix.metamodels.wsdl.mime.MimePackage;
import com.metamatrix.metamodels.wsdl.mime.MimePart;
import com.metamatrix.metamodels.wsdl.soap.SoapPackage;
import com.metamatrix.metamodels.wsdl.soap.impl.SoapPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class MimePackageImpl extends EPackageImpl implements MimePackage {
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
	private EClass mimeContentEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass mimeMultipartRelatedEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass mimePartEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass mimeElementOwnerEClass = null;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	private EClass mimeElementEClass = null;

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
     * @see com.metamatrix.metamodels.wsdl.mime.MimePackage#eNS_URI
     * @see #init()
     * @generated
     */
	private MimePackageImpl() {
        super(eNS_URI, MimeFactory.eINSTANCE);
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
	public static MimePackage init() {
        if (isInited) return (MimePackage)EPackage.Registry.INSTANCE.getEPackage(MimePackage.eNS_URI);

        // Obtain or create and register package
        MimePackageImpl theMimePackage = (MimePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof MimePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new MimePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        XSDPackageImpl.init();

        // Obtain or create and register interdependencies
        WsdlPackageImpl theWsdlPackage = (WsdlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI) instanceof WsdlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(WsdlPackage.eNS_URI) : WsdlPackage.eINSTANCE);
        SoapPackageImpl theSoapPackage = (SoapPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI) instanceof SoapPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SoapPackage.eNS_URI) : SoapPackage.eINSTANCE);
        HttpPackageImpl theHttpPackage = (HttpPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI) instanceof HttpPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(HttpPackage.eNS_URI) : HttpPackage.eINSTANCE);

        // Create package meta-data objects
        theMimePackage.createPackageContents();
        theWsdlPackage.createPackageContents();
        theSoapPackage.createPackageContents();
        theHttpPackage.createPackageContents();

        // Initialize created meta-data
        theMimePackage.initializePackageContents();
        theWsdlPackage.initializePackageContents();
        theSoapPackage.initializePackageContents();
        theHttpPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theMimePackage.freeze();

        return theMimePackage;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMimeContent() {
        return mimeContentEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getMimeContent_Type() {
        return (EAttribute)mimeContentEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EAttribute getMimeContent_Xml() {
        return (EAttribute)mimeContentEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMimeContent_MessagePart() {
        return (EReference)mimeContentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMimeMultipartRelated() {
        return mimeMultipartRelatedEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMimeMultipartRelated_MimeParts() {
        return (EReference)mimeMultipartRelatedEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMimePart() {
        return mimePartEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMimePart_MimeMultipartRelated() {
        return (EReference)mimePartEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMimeElementOwner() {
        return mimeElementOwnerEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMimeElementOwner_MimeElements() {
        return (EReference)mimeElementOwnerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EClass getMimeElement() {
        return mimeElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EReference getMimeElement_MimeElementOwner() {
        return (EReference)mimeElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public MimeFactory getMimeFactory() {
        return (MimeFactory)getEFactoryInstance();
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
        mimeContentEClass = createEClass(MIME_CONTENT);
        createEReference(mimeContentEClass, MIME_CONTENT__MESSAGE_PART);
        createEAttribute(mimeContentEClass, MIME_CONTENT__TYPE);
        createEAttribute(mimeContentEClass, MIME_CONTENT__XML);

        mimeMultipartRelatedEClass = createEClass(MIME_MULTIPART_RELATED);
        createEReference(mimeMultipartRelatedEClass, MIME_MULTIPART_RELATED__MIME_PARTS);

        mimePartEClass = createEClass(MIME_PART);
        createEReference(mimePartEClass, MIME_PART__MIME_MULTIPART_RELATED);

        mimeElementOwnerEClass = createEClass(MIME_ELEMENT_OWNER);
        createEReference(mimeElementOwnerEClass, MIME_ELEMENT_OWNER__MIME_ELEMENTS);

        mimeElementEClass = createEClass(MIME_ELEMENT);
        createEReference(mimeElementEClass, MIME_ELEMENT__MIME_ELEMENT_OWNER);
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
        mimeContentEClass.getESuperTypes().add(this.getMimeElement());
        mimeMultipartRelatedEClass.getESuperTypes().add(this.getMimeElement());
        mimePartEClass.getESuperTypes().add(this.getMimeElementOwner());

        // Initialize classes and features; add operations and parameters
        initEClass(mimeContentEClass, MimeContent.class, "MimeContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMimeContent_MessagePart(), theWsdlPackage.getMessagePart(), null, "messagePart", null, 0, 1, MimeContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getMimeContent_Type(), ecorePackage.getEString(), "type", null, 0, 1, MimeContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getMimeContent_Xml(), ecorePackage.getEBoolean(), "xml", "false", 0, 1, MimeContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$

        initEClass(mimeMultipartRelatedEClass, MimeMultipartRelated.class, "MimeMultipartRelated", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMimeMultipartRelated_MimeParts(), this.getMimePart(), this.getMimePart_MimeMultipartRelated(), "mimeParts", null, 0, -1, MimeMultipartRelated.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(mimePartEClass, MimePart.class, "MimePart", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMimePart_MimeMultipartRelated(), this.getMimeMultipartRelated(), this.getMimeMultipartRelated_MimeParts(), "mimeMultipartRelated", null, 1, 1, MimePart.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(mimeElementOwnerEClass, MimeElementOwner.class, "MimeElementOwner", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMimeElementOwner_MimeElements(), this.getMimeElement(), this.getMimeElement_MimeElementOwner(), "mimeElements", null, 0, -1, MimeElementOwner.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(mimeElementEClass, MimeElement.class, "MimeElement", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getMimeElement_MimeElementOwner(), this.getMimeElementOwner(), this.getMimeElementOwner_MimeElements(), "mimeElementOwner", null, 1, 1, MimeElement.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //MimePackageImpl
