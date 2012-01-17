/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui;

import java.util.ResourceBundle;

import com.metamatrix.core.PluginUtil;
import com.metamatrix.core.util.PluginUtilImpl;

public interface AdvisorUiConstants {
	
	/**
     * The plug-in ID where this interface is located.
     * 
     * @since 4.4
     */
    String PLUGIN_ID = "org.teiid.designer.advisor.ui"; //$NON-NLS-1$

    String PACKAGE_ID = AdvisorUiConstants.class.getPackage().getName();
    
    /**
     * The resource bundle path/filename.
     * 
     * @since 4.2
     */
    String I18N_NAME = PACKAGE_ID + ".i18n"; //$NON-NLS-1$

    /**
     * Provides access to the plug-in's log, internationalized properties, and debugger.
     * 
     * @since 4.2
     */
    PluginUtil UTIL = new PluginUtilImpl(PLUGIN_ID, I18N_NAME, ResourceBundle.getBundle(I18N_NAME));
    
	String[] MODELING_ASPECT_LABELS_LIST = {
			
			MODELING_ASPECT_LABELS.MODEL_PROJECT_MANAGEMENT,
			MODELING_ASPECT_LABELS.MODEL_DATA_SOURCES,
			MODELING_ASPECT_LABELS.MANAGE_CONNECTIONS,
			//MODELING_ASPECT_LABELS.MODEL_VIEWS,
			MODELING_ASPECT_LABELS.MANAGE_VDBS,
			MODELING_ASPECT_LABELS.CONSUME_SOAP_WS,
			MODELING_ASPECT_LABELS.CONSUME_REST_WS,
			MODELING_ASPECT_LABELS.CREATE_SOAP_WS,
			MODELING_ASPECT_LABELS.CREATE_REST_WS
	};
    
    interface MODEL_CLASSES {
		String RELATIONAL = "Relational";  //$NON-NLS-1$
		String XML = "XML";  //$NON-NLS-1$
		String XML_SCHEMA = "XML Schema (XSD)";  //$NON-NLS-1$
		String WEB_SERVICE = "Web Service";  //$NON-NLS-1$
		String FUNCTION = "Function";  //$NON-NLS-1$
		String MODEL_EXTENSION = "ModelAdvisorUiConstants Extension (Deprecated)";  //$NON-NLS-1$
	}

	interface COMMAND_IDS {
		// These are not currently used by extension points (i.e AbstractHandler instances);
		// When 
		String IMPORT_JDBC = "org.teiid.designer.importJdbcCommand";  //$NON-NLS-1$
		String IMPORT_DDL = "org.teiid.designer.importDdlCommand";  //$NON-NLS-1$
		String IMPORT_SALESFORCE = "org.teiid.designer.importSalesforceCommand";  //$NON-NLS-1$
		String IMPORT_FLAT_FILE = "org.teiid.designer.importFlatFileCommand";  //$NON-NLS-1$
		String IMPORT_XML_FILE = "org.teiid.designer.importXmlFileCommand";  //$NON-NLS-1$
		String IMPORT_WSDL_TO_SOURCE = "org.teiid.designer.importWsdlToSourceCommand";  //$NON-NLS-1$
		String IMPORT_WSDL_TO_WS = "org.teiid.designer.importWsdlToWSCommand";  //$NON-NLS-1$
		
		String NEW_MODEL_RELATIONAL_SOURCE = "org.teiid.designer.newModelRelationalSourceCommand";  //$NON-NLS-1$
		String NEW_MODEL_RELATIONAL_VIEW = "org.teiid.designer.newModelRelationalViewCommand";  //$NON-NLS-1$
		String NEW_MODEL_WS = "org.teiid.designer.newModelWSCommand";  //$NON-NLS-1$
		String NEW_MODEL_XML_DOC = "org.teiid.designer.newModelXmlDocCommand";  //$NON-NLS-1$
		String NEW_MODEL_MED = "org.teiid.designer.newModelMEDCommand";  //$NON-NLS-1$
		
		String NEW_OBJECT_BASE_TABLE = "org.teiid.designer.newObjectBaseTable";  //$NON-NLS-1$
		String NEW_OBJECT_SOURCE_FUNCTION = "org.teiid.designer.newObjectSourceFunction";  //$NON-NLS-1$
		
