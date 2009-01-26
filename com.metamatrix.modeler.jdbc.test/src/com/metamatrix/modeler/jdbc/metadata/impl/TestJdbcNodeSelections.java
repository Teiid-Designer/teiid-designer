/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import com.metamatrix.modeler.jdbc.JdbcFactory;
import com.metamatrix.modeler.jdbc.JdbcImportSettings;
import com.metamatrix.modeler.jdbc.JdbcPlugin;
import com.metamatrix.modeler.jdbc.JdbcSource;
import com.metamatrix.modeler.jdbc.impl.JdbcFactoryImpl;
import com.metamatrix.modeler.jdbc.metadata.JdbcDatabase;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * TestJdbcNodeSelections
 */
public class TestJdbcNodeSelections extends TestCase {

    private static final int SELECTED = JdbcNodeSelections.SELECTED;
    private static final int UNSELECTED = JdbcNodeSelections.UNSELECTED;
    private static final int PARTIALLY = JdbcNodeSelections.PARTIALLY_SELECTED;
    private static final int UNKNOWN = JdbcNodeSelections.UNKNOWN;

    private static final JdbcFactory JDBC_FACTORY = new JdbcFactoryImpl();

    private JdbcImportSettings settingsWithSchemas;
    private JdbcImportSettings settingsWithCatalogs;
    private JdbcImportSettings settingsWithCatalogsAndSchemas;
    private JdbcImportSettings settingsWithNoCatalogsOrSchemas;

    private JdbcDatabase dbWithSchemas;
    private JdbcDatabase dbWithCatalogs;
    private JdbcDatabase dbWithCatalogsAndSchemas;
    private JdbcDatabase dbWithNoCatalogsOrSchemas;

    private JdbcNodeSelections selectionsWithSchemas;
    private JdbcNodeSelections selectionsWithCatalogs;
    private JdbcNodeSelections selectionsWithCatalogsAndSchemas;
    private JdbcNodeSelections selectionsWithNoCatalogsOrSchemas;

    private JdbcImportSettings outputSettings;
    private JdbcSource outputSource;

