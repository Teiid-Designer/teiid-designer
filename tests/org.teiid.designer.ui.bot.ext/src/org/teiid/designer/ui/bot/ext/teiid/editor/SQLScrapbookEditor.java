package org.teiid.designer.ui.bot.ext.teiid.editor;

import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.condition.TaskDuration;



public class SQLScrapbookEditor extends Editor {

	public SQLScrapbookEditor() {
		this("SQL Scrapbook 0");
	}
	
	public SQLScrapbookEditor(String name) {
		super(name);
	}

	public void setDatabase(String dbName){
		getBot().comboBoxWithLabel("Database:").setSelection(dbName);
	}
	
	public void setText(String text){
		getBot().styledText().setText(text);
	}
	
	public void executeAll(){
		getBot().styledText().contextMenu("Execute All").click();
		
		SWTBotShell shell = getBot().shell("SQL Statement Execution");
		getBot().waitUntil(Conditions.shellCloses(shell), TaskDuration.LONG.getTimeout());
	}
}
