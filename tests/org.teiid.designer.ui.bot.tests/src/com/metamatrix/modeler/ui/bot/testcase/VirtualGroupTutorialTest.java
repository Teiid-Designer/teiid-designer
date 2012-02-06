package com.metamatrix.modeler.ui.bot.testcase;


import org.eclipse.swt.custom.StyledText;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerType;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.StyledTextHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.designer.ui.bot.ext.teiid.SWTBotTeiidCanvas;
import org.teiid.designer.ui.bot.ext.teiid.SWTTeiidBot;

import com.metamatrix.modeler.ui.bot.testsuite.Properties;
import com.metamatrix.modeler.ui.bot.testsuite.TeiidDesignerTest;

@Require(server=@Server(type=ServerType.SOA,version="5.1", state=ServerState.Running), perspective="Teiid Designer")
public class VirtualGroupTutorialTest extends TeiidDesignerTest {

	private static final String CONNERR_MSG = "Unable to connect using the specified server properties." +
	  										  "The server properties could be invalid or the server may be offline.";

	@BeforeClass
	public static void beforeClass(){
		createProject();
		addOracleDriver(Properties.PROJECT_NAME);
		prepareOracleDatabase();
		addSQLServerDriver(Properties.PROJECT_NAME);
		prepareSQLServerDatabase();
//		addTeiidDriver(Properties.PROJECT_NAME);
//		prepareTeiidDatabase();
		
		openPerspective("Teiid Designer");
	}
	
	
	@Test
	public void oracleDataSource(){
		
		bot.menu(IDELabel.Menu.IMPORT).click();
		
		SWTBotShell shell = bot.shell("Import");
		shell.activate();
		shell.bot().tree(0).expandNode("Teiid Designer").select("JDBC Database >> Source Model");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().comboBox(0).setSelection(Properties.ORACLE_CONNPROFILE_NAME);
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().button("Deselect All").click();
		shell.bot().tableInGroup("Table Types").click(1, 0);
		shell.bot().button(IDELabel.Button.NEXT).click();
		
		assertTrue(shell.bot().tree(0).getTreeItem("PARTSSUPPLIER").isSelected());
		
		shell.bot().button(IDELabel.Button.NEXT).click();
		/*shell.bot().checkBoxInGroup("Include In Model").deselect();*/
		log.info("Uncheck Fully Qualified Names");
		shell.bot().checkBoxInGroup ("Model Object Names (Tables, Procedures, Columns, etc...)", 0).deselect();
		shell.bot().textWithLabel("Model Name:").setText(Properties.ORACLE_MODEL_NAME);
		shell.bot().button(1).click();
		
		SWTBotShell select_shell = bot.shell("Select a Folder");
		select_shell.bot().tree(0).select(Properties.PROJECT_NAME);
		select_shell.bot().button(IDELabel.Button.OK).click();
		
		open.finish(shell.bot());
		
		assertTrue(Properties.ORACLE_MODEL_NAME + " not created!", 
					projectExplorer.existsResource(Properties.PROJECT_NAME, 
												   Properties.ORACLE_MODEL_NAME));
	}
	
	
	@Test
	public void sqlserverDataSource(){
		
		bot.menu(IDELabel.Menu.IMPORT).click();
		
		SWTBotShell shell = bot.shell("Import");
		shell.activate();
		shell.bot().tree(0).expandNode("Teiid Designer").select("JDBC Database >> Source Model");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().comboBoxInGroup("Connection Profile").setSelection(Properties.SQLSERVER_CONNPROFILE_NAME);
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().button("Deselect All").click();
		shell.bot().tableInGroup("Table Types").click(1, 0);
		shell.bot().button(IDELabel.Button.NEXT).click();
		
		assertTrue(shell.bot().tree(0).getTreeItem("partssupplier").isSelected());
		
		shell.bot().button(IDELabel.Button.NEXT).click();
		
		/*shell.bot().checkBoxInGroup("Database", "Include In Model").deselect();*/
		/*shell.bot().checkBoxInGroup("Schema", "Include In Model").deselect();*/
		log.info("Uncheck Fully Qualified Names");
		shell.bot().checkBoxInGroup ("Model Object Names (Tables, Procedures, Columns, etc...)", 0).deselect();
		shell.bot().textWithLabel("Model Name:").setText(Properties.SQLSERVER_MODEL_NAME);
		shell.bot().button(1).click();
		
		SWTBotShell select_shell = bot.shell("Select a Folder");
		select_shell.bot().tree(0).select(Properties.PROJECT_NAME);
		select_shell.bot().button(IDELabel.Button.OK).click();
		
		open.finish(shell.bot());
		
		assertTrue(Properties.SQLSERVER_MODEL_NAME + " not created!", 
					projectExplorer.existsResource(Properties.PROJECT_NAME, 
												   Properties.SQLSERVER_MODEL_NAME));
	}
	
	
	@Test
	public void partsVirtualDataSource(){
		
		bot.viewByTitle("Model Explorer").show();
		SWTBotView view = bot.viewByTitle("Model Explorer");
		SWTBot viewBot = view.bot();
		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(viewBot, Properties.PROJECT_NAME);
		
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(),node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "New", "Other...");
		
