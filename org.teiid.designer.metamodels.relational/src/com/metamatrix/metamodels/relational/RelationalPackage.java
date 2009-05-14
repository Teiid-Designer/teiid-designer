/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational;

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
 * @see com.metamatrix.metamodels.relational.RelationalFactory
 * @generated
 */
public interface RelationalPackage extends EPackage{
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "relational"; //$NON-NLS-1$

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://www.metamatrix.com/metamodels/Relational"; //$NON-NLS-1$

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "relational"; //$NON-NLS-1$

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    RelationalPackage eINSTANCE = com.metamatrix.metamodels.relational.impl.RelationalPackageImpl.init();

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.RelationalEntityImpl <em>Entity</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.RelationalEntityImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getRelationalEntity()
     * @generated
     */
    int RELATIONAL_ENTITY = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONAL_ENTITY__NAME = 0;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONAL_ENTITY__NAME_IN_SOURCE = 1;

    /**
     * The number of structural features of the the '<em>Entity</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONAL_ENTITY_FEATURE_COUNT = 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ColumnSetImpl <em>Column Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ColumnSetImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getColumnSet()
     * @generated
     */
    int COLUMN_SET = 18;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_SET__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_SET__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_SET__COLUMNS = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Column Set</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_SET_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.TableImpl <em>Table</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.TableImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getTable()
     * @generated
     */
    int TABLE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__NAME = COLUMN_SET__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__NAME_IN_SOURCE = COLUMN_SET__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__COLUMNS = COLUMN_SET__COLUMNS;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__SYSTEM = COLUMN_SET_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Cardinality</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__CARDINALITY = COLUMN_SET_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Supports Update</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__SUPPORTS_UPDATE = COLUMN_SET_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Materialized</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int TABLE__MATERIALIZED = COLUMN_SET_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Schema</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__SCHEMA = COLUMN_SET_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Access Patterns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__ACCESS_PATTERNS = COLUMN_SET_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__CATALOG = COLUMN_SET_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Logical Relationships</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE__LOGICAL_RELATIONSHIPS = COLUMN_SET_FEATURE_COUNT + 7;

    /**
     * The number of structural features of the the '<em>Table</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TABLE_FEATURE_COUNT = COLUMN_SET_FEATURE_COUNT + 8;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ColumnImpl <em>Column</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ColumnImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getColumn()
     * @generated
     */
    int COLUMN = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Native Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__NATIVE_TYPE = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__LENGTH = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Fixed Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__FIXED_LENGTH = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Precision</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__PRECISION = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Scale</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__SCALE = RELATIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Nullable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__NULLABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Auto Incremented</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__AUTO_INCREMENTED = RELATIONAL_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Default Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__DEFAULT_VALUE = RELATIONAL_ENTITY_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Minimum Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__MINIMUM_VALUE = RELATIONAL_ENTITY_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Maximum Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__MAXIMUM_VALUE = RELATIONAL_ENTITY_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Format</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__FORMAT = RELATIONAL_ENTITY_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Character Set Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__CHARACTER_SET_NAME = RELATIONAL_ENTITY_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Collation Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__COLLATION_NAME = RELATIONAL_ENTITY_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Selectable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__SELECTABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Updateable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__UPDATEABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 14;

    /**
     * The feature id for the '<em><b>Case Sensitive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__CASE_SENSITIVE = RELATIONAL_ENTITY_FEATURE_COUNT + 15;

    /**
     * The feature id for the '<em><b>Searchability</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__SEARCHABILITY = RELATIONAL_ENTITY_FEATURE_COUNT + 16;

    /**
     * The feature id for the '<em><b>Currency</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__CURRENCY = RELATIONAL_ENTITY_FEATURE_COUNT + 17;

    /**
     * The feature id for the '<em><b>Radix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__RADIX = RELATIONAL_ENTITY_FEATURE_COUNT + 18;

    /**
     * The feature id for the '<em><b>Signed</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__SIGNED = RELATIONAL_ENTITY_FEATURE_COUNT + 19;

    /**
     * The feature id for the '<em><b>Distinct Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__DISTINCT_VALUE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 20;

    /**
     * The feature id for the '<em><b>Null Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__NULL_VALUE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 21;

    /**
     * The feature id for the '<em><b>Unique Keys</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__UNIQUE_KEYS = RELATIONAL_ENTITY_FEATURE_COUNT + 22;

    /**
     * The feature id for the '<em><b>Indexes</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__INDEXES = RELATIONAL_ENTITY_FEATURE_COUNT + 23;

    /**
     * The feature id for the '<em><b>Foreign Keys</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__FOREIGN_KEYS = RELATIONAL_ENTITY_FEATURE_COUNT + 24;

    /**
     * The feature id for the '<em><b>Access Patterns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__ACCESS_PATTERNS = RELATIONAL_ENTITY_FEATURE_COUNT + 25;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__OWNER = RELATIONAL_ENTITY_FEATURE_COUNT + 26;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN__TYPE = RELATIONAL_ENTITY_FEATURE_COUNT + 27;

    /**
     * The number of structural features of the the '<em>Column</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMN_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 28;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.SchemaImpl <em>Schema</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.SchemaImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getSchema()
     * @generated
     */
    int SCHEMA = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Tables</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__TABLES = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__CATALOG = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Procedures</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__PROCEDURES = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Indexes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__INDEXES = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Logical Relationships</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA__LOGICAL_RELATIONSHIPS = RELATIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Schema</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SCHEMA_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.UniqueKeyImpl <em>Unique Key</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.UniqueKeyImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getUniqueKey()
     * @generated
     */
    int UNIQUE_KEY = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_KEY__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_KEY__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_KEY__COLUMNS = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Foreign Keys</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_KEY__FOREIGN_KEYS = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Unique Key</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_KEY_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.PrimaryKeyImpl <em>Primary Key</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.PrimaryKeyImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getPrimaryKey()
     * @generated
     */
    int PRIMARY_KEY = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRIMARY_KEY__NAME = UNIQUE_KEY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRIMARY_KEY__NAME_IN_SOURCE = UNIQUE_KEY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRIMARY_KEY__COLUMNS = UNIQUE_KEY__COLUMNS;

