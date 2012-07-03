/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.db.generic.ui.wizard.NewJDBCFilteredCPWizard;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.OpenCheatSheetAction;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.AdvisorUiPlugin;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.runtime.Server;
import org.teiid.designer.runtime.ui.actions.DeployVdbAction;
import org.teiid.designer.runtime.ui.actions.EditVdbAction;
import org.teiid.designer.runtime.ui.connection.CreateDataSourceAction;
import org.teiid.designer.runtime.ui.preview.PreviewDataAction;
import org.teiid.designer.runtime.ui.server.RuntimeAssistant;
import org.teiid.designer.runtime.ui.vdb.ExecuteVdbAction;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.dqp.DqpPlugin;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.modeler.ui.viewsupport.IPropertiesContext;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * Factory intended to provide high-level access to actions and their handlers for Teiid Designer Advisor framework
 */
public class AdvisorActionFactory implements AdvisorUiConstants, IPropertyChangeListener {
	static boolean actionsLoaded = false;
	
    static final String EXT_PT = "cheatSheetContent"; //$NON-NLS-1$
    static final String ID_ATTR = "id"; //$NON-NLS-1$
    static final String NAME_ATTR = "name"; //$NON-NLS-1$
    static final String CHEATSHEET_ELEMENT = "cheatsheet"; //$NON-NLS-1$
    static final String CHEAT_SHEET_PLUGIN_ID = "org.eclipse.ui.cheatsheets"; //$NON-NLS-1$
	
	static Map<String, AdvisorActionInfo> actionInfos;
	
	static IConfigurationElement[] cheatsheets;
	static Collection<IAction> cheatSheetActions;
	
	static IAction ACTION_IMPORT_DDL;
	static IAction ACTION_IMPORT_FLAT_FILE;
	static IAction ACTION_IMPORT_JDBC;
	static IAction ACTION_IMPORT_SALESFORCE;
	static IAction ACTION_IMPORT_WSDL_TO_SOURCE;
	static IAction ACTION_IMPORT_WSDL_TO_WS;
	static IAction ACTION_IMPORT_XML_FILE;
	static IAction ACTION_IMPORT_XML_FILE_REMOTE;
	static IAction ACTION_CREATE_CONNECTION_FLAT_FILE;
	static IAction ACTION_CREATE_CONNECTION_JDBC;
	static IAction ACTION_CREATE_CONNECTION_LDAP;
	static IAction ACTION_CREATE_CONNECTION_MODESHAPE;
	static IAction ACTION_CREATE_CONNECTION_SALESFORCE;
	static IAction ACTION_CREATE_CONNECTION_WEB_SERVICE;
	static IAction ACTION_CREATE_CONNECTION_WEB_SERVICE_ODA;
	static IAction ACTION_CREATE_CONNECTION_XML_FILE_LOCAL;
	static IAction ACTION_CREATE_CONNECTION_XML_FILE_URL;
	static IAction ACTION_NEW_MODEL_RELATIONAL_SOURCE;
	static IAction ACTION_NEW_MODEL_RELATIONAL_VIEW;
	static IAction ACTION_NEW_MODEL_WS;
	static IAction ACTION_NEW_MODEL_XML_DOC;
	static IAction ACTION_CREATE_VDB;
	static IAction ACTION_DEFINE_VDB;
	static IAction ACTION_EXECUTE_VDB;
	static IAction ACTION_EDIT_VDB;
	static IAction ACTION_DEPLOY_VDB;
	static IAction ACTION_PREVIEW_DATA;
	static IAction ACTION_OPEN_DATA_SOURCE_EXPLORER_VIEW;
	static IAction ACTION_GENERATE_WS_MODELS_FROM_WSDL;
	static IAction ACTION_NEW_TEIID_SERVER;
	static IAction ACTION_EDIT_TEIID_SERVER;
	static IAction ACTION_CREATE_DATA_SOURCE;
	static IAction ACTION_NEW_TEIID_MODEL_PROJECT;
	static IAction ACTION_DEFINE_TEIID_MODEL_PROJECT;
	static IAction ACTION_DEFINE_VIEW_TABLE;
	static IAction ACTION_DEFINE_REST_PROCEDURE;

