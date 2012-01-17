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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.Messages;
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;

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
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL, COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_LOCAL, COMMAND_LABELS_SHORT.CREATE_CONNECTION_XML_FILE_LOCAL);
        addActionHandler(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL, COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_URL, COMMAND_LABELS_SHORT.CREATE_CONNECTION_XML_FILE_URL);
        addActionHandler(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE, COMMAND_LABELS.NEW_MODEL_RELATIONAL_SOURCE, COMMAND_LABELS_SHORT.NEW_MODEL_RELATIONAL_SOURCE);
        addActionHandler(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW, COMMAND_LABELS.NEW_MODEL_RELATIONAL_VIEW, COMMAND_LABELS_SHORT.NEW_MODEL_RELATIONAL_VIEW);
        addActionHandler(COMMAND_IDS.NEW_MODEL_WS, COMMAND_LABELS.NEW_MODEL_WS, COMMAND_LABELS_SHORT.NEW_MODEL_WS);
        addActionHandler(COMMAND_IDS.NEW_MODEL_XML_DOC, COMMAND_LABELS.NEW_MODEL_XML_DOC, COMMAND_LABELS_SHORT.NEW_MODEL_XML_DOC);
        addActionHandler(COMMAND_IDS.CREATE_VDB, COMMAND_LABELS.CREATE_VDB, COMMAND_LABELS_SHORT.CREATE_VDB);
        addActionHandler(COMMAND_IDS.EXECUTE_VDB, COMMAND_LABELS.EXECUTE_VDB, COMMAND_LABELS.EXECUTE_VDB);
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
		return new AdvisorActionInfo(commandId, displayName, shortDisplayName, handler);
	}
	
	public static void executeAction(TeiidDesignerActionHandler actionHandler) {
		String id = actionHandler.getId();
		
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
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_XML_FILE_LOCAL);
	        return;
		}
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL)) {
			createConnection(CONNECTION_PROFILE_IDS.CATEGORY_XML_FILE_URL);
	        return;
		}
		
		if( id.equalsIgnoreCase(COMMAND_IDS.CREATE_VDB)) {
			NewVdbAction action = new NewVdbAction();
			action.run();
	        return;
		}
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
	        WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), wiz);
	        wizardDialog.setBlockOnOpen(true);
	        wizardDialog.open();
		} else {
			INewWizard wiz = (INewWizard) new NewTeiidFilteredCPWizard(id);
	
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), (Wizard) wiz);
			wizardDialog.setBlockOnOpen(true);
			wizardDialog.open();
		}
	}
}