    /**
     * The feature id for the '<em><b>Foreign Keys</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRIMARY_KEY__FOREIGN_KEYS = UNIQUE_KEY__FOREIGN_KEYS;

    /**
     * The feature id for the '<em><b>Table</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRIMARY_KEY__TABLE = UNIQUE_KEY_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Primary Key</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRIMARY_KEY_FEATURE_COUNT = UNIQUE_KEY_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.RelationshipImpl <em>Relationship</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.RelationshipImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getRelationship()
     * @generated
     */
    int RELATIONSHIP = 14;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The number of structural features of the the '<em>Relationship</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int RELATIONSHIP_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ForeignKeyImpl <em>Foreign Key</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ForeignKeyImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getForeignKey()
     * @generated
     */
    int FOREIGN_KEY = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__NAME = RELATIONSHIP__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__NAME_IN_SOURCE = RELATIONSHIP__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Foreign Key Multiplicity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__FOREIGN_KEY_MULTIPLICITY = RELATIONSHIP_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Primary Key Multiplicity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__PRIMARY_KEY_MULTIPLICITY = RELATIONSHIP_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Columns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__COLUMNS = RELATIONSHIP_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Unique Key</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__UNIQUE_KEY = RELATIONSHIP_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Table</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY__TABLE = RELATIONSHIP_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Foreign Key</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int FOREIGN_KEY_FEATURE_COUNT = RELATIONSHIP_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ViewImpl <em>View</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ViewImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getView()
     * @generated
     */
    int VIEW = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__NAME = TABLE__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__NAME_IN_SOURCE = TABLE__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__COLUMNS = TABLE__COLUMNS;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__SYSTEM = TABLE__SYSTEM;

    /**
     * The feature id for the '<em><b>Cardinality</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__CARDINALITY = TABLE__CARDINALITY;

    /**
     * The feature id for the '<em><b>Supports Update</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__SUPPORTS_UPDATE = TABLE__SUPPORTS_UPDATE;

    /**
     * The feature id for the '<em><b>Materialized</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int VIEW__MATERIALIZED = TABLE__MATERIALIZED;

    /**
     * The feature id for the '<em><b>Schema</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__SCHEMA = TABLE__SCHEMA;

    /**
     * The feature id for the '<em><b>Access Patterns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__ACCESS_PATTERNS = TABLE__ACCESS_PATTERNS;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__CATALOG = TABLE__CATALOG;

    /**
     * The feature id for the '<em><b>Logical Relationships</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW__LOGICAL_RELATIONSHIPS = TABLE__LOGICAL_RELATIONSHIPS;

    /**
     * The number of structural features of the the '<em>View</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VIEW_FEATURE_COUNT = TABLE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.CatalogImpl <em>Catalog</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.CatalogImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getCatalog()
     * @generated
     */
    int CATALOG = 8;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Schemas</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__SCHEMAS = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Procedures</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__PROCEDURES = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Indexes</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__INDEXES = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Tables</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__TABLES = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Logical Relationships</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG__LOGICAL_RELATIONSHIPS = RELATIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The number of structural features of the the '<em>Catalog</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CATALOG_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ProcedureImpl <em>Procedure</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ProcedureImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getProcedure()
     * @generated
     */
    int PROCEDURE = 9;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Function</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__FUNCTION = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Schema</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__SCHEMA = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__PARAMETERS = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__CATALOG = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Result</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__RESULT = RELATIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Update Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE__UPDATE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The number of structural features of the the '<em>Procedure</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 6;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.IndexImpl <em>Index</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.IndexImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getIndex()
     * @generated
     */
    int INDEX = 10;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Filter Condition</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__FILTER_CONDITION = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Nullable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__NULLABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Auto Update</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__AUTO_UPDATE = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Unique</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__UNIQUE = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Schema</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__SCHEMA = RELATIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Columns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__COLUMNS = RELATIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX__CATALOG = RELATIONAL_ENTITY_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the the '<em>Index</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INDEX_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 7;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl <em>Procedure Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ProcedureParameterImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getProcedureParameter()
     * @generated
     */
    int PROCEDURE_PARAMETER = 11;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Direction</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__DIRECTION = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Default Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__DEFAULT_VALUE = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Native Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__NATIVE_TYPE = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__LENGTH = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Precision</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__PRECISION = RELATIONAL_ENTITY_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Scale</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__SCALE = RELATIONAL_ENTITY_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Nullable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__NULLABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Radix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__RADIX = RELATIONAL_ENTITY_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Procedure</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__PROCEDURE = RELATIONAL_ENTITY_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER__TYPE = RELATIONAL_ENTITY_FEATURE_COUNT + 9;

