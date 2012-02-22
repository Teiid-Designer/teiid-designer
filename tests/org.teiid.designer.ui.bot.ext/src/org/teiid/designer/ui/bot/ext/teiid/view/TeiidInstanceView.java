package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.teiid.designer.ui.bot.ext.teiid.instance.TeiidInstanceDialog;

/**
 * Represents a view where one can manage Teiid instances. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class TeiidInstanceView extends View {

	public TeiidInstanceView() {
		super("Teiid");
	}

	public TeiidInstanceDialog newTeiidInstance(){
		SWTBot bot = SWTBotFactory.getBot();
		bot.toolbarButtonWithTooltip("Create a new Teiid instance").click();
		SWTBotShell shell = bot.shell("New Teiid Instance").activate();
		return new TeiidInstanceDialog(shell);
	}
	
	public void reconnect(String teiidInstance){
		SWTBot bot = SWTBotFactory.getBot();

		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(bot, teiidInstance);

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Reconnect");
	}

	public void deleteDataSource(String teiidInstance, String dataSource){
		SWTBot bot = SWTBotFactory.getBot();

		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(bot, teiidInstance, "Data Sources", dataSource);

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Delete Data Source");
	}

	public void undeployVDB(String teiidInstance, String vdb){
		SWTBot bot = SWTBotFactory.getBot();

		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(bot, teiidInstance, "VDBs", vdb);

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Undeploy VDB");
	}

	public boolean containsDataSource(String teiidInstance, String datasource){
		SWTBot bot = SWTBotFactory.getBot();
		try {
			SWTBotTreeItem item = bot.tree().expandNode(teiidInstance, "Data Sources");
			item.getNode(datasource);
			return true;
		} catch (WidgetNotFoundException e){
			return false;
		}
	}
	
	public boolean containsVDB(String teiidInstance, String vdb){
		SWTBot bot = SWTBotFactory.getBot();
		try {
			SWTBotTreeItem item = bot.tree().expandNode(teiidInstance, "VDBs");
			item.getNode(vdb);
			return true;
		} catch (WidgetNotFoundException e){
			return false;
		}
	}
	
	public boolean containsTeiidInstance(String name){
		SWTBot bot = SWTBotFactory.getBot();
		
		try {
			bot.tree().getTreeItem(name);
			return true;
		} catch (WidgetNotFoundException e){
			return false;
		}
	}
}
