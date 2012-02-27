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

public class AdvisorCheatSheets implements AdvisorUiConstants {
	Map<String, AdvisorActionInfo[]> actionInfoMap;
	
	public static final String PROJECT_SETUP = Messages.ProjectManagement;
	public static final String SOURCE_MODELING = Messages.SourceModeling;
	public static final String IMPORTING = Messages.Importing;
	public static final String XML_MODELING = Messages.XML_Modeling;
	public static final String TESTING = Messages.Testing;
			
	public AdvisorCheatSheets() {
		super();
		init();
	}
	
	@SuppressWarnings("unused")
	private void init() {
		this.actionInfoMap = new HashMap<String, AdvisorActionInfo[]>(5);
		AdvisorActionInfo[] infoArray = null;
		Collection<AdvisorActionInfo> infoList = null;
		
		PROJECT_SETUP : {
			infoList = new ArrayList<AdvisorActionInfo>();
			infoList.add(AdvisorActionFactory.getActionInfo(COMMAND_IDS.NEW_TEIID_MODEL_PROJECT));

			infoArray = (AdvisorActionInfo[])infoList.toArray(new AdvisorActionInfo[infoList.size()]);
			actionInfoMap.put(PROJECT_SETUP, infoArray);
		}
		
		SOURCE_MODELING : {
			infoList = new ArrayList<AdvisorActionInfo>();
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_FROM_JDBC_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_FLAT_FILE_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_LOCAL_XML_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MULTI_SOURCE_VDB));
	
			infoArray = (AdvisorActionInfo[])infoList.toArray(new AdvisorActionInfo[infoList.size()]);
			actionInfoMap.put(SOURCE_MODELING, infoArray);
		}
		
		IMPORTING : {
			infoList = new ArrayList<AdvisorActionInfo>();
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_FROM_JDBC_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_FLAT_FILE_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_LOCAL_XML_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.CONSUME_SOAP_SERVICE));
	
			infoArray = (AdvisorActionInfo[])infoList.toArray(new AdvisorActionInfo[infoList.size()]);
			actionInfoMap.put(IMPORTING, infoArray);
		}
		
		XML_MODELING : {
			infoList = new ArrayList<AdvisorActionInfo>();
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MODEL_LOCAL_XML_SOURCE));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.CONSUME_SOAP_SERVICE));
	
			infoArray = (AdvisorActionInfo[])infoList.toArray(new AdvisorActionInfo[infoList.size()]);
			actionInfoMap.put(XML_MODELING, infoArray);
		}
		
		TESTING : {
			infoList = new ArrayList<AdvisorActionInfo>();
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.CREATE_AND_TEST_VDB));
			infoList.add(AdvisorActionFactory.getActionInfo(CHEAT_SHEET_IDS.MULTI_SOURCE_VDB));
	
			infoArray = (AdvisorActionInfo[])infoList.toArray(new AdvisorActionInfo[infoList.size()]);
			actionInfoMap.put(TESTING, infoArray);
		}
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
