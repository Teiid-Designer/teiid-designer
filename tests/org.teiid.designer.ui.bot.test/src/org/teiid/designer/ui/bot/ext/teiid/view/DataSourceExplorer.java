package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;


public class DataSourceExplorer extends View {

	public DataSourceExplorer() {
		super("Data Source Explorer");
	}

	public void openSQLScrapbook(String datasource){
		openSQLScrapbook(datasource, false);
	}

	public void openSQLScrapbook(String datasource, boolean useRegularExpression){
		SWTBot bot = getBot();
		SWTBotTreeItem root =  SWTEclipseExt.selectTreeLocation(getBot(), "Database Connections");
		SWTBotTreeItem db_node;

		if (useRegularExpression){
			db_node = getNode(root, datasource);
		} else {
			db_node= root.getNode(datasource);
		}
		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), db_node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Open SQL Scrapbook");	
	}

	private SWTBotTreeItem getNode(SWTBotTreeItem root, String datasource) {
		for (SWTBotTreeItem item : root.getItems()){
			if (item.getText().matches(datasource)){
				return item;
			}
		}
		
		throw new WidgetNotFoundException("Tree item with regular expression " + datasource + " not found");
	}
}
