package com.metamatrix.modeler.ui.bot.testcase;


import org.eclipse.swt.custom.StyledText;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerType;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.StyledTextHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.designer.ui.bot.ext.teiid.SWTBotTeiidCanvas;
import org.teiid.designer.ui.bot.ext.teiid.SWTTeiidBot;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourceDialog;
import org.teiid.designer.ui.bot.ext.teiid.database.DatasourcePasswordDialog;
import org.teiid.designer.ui.bot.ext.teiid.editor.CriteriaBuilder;
import org.teiid.designer.ui.bot.ext.teiid.editor.ModelEditor;
import org.teiid.designer.ui.bot.ext.teiid.editor.SQLScrapbookEditor;
import org.teiid.designer.ui.bot.ext.teiid.editor.VDBEditor;
import org.teiid.designer.ui.bot.ext.teiid.instance.NewTeiidInstance;
import org.teiid.designer.ui.bot.ext.teiid.perspective.DatabaseDevelopmentPerspective;
import org.teiid.designer.ui.bot.ext.teiid.perspective.TeiidPerspective;
import org.teiid.designer.ui.bot.ext.teiid.view.ModelExplorerView;
import org.teiid.designer.ui.bot.ext.teiid.view.TeiidInstanceView;
import org.teiid.designer.ui.bot.ext.teiid.wizard.CreateMetadataModel;
import org.teiid.designer.ui.bot.ext.teiid.wizard.CreateVDB;
import org.teiid.designer.ui.bot.ext.teiid.wizard.ImportJDBCDatabaseWizard;

import com.metamatrix.modeler.ui.bot.testsuite.Properties;
import com.metamatrix.modeler.ui.bot.testsuite.TeiidDesignerTest;

@Require(server=@Server(type=ServerType.SOA,version="5.1", state=ServerState.Running), perspective="Teiid Designer")
public class VirtualGroupTutorialTest extends TeiidDesignerTest {

	@BeforeClass
	public static void beforeClass(){
		createProject();
		addOracleDriver(Properties.PROJECT_NAME);
		prepareOracleDatabase();
		addSQLServerDriver(Properties.PROJECT_NAME);
		prepareSQLServerDatabase();
	}


	@Test
	public void createOracleModel(){
		ImportJDBCDatabaseWizard wizard = new ImportJDBCDatabaseWizard();
		wizard.setConnectionProfile(Properties.ORACLE_CONNPROFILE_NAME);
		wizard.setProjectName(Properties.PROJECT_NAME);
		wizard.setModelName(Properties.ORACLE_MODEL_NAME);
		wizard.execute();

		assertTrue(Properties.ORACLE_MODEL_NAME + " not created!", 
				projectExplorer.existsResource(Properties.PROJECT_NAME, 
						Properties.ORACLE_MODEL_NAME));
	}


	@Test
	public void createSQLServerModel(){
		ImportJDBCDatabaseWizard wizard = new ImportJDBCDatabaseWizard();
		wizard.setConnectionProfile(Properties.SQLSERVER_CONNPROFILE_NAME);
		wizard.setProjectName(Properties.PROJECT_NAME);
		wizard.setModelName(Properties.SQLSERVER_MODEL_NAME);
		wizard.execute();

		assertTrue(Properties.SQLSERVER_MODEL_NAME + " not created!", 
				projectExplorer.existsResource(Properties.PROJECT_NAME, 
						Properties.SQLSERVER_MODEL_NAME));
	}


	@Test
	public void createViewModel(){
		CreateMetadataModel newModel = new CreateMetadataModel();
		newModel.setLocation(Properties.PROJECT_NAME);
		newModel.setName(Properties.PARTSVIRTUAL_MODEL_NAME);
		newModel.setType("View Model");
		newModel.execute();

		assertTrue(Properties.PARTSVIRTUAL_MODEL_NAME + " not created!", 
				projectExplorer.existsResource(Properties.PROJECT_NAME, 
						Properties.PARTSVIRTUAL_MODEL_NAME));
	}

