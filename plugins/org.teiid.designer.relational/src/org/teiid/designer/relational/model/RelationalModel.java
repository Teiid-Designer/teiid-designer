/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.relational.model;

import java.util.ArrayList;
import java.util.Collection;

import org.teiid.core.designer.util.CoreArgCheck;

/**
 * 
 *
 * @since 8.0
 */
public class RelationalModel extends RelationalReference {
	private Collection<RelationalReference> allRefs = new ArrayList<RelationalReference>();
    private Collection<RelationalReference> children;
    
    /**
     * RelationalModel constructor
     * @param name the model name
     */
    public RelationalModel( String name ) {
        super(name);
        setType(TYPES.MODEL);
        this.children = new ArrayList<RelationalReference>();
    }
       
    /**
     * Get all reference objects for this model
     * @return all reference objects
     */
    public Collection<RelationalReference> getAllReferences() {
    	this.allRefs.clear();
    	if(this.children!=null) {
        	for(RelationalReference ref:this.children) {
        		addRecursive(ref,this.allRefs);
        	}
    	}
    	return this.allRefs;
    }
    
    /**
     * Get the top level children for this model
     * @return model children
     */
    public Collection<RelationalReference> getChildren() {
        return this.children;
    }
    
    /**
     * Add a child to this model
     * @param child the child
     * @return 'true' if child was added
     */
    public boolean addChild(RelationalReference child) {
        if( this.children == null ) {
            this.children = new ArrayList<RelationalReference>();
        }

        boolean wasAdded = false;
        if( !this.children.contains(child) ) {
        	child.setParent(this);
            wasAdded = this.children.add(child);
        }
        
        return wasAdded;
    }
    
    /**
     * Remove specified child from the model
     * @param child the child to remove
     * @return 'true' if child was removed
     */
    public boolean removeChild(RelationalReference child) {
        if( this.children == null ) {
            return false;
        }
        
        if( this.children.contains(child) ) {
            return this.children.remove(child);
        }
        
        return false;
    }
    
    /**
     * Determine if the model has a child with the specified name
     * @param name the child name
     * @return 'true' if model contains child with matching name
     */
    public boolean hasChild(String name) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        for( RelationalReference child : this.children ) {
            if( name.equalsIgnoreCase( child.getName())) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the model child with the specified name
     * @param name the child name
     * @return the child, null if no matching child
     */
    public RelationalReference getChildWithName(String name) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        for( RelationalReference child : this.children ) {
            if( name.equalsIgnoreCase( child.getName())) {
                return child;
            }
        }
        
        return null;
    }
    
    /**
     * Recursively adds all references
     * @param ref
     * @param allRefs
     */
    private void addRecursive(RelationalReference ref, Collection<RelationalReference> allRefs) {
    	if(ref instanceof RelationalTable) {
    		allRefs.add(ref);
    		Collection<RelationalAccessPattern> accessPatterns = ((RelationalTable)ref).getAccessPatterns();
    		for(RelationalReference ap: accessPatterns) {
    			addRecursive(ap,allRefs);
    		}
    		
    		Collection<RelationalColumn> columns  = ((RelationalTable)ref).getColumns();
    		for(RelationalReference col: columns) {
    			addRecursive(col,allRefs);
    		}
    		
    		Collection<RelationalForeignKey> fks = ((RelationalTable)ref).getForeignKeys();
    		for(RelationalReference fk: fks) {
    			addRecursive(fk,allRefs);
    		}
    		
    		RelationalPrimaryKey  pk = ((RelationalTable)ref).getPrimaryKey();
    		addRecursive(pk,allRefs);
    		
    		Collection<RelationalIndex> indexes = ((RelationalTable)ref).getIndexes();
    		for(RelationalReference index: indexes) {
    			addRecursive(index,allRefs);
    		}

    		Collection<RelationalUniqueConstraint> ucs = ((RelationalTable)ref).getUniqueConstraints();
    		if(ucs!=null) {
    			for(RelationalReference uc: ucs) {
    				addRecursive(uc,allRefs);
    			}
    		}
    	} else if(ref instanceof RelationalProcedure) {
    		allRefs.add(ref);
    		Collection<RelationalParameter> procParams = ((RelationalProcedure)ref).getParameters();
    		for(RelationalReference param: procParams) {
    			addRecursive(param,allRefs);
    		}
    		RelationalProcedureResultSet resultSet = ((RelationalProcedure)ref).getResultSet();
    		addRecursive(resultSet,allRefs);
    		
    	} else if(ref instanceof RelationalView) {
    		allRefs.add(ref);
    		Collection<RelationalAccessPattern> accessPatterns = ((RelationalView)ref).getAccessPatterns();
    		for(RelationalReference ap: accessPatterns) {
    			addRecursive(ap,allRefs);
    		}
    		Collection<RelationalColumn> columns = ((RelationalView)ref).getColumns();
    		for(RelationalReference col: columns) {
    			addRecursive(col,allRefs);
    		}
    		
    	} else {
    		allRefs.add(ref);
    	}
    }
        
}
