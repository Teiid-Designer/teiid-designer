/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;

import com.metamatrix.core.util.CoreArgCheck;
import com.metamatrix.core.util.StringUtilities;
import com.metamatrix.metamodels.relational.RelationalPlugin;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.validation.rules.StringNameValidator;

/**
 * Class to hold basic Pushdown-function data structure and method access to key values
 */

public class PushdownFunctionData {
	String name;
	String description;
	boolean deterministic;
	String nameInSource;
	
	Collection<ParameterData> inputParameters;
	ParameterData returnParameter;
	public static String DEFAULT_TYPE = "string"; //$NON-NLS-1$
	public static int DEFAULT_LENGTH = 255;
	public static String DETERMINISTIC_PROPERTY_KEY = "ext-custom:determistic"; //$NON-NLS-1$
	
	private StringNameValidator validator = new StringNameValidator(1, 200, false);
    	
	public PushdownFunctionData() {
		super();
		this.inputParameters = new ArrayList<ParameterData>();
	}
	
	public void setToDefault(String name, String description, int numParams, int maxParams) {
		setName(name);
		setDescription(description);
		ParameterData param = null;
		Collection<ParameterData> params = new ArrayList<ParameterData>(5);
		for( int i=0; i<numParams; i++ ) {
    		//param = new ParameterData("param_"+Integer.toString(i+1), defaultType, defaultLength);
    		param = new ParameterData(StringUtilities.EMPTY_STRING, PushdownFunctionData.DEFAULT_TYPE, PushdownFunctionData.DEFAULT_LENGTH);
    		param.include();
    		params.add(param);
		}
		for( int i=numParams; i<maxParams; i++ ) {
			//param = new ParameterData("param_"+Integer.toString(i+1), defaultType, defaultLength);
			param = new ParameterData(StringUtilities.EMPTY_STRING, PushdownFunctionData.DEFAULT_TYPE, PushdownFunctionData.DEFAULT_LENGTH);
			params.add(param);
		}
		setInputParameters(params);
		setReturnParameterName(StringUtilities.EMPTY_STRING);
		setReturnParameterLength(PushdownFunctionData.DEFAULT_LENGTH);
		setReturnParameterType(PushdownFunctionData.DEFAULT_TYPE);
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getNameInSource() {
		return this.nameInSource;
	}

	public void setNameInSource(String nameInSource) {
		this.nameInSource = nameInSource;
	}
	
	public boolean getDeterministic() {
		return this.deterministic;
	}

	public void setDeterministic(boolean deterministic) {
		this.deterministic = deterministic;
	}

	public String getReturnParameterName() {
		if( this.returnParameter != null ) {
			return this.returnParameter.getName();
		}
		return null;
	}
	
	public ParameterData getReturnParameterData() {
		return this.returnParameter;
	}

	public void setReturnParameterName(String returnParameterName) {
		if( this.returnParameter == null ) {
			this.returnParameter = new ParameterData(returnParameterName, PushdownFunctionData.DEFAULT_TYPE, PushdownFunctionData.DEFAULT_LENGTH);
		} else {
			this.returnParameter.setName(returnParameterName);
		}
	}

	public String getReturnParameterType() {
		if( this.returnParameter != null ) {
			return this.returnParameter.getType();
		}
		return null;
	}
	
	public int getReturnParameterLength() {
		if( this.returnParameter != null ) {
			return this.returnParameter.getLength();
		}
		return 255;
	}
	
	public void setReturnParameterLength(int length) {
		if( this.returnParameter != null ) {
			this.returnParameter.setLength(length);
		}
	}

	public void setReturnParameterType(String returnParameterType) {
		if( this.returnParameter != null ) {
			this.returnParameter.setType(returnParameterType);
		}
	}

	public Collection<ParameterData> getInputParameters() {
		return this.inputParameters;
	}
	
	public void addInputParameter(String name, String type, int length) {
		CoreArgCheck.isNotNull(name, "name"); //$NON-NLS-1$
		CoreArgCheck.isNotNull(name, "type"); //$NON-NLS-1$
		if( !inputParameterExists(name) ) {
			this.inputParameters.add(new ParameterData(name, type, length));
		}
	}
	
	public void setInputParameters(Collection<ParameterData> params) {
		this.inputParameters.clear();
		this.inputParameters.addAll(params);
	}
	
	public void addInputParameter(ParameterData param) {
		CoreArgCheck.isNotNull(param, "data"); //$NON-NLS-1$
		if( !inputParameterExists(param.getName()) ) {
			this.inputParameters.add(param);
		}
	}
	
	public boolean inputParameterExists(String name) {
		for( ParameterData data : this.inputParameters ) {
			if( data.getName().equalsIgnoreCase(name) ) {
				return true;
			}
		}
		
		return false;
	}
	
	public ParameterData getParameterData(int index) {
		int nParams = this.inputParameters.size();
		
		if( index < nParams) {
			return (ParameterData)((ArrayList)this.inputParameters).get(index);
		}
		
		return null;
	}
	
	public void setInputParameterData(int index, String name, String type, int length) {
		int nParams = this.inputParameters.size();
		
		if( index < nParams) {
			ParameterData data = (ParameterData)((ArrayList)this.inputParameters).get(index);
			data.setName(name);
			data.setType(type);
			data.setLength(length);
		}
	}
	
	public void removeInputLastParameter() {
		// Should only remove the "last" parameter in the lists
		int nParams = this.inputParameters.size();
		
		if( nParams > 0 ) {
			((ArrayList)this.inputParameters).remove(nParams-1);
		}
	}
	
	/**
	 * 
	 * @return status the IStatus
	 */
	public IStatus validate() {
		IStatus status = new Status(IStatus.OK, RelationalPlugin.PLUGIN_ID, "OK"); //$NON-NLS-1$
		
		String errorMessage = this.validator.checkValidName(this.name);
		
		if( errorMessage != null ) {
			return new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, errorMessage);
		}
		Collection<String> paramNames = new ArrayList<String>();
		for( ParameterData param : getInputParameters() ) {
			if( param.isIncluded() ) {
    			errorMessage = this.validator.checkValidName(param.getName());
    			if( errorMessage != null ) {
        			return new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
        					RelationalPlugin.Util.getString("PushdownFunctionData.invalidInputParameterName", errorMessage)); //$NON-NLS-1$
        		}
    			int length = param.getLength();
    			if( length < 0 || length > 65000 ) {
    				return new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID,
    						RelationalPlugin.Util.getString("PushdownFunctionData.invalidParameterLength", Integer.toString(length))); //$NON-NLS-1$
    			}
    			if( paramNames.contains(param.getName().toUpperCase())) {
    				return new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
    						RelationalPlugin.Util.getString("PushdownFunctionData.duplicateInputParameterName", param.getName())); //$NON-NLS-1$
    			}
    			
    			paramNames.add(param.getName().toUpperCase());
			}
		}
		errorMessage = this.validator.checkValidName(getReturnParameterName());
		if( errorMessage != null ) {
			return new Status(IStatus.ERROR, RelationalPlugin.PLUGIN_ID, 
					RelationalPlugin.Util.getString("PushdownFunctionData.invalidReturnParameterName", errorMessage)); //$NON-NLS-1$
		}

		return status;
	}
	
	public EObject getDatatype(String type) {
		EObject datatype = null;
		
		try {
			datatype = ModelerCore.getBuiltInTypesManager().findDatatype(type);
		} catch (ModelerCoreException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return datatype;
	}
}
