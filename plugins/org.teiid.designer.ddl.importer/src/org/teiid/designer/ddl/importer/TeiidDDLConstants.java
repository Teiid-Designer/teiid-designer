/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.ddl.importer;

/**
 * Teiid DDL Contants - from org.teiid.query.metadata.DDLConstants
 * Most of these are Option property keys
 */
@SuppressWarnings("javadoc")
public interface TeiidDDLConstants {
		public static final String JAVA_METHOD = "JAVA_METHOD";//$NON-NLS-1$
		public static final String JAVA_CLASS = "JAVA_CLASS";//$NON-NLS-1$
		public static final String DETERMINISM = "DETERMINISM";//$NON-NLS-1$
		public static final String CATEGORY = "CATEGORY";//$NON-NLS-1$
		public static final String UPDATECOUNT = "UPDATECOUNT";//$NON-NLS-1$
		public static final String DISTINCT_VALUES = "DISTINCT_VALUES";//$NON-NLS-1$
		public static final String NULL_VALUE_COUNT = "NULL_VALUE_COUNT";//$NON-NLS-1$
		public static final String RADIX = "RADIX";//$NON-NLS-1$
		public static final String NATIVE_TYPE = "NATIVE_TYPE";//$NON-NLS-1$
		public static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";//$NON-NLS-1$
		public static final String MAX_VALUE = "MAX_VALUE"; //$NON-NLS-1$
		public static final String MIN_VALUE = "MIN_VALUE";//$NON-NLS-1$
		public static final String SEARCHABLE = "SEARCHABLE";//$NON-NLS-1$
		public static final String FIXED_LENGTH = "FIXED_LENGTH";//$NON-NLS-1$
		public static final String CURRENCY = "CURRENCY";//$NON-NLS-1$
		public static final String SIGNED = "SIGNED";//$NON-NLS-1$
		public static final String SELECTABLE = "SELECTABLE";//$NON-NLS-1$
		public static final String CASE_SENSITIVE = "CASE_SENSITIVE";//$NON-NLS-1$
		public static final String AUTO_INCREMENT = "AUTO_INCREMENT";//$NON-NLS-1$
		public static final String NOT_NULL = "NOT NULL";//$NON-NLS-1$
		public static final String FOREIGN_KEY = "FOREIGN KEY";//$NON-NLS-1$
		public static final String PRIMARY_KEY = "PRIMARY KEY";//$NON-NLS-1$
		public static final String ACCESSPATTERN = "ACCESSPATTERN";//$NON-NLS-1$
		public static final String NAMEINSOURCE = "NAMEINSOURCE";//$NON-NLS-1$
		public static final String ANNOTATION = "ANNOTATION";//$NON-NLS-1$
		public static final String UUID = "UUID";//$NON-NLS-1$
		public static final String CARDINALITY = "CARDINALITY";//$NON-NLS-1$
		public static final String UPDATABLE = "UPDATABLE";//$NON-NLS-1$
		public static final String MATERIALIZED_TABLE = "MATERIALIZED_TABLE";//$NON-NLS-1$
		public static final String MATERIALIZED = "MATERIALIZED";//$NON-NLS-1$
		public static final String INSTEAD_OF = "INSTEAD OF";//$NON-NLS-1$
		public static final String CREATE_TRIGGER_ON = "CREATE TRIGGER ON";//$NON-NLS-1$
		public static final String FOREIGN_TABLE = "FOREIGN TABLE";//$NON-NLS-1$
		public static final String UDT = "UDT";//$NON-NLS-1$
		public static final String AGGREGATE_PROP = "AGGREGATE"; //$NON-NLS-1$
		public static final String ALLOWS_DISTINCT_PROP = "ALLOWS-DISTINCT"; //$NON-NLS-1$
		public static final String ALLOWS_ORDER_BY_PROP = "ALLOWS-ORDERBY"; //$NON-NLS-1$
		public static final String ANALYTIC_PROP = "ANALYTIC"; //$NON-NLS-1$
		public static final String DECOMPOSABLE_PROP = "DECOMPOSABLE"; //$NON-NLS-1$
		public static final String NON_PREPARED_PROP = "NON-PREPARED"; //$NON-NLS-1$
		public static final String NULL_ON_NULL_PROP = "NULL-ON-NULL"; //$NON-NLS-1$
		public static final String USES_DISTINCT_ROWS_PROP = "USES-DISTINCT-ROWS"; //$NON-NLS-1$
		public static final String VARARGS_PROP = "VARARGS"; //$NON-NLS-1$
		public static final String FUNCTION_CATEGORY_PROP = "FUNCTION-CATEGORY"; //$NON-NLS-1$
		public static final String DETERMINISM_PROP = "DETERMINISM"; //$NON-NLS-1$
		public static final String NATIVE_QUERY_PROP = "NATIVE-QUERY"; //$NON-NLS-1$
		public static final String DETERMINISM_OPT_NONDETERMINISTIC = "NONDETERMINISTIC"; //$NON-NLS-1$
		public static final String DETERMINISM_OPT_COMMAND_DETERMINISTIC = "COMMAND_DETERMINISTIC"; //$NON-NLS-1$
		public static final String DETERMINISM_OPT_SESSION_DETERMINISTIC = "SESSION_DETERMINISTIC"; //$NON-NLS-1$
		public static final String DETERMINISM_OPT_USER_DETERMINISTIC = "USER_DETERMINISTIC"; //$NON-NLS-1$
		public static final String DETERMINISM_OPT_VDB_DETERMINISTIC = "VDB_DETERMINISTIC"; //$NON-NLS-1$
		public static final String DETERMINISM_OPT_DETERMINISTIC = "DETERMINISTIC"; //$NON-NLS-1$

	    public String DDL_IMPORT_FILTER_CONSTRAINTS = "ddlImport_filterConstraints"; //$NON-NLS-1$
	    public String DDL_IMPORT_TABLE_UPDATABLE_OVERRIDE = "ddlImport_table_updatable_override"; //$NON-NLS-1$
		public static final String RETURNS = "RETURNS"; //$NON-NLS-1$
	    

}
