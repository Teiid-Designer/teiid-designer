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

package com.metamatrix.modeler.internal.relationship.ui.textimport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.metamatrix.core.util.EquivalenceUtil;


/** 
 * @since 4.2
 */
public class RelationshipTableRowObject {
    private String row;
    private String name;
    private String description;
    private String relType;
    private List sourceObjects;
    private List targetObjects;
    private String location;
    private static final char COMMA = ',';
    private static final char DQUOTE = '"';
    private static final char VBAR = '|';
    private boolean valid = false;
    /** 
     * 
     * @since 4.2
     */
    public RelationshipTableRowObject(String row) {
        super();
        this.row = row;
        parseRow();
    }
    
    public RelationshipTableRowObject(String name, String description, String type, List strSources, List strTargets, String location) {
    	this.name = name;
    	this.description = description;
    	this.relType = type;
    	this.sourceObjects = new ArrayList(strSources);
    	this.targetObjects = new ArrayList(strTargets);
    	this.location = location;
    }
    
    private List parseIntoSegments() {
        List segments = new ArrayList(5);
        // Extract the table name
    	// NAME, TYPE, SOURCE_OBJ, TARGET_OBJ_1 | TARGET_OBJ_2 | TARGET_OBJ_3, "DESCRIPTION", Project\RelationshipModel\FolderWIthinRelationship
    	try {
    		String seg = null;
    		String restOfRow = null;
    		boolean lastSegment = false;
	        if( row != null && row.length() > 8 ) {
	        	int commaIndex = row.indexOf(COMMA); // Name should not have double quotes
	        	seg = row.substring(0, commaIndex).trim();
	        	segments.add(seg);
	        	
	        	restOfRow = row.substring(commaIndex + 1);
	        	commaIndex = restOfRow.indexOf(COMMA); // Type should not have double quotes
	        	seg = restOfRow.substring(0, commaIndex).trim();
	        	segments.add(seg);
	        	
	        	restOfRow = restOfRow.substring(commaIndex + 1);
	        	commaIndex = restOfRow.indexOf(COMMA); // Source Object should not have double quotes
	        	seg = restOfRow.substring(0, commaIndex).trim();
	        	segments.add(seg);
	        	
	        	restOfRow = restOfRow.substring(commaIndex + 1);
	        	commaIndex = restOfRow.indexOf(COMMA); // Target Objects should not have double quotes
	        	if( commaIndex == -1 )
	        		lastSegment = true;
	        	if( lastSegment ) {
	        		seg = restOfRow;
	        		segments.add(seg.trim());
	        		return segments;
	        	}
	        	
	        	seg = restOfRow.substring(0, commaIndex).trim();
	        	segments.add(seg);
	        	
	        	
	        	// NOTE: USER NOT REQUIRED TO HAVE DESCRIPTION OR FOLDER
	        	int indexOfFirstDQuote = restOfRow.indexOf(DQUOTE);
	        	boolean hasDQuotes = indexOfFirstDQuote > -1;
	        	if( !hasDQuotes) {
//		        	restOfRow = restOfRow.substring(commaIndex + 1);
		        	commaIndex = restOfRow.indexOf(COMMA); // Description has double quotes
		        	if( commaIndex == -1 ) // 
		        		lastSegment = true;
		        	if( lastSegment ) {
		        		seg = restOfRow;
		        		segments.add(seg);
		        		return segments;
		        	}
		        	
		        	seg = restOfRow.substring(0, commaIndex).trim();
		        	segments.add(seg);
		        	
	        	} else {
	        		// We need to find the index of last DQuote
	        		int indexOfLastDQuote = restOfRow.lastIndexOf(DQUOTE);
	        		if( indexOfLastDQuote > -1 && indexOfLastDQuote > indexOfFirstDQuote ) {
	        			seg = restOfRow.substring(indexOfFirstDQuote, indexOfLastDQuote).trim();
	        			segments.add(seg);
	        			restOfRow = restOfRow.substring(indexOfLastDQuote + 1);
	        			commaIndex = restOfRow.indexOf(COMMA);
	        			if( commaIndex == -1 )
	        				return segments;
	        		}
	        	}
	        	
	        	// WE HAVE A LOCATION 
	        	seg = restOfRow.substring(commaIndex + 1).trim();
	        	if( seg.length() > 1 )
	        		segments.add(seg);
	        }
    	} catch (Exception ex ) {
    		valid = false;
    	}
    	
    	return segments;
    }
    
