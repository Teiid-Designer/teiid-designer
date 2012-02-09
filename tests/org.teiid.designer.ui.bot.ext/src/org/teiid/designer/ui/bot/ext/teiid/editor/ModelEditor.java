package org.teiid.designer.ui.bot.ext.teiid.editor;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.teiid.designer.ui.bot.ext.teiid.SWTBotTeiidCanvas;
import org.teiid.designer.ui.bot.ext.teiid.SWTTeiidBot;

public class ModelEditor extends Editor {

	private ModelEditor(String name){
		super(name);
	}
	
	public static ModelEditor getInstance(String name){
		return new ModelEditor(name);
	}
	
	public void showTransformation(){
		SWTTeiidBot teiidBot = new SWTTeiidBot();
		SWTBotTeiidCanvas canvas = teiidBot.getTeiidCanvas(0);
		canvas.tFigure().doubleClick();
	}
	
	public CriteriaBuilder criteriaBuilder(){
		SWTBot bot = getEditor().bot();
		bot.toolbarButtonWithTooltip("Criteria Builder").click();
		
		SWTBotShell shell = bot.shell("Criteria Builder");
		shell.activate();
		return new CriteriaBuilder(shell);
	}
	
	public String getTransformation(){
		return getBot().styledText(0).getText();
	}
}