    /**
     * The number of structural features of the the '<em>Procedure Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_PARAMETER_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 10;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.UniqueConstraintImpl <em>Unique Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.UniqueConstraintImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getUniqueConstraint()
     * @generated
     */
    int UNIQUE_CONSTRAINT = 12;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_CONSTRAINT__NAME = UNIQUE_KEY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_CONSTRAINT__NAME_IN_SOURCE = UNIQUE_KEY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_CONSTRAINT__COLUMNS = UNIQUE_KEY__COLUMNS;

    /**
     * The feature id for the '<em><b>Foreign Keys</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_CONSTRAINT__FOREIGN_KEYS = UNIQUE_KEY__FOREIGN_KEYS;

    /**
     * The feature id for the '<em><b>Table</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_CONSTRAINT__TABLE = UNIQUE_KEY_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Unique Constraint</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int UNIQUE_CONSTRAINT_FEATURE_COUNT = UNIQUE_KEY_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.AccessPatternImpl <em>Access Pattern</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.AccessPatternImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getAccessPattern()
     * @generated
     */
    int ACCESS_PATTERN = 13;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACCESS_PATTERN__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACCESS_PATTERN__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACCESS_PATTERN__COLUMNS = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Table</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACCESS_PATTERN__TABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the the '<em>Access Pattern</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ACCESS_PATTERN_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipImpl <em>Logical Relationship</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.LogicalRelationshipImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getLogicalRelationship()
     * @generated
     */
    int LOGICAL_RELATIONSHIP = 15;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP__NAME = RELATIONSHIP__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP__NAME_IN_SOURCE = RELATIONSHIP__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP__CATALOG = RELATIONSHIP_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Schema</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP__SCHEMA = RELATIONSHIP_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Ends</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP__ENDS = RELATIONSHIP_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Logical Relationship</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_FEATURE_COUNT = RELATIONSHIP_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.LogicalRelationshipEndImpl <em>Logical Relationship End</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.LogicalRelationshipEndImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getLogicalRelationshipEnd()
     * @generated
     */
    int LOGICAL_RELATIONSHIP_END = 16;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_END__NAME = RELATIONAL_ENTITY__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_END__NAME_IN_SOURCE = RELATIONAL_ENTITY__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Multiplicity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_END__MULTIPLICITY = RELATIONAL_ENTITY_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Table</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_END__TABLE = RELATIONAL_ENTITY_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Relationship</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_END__RELATIONSHIP = RELATIONAL_ENTITY_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Logical Relationship End</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int LOGICAL_RELATIONSHIP_END_FEATURE_COUNT = RELATIONAL_ENTITY_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.BaseTableImpl <em>Base Table</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.BaseTableImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getBaseTable()
     * @generated
     */
    int BASE_TABLE = 17;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__NAME = TABLE__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__NAME_IN_SOURCE = TABLE__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__COLUMNS = TABLE__COLUMNS;

    /**
     * The feature id for the '<em><b>System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__SYSTEM = TABLE__SYSTEM;

    /**
     * The feature id for the '<em><b>Cardinality</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__CARDINALITY = TABLE__CARDINALITY;

    /**
     * The feature id for the '<em><b>Supports Update</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__SUPPORTS_UPDATE = TABLE__SUPPORTS_UPDATE;

    /**
     * The feature id for the '<em><b>Materialized</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int BASE_TABLE__MATERIALIZED = TABLE__MATERIALIZED;

    /**
     * The feature id for the '<em><b>Schema</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__SCHEMA = TABLE__SCHEMA;

    /**
     * The feature id for the '<em><b>Access Patterns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__ACCESS_PATTERNS = TABLE__ACCESS_PATTERNS;

    /**
     * The feature id for the '<em><b>Catalog</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__CATALOG = TABLE__CATALOG;

    /**
     * The feature id for the '<em><b>Logical Relationships</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__LOGICAL_RELATIONSHIPS = TABLE__LOGICAL_RELATIONSHIPS;

    /**
     * The feature id for the '<em><b>Foreign Keys</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__FOREIGN_KEYS = TABLE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Primary Key</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__PRIMARY_KEY = TABLE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Unique Constraints</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE__UNIQUE_CONSTRAINTS = TABLE_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the the '<em>Base Table</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int BASE_TABLE_FEATURE_COUNT = TABLE_FEATURE_COUNT + 3;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.impl.ProcedureResultImpl <em>Procedure Result</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.impl.ProcedureResultImpl
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getProcedureResult()
     * @generated
     */
    int PROCEDURE_RESULT = 19;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_RESULT__NAME = COLUMN_SET__NAME;

