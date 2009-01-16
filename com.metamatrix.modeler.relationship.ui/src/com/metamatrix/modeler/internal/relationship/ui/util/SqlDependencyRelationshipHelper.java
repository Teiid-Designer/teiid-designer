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

package com.metamatrix.modeler.internal.relationship.ui.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.metamodels.transformation.TransformationMappingRoot;
import com.metamatrix.modeler.core.workspace.ModelResource;
import com.metamatrix.modeler.internal.relationship.ui.textimport.RelationshipTableRowObject;
import com.metamatrix.modeler.internal.transformation.util.TransformationHelper;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelObjectUtilities;
import com.metamatrix.modeler.internal.ui.viewsupport.ModelUtilities;
import com.metamatrix.modeler.transformation.ui.actions.TransformationSourceManager;

/**
 * Class for creation of table and column relationships, given a list of
 * starting target virtual tables
 * 
 */

public class SqlDependencyRelationshipHelper {
    private static final String TO_STRING = "_TO_"; //$NON-NLS-1$
    private static final String DEFAULT_NAME = "NewRelationship"; //$NON-NLS-1$
    private static final String NAME_PREFIX = "REL"; //$NON-NLS-1$
    private static final String DEFAULT_REL_TYPE = "Transformation"; //$NON-NLS-1$
    private static final String BLANK_STR = ""; //$NON-NLS-1$
    public static final int ALL_LEVELS = -1; 
    
	private Collection   	targetTables;
	private Object 		location;
	private String 		locationStr;
	private String			relType = DEFAULT_REL_TYPE;

    private int 			relCount = 0;
    private boolean 		createTableRelationships = true;
    private boolean 		createColumnRelationships = true;
    private boolean 		createAllColumnRelationships = true;
    private boolean 		autoGenerateNames = true;
    private List 			relRowStrings = new ArrayList();
    
    private List	tableDependencies = new ArrayList();
    private int nLevels = ALL_LEVELS;
    private int maxLevels = 0;
    
	/**
	 * Constructor
	 * @param targetTables  List of top-level target virtual tables to generate relationships
	 * @param location  Model, Relationship folder, or Relationship container where the new relationships will be created in.
	 * @param relType the type of relationship to generate
	 * @param createTableRels flag whether to create table relationships
	 * @param createColumnRels flag whether to create column relationships
	 * @param createAllColumnRels flag whether to create all Column Rels or just descendants of targets.
	 * @param autoGenNames flag whether to auto-generate names or use relationship entities to gen names.
	 * @param nLevels number of levels to consider for generation of relationships
	 */
	public SqlDependencyRelationshipHelper(
			Collection targetTables, 
			Object location,
			String relType,
			boolean createTableRels, 
			boolean createColumnRels, 
			boolean createAllColumnRels, 
			boolean autoGenNames,
			int nLevels) {
		
		this.targetTables = targetTables;
		this.location = location;
		this.relType = relType;
		this.createTableRelationships = createTableRels;
		this.createColumnRelationships = createColumnRels;
		this.createAllColumnRelationships = createAllColumnRels;
		this.autoGenerateNames = autoGenNames;
		this.nLevels = nLevels;
		
		// Initialize the table dependency maps
		loadTableDependencies();
	}
	
	/**
	 * Constructor
	 * @param targetTables  List of top-level target virtual tables to generate relationships
	 */
	public SqlDependencyRelationshipHelper(Collection targetTables) {
		
		this.targetTables = targetTables;
		
		// Initialize the table dependency maps
		loadTableDependencies();
	}

	/**
	 * Convenience method to set all options.
	 * @param targetTables  List of top-level target virtual tables to generate relationships
	 */
	public void setOptions(Object location,String relType,boolean createTableRels, 
			                boolean createColumnRels,boolean createAllColumnRels, 
			                boolean autoGenNames,int nLevels) {
		this.location = location;
		setRelationshipLocation();
		this.relType = relType;
		this.createTableRelationships = createTableRels;
		this.createColumnRelationships = createColumnRels;
		this.createAllColumnRelationships = createAllColumnRels;
		this.autoGenerateNames = autoGenNames;
		this.nLevels = nLevels;
        
        createRelationships();
    }
    
