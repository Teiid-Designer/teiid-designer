package org.teiid.designer.ui.bot.ext.teiid.database;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class DatasourcePasswordDialog {

	private SWTBotShell shell;
	
	public DatasourcePasswordDialog(SWTBotShell shell) {
		this.shell = shell;
	}

	public void setPassword(String password){
		shell.bot().textWithLabel("Password:").setText("mm");
	}
	
	public void finish(){
		SWTBotFactory.getOpen().finish(shell.bot(), IDELabel.Button.OK);		
	}
}