	@Test
	public void createTransformation(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();
		modelView.newBaseTable(Properties.PROJECT_NAME, Properties.PARTSVIRTUAL_MODEL_NAME, "OnHand");
		modelView.addTransformationSource(Properties.PROJECT_NAME, Properties.ORACLE_MODEL_NAME, "SUPPLIER");
		modelView.addTransformationSource(Properties.PROJECT_NAME, Properties.SQLSERVER_MODEL_NAME, "SUPPLIER_PARTS");

		ModelEditor editor = ModelEditor.getInstance(Properties.PARTSVIRTUAL_MODEL_NAME);
		editor.show();
		editor.showTransformation();

		CriteriaBuilder criteriaBuilder = editor.criteriaBuilder();
		criteriaBuilder.selectRightAttribute(Properties.ORACLE_SUPPLIER, "SUPPLIER_ID");
		criteriaBuilder.selectLeftAttribute(Properties.SQLSERVER_SUPPLIER_PARTS, "SUPPLIER_ID");
		criteriaBuilder.apply();
		criteriaBuilder.finish();

		editor.save();

		assertEquals("SQL Statements do not match!", Properties.TEIID_SQL, editor.getTransformation());
	}


	@Test
	public void createTeiidInstance(){
		NewTeiidInstance teiid = new NewTeiidInstance();
		teiid.setHost(Properties.TEIID_HOST);
		teiid.setAdminPort(Properties.TEIID_ADMIN_PORT);
		teiid.setAdminUser("admin");
		teiid.setAdminPassword("admin");

		teiid.setUserPort(Properties.TEIID_JDBC_PORT);
		teiid.setUserName("user");
		teiid.setUserPassword("user");

		teiid.execute();

		assertTrue("Created server not visible in Teiid view.", 
				TeiidPerspective.getInstance().getTeiidInstanceView().containsTeiidInstance(Properties.TEIID_URL));	
	}

	@Test
	public void cleanupOldDatasources(){
		TeiidInstanceView view = TeiidPerspective.getInstance().getTeiidInstanceView();

		if (view.containsDataSource(Properties.TEIID_URL, Properties.ORACLE_TEIID_SOURCE)){
			view.deleteDataSource(Properties.TEIID_URL, Properties.ORACLE_TEIID_SOURCE);	
		}

		if (view.containsDataSource(Properties.TEIID_URL, Properties.SQLSERVER_TEIID_SOURCE)){
			view.deleteDataSource(Properties.TEIID_URL, Properties.SQLSERVER_TEIID_SOURCE);	
		}

		if (view.containsVDB(Properties.TEIID_URL, Properties.VDB_NAME)){
			view.undeployVDB(Properties.TEIID_URL, Properties.VDB_NAME);		
		}
	}

	@Test
	public void createOracleDataSource(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();

		DatasourceDialog dataSourceDialog = modelView.createDataSource(Properties.PROJECT_NAME, Properties.ORACLE_MODEL_NAME);
		dataSourceDialog.setName(Properties.ORACLE_TEIID_SOURCE);
		dataSourceDialog.finish();

		DatasourcePasswordDialog passwordDialog = dataSourceDialog.getPasswordDialog();
		passwordDialog.setPassword("mm");
		passwordDialog.finish();

		assertTrue("Data Source not created!",
				TeiidPerspective.getInstance().getTeiidInstanceView().containsDataSource(Properties.TEIID_URL, Properties.ORACLE_TEIID_SOURCE));
	}

	@Test
	public void createSQLServerDataSource(){
		ModelExplorerView modelView = TeiidPerspective.getInstance().getModelExplorerView();

		DatasourceDialog dataSourceDialog = modelView.createDataSource(Properties.PROJECT_NAME, Properties.SQLSERVER_MODEL_NAME);
		dataSourceDialog.setName(Properties.SQLSERVER_TEIID_SOURCE);
		dataSourceDialog.finish();

		DatasourcePasswordDialog passwordDialog = dataSourceDialog.getPasswordDialog();
		passwordDialog.setPassword("mm");
		passwordDialog.finish();

		assertTrue("Data Source not created!",
				TeiidPerspective.getInstance().getTeiidInstanceView().containsDataSource(Properties.TEIID_URL, Properties.SQLSERVER_TEIID_SOURCE));
	}

