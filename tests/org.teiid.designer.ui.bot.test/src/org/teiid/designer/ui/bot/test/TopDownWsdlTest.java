package org.teiid.designer.ui.bot.test;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerType;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.helper.ResourceHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.wizards.SWTBotImportWizard;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourceDialog;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourcePasswordDialog;
import org.teiid.designer.ui.bot.ext.teiid.editor.ModelEditor;
import org.teiid.designer.ui.bot.ext.teiid.editor.SQLScrapbookEditor;
import org.teiid.designer.ui.bot.ext.teiid.editor.VDBEditor;
import org.teiid.designer.ui.bot.ext.teiid.instance.NewDefaultTeiidInstance;
import org.teiid.designer.ui.bot.ext.teiid.perspective.DatabaseDevelopmentPerspective;
import org.teiid.designer.ui.bot.ext.teiid.perspective.TeiidPerspective;
import org.teiid.designer.ui.bot.ext.teiid.view.ModelExplorerView;
import org.teiid.designer.ui.bot.ext.teiid.view.SQLResult;
import org.teiid.designer.ui.bot.ext.teiid.wizard.CreateVDB;
import org.teiid.designer.ui.bot.ext.teiid.wizard.ImportJDBCDatabaseWizard;
import org.teiid.designer.ui.bot.test.suite.Properties;
import org.teiid.designer.ui.bot.test.suite.TeiidDesignerTestCase;

/**
 * 
 * @author apodhrad
 * 
 */
@Require(server = @Server(type = ServerType.ALL, state = ServerState.Running), perspective = "Teiid Designer")
public class TopDownWsdlTest extends TeiidDesignerTestCase {

	public static final String BUNDLE = "org.teiid.designer.ui.bot.test";
	public static final String PROJECT_NAME = "TopDownWsdlTest";
	public static final String WS_NAME = "ChkOrdSvc";
	public static final String CONNECTION_PROFILE = "TopDownWsdl SQL Profile";
	public static final String VDB_NAME = "testVDB";

	@BeforeClass
	public static void createTeiidProject() {
		// Create new project
		createProject(PROJECT_NAME);

		// Import wsdl
		SWTBotImportWizard wizard = new SWTBotImportWizard();
		wizard.open("File System", "General");
		String path = ResourceHelper.getResourceAbsolutePath(BUNDLE, "resources", "wsdl");
		wizard.bot().comboBoxWithLabel("From directory:").typeText(path);
		wizard.bot().tree().setFocus();
		wizard.bot().tree().getTreeItem("wsdl").check();
		wizard.bot().textWithLabel("Into folder:").setText(PROJECT_NAME + "/wsdl");
		wizard.bot().button("Finish").click();

		// Create DB connection profile
		DriverEntity entity = new DriverEntity();
		String drvPath = TestConfigurator.currentConfig.getServer().runtimeHome
				+ "/server/default/lib/" + Properties.SQLSERVER_DRIVER;
		entity.setDrvPath(drvPath);
		entity.setDatabaseName("tpcr");
		entity.setInstanceName("Microsoft SQL Server 2008 JDBC Driver");
		entity.setProfileName(CONNECTION_PROFILE);
		entity.setProfileDescription("PartsSupplier SQL Server database");
		entity.setJdbcString("jdbc:sqlserver://slntdb02.mw.lab.eng.bos.redhat.com:1433;databaseName=PartsSupplier");
		entity.setDriverTemplateDescId("org.eclipse.datatools.enablement.msft.sqlserver.2008.driverTemplate");
		entity.setDriverDefId("SQL Server DB");
		entity.setUser("tpcr");
		entity.setPassword("mm");
		try {
			DatabaseHelper.createDriver(entity, CONNECTION_PROFILE);
		} catch (ConnectionProfileException e) {
			fail("Couldn't create a driver.");
		}

		// Create Teiid instance
		NewDefaultTeiidInstance teiid = new NewDefaultTeiidInstance();
		teiid.execute();
	}

