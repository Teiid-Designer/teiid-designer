/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;

/**
 * TestJdbcTableImpl
 */
public class TestJdbcProcedureImpl extends TestCase {

    private FakeJdbcDatabase dbNodeWithSchema;
    private FakeJdbcDatabase dbNodeWithCatalog;
    private FakeJdbcDatabase dbNodeWithCatalogAndSchema;
    private FakeJdbcDatabase dbNodeWithoutCatalogAndSchema;

    private JdbcNodeVisitor visitor;

    /**
     * Constructor for TestJdbcTableImpl.
     * 
     * @param name
     */
    public TestJdbcProcedureImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.dbNodeWithSchema = new FakeJdbcDatabase("dbNodeWithSchema"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithSchema, false, true);

        this.dbNodeWithCatalog = new FakeJdbcDatabase("dbNodeWithCatalog"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithCatalog, true, false);

        this.dbNodeWithCatalogAndSchema = new FakeJdbcDatabase("dbNodeWithCatalogAndSchema"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithCatalogAndSchema, true, true);

        this.dbNodeWithoutCatalogAndSchema = new FakeJdbcDatabase("dbNodeWithoutCatalogAndSchema"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithoutCatalogAndSchema, false, false);

        this.visitor = new JdbcNodeMethodCallingVisitor();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestJdbcProcedureImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcProcedureImpl.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            }

            @Override
            public void tearDown() {
            }
        };
    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    public static void helpTestPathInSource( final JdbcDatabase dbNode,
                                             final String path,
                                             final String expectedPathInSourceStr ) {
        final JdbcNode node = dbNode.findJdbcNode(path);
        assertNotNull(node);
        final IPath pathInSource = node.getPathInSource();
        if (expectedPathInSourceStr == null) {
            assertNull(pathInSource);
        } else {
            final String pathInSourceStr = pathInSource.toString();
            assertEquals(expectedPathInSourceStr, pathInSourceStr);
        }
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testGetPathInSourceForTable1() {
        final String nodePath = "/PartsSupplier/Table/PARTS"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/PARTS"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForTable2() {
        final String nodePath = "/PartsSupplier/Table/SUPPLIER"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/SUPPLIER"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForTable3() {
        final String nodePath = "/PartsSupplier/Table/SUPPLIER_PARTS"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/SUPPLIER_PARTS"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForView1() {
        final String nodePath = "/PartsSupplier/View/CUSTOMERS_VIEW"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/CUSTOMERS_VIEW"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForView2() {
        final String nodePath = "/PartsSupplier/View/ORDERS_VIEW"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/ORDERS_VIEW"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForProcedure1() {
        final String nodePath = "/PartsSupplier/StoredProcedure/CreateCustomerProcedure"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/CreateCustomerProcedure"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForProcedure2() {
        final String nodePath = "/PartsSupplier/StoredProcedure/ChangeCustomerProcedure"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/ChangeCustomerProcedure"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testGetPathInSourceForProcedure3() {
        final String nodePath = "/PartsSupplier/StoredProcedure/DeleteCustomerProcedure"; //$NON-NLS-1$
        final String pathInSource = "/PartsSupplier/DeleteCustomerProcedure"; //$NON-NLS-1$
        helpTestPathInSource(dbNodeWithSchema, nodePath, pathInSource);
    }

    public void testInvokeJdbcNodeMethodsInDbWithSchema() throws Exception {
        dbNodeWithSchema.accept(this.visitor, JdbcNode.DEPTH_INFINITE);
    }

    public void testInvokeJdbcNodeMethodsInDbWithCatalog() throws Exception {
        dbNodeWithCatalog.accept(this.visitor, JdbcNode.DEPTH_INFINITE);
    }

    public void testInvokeJdbcNodeMethodsInDbWithCatalogAndSchema() throws Exception {
        dbNodeWithCatalogAndSchema.accept(this.visitor, JdbcNode.DEPTH_INFINITE);
    }

    public void testInvokeJdbcNodeMethodsInDbWithoutCatalogAndSchema() throws Exception {
        dbNodeWithoutCatalogAndSchema.accept(this.visitor, JdbcNode.DEPTH_INFINITE);
    }

}
