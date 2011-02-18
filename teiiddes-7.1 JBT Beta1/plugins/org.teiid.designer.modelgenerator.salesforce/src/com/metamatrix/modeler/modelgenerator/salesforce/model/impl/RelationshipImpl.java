/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import com.metamatrix.modeler.modelgenerator.salesforce.model.Relationship;

public class RelationshipImpl implements Relationship {

    private boolean cascadeDelete;
    public String childTablename;
    public String parentTableName;
    public String foreignKeyField;

    @Override
    public void setCascadeDelete( boolean delete ) {
        cascadeDelete = delete;
    }

    public boolean isCascadeDelete() {
        return cascadeDelete;
    }

    @Override
    public void setChildTable( String childTable ) {
        childTablename = childTable;
    }

    @Override
    public String getChildTable() {
        return childTablename;
    }

    @Override
    public String getForeignKeyField() {
        return foreignKeyField;
    }

    @Override
    public void setForeignKeyField( String foreignKeyField ) {
        this.foreignKeyField = foreignKeyField;
    }

    @Override
    public String getParentTable() {
        return parentTableName;
    }

    @Override
    public void setParentTable( String parentTableName ) {
        this.parentTableName = parentTableName;
    }

    @Override
    public boolean relatesToAuditField() {
        return SalesforceFieldImpl.isAuditField(foreignKeyField);
    }
}