	@Test
	public void topDownWsdlTestScript() throws Exception {
		SWTBotImportWizard importWizard;
		/* Create the Web Service model */
		importWizard = new SWTBotImportWizard();
		importWizard.open("WSDL File or URL >> Web Service Model", "Teiid Designer");
		importWizard.bot().textWithLabel("Web Service Model Name").setText(WS_NAME);
		importWizard.bot().textInGroup("Target Workspace Folder").setText(PROJECT_NAME);
		importWizard.bot().button("Workspace...").click();

		SWTBotShell wsdlSelection = bot.shell("WSDL File Selection");
		wsdlSelection.activate();
		wsdlSelection.setFocus();
		wsdlSelection.bot().tree().expandNode(PROJECT_NAME, "wsdl", "TpcrOrderChecking.wsdl")
				.select();
		wsdlSelection.bot().button("OK").click();

		importWizard.bot().button("Next >").click();
		// Namespace Resolution
		assertTrue("Not all namespaces are resolved.", importWizard.bot().button("Next >")
				.isEnabled());
		importWizard.bot().button("Next >").click();
		// WSDL Operations Selection
		SWTBotTreeItem root = importWizard.bot().tree().getAllItems()[0];
		SWTBotTreeItem node1 = root.getNode("Service1Soap");
		assertTrue(node1.isChecked());
		SWTBotTreeItem node2 = node1.getNode("CheckOrder");
		assertTrue(node2.isChecked());
		assertTrue(importWizard.bot().button("Next >").isEnabled());
		importWizard.bot().button("Next >").click();
		// Schema Workspace Location Selection
		assertTrue("Not all schema workspace locations are valid",
				importWizard.bot().button("Next >").isEnabled());
		importWizard.bot().button("Next >").click();
		// XML Model Generation
		assertTrue(importWizard.bot().checkBox("Generate virtual XML document model").isChecked());
		assertEquals(WS_NAME + "Responses", importWizard.bot().textWithLabel("XML Model:")
				.getText());
		assertTrue("Couldn't finish Web Service Model", importWizard.bot().button("Finish")
				.isEnabled());
		importWizard.bot().button("Finish").click();

		SWTBotShell shell = bot.shell("Progress Information");
		shell.activate();
		bot.waitUntil(shellCloses(shell), Timing.time100S());

		// TODO: check the result tree

		/* Import a Relational Source */
		importMetadataFromDB(CONNECTION_PROFILE, PROJECT_NAME, "TPCR_S2k.xmi");

		ModelExplorerView modelExplorer = TeiidPerspective.getInstance().getModelExplorerView();
		modelExplorer.open(PROJECT_NAME, WS_NAME + "Responses.xmi",
				"Service1Soap_CheckOrder_OCout", "Mapping Diagram");

		ModelEditor modelEditor = modelEditor(WS_NAME + "Responses.xmi");
		modelEditor.show();
		modelEditor.showMappingTransformation("CONTAINER");

		/* Map XML view to Relational Sources */
		String sql = "SELECT convert(O_ORDERKEY, string) AS ORDER_KEY, convert(O_ORDERDATE, date) AS ORDER_DATE, C_NAME AS CUSTOMER, convert(P_PARTKEY, string) AS PART_KEY, P_NAME AS PART_NAME, convert(L_SHIPDATE, date) AS SHIP_DATE, O_ORDERSTATUS AS ORDER_STATUS, P_COMMENT AS PART_COMMENT, C_COMMENT AS CUSTOMER_COMMENT "
				+ "FROM TPCR_S2k.ORDERS, TPCR_S2k.PART, TPCR_S2k.LINEITEM, TPCR_S2k.CUSTOMER "
				+ "WHERE (O_ORDERDATE = {ts'1993-03-31 00:00:00.0'}) AND (O_ORDERKEY = L_ORDERKEY) AND (O_CUSTKEY = C_CUSTKEY) AND (L_PARTKEY = P_PARTKEY) AND (L_SHIPDATE BETWEEN {ts'1993-04-01 00:00:00.0'} AND {ts'1993-04-15 00:00:00.0'})";

		modelEditor.setTransformation(sql);
		modelEditor.saveAndValidateSql();
		modelEditor.save();

		/* Build the WS Operation's transformation */

		String procedureSql = "CREATE VIRTUAL PROCEDURE\n"
				+ "BEGIN\n"
				+ "\tDECLARE string VARIABLES.IN_ShipDateHigh;\n"
				+ "\tVARIABLES.IN_ShipDateHigh = xpathValue(ChkOrdSvc.Service1Soap.CheckOrder.OCin, '/*:OC_Input/*:ShipDateHigh');\n"
				+ "\tDECLARE string VARIABLES.IN_ShipDateLow;\n"
				+ "\tVARIABLES.IN_ShipDateLow = xpathValue(ChkOrdSvc.Service1Soap.CheckOrder.OCin, '/*:OC_Input/*:ShipDateLow');\n"
				+ "\tDECLARE string VARIABLES.IN_OrderDate;\n"
				+ "\tVARIABLES.IN_OrderDate = xpathValue(ChkOrdSvc.Service1Soap.CheckOrder.OCin, '/*:OC_Input/*:OrderDate');\n";
		String selectSql = "SELECT * FROM ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout WHERE (ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout.OC_Output.CONTAINER.ORDER_DATE = parseDate(VARIABLES.IN_ORDERDATE, 'yyyy-MM-dd')) AND ((ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout.OC_Output.CONTAINER.SHIP_DATE >= parseDate(VARIABLES.IN_SHIPDATELOW, 'yyyy-MM-dd')) AND (ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout.OC_Output.CONTAINER.SHIP_DATE <= parseDate(VARIABLES.IN_SHIPDATEHIGH, 'yyyy-MM-dd')));";

		modelExplorer.open(PROJECT_NAME, WS_NAME + ".xmi", "Service1Soap", "CheckOrder",
				"Transformation Diagram");

		modelEditor = modelEditor(WS_NAME + ".xmi");
		modelEditor.show();
		modelEditor.showTransformation();
		modelEditor.setTransformation(procedureSql + selectSql + "\nEND");
		modelEditor.saveAndValidateSql();
		modelEditor.save();

		/* Create a VDB */
		CreateVDB createVDB = new CreateVDB();
		createVDB.setFolder(PROJECT_NAME);
		createVDB.setName(VDB_NAME);
		createVDB.execute();

		VDBEditor editor = VDBEditor.getInstance("testVDB.vdb");
		editor.show();
		editor.addModel(PROJECT_NAME, WS_NAME + ".xmi");
		editor.save();

		// TODO: check the following ChkOrdSvc.xmi ChkOrdSvcResponse.xmi
		// TPCSR_S2k.xmi TpcrOrderChecking.xsd

		/* Test XML Sources and Views */
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();

		DatasourceDialog dataSourceDialog = modelView
				.createDataSource(PROJECT_NAME, "TPCR_S2k.xmi");
		dataSourceDialog.setName("TPCR_S2k");
		dataSourceDialog.finish();

		DatasourcePasswordDialog passwordDialog = dataSourceDialog.getPasswordDialog();
		passwordDialog.setPassword("mm");
		passwordDialog.finish();

		TeiidPerspective.getInstance().getModelExplorerView()
				.executeVDB(PROJECT_NAME, VDB_NAME + ".vdb");
		TeiidPerspective.getInstance().getTeiidInstanceView()
				.reconnect(NewDefaultTeiidInstance.TEIID_URL);

		DatabaseDevelopmentPerspective.getInstance().getExplorerView()
				.openSQLScrapbook(VDB_NAME + ".*", true);

		SQLScrapbookEditor sqlEditor = new SQLScrapbookEditor();
		sqlEditor.show();
		sqlEditor.setDatabase(VDB_NAME);

		String testSql = "SELECT * FROM ChkOrdSvcResponses.Service1Soap_CheckOrder_OCout";
		sqlEditor.setText(testSql);
		sqlEditor.executeAll();

		SQLResult result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView()
				.getByOperation(testSql);
		assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());
		
