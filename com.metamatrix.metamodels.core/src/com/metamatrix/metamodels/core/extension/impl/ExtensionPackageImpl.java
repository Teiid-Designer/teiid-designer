/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.metamodels.core.extension.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import com.metamatrix.metamodels.core.extension.ExtensionFactory;
import com.metamatrix.metamodels.core.extension.ExtensionPackage;
import com.metamatrix.metamodels.core.extension.XAttribute;
import com.metamatrix.metamodels.core.extension.XClass;
import com.metamatrix.metamodels.core.extension.XEnum;
import com.metamatrix.metamodels.core.extension.XEnumLiteral;
import com.metamatrix.metamodels.core.extension.XPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ExtensionPackageImpl extends EPackageImpl implements ExtensionPackage {
    /**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) 2000-2008 MetaMatrix Corporation.  All rights reserved."; //$NON-NLS-1$

				/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass xClassEClass = null;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass xPackageEClass = null;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass xAttributeEClass = null;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass xEnumEClass = null;

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass xEnumLiteralEClass = null;

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
	 * @see com.metamatrix.metamodels.core.extension.ExtensionPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
    private ExtensionPackageImpl() {
		super(eNS_URI, ExtensionFactory.eINSTANCE);
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
    public static ExtensionPackage init() {
		if (isInited) return (ExtensionPackage)EPackage.Registry.INSTANCE.getEPackage(ExtensionPackage.eNS_URI);

		// Obtain or create and register package
		ExtensionPackageImpl theExtensionPackage = (ExtensionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ExtensionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ExtensionPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		EcorePackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theExtensionPackage.createPackageContents();

		// Initialize created meta-data
		theExtensionPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theExtensionPackage.freeze();

		return theExtensionPackage;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getXClass() {
		return xClassEClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getXClass_ExtendedClass() {
		return (EReference)xClassEClass.getEStructuralFeatures().get(0);
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getXPackage() {
		return xPackageEClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getXAttribute() {
		return xAttributeEClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getXEnum() {
		return xEnumEClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getXEnumLiteral() {
		return xEnumLiteralEClass;
	}

    /**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public ExtensionFactory getExtensionFactory() {
		return (ExtensionFactory)getEFactoryInstance();
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
		xClassEClass = createEClass(XCLASS);
		createEReference(xClassEClass, XCLASS__EXTENDED_CLASS);

		xPackageEClass = createEClass(XPACKAGE);

		xAttributeEClass = createEClass(XATTRIBUTE);

		xEnumEClass = createEClass(XENUM);

		xEnumLiteralEClass = createEClass(XENUM_LITERAL);
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
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

		// Add supertypes to classes
		xClassEClass.getESuperTypes().add(theEcorePackage.getEClass());
		xPackageEClass.getESuperTypes().add(theEcorePackage.getEPackage());
		xAttributeEClass.getESuperTypes().add(theEcorePackage.getEAttribute());
		xEnumEClass.getESuperTypes().add(theEcorePackage.getEEnum());
		xEnumLiteralEClass.getESuperTypes().add(theEcorePackage.getEEnumLiteral());

		// Initialize classes and features; add operations and parameters
		initEClass(xClassEClass, XClass.class, "XClass", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEReference(getXClass_ExtendedClass(), theEcorePackage.getEClass(), null, "extendedClass", null, 0, 1, XClass.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(xPackageEClass, XPackage.class, "XPackage", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		EOperation op = addEOperation(xPackageEClass, this.getXClass(), "findXClass", 0, 1); //$NON-NLS-1$
		addEParameter(op, theEcorePackage.getEClass(), "extendedClass", 0, 1); //$NON-NLS-1$

		initEClass(xAttributeEClass, XAttribute.class, "XAttribute", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(xEnumEClass, XEnum.class, "XEnum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		initEClass(xEnumLiteralEClass, XEnumLiteral.class, "XEnumLiteral", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);
	}

} //ExtensionPackageImpl
