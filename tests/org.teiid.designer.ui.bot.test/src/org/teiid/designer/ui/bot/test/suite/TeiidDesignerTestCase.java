package org.teiid.designer.ui.bot.test.suite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.condition.NonSystemJobRunsCondition;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.AfterClass;
import org.teiid.designer.ui.bot.ext.teiid.editor.ModelEditor;
import org.teiid.designer.ui.bot.ext.teiid.wizard.NewTeiidModelProjectWizard;
import org.teiid.designer.ui.bot.test.Activator;


/**
 * Common ancestor for Teiid tests. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class TeiidDesignerTestCase extends SWTTestExt {
	
	public static void createProject(String name){
		NewTeiidModelProjectWizard wizard = new NewTeiidModelProjectWizard();
		wizard.setProjectName(name);
		wizard.execute();
	}
	
	public static ModelEditor modelEditor(String title) {
		SWTBotEditor editor = bot.editorByTitle(title);
		ModelEditor modelEditor = new ModelEditor(editor.getReference(), bot);
		return modelEditor;
	}
	
	public static void openPerspective(final String name){
		
		bot.menu(IDELabel.Menu.WINDOW)
		   .menu(IDELabel.Menu.OPEN_PERSPECTIVE)
		   .menu(IDELabel.Menu.OTHER).click();
		
		SWTBotShell shell = bot.shell("Open Perspective");
		shell.bot().table().select(name);
		open.finish(shell.bot(), IDELabel.Button.OK);
		
	}
	
	/**
	 * Copy driver into project
	 */
	public static void addOracleDriver(final String projectName) {
		try {
			addDriver(Properties.ORACLE_DRIVER, projectName);
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
	public static void addSQLServerDriver(final String projectName) {
		try {
			addDriver(Properties.SQLSERVER_DRIVER, projectName);
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
	public static void addTeiidDriver(final String projectName) {
		try {
			addDriver("teiid-7.2.0.Final-client.jar", projectName);
			addDriver("teiid-hibernate-dialect-7.2.0.Final.jar", projectName);
		}catch (FileNotFoundException e) {
			fail(e.getMessage());
		} catch (IOException e) {
			fail(e.getMessage());
		}
		//addDriverClassPath(Properties.TEIID_HIBDIALECT_DRIVER, projectName);
	}
	
	/**
	 * Add Driver to classpath
	 * @param driver jar file name
	 */
	public static void addDriverClassPath(String driver, final String projectName) {
			
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
	    bot.tree().expandNode(projectName).expandNode(driver).select();
	    
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(TIME_1S);
	}
	
	public static void prepareDatabase(DriverEntity entity, final String profileName){
		
		try {
			DatabaseHelper.createDriver(entity, profileName);
		} catch (ConnectionProfileException e) {
			log.error("Unable to create Server Driver" + e);
			fail();	
		}	
	}
	
	
	/**
	 * Add driver into project
	 * @param jar_name jar file name
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void addDriver(final String jar_name,final String projectName) throws FileNotFoundException, IOException {
		
		
		File in = SWTUtilExt.getResourceFile(Activator.PLUGIN_ID, "lib",jar_name);
		File out = new File(Platform.getLocation() + File.separator + projectName + File.separator + jar_name);
		
        FileChannel inChannel = null;
        FileChannel outChannel = null;

		inChannel = new FileInputStream(in).getChannel();
		outChannel = new FileOutputStream(out).getChannel();

    	inChannel.transferTo(0, inChannel.size(),	outChannel);

    	if (inChannel != null) inChannel.close();
    	if (outChannel != null) outChannel.close();
    	log.info("Driver " + jar_name + " copied");
	}

	@AfterClass
	public static void saveAllFiles() {
		if(bot.menu("File").menu("Save All").isEnabled()) {
			bot.menu("File").menu("Save All").click();
			bot.waitWhile(new NonSystemJobRunsCondition(), TaskDuration.LONG.getTimeout());
			log.info("All files saved.");
		}
	}
}