	/**
	 * Generate the table dependency maps, based on the current target tables
	 */
    private void loadTableDependencies() { 
    	this.tableDependencies.clear();
    	
    	// Create the first level table Map
    	Map tableMap = new HashMap();
    	Iterator iter = this.targetTables.iterator();
    	while(iter.hasNext()) {
    		EObject vTable = (EObject)iter.next();
    		
        	EObject transformationEObject = getTransformation(vTable);
    		
        	// Get transformation Sources
        	List sources = getSources(transformationEObject);
        	if(sources.size()>0) {
        		tableMap.put(vTable,sources);
        	}
    	}
    	
    	if(tableMap.size()>0) {
    		tableDependencies.add(tableMap);
    	}
    	
    	int currentLevel = 0;
    	boolean lastLevelFound = false;
    	while(!lastLevelFound) {
        	Map nextLevelMap = createNextLevelTableMap(currentLevel);
        	if(nextLevelMap.size()!=0) {
        		tableDependencies.add(nextLevelMap);
        	} else {
        		lastLevelFound=true;
        	}
        	currentLevel++;
    	}
    	
    	// Set the max number of levels
    	this.maxLevels = tableDependencies.size();
    }
    
	/**
	 * ReGenerate the relationships, using the table dependency maps and
	 * the current option settings.
	 */
    private void createRelationships() {
    	this.relRowStrings.clear();
    	this.relCount=0;
    	
    	// Dont even create if the target location is null
    	if(this.locationStr!=null && this.locationStr.trim().length()!=0) {
	        if( createTableRelationships) {
	        	createTableRelationships(nLevels);
	        }
	        
	        if( createColumnRelationships ) {
	        	createColumnRelationships(nLevels);
	        } 
    	}
    	return;
    }
    
	/**
	 * Get the current list of relationships.
	 * @return the list of generated relationships
	 */
    public List getRelationshipRows() {
    	return relRowStrings;
    }

	/**
	 * Create table mappings for the next level following the provided level
	 * @param currentLevel the current level of table mappings 
	 * @return the table mappings for the next level
	 */
    private Map createNextLevelTableMap(int currentLevel) {
    	Map newLevelMap = new HashMap();

    	if(tableDependencies.size()>currentLevel) {
	    	Map tableMap = (Map)tableDependencies.get(currentLevel);
	
	    	
	    	if(tableMap!=null) {
		    	// Sources from previous mappings will be targets for new level
		    	List targets = new ArrayList();
		    	Iterator keyIter = tableMap.keySet().iterator();
		    	while(keyIter.hasNext()) {
		    		List sources = (List)tableMap.get(keyIter.next());
		    		Iterator srcIter = sources.iterator();
		    		while(srcIter.hasNext()) {
		    			EObject srcObj = (EObject)srcIter.next();
		    	        if (TransformationHelper.isValidSqlTransformationTarget(srcObj) && !targets.contains(srcObj)) {
		    				targets.add(srcObj);
		    			}
		    		}
		    	}
		    	
		    	// Iterate provided target virtual groups - add mapping to sources
		    	// Only add mappings if the target is virtual
		    	Iterator targetIter = targets.iterator();
		    	while(targetIter.hasNext()) {
		    		EObject target = (EObject)targetIter.next();
			    	// Get transformation
			    	EObject transformationEObject = getTransformation(target);
			    	
			    	// Get transformation Sources
			    	List sources = getSources(transformationEObject);
			    	
			    	newLevelMap.put(target,sources);
		    	}
	    	}
    	}
    	
    	return newLevelMap;
    }
    
	/**
	 * Set the location where the generated relationships will be placed.  
	 * @param location  Model, Relationship folder, or Relationship container where the new relationships will be created in.
	 */
	public void setLocation(Object location) {
		setRelationshipLocation();
		createRelationships();
	}

	/**
	 * Get the location (string form) where the generated relationships will be placed.  
	 * @return the location where the new relationships will be placed.
	 */
	public String getLocationString() {
		return this.locationStr!=null ? this.locationStr : BLANK_STR;
	}
	
