/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.relationship;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;

/**
 * TestNavigationContextInfo
 */
public class TestNavigationContextInfo extends TestCase {

    private static final String URI_VALUE = "/myProject/folder A/model BCD#uuidOfObject"; //$NON-NLS-1$
    private NavigationContextInfo info;

    /**
     * Constructor for TestNavigationContextInfo.
     * 
     * @param name
     */
    public TestNavigationContextInfo( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.info = new NavigationContextInfo(URI_VALUE);
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
        TestSuite suite = new TestSuite("TestNavigationContextInfo"); //$NON-NLS-1$
        suite.addTestSuite(TestNavigationContextInfo.class);
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

    // public void testDataPathSupplied() {
    // UnitTestUtil.assertTestDataPathSet();
    // }

    public void testSetup() {
        assertNotNull(this.info);
    }

    public void testConstructorWithNullEObjectArgument() {
        try {
            new NavigationContextInfo((EObject)null, "http://some.uri"); //$NON-NLS-1$
            fail("Uncaught null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithNullUriArgument() {
        try {
            new NavigationContextInfo(EcoreFactory.eINSTANCE.createEObject(), null);
            fail("Uncaught null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithNullStringArgument() {
        try {
            new NavigationContextInfo((String)null);
            fail("Uncaught null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testHashCode() {
        final int hc = this.info.hashCode();
        final int expected = URI_VALUE.hashCode();
        assertEquals(expected, hc);
    }

    /*
     * Test for String toString()
     */
    public void testToString() {
        final String hc = this.info.toString();
        final String expected = URI_VALUE;
        assertEquals(expected, hc);
        assertSame(expected, hc);
    }

    /*
     * Test for boolean equals(Object)
     */
    public void testEqualsObject() {
        assertEquals(true, info.equals(this.info));
        assertEquals(false, info.equals(URI_VALUE));
        assertEquals(false, info.equals(null));

        final NavigationContextInfo info2 = new NavigationContextInfo(URI_VALUE);
        assertEquals(true, info2.equals(this.info));
        assertEquals(true, this.info.equals(info2));

        final NavigationContextInfo info3 = new NavigationContextInfo(URI_VALUE + "asasdf"); //$NON-NLS-1$
        assertEquals(false, info3.equals(this.info));
        assertEquals(false, this.info.equals(info3));
    }

}
