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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;



/**
 * 
 *
 * @since 8.0
 */
public class RelationalUniqueConstraint extends RelationalReference {
    private List<RelationalColumn> columns;
    
    /**
     * RelationalUniqueConstraint constructor
     */
    public RelationalUniqueConstraint() {
        super();
        setType(TYPES.UC);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(true, true));
    }
    
    /**
     * RelationalUniqueConstraint constructor
     * @param name the UC name
     */
    public RelationalUniqueConstraint( String name ) {
        super(name);
        setType(TYPES.UC);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(true, true));
    }
    
    @Override
	public RelationalUniqueConstraint clone() {
    	RelationalUniqueConstraint cloneUC = new RelationalUniqueConstraint(getName());
    	cloneUC.setNameInSource(getNameInSource());
    	cloneUC.setDescription(getDescription());
    	cloneUC.setModelType(getModelType());
    	for( RelationalColumn col : getColumns() ) {
    		cloneUC.addColumn(col);
    	}
    	return cloneUC;
    }
    
    @Override
    public void inject(RelationalReference originalUC) {
    	super.inject(originalUC);
    	RelationalUniqueConstraint theUC = (RelationalUniqueConstraint)originalUC;
    	setName(theUC.getName());
    	setNameInSource(theUC.getNameInSource());
    	setDescription(theUC.getDescription());
    	setModelType(theUC.getModelType());
    	getColumns().clear();
    	for( RelationalColumn col : theUC.getColumns() ) {
    		addColumn(col);
    	}
    }
 
    /**
     * @return columns
     */
    public List<RelationalColumn> getColumns() {
        return columns;
    }
    /**
     * @param columns Sets columns to the specified value.
     */
    public void setColumns( Collection<RelationalColumn> columns ) {
        this.columns = new ArrayList<RelationalColumn>(columns);
        handleInfoChanged();
    }
    
    /**
     * Add a column to the unique constraint
     * @param column the column
     */
    public void addColumn(RelationalColumn column) {
    	if( this.columns.add(column) ) {
    		column.setParent(this);
    		handleInfoChanged();
    	} 
    }

    /**
     * Set the object properties
     * @param props the properties
     */
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
        handleInfoChanged();
    }
    
    /**
     * Get the parent table
     * @return the table
     */
    public RelationalTable getTable() {
    	if( getParent() != null ) {
    		return (RelationalTable)getParent();
    	}
    	
    	return null;
    }
    
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		if( !this.getStatus().isOK() ) {
			return;
		}
		
		if( this.getColumns().isEmpty() ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
						NLS.bind(Messages.validate_error_ucNoColumnsDefined, getName())));
			return;
		}
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
        final RelationalUniqueConstraint other = (RelationalUniqueConstraint)object;

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
