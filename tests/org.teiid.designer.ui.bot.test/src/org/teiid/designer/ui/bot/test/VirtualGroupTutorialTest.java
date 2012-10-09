package org.teiid.designer.ui.bot.test;


import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerType;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourceDialog;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourcePasswordDialog;
import org.teiid.designer.ui.bot.ext.teiid.editor.CriteriaBuilder;
import org.teiid.designer.ui.bot.ext.teiid.editor.ModelEditor;
import org.teiid.designer.ui.bot.ext.teiid.editor.SQLScrapbookEditor;
import org.teiid.designer.ui.bot.ext.teiid.editor.VDBEditor;
import org.teiid.designer.ui.bot.ext.teiid.instance.NewDefaultTeiidInstance;
import org.teiid.designer.ui.bot.ext.teiid.perspective.DatabaseDevelopmentPerspective;
import org.teiid.designer.ui.bot.ext.teiid.perspective.TeiidPerspective;
import org.teiid.designer.ui.bot.ext.teiid.view.ModelExplorerView;
import org.teiid.designer.ui.bot.ext.teiid.view.Procedure;
import org.teiid.designer.ui.bot.ext.teiid.view.SQLResult;
import org.teiid.designer.ui.bot.ext.teiid.view.TeiidInstanceView;
import org.teiid.designer.ui.bot.ext.teiid.wizard.CreateMetadataModel;
import org.teiid.designer.ui.bot.ext.teiid.wizard.CreateVDB;
import org.teiid.designer.ui.bot.ext.teiid.wizard.ImportJDBCDatabaseWizard;
import org.teiid.designer.ui.bot.test.suite.Properties;
import org.teiid.designer.ui.bot.test.suite.TeiidDesignerTestCase;


@Require(server=@Server(type=ServerType.ALL, state=ServerState.Running), perspective="Teiid Designer")
public class VirtualGroupTutorialTest extends TeiidDesignerTestCase {

	private static final String PROJECT_NAME = "MyFirstProject";

	private static final String ORACLE_MODEL_NAME = "PartsSupplier_Oracle.xmi";

	private static final String ORACLE_CONNPROFILE_NAME = "PartsSupplier Oracle";

	private static final String ORACLE_DATA_SOURCE = "PartsSupplier_Oracle";

	private static final String SQLSERVER_MODEL_NAME = "PartsSupplier_SQLServer.xmi";

	private static final String SQLSERVER_CONNPROFILE_NAME = "PartsSupplier SQL Server";

	private static final String SQLSERVER_DATA_SOURCE = "PartsSupplier_SQLServer";

	private static final String VIRTUAL_MODEL_NAME = "PartsVirtual.xmi";
	
	private static final String VDB_NAME = "MyFirstVDB";
	
	private static final String VDB_FILE_NAME = VDB_NAME + ".vdb";

	private static final String TRANSFORMATION_SQL = "SELECT\n\t\t"           +
		
	         "PartsSupplier_Oracle.SUPPLIER.SUPPLIER_ID, "      +
	         "PartsSupplier_Oracle.SUPPLIER.SUPPLIER_NAME, "    +
	         "PartsSupplier_Oracle.SUPPLIER.SUPPLIER_STATUS, "  +
	         "PartsSupplier_Oracle.SUPPLIER.SUPPLIER_CITY, "    + 
	         "PartsSupplier_Oracle.SUPPLIER.SUPPLIER_STATE, "   +
	               "PartsSupplier_SQLServer.SUPPLIER_PARTS."    + 
	                                       "SUPPLIER_ID AS "    + 
	                                       "SUPPLIER_ID_1, "    +
	"PartsSupplier_SQLServer.SUPPLIER_PARTS.PART_ID, "          +
	"PartsSupplier_SQLServer.SUPPLIER_PARTS.QUANTITY, "         +
	"PartsSupplier_SQLServer.SUPPLIER_PARTS.SHIPPER_ID\n\t"     +
	                             
	                                       "FROM\n\t\t"         +
	                                                            