    /**
     * Constructor for TestJdbcNodeSelections.
     * 
     * @param name
     */
    public TestJdbcNodeSelections( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.outputSource = JDBC_FACTORY.createJdbcSource();
        this.outputSettings = JDBC_FACTORY.createJdbcImportSettings();
        this.outputSource.setImportSettings(this.outputSettings);

        // Set up the "database" with only schemas ...
        this.settingsWithSchemas = JDBC_FACTORY.createJdbcImportSettings();
        this.settingsWithSchemas.getIncludedSchemaPaths().add("/PARTSSUPPLIER"); //$NON-NLS-1$
        this.settingsWithSchemas.getExcludedObjectPaths().add("/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        this.selectionsWithSchemas = new JdbcNodeSelections();
        this.selectionsWithSchemas.initialize(this.settingsWithSchemas);
        this.dbWithSchemas = new FakeJdbcDatabase("DbWithSchemas", this.selectionsWithSchemas); //$NON-NLS-1$
        ((DatabaseInfoImpl)this.dbWithSchemas.getDatabaseInfo()).setUserName("partssupplier"); //$NON-NLS-1$

        // Set up the "database" with only catalogs ...
        this.settingsWithCatalogs = JDBC_FACTORY.createJdbcImportSettings();
        this.settingsWithCatalogs.getIncludedCatalogPaths().add("/PARTSSUPPLIER"); //$NON-NLS-1$
        this.settingsWithCatalogs.getExcludedObjectPaths().add("/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        this.selectionsWithCatalogs = new JdbcNodeSelections();
        this.selectionsWithCatalogs.initialize(this.settingsWithCatalogs);
        this.dbWithCatalogs = new FakeJdbcDatabase("DbWithCatalogs", this.selectionsWithCatalogs); //$NON-NLS-1$
        ((DatabaseInfoImpl)this.dbWithCatalogs.getDatabaseInfo()).setUserName("partssupplier"); //$NON-NLS-1$

        // Set up the "database" with catalogs AND schemas ...
        this.settingsWithCatalogsAndSchemas = JDBC_FACTORY.createJdbcImportSettings();
        this.settingsWithCatalogsAndSchemas.getIncludedCatalogPaths().add("/dbo"); //$NON-NLS-1$
        this.settingsWithCatalogsAndSchemas.getIncludedSchemaPaths().add("/dbo/FULLY_SELECTED"); //$NON-NLS-1$
        this.settingsWithCatalogsAndSchemas.getIncludedSchemaPaths().add("/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        this.settingsWithCatalogsAndSchemas.getExcludedObjectPaths().add("/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        this.selectionsWithCatalogsAndSchemas = new JdbcNodeSelections();
        this.selectionsWithCatalogsAndSchemas.initialize(this.settingsWithCatalogsAndSchemas);
        this.dbWithCatalogsAndSchemas = new FakeJdbcDatabase("DbWithCatalogsAndSchemas", this.selectionsWithCatalogsAndSchemas); //$NON-NLS-1$
        ((DatabaseInfoImpl)this.dbWithCatalogsAndSchemas.getDatabaseInfo()).setUserName("partssupplier"); //$NON-NLS-1$

        // Set up the "database" with NO catalogs or schemas ...
        this.settingsWithNoCatalogsOrSchemas = JDBC_FACTORY.createJdbcImportSettings();
        this.settingsWithNoCatalogsOrSchemas.getExcludedObjectPaths().add("/Table/CUSTOMER"); //$NON-NLS-1$
        this.selectionsWithNoCatalogsOrSchemas = new JdbcNodeSelections();
        this.selectionsWithNoCatalogsOrSchemas.initialize(this.settingsWithNoCatalogsOrSchemas);
        this.dbWithNoCatalogsOrSchemas = new FakeJdbcDatabase("DbWithNoCatalogsOrSchemas", this.selectionsWithNoCatalogsOrSchemas); //$NON-NLS-1$
        ((DatabaseInfoImpl)this.dbWithNoCatalogsOrSchemas.getDatabaseInfo()).setUserName("partssupplier"); //$NON-NLS-1$
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
        TestSuite suite = new TestSuite("TestJdbcNodeSelections"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcNodeSelections.class);
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

    public static void helpTestSelection( final JdbcNodeSelections selections,
                                          final int expectedSelectionMode,
                                          final String strPath ) {
        final IPath path = new Path(strPath);
        final int actualMode = selections.getSelectionMode(path);
        assertEquals(expectedSelectionMode, actualMode);
    }

    public static void helpTestSelection( final JdbcDatabase dbNode,
                                          final int expectedSelectionMode,
                                          final String strPath ) {
        final JdbcNode node = dbNode.findJdbcNode(strPath);
        assertNotNull("Unable to find JdbcNode for path \"" + strPath + "\"", node); //$NON-NLS-1$//$NON-NLS-2$
        final int actualMode = node.getSelectionMode();
        assertEquals(expectedSelectionMode, actualMode);
    }

    public void helpPopulateDatabaseWithSchema() throws Exception {
        // make sure the database has no children ...
        this.dbWithSchemas.refresh();
        JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithSchemas;

        // Add the database objects ...
        final JdbcSchemaImpl partssupplier = new JdbcSchemaImpl(db, "PARTSSUPPLIER"); //$NON-NLS-1$
        db.addChild(partssupplier);
        final JdbcSchemaImpl other = new JdbcSchemaImpl(db, "OTHER"); //$NON-NLS-1$
        db.addChild(other);

        final JdbcTableTypeImpl tables = new JdbcTableTypeImpl(partssupplier, "Table"); //$NON-NLS-1$
        partssupplier.addChild(tables);
        final JdbcTableTypeImpl views = new JdbcTableTypeImpl(partssupplier, "View"); //$NON-NLS-1$
        partssupplier.addChild(views);

        // Add tables to the schema ...
        final JdbcTableImpl customers = new JdbcTableImpl(tables, "CUSTOMER"); //$NON-NLS-1$
        tables.addChild(customers);
        final JdbcTableImpl parts = new JdbcTableImpl(tables, "PARTS"); //$NON-NLS-1$
        tables.addChild(parts);
        final JdbcTableImpl suppliers = new JdbcTableImpl(tables, "SUPPLIERS"); //$NON-NLS-1$
        tables.addChild(suppliers);
        final JdbcTableImpl supplier_parts = new JdbcTableImpl(tables, "SUPPLIER_PARTS"); //$NON-NLS-1$
        tables.addChild(supplier_parts);
    }

    public void helpPopulateDatabaseWithCatalog() throws Exception {
        // make sure the database has no children ...
        this.dbWithCatalogs.refresh();
        JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithCatalogs;

        // Add the database objects ...
        final JdbcCatalogImpl partssupplier = new JdbcCatalogImpl(db, "PARTSSUPPLIER"); //$NON-NLS-1$
        db.addChild(partssupplier);
        final JdbcCatalogImpl other = new JdbcCatalogImpl(db, "OTHER"); //$NON-NLS-1$
        db.addChild(other);

        final JdbcTableTypeImpl tables = new JdbcTableTypeImpl(partssupplier, "Table"); //$NON-NLS-1$
        partssupplier.addChild(tables);
        final JdbcTableTypeImpl views = new JdbcTableTypeImpl(partssupplier, "View"); //$NON-NLS-1$
        partssupplier.addChild(views);

        // Add tables to the schema ...
        final JdbcTableImpl customers = new JdbcTableImpl(tables, "CUSTOMER"); //$NON-NLS-1$
        tables.addChild(customers);
        final JdbcTableImpl parts = new JdbcTableImpl(tables, "PARTS"); //$NON-NLS-1$
        tables.addChild(parts);
        final JdbcTableImpl suppliers = new JdbcTableImpl(tables, "SUPPLIERS"); //$NON-NLS-1$
        tables.addChild(suppliers);
        final JdbcTableImpl supplier_parts = new JdbcTableImpl(tables, "SUPPLIER_PARTS"); //$NON-NLS-1$
        tables.addChild(supplier_parts);
    }

    public void helpPopulateDatabaseWithCatalogAndSchema() throws Exception {
        // make sure the database has no children ...
        this.dbWithCatalogsAndSchemas.refresh();
        JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithCatalogsAndSchemas;

        // Add the database objects ...
        final JdbcCatalogImpl dbo = new JdbcCatalogImpl(db, "dbo"); //$NON-NLS-1$
        db.addChild(dbo);
        final JdbcCatalogImpl dbx = new JdbcCatalogImpl(db, "dbx"); //$NON-NLS-1$
        db.addChild(dbx);

        // Add the database objects ...
        final JdbcSchemaImpl partssupplier = new JdbcSchemaImpl(dbo, "PARTSSUPPLIER"); //$NON-NLS-1$
        dbo.addChild(partssupplier);
        final JdbcSchemaImpl other = new JdbcSchemaImpl(dbo, "OTHER"); //$NON-NLS-1$
        dbo.addChild(other);
        final JdbcSchemaImpl other2 = new JdbcSchemaImpl(dbx, "OTHER_IN_X"); //$NON-NLS-1$
        dbx.addChild(other2);

        final JdbcTableTypeImpl tables = new JdbcTableTypeImpl(partssupplier, "Table"); //$NON-NLS-1$
        partssupplier.addChild(tables);
        final JdbcTableTypeImpl views = new JdbcTableTypeImpl(partssupplier, "View"); //$NON-NLS-1$
        partssupplier.addChild(views);

        // Add tables to the schema ...
        final JdbcTableImpl customers = new JdbcTableImpl(tables, "CUSTOMER"); //$NON-NLS-1$
        tables.addChild(customers);
        final JdbcTableImpl parts = new JdbcTableImpl(tables, "PARTS"); //$NON-NLS-1$
        tables.addChild(parts);
        final JdbcTableImpl suppliers = new JdbcTableImpl(tables, "SUPPLIERS"); //$NON-NLS-1$
        tables.addChild(suppliers);
        final JdbcTableImpl supplier_parts = new JdbcTableImpl(tables, "SUPPLIER_PARTS"); //$NON-NLS-1$
        tables.addChild(supplier_parts);

        // Add the fully-selected schema
        final JdbcSchemaImpl fullySelected = new JdbcSchemaImpl(dbo, "FULLY_SELECTED"); //$NON-NLS-1$
        dbo.addChild(fullySelected);
        final JdbcTableTypeImpl tables2 = new JdbcTableTypeImpl(fullySelected, "Table"); //$NON-NLS-1$
        partssupplier.addChild(tables2);
        final JdbcTableTypeImpl views2 = new JdbcTableTypeImpl(fullySelected, "View"); //$NON-NLS-1$
        partssupplier.addChild(views2);

        final JdbcTableImpl someTable1 = new JdbcTableImpl(tables2, "MYTABLE"); //$NON-NLS-1$
        tables2.addChild(someTable1);
        final JdbcTableImpl someTable2 = new JdbcTableImpl(tables2, "YOURTABLE"); //$NON-NLS-1$
        tables2.addChild(someTable2);

    }

    public void helpPopulateDatabaseWithNoCatalogOrSchema() throws Exception {
        // make sure the database has no children ...
        this.dbWithNoCatalogsOrSchemas.refresh();
        JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithNoCatalogsOrSchemas;

        // Add the database objects ...
        final JdbcTableTypeImpl tables = new JdbcTableTypeImpl(db, "Table"); //$NON-NLS-1$
        db.addChild(tables);
        final JdbcTableTypeImpl views = new JdbcTableTypeImpl(db, "View"); //$NON-NLS-1$
        db.addChild(views);

        // Add tables ...
        final JdbcTableImpl customers = new JdbcTableImpl(tables, "CUSTOMER"); //$NON-NLS-1$
        tables.addChild(customers);
        final JdbcTableImpl parts = new JdbcTableImpl(tables, "PARTS"); //$NON-NLS-1$
        tables.addChild(parts);
        final JdbcTableImpl suppliers = new JdbcTableImpl(tables, "SUPPLIERS"); //$NON-NLS-1$
        tables.addChild(suppliers);
        final JdbcTableImpl supplier_parts = new JdbcTableImpl(tables, "SUPPLIER_PARTS"); //$NON-NLS-1$
        tables.addChild(supplier_parts);

        // Add views ...
        final JdbcTableImpl products = new JdbcTableImpl(views, "PRODUCTS"); //$NON-NLS-1$
        views.addChild(products);
        final JdbcTableImpl orders = new JdbcTableImpl(views, "ORDERS"); //$NON-NLS-1$
        views.addChild(orders);
    }

    public void helpWriteJdbcImportSettings( final JdbcDatabase dbNode,
                                             final JdbcImportSettings originalSettings ) throws Exception {
        JdbcPlugin.recordJdbcDatabaseSelections(this.outputSource, dbNode);

        // Compare the original to the output ...
        final List actualExcludedObjs = this.outputSettings.getExcludedObjectPaths();
        final List expectedExcludedObj = originalSettings.getExcludedObjectPaths();
        assertEquals(expectedExcludedObj.size(), actualExcludedObjs.size());
        assertTrue(actualExcludedObjs.containsAll(expectedExcludedObj));
        assertTrue(expectedExcludedObj.containsAll(actualExcludedObjs));

        final List actualIncludedSchemas = this.outputSettings.getIncludedSchemaPaths();
        final List expectedIncludedSchemas = originalSettings.getIncludedSchemaPaths();
        assertEquals(expectedIncludedSchemas.size(), actualIncludedSchemas.size());
        assertTrue(actualIncludedSchemas.containsAll(expectedIncludedSchemas));
        assertTrue(expectedIncludedSchemas.containsAll(actualIncludedSchemas));

        final List actualIncludedCatalogs = this.outputSettings.getIncludedCatalogPaths();
        final List expectedIncludedCatalogs = originalSettings.getIncludedCatalogPaths();
        assertEquals(expectedIncludedCatalogs.size(), actualIncludedCatalogs.size());
        assertTrue(actualIncludedCatalogs.containsAll(expectedIncludedCatalogs));
        assertTrue(expectedIncludedCatalogs.containsAll(actualIncludedCatalogs));

    }

    public void testDefaultConstructor() {
        new JdbcNodeSelections();
    }

    // -------------------------------------------------------------------------
    // DB WITH SCHEMAS
    // -------------------------------------------------------------------------

    public void testSelectionsForDbWithSchemas() {
        final JdbcNodeSelections selections = this.selectionsWithSchemas;

        helpTestSelection(selections, PARTIALLY, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testNodeSelectionModesForDbWithSchemas() throws Exception {
        helpPopulateDatabaseWithSchema();

        final JdbcNodeSelections selections = this.selectionsWithSchemas;
        final JdbcDatabase db = this.dbWithSchemas;

        helpTestSelection(selections, PARTIALLY, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, PARTIALLY, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    // -------------------------------------------------------------------------
    // DB WITH CATALOGS
    // -------------------------------------------------------------------------

    public void testSelectionsForDbWithCatalogs() {
        final JdbcNodeSelections selections = this.selectionsWithCatalogs;

        helpTestSelection(selections, PARTIALLY, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testNodeSelectionModesForDbWithCatalogs() throws Exception {
        helpPopulateDatabaseWithCatalog();

        final JdbcNodeSelections selections = this.selectionsWithCatalogs;
        final JdbcDatabase db = this.dbWithCatalogs;

        helpTestSelection(selections, PARTIALLY, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, PARTIALLY, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    // -------------------------------------------------------------------------
    // DB WITH CATALOGS AND SCHEMAS
    // -------------------------------------------------------------------------

    public void testSelectionsForDbWithCatalogsAndSchemas() {
        final JdbcNodeSelections sel = this.selectionsWithCatalogsAndSchemas;

        helpTestSelection(sel, PARTIALLY, "/dbo"); //$NON-NLS-1$
        helpTestSelection(sel, UNSELECTED, "/dbx"); //$NON-NLS-1$
        helpTestSelection(sel, SELECTED, "/dbo/FULLY_SELECTED"); //$NON-NLS-1$
        helpTestSelection(sel, SELECTED, "/dbo/FULLY_SELECTED/Table/MYTABLE"); //$NON-NLS-1$
        helpTestSelection(sel, PARTIALLY, "/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(sel, UNSELECTED, "/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(sel, UNKNOWN, "/dbo/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(sel, UNKNOWN, "/dbo/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(sel, UNKNOWN, "/dbo/OTHER"); //$NON-NLS-1$
        helpTestSelection(sel, UNSELECTED, "/dbx/OTHER_IN_X"); //$NON-NLS-1$
        helpTestSelection(sel, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testNodeSelectionModesForDbWithCatalogsAndSchemas() throws Exception {
        helpPopulateDatabaseWithCatalogAndSchema();

        final JdbcNodeSelections sel = this.selectionsWithCatalogsAndSchemas;
        final JdbcDatabase db = this.dbWithCatalogsAndSchemas;

        helpTestSelection(sel, PARTIALLY, "/dbo"); //$NON-NLS-1$
        helpTestSelection(sel, UNSELECTED, "/dbx"); //$NON-NLS-1$
        helpTestSelection(sel, SELECTED, "/dbo/FULLY_SELECTED"); //$NON-NLS-1$
        helpTestSelection(sel, SELECTED, "/dbo/FULLY_SELECTED/Table/MYTABLE"); //$NON-NLS-1$
        helpTestSelection(sel, PARTIALLY, "/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(sel, UNSELECTED, "/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(sel, UNKNOWN, "/dbo/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(sel, UNKNOWN, "/dbo/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(sel, UNKNOWN, "/dbo/OTHER"); //$NON-NLS-1$
        helpTestSelection(sel, UNSELECTED, "/dbx/OTHER_IN_X"); //$NON-NLS-1$
        helpTestSelection(sel, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, PARTIALLY, "/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbo/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbx"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbx/OTHER_IN_X"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    // -------------------------------------------------------------------------
    // DB WITH NO CATALOGS OR SCHEMAS
    // -------------------------------------------------------------------------

    public void testSelectionsForDbWithNoCatalogsOrSchemas() {
        final JdbcNodeSelections selections = this.selectionsWithNoCatalogsOrSchemas;

        helpTestSelection(selections, UNSELECTED, "/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testNodeSelectionModesForDbWithNoCatalogsOrSchemas() throws Exception {
        helpPopulateDatabaseWithNoCatalogOrSchema();

        final JdbcNodeSelections selections = this.selectionsWithNoCatalogsOrSchemas;
        final JdbcDatabase db = this.dbWithNoCatalogsOrSchemas;

        helpTestSelection(selections, UNSELECTED, "/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, SELECTED, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    // -------------------------------------------------------------------------
    // USING IMPORT SETTINGS
    // -------------------------------------------------------------------------
    public void testOutputtingImportSettings1() throws Exception {
        helpPopulateDatabaseWithSchema();
        helpWriteJdbcImportSettings(this.dbWithSchemas, this.settingsWithSchemas);
    }

    public void testOutputtingImportSettings2() throws Exception {
        helpPopulateDatabaseWithCatalog();
        helpWriteJdbcImportSettings(this.dbWithCatalogs, this.settingsWithCatalogs);
    }

    public void testOutputtingImportSettings3() throws Exception {
        helpPopulateDatabaseWithCatalogAndSchema();
        helpWriteJdbcImportSettings(this.dbWithCatalogsAndSchemas, this.settingsWithCatalogsAndSchemas);
    }

    public void testOutputtingImportSettings4() throws Exception {
        helpPopulateDatabaseWithNoCatalogOrSchema();
        helpWriteJdbcImportSettings(this.dbWithNoCatalogsOrSchemas, this.settingsWithNoCatalogsOrSchemas);
    }

    // -------------------------------------------------------------------------
    // MARKING DEFAULT NODES
    // -------------------------------------------------------------------------

    public void testSelectingDefaultNodesOnDbWithSchema() throws Exception {
        final JdbcImportSettings settings = this.settingsWithSchemas;
        final JdbcNodeSelections selections = this.selectionsWithSchemas;
        final JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithSchemas;

        settings.getIncludedCatalogPaths().clear();
        settings.getIncludedSchemaPaths().clear();
        settings.getExcludedObjectPaths().clear();

        // populate the nodes ...
        helpPopulateDatabaseWithSchema();

        // make the default selections ...
        db.selectDefaultNodes();

        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testSelectingDefaultNodesOnDbWithCatalog() throws Exception {
        final JdbcImportSettings settings = this.settingsWithCatalogs;
        final JdbcNodeSelections selections = this.selectionsWithCatalogs;
        final JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithCatalogs;

        settings.getIncludedCatalogPaths().clear();
        settings.getIncludedSchemaPaths().clear();
        settings.getExcludedObjectPaths().clear();

        // populate the nodes ...
        helpPopulateDatabaseWithCatalog();

        // make the default selections ...
        db.selectDefaultNodes();

        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testSelectingDefaultNodesOnDbWithCatalogAndSchema() throws Exception {
        final JdbcImportSettings settings = this.settingsWithCatalogsAndSchemas;
        final JdbcNodeSelections selections = this.selectionsWithCatalogsAndSchemas;
        final JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithCatalogsAndSchemas;

        settings.getIncludedCatalogPaths().clear();
        settings.getIncludedSchemaPaths().clear();
        settings.getExcludedObjectPaths().clear();

        // populate the nodes ...
        helpPopulateDatabaseWithCatalogAndSchema();

        helpTestSelection(db, PARTIALLY, "/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbo/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbx"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbx/OTHER_IN_X"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$

        // make the default selections ...
        db.selectDefaultNodes();

        helpTestSelection(selections, PARTIALLY, "/dbo"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/dbx"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/dbo/FULLY_SELECTED"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/dbo/FULLY_SELECTED/Table/MYTABLE"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/dbo/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/dbo/PARTSSUPPLIER/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/dbo/OTHER"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/dbx/OTHER_IN_X"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/dbo/PARTSSUPPLIER/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbo/OTHER"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbx"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/dbx/OTHER_IN_X"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    public void testSelectingDefaultNodesOnDbWithNoCatalogOrSchema() throws Exception {
        final JdbcImportSettings settings = this.settingsWithNoCatalogsOrSchemas;
        final JdbcNodeSelections selections = this.selectionsWithNoCatalogsOrSchemas;
        final JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithNoCatalogsOrSchemas;

        settings.getIncludedCatalogPaths().clear();
        settings.getIncludedSchemaPaths().clear();
        settings.getExcludedObjectPaths().clear();

        // populate the nodes ...
        helpPopulateDatabaseWithNoCatalogOrSchema();

        // make the default selections ...
        db.selectDefaultNodes();

        helpTestSelection(selections, UNSELECTED, "/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNKNOWN, "/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, SELECTED, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

    /*
     * Added test case for Excel DB Import - sets productName to 'excel'
     * Expected result is the tables are selected by default - excel doesnt have Catalogs / Schemas
     * as expected for other Jdbc imports.
     */
    public void testSelectingDefaultNodesOnExcelDbWithNoCatalogOrSchema() throws Exception {
        final JdbcImportSettings settings = this.settingsWithNoCatalogsOrSchemas;
        final JdbcNodeSelections selections = this.selectionsWithNoCatalogsOrSchemas;
        final JdbcDatabaseImpl db = (JdbcDatabaseImpl)this.dbWithNoCatalogsOrSchemas;

        settings.getIncludedCatalogPaths().clear();
        settings.getIncludedSchemaPaths().clear();
        settings.getExcludedObjectPaths().clear();

        // populate the nodes ...
        helpPopulateDatabaseWithNoCatalogOrSchema();

        // Set the productName to excel
        ((DatabaseInfoImpl)this.dbWithNoCatalogsOrSchemas.getDatabaseInfo()).setProductName("excel"); //$NON-NLS-1$

        // make the default selections ...
        db.selectDefaultNodes();

        helpTestSelection(selections, SELECTED, "/Table/CUSTOMER"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(selections, SELECTED, "/Table/OTHER_TABLE"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(selections, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(selections, PARTIALLY, "/"); //$NON-NLS-1$

        helpTestSelection(db, SELECTED, "/Table/PARTS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/Table/SUPPLIERS"); //$NON-NLS-1$
        helpTestSelection(db, SELECTED, "/Table/SUPPLIER_PARTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/View/PRODUCTS"); //$NON-NLS-1$
        helpTestSelection(db, UNSELECTED, "/View/ORDERS"); //$NON-NLS-1$
        helpTestSelection(db, PARTIALLY, "/"); //$NON-NLS-1$
    }

}
