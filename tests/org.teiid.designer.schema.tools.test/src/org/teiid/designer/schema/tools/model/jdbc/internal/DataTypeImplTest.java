/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.jdbc.internal;

import org.teiid.designer.schema.tools.model.jdbc.internal.DataTypeImpl;

import junit.framework.TestCase;

public class DataTypeImplTest extends TestCase {

    private static final String typeName = "string"; //$NON-NLS-1$
    private static final String namespace = "http://www.metamatrix.com/xml/types"; //$NON-NLS-1$

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DataTypeImpl.DataTypeImpl(String, String)'
     */
    public void testDataTypeImpl() {
        DataTypeImpl impl = new DataTypeImpl();
        assertNotNull(impl);
    }

    public void testDataTypeImplNameNamespace() {
        DataTypeImpl impl = new DataTypeImpl(typeName, namespace);
        assertNotNull(impl);
        assertEquals(impl.getTypeName(), typeName);
        assertEquals(impl.getTypeNamespace(), namespace);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DataTypeImpl.getTypeName()'
     */
    public void testGetSetTypeName() {
        DataTypeImpl impl = new DataTypeImpl();
        impl.setTypeName(typeName);
        assertEquals(impl.getTypeName(), typeName);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DataTypeImpl.getTypeNamespace()'
     */
    public void testGetSetTypeNamespace() {
        DataTypeImpl impl = new DataTypeImpl();
        impl.setTypeNamespace(namespace);
        assertEquals(impl.getTypeNamespace(), namespace);
    }
}
