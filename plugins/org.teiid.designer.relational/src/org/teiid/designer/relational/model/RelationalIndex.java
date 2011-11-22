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

import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;


/**
 * 
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
    
    public RelationalIndex() {
        super();
        setType(TYPES.INDEX);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
    }
    
    /**
     * @param name
     */
    public RelationalIndex( String name ) {
        super(name);
        setType(TYPES.INDEX);
        this.columns = new ArrayList<RelationalColumn>();
        setNameValidator(new RelationalStringNameValidator(false, true));
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
}
