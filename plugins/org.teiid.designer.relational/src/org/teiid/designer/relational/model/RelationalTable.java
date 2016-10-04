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
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.teiid.core.designer.HashCodeUtil;
import org.teiid.core.designer.util.CoreStringUtil;
import org.teiid.core.designer.util.StringUtilities;
import org.teiid.designer.metamodels.relational.aspects.validation.RelationalStringNameValidator;
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;


/**
 * 
 *
 * @since 8.0
 */
public class RelationalTable extends RelationalReference {
    public static final String KEY_CARDINALITY = "CARDINALITY"; //$NON-NLS-1$
    public static final String KEY_MATERIALIZED = "MATERIALIZED"; //$NON-NLS-1$
    public static final String KEY_MATERIALIZED_TABLE = "MATERIALIZEDTABLE"; //$NON-NLS-1$
    public static final String KEY_SUPPORTS_UPDATE = "SUPPORTSUPDATE"; //$NON-NLS-1$
    public static final String KEY_SYSTEM = "SYSTEM"; //$NON-NLS-1$
    
    public static final int DEFAULT_CARDINALITY = -1;
    public static final boolean DEFAULT_MATERIALIZED = false;
    public static final String DEFAULT_MATERIALIZED_TABLE = null; 
    public static final boolean DEFAULT_SUPPORTS_UPDATE = true; 
    public static final boolean DEFAULT_SYSTEM = false;

    
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$

    private int  cardinality = DEFAULT_CARDINALITY;
    private boolean materialized = DEFAULT_MATERIALIZED;
    private String materializedTableName;
    private String materializedTableModelPath;
    private boolean supportsUpdate = DEFAULT_SUPPORTS_UPDATE;
    private boolean system = DEFAULT_SYSTEM;
    private List<RelationalColumn> columns;
    private RelationalPrimaryKey primaryKey;
    private List<RelationalUniqueConstraint> uniqueConstraints;
    private List<RelationalAccessPattern> accessPatterns;
    private List<RelationalForeignKey> foreignKeys;
    private List<RelationalIndex> indexes;
    private String nativeQuery;
    
    
    /**
     * RelationalTable constructor
     */
    public RelationalTable() {
        super();
        setType(TYPES.TABLE);
        init();
    }
    
    /**
     * RelationalTable constructor
     * @param name the table name
     */
    public RelationalTable( String name ) {
        super(name);
        setType(TYPES.TABLE);
        init();
    }
    
    private void init() {
        this.columns = new ArrayList<RelationalColumn>();
        this.accessPatterns = new ArrayList<RelationalAccessPattern>();
        this.foreignKeys = new ArrayList<RelationalForeignKey>();
        this.indexes = new ArrayList<RelationalIndex>();
        this.uniqueConstraints = new ArrayList<RelationalUniqueConstraint>();
        setNameValidator(new RelationalStringNameValidator(true));
    }
    
    /**
     * @return cardinality
     */
    public int getCardinality() {
        return cardinality;
    }
    /**
     * @param cardinality Sets cardinality to the specified value.
     */
    public void setCardinality( int cardinality ) {
        if( this.cardinality != cardinality ) {
        	this.cardinality = cardinality;
        	handleInfoChanged();
        }
    }
    /**
     * @return materialized
     */
    public boolean isMaterialized() {
        return materialized;
    }
    /**
     * @param materialized Sets materialized to the specified value.
     */
    public void setMaterialized( boolean materialized ) {
        if( this.materialized != materialized ) {
        	this.materialized = materialized;
        	handleInfoChanged();
        }
    }
    /**
     * @return materializedTable
     */
    public String getMaterializedTable() {
        return materializedTableName;
    }
    /**
     * @param materializedTable Sets materializedTable to the specified value.
     */
    public void setMaterializedTable( String materializedTableName ) {
    	if( this.materializedTableName == null || !this.materializedTableName.equalsIgnoreCase(materializedTableName) ) {
    		this.materializedTableName = materializedTableName;
    		handleInfoChanged();
    	}
    }
    /**
     * @return materializedTable
     */
    public String getMaterializedTableModelPath() {
        return materializedTableModelPath;
    }
    /**
     * @param materializedTable Sets materializedTable to the specified value.
     */
    public void setMaterializedTableModelPath( String materializedTableModelPath ) {
    	if( this.materializedTableModelPath == null || !this.materializedTableModelPath.equalsIgnoreCase(materializedTableModelPath) ) {
    		this.materializedTableModelPath = materializedTableModelPath;
    		handleInfoChanged();
    	}
    }
    /**
     * @return supportsUpdate
     */
    public boolean getSupportsUpdate() {
        return supportsUpdate;
    }
    /**
     * @param supportsUpdate Sets supportsUpdate to the specified value.
     */
    public void setSupportsUpdate( boolean supportsUpdate ) {
    	if( this.supportsUpdate != supportsUpdate ) {
    		this.supportsUpdate = supportsUpdate;
    		handleInfoChanged();
    	}
    }
    /**
     * @return system
     */
    public boolean isSystem() {
        return system;
    }
    /**
     * @param system Sets system to the specified value.
     */
    public void setSystem( boolean system ) {
    	if( this.system != system ) {
	        this.system = system;
	        handleInfoChanged();
    	}
    }
    
