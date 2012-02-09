package org.teiid.designer.ui.bot.ext.teiid.wizard;

import org.jboss.tools.ui.bot.ext.wizards.SWTBotNewObjectWizard;

/**
 * Creates a new metadata model. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class CreateMetadataModel {

	private SWTBotNewObjectWizard wizard = new SWTBotNewObjectWizard();
	
	private String location;
	
	private String name;
	
	private String type;
	
	public void execute(){
		wizard.open("Teiid Metadata Model", "Teiid Designer");
		fillFirstPage();
		wizard.finishWithWait();
	}

	private void fillFirstPage() {
		wizard.bot().textWithLabel("Location:").setText(location);
		wizard.bot().textWithLabel("Model Name:").setText(name);
		wizard.bot().comboBoxWithLabel("Model Type:").setSelection(type);		
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}
}