		String CREATE_CONNECTION_JDBC = "org.teiid.designer.connection.new.jdbc"; //$NON-NLS-1$
		String CREATE_CONNECTION_FLAT_FILE = "org.teiid.designer.connection.new.flatfile"; //$NON-NLS-1$
		String CREATE_CONNECTION_XML_FILE_URL = "org.teiid.designer.connection.new.xmlfileurl"; //$NON-NLS-1$
		String CREATE_CONNECTION_XML_FILE_LOCAL = "org.teiid.designer.connection.new.xmlfilelocal"; //$NON-NLS-1$
		String CREATE_CONNECTION_SALESFORCE = "org.teiid.designer.connection.new.salesforce"; //$NON-NLS-1$
		String CREATE_CONNECTION_LDAP = "org.teiid.designer.connection.new.ldap"; //$NON-NLS-1$
		String CREATE_CONNECTION_MODESHAPE = "org.teiid.designer.connection.new.modeshape"; //$NON-NLS-1$
		String CREATE_CONNECTION_WEB_SERVICE = "org.teiid.designer.connection.new.ws"; //$NON-NLS-1$
		
		String OPEN_DATA_SOURCE_EXPLORER_PERSPECTIVE = "org.eclipse.datatools.openperspective"; //$NON-NLS-1$
		
		String NEW_TEIID_MODEL_PROJECT = "org.teiid.designer.newProjectCommand"; //$NON-NLS-1$
		
		String CREATE_VDB = "org.teiid.designer.new.vdb"; //$NON-NLS-1$
		String EXECUTE_VDB = "org.teiid.designer.execute.vdb"; //$NON-NLS-1$
	}
	
	interface COMMAND_LABELS {
		String IMPORT_JDBC = Messages.CreateSourceModelFromJdbcSource;
		String IMPORT_DDL = Messages.CreateSourceModelFromDdlFile;
		String IMPORT_SALESFORCE = Messages.CreateSourceModelFromSalesforceDataSource;
		String IMPORT_FLAT_FILE = Messages.ConsumeLocalFlatFileDataSource;
		String IMPORT_XML_FILE = Messages.ConsumeXmlFileSource;
		String IMPORT_WSDL_TO_SOURCE = Messages.CreateSourceModelFromWsdlSource;
		String IMPORT_WSDL_TO_WS = "Consume Web Service WSDL";  //$NON-NLS-1$
		
		String NEW_MODEL_RELATIONAL_SOURCE = Messages.CreateNewRelationalSourceModel;
		String NEW_MODEL_RELATIONAL_VIEW = Messages.CreateNewRelationalViewModel;
		String NEW_MODEL_WS = Messages.CreateNewWebServiceViewModel;
		String NEW_MODEL_XML_DOC = Messages.CreateNewXmlDocumentViewModel;
		String NEW_MODEL_MED = Messages.CreateNewModelExtensionDefinition;
		
		String NEW_OBJECT_BASE_TABLE = Messages.CreateNewRelationalBaseTable;
		String NEW_OBJECT_SOURCE_FUNCTION = Messages.CreateNewRelationalSourceFunction;
		
		String CREATE_CONNECTION_JDBC = Messages.CreateJdbcConnection;
		String CREATE_CONNECTION_FLAT_FILE = Messages.CreateTeiidFlatFileConnection;
		String CREATE_CONNECTION_XML_FILE_URL = Messages.CreateTeiidRemoteXmlConnection;
		String CREATE_CONNECTION_XML_FILE_LOCAL = Messages.CreateTeiidLocalXmlConnection;
		String CREATE_CONNECTION_SALESFORCE = Messages.CreateSalesforceConnection;
		String CREATE_CONNECTION_LDAP = Messages.CreateLDAPConnection;
		String CREATE_CONNECTION_MODESHAPE = Messages.CreateModeshapeConnection;
		String CREATE_CONNECTION_WEB_SERVICE = Messages.CreateWebServicesConnection;
		
		String OPEN_DATA_SOURCE_EXPLORER_PERSPECTIVE = Messages.OpenDatatoolsDataSourceExplorer;
		
		String NEW_TEIID_MODEL_PROJECT = UTIL.getString("createTeiidModelProject");  //$NON-NLS-1$
		String CREATE_VDB = Messages.CreateVdb;
		String EXECUTE_VDB = Messages.ExecuteVdb; 
	}

	interface MODEL_IDS {
		String RELATIONAL_ID = "Relational"; //$NON-NLS-1$
		String WEB_SERVICES_ID = "Web Services"; //$NON-NLS-1$
		String XML_ID = "XML"; //$NON-NLS-1$
	}

