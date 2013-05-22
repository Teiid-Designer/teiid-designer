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
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;



/**
 * 
 *
 * @since 8.0
 */
public class RelationalIndex extends RelationalReference {

	public static final String KEY_AUTO_UPDATE = "AUTOUPDATE"; //$NON-NLS-1$
    public static final String KEY_FILTER_CONDITION = "FILTERCONDITION"; //$NON-NLS-1$
    public static final String KEY_NULLABLE = "NULLABLE"; //$NON-NLS-1$
    public static final String KEY_UNIQUE = "UNIQUE"; //$NON-NLS-1$
    
    public static final boolean DEFAULT_AUTO_UPDATE = false;
    public static final String DEFAULT_FILTER_CONDITION = null;
    public static final boolean DEFAULT_NULLABLE = false;
    public static final boolean DEFAULT_UNIQUE = false;
    
    private Collection<RelationalColumn> columns;
    private boolean autoUpdate;
    private String  filterCondition;
    private boolean nullable;
    private boolean unique;
    
    private boolean existingTable;
    private RelationalTable relationalTable;
    
    /**
     * 
     */
    public RelationalIndex() {
        super();
        setType(TYPES.INDEX);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @param name the index name
     */
    public RelationalIndex( String name ) {
        super(name);
        setType(TYPES.INDEX);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    @Override
	public RelationalIndex clone() {
    	RelationalIndex clonedIndex = new RelationalIndex(getName());
    	clonedIndex.setNameInSource(getNameInSource());
    	clonedIndex.setDescription(getDescription());
    	clonedIndex.setModelType(getModelType());
    	clonedIndex.setUnique(isUnique());
    	clonedIndex.setAutoUpdate(isAutoUpdate());
    	clonedIndex.setFilterCondition(getFilterCondition());
    	clonedIndex.setNullable(isNullable());
    	for( RelationalColumn col : getColumns() ) {
    		clonedIndex.addColumn(col);
    	}
    	return clonedIndex;
    }
    
    @Override
    public void inject(RelationalReference originalIndex) {
    	super.inject(originalIndex);
    	RelationalIndex theIndex = (RelationalIndex)originalIndex;
    	setName(theIndex.getName());
    	setNameInSource(theIndex.getNameInSource());
    	setDescription(theIndex.getDescription());
    	setModelType(theIndex.getModelType());
    	setFilterCondition(theIndex.getFilterCondition());
    	setNullable(theIndex.isNullable());
    	setAutoUpdate(theIndex.isAutoUpdate());
    	setUnique(theIndex.isUnique());
    	getColumns().clear();
    	for( RelationalColumn col : theIndex.getColumns() ) {
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
     * @param column the collumn
     */
    public void addColumn( RelationalColumn column ) {
        this.columns.add(column);
    }
    /**
     * @return autoUpdate
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }
    /**
     * @param autoUpdate Sets autoUpdate to the specified value.
     */
    public void setAutoUpdate( boolean autoUpdate ) {
        this.autoUpdate = autoUpdate;
    }
    /**
     * @return filterCondition
     */
    public String getFilterCondition() {
        return filterCondition;
    }
    /**
     * @param filterCondition Sets filterCondition to the specified value.
     */
    public void setFilterCondition( String filterCondition ) {
        this.filterCondition = filterCondition;
    }
    /**
     * @return nullable
     */
    public boolean isNullable() {
        return nullable;
    }
    /**
     * @param nullable Sets nullable to the specified value.
     */
    public void setNullable( boolean nullable ) {
        this.nullable = nullable;
    }
    /**
     * @return unique
     */
    public boolean isUnique() {
        return unique;
    }
    /**
     * @param unique Sets unique to the specified value.
     */
    public void setUnique( boolean unique ) {
        this.unique = unique;
    }
    
    /**
	 * @return the existingTable
	 */
	public boolean usesExistingTable() {
		return this.existingTable;
	}

	/**
	 * @param existingTable the existingTable to set
	 */
	public void setUsesExistingTable(boolean usesExistingTable) {
		this.existingTable = usesExistingTable;
	}

	/**
	 * @return the relationalTable
	 */
	public RelationalTable getRelationalTable() {
		return this.relationalTable;
	}

	/**
	 * @param relationalTable the relationalTable to set
	 */
	public void setRelationalTable(RelationalTable relationalTable) {
		this.relationalTable = relationalTable;
	}

	/**
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
            } else if(keyStr.equalsIgnoreCase(KEY_NULLABLE) ) {
                setNullable(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_UNIQUE) ) {
                setUnique(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_AUTO_UPDATE) ) {
                setAutoUpdate(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_FILTER_CONDITION) ) {
                setFilterCondition(value);
            }
        }
    }
    
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		if( getStatus().getSeverity() == IStatus.ERROR ) {
			return;
		}
		
		
		
		// Check Column Status values
		for( RelationalColumn col : getColumns() ) {
			if( col.getStatus().getSeverity() == IStatus.ERROR ) {
				setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, col.getStatus().getMessage() ));
				return;
			}
		}
		
		// Check Column Status values
		for( RelationalColumn outerColumn : getColumns() ) {
			for( RelationalColumn innerColumn : getColumns() ) {
				if( outerColumn != innerColumn ) {
					if( outerColumn.getName().equalsIgnoreCase(innerColumn.getName())) {
						setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
								NLS.bind(Messages.validate_error_duplicateColumnNamesReferencedInIndex, getName())));
					}
				}
			}
		}
		
		if( this.getColumns().isEmpty() ) {
			setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					NLS.bind(Messages.validate_warning_noColumnReferencesDefined, getName()) ));
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
