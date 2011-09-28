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
    

    private int  cardinality;
    private boolean materialized;
    private RelationalReference   materializedTable;
    private boolean supportsUpdate;
    private boolean system;
    private Collection<RelationalColumn> columns;
    private Collection<RelationalPrimaryKey> primaryKeys;
    private Collection<RelationalUniqueConstraint> uniqueContraints;
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
        this.primaryKeys = new ArrayList<RelationalPrimaryKey>();
        this.uniqueContraints = new ArrayList<RelationalUniqueConstraint>();
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
        this.cardinality = cardinality;
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
        this.materialized = materialized;
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
        this.materializedTable = materializedTable;
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
        this.supportsUpdate = supportsUpdate;
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
        this.system = system;
    }

    /**
     * @return columns
     */
    public Collection<RelationalColumn> getColumns() {
        return columns;
    }
    
    public void addColumn(RelationalColumn column) {
        column.setParent(this);
        this.columns.add(column);
    }

    /**
     * @return primaryKeys
     */
    public Collection<RelationalPrimaryKey> getPrimaryKeys() {
        return primaryKeys;
    }

    public void addPrimaryKey(RelationalPrimaryKey pk) {
        pk.setParent(this);
        this.primaryKeys.add(pk);
    }
    
    /**
     * @return uniqueContraints
     */
    public Collection<RelationalUniqueConstraint> getUniqueContraints() {
        return uniqueContraints;
    }
    
    public void addUniqueConstraint(RelationalUniqueConstraint uc) {
        uc.setParent(this);
        this.uniqueContraints.add(uc);
    }
    
    /**
     * @return accessPatterns
     */
    public Collection<RelationalAccessPattern> getAccessPatterns() {
        return accessPatterns;
    }
    
    public void addAccessPattern(RelationalAccessPattern ap) {
        ap.setParent(this);
        this.accessPatterns.add(ap);
    }

    /**
     * @return foreignKeys
     */
    public Collection<RelationalForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void addForeignKey(RelationalForeignKey fk) {
        this.foreignKeys.add(fk);
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

}
