/*************************************************************************************
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership. Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 ************************************************************************************/
package org.teiid.designer.transformation.ddl;

/**
 * Teiid DDL Contants
 * Most of these are Option property keys
 */
@SuppressWarnings("javadoc")
public interface TeiidDDLConstants {
		public static final String CREATE_FOREIGN_TABLE = "CREATE FOREIGN TABLE";
		public static final String CREATE_FOREIGN_FUNCTION = "CREATE FOREIGN FUNCTION";
		public static final String CREATE_FOREIGN_PROCEDURE = "CREATE FOREIGN PROCEDURE";
		public static final String CREATE_VIEW = "CREATE VIEW";
		public static final String CREATE_VIRTUAL_PROCEDURE = "CREATE VIRTUAL PROCEDURE";
		public static final String CREATE_VIRTUAL_FUNCTION = "CREATE VIRTUAL FUNCTION";
		public static final String CREATE_GLOBAL_TEMPORARY_TABLE = "CREATE GLOBAL TEMPORARY TABLE";
		
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
		public static final String CONSTRAINT = "CONSTRAINT";//$NON-NLS-1$
		public static final String UNIQUE = "UNIQUE";//$NON-NLS-1$
		public static final String REFERENCES = "REFERENCES";//$NON-NLS-1$
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
		public static final String RETURNS = "RETURNS"; //$NON-NLS-1$
		public static final String TABLE = "TABLE"; //$NON-NLS-1$
		public static final String IN = "IN"; //$NON-NLS-1$
		public static final String INOUT = "INOUT"; //$NON-NLS-1$
		public static final String OUT = "OUT"; //$NON-NLS-1$
		public static final String DEFAULT = "DEFAULT"; //$NON-NLS-1$
		
		/* REST EXTENSION PROPERTIES
		METHOD 	HTTP Method to use 	Yes 	GET | POST| PUT | DELETE
		URI 	URI of procedure 	Yes 	ex:/procedure
		PRODUCES 	Type of content produced by the service 	no 	xml | json | plain | any text
		CHARSET 	When procedure returns Blob, and content type text based, this character set to used to convert the data 	no 	US-ASCII | UTF-8 
		*/
		public static final String METHOD = "METHOD"; //$NON-NLS-1$
		public static final String REST_METHOD = "REST:METHOD"; //$NON-NLS-1$
		public static final String URI = "URI"; //$NON-NLS-1$
		public static final String REST_URI = "REST:URI"; //$NON-NLS-1$
		public static final String PRODUCES = "PRODUCES"; //$NON-NLS-1$
		public static final String REST_PRODUCES = "REST:PRODUCES"; //$NON-NLS-1$
		public static final String CHARSET = "CHARSET"; //$NON-NLS-1$
		public static final String REST_CHARSET = "REST:CHARSET"; //$NON-NLS-1$
		public static final String REST_TEIID_SET_NAMESPACE = "SET NAMESPACE 'http://teiid.org/rest' AS REST;"; //$NON-NLS-1$
		
		public static String TEIID_SF_PREFIX = "teiid_sf";  //$NON-NLS-1$
	    public static String SALESFORCE_PREFIX = "salesforce";  //$NON-NLS-1$
	    
		public static String TEIID_MONGO_PREFIX = "teiid_mongo";  //$NON-NLS-1$
	    public static String MONGODB_PREFIX = "mongodb";  //$NON-NLS-1$";  //$NON-NLS-1$
	    

	    /*
	     * Teiid's EXCEL extension properties
	     */
		public static String TEIID_EXCEL_PREFIX = "teiid_excel";  //$NON-NLS-1$
	    public static String EXCEL_PREFIX = "excel";  //$NON-NLS-1$";  //$NON-NLS-1$
	    public static String EXCEL_CELL_NUMBER = "CELL_NUMBER";  //$NON-NLS-1$";  //$NON-NLS-1$  COLUMN ONLY
	    public static String EXCEL_FILE = "FILE";  //$NON-NLS-1$";  //$NON-NLS-1$  TABLE
	    public static String EXCEL_FIRST_DATA_ROW_NUMBER = "FIRST_DATA_ROW_NUMBER";  //$NON-NLS-1$";  //$NON-NLS-1$  TABLE
	    
}