    /**
     * The feature id for the '<em><b>Name In Source</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_RESULT__NAME_IN_SOURCE = COLUMN_SET__NAME_IN_SOURCE;

    /**
     * The feature id for the '<em><b>Columns</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_RESULT__COLUMNS = COLUMN_SET__COLUMNS;

    /**
     * The feature id for the '<em><b>Procedure</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_RESULT__PROCEDURE = COLUMN_SET_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the the '<em>Procedure Result</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PROCEDURE_RESULT_FEATURE_COUNT = COLUMN_SET_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.NullableType <em>Nullable Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.NullableType
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getNullableType()
     * @generated
     */
    int NULLABLE_TYPE = 20;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.DirectionKind <em>Direction Kind</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.DirectionKind
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getDirectionKind()
     * @generated
     */
    int DIRECTION_KIND = 21;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.MultiplicityKind <em>Multiplicity Kind</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.MultiplicityKind
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getMultiplicityKind()
     * @generated
     */
    int MULTIPLICITY_KIND = 22;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.SearchabilityType <em>Searchability Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.SearchabilityType
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getSearchabilityType()
     * @generated
     */
    int SEARCHABILITY_TYPE = 23;

    /**
     * The meta object id for the '{@link com.metamatrix.metamodels.relational.ProcedureUpdateCount <em>Procedure Update Count</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.metamatrix.metamodels.relational.ProcedureUpdateCount
     * @see com.metamatrix.metamodels.relational.impl.RelationalPackageImpl#getProcedureUpdateCount()
     * @generated
     */
    int PROCEDURE_UPDATE_COUNT = 24;


    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Table <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Table</em>'.
     * @see com.metamatrix.metamodels.relational.Table
     * @generated
     */
    EClass getTable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Table#isSystem <em>System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>System</em>'.
     * @see com.metamatrix.metamodels.relational.Table#isSystem()
     * @see #getTable()
     * @generated
     */
    EAttribute getTable_System();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Table#getCardinality <em>Cardinality</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Cardinality</em>'.
     * @see com.metamatrix.metamodels.relational.Table#getCardinality()
     * @see #getTable()
     * @generated
     */
    EAttribute getTable_Cardinality();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Table#isSupportsUpdate <em>Supports Update</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Supports Update</em>'.
     * @see com.metamatrix.metamodels.relational.Table#isSupportsUpdate()
     * @see #getTable()
     * @generated
     */
    EAttribute getTable_SupportsUpdate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Table#isMaterialized <em>Materialized</em>}'.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Materialized</em>'.
     * @see com.metamatrix.metamodels.relational.Table#isMaterialized()
     * @see #getTable()
     * @generated
     */
	EAttribute getTable_Materialized();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Table#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Schema</em>'.
     * @see com.metamatrix.metamodels.relational.Table#getSchema()
     * @see #getTable()
     * @generated
     */
    EReference getTable_Schema();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Table#getAccessPatterns <em>Access Patterns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Access Patterns</em>'.
     * @see com.metamatrix.metamodels.relational.Table#getAccessPatterns()
     * @see #getTable()
     * @generated
     */
    EReference getTable_AccessPatterns();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Table#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Catalog</em>'.
     * @see com.metamatrix.metamodels.relational.Table#getCatalog()
     * @see #getTable()
     * @generated
     */
    EReference getTable_Catalog();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.Table#getLogicalRelationships <em>Logical Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Logical Relationships</em>'.
     * @see com.metamatrix.metamodels.relational.Table#getLogicalRelationships()
     * @see #getTable()
     * @generated
     */
    EReference getTable_LogicalRelationships();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Column <em>Column</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Column</em>'.
     * @see com.metamatrix.metamodels.relational.Column
     * @generated
     */
    EClass getColumn();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getNativeType <em>Native Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Native Type</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getNativeType()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_NativeType();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getLength <em>Length</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Length</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getLength()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Length();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isFixedLength <em>Fixed Length</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Fixed Length</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isFixedLength()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_FixedLength();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getPrecision <em>Precision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Precision</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getPrecision()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Precision();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getScale <em>Scale</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Scale</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getScale()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Scale();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getNullable <em>Nullable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Nullable</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getNullable()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Nullable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isAutoIncremented <em>Auto Incremented</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Auto Incremented</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isAutoIncremented()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_AutoIncremented();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getDefaultValue <em>Default Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Default Value</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getDefaultValue()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_DefaultValue();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getMinimumValue <em>Minimum Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Minimum Value</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getMinimumValue()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_MinimumValue();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getMaximumValue <em>Maximum Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Maximum Value</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getMaximumValue()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_MaximumValue();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getFormat <em>Format</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Format</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getFormat()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Format();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getCharacterSetName <em>Character Set Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Character Set Name</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getCharacterSetName()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_CharacterSetName();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getCollationName <em>Collation Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Collation Name</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getCollationName()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_CollationName();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isSelectable <em>Selectable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Selectable</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isSelectable()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Selectable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isUpdateable <em>Updateable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Updateable</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isUpdateable()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Updateable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isCaseSensitive <em>Case Sensitive</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Case Sensitive</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isCaseSensitive()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_CaseSensitive();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getSearchability <em>Searchability</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Searchability</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getSearchability()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Searchability();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isCurrency <em>Currency</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Currency</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isCurrency()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Currency();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getRadix <em>Radix</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Radix</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getRadix()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Radix();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#isSigned <em>Signed</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Signed</em>'.
     * @see com.metamatrix.metamodels.relational.Column#isSigned()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_Signed();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getDistinctValueCount <em>Distinct Value Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Distinct Value Count</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getDistinctValueCount()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_DistinctValueCount();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Column#getNullValueCount <em>Null Value Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Null Value Count</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getNullValueCount()
     * @see #getColumn()
     * @generated
     */
    EAttribute getColumn_NullValueCount();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.Column#getUniqueKeys <em>Unique Keys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Unique Keys</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getUniqueKeys()
     * @see #getColumn()
     * @generated
     */
    EReference getColumn_UniqueKeys();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.Column#getIndexes <em>Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Indexes</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getIndexes()
     * @see #getColumn()
     * @generated
     */
    EReference getColumn_Indexes();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.Column#getForeignKeys <em>Foreign Keys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Foreign Keys</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getForeignKeys()
     * @see #getColumn()
     * @generated
     */
    EReference getColumn_ForeignKeys();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.Column#getAccessPatterns <em>Access Patterns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Access Patterns</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getAccessPatterns()
     * @see #getColumn()
     * @generated
     */
    EReference getColumn_AccessPatterns();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Column#getOwner <em>Owner</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Owner</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getOwner()
     * @see #getColumn()
     * @generated
     */
    EReference getColumn_Owner();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relational.Column#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see com.metamatrix.metamodels.relational.Column#getType()
     * @see #getColumn()
     * @generated
     */
    EReference getColumn_Type();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Schema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Schema</em>'.
     * @see com.metamatrix.metamodels.relational.Schema
     * @generated
     */
    EClass getSchema();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Schema#getTables <em>Tables</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Tables</em>'.
     * @see com.metamatrix.metamodels.relational.Schema#getTables()
     * @see #getSchema()
     * @generated
     */
    EReference getSchema_Tables();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Schema#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Catalog</em>'.
     * @see com.metamatrix.metamodels.relational.Schema#getCatalog()
     * @see #getSchema()
     * @generated
     */
    EReference getSchema_Catalog();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Schema#getProcedures <em>Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Procedures</em>'.
     * @see com.metamatrix.metamodels.relational.Schema#getProcedures()
     * @see #getSchema()
     * @generated
     */
    EReference getSchema_Procedures();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Schema#getIndexes <em>Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Indexes</em>'.
     * @see com.metamatrix.metamodels.relational.Schema#getIndexes()
     * @see #getSchema()
     * @generated
     */
    EReference getSchema_Indexes();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Schema#getLogicalRelationships <em>Logical Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Logical Relationships</em>'.
     * @see com.metamatrix.metamodels.relational.Schema#getLogicalRelationships()
     * @see #getSchema()
     * @generated
     */
    EReference getSchema_LogicalRelationships();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.PrimaryKey <em>Primary Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Primary Key</em>'.
     * @see com.metamatrix.metamodels.relational.PrimaryKey
     * @generated
     */
    EClass getPrimaryKey();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.PrimaryKey#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Table</em>'.
     * @see com.metamatrix.metamodels.relational.PrimaryKey#getTable()
     * @see #getPrimaryKey()
     * @generated
     */
    EReference getPrimaryKey_Table();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.ForeignKey <em>Foreign Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Foreign Key</em>'.
     * @see com.metamatrix.metamodels.relational.ForeignKey
     * @generated
     */
    EClass getForeignKey();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ForeignKey#getForeignKeyMultiplicity <em>Foreign Key Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Foreign Key Multiplicity</em>'.
     * @see com.metamatrix.metamodels.relational.ForeignKey#getForeignKeyMultiplicity()
     * @see #getForeignKey()
     * @generated
     */
    EAttribute getForeignKey_ForeignKeyMultiplicity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ForeignKey#getPrimaryKeyMultiplicity <em>Primary Key Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Primary Key Multiplicity</em>'.
     * @see com.metamatrix.metamodels.relational.ForeignKey#getPrimaryKeyMultiplicity()
     * @see #getForeignKey()
     * @generated
     */
    EAttribute getForeignKey_PrimaryKeyMultiplicity();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.ForeignKey#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Columns</em>'.
     * @see com.metamatrix.metamodels.relational.ForeignKey#getColumns()
     * @see #getForeignKey()
     * @generated
     */
    EReference getForeignKey_Columns();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relational.ForeignKey#getUniqueKey <em>Unique Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Unique Key</em>'.
     * @see com.metamatrix.metamodels.relational.ForeignKey#getUniqueKey()
     * @see #getForeignKey()
     * @generated
     */
    EReference getForeignKey_UniqueKey();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.ForeignKey#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Table</em>'.
     * @see com.metamatrix.metamodels.relational.ForeignKey#getTable()
     * @see #getForeignKey()
     * @generated
     */
    EReference getForeignKey_Table();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.RelationalEntity <em>Entity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Entity</em>'.
     * @see com.metamatrix.metamodels.relational.RelationalEntity
     * @generated
     */
    EClass getRelationalEntity();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.RelationalEntity#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see com.metamatrix.metamodels.relational.RelationalEntity#getName()
     * @see #getRelationalEntity()
     * @generated
     */
    EAttribute getRelationalEntity_Name();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.RelationalEntity#getNameInSource <em>Name In Source</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name In Source</em>'.
     * @see com.metamatrix.metamodels.relational.RelationalEntity#getNameInSource()
     * @see #getRelationalEntity()
     * @generated
     */
    EAttribute getRelationalEntity_NameInSource();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.UniqueKey <em>Unique Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Unique Key</em>'.
     * @see com.metamatrix.metamodels.relational.UniqueKey
     * @generated
     */
    EClass getUniqueKey();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.UniqueKey#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Columns</em>'.
     * @see com.metamatrix.metamodels.relational.UniqueKey#getColumns()
     * @see #getUniqueKey()
     * @generated
     */
    EReference getUniqueKey_Columns();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.UniqueKey#getForeignKeys <em>Foreign Keys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Foreign Keys</em>'.
     * @see com.metamatrix.metamodels.relational.UniqueKey#getForeignKeys()
     * @see #getUniqueKey()
     * @generated
     */
    EReference getUniqueKey_ForeignKeys();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.View <em>View</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>View</em>'.
     * @see com.metamatrix.metamodels.relational.View
     * @generated
     */
    EClass getView();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Catalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Catalog</em>'.
     * @see com.metamatrix.metamodels.relational.Catalog
     * @generated
     */
    EClass getCatalog();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Catalog#getSchemas <em>Schemas</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Schemas</em>'.
     * @see com.metamatrix.metamodels.relational.Catalog#getSchemas()
     * @see #getCatalog()
     * @generated
     */
    EReference getCatalog_Schemas();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Catalog#getProcedures <em>Procedures</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Procedures</em>'.
     * @see com.metamatrix.metamodels.relational.Catalog#getProcedures()
     * @see #getCatalog()
     * @generated
     */
    EReference getCatalog_Procedures();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Catalog#getIndexes <em>Indexes</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Indexes</em>'.
     * @see com.metamatrix.metamodels.relational.Catalog#getIndexes()
     * @see #getCatalog()
     * @generated
     */
    EReference getCatalog_Indexes();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Catalog#getTables <em>Tables</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Tables</em>'.
     * @see com.metamatrix.metamodels.relational.Catalog#getTables()
     * @see #getCatalog()
     * @generated
     */
    EReference getCatalog_Tables();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Catalog#getLogicalRelationships <em>Logical Relationships</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Logical Relationships</em>'.
     * @see com.metamatrix.metamodels.relational.Catalog#getLogicalRelationships()
     * @see #getCatalog()
     * @generated
     */
    EReference getCatalog_LogicalRelationships();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Procedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Procedure</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure
     * @generated
     */
    EClass getProcedure();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Procedure#isFunction <em>Function</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Function</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure#isFunction()
     * @see #getProcedure()
     * @generated
     */
    EAttribute getProcedure_Function();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Procedure#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Schema</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure#getSchema()
     * @see #getProcedure()
     * @generated
     */
    EReference getProcedure_Schema();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.Procedure#getParameters <em>Parameters</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Parameters</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure#getParameters()
     * @see #getProcedure()
     * @generated
     */
    EReference getProcedure_Parameters();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Procedure#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Catalog</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure#getCatalog()
     * @see #getProcedure()
     * @generated
     */
    EReference getProcedure_Catalog();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.relational.Procedure#getResult <em>Result</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Result</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure#getResult()
     * @see #getProcedure()
     * @generated
     */
    EReference getProcedure_Result();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Procedure#getUpdateCount <em>Update Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Update Count</em>'.
     * @see com.metamatrix.metamodels.relational.Procedure#getUpdateCount()
     * @see #getProcedure()
     * @generated
     */
    EAttribute getProcedure_UpdateCount();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Index <em>Index</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Index</em>'.
     * @see com.metamatrix.metamodels.relational.Index
     * @generated
     */
    EClass getIndex();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Index#getFilterCondition <em>Filter Condition</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filter Condition</em>'.
     * @see com.metamatrix.metamodels.relational.Index#getFilterCondition()
     * @see #getIndex()
     * @generated
     */
    EAttribute getIndex_FilterCondition();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Index#isNullable <em>Nullable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Nullable</em>'.
     * @see com.metamatrix.metamodels.relational.Index#isNullable()
     * @see #getIndex()
     * @generated
     */
    EAttribute getIndex_Nullable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Index#isAutoUpdate <em>Auto Update</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Auto Update</em>'.
     * @see com.metamatrix.metamodels.relational.Index#isAutoUpdate()
     * @see #getIndex()
     * @generated
     */
    EAttribute getIndex_AutoUpdate();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.Index#isUnique <em>Unique</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Unique</em>'.
     * @see com.metamatrix.metamodels.relational.Index#isUnique()
     * @see #getIndex()
     * @generated
     */
    EAttribute getIndex_Unique();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Index#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Schema</em>'.
     * @see com.metamatrix.metamodels.relational.Index#getSchema()
     * @see #getIndex()
     * @generated
     */
    EReference getIndex_Schema();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.Index#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Columns</em>'.
     * @see com.metamatrix.metamodels.relational.Index#getColumns()
     * @see #getIndex()
     * @generated
     */
    EReference getIndex_Columns();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.Index#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Catalog</em>'.
     * @see com.metamatrix.metamodels.relational.Index#getCatalog()
     * @see #getIndex()
     * @generated
     */
    EReference getIndex_Catalog();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.ProcedureParameter <em>Procedure Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Procedure Parameter</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter
     * @generated
     */
    EClass getProcedureParameter();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getDirection <em>Direction</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Direction</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getDirection()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_Direction();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getDefaultValue <em>Default Value</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Default Value</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getDefaultValue()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_DefaultValue();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getNativeType <em>Native Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Native Type</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getNativeType()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_NativeType();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getLength <em>Length</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Length</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getLength()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_Length();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getPrecision <em>Precision</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Precision</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getPrecision()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_Precision();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getScale <em>Scale</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Scale</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getScale()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_Scale();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getNullable <em>Nullable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Nullable</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getNullable()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_Nullable();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getRadix <em>Radix</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Radix</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getRadix()
     * @see #getProcedureParameter()
     * @generated
     */
    EAttribute getProcedureParameter_Radix();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Procedure</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getProcedure()
     * @see #getProcedureParameter()
     * @generated
     */
    EReference getProcedureParameter_Procedure();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relational.ProcedureParameter#getType <em>Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Type</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureParameter#getType()
     * @see #getProcedureParameter()
     * @generated
     */
    EReference getProcedureParameter_Type();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.UniqueConstraint <em>Unique Constraint</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Unique Constraint</em>'.
     * @see com.metamatrix.metamodels.relational.UniqueConstraint
     * @generated
     */
    EClass getUniqueConstraint();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.UniqueConstraint#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Table</em>'.
     * @see com.metamatrix.metamodels.relational.UniqueConstraint#getTable()
     * @see #getUniqueConstraint()
     * @generated
     */
    EReference getUniqueConstraint_Table();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.AccessPattern <em>Access Pattern</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Access Pattern</em>'.
     * @see com.metamatrix.metamodels.relational.AccessPattern
     * @generated
     */
    EClass getAccessPattern();

