package org.teiid.designer.ui.bot.ext.teiid.view;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;
import org.jboss.tools.ui.bot.ext.condition.TreeContainsNode;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class Procedure {

	private String project;
	
	private String model;
	
	private String procedure;
	
	private SWTBot bot;
	
	public Procedure(String project, String model, String procedure, SWTBot bot) {
		this.project = project;
		this.model = model;
		this.procedure = procedure;
		this.bot = bot;
	}

	public void addParameter(String name, String type){
		addParameterName(name);
		addParameterType(name, type);
	}
	
	private void addParameterName(String parameter){
		SWTBotTreeItem procedure_node =  SWTEclipseExt.selectTreeLocation(bot, project, model, procedure);
		
		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), procedure_node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "New Child", "Procedure Parameter");

		bot.text("NewProcedureParameter").setText(parameter);        
		bot.tree().setFocus();
		
		bot.waitUntil(new TreeContainsNode(bot.tree(), project, model, procedure, parameter), TaskDuration.NORMAL.getTimeout());
	}
	
	private void addParameterType(String parameter, String type){
		SWTBotTreeItem node =  SWTEclipseExt.selectTreeLocation(bot, project, model, procedure, parameter);
		
		ContextMenuHelper.prepareTreeItemForContextMenu(bot.tree(), node);
		ContextMenuHelper.clickContextMenu(bot.tree(), "Modeling", "Set Datatype");

		SWTBotShell shell = bot.shell("Select a Datatype");
		shell.bot().table().getTableItem(type).select();
		SWTBotFactory.getOpen().finish(shell.bot(), IDELabel.Button.OK);
	}
}
