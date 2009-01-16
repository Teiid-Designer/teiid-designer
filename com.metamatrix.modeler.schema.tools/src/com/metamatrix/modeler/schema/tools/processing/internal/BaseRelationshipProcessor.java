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

package com.metamatrix.modeler.schema.tools.processing.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.metamatrix.modeler.schema.tools.model.schema.Column;
import com.metamatrix.modeler.schema.tools.model.schema.Relationship;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaModel;
import com.metamatrix.modeler.schema.tools.model.schema.SchemaObject;
import com.metamatrix.modeler.schema.tools.processing.RelationshipProcessor;
import com.metamatrix.modeler.schema.tools.processing.RelationshipRules;

public abstract class BaseRelationshipProcessor implements RelationshipProcessor {
	
	RelationshipRules rules;
	
	protected Map tableRelationships; // key: Relationship, value Integer - Relationship type

	protected SchemaModel schemaModel;

	public BaseRelationshipProcessor() {
		tableRelationships = new HashMap();
	}
	
	protected void setSechemaModel(SchemaModel model) {
		this.schemaModel = model;
	}

	public void addRelationship(String key, Integer value) {
		tableRelationships.put(key, value);
	}

	public void setRelationshipRules(RelationshipRules rules) {
		this.rules = rules;
	}

	protected int calculateCValue(List parents) {
		int C_value = -3;
		for (Iterator parentIter = parents.iterator(); parentIter.hasNext();) {
			Object o = parentIter.next();
			Relationship tableRelationship = (Relationship) o;
			int maxOccursThisLoop = tableRelationship.getMaxOccurs();
			if (C_value == -3) {
				C_value = maxOccursThisLoop;
			} else if (C_value != maxOccursThisLoop) {
				C_value = -2;
				break;
			}
		}
		return C_value;
	}

