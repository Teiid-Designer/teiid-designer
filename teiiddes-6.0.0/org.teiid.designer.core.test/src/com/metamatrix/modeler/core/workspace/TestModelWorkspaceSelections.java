/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.workspace;

import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * TestModelWorkspaceSelections
 */
public class TestModelWorkspaceSelections extends TestCase {

    private static final String OBJECT_CONSIDERED_SELECTABLE_BY_FILTER1 = "This is some object1"; //$NON-NLS-1$
    private static final String OBJECT_CONSIDERED_SELECTABLE_BY_FILTER2 = "This is some object2"; //$NON-NLS-1$
    public static final ModelWorkspaceSelectionFilter TEST_FILTER1 = new ModelWorkspaceSelectionFilter() {
        public boolean isSelectable( final Object element ) {
            return element == OBJECT_CONSIDERED_SELECTABLE_BY_FILTER1;
        }
    };
    public static final ModelWorkspaceSelectionFilter TEST_FILTER2 = new ModelWorkspaceSelectionFilter() {
        public boolean isSelectable( final Object element ) {
            return element == OBJECT_CONSIDERED_SELECTABLE_BY_FILTER2;
        }
    };

    private ModelWorkspaceSelections selections;
    private IPath path1;
    private IPath path2;
    private IPath path3;
    private IPath path4;
    private IPath path5;

    /**
     * Constructor for TestModelWorkspaceSelections.
     * 
     * @param name
     */
    public TestModelWorkspaceSelections( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.selections = new ModelWorkspaceSelections();
        this.path1 = new Path("/a/b"); //$NON-NLS-1$
        this.path2 = new Path("/a/b/c"); //$NON-NLS-1$
        this.path3 = new Path("/a/b/c/d"); //$NON-NLS-1$
        this.path4 = new Path("/a/b/c/e"); //$NON-NLS-1$
        this.path5 = new Path("/a/b/x"); //$NON-NLS-1$
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.selections = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelWorkspaceSelections"); //$NON-NLS-1$
        suite.addTestSuite(TestModelWorkspaceSelections.class);
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

    public void testModelWorkspaceSelections() {
        new ModelWorkspaceSelections();
    }

    public void testGetModelWorkspaceSelectionFilters() {
        final List filters = this.selections.getModelWorkspaceSelectionFilters();
        assertNotNull(filters);
        assertEquals(0, filters.size());

        filters.add(TEST_FILTER1);
        filters.add(TEST_FILTER2);

        final List filters2 = this.selections.getModelWorkspaceSelectionFilters();
        assertNotNull(filters2);
        assertEquals(2, filters2.size());

        this.selections.getModelWorkspaceSelectionFilters().clear();
        assertNotNull(filters);
        assertEquals(0, filters.size());
    }

    public void testIsSelectable() {
        assertEquals(false, this.selections.isSelectable(OBJECT_CONSIDERED_SELECTABLE_BY_FILTER1));
        assertEquals(false, this.selections.isSelectable(OBJECT_CONSIDERED_SELECTABLE_BY_FILTER2));

        final List filters = this.selections.getModelWorkspaceSelectionFilters();
        filters.add(TEST_FILTER1);

        assertEquals(true, this.selections.isSelectable(OBJECT_CONSIDERED_SELECTABLE_BY_FILTER1));
        assertEquals(false, this.selections.isSelectable(OBJECT_CONSIDERED_SELECTABLE_BY_FILTER2));

        filters.add(TEST_FILTER2);

        assertEquals(true, this.selections.isSelectable(OBJECT_CONSIDERED_SELECTABLE_BY_FILTER1));
        assertEquals(true, this.selections.isSelectable(OBJECT_CONSIDERED_SELECTABLE_BY_FILTER2));
    }

    public void testHasSelectionModes() {
        assertEquals(false, this.selections.hasSelectionModes());

        // Mark one path as selectable ...
        this.selections.setSelected(this.path1, ModelWorkspaceSelections.SELECTED);
        assertEquals(true, this.selections.hasSelectionModes());
    }

    public void testGetSelectionModeIPathWithNothingSelected() {
        // Nothing is selected!
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path1));
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path2));
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path3));
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path4));
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path5));
    }

    public void testGetSelectionModeIPathAfterSelectingRootPath() {
        this.selections.setSelected(this.path1, ModelWorkspaceSelections.SELECTED);

        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path1));
        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path2));
        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path3));
        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path4));
        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path5));
    }

    public void testGetSelectionModeIPathAfterSelectingNonRootPath() {
        this.selections.setSelected(this.path2, ModelWorkspaceSelections.SELECTED);

        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path2));
        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path3));
        assertEquals(ModelWorkspaceSelections.SELECTED, this.selections.getSelectionMode(this.path4));
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path5));
        assertEquals(ModelWorkspaceSelections.UNSELECTED, this.selections.getSelectionMode(this.path1));
    }

    public void testSetSelectedWithoutViewReference() throws Exception {
        this.selections.setSelected("Some object", 0); //$NON-NLS-1$
    }

    public void testGetSelectionModeWithoutViewReference() {
        try {
            this.selections.getSelectionMode("Some object"); //$NON-NLS-1$
            fail("Missed calling without view reference"); //$NON-NLS-1$
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testToString() {
        this.selections.setSelected(this.path2, ModelWorkspaceSelections.SELECTED);
        System.out.println(this.selections.toString());
    }

}
