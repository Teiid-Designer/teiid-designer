/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model;

import java.util.List;

public interface SalesforceField {

	public static final String AUDIT_FIELD_CREATED_BY_ID = "CreatedById"; //$NON-NLS-1$

	public static final String AUDIT_FIELD_CREATED_DATE = "CreatedDate"; //$NON-NLS-1$

	public static final String AUDIT_FIELD_LAST_MODIFIED_BY_ID = "LastModifiedById"; //$NON-NLS-1$

	public static final String AUDIT_FIELD_LAST_MODIFIED_DATE = "LastModifiedDate"; //$NON-NLS-1$

	public static final String AUDIT_FIELD_SYSTEM_MOD_STAMP = "SystemModstamp"; //$NON-NLS-1$

	public static final String PICKLIST_TYPE = "picklist"; //$NON-NLS-1$

	public static final String MULTIPICKLIST_TYPE = "multipicklist"; //$NON-NLS-1$

	public static final String COMBOBOX_TYPE = "combobox"; //$NON-NLS-1$

	public static final String ANYTYPE_TYPE = "anyType"; //$NON-NLS-1$

	public static final String REFERENCE_TYPE = "reference"; //$NON-NLS-1$

	public static final String STRING_TYPE = "string"; //$NON-NLS-1$

	public static final String BASE64_TYPE = "base64"; //$NON-NLS-1$

	public static final String BOOLEAN_TYPE = "boolean"; //$NON-NLS-1$

	public static final String CURRENCY_TYPE = "currency"; //$NON-NLS-1$

	public static final String TEXTAREA_TYPE = "textarea"; //$NON-NLS-1$

	public static final String INT_TYPE = "int"; //$NON-NLS-1$

	public static final String DOUBLE_TYPE = "double"; //$NON-NLS-1$

	public static final String PERCENT_TYPE = "percent"; //$NON-NLS-1$

	public static final String PHONE_TYPE = "phone"; //$NON-NLS-1$

	public static final String ID_TYPE = "id"; //$NON-NLS-1$

	public static final String DATE_TYPE = "date"; //$NON-NLS-1$

	public static final String DATETIME_TYPE = "datetime"; //$NON-NLS-1$

	public static final String URL_TYPE = "url"; //$NON-NLS-1$

	public static final String EMAIL_TYPE = "email"; //$NON-NLS-1$

	public static final String RESTRICTED_PICKLIST_TYPE = "restrictedpicklist"; //$NON-NLS-1$
	
	public static final String RESTRICTED_MULTISELECT_PICKLIST_TYPE = "restrictedmultiselectpicklist"; //$NON-NLS-1$
	

	/**
	 * The internal name of the field
	 * @return The internal name of the field
	 */
	String getName();

	int getLength();

	String getLabel();

	String getType();

	public boolean isPrimaryKey();

	int getScale();

	int getPrecision();

	int getDigits();

	public boolean isUpdateable();

	public boolean isAuditField();

	public List getAllowedValues();
	
	public boolean isSearchable();

	public boolean isCalculated();

	public boolean isCustom();

	public boolean isDefaultedOnCreate();

	public boolean isRestrictedPicklist();
}