	protected void removeRecursiveMerges(List elements) {
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			SchemaObject element = (SchemaObject) iter.next();

			LinkedList fullPath = new LinkedList();
			LinkedList mergedPath = new LinkedList();
			removeRecursiveMergesForTable(element, fullPath, mergedPath);
		}
	}
	
	protected void removeRecursiveMergesForTable(SchemaObject element, LinkedList fullPath,
			LinkedList mergedPath) {
		fullPath.addLast(element);
		mergedPath.addLast(element);
		for (Iterator iter = element.getChildren().iterator(); iter.hasNext();) {
			Object o = iter.next();
			Relationship tableRelationship = (Relationship) o;
			SchemaObject child = tableRelationship.getChild();
            String key = child.getSimpleName() + ':' + child.getNamespace();
			Integer relation = (Integer)tableRelationships.get(key);
			int representation = relation.intValue();
			
			// TODO: check if we have arrived at a recursive merge

			LinkedList mergedPathParam = mergedPath;
			if (representation == Relationship.MERGE_IN_PARENT_SINGLE
					|| representation == Relationship.MERGE_IN_PARENT_MULTIPLE) {
				if (mergedPath.contains(child)) {
					if (representation == Relationship.MERGE_IN_PARENT_SINGLE) {
						representation = Relationship.KEY_IN_PARENT_SINGLE;
					} else if (representation == Relationship.MERGE_IN_PARENT_MULTIPLE) {
						representation = Relationship.KEY_IN_PARENT_MULTIPLE;
					}
					SchemaObject parent = tableRelationship.getParent();
					parent.setAllParentRepresentations(representation, this);
					mergedPathParam = new LinkedList();
				}
			} else {
				mergedPathParam = new LinkedList();
				continue;
			}

			if (fullPath.contains(child)) {
				continue;
			}

			removeRecursiveMergesForTable(child, fullPath, mergedPathParam);
			mergedPath.removeLast();
			fullPath.removeLast();
		}
	}

	protected void qualifyDuplicateMergedTableNames() {
        List processedTables = new ArrayList();
        for (Iterator iter = schemaModel.getElements().iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            SchemaObject table = (SchemaObject)o;
            qualifyDuplicateMergedChildTableNames(table, processedTables);
        }
    }

	protected void qualifyDuplicateMergedChildTableNames(SchemaObject table, List processedTables)
    {
        if (processedTables.contains(table)) {
            return;
        }

        // Add before to prevent infinite recursion
        processedTables.add(table);
        
        List children = table.getChildren();
        for (Iterator iter = children.iterator(); iter.hasNext(); ) {
            Object oTableRelationship = iter.next();
            Relationship tableRelationship = (Relationship)oTableRelationship;
            qualifyDuplicateMergedChildTableNames(tableRelationship.getChild(), processedTables);
        }
        checkForDuplicateMergedChildNames(children);
    }
    
	protected void checkForDuplicateMergedChildNames(List tableRelationships)
    {
        Map tablesByName = new HashMap();

        for (Iterator allTablesIter = tableRelationships.iterator(); allTablesIter.hasNext();) {
            Object o = allTablesIter.next();
            Relationship tableRelationship = (Relationship)o;

            int representation = tableRelationship.getType();
            if (representation != Relationship.MERGE_IN_PARENT_SINGLE &&
                representation  != Relationship.MERGE_IN_PARENT_MULTIPLE)
            {
                continue;
            }

            SchemaObject table = tableRelationship.getChild();
            String name = table.getSimpleName();
            Object oExisting = tablesByName.get(name);
            if (oExisting == null) {
                tablesByName.put(name, table);
            }
            else  {
                SchemaObject existing = (SchemaObject)oExisting;
                existing.setMustBeQualified();
                table.setMustBeQualified();
            }
        }
    }

	protected void mergeRelationships() {
     	List processedTables = new ArrayList();
        for (Iterator iter = schemaModel.getElements().iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            SchemaObject element = (SchemaObject)o;
            if(schemaModel.isSelectedRootElement(element)) {
            	mergeChildRelationships(element, processedTables);
            }
        }
	}

    /**
     * A recursive function that walks down to the end of the graph of relationships of the
     * supplied table and the walks back up the graph merging child schemaModel into parents as
     * appropriate.
     * 
     * This function is called for each table in the list of known schemaModel, but appends to the 
     * processedTables List so that it operates only once on each table, and does not infinitly
     * recurse on circular relationships.
     * 
     * @param table The table at the top of the graph.
     * @param processedTables the list of schemaModel that have been processed by this function.
     */
	protected void mergeChildRelationships(SchemaObject table, List processedTables)
    {
    	if (processedTables.contains(table)) {
    		return;
    	}

        // Add before to prevent infinite recursion
        processedTables.add(table);
        table.setWithinSelectedHierarchy(true);
        // This is a slightly precarious recursive algorithm, in that children are allowed
        // to add elements to the end of their parents' list of children. That's why
        // we use indexed iteration rather that iterators.
        
        Object[] children = table.getChildren().toArray();
        for (int i = 0; i < children.length; i++) {
        	Object relObject = children[i];
        	Relationship tableRelationship = (Relationship)relObject; 
            // depth first: merge the child's children first because they themselves
            // may need to get merged into the table
            SchemaObject child = tableRelationship.getChild();
            mergeChildRelationships(child, processedTables);
            String key = child.getSimpleName() + ':' + child.getNamespace();
            if(null != tableRelationships.get(key)) {
	            int representation = ((Integer)tableRelationships.get(key)).intValue();
	            if (representation == Relationship.MERGE_IN_PARENT_SINGLE ||
	              representation  == Relationship.MERGE_IN_PARENT_MULTIPLE) {
	                mergeChild(table, tableRelationship);
	                // The following statement has the effect of removing the relationship
	                // from the list, so we need to mess with the loop variable.
	                tableRelationship.removeRelationship();
	            }
            }
        }
    }

    /**
     * Merges the columns and TableRelationships of a child into its parent(s).
     * @param parent The table to merge into.
     * @param tableRelationship The Relationship to the child being merged.
     */
	protected void mergeChild(SchemaObject parent, Relationship tableRelationship)
    {
        SchemaObject child = tableRelationship.getChild();
        child.setWithinSelectedHierarchy(false);
        Object[] cols = child.getAttributes().toArray();
        for (int i = 0; i < cols.length; i++ ) {
            Column col = (Column)cols[i];
            int maxOccurs = tableRelationship.getMaxOccurs();
            for (int iOccurrence = 1; iOccurrence <= maxOccurs; iOccurrence++) {
                int iOccurenceParam = maxOccurs > 1 ? iOccurrence : -1;
                col.mergeIntoParent(tableRelationship, iOccurenceParam);
            }
        }        

       	pullUpGrandChildRelationships(child.getParents(), child.getChildren());
    }

    /**
     * Merges each child Relationship and each parent Relationship of a 
     * table being merged and then deletes the Relationship between the merged table
     * and its children.
     * @param parentRelationships the List of parent TableRelationships to a table
     * @param grandChildren the List of child TableRelationships to a table
     */
	protected void pullUpGrandChildRelationships(List parentRelationships, List grandChildren) {
		List newRelationships = new ArrayList();
		List foldedRelationships = new ArrayList();
		
        //Create the merged relationship and recorded both the new relationship and the folded one.
		for(Iterator iter = grandChildren.iterator(); iter.hasNext();) {
            Object o = iter.next();
            Relationship grandChild = (Relationship)o;
            for(Iterator pIter = parentRelationships.iterator(); pIter.hasNext(); ) {
            	Relationship tableRelationship = (Relationship)pIter.next();	
            	Relationship mergedRelationship = tableRelationship.merge(grandChild);
            	newRelationships.add(mergedRelationship);
            	foldedRelationships.add(grandChild);
            }
        }
        
		// Remove all of the folded ones.
        for (Iterator iter = foldedRelationships.iterator(); iter.hasNext(); ) {
        	Object o = iter.next();
            Relationship foldedRelationship = (Relationship)o;
            foldedRelationship.removeRelationship();
        }
        
        // Add the new ones
        for (Iterator iter = newRelationships.iterator(); iter.hasNext(); ) {
        	Object o = iter.next();
            Relationship newRelationship = (Relationship)o;
            newRelationship.addNewRelationship();
        }
	}


    protected void removeFullyMergedTables()
    {
        List nonMergedTables = new ArrayList();
        for (Iterator iter = schemaModel.getElements().iterator(); iter.hasNext(); ) {
            Object o = iter.next();
            SchemaObject table = (SchemaObject)o;

            if (table.isWithinSelectedHierarchy()) {
            	nonMergedTables.add(table);
                continue;
            }
        }
        schemaModel.setElements(nonMergedTables);
    }

    protected void qualifyDuplicateNonMergedTableNames() {
        Map tablesByName = new HashMap();
		
		for (Iterator allTablesIter = schemaModel.getElements().iterator(); allTablesIter.hasNext();) {
		    Object o = allTablesIter.next();
		    SchemaObject table = (SchemaObject)o;
		
		    String name = table.getSimpleName();
		    Object oExisting = tablesByName.get(name);
		    if (oExisting == null) {
		        tablesByName.put(name, table);
		    }
		    else  {
		        SchemaObject existing = (SchemaObject)oExisting;
		        existing.setMustBeQualified();
		        table.setMustBeQualified();
		    }
		}
    }

}
