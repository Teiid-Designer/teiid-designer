/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.transformation.ui.textimport;


/** 
 * @since 4.2
 */
public class VirtualTableRowObject {
    private String row;
    private String name;
    private String description;
    private String selectSql;
    private static char COMMA = ',';
    private static char DQUOTE = '"';
    private static char ESCAPE = '\\';
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
    
    public VirtualTableRowObject(String name, String description, String selectSql) {
    	super();
    	this.name = name;
    	this.description = description;
    	this.selectSql = selectSql;
    	if( this.name != null && this.name.length() > 0 && this.selectSql != null && this.selectSql.length() > 0 ) {
    		this.valid = true;
    	}
    }
    
    
    private void parseRow() {
        // Extract the table name
    	try {
	        if( row != null && row.length() > 8 ) {
	            int nextCommaIndex = row.indexOf(COMMA);
	            int firstdQuoteIndex = row.indexOf(DQUOTE);
	            
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
	                int dQuote2 = ss1.indexOf(DQUOTE);
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
	            nextCommaIndex = restOfRow1.indexOf(COMMA);
	            firstdQuoteIndex = getIndexOfFirstNonExcapedDoubleQuote(restOfRow1);
	            
	            if( firstdQuoteIndex > -1 && (firstdQuoteIndex < nextCommaIndex || nextCommaIndex == -1 ) ) {
	                int sqlBegin = firstdQuoteIndex +1;
	                String restOfRow2 = restOfRow1.substring(sqlBegin);
	                int seconddQuoteIndex = getIndexOfFirstNonExcapedDoubleQuote(restOfRow2);
	                int sqlEnd = seconddQuoteIndex;
	                String restOfRow3 = restOfRow1.substring(seconddQuoteIndex + 1);
	                nextCommaIndex = restOfRow3.indexOf(COMMA);
	                
	                
	                selectSql = restOfRow2.substring(0, sqlEnd).trim();
	                // We need to remove all escape character for all escaped double quotes
	                
	                selectSql = removeEscapeCharsFromSQL(selectSql);
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
    
    private int getIndexOfFirstNonExcapedDoubleQuote(String str) {
    	int index = 0;
    	char[] charArray = str.toCharArray();
    	for( char theChar : charArray ) {
    		if( index > 0 ) {
    			if( theChar == DQUOTE && charArray[index-1] != ESCAPE) {
    				return index;
    			}
    		}
    		index++;	
    	}
    	return -1;
    }
    
    private String removeEscapeCharsFromSQL(String str) {
    	StringBuffer sb = new StringBuffer(str.length());
    	int index = 0;
    	char[] charArray = str.toCharArray();
    	for( char theChar : charArray ) {
    		if( index < charArray.length ) {
    			if( index == charArray.length-1 || (theChar != ESCAPE && charArray[index+1] != DQUOTE) ) {
    				sb.append(theChar);
    			}
    		}
    		index++;
    	}
    	
    	return sb.toString();
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
            if( i < length-2 && subString.charAt(i) == (DQUOTE) ) {
                if(removedFirstDQuote) {
                    if( subString.charAt(i+1) == (DQUOTE))
                        addChar = false;
                    else
                        addChar = true;
                } else {
                    // skip the first dQuote
                    removedFirstDQuote = true;
                }
            } else if( subString.charAt(i) == (DQUOTE) && i == length-1) {
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
