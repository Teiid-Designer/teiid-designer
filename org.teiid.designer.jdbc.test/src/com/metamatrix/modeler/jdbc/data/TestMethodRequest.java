/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.core.runtime.IStatus;

/**
 * TestMethodRequest
 */
public class TestMethodRequest extends TestCase {

    private static final String REQUEST_NAME = "MethodRequest 1"; //$NON-NLS-1$
    private static final String TARGET_OBJECT = new String("this is the test string"); //$NON-NLS-1$
    private static final String METHOD_NAME = "length"; //$NON-NLS-1$
    private static final Object[] METHOD_PARAMS = new Object[]{};

    private static final String FAIL_REQUEST_NAME = "MethodRequest that should fail"; //$NON-NLS-1$
    private static final String FAIL_TARGET_OBJECT = new String("this is the failure test string"); //$NON-NLS-1$
    private static final String FAIL_METHOD_NAME = "charAt"; //$NON-NLS-1$
    private static final Object[] FAIL_METHOD_PARAMS = new Object[]{new Integer(FAIL_TARGET_OBJECT.length() + 4)};
    private MethodRequest request;
    private MethodRequest failRequest;

    /**
     * Constructor for TestMethodRequest.
     * @param name
     */
    public TestMethodRequest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.request = new MethodRequest(REQUEST_NAME,TARGET_OBJECT,METHOD_NAME,METHOD_PARAMS);
        this.failRequest = new MethodRequest(FAIL_REQUEST_NAME,FAIL_TARGET_OBJECT,FAIL_METHOD_NAME,FAIL_METHOD_PARAMS);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.request = null;
        this.failRequest = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestMethodRequest"); //$NON-NLS-1$
        suite.addTestSuite(TestMethodRequest.class);
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

    public void testDefaultConstructor() {
        assertNotNull( new MethodRequest(REQUEST_NAME,TARGET_OBJECT,METHOD_NAME,METHOD_PARAMS) );
    }

    public void testConstructorWithNullName() {
        try {
            new MethodRequest(null,TARGET_OBJECT,METHOD_NAME,METHOD_PARAMS);
            fail("Missed catching null name"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithZeroLengthName() {
        try {
            new MethodRequest("",TARGET_OBJECT,METHOD_NAME,METHOD_PARAMS); //$NON-NLS-1$
            fail("Missed catching zero-length name"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithNullTarget() {
        try {
            new MethodRequest(REQUEST_NAME,null,METHOD_NAME,METHOD_PARAMS);
            fail("Missed catching null target"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithNullMethodName() {
        try {
            new MethodRequest(REQUEST_NAME,TARGET_OBJECT,null,METHOD_PARAMS);
            fail("Missed catching null method name"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithZeroLengthMethodName() {
        try {
            new MethodRequest(REQUEST_NAME,TARGET_OBJECT,"",METHOD_PARAMS); //$NON-NLS-1$
            fail("Missed catching zero-length method name"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithNullParams() {
        try {
            new MethodRequest(REQUEST_NAME,TARGET_OBJECT,METHOD_NAME,null);
            fail("Missed catching null parameters"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    //-------------------------------------------------------------------------
    //                   Test Request methods
    //-------------------------------------------------------------------------

    public void testGetName() {
        final String name = request.getName();
        assertTrue(REQUEST_NAME == name);
    }

    public void testToString() {
        assertTrue(request.toString() == request.getName());
    }

    public void testHasResults() {
        assertTrue(!request.hasResponse());
    }

    public void testGetResults() {
        assertTrue(request.getResponse() == null);
    }

    public void testHasProblems() {
        assertTrue(!request.hasProblems());
    }

    public void testGetProblems() {
        assertTrue(request.getProblems() == null);
    }

    public void testClear() {
        assertTrue(!request.hasResponse());
		assertTrue(request.getResponse() == null);
		assertTrue(!request.hasProblems());
		assertTrue(request.getProblems() == null);
    }

    public void testInvokeWithSuccess() {
        final Request r = this.request;
        final boolean success = r.invoke();    // should succeed
        assertTrue(success);
        final IStatus status = r.getProblems();
        assertNull(status);
        assertTrue(r.hasResponse());
		assertTrue(r.getResponse() != null);
		assertTrue(!r.hasProblems());
		assertTrue(r.getProblems() == null);

        r.clear();
        assertTrue(!r.hasResponse());
		assertTrue(r.getResponse() == null);
		assertTrue(!r.hasProblems());
		assertTrue(r.getProblems() == null);
    }

    public void testInvokeWithFailure() {
        final Request r = this.failRequest;
        final boolean success = r.invoke();    // should succeed
        assertTrue(!success);
        final IStatus status = r.getProblems();
        assertNotNull(status);
        assertTrue(r.hasResponse());
		assertTrue(r.getResponse() != null);
		assertTrue(r.hasProblems());
		assertTrue(r.getProblems() != null);

        r.clear();
        assertTrue(!r.hasResponse());
		assertTrue(r.getResponse() == null);
		assertTrue(!r.hasProblems());
		assertTrue(r.getProblems() == null);
    }


}
