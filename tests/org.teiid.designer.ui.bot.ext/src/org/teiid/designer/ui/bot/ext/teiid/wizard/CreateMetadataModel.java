package org.teiid.designer.ui.bot.ext.teiid.wizard;

import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.SWTBotFactory;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.wizards.SWTBotNewObjectWizard;

/**
 * Creates a new metadata model. 
 * 
 * @author Lucia Jelinkova
 *
 */
public class CreateMetadataModel {

	public static class ModelClass {

		public static final String RELATIONAL = "Relational";

		public static final String XML = "XML";

		public static final String XSD = "XML Schema (XSD)";

		public static final String WEBSERVICE = "Web Service";

		public static final String MODEL_EXTENSION = "Model Extension (Deprecated)";

		public static final String FUNCTION = "Function";

	}
	
	public static class ModelType {

		public static final String SOURCE = "Source Model";

		public static final String VIEW = "View Model";

		public static final String DATATYPE = "Datatype Model";

		public static final String EXTENSION = "Model Class Extension";

		public static final String FUNCTION = "User Defined Function";

	}

	private SWTBotNewObjectWizard wizard = new SWTBotNewObjectWizard();

	private String location;

	private String name;

	private String clazz;

	private String type;

	public void execute(){
		wizard.open("Teiid Metadata Model", "Teiid Designer");
		fillFirstPage();
		wizard.finishWithWait();

		xsdSchemaSelection();
	}

	private void fillFirstPage() {
		wizard.bot().textWithLabel("Location:").setText(location);
		wizard.bot().textWithLabel("Model Name:").setText(name);
		wizard.bot().comboBoxWithLabel("Model Class:").setSelection(clazz);
		wizard.bot().comboBoxWithLabel("Model Type:").setSelection(type);		
	}

	private void xsdSchemaSelection() {
		if (ModelClass.XSD.equals(clazz)){
			SWTBotShell shell = SWTBotFactory.getBot().shell("Model Initializer");
			shell.bot().table().select("XML Schema (2001)");
			shell.bot().button(IDELabel.Button.OK).click();
			SWTBotFactory.getBot().waitUntil(Conditions.shellCloses(shell));
		}
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

	public void setClass(String clazz){
		this.clazz = clazz;
	}
}
