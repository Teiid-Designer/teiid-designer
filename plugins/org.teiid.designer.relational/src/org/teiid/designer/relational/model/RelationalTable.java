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
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;

/**
 * 
 */
public class RelationalTable extends RelationalReference {
    public static final String KEY_CARDINALITY = "CARDINALITY"; //$NON-NLS-1$
    public static final String KEY_MATERIALIZED = "MATERIALIZED"; //$NON-NLS-1$
    public static final String KEY_MATERIALIZED_TABLE = "MATERIALIZEDTABLE"; //$NON-NLS-1$
    public static final String KEY_SUPPORTS_UPDATE = "SUPPORTSUPDATE"; //$NON-NLS-1$
    public static final String KEY_SYSTEM = "SYSTEM"; //$NON-NLS-1$
    
    public static final String DEFAULT_CARDINALITY = null;
    public static final boolean DEFAULT_MATERIALIZED = false;
    public static final String DEFAULT_MATERIALIZED_TABLE = null; 
    public static final boolean DEFAULT_SUPPORTS_UPDATE = true; 
    public static final boolean DEFAULT_SYSTEM = false;
    
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$

    private int  cardinality;
    private boolean materialized;
    private RelationalReference   materializedTable;
    private boolean supportsUpdate;
    private boolean system;
    private Collection<RelationalColumn> columns;
    private RelationalPrimaryKey primaryKey;
    private RelationalUniqueConstraint uniqueContraint;
    private Collection<RelationalAccessPattern> accessPatterns;
    private Collection<RelationalForeignKey> foreignKeys;
    
    
    public RelationalTable() {
        super();
        setType(TYPES.TABLE);
        init();
    }
    /**
     * @param name
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
    public RelationalReference getMaterializedTable() {
        return materializedTable;
    }
    /**
     * @param materializedTable Sets materializedTable to the specified value.
     */
    public void setMaterializedTable( RelationalReference materializedTable ) {
    	if( this.materializedTable != materializedTable ) {
    		this.materializedTable = materializedTable;
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
     * @return columns
     */
    public Collection<RelationalColumn> getColumns() {
        return columns;
    }
    
    public void addColumn(RelationalColumn column) {
    	if( this.columns.add(column) ) {
    		column.setParent(this);
    		handleInfoChanged();
    	} 
    }
    
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
     * @return uniqueContraints
     */
    public RelationalUniqueConstraint getUniqueContraint() {
        return uniqueContraint;
    }
    
    public void setUniqueConstraint(RelationalUniqueConstraint uc) {
    	if( this.uniqueContraint != uc ) {
	    	if( uc != null ) {
	    		uc.setParent(this);
	    	}
	        this.uniqueContraint = uc;
	        handleInfoChanged();
    	}
    }
    
    /**
     * @return accessPatterns
     */
    public Collection<RelationalAccessPattern> getAccessPatterns() {
        return accessPatterns;
    }
    
    public void addAccessPattern(RelationalAccessPattern ap) {
    	if( this.accessPatterns.add(ap) ) {
    		ap.setParent(this);
    		handleInfoChanged();
    	}
    }
    
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
    public Collection<RelationalForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void addForeignKey(RelationalForeignKey fk) {
    	if( this.foreignKeys.add(fk) ) {
    		fk.setParent(this);
    		handleInfoChanged();
    	}
    }
    
    public boolean removeForeignKey(RelationalForeignKey fk) {
    	if( this.foreignKeys.remove(fk) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
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
			
			Collection<RelationalColumn> newColumns = new ArrayList<RelationalColumn>(existingColumns.length);
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
			
			Collection<RelationalColumn> newColumns = new ArrayList<RelationalColumn>(existingColumns.length);
			for( RelationalColumn info : existingColumns) {
				newColumns.add(info);
			}
			
			this.columns = newColumns;
		}
	}
	
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		if( getStatus().getSeverity() == IStatus.ERROR ) {
			return;
		}
		
		if( this.isMaterialized() && this.materializedTable == null ) {
			setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_error_materializedTableHasNoTableDefined ));
			return;
		}
		
		if( this.getPrimaryKey() != null && !this.getPrimaryKey().getStatus().isOK()) {
			setStatus(this.getPrimaryKey().getStatus());
			return;
		}
		
		if( this.getUniqueContraint() != null && !this.getUniqueContraint().getStatus().isOK()) {
			setStatus(this.getUniqueContraint().getStatus());
			return;
		}
		
		for( RelationalForeignKey fk : this.getForeignKeys() ) {
			if( !fk.getStatus().isOK()) {
				setStatus(fk.getStatus());
				return;
			}
		}
		
		if( this.getColumns().isEmpty() ) {
			setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_warning_noColumnsDefined ));
		}
	}

}