	/**
	 * Set the type of relationships to be generated. 
	 * @param relType the type of relationship to be generated
	 */
	public void setRelationshipType(String relType) {
		this.relType = relType;
		createRelationships();
	}

	/**
	 * Get the type of relationships to be generated.  
	 * @return the relationship type.
	 */
	public String getRelationshipType() {
		return this.relType!=null ? this.relType : BLANK_STR;
	}

	/**
	 * Set the type of relationships to be generated. 
	 * @param relType the type of relationship to be generated
	 */
	public void setAutoGenNames(boolean autoGen) {
		this.autoGenerateNames = autoGen;
		createRelationships();
	}

	/**
	 * Get the autogen flag 
	 * @return the auto gen names flag
	 */
	public boolean getAutoGenNames() {
		return this.autoGenerateNames;
	}

	/**
	 * Set the locationStr for the current relationship location 
	 */
    private void setRelationshipLocation( ) {
        if( location instanceof IFile) {
            // Let's get the model resource and work from there...
        	locationStr = ((IFile)location).getName();
        } else if( location instanceof EObject ) {
            locationStr = getFullPathForEObject((EObject)location);
        } else if( location instanceof ModelResource ) {
        	ModelResource targetResource = (ModelResource)location;
            locationStr = targetResource.getItemName();
        } else if( location instanceof String ) {
        	locationStr = (String)location;
        }
    }
    
    /**
	 * Reset the number of levels to include.  This will regenerate the relationships list. 
	 * @param nLevels the number of transformation levels to include.
	 */
	public void setNLevels(int nLevels) {
		if(nLevels==ALL_LEVELS) {
			this.nLevels=this.maxLevels;
		} else {
			this.nLevels=nLevels;
		}
		createRelationships();
	}
	
    /**
	 * Get the number of levels to include.  
	 * @return the number of levels.
	 */
	public int getNLevels() {
		return this.nLevels;
	}

	/**
	 * Get the maximum number of transformation levels for the current dependency mappings.
	 * @return the max number of transformation levels for the current target tables.
	 */
	public int getMaxLevels() {
		return this.maxLevels;
	}
	
	/**
	 * Reset the 'createTableRelationships' option.  This will regenerate the relationships list. 
	 * @param shouldCreate 'true' to generate table relationships, 'false' to skip.
	 */
	public void setCreateTableRelationships(boolean shouldCreate) {
		if(shouldCreate!=this.createTableRelationships) {
			this.createTableRelationships = shouldCreate;
		}
		createRelationships();
	}
	
	/**
	 * Get the 'createTableRelationships' option.  
	 * return 'true' to generate table relationships, 'false' to skip.
	 */
	public boolean getCreateTableRelationshipsOption() {
		return this.createTableRelationships;
	}

	/**
	 * Reset the 'createColumnRelationships' option.  This will regenerate the relationships list. 
	 * @param shouldCreate 'true' to generate column relationships, 'false' to skip.
	 */
	public void setCreateColumnRelationships(boolean shouldCreate) {
		if(shouldCreate!=this.createColumnRelationships) {
			this.createColumnRelationships = shouldCreate;
		}
		createRelationships();
	}
	
	/**
	 * Get the 'createColumnRelationships' option.  
	 * return 'true' to generate column relationships, 'false' to skip.
	 */
	public boolean getCreateColumnRelationshipsOption() {
		return this.createColumnRelationships;
	}

	/**
	 * Reset the 'createAllColumnRelationships' option.  This will regenerate the relationships list. 
	 * @param shouldCreate 'true' to generate all column relationships, 'false' to generate only the 
	 * column relationships that are descendents from the target table columns.
	 */
	public void setCreateAllColumnRelationships(boolean createAll) {
		if(createAll!=this.createAllColumnRelationships) {
			this.createAllColumnRelationships = createAll;
		}
		createRelationships();
	}

