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
public class RelationalProcedure extends RelationalReference {
    public static final String KEY_FUNCTION = "FUNCTION"; //$NON-NLS-1$
    public static final String KEY_UPDATE_COUNT = "UPDATECOUNT"; //$NON-NLS-1$
    
    public static final boolean DEFAULT_FUNCTION = false;
    public static final String DEFAULT_UPDATE_COUNT = "AUTO"; //$NON-NLS-1$
    
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    
    private boolean function;
    private boolean nonPrepared;
    private boolean deterministic;
    private boolean returnsNullOnNull;
    private boolean variableArguments;
    private boolean aggregate;
    private boolean allowsDistinct;
    private boolean allowsOrderBy;
    private boolean analytic;
    private boolean decomposable;
    private boolean useDistinctRows;
    
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
     * @return function
     */
    public boolean isNonPrepared() {
        return nonPrepared;
    }
    /**
     * @param nonPrepared Sets non-prepared to the specified value.
     */
    public void setNonPrepared( boolean nonPrepared ) {
        this.nonPrepared = nonPrepared;
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
     * @return deterministic
     */
    public boolean isDeterministic() {
        return deterministic;
    }
    /**
     * @param deterministic Sets deterministic to the specified value.
     */
    public void setDeterministic( boolean deterministic ) {
        this.deterministic = deterministic;
    }
    
    /**
     * @return returnsNullOnNull
     */
    public boolean isReturnsNullOnNull() {
        return returnsNullOnNull;
    }
    /**
     * @param returnsNullOnNull Sets returnsNullOnNull to the specified value.
     */
    public void setReturnsNullOnNull( boolean returnsNullOnNull ) {
        this.returnsNullOnNull = returnsNullOnNull;
    }
    
    /**
     * @return variableArguments
     */
    public boolean isVariableArguments() {
        return variableArguments;
    }
    /**
     * @param variableArguments Sets variableArguments to the specified value.
     */
    public void setVariableArguments( boolean variableArguments ) {
        this.variableArguments = variableArguments;
    }
    
    /**
     * @return aggregate
     */
    public boolean isAggregate() {
        return aggregate;
    }
    /**
     * @param aggregate Sets aggregate to the specified value.
     */
    public void setAggregate( boolean aggregate ) {
        this.aggregate = aggregate;
    }
    
    /**
     * @return allowsDistinct
     */
    public boolean isAllowsDistinct() {
        return allowsDistinct;
    }
    /**
     * @param allowsDistinct Sets allowsDistinct to the specified value.
     */
    public void setAllowsDistinct( boolean allowsDistinct ) {
        this.allowsDistinct = allowsDistinct;
    }
    
    /**
     * @return allowsOrderBy
     */
    public boolean isAllowsOrderBy() {
        return allowsOrderBy;
    }
    /**
     * @param allowsOrderBy Sets allowsOrderBy to the specified value.
     */
    public void setAllowsOrderBy( boolean allowsOrderBy ) {
        this.allowsOrderBy = allowsOrderBy;
    }
    
    /**
     * @return analytic
     */
    public boolean isAnalytic() {
        return analytic;
    }
    /**
     * @param analytic Sets analytic to the specified value.
     */
    public void setAnalytic( boolean analytic ) {
        this.analytic = analytic;
    }
    
    /**
     * @return decomposable
     */
    public boolean isDecomposable() {
        return decomposable;
    }
    /**
     * @param decomposable Sets decomposable to the specified value.
     */
    public void setDecomposable( boolean decomposable ) {
        this.decomposable = decomposable;
    }
    
    /**
     * @return useDistinctRows
     */
    public boolean isUseDistinctRows() {
        return useDistinctRows;
    }
    /**
     * @param useDistinctRows Sets useDistinctRows to the specified value.
     */
    public void setUseDistinctRows( boolean useDistinctRows ) {
        this.useDistinctRows = useDistinctRows;
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
     * @return parameters
     */
    public Collection<RelationalParameter> getParameters() {
        return this.parameters;
    }
    
    public void addParameter(RelationalParameter parameter) {
        if( this.parameters.add(parameter) ) {
        	parameter.setParent(this);
    		handleInfoChanged();
        }
    }
    
    public boolean removeParameter(RelationalParameter parameter) {
    	if( this.parameters.remove(parameter) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }
    
    public RelationalParameter createParameter() {
    	return createParameter(DEFAULT_DATATYPE, RelationalParameter.DEFAULT_STRING_LENGTH);
    }
    
    public RelationalParameter createParameter(String datatype, int length) {
    	return createParameter("newParameter_" + (getParameters().size() + 1), datatype, length); //$NON-NLS-1$
    }
    
    public RelationalParameter createParameter(String name, String datatype, int length) {
    	RelationalParameter newParameter = new RelationalParameter(name);
    	newParameter.setDatatype(datatype);
    	newParameter.setLength(length);
    	addParameter(newParameter);
    	return newParameter;
    }
    
	public boolean canMoveParameterUp(RelationalParameter parameter) {
		return getParameterIndex(parameter) > 0;
	}
	
	public boolean canMoveParameterDown(RelationalParameter parameter) {
		return getParameterIndex(parameter) < getParameters().size()-1;
	}
	
	private int getParameterIndex(RelationalParameter parameter) {
		int i=0;
		for( RelationalParameter existingParameter : getParameters() ) {
			if( existingParameter == parameter) {
				return i;
			}
			i++;
		}
		
		// Shouldn't ever get here!
		return -1;
	}
	
	public void moveParameterUp(RelationalParameter theParameter) {
		int startIndex = getParameterIndex(theParameter);
		if( startIndex > 0 ) {
			// Make Copy of List & get parameterInfo of startIndex-1
			RelationalParameter[] existingParameters = getParameters().toArray(new RelationalParameter[0]);
			RelationalParameter priorParameter = existingParameters[startIndex-1];
			existingParameters[startIndex-1] = theParameter;
			existingParameters[startIndex] = priorParameter;
			
			Collection<RelationalParameter> newParameters = new ArrayList<RelationalParameter>(existingParameters.length);
			for( RelationalParameter info : existingParameters) {
				newParameters.add(info);
			}
			
			this.parameters = newParameters;
		}
	}
	
	public void moveParameterDown(RelationalParameter theParameter) {
		int startIndex = getParameterIndex(theParameter);
		if( startIndex < (getParameters().size()-1) ) {
			// Make Copy of List & get parameterInfo of startIndex+1
			RelationalParameter[] existingParameters = getParameters().toArray(new RelationalParameter[0]);
			RelationalParameter afterParameter = existingParameters[startIndex+1];
			existingParameters[startIndex+1] = theParameter;
			existingParameters[startIndex] = afterParameter;
			
			Collection<RelationalParameter> newParameters = new ArrayList<RelationalParameter>(existingParameters.length);
			for( RelationalParameter info : existingParameters) {
				newParameters.add(info);
			}
			
			this.parameters = newParameters;
		}
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
    
	@Override
	public void validate() {
		// Walk through the properties for the table and set the status
		super.validate();
		
		// Validate Children
		for( RelationalParameter param : getParameters() ) {
			param.validate();
		}
		
		if( getStatus().getSeverity() == IStatus.ERROR ) {
			return;
		}
		
		// Check Column Status values
		for( RelationalParameter param : getParameters() ) {
			if( param.getStatus().getSeverity() == IStatus.ERROR ) {
				setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, param.getStatus().getMessage() ));
				return;
			}
		}
		
		// Check Column Status values
		for( RelationalParameter outerParam : getParameters() ) {
			for( RelationalParameter innerParam : getParameters() ) {
				if( outerParam != innerParam ) {
					if( outerParam.getName().equalsIgnoreCase(innerParam.getName())) {
						setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
								NLS.bind(Messages.validate_error_duplicateParameterNamesInProcedure, getName())));
						return;
					}
				}
			}
		}
		
		if( this.getParameters().isEmpty() ) {
			setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_warning_noParametersDefined ));
		}
		
		// Check for more than one RETURN parameter if Function
		if( this.isFunction() ) {
			boolean foundResultParam = false;
			for( RelationalParameter param : getParameters() ) {
				if( param.getDirection().equalsIgnoreCase(DIRECTION.RETURN)) {
					if( foundResultParam ) {
						setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
								Messages.validate_error_tooManyResultParametersInFunction ));
						return;
					} else {
						foundResultParam = true;
					}
				}
			}
		}
	}
}
