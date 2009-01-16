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

package com.metamatrix.modeler.jdbc.impl;

import com.metamatrix.modeler.jdbc.CaseConversion;
import com.metamatrix.modeler.jdbc.JdbcDriver;
import com.metamatrix.modeler.jdbc.JdbcDriverContainer;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcImportOptions;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPackage;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.JdbcSourceContainer;
import com.metamatrix.modeler.jdbc.JdbcSourceProperty;
import com.metamatrix.modeler.jdbc.SourceNames;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class JdbcPackageImpl extends EPackageImpl implements JdbcPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcSourcePropertyEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcDriverEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcSourceEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcDriverContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcSourceContainerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcImportSettingsEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass jdbcImportOptionsEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum caseConversionEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum sourceNamesEEnum = null;

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
     * @see com.metamatrix.modeler.jdbc.JdbcPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private JdbcPackageImpl() {
        super(eNS_URI, JdbcFactory.eINSTANCE);
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
    public static JdbcPackage init() {
        if (isInited) return (JdbcPackage)EPackage.Registry.INSTANCE.getEPackage(JdbcPackage.eNS_URI);

        // Obtain or create and register package
        JdbcPackageImpl theJdbcPackage = (JdbcPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof JdbcPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new JdbcPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theJdbcPackage.createPackageContents();

        // Initialize created meta-data
        theJdbcPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theJdbcPackage.freeze();

        return theJdbcPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcSourceProperty() {
        return jdbcSourcePropertyEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcSourceProperty_Source() {
        return (EReference)jdbcSourcePropertyEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSourceProperty_Name() {
        return (EAttribute)jdbcSourcePropertyEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSourceProperty_Value() {
        return (EAttribute)jdbcSourcePropertyEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcDriver() {
        return jdbcDriverEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcDriver_JdbcDriverContainer() {
        return (EReference)jdbcDriverEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcDriver_Name() {
        return (EAttribute)jdbcDriverEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcDriver_UrlSyntax() {
        return (EAttribute)jdbcDriverEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcDriver_JarFileUris() {
        return (EAttribute)jdbcDriverEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcDriver_AvailableDriverClassNames() {
        return (EAttribute)jdbcDriverEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcDriver_PreferredDriverClassName() {
        return (EAttribute)jdbcDriverEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcSource() {
        return jdbcSourceEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcSource_JdbcDriver() {
        return (EReference)jdbcSourceEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcSource_Properties() {
        return (EReference)jdbcSourceEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcSource_JdbcSourceContainer() {
        return (EReference)jdbcSourceEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcSource_ImportSettings() {
        return (EReference)jdbcSourceEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSource_Name() {
        return (EAttribute)jdbcSourceEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSource_DriverName() {
        return (EAttribute)jdbcSourceEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSource_DriverClass() {
        return (EAttribute)jdbcSourceEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSource_Username() {
        return (EAttribute)jdbcSourceEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcSource_Url() {
        return (EAttribute)jdbcSourceEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcDriverContainer() {
        return jdbcDriverContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcDriverContainer_JdbcDrivers() {
        return (EReference)jdbcDriverContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcSourceContainer() {
        return jdbcSourceContainerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcSourceContainer_JdbcSources() {
        return (EReference)jdbcSourceContainerEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcImportSettings() {
        return jdbcImportSettingsEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcImportSettings_Source() {
        return (EReference)jdbcImportSettingsEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcImportSettings_Options() {
        return (EReference)jdbcImportSettingsEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_CreateCatalogsInModel() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_CreateSchemasInModel() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_ConvertCaseInModel() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_GenerateSourceNamesInModel() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludedCatalogPaths() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludedSchemaPaths() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_ExcludedObjectPaths() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludeForeignKeys() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludeIndexes() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludeProcedures() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludeApproximateIndexes() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(12);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludeUniqueIndexes() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(13);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportSettings_IncludedTableTypes() {
        return (EAttribute)jdbcImportSettingsEClass.getEStructuralFeatures().get(14);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getJdbcImportOptions() {
        return jdbcImportOptionsEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getJdbcImportOptions_ImportSettings() {
        return (EReference)jdbcImportOptionsEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportOptions_Name() {
        return (EAttribute)jdbcImportOptionsEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getJdbcImportOptions_Value() {
        return (EAttribute)jdbcImportOptionsEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getCaseConversion() {
        return caseConversionEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getSourceNames() {
        return sourceNamesEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JdbcFactory getJdbcFactory() {
        return (JdbcFactory)getEFactoryInstance();
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
        jdbcSourcePropertyEClass = createEClass(JDBC_SOURCE_PROPERTY);
        createEReference(jdbcSourcePropertyEClass, JDBC_SOURCE_PROPERTY__SOURCE);
        createEAttribute(jdbcSourcePropertyEClass, JDBC_SOURCE_PROPERTY__NAME);
        createEAttribute(jdbcSourcePropertyEClass, JDBC_SOURCE_PROPERTY__VALUE);

        jdbcDriverEClass = createEClass(JDBC_DRIVER);
        createEReference(jdbcDriverEClass, JDBC_DRIVER__JDBC_DRIVER_CONTAINER);
        createEAttribute(jdbcDriverEClass, JDBC_DRIVER__NAME);
        createEAttribute(jdbcDriverEClass, JDBC_DRIVER__URL_SYNTAX);
        createEAttribute(jdbcDriverEClass, JDBC_DRIVER__JAR_FILE_URIS);
        createEAttribute(jdbcDriverEClass, JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES);
        createEAttribute(jdbcDriverEClass, JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME);

        jdbcSourceEClass = createEClass(JDBC_SOURCE);
        createEReference(jdbcSourceEClass, JDBC_SOURCE__JDBC_DRIVER);
        createEReference(jdbcSourceEClass, JDBC_SOURCE__PROPERTIES);
        createEReference(jdbcSourceEClass, JDBC_SOURCE__JDBC_SOURCE_CONTAINER);
        createEReference(jdbcSourceEClass, JDBC_SOURCE__IMPORT_SETTINGS);
        createEAttribute(jdbcSourceEClass, JDBC_SOURCE__NAME);
        createEAttribute(jdbcSourceEClass, JDBC_SOURCE__DRIVER_NAME);
        createEAttribute(jdbcSourceEClass, JDBC_SOURCE__DRIVER_CLASS);
        createEAttribute(jdbcSourceEClass, JDBC_SOURCE__USERNAME);
        createEAttribute(jdbcSourceEClass, JDBC_SOURCE__URL);

        jdbcDriverContainerEClass = createEClass(JDBC_DRIVER_CONTAINER);
        createEReference(jdbcDriverContainerEClass, JDBC_DRIVER_CONTAINER__JDBC_DRIVERS);

        jdbcSourceContainerEClass = createEClass(JDBC_SOURCE_CONTAINER);
        createEReference(jdbcSourceContainerEClass, JDBC_SOURCE_CONTAINER__JDBC_SOURCES);

        jdbcImportSettingsEClass = createEClass(JDBC_IMPORT_SETTINGS);
        createEReference(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__SOURCE);
        createEReference(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__OPTIONS);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES);
        createEAttribute(jdbcImportSettingsEClass, JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES);

        jdbcImportOptionsEClass = createEClass(JDBC_IMPORT_OPTIONS);
        createEReference(jdbcImportOptionsEClass, JDBC_IMPORT_OPTIONS__IMPORT_SETTINGS);
        createEAttribute(jdbcImportOptionsEClass, JDBC_IMPORT_OPTIONS__NAME);
        createEAttribute(jdbcImportOptionsEClass, JDBC_IMPORT_OPTIONS__VALUE);

        // Create enums
        caseConversionEEnum = createEEnum(CASE_CONVERSION);
        sourceNamesEEnum = createEEnum(SOURCE_NAMES);
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

        // Add supertypes to classes

        // Initialize classes and features; add operations and parameters
        initEClass(jdbcSourcePropertyEClass, JdbcSourceProperty.class, "JdbcSourceProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcSourceProperty_Source(), this.getJdbcSource(), this.getJdbcSource_Properties(), "source", null, 0, 1, JdbcSourceProperty.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSourceProperty_Name(), ecorePackage.getEString(), "name", null, 0, 1, JdbcSourceProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSourceProperty_Value(), ecorePackage.getEString(), "value", null, 0, 1, JdbcSourceProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(jdbcDriverEClass, JdbcDriver.class, "JdbcDriver", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcDriver_JdbcDriverContainer(), this.getJdbcDriverContainer(), this.getJdbcDriverContainer_JdbcDrivers(), "jdbcDriverContainer", null, 0, 1, JdbcDriver.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcDriver_Name(), ecorePackage.getEString(), "name", null, 0, 1, JdbcDriver.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcDriver_UrlSyntax(), ecorePackage.getEString(), "urlSyntax", null, 0, 1, JdbcDriver.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcDriver_JarFileUris(), ecorePackage.getEString(), "jarFileUris", null, 0, -1, JdbcDriver.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcDriver_AvailableDriverClassNames(), ecorePackage.getEString(), "availableDriverClassNames", null, 0, -1, JdbcDriver.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcDriver_PreferredDriverClassName(), ecorePackage.getEString(), "preferredDriverClassName", null, 0, 1, JdbcDriver.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(jdbcSourceEClass, JdbcSource.class, "JdbcSource", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcSource_JdbcDriver(), this.getJdbcDriver(), null, "jdbcDriver", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getJdbcSource_Properties(), this.getJdbcSourceProperty(), this.getJdbcSourceProperty_Source(), "properties", null, 0, -1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getJdbcSource_JdbcSourceContainer(), this.getJdbcSourceContainer(), this.getJdbcSourceContainer_JdbcSources(), "jdbcSourceContainer", null, 0, 1, JdbcSource.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getJdbcSource_ImportSettings(), this.getJdbcImportSettings(), this.getJdbcImportSettings_Source(), "importSettings", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSource_Name(), ecorePackage.getEString(), "name", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSource_DriverName(), ecorePackage.getEString(), "driverName", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSource_DriverClass(), ecorePackage.getEString(), "driverClass", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSource_Username(), ecorePackage.getEString(), "username", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcSource_Url(), ecorePackage.getEString(), "url", null, 0, 1, JdbcSource.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(jdbcDriverContainerEClass, JdbcDriverContainer.class, "JdbcDriverContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcDriverContainer_JdbcDrivers(), this.getJdbcDriver(), this.getJdbcDriver_JdbcDriverContainer(), "jdbcDrivers", null, 0, -1, JdbcDriverContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(jdbcSourceContainerEClass, JdbcSourceContainer.class, "JdbcSourceContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcSourceContainer_JdbcSources(), this.getJdbcSource(), this.getJdbcSource_JdbcSourceContainer(), "jdbcSources", null, 0, -1, JdbcSourceContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(jdbcImportSettingsEClass, JdbcImportSettings.class, "JdbcImportSettings", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcImportSettings_Source(), this.getJdbcSource(), this.getJdbcSource_ImportSettings(), "source", null, 0, 1, JdbcImportSettings.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEReference(getJdbcImportSettings_Options(), this.getJdbcImportOptions(), this.getJdbcImportOptions_ImportSettings(), "options", null, 0, -1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportSettings_CreateCatalogsInModel(), ecorePackage.getEBoolean(), "createCatalogsInModel", "true", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_CreateSchemasInModel(), ecorePackage.getEBoolean(), "createSchemasInModel", "true", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_ConvertCaseInModel(), this.getCaseConversion(), "convertCaseInModel", null, 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportSettings_GenerateSourceNamesInModel(), this.getSourceNames(), "generateSourceNamesInModel", "UNQUALIFIED", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_IncludedCatalogPaths(), ecorePackage.getEString(), "includedCatalogPaths", null, 0, -1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportSettings_IncludedSchemaPaths(), ecorePackage.getEString(), "includedSchemaPaths", null, 0, -1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportSettings_ExcludedObjectPaths(), ecorePackage.getEString(), "excludedObjectPaths", null, 0, -1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportSettings_IncludeForeignKeys(), ecorePackage.getEBoolean(), "includeForeignKeys", "true", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_IncludeIndexes(), ecorePackage.getEBoolean(), "includeIndexes", "true", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_IncludeProcedures(), ecorePackage.getEBoolean(), "includeProcedures", "false", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_IncludeApproximateIndexes(), ecorePackage.getEBoolean(), "includeApproximateIndexes", "true", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_IncludeUniqueIndexes(), ecorePackage.getEBoolean(), "includeUniqueIndexes", "false", 0, 1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$ //$NON-NLS-2$
        initEAttribute(getJdbcImportSettings_IncludedTableTypes(), ecorePackage.getEString(), "includedTableTypes", null, 0, -1, JdbcImportSettings.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(jdbcImportOptionsEClass, JdbcImportOptions.class, "JdbcImportOptions", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(getJdbcImportOptions_ImportSettings(), this.getJdbcImportSettings(), this.getJdbcImportSettings_Options(), "importSettings", null, 1, 1, JdbcImportOptions.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportOptions_Name(), ecorePackage.getEString(), "name", null, 0, 1, JdbcImportOptions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(getJdbcImportOptions_Value(), ecorePackage.getEString(), "value", null, 0, 1, JdbcImportOptions.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize enums and add enum literals
        initEEnum(caseConversionEEnum, CaseConversion.class, "CaseConversion"); //$NON-NLS-1$
        addEEnumLiteral(caseConversionEEnum, CaseConversion.NONE_LITERAL);
        addEEnumLiteral(caseConversionEEnum, CaseConversion.TO_UPPERCASE_LITERAL);
        addEEnumLiteral(caseConversionEEnum, CaseConversion.TO_LOWERCASE_LITERAL);

        initEEnum(sourceNamesEEnum, SourceNames.class, "SourceNames"); //$NON-NLS-1$
        addEEnumLiteral(sourceNamesEEnum, SourceNames.NONE_LITERAL);
        addEEnumLiteral(sourceNamesEEnum, SourceNames.UNQUALIFIED_LITERAL);
        addEEnumLiteral(sourceNamesEEnum, SourceNames.FULLY_QUALIFIED_LITERAL);

        // Create resource
        createResource(eNS_URI);
    }

} //JdbcPackageImpl
