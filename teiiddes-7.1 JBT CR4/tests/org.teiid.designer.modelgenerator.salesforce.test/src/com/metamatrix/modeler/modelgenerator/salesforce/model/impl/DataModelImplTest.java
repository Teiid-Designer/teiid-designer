/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import junit.framework.TestCase;

public class DataModelImplTest extends TestCase {

    private static final String FOO = "foo"; //$NON-NLS-1$
    private static final Integer SIZE = 427;

    DataModelImpl test;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        test = new DataModelImpl();
        test.setEncoding(FOO);
        test.setMaxBatchSize(SIZE);
        test.addSalesforceObject(FOO, new SalesforceObjectImpl());
    }

    public void testGetMaxBatchSize() {
        assertEquals(SIZE, test.getMaxBatchSize());
    }

    public void testGetEncoding() {
        assertEquals(FOO, test.getEncoding());
    }

    public void testGetSalesforceObjects() {
        assertEquals(1, test.getSalesforceObjects().length);
    }

    public void testGetSalesforceObject() {
        assertNotNull(test.getSalesforceObject(FOO));
    }

    public void testHasSelectedObjects() {
        DataModelImpl local = new DataModelImpl();
        assertEquals(false, local.hasSelectedObjects());
        local.incrementSelectionCount();
        assertEquals(true, local.hasSelectedObjects());
    }

    public void testIncrementSelectionCount() {
        DataModelImpl local = new DataModelImpl();
        assertEquals(false, local.hasSelectedObjects());
        local.incrementSelectionCount();
        assertEquals(true, local.hasSelectedObjects());
    }

    public void testDecrementSelectionCount() {
        DataModelImpl local = new DataModelImpl();
        assertEquals(false, local.hasSelectedObjects());
        local.incrementSelectionCount();
        assertEquals(true, local.hasSelectedObjects());
        local.decrementSelectionCount();
        assertEquals(false, local.hasSelectedObjects());
    }

    public void testAddDupObject() {
        DataModelImpl local = new DataModelImpl();
        try {
            local.addSalesforceObject(FOO, new SalesforceObjectImpl());
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            local.addSalesforceObject(FOO, new SalesforceObjectImpl());
        } catch (Exception e) {
            // good.
        }
    }

}
