package org.teiid.designer.ui.bot.ext.teiid.wizard;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.wizards.SWTBotImportWizard;

/**
 * Imports JDBC Database to Teiid project.  
 * 
 * @author Lucia Jelinkova
 *
 */
public class ImportJDBCDatabaseWizard {

	private SWTBotImportWizard wizard = new SWTBotImportWizard();
	
	private String connectionProfile;
	
	private String projectName;
	
	private String modelName;
	
	public void execute(){
		wizard.open("JDBC Database >> Source Model", "Teiid Designer");
		fillFirstPage();
		wizard.nextWithWait();
		fillSecondPage();
		wizard.nextWithWait();
		fillThirdPage();
		wizard.nextWithWait();
		fillFourthPage();
		wizard.finishWithWait();
	}

	private void fillFirstPage() {
		wizard.bot().comboBoxInGroup("Connection Profile").setSelection(connectionProfile);
	}
	
	private void fillSecondPage() {
		wizard.bot().button("Deselect All").click();
		wizard.bot().tableInGroup("Table Types").click(1, 0);
	}
	
	private void fillThirdPage() {
		
	}
	
	private void fillFourthPage() {
		wizard.bot().checkBoxInGroup ("Model Object Names (Tables, Procedures, Columns, etc...)", 0).deselect();
		wizard.bot().textWithLabel("Model Name:").setText(modelName);
		wizard.bot().checkBox("Update (if existing model selected)").deselect();
		wizard.bot().button(1).click();
		
		SWTBotShell select_shell = wizard.bot().shell("Select a Folder");
		select_shell.bot().tree(0).select(projectName);
		select_shell.bot().button(IDELabel.Button.OK).click();		
	}
	
	public void setConnectionProfile(String connectionProfile) {
		this.connectionProfile = connectionProfile;
	}
	
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
}