	                  "PartsSupplier_Oracle.SUPPLIER, "         +
	               "PartsSupplier_SQLServer.SUPPLIER_PARTS\n\t" +
	                              
	                                       "WHERE\n\t\t"        +
	                                                            
	               "PartsSupplier_SQLServer.SUPPLIER_PARTS."    + 
	                                       "SUPPLIER_ID = "     + 
	                  "PartsSupplier_Oracle.SUPPLIER."          +
	                                       "SUPPLIER_ID";

	private static final String PROCEDURE_SQL = "CREATE VIRTUAL PROCEDURE\n"                +
	   "BEGIN\n\t"                                 +
	   "SELECT * FROM PartsVirtual.OnHand "        +
	   "WHERE PartsVirtual.OnHand.QUANTITY = "     +
	   "PartsVirtual.getOnHandByQuantity.qtyIn;\n" +
	   "END";

	private static final String TESTSQL_1 = "SELECT * FROM PartsSupplier_Oracle.PARTS";

	private static final String TESTSQL_2 = "SELECT * FROM PartsVirtual.OnHand";

	private static final String TESTSQL_3 = "SELECT * FROM PartsVirtual.OnHand WHERE QUANTITY > 200"; //should return 126 rows

	private static final String TESTSQL_4 = "SELECT " + 
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

	private static final String TESTSQL_5 = "EXEC PartsVirtual.getOnHandByQuantity( 200 )"; //it should return 30 rows

	private static final String VIRTUAL_TABLE_NAME = "OnHand";
	
	private static final String PROCEDURE_NAME = "getOnHandByQuantity";
	
	@BeforeClass
	public static void beforeClass(){
		createProject(PROJECT_NAME);
		
		addOracleDriver(PROJECT_NAME);
		prepareOracleDatabase();
		addSQLServerDriver(PROJECT_NAME);
		prepareSQLServerDatabase();
	}


	@Test
	public void createOracleModel(){
		ImportJDBCDatabaseWizard wizard = new ImportJDBCDatabaseWizard();
		wizard.setConnectionProfile(ORACLE_CONNPROFILE_NAME);
		wizard.setProjectName(PROJECT_NAME);
		wizard.setModelName(ORACLE_MODEL_NAME);
		wizard.execute();

		assertTrue(ORACLE_MODEL_NAME + " not created!", projectExplorer.existsResource(PROJECT_NAME, ORACLE_MODEL_NAME));
	}


	@Test
	public void createSQLServerModel(){
		ImportJDBCDatabaseWizard wizard = new ImportJDBCDatabaseWizard();
		wizard.setConnectionProfile(SQLSERVER_CONNPROFILE_NAME);
		wizard.setProjectName(PROJECT_NAME);
		wizard.setModelName(SQLSERVER_MODEL_NAME);
		wizard.execute();

		assertTrue(SQLSERVER_MODEL_NAME + " not created!", projectExplorer.existsResource(PROJECT_NAME, SQLSERVER_MODEL_NAME));
	}


	@Test
	public void createViewModel(){
		CreateMetadataModel newModel = new CreateMetadataModel();
		newModel.setLocation(PROJECT_NAME);
		newModel.setName(VIRTUAL_MODEL_NAME);
		newModel.setClass(CreateMetadataModel.ModelClass.RELATIONAL);
		newModel.setType(CreateMetadataModel.ModelType.VIEW);
		newModel.execute();

		assertTrue(VIRTUAL_MODEL_NAME + " not created!", projectExplorer.existsResource(PROJECT_NAME, VIRTUAL_MODEL_NAME));
	}

