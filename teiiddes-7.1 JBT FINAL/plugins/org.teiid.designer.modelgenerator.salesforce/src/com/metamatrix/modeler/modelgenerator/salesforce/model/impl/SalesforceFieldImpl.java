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

    public SalesforceFieldImpl( Field field ) {
        salesforceField = field;
    }

    @Override
    public String getLabel() {
        return salesforceField.getLabel();
    }

    @Override
    public int getLength() {
        return salesforceField.getLength();
    }

    @Override
    public String getName() {
        return salesforceField.getName();
    }

    @Override
    public String getType() {
        return salesforceField.getType().getValue();
    }

    @Override
    public boolean isPrimaryKey() {
        return salesforceField.getType().getValue() == "id"; //$NON-NLS-1$
    }

    @Override
    public int getDigits() {
        return salesforceField.getDigits();
    }

    @Override
    public int getPrecision() {
        return salesforceField.getPrecision();
    }

    @Override
    public int getScale() {
        return salesforceField.getScale();
    }

    @Override
    public boolean isUpdateable() {
        return salesforceField.isUpdateable();
    }

    @Override
    public boolean isAuditField() {
        return isAuditField(salesforceField.getName());
    }

    public static boolean isAuditField( String name ) {
        boolean result = false;
        if (name.equals(SalesforceField.AUDIT_FIELD_CREATED_BY_ID) || name.equals(SalesforceField.AUDIT_FIELD_CREATED_DATE)
            || name.equals(SalesforceField.AUDIT_FIELD_LAST_MODIFIED_BY_ID)
            || name.equals(SalesforceField.AUDIT_FIELD_LAST_MODIFIED_DATE)
            || name.equals(SalesforceField.AUDIT_FIELD_SYSTEM_MOD_STAMP)) {
            result = true;
        }
        return result;
    }

    @Override
    public List<String> getAllowedValues() {
        List<String> result = new ArrayList<String>();
        PicklistEntry[] entries = salesforceField.getPicklistValues();
        if (null != entries) {
            for (int i = 0; i < entries.length; i++) {
                PicklistEntry entry = entries[i];
                result.add(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public boolean isSearchable() {
        return salesforceField.isFilterable();
    }

    @Override
    public boolean isCalculated() {
        return salesforceField.isCalculated();
    }

    @Override
    public boolean isCustom() {
        return salesforceField.isCustom();
    }

    @Override
    public boolean isDefaultedOnCreate() {
        return salesforceField.isDefaultedOnCreate();
    }

    @Override
    public boolean isRestrictedPicklist() {
        return salesforceField.isRestrictedPicklist();
    }

}
