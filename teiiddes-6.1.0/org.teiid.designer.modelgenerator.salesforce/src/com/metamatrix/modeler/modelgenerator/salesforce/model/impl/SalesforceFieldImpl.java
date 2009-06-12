/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import java.util.ArrayList;
import java.util.List;

import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceField;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PicklistEntry;

public class SalesforceFieldImpl implements SalesforceField {
	
	public Field salesforceField;

	public SalesforceFieldImpl(Field field) {
		salesforceField = field;
	}

	public String getLabel() {
		return salesforceField.getLabel();
	}

	public int getLength() {
		return salesforceField.getLength();
	}

	public String getName() {
		return salesforceField.getName();
	}
	
	public String getType() {
		return salesforceField.getType().getValue();
	}
	
	public boolean isPrimaryKey() {
		return salesforceField.getType().getValue() == "id"; //$NON-NLS-1$
	}

	public int getDigits() {
		return salesforceField.getDigits();
	}

	public int getPrecision() {
		return salesforceField.getPrecision();
	}

	public int getScale() {
		return salesforceField.getScale();
	}
	
	public boolean isUpdateable() {
		return salesforceField.isUpdateable();
	}

	public boolean isAuditField() {
		return isAuditField(salesforceField.getName());
	}
	
	public static boolean isAuditField(String name) {
		boolean result = false;
		if(name.equals(SalesforceField.AUDIT_FIELD_CREATED_BY_ID) ||
				name.equals(SalesforceField.AUDIT_FIELD_CREATED_DATE) ||
				name.equals(SalesforceField.AUDIT_FIELD_LAST_MODIFIED_BY_ID) ||
				name.equals(SalesforceField.AUDIT_FIELD_LAST_MODIFIED_DATE) ||
				name.equals(SalesforceField.AUDIT_FIELD_SYSTEM_MOD_STAMP)) {
			result = true;
		}
		return result;
	}
	
	public List<String> getAllowedValues() {
		List<String> result = new ArrayList<String>();
		PicklistEntry[] entries = salesforceField.getPicklistValues();
		if(null != entries) {
			for(int i = 0; i < entries.length; i++) {
				PicklistEntry entry = entries[i];
				result.add(entry.getValue());
			}
		}
		return result;
	}

	public boolean isSearchable() {
		return salesforceField.isFilterable();
	}
	
	public boolean isCalculated() {
		return salesforceField.isCalculated();
	}
	
	public boolean isCustom() {
		return salesforceField.isCustom();
	}
	
	public boolean isDefaultedOnCreate() {
		return salesforceField.isDefaultedOnCreate();
	}
	
	public boolean isRestrictedPicklist() {
		return salesforceField.isRestrictedPicklist();
	}

}
