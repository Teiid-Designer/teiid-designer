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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;



/**
 * 
 *
 * @since 8.0
 */
public class RelationalForeignKey extends RelationalReference {
    public static final String KEY_FOREIGN_KEY_MULTIPLICITY = "FKMULTIPLICITY"; //$NON-NLS-1$
    public static final String KEY_PRIMARY_KEY_MULTIPLICITY = "PKMULTIPLICITY"; //$NON-NLS-1$
    public static final String KEY_UNIQUE_KEY_NAME = "UNIQUEKEYNAME"; //$NON-NLS-1$
    public static final String KEY_UNIQUE_KEY_TABLE_NAME = "UNIQUEKEYTABLENAME"; //$NON-NLS-1$
    
    public static final String DEFAULT_FOREIGN_KEY_MULTIPLICITY = MULTIPLICITY.ZERO_TO_MANY;
    public static final String DEFAULT_PRIMARY_KEY_MULTIPLICITY = MULTIPLICITY.ONE;
    public static final String DEFAULT_UNIQUE_KEY_NAME = null;
    public static final String DEFAULT_UNIQUE_KEY_TABLE_NAME = null;
    
    
    private Collection<RelationalColumn> columns;
    private String foreignKeyMultiplicity;
    private String  primaryKeyMultiplicity;
    private String   uniqueKeyName;
    private String   uniqueKeyTableName;
    
