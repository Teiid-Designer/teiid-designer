/*
 * JBoss, Home of Professional Open Source.
*
* See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
*
* See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
*/
package com.metamatrix.modeler.ui.viewsupport;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.ui.cheatsheets.CheatSheetListener;
import org.eclipse.ui.cheatsheets.ICheatSheetEvent;

import com.metamatrix.core.util.CoreArgCheck;

/**
 * Simple class to track a map of Properties
 * 
 * Initial implementation was for managing the cheat sheet properties state where the "id" is the 
 * cheat sheet id.
 * 
 */
public class PropertiesContextManager extends CheatSheetListener {

	Map<String, Properties> cachedPropertiesMap;
	
	public PropertiesContextManager() {
		super();
		cachedPropertiesMap = new HashMap<String, Properties>();
	}
	/**
	 * Adds (or replaces) a property to the Properties for the input id
	 * 
	 * @param id
	 * @param key
	 * @param value
	 */
	public void addProperty(String id, String key, String value) {
		CoreArgCheck.isNotEmpty(id, "id"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(key, "key"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(value, "value"); //$NON-NLS-1$
		Properties sheetProperties = getProperties(id);
		sheetProperties.put(key, value);
	}
	
	/**
	 * Returns a property value for a given Properties id and specific key value
	 * 
	 * @param id
	 * @param key
	 * @return string property. May return null
	 */
	public String getProperty(String id, String key) {
		CoreArgCheck.isNotEmpty(id, "id"); //$NON-NLS-1$
		CoreArgCheck.isNotEmpty(key, "key"); //$NON-NLS-1$
		Properties sheetProperties = cachedPropertiesMap.get(id);
		if( sheetProperties != null ) {
			return (String)sheetProperties.get(key);
		}
		return null;
	}
	
	/**
	 * Returns a Properties object
	 * 
	 * @param id
	 * @return properties
	 */
	public Properties getProperties(String id) {
		CoreArgCheck.isNotEmpty(id, "id"); //$NON-NLS-1$
		Properties sheetProperties = cachedPropertiesMap.get(id);
		if( sheetProperties == null ) {
			sheetProperties = new DesignerProperties(id);
			cachedPropertiesMap.put(id, sheetProperties);
		}
		return sheetProperties;
	}
	
	@Override
	public void cheatSheetEvent(ICheatSheetEvent event) {
		if( event.getEventType() == ICheatSheetEvent.CHEATSHEET_RESTARTED) {
			Properties sheetProperties = cachedPropertiesMap.get(event.getCheatSheetID());
			if( sheetProperties != null ) {
				sheetProperties.clear();
			}
		}
		
	}
	
	
}