		testSql = "EXEC ChkOrdSvc.Service1Soap.CheckOrder('<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<OC_Input xmlns=\"http://com.metamatrix/TPCRwsdl_VDB\">"
				+ "<OrderDate>1993-03-31</OrderDate>"
				+ "<ShipDateLow>1993-04-01</ShipDateLow>"
				+ "<ShipDateHigh>1993-04-02</ShipDateHigh>" + "</OC_Input>')";

		sqlEditor.setText(testSql);
		sqlEditor.executeAll();
		result = DatabaseDevelopmentPerspective.getInstance().getSqlResultsView()
				.getByOperation(testSql);
	 	assertEquals(SQLResult.STATUS_SUCCEEDED, result.getStatus());
		
	 	// Close the editor without saving
	 	sqlEditor.close();
	 	
	 	// Generate the WAR file
		
		System.out.println("Done.");
	}

	private void importMetadataFromDB(String connectionProfile, String projectName, String modelName) {
		ImportJDBCDatabaseWizard wizard = new ImportJDBCDatabaseWizard();
		wizard.setConnectionProfile(connectionProfile);
		wizard.setProjectName(projectName);
		wizard.setModelName(modelName);
		wizard.execute();
		assertTrue("Metadata not created!", projectExplorer.existsResource(PROJECT_NAME, "TPCR_S2k.xmi"));
	}

}