	/**
	 * Get the 'createAllColumnRelationships' option.  
	 * return 'true' to generate all column relationships, 'false' to generate target descendents only.
	 */
	public boolean getCreateAllColumnRelationshipsOption() {
		return this.createAllColumnRelationships;
	}


	/**
	 * Get the transformation mappingRoot for the supplied target group. 
	 * @param targetVirtualGroupEObject the target group
	 * @return the transformation mappingRoot for the supplied target table 
	 */
	private EObject getTransformation(EObject targetVirtualGroupEObject){
        return TransformationHelper.getTransformationMappingRoot(targetVirtualGroupEObject);
    }
    
	/**
	 * Get the sources for the supplied transformation mappingRoot. 
	 * @param transformationEObject the transformation mappingRoot
	 * @return the list of sources for the supplied mappingRoot 
	 */
    private List getSources(EObject transformationEObject) {
        return TransformationSourceManager.getSourceEObjects(transformationEObject);
    }
    
	/**
	 * Generate table-to-table relationships using the table dependency mappings.  
	 * The generated relationships are added to the relRowStrings list.  Relationships
	 * are generated for the specified number of transformation levels from the
	 * target table(s)
	 * @param nLevels the number of transformation levels to generated table relationships from 
	 * the target table(s) 
	 */
    private void createTableRelationships(int nLevels) {
    	if(!createTableRelationships) return;
    	
    	if(nLevels==ALL_LEVELS) {
    		nLevels = this.maxLevels;
    	}
    	// ------------------------------------------
    	// Iterate the number of specified levels
    	// ------------------------------------------
    	for(int i=0; i<nLevels; i++) {
    		Map tableMap = (Map)this.tableDependencies.get(i);
    		Iterator keyIter = tableMap.keySet().iterator();
    		while (keyIter.hasNext()) {
    			// target
    			EObject vTarget = (EObject)keyIter.next();
    			// Sources for this target
    			List sources = (List)tableMap.get(vTarget);
    			
    			if(sources!=null && !sources.isEmpty()) {
	    			EObject firstSource = (EObject)sources.get(0);
	    			
	    			List targetTableList = new ArrayList(1);
	    			targetTableList.add(getFullPathForEObject(vTarget));
	    	        RelationshipTableRowObject rowObj = new RelationshipTableRowObject(generateName(firstSource, vTarget), null, relType, targetTableList, getPathStrings(sources), this.locationStr );
	    	    	relRowStrings.add(rowObj); 
    			}
    		}
    	}
    	
    }
	
	/**
	 * Generate column-to-column relationships using the table dependency mappings.  
	 * The generated relationships are added to the relRowStrings list.  Relationships
	 * are generated for the specified number of transformation levels from the
	 * target table(s)
	 * @param nLevels the number of transformation levels to generated column relationships from 
	 * the target table(s) 
	 */
    private void createColumnRelationships(int nLevels) {
    	if(!createColumnRelationships) return;
    	
    	if(nLevels==ALL_LEVELS) {
    		nLevels = this.maxLevels;
    	}
    	// If generating relationships for all source columns, iterate the list of target tables,
    	// relationships created for all target attributes regardless of participation in top level transformation
    	if(this.createAllColumnRelationships) {
        	for(int i=0; i<nLevels; i++) {
        		Map tableMap = (Map)this.tableDependencies.get(i);
        		Iterator keyIter = tableMap.keySet().iterator();
        		while (keyIter.hasNext()) {
        			// target
        			EObject vTarget = (EObject)keyIter.next();
        			
        			// Sources for this target
        			List sources = (List)tableMap.get(vTarget);
        			
        			// If the target has any sources, continue
        			if(!sources.isEmpty()) {
	    	        	// Get the columns for current target table
	    	            List columns = TransformationHelper.getTableColumns(vTarget);
	    	            
	    	            
	    	            // Add relationships for all columns
	    	            addSourceRelationshipRows(columns, new ArrayList());
        			}
        		}
        	}
	    // Generate relationships starting at top level target group, and recurse the heirarchy
    	} else {
    		List nextLevelAttrs = new ArrayList();
    		Map tableMap = (Map)this.tableDependencies.get(0);
    		Iterator keyIter = tableMap.keySet().iterator();
    		while(keyIter.hasNext()) {
    			// target
    			EObject vTarget = (EObject)keyIter.next();

    			// Sources for this target
    			List sources = (List)tableMap.get(vTarget);
    			
    			// If the target has any sources, continue
    			if(!sources.isEmpty()) {
	    			List tableAttrs = TransformationHelper.getTableColumns(vTarget);
	    			Iterator tableAttrIter = tableAttrs.iterator();
	    			while(tableAttrIter.hasNext()) {
	    				Object tableAttr = tableAttrIter.next();
	    				if(!nextLevelAttrs.contains(tableAttr)) {
	    					nextLevelAttrs.add(tableAttr);
	    				}
	    			}
    			}
    		}

    		List targetAttrs = new ArrayList();
    		targetAttrs.add(nextLevelAttrs);
    		for(int i=0; i<nLevels; i++) {
    			List attrs = (List)targetAttrs.get(i);
    			List dependentAttrs = new ArrayList();
    			addSourceRelationshipRows(attrs,dependentAttrs);
    			targetAttrs.add(i+1,dependentAttrs);
    		}
    	}
    }
    