    /**
     * RelationalForeignKey constructor
     */
    public RelationalForeignKey() {
        super();
        setType(TYPES.FK);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * RelationalForeignKey constructor
     * @param name the FK name
     */
    public RelationalForeignKey( String name ) {
        super(name);
        setType(TYPES.FK);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    @Override
	public RelationalForeignKey clone() {
    	RelationalForeignKey clonedFK = new RelationalForeignKey(getName());
    	clonedFK.setNameInSource(getNameInSource());
    	clonedFK.setDescription(getDescription());
    	clonedFK.setForeignKeyMultiplicity(getForeignKeyMultiplicity());
    	clonedFK.setPrimaryKeyMultiplicity(getPrimaryKeyMultiplicity());
    	clonedFK.setUniqueKeyName(getUniqueKeyName());
    	clonedFK.setUniqueKeyTableName(getUniqueKeyTableName());
    	clonedFK.setModelType(getModelType());
    	for( RelationalColumn col : getColumns() ) {
    		clonedFK.addColumn(col);
    	}
    	return clonedFK;
    }
    
    @Override
    public void inject(RelationalReference originalFK) {
    	super.inject(originalFK);
    	RelationalForeignKey theFK = (RelationalForeignKey)originalFK;
    	setName(theFK.getName());
    	setNameInSource(theFK.getNameInSource());
    	setDescription(theFK.getDescription());
    	setForeignKeyMultiplicity(theFK.getForeignKeyMultiplicity());
    	setPrimaryKeyMultiplicity(theFK.getPrimaryKeyMultiplicity());
    	setUniqueKeyName(theFK.getUniqueKeyName());
    	setUniqueKeyTableName(theFK.getUniqueKeyTableName());
    	setModelType(theFK.getModelType());
    	getColumns().clear();
    	for( RelationalColumn col : theFK.getColumns() ) {
    		addColumn(col);
    	}
    }
    
    /**
     * @return columns
     */
    public Collection<RelationalColumn> getColumns() {
        return columns;
    }

    /**
     * Add a column to this FK
     * @param column the column
     */
    public void addColumn( RelationalColumn column ) {
        this.columns.add(column);
    }
    /**
     * @return foreignKeyMultiplicity
     */
    public String getForeignKeyMultiplicity() {
        return foreignKeyMultiplicity;
    }
    /**
     * @param foreignKeyMultiplicity Sets foreignKeyMultiplicity to the specified value.
     */
    public void setForeignKeyMultiplicity( String foreignKeyMultiplicity ) {
        this.foreignKeyMultiplicity = foreignKeyMultiplicity;
    }
    /**
     * @return primaryKeyMultiplicity
     */
    public String getPrimaryKeyMultiplicity() {
        return primaryKeyMultiplicity;
    }
    /**
     * @param primaryKeyMultiplicity Sets primaryKeyMultiplicity to the specified value.
     */
    public void setPrimaryKeyMultiplicity( String primaryKeyMultiplicity ) {
        this.primaryKeyMultiplicity = primaryKeyMultiplicity;
    }
    /**
     * @return uniqueKeyName
     */
    public String getUniqueKeyName() {
        return uniqueKeyName;
    }
    /**
     * @param uniqueKeyName Sets uniqueKeyName to the specified value.
     */
    public void setUniqueKeyName( String uniqueKeyName ) {
        this.uniqueKeyName = uniqueKeyName;
    }
    
    /**
     * @return uniqueKeyTableName
     */
    public String getUniqueKeyTableName() {
        return uniqueKeyTableName;
    }
    
    /**
     * @param uniqueKeyTableName Sets uniqueKeyTableName to the specified value.
     */
    public void setUniqueKeyTableName( String uniqueKeyTableName ) {
        this.uniqueKeyTableName = uniqueKeyTableName;
    }
    
    /**
     * @return the table
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
            } else if(keyStr.equalsIgnoreCase(KEY_FOREIGN_KEY_MULTIPLICITY) ) {
                setForeignKeyMultiplicity(value);
            } else if(keyStr.equalsIgnoreCase(KEY_PRIMARY_KEY_MULTIPLICITY) ) {
                setPrimaryKeyMultiplicity(value);
            } else if(keyStr.equalsIgnoreCase(KEY_UNIQUE_KEY_NAME) ) {
                setUniqueKeyName(value);
            } else if(keyStr.equalsIgnoreCase(KEY_UNIQUE_KEY_TABLE_NAME) ) {
                setUniqueKeyTableName(value);
            }
        }
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
						NLS.bind(Messages.validate_error_fkNoColumnsDefined, getName())));
			return;
		}
		
		if( this.getUniqueKeyName() == null || this.getUniqueKeyName().length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
					NLS.bind(Messages.validate_error_fKUniqueKeyNameIsUndefined, getName())));
			return;
		}
		
		if( this.getUniqueKeyTableName() == null || this.getUniqueKeyTableName().length() == 0 ) {
			setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_error_fKReferencedUniqueKeyTableIsUndefined));
			return;
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
        final RelationalForeignKey other = (RelationalForeignKey)object;

        // string properties
        if (!CoreStringUtil.valuesAreEqual(getForeignKeyMultiplicity(), other.getForeignKeyMultiplicity()) ||
        		!CoreStringUtil.valuesAreEqual(getPrimaryKeyMultiplicity(), other.getPrimaryKeyMultiplicity()) ||
        		!CoreStringUtil.valuesAreEqual(getUniqueKeyName(), other.getUniqueKeyName()) || 
        		!CoreStringUtil.valuesAreEqual(getUniqueKeyTableName(), other.getUniqueKeyTableName()) ) {
            return false;
        }
        
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

        // string properties
        if (!CoreStringUtil.isEmpty(getForeignKeyMultiplicity())) {
            result = HashCodeUtil.hashCode(result, getForeignKeyMultiplicity());
        }
        if (!CoreStringUtil.isEmpty(getPrimaryKeyMultiplicity())) {
            result = HashCodeUtil.hashCode(result, getPrimaryKeyMultiplicity());
        }
        if (!CoreStringUtil.isEmpty(getUniqueKeyName())) {
            result = HashCodeUtil.hashCode(result, getUniqueKeyName());
        }
        if (!CoreStringUtil.isEmpty(getUniqueKeyTableName())) {
            result = HashCodeUtil.hashCode(result, getUniqueKeyTableName());
        }
        
        Collection<RelationalColumn> cols = getColumns();
        for(RelationalColumn col: cols) {
            result = HashCodeUtil.hashCode(result, col);
        }
                
        return result;
    }    

}
