/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.jdbc;

public interface Column extends DatabaseElement {

	public boolean isAttributeOfParent();

	public void setIsAttributeOfParent(boolean isAttributeOfParent);

	public String getDataAttributeName();

	public void setDataAttributeName(String name);

	public boolean isInputParameter();

	public void setIsInputParameter(boolean isInputParameter);

	public Integer getMultipleValues();

	public void setMultipleValues(Integer multiValues);

	public boolean isRequiredValue();

	public void setIsRequiredValue(boolean isRequired);

	public Integer getRole();

	public void setRole(Integer role);

	public void setDataType(DataType type);

	public DataType getDataType();
	
	public boolean isForeignKey();
	
	public void setIsForeignKey(boolean isForeignKey);
}