	static void loadHandlers() {

        AdvisorActionFactory.actionInfos = new HashMap<String, AdvisorActionInfo>();
        addActionHandler(
        		COMMAND_IDS.IMPORT_DDL, 
        		COMMAND_LABELS.IMPORT_DDL, 
        		COMMAND_LABELS_SHORT.IMPORT_DDL,
        		COMMAND_DESC.IMPORT_DDL);
        addActionHandler(
        		COMMAND_IDS.IMPORT_FLAT_FILE, 
        		COMMAND_LABELS.IMPORT_FLAT_FILE, 
        		COMMAND_LABELS_SHORT.IMPORT_FLAT_FILE,
        		COMMAND_DESC.IMPORT_FLAT_FILE);
        addActionHandler(
        		COMMAND_IDS.IMPORT_JDBC, 
        		COMMAND_LABELS.IMPORT_JDBC, 
        		COMMAND_LABELS_SHORT.IMPORT_JDBC,
        		COMMAND_DESC.IMPORT_JDBC);
        addActionHandler(
        		COMMAND_IDS.IMPORT_SALESFORCE, 
        		COMMAND_LABELS.IMPORT_SALESFORCE, 
        		COMMAND_LABELS_SHORT.IMPORT_SALESFORCE,
        		COMMAND_DESC.IMPORT_SALESFORCE);
        addActionHandler(
        		COMMAND_IDS.IMPORT_WSDL_TO_SOURCE, 
        		COMMAND_LABELS.IMPORT_WSDL_TO_SOURCE, 
        		COMMAND_LABELS_SHORT.IMPORT_WSDL_TO_SOURCE,
        		COMMAND_DESC.IMPORT_WSDL_TO_SOURCE);
        addActionHandler(
        		COMMAND_IDS.IMPORT_WSDL_TO_WS, 
        		COMMAND_LABELS.IMPORT_WSDL_TO_WS, 
        		COMMAND_LABELS_SHORT.IMPORT_WSDL_TO_WS,
        		COMMAND_DESC.IMPORT_WSDL_TO_WS);
        addActionHandler(
        		COMMAND_IDS.IMPORT_XML_FILE, 
        		COMMAND_LABELS.IMPORT_XML_FILE, 
        		COMMAND_LABELS_SHORT.IMPORT_XML_FILE,
        		COMMAND_DESC.IMPORT_XML_FILE);
        addActionHandler(
        		COMMAND_IDS.IMPORT_XML_FILE_URL, 
        		COMMAND_LABELS.IMPORT_XML_FILE_URL, 
        		COMMAND_LABELS_SHORT.IMPORT_XML_FILE_URL,
        		COMMAND_DESC.IMPORT_XML_FILE_URL);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE, 
        		COMMAND_LABELS.CREATE_CONNECTION_FLAT_FILE, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_FLAT_FILE,
        		COMMAND_DESC.CREATE_CONNECTION_FLAT_FILE);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_JDBC, 
        		COMMAND_LABELS.CREATE_CONNECTION_JDBC, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_JDBC,
        		COMMAND_DESC.CREATE_CONNECTION_JDBC);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_LDAP, 
        		COMMAND_LABELS.CREATE_CONNECTION_LDAP, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_LDAP,
        		COMMAND_DESC.CREATE_CONNECTION_LDAP);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_MODESHAPE, 
        		COMMAND_LABELS.CREATE_CONNECTION_MODESHAPE, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_MODESHAPE,
        		COMMAND_DESC.CREATE_CONNECTION_MODESHAPE);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_SALESFORCE, 
        		COMMAND_LABELS.CREATE_CONNECTION_SALESFORCE, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_SALESFORCE,
        		COMMAND_DESC.CREATE_CONNECTION_SALESFORCE);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE, 
        		COMMAND_LABELS.CREATE_CONNECTION_WEB_SERVICE, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_WEB_SERVICE,
        		COMMAND_DESC.CREATE_CONNECTION_WEB_SERVICE);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA, 
        		COMMAND_LABELS.CREATE_CONNECTION_WEB_SERVICE_ODA, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_WEB_SERVICE_ODA,
        		COMMAND_DESC.CREATE_CONNECTION_WEB_SERVICE_ODA);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL, 
        		COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_LOCAL, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_XML_FILE_LOCAL,
        		COMMAND_DESC.CREATE_CONNECTION_XML_FILE_LOCAL);
        addActionHandler(
        		COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL, 
        		COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_URL, 
        		COMMAND_LABELS_SHORT.CREATE_CONNECTION_XML_FILE_URL,
        		COMMAND_DESC.CREATE_CONNECTION_XML_FILE_URL);
        addActionHandler(
        		COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE, 
        		COMMAND_LABELS.NEW_MODEL_RELATIONAL_SOURCE, 
        		COMMAND_LABELS_SHORT.NEW_MODEL_RELATIONAL_SOURCE,
        		COMMAND_DESC.NEW_MODEL_RELATIONAL_SOURCE);
        addActionHandler(
        		COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW, 
        		COMMAND_LABELS.NEW_MODEL_RELATIONAL_VIEW, 
        		COMMAND_LABELS_SHORT.NEW_MODEL_RELATIONAL_VIEW,
        		COMMAND_DESC.NEW_MODEL_RELATIONAL_VIEW);
        addActionHandler(
        		COMMAND_IDS.NEW_MODEL_WS, 
        		COMMAND_LABELS.NEW_MODEL_WS, 
        		COMMAND_LABELS_SHORT.NEW_MODEL_WS,
        		COMMAND_DESC.NEW_MODEL_WS);
        addActionHandler(
        		COMMAND_IDS.NEW_MODEL_XML_DOC, 
        		COMMAND_LABELS.NEW_MODEL_XML_DOC, 
        		COMMAND_LABELS_SHORT.NEW_MODEL_XML_DOC,
        		COMMAND_DESC.NEW_MODEL_XML_DOC);
        addActionHandler(
        		COMMAND_IDS.CREATE_VDB, 
        		COMMAND_LABELS.CREATE_VDB, 
        		COMMAND_LABELS_SHORT.CREATE_VDB,
        		COMMAND_DESC.CREATE_VDB);
        addActionHandler(
        		COMMAND_IDS.DEFINE_VDB,
                COMMAND_LABELS.DEFINE_VDB,
                COMMAND_LABELS_SHORT.DEFINE_VDB,
                COMMAND_DESC.DEFINE_VDB);
        addActionHandler(
        		COMMAND_IDS.DEFINE_SOURCE,
                COMMAND_LABELS.DEFINE_SOURCE,
                COMMAND_LABELS_SHORT.DEFINE_SOURCE,
                COMMAND_DESC.DEFINE_SOURCE);
        addActionHandler(
        		COMMAND_IDS.EXECUTE_VDB, 
        		COMMAND_LABELS.EXECUTE_VDB, 
        		COMMAND_LABELS.EXECUTE_VDB,
        		COMMAND_DESC.EXECUTE_VDB);
        addActionHandler(
        		COMMAND_IDS.EDIT_VDB, 
        		COMMAND_LABELS.EDIT_VDB, 
        		COMMAND_LABELS_SHORT.EDIT_VDB,
        		COMMAND_DESC.EDIT_VDB);
        addActionHandler(
        		COMMAND_IDS.DEPLOY_VDB, 
        		COMMAND_LABELS.DEPLOY_VDB, 
        		COMMAND_LABELS_SHORT.DEPLOY_VDB,
        		COMMAND_DESC.DEPLOY_VDB);
        addActionHandler(
        		COMMAND_IDS.PREVIEW_DATA, 
        		COMMAND_LABELS.PREVIEW_DATA, 
        		COMMAND_LABELS.PREVIEW_DATA,
        		COMMAND_DESC.PREVIEW_DATA);
        addActionHandler(
        		COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW, 
        		COMMAND_LABELS.OPEN_DATA_SOURCE_EXPLORER_VIEW, 
        		COMMAND_LABELS.OPEN_DATA_SOURCE_EXPLORER_VIEW,
        		COMMAND_DESC.OPEN_DATA_SOURCE_EXPLORER_VIEW);
        addActionHandler(
        		COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL, 
        		COMMAND_LABELS.GENERATE_WS_MODELS_FROM_WSDL, 
        		COMMAND_LABELS.GENERATE_WS_MODELS_FROM_WSDL,
        		COMMAND_DESC.GENERATE_WS_MODELS_FROM_WSDL);
        addActionHandler(
        		COMMAND_IDS.NEW_TEIID_SERVER, 
        		COMMAND_LABELS.NEW_TEIID_SERVER, 
        		COMMAND_LABELS_SHORT.NEW_TEIID_SERVER,
        		COMMAND_DESC.NEW_TEIID_SERVER);
        addActionHandler(
        		COMMAND_IDS.EDIT_TEIID_SERVER, 
        		COMMAND_LABELS.EDIT_TEIID_SERVER, 
        		COMMAND_LABELS_SHORT.EDIT_TEIID_SERVER,
        		COMMAND_DESC.EDIT_TEIID_SERVER);
        addActionHandler(
        		COMMAND_IDS.CREATE_DATA_SOURCE, 
        		COMMAND_LABELS.CREATE_DATA_SOURCE, 
        		COMMAND_LABELS_SHORT.CREATE_DATA_SOURCE,
        		COMMAND_DESC.CREATE_DATA_SOURCE);
        addActionHandler(
        		COMMAND_IDS.NEW_TEIID_MODEL_PROJECT, 
        		COMMAND_LABELS.NEW_TEIID_MODEL_PROJECT, 
        		COMMAND_LABELS_SHORT.NEW_TEIID_MODEL_PROJECT,
        		COMMAND_DESC.NEW_TEIID_MODEL_PROJECT);
        addActionHandler(
        		COMMAND_IDS.DEFINE_TEIID_MODEL_PROJECT, 
        		COMMAND_LABELS.DEFINE_TEIID_MODEL_PROJECT, 
        		COMMAND_LABELS_SHORT.DEFINE_TEIID_MODEL_PROJECT,
        		COMMAND_DESC.DEFINE_TEIID_MODEL_PROJECT);
        addActionHandler(
        		COMMAND_IDS.NEW_OBJECT_VIEW_TABLE,
                COMMAND_LABELS.NEW_OBJECT_VIEW_TABLE,
                COMMAND_LABELS_SHORT.NEW_OBJECT_VIEW_TABLE,
                COMMAND_DESC.NEW_OBJECT_VIEW_TABLE);
        addActionHandler(
        		COMMAND_IDS.DEFINE_VIEW_TABLE,
                COMMAND_LABELS.DEFINE_VIEW_TABLE,
                COMMAND_LABELS_SHORT.DEFINE_VIEW_TABLE,
                COMMAND_DESC.DEFINE_VIEW_TABLE);
        addActionHandler(
        		COMMAND_IDS.DEFINE_VIEW_PROCEDURE,
                COMMAND_LABELS.DEFINE_VIEW_PROCEDURE,
                COMMAND_LABELS_SHORT.DEFINE_VIEW_PROCEDURE,
                COMMAND_DESC.DEFINE_VIEW_PROCEDURE);
        addActionHandler(
        		COMMAND_IDS.NEW_OBJECT_REST_PROCEDURE,
                COMMAND_LABELS.NEW_OBJECT_REST_PROCEDURE,
                COMMAND_LABELS_SHORT.NEW_OBJECT_REST_PROCEDURE,
                COMMAND_DESC.NEW_OBJECT_REST_PROCEDURE);
        addActionHandler(
        		COMMAND_IDS.GENERATE_REST_WAR,
                COMMAND_LABELS.GENERATE_REST_WAR,
                COMMAND_LABELS_SHORT.GENERATE_REST_WAR,
                COMMAND_DESC.GENERATE_REST_WAR);

        addActionHandler(
        		COMMAND_IDS.GENERATE_SOAP_WAR,
                COMMAND_LABELS.GENERATE_SOAP_WAR,
                COMMAND_LABELS_SHORT.GENERATE_SOAP_WAR,
                COMMAND_DESC.GENERATE_SOAP_WAR);
        
        addActionHandler(
        		COMMAND_IDS.DEPLOY_WAR,
                COMMAND_LABELS.DEPLOY_WAR,
                COMMAND_LABELS_SHORT.DEPLOY_WAR,
                COMMAND_DESC.DEPLOY_WAR);
        
        addActionHandler(
        		CHEAT_SHEET_IDS.MODEL_FROM_JDBC_SOURCE, 
        		CHEAT_SHEET_DISPLAY_NAMES.MODEL_FROM_JDBC_SOURCE, 
        		CHEAT_SHEET_DISPLAY_NAMES.MODEL_FROM_JDBC_SOURCE);
        addActionHandler(
        		CHEAT_SHEET_IDS.MULTI_SOURCE_VDB, 
        		CHEAT_SHEET_DISPLAY_NAMES.MULTI_SOURCE_VDB, 
        		CHEAT_SHEET_DISPLAY_NAMES.MULTI_SOURCE_VDB);
        addActionHandler(
        		CHEAT_SHEET_IDS.MODEL_XML_LOCAL_SOURCE,
                CHEAT_SHEET_DISPLAY_NAMES.MODEL_XML_LOCAL_SOURCE,
                CHEAT_SHEET_DISPLAY_NAMES.MODEL_XML_LOCAL_SOURCE);
        addActionHandler(
        		CHEAT_SHEET_IDS.MODEL_XML_REMOTE_SOURCE,
                CHEAT_SHEET_DISPLAY_NAMES.MODEL_XML_REMOTE_SOURCE,
                CHEAT_SHEET_DISPLAY_NAMES.MODEL_XML_REMOTE_SOURCE);
        addActionHandler(
        		CHEAT_SHEET_IDS.MODEL_FLAT_FILE_SOURCE, 
        		CHEAT_SHEET_DISPLAY_NAMES.MODEL_FLAT_FILE_SOURCE, 
        		CHEAT_SHEET_DISPLAY_NAMES.MODEL_FLAT_FILE_SOURCE);
        addActionHandler(
        		CHEAT_SHEET_IDS.CREATE_AND_TEST_VDB, 
        		CHEAT_SHEET_DISPLAY_NAMES.CREATE_AND_TEST_VDB, 
        		CHEAT_SHEET_DISPLAY_NAMES.CREATE_AND_TEST_VDB);
        addActionHandler(
        		CHEAT_SHEET_IDS.CONSUME_SOAP_SERVICE, 
        		CHEAT_SHEET_DISPLAY_NAMES.CONSUME_SOAP_SERVICE, 
        		CHEAT_SHEET_DISPLAY_NAMES.CONSUME_SOAP_SERVICE);

	}
	
	public static AbstractHandler getActionHandler(String id) {
		if( actionInfos == null ) {
			loadHandlers();
		}
		AdvisorActionInfo info = getActionInfo(id);
		if( info != null ) {
			return info.getActionHandler();
		}
		return null;
	}
	
	public static AdvisorActionInfo getActionInfo(String id) {
		if( actionInfos == null ) {
			loadHandlers();
		}
		return actionInfos.get(id);
	}
	
	private static void addActionHandler(String id, String displayName, String shortDisplayName) {
		actionInfos.put(id, createInfo(id, displayName, shortDisplayName));
	}
	
	private static void addActionHandler(String id, String displayName, String shortDisplayName, String description) {
		AdvisorActionInfo info = createInfo(id, displayName, shortDisplayName);
		if( description != null ) {
			info.setDescription(description);
		}
		actionInfos.put(id, info);
	}
	
	public static AdvisorActionInfo createInfo(String commandId, String displayName, String shortDisplayName) {
		AbstractHandler handler = new TeiidDesignerActionHandler(commandId, displayName);
		AdvisorActionInfo info = new AdvisorActionInfo(commandId, displayName, shortDisplayName, handler);
		String imageId = getImageId(commandId);
		info.setImageId(imageId);
		return info;
	}
	
	public static void executeAction(TeiidDesignerActionHandler actionHandler, boolean synchronous) {
		AdvisorActionFactory.executeAction(actionHandler.getId(), synchronous);
	}
	
	public static void executeAction(String id, boolean synchronous) {
		AdvisorActionFactory.executeAction(id, null, synchronous);
	}
	
	public static void executeAction(String id, Properties properties, boolean synchronous) {
		
		
		// IMPORT OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_DDL)) {
			 launchWizard(ImportMetadataAction.DDL_TO_RELATIONAL, properties, synchronous);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_FLAT_FILE)) {
			 launchWizard(ImportMetadataAction.TEIID_FLAT_FILE, properties, synchronous);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_JDBC)) {
			 launchWizard(ImportMetadataAction.JDBC, properties, synchronous);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_SALESFORCE)) {
			 launchWizard(ImportMetadataAction.SALESFORCE_TO_RELATIONAL, properties, synchronous);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_XML_FILE)) {
			 properties.put(IPropertiesContext.KEY_IMPORT_XML_TYPE, IPropertiesContext.IMPORT_XML_LOCAL);
			 launchWizard(ImportMetadataAction.TEIID_XML_FILE, properties, synchronous);
			 return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_XML_FILE_URL)) {
			 properties.put(IPropertiesContext.KEY_IMPORT_XML_TYPE, IPropertiesContext.IMPORT_XML_REMOTE);
			 launchWizard(ImportMetadataAction.TEIID_XML_FILE, properties, synchronous);
			 return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE)) {
			 launchWizard(ImportMetadataAction.WSDL_TO_RELATIONAL, properties, synchronous);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_WS)) {
			launchWizard(ImportMetadataAction.WSDL_TO_WEB_SERVICE, properties, synchronous);
			 return;
		}
		
		// NEW MODEL OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE)) {
			createNewModel(ModelType.PHYSICAL_LITERAL, MODEL_CLASSES.RELATIONAL, properties);
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW)) {
			createNewModel(ModelType.VIRTUAL_LITERAL, MODEL_CLASSES.RELATIONAL, properties);
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_WS)) {
			createNewModel(ModelType.VIRTUAL_LITERAL, MODEL_CLASSES.WEB_SERVICE, properties);
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_XML_DOC)) {
			createNewModel(ModelType.VIRTUAL_LITERAL, MODEL_CLASSES.XML, properties);
	        return;
		}
		
        // NEW OBJECT OPTIONS
        if (id.equalsIgnoreCase(COMMAND_IDS.DEFINE_VIEW_TABLE)) {
        	DefineViewTableAction action = new DefineViewTableAction(properties);
            action.run();
            return;
        }
        
        // NEW OBJECT OPTIONS
        if (id.equalsIgnoreCase(COMMAND_IDS.DEFINE_VIEW_PROCEDURE)) {
        	DefineViewProcedureAction action = new DefineViewProcedureAction(properties);
            action.run();
            return;
        }

        // CONNECTIONPROFILE OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_JDBC)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_JDBC, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_ODA_FLAT_FILE_ID, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_LDAP)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_LDAP_CONNECTION, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_MODESHAPE, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_SALESFORCE_CONNECTION, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_WS_CONNECTION, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_ODA_WS_ID, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_XML_FILE_LOCAL, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_XML_FILE_URL, properties);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL)) {
			 launchWizard(ImportMetadataAction.WSDL_TO_RELATIONAL, properties, synchronous);
			 return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_VDB)) {
			ModelerUiViewUtils.launchWizard("newVdbWizard", new StructuredSelection(), properties, synchronous); //$NON-NLS-1$
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_VDB)) {
			DefineVdbAction action = new DefineVdbAction(properties);
			action.run();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EDIT_VDB)) {
			EditVdbAction action = new EditVdbAction();
			action.run();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EXECUTE_VDB)) {
			ExecuteVdbAction action = new ExecuteVdbAction(properties);
			action.run();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEPLOY_VDB)) {
			DeployVdbAction action = new DeployVdbAction(properties);
			action.queryUserAndRun();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT)) {;
			ModelerUiViewUtils.launchWizard("newModelProject", new StructuredSelection(), properties, synchronous); //$NON-NLS-1$
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_TEIID_MODEL_PROJECT)) {
			DefineProjectAction action = new DefineProjectAction(properties);
			action.run();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW)) {
	        try {
	            UiUtil.getWorkbenchPage().showView("org.eclipse.datatools.connectivity.DataSourceExplorerNavigator"); //$NON-NLS-1$
	        } catch (final PartInitException err) {
	            AdvisorUiConstants.UTIL.log(err);
	            WidgetUtil.showError(err.getLocalizedMessage());
	        }
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.PREVIEW_DATA)) {
            PreviewDataAction action = new PreviewDataAction(properties);
			action.run();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_SERVER)) {
			RuntimeAssistant.runNewServerAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EDIT_TEIID_SERVER)) {
			RuntimeAssistant.runEditServerAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_DATA_SOURCE)) {
            // make sure there is a Teiid connection
            if (RuntimeAssistant.ensureServerConnection(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
            		Messages.CreateDataSource_NoServerMessage)) {
            	try {
					Server server = DqpPlugin.getInstance().getServerManager().getDefaultServer();
					
					CreateDataSourceAction action = new CreateDataSourceAction();
					action.setAdmin(server.getAdmin());

					action.setSelection(new StructuredSelection());

					action.setEnabled(true);
					action.run();
				} catch (Exception ex) {
					AdvisorUiConstants.UTIL.log(ex);
				}
            }

	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_SOURCE)) {
			DefineSourceAction action = new DefineSourceAction(properties);
			action.run();
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_REST_WAR)) {
			GenerateRESTWarAction action = new GenerateRESTWarAction(properties);
			action.run();
	        return;
		}
		
