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
import java.util.List;
import java.util.Properties;

import org.teiid.core.designer.HashCodeUtil;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;



/**
 * RelationalAccessPattern
 *
 * @since 8.0
 */
public class RelationalAccessPattern extends RelationalReference {
    private List<RelationalColumn> columns;
    
    /**
     * constructor
     */
    public RelationalAccessPattern() {
        super();
        setType(TYPES.AP);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false));
    }
    
    /**
     * constructor
     * @param name the name for the access pattern
     */
    public RelationalAccessPattern( String name ) {
        super(name);
        setType(TYPES.AP);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false));
    }
    
    /**
     * @return columns
     */
    public List<RelationalColumn> getColumns() {
        return columns;
    }
    
    /**
     * Add a column
     * @param column the relational column to add
     */
    public void addColumn( RelationalColumn column ) {
        this.columns.add(column);
    }
    
    /**
     * Get the table
     * @return the relational table
     */
    public RelationalTable getTable() {
    	if( getParent() != null ) {
    		return (RelationalTable)getParent();
    	}
    	
    	return null;
    }
    
    /**
     * Set properties
     * @param props the properties
     */
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
	
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object object ) {
		if (!super.equals(object)) {
			return false;
		}
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        final RelationalAccessPattern other = (RelationalAccessPattern)object;

        // Columns
        Collection<RelationalColumn> thisColumns = getColumns();
        Collection<RelationalColumn> thatColumns = other.getColumns();

        if (thisColumns.size() != thatColumns.size()) {
            return false;
        }
        
        if (!thisColumns.isEmpty() && !thisColumns.containsAll(thatColumns)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();

        List<RelationalColumn> cols = getColumns();
        for(RelationalColumn col: cols) {
            result = HashCodeUtil.hashCode(result, col);
        }
        
        return result;
    }    

}
