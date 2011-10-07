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
import com.metamatrix.core.util.CoreArgCheck;

/**
 * 
 */
public class RelationalModel extends RelationalReference {
    private Collection<RelationalReference> children;
    /**
     * @param name
     */
    public RelationalModel( String name ) {
        super(name);
        setType(TYPES.MODEL);
        this.children = new ArrayList<RelationalReference>();
    }
    
    public Collection<RelationalReference> getChildren() {
        return this.children;
    }
    
    public boolean addChild(RelationalReference child) {
        if( this.children == null ) {
            this.children = new ArrayList<RelationalReference>();
        }
        
        if( !this.children.contains(child) ) {
            return this.children.add(child);
        }
        
        return false;
    }
    
    public boolean removeChild(RelationalReference child) {
        if( this.children == null ) {
            return false;
        }
        
        if( this.children.contains(child) ) {
            return this.children.remove(child);
        }
        
        return false;
    }
    
    public boolean hasChild(String name) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        for( RelationalReference child : this.children ) {
            if( name.equalsIgnoreCase( child.getName())) {
                return true;
            }
        }
        
        return false;
    }
    
    public RelationalReference getChildWithName(String name) {
        CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
        for( RelationalReference child : this.children ) {
            if( name.equalsIgnoreCase( child.getName())) {
                return child;
            }
        }
        
        return null;
    }
}
