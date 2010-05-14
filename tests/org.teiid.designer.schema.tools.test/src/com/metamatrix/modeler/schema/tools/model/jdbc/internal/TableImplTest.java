/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.schema.tools.model.jdbc.internal;

import junit.framework.TestCase;
import org.jdom.Namespace;
import com.metamatrix.modeler.schema.tools.model.jdbc.Column;

public class TableImplTest extends TestCase {

    private static final String NAME = "ElementName"; //$NON-NLS-1$
    private static final String OUT_XPATH = "foo/text()"; //$NON-NLS-1$
    private static final String IN_XPATH = "bar"; //$NON-NLS-1$
    private static final String CATALOG = "myCatalog"; //$NON-NLS-1$
    private static final String NS_PREFIX = "pre"; //$NON-NLS-1$
    private static final String NS_URI = "http://www.metamatrix.com/xml/ns"; //$NON-NLS-1$

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.TableImpl.TableImpl()'
     */
    public void testTableImpl() {
        TableImpl impl = new TableImpl();
        assertNotNull(impl);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.TableImpl.TableImpl(String, String, String, String)'
     */
    public void testTableImplStringStringStringString() {
        TableImpl impl = new TableImpl(NAME, CATALOG, IN_XPATH, OUT_XPATH);
        assertEquals(impl.getName(), NAME);
        assertEquals(impl.getCatalog(), CATALOG);
        assertEquals(impl.getInputXPath(), IN_XPATH);
        assertEquals(impl.getOutputXPath(), OUT_XPATH);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.TableImpl.getNamespaceDeclaration()'
     */
    public void testAddGetNamespaceDeclaration() {
        final String expectedOut = "xmlns:pre='" + NS_URI + "'"; //$NON-NLS-1$ //$NON-NLS-2$
        TableImpl impl = new TableImpl();
        Namespace ns = Namespace.getNamespace(NS_PREFIX, NS_URI);
        impl.addNamespace(ns);
        assertEquals(impl.getNamespaceDeclaration(), expectedOut);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.TableImpl.getCatalog()'
     */
    public void testSetGetCatalog() {
        TableImpl impl = new TableImpl();
        impl.setCatalog(CATALOG);
        assertEquals(impl.getCatalog(), CATALOG);
    }

    /*
     * Test method for 'com.metamatrix.modeler.modelgenerator.wsdl.model.jdbc.internal.TableImpl.addColumn(Column)'
     */
    public void testAddGetColumn() {
        TableImpl impl = new TableImpl();
        ColumnImpl colImpl = new ColumnImpl();
        impl.addColumn(colImpl);
        Column[] colList = impl.getColumns();
        assertEquals(1, colList.length);
        assertEquals(colList[0], colImpl);
    }

}