	interface MODELING_ASPECT_IDS {
		String MODEL_PROJECT_MANAGEMENT = "modelProjectManagement"; //$NON-NLS-1$
		String MODEL_DATA_SOURCES = "modelDataSources"; //$NON-NLS-1$
		String MANAGE_CONNECTIONS = "manageConnections"; //$NON-NLS-1$
		//String MODEL_VIEWS = "modelViews"; //$NON-NLS-1$
		String MANAGE_VDBS = "manageVdbs"; //$NON-NLS-1$
		String CONSUME_SOAP_WS = "consumeSoapWS"; //$NON-NLS-1$
		String CONSUME_REST_WS = "consumeRestWS"; //$NON-NLS-1$
		String CREATE_SOAP_WS = "createSoapWS"; //$NON-NLS-1$
		String CREATE_REST_WS = "createRestWS"; //$NON-NLS-1$
	}
	
	interface MODELING_ASPECT_LABELS {
		String MODEL_PROJECT_MANAGEMENT = Messages.ManageModelProjects;
		String MODEL_DATA_SOURCES= Messages.ModelDataSources;
		String MANAGE_CONNECTIONS= Messages.ManageConnections;
		String MODEL_VIEWS= Messages.ModelViews;
		String MANAGE_VDBS= Messages.ManageVdbs;
		String CONSUME_SOAP_WS= Messages.ConsumeASOAPWebService;
		String CONSUME_REST_WS= Messages.ConsumeARESTWebService;
		String CREATE_SOAP_WS= Messages.CreateASOAPWebService;
		String CREATE_REST_WS= Messages.CreateARESTWebService;
	}

	interface Images {
	    String IMG_PATH = "icons/full/"; //$NON-NLS-1$
	
	    String CTOOL16 = IMG_PATH + "ctool16/"; //$NON-NLS-1$
	    String CVIEW16 = IMG_PATH + "cview16/"; //$NON-NLS-1$
	    String DTOOL16 = IMG_PATH + "dtool16/"; //$NON-NLS-1$
	    String OBJ16 = IMG_PATH + "obj16/"; //$NON-NLS-1$
	    String OVR16 = IMG_PATH + "ovr16/"; //$NON-NLS-1$
	    String WIZBAN = IMG_PATH + "wizban/"; //$NON-NLS-1$
	
	    String IMPORT_WSDL = CTOOL16 + "import-wsdl.gif"; //$NON-NLS-1$
	    String IMPORT_JDBC = CTOOL16 + "import-jdbc.gif"; //$NON-NLS-1$
	    String CREATE_WEB_SERVICE = CTOOL16 + "create-web-service.png"; //$NON-NLS-1$
	
	    String NEW_WS_MODEL = OBJ16 + "new-web-service.png"; //$NON-NLS-1$
	    String EXPORT_WAR = CTOOL16 + "export-war.gif"; //$NON-NLS-1$
	    String NEW_VDB = CTOOL16 + "new-vdb-wiz.gif"; //$NON-NLS-1$
	    String NEW_MODEL_ACTION = CTOOL16 + "new-model-wiz.gif"; //$NON-NLS-1$
	    String NEW_PROJECT_ACTION = CTOOL16 + "new-project-wiz.gif";  //$NON-NLS-1$
	    String EXECUTE_VDB_ACTION = CTOOL16 + "execute-vdb.gif"; //$NON-NLS-1$
	}
	
	interface CONNECTION_PROFILE_IDS {
		String CATEGORY_JDBC = "org.eclipse.datatools.connectivity.db.category"; //$NON-NLS-1$
		String CATEGORY_ODA_FLAT_FILE_ID = "org.eclipse.datatools.connectivity.oda.flatfile"; //$NON-NLS-1$
		String CATEGORY_MODESHAPE = "org.teiid.designer.datatools.profiles.modeshape.ModeShapeConnectionProfile"; //$NON-NLS-1$
		String CATEGORY_XML_FILE_LOCAL = "org.teiid.designer.datatools.profiles.xml.localfile"; //$NON-NLS-1$
		String CATEGORY_XML_FILE_URL = "org.teiid.designer.datatools.profiles.xml.fileurl"; //$NON-NLS-1$
		String CATEGORY_WS_CONNECTION = "org.teiid.designer.datatools.profiles.ws.WSConnectionProfile"; //$NON-NLS-1$
		String CATEGORY_LDAP_CONNECTION = "org.teiid.designer.datatools.profiles.ldap.LDAPConnectionProfile"; //$NON-NLS-1$
		String CATEGORY_SALESFORCE_CONNECTION = "org.teiid.designer.datatools.salesforce.connectionProfile"; //$NON-NLS-1$
	}


}
