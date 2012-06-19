package org.teiid.designer.ui.bot.test;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.Seam;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerType;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.designer.ui.bot.test.suite.Properties;
import org.teiid.designer.ui.bot.test.suite.TeiidDesignerTestCase;


/**
 * 
 * @author psrna
 *
 */
@Require(server=@Server(type=ServerType.SOA,version="5.1"), seam=@Seam())
public class TeiidSourceInSeamTest extends TeiidDesignerTestCase {
	
	private static final String TEMP_PROJECT = "temp";
	
	@BeforeClass
	public static void beforeClass(){
		openPerspective("Seam (default)");
		
		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.NEW).menu(IDELabel.Menu.OTHER).click();
		SWTBotShell shell = bot.shell("New");
		shell.bot().tree(0).expandNode("General").select("Project");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().textWithLabel("Project name:").setText(TEMP_PROJECT);
		open.finish(shell.bot());
		
		setupConnection();
	}
	
	public static void setupConnection(){

		addTeiidDriver(TEMP_PROJECT);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(TEMP_PROJECT);
		stringBuilder.append(File.separator);
		stringBuilder.append("teiid-7.2.0.Final-client.jar");
		
		DriverEntity entity = new DriverEntity();
		entity.setDrvPath(stringBuilder.toString());
		entity.setDatabaseName("ModeShape");
		entity.setInstanceName("Teiid Server JDBC Driver");
		entity.setProfileName(Properties.SEAM_CONNPROFILE_NAME);
		entity.setProfileDescription("Teiid Source in Seam");
		entity.setJdbcString("jdbc:teiid:ModeShape@mm://localhost:31000");
		entity.setDriverTemplateDescId(DatabaseHelper.getDriverTemplate(DatabaseHelper.DBType.teiid));
		entity.setDriverDefId("Teiid Server DB");
		entity.setUser("admin");
		entity.setPassword("teiid");

		prepareDatabase(entity, Properties.SEAM_CONNPROFILE_NAME);
	}
	
	
	@Test
	public void createSeamProject(){
		
		eclipse.maximizeActiveShell();
		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.NEW).menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("New");
		shell.bot().tree(0).expandNode("Seam").select("Seam Web Project");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().textWithLabel("Project name:").setText(Properties.SEAM_PROJECT_NAME);
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().button(IDELabel.Button.NEXT).click();
		
		shell.bot().comboBoxWithLabel("Database Type:").setSelection("Teiid");
		shell.bot().comboBoxWithLabel("Connection profile:").setSelection(Properties.SEAM_CONNPROFILE_NAME);
		shell.bot().checkBoxWithLabel("Create Test Project:").deselect();
		open.finish(shell.bot());
		
		bot.sleep(TIME_10S);
		
		assertTrue(SWTTestExt.projectExplorer.existsResource(Properties.SEAM_PROJECT_NAME));
	}
	
	@Test
	public void connectDBTest(){
		
		openPerspective("Hibernate");
		
		bot.viewByTitle("Hibernate Configurations").setFocus();
		bot.viewByTitle("Hibernate Configurations").show();
		
		SWTBot viewBot = bot.viewByTitle("Hibernate Configurations").bot();
		SWTBotTreeItem item = TreeHelper.expandNode(viewBot, 
				                                    Properties.SEAM_PROJECT_NAME, 
				                                    "Database", 
				                                    "ModeShape.ModeShape");
		item.select();
		assertTrue("Could not find node with text:" + item.getText(),item.isVisible());
	}
	
	@Test
	public void seamGenerateEntitiesTest(){
		
		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.NEW).menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("New");
		shell.activate();
		shell.bot().tree(0).expandNode("Seam").select("Seam Generate Entities");
		shell.bot().button(IDELabel.Button.NEXT).click();

		shell.bot().button("Browse...").click();
		shell = bot.shell("Seam Web Projects");
		shell.activate();
		shell.bot().table(0).getTableItem(Properties.SEAM_PROJECT_NAME).click();
		open.finish(shell.bot(), IDELabel.Button.OK);
		
		shell = bot.shell("Generate Seam Entities");
		shell.activate();
		shell.bot().comboBoxWithLabel("Hibernate Console Configuration:").setSelection(Properties.SEAM_PROJECT_NAME);
		shell.bot().radio("Reverse engineer from database").click();
		shell.bot().button(IDELabel.Button.NEXT).click();
		
		shell.bot().button("Refresh").click();
		shell.bot().sleep(TIME_5S);
		
		SWTBotTreeItem item = shell.bot().tree(0).getTreeItem("ModeShape");
		item.expand();
		shell.bot().sleep(TIME_5S);
		item = item.getNode("ModeShape");
		item.expand();
		shell.bot().sleep(TIME_5S);
		item = item.getNode("xmi_model");
		item.select();
		
		shell.bot().button("Include...").click();
		
		assertTrue(shell.bot().table(0).cell(0, "Catalog").equals("ModeShape"));
		assertTrue(shell.bot().table(0).cell(0, "Schema").equals("ModeShape"));
		assertTrue(shell.bot().table(0).cell(0, "Table").equals("xmi_model"));
		
		open.finish(shell.bot());
		
		assertTrue(SWTTestExt.projectExplorer.existsResource(Properties.SEAM_PROJECT_NAME, 
				                                             "src", 
				                                             "main", 
				                                             "org", 
				                                             "domain", 
				                                             Properties.SEAM_PROJECT_NAME, 
				                                             "entity", 
				                                             "XmiModel.java"));
		
		assertTrue(SWTTestExt.projectExplorer.existsResource(Properties.SEAM_PROJECT_NAME, 
                                                             "src", 
                                                             "main", 
                                                             "org", 
                                                             "domain", 
                                                             Properties.SEAM_PROJECT_NAME, 
                                                             "entity", 
                                                             "XmiModelId.java"));
	}
	

}
