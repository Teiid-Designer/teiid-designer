/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.jdbc.internal;

import org.teiid.designer.schema.tools.model.jdbc.Column;
import org.teiid.designer.schema.tools.model.jdbc.DataType;

/**
 * @since 8.0
 */
public class ColumnImpl extends DatabaseElementImpl implements Column {

	private boolean m_attrOfParent;

	private boolean m_inputParam;

	private boolean m_required;

	private String m_attributeName;

	private Integer m_multiValues;

	private Integer m_role;

	private DataType m_dataType;
	
	private boolean m_isForeignKey;
	
	public ColumnImpl() {
		initDefaults();
	}

	private void initDefaults() {
		m_attrOfParent = false;
		m_inputParam = false;
		m_isForeignKey = false;
		m_required = false;
		m_role = 0; //data role
	}

	@Override
	public boolean isAttributeOfParent() {
		return m_attrOfParent;
	}

	@Override
	public void setIsAttributeOfParent(boolean isAttributeOfParent) {
		m_attrOfParent = isAttributeOfParent;
	}

	@Override
	public String getDataAttributeName() {
		return m_attributeName;
	}

	@Override
	public void setDataAttributeName(String name) {
		m_attributeName = name;
	}

	@Override
	public boolean isInputParameter() {
		return m_inputParam;
	}

	@Override
	public void setIsInputParameter(boolean isInputParameter) {
		m_inputParam = isInputParameter;
	}

	@Override
	public Integer getMultipleValues() {
		return m_multiValues;
	}

	@Override
	public void setMultipleValues(Integer multiValues) {
		m_multiValues = multiValues;
	}

	@Override
	public boolean isRequiredValue() {
		return m_required;
	}

	@Override
	public void setIsRequiredValue(boolean isRequired) {
		m_required = isRequired;
	}

	@Override
	public Integer getRole() {
		return m_role;
	}

	@Override
	public void setRole(Integer role) {
		m_role = role;
	}

	@Override
	public void setDataType(DataType type) {
		m_dataType = type;
	}

	@Override
	public DataType getDataType() {
		return m_dataType;
	}
	
	@Override
	public void setIsForeignKey(boolean isForeignKey) {
		m_isForeignKey = isForeignKey;
	}
	
	@Override
	public boolean isForeignKey() {
		return m_isForeignKey;
	}

	@Override
    public String toString() {
		return this.getName();
	}

}
