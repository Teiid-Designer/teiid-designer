/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.actions;

public interface ModelActionConstants {
	String MODELING_LABEL = "Modeling...";
	
    interface Resource {
        String SHOW_MODEL_STATISTICS_ACTION = "ShowModelStatisticsAction"; //$NON-NLS-1$
        String BUILD_MODEL_IMPORTS = "BuildModelImportsAction"; //$NON-NLS-1$
        String CONVERT_TO_ENTERPRIZE_TYPES = "ConvertToEnterpriseTypesAction"; //$NON-NLS-1$
        String REMOVE_CONNECTION_INFO = "RemoveConnectionInfoAction"; //$NON-NLS-1$
        String VIEW_CONNECTION_PROFILE = "ViewConnectionProfileAction"; //$NON-NLS-1$
        String GENERATE_REST_WAR = "GenerateRestWarAction"; //$NON-NLS-1$
        String CREATE_DATA_SOURCE = "CreateDataSourceAction"; //$NON-NLS-1$
        String CREATE_VDB_DATA_SOURCE = "CreateVdbDataSourceAction"; //$NON-NLS-1$
        String SET_TRANSLATOR_NAME = "SetTranslatorNameAction"; //$NON-NLS-1$
        String SET_JBOSS_DATA_SOURCE_NAME = "SetJBossDataSourceNameAction"; //$NON-NLS-1$
        String EDIT_TRANSLATOR_OVERRIDES = "EditTranslatorOverridesAction"; //$NON-NLS-1$
        String GENERATE_DYNAMIC_VDB = "GenerateDynamicVdbAction"; //$NON-NLS-1$
        String GENERATE_ARCHIVE_VDB = "GenerateArchiveVdbAction"; //$NON-NLS-1$
        String SET_CONNECTION_PROFILE = "SetConnectionProfileAction"; //$NON-NLS-1$
        String MANAGE_MODEL_EXTENSIONS = "ManageModelExtensionDefnsAction"; //$NON-NLS-1$
        String UPDATE_REGISTRY_MODEL_EXTENSIONS = "updateRegistryModelExtensionDefinitionAction"; //$NON-NLS-1$
        String CREATE_SALESFORCE_FUNCTIONS = "CreateSalesForceFunctionsAction"; //$NON-NLS-1$
        String EDIT_TRANSFORMATION = "EditTransformationAction"; //$NON-NLS-1$
        String CONVERT_FUNCTIONS = "ConvertFunctionsAction"; //$NON-NLS-1$
        String REVALIDATE_TRANSFORMATIONS = "RevalidateModelTransformationsAction"; //$NON-NLS-1$
        String PREVIEW_WSDL = "PreviewWsdlAction"; //$NON-NLS-1$
        String CREATE_VIRTUAL_MODEL_FROM_SCHEMA = "createVirtualModelFromSchemaAction"; //$NON-NLS-1$
        String DEPLOY_VDB = "DeployVdbAction"; //$NON-NLS-1$
        String EXECUTE_VDB = "ExecuteVdbAction"; //$NON-NLS-1$
    }
    
