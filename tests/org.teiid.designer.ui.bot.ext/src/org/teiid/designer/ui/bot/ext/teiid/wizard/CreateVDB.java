package org.teiid.designer.ui.bot.ext.teiid.wizard;

import org.jboss.tools.ui.bot.ext.wizards.SWTBotNewObjectWizard;

/**
 * Creates a new virtual database.  
 * 
 * @author Lucia Jelinkova
 *
 */
public class CreateVDB {

	private SWTBotNewObjectWizard wizard = new SWTBotNewObjectWizard();
	
	private String folder;
	
	private String name;
	
	public void execute(){
		wizard.open("Teiid VDB", "Teiid Designer");
		fillFirstPage();
		wizard.finishWithWait();
	}

	private void fillFirstPage() {
		wizard.bot().textWithLabel("In Folder:").setText(folder);
		wizard.bot().textWithLabel("VDB Name:").setText(name);		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
}