    /**
     * @return nativeQuery may be null
     */
    public String getNativeQuery() {
        return nativeQuery;
    }
    /**
     * @param newQuery sets nativeQuery to the specified value. may be null
     */
    public void setNativeQuery( String newQuery ) {
    	if( StringUtilities.areDifferent(this.nativeQuery, newQuery) ) {
    		this.nativeQuery = newQuery;
    		handleInfoChanged();
    	}
    }

    /**
     * @return columns
     */
    public List<RelationalColumn> getColumns() {
        return columns;
    }
    
    /**
     * Add a column to the table
     * @param column the column to add
     */
    public void addColumn(RelationalColumn column) {
    	if( this.columns.add(column) ) {
    		column.setParent(this);
    		handleInfoChanged();
    	} 
    }
    
    /**
     * Remove a column from the table
     * @param column the column to remove
     */
    public boolean removeColumn(RelationalColumn column) {
    	if( this.columns.remove(column) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }

    /**
     * @return primaryKeys
     */
    public RelationalPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Set the tables PK
     * @param pk the pk
     */
    public void setPrimaryKey(RelationalPrimaryKey pk) {
    	if( this.primaryKey != pk ) {
	    	if( pk != null ) {
	    		pk.setParent(this);
	    	}
	        this.primaryKey = pk;
	        handleInfoChanged();
    	}
    }

    /**
     * Retained for backward compatibility with
     * RelationalTableEditorPanel / ViewTableEditorPanel
     *
     * @deprecated
     * @return uniqueContraints
     */
    public RelationalUniqueConstraint getUniqueContraint() {
        if (getUniqueConstraints() == null || getUniqueConstraints().isEmpty())
            return null;

        return getUniqueConstraints().iterator().next();
    }

    /**
     * Set the single and only unique constraint
     *
     * @deprecated
     * Retained for backward compatibility with
     * RelationalTableEditorPanel / ViewTableEditorPanel
     *
     * @param uc the uc
     */
    public void setUniqueConstraint(RelationalUniqueConstraint uc) {
        if (uniqueConstraints != null)
            uniqueConstraints.clear();

        addUniqueConstraint(uc);
    }

    /**
     * @return uniqueContraints
     */
    public Collection<RelationalUniqueConstraint> getUniqueConstraints() {
        return uniqueConstraints;
    }
    
    /**
     * Add a unique constraint
     * @param constraint the constraint
     */
    public void addUniqueConstraint(RelationalUniqueConstraint constraint) {
    	if( this.uniqueConstraints.add(constraint) ) {
    		constraint.setParent(this);
    		handleInfoChanged();
    	}
    }
    
    /**
     * Remove a unique constraint
     * @param constraint the constraint
     * @return 'true' if removed, 'false' if not
     */
    public boolean removeUniqueConstraint(RelationalUniqueConstraint constraint) {
    	if( this.uniqueConstraints.remove(constraint) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }

    /**
     * @return accessPatterns
     */
    public List<RelationalAccessPattern> getAccessPatterns() {
        return accessPatterns;
    }
    
    /**
     * Add an AccessPattern to the table
     * @param ap the AccessPattern
     */
    public void addAccessPattern(RelationalAccessPattern ap) {
    	if( this.accessPatterns.add(ap) ) {
    		ap.setParent(this);
    		handleInfoChanged();
    	}
    }
    
    /**
     * Remove an AccessPattern from the table
     * @param ap the AccessPattern
     * @return 'true' if removed, 'false' if not
     */
    public boolean removeAccessPattern(RelationalAccessPattern ap) {
    	if( this.accessPatterns.remove(ap) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }

    /**
     * @return foreignKeys
     */
    public List<RelationalForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    /**
     * Add FK to the table
     * @param fk the fk
     */
    public void addForeignKey(RelationalForeignKey fk) {
    	if( this.foreignKeys.add(fk) ) {
    		fk.setParent(this);
    		handleInfoChanged();
    	}
    }
    
    /**
     * Remove FK from the table
     * @param fk the fk
     * @return 'true' if removed, 'false' if not.
     */
    public boolean removeForeignKey(RelationalForeignKey fk) {
    	if( this.foreignKeys.remove(fk) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }
    
    /**
     * @return indexes
     */
    public List<RelationalIndex> getIndexes() {
        return indexes;
    }

    /**
     * @param index the index
     */
    public void addIndex(RelationalIndex index) {
    	if( this.indexes.add(index) ) {
    		// NOTE: indexes are children of a schema so set parent to table's parent
    		index.setParent(this.getParent());
    		handleInfoChanged();
    	}
    }
    
    /**
     * @param index the index
     * @return if index was removed
     */
    public boolean removeIndex(RelationalIndex index) {
    	if( this.indexes.remove(index) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }
    
    /**
     * Set the object properties
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
            } else if(keyStr.equalsIgnoreCase(KEY_CARDINALITY) ) {
                setCardinality(Integer.parseInt(value));
            } else if(keyStr.equalsIgnoreCase(KEY_MATERIALIZED) ) {
                setMaterialized(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_SUPPORTS_UPDATE) ) {
                setSupportsUpdate(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_SYSTEM) ) {
                setSystem(Boolean.parseBoolean(value));
            } 
        }
    }
    
    public RelationalColumn createColumn() {
    	return createColumn(DEFAULT_DATATYPE, RelationalColumn.DEFAULT_STRING_LENGTH);
    }
    
    public RelationalColumn createColumn(String datatype, int length) {
    	return createColumn("newColumn_" + (getColumns().size() + 1), datatype, length); //$NON-NLS-1$
    }
    
    public RelationalColumn createColumn(String name, String datatype, int length) {
    	RelationalColumn newColumn = new RelationalColumn(name);
    	newColumn.setDatatype(datatype);
    	newColumn.setLength(length);
    	addColumn(newColumn);
    	return newColumn;
    }
    
	public boolean canMoveColumnUp(RelationalColumn column) {
		return getColumnIndex(column) > 0;
	}
	
	public boolean canMoveColumnDown(RelationalColumn column) {
		return getColumnIndex(column) < getColumns().size()-1;
	}
	
	private int getColumnIndex(RelationalColumn column) {
		int i=0;
		for( RelationalColumn existingColumn : getColumns() ) {
			if( existingColumn == column) {
				return i;
			}
			i++;
		}
		
		// Shouldn't ever get here!
		return -1;
	}
	
	public void moveColumnUp(RelationalColumn theColumn) {
		int startIndex = getColumnIndex(theColumn);
		if( startIndex > 0 ) {
			// Make Copy of List & get columnInfo of startIndex-1
			RelationalColumn[] existingColumns = getColumns().toArray(new RelationalColumn[0]);
			RelationalColumn priorColumn = existingColumns[startIndex-1];
			existingColumns[startIndex-1] = theColumn;
			existingColumns[startIndex] = priorColumn;
			
			List<RelationalColumn> newColumns = new ArrayList<RelationalColumn>(existingColumns.length);
			for( RelationalColumn info : existingColumns) {
				newColumns.add(info);
			}
			
			this.columns = newColumns;
		}
	}
	
	public void moveColumnDown(RelationalColumn theColumn) {
		int startIndex = getColumnIndex(theColumn);
		if( startIndex < (getColumns().size()-1) ) {
			// Make Copy of List & get columnInfo of startIndex+1
			RelationalColumn[] existingColumns = getColumns().toArray(new RelationalColumn[0]);
			RelationalColumn afterColumn = existingColumns[startIndex+1];
			existingColumns[startIndex+1] = theColumn;
			existingColumns[startIndex] = afterColumn;
			
			List<RelationalColumn> newColumns = new ArrayList<RelationalColumn>(existingColumns.length);
			for( RelationalColumn info : existingColumns) {
				newColumns.add(info);
			}
			
			this.columns = newColumns;
		}
	}
	
	@Override
	public void handleInfoChanged() {
		super.handleInfoChanged();
		
		// Set extension properties here??
		
		if( this.nativeQuery != null ) {
			getExtensionProperties().put(NATIVE_QUERY, this.nativeQuery );
		} else getExtensionProperties().remove(NATIVE_QUERY);
			
	}
	
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		if( getStatus().getSeverity() == IStatus.ERROR ) {
			return;
		}
		
		if( this.isMaterialized() && this.materializedTableName == null ) {
			setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_error_materializedTableHasNoTableDefined ));
			return;
		}
		
		if( this.getPrimaryKey() != null && !this.getPrimaryKey().getStatus().isOK()) {
			setStatus(this.getPrimaryKey().getStatus());
			return;
		}

		if (getUniqueConstraints() != null) {
		    for (RelationalUniqueConstraint uniqueConstraint : getUniqueConstraints()) {
		        if( uniqueConstraint != null && !uniqueConstraint.getStatus().isOK()) {
		            setStatus(uniqueConstraint.getStatus());
		            return;
		        }
		    }
		}

		for( RelationalForeignKey fk : this.getForeignKeys() ) {
			if( !fk.getStatus().isOK()) {
				setStatus(fk.getStatus());
				return;
			}
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
								NLS.bind(Messages.validate_error_duplicateColumnNamesInTable, getName())));
						return;
					}
				}
			}
		}
		
		if( this.getColumns().isEmpty() ) {
			if( this.getParent() != null && this.getParent() instanceof RelationalProcedure ) {
				setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
						Messages.validate_warning_noColumnsDefinedForResultSet ));
				return;
			} else {
				setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_warning_noColumnsDefined ));
				return;
			}
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
        final RelationalTable other = (RelationalTable)object;

        // string properties
        if (!CoreStringUtil.valuesAreEqual(getNativeQuery(), other.getNativeQuery())) {
            return false;
        }
        
        if( !(getCardinality()==other.getCardinality()) ||  
            !(getSupportsUpdate()==other.getSupportsUpdate()) ||
            !(isMaterialized()==other.isMaterialized()) ||
        	!(isSystem()==other.isSystem())) {
        	return false;
        }
 
        if (materializedTableName == null) {
            if (other.materializedTableName != null)
                return false;   
        } else if (!materializedTableName.equals(other.materializedTableName))
            return false;

        if (materializedTableModelPath == null) {
            if (other.materializedTableModelPath != null)
                return false;   
        } else if (!materializedTableModelPath.equals(other.materializedTableModelPath))
            return false;
        
        if (uniqueConstraints == null) {
            if (other.uniqueConstraints != null)
                return false;   
        } else if (!uniqueConstraints.equals(other.uniqueConstraints))
            return false;

        if (primaryKey == null) {
            if (other.primaryKey != null)
                return false;   
        } else if (!primaryKey.equals(other.primaryKey))
            return false;

        // Columns
        List<RelationalColumn> thisColumns = getColumns();
        List<RelationalColumn> thatColumns = other.getColumns();

        if (thisColumns.size() != thatColumns.size()) {
            return false;
        }
        
        if (!thisColumns.isEmpty() && !thisColumns.equals(thatColumns)) {
            return false;
        }
        
        // ForeignKeys
        List<RelationalForeignKey> thisFKs = getForeignKeys();
        List<RelationalForeignKey> thatFKs = other.getForeignKeys();

        if (thisFKs.size() != thatFKs.size()) {
            return false;
        }
        
        if (thisFKs.size()==1) {
        	if(!thisFKs.get(0).equals(thatFKs.get(0))) {
        		return false;
        	}
        } else if(thisFKs.size()>1){
            ReferenceComparator comparator = new ReferenceComparator();
            List<RelationalForeignKey> sortedThisFKs = new ArrayList<RelationalForeignKey>(getForeignKeys());
            List<RelationalForeignKey> sortedThatFKs = new ArrayList<RelationalForeignKey>(other.getForeignKeys());
            Collections.sort(sortedThisFKs,comparator);
            Collections.sort(sortedThatFKs,comparator);
            
            if (!sortedThisFKs.equals(sortedThatFKs)) {
                return false;
            }
        }
        
        // Indexes
        List<RelationalIndex> thisIndexes = getIndexes();
        List<RelationalIndex> thatIndexes = other.getIndexes();

        if (thisIndexes.size() != thatIndexes.size()) {
            return false;
        }
        
        if (!thisIndexes.isEmpty() && !thisIndexes.equals(thatIndexes)) {
            return false;
        }

        // AccessPatterns
        List<RelationalAccessPattern> thisAPs = getAccessPatterns();
        List<RelationalAccessPattern> thatAPs = other.getAccessPatterns();

        if (thisAPs.size() != thatAPs.size()) {
            return false;
        }
        
        if (!thisAPs.isEmpty() && !thisAPs.equals(thatAPs)) {
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
        if (!CoreStringUtil.isEmpty(getNativeQuery())) {
            result = HashCodeUtil.hashCode(result, getNativeQuery());
        }
        
        result = HashCodeUtil.hashCode(result, getCardinality());
        result = HashCodeUtil.hashCode(result, getSupportsUpdate());
        result = HashCodeUtil.hashCode(result, isMaterialized());
        result = HashCodeUtil.hashCode(result, isSystem());
        
        if(materializedTableName!=null) {
            result = HashCodeUtil.hashCode(result, materializedTableName);
        }
        if(materializedTableModelPath!=null) {
            result = HashCodeUtil.hashCode(result, materializedTableModelPath);
        }
        if(uniqueConstraints!=null) {
            result = HashCodeUtil.hashCode(result, uniqueConstraints);
        }
        if(primaryKey!=null) {
            result = HashCodeUtil.hashCode(result, primaryKey);
        }

        List<RelationalColumn> cols = getColumns();
        for(RelationalColumn col: cols) {
            result = HashCodeUtil.hashCode(result, col);
        }
        
        List<RelationalForeignKey> fks = getForeignKeys();
        for(RelationalForeignKey fk: fks) {
            result = HashCodeUtil.hashCode(result, fk);
        }

        List<RelationalIndex> indexes = getIndexes();
        for(RelationalIndex index: indexes) {
            result = HashCodeUtil.hashCode(result, index);
        }

        List<RelationalAccessPattern> aps = getAccessPatterns();
        for(RelationalAccessPattern ap: aps) {
            result = HashCodeUtil.hashCode(result, ap);
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
		if( primaryKey != null ) {
			sb.append("\n\t").append("PK = ").append(primaryKey); //$NON-NLS-1$  //$NON-NLS-2$
		}
		if( uniqueConstraints != null ) {
			sb.append("\n\t").append("UC = ").append(uniqueConstraints); //$NON-NLS-1$  //$NON-NLS-2$
		}
		if( !getAccessPatterns().isEmpty() ) {
			sb.append("\n\t").append(getAccessPatterns().size()).append(" access patterns"); //$NON-NLS-1$  //$NON-NLS-2$
			for( RelationalAccessPattern ap : getAccessPatterns() ) {
				sb.append("\n\tap = ").append(ap); //$NON-NLS-1$
			}
		}
		return sb.toString();
	}

}
