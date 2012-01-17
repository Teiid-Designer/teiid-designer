/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui;

import org.eclipse.osgi.util.NLS;

public class Messages  extends NLS {

    public static String ConsumeLocalFlatFileDataSource;
    public static String ConsumeWebSercieWsdl;
    public static String ConsumeXmlFileSource;
    public static String ConsumeARESTWebService;
    public static String ConsumeASOAPWebService;

    public static String CreateARESTWebService;
    public static String CreateASOAPWebService;
    public static String CreateJdbcConnection;
    public static String CreateLDAPConnection;
    public static String CreateModeshapeConnection;
    public static String CreateNewModelExtensionDefinition;
    public static String CreateNewRelationalBaseTable;
    public static String CreateNewRelationalSourceFunction;
    public static String CreateNewRelationalSourceModel;
    public static String CreateNewRelationalViewModel;
    public static String CreateNewWebServiceViewModel;
    public static String CreateNewXmlDocumentViewModel;
    public static String CreateSalesforceConnection;
    public static String CreateSourceModelFromDdlFile;
    public static String CreateSourceModelFromJdbcSource;
    public static String CreateSourceModelFromSalesforceDataSource;
    public static String CreateSourceModelFromWsdlSource;
    public static String CreateTeiidFlatFileConnection;
    public static String CreateTeiidLocalXmlConnection;
    public static String CreateTeiidModelProject;
    public static String CreateTeiidRemoteXmlConnection;
    public static String CreateVdb;
    public static String CreateWebServicesConnection;
    
    public static String ExecuteVdb;
    
    public static String ModelingAspectOptions;
    public static String ManageModelProjects;
    public static String ModelDataSources;
    public static String ManageConnections;
    public static String ModelViews;
    public static String ManageVdbs;
    
    public static String NewModelWizardErrorMessage;
    public static String NewModelWizardErrorTitle;
    
    public static String OpenDatatoolsDataSourceExplorer;
    
    public static String TeiidTaskManager;

    static {
        NLS.initializeMessages("org.teiid.designer.advisor.ui.messages", Messages.class); //$NON-NLS-1$
    }
    
    /*
     * EXAMPLE:
     * 
     * NLS.bind(Messages.ModelDoesNotHaveConnectionInfoError, model.getFullPath()), null);
     * 
     */
}
