/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.io.PrintStream;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.PathPrintingVisitor;
import com.metamatrix.modeler.jdbc.metadata.impl.FakeJdbcDatabase;

/**
 * TestJdbcModelStructure
 */
public class TestJdbcModelStructure extends TestCase {

    private static final boolean PRINT = true;

    private JdbcModelStructure emptyStructure;
    private FakeJdbcDatabase dbNodeWithSchema;
    private FakeJdbcDatabase dbNodeWithCatalog;
    private FakeJdbcDatabase dbNodeWithCatalogAndSchema;
    private FakeJdbcDatabase dbNodeWithoutCatalogAndSchema;

    /**
     * Constructor for TestJdbcModelStructure.
     * 
     * @param name
     */
    public TestJdbcModelStructure( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.emptyStructure = new JdbcModelStructure(); // use protected constructor

        this.dbNodeWithSchema = new FakeJdbcDatabase("dbNodeWithSchema"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithSchema, false, true);

        this.dbNodeWithCatalog = new FakeJdbcDatabase("dbNodeWithCatalog"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithCatalog, true, false);

        this.dbNodeWithCatalogAndSchema = new FakeJdbcDatabase("dbNodeWithCatalogAndSchema"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithCatalogAndSchema, true, true);

        this.dbNodeWithoutCatalogAndSchema = new FakeJdbcDatabase("dbNodeWithoutCatalogAndSchema"); //$NON-NLS-1$
        FakeJdbcDatabase.initialize(this.dbNodeWithoutCatalogAndSchema, false, false);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.emptyStructure = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestJdbcModelStructure"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcModelStructure.class);
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

    public static void helpSelectNode( final JdbcDatabase dbNode,
                                       final String path ) {
        helpSelectNode(dbNode, path, true);
    }

    public static void helpSelectNode( final JdbcDatabase dbNode,
                                       final String path,
                                       final boolean selected ) {
        final JdbcNode node = dbNode.findJdbcNode(path);
        assertNotNull("Unable to find node with path: " + path, node); //$NON-NLS-1$
        node.setSelected(selected);
    }

    public static void helpCheckNodeSelection( final JdbcDatabase dbNode,
                                               final String path,
                                               final int expectedMode ) {
        final JdbcNode node = dbNode.findJdbcNode(path);
        assertNotNull("Unable to find node with path: " + path, node); //$NON-NLS-1$
        assertEquals("Selection mode did not match for node: " + path, //$NON-NLS-1$
                     expectedMode,
                     node.getSelectionMode());
    }

    /**
     * Help check structure.
     * 
     * @param dbNode the database node
     * @param structure the structure to be checked
     * @param parentPath the path of the parent; null if the child is a root
     * @param childPath the path of the child; null if the parent should have no children
     */
    public static void helpCheckStructure( final JdbcDatabase dbNode,
                                           final JdbcModelStructure structure,
                                           final String parentPath,
                                           final String childPath ) {
        JdbcNode parent = null;
        JdbcNode child = null;
        if (parentPath != null) {
            parent = dbNode.findJdbcNode(parentPath);
            assertNotNull("Unable to find parent node with path: " + parentPath, parent); //$NON-NLS-1$
        }
        if (childPath != null) {
            child = dbNode.findJdbcNode(childPath);
            assertNotNull("Unable to find child node with path: " + childPath, child); //$NON-NLS-1$

            final JdbcNode parentForChild = structure.getParent(child);
            assertSame("Parent is different", parent, parentForChild); //$NON-NLS-1$
        }

        final List children = structure.getChildren(parent);
        if (child != null) {
            // Then there should be some children
            assertNotNull(children);
            assertTrue(children.size() > 0);
            assertTrue(children.contains(child));
        } else {
            // There should not be children
            assertNull(children);
        }
    }

    public static void helpCheckNotInStructure( final JdbcDatabase dbNode,
                                                final JdbcModelStructure structure,
                                                final String nodePath ) {
        final JdbcNode node = dbNode.findJdbcNode(nodePath);
        assertNotNull("Unable to find node with path: " + nodePath, node); //$NON-NLS-1$

        // Check that node is not a parent ...
        final List children = structure.getChildren(node);
        assertNull(children);

        // Check that node is not a child ...
        final JdbcNode parent = structure.getParent(node);
        assertNull(parent);
    }

    public static void helpPrintJdbcDatabase( final JdbcDatabase dbNode,
                                              final PrintStream stream ) throws Exception {
        final PathPrintingVisitor visitor = new PathPrintingVisitor(stream);
        dbNode.accept(visitor, JdbcNode.DEPTH_INFINITE);
    }

    public void testProtectedConstructor() {
        new JdbcModelStructure();
    }

    public void testGetChildrenOnEmptyStructure() {
        final List children = this.emptyStructure.getChildren(null);
        assertNull(children);
    }

    public void testBuildingStructureUsingDbWithSchema() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithSchema;
        final boolean includeSchemas = true;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeCatalogs = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            helpCheckStructure(dbNode, structure, null, "/PartsSupplier"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$//$NON-NLS-2$
            helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$//$NON-NLS-2$
            helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/Table/PARTS"); //$NON-NLS-1$//$NON-NLS-2$
            helpCheckStructure(dbNode, structure, "/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$

            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithSchemaExcludingSchema() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithSchema;
        final boolean includeSchemas = false;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeCatalogs = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            helpCheckStructure(dbNode, structure, null, "/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, null, "/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, null, "/PartsSupplier/Table/PARTS"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, "/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$

            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithCatalog() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithCatalog;
        final boolean includeCatalogs = true;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeSchemas = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            helpCheckStructure(dbNode, structure, null, "/PartsSupplier"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$//$NON-NLS-2$
            helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$//$NON-NLS-2$
            helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/Table/PARTS"); //$NON-NLS-1$//$NON-NLS-2$
            helpCheckStructure(dbNode, structure, "/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$

            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithCatalogExcludingCatalog() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithCatalog;
        final boolean includeCatalogs = false;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeSchemas = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            helpCheckStructure(dbNode, structure, null, "/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, null, "/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, null, "/PartsSupplier/Table/PARTS"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, "/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$

            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithCatalogAndSchema() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithCatalogAndSchema;
        final boolean includeCatalogs = true;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeSchemas = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode,
                                   "/PartsSupplier/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            if (includeSchemas) {
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode,
                                   structure,
                                   "/PartsSupplier/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode,
                                   structure,
                                   "/PartsSupplier/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$
            } else {
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$
            }
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithCatalogAndSchemaExcludingCatalog() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithCatalogAndSchema;
        final boolean includeCatalogs = false;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeSchemas = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode,
                                   "/PartsSupplier/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            if (includeSchemas) {
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
                helpCheckStructure(dbNode,
                                   structure,
                                   "/PartsSupplier/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode,
                                   structure,
                                   "/PartsSupplier/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$
            } else {
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier/Table/PARTS"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$
            }
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithCatalogAndSchemaExcludingSchema() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithCatalogAndSchema;
        final boolean includeSchemas = false;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 2; ++i) {
            final boolean includeCatalogs = (i == 0);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/PartsSupplier/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/PartsSupplier/PartsSupplier/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode,
                                   "/PartsSupplier/PartsSupplier/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            if (includeCatalogs) {
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier", "/PartsSupplier/PartsSupplier/Table/PARTS"); //$NON-NLS-1$//$NON-NLS-2$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$
                helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier"); //$NON-NLS-1$
            } else {
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier/Table/SUPPLIER"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, null, "/PartsSupplier/PartsSupplier/Table/PARTS"); //$NON-NLS-1$
                helpCheckStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table/SUPPLIER", null); //$NON-NLS-1$
            }
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/PartsSupplier/PartsSupplier/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

    public void testBuildingStructureUsingDbWithoutCatalogOrSchemas() throws Exception {
        final JdbcDatabase dbNode = this.dbNodeWithoutCatalogAndSchema;

        // helpPrintJdbcDatabase(dbNode,System.out);

        for (int i = 0; i < 4; ++i) {
            final boolean includeSchemas = (i == 1 || i == 3);
            final boolean includeCatalogs = (i == 2 || i == 3);

            // Mark the nodes ...
            helpSelectNode(dbNode, "/Table"); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/Table", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/Table/SUPPLIER", JdbcNode.SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/View", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/View/CUSTOMERS_VIEW", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/", JdbcNode.PARTIALLY_SELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/StoredProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$
            helpCheckNodeSelection(dbNode, "/StoredProcedure/CreateCustomerProcedure", JdbcNode.UNSELECTED); //$NON-NLS-1$

            // Build the structure ...
            JdbcModelStructure structure = null;
            structure = JdbcModelStructure.build(dbNode, includeCatalogs, includeSchemas);
            if (PRINT) {
                System.out.println("\n " + dbNode.getName() + " catalogs=" + includeCatalogs + "; schemas=" + includeSchemas); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                structure.print(System.out);
            }

            helpCheckStructure(dbNode, structure, null, "/Table/SUPPLIER"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, null, "/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, null, "/Table/PARTS"); //$NON-NLS-1$
            helpCheckStructure(dbNode, structure, "/Table/SUPPLIER", null); //$NON-NLS-1$

            helpCheckNotInStructure(dbNode, structure, "/Table"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/View"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/View/CUSTOMERS_VIEW"); //$NON-NLS-1$
            helpCheckNotInStructure(dbNode, structure, "/StoredProcedure/CreateCustomerProcedure"); //$NON-NLS-1$
        }
    }

}
