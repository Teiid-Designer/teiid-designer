package org.teiid.designer.ui.bot.ext.teiid.wizard;

import org.jboss.tools.ui.bot.ext.wizards.SWTBotNewObjectWizard;

/**
 * Creates a new virtual database.  
 * 
 * @author Lucia Jelinkova
 *
 */
public class CreateVDB extends SWTBotNewObjectWizard {

	private String folder;
	
	private String name;
	
	public void execute(){
		open("Teiid VDB", "Teiid Designer");
		fillFirstPage();
		finishWithWait();
	}

	private void fillFirstPage() {
		bot().textWithLabel("In Folder:").setText(folder);
		bot().textWithLabel("VDB Name:").setText(name);		
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setFolder(String folder) {
		this.folder = folder;
	}
}
