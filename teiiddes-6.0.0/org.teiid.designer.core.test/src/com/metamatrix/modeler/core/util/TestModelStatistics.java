/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EClass;

import com.metamatrix.metamodels.core.CorePackage;

/**
 * TestModelStatistics
 */
public class TestModelStatistics extends TestCase {

    private ModelStatistics stats1;
    private ModelStatistics stats1Copy;
    private ModelStatistics stats2;
    private ModelStatistics emptyStats;

    /**
     * Constructor for TestModelStatistics.
     * @param name
     */
    public TestModelStatistics(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.stats1 = new ModelStatistics();
        this.stats1Copy = new ModelStatistics();
        this.stats2 = new ModelStatistics();
        this.emptyStats = new ModelStatistics();

        this.stats1.add(CorePackage.eINSTANCE.getModelAnnotation(),2);
        this.stats1.add(CorePackage.eINSTANCE.getModelImport(),2);
        this.stats1.add(CorePackage.eINSTANCE.getAnnotation(),3);

        this.stats1Copy.add(CorePackage.eINSTANCE.getModelAnnotation(),2);
        this.stats1Copy.add(CorePackage.eINSTANCE.getModelImport(),2);
        this.stats1Copy.add(CorePackage.eINSTANCE.getAnnotation(),3);

        this.stats2.add(CorePackage.eINSTANCE.getModelAnnotation(),0);
        this.stats2.add(CorePackage.eINSTANCE.getAnnotation(),3);
        this.stats2.add(CorePackage.eINSTANCE.getLinkContainer(),5);
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
        TestSuite suite = new TestSuite("TestModelStatistics"); //$NON-NLS-1$
        suite.addTestSuite(TestModelStatistics.class);
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
    //                      H E L P E R   M E T H O D S
    // =========================================================================

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    //public void testDataPathSupplied() {
    //    UnitTestUtil.assertTestDataPathSet();
    //}

    public void testSetup() {
        assertNotNull(this.stats1);
        assertNotNull(this.stats2);
        assertNotNull(this.emptyStats);
    }

    public void testGetDescription() {
        assertNull(this.stats1.getDescription());
        final String newDesc = "This is the new description"; //$NON-NLS-1$
        this.stats1.setDescription(newDesc);
        assertSame(newDesc, this.stats1.getDescription());
        final String newDesc2 = ""; //$NON-NLS-1$
        this.stats1.setDescription(newDesc2);
        assertSame(newDesc2, this.stats1.getDescription());
        this.stats1.setDescription(null);
        assertNull(this.stats1.getDescription());
    }

    public void testGetEClasses() {
        assertNotNull(this.emptyStats.getEClasses());
        assertEquals(0, this.emptyStats.getEClasses().size());
    }

    public void testGetCount() {
        final EClass metaclass = CorePackage.eINSTANCE.getAnnotation();
        assertEquals(0, this.emptyStats.getCount(metaclass));
    }

    public void testGetResourceCount() {
        assertEquals(0, this.emptyStats.getResourceCount());
    }

    public void testSetWithNullMetaclass() {
        try {
            this.emptyStats.set(null,4);
            fail("Should have caught null metaclass reference"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testAddWithNullMetaclass() {
        try {
            this.emptyStats.add(null,4);
            fail("Should have caught null metaclass reference"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testSet() {
        final EClass metaclass = CorePackage.eINSTANCE.getAnnotation();
        assertEquals(0, this.emptyStats.getCount(metaclass));
        this.emptyStats.set(metaclass,5);
        assertEquals(5, this.emptyStats.getCount(metaclass));
        this.emptyStats.set(metaclass,10);
        assertEquals(10, this.emptyStats.getCount(metaclass));
        this.emptyStats.set(metaclass,0);
        assertEquals(0, this.emptyStats.getCount(metaclass));
    }

    public void testAdd() {
        final EClass metaclass = CorePackage.eINSTANCE.getAnnotation();
        assertEquals(0, this.emptyStats.getCount(metaclass));
        this.emptyStats.add(metaclass,5);
        assertEquals(5, this.emptyStats.getCount(metaclass));
        this.emptyStats.add(metaclass,10);
        assertEquals(15, this.emptyStats.getCount(metaclass));
        this.emptyStats.add(metaclass,-2);
        assertEquals(13, this.emptyStats.getCount(metaclass));
    }

    public void testSetResourceCount() {
        assertEquals(0, this.emptyStats.getResourceCount());
        this.emptyStats.setResourceCount(5);
        assertEquals(5, this.emptyStats.getResourceCount());
        this.emptyStats.setResourceCount(10);
        assertEquals(10, this.emptyStats.getResourceCount());
        this.emptyStats.setResourceCount(0);
        assertEquals(0, this.emptyStats.getResourceCount());
    }

    public void testAddResourceCount() {
        assertEquals(0, this.emptyStats.getResourceCount());
        this.emptyStats.addResourceCount(5);
        assertEquals(5, this.emptyStats.getResourceCount());
        this.emptyStats.addResourceCount(10);
        assertEquals(15, this.emptyStats.getResourceCount());
        this.emptyStats.addResourceCount(0);
        assertEquals(15, this.emptyStats.getResourceCount());
        this.emptyStats.addResourceCount(-2);
        assertEquals(13, this.emptyStats.getResourceCount());
    }

    public void testClear() {
    }

    public void testCompare() {
        final IStatus stats1_stats1Copy = this.stats1.compare(this.stats1Copy);
        assertEquals(true, stats1_stats1Copy.isOK());
		assertEquals(true, stats1_stats1Copy instanceof Status);

        final IStatus stats1Copy_stats1 = this.stats1Copy.compare(this.stats1);
        assertEquals(true, stats1Copy_stats1.isOK());
		assertEquals(true, stats1Copy_stats1 instanceof Status);

        // stats2 has 3 diffs:
        //   - extra LinkContainer (5 vs none)
        //   - different ModelAnnotation (0 vs 2)
        //   - missing ModelImport (none vs 2)
        final IStatus stats1_stats2 = this.stats1.compare(this.stats2);
        assertEquals(false, stats1_stats2.isOK());
		assertEquals(IStatus.ERROR, stats1_stats2.getSeverity());
		assertEquals(3, stats1_stats2.getChildren().length);
		assertEquals(true, stats1_stats2 instanceof MultiStatus);
    }
}
