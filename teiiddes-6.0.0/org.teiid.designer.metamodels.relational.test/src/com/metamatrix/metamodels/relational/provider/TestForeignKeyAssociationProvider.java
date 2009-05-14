/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.metamodels.relational.Table;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.core.association.AssociationProvider;

/**
 * TestForeignKeyAssociationProvider
 */
public class TestForeignKeyAssociationProvider extends TestCase {

    private static final Class[] VALID_CLASSES_TYPES = new Class[] {Table.class, Column.class, PrimaryKey.class, ForeignKey.class};

    /**
     * Constructor for TestForeignKeyAssociationProvider.
     * 
     * @param name
     */
    public TestForeignKeyAssociationProvider( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestForeignKeyAssociationProvider"); //$NON-NLS-1$
        suite.addTestSuite(TestForeignKeyAssociationProvider.class);

        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }

            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }

    public static void main( String args[] ) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    public static void oneTimeSetUp() {
    }

    public static void oneTimeTearDown() {
    }

    public static Table helpAddNewTableToList( final List objs ) {
        Table table = RelationalFactory.eINSTANCE.createBaseTable();
        objs.add(table);
        return table;
    }

    public static Table helpAddNewColumnToList( final List objs ) {
        Table table = RelationalFactory.eINSTANCE.createBaseTable();
        Column col = RelationalFactory.eINSTANCE.createColumn();
        col.setOwner(table);
        objs.add(col);
        return table;
    }

    public static BaseTable helpAddNewFkToList( final List objs ) {
        BaseTable table = RelationalFactory.eINSTANCE.createBaseTable();
        ForeignKey fk = RelationalFactory.eINSTANCE.createForeignKey();
        fk.setTable(table);
        objs.add(fk);
        return table;
    }

    public static BaseTable helpAddNewPkToList( final List objs ) {
        BaseTable table = RelationalFactory.eINSTANCE.createBaseTable();
        PrimaryKey pk = RelationalFactory.eINSTANCE.createPrimaryKey();
        pk.setTable(table);
        objs.add(pk);
        return table;
    }

    public static List helpCreateNewListWithInstance( final Object obj ) {
        final List objs = new ArrayList();
        objs.add(obj);
        return objs;
    }

    public AssociationDescriptor helpGetFirstDescriptor( final Collection descriptors ) {
        assertNotNull(descriptors);
        assertTrue(descriptors.size() > 0);
        final AssociationDescriptor firstDescr = (AssociationDescriptor)descriptors.iterator().next();
        assertNotNull(firstDescr);
        return firstDescr;
    }

    public void testCreate() {
        System.out.println("TestForeignKeyAssociationProvider.testCreate()"); //$NON-NLS-1$
        new ForeignKeyAssociationProvider();
    }

    public void testGetNewAssociationDescriptors0() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors0()"); //$NON-NLS-1$

        // Pass a null list in
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        try {
            provider.getNewAssociationDescriptors(null);
        } catch (IllegalArgumentException e) {
            // Expected error
            return;
        }
        fail("Expected a failure but instead succeeded"); //$NON-NLS-1$
    }

    public void testGetNewAssociationDescriptors1() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors1()"); //$NON-NLS-1$

        // Pass an empty list in
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        Collection descriptors = null;
        List eObjects = new ArrayList();
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(0, descriptors.size());

        // Pass a list containing an invalid object in
        Schema schema = RelationalFactory.eINSTANCE.createSchema();
        eObjects.add(schema);
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(0, descriptors.size());

        // Pass a list containing only one table
        eObjects.clear();
        helpAddNewTableToList(eObjects);
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(0, descriptors.size());
    }

    public void testGetNewAssociationDescriptors2() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors2()"); //$NON-NLS-1$

        // Create a selection list containing only two tables
        List eObjects = new ArrayList();
        helpAddNewTableToList(eObjects);
        helpAddNewTableToList(eObjects);
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        Collection descriptors = null;
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(1, descriptors.size());
        assertEquals(false, helpGetFirstDescriptor(descriptors).isAmbiguous());
    }

    public void testGetNewAssociationDescriptors3() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors3()"); //$NON-NLS-1$

        // Create a selection list containing a primary key and a foreign key
        List eObjects = new ArrayList();
        helpAddNewPkToList(eObjects);
        helpAddNewFkToList(eObjects);
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        Collection descriptors = null;
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(1, descriptors.size());
        assertEquals(false, helpGetFirstDescriptor(descriptors).isAmbiguous());
    }

    public void testGetNewAssociationDescriptors4() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors4()"); //$NON-NLS-1$

        // Create a selection list containing a primary key and a foreign key where
        // the foreign key already has a reference to a different primary key
        List eObjects = new ArrayList();
        helpAddNewPkToList(eObjects);
        BaseTable tableB = helpAddNewFkToList(eObjects);
        PrimaryKey pkC = RelationalFactory.eINSTANCE.createPrimaryKey();
        ((ForeignKey)tableB.getForeignKeys().get(0)).setUniqueKey(pkC);
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        Collection descriptors = null;
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(1, descriptors.size());
        assertEquals(true, helpGetFirstDescriptor(descriptors).isAmbiguous());
        assertEquals(1, helpGetFirstDescriptor(descriptors).getChildren().length);
    }

    public void testGetNewAssociationDescriptors5() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors5()"); //$NON-NLS-1$

        // Create a selection list containing a primary key, columns in
        // the primary key table, and a foreign key
        List eObjects = new ArrayList();
        Table tableA = helpAddNewPkToList(eObjects);
        Column colA = RelationalFactory.eINSTANCE.createColumn();
        colA.setOwner(tableA);
        eObjects.add(colA);
        helpAddNewFkToList(eObjects);
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        Collection descriptors = null;
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(1, descriptors.size());
        assertEquals(true, helpGetFirstDescriptor(descriptors).isAmbiguous());
        assertEquals(1, helpGetFirstDescriptor(descriptors).getChildren().length);
    }

    public void testGetNewAssociationDescriptors6() throws Exception {
        System.out.println("TestForeignKeyAssociationProvider.testGetNewAssociationDescriptors6()"); //$NON-NLS-1$

        // Create a selection list containing a primary key, a foreign key,
        // and columns in the foreign key table
        List eObjects = new ArrayList();
        helpAddNewPkToList(eObjects);
        Table tableB = helpAddNewFkToList(eObjects);
        Column colB = RelationalFactory.eINSTANCE.createColumn();
        colB.setOwner(tableB);
        eObjects.add(colB);
        AssociationProvider provider = new ForeignKeyAssociationProvider();
        Collection descriptors = null;
        descriptors = provider.getNewAssociationDescriptors(eObjects);
        assertNotNull(descriptors);
        assertEquals(1, descriptors.size());
        assertEquals(true, helpGetFirstDescriptor(descriptors).isAmbiguous());
        assertEquals(2, helpGetFirstDescriptor(descriptors).getChildren().length);
    }

    public void testContainsValidObjects() {
        System.out.println("TestForeignKeyAssociationProvider.testContainsValidObjects()"); //$NON-NLS-1$

        // Create a selection list containing a null
        List eObjects = helpCreateNewListWithInstance(null);
        assertEquals(false, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one table
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createBaseTable());
        assertEquals(true, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one view
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createView());
        assertEquals(false, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one column
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createColumn());
        assertEquals(true, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one foreign key
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createForeignKey());
        assertEquals(true, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one primary key
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createPrimaryKey());
        assertEquals(true, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one index
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createIndex());
        assertEquals(false, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one Schema
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createSchema());
        assertEquals(false, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one Catalog
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createCatalog());
        assertEquals(false, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));

        // Create a selection list containing one AccessPattern
        eObjects = helpCreateNewListWithInstance(RelationalFactory.eINSTANCE.createAccessPattern());
        assertEquals(false, ForeignKeyAssociationProvider.containsValidObjects(eObjects, VALID_CLASSES_TYPES));
    }

    public void testGetTables1() {
        System.out.println("TestForeignKeyAssociationProvider.testGetTables1()"); //$NON-NLS-1$

        try {
            ForeignKeyAssociationProvider.getTables(null);
        } catch (IllegalArgumentException e) {
            // Expected
            return;
        }
        fail("Expected failure but succeeded"); //$NON-NLS-1$
    }

    public void testGetTables2() {
        System.out.println("TestForeignKeyAssociationProvider.testGetTables2()"); //$NON-NLS-1$

        // Create a selection list containing only a null
        List eObjects = new ArrayList();
        eObjects.add(null);
        assertEquals(0, ForeignKeyAssociationProvider.getTables(eObjects).size());

        // Create a selection list containing only one table
        eObjects = new ArrayList();
        Table tableA = helpAddNewTableToList(eObjects);
        assertEquals(1, ForeignKeyAssociationProvider.getTables(eObjects).size());
        assertEquals(tableA, ForeignKeyAssociationProvider.getTables(eObjects).get(0));

        // Add a second table to the list and test
        Table tableB = helpAddNewTableToList(eObjects);
        assertEquals(2, ForeignKeyAssociationProvider.getTables(eObjects).size());
        assertEquals(tableA, ForeignKeyAssociationProvider.getTables(eObjects).get(0));
        assertEquals(tableB, ForeignKeyAssociationProvider.getTables(eObjects).get(1));
    }

    public void testGetTables3() {
        System.out.println("TestForeignKeyAssociationProvider.testGetTables3()"); //$NON-NLS-1$

        // Create a selection list containing only one column
        List eObjects = new ArrayList();
        Table tableA = helpAddNewColumnToList(eObjects);
        assertEquals(1, ForeignKeyAssociationProvider.getTables(eObjects).size());
        assertEquals(tableA, ForeignKeyAssociationProvider.getTables(eObjects).get(0));

        // Add a second column to the list and test
        Table tableB = helpAddNewColumnToList(eObjects);
        assertEquals(2, ForeignKeyAssociationProvider.getTables(eObjects).size());
        assertEquals(tableA, ForeignKeyAssociationProvider.getTables(eObjects).get(0));
        assertEquals(tableB, ForeignKeyAssociationProvider.getTables(eObjects).get(1));
    }

    public void testGetTables4() {
        System.out.println("TestForeignKeyAssociationProvider.testGetTables4()"); //$NON-NLS-1$

        // Create a selection list containing only one foreign key
        List eObjects = new ArrayList();
        Table tableA = helpAddNewFkToList(eObjects);
        assertEquals(1, ForeignKeyAssociationProvider.getTables(eObjects).size());
        assertEquals(tableA, ForeignKeyAssociationProvider.getTables(eObjects).get(0));

        // Add a primary key to the list and test
        Table tableB = helpAddNewPkToList(eObjects);
        assertEquals(2, ForeignKeyAssociationProvider.getTables(eObjects).size());
        assertEquals(tableA, ForeignKeyAssociationProvider.getTables(eObjects).get(0));
        assertEquals(tableB, ForeignKeyAssociationProvider.getTables(eObjects).get(1));
    }

    public void testGetColumns1() {
        System.out.println("TestForeignKeyAssociationProvider.testGetColumns1()"); //$NON-NLS-1$

        try {
            ForeignKeyAssociationProvider.getColumns(Collections.EMPTY_LIST, null);
        } catch (IllegalArgumentException e) {
            // Expected
            return;
        }
        fail("Expected failure but succeeded"); //$NON-NLS-1$
    }

    public void testGetColumns2() {
        System.out.println("TestForeignKeyAssociationProvider.testGetColumns2()"); //$NON-NLS-1$

        // Create a selection list containing only a null
        List eObjects = new ArrayList();
        Table tableA = RelationalFactory.eINSTANCE.createBaseTable();
        eObjects.add(null);
        assertEquals(0, ForeignKeyAssociationProvider.getColumns(eObjects, tableA).size());

        // Create a selection list containing only two columns
        eObjects = new ArrayList();
        Table tableB = helpAddNewColumnToList(eObjects);
        Table tableC = helpAddNewColumnToList(eObjects);

        assertEquals(0, ForeignKeyAssociationProvider.getColumns(eObjects, tableA).size());
        assertEquals(1, ForeignKeyAssociationProvider.getColumns(eObjects, tableB).size());
        assertEquals(1, ForeignKeyAssociationProvider.getColumns(eObjects, tableC).size());
        assertEquals(tableB.getColumns().get(0), ForeignKeyAssociationProvider.getColumns(eObjects, tableB).get(0));
        assertEquals(tableC.getColumns().get(0), ForeignKeyAssociationProvider.getColumns(eObjects, tableC).get(0));
    }

    public void testGetForeignKey1() {
        System.out.println("TestForeignKeyAssociationProvider.testGetForeignKey1()"); //$NON-NLS-1$

        try {
            ForeignKeyAssociationProvider.getForeignKey(Collections.EMPTY_LIST, null);
        } catch (IllegalArgumentException e) {
            // Expected
            return;
        }
        fail("Expected failure but succeeded"); //$NON-NLS-1$
    }

    public void testGetForeignKey2() {
        System.out.println("TestForeignKeyAssociationProvider.testGetForeignKey2()"); //$NON-NLS-1$

        // Create a selection list containing only a null
        List eObjects = new ArrayList();
        Table tableA = RelationalFactory.eINSTANCE.createBaseTable();
        eObjects.add(null);
        assertNull(ForeignKeyAssociationProvider.getForeignKey(eObjects, tableA));

        // Create a selection list containing only two columns
        eObjects = new ArrayList();
        BaseTable tableB = helpAddNewFkToList(eObjects);
        BaseTable tableC = helpAddNewFkToList(eObjects);

        assertNull(ForeignKeyAssociationProvider.getForeignKey(eObjects, tableA));
        assertNotNull(ForeignKeyAssociationProvider.getForeignKey(eObjects, tableB));
        assertNotNull(ForeignKeyAssociationProvider.getForeignKey(eObjects, tableC));
        assertEquals(tableB.getForeignKeys().get(0), ForeignKeyAssociationProvider.getForeignKey(eObjects, tableB));
        assertEquals(tableC.getForeignKeys().get(0), ForeignKeyAssociationProvider.getForeignKey(eObjects, tableC));
    }

    public void testGetForeignKeys1() {
        System.out.println("TestForeignKeyAssociationProvider.testGetForeignKeys1()"); //$NON-NLS-1$

        try {
            ForeignKeyAssociationProvider.getForeignKeys(Collections.EMPTY_LIST, null);
        } catch (IllegalArgumentException e) {
            // Expected
            return;
        }
        fail("Expected failure but succeeded"); //$NON-NLS-1$
    }

    public void testGetForeignKeys2() {
        System.out.println("TestForeignKeyAssociationProvider.testGetForeignKeys2()"); //$NON-NLS-1$

        // Create a selection list containing only a null
        List eObjects = new ArrayList();
        BaseTable tableA = RelationalFactory.eINSTANCE.createBaseTable();
        eObjects.add(null);
        assertEquals(0, ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableA).size());

        // Create a selection list containing only two columns
        eObjects = new ArrayList();
        BaseTable tableB = helpAddNewFkToList(eObjects);
        BaseTable tableC = helpAddNewFkToList(eObjects);

        assertEquals(0, ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableA).size());
        assertEquals(1, ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableB).size());
        assertEquals(1, ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableC).size());
        assertEquals(tableB.getForeignKeys().get(0), ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableB).get(0));
        assertEquals(tableC.getForeignKeys().get(0), ForeignKeyAssociationProvider.getForeignKeys(eObjects, tableC).get(0));
    }

}