	/**
	 * Add relationship rows for the supplied List of target attributes.  
	 * The generated relationships are added to the relRowStrings list.  
	 * @param targetAttrs the list of target attributes to generate column relationships
	 * @param allSourceAttrs accumulator for source attributes
	 */
    private void addSourceRelationshipRows(List targetAttrs, List allSourceAttrs) {
    	// Create relationships for all supplied virtual attributes
        Iterator attrIter = targetAttrs.iterator();
        while(attrIter.hasNext()) {
        	EObject targetAttr = (EObject)attrIter.next();
            // As long as it's virtual attribute we recursively search.
            if( ModelObjectUtilities.isVirtual(targetAttr) ) {
                
                // Get the attribute's parent 
                EObject virtualTarget = targetAttr.eContainer();
                // if virtualTarget is a Procedure ResultSet, get the parent Procedure
                if(!TransformationHelper.isValidSqlTransformationTarget(virtualTarget) && 
                    TransformationHelper.isSqlColumnSet(virtualTarget)) {
                    virtualTarget = virtualTarget.eContainer();                
                }
                
                RelationshipTableRowObject rowObj = null;
                if( virtualTarget != null ) {
                    TransformationMappingRoot mappingRoot = (TransformationMappingRoot)getTransformation(virtualTarget);
                    List sourceTables = getSources(mappingRoot);
                    
                    if(!sourceTables.isEmpty()) {
                        List targetAttrList = new ArrayList(1);
                        targetAttrList.add(getFullPathForEObject(targetAttr));
                        
	                    List attrDepList = new ArrayList(TransformationHelper.getSourceAttributesForTargetAttr(targetAttr, mappingRoot));
	
	                    if ( !attrDepList.isEmpty() ) {
	                    	// Let's create a row for this relationship
	                    	List sourceAttrList = new ArrayList(attrDepList.size());
	                    	
	                        EObject nextEObject = null;
	                    	Iterator iter = attrDepList.iterator();
	                    	// Loop through these attributes
	                    	EObject firstSourceAttr = null;
	                    	while( iter.hasNext() ) {
	                        	nextEObject = (EObject)iter.next();
	                        	if( !nextEObject.equals(targetAttr)) {
	                        		if( firstSourceAttr == null )
	                        			firstSourceAttr = nextEObject;
	                            	// Now see if attribute's table is a source table
	                            	EObject attributeParent = nextEObject.eContainer();
	                            	if( sourceTables.contains(attributeParent) ) {
	                                	sourceAttrList.add(nextEObject);
	                                	if(!allSourceAttrs.contains(nextEObject)) {
	                                		allSourceAttrs.add(nextEObject);
	                                	}
	                            	}
	                        	}
	                    	}
	                    	
	                    	rowObj = new RelationshipTableRowObject(generateName(firstSourceAttr, targetAttr), null, relType, targetAttrList, getPathStrings(sourceAttrList), this.locationStr );
	                    	// Check whether an equivalent Row is already in the list
	                    	if(!containsEquivalentRow(relRowStrings,rowObj)) {
	                        	relRowStrings.add(rowObj);
	                        // If not added, decrement count to reuse
	                        } else {
	                        	if(this.autoGenerateNames) relCount--;
	                        }
	                    }
                    }
                }
            }
        }
                
    }
    
