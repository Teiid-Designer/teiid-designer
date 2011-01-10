package com.metamatrix.modeler.ui.bot.testcase;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Seam;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerType;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.BeforeClass;
import org.junit.Test;

import com.metamatrix.modeler.ui.bot.testsuite.Properties;
import com.metamatrix.modeler.ui.bot.testsuite.TeiidDesignerTest;

/**
 * 
 * @author psrna
 *
 */
@SWTBotTestRequires(server=@Server(type=ServerType.SOA,version="5.1")/*, seam=@Seam()*/)
public class TeiidSourceInHibernateToolsTest extends TeiidDesignerTest{

	@BeforeClass
	public static void beforeClass(){
	
		openPerspective("JPA");
		
		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.NEW).menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("New");
		shell.activate();
		shell.bot().tree(0).expandNode("JPA").select("JPA Project");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().textWithLabel("Project name:").setText(Properties.HIB_PROJECT_NAME);
		//shell.bot().button(IDELabel.Button.NEXT).click();
		//shell.bot().button(IDELabel.Button.NEXT).click();
		open.finish(shell.bot());
		
		setupConnection();
	}
	
	public static void setupConnection(){

		addTeiidDriver(Properties.HIB_PROJECT_NAME);
		addDriverClassPath(Properties.TEIID_HIBDIALECT_DRIVER, Properties.HIB_PROJECT_NAME);
		addDriverClassPath(Properties.TEIID_DRIVER, Properties.HIB_PROJECT_NAME);
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Platform.getLocation());
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.HIB_PROJECT_NAME);
		stringBuilder.append(File.separator);
		stringBuilder.append(Properties.TEIID_DRIVER);
		
		DriverEntity entity = new DriverEntity();
		entity.setDrvPath(stringBuilder.toString());
		entity.setDatabaseName("ModeShape");
		entity.setInstanceName("Teiid Server JDBC Driver");
		entity.setProfileName(Properties.HIB_CONNPROFILE_NAME);
		entity.setProfileDescription("Teiid Source in Hibernate Tools");
		entity.setJdbcString("jdbc:teiid:ModeShape@mm://localhost:31000");
		entity.setDriverTemplateDescId(DatabaseHelper.getDriverTemplate(DatabaseHelper.DBType.teiid));
		entity.setDriverDefId("Teiid Server DB");
		entity.setUser("teiid");
		entity.setPassword("teiid");

		prepareDatabase(entity, Properties.HIB_CONNPROFILE_NAME);
	}
	
	@Test
	public void createConsoleConfigurationTest(){
		
		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.NEW).menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("New");
		shell.activate();
		shell.bot().tree(0).expandNode("Hibernate").select("Hibernate Console Configuration");
		shell.bot().button(IDELabel.Button.NEXT).click();
		shell.bot().textWithLabel("Name:").setText(Properties.HIB_PROJECT_NAME);
		shell.bot().buttonInGroup("Browse...", "Project:").click();
		SWTBotShell select_shell = bot.shell("Select java project");
		select_shell.bot().table(0).getTableItem(Properties.HIB_PROJECT_NAME).select();
		open.finish(select_shell.bot(), IDELabel.Button.OK);
		
		shell.activate();
		shell.bot().comboBoxInGroup("Database connection:").setSelection(Properties.HIB_CONNPROFILE_NAME);
		
		open.finish(shell.bot());
		
		openPerspective("Hibernate");
		
		bot.viewByTitle("Hibernate Configurations").show();
		bot.viewByTitle("Hibernate Configurations").setFocus();
		
		SWTBot viewBot = bot.viewByTitle("Hibernate Configurations").bot();
		SWTBotTreeItem item = viewBot.tree(0).getTreeItem(Properties.HIB_PROJECT_NAME);
		assertTrue(item.isVisible());
	}
	
	@Test
	public void dbConnectTest(){
		openPerspective("Hibernate");
		
		bot.viewByTitle("Hibernate Configurations").show();
		bot.viewByTitle("Hibernate Configurations").setFocus();
		
		SWTBot viewBot = bot.viewByTitle("Hibernate Configurations").bot();		
		viewBot.tree().setFocus();
		
		SWTBotTreeItem item = TreeHelper.expandNode(viewBot, 
				                                    Properties.HIB_PROJECT_NAME, 
				                                    "Database", 
				                                    "ModeShape.ModeShape");
		item.select();
		assertTrue("Could not find node with text:" + item.getText(),item.isVisible());
	}
}
