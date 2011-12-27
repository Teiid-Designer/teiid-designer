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
import java.util.Properties;


/**
 * 
 */
public class RelationalUniqueConstraint extends RelationalReference {
    private Collection<RelationalColumn> columns;
    
    public RelationalUniqueConstraint() {
        super();
        setType(TYPES.UC);
        this.columns = new ArrayList<RelationalColumn>();
    }
    
    /**
     * @param name
     */
    public RelationalUniqueConstraint( String name ) {
        super(name);
        setType(TYPES.UC);
        this.columns = new ArrayList<RelationalColumn>();
    }
 
    /**
     * @return columns
     */
    public Collection<RelationalColumn> getColumns() {
        return columns;
    }
    /**
     * @param columns Sets columns to the specified value.
     */
    public void setColumns( Collection<RelationalColumn> columns ) {
        this.columns = columns;
    }
    
    public void addColumn(RelationalColumn column) {
        this.columns.add(column);
    }

    public void setProperties(Properties props) {
        for( Object key : props.keySet() ) {
            String keyStr = (String)key;
            String value = props.getProperty(keyStr);
            if( keyStr.equalsIgnoreCase(KEY_NAME) ) {
                setName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NAME_IN_SOURCE) ) {
                setNameInSource(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DESCRIPTION) ) {
                setDescription(value);
            }
        }
    }
}
