package org.teiid.designer.ui.bot.ext.teiid.editor;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class VDBEditor extends Editor{
	
	private VDBEditor(String name){
		super(name);
	}
	
	public static VDBEditor getInstance(String name){
		return new VDBEditor(name);
	}
	
	public void addModel(String projectName, String model){
		SWTBot bot = getEditor().bot();
		bot.toolbarButtonWithTooltip("Add model").click();
		
		SWTBotShell shell = bot.shell("Add File(s) to VDB");
		shell.activate();
		shell.bot().tree(0).expandNode(projectName).select(model);
		SWTBotFactory.getOpen().finish(shell.bot(), IDELabel.Button.OK);
	}
	
	public String getModel(int index){
		return getBot().table(0).cell(index, 0);
	}
}