		SWTBotShell wiz = bot.shell("New");
		wiz.bot().tree().expandNode("Teiid Designer").select("Teiid Metadata Model");
		
		wiz.bot().button(IDELabel.Button.NEXT).click();
		
		wiz.bot().textWithLabel("Model Name:").setText(Properties.PARTSVIRTUAL_MODEL_NAME);
		wiz.bot().comboBoxWithLabel("Model Type:").setSelection("View Model");
		
		open.finish(wiz.bot());
		
		assertTrue(Properties.PARTSVIRTUAL_MODEL_NAME + " not created!", 
				projectExplorer.existsResource(Properties.PROJECT_NAME, 
											   Properties.PARTSVIRTUAL_MODEL_NAME));
	
		
		SWTBotTreeItem model_node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				                                                      Properties.PROJECT_NAME, 
				                                                      Properties.PARTSVIRTUAL_MODEL_NAME);
		
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), model_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "New Child", "Base Table");
		
		viewBot.text("NewBaseTable").setText("OnHand");
		viewBot.tree().setFocus();
		
		viewBot.tree(0).expandNode(Properties.PROJECT_NAME, Properties.ORACLE_MODEL_NAME);
		viewBot.tree(0).expandNode(Properties.PROJECT_NAME, Properties.SQLSERVER_MODEL_NAME);
		viewBot.tree(0).expandNode(Properties.PROJECT_NAME, Properties.PARTSVIRTUAL_MODEL_NAME);
		
		
		assertTrue(Properties.PARTSVIRTUAL_MODEL_NAME + " not opened in active editor!",
				   bot.activeEditor().getTitle().equals(Properties.PARTSVIRTUAL_MODEL_NAME));
		
		bot.activeEditor().setFocus();
		SWTBotTreeItem oracle_node = SWTEclipseExt.selectTreeLocation(viewBot, 
															          Properties.PROJECT_NAME, 
															          Properties.ORACLE_MODEL_NAME,
															          "SUPPLIER");
		
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), oracle_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Add Transformation Source(s)");
		
		
		
		assertTrue(Properties.PARTSVIRTUAL_MODEL_NAME + " not opened in active editor!",
				   bot.activeEditor().getTitle().equals(Properties.PARTSVIRTUAL_MODEL_NAME));
		
		bot.activeEditor().setFocus();
		SWTBotTreeItem sql_node = SWTEclipseExt.selectTreeLocation(viewBot, 
				   												   Properties.PROJECT_NAME, 
				   												   Properties.SQLSERVER_MODEL_NAME,
				   				 								   "SUPPLIER_PARTS");

		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), sql_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Add Transformation Source(s)");
		
		bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).show();
		bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).setFocus();
		
		
		bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).save(); //calling it once doesn't work for me
		bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).save();
		

		SWTTeiidBot teiidBot = new SWTTeiidBot();
		SWTBotTeiidCanvas canvas = teiidBot.getTeiidCanvas(0);
		canvas.tFigure().doubleClick();

		
		SWTBot editorBot = bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).bot();
		
		//set caret to the end
		int lineCount = editorBot.styledText(0).getLineCount();
		String lastLine = editorBot.styledText(0).getLines().get(lineCount-1);
		int column = lastLine.length();
		
		editorBot.styledText(0).navigateTo(lineCount-1, column);
		
		final StyledText textWidget = editorBot.styledText(0).widget;
		StyledTextHelper.mouseClickOnCaret(textWidget);
				
				
		editorBot.toolbarButtonWithTooltip("Criteria Builder").click();

		SWTBotShell shell = bot.shell("Criteria Builder");
		shell.activate();
		
		shell.bot().tree(1).expandNode(Properties.SQLSERVER_SUPPLIER_PARTS)
								.select(Properties.SQLSERVER_SUPPLIER_PARTS + ".SUPPLIER_ID");
		
		shell.bot().tree(2).setFocus();
		shell.bot().tree(2).expandNode(Properties.ORACLE_SUPPLIER)
								.select(Properties.ORACLE_SUPPLIER + ".SUPPLIER_ID");
		
	
		shell.bot().button("Apply").click();
		
		//Assert
		assertTrue(shell.bot().tree(0).select(Properties.SQLSERVER_SUPPLIER_PARTS + ".SUPPLIER_ID = " + 
				                   Properties.ORACLE_SUPPLIER + ".SUPPLIER_ID").isVisible());

		open.finish(shell.bot(), IDELabel.Button.OK);
		bot.editorByTitle(Properties.PARTSVIRTUAL_MODEL_NAME).save();
		
		assertTrue("SQL Statements do not match!", 
				editorBot.styledText(0).getText().equals(Properties.TEIID_SQL));
		
	}
	
	
	@Test
	public void teiidInstance(){
		
		bot.toolbarButtonWithTooltip("Create a new Teiid instance").click();
		SWTBotShell shell = bot.shell("New Teiid Instance");
		shell.activate();
		
		shell.bot().textWithLabel("Host:").setText(Properties.TEIID_HOST);
		shell.bot().textWithLabelInGroup("Port number:", "Teiid Admin Connection Info").setText(Properties.TEIID_ADMIN_PORT);
		shell.bot().textWithLabelInGroup("User name:", "Teiid Admin Connection Info").setText("admin");
		shell.bot().textWithLabelInGroup("Password:", "Teiid Admin Connection Info").setText("admin");
		
		shell.bot().textWithLabelInGroup("Port number:", "Teiid JDBC Connection Info").setText(Properties.TEIID_JDBC_PORT);
		shell.bot().textWithLabelInGroup("User name:", "Teiid JDBC Connection Info",1).setText("user");
		shell.bot().textWithLabelInGroup("Password:", "Teiid JDBC Connection Info",2).setText("user");
		
		shell.bot().button("Test").click();
		shell = bot.shell("Test Teiid Connection");
		
		String msg = shell.bot().label(1).getText();
		assertTrue(CONNERR_MSG, msg.equals("Successfully connected using the specified Teiid server properties."));

		open.finish(bot.activeShell().bot(), IDELabel.Button.OK);
		shell = bot.shell("New Teiid Instance");
		shell.activate();

		open.finish(shell.bot()); 
		
		SWTBotView view = bot.viewByTitle("Teiid");
		
		assertTrue("Created server not visible in Teiid view.", 
				    view.bot().tree().getTreeItem(Properties.TEIID_URL).isVisible());	
	}
	
	@Test
	public void teiidOracleDataSource(){
		
		SWTBot viewBot = bot.viewByTitle("Model Explorer").bot();
		SWTBotTreeItem oracle_node =  SWTEclipseExt.selectTreeLocation(viewBot, 
																	   Properties.PROJECT_NAME, 
																	   Properties.ORACLE_MODEL_NAME);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), oracle_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Set Connection Profile");
		
		SWTBotShell shell = bot.shell("Set Connection Profile");
		shell.activate();
		shell.bot().tree(0).expandNode("Database Connections").select(Properties.ORACLE_CONNPROFILE_NAME);
		open.finish(shell.bot(), IDELabel.Button.OK);
		
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Create Data Source");
		
		shell = bot.shell("Create Data Source");
		shell.activate();
		shell.bot().textWithLabel("Data Source Name:").setText(Properties.ORACLE_TEIID_SOURCE);
		open.finish(shell.bot());
		
		shell = bot.shell("Data Source Password");
		shell.activate();
		shell.bot().textWithLabel("Password:").setText("mm");
		open.finish(shell.bot(), IDELabel.Button.OK);
		
		SWTBot teiidViewBot = bot.viewByTitle("Teiid").bot();
		
		assertTrue("Data Source not created!", 
				    teiidViewBot.tree().expandNode(Properties.TEIID_URL,
				    							   "Data Sources", 
				    							   	Properties.ORACLE_TEIID_SOURCE).isVisible());
		
		bot.editorByTitle(Properties.ORACLE_MODEL_NAME).save();
	}
	
	@Test
	public void teiidSQLSERVERDataSource(){
		
		SWTBot viewBot = bot.viewByTitle("Model Explorer").bot();
		SWTBotTreeItem sql_node =  SWTEclipseExt.selectTreeLocation(viewBot, 
																	   Properties.PROJECT_NAME, 
																	   Properties.SQLSERVER_MODEL_NAME);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), sql_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Set Connection Profile");
		
		SWTBotShell shell = bot.shell("Set Connection Profile");
		shell.activate();
		shell.bot().tree(0).expandNode("Database Connections").select(Properties.SQLSERVER_CONNPROFILE_NAME);
		open.finish(shell.bot(), IDELabel.Button.OK);
		
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Create Data Source");
		
		shell = bot.shell("Create Data Source");
		shell.activate();
		shell.bot().textWithLabel("Data Source Name:").setText(Properties.SQLSERVER_TEIID_SOURCE);
		open.finish(shell.bot());
		
		shell = bot.shell("Data Source Password");
		shell.activate();
		shell.bot().textWithLabel("Password:").setText("mm");
		open.finish(shell.bot(), IDELabel.Button.OK);
		
		SWTBot teiidViewBot = bot.viewByTitle("Teiid").bot();
		
		assertTrue("Data Source not created!", 
				    teiidViewBot.tree().expandNode(Properties.TEIID_URL,
				    							   "Data Sources", 
				    							   	Properties.SQLSERVER_TEIID_SOURCE).isVisible());
		
		bot.editorByTitle(Properties.SQLSERVER_MODEL_NAME).save();
	}
	

	
	
	@Test
	public void vdbDefinition(){
		
		SWTBot viewBot = bot.viewByTitle("Model Explorer").bot();
		SWTBotTreeItem node =  SWTEclipseExt.selectTreeLocation(viewBot, Properties.PROJECT_NAME);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "New", "Other...");
		
		SWTBotShell shell = bot.shell("New");
		shell.activate();
		shell.bot().tree(0).expandNode("Teiid Designer").select("Teiid VDB");
		shell.bot().button(IDELabel.Button.NEXT).click();
		
		shell.bot().textWithLabel("VDB Name:").setText(Properties.VDB_NAME);
		open.finish(shell.bot());
		
		bot.editorByTitle(Properties.VDB_NAME + ".vdb").setFocus();
		SWTBot editorBot = bot.editorByTitle(Properties.VDB_NAME + ".vdb").bot();
		editorBot.toolbarButtonWithTooltip("Add model").click();
		
		shell = bot.shell("Add File(s) to VDB");
		shell.activate();
		shell.bot().tree(0).expandNode(Properties.PROJECT_NAME).select(Properties.PARTSVIRTUAL_MODEL_NAME);
		open.finish(shell.bot(), IDELabel.Button.OK);
		
		bot.editorByTitle(Properties.VDB_NAME + ".vdb").save();
		
		
		
		SWTBotTable table = editorBot.table(0); //table with models
		
		assertTrue(table.cell(0, 0).equals(Properties.ORACLE_MODEL_NAME));
		assertTrue(table.cell(1, 0).equals(Properties.SQLSERVER_MODEL_NAME));
		assertTrue(table.cell(2, 0).equals(Properties.PARTSVIRTUAL_MODEL_NAME));

	}
	
	@Test
	public void deployVDB(){
		
		SWTBot viewBot = bot.viewByTitle("Model Explorer").bot();
		SWTBotTreeItem vdb_node =  SWTEclipseExt.selectTreeLocation(viewBot, 
																	   Properties.PROJECT_NAME, 
																	   Properties.VDB_NAME + ".vdb");
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), vdb_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Modeling", "Execute VDB");
		
		bot.sleep(TIME_5S);
		
		openPerspective("Teiid Designer");
		SWTBot teiidBot = bot.viewByTitle("Teiid").bot();
		assertTrue("VDB deployment error!", 
				          teiidBot.tree(0).expandNode(Properties.TEIID_URL, "VDBs")
				                          .select(Properties.VDB_NAME).isVisible());
		
	}
	
	
	//@Test
	public void procedureDefinition(){
		openPerspective("Teiid Designer");
		
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
		
		prepareWorkspaceForQueries();
		
		// TESTSQL_1  
		SWTBot scrapbookBot = bot.editorByTitle("SQL Scrapbook 0").bot();
		scrapbookBot.styledText().setText(Properties.TESTSQL_1);
		scrapbookBot.styledText().contextMenu("Execute All").click();
		SWTBotShell shell = bot.shell("SQL Statement Execution");
		shell.activate();
		scrapbookBot.waitUntil(Conditions.shellCloses(shell));
		QueryResult res = getQueryResult(Properties.TESTSQL_1);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));
		
		
		// TESTSQL_2 
		scrapbookBot = bot.editorByTitle("SQL Scrapbook 0").bot();
		scrapbookBot.styledText().setText(Properties.TESTSQL_2);
		scrapbookBot.styledText().contextMenu("Execute All").click();
		shell = bot.shell("SQL Statement Execution");
		shell.activate();
		scrapbookBot.waitUntil(Conditions.shellCloses(shell));
		res = getQueryResult(Properties.TESTSQL_2);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));
		
		// TESTSQL_3 
		scrapbookBot = bot.editorByTitle("SQL Scrapbook 0").bot();
		scrapbookBot.styledText().setText(Properties.TESTSQL_3);
		scrapbookBot.styledText().contextMenu("Execute All").click();
		shell = bot.shell("SQL Statement Execution");
		shell.activate();
		scrapbookBot.waitUntil(Conditions.shellCloses(shell));
		res = getQueryResult(Properties.TESTSQL_3);
		assertTrue("SQL query status:" + res.getStatus(), res.getStatus().equals("Succeeded"));
		assertTrue("SQL result rows:" + res.getRows(), res.getRows() == Properties.TESTSQL3_ROW_COUNT);
		
		// TESTSQL_4
		scrapbookBot = bot.editorByTitle("SQL Scrapbook 0").bot();
		scrapbookBot.styledText().setText(Properties.TESTSQL_4);
		scrapbookBot.styledText().contextMenu("Execute All").click();
		shell = bot.shell("SQL Statement Execution");
		shell.activate();
		scrapbookBot.waitUntil(Conditions.shellCloses(shell));
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
	
	@Test
	public void removeResourcesTest(){
		openPerspective("Teiid Designer");
		
		bot.viewByTitle("Teiid").show();
		bot.viewByTitle("Teiid").setFocus();
		
		SWTBot viewBot = bot.viewByTitle("Teiid").bot();
		viewBot.tree().getTreeItem(Properties.TEIID_URL);
		
		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(viewBot, Properties.TEIID_URL, "Data Sources", Properties.ORACLE_TEIID_SOURCE);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(),node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Delete Data Source");
		
		node = SWTEclipseExt.selectTreeLocation(viewBot, Properties.TEIID_URL, "Data Sources", Properties.SQLSERVER_TEIID_SOURCE);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(),node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Delete Data Source");
		
		node = SWTEclipseExt.selectTreeLocation(viewBot, Properties.TEIID_URL, "VDBs", Properties.VDB_NAME);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(),node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Undeploy VDB");
		
		SWTBotTreeItem item = viewBot.tree().getTreeItem(Properties.TEIID_URL);
		try{
			node = item.expandNode("Data Sources", Properties.ORACLE_TEIID_SOURCE);
			if(node.isVisible())
				fail("Resource " + Properties.ORACLE_TEIID_SOURCE + " not removed!");
		
		}catch (WidgetNotFoundException e) {}
		
		try{
			node = item.expandNode("Data Sources", Properties.SQLSERVER_TEIID_SOURCE);
			if(node.isVisible())
				fail("Resource " + Properties.SQLSERVER_TEIID_SOURCE + " not removed!");
		
		}catch (WidgetNotFoundException e) {}
		
		try{
			node = item.expandNode("VDBs", Properties.VDB_NAME);
			if(node.isVisible())
				fail("Resource " + Properties.VDB_NAME + " not removed!");
		
		}catch (WidgetNotFoundException e) {}		
	}

}
