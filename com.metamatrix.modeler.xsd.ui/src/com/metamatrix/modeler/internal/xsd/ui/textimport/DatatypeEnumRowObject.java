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

package com.metamatrix.modeler.internal.xsd.ui.textimport;

import com.metamatrix.modeler.tools.textimport.ui.wizards.AbstractRowObject;


/** 
 * @since 4.2
 */
public class DatatypeEnumRowObject extends AbstractRowObject {
    /* SAMPLE DATA
    #ENUM, value, description
    */
    private boolean valid;
    
    /** 
     * @param row
     * @since 4.2
     */
    public DatatypeEnumRowObject(String row) {
        super(row);
        setObjectType(DatatypeRowFactory.ENUM);
    }

    /** 
     * @see com.metamatrix.modeler.internal.textimport.ui.wizards.AbstractRowObject#parseRow()
     * @since 4.2
     */
    @Override
    public void parseRow() {
        // This copied from DatatypeAtomicRowObject (customize for ENUM)
        //------------------------------------------------------------------------
        String rowString = getDataString();
        valid = false;
        
        // Extract the data
        if( rowString != null && rowString.length() > 0 ) {
        	try {
	            //=================================
	            // NAME
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
	            valid = true;
        	} catch (Exception ex) {
        		valid = false;
        	}
        } // endif -- row has data
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

	@Override
    public boolean isValid() {
		return valid;
	}
    
//    public void setBaseType(String baseTypeVal) {
//        this.baseType = baseTypeVal;
//    }
//    
//    public String getBaseType() {
//        return this.baseType;
//    }
//    
//    public void setLength(int lengthVal) {
//        this.length = lengthVal;
//    }
//    
//    public int getLength() {
//        return this.length;
//    }
//    
//    public void setMinLength(int minLengthVal) {
//        this.minLength = minLengthVal;
//    }
//    
//    public int getMinLength() {
//        return this.minLength;
//    }
//    
//    public void setMaxLength(int maxLengthVal) {
//        this.maxLength = maxLengthVal;
//    }
//    
//    public int getMaxLength() {
//        return this.maxLength;
//    }
//    
//    public void setMinBound(int minBoundVal) {
//        this.minBound = minBoundVal;
//    }
//
//    public int getMinBound() {
//        return this.minBound;
//    }
//    
//    public void setMinInclusive(int minInclusiveVal) {
//        this.minInclusive = minInclusiveVal;
//    }
//    
//    public int getMinInclusive() {
//        return this.minInclusive;
//    }
//    
//    public void setMaxBound(int maxBoundVal) {
//        this.maxBound = maxBoundVal;
//    }
//    
//    public int getMaxBound() {
//        return this.maxBound;
//    }
//    
//    public void setMaxInclusive(int maxInclusiveVal) {
//        this.maxInclusive = maxInclusiveVal;
//    }
//    
//    public int getMaxInclusive() {
//        return this.maxInclusive;
//    }
//    
//    public void setTotalDigits(int totalDigitsVal) {
//        this.totalDigits = totalDigitsVal;
//    }
//    
//    public int getTotalDigits() {
//        return this.totalDigits;
//    }
//    
//    public void setFractionDigits(int fractionDigitsVal) {
//        this.fractionDigits = fractionDigitsVal;
//    }
//        
//    public int getFractionDigits() {
//        return this.fractionDigits;
//    }
//    
//    private int getInt(String segment) {
//        int value = 0;
//        if( segment != null && segment.length() > 0 ) {
//            try {
//                // Convert to integer?
//                value = Integer.parseInt(segment);
//            } catch (NumberFormatException err) {
//
//            } finally {
//                if( value < 0 )
//                    value = value*(-1);
//            }
//        }
//        
//        return value;
//    }
}
