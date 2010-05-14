/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.data;

import java.sql.Connection;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.modeler.jdbc.FakeConnection;

/**
 * TestQueryRequest
 */
public class TestQueryRequest extends TestCase {

    private static final String REQUEST_NAME = "QueryRequest 1"; //$NON-NLS-1$
    private static final String DEFAULT_SQL = "SELECT COUNT(*) FROM SOMETHING"; //$NON-NLS-1$

    private Connection conn;
    private QueryRequest request;

    /**
     * Constructor for TestQueryRequest.
     * @param name
     */
    public TestQueryRequest(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.conn = new FakeConnection();
        this.request = new QueryRequest(REQUEST_NAME,this.conn,DEFAULT_SQL);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.request = null;
        this.conn = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestQueryRequest"); //$NON-NLS-1$
        suite.addTestSuite(TestQueryRequest.class);
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
        assertNotNull( new QueryRequest(REQUEST_NAME,this.conn,DEFAULT_SQL));
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

}
