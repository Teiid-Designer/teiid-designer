package org.teiid.designer.ui.bot.ext.teiid.view;

import java.util.List;
import java.util.Vector;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.gen.IView;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.view.ViewBase;
import org.teiid.designer.ui.bot.ext.teiid.instance.TeiidInstanceDialog;

/**
 * Represents a view where one can manage Teiid instances. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class TeiidInstanceView extends ViewBase {

	public static final String TOOLBAR_CREATE_TEIID = "Create a new Teiid instance";
	public static final String TOOLBAR_RECONNECT_TEIID = "Reconnect to the selected Teiid Instance";
	
	public TeiidInstanceView() {
		viewObject = new IView() {
			public String getName() {
				return "Teiid";
			}

			public List<String> getGroupPath() {
				List<String> list = new Vector<String>();
				list.add("Teiid Designer");
				return list;
			}
		};
	}

	public TeiidInstanceDialog newTeiidInstance(){
		getToolbarButtonWitTooltip(TOOLBAR_CREATE_TEIID).click();
		SWTBotShell shell = bot.shell("New Teiid Instance").activate();
		return new TeiidInstanceDialog(shell);
	}
	
	public void reconnect(String teiidInstance){
		bot().tree().getTreeItem(teiidInstance).select();
		getToolbarButtonWitTooltip(TOOLBAR_RECONNECT_TEIID).click();
	}

	public void deleteDataSource(String teiidInstance, String dataSource){
		SWTBot bot = bot();

		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(bot, teiidInstance, "Data Sources", dataSource);

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Delete Data Source");
	}

	public void undeployVDB(String teiidInstance, String vdb){
		SWTBot bot = bot();

		SWTBotTreeItem node = SWTEclipseExt.selectTreeLocation(bot, teiidInstance, "VDBs", vdb);

		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Undeploy VDB");
	}

	public boolean containsDataSource(String teiidInstance, String datasource){
		SWTBot bot = bot();
		try {
			SWTBotTreeItem item = bot.tree().expandNode(teiidInstance, "Data Sources");
			item.getNode(datasource);
			return true;
		} catch (WidgetNotFoundException e){
			return false;
		}
	}
	
	public boolean containsVDB(String teiidInstance, String vdb){
		SWTBot bot = bot();
		try {
			SWTBotTreeItem item = bot.tree().expandNode(teiidInstance, "VDBs");
			item.getNode(vdb);
			return true;
		} catch (WidgetNotFoundException e){
			return false;
		}
	}
	
	public boolean containsTeiidInstance(String name){
		SWTBot bot = bot();
		try {
			bot.tree().getTreeItem(name);
			return true;
		} catch (WidgetNotFoundException e){
			return false;
		}
	}
}