	/**
	 * Test whether an 'equivalent' to the supplied rowObj is already in the list.  This is looking
	 * for an already-existing relationship of the same type between the same sources and targets.  
	 * (The names and descriptions may be different)
	 * @param rowList the supplied list of already existing rowObjects
	 * @param rowObj the supplied row object to test
	 * @return true if the supplied row object is equivalent to one in the rowList
	 */
    private boolean containsEquivalentRow(List rowList, RelationshipTableRowObject rowObj) {
    	boolean containsEquivalentRow = false;
    	if(rowList!=null && rowObj!=null && !rowList.isEmpty()) {
        	Iterator rowIter = rowList.iterator();
        	while(rowIter.hasNext()) {
        		RelationshipTableRowObject listRow = (RelationshipTableRowObject)rowIter.next();
        		if(listRow.referencesEqual(rowObj)) {
        			containsEquivalentRow = true;
        			break;
        		}
        	}
    	}
    	return containsEquivalentRow;
    }
    
	/**
	 * Generate a name based on the supplied source and target objects.
	 * @param sourceEObj the supplied source object
	 * @param targetEObj the supplied target object
	 * @return the generated name
	 */
    private String generateName(EObject sourceEObj, EObject targetEObj) {
    	String newName = DEFAULT_NAME;
    	if( autoGenerateNames ) {
    		relCount++;
    		String zeros = ""; //$NON-NLS-1$
    		if( relCount < 10 ) {
    			zeros = "000"; //$NON-NLS-1$
    		} else if( relCount < 100 ) {
    			zeros = "00"; //$NON-NLS-1$
    		} else if( relCount < 1000 ) {
    			zeros = "0"; //$NON-NLS-1$
    		}
    		newName = NAME_PREFIX + zeros + relCount;
    	} else {
	    	String srcName = getFullPathForEObject(sourceEObj);
	    	srcName = srcName.replace('/', '_');
	    	srcName = srcName.replace(' ', '_');
	    	String tgtName = getFullPathForEObject(targetEObj);
	    	tgtName = tgtName.replace('/', '_');
	    	tgtName = tgtName.replace(' ', '_');
	    	newName = (srcName + TO_STRING + tgtName);
    	}
    	return newName;
    }
    
	/**
	 * Generate a list of fullPathStrings, based on the supplied list of EObjects
	 * @param eObjs the supplied list of EObjects
	 * @return the corresponding list of fullPath strings
	 */
    private List getPathStrings(List eObjs){
    	List strList = new ArrayList(eObjs.size());
    	EObject nextEObj = null;
    	String nextPath = null;
    	for( Iterator iter = eObjs.iterator(); iter.hasNext(); ) {
    		nextEObj = (EObject)iter.next();
    		nextPath = getFullPathForEObject(nextEObj);
    		strList.add(nextPath);
    	}
    	
    	return strList;
    }
    
	/**
	 * Get the fullPath string for the supplied EObject
	 * @param eObj the supplied EObject
	 * @return the corresponding fullPath string
	 */
    private String getFullPathForEObject(EObject eObj) {
    	String loc = null;
    	ModelResource mr = ModelUtilities.getModelResourceForModelObject(eObj);
    	if( mr != null ) {
	    	String pathToModel = mr.getPath().removeFileExtension().toString();
	        if( pathToModel.charAt(0) == '/' )
	        	pathToModel = pathToModel.substring(1);
	        loc = pathToModel;
	        
	        String pathToObject = ModelObjectUtilities.getRelativePath(eObj);
	        if( pathToObject != null ) {
	        	loc = loc + '/' + pathToObject;
	        }
    	} else {
    		
    	}
        
        return loc;
    }
    
}
