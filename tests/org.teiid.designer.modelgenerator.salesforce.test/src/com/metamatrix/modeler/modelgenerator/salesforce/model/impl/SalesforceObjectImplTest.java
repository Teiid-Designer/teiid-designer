/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import junit.framework.TestCase;
import com.sforce.soap.partner.ChildRelationship;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;

public class SalesforceObjectImplTest extends TestCase {

    private SalesforceObjectImpl test;
    private Field testField;
    private DescribeSObjectResult metadata;

    private DescribeSObjectResult metadata2;
    private SalesforceObjectImpl test2;
    private MockDataModel mockDataModel;

    private static final String LABEL = "visible"; //$NON-NLS-1$
    private static final String NAME = "name"; //$NON-NLS-1$

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mockDataModel = new MockDataModel();
        metadata = new DescribeSObjectResult();
        metadata.setLabel(LABEL);
        metadata.setQueryable(true);
        metadata.setCreateable(true);
        metadata.setUpdateable(true);
        metadata.setDeletable(true);
        metadata.setName(NAME);

        test = new SalesforceObjectImpl();
        test.setObjectMetadata(metadata, mockDataModel);
        test.setSelected(true);

        testField = new Field();

        metadata2 = new DescribeSObjectResult();
        metadata2.getFields().add(testField);
        ChildRelationship relationship = new ChildRelationship();
        metadata2.getChildRelationships().add(relationship);
        test2 = new SalesforceObjectImpl();
        test2.setObjectMetadata(metadata2, mockDataModel);
        test2.setSelected(false);
    }

    public void testSetObjectMetadata() {
        SalesforceObjectImpl test2 = new SalesforceObjectImpl();
        test2.setObjectMetadata(metadata, null);
        assertEquals(metadata, test2.objectMetadata);
    }

    public void testIsQueryable() {
        assertTrue(test.isQueryable());
    }

    public void testGetVisibleName() {
        assertEquals(LABEL, test.getLabel());
    }

    public void testCreateable() {
        assertTrue(test.isCreateable());
    }

    public void testUpdateable() {
        assertTrue(test.isUpdateable());
    }

    public void testDeleteable() {
        assertTrue(test.isDeleteable());
    }

    public void testGetFieldCountNoFields() {
        assertEquals(0, test.getFieldCount());
    }

    public void testGetFieldCountOneField() {
        assertEquals(1, test2.getFieldCount());
    }

    public void testGetFieldsEmpty() {
        assertTrue(test.getFields().length == 0);
    }

    public void testGetFieldsOne() {
        assertTrue(test2.getFields().length == 1);
    }

    public void testGetName() {
        assertEquals(NAME, test.getName());
    }

    public void testSelected() {
        assertTrue(test.isSelected());
        assertFalse(test2.isSelected());
    }

    public void testGetRelationshipsEmpty() {
        assertEquals(0, test.getSelectedRelationships().size());
    }

    public void testGetRelationship() {
        assertEquals(1, test2.getSelectedRelationships().size());
    }

}
