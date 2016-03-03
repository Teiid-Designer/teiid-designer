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
public class RelationalProcedure extends RelationalReference {
	public enum PROCEDURE_TYPE {
		PROCEDURE, FUNCTION, SOURCE_FUNCTION, NATIVE_QUERY_PROCEDURE
	}
	 
    /**
     * 
     */
    public static final String KEY_FUNCTION = "FUNCTION"; //$NON-NLS-1$
    /**
     * 
     */
	public static final String KEY_UPDATE_COUNT = "UPDATECOUNT"; //$NON-NLS-1$
	/**
     * 
     */
    public static final boolean DEFAULT_FUNCTION = false;
    /**
     * 
     */
    public static final String DEFAULT_UPDATE_COUNT = "AUTO"; //$NON-NLS-1$
    /**
     * 
     */
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    
    private boolean nonPrepared = false;
    private boolean deterministic = false;
    private boolean returnsNullOnNull = false;
    private boolean variableArguments = false;
    private boolean aggregate = false;
    private boolean allowsDistinct = false;
    private boolean allowsOrderBy = false;
    private boolean analytic = false;
    private boolean decomposable = false;
    private boolean useDistinctRows = false;
    private String nativeQuery;
    private String javaClass;
    private String javaMethod;
    private String udfJarPath;
    private String functionCategory;
    boolean nativeQueryProcedure;
    
    PROCEDURE_TYPE procedureType;
    
    private String  updateCount;
    private List<RelationalParameter> parameters;
    private RelationalProcedureResultSet resultSet;
    
    /**
     * RelationalProcedure constructor
     */
    public RelationalProcedure() {
        super();
        setType(TYPES.PROCEDURE);
        this.parameters = new ArrayList<RelationalParameter>();
        setNameValidator(new RelationalStringNameValidator(true, true));
    }
    
