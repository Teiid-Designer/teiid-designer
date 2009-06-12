/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.xsd.ui.textimport;

import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractRowObject;



/** 
 * @since 4.2
 */
public class DatatypeAtomicRowObject extends AbstractRowObject {
    /* SAMPLE DATA
    #TYPE,TYPENAME,DESCRIPTION,BASETYPE,LENGTH,MINLENGTH,MAXLENGTH,MINBOUND,MININCLUSIVE,MAXBOUND,MAXINCLUSIVE,TOTALDIGITS,FRACTIONDIGITS
    */
    
    private String baseType;
    private int length;
    private int minLength;
    private int maxLength;
    private int minBound;
    private int minInclusive;
    private int maxBound;
    private int maxInclusive;
    private int totalDigits;
    private int fractionDigits;
    private boolean valid;
    
    /** 
     * @param row
     * @since 4.2
     */
    public DatatypeAtomicRowObject(String row) {
        super(row);
        setObjectType(DatatypeRowFactory.TYPE);
    }

    /** 
     * @see com.metamatrix.modeler.internal.relational.ui.relationaltable.AbstractRowObject#parseRow()
     * @since 4.2
     */
    @Override
    public void parseRow() {
        String rowString = getDataString();
        // Extract the table name
        valid = false;
        // Extract the table name
        if( rowString != null && rowString.length() > 0 ) {
        	try {
	            //=================================
	            // TYPENAME
	            //=================================
	            String segment = getSegmentBeforeDelim(rowString,COMMA);
	            String restOfRow = getSegmentAfterDelim(rowString,COMMA);
	            if(segment!=null) {
	                setName(segment);
	            }
	            
	            //=================================
	            // DESCRIPTION
	            //=================================
	            segment = getDescriptionSegment(restOfRow,DQUOTE);
	            restOfRow = getSegmentAfterDescription(restOfRow,DQUOTE);
	            
	            if(segment!=null) {
	                setDescription(segment);
	            }
	            
	            //=================================
	            // BASETYPE
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setBaseType(segment);
	            }
	            
	            //=================================
	            // LENGTH
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setLength(getInt(segment));
	            }
	            
	            //=================================
	            // MINLENGTH
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setMinLength(getInt(segment));
	            }
	            
	            //=================================
	            // MAXLENGTH
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setMaxLength(getInt(segment));
	            }
	            
	            //=================================
	            // MINBOUND
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setMinBound(getInt(segment));
	            }
	            
	            //=================================
	            // MININCLUSIVE
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setMinInclusive(getInt(segment));
	            }
	            
	            //=================================
	            // MAXBOUND
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setMaxBound(getInt(segment));
	            }
	            
	            //=================================
	            // MAXINCLUSIVE
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setMaxInclusive(getInt(segment));
	            }
	            
	            //=================================
	            // TOTALDIGITS
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setTotalDigits(getInt(segment));
	            }
	            
	            //=================================
	            // FRACTIONDIGITS
	            //=================================
	            segment = getSegmentBeforeDelim(restOfRow,COMMA);
	            restOfRow = getSegmentAfterDelim(restOfRow,COMMA);
	            if(segment!=null) {
	                setFractionDigits(getInt(segment));
	            }
	            valid = true;
        	} catch (Exception ex) {
        		valid = false;
        	}
        }
        
    }
    
    public String getSegmentBeforeDelim(String str, char delim) {
        String segment = null;
        int index = str.indexOf(delim); 
        
        if(index!=-1) {
            segment = str.substring(0, index).trim();
        } else {
            segment = str;
        }
        
        if( segment != null && segment.length() > 0 ) {
            return segment;
        }
        return null;
    }
    
    public String getSegmentAfterDelim(String str, char delim) {
        String restStr = null;
        int index = str.indexOf(delim);
        if(index!=-1 && str.length()>index) {
            restStr = str.substring(index+1).trim();
        }
        return restStr;
    }
    
    public String getDescriptionSegment(String str, char delim) {
        String segment = null;
        int firstIndx = str.indexOf(delim);
        int lastIndx = str.lastIndexOf(delim); 
        
        segment = str.substring(firstIndx+1, lastIndx).trim();
        if( segment != null && segment.length() > 0 ) {
            return segment;
        }
        return null;
    }
    
    public String getSegmentAfterDescription(String str, char delim) {
        String restStr = null;
        int index = str.lastIndexOf(delim); 
        if(str.length()>index) {
            restStr = str.substring(index+1).trim();
        }
        // Trim leading COMMA from the remaining string
        index = restStr.indexOf(COMMA);
        if(restStr.length()>index) {
            restStr = restStr.substring(index+1).trim();
        }
        return restStr;
    }
    
    public void setBaseType(String baseTypeVal) {
        this.baseType = baseTypeVal;
    }
    
    public String getBaseType() {
        return this.baseType;
    }
    
    public void setLength(int lengthVal) {
        this.length = lengthVal;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public void setMinLength(int minLengthVal) {
        this.minLength = minLengthVal;
    }
    
    public int getMinLength() {
        return this.minLength;
    }
    
    public void setMaxLength(int maxLengthVal) {
        this.maxLength = maxLengthVal;
    }
    
    public int getMaxLength() {
        return this.maxLength;
    }
    
    public void setMinBound(int minBoundVal) {
        this.minBound = minBoundVal;
    }

    public int getMinBound() {
        return this.minBound;
    }
    
    public void setMinInclusive(int minInclusiveVal) {
        this.minInclusive = minInclusiveVal;
    }
    
    public int getMinInclusive() {
        return this.minInclusive;
    }
    
    public void setMaxBound(int maxBoundVal) {
        this.maxBound = maxBoundVal;
    }
    
    public int getMaxBound() {
        return this.maxBound;
    }
    
    public void setMaxInclusive(int maxInclusiveVal) {
        this.maxInclusive = maxInclusiveVal;
    }
    
    public int getMaxInclusive() {
        return this.maxInclusive;
    }
    
    public void setTotalDigits(int totalDigitsVal) {
        this.totalDigits = totalDigitsVal;
    }
    
    public int getTotalDigits() {
        return this.totalDigits;
    }
    
    public void setFractionDigits(int fractionDigitsVal) {
        this.fractionDigits = fractionDigitsVal;
    }
        
    public int getFractionDigits() {
        return this.fractionDigits;
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
}
