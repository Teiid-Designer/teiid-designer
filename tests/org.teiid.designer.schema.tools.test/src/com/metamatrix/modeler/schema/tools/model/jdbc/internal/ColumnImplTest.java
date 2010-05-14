/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.jdbc.internal;

import junit.framework.TestCase;
import com.metamatrix.modeler.schema.tools.model.jdbc.DataType;

public class ColumnImplTest extends TestCase {

    public ColumnImplTest( String name ) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.ColumnImpl()'
     */
    public void testColumnImpl() {
        ColumnImpl impl = new ColumnImpl();
        assertNotNull(impl);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.isAttributeOfParent()'
     */
    public void testSetIsAttributeOfParent() {
        ColumnImpl impl = new ColumnImpl();
        impl.setIsAttributeOfParent(true);
        assertTrue(impl.isAttributeOfParent());
        impl.setIsAttributeOfParent(false);
        assertFalse(impl.isAttributeOfParent());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.getDataAttributeName()'
     */
    public void testSetGetDataAttributeName() {
        final String name = "myAttribute"; //$NON-NLS-1$
        ColumnImpl impl = new ColumnImpl();
        impl.setDataAttributeName(name);
        assertEquals(name, impl.getDataAttributeName());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.isInputParameter()'
     */
    public void testSetIsInputParameter() {
        ColumnImpl impl = new ColumnImpl();
        impl.setIsInputParameter(true);
        assertTrue(impl.isInputParameter());
        impl.setIsInputParameter(false);
        assertFalse(impl.isInputParameter());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.getMultipleValues()'
     */
    public void testSetGetMultipleValues() {
        ColumnImpl impl = new ColumnImpl();
        impl.setMultipleValues(0);
        assertEquals(impl.getMultipleValues(), Integer.valueOf(0));
        impl.setMultipleValues(1);
        assertEquals(impl.getMultipleValues(), Integer.valueOf(1));
        impl.setMultipleValues(3);
        assertEquals(impl.getMultipleValues(), Integer.valueOf(3));

    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.isRequiredValue()'
     */
    public void testSetIsRequiredValue() {
        ColumnImpl impl = new ColumnImpl();
        impl.setIsRequiredValue(true);
        assertTrue(impl.isRequiredValue());
        impl.setIsRequiredValue(false);
        assertFalse(impl.isRequiredValue());
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.getRole()'
     */
    public void testSetGetRole() {
        ColumnImpl impl = new ColumnImpl();
        impl.setRole(0);
        assertEquals(impl.getRole(), Integer.valueOf(0));
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.ColumnImpl.setDataType(DataType)'
     */
    public void testSetGetDataType() {
        DataType type = new DataTypeImpl("string", "http://www.metamatrix.com"); //$NON-NLS-1$ //$NON-NLS-2$
        ColumnImpl impl = new ColumnImpl();
        impl.setDataType(type);
        assertEquals(impl.getDataType(), type);
    }
}
