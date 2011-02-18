/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relational.ui.textimport;

import org.eclipse.emf.ecore.EObject;
import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractRowObject;



/** 
 * @since 4.2
 */
public class ColumnRowObject extends AbstractRowObject {
    /* SAMPLE DATA
    COMMENT,    NAME,       DATATYPE,   LENGTH, DESCRIPTION
    COLUMN,     PROJECT_ID, NUMBER,     -15,    The system-generated number that uniquely identifies the project
    */
    
    private EObject datatype;
    private int length = 0;
    private int precision = 0;
    private int scale = 0;
    private boolean valid;
    
    /** 
     * @param row
     * @since 4.2
     */
    public ColumnRowObject(String row) {
        super(row);
        setObjectType(RelationalRowFactory.COLUMN);
    }

    /** 
     * @see com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractRowObject#parseRow()
     * @since 4.2
     */
    @Override
    public void parseRow() {
        String rowString = getDataString();
        valid = false;
        // Extract the column name
        boolean usedAllSegments = false;
        if( rowString != null && rowString.length() > 8 ) {
        	try {
	            String segment = null;
	            //-------------------------------
	            // NAME
	            //-------------------------------
	            int index = rowString.indexOf(COMMA); // Name should not have double quotes
	            segment = rowString.substring(0, index).trim();
	            if( segment != null && segment.length() > 0 )
	                setName(segment);
	            
	            if( rowString.length() > index)
	                index++;
	            
	            // rest of Row following name
	            String restOfRow = rowString.substring(index);
	            
	            //-------------------------------
	            // DATATYPE
	            //-------------------------------
	            int nextCommaIndex = restOfRow.indexOf(COMMA);
	            
	            if( nextCommaIndex == -1 || nextCommaIndex >= restOfRow.length() )
	                return;
	            
	            // Datatype segment
	            segment = restOfRow.substring(0, nextCommaIndex).trim();
	            
	            // Check to see if the segement contains LeftParenth and Right Parenth
	            // If one and not the other, find index for Right and create segment for the whole thing
	            int leftParenthIndex = segment.indexOf(LEFT_PARENTH);
	            int rightParenthIndex = segment.indexOf(RIGHT_PARENTH);
	            if( leftParenthIndex == -1 && rightParenthIndex == -1 ) {
	                // ALL OK
	            } else if( leftParenthIndex != -1 && rightParenthIndex != -1) {
	                // ALL OK here
	            } else if( leftParenthIndex != -1 ) {
	                // Missing right parenth, so let's set the segment differently
	                rightParenthIndex = restOfRow.indexOf(RIGHT_PARENTH);
	                segment = restOfRow.substring(0, rightParenthIndex +1).trim();
	                int thisLength = restOfRow.length();
	                restOfRow = restOfRow.substring(rightParenthIndex + 1, thisLength);
	                nextCommaIndex = restOfRow.indexOf(COMMA);
	                thisLength = restOfRow.length();
	                restOfRow = restOfRow.substring(nextCommaIndex + 1, thisLength);
	            }
	            String dtString = null;
	            if( segment != null && segment.length() > 0 ) {
	                dtString = segment;
	            }
	            if( dtString != null ) {
	                leftParenthIndex = dtString.indexOf(LEFT_PARENTH);
	                if( leftParenthIndex != -1) {
	                    rightParenthIndex = dtString.indexOf(RIGHT_PARENTH);
	                    // We've got to parse this string (i.e. VARCHAR(30), or NUMBER(5,2)
	                    int commaIndex = dtString.indexOf(COMMA);
	                    if( commaIndex != -1 ) {
	                        String typeString = dtString.substring(0, leftParenthIndex);
	                        String precisionString = dtString.substring(leftParenthIndex+1, commaIndex);
	                        String scaleString = dtString.substring(commaIndex+1, rightParenthIndex);
	                        precision = getInt(precisionString);
	                        scale = getInt(scaleString);
	                        length = scale + precision + 2;
	                        findDatatype(typeString);
	                    } else {
	                        String typeString = dtString.substring(0, leftParenthIndex);
	                        String lengthString = dtString.substring(leftParenthIndex+1, rightParenthIndex);
	                        length = getInt(lengthString);
	                        findDatatype(typeString);
	                    }
	                    
	                   
	                } else {
	    	            //-------------------------------
	                    // LENGTH
	    	            //-------------------------------
	                    // Get the rest of the row....
	                    restOfRow = restOfRow.substring(nextCommaIndex+1);
	                    nextCommaIndex = restOfRow.indexOf(COMMA);
	                    
	                    if( nextCommaIndex == -1 ) {
		                    segment = restOfRow.trim();
		                    length = getInt(segment);
		                    precision = length;
		                    scale = 0;
		                    findDatatype(dtString);
		                    usedAllSegments = true;
	                    } else {
		                    segment = restOfRow.substring(0, nextCommaIndex).trim();
		                    length = getInt(segment);
		                    precision = length;
		                    scale = 0;
		                    findDatatype(dtString);
		                    
		                    restOfRow = restOfRow.substring(nextCommaIndex+1);
		                    nextCommaIndex = restOfRow.indexOf(COMMA);
	                    }
	                }
	            }
	            if( !usedAllSegments ) {
		            //-------------------------------
		            // DESCRIPTION
		            //-------------------------------		            
		            String someDescription = parseDescription(restOfRow.trim());
		            if( someDescription != null ) {
		                setDescription(someDescription);
		            }
	            }
	            valid = true;
        	} catch (Exception ex) {
        		// Probably a string index OOB exception, but basically we don't want to impede the import process with
        		// one bad row, so we just say this row is invalid.
        		valid = false;
        	}
        }
        
    }
    
    private void findDatatype(String someType) {
        if( someType != null )
            datatype = RelationalRowFactory.getDataType(someType, getLength(), precision, scale);
    }

    public EObject getDatatype() {
        return this.datatype;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getPrecision() {
        return this.precision;
    }
    
    public int getScale() {
        return this.scale;
    }

    private int getInt(String segment) {
        int value = 0;
        if( segment != null && segment.length() > 0 ) {
            try {
                // Convert to integer?
                value = Integer.parseInt(segment);
            } catch (NumberFormatException err) {

            } finally {
                if( value < 0 )
                    value = value*(-1);
            }
        }
        
        return value;
    }

	@Override
    public boolean isValid() {
		return valid;
	}
	
    /** 
     * Parse the description from the supplied string.  This method will find the first double-quote
     * and last double-quote in the supplied string.  The description will be everything between the
     * first and last double quotes.
     * @param string the supplied string
     * @return the parsed description string, null if not found
     */
    @Override
    protected String parseDescription(String string) {
        // Find the first dQuote and last dQuote.  Use what is between them for the description.
        int firstDQuoteIndex = string.indexOf(DQUOTE);
        int lastDQuoteIndex = string.lastIndexOf(DQUOTE);
        if(firstDQuoteIndex>=0 && lastDQuoteIndex>firstDQuoteIndex) {
            String description = string.substring(firstDQuoteIndex+1,lastDQuoteIndex);
            if(description!=null && description.length()>0) {
            	return description;
            }
        } 
        return null;
    }
    
}
