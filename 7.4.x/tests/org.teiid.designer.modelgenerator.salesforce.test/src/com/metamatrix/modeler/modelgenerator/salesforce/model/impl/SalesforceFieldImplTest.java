/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.modelgenerator.salesforce.model.impl;

import junit.framework.TestCase;
import com.metamatrix.modeler.modelgenerator.salesforce.model.SalesforceField;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;

public class SalesforceFieldImplTest extends TestCase {

    private static String LABEL = "the label"; //$NON-NLS-1$
    private static int LENGTH = 30;
    private static String NAME = "the name"; //$NON-NLS-1$

    SalesforceField test;
    SalesforceField test2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Field axisFieldID = new Field();
        axisFieldID.setLabel(LABEL);
        axisFieldID.setLength(30);
        axisFieldID.setName(NAME);
        axisFieldID.setDigits(5);
        axisFieldID.setPrecision(5);

        FieldType type = FieldType.fromValue("id"); //$NON-NLS-1$
        axisFieldID.setType(type);

        test = new SalesforceFieldImpl(axisFieldID);

        Field axisFieldNonID = new Field();
        FieldType type2 = FieldType.fromValue("int"); //$NON-NLS-1$
        axisFieldNonID.setType(type2);
        test2 = new SalesforceFieldImpl(axisFieldNonID);
    }

    public void testGetLabel() {
        assertEquals(LABEL, test.getLabel());
    }

    public void testGetLength() {
        assertEquals(LENGTH, test.getLength());
    }

    public void testGetName() {
        assertEquals(NAME, test.getName());
    }

    public void testGetType() {
        assertEquals("id", test.getType()); //$NON-NLS-1$
    }

    public void testIsPrimaryKey() {
        assertTrue(test.isPrimaryKey());
    }

    public void testNotPrimaryKey() {
        assertFalse(test2.isPrimaryKey());
    }

    public void testGetDigits() {
        assertEquals(5, test.getDigits());
    }

    public void testGetPrecision() {
        assertEquals(5, test.getPrecision());
    }
}