    /**
     * RelationalProcedure constructor
     * @param name the procedure name
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
    	return this.procedureType == PROCEDURE_TYPE.FUNCTION || this.procedureType == PROCEDURE_TYPE.SOURCE_FUNCTION;
    }
    
    /**
     * @param function Sets function to the specified value.
     */
    public void setFunction( boolean function ) {
        if( function ) {
        	setProcedureType(PROCEDURE_TYPE.FUNCTION);
        } else {
        	setProcedureType(PROCEDURE_TYPE.PROCEDURE);
        }
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
     * @return is source function
     */
    public boolean isSourceFunction() {
    	return this.procedureType == PROCEDURE_TYPE.SOURCE_FUNCTION;
    }

    /**
     * @return is native query procedure
     */
    public boolean isNativeQueryProcedure() {
        return this.procedureType == PROCEDURE_TYPE.NATIVE_QUERY_PROCEDURE;
    }
    
    /**
     * 
     */
    public PROCEDURE_TYPE getProcedureType() {
		return procedureType;
	}

    /**
     * 
     */
	public void setProcedureType(PROCEDURE_TYPE type) {
		this.procedureType = type;
	}

	/**
     * @return java class name for function may be null
     */
    public String getJavaClassName() {
        return javaClass;
    }
    
    /**
     * @param javaClassName sets java class name to the specified value. may be null
     */
    public void setJavaClassName( String javaClassName ) {
    	if( StringUtilities.areDifferent(this.javaClass, javaClassName) ) {
	        this.javaClass = javaClassName;
    		 handleInfoChanged();
    	}
    }
    
    /**
     * @return java class name for function may be null
     */
    public String getJavaMethodName() {
        return javaMethod;
    }
    
    /**
     * @param javaMethodName sets java method name to the specified value. may be null
     */
    public void setJavaMethodName( String javaMethodName ) {
    	if( StringUtilities.areDifferent(this.javaMethod, javaMethodName) ) {
	        this.javaMethod = javaMethodName;
    		 handleInfoChanged();
    	}
    }
    
    /**
     * @return udf jar path
     */
    public String getUdfJarPath() {
        return udfJarPath;
    }
    
    /**
     * @param udfJarPath sets relative udf jar path. may be null
     */
    public void setUdfJarPath( String udfJarPath ) {
    	if( StringUtilities.areDifferent(this.udfJarPath, udfJarPath) ) {
	        this.udfJarPath = udfJarPath;
    		 handleInfoChanged();
    	}
    }
    
    /**
     * @return function category
     */
    public String getFunctionCategory() {
        return functionCategory;
    }
    
    /**
     * @param category sets user defined function category. may be null
     */
    public void setFunctionCategory( String category ) {
    	if( StringUtilities.areDifferent(this.functionCategory, category) ) {
	        this.functionCategory = category;
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
     * @return resultSet
     */
    public RelationalProcedureResultSet getResultSet() {
        return resultSet;
    }
    /**
     * @param resultSet Sets resultSet to the specified value.
     */
    public void setResultSet( RelationalProcedureResultSet resultSet ) {
    	if( this.resultSet != null ) {
    		this.resultSet.setParent(null);
    	}
        this.resultSet = resultSet;
        if( this.resultSet != null) {
        	this.resultSet.setParent(this);
        }
    }
    /**
     * @return parameters
     */
    public List<RelationalParameter> getParameters() {
        return this.parameters;
    }
    
    /**
     * @param parameter the new parameter
     */
    public void addParameter(RelationalParameter parameter) {
        if( this.parameters.add(parameter) ) {
        	parameter.setParent(this);
    		handleInfoChanged();
        }
    }
    
    /**
     * @param parameter the parameter to remove
     * @return if parameter was removed or not
     */
    public boolean removeParameter(RelationalParameter parameter) {
    	if( this.parameters.remove(parameter) ) {
    		handleInfoChanged();
    		return true;
    	}
    	return false;
    }
    
    /**
     * @return the new RelationalParameter
     */
    public RelationalParameter createParameter() {
    	return createParameter(DEFAULT_DATATYPE, RelationalParameter.DEFAULT_STRING_LENGTH);
    }
    
    /**
     * @param datatype the datatype name
     * @param length the datatype length
     * @return the new RelationalParameter
     */
    public RelationalParameter createParameter(String datatype, int length) {
    	return createParameter("newParameter_" + (getParameters().size() + 1), datatype, length); //$NON-NLS-1$
    }
    
    /**
     * @param name the name of the parameter
     * @param datatype the datatype name
     * @param length the datatype length
     * @return the new RelationalParameter
     */
    public RelationalParameter createParameter(String name, String datatype, int length) {
    	RelationalParameter newParameter = new RelationalParameter(name);
    	newParameter.setDatatype(datatype);
    	newParameter.setLength(length);
    	addParameter(newParameter);
    	return newParameter;
    }
    
	/**
	 * @param parameter the parameter
	 * @return if parameter can be moved up in child list
	 */
	public boolean canMoveParameterUp(RelationalParameter parameter) {
		return getParameterIndex(parameter) > 0;
	}
	
	/**
	 * @param parameter the parameter
	 * @return if parameter can be moved down in child list
	 */
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
	
	/**
	 * @param theParameter the parameter
	 */
	public void moveParameterUp(RelationalParameter theParameter) {
		int startIndex = getParameterIndex(theParameter);
		if( startIndex > 0 ) {
			// Make Copy of List & get parameterInfo of startIndex-1
			RelationalParameter[] existingParameters = getParameters().toArray(new RelationalParameter[0]);
			RelationalParameter priorParameter = existingParameters[startIndex-1];
			existingParameters[startIndex-1] = theParameter;
			existingParameters[startIndex] = priorParameter;
			
			List<RelationalParameter> newParameters = new ArrayList<RelationalParameter>(existingParameters.length);
			for( RelationalParameter info : existingParameters) {
				newParameters.add(info);
			}
			
			this.parameters = newParameters;
		}
	}
	
	/**
	 * @param theParameter the parameter
	 */
	public void moveParameterDown(RelationalParameter theParameter) {
		int startIndex = getParameterIndex(theParameter);
		if( startIndex < (getParameters().size()-1) ) {
			// Make Copy of List & get parameterInfo of startIndex+1
			RelationalParameter[] existingParameters = getParameters().toArray(new RelationalParameter[0]);
			RelationalParameter afterParameter = existingParameters[startIndex+1];
			existingParameters[startIndex+1] = theParameter;
			existingParameters[startIndex] = afterParameter;
			
			List<RelationalParameter> newParameters = new ArrayList<RelationalParameter>(existingParameters.length);
			for( RelationalParameter info : existingParameters) {
				newParameters.add(info);
			}
			
			this.parameters = newParameters;
		}
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
            } else if(keyStr.equalsIgnoreCase(KEY_FUNCTION) ) {
            	if( Boolean.parseBoolean(value) ) {
            		setProcedureType(PROCEDURE_TYPE.FUNCTION);
            	}
            } else if(keyStr.equalsIgnoreCase(KEY_UPDATE_COUNT) ) {
                setUpdateCount(value);
            }
        }
    }
    
	@Override
	public void handleInfoChanged() {
		super.handleInfoChanged();
		
		// Set extension properties here??
		
		if( this.isFunction() ) {
			if( this.functionCategory != null ) {
				getExtensionProperties().put(FUNCTION_CATEGORY, this.functionCategory );
			} else getExtensionProperties().remove(FUNCTION_CATEGORY);
			
			if( this.javaClass != null ) {
				getExtensionProperties().put(JAVA_CLASS, this.javaClass );
			} else getExtensionProperties().remove(JAVA_CLASS);
			
			if( this.javaMethod != null ) {
				getExtensionProperties().put(JAVA_METHOD, this.javaMethod );
			} else getExtensionProperties().remove(JAVA_METHOD);
			
			if( this.udfJarPath != null ) {
				getExtensionProperties().put(UDF_JAR_PATH, this.udfJarPath );
			} else getExtensionProperties().remove(UDF_JAR_PATH);
			
			getExtensionProperties().put(AGGREGATE, Boolean.toString(this.isAggregate()) );
			
			getExtensionProperties().put(VARARGS, Boolean.toString(this.isVariableArguments()) );
			
			getExtensionProperties().put(DETERMINISTIC, Boolean.toString(this.isDeterministic()) );
			
			getExtensionProperties().put(NULL_ON_NULL, Boolean.toString(this.isReturnsNullOnNull()) );
			
			// If aggregate == FALSE 
			if( this.isAggregate() ) {
				getExtensionProperties().remove(ANALYTIC);
				getExtensionProperties().remove(ALLOWS_ORDER_BY);
				getExtensionProperties().remove(USES_DISTINCT_ROWS);
				getExtensionProperties().remove(DECOMPOSABLE);
				getExtensionProperties().remove(ALLOWS_DISTINCT);
			} else {
				getExtensionProperties().put(ANALYTIC, Boolean.toString(this.isAnalytic()));
				getExtensionProperties().put(ALLOWS_ORDER_BY, Boolean.toString(this.isAllowsOrderBy()));
				getExtensionProperties().put(USES_DISTINCT_ROWS, Boolean.toString(this.isUseDistinctRows()));
				getExtensionProperties().put(DECOMPOSABLE, Boolean.toString(this.isDecomposable()));
				getExtensionProperties().put(ALLOWS_DISTINCT, Boolean.toString(this.isAllowsDistinct()));
			}
			
			getExtensionProperties().remove(NATIVE_QUERY);
		} else {
			if( this.nativeQuery != null ) {
				getExtensionProperties().put(NATIVE_QUERY, this.nativeQuery );
			} else getExtensionProperties().remove(NATIVE_QUERY);
			
			getExtensionProperties().put(NON_PREPARED, Boolean.toString(this.isNonPrepared()));
			
	        // make sure model object does not have these extension properties for when function is false
			getExtensionProperties().remove(DETERMINISTIC);
			getExtensionProperties().remove(JAVA_CLASS);
			getExtensionProperties().remove(JAVA_METHOD);
			getExtensionProperties().remove(FUNCTION_CATEGORY);
			getExtensionProperties().remove(UDF_JAR_PATH);
			getExtensionProperties().remove(VARARGS);
			getExtensionProperties().remove(NULL_ON_NULL);
			getExtensionProperties().remove(AGGREGATE);
			getExtensionProperties().remove(ANALYTIC);
			getExtensionProperties().remove(ALLOWS_ORDER_BY);
			getExtensionProperties().remove(USES_DISTINCT_ROWS);
			getExtensionProperties().remove(DECOMPOSABLE);
			getExtensionProperties().remove(ALLOWS_DISTINCT);
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
			
			if( this.isSourceFunction() ) {
				if( getResultSet() != null ) {
					setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
							Messages.validate_noResultSetAllowedInFunction ));
					return;
				}
			} else {
				// Check for null category, class or method name
				if( this.functionCategory == null || this.functionCategory.trim().length() == 0 ) {
					setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
							Messages.validate_categoryUndefinedForUDF ));
					return;
				}
				if( this.javaClass == null || this.javaClass.trim().length() == 0 ) {
					setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
							Messages.validate_javaClassUndefinedForUDF ));
					return;
				}
				if( this.javaMethod == null || this.javaMethod.trim().length() == 0 ) {
					setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
							Messages.validate_javaMethodUndefinedForUDF ));
					return;
				}
			}
		} else {
			if( getResultSet() != null ) {
				if( getResultSet().getStatus().getSeverity() == IStatus.ERROR ) {
					setStatus(new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, getResultSet().getStatus().getMessage() ));
					return;
				}
				
				if( getResultSet().getStatus().getSeverity() == IStatus.WARNING ) {
					setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, getResultSet().getStatus().getMessage() ));
					return;
				}
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
        final RelationalProcedure other = (RelationalProcedure)object;

        // string properties
        if (!CoreStringUtil.valuesAreEqual(getNativeQuery(), other.getNativeQuery()) ||
        		!CoreStringUtil.valuesAreEqual(getFunctionCategory(), other.getFunctionCategory()) ||
        		!CoreStringUtil.valuesAreEqual(getJavaClassName(), other.getJavaClassName()) || 
        		!CoreStringUtil.valuesAreEqual(getJavaMethodName(), other.getJavaMethodName()) ||
        		!CoreStringUtil.valuesAreEqual(getUdfJarPath(), other.getUdfJarPath()) ||
        		!CoreStringUtil.valuesAreEqual(getUpdateCount(), other.getUpdateCount()) ) {
            return false;
        }
        
        if( !(isAggregate()==other.isAggregate()) ||  
            !(isAllowsDistinct()==other.isAllowsDistinct()) ||
            !(isAllowsOrderBy()==other.isAllowsOrderBy()) ||
            !(isAnalytic()==other.isAnalytic()) ||
            !(isDecomposable()==other.isDecomposable()) ||
            !(isDeterministic()==other.isDeterministic()) ||
            !(isFunction()==other.isFunction()) ||
            !(isNonPrepared()==other.isNonPrepared()) ||
            !(isReturnsNullOnNull()==other.isReturnsNullOnNull()) ||
            !(isSourceFunction()==other.isSourceFunction()) ||
            !(isUseDistinctRows()==other.isUseDistinctRows()) ||
            !(isVariableArguments()==other.isVariableArguments()) ) {
        	return false;
        }
        
        // ResultSet
        if (resultSet == null) {
            if (other.resultSet != null)
                return false;   
        } else if (!resultSet.equals(other.resultSet))
            return false;
                
        // Parameters
        Collection<RelationalParameter> thisParameters = getParameters();
        Collection<RelationalParameter> thatParameters = other.getParameters();

        if (thisParameters.size() != thatParameters.size()) {
            return false;
        }
        
        if (!thisParameters.isEmpty() && !thisParameters.equals(thatParameters)) {
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
        if (!CoreStringUtil.isEmpty(getFunctionCategory())) {
            result = HashCodeUtil.hashCode(result, getFunctionCategory());
        }
        if (!CoreStringUtil.isEmpty(getJavaClassName())) {
            result = HashCodeUtil.hashCode(result, getJavaClassName());
        }
        if (!CoreStringUtil.isEmpty(getJavaMethodName())) {
            result = HashCodeUtil.hashCode(result, getJavaMethodName());
        }
        if (!CoreStringUtil.isEmpty(getUdfJarPath())) {
            result = HashCodeUtil.hashCode(result, getUdfJarPath());
        }
        if (!CoreStringUtil.isEmpty(getUpdateCount())) {
            result = HashCodeUtil.hashCode(result, getUpdateCount());
        }

		result = HashCodeUtil.hashCode(result, isAggregate());
        result = HashCodeUtil.hashCode(result, isAllowsDistinct());
        result = HashCodeUtil.hashCode(result, isAllowsOrderBy());
        result = HashCodeUtil.hashCode(result, isAnalytic());
		result = HashCodeUtil.hashCode(result, isDecomposable());
        result = HashCodeUtil.hashCode(result, isDeterministic());
        result = HashCodeUtil.hashCode(result, isFunction());
        result = HashCodeUtil.hashCode(result, isNonPrepared());
		result = HashCodeUtil.hashCode(result, isReturnsNullOnNull());
        result = HashCodeUtil.hashCode(result, isSourceFunction());
        result = HashCodeUtil.hashCode(result, isUseDistinctRows());
        result = HashCodeUtil.hashCode(result, isVariableArguments());
        
        if(resultSet!=null) {
            result = HashCodeUtil.hashCode(result, resultSet);
        }

        List<RelationalParameter> params = getParameters();
        for(RelationalParameter param: params) {
            result = HashCodeUtil.hashCode(result, param);
        }

        return result;
    }    
	
}
