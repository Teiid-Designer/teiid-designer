package org.teiid.designer.ui.bot.test;

import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teiid.designer.ui.bot.ext.teiid.wizard.CreateMetadataModel;
import org.teiid.designer.ui.bot.test.suite.TeiidDesignerTestCase;


/**
 * 
 * Test for the creation of new models using Teiid Designer wizard. 
 * 
 * @author psrna
 *
 */
@Require(perspective="Teiid Designer")
public class ModelWizardTest extends TeiidDesignerTestCase{

	private static final String PROJECT_NAME = "ModelWizardTestProject";
	
	public static final String RELATIONAL_SOURCE_MODEL_NAME = "relational_source";

	public static final String RELATIONAL_VIEW_MODEL_NAME = "relational_view";
	
	public static final String XML_VIEW_MODEL_NAME = "xml_view";
	
	public static final String XSD_DATATYPE_MODEL_NAME = "xsd_datatype";
	
	public static final String WEBSERVICE_MODEL_NAME = "webservice_view";
	
	public static final String MODELEXT_MODEL_NAME = "modelext";
	
	public static final String FUNCTION_MODEL_NAME = "function_userdef";
	
	@BeforeClass
	public static void beforeClass(){
		createProject(PROJECT_NAME);
	}

	@Test
	public void relationalSourceModel(){
		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(RELATIONAL_SOURCE_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.RELATIONAL);
		createModel.setType(CreateMetadataModel.ModelType.SOURCE);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				RELATIONAL_SOURCE_MODEL_NAME + ".xmi"));

		assertTrue(modelEditor(RELATIONAL_SOURCE_MODEL_NAME + ".xmi").isActive());
	}

	@Test
	public void relationalViewModel(){
		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(RELATIONAL_VIEW_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.RELATIONAL);
		createModel.setType(CreateMetadataModel.ModelType.VIEW);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				RELATIONAL_VIEW_MODEL_NAME + ".xmi"));

		assertTrue(modelEditor(RELATIONAL_SOURCE_MODEL_NAME + ".xmi").isActive());
	}

	@Test
	public void xmlViewModel(){
		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(XML_VIEW_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.XML);
		createModel.setType(CreateMetadataModel.ModelType.VIEW);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				XML_VIEW_MODEL_NAME + ".xmi"));

		assertTrue(modelEditor(XML_VIEW_MODEL_NAME + ".xmi").isActive());
	}

	@Test
	public void xsdDatatypeModel(){
		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(XSD_DATATYPE_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.XSD);
		createModel.setType(CreateMetadataModel.ModelType.DATATYPE);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				XSD_DATATYPE_MODEL_NAME + ".xsd"));

		assertTrue(modelEditor(XSD_DATATYPE_MODEL_NAME + ".xsd").isActive());
	}

	@Test
	public void webserviceModel(){

		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(WEBSERVICE_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.WEBSERVICE);
		createModel.setType(CreateMetadataModel.ModelType.VIEW);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				WEBSERVICE_MODEL_NAME + ".xmi"));

		assertTrue(modelEditor(WEBSERVICE_MODEL_NAME + ".xmi").isActive());
	}

	@Test
	public void modelExtensionModel(){
		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(MODELEXT_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.MODEL_EXTENSION);
		createModel.setType(CreateMetadataModel.ModelType.EXTENSION);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				MODELEXT_MODEL_NAME + ".xmi"));

		assertTrue(modelEditor(MODELEXT_MODEL_NAME + ".xmi").isActive());
	}


	@Test
	public void functionModel(){
		CreateMetadataModel createModel = new CreateMetadataModel();
		createModel.setLocation(PROJECT_NAME);
		createModel.setName(FUNCTION_MODEL_NAME);
		createModel.setClass(CreateMetadataModel.ModelClass.FUNCTION);
		createModel.setType(CreateMetadataModel.ModelType.FUNCTION);
		createModel.execute();

		assertTrue(projectExplorer.existsResource(PROJECT_NAME, 
				FUNCTION_MODEL_NAME + ".xmi"));

		assertTrue(modelEditor(FUNCTION_MODEL_NAME + ".xmi").isActive());
	}
}