	@Test
	public void createVDB(){
		CreateVDB createVDB = new CreateVDB();
		createVDB.setFolder(Properties.PROJECT_NAME);
		createVDB.setName(Properties.VDB_NAME);
		createVDB.execute();

		VDBEditor editor = VDBEditor.getInstance(Properties.VDB_NAME + ".vdb");
		editor.show();
		editor.addModel(Properties.PROJECT_NAME, Properties.PARTSVIRTUAL_MODEL_NAME);
		editor.save();

		assertEquals(Properties.ORACLE_MODEL_NAME, editor.getModel(0));
		assertEquals(Properties.SQLSERVER_MODEL_NAME, editor.getModel(1));
		assertEquals(Properties.PARTSVIRTUAL_MODEL_NAME, editor.getModel(2));
	}

	@Test
	public void executeVDB(){
		TeiidPerspective.getInstance().getModelExplorerView().executeVDB(Properties.PROJECT_NAME, Properties.VDB_NAME + ".vdb");

		assertTrue("VDB not deployed!", TeiidPerspective.getInstance().getTeiidInstanceView().containsVDB(Properties.TEIID_URL, Properties.VDB_NAME));
	}


	//@Test
	public void procedureDefinition(){
		TeiidPerspective.getInstance().open();

		SWTBot viewBot = bot.viewByTitle("Model Explorer").bot();
		//create procedure
		SWTBotTreeItem node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				Properties.PROJECT_NAME, 
				Properties.PARTSVIRTUAL_MODEL_NAME,
				"OnHand");
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "New Sibling", "Procedure");

		viewBot.text("NewProcedure").setText("getOnHandByQuantity");
		viewBot.tree().setFocus();

		bot.sleep(TIME_1S);

		//create procedure parameter
		node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				Properties.PROJECT_NAME, 
				Properties.PARTSVIRTUAL_MODEL_NAME,
				"getOnHandByQuantity");
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "New Child", "Procedure Parameter");

		viewBot.text("NewProcedureParameter").setText("qtyIn");        
		viewBot.tree().setFocus();
		bot.sleep(TIME_1S);

		node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				Properties.PROJECT_NAME, 
				Properties.PARTSVIRTUAL_MODEL_NAME,
				"getOnHandByQuantity",
				"qtyIn");
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Set Datatype");

		SWTBotShell shell = bot.shell("Select a Datatype");
		shell.bot().table().getTableItem("short : xs:int").select();
		open.finish(shell.bot(), IDELabel.Button.OK);

		SWTTeiidBot teiidBot = new SWTTeiidBot();
		SWTBotTeiidCanvas canvas = teiidBot.getTeiidCanvas(0);
		canvas.debugCanvas();

		canvas.figure("getOnHandByQuantity").doubleClick();
		bot.sleep(TIME_1S);
		canvas.tFigure().doubleClick();

		node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				Properties.PROJECT_NAME, 
				Properties.PARTSVIRTUAL_MODEL_NAME,
				"OnHand");
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Add Transformation Source(s)");



		SWTBot editorBot = bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).bot();

		//set caret to the end
		int line = editorBot.styledText(0).getLineCount() - 2;
		String linestr = editorBot.styledText(0).getLines().get(line);
		int col = linestr.length()-1;

		editorBot.styledText(0).navigateTo(line, col);
		final StyledText textWidget = editorBot.styledText(0).widget;

		StyledTextHelper.mouseClickOnCaret(textWidget);

		editorBot.toolbarButtonWithTooltip("Criteria Builder").click();
		shell = bot.shell("Criteria Builder");
		shell.activate();

		shell.bot().tree(1).expandNode("PartsVirtual.OnHand")
		.select("PartsVirtual.OnHand.QUANTITY");

		shell.bot().tree(2).expandNode("PartsVirtual.getOnHandByQuantity")
		.select("PartsVirtual.getOnHandByQuantity.qtyIn");

		shell.bot().button("Apply").click();

		//Assert
		assertTrue(shell.bot().tree(0).select("PartsVirtual.OnHand.QUANTITY = PartsVirtual.getOnHandByQuantity.qtyIn").isVisible());

		open.finish(shell.bot(), IDELabel.Button.OK);
		bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).save();

		assertTrue("SQL Statements do not match!", 
				editorBot.styledText(0).getText().equals(Properties.PROCEDURE_SQL));

	}


	//@Test
	public void deployChanges(){

		bot.editorByTitle(Properties.VDB_NAME + ".vdb").show();
		bot.editorByTitle(Properties.VDB_NAME + ".vdb").setFocus();
		SWTBot editorBot = bot.editorByTitle(Properties.VDB_NAME + ".vdb").bot();
		editorBot.table(0).click(2, 2);

		SWTBotShell shell = bot.shell("Confirm");
		shell.activate();

		open.finish(shell.bot(), IDELabel.Button.OK);
		bot.editorByTitle(Properties.VDB_NAME + ".vdb").save();

		SWTBot viewBot = bot.viewByTitle("Model Explorer").bot();
		SWTBotTreeItem vdb_node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				Properties.PROJECT_NAME, 
				Properties.VDB_NAME + ".vdb");
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), vdb_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Deploy");

		bot.sleep(TIME_1S);

		SWTBot teiidBot = bot.viewByTitle("Teiid").bot();
		assertTrue("VDB deployment error!", 
				teiidBot.tree(0).expandNode(Properties.TEIID_URL, "VDBs")
				.select(Properties.VDB_NAME).isVisible());
	}


	@Test
	public void executeSqlQueries(){

		DatabaseDevelopmentPerspective.getInstance().getExplorerView().openSQLScrapbook(Properties.VDB_NAME + ".*", true);

		SQLScrapbookEditor editor = new SQLScrapbookEditor("SQL Scrapbook 0");
		editor.show();
		editor.setDatabase(Properties.VDB_NAME);


		// TESTSQL_1  
		editor.setText(Properties.TESTSQL_1);
		editor.executeAll();
		QueryResult res = getQueryResult(Properties.TESTSQL_1);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));


		// TESTSQL_2 
		editor.show();
		editor.setText(Properties.TESTSQL_2);
		editor.executeAll();

		res = getQueryResult(Properties.TESTSQL_2);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));

		// TESTSQL_3 
		editor.show();
		editor.setText(Properties.TESTSQL_3);
		editor.executeAll();

		res = getQueryResult(Properties.TESTSQL_3);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));
		assertTrue("SQL result rows:" + res.getRows(), res.getRows() == Properties.TESTSQL3_ROW_COUNT);

		// TESTSQL_4
		editor.show();
		editor.setText(Properties.TESTSQL_4);
		editor.executeAll();

		res = getQueryResult(Properties.TESTSQL_4);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));
		assertTrue("SQL result rows:" + res.getRows(), res.getRows() == Properties.TESTSQL4_ROW_COUNT);

		/*
		// TESTSQL_5
		scrapbookBot = bot.editorByTitle("SQL Scrapbook 0").bot();
		scrapbookBot.styledText().setText(Properties.TESTSQL_5);
		scrapbookBot.styledText().contextMenu("Execute All").click();
		shell = bot.shell("SQL Statement Execution");
		shell.activate();
		scrapbookBot.waitUntil(Conditions.shellCloses(shell));
		res = getQueryResult(Properties.TESTSQL_5);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));
		assertTrue("SQL result rows:" + res.getRows(), res.getRows() == Properties.TESTSQL5_ROW_COUNT);
		 */
		bot.editorByTitle("SQL Scrapbook 0").close();
	}

	private QueryResult  getQueryResult(final String sql){

		QueryResult result = new QueryResult();
		SWTBot resultsBot = bot.viewByTitle("SQL Results").bot();
		SWTBotTreeItem found = null;

		SWTBotTreeItem[] items = resultsBot.tree(0).getAllItems();
		for(SWTBotTreeItem item : items){

			if(item.cell(1).trim().equals(sql)){
				found = item;
				break;
			}
		}
		if(found == null){
			return result;
		}

		found.click();
		resultsBot.cTabItem("Result1").activate();

		result.setStatus(found.cell(0));		
		result.setRows(resultsBot.table().rowCount());
		return result;
	}




	private class QueryResult {

		private int rows = 0;
		private String status = "Not Found";

		public QueryResult() {
		}

		public String getStatus(){
			return this.status;
		}

		public void setStatus(String status){
			this.status = status;
		}

		public int getRows(){
			return this.rows;
		}

		public void setRows(int rows){
			this.rows = rows;
		}
	}
}