    /**
     * Returns the meta object for the reference list '{@link com.metamatrix.metamodels.relational.AccessPattern#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Columns</em>'.
     * @see com.metamatrix.metamodels.relational.AccessPattern#getColumns()
     * @see #getAccessPattern()
     * @generated
     */
    EReference getAccessPattern_Columns();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.AccessPattern#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Table</em>'.
     * @see com.metamatrix.metamodels.relational.AccessPattern#getTable()
     * @see #getAccessPattern()
     * @generated
     */
    EReference getAccessPattern_Table();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.Relationship <em>Relationship</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Relationship</em>'.
     * @see com.metamatrix.metamodels.relational.Relationship
     * @generated
     */
    EClass getRelationship();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.LogicalRelationship <em>Logical Relationship</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Logical Relationship</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationship
     * @generated
     */
    EClass getLogicalRelationship();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.LogicalRelationship#getCatalog <em>Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Catalog</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationship#getCatalog()
     * @see #getLogicalRelationship()
     * @generated
     */
    EReference getLogicalRelationship_Catalog();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.LogicalRelationship#getSchema <em>Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Schema</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationship#getSchema()
     * @see #getLogicalRelationship()
     * @generated
     */
    EReference getLogicalRelationship_Schema();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.LogicalRelationship#getEnds <em>Ends</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Ends</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationship#getEnds()
     * @see #getLogicalRelationship()
     * @generated
     */
    EReference getLogicalRelationship_Ends();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd <em>Logical Relationship End</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Logical Relationship End</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationshipEnd
     * @generated
     */
    EClass getLogicalRelationshipEnd();