	@Test
	public void createTransformation(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();
		modelView.newBaseTable(PROJECT_NAME, VIRTUAL_MODEL_NAME, VIRTUAL_TABLE_NAME);
		modelView.openTransformationDiagram(PROJECT_NAME, VIRTUAL_MODEL_NAME, VIRTUAL_TABLE_NAME);
		modelView.addTransformationSource(PROJECT_NAME, ORACLE_MODEL_NAME, "SUPPLIER");
		modelView.addTransformationSource(PROJECT_NAME, SQLSERVER_MODEL_NAME, "SUPPLIER_PARTS");

		ModelEditor editor = modelEditor(VIRTUAL_MODEL_NAME);
		editor.show();
		editor.showTransformation();

		CriteriaBuilder criteriaBuilder = editor.criteriaBuilder();
		criteriaBuilder.selectRightAttribute("PartsSupplier_Oracle.SUPPLIER", "SUPPLIER_ID");
		criteriaBuilder.selectLeftAttribute("PartsSupplier_SQLServer.SUPPLIER_PARTS", "SUPPLIER_ID");
		criteriaBuilder.apply();
		criteriaBuilder.finish();

		editor.save();

		assertEquals("SQL Statements do not match!", TRANSFORMATION_SQL, editor.getTransformation());
	}


	@Test
	public void createTeiidInstance(){
		NewDefaultTeiidInstance teiid = new NewDefaultTeiidInstance();
		teiid.execute();

		assertTrue("Created server not visible in Teiid view.", 
				TeiidPerspective.getInstance().getTeiidInstanceView().containsTeiidInstance(NewDefaultTeiidInstance.TEIID_URL));	
	}

	@Test
	public void cleanupOldDatasources(){
		TeiidInstanceView view = TeiidPerspective.getInstance().getTeiidInstanceView();

		if (view.containsDataSource(NewDefaultTeiidInstance.TEIID_URL, ORACLE_DATA_SOURCE)){
			view.deleteDataSource(NewDefaultTeiidInstance.TEIID_URL, ORACLE_DATA_SOURCE);	
		}

		if (view.containsDataSource(NewDefaultTeiidInstance.TEIID_URL, SQLSERVER_DATA_SOURCE)){
			view.deleteDataSource(NewDefaultTeiidInstance.TEIID_URL, SQLSERVER_DATA_SOURCE);	
		}

		if (view.containsVDB(NewDefaultTeiidInstance.TEIID_URL, VDB_NAME)){
			view.undeployVDB(NewDefaultTeiidInstance.TEIID_URL, VDB_NAME);		
		}
	}

	@Test
	public void createOracleDataSource(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();

		DatasourceDialog dataSourceDialog = modelView.createDataSource(PROJECT_NAME, ORACLE_MODEL_NAME);
		dataSourceDialog.setName(ORACLE_DATA_SOURCE);
		dataSourceDialog.finish();

		DatasourcePasswordDialog passwordDialog = dataSourceDialog.getPasswordDialog();
		passwordDialog.setPassword("mm");
		passwordDialog.finish();
		
		TeiidPerspective.getInstance().getTeiidInstanceView().reconnect(NewDefaultTeiidInstance.TEIID_URL);

		assertTrue("Data Source not created!",
				TeiidPerspective.getInstance().getTeiidInstanceView().containsDataSource(NewDefaultTeiidInstance.TEIID_URL, ORACLE_DATA_SOURCE));
	}

	@Test
	public void createSQLServerDataSource(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();

		DatasourceDialog dataSourceDialog = modelView.createDataSource(PROJECT_NAME, SQLSERVER_MODEL_NAME);
		dataSourceDialog.setName(SQLSERVER_DATA_SOURCE);
		dataSourceDialog.finish();

		DatasourcePasswordDialog passwordDialog = dataSourceDialog.getPasswordDialog();
		passwordDialog.setPassword("mm");
		passwordDialog.finish();

		assertTrue("Data Source not created!",
				TeiidPerspective.getInstance().getTeiidInstanceView().containsDataSource(NewDefaultTeiidInstance.TEIID_URL, SQLSERVER_DATA_SOURCE));
	}

