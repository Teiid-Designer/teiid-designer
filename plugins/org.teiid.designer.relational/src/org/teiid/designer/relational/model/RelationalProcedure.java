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
public class RelationalProcedure extends RelationalReference {
    public static final String KEY_FUNCTION = "FUNCTION"; //$NON-NLS-1$
    public static final String KEY_UPDATE_COUNT = "UPDATECOUNT"; //$NON-NLS-1$
    
    public static final boolean DEFAULT_FUNCTION = false;
    public static final String DEFAULT_UPDATE_COUNT = "AUTO"; //$NON-NLS-1$
    
    private boolean function;
    private String  updateCount;
    private Collection<RelationalParameter> parameters;
    private RelationalProcedureResultSet resultSet;
    
    
    public RelationalProcedure() {
        super();
        setType(TYPES.PROCEDURE);
        this.parameters = new ArrayList<RelationalParameter>();
        setNameValidator(new RelationalStringNameValidator(true, true));
    }
    /**
     * @param name
     */
    public RelationalProcedure( String name ) {
        super(name);
        setType(TYPES.PROCEDURE);
        this.parameters = new ArrayList<RelationalParameter>();
        setNameValidator(new RelationalStringNameValidator(true, true));
    }

    /**
     * @return function
     */
    public boolean isFunction() {
        return function;
    }
    /**
     * @param function Sets function to the specified value.
     */
    public void setFunction( boolean function ) {
        this.function = function;
    }
    /**
     * @return updateCount
     */
    public String getUpdateCount() {
        return updateCount;
    }
    /**
     * @param updateCount Sets updateCount to the specified value.
     */
    public void setUpdateCount( String updateCount ) {
        this.updateCount = updateCount;
    }
    /**
     * @return resultSet
     */
    public RelationalProcedureResultSet getResultSet() {
        return resultSet;
    }
    /**
     * @param resultSet Sets resultSet to the specified value.
     */
    public void setResultSet( RelationalProcedureResultSet resultSet ) {
        this.resultSet = resultSet;
    }
    /**
     * @return columns
     */
    public Collection<RelationalParameter> getParameters() {
        return this.parameters;
    }
    
    public void addParameter(RelationalParameter parameter) {
        this.parameters.add(parameter);
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
            } else if(keyStr.equalsIgnoreCase(KEY_FUNCTION) ) {
                setFunction(Boolean.parseBoolean(value));
            } else if(keyStr.equalsIgnoreCase(KEY_UPDATE_COUNT) ) {
                setUpdateCount(value);
            }
        }
    }
}