    private void parseRow() {
        // Extract the table name
    	// NAME, TYPE, SOURCE_OBJ, TARGET_OBJ_1 | TARGET_OBJ_2 | TARGET_OBJ_3, "DESCRIPTION", Project\RelationshipModel\FolderWIthinRelationship
    	try {
	        if( row != null && row.length() > 8 ) {
	            List segments = parseIntoSegments();
	        	int nSegs = segments.size();
	            if( nSegs > 4 ) {
	            	valid = true;
	            	name = (String)segments.get(0);
	            	relType = (String)segments.get(1);
	            	sourceObjects = parseSubStringForObjects((String)segments.get(2));
	            	targetObjects = parseSubStringForObjects((String)segments.get(3));
	            	if( nSegs > 4 && ((String)segments.get(4)) != null ) {
	            		description = parseDescription((String)segments.get(4));
	            	}
	            	if( nSegs > 5 && ((String)segments.get(5)) != null ) {
	            		location = (String)segments.get(5);
	            	}
	            }
	        }
    	} catch (Exception ex ) {
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
//        buffer.append("Table Row Object")             //$NON-NLS-1$
        buffer.append("Name = " + name )               //$NON-NLS-1$
            .append(" Type = " + relType) //$NON-NLS-1$
//            .append("    Source Role Object = " + sourceRole) //$NON-NLS-1$
//            .append("    Target Role Object = " + targetRole) //$NON-NLS-1$
            .append(" Location = " + location); //$NON-NLS-1$
        if( description != null )
        	buffer.append(" Description = " + description);   //$NON-NLS-1$
        
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
    public String getRelType() {
        return this.relType;
    }
    public List getSourceRoles() {
        return this.sourceObjects;
    }
    public List getTargetRoles() {
        return this.targetObjects;
    }
    
    /**
     * test whether the supplied object is equal to this object
     * @param obj the supplied object
     * @return true if the objects are equal, false if not.
     */
    @Override
    public boolean equals(Object obj) {
        // Quick same object test
        if(this == obj) {
            return true;
        }

        // Quick fail tests
        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        
        RelationshipTableRowObject other = (RelationshipTableRowObject) obj;

        // Check whether all fields are equal
        return EquivalenceUtil.areEqual(getName(),other.getName()) && 
               EquivalenceUtil.areEqual(getDescription(),other.getDescription()) &&
               referencesEqual(obj);
    }
    
    /**
     * test whether 'everything other than name and description' are equal.  This allows test
     * of objects that are named differently to see if it contains source and target references
     * to the same object(s)
     * @param obj the supplied object
     * @return true if the objects are equal, false if not.
     */
    public boolean referencesEqual(Object obj) {
        // Quick same object test
        if(this == obj) {
            return true;
        }

        // Quick fail tests
        if(obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        
        RelationshipTableRowObject other = (RelationshipTableRowObject) obj;

        // Test everything except name and description
        return EquivalenceUtil.areEqual(getRelType(),other.getRelType()) &&
           EquivalenceUtil.areEquivalent(getSourceRoles().toArray(),other.getSourceRoles().toArray()) &&
           EquivalenceUtil.areEquivalent(getTargetRoles().toArray(),other.getTargetRoles().toArray()) &&
           EquivalenceUtil.areEqual(getLocation(),other.getLocation()) ;
    }
    
    // Method to parse a substring and create a list of source or target objects (path based objects)
    // EXAMPLE :  "Project_1/PModels/P1/T3 | Project_1/PModels/P1/T4 | Project_1/PModels/P1/T5"
    private List parseSubStringForObjects(String segment) {
    	List foundObjects = new ArrayList();
        if( segment != null && segment.length() > 0 ) {
        	boolean endOfString = false;
        	String tempObjStr = null;
        	String restOfRow = segment;
            while( !endOfString ) {
	            int vbarIndex = restOfRow.indexOf(VBAR);
	            if( vbarIndex == -1 ) {
	            	// Last string
	            	tempObjStr = restOfRow.trim();
	            	foundObjects.add(tempObjStr);
	            	endOfString = true;
	            } else {
	            	tempObjStr = restOfRow.substring(0, vbarIndex).trim();
	            	restOfRow = restOfRow.substring(vbarIndex + 1);
	            	foundObjects.add(tempObjStr);
	            }
            }
        }
        if( foundObjects.isEmpty() )
        	return Collections.EMPTY_LIST;
        
    	return foundObjects;
    }

	public String getLocation() {
		return location;
	}
}