	@Test
	public void createVDB(){
		CreateVDB createVDB = new CreateVDB();
		createVDB.setFolder(PROJECT_NAME);
		createVDB.setName(VDB_NAME);
		createVDB.execute();

		VDBEditor editor = VDBEditor.getInstance(VDB_FILE_NAME);
		editor.show();
		editor.addModel(PROJECT_NAME, VIRTUAL_MODEL_NAME);
		editor.save();

		assertEquals(ORACLE_MODEL_NAME, editor.getModel(0));
		assertEquals(SQLSERVER_MODEL_NAME, editor.getModel(1));
		assertEquals(VIRTUAL_MODEL_NAME, editor.getModel(2));
	}

	@Test
	public void executeVDB(){
		TeiidPerspective.getInstance().getModelExplorerView().executeVDB(PROJECT_NAME, VDB_FILE_NAME);

		TeiidPerspective.getInstance().getTeiidInstanceView().reconnect(NewDefaultTeiidInstance.TEIID_URL);
		
		assertTrue("VDB not deployed!", TeiidPerspective.getInstance().getTeiidInstanceView().containsVDB(NewDefaultTeiidInstance.TEIID_URL, VDB_NAME));
	}

	@Test
	public void executeSqlQueries(){

		DatabaseDevelopmentPerspective.getInstance().getExplorerView().openSQLScrapbook(VDB_NAME + ".*", true);

		SQLScrapbookEditor editor = new SQLScrapbookEditor();
		editor.show();
		editor.setDatabase(VDB_NAME);


		// TESTSQL_1  
		editor.setText(TESTSQL_1);
		editor.executeAll();
		
		SQLResult result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView().getByOperation(TESTSQL_1);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());

		// TESTSQL_2 
		editor.show();
		editor.setText(TESTSQL_2);
		editor.executeAll();

