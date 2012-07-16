/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package org.teiid.designer.ui.common.eventsupport;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.teiid.designer.ui.common.testsupport.FakeEObject;

/**
 * TestSelectionUtilities
 */
public final class TestSelectionUtilities extends TestCase {

    private static final ISelection emptySelection;
    private static final ISelection singleSelection;
    private static final ISelection singleEObjectSelection;
    private static final ISelection multiSelection;

    private static final IStructuredSelection allEObjects;
    private static final IStructuredSelection allNonEObjects;
    private static final IStructuredSelection mixedObjects;

    private static final int NUM_EOBJECTS;

    public static void main( String[] theArgs ) {
        TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestSelectionUtilities.class);
    }

    static {
        emptySelection = new StructuredSelection();

        singleSelection = new StructuredSelection(new Object());

        singleEObjectSelection = new StructuredSelection(new FakeEObject());

        multiSelection = new StructuredSelection(new Object[] {new Object(), new Object()});

        allEObjects = new StructuredSelection(new Object[] {new FakeEObject(), new FakeEObject()});

        mixedObjects = new StructuredSelection(new Object[] {new FakeEObject(), new Object(), new FakeEObject(), new Object()});

        NUM_EOBJECTS = 2; // must match the number of EObjects in the mixedObjects list

        allNonEObjects = new StructuredSelection(new Object[] {new Object(), new Object(), new Object(), new Object(),
            new Object()});
    }

    /**
     * Constructor for TestSelectionUtilities.
     * 
     * @param name
     */
    public TestSelectionUtilities( String theName ) {
        super(theName);
    }

    public void testGetSelectedEObject_1() {
        assertNull("Null selection should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedEObject(null));
    }

    public void testGetSelectedEObject_2() {
        assertNull("Empty-selection should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedEObject(emptySelection));
    }

    public void testGetSelectedEObject_3() {
        assertNull("Single-selection non-EObject should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedEObject(singleSelection));
    }

    public void testGetSelectedEObject_4() {
        assertNotNull("Single-selection EObject returned null", //$NON-NLS-1$
                      SelectionUtilities.getSelectedEObject(singleEObjectSelection));
    }

    public void testGetSelectedEObject_5() {
        assertNull("Multi-selection of mixed objects should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedEObject(mixedObjects));
    }

    public void testGetSelectedEObject_6() {
        assertNull("Multi-selection of all EObjects should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedEObject(allEObjects));
    }

    public void testGetSelectedEObject_7() {
        assertNull("Multi-selection of all non-EObjects should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedEObject(allNonEObjects));
    }

    public void testGetSelectedEObjects_1() {
        List results = SelectionUtilities.getSelectedEObjects(null);

        if (results == null) {
            fail("Null selection should not return a null list"); //$NON-NLS-1$
        } else {
            assertTrue("Null selection should not return a non-empty list", results.isEmpty()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedEObjects_2() {
        List results = SelectionUtilities.getSelectedEObjects(emptySelection);

        if (results == null) {
            fail("Empty-selection should not return a null list"); //$NON-NLS-1$
        } else {
            assertTrue("Empty-selection should not return a non-empty list", results.isEmpty()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedEObjects_3() {
        List results = SelectionUtilities.getSelectedEObjects(singleSelection);

        if (results == null) {
            fail("Single-selection non-EObject should not return a null list"); //$NON-NLS-1$
        } else {
            assertTrue("Single-selection non-EObject should return an empty list", results.isEmpty()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedEObjects_4() {
        List results = SelectionUtilities.getSelectedEObjects(singleEObjectSelection);

        if (results == null) {
            fail("Single-selection EObject should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Single-selection EObject list is wrong size", 1, results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedEObjects_5() {
        List results = SelectionUtilities.getSelectedEObjects(mixedObjects);

        if (results == null) {
            fail("Multi-selection mixed objects should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Multi-selection mixed objects list is wrong size", NUM_EOBJECTS, results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedEObjects_6() {
        List results = SelectionUtilities.getSelectedEObjects(allEObjects);

        if (results == null) {
            fail("Multi-selection EObjects should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Multi-selection EObjects list is wrong size", allEObjects.size(), results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedEObjects_7() {
        List results = SelectionUtilities.getSelectedEObjects(allNonEObjects);

        if (results == null) {
            fail("Multi-selection non-EObjects should not return a null list"); //$NON-NLS-1$
        } else {
            assertTrue("Multi-selection non-EObjects should return an empty list", results.isEmpty()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObject_1() {
        assertNull("Null selection should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedObject(null));
    }

    public void testGetSelectedObject_2() {
        assertNull("Empty-selection should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedObject(emptySelection));
    }

    public void testGetSelectedObject_3() {
        assertNotNull("Single-selection non-EObject should return a value", //$NON-NLS-1$
                      SelectionUtilities.getSelectedObject(singleSelection));
    }

    public void testGetSelectedObject_4() {
        assertNotNull("Single-selection EObject should return a value", //$NON-NLS-1$
                      SelectionUtilities.getSelectedObject(singleEObjectSelection));
    }

    public void testGetSelectedObject_5() {
        assertNull("Multi-selection of mixed objects should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedObject(mixedObjects));
    }

    public void testGetSelectedObject_6() {
        assertNull("Multi-selection of all EObjects should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedObject(allEObjects));
    }

    public void testGetSelectedObject_7() {
        assertNull("Multi-selection of all non-EObjects should not return a value", //$NON-NLS-1$
                   SelectionUtilities.getSelectedObject(allNonEObjects));
    }

    public void testGetSelectedObjects_1() {
        List results = SelectionUtilities.getSelectedObjects(null);

        if (results == null) {
            fail("Null selection should not return a null list"); //$NON-NLS-1$
        } else {
            assertTrue("Null selection should not return a non-empty list", results.isEmpty()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObjects_2() {
        List results = SelectionUtilities.getSelectedObjects(emptySelection);

        if (results == null) {
            fail("Empty-selection should not return a null list"); //$NON-NLS-1$
        } else {
            assertTrue("Empty-selection should not return a non-empty list", results.isEmpty()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObjects_3() {
        List results = SelectionUtilities.getSelectedObjects(singleSelection);

        if (results == null) {
            fail("Single-selection non-EObject should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Single-selection non-EObject list is wrong size", 1, results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObjects_4() {
        List results = SelectionUtilities.getSelectedObjects(singleEObjectSelection);

        if (results == null) {
            fail("Single-selection EObject should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Single-selection EObject list is wrong size", 1, results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObjects_5() {
        List results = SelectionUtilities.getSelectedObjects(mixedObjects);

        if (results == null) {
            fail("Multi-selection mixed objects should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Multi-selection mixed objects list is wrong size", mixedObjects.size(), results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObjects_6() {
        List results = SelectionUtilities.getSelectedObjects(allEObjects);

        if (results == null) {
            fail("Multi-selection EObjects should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Multi-selection EObjects list is wrong size", allEObjects.size(), results.size()); //$NON-NLS-1$
        }
    }

    public void testGetSelectedObjects_7() {
        List results = SelectionUtilities.getSelectedObjects(allNonEObjects);

        if (results == null) {
            fail("Multi-selection non-EObjects should not return a null list"); //$NON-NLS-1$
        } else {
            assertEquals("Multi-selection non-EObjects list is wrong size", allNonEObjects.size(), results.size()); //$NON-NLS-1$
        }
    }

    public void testIsAllEObjects_1() {
        assertFalse("Null should not indicate that all EObjects were selected", //$NON-NLS-1$
                    SelectionUtilities.isAllEObjects(null));
    }

    public void testIsAllEObjects_2() {
        assertFalse("Empty-selection should not indicate that all EObjects were selected", //$NON-NLS-1$
                    SelectionUtilities.isAllEObjects(emptySelection));
    }

    public void testIsAllEObjects_3() {
        assertTrue("All EObjects collection did not indicate that all EObjects were selected", //$NON-NLS-1$
                   SelectionUtilities.isAllEObjects(allEObjects));
    }

    public void testIsAllEObjects_4() {
        assertFalse("Non-EObject collection should not indicate that all EObjects were selected", //$NON-NLS-1$
                    SelectionUtilities.isAllEObjects(allNonEObjects));
    }

    public void testIsAllEObjects_5() {
        assertFalse("Mixed collection should not indicate that all EObjects were selected", //$NON-NLS-1$
                    SelectionUtilities.isAllEObjects(mixedObjects));
    }

    public void testIsMultiSelection_1() {
        assertFalse("Null should not be a multi-selection", //$NON-NLS-1$
                    SelectionUtilities.isMultiSelection(null));
    }

    public void testIsMultiSelection_2() {
        assertFalse("Empty-selection should not be a multi-selection", //$NON-NLS-1$
                    SelectionUtilities.isMultiSelection(emptySelection));
    }

    public void testIsMultiSelection_3() {
        assertFalse("Single-selection should not be a multi-selection", //$NON-NLS-1$
                    SelectionUtilities.isMultiSelection(singleSelection));
    }

    public void testIsMultiSelection_4() {
        assertTrue("Multi-selection was not be a multi-selection", //$NON-NLS-1$
                   SelectionUtilities.isMultiSelection(multiSelection));
    }

    public void testIsSingleSelection_1() {
        assertFalse("Null should not be a single-selection", //$NON-NLS-1$
                    SelectionUtilities.isSingleSelection(null));
    }

    public void testIsSingleSelection_2() {
        assertFalse("Empty-selection should not be a single-selection", //$NON-NLS-1$
                    SelectionUtilities.isSingleSelection(emptySelection));
    }

    public void testIsSingleSelection_3() {
        assertTrue("Single-selection was not be a single-selection", //$NON-NLS-1$
                   SelectionUtilities.isSingleSelection(singleSelection));
    }

    public void testIsSingleSelection_4() {
        assertFalse("Multi-selection should not be a single-selection", //$NON-NLS-1$
                    SelectionUtilities.isSingleSelection(multiSelection));
    }

    public void testGetSelectedEObjectsWithNonEObjectWithBadEqualsImplementation() {
        ISelection selection = new StructuredSelection(new Object[] {new ClassWithBadEqualsMethod(1),
            new ClassWithBadEqualsMethod(1)});
        assertEquals(0, SelectionUtilities.getSelectedEObjects(selection).size());
    }

    public void testGetSelectedEObjectsWithNonEObjectWithGoodEqualsImplementation() {
        ISelection selection = new StructuredSelection(new Object[] {new ClassWithGoodEqualsMethod(1),
            new ClassWithGoodEqualsMethod(1)});
        assertEquals(0, SelectionUtilities.getSelectedEObjects(selection).size());
    }

    public void testGetSelectedIResourceObjectsWithNonIResourceWithBadEqualsImplementation() {
        ISelection selection = new StructuredSelection(new Object[] {new ClassWithBadEqualsMethod(1),
            new ClassWithBadEqualsMethod(2)});
        assertEquals(0, SelectionUtilities.getSelectedIResourceObjects(selection).size());
    }

    public void testGetSelectedIResourceObjectsWithNonIResourceWithGoodEqualsImplementation() {
        ISelection selection = new StructuredSelection(new Object[] {new ClassWithGoodEqualsMethod(1),
            new ClassWithGoodEqualsMethod(2)});
        assertEquals(0, SelectionUtilities.getSelectedIResourceObjects(selection).size());
    }

    public class ClassWithBadEqualsMethod {
        private final int id;

        public ClassWithBadEqualsMethod( int id ) {
            this.id = id;
        }

        @Override
        public boolean equals( Object obj ) {
            // a bad implementation
            if (obj instanceof ClassWithBadEqualsMethod) {
                return false;
            }

            return super.equals(obj);
        }

        protected int getId() {
            return this.id;
        }
    }

    public class ClassWithGoodEqualsMethod extends ClassWithBadEqualsMethod {

        public ClassWithGoodEqualsMethod( int id ) {
            super(id);
        }

        @Override
        public boolean equals( Object obj ) {
            if (obj instanceof ClassWithGoodEqualsMethod) {
                return getId() == (((ClassWithGoodEqualsMethod)obj).getId());
            }

            return super.equals(obj);
        }

    }

}