    /**
     * Returns the meta object for the attribute '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getMultiplicity <em>Multiplicity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Multiplicity</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getMultiplicity()
     * @see #getLogicalRelationshipEnd()
     * @generated
     */
    EAttribute getLogicalRelationshipEnd_Multiplicity();

    /**
     * Returns the meta object for the reference '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getTable <em>Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Table</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getTable()
     * @see #getLogicalRelationshipEnd()
     * @generated
     */
    EReference getLogicalRelationshipEnd_Table();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getRelationship <em>Relationship</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Relationship</em>'.
     * @see com.metamatrix.metamodels.relational.LogicalRelationshipEnd#getRelationship()
     * @see #getLogicalRelationshipEnd()
     * @generated
     */
    EReference getLogicalRelationshipEnd_Relationship();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.BaseTable <em>Base Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Base Table</em>'.
     * @see com.metamatrix.metamodels.relational.BaseTable
     * @generated
     */
    EClass getBaseTable();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.BaseTable#getForeignKeys <em>Foreign Keys</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Foreign Keys</em>'.
     * @see com.metamatrix.metamodels.relational.BaseTable#getForeignKeys()
     * @see #getBaseTable()
     * @generated
     */
    EReference getBaseTable_ForeignKeys();

    /**
     * Returns the meta object for the containment reference '{@link com.metamatrix.metamodels.relational.BaseTable#getPrimaryKey <em>Primary Key</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Primary Key</em>'.
     * @see com.metamatrix.metamodels.relational.BaseTable#getPrimaryKey()
     * @see #getBaseTable()
     * @generated
     */
    EReference getBaseTable_PrimaryKey();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.BaseTable#getUniqueConstraints <em>Unique Constraints</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Unique Constraints</em>'.
     * @see com.metamatrix.metamodels.relational.BaseTable#getUniqueConstraints()
     * @see #getBaseTable()
     * @generated
     */
    EReference getBaseTable_UniqueConstraints();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.ColumnSet <em>Column Set</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Column Set</em>'.
     * @see com.metamatrix.metamodels.relational.ColumnSet
     * @generated
     */
    EClass getColumnSet();