		result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView().getByOperation(TESTSQL_2);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());

		// TESTSQL_3 
		editor.show();
		editor.setText(TESTSQL_3);
		editor.executeAll();

		result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView().getByOperation(TESTSQL_3);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());
		assertEquals(126, result.getCount());

		// TESTSQL_4
		editor.show();
		editor.setText(TESTSQL_4);
		editor.executeAll();

		result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView().getByOperation(TESTSQL_4);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());
		assertEquals(30, result.getCount());
	}

	@Test
	public void createProcedure(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();
		Procedure procedure = modelView.newProcedure(PROJECT_NAME, VIRTUAL_MODEL_NAME, PROCEDURE_NAME);
		procedure.addParameter("qtyIn", "short : xs:int");
		modelView.openTransformationDiagram(PROJECT_NAME, VIRTUAL_MODEL_NAME, PROCEDURE_NAME);
		
		ModelEditor editor = modelEditor(VIRTUAL_MODEL_NAME);
		editor.show();
		editor.showTransformation();
		editor.setTransformationProcedureBody("SELECT * FROM PartsVirtual.OnHand;");
		editor.save();
		
		CriteriaBuilder criteriaBuilder = editor.criteriaBuilder();
		criteriaBuilder.selectLeftAttribute("PartsVirtual." + VIRTUAL_TABLE_NAME, "QUANTITY");
		criteriaBuilder.selectRightAttribute("PartsVirtual." + PROCEDURE_NAME, "qtyIn");
		criteriaBuilder.apply();
		criteriaBuilder.finish();

		editor.save();

		assertEquals(PROCEDURE_SQL, editor.getTransformation());
	}

	@Test
	public void updateVDB(){
		TeiidPerspective.getInstance().getModelExplorerView().open(PROJECT_NAME, VDB_FILE_NAME);
		
		VDBEditor editor = VDBEditor.getInstance(VDB_FILE_NAME);
		editor.show();
		editor.synchronizeAll();
		editor.save();

		assertEquals(ORACLE_MODEL_NAME, editor.getModel(0));
		assertEquals(SQLSERVER_MODEL_NAME, editor.getModel(1));
		assertEquals(VIRTUAL_MODEL_NAME, editor.getModel(2));
	}

	@Test
	public void deployUpdatedVDB(){
		TeiidPerspective.getInstance().getModelExplorerView().deployVDB(PROJECT_NAME, VDB_FILE_NAME);

		assertTrue("VDB not deployed!", TeiidPerspective.getInstance().getTeiidInstanceView().containsVDB(NewDefaultTeiidInstance.TEIID_URL, VDB_NAME));
	}


	@Test
	public void executeProcedureQuery(){

		DatabaseDevelopmentPerspective.getInstance().getExplorerView().openSQLScrapbook(VDB_NAME + ".*", true);

		SQLScrapbookEditor editor = new SQLScrapbookEditor();
		editor.show();
		editor.setDatabase(VDB_NAME);
		editor.setText(TESTSQL_5);
		editor.executeAll();
		
		SQLResult result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView().getByOperation(TESTSQL_5);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());
		assertEquals(30, result.getCount());
	}

	@AfterClass
	public static void closeScrapbookEditor(){
		closeScrapbook();
		closeVDBEditor();
		closeModelEditor(VIRTUAL_MODEL_NAME);
		closeModelEditor(ORACLE_MODEL_NAME);
		closeModelEditor(SQLSERVER_MODEL_NAME);
		closeAutoConnectToTeiidInstance();
	}

	/**
	 * Prepares database 
	 */
	public static void prepareOracleDatabase()  {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.ORACLE_DRIVER);
		
		DriverEntity entity = new DriverEntity();
		entity.setDrvPath(stringBuilder.toString());
		entity.setDatabaseName("ORCL");
		entity.setInstanceName("Oracle Thin Driver");
		entity.setProfileName(ORACLE_CONNPROFILE_NAME);
		entity.setProfileDescription("PartsSupplier Oracle database");
		entity.setJdbcString("jdbc:oracle:thin:@englxdbs11.mw.lab.eng.bos.redhat.com:1521:ORCL");
		entity.setDriverTemplateDescId("org.eclipse.datatools.enablement.oracle.10.driverTemplate");
		entity.setDriverDefId("Oracle DB");
		entity.setUser("partssupplier");
		entity.setPassword("mm");
			
		prepareDatabase(entity, ORACLE_CONNPROFILE_NAME);	
			
	}
	
	/**
	 * Prepares database 
	 */
	public static void prepareSQLServerDatabase()  {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.SQLSERVER_DRIVER);
		
		DriverEntity entity = new DriverEntity();
		entity.setDrvPath(stringBuilder.toString());
		entity.setDatabaseName("PartsSupplier");
		entity.setInstanceName("Microsoft SQL Server 2008 JDBC Driver");
		entity.setProfileName(SQLSERVER_CONNPROFILE_NAME);
		entity.setProfileDescription("PartsSupplier SQL Server database");
		entity.setJdbcString("jdbc:sqlserver://slntdb02.mw.lab.eng.bos.redhat.com:1433;databaseName=PartsSupplier");
		entity.setDriverTemplateDescId("org.eclipse.datatools.enablement.msft.sqlserver.2008.driverTemplate");
		entity.setDriverDefId("SQL Server DB");
		entity.setUser("PartsSupplier");
		entity.setPassword("mm");
		
		prepareDatabase(entity, SQLSERVER_CONNPROFILE_NAME);
	}

	private static void closeScrapbook() {
		try {
			SQLScrapbookEditor editor = new SQLScrapbookEditor("SQL Scrapbook 0");
			editor.show();
			editor.close();
		} catch (WidgetNotFoundException e){
			
		}
	}
	
	private static void closeVDBEditor() {
		try {
			VDBEditor editor = VDBEditor.getInstance(VDB_NAME + ".vdb");
			editor.close();
		} catch (WidgetNotFoundException e){
			
		}
	}
	
	private static void closeModelEditor(String name) {
		try {
			ModelEditor editor = modelEditor(name);
			editor.close();
		} catch (WidgetNotFoundException e){
			
		}
	}
	
	private static void closeAutoConnectToTeiidInstance() {
		SWTBotExt bot = SWTBotFactory.getBot();
		
		try {
			bot.shell("Auto Connect to New Teiid Instance").close();
		} catch (WidgetNotFoundException e){
			
		}
	}
}
