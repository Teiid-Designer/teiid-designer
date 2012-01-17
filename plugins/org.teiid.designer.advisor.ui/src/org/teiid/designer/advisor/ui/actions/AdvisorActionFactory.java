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
import org.teiid.designer.datatools.ui.dialogs.NewTeiidFilteredCPWizard;

import com.metamatrix.metamodels.core.ModelType;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelerUiViewUtils;

/**
 * Factory intended to provide high-level access to actions and their handlers for Teiid Designer Advisor framework
 */
public class AdvisorActionFactory implements AdvisorUiConstants {
	static Map<String, AbstractHandler> actionHandlers;

	static void loadHandlers() {
		AdvisorActionFactory.actionHandlers = new HashMap<String, AbstractHandler>();
		
		actionHandlers.put(COMMAND_IDS.IMPORT_DDL, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_DDL, COMMAND_LABELS.IMPORT_DDL));
		actionHandlers.put(COMMAND_IDS.IMPORT_FLAT_FILE, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_FLAT_FILE, COMMAND_LABELS.IMPORT_FLAT_FILE));
		actionHandlers.put(COMMAND_IDS.IMPORT_JDBC, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_JDBC, COMMAND_LABELS.IMPORT_JDBC));
		actionHandlers.put(COMMAND_IDS.IMPORT_SALESFORCE, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_SALESFORCE, COMMAND_LABELS.IMPORT_SALESFORCE));
		actionHandlers.put(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE, COMMAND_LABELS.IMPORT_WSDL_TO_SOURCE));
		actionHandlers.put(COMMAND_IDS.IMPORT_WSDL_TO_WS, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_WSDL_TO_WS, COMMAND_LABELS.IMPORT_WSDL_TO_WS));
		actionHandlers.put(COMMAND_IDS.IMPORT_XML_FILE, 
				new TeiidDesignerActionHandler(COMMAND_IDS.IMPORT_XML_FILE, COMMAND_LABELS.IMPORT_XML_FILE));
		
		//actionHandlers.put(COMMAND_IDS.NEW_MODEL_MED, new NewMEDCommandHandler());
        actionHandlers.put(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE, 
				new TeiidDesignerActionHandler(COMMAND_IDS.NEW_MODEL_RELATIONAL_SOURCE, COMMAND_LABELS.NEW_MODEL_RELATIONAL_SOURCE));
        actionHandlers.put(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW,  
				new TeiidDesignerActionHandler(COMMAND_IDS.NEW_MODEL_RELATIONAL_VIEW, COMMAND_LABELS.NEW_MODEL_RELATIONAL_VIEW));
        actionHandlers.put(COMMAND_IDS.NEW_MODEL_WS,  
				new TeiidDesignerActionHandler(COMMAND_IDS.NEW_MODEL_WS, COMMAND_LABELS.NEW_MODEL_WS));
        actionHandlers.put(COMMAND_IDS.NEW_MODEL_XML_DOC,  
				new TeiidDesignerActionHandler(COMMAND_IDS.NEW_MODEL_XML_DOC, COMMAND_LABELS.NEW_MODEL_XML_DOC));
        
        actionHandlers.put(COMMAND_IDS.CREATE_VDB,  
				new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_VDB, COMMAND_LABELS.CREATE_VDB));
        actionHandlers.put(COMMAND_IDS.EXECUTE_VDB,  
				new TeiidDesignerActionHandler(COMMAND_IDS.EXECUTE_VDB, COMMAND_LABELS.EXECUTE_VDB));
        
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_JDBC, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_JDBC, COMMAND_LABELS.CREATE_CONNECTION_JDBC));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE, COMMAND_LABELS.CREATE_CONNECTION_FLAT_FILE));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_LDAP, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_LDAP, COMMAND_LABELS.CREATE_CONNECTION_LDAP));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE, COMMAND_LABELS.CREATE_CONNECTION_MODESHAPE));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE, COMMAND_LABELS.CREATE_CONNECTION_SALESFORCE));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE, COMMAND_LABELS.CREATE_CONNECTION_WEB_SERVICE));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL, COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_LOCAL));
        actionHandlers.put(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL, 
        		new TeiidDesignerActionHandler(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL, COMMAND_LABELS.CREATE_CONNECTION_XML_FILE_URL));
	}
	
	public static AbstractHandler getActionHandler(String id) {
		if( actionHandlers == null ) {
			loadHandlers();
		}
		return actionHandlers.get(id);
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