//		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_SOAP_WAR)) {
//			// TODO
//	        return;
//		}

		if( id.equalsIgnoreCase(COMMAND_IDS.DEPLOY_WAR)) {
			LaunchInstructionsAction action = new LaunchInstructionsAction(INSTRUCTIONS.DEPLOY_WAR_FILE);
			action.run();
	        return;
		}


		if( id.equalsIgnoreCase(CHEAT_SHEET_IDS.CONSUME_SOAP_SERVICE) ||
				id.equalsIgnoreCase(CHEAT_SHEET_IDS.CREATE_AND_TEST_VDB) ||
				id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_FLAT_FILE_SOURCE) ||
				id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_FROM_JDBC_SOURCE) ||
 id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_XML_LOCAL_SOURCE)
            || id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_XML_REMOTE_SOURCE)
            ||
				id.equalsIgnoreCase(CHEAT_SHEET_IDS.MULTI_SOURCE_VDB) ) {
			executeCheatSheet(id);
			return;
		}
				
		
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Unimplemented Action",  //$NON-NLS-1$
					"Action for ID [" + id + "] is not yet implemented"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static ImageDescriptor getImageDesciptor(String commandId) {
		String id = getImageId(commandId);
		if( id != null ) {
			return AdvisorUiPlugin.getDefault().getImageDescriptor(getImageId(commandId));
		}
		
		return null;
	}
	
	public static String getImageId(String id) {
		// IMPORT OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_DDL)) {
			 return Images.IMPORT;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_FLAT_FILE)) {
			return Images.IMPORT;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_JDBC)) {
			return Images.IMPORT_JDBC;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_SALESFORCE)) {
			return Images.IMPORT;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_XML_FILE)) {
			return Images.IMPORT;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_XML_FILE_URL)) {
			return Images.IMPORT;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE)) {
			return Images.IMPORT_WSDL;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_WS)) {
			return Images.IMPORT_WSDL;
		}
		
		// NEW MODEL OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE)) {
			return Images.NEW_MODEL_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW)) {
			return Images.NEW_MODEL_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_WS)) {
			return Images.NEW_MODEL_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_XML_DOC)) {
			return Images.NEW_MODEL_ACTION;
		}
		
		// CONNECTIONPROFILE OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_JDBC)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_LDAP)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL)) {
			return Images.NEW_CONNECTION_PROFILE;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL)) {
			return Images.NEW_WEB_SERVICES_MODEL;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_VDB)) {
			return Images.NEW_VDB;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_VDB)) {
			return Images.NEW_VDB;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EXECUTE_VDB)) {
			return Images.EXECUTE_VDB_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.PREVIEW_DATA)) {
			return Images.PREVIEW_DATA;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_DATA_SOURCE)) {
			return Images.CREATE_DATA_SOURCE_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EDIT_VDB)) {
			return Images.EDIT_VDB_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEPLOY_VDB)) {
			return Images.DEPLOY_VDB_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT)) {
			return Images.NEW_PROJECT_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_TEIID_MODEL_PROJECT)) {
			return Images.NEW_PROJECT_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW)) {
			return Images.DATA_SOURCE_EXPLORER_VIEW;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_SERVER)) {
			return Images.NEW_TEIID_SERVER;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EDIT_TEIID_SERVER)) {
			return Images.EDIT_TEIID_SERVER;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_OBJECT_VIEW_TABLE)) {
			return Images.NEW_VIRTUAL_TABLE_ICON;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_VIEW_TABLE)) {
			return Images.NEW_VIRTUAL_TABLE_ICON;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_VIEW_PROCEDURE)) {
			return Images.NEW_VIRTUAL_PROCEDURE_ICON;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_OBJECT_REST_PROCEDURE)) {
			return Images.NEW_VIRTUAL_PROCEDURE_ICON;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_REST_WAR)) {
			return Images.GENERATE_WAR;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_SOAP_WAR)) {
			return Images.GENERATE_WAR;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEPLOY_WAR)) {
			return Images.DEPLOY_WAR;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.DEFINE_SOURCE)) {
			return Images.CREATE_SOURCES;
		}
		
		if( id.equalsIgnoreCase(CHEAT_SHEET_IDS.CONSUME_SOAP_SERVICE) ) {
			return CHEAT_SHEET_IMAGE_IDS.CONSUME_SOAP_SERVICE;
		}
		if(	id.equalsIgnoreCase(CHEAT_SHEET_IDS.CREATE_AND_TEST_VDB) ) {
			return CHEAT_SHEET_IMAGE_IDS.CREATE_AND_TEST_VDB;
		}
		if( id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_FLAT_FILE_SOURCE) ) {
			return CHEAT_SHEET_IMAGE_IDS.MODEL_FLAT_FILE_SOURCE;
		}
		if(	id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_FROM_JDBC_SOURCE) ) {
			return CHEAT_SHEET_IMAGE_IDS.MODEL_FROM_JDBC_SOURCE;
		}
        if (id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_XML_LOCAL_SOURCE)) {
            return CHEAT_SHEET_IMAGE_IDS.MODEL_XML_LOCAL_SOURCE;
		}
        if (id.equalsIgnoreCase(CHEAT_SHEET_IDS.MODEL_XML_REMOTE_SOURCE)) {
            return CHEAT_SHEET_IMAGE_IDS.MODEL_XML_REMOTE_SOURCE;
        }
		if(	id.equalsIgnoreCase(CHEAT_SHEET_IDS.MULTI_SOURCE_VDB) ) {
			return CHEAT_SHEET_IMAGE_IDS.MULTI_SOURCE_VDB;
		}

		return null;
	}
	
	public static Image getImage(AdvisorActionInfo actionInfo) {
		return getImage(getImageId(actionInfo.getId()));
	}
	
	public static Image getImage(String imageId) {
		return AdvisorUiPlugin.getDefault().getImage(imageId);
	}
	
	private static void createNewModel(ModelType type, String modelClass, Properties properties) {
        NewModelAction nma = new NewModelAction(type, modelClass, null, properties);
        nma.run();
	}
	
	private static void launchWizard(String id, Properties properties, boolean synchronous) {
		ModelerUiViewUtils.launchWizard(id, new StructuredSelection(), properties, synchronous);
	}
	
	private static void createConnection(final String id, final Properties properties) {
		// Add then remove profile changed listener so new CP name can be set in properties
		IProfileListener listener = new PropertiesProfileChangedListener(properties);
		ProfileManager.getInstance().addProfileListener(listener);
		
		if( id.equalsIgnoreCase(CONNECTION_PROFILE_IDS.CATEGORY_JDBC) ) {
			NewJDBCFilteredCPWizard wiz = new NewJDBCFilteredCPWizard();
			ModelerUiViewUtils.launchWizard(wiz, new StructuredSelection(), properties, true);
		} else {
            INewWizard wiz = new NewTeiidFilteredCPWizard(id);
			ModelerUiViewUtils.launchWizard(wiz, new StructuredSelection(), properties, true);
		}
		
		ProfileManager.getInstance().removeProfileListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
	}

	public static void addActionsLibraryToMenu(IMenuManager manager) {
		if( !actionsLoaded ) {
			initActions();
		}
		
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.MODEL_PROJECT_MANAGEMENT, ASPECT_MODEL_PROJECT_MANAGEMENT);
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.DEFINE_MODELS, ASPECT_DEFINE_MODELS);
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.MANAGE_CONNECTIONS, ASPECT_MANAGE_CONNECTIONS);
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.MODEL_DATA_SOURCES, ASPECT_MODEL_DATA_SOURCES);
		//addMenuForAspect(manager, MODELING_ASPECT_LABELS.CONSUME_REST_WS, ASPECT_CONSUME_REST_WS);
