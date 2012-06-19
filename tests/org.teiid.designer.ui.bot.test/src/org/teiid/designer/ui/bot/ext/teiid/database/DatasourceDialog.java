package org.teiid.designer.ui.bot.ext.teiid.database;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;

public class DatasourceDialog {

	private SWTBotShell shell;
	
	public DatasourceDialog(SWTBotShell shell) {
		super();
		this.shell = shell;
	}

	public void setName(String name){
		shell.bot().textWithLabel("Data Source Name:").setText(name);
	}

	public DatasourcePasswordDialog getPasswordDialog(){
		SWTBotShell passwordShell = getBot().shell("Data Source Password");
		passwordShell.activate();
		return new DatasourcePasswordDialog(passwordShell);
		
	}

	public void finish(){
		SWTBotFactory.getOpen().finish(shell.bot());
	}
	
	private SWTBotExt getBot(){
		return SWTBotFactory.getBot();
	}
}
