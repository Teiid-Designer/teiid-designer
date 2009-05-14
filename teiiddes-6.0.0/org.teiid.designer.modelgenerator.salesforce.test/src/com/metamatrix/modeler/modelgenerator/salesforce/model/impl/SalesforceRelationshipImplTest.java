/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import junit.framework.TestCase;

public class SalesforceRelationshipImplTest extends TestCase {

    private static final String FOO = "foo"; //$NON-NLS-1$
    private static final String BAR = "bar"; //$NON-NLS-1$
    private RelationshipImpl test1;
    private RelationshipImpl test2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        test1 = new RelationshipImpl();
        test1.setCascadeDelete(false);
        test1.setChildTable(FOO);
        test1.setForeignKeyField(FOO);
        test1.setParentTable(FOO);

        test2 = new RelationshipImpl();
        test2.setCascadeDelete(true);
        test2.setChildTable(BAR);
        test2.setForeignKeyField(BAR);
        test2.setParentTable(BAR);
    }

    public void testIsCascadeDelete() {
        assertFalse(test1.isCascadeDelete());
        assertTrue(test2.isCascadeDelete());
    }

    public void testGetChildTable() {
        assertEquals(FOO, test1.getChildTable());
        assertEquals(BAR, test2.getChildTable());
    }

    public void testGetForeignKeyField() {
        assertEquals(FOO, test1.getForeignKeyField());
        assertEquals(BAR, test2.getForeignKeyField());
    }

    public void testGetParentTable() {
        assertEquals(FOO, test1.getParentTable());
        assertEquals(BAR, test2.getParentTable());
    }
}
