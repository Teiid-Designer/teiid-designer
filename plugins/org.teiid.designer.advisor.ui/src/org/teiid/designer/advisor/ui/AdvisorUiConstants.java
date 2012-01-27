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
			//MODELING_ASPECT_LABELS.CREATE_SOAP_WS,
			//MODELING_ASPECT_LABELS.CREATE_REST_WS,
			MODELING_ASPECT_LABELS.DEFINE_MODELS,
			MODELING_ASPECT_LABELS.TEIID_SERVER,
			MODELING_ASPECT_LABELS.TEST
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
		String CREATE_CONNECTION_WEB_SERVICE_ODA = "org.teiid.designer.connection.new.odaws"; //$NON-NLS-1$
		
		String CREATE_WEB_SRVICES_DATA_FILE= "org.teiid.designer.connection.new.wsdatafile"; //$NON-NLS-1$
		
		String GENERATE_WS_MODELS_FROM_WSDL = "org.teiid.designer.generatewsmodelsfromwsdl";  //$NON-NLS-1$
		String OPEN_DATA_SOURCE_EXPLORER_VIEW = "org.eclipse.datatools.openexplorerview"; //$NON-NLS-1$
		
		String NEW_TEIID_MODEL_PROJECT = "org.teiid.designer.newProjectCommand"; //$NON-NLS-1$
		
		String CREATE_VDB = "org.teiid.designer.new.vdb"; //$NON-NLS-1$
		String EXECUTE_VDB = "org.teiid.designer.execute.vdb"; //$NON-NLS-1$
		String PREVIEW_DATA = "org.teiid.designer.previewData"; //$NON-NLS-1$
		
		String NEW_TEIID_SERVER = "org.teiid.designer.runtime.newTeiidServer"; //$NON-NLS-1$
		String EDIT_TEIID_SERVER = "org.teiid.designer.runtime.editTeiidServer"; //$NON-NLS-1$
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
		String CREATE_CONNECTION_WEB_SERVICE_ODA = Messages.CreateOdaWebServicesConnection;
		String CREATE_WEB_SRVICES_DATA_FILE= Messages.CreateWebServicesDataFile;
		
		String GENERATE_WS_MODELS_FROM_WSDL= Messages.GenerateWSModelsFromWsdl;
		
		String OPEN_DATA_SOURCE_EXPLORER_VIEW = Messages.OpenDatatoolsDataSourceExplorer;
		
		String NEW_TEIID_MODEL_PROJECT = Messages.CreateTeiidModelProject;
		String CREATE_VDB = Messages.CreateVdb;
		String EXECUTE_VDB = Messages.ExecuteVdb;
		String PREVIEW_DATA = Messages.PreviewData;
		
		String NEW_TEIID_SERVER = Messages.NewTeiidServer;
		String EDIT_TEIID_SERVER = Messages.EditTeiidServer;
	}
	
	interface COMMAND_LABELS_SHORT {
		String IMPORT_JDBC = Messages.CreateSourceModelFromJdbcSource_Short;
		String IMPORT_DDL = Messages.CreateSourceModelFromDdlFile_Short;
		String IMPORT_SALESFORCE = Messages.CreateSourceModelFromSalesforceDataSource_Short;
		String IMPORT_FLAT_FILE = Messages.ConsumeLocalFlatFileDataSource_Short;
		String IMPORT_XML_FILE = Messages.ConsumeXmlFileSource_Short;
		String IMPORT_WSDL_TO_SOURCE = Messages.CreateSourceModelFromWsdlSource_Short;
		String IMPORT_WSDL_TO_WS = Messages.ConsumeWebSercieWsdl_Short;
		
		String NEW_MODEL_RELATIONAL_SOURCE = Messages.CreateNewRelationalSourceModel_Short;
		String NEW_MODEL_RELATIONAL_VIEW = Messages.CreateNewRelationalViewModel_Short;
		String NEW_MODEL_WS = Messages.CreateNewWebServiceViewModel_Short;
		String NEW_MODEL_XML_DOC = Messages.CreateNewXmlDocumentViewModel_Short;
		String NEW_MODEL_MED = Messages.CreateNewModelExtensionDefinition_Short;
		
		String NEW_OBJECT_BASE_TABLE = Messages.CreateNewRelationalBaseTable_Short;
		String NEW_OBJECT_SOURCE_FUNCTION = Messages.CreateNewRelationalSourceFunction_Short;
		
		String CREATE_CONNECTION_JDBC = Messages.CreateJdbcConnection_Short;
		String CREATE_CONNECTION_FLAT_FILE = Messages.CreateTeiidFlatFileConnection_Short;
		String CREATE_CONNECTION_XML_FILE_URL = Messages.CreateTeiidRemoteXmlConnection_Short;
		String CREATE_CONNECTION_XML_FILE_LOCAL = Messages.CreateTeiidLocalXmlConnection_Short;
		String CREATE_CONNECTION_SALESFORCE = Messages.CreateSalesforceConnection_Short;
		String CREATE_CONNECTION_LDAP = Messages.CreateLDAPConnection_Short;
		String CREATE_CONNECTION_MODESHAPE = Messages.CreateModeshapeConnection_Short;
		String CREATE_CONNECTION_WEB_SERVICE = Messages.CreateWebServicesConnection_Short;
		String CREATE_CONNECTION_WEB_SERVICE_ODA= Messages.CreateOdaWebServicesConnection_Short;
		String CREATE_WEB_SRVICES_DATA_FILE= Messages.CreateWebServicesDataFile_Short;
		
		String GENERATE_WS_MODELS_FROM_WSDL= Messages.GenerateWSModelsFromWsdl_Short;
		
		String OPEN_DATA_SOURCE_EXPLORER_PERSPECTIVE = Messages.OpenDatatoolsDataSourceExplorer;
		
		String NEW_TEIID_MODEL_PROJECT = Messages.CreateTeiidModelProject;
		String CREATE_VDB = Messages.CreateVdb_Short;
		String EXECUTE_VDB = Messages.ExecuteVdb;
		
		String NEW_TEIID_SERVER = Messages.NewTeiidServer_Short;
		String EDIT_TEIID_SERVER = Messages.EditTeiidServer_Short;
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
		String DEFINE_MODELS = "defineModels"; //$NON-NLS-1$
		String TEST = "test"; //$NON-NLS-1$
		String TEIID_SERVER = Messages.TeiidServer;
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
		String DEFINE_MODELS= Messages.DefineModels;
		String TEST = Messages.Test;
		String TEIID_SERVER = Messages.TeiidServer;
	}
	
	interface VIEW_IDS {
		String DATA_SOURCE_EXPLORER = "org.eclipse.datatools.connectivity.DataSourceExplorerNavigator"; //$NON-NLS-1$
		String PROBLEMS_VIEW = "org.eclipse.ui.views.ProblemView"; //$NON-NLS-1$
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
	    String NEW_WEB_SERVICES_MODEL = CTOOL16 + "new-web-services-model.png"; //$NON-NLS-1$
	    String CREATE_WEB_SRVICES_DATA_FILE = CTOOL16 + "new-web-services-definition-file.png"; //$NON-NLS-1$
	
	    String NEW_WS_MODEL = OBJ16 + "new-web-service.png"; //$NON-NLS-1$
	    String EXPORT_WAR = CTOOL16 + "export-war.png"; //$NON-NLS-1$
	    String NEW_VDB = CTOOL16 + "new-vdb-wiz.gif"; //$NON-NLS-1$
	    String NEW_MODEL_ACTION = CTOOL16 + "new-model-wiz.gif"; //$NON-NLS-1$
	    String NEW_PROJECT_ACTION = CTOOL16 + "new-project-wiz.gif";  //$NON-NLS-1$
	    String EXECUTE_VDB_ACTION = CTOOL16 + "execute-vdb.gif"; //$NON-NLS-1$
	    String PREVIEW_DATA = CTOOL16 + "preview-data.gif"; //$NON-NLS-1$
	    
	    String DATA_SOURCE_EXPLORER_VIEW = CTOOL16 + "data-source-explorer-view.gif";  //$NON-NLS-1$
	    String NEW_CONNECTION_PROFILE = CTOOL16 + "new-connection-profile.gif";  //$NON-NLS-1$
	    String IMPORT = CTOOL16 + "import.gif";  //$NON-NLS-1$
	    String EXPORT = CTOOL16 + "export.gif";  //$NON-NLS-1$
	    
	    String LINK_TO_HELP = CTOOL16 + "link-to-help.gif"; //$NON-NLS-1$
	    String LIGHT_BULB = CTOOL16 + "light-bulb.gif"; //$NON-NLS-1$
	    
	    
        public static String VDB_PROJECT = CVIEW16 + "vdbproject.png"; //$NON-NLS-1$
        public static String MODEL_PROJECT = CVIEW16 + "modelproject.png"; //$NON-NLS-1$
        public static String EXIT = CTOOL16 + "exit.gif"; //$NON-NLS-1$
        public static String DELETE = CTOOL16 + "delete.gif"; //$NON-NLS-1$
        public static String NEW_VDB_WIZARD = WIZBAN + "newvdbwizard.gif"; //$NON-NLS-1$
        public static String OPEN = CTOOL16 + "openVdb.gif"; //$NON-NLS-1$
        public static String OPEN_OR_CREATE_VDB = CTOOL16 + "openOrCreateVdb.gif"; //$NON-NLS-1$
        public static String REBUILD_VDB = CTOOL16 + "rebuild_vdb.gif"; //$NON-NLS-1$
        public static String CONFIGURATION_MANAGER_VIEW = CVIEW16 + "ConfigurationManagerView.gif"; //$NON-NLS-1$
        public static String CONNECTOR_BINDINGS = CVIEW16 + "ConnectorBindings.gif"; //$NON-NLS-1$
        public static String CONNECTOR_BINDINGS_WITH_ERROR = CVIEW16 + "ConnectorBindingsWithError.gif"; //$NON-NLS-1$
        public static String BUILD_ALL = CVIEW16 + "build_exec.gif"; //$NON-NLS-1$
        public static String MODEL_STATISTICS = CVIEW16 + "statistics.gif"; //$NON-NLS-1$
        public static String VIEW_MODEL_TYPES = CVIEW16 + "viewModelTypes.gif"; //$NON-NLS-1$
        public static String VIEW_WEB_SERVICES = CVIEW16 + "viewWebServices.gif"; //$NON-NLS-1$
        public static String BUILD_IMPORTS = OBJ16 + "Imports.gif"; //$NON-NLS-1$
        public static String IMPORT_EXAMPLE_VDB = WIZBAN + "importExampleVdb.gif"; //$NON-NLS-1$
        public static String NEW_WEB_SERVICE = CTOOL16 + "NewWebService.png"; //$NON-NLS-1$

        // --------------------------------------------------------------------------------
        // For WebServiceAdvisorStatusPanel
        public static String CHECKED_BOX = CTOOL16 + "checked_box.png"; //$NON-NLS-1$
        public static String WARNING_CHECKED_BOX = CTOOL16 + "warning_checked_box.png"; //$NON-NLS-1$
        public static String EMPTY_BOX = CTOOL16 + "empty_box.png"; //$NON-NLS-1$
        public static String WARNING_EMPTY_BOX = CTOOL16 + "warning_empty_box.png"; //$NON-NLS-1$
        public static String PROBLEM_BOX = CTOOL16 + "problem_box.png"; //$NON-NLS-1$
        public static String WARNING_PROBLEM_BOX = CTOOL16 + "warning_problem_box.gif"; //$NON-NLS-1$
        public static String EMPTY_GRAY_BOX = CTOOL16 + "empty_gray_box.gif"; //$NON-NLS-1$
        // button versions....
        public static String CHECKED_BOX_BUTTON = CTOOL16 + "checked_box_button.gif"; //$NON-NLS-1$
        public static String WARNING_CHECKED_BOX_BUTTON = CTOOL16 + "warning_checked_box_button.gif"; //$NON-NLS-1$
        public static String EMPTY_BOX_BUTTON = CTOOL16 + "empty_box_button.gif"; //$NON-NLS-1$
        public static String WARNING_EMPTY_BOX_BUTTON = CTOOL16 + "warning_empty_box_button.gif"; //$NON-NLS-1$
        public static String PROBLEM_BOX_BUTTON = CTOOL16 + "problem_box_button.gif"; //$NON-NLS-1$
        public static String WARNING_PROBLEM_BOX_BUTTON = CTOOL16 + "warning_problem_box_button.gif"; //$NON-NLS-1$
        public static String EMPTY_GRAY_BOX_BUTTON = CTOOL16 + "empty_gray_box_button.gif"; //$NON-NLS-1$

        public static String VDB_OK = CTOOL16 + "vdb_ok_status.gif"; //$NON-NLS-1$
        public static String VDB_ERROR = CTOOL16 + "vdb_error_status.gif"; //$NON-NLS-1$
        public static String VDB_SAVE_REQUIRED = CTOOL16 + "vdb_save_required.gif"; //$NON-NLS-1$

        public static String IMPORT_XSD = CTOOL16 + "import_xsd.gif"; //$NON-NLS-1$
        public static String IMPORT_VDB_ICON = WIZBAN + "import_vdb.gif"; //$NON-NLS-1$
        public static String EDIT_BINDINGS = CVIEW16 + "ConnectorBindings.gif"; //$NON-NLS-1$
        public static String NEW_MODEL_WIZARD = CVIEW16 + "newmodel_wiz.gif"; //$NON-NLS-1$
        public static String FIX_IT = CTOOL16 + "fix_it.gif"; //$NON-NLS-1$
        public static String LIGHTBULB_ICON = CTOOL16 + "lightbulb.gif"; //$NON-NLS-1$

        public static String PREVIEW_WSDL = CTOOL16 + "preview_wsdl.gif"; //$NON-NLS-1$
        public static String PREVIEW_WSDL_ERROR = CTOOL16 + "preview_wsdl_error.gif"; //$NON-NLS-1$
        public static String PREVIEW_WSDL_GRAY = DTOOL16 + "preview_wsdl_disabled.gif"; //$NON-NLS-1$

        public static String PROBLEM_ERROR = CTOOL16 + "ProblemMarker_error.gif"; //$NON-NLS-1$
        public static String PROBLEM_WARNING = CTOOL16 + "ProblemMarker_warning.gif"; //$NON-NLS-1$
        public static String PROBLEM_INFO = CTOOL16 + "ProblemMarker_info.gif"; //$NON-NLS-1$

        public static String HELP_ICON = CTOOL16 + "linkto_help.gif"; //$NON-NLS-1$
        public static String PROBLEMS_VIEW = CTOOL16 + "problems_view.gif"; //$NON-NLS-1$
        public static String NEW_MODEL = CTOOL16 + "newmodel_wiz.gif"; //$NON-NLS-1$
        // --------------------------------------------------------------------------------
	}
	
	interface CONNECTION_PROFILE_IDS {
		String CATEGORY_JDBC = "org.eclipse.datatools.connectivity.db.category"; //$NON-NLS-1$
		String CATEGORY_ODA_FLAT_FILE_ID = "org.eclipse.datatools.connectivity.oda.flatfile"; //$NON-NLS-1$
		String CATEGORY_ODA_WS_ID = "org.eclipse.datatools.enablement.oda.ws"; //$NON-NLS-1$
		String CATEGORY_MODESHAPE = "org.teiid.designer.datatools.profiles.modeshape.ModeShapeConnectionProfile"; //$NON-NLS-1$
		String CATEGORY_XML_FILE_LOCAL = "org.teiid.designer.datatools.profiles.xml.localfile"; //$NON-NLS-1$
		String CATEGORY_XML_FILE_URL = "org.teiid.designer.datatools.profiles.xml.fileurl"; //$NON-NLS-1$
		String CATEGORY_WS_CONNECTION = "org.teiid.designer.datatools.profiles.ws.WSConnectionProfile"; //$NON-NLS-1$
		String CATEGORY_LDAP_CONNECTION = "org.teiid.designer.datatools.profiles.ldap.LDAPConnectionProfile"; //$NON-NLS-1$
		String CATEGORY_SALESFORCE_CONNECTION = "org.teiid.designer.datatools.salesforce.connectionProfile"; //$NON-NLS-1$
	}

	/**
	 * Constants related to extensions, including all extension ID's.
	 * 
	 * @since 4.0
	 */
	interface Extensions {
	    // Perspectives
	
	    // content supplier's IDs must match their class name
	
	    String ADVISOR_VIEW_ID = PLUGIN_ID + ".views.DSPAdvisorView"; //$NON-NLS-1$
	    String JDBC_IMPORT_POST_PROCESSSOR = PLUGIN_ID + ".wizards.vdbViewImportPostProcessor"; //$NON-NLS-1$
	}

	/**
	 * Constants related to extension points, including all extension point ID's and extension point schema component names.
	 * 
	 * @since 4.0
	 */
	interface ExtensionPoints {
	    interface AdvisorStatusManagerExtension {
	        String ID = "advisorStatusProvider"; //$NON-NLS-1$
	        String CLASSNAME = "name"; //$NON-NLS-1$
	    }
	}

	public interface Groups {
	    public static final int GROUP_MODEL_VALIDATION = 0;
	    public static final int GROUP_SOURCES = 1;
	    public static final int GROUP_CONNECTIONS = 2;
	    public static final int GROUP_VIEWS = 3;
	    public static final int GROUP_VIEW_MAPPINGS = 4;
	    public static final int GROUP_XML_SCHEMAS = 5;
	    public static final int GROUP_WEBSERVICE_MODELS = 6;
	    public static final int GROUP_PREVIEW_WSDL = 7;
	    public static final int GROUP_VDBS = 8;
	    public static final int GROUP_PROJECT = 9;
	}

}
