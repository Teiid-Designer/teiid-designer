/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package org.teiid.designer.advisor.ui.util;

import java.util.Properties;

import org.teiid.designer.advisor.ui.AdvisorUiConstants.COMMAND_IDS;
import org.teiid.designer.ui.viewsupport.DesignerPropertiesUtil;

/**
 *
 */
public class DesignerPropertiesMapperUtil {

	public static String IGNORE = "IGNORE";  //$NON-NLS-1$

	// Need a method that can find the property string value given the action guide ID
	
    public static String getActionsValueLabel(String actionId, Properties properties ) {
    	if( 	   actionId.equals(COMMAND_IDS.DEFINE_VDB) 
    			|| actionId.equals(COMMAND_IDS.EDIT_VDB)
    			|| actionId.equals(COMMAND_IDS.EXECUTE_VDB) ) {
    		return DesignerPropertiesUtil.getVdbName(properties);
    	}
    	
    	if( actionId.equals(COMMAND_IDS.DEFINE_TEIID_MODEL_PROJECT) ) {
    		return DesignerPropertiesUtil.getProjectName(properties);
    	}
    	
    	if( actionId.equals(COMMAND_IDS.IMPORT_JDBC) ||
    		actionId.equals(COMMAND_IDS.IMPORT_TEIID_CONNECTION_DDL)) {
    		return DesignerPropertiesUtil.getSourceModelName(properties);
    	}
    	
    	if( actionId.equals(COMMAND_IDS.IMPORT_WSDL_TO_SOURCE) ) {
    		return DesignerPropertiesUtil.getViewModelName(properties);
    	}
    	
    	if( 	actionId.equals(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_JDBC)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_LDAP)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_MODESHAPE)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_SALESFORCE)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL)
    			|| actionId.equals(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL)) {
    		return DesignerPropertiesUtil.getConnectionProfileName(properties);
    	}
    	
    	if( actionId.equals(COMMAND_IDS.CREATE_DATA_SOURCE) ) {
    		return IGNORE;
    	}
    	
    	return null;
    }

}