    /**
     * Returns the meta object for the containment reference list '{@link com.metamatrix.metamodels.relational.ColumnSet#getColumns <em>Columns</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Columns</em>'.
     * @see com.metamatrix.metamodels.relational.ColumnSet#getColumns()
     * @see #getColumnSet()
     * @generated
     */
    EReference getColumnSet_Columns();

    /**
     * Returns the meta object for class '{@link com.metamatrix.metamodels.relational.ProcedureResult <em>Procedure Result</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Procedure Result</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureResult
     * @generated
     */
    EClass getProcedureResult();

    /**
     * Returns the meta object for the container reference '{@link com.metamatrix.metamodels.relational.ProcedureResult#getProcedure <em>Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Procedure</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureResult#getProcedure()
     * @see #getProcedureResult()
     * @generated
     */
    EReference getProcedureResult_Procedure();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.relational.NullableType <em>Nullable Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Nullable Type</em>'.
     * @see com.metamatrix.metamodels.relational.NullableType
     * @generated
     */
    EEnum getNullableType();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.relational.DirectionKind <em>Direction Kind</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Direction Kind</em>'.
     * @see com.metamatrix.metamodels.relational.DirectionKind
     * @generated
     */
    EEnum getDirectionKind();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.relational.MultiplicityKind <em>Multiplicity Kind</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Multiplicity Kind</em>'.
     * @see com.metamatrix.metamodels.relational.MultiplicityKind
     * @generated
     */
    EEnum getMultiplicityKind();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.relational.SearchabilityType <em>Searchability Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Searchability Type</em>'.
     * @see com.metamatrix.metamodels.relational.SearchabilityType
     * @generated
     */
    EEnum getSearchabilityType();

    /**
     * Returns the meta object for enum '{@link com.metamatrix.metamodels.relational.ProcedureUpdateCount <em>Procedure Update Count</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Procedure Update Count</em>'.
     * @see com.metamatrix.metamodels.relational.ProcedureUpdateCount
     * @generated
     */
    EEnum getProcedureUpdateCount();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    RelationalFactory getRelationalFactory();

} //RelationalPackage
