/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.schema.tools.model.jdbc.internal;

import org.teiid.designer.schema.tools.model.jdbc.internal.ColumnImpl;
import org.teiid.designer.schema.tools.model.jdbc.internal.DatabaseElementImpl;
import org.teiid.designer.schema.tools.model.jdbc.internal.TableImpl;

import junit.framework.TestCase;

public class DatabaseElementImplTest extends TestCase {

    private static final String NAME = "ElementName"; //$NON-NLS-1$
    private static final String OUT_XPATH = "foo/text()"; //$NON-NLS-1$
    private static final String IN_XPATH = "bar"; //$NON-NLS-1$

    public DatabaseElementImplTest( String name ) {
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
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DatabaseElementImpl.DatabaseElementImpl()'
     */
    public void testDatabaseElementImpl() {
        DatabaseElementImpl impl = new ColumnImpl();
        assertNotNull(impl);
        assertNull(impl.getName());
        assertNull(impl.getInputXPath());
        assertNull(impl.getOutputXPath());
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DatabaseElementImpl.DatabaseElementImpl(String, String, String)'
     */
    public void testDatabaseElementImplStringStringString() {
        DatabaseElementImpl impl = new TableImpl(NAME, "catalog", IN_XPATH, OUT_XPATH); //$NON-NLS-1$
        assertNotNull(impl);
        assertEquals(impl.getName(), NAME);
        assertEquals(impl.getInputXPath(), IN_XPATH);
        assertEquals(impl.getOutputXPath(), OUT_XPATH);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DatabaseElementImpl.getName()'
     */
    public void testGetSetName() {
        DatabaseElementImpl impl = new ColumnImpl();
        impl.setName(NAME);
        assertEquals(impl.getName(), NAME);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DatabaseElementImpl.getInputXPath()'
     */
    public void testGetSetInputXPath() {
        DatabaseElementImpl impl = new ColumnImpl();
        impl.setInputXPath(IN_XPATH);
        assertEquals(impl.getInputXPath(), IN_XPATH);
    }

    /*
     * Test method for 'org.teiid.designer.modelgenerator.wsdl.model.jdbc.internal.DatabaseElementImpl.getOutputXPath()'
     */
    public void testGetSetOutputXPath() {
        DatabaseElementImpl impl = new ColumnImpl();
        impl.setOutputXPath(OUT_XPATH);
        assertEquals(impl.getOutputXPath(), OUT_XPATH);
    }
}
