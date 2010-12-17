package com.metamatrix.modeler.ui.bot.testsuite;

/**
 * 
 * @author psrna
 *
 */
public class Properties {
	
	public static final String PLUGIN_ID = "org.teiid.designer.ui.bot.tests";
	public static final String PROJECT_NAME = "MyFirstProject";
	
	public static final String ORACLE_DRIVER = "ojdbc14.jar";
	public static final String ORACLE_MODEL_NAME = "PartsSupplier_Oracle.xmi";
	public static final String ORACLE_CONNPROFILE_NAME = "PartsSupplier Oracle";
	public static final String ORACLE_TEIID_SOURCE = "PartsSupplier_Oracle";
	public static final String ORACLE_SUPPLIER = "PartsSupplier_Oracle.SUPPLIER";
	
	public static final String SQLSERVER_DRIVER = "sqljdbc.jar";
	public static final String SQLSERVER_MODEL_NAME = "PartsSupplier_SQLServer.xmi";
	public static final String SQLSERVER_CONNPROFILE_NAME = "PartsSupplier SQL Server";
	public static final String SQLSERVER_TEIID_SOURCE = "PartsSupplier_SQLServer";
	public static final String SQLSERVER_SUPPLIER_PARTS = "PartsSupplier_SQLServer.SUPPLIER_PARTS";
	
	
	public static final String PARTSVIRTUAL_MODEL_NAME = "PartsVirtual.xmi";
	
	public static final String TEIID_DRIVER = "teiid-7.2.0.Final-client.jar";
	public static final String TEIID_CONNPROFILE_NAME = "PartsSupplier Teiid";
	public static final String TEIID_URL = "mms://localhost:31443";
	
	public static final String VDB_NAME = "MyFirstVDB";
	
	public static final String TEIID_SQL = "SELECT\n\t\t"           +
	
	                      "PartsSupplier_Oracle.SUPPLIER"           +
	                                          ".SUPPLIER_ID, "      +
	                                           "SUPPLIER_NAME, "    +
	                                           "SUPPLIER_STATUS, "  +
	                                           "SUPPLIER_CITY, "    + 
	                                           "SUPPLIER_STATE, "   +
	                   "PartsSupplier_SQLServer.SUPPLIER_PARTS."    + 
	                                           "SUPPLIER_ID AS "    + 
	                                           "SUPPLIER_ID_1, "    +
	                                           "PART_ID, "          +
	                                           "QUANTITY, "         +
	                                           "SHIPPER_ID\n\t"     +
	                                 
	                                           "FROM\n\t\t"         +
	                                                                
	                      "PartsSupplier_Oracle.SUPPLIER, "         +
	                   "PartsSupplier_SQLServer.SUPPLIER_PARTS\n\t" +
	                                  
	                                           "WHERE\n\t\t"        +
	                                                                
	                   "PartsSupplier_SQLServer.SUPPLIER_PARTS."    + 
	                                           "SUPPLIER_ID = "     + 
	                      "PartsSupplier_Oracle.SUPPLIER."          +
	                                           "SUPPLIER_ID";

	public static final String PROCEDURE_SQL = "CREATE VIRTUAL PROCEDURE\n"                +
	                                           "BEGIN\n\t"                                 +
	                                           "SELECT * FROM PartsVirtual.OnHand "        +
	                                           "WHERE PartsVirtual.OnHand.QUANTITY = "     +
	                                           "PARTSVIRTUAL.GETONHANDBYQUANTITY.qtyIn;\n" +
	                                           "END";
	
	/* ###################### SQL TEST QUERIES ####################### */
	
	public static final String TESTSQL_1 = "SELECT * FROM PartsSupplier_Oracle.PARTS";
	public static final String TESTSQL_2 = "SELECT * FROM PartsVirtual.OnHand";
	public static final String TESTSQL_3 = "SELECT * FROM PartsVirtual.OnHand WHERE QUANTITY > 200"; //should return 126 rows
	public static final int    TESTSQL3_ROW_COUNT = 126;
	public static final String TESTSQL_4 = "SELECT " + 
	                                               "O.SUPPLIER_NAME, "                  + 
	                                               "O.PART_ID, "                        +
	                                               "P.PART_NAME "                       + 
	                                       "FROM "                                      + 
	                                               "PartsSupplier_Oracle.PARTS AS P, "  + 
	                                               "PartsVirtual.OnHand AS O "          + 
	                                       "WHERE "                                     + 
	                                               "(P.PART_ID = O.PART_ID) and "       + 
	                                               "(O.SUPPLIER_NAME LIKE '%la%') "     + 
	                                       "ORDER BY PART_NAME"; //it should return 30 rows
	public static final int    TESTSQL4_ROW_COUNT = 30;
	
	public static final String TESTSQL_5 = "EXEC PartsVirtual.getOnHandByQuantity( 200 )"; //it should return 30 rows
	public static final int    TESTSQL5_ROW_COUNT = 30;
	
	
}
