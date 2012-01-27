/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.datatools.connectivity.db.generic.ui.wizard.NewJDBCFilteredCPWizard;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;
import org.teiid.designer.runtime.ui.preview.PreviewDataAction;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;
import com.metamatrix.ui.internal.util.UiUtil;
import com.metamatrix.ui.internal.util.WidgetUtil;

/**
 * Factory intended to provide high-level access to actions and their handlers for Teiid Designer Advisor framework
 */
public class AdvisorActionFactory implements AdvisorUiConstants {
	
	static Map<String, AdvisorActionInfo> actionInfos;

	static void loadHandlers() {

        AdvisorActionFactory.actionInfos = new HashMap<String, AdvisorActionInfo>();
        addActionHandler(COMMAND_IDS.IMPORT_DDL, COMMAND_LABELS.IMPORT_DDL, COMMAND_LABELS_SHORT.IMPORT_DDL);
        addActionHandler(COMMAND_IDS.IMPORT_FLAT_FILE, COMMAND_LABELS.IMPORT_FLAT_FILE, COMMAND_LABELS_SHORT.IMPORT_FLAT_FILE);
        addActionHandler(COMMAND_IDS.IMPORT_JDBC, COMMAND_LABELS.IMPORT_JDBC, COMMAND_LABELS_SHORT.IMPORT_JDBC);
        addActionHandler(COMMAND_IDS.IMPORT_SALESFORCE, COMMAND_LABELS.IMPORT_SALESFORCE, COMMAND_LABELS_SHORT.IMPORT_SALESFORCE);
        addActionHandler(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE, COMMAND_LABELS.IMPORT_WSDL_TO_SOURCE, COMMAND_LABELS_SHORT.IMPORT_WSDL_TO_SOURCE);
        addActionHandler(COMMAND_IDS.IMPORT_WSDL_TO_WS, COMMAND_LABELS.IMPORT_WSDL_TO_WS, COMMAND_LABELS_SHORT.IMPORT_WSDL_TO_WS);
        addActionHandler(COMMAND_IDS.IMPORT_XML_FILE, COMMAND_LABELS.IMPORT_XML_FILE, COMMAND_LABELS_SHORT.IMPORT_XML_FILE);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE, COMMAND_LABELS.CREATE_CONNECTION_FLAT_FILE, COMMAND_LABELS_SHORT.CREATE_CONNECTION_FLAT_FILE);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_JDBC, COMMAND_LABELS.CREATE_CONNECTION_JDBC, COMMAND_LABELS_SHORT.CREATE_CONNECTION_JDBC);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_LDAP, COMMAND_LABELS.CREATE_CONNECTION_LDAP, COMMAND_LABELS_SHORT.CREATE_CONNECTION_LDAP);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE, COMMAND_LABELS.CREATE_CONNECTION_MODESHAPE, COMMAND_LABELS_SHORT.CREATE_CONNECTION_MODESHAPE);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE, COMMAND_LABELS.CREATE_CONNECTION_SALESFORCE, COMMAND_LABELS_SHORT.CREATE_CONNECTION_SALESFORCE);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE, COMMAND_LABELS.CREATE_CONNECTION_WEB_SERVICE, COMMAND_LABELS_SHORT.CREATE_CONNECTION_WEB_SERVICE);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA, COMMAND_LABELS.CREATE_CONNECTION_WEB_SERVICE_ODA, COMMAND_LABELS_SHORT.CREATE_CONNECTION_WEB_SERVICE_ODA);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL, COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_LOCAL, COMMAND_LABELS_SHORT.CREATE_CONNECTION_XML_FILE_LOCAL);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL, COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_URL, COMMAND_LABELS_SHORT.CREATE_CONNECTION_XML_FILE_URL);
        addActionHandler(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE, COMMAND_LABELS.NEW_MODEL_RELATIONAL_SOURCE, COMMAND_LABELS_SHORT.NEW_MODEL_RELATIONAL_SOURCE);
        addActionHandler(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW, COMMAND_LABELS.NEW_MODEL_RELATIONAL_VIEW, COMMAND_LABELS_SHORT.NEW_MODEL_RELATIONAL_VIEW);
        addActionHandler(COMMAND_IDS.NEW_MODEL_WS, COMMAND_LABELS.NEW_MODEL_WS, COMMAND_LABELS_SHORT.NEW_MODEL_WS);
        addActionHandler(COMMAND_IDS.NEW_MODEL_XML_DOC, COMMAND_LABELS.NEW_MODEL_XML_DOC, COMMAND_LABELS_SHORT.NEW_MODEL_XML_DOC);
        addActionHandler(COMMAND_IDS.CREATE_VDB, COMMAND_LABELS.CREATE_VDB, COMMAND_LABELS_SHORT.CREATE_VDB);
        addActionHandler(COMMAND_IDS.EXECUTE_VDB, COMMAND_LABELS.EXECUTE_VDB, COMMAND_LABELS.EXECUTE_VDB);
        addActionHandler(COMMAND_IDS.PREVIEW_DATA, COMMAND_LABELS.PREVIEW_DATA, COMMAND_LABELS.PREVIEW_DATA);
        addActionHandler(COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW, COMMAND_LABELS.OPEN_DATA_SOURCE_EXPLORER_VIEW, COMMAND_LABELS.OPEN_DATA_SOURCE_EXPLORER_VIEW);
        addActionHandler(COMMAND_IDS.CREATE_WEB_SRVICES_DATA_FILE, COMMAND_LABELS.CREATE_WEB_SRVICES_DATA_FILE, COMMAND_LABELS.CREATE_WEB_SRVICES_DATA_FILE);
        addActionHandler(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL, COMMAND_LABELS.GENERATE_WS_MODELS_FROM_WSDL, COMMAND_LABELS.GENERATE_WS_MODELS_FROM_WSDL);

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
	
	public static AdvisorActionInfo createInfo(String commandId, String displayName, String shortDisplayName) {
		AbstractHandler handler = new TeiidDesignerActionHandler(commandId, displayName);
		AdvisorActionInfo info = new AdvisorActionInfo(commandId, displayName, shortDisplayName, handler);
		String imageId = getImageId(commandId);
		info.setImageId(imageId);
		return info;
	}
	
	public static void executeAction(TeiidDesignerActionHandler actionHandler) {
		AdvisorActionFactory.executeAction(actionHandler.getId());
	}
	
	public static void executeAction(String id) {
		
		
		// IMPORT OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_DDL)) {
			 launchWizard(ImportMetadataAction.DDL_TO_RELATIONAL);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_FLAT_FILE)) {
			 launchWizard(ImportMetadataAction.TEIID_FLAT_FILE);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_JDBC)) {
			 launchWizard(ImportMetadataAction.JDBC);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_SALESFORCE)) {
			 launchWizard(ImportMetadataAction.SALESFORCE_TO_RELATIONAL);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_XML_FILE)) {
			 launchWizard(ImportMetadataAction.TEIID_XML_FILE);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE)) {
			 launchWizard(ImportMetadataAction.WSDL_TO_RELATIONAL);
			 return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.IMPORT_WSDL_TO_WS)) {
			launchWizard(ImportMetadataAction.WSDL_TO_WEB_SERVICE);
			 return;
		}
		
		// NEW MODEL OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE)) {
			createNewModel(ModelType.PHYSICAL_LITERAL, MODEL_CLASSES.RELATIONAL);
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW)) {
			createNewModel(ModelType.VIRTUAL_LITERAL, MODEL_CLASSES.RELATIONAL);
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_MODEL_WS)) {
			createNewModel(ModelType.VIRTUAL_LITERAL, MODEL_CLASSES.WEB_SERVICE);
	        return;
		}
		
		// CONNECTIONPROFILE OPTIONS
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_JDBC)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_JDBC);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_ODA_FLAT_FILE_ID);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_LDAP)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_LDAP_CONNECTION);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_MODESHAPE);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_SALESFORCE_CONNECTION);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_WS_CONNECTION);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_ODA_WS_ID);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_XML_FILE_LOCAL);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_XML_FILE_URL);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL)) {
			 launchWizard(ImportMetadataAction.WSDL_TO_RELATIONAL);
			 return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_VDB)) {
			ModelerUiViewUtils.launchWizard("newVdbWizard", new StructuredSelection()); //$NON-NLS-1$
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT)) {
			ModelerUiViewUtils.launchWizard("newModelProject", new StructuredSelection()); //$NON-NLS-1$
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
			PreviewDataAction action = new PreviewDataAction();
			action.run();
	        return;
		}
		
		MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Unimplemented Action",  //$NON-NLS-1$
					"Action for ID [" + id + "] is not yet implemented"); //$NON-NLS-1$ //$NON-NLS-2$
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
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_WEB_SRVICES_DATA_FILE)) {
			return Images.CREATE_WEB_SRVICES_DATA_FILE;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_VDB)) {
			return Images.NEW_VDB;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.EXECUTE_VDB)) {
			return Images.EXECUTE_VDB_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.PREVIEW_DATA)) {
			return Images.PREVIEW_DATA;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT)) {
			return Images.NEW_PROJECT_ACTION;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.OPEN_DATA_SOURCE_EXPLORER_VIEW)) {
			return Images.DATA_SOURCE_EXPLORER_VIEW;
		}
		return null;
	}
	
	private static void createNewModel(ModelType type, String modelClass) {
        NewModelAction nma = new NewModelAction(type, modelClass, null);
        nma.run();
	}
	
	private static void launchWizard(String id) {
		ModelerUiViewUtils.launchWizard(id, new StructuredSelection());
	}
	
	private static void createConnection(String id) {
		if( id.equalsIgnoreCase(CONNECTION_PROFILE_IDS.CATEGORY_JDBC) ) {
			NewJDBCFilteredCPWizard wiz = new NewJDBCFilteredCPWizard();
			ModelerUiViewUtils.launchWizard(wiz, new StructuredSelection());
		} else {
			INewWizard wiz = (INewWizard) new NewTeiidFilteredCPWizard(id);
			ModelerUiViewUtils.launchWizard(wiz, new StructuredSelection());
		}
	}
	
}
