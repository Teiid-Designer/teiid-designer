package com.metamatrix.modeler.ui.bot.testsuite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;


import com.metamatrix.modeler.ui.bot.testcase.Activator;

public class TeiidDesignerTest extends SWTTestExt {
	
	public static void createProject(){
		
		eclipse.maximizeActiveShell();
		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.NEW).menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("New");
		shell.bot().tree(0).expandNode("Teiid Designer").select("Teiid Model Project");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().textWithLabel("Project name:").typeText(Properties.PROJECT_NAME);
		
		open.finish(shell.bot());
		
		//assertTrue("Teiid Model Project creation failure", projectExplorer.existsResource(Properties.PROJECT_NAME));
	}
	
	
	public static void openPerspective(final String name){
		
		bot.menu(IDELabel.Menu.WINDOW)
		   .menu(IDELabel.Menu.OPEN_PERSPECTIVE)
		   .menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("Open Perspective");
		shell.bot().table().select(name);
		open.finish(shell.bot(), IDELabel.Button.OK);
		
	}
	
	private static void openSqlScrapbook(){
		
		SWTBot viewBot = bot.viewByTitle("Data Source Explorer").bot();
		
		viewBot.tree().expandNode("Database Connections").getNode(Properties.TEIID_CONNPROFILE_NAME)
		                                                 .doubleClick();
		
		SWTBotTreeItem db_node =  SWTEclipseExt.selectTreeLocation(viewBot, 
				                                                  "Database Connections", 
				                                                   Properties.TEIID_CONNPROFILE_NAME);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(), db_node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Open SQL Scrapbook");	
		
	}
	
	private static void connectTeiidDB(){
		
		SWTBot viewBot = bot.viewByTitle("Data Source Explorer").bot();
		SWTBotTreeItem node = viewBot.tree(0).expandNode("Database Connections").getNode(Properties.TEIID_CONNPROFILE_NAME);
		ContextMenuHelper.prepareTreeItemForContextMenu(viewBot.tree(0), node);
		ContextMenuHelper.clickContextMenu(viewBot.tree(0), "Connect");
		
	}
	
	
	public static void prepareWorkspaceForQueries(){
		openPerspective("Database Development");
		connectTeiidDB();
		openSqlScrapbook();
	}
	
	
	/**
	 * Copy driver into project
	 */
	public static void addOracleDriver() {
		try {
			addDriver(Properties.ORACLE_DRIVER);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		//addDriverClassPath(Properties.ORACLE_DRIVER);		
	}
	
	/**
	 * Copy driver into project
	 */
	public static void addSQLServerDriver() {
		try {
			addDriver(Properties.SQLSERVER_DRIVER);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		//addDriverClassPath(Properties.SQLSERVER_DRIVER);		
	}
	
	/**
	 * Copy driver into project
	 */
	public static void addTeiidDriver() {
		try {
			addDriver(Properties.TEIID_DRIVER);
		}catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		//addDriverClassPath(Properties.TEIID_DRIVER);
	}
	
	/**
	 * Add Driver to classpath
	 * @param driver jar file name
	 */
	public static void addDriverClassPath(String driver) {
			
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTBotView view = bot.viewByTitle("Project Explorer");
		view.show();
		view.setFocus();
		SWTBot packageExplorerBot = view.bot();		
		SWTBotTree tree = packageExplorerBot.tree();

		// Bot workaround for Bot menu
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);	    
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Refresh",false)).click();

		ContextMenuHelper.prepareTreeItemForContextMenu(tree);	    
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Properties",false)).click();
	    
	    // Set build path
	    bot.tree().expandNode("Java Build Path").select();
	    bot.tabItem("Libraries").activate();
	    bot.button("Add JARs...").click();
	    bot.sleep(TIME_500MS);
	    bot.tree().expandNode(Properties.PROJECT_NAME).expandNode(driver).select();
	    
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	}
	
	/**
	 * Prepares database 
	 */
	public static void prepareOracleDatabase()  {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.ORACLE_DRIVER);
		
		try {
			DriverEntity entity = new DriverEntity();
			entity.setDrvPath(stringBuilder.toString());
			entity.setDatabaseName("ORCL");
			entity.setInstanceName("Oracle Thin Driver");
			entity.setProfileName(Properties.ORACLE_CONNPROFILE_NAME);
			entity.setProfileDescription("PartsSupplier Oracle database");
			entity.setJdbcString("jdbc:oracle:thin:@englxdbs11.mm.atl2.redhat.com:1521:ORCL");
			entity.setDriverTemplateDescId("org.eclipse.datatools.enablement.oracle.10.driverTemplate");
			entity.setDriverDefId("Oracle DB");
			entity.setUser("partssupplier");
			entity.setPassword("mm");
			DatabaseHelper.createDriver(entity, Properties.ORACLE_CONNPROFILE_NAME);
		} catch (ConnectionProfileException e) {
			log.error("Unable to create Oracle Driver" + e);
			fail();			
		}

	}
	
	/**
	 * Prepares database 
	 */
	public static void prepareSQLServerDatabase()  {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.SQLSERVER_DRIVER);
		
		try {
			DriverEntity entity = new DriverEntity();
			entity.setDrvPath(stringBuilder.toString());
			entity.setDatabaseName("PartsSupplier");
			entity.setInstanceName("Microsoft SQL Server 2008 JDBC Driver");
			entity.setProfileName(Properties.SQLSERVER_CONNPROFILE_NAME);
			entity.setProfileDescription("PartsSupplier SQL Server database");
			entity.setJdbcString("jdbc:sqlserver://slntdb02.mm.atl2.redhat.com:1433;databaseName=PartsSupplier");
			entity.setDriverTemplateDescId("org.eclipse.datatools.enablement.msft.sqlserver.2008.driverTemplate");
			entity.setDriverDefId("SQL Server DB");
			entity.setUser("PartsSupplier");
			entity.setPassword("mm");
			DatabaseHelper.createDriver(entity, Properties.SQLSERVER_CONNPROFILE_NAME);
		} catch (ConnectionProfileException e) {
			log.error("Unable to create SQL Server Driver" + e);
			fail();			
		}
	}
	
	/**
	 * Prepares database
	 */
	public static void prepareTeiidDatabase(){

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.TEIID_DRIVER);
		
		try {
			DriverEntity entity = new DriverEntity();
			entity.setDrvPath(stringBuilder.toString());
			entity.setDatabaseName(Properties.VDB_NAME);
			entity.setInstanceName("Teiid Server JDBC Driver");
			entity.setProfileName(Properties.TEIID_CONNPROFILE_NAME);
			entity.setProfileDescription("PartsSupplier Teiid Server database");
			entity.setJdbcString("jdbc:teiid:MyFirstVDB@mm://localhost:31000");
			entity.setDriverTemplateDescId(DatabaseHelper.getDriverTemplate(DatabaseHelper.DBType.teiid));
			entity.setDriverDefId("Teiid Server DB");
			entity.setUser("admin");
			entity.setPassword("teiid");
			DatabaseHelper.createDriver(entity, Properties.TEIID_CONNPROFILE_NAME);
		} catch (ConnectionProfileException e) {
			log.error("Unable to create Teiid Server Driver" + e);
			fail();			
		}
	}
	
	/**
	 * Add driver into project
	 * @param jar_name jar file name
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addDriver(String jar_name) throws FileNotFoundException, IOException {
		
		
		File in = SWTUtilExt.getResourceFile(Activator.PLUGIN_ID, "lib",jar_name);
		File out = new File(Platform.getLocation() + File.separator + Properties.PROJECT_NAME + File.separator + jar_name);
		
        FileChannel inChannel = null;
        FileChannel outChannel = null;

		inChannel = new FileInputStream(in).getChannel();
		outChannel = new FileOutputStream(out).getChannel();

    	inChannel.transferTo(0, inChannel.size(),	outChannel);

    	if (inChannel != null) inChannel.close();
    	if (outChannel != null) outChannel.close();
    	log.info("Driver " + jar_name + " copied");
	}

}
