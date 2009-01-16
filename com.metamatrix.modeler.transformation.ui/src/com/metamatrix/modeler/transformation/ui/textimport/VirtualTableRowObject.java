/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */ 

package com.metamatrix.modeler.transformation.ui.textimport;


/** 
 * @since 4.2
 */
public class VirtualTableRowObject {
    private String row;
    private String name;
    private String description;
    private String selectSql;
    private char comma = ',';
    private char dQuote = '"';
    private boolean valid = false;
    /** 
     * 
     * @since 4.2
     */
    public VirtualTableRowObject(String row) {
        super();
        this.row = row;
        parseRow();
    }
    
    
    private void parseRow() {
        // Extract the table name
    	try {
	        if( row != null && row.length() > 8 ) {
	            int nextCommaIndex = row.indexOf(comma);
	            int firstdQuoteIndex = row.indexOf(dQuote);
	            
	            //-----------------------------
	            // VIRTUAL TABLE NAME
	            //-----------------------------
	            if( firstdQuoteIndex > nextCommaIndex ) {
	                name = row.substring(0, nextCommaIndex).trim();
	                valid = true;
	            } else {
	                // We have to parse between the first two dQuotes
	                // Get the substring between first quote, and first comma
	                String ss1 = row.substring(firstdQuoteIndex+1, nextCommaIndex);
	                // Now get the index for the second quote
	                int dQuote2 = ss1.indexOf(dQuote);
	                name = ss1.substring(0, dQuote2).trim();
	                valid = true;
	            }
	
	            if( row.length() > nextCommaIndex)
	                nextCommaIndex++;
	            String restOfRow1 = row.substring(nextCommaIndex);
	            
	            //-----------------------------
	            // SELECT SQL
	            //-----------------------------
	            // Now we look for next comma. if we find a dQuote first, then we look again until
	            // we find the second dQuote and then the comma
	            nextCommaIndex = restOfRow1.indexOf(comma);
	            firstdQuoteIndex = restOfRow1.indexOf(dQuote);
	            if( firstdQuoteIndex > -1 && firstdQuoteIndex < nextCommaIndex ) {
	                int sqlBegin = firstdQuoteIndex +1;
	                String restOfRow2 = restOfRow1.substring(sqlBegin);
	                int seconddQuoteIndex = restOfRow2.indexOf(dQuote);
	                int sqlEnd = seconddQuoteIndex;
	                String restOfRow3 = restOfRow1.substring(seconddQuoteIndex + 1);
	                nextCommaIndex = restOfRow3.indexOf(comma);
	                
	                
	                selectSql = restOfRow2.substring(0, sqlEnd).trim();
		            //-----------------------------
		            // DESCRIPTION
		            //-----------------------------
	                description = parseDescription(restOfRow3.substring(nextCommaIndex+1).trim());
	                valid = true;
	            } else {
	                // ERROR Cannot parse Row
	            }
	            
	        }
    	} catch (Exception ex) {
    		// Swallow any exception here so we can continue with next row
    		valid = false;
    	}
    }
    
    private String parseDescription(String subString) {
        // Remove unneeded double quotes
        // Walk char by char
        StringBuffer buffer = new StringBuffer();
        boolean removedFirstDQuote = false;
        boolean addChar = false;
        int length = subString.length();
        for(int i=0; i<length; i++ ) {
            // check i and i+1 for dquotes
            if( i < length-2 && subString.charAt(i) == (dQuote) ) {
                if(removedFirstDQuote) {
                    if( subString.charAt(i+1) == (dQuote))
                        addChar = false;
                    else
                        addChar = true;
                } else {
                    // skip the first dQuote
                    removedFirstDQuote = true;
                }
            } else if( subString.charAt(i) == (dQuote) && i == length-1) {
                addChar = false; 
            } else {
                addChar = true;
            }
            
            if( addChar )
                buffer.append(subString.charAt(i));
        }
        
        return buffer.toString();
    }
    
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Table Row Object")             //$NON-NLS-1$
            .append("\n    Name = " + name )               //$NON-NLS-1$
            .append("\n    Description = " + description) //$NON-NLS-1$
            .append("\n    Select SQL = " + selectSql);   //$NON-NLS-1$
        
        return buffer.toString();
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public String getDescription() {
        return this.description;
    }
    public String getName() {
        return this.name;
    }
    public String getSelectSql() {
        return this.selectSql;
    }
}
