/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc;

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
 * @see org.teiid.designer.jdbc.JdbcFactory
 * @generated
 *
 * @since 8.0
 */
public interface JdbcPackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "jdbc"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/JDBC"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "jdbc"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    JdbcPackage eINSTANCE = org.teiid.designer.jdbc.impl.JdbcPackageImpl.init();

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcSourcePropertyImpl <em>Source Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcSourcePropertyImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcSourceProperty()
     * @generated
     */
    int JDBC_SOURCE_PROPERTY = 0;

    /**
     * The feature id for the '<em><b>Source</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_PROPERTY__SOURCE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_PROPERTY__NAME = 1;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_PROPERTY__VALUE = 2;

    /**
     * The number of structural features of the the '<em>Source Property</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_PROPERTY_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcDriverImpl <em>Driver</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcDriverImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcDriver()
     * @generated
     */
    int JDBC_DRIVER = 1;

    /**
     * The feature id for the '<em><b>Jdbc Driver Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER__JDBC_DRIVER_CONTAINER = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER__NAME = 1;

    /**
     * The feature id for the '<em><b>Url Syntax</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER__URL_SYNTAX = 2;

    /**
     * The feature id for the '<em><b>Jar File Uris</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER__JAR_FILE_URIS = 3;

    /**
     * The feature id for the '<em><b>Available Driver Class Names</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER__AVAILABLE_DRIVER_CLASS_NAMES = 4;

    /**
     * The feature id for the '<em><b>Preferred Driver Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER__PREFERRED_DRIVER_CLASS_NAME = 5;

    /**
     * The number of structural features of the the '<em>Driver</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER_FEATURE_COUNT = 6;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcSourceImpl <em>Source</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcSourceImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcSource()
     * @generated
     */
    int JDBC_SOURCE = 2;

    /**
     * The feature id for the '<em><b>Jdbc Driver</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__JDBC_DRIVER = 0;

    /**
     * The feature id for the '<em><b>Properties</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__PROPERTIES = 1;

    /**
     * The feature id for the '<em><b>Jdbc Source Container</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__JDBC_SOURCE_CONTAINER = 2;

    /**
     * The feature id for the '<em><b>Import Settings</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__IMPORT_SETTINGS = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__NAME = 4;

    /**
     * The feature id for the '<em><b>Driver Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__DRIVER_NAME = 5;

    /**
     * The feature id for the '<em><b>Driver Class</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__DRIVER_CLASS = 6;

    /**
     * The feature id for the '<em><b>Username</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__USERNAME = 7;

    /**
     * The feature id for the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE__URL = 8;

    /**
     * The number of structural features of the the '<em>Source</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_FEATURE_COUNT = 9;


    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcDriverContainerImpl <em>Driver Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcDriverContainerImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcDriverContainer()
     * @generated
     */
    int JDBC_DRIVER_CONTAINER = 3;

    /**
     * The feature id for the '<em><b>Jdbc Drivers</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER_CONTAINER__JDBC_DRIVERS = 0;

    /**
     * The number of structural features of the the '<em>Driver Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_DRIVER_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcSourceContainerImpl <em>Source Container</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcSourceContainerImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcSourceContainer()
     * @generated
     */
    int JDBC_SOURCE_CONTAINER = 4;

    /**
     * The feature id for the '<em><b>Jdbc Sources</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_CONTAINER__JDBC_SOURCES = 0;

    /**
     * The number of structural features of the the '<em>Source Container</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_SOURCE_CONTAINER_FEATURE_COUNT = 1;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcImportSettingsImpl <em>Import Settings</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcImportSettingsImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcImportSettings()
     * @generated
     */
    int JDBC_IMPORT_SETTINGS = 5;

    /**
     * The feature id for the '<em><b>Source</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__SOURCE = 0;

    /**
     * The feature id for the '<em><b>Options</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__OPTIONS = 1;

    /**
     * The feature id for the '<em><b>Create Catalogs In Model</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__CREATE_CATALOGS_IN_MODEL = 2;

    /**
     * The feature id for the '<em><b>Create Schemas In Model</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__CREATE_SCHEMAS_IN_MODEL = 3;

    /**
     * The feature id for the '<em><b>Convert Case In Model</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__CONVERT_CASE_IN_MODEL = 4;

    /**
     * The feature id for the '<em><b>Generate Source Names In Model</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__GENERATE_SOURCE_NAMES_IN_MODEL = 5;

    /**
     * The feature id for the '<em><b>Included Catalog Paths</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDED_CATALOG_PATHS = 6;

    /**
     * The feature id for the '<em><b>Included Schema Paths</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDED_SCHEMA_PATHS = 7;

    /**
     * The feature id for the '<em><b>Excluded Object Paths</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__EXCLUDED_OBJECT_PATHS = 8;

    /**
     * The feature id for the '<em><b>Include Foreign Keys</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDE_FOREIGN_KEYS = 9;

    /**
     * The feature id for the '<em><b>Include Indexes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDE_INDEXES = 10;

    /**
     * The feature id for the '<em><b>Include Procedures</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDE_PROCEDURES = 11;

    /**
     * The feature id for the '<em><b>Include Approximate Indexes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDE_APPROXIMATE_INDEXES = 12;

    /**
     * The feature id for the '<em><b>Include Unique Indexes</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDE_UNIQUE_INDEXES = 13;

    /**
     * The feature id for the '<em><b>Included Table Types</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS__INCLUDED_TABLE_TYPES = 14;

    /**
     * The number of structural features of the the '<em>Import Settings</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_SETTINGS_FEATURE_COUNT = 15;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.impl.JdbcImportOptionsImpl <em>Import Options</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.impl.JdbcImportOptionsImpl
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getJdbcImportOptions()
     * @generated
     */
    int JDBC_IMPORT_OPTIONS = 6;

    /**
     * The feature id for the '<em><b>Import Settings</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_OPTIONS__IMPORT_SETTINGS = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_OPTIONS__NAME = 1;

    /**
     * The feature id for the '<em><b>Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_OPTIONS__VALUE = 2;

    /**
     * The number of structural features of the the '<em>Import Options</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JDBC_IMPORT_OPTIONS_FEATURE_COUNT = 3;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.CaseConversion <em>Case Conversion</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.CaseConversion
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getCaseConversion()
     * @generated
     */
    int CASE_CONVERSION = 7;

    /**
     * The meta object id for the '{@link org.teiid.designer.jdbc.SourceNames <em>Source Names</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.teiid.designer.jdbc.SourceNames
     * @see org.teiid.designer.jdbc.impl.JdbcPackageImpl#getSourceNames()
     * @generated
     */
    int SOURCE_NAMES = 8;


    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcSourceProperty <em>Source Property</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Source Property</em>'.
     * @see org.teiid.designer.jdbc.JdbcSourceProperty
     * @generated
     */
    EClass getJdbcSourceProperty();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSourceProperty#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.teiid.designer.jdbc.JdbcSourceProperty#getName()
     * @see #getJdbcSourceProperty()
     * @generated
     */
    EAttribute getJdbcSourceProperty_Name();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSourceProperty#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.teiid.designer.jdbc.JdbcSourceProperty#getValue()
     * @see #getJdbcSourceProperty()
     * @generated
     */
    EAttribute getJdbcSourceProperty_Value();

    /**
     * Returns the meta object for the container reference '{@link org.teiid.designer.jdbc.JdbcSourceProperty#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Source</em>'.
     * @see org.teiid.designer.jdbc.JdbcSourceProperty#getSource()
     * @see #getJdbcSourceProperty()
     * @generated
     */
    EReference getJdbcSourceProperty_Source();

    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcDriver <em>Driver</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Driver</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver
     * @generated
     */
    EClass getJdbcDriver();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcDriver#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver#getName()
     * @see #getJdbcDriver()
     * @generated
     */
    EAttribute getJdbcDriver_Name();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcDriver#getUrlSyntax <em>Url Syntax</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Url Syntax</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver#getUrlSyntax()
     * @see #getJdbcDriver()
     * @generated
     */
    EAttribute getJdbcDriver_UrlSyntax();

    /**
     * Returns the meta object for the attribute list '{@link org.teiid.designer.jdbc.JdbcDriver#getJarFileUris <em>Jar File Uris</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Jar File Uris</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver#getJarFileUris()
     * @see #getJdbcDriver()
     * @generated
     */
    EAttribute getJdbcDriver_JarFileUris();

    /**
     * Returns the meta object for the attribute list '{@link org.teiid.designer.jdbc.JdbcDriver#getAvailableDriverClassNames <em>Available Driver Class Names</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Available Driver Class Names</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver#getAvailableDriverClassNames()
     * @see #getJdbcDriver()
     * @generated
     */
    EAttribute getJdbcDriver_AvailableDriverClassNames();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcDriver#getPreferredDriverClassName <em>Preferred Driver Class Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Preferred Driver Class Name</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver#getPreferredDriverClassName()
     * @see #getJdbcDriver()
     * @generated
     */
    EAttribute getJdbcDriver_PreferredDriverClassName();

    /**
     * Returns the meta object for the container reference '{@link org.teiid.designer.jdbc.JdbcDriver#getJdbcDriverContainer <em>Jdbc Driver Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Jdbc Driver Container</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriver#getJdbcDriverContainer()
     * @see #getJdbcDriver()
     * @generated
     */
    EReference getJdbcDriver_JdbcDriverContainer();

    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Source</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource
     * @generated
     */
    EClass getJdbcSource();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSource#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getName()
     * @see #getJdbcSource()
     * @generated
     */
    EAttribute getJdbcSource_Name();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSource#getDriverName <em>Driver Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Driver Name</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getDriverName()
     * @see #getJdbcSource()
     * @generated
     */
    EAttribute getJdbcSource_DriverName();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSource#getDriverClass <em>Driver Class</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Driver Class</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getDriverClass()
     * @see #getJdbcSource()
     * @generated
     */
    EAttribute getJdbcSource_DriverClass();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSource#getUsername <em>Username</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Username</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getUsername()
     * @see #getJdbcSource()
     * @generated
     */
    EAttribute getJdbcSource_Username();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcSource#getUrl <em>Url</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Url</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getUrl()
     * @see #getJdbcSource()
     * @generated
     */
    EAttribute getJdbcSource_Url();

    /**
     * Returns the meta object for the reference '{@link org.teiid.designer.jdbc.JdbcSource#getJdbcDriver <em>Jdbc Driver</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Jdbc Driver</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getJdbcDriver()
     * @see #getJdbcSource()
     * @generated
     */
    EReference getJdbcSource_JdbcDriver();

    /**
     * Returns the meta object for the containment reference list '{@link org.teiid.designer.jdbc.JdbcSource#getProperties <em>Properties</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Properties</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getProperties()
     * @see #getJdbcSource()
     * @generated
     */
    EReference getJdbcSource_Properties();

    /**
     * Returns the meta object for the container reference '{@link org.teiid.designer.jdbc.JdbcSource#getJdbcSourceContainer <em>Jdbc Source Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Jdbc Source Container</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getJdbcSourceContainer()
     * @see #getJdbcSource()
     * @generated
     */
    EReference getJdbcSource_JdbcSourceContainer();

    /**
     * Returns the meta object for the containment reference '{@link org.teiid.designer.jdbc.JdbcSource#getImportSettings <em>Import Settings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Import Settings</em>'.
     * @see org.teiid.designer.jdbc.JdbcSource#getImportSettings()
     * @see #getJdbcSource()
     * @generated
     */
    EReference getJdbcSource_ImportSettings();

    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcDriverContainer <em>Driver Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Driver Container</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriverContainer
     * @generated
     */
    EClass getJdbcDriverContainer();

    /**
     * Returns the meta object for the containment reference list '{@link org.teiid.designer.jdbc.JdbcDriverContainer#getJdbcDrivers <em>Jdbc Drivers</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Jdbc Drivers</em>'.
     * @see org.teiid.designer.jdbc.JdbcDriverContainer#getJdbcDrivers()
     * @see #getJdbcDriverContainer()
     * @generated
     */
    EReference getJdbcDriverContainer_JdbcDrivers();

    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcSourceContainer <em>Source Container</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Source Container</em>'.
     * @see org.teiid.designer.jdbc.JdbcSourceContainer
     * @generated
     */
    EClass getJdbcSourceContainer();

    /**
     * Returns the meta object for the containment reference list '{@link org.teiid.designer.jdbc.JdbcSourceContainer#getJdbcSources <em>Jdbc Sources</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Jdbc Sources</em>'.
     * @see org.teiid.designer.jdbc.JdbcSourceContainer#getJdbcSources()
     * @see #getJdbcSourceContainer()
     * @generated
     */
    EReference getJdbcSourceContainer_JdbcSources();

    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcImportSettings <em>Import Settings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Import Settings</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings
     * @generated
     */
    EClass getJdbcImportSettings();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isCreateCatalogsInModel <em>Create Catalogs In Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Create Catalogs In Model</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isCreateCatalogsInModel()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_CreateCatalogsInModel();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isCreateSchemasInModel <em>Create Schemas In Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Create Schemas In Model</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isCreateSchemasInModel()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_CreateSchemasInModel();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#getConvertCaseInModel <em>Convert Case In Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Convert Case In Model</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getConvertCaseInModel()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_ConvertCaseInModel();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#getGenerateSourceNamesInModel <em>Generate Source Names In Model</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Generate Source Names In Model</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getGenerateSourceNamesInModel()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_GenerateSourceNamesInModel();

    /**
     * Returns the meta object for the attribute list '{@link org.teiid.designer.jdbc.JdbcImportSettings#getIncludedCatalogPaths <em>Included Catalog Paths</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Included Catalog Paths</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getIncludedCatalogPaths()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludedCatalogPaths();

    /**
     * Returns the meta object for the attribute list '{@link org.teiid.designer.jdbc.JdbcImportSettings#getIncludedSchemaPaths <em>Included Schema Paths</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Included Schema Paths</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getIncludedSchemaPaths()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludedSchemaPaths();

    /**
     * Returns the meta object for the attribute list '{@link org.teiid.designer.jdbc.JdbcImportSettings#getExcludedObjectPaths <em>Excluded Object Paths</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Excluded Object Paths</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getExcludedObjectPaths()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_ExcludedObjectPaths();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isIncludeForeignKeys <em>Include Foreign Keys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Foreign Keys</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isIncludeForeignKeys()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludeForeignKeys();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isIncludeIndexes <em>Include Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Indexes</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isIncludeIndexes()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludeIndexes();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isIncludeProcedures <em>Include Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Procedures</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isIncludeProcedures()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludeProcedures();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isIncludeApproximateIndexes <em>Include Approximate Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Approximate Indexes</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isIncludeApproximateIndexes()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludeApproximateIndexes();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportSettings#isIncludeUniqueIndexes <em>Include Unique Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Include Unique Indexes</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#isIncludeUniqueIndexes()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludeUniqueIndexes();

    /**
     * Returns the meta object for the attribute list '{@link org.teiid.designer.jdbc.JdbcImportSettings#getIncludedTableTypes <em>Included Table Types</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Included Table Types</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getIncludedTableTypes()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EAttribute getJdbcImportSettings_IncludedTableTypes();

    /**
     * Returns the meta object for the container reference '{@link org.teiid.designer.jdbc.JdbcImportSettings#getSource <em>Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Source</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getSource()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EReference getJdbcImportSettings_Source();

    /**
     * Returns the meta object for the containment reference list '{@link org.teiid.designer.jdbc.JdbcImportSettings#getOptions <em>Options</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Options</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportSettings#getOptions()
     * @see #getJdbcImportSettings()
     * @generated
     */
    EReference getJdbcImportSettings_Options();

    /**
     * Returns the meta object for class '{@link org.teiid.designer.jdbc.JdbcImportOptions <em>Import Options</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Import Options</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportOptions
     * @generated
     */
    EClass getJdbcImportOptions();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportOptions#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportOptions#getName()
     * @see #getJdbcImportOptions()
     * @generated
     */
    EAttribute getJdbcImportOptions_Name();

    /**
     * Returns the meta object for the attribute '{@link org.teiid.designer.jdbc.JdbcImportOptions#getValue <em>Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Value</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportOptions#getValue()
     * @see #getJdbcImportOptions()
     * @generated
     */
    EAttribute getJdbcImportOptions_Value();

    /**
     * Returns the meta object for the container reference '{@link org.teiid.designer.jdbc.JdbcImportOptions#getImportSettings <em>Import Settings</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Import Settings</em>'.
     * @see org.teiid.designer.jdbc.JdbcImportOptions#getImportSettings()
     * @see #getJdbcImportOptions()
     * @generated
     */
    EReference getJdbcImportOptions_ImportSettings();

    /**
     * Returns the meta object for enum '{@link org.teiid.designer.jdbc.CaseConversion <em>Case Conversion</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Case Conversion</em>'.
     * @see org.teiid.designer.jdbc.CaseConversion
     * @generated
     */
    EEnum getCaseConversion();

    /**
     * Returns the meta object for enum '{@link org.teiid.designer.jdbc.SourceNames <em>Source Names</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Source Names</em>'.
     * @see org.teiid.designer.jdbc.SourceNames
     * @generated
     */
    EEnum getSourceNames();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    JdbcFactory getJdbcFactory();

} //JdbcPackage