//		addMenuForAspect(manager, MODELING_ASPECT_LABELS.CONSUME_SOAP_WS, ASPECT_CONSUME_SOAP_WS);
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.MANAGE_VDBS, ASPECT_MANAGE_VDBS);
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.TEIID_SERVER, ASPECT_TEIID_SERVER);
		addMenuForAspect(manager, MODELING_ASPECT_LABELS.TEST, ASPECT_TEST);
		
		if( !cheatSheetActions.isEmpty() ) {
			manager.add( new Separator());
			MenuManager subManager = new MenuManager("Cheat Sheets"); //$NON-NLS-1$
			for(IAction action : cheatSheetActions) {
				subManager.add(action);
			}
			manager.add(subManager);
		}
	}
	
	private static void addMenuForAspect(final IMenuManager manager, final String aspectId, final String[] aspectCommandsIds ) {
		if( aspectCommandsIds.length == 0 ) return;
		
		MenuManager subManager = new MenuManager(aspectId);
		for(String commandId :  aspectCommandsIds) {
			IAction action = getAction(commandId);
			if( action != null ) {
				subManager.add(action);
			}
		}
		manager.add(subManager);
	}
	
	public static void executeCheatSheet(String id) {
		for(IAction action : cheatSheetActions) {
			if( action.getId().equalsIgnoreCase(id)) {
				action.run();
			}
		}
	}
	
	private static IAction getAction(final String commandId) {
		// IMPORT OPTIONS
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_DDL)) {
			 return ACTION_IMPORT_DDL;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_FLAT_FILE)) {
			return ACTION_IMPORT_FLAT_FILE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_JDBC)) {
			return ACTION_IMPORT_JDBC;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_SALESFORCE)) {
			return ACTION_IMPORT_SALESFORCE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_XML_FILE)) {
			return ACTION_IMPORT_WSDL_TO_SOURCE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE)) {
			return ACTION_IMPORT_WSDL_TO_WS;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_WS)) {
			return ACTION_IMPORT_XML_FILE;
		}
		
		// NEW MODEL OPTIONS
		if( commandId.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE)) {
			return ACTION_NEW_MODEL_RELATIONAL_SOURCE;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW)) {
			return ACTION_NEW_MODEL_RELATIONAL_VIEW;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_WS)) {
			return ACTION_NEW_MODEL_WS;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_XML_DOC)) {
			return ACTION_NEW_MODEL_XML_DOC;
		}
		
		// CONNECTIONPROFILE OPTIONS
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_JDBC)) {
			return ACTION_CREATE_CONNECTION_FLAT_FILE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE)) {
			return ACTION_CREATE_CONNECTION_JDBC;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_LDAP)) {
			return ACTION_CREATE_CONNECTION_LDAP;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE)) {
			return ACTION_CREATE_CONNECTION_MODESHAPE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE)) {
			return ACTION_CREATE_CONNECTION_SALESFORCE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE)) {
			return ACTION_CREATE_CONNECTION_WEB_SERVICE;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA)) {
			return ACTION_CREATE_CONNECTION_WEB_SERVICE_ODA;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL)) {
			return ACTION_CREATE_CONNECTION_XML_FILE_LOCAL;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL)) {
			return ACTION_CREATE_CONNECTION_XML_FILE_URL;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL)) {
			return ACTION_GENERATE_WS_MODELS_FROM_WSDL;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_VDB)) {
			return ACTION_CREATE_VDB;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.DEFINE_VDB)) {
			return ACTION_CREATE_VDB;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.EXECUTE_VDB)) {
			return ACTION_EXECUTE_VDB;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.PREVIEW_DATA)) {
			return ACTION_PREVIEW_DATA;
		}
		if( commandId.equalsIgnoreCase(COMMAND_IDS.CREATE_DATA_SOURCE)) {
			return ACTION_CREATE_DATA_SOURCE;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.EDIT_VDB)) {
			return ACTION_EDIT_VDB;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.DEPLOY_VDB)) {
			return ACTION_DEPLOY_VDB;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT)) {
			return ACTION_NEW_TEIID_MODEL_PROJECT;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.DEFINE_TEIID_MODEL_PROJECT)) {
			return ACTION_DEFINE_TEIID_MODEL_PROJECT;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW)) {
			return ACTION_OPEN_DATA_SOURCE_EXPLORER_VIEW;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_SERVER)) {
			return ACTION_NEW_TEIID_SERVER;
		}
		
		if( commandId.equalsIgnoreCase(COMMAND_IDS.EDIT_TEIID_SERVER)) {
			return ACTION_EDIT_TEIID_SERVER;
		}
		return null;
	}
	
	private static void initActions() {
		actionsLoaded = true;
		loadCheatSheetExtensions();

		cheatSheetActions = new ArrayList<IAction>();

        for (IConfigurationElement cheatSheet : cheatsheets) {
            String id = cheatSheet.getAttribute(ID_ATTR);
            // Only includes metamatrix cheat sheets for now
            if (id.indexOf("teiid") > -1) { //$NON-NLS-1$
                String sheetName = cheatSheet.getAttribute(NAME_ATTR);

                IAction action = createCheatSheetAction(id, sheetName);
                if( action != null ) {
                	cheatSheetActions.add(action);
                }
            }
        }
		
		ACTION_IMPORT_DDL = createAction(COMMAND_IDS.IMPORT_DDL);
		ACTION_IMPORT_FLAT_FILE = createAction(COMMAND_IDS.IMPORT_FLAT_FILE);
		ACTION_IMPORT_JDBC = createAction(COMMAND_IDS.IMPORT_JDBC);
		ACTION_IMPORT_SALESFORCE = createAction(COMMAND_IDS.IMPORT_SALESFORCE);
		ACTION_IMPORT_WSDL_TO_SOURCE = createAction(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE);
		ACTION_IMPORT_WSDL_TO_WS = createAction(COMMAND_IDS.IMPORT_WSDL_TO_WS);
		ACTION_IMPORT_XML_FILE = createAction(COMMAND_IDS.IMPORT_XML_FILE);
		ACTION_IMPORT_XML_FILE_REMOTE = createAction(COMMAND_IDS.IMPORT_XML_FILE_URL);
		ACTION_CREATE_CONNECTION_FLAT_FILE = createAction(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE);
		ACTION_CREATE_CONNECTION_JDBC = createAction(COMMAND_IDS.CREATE_CONNECTION_JDBC);
		ACTION_CREATE_CONNECTION_LDAP = createAction(COMMAND_IDS.CREATE_CONNECTION_LDAP);
		ACTION_CREATE_CONNECTION_MODESHAPE = createAction(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE);
		ACTION_CREATE_CONNECTION_SALESFORCE = createAction(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE);
		ACTION_CREATE_CONNECTION_WEB_SERVICE = createAction(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE);
		ACTION_CREATE_CONNECTION_WEB_SERVICE_ODA = createAction(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA);
		ACTION_CREATE_CONNECTION_XML_FILE_LOCAL = createAction(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL);
		ACTION_CREATE_CONNECTION_XML_FILE_URL = createAction(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL);
		ACTION_NEW_MODEL_RELATIONAL_SOURCE = createAction(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE);
		ACTION_NEW_MODEL_RELATIONAL_VIEW = createAction(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW);
		ACTION_NEW_MODEL_WS = createAction(COMMAND_IDS.NEW_MODEL_WS);
		ACTION_NEW_MODEL_XML_DOC = createAction(COMMAND_IDS.NEW_MODEL_XML_DOC);
		ACTION_CREATE_VDB = createAction(COMMAND_IDS.CREATE_VDB);
		ACTION_EXECUTE_VDB = createAction(COMMAND_IDS.EXECUTE_VDB);
		ACTION_EDIT_VDB = createAction(COMMAND_IDS.EDIT_VDB);
		ACTION_DEPLOY_VDB = createAction(COMMAND_IDS.DEPLOY_VDB);
		ACTION_PREVIEW_DATA = createAction(COMMAND_IDS.PREVIEW_DATA);
		ACTION_OPEN_DATA_SOURCE_EXPLORER_VIEW = createAction(COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW);
		ACTION_GENERATE_WS_MODELS_FROM_WSDL = createAction(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL);
		ACTION_NEW_TEIID_SERVER = createAction(COMMAND_IDS.NEW_TEIID_SERVER);
		ACTION_EDIT_TEIID_SERVER = createAction(COMMAND_IDS.EDIT_TEIID_SERVER);
		ACTION_CREATE_DATA_SOURCE = createAction(COMMAND_IDS.CREATE_DATA_SOURCE);
		ACTION_NEW_TEIID_MODEL_PROJECT = createAction(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT);
		ACTION_DEFINE_TEIID_MODEL_PROJECT = createAction(COMMAND_IDS.DEFINE_TEIID_MODEL_PROJECT);
		
	}
	
	private static IAction createAction(final String commandId) {
		final AdvisorActionInfo info = getActionInfo(commandId);
		if( info != null ) {
			final IAction action = new Action(info.getDisplayName()) {
	            @Override
	            public void run() {
	            	AdvisorActionFactory.executeAction(info.getId(), true);
	            }
	        };
	
			ImageDescriptor desc = AdvisorActionFactory.getImageDesciptor(info.getId());
            if (desc != null) {
				action.setImageDescriptor(desc);
			}
			
			return action;
		}
		
		return null;
	}
	
	private static IAction createCheatSheetAction(final String sheetId, final String name) {

		if( sheetId != null ) {
			final IAction action = new Action(name) {
	            @Override
	            public void run() {
	            	OpenCheatSheetAction action = new OpenCheatSheetAction(sheetId);
	                action.run();
	            }
	        };
	        action.setId(sheetId);
			return action;
		}
		
		return null;
	}
	
	private static void loadCheatSheetExtensions() {
        IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CHEAT_SHEET_PLUGIN_ID, EXT_PT);

        if (extensionPoint != null) {
            IExtension[] extensions = extensionPoint.getExtensions();

            if (extensions.length != 0) {
                List temp = new ArrayList();

                for (int i = 0; i < extensions.length; ++i) {
                    IConfigurationElement[] elements = extensions[i].getConfigurationElements();

                    // only care about cheatsheet configuration elements. don't care about category elements.
                    for (int j = 0; j < elements.length; ++j) {
                        if (elements[j].getName().equals(CHEATSHEET_ELEMENT)) {
                            temp.add(elements[j]);
                        }
                    }
                }

                if (!temp.isEmpty()) {
                    temp.toArray(cheatsheets = new IConfigurationElement[temp.size()]);
                } else {
                    cheatsheets = new IConfigurationElement[0];
                }
            }
        }

        if (cheatsheets == null) {
            cheatsheets = new IConfigurationElement[0];
        }
    }
}
