/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */

package com.metamatrix.metadata.runtime.model;

import com.metamatrix.metadata.runtime.api.DataType;
import com.metamatrix.metadata.runtime.api.ProcedureID;
import com.metamatrix.metadata.runtime.api.ProcedureParameter;
import com.metamatrix.modeler.core.metadata.runtime.MetadataConstants;

public class BasicProcedureParameter implements ProcedureParameter {
	private BasicProcedureID procID;
	private DataType dataType;
        private short parameterType;
        private boolean isOptional;
        private int position;
        private int resultSetPosition;
        private String name;
        private String defaultValue;
	
/**
 * Call constructor to instantiate a procedure parameter object by passing the name that identifies the entity.
 */
    public BasicProcedureParameter(String name, BasicProcedureID procedureID, DataType dType, short paramType, int paramPosition, int resultSetPosition, boolean isOpitional) {
	      procID = procedureID;
	      dataType = dType;
	      parameterType = paramType;
	      position = paramPosition;
          this.name = name;
          this.isOptional = isOpitional;
          this.resultSetPosition = resultSetPosition; 
    }

     public BasicProcedureParameter(String name) {
          this.name = name;
    }

     public BasicProcedureParameter(String name, BasicProcedureID procedureID) {
          this.name = name;
          this.procID = procedureID;
    }

    /**
    * Override the super method so that when the name
    * is returned, it is the name and not the full path for
    * a parameter.
    */
    public String getNameInSource() {
          return getName();
    }

    public DataType getDataType() {
    	if(dataType.getType() == MetadataConstants.DATATYPE_TYPES.RESULT_SET){
    		return dataType;	
    	}
	    if(dataType.getRuntimeType() != null){
	      return dataType.getRuntimeType();
    	}
    	return dataType;
    }
    public DataType getActualDataType(){
    	return dataType;
    }
    public short getParameterType() {
	      return parameterType;
    }
    public boolean isOptional() {
	      return isOptional;
    }
    public int getPosition() {
	      return position;
    }
    public int getResultSetPosition() {
	      return resultSetPosition;
    }
    public String getName(){
        return name;
    }
    public String getDefaultValue(){
        return defaultValue;
    }
    public void setIsOptional(boolean optional) {
	      this.isOptional = optional;
    }
    public void setPosition(int pos) {
	      this.position = pos;
    }
    public void setParameterType(short type) {
	      this.parameterType = type;
    }
    public void setDataType(DataType dataType) {
	      this.dataType = dataType;
    }
     public void setResultSetPosition(int resultSetPosition) {
	      this.resultSetPosition = resultSetPosition;
    }
    public void setName(String name){
        this.name = name;
    }
     public void setDefaultValue(String value){
        this.defaultValue = value;
    }
    /**
     * Returns the procID.
     * @return BasicProcedureID
     */
    public ProcedureID getProcID() {
        return procID;
    }

    /**
     * Sets the procID.
     * @param procID The procID to set
     */
    public void setProcID(BasicProcedureID procID) {
        this.procID = procID;
    }
    /**
     * Returns a string representing the current state of the object.
     * @return the string representation of this instance.
     */
    @Override
    public String toString(){
        return this.name;
    }

}

