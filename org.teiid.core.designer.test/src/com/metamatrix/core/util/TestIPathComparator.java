/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.core.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * TestIPathComparator
 */
public class TestIPathComparator extends TestCase {

    private IPath path1;
    private IPath path2;
    private IPath path3;
    private IPath path4;
    private IPath path5;
    private IPath path6;
    private IPath path7;
    private IPathComparator comparator;

    /**
     * Constructor for TestIPathComparator.
     * 
     * @param name
     */
    public TestIPathComparator( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comparator = new IPathComparator();
        path1 = new Path("/alpha/beta/gamma/delta"); //$NON-NLS-1$
        path2 = new Path("/alpha/beta/gamma/delta"); //$NON-NLS-1$
        path3 = new Path("/alpha/beta/gamma"); //$NON-NLS-1$
        path4 = new Path("/alpha/beta/Gamma"); //$NON-NLS-1$
        path5 = new Path("/alpha/beta"); //$NON-NLS-1$
        path6 = new Path("/"); //$NON-NLS-1$
        path7 = new Path("/alpha0/beta"); //$NON-NLS-1$
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        path1 = null;
        path2 = null;
        path3 = null;
        path4 = null;
        path5 = null;
        path6 = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestIPathComparator"); //$NON-NLS-1$
        suite.addTestSuite(TestIPathComparator.class);
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

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testCompareToWithSameReference() {
        assertEquals(0, comparator.compare(path1, path1));
    }

    public void testCompareToWithNullFirstParameter() {
        assertEquals(-1, comparator.compare(null, path1));
    }

    public void testCompareToWithNullSecondParameter() {
        assertEquals(1, comparator.compare(path1, null));
    }

    public void testCompareToWithWrongTypeFirstParameter() {
        try {
            comparator.compare("this is some string", path1); //$NON-NLS-1$
            fail("Did not catch null parameter"); //$NON-NLS-1$
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testCompareToWithWrongTypeSecondParameter() {
        try {
            comparator.compare("this is some string", path1); //$NON-NLS-1$
            fail("Did not catch null parameter"); //$NON-NLS-1$
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testCompareToWithNullFirstParameterAndWrongTypeSecondParameter() {
        try {
            comparator.compare(null, "this is some string"); //$NON-NLS-1$
            fail("Did not catch null parameter"); //$NON-NLS-1$
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testCompareToWithWrongTypeFirstParameterAndNullSecondParameter() {
        try {
            comparator.compare("this is some string", null); //$NON-NLS-1$
            fail("Did not catch null parameter"); //$NON-NLS-1$
        } catch (ClassCastException e) {
            // expected
        }
    }

    public void testCompareTo1() {
        assertEquals(0, comparator.compare(path1, path2));
        assertEquals(0, comparator.compare(path2, path2));
    }

    public void testCompareTo2() {
        assertTrue(comparator.compare(path1, path3) > 0);
        assertTrue(comparator.compare(path3, path1) < 0);
    }

    public void testCompareTo3() {
        assertTrue(comparator.compare(path3, path4) > 0);
        assertTrue(comparator.compare(path4, path3) < 0);
    }

    public void testCompareTo4() {
        assertTrue(comparator.compare(path1, path5) > 0);
        assertTrue(comparator.compare(path5, path1) < 0);
    }

    public void testCompareTo5() {
        assertTrue(comparator.compare(path5, path6) > 0);
        assertTrue(comparator.compare(path6, path5) < 0);
    }

    public void testCompareTo6() {
        assertTrue(comparator.compare(path5, path7) < 0);
        assertTrue(comparator.compare(path7, path5) > 0);
    }

}
