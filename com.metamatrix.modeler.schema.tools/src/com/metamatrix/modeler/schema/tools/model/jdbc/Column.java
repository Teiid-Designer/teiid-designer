/* ================================================================================== 
 * JBoss, Home of Professional Open Source. 
 * 
 * Copyright (c) 2000, 2009 MetaMatrix, Inc. and Red Hat, Inc. 
 * 
 * Some portions of this file may be copyrighted by other 
 * contributors and licensed to Red Hat, Inc. under one or more 
 * contributor license agreements. See the copyright.txt file in the 
 * distribution for a full listing of individual contributors. 
 * 
 * This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * ================================================================================== */

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
