package org.teiid.designer.ui.bot.ext.teiid.wizard;

import org.jboss.tools.ui.bot.ext.wizards.SWTBotNewObjectWizard;

public class NewTeiidModelProjectWizard {
	
	private SWTBotNewObjectWizard wizard = new SWTBotNewObjectWizard();

	private String projectName;

	public void execute(){
		wizard.open("Teiid Model Project", "Teiid Designer");
		wizard.bot().textWithLabel("Project name:").setText(projectName);
		wizard.finishWithWait();
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
