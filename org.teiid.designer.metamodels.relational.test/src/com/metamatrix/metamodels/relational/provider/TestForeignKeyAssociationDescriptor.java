/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.EObject;
import com.metamatrix.metamodels.relational.BaseTable;
import com.metamatrix.metamodels.relational.Column;
import com.metamatrix.metamodels.relational.ForeignKey;
import com.metamatrix.metamodels.relational.PrimaryKey;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.Schema;
import com.metamatrix.modeler.core.ModelerCoreException;
import com.metamatrix.modeler.core.association.AssociationDescriptor;
import com.metamatrix.modeler.internal.core.association.AbstractAssociationDescriptor;

/**
 * TestForeignKeyAssociationDescriptor
 */
public class TestForeignKeyAssociationDescriptor extends TestCase {

    /**
     * Constructor for TestForeignKeyAssociationDescriptor.
     * 
     * @param name
     */
    public TestForeignKeyAssociationDescriptor( String name ) {
        super(name);
    }

    /**
     * Construct the test suite, which uses a one-time setup call and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestForeignKeyAssociationDescriptor"); //$NON-NLS-1$
        suite.addTestSuite(TestForeignKeyAssociationDescriptor.class);

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

    private void helpAddNewTableToList( final List objs ) {
        TestForeignKeyAssociationProvider.helpAddNewTableToList(objs);
    }

    private void helpAddNewColumnToList( final List objs ) {
        TestForeignKeyAssociationProvider.helpAddNewColumnToList(objs);
    }

    private void helpAddNewFkToList( final List objs ) {
        TestForeignKeyAssociationProvider.helpAddNewFkToList(objs);
    }

    private void helpAddNewPkToList( final List objs ) {
        TestForeignKeyAssociationProvider.helpAddNewPkToList(objs);
    }

    public void testCreate1() {
        System.out.println("TestForeignKeyAssociationDescriptor.testCreate1()"); //$NON-NLS-1$
        try {
            new ForeignKeyAssociationDescriptor(null);
        } catch (IllegalArgumentException e) {
            // Expected error
            return;
        }
        fail("Expected failure but the test succeeded"); //$NON-NLS-1$
    }

    public void testCreate2() {
        System.out.println("TestForeignKeyAssociationDescriptor.testCreate2()"); //$NON-NLS-1$
        AssociationDescriptor descriptor = null;
        descriptor = new ForeignKeyAssociationDescriptor(Collections.EMPTY_LIST);
        assertNotNull(descriptor);
    }

    public void testGetType() {
        System.out.println("TestForeignKeyAssociationDescriptor.testGetType()"); //$NON-NLS-1$
        AssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(Collections.EMPTY_LIST);
        assertNotNull(descriptor.getType());
    }

    public void testGetText() {
        System.out.println("TestForeignKeyAssociationDescriptor.testGetText()"); //$NON-NLS-1$
        AssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(Collections.EMPTY_LIST);
        assertNotNull(descriptor.getText());
    }

    public void testCanCreate1() {
        System.out.println("TestForeignKeyAssociationDescriptor.testCanCreate1()"); //$NON-NLS-1$
        AbstractAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(Collections.EMPTY_LIST);
        assertEquals(true, descriptor.canCreate());
    }

    public void testCanCreate2() {
        System.out.println("TestForeignKeyAssociationDescriptor.testCanCreate2()"); //$NON-NLS-1$
        Schema schema = RelationalFactory.eINSTANCE.createSchema();
        List eObjects = new ArrayList();
        eObjects.add(schema);

        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).canCreate());
    }

    public void testCanCreate3() {
        System.out.println("TestForeignKeyAssociationDescriptor.testCanCreate3()"); //$NON-NLS-1$
        List eObjects = new ArrayList();
        helpAddNewTableToList(eObjects);

        assertEquals(true, new ForeignKeyAssociationDescriptor(eObjects).canCreate());
    }

    public void testIsComplete1() {
        System.out.println("TestForeignKeyAssociationDescriptor.testIsComplete1()"); //$NON-NLS-1$

        // Create a selection list containing only one table
        List eObjects = new ArrayList();
        helpAddNewTableToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a second table to the list and test
        helpAddNewTableToList(eObjects);
        assertEquals(true, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a third table to the list and test
        helpAddNewTableToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());
    }

    public void testIsComplete2() {
        System.out.println("TestForeignKeyAssociationDescriptor.testIsComplete2()"); //$NON-NLS-1$

        // Create a selection list containing only one column
        List eObjects = new ArrayList();
        helpAddNewColumnToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a second column to the list and test
        helpAddNewColumnToList(eObjects);
        assertEquals(true, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a third column to the list that exists in a different table
        // than the first two
        helpAddNewColumnToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());
    }

    public void testIsComplete3() {
        System.out.println("TestForeignKeyAssociationDescriptor.testIsComplete3()"); //$NON-NLS-1$

        // Create a selection list containing a primary key
        List eObjects = new ArrayList();
        helpAddNewPkToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a foreign key associated with a different table to the list
        helpAddNewFkToList(eObjects);
        assertEquals(true, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a third column to the list that exists in a different table
        // than the first two
        helpAddNewColumnToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());
    }

    public void testIsComplete4() {
        System.out.println("TestForeignKeyAssociationDescriptor.testIsComplete4()"); //$NON-NLS-1$

        // Create a selection list containing a primary key
        List eObjects = new ArrayList();
        helpAddNewPkToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a foreign key associated with a different table to the list
        helpAddNewPkToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Create a selection list containing a primary key
        eObjects = new ArrayList();
        helpAddNewFkToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());

        // Add a foreign key associated with a different table to the list
        helpAddNewFkToList(eObjects);
        assertEquals(false, new ForeignKeyAssociationDescriptor(eObjects).isComplete());
    }

    public void testExecute1() {
        System.out.println("TestForeignKeyAssociationDescriptor.testExecute1()"); //$NON-NLS-1$

        // Create a selection list containing only two tables
        List eObjects = new ArrayList();
        helpAddNewTableToList(eObjects);
        helpAddNewTableToList(eObjects);
        AbstractAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(eObjects);
        try {
            EObject result = descriptor.create();
            assertNotNull(result);
            assertTrue(result instanceof ForeignKey);

            BaseTable tableA = (BaseTable)eObjects.get(0);
            BaseTable tableB = (BaseTable)eObjects.get(1);
            PrimaryKey pkA = tableA.getPrimaryKey();
            ForeignKey fkB = (ForeignKey)tableB.getForeignKeys().get(0);
            assertNotNull(pkA);
            assertNotNull(fkB);
            assertEquals(pkA, fkB.getUniqueKey());
        } catch (ModelerCoreException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testExecute2() {
        System.out.println("TestForeignKeyAssociationDescriptor.testExecute2()"); //$NON-NLS-1$

        // Create a selection list containing only two columns
        List eObjects = new ArrayList();
        helpAddNewColumnToList(eObjects);
        helpAddNewColumnToList(eObjects);
        AbstractAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(eObjects);
        try {
            EObject result = descriptor.create();
            assertNotNull(result);
            assertTrue(result instanceof ForeignKey);

            BaseTable tableA = (BaseTable)((Column)eObjects.get(0)).eContainer();
            BaseTable tableB = (BaseTable)((Column)eObjects.get(1)).eContainer();
            PrimaryKey pkA = tableA.getPrimaryKey();
            ForeignKey fkB = (ForeignKey)tableB.getForeignKeys().get(0);
            assertNotNull(pkA);
            assertNotNull(fkB);
            assertEquals(pkA, fkB.getUniqueKey());
        } catch (ModelerCoreException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testExecute3() {
        System.out.println("TestForeignKeyAssociationDescriptor.testExecute3()"); //$NON-NLS-1$

        // Create a selection list containing a PK and FK
        List eObjects = new ArrayList();
        helpAddNewPkToList(eObjects);
        helpAddNewFkToList(eObjects);
        AbstractAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(eObjects);
        try {
            EObject result = descriptor.create();
            assertNotNull(result);
            assertTrue(result instanceof ForeignKey);

            BaseTable tableA = (BaseTable)((PrimaryKey)eObjects.get(0)).eContainer();
            BaseTable tableB = (BaseTable)((ForeignKey)eObjects.get(1)).eContainer();
            PrimaryKey pkA = tableA.getPrimaryKey();
            ForeignKey fkB = (ForeignKey)tableB.getForeignKeys().get(0);
            assertNotNull(pkA);
            assertNotNull(fkB);
            assertEquals(pkA, fkB.getUniqueKey());
        } catch (ModelerCoreException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testExecute4() {
        System.out.println("TestForeignKeyAssociationDescriptor.testExecute4()"); //$NON-NLS-1$

        // Create a selection list containing a FK and PK - reverse order from above test
        List eObjects = new ArrayList();
        helpAddNewFkToList(eObjects);
        helpAddNewPkToList(eObjects);
        AbstractAssociationDescriptor descriptor = new ForeignKeyAssociationDescriptor(eObjects);
        try {
            EObject result = descriptor.create();
            assertNotNull(result);
            assertTrue(result instanceof ForeignKey);

            BaseTable tableB = (BaseTable)((ForeignKey)eObjects.get(0)).eContainer();
            BaseTable tableA = (BaseTable)((PrimaryKey)eObjects.get(1)).eContainer();
            PrimaryKey pkA = tableA.getPrimaryKey();
            ForeignKey fkB = (ForeignKey)tableB.getForeignKeys().get(0);
            assertNotNull(pkA);
            assertNotNull(fkB);
            assertEquals(pkA, fkB.getUniqueKey());
        } catch (ModelerCoreException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
