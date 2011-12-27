/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.datatools.models.teiidsqlmodel;

import org.eclipse.datatools.modelbase.sql.schema.SQLSchemaPackage;

import org.eclipse.datatools.modelbase.sql.tables.SQLTablesPackage;
import org.eclipse.emf.ecore.EClass;
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
 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidsqlmodelFactory
 * @model kind="package"
 * @generated
 */
public interface TeiidsqlmodelPackage extends EPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String copyright = "Teiid Designer (http://www.jboss.org/projects/teiiddesigner.html) is copyright 2000-2010 MetaMatrix, Inc. and Red Hat, Inc.  This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which is available at http://www.eclipse.org/legal/epl-v10.html.  Some portions may be licensed to Red Hat, Inc. under one or more contributor license agreements."; //$NON-NLS-1$

	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "teiidsqlmodel"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http:///org/teiid/datatools/connectivity/teiidsqlmodel.ecore"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "TeiidModel"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	TeiidsqlmodelPackage eINSTANCE = org.teiid.datatools.models.teiidsqlmodel.impl.TeiidsqlmodelPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.teiid.datatools.models.teiidsqlmodel.impl.TeiidSchemaImpl <em>Teiid Schema</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.teiid.datatools.models.teiidsqlmodel.impl.TeiidSchemaImpl
	 * @see org.teiid.datatools.models.teiidsqlmodel.impl.TeiidsqlmodelPackageImpl#getTeiidSchema()
	 * @generated
	 */
	int TEIID_SCHEMA = 1;

	/**
	 * The meta object id for the '{@link org.teiid.datatools.models.teiidsqlmodel.impl.DocumentImpl <em>Document</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.teiid.datatools.models.teiidsqlmodel.impl.DocumentImpl
	 * @see org.teiid.datatools.models.teiidsqlmodel.impl.TeiidsqlmodelPackageImpl#getDocument()
	 * @generated
	 */
	int DOCUMENT = 0;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__EANNOTATIONS = SQLTablesPackage.VIEW_TABLE__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__NAME = SQLTablesPackage.VIEW_TABLE__NAME;

	/**
	 * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__DEPENDENCIES = SQLTablesPackage.VIEW_TABLE__DEPENDENCIES;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__DESCRIPTION = SQLTablesPackage.VIEW_TABLE__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__LABEL = SQLTablesPackage.VIEW_TABLE__LABEL;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__COMMENTS = SQLTablesPackage.VIEW_TABLE__COMMENTS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__EXTENSIONS = SQLTablesPackage.VIEW_TABLE__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Privileges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__PRIVILEGES = SQLTablesPackage.VIEW_TABLE__PRIVILEGES;

	/**
	 * The feature id for the '<em><b>Columns</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__COLUMNS = SQLTablesPackage.VIEW_TABLE__COLUMNS;

	/**
	 * The feature id for the '<em><b>Supertable</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__SUPERTABLE = SQLTablesPackage.VIEW_TABLE__SUPERTABLE;

	/**
	 * The feature id for the '<em><b>Subtables</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__SUBTABLES = SQLTablesPackage.VIEW_TABLE__SUBTABLES;

	/**
	 * The feature id for the '<em><b>Schema</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__SCHEMA = SQLTablesPackage.VIEW_TABLE__SCHEMA;

	/**
	 * The feature id for the '<em><b>Udt</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__UDT = SQLTablesPackage.VIEW_TABLE__UDT;

	/**
	 * The feature id for the '<em><b>Triggers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__TRIGGERS = SQLTablesPackage.VIEW_TABLE__TRIGGERS;

	/**
	 * The feature id for the '<em><b>Index</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__INDEX = SQLTablesPackage.VIEW_TABLE__INDEX;

	/**
	 * The feature id for the '<em><b>Self Ref Column Generation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__SELF_REF_COLUMN_GENERATION = SQLTablesPackage.VIEW_TABLE__SELF_REF_COLUMN_GENERATION;

	/**
	 * The feature id for the '<em><b>Insertable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__INSERTABLE = SQLTablesPackage.VIEW_TABLE__INSERTABLE;

	/**
	 * The feature id for the '<em><b>Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__UPDATABLE = SQLTablesPackage.VIEW_TABLE__UPDATABLE;

	/**
	 * The feature id for the '<em><b>Query Expression</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__QUERY_EXPRESSION = SQLTablesPackage.VIEW_TABLE__QUERY_EXPRESSION;

	/**
	 * The feature id for the '<em><b>Check Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT__CHECK_TYPE = SQLTablesPackage.VIEW_TABLE__CHECK_TYPE;

	/**
	 * The number of structural features of the '<em>Document</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DOCUMENT_FEATURE_COUNT = SQLTablesPackage.VIEW_TABLE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>EAnnotations</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__EANNOTATIONS = SQLSchemaPackage.SCHEMA__EANNOTATIONS;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__NAME = SQLSchemaPackage.SCHEMA__NAME;

	/**
	 * The feature id for the '<em><b>Dependencies</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__DEPENDENCIES = SQLSchemaPackage.SCHEMA__DEPENDENCIES;

	/**
	 * The feature id for the '<em><b>Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__DESCRIPTION = SQLSchemaPackage.SCHEMA__DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__LABEL = SQLSchemaPackage.SCHEMA__LABEL;

	/**
	 * The feature id for the '<em><b>Comments</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__COMMENTS = SQLSchemaPackage.SCHEMA__COMMENTS;

	/**
	 * The feature id for the '<em><b>Extensions</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__EXTENSIONS = SQLSchemaPackage.SCHEMA__EXTENSIONS;

	/**
	 * The feature id for the '<em><b>Privileges</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__PRIVILEGES = SQLSchemaPackage.SCHEMA__PRIVILEGES;

	/**
	 * The feature id for the '<em><b>Triggers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__TRIGGERS = SQLSchemaPackage.SCHEMA__TRIGGERS;

	/**
	 * The feature id for the '<em><b>Indices</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__INDICES = SQLSchemaPackage.SCHEMA__INDICES;

	/**
	 * The feature id for the '<em><b>Tables</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__TABLES = SQLSchemaPackage.SCHEMA__TABLES;

	/**
	 * The feature id for the '<em><b>Sequences</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__SEQUENCES = SQLSchemaPackage.SCHEMA__SEQUENCES;

	/**
	 * The feature id for the '<em><b>Database</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__DATABASE = SQLSchemaPackage.SCHEMA__DATABASE;

	/**
	 * The feature id for the '<em><b>Catalog</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__CATALOG = SQLSchemaPackage.SCHEMA__CATALOG;

	/**
	 * The feature id for the '<em><b>Assertions</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__ASSERTIONS = SQLSchemaPackage.SCHEMA__ASSERTIONS;

	/**
	 * The feature id for the '<em><b>User Defined Types</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__USER_DEFINED_TYPES = SQLSchemaPackage.SCHEMA__USER_DEFINED_TYPES;

	/**
	 * The feature id for the '<em><b>Char Sets</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__CHAR_SETS = SQLSchemaPackage.SCHEMA__CHAR_SETS;

	/**
	 * The feature id for the '<em><b>Routines</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__ROUTINES = SQLSchemaPackage.SCHEMA__ROUTINES;

	/**
	 * The feature id for the '<em><b>Owner</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__OWNER = SQLSchemaPackage.SCHEMA__OWNER;

	/**
	 * The feature id for the '<em><b>Documents</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA__DOCUMENTS = SQLSchemaPackage.SCHEMA_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Teiid Schema</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int TEIID_SCHEMA_FEATURE_COUNT = SQLSchemaPackage.SCHEMA_FEATURE_COUNT + 1;


	/**
	 * Returns the meta object for class '{@link org.teiid.datatools.models.teiidsqlmodel.TeiidSchema <em>Teiid Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Teiid Schema</em>'.
	 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidSchema
	 * @generated
	 */
	EClass getTeiidSchema();

	/**
	 * Returns the meta object for the reference list '{@link org.teiid.datatools.models.teiidsqlmodel.TeiidSchema#getDocuments <em>Documents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Documents</em>'.
	 * @see org.teiid.datatools.models.teiidsqlmodel.TeiidSchema#getDocuments()
	 * @see #getTeiidSchema()
	 * @generated
	 */
	EReference getTeiidSchema_Documents();

	/**
	 * Returns the meta object for class '{@link org.teiid.datatools.models.teiidsqlmodel.Document <em>Document</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Document</em>'.
	 * @see org.teiid.datatools.models.teiidsqlmodel.Document
	 * @generated
	 */
	EClass getDocument();

	/**
	 * Returns the meta object for the reference '{@link org.teiid.datatools.models.teiidsqlmodel.Document#getSchema <em>Schema</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Schema</em>'.
	 * @see org.teiid.datatools.models.teiidsqlmodel.Document#getSchema()
	 * @see #getDocument()
	 * @generated
	 */
	EReference getDocument_Schema();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	TeiidsqlmodelFactory getTeiidsqlmodelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.teiid.datatools.models.teiidsqlmodel.impl.TeiidSchemaImpl <em>Teiid Schema</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.teiid.datatools.models.teiidsqlmodel.impl.TeiidSchemaImpl
		 * @see org.teiid.datatools.models.teiidsqlmodel.impl.TeiidsqlmodelPackageImpl#getTeiidSchema()
		 * @generated
		 */
		EClass TEIID_SCHEMA = eINSTANCE.getTeiidSchema();

		/**
		 * The meta object literal for the '<em><b>Documents</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference TEIID_SCHEMA__DOCUMENTS = eINSTANCE.getTeiidSchema_Documents();

		/**
		 * The meta object literal for the '{@link org.teiid.datatools.models.teiidsqlmodel.impl.DocumentImpl <em>Document</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.teiid.datatools.models.teiidsqlmodel.impl.DocumentImpl
		 * @see org.teiid.datatools.models.teiidsqlmodel.impl.TeiidsqlmodelPackageImpl#getDocument()
		 * @generated
		 */
		EClass DOCUMENT = eINSTANCE.getDocument();

		/**
		 * The meta object literal for the '<em><b>Schema</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DOCUMENT__SCHEMA = eINSTANCE.getDocument_Schema();

	}

} //TeiidsqlmodelPackage
