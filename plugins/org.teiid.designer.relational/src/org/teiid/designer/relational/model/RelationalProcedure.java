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
import org.teiid.designer.relational.Messages;
import org.teiid.designer.relational.RelationalPlugin;

import com.metamatrix.metamodels.relational.aspects.validation.RelationalStringNameValidator;


/**
 * 
 */
public class RelationalProcedure extends RelationalReference {
    public static final String KEY_FUNCTION = "FUNCTION"; //$NON-NLS-1$
    public static final String KEY_UPDATE_COUNT = "UPDATECOUNT"; //$NON-NLS-1$
    
    public static final boolean DEFAULT_FUNCTION = false;
    public static final String DEFAULT_UPDATE_COUNT = "AUTO"; //$NON-NLS-1$
    
    public static final String DEFAULT_DATATYPE = "string"; //$NON-NLS-1$
    
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
					}
				}
			}
		}
		
		if( this.getParameters().isEmpty() ) {
			setStatus(new Status(IStatus.WARNING, RelationalPlugin.PLUGIN_ID, 
					Messages.validate_warning_noParametersDefined ));
		}
		
	}
}