    interface Special {
        String CREATE_WEB_SERVICE_FROM_RELATIONAL = "GenerateXsdSchemaAction2"; //$NON-NLS-1$
        String SET_DATATYPE = "SetDatatypeModelingAction"; //$NON-NLS-1$
        String GET_EXECUTION_PLAN_CONTEXT = "GetExecutionPlanContextAction"; //$NON-NLS-1$
        String PREVIEW_DATA = "PreviewTableDataContextAction"; //$NON-NLS-1$
        String JDBC_COST_ANALYSIS = "JdbcCostAnalysisAction"; //$NON-NLS-1$
        String SET_EXCLUDE_FROM_DOCUMENT = "SetExcludeFromDocumentSpecialAction"; //$NON-NLS-1$
        String SET_INCLUDE_IN_DOCUMENT = "SetIncludeInDocumentSpecialAction"; //$NON-NLS-1$
        String CREATE_XML_TO_SCALAR_TRANSFORM = "CreateXMLToScalarTransformation"; //$NON-NLS-1$
        String CREATE_SCALAR_TO_XML_TRANSFORM = "CreateScalarToXMLTransformation"; //$NON-NLS-1$
        String MATERIALIZE = "MaterializationAction"; //$NON-NLS-1$
        String CLEAR_MATERIALIZATIO = "ClearMaterializationAction"; //$NON-NLS-1$
        String CREATE_POJO = "CreatePojoAction"; //$NON-NLS-1$
        String CREATE_VIEW_FROM_TABLE = "CreateViewFromTableAction"; //$NON-NLS-1$
        String CREATE_VIEW = "CreateViewInModelAction"; //$NON-NLS-1$
        String SET_FUNCTION_PARAMETER_TYPE = "SetFunctionParameterTypeModelingAction"; //$NON-NLS-1$
        String GENERATE_REST_VIRTUAL_PROCEDURES = "GenerateRestVirtualProceduresAction"; //$NON-NLS-1$
        String CREATE_WEB_SERVICE_FROM_XML = "GenerateWebServiceModelAction2"; //$NON-NLS-1$
        String CREATE_XML_VIEW_FROM_XSD = "CreateXmlViewFromXsdAction"; //$NON-NLS-1$
        String GENERATE_DATA_SERVICE = "GenerateDataServiceAction"; //$NON-NLS-1$
    }
    
    interface WizardsIDs {
    	String JDBC_IMPORT = "jdbcImportWizard"; //$NON-NLS-1$
    	String EXPORT_TEIID_DDL = "exportTeiidDdlWizard"; //$NON-NLS-1$
    	String NEW_VDB = "newVdbWizard"; //$NON-NLS-1$
    	String FLAT_FILE_IMPORT = "teiidMetadataImportWizard"; //$NON-NLS-1$
    	String XML_FILE_IMPORT = "teiidXmlImportWizard"; //$NON-NLS-1$
    	String REST_WS_IMPORT = "teiidRestImportWizard"; //$NON-NLS-1$
    	String TEIID_IMPORT = "teiidImportWizard"; //$NON-NLS-1$
    	String WSDL_WS_IMPORT= "wsdlFileSystemImportWizard"; //$NON-NLS-1$
    	String SOAP_WS_IMPORT= "org.teiid.designer.modelgenerator.wsdl.ui.wizards.soap.ImportWsdlSoapWizardID"; //$NON-NLS-1$
    	String SALESFORCE_IMPORT= "SalesforceToRelationalImportWizard"; //$NON-NLS-1$
    	String LDAP_IMPORT= "org.teiid.designer.modelgenerator.ldap.ui.wizards.ImportLdapWizardID"; //$NON-NLS-1$
    	String DDL_IMPORT= "org.teiid.designer.ddl.importer.ui.ddlImportWizard"; //$NON-NLS-1$
    	String TEIID_DDL_IMPORT= "org.teiid.designer.ddl.importer.ui.teiidDdlImportWizard"; //$NON-NLS-1$
    }
    interface ProfileIDs {
    	String REST_WS = "org.teiid.designer.datatools.profiles.ws.WSConnectionProfile";  //$NON-NLS-1$
    	String SALESFORCE = "org.teiid.designer.datatools.salesforce.connectionProfile"; //$NON-NLS-1$
    	String JBOSS_DS = "org.teiid.designer.datatools.profiles.jbossds.JBossDsConnectionProfile"; //$NON-NLS-1$
    	String FILE_URL_REMOTE = "org.teiid.designer.datatools.profiles.xml.fileurl"; //$NON-NLS-1$
    }
    
    interface Custom {
    	String SHOW_DEPENCENCY_DIAGRAM = "ShowDependencyDiagramAction"; //$NON-NLS-1$
    	String GENERATE_DEPENDENCY_REPORT = "GenerateDependencyReportAction"; //$NON-NLS-1$
    }

}
