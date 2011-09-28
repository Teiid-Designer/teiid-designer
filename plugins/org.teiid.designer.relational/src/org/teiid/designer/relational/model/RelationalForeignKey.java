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
    
    public RelationalForeignKey() {
        super();
        setType(TYPES.FK);
        this.columns = new ArrayList<RelationalColumn>();
    }
    
    /**
     * @param name
     */
    public RelationalForeignKey( String name ) {
        super(name);
        setType(TYPES.FK);
        this.columns = new ArrayList<RelationalColumn>();
    }
    
    /**
     * @return columns
     */
    public Collection<RelationalColumn> getColumns() {
        return columns;
    }

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
}
