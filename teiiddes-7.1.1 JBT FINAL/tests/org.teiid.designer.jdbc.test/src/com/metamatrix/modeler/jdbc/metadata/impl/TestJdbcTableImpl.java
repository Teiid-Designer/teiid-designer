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
public class TestJdbcTableImpl extends TestCase {

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
    public TestJdbcTableImpl( String name ) {
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
        TestSuite suite = new TestSuite("TestJdbcTableImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcTableImpl.class);
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
        assertNotNull("Unable to find node with path: " + path, node); //$NON-NLS-1$
        final IPath pathInSource = node.getPathInSource();
        if (expectedPathInSourceStr == null) {
            assertNull(pathInSource);
        } else {
            final String pathInSourceStr = pathInSource.toString();
            assertEquals(expectedPathInSourceStr, pathInSourceStr);
        }
    }

    public static void helpTestIsDatabaseObject( final JdbcDatabase dbNode,
                                                 final String path,
                                                 final boolean expected ) {
        final JdbcNode node = dbNode.findJdbcNode(path);
        assertNotNull("Unable to find node with path: " + path, node); //$NON-NLS-1$
        final boolean actual = node.isDatabaseObject();
        assertEquals(expected, actual);
    }

    public static void helpTestGetParentDatabaseObject( final JdbcDatabase dbNode,
                                                        final String path,
                                                        final boolean includeCatalogs,
                                                        final boolean includeSchemas,
                                                        final String pathToParentDbObject ) {
        final JdbcNode node = dbNode.findJdbcNode(path);
        assertNotNull("Unable to find node with path: " + path, node); //$NON-NLS-1$
        final JdbcNode expectedParentDbObject = pathToParentDbObject != null ? dbNode.findJdbcNode(pathToParentDbObject) : null;
        final JdbcNode actual = node.getParentDatabaseObject(includeCatalogs, includeSchemas);
        assertEquals(expectedParentDbObject, actual);
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

    // -------------------------------------------------------------------------
    // Database Object Test Methods
    // -------------------------------------------------------------------------

    public void testIsDatabaseObjectForProcedure() {
        final String nodePath = "/PartsSupplier/StoredProcedure/DeleteCustomerProcedure"; //$NON-NLS-1$
        final boolean isDbObject = true;
        helpTestIsDatabaseObject(dbNodeWithSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForProcedureType() {
        final String nodePath = "/PartsSupplier/StoredProcedure"; //$NON-NLS-1$
        final boolean isDbObject = false;
        helpTestIsDatabaseObject(dbNodeWithSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForTable() {
        final String nodePath = "/PartsSupplier/Table/SUPPLIER_PARTS"; //$NON-NLS-1$
        final boolean isDbObject = true;
        helpTestIsDatabaseObject(dbNodeWithSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForTableType() {
        final String nodePath = "/PartsSupplier/Table"; //$NON-NLS-1$
        final boolean isDbObject = false;
        helpTestIsDatabaseObject(dbNodeWithSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForTableTypeOfView() {
        final String nodePath = "/PartsSupplier/View"; //$NON-NLS-1$
        final boolean isDbObject = false;
        helpTestIsDatabaseObject(dbNodeWithSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForSchema1() {
        final String nodePath = "/PartsSupplier"; //$NON-NLS-1$
        final boolean isDbObject = true;
        helpTestIsDatabaseObject(dbNodeWithSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForSchema2() {
        final String nodePath = "/PartsSupplier/PartsSupplier"; //$NON-NLS-1$
        final boolean isDbObject = true;
        helpTestIsDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForCatalog1() {
        final String nodePath = "/PartsSupplier"; //$NON-NLS-1$
        final boolean isDbObject = true;
        helpTestIsDatabaseObject(dbNodeWithCatalog, nodePath, isDbObject);
    }

    public void testIsDatabaseObjectForCatalog2() {
        final String nodePath = "/PartsSupplier"; //$NON-NLS-1$
        final boolean isDbObject = true;
        helpTestIsDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, isDbObject);
    }

    // -------------------------------------------------------------------------
    // Parent Database Object Tests
    // -------------------------------------------------------------------------

    public void testGetParentDatabaseObjectForProcedureBelowCatalog() {
        final String nodePath = "/PartsSupplier/StoredProcedure/DeleteCustomerProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedureBelowSchema() {
        final String nodePath = "/PartsSupplier/StoredProcedure/DeleteCustomerProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedureBelowSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier/StoredProcedure/DeleteCustomerProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedure() {
        final String nodePath = "/StoredProcedure/DeleteCustomerProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedureTypeBelowCatalog() {
        final String nodePath = "/PartsSupplier/StoredProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedureTypeBelowSchema() {
        final String nodePath = "/PartsSupplier/StoredProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedureTypeBelowSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier/StoredProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForProcedureType() {
        final String nodePath = "/StoredProcedure"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableBelowCatalog() {
        final String nodePath = "/PartsSupplier/Table/SUPPLIER_PARTS"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableBelowSchema() {
        final String nodePath = "/PartsSupplier/Table/SUPPLIER_PARTS"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableBelowSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTable() {
        final String nodePath = "/Table/SUPPLIER_PARTS"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeBelowCatalog() {
        final String nodePath = "/PartsSupplier/Table"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeBelowSchema() {
        final String nodePath = "/PartsSupplier/Table"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeBelowSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier/Table"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableType() {
        final String nodePath = "/Table"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForViewBelowCatalog() {
        final String nodePath = "/PartsSupplier/View/CUSTOMERS_VIEW"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForViewBelowSchema() {
        final String nodePath = "/PartsSupplier/View/CUSTOMERS_VIEW"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForViewBelowSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForView() {
        final String nodePath = "/View/CUSTOMERS_VIEW"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeOfViewBelowCatalog() {
        final String nodePath = "/PartsSupplier/View"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeOfViewBelowSchema() {
        final String nodePath = "/PartsSupplier/View"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeOfViewBelowSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier/View"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForTableTypeOfView() {
        final String nodePath = "/View"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithoutCatalogAndSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForCatalog() {
        final String nodePath = "/PartsSupplier"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalog, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForSchema() {
        final String nodePath = "/PartsSupplier"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, true, false, null);
        helpTestGetParentDatabaseObject(dbNodeWithSchema, nodePath, false, false, null);
    }

    public void testGetParentDatabaseObjectForSchemaBelowCatalog() {
        final String nodePath = "/PartsSupplier/PartsSupplier"; //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, true, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, true, null);
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, true, false, "/PartsSupplier"); //$NON-NLS-1$
        helpTestGetParentDatabaseObject(dbNodeWithCatalogAndSchema, nodePath, false, false, null);
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
