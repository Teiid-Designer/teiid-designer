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

package com.metamatrix.modeler.compare.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.impl.EcorePackageImpl;
import org.eclipse.emf.mapping.MappingPackage;
import org.eclipse.emf.mapping.impl.MappingPackageImpl;

import com.metamatrix.modeler.compare.CompareFactory;
import com.metamatrix.modeler.compare.ComparePackage;
import com.metamatrix.modeler.compare.DifferenceDescriptor;
import com.metamatrix.modeler.compare.DifferenceReport;
import com.metamatrix.modeler.compare.DifferenceType;
import com.metamatrix.modeler.compare.PropertyDifference;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ComparePackageImpl extends EPackageImpl implements ComparePackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass differenceDescriptorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass differenceReportEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass propertyDifferenceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum differenceTypeEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType anyTypeEDataType = null;

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
     * @see com.metamatrix.modeler.compare.ComparePackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ComparePackageImpl() {
        super(eNS_URI, CompareFactory.eINSTANCE);
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
    public static ComparePackage init() {
        if (isInited) return (ComparePackage)EPackage.Registry.INSTANCE.getEPackage(ComparePackage.eNS_URI);

        // Obtain or create and register package
        ComparePackageImpl theComparePackage = (ComparePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ComparePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ComparePackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackageImpl.init();
        MappingPackageImpl.init();

        // Create package meta-data objects
        theComparePackage.createPackageContents();

        // Initialize created meta-data
        theComparePackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theComparePackage.freeze();

        return theComparePackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDifferenceDescriptor() {
        return differenceDescriptorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceDescriptor_Type() {
        return (EAttribute)differenceDescriptorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceDescriptor_Skip() {
        return (EAttribute)differenceDescriptorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDifferenceDescriptor_PropertyDifferences() {
        return (EReference)differenceDescriptorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDifferenceReport() {
        return differenceReportEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_Title() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_TotalAdditions() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_TotalDeletions() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_TotalChanges() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_AnalysisTime() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_SourceUri() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDifferenceReport_ResultUri() {
        return (EAttribute)differenceReportEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getDifferenceReport_Mapping() {
        return (EReference)differenceReportEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPropertyDifference() {
        return propertyDifferenceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getPropertyDifference_NewValue() {
        return (EAttribute)propertyDifferenceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getPropertyDifference_OldValue() {
        return (EAttribute)propertyDifferenceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getPropertyDifference_Skip() {
        return (EAttribute)propertyDifferenceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getPropertyDifference_AffectedFeature() {
        return (EReference)propertyDifferenceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getPropertyDifference_Descriptor() {
        return (EReference)propertyDifferenceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getDifferenceType() {
        return differenceTypeEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getAnyType() {
        return anyTypeEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public CompareFactory getCompareFactory() {
        return (CompareFactory)getEFactoryInstance();
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
        differenceDescriptorEClass = createEClass(DIFFERENCE_DESCRIPTOR);
        createEAttribute(differenceDescriptorEClass, DIFFERENCE_DESCRIPTOR__TYPE);
        createEAttribute(differenceDescriptorEClass, DIFFERENCE_DESCRIPTOR__SKIP);
        createEReference(differenceDescriptorEClass, DIFFERENCE_DESCRIPTOR__PROPERTY_DIFFERENCES);

        differenceReportEClass = createEClass(DIFFERENCE_REPORT);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__TITLE);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__TOTAL_ADDITIONS);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__TOTAL_DELETIONS);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__TOTAL_CHANGES);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__ANALYSIS_TIME);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__SOURCE_URI);
        createEAttribute(differenceReportEClass, DIFFERENCE_REPORT__RESULT_URI);
        createEReference(differenceReportEClass, DIFFERENCE_REPORT__MAPPING);

        propertyDifferenceEClass = createEClass(PROPERTY_DIFFERENCE);
        createEAttribute(propertyDifferenceEClass, PROPERTY_DIFFERENCE__NEW_VALUE);
        createEAttribute(propertyDifferenceEClass, PROPERTY_DIFFERENCE__OLD_VALUE);
        createEAttribute(propertyDifferenceEClass, PROPERTY_DIFFERENCE__SKIP);
        createEReference(propertyDifferenceEClass, PROPERTY_DIFFERENCE__AFFECTED_FEATURE);
        createEReference(propertyDifferenceEClass, PROPERTY_DIFFERENCE__DESCRIPTOR);

        // Create enums
        differenceTypeEEnum = createEEnum(DIFFERENCE_TYPE);

        // Create data types
        anyTypeEDataType = createEDataType(ANY_TYPE);
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
        MappingPackageImpl theMappingPackage = (MappingPackageImpl)EPackage.Registry.INSTANCE.getEPackage(MappingPackage.eNS_URI);
        EcorePackageImpl theEcorePackage = (EcorePackageImpl)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Add supertypes to classes
        differenceDescriptorEClass.getESuperTypes().add(theMappingPackage.getMappingHelper());

        // Initialize classes and features; add operations and parameters
        initEClass(differenceDescriptorEClass, DifferenceDescriptor.class, "DifferenceDescriptor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getDifferenceDescriptor_Type(), this.getDifferenceType(), "type", null, 0, 1, DifferenceDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceDescriptor_Skip(), ecorePackage.getEBoolean(), "skip", "false", 0, 1, DifferenceDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getDifferenceDescriptor_PropertyDifferences(), this.getPropertyDifference(), this.getPropertyDifference_Descriptor(), "propertyDifferences", null, 0, -1, DifferenceDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        addEOperation(differenceDescriptorEClass, ecorePackage.getEBoolean(), "isDeletion"); //$NON-NLS-1$

        addEOperation(differenceDescriptorEClass, ecorePackage.getEBoolean(), "isAddition"); //$NON-NLS-1$

        addEOperation(differenceDescriptorEClass, ecorePackage.getEBoolean(), "isChanged"); //$NON-NLS-1$

        addEOperation(differenceDescriptorEClass, ecorePackage.getEBoolean(), "isChangedBelow"); //$NON-NLS-1$

        addEOperation(differenceDescriptorEClass, ecorePackage.getEBoolean(), "isNoChange"); //$NON-NLS-1$

        initEClass(differenceReportEClass, DifferenceReport.class, "DifferenceReport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_Title(), ecorePackage.getEString(), "title", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_TotalAdditions(), ecorePackage.getEInt(), "totalAdditions", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_TotalDeletions(), ecorePackage.getEInt(), "totalDeletions", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_TotalChanges(), ecorePackage.getEInt(), "totalChanges", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_AnalysisTime(), ecorePackage.getELong(), "analysisTime", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_SourceUri(), ecorePackage.getEString(), "sourceUri", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getDifferenceReport_ResultUri(), ecorePackage.getEString(), "resultUri", null, 0, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getDifferenceReport_Mapping(), theMappingPackage.getMapping(), null, "mapping", null, 1, 1, DifferenceReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(propertyDifferenceEClass, PropertyDifference.class, "PropertyDifference", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(getPropertyDifference_NewValue(), this.getAnyType(), "newValue", null, 0, 1, PropertyDifference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getPropertyDifference_OldValue(), this.getAnyType(), "oldValue", null, 0, 1, PropertyDifference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getPropertyDifference_Skip(), ecorePackage.getEBoolean(), "skip", "false", 0, 1, PropertyDifference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEReference(getPropertyDifference_AffectedFeature(), theEcorePackage.getEStructuralFeature(), null, "affectedFeature", null, 1, 1, PropertyDifference.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getPropertyDifference_Descriptor(), this.getDifferenceDescriptor(), this.getDifferenceDescriptor_PropertyDifferences(), "descriptor", null, 1, 1, PropertyDifference.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(differenceTypeEEnum, DifferenceType.class, "DifferenceType"); //$NON-NLS-1$
        addEEnumLiteral(differenceTypeEEnum, DifferenceType.NO_CHANGE_LITERAL);
        addEEnumLiteral(differenceTypeEEnum, DifferenceType.ADDITION_LITERAL);
        addEEnumLiteral(differenceTypeEEnum, DifferenceType.DELETION_LITERAL);
        addEEnumLiteral(differenceTypeEEnum, DifferenceType.CHANGE_LITERAL);
        addEEnumLiteral(differenceTypeEEnum, DifferenceType.CHANGE_BELOW_LITERAL);

        // Initialize data types
        initEDataType(anyTypeEDataType, Object.class, "AnyType", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //ComparePackageImpl
