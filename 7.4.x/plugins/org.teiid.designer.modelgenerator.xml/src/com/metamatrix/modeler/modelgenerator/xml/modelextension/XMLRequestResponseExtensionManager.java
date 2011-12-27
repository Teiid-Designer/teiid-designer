/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.xml.modelextension;

import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.RelationalEntity;

public interface XMLRequestResponseExtensionManager extends BaseXMLRelationalExtensionManager {

	public abstract void setColumnRoleAttribute(Column column, Integer role);

	public abstract void setColumnInputParamAttribute(Column column,
			Boolean input);

	public abstract void setXPathRootForInputAttribute(RelationalEntity table,
			String xrfi_attribute_value);

	public abstract void setColumnXPathForInput(Column relColumn, String xpath);

	public abstract void setAllowEmptyInputElement(Column relCol,
			boolean request);

	public abstract void setMultipleValue(Column relColumn, Integer value);

}
