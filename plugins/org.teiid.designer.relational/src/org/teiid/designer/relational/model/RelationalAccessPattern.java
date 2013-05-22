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

import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;



/**
 * 
 *
 * @since 8.0
 */
public class RelationalAccessPattern extends RelationalReference {
    private Collection<RelationalColumn> columns;
    
    public RelationalAccessPattern() {
        super();
        setType(TYPES.AP);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @param name
     */
    public RelationalAccessPattern( String name ) {
        super(name);
        setType(TYPES.AP);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
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
    public void addColumn( RelationalColumn column ) {
        this.columns.add(column);
    }
    
    public RelationalTable getTable() {
    	if( getParent() != null ) {
    		return (RelationalTable)getParent();
    	}
    	
    	return null;
    }
    
    public void setProperties(Properties props) {
        for( Object key : props.keySet() ) {
            String keyStr = (String)key;
            String value = props.getProperty(keyStr);

            if( value != null && value.length() == 0 ) {
                continue;
            }
            
            if( keyStr.equalsIgnoreCase(KEY_NAME) ) {
                setName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_NAME_IN_SOURCE) ) {
                setNameInSource(value);
            } else if(keyStr.equalsIgnoreCase(KEY_DESCRIPTION) ) {
                setDescription(value);
            }
        }
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append(" : name = ").append(getName()); //$NON-NLS-1$
		if( !getColumns().isEmpty() ) {
			sb.append("\n\t").append(getColumns().size()).append(" columns"); //$NON-NLS-1$  //$NON-NLS-2$
			for( RelationalColumn col : getColumns() ) {
				sb.append("\n\tcol = ").append(col); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}
}
