package com.metamatrix.modeler.ui.bot.testcase;

import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.junit.BeforeClass;
import org.junit.Test;


import com.metamatrix.modeler.ui.bot.testsuite.Properties;
import com.metamatrix.modeler.ui.bot.testsuite.TeiidDesignerTest;

/**
 * 
 * @author psrna
 *
 */
@SWTBotTestRequires(perspective="Teiid Designer")
public class ModelWizardTest extends TeiidDesignerTest{
	
	public SWTBotShell getModelWizardShell(){
		
		bot.menu(IDELabel.Menu.FILE)
		   .menu(IDELabel.Menu.NEW)
		   .menu("Teiid Metadata Model").click();
		
		SWTBotShell shell = bot.shell("New Model Wizard");
		shell.activate();
		
		return shell;
	}
	
	
	@BeforeClass
	public static void beforeClass(){
		createProject();
	}
	
	@Test
	public void relationalSourceModel(){
		
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.RELATIONAL_SOURCE_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("Relational");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("Source Model");
		
		open.finish(shell.bot());
		log.info("-> Relational Source Model Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                                  Properties.RELATIONAL_SOURCE_MODEL_NAME + ".xmi"));
		
		assertTrue(bot.editorByTitle(Properties.RELATIONAL_SOURCE_MODEL_NAME + ".xmi").isActive());	
	}
	
	@Test
	public void relationalViewModel(){
	
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.RELATIONAL_VIEW_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("Relational");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("View Model");
		
		open.finish(shell.bot());
		log.info("-> Relational View Model Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                               Properties.RELATIONAL_VIEW_MODEL_NAME + ".xmi"));
		
		assertTrue(bot.editorByTitle(Properties.RELATIONAL_VIEW_MODEL_NAME + ".xmi").isActive());	
		
	}
	
	@Test
	public void xmlViewModel(){
		
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.XML_VIEW_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("XML");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("View Model");
		
		open.finish(shell.bot());
		log.info("-> XML View Model Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                                  Properties.XML_VIEW_MODEL_NAME + ".xmi"));
		
		assertTrue(bot.editorByTitle(Properties.XML_VIEW_MODEL_NAME + ".xmi").isActive());	
		
	}
	
	@Test
	public void xsdDatatypeModel(){
		
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.XSD_DATATYPE_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("XML Schema (XSD)");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("Datatype Model");
		
		shell.bot().button(IDELabel.Button.FINISH).click();
		
		bot.shell("Model Initializer").bot().table().select("XML Schema (2001)");
		bot.shell("Model Initializer").bot().button(IDELabel.Button.OK).click();
		
		bot.waitUntil(Conditions.shellCloses(shell));
		
		log.info("-> XSD Datatype Model Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                                  Properties.XSD_DATATYPE_MODEL_NAME + ".xsd"));
		
		assertTrue(bot.editorByTitle(Properties.XSD_DATATYPE_MODEL_NAME + ".xsd").isActive());	
		
	}
	
	@Test
	public void webserviceModel(){
		
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.WEBSERVICE_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("Web Service");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("View Model");
		
		open.finish(shell.bot());
		log.info("-> Web Service View Model Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                                  Properties.WEBSERVICE_MODEL_NAME + ".xmi"));
		
		assertTrue(bot.editorByTitle(Properties.WEBSERVICE_MODEL_NAME + ".xmi").isActive());	
	}
	
	@Test
	public void modelExtensionModel(){
		
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.MODELEXT_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("Model Extension");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("Model Class Extension");
		
		open.finish(shell.bot());
		log.info("-> Model Extensions Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                                  Properties.MODELEXT_MODEL_NAME + ".xmi"));
		
		assertTrue(bot.editorByTitle(Properties.MODELEXT_MODEL_NAME + ".xmi").isActive());	
	}
	
	
	@Test
	public void functionModel(){
		
		SWTBotShell shell = getModelWizardShell();
		
		shell.bot().button("Browse...").click();
		bot.shell("Select a Folder").bot().tree().select(Properties.PROJECT_NAME);
		bot.shell("Select a Folder").bot().button(IDELabel.Button.OK).click();
		
		shell.bot().textWithLabel("Model Name:").setText(Properties.FUNCTION_MODEL_NAME);
		shell.bot().comboBoxWithLabel("Model Class:").setSelection("Function");
		shell.bot().comboBoxWithLabel("Model Type:").setSelection("User Defined Function");
		
		open.finish(shell.bot());
		log.info("-> Function Model Wizard finished. <-");
		log.info("-> Asserts <-");
		
		assertTrue(projectExplorer.existsResource(Properties.PROJECT_NAME, 
                                                  Properties.FUNCTION_MODEL_NAME + ".xmi"));
		
		assertTrue(bot.editorByTitle(Properties.FUNCTION_MODEL_NAME + ".xmi").isActive());	
		
	}
	
	
	
	
	
	
}
