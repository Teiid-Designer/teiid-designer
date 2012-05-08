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
import org.teiid.designer.advisor.ui.AdvisorUiConstants;
import org.teiid.designer.advisor.ui.Messages;

public class AdvisorGuides implements AdvisorUiConstants {

	Map<String, AdvisorActionInfo[]> actionInfoMap;
	
	public static final String MODEL_JDBC_SOURCE = Messages.ModelJdbcSource;
	public static final String MODEL_FLAT_FILE_SOURCE = Messages.ModelFlatFileSource;
	public static final String MODEL_REMOTE_XML_SOURCE = Messages.ModelRemoteXmlFileSource;
	public static final String MODEL_LOCAL_XML_SOURCE = Messages.ModelLocalXmlFileSource;
	public static final String CONSUME_SOAP_WEB_SERVICE = Messages.ConsumeSoapWebService;
	public static final String TEIID_SERVER_ACTIONS = Messages.TeiidServer;
			
	public AdvisorGuides() {
		super();
		init();
	}
	
	private void init() {
		this.actionInfoMap = new HashMap<String, AdvisorActionInfo[]>(3);
		Collection<AdvisorActionInfo> infoList = new ArrayList<AdvisorActionInfo>();
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_CONNECTION_JDBC));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.IMPORT_JDBC));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.PREVIEW_DATA));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_VDB));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.EXECUTE_VDB));
		
        AdvisorActionInfo[] infoArray = infoList.toArray(new AdvisorActionInfo[infoList.size()]);
		actionInfoMap.put(MODEL_JDBC_SOURCE, infoArray);
		
		infoList = new ArrayList<AdvisorActionInfo>();
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_CONNECTION_FLAT_FILE));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.IMPORT_FLAT_FILE));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.PREVIEW_DATA));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_VDB));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.EXECUTE_VDB));

        infoArray = infoList.toArray(new AdvisorActionInfo[infoList.size()]);
		actionInfoMap.put(MODEL_FLAT_FILE_SOURCE, infoArray);
		
		infoList = new ArrayList<AdvisorActionInfo>();
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_LOCAL));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.IMPORT_XML_FILE));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.PREVIEW_DATA));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_VDB));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.EXECUTE_VDB));

        infoArray = infoList.toArray(new AdvisorActionInfo[infoList.size()]);
		actionInfoMap.put(MODEL_LOCAL_XML_SOURCE, infoArray);

		infoList = new ArrayList<AdvisorActionInfo>();
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_CONNECTION_XML_FILE_URL));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.IMPORT_XML_FILE_URL));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.PREVIEW_DATA));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_VDB));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.EXECUTE_VDB));

        infoArray = infoList.toArray(new AdvisorActionInfo[infoList.size()]);
		actionInfoMap.put(MODEL_REMOTE_XML_SOURCE, infoArray);
		
		infoList = new ArrayList<AdvisorActionInfo>();
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_CONNECTION_WEB_SERVICE_ODA));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.GENERATE_WS_MODELS_FROM_WSDL));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.PREVIEW_DATA));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.CREATE_VDB));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.EXECUTE_VDB));

        infoArray = infoList.toArray(new AdvisorActionInfo[infoList.size()]);
		actionInfoMap.put(CONSUME_SOAP_WEB_SERVICE, infoArray);
		
		infoList = new ArrayList<AdvisorActionInfo>();
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_SERVER));
		infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.EDIT_TEIID_SERVER));

        infoArray = infoList.toArray(new AdvisorActionInfo[infoList.size()]);
		actionInfoMap.put(TEIID_SERVER_ACTIONS, infoArray);
		
	}
	
	public Object[] getChildren(String categoryId) {
		return actionInfoMap.get(categoryId);
	}
	
	public List<String> getCategories() {
		List<String> categories = new ArrayList<String>();
		for( String cat : actionInfoMap.keySet()) {
			categories.add(cat);
		}
		return categories;
	}
}
