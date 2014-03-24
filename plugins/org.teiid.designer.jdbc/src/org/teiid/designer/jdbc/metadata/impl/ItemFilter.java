/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.jdbc.metadata.impl;

/**
 *  This class is used to filter JdbcDatabase items, based on the Connection Profile predicate filter settings.
 *  
 */
public class ItemFilter {

	boolean like = false;
	boolean startsWith = false;
	boolean contains = false;
	boolean endsWith = false;
	String searchText;
	
	/**
	 * Constructor
	 * @param filterStr the predicate filter string
	 */
	public ItemFilter(String filterStr) {
		String fText = ""; //$NON-NLS-1$
		if(filterStr.toUpperCase().startsWith("LIKE")) {  //$NON-NLS-1$
			like = true;
			fText = filterStr.substring("LIKE".length()).trim();  //$NON-NLS-1$
		} else {
			like = false;
			fText = filterStr.substring("NOT LIKE".length()).trim();  //$NON-NLS-1$
		}
		// Remove leading and trailing ticks
		String rText = removeLeadingTrailingTicks(fText);
		
		// Determines leading-trailing pcts and returns search text
		searchText = setLeadingTrailingFlags(rText);		
	}
	
	private String removeLeadingTrailingTicks(String str) {
		String result = null;
		if(str.startsWith("'")) {  //$NON-NLS-1$
			result = str.substring(1);
		}
		if(result.endsWith("'")) {  //$NON-NLS-1$
			result = result.substring(0, result.length()-1);
		}
		return result;
	}
	
	private String setLeadingTrailingFlags(String str) {
		boolean leadingPct = str.startsWith("%");  //$NON-NLS-1$
		if(leadingPct) {
			str = str.substring(1);
		}
		boolean trailingPct = str.endsWith("%");  //$NON-NLS-1$
		if(trailingPct) {
			str = str.substring(0, str.length()-1);
		}
		
		if(leadingPct && trailingPct) {
			contains = true;
		} else if(leadingPct) {
			endsWith = true;
		} else if(trailingPct) {
			startsWith = true;
		}
		return str;
	}
	
	/**
	 * Determines if the suppled itemName is a match, based on the filter string
	 * @param itemName the item name being tested
	 * @return 'true' if supplied name is a match, 'false' otherwise
	 */
	public boolean isMatch(String itemName) {
		if(startsWith) {
			if( like && itemName.startsWith(searchText) ) {
				return true;
			} else if( !like && !itemName.startsWith(searchText) ) {
				return true;
			}
		} else if(endsWith) {
			if( like && itemName.endsWith(searchText) ) {
				return true;
			} else if( !like && !itemName.endsWith(searchText) ) {
				return true;
			}
		} else if(contains) {
			if( like && itemName.contains(searchText) ) {
				return true;
			} else if( !like && !itemName.startsWith(searchText) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the filter description
	 * @return the filter description 
	 */
	public String getFilterDescription() {
		String description = null;
		if(startsWith) {
			if( like ) {
				description = "[Starts with '"+searchText+"']";  //$NON-NLS-1$  //$NON-NLS-2$
			} else {
				description = "[Does not start with '"+searchText+"']";  //$NON-NLS-1$  //$NON-NLS-2$
			}
		} else if(endsWith) {
			if( like ) {
				description = "[Ends with '"+searchText+"']";  //$NON-NLS-1$  //$NON-NLS-2$
			} else {
				description = "[Does not end with '"+searchText+"']";  //$NON-NLS-1$  //$NON-NLS-2$
			}
		} else if(contains) {
			if( like ) {
				description = "[Contains '"+searchText+"']";  //$NON-NLS-1$  //$NON-NLS-2$
			} else {
				description = "[Does not contain '"+searchText+"']";  //$NON-NLS-1$  //$NON-NLS-2$
			}
		}
		return description;
	}
	
}
