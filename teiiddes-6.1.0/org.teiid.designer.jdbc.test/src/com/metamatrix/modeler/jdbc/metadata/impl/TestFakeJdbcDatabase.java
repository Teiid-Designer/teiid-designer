/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestFakeJdbcDatabase
 */
public class TestFakeJdbcDatabase extends TestCase {

    /**
     * Constructor for TestFakeJdbcDatabase.
     * 
     * @param name
     */
    public TestFakeJdbcDatabase( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
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
        TestSuite suite = new TestSuite("TestFakeJdbcDatabase"); //$NON-NLS-1$
        suite.addTestSuite(TestFakeJdbcDatabase.class);
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

    public void testConstructor() {
        new FakeJdbcDatabase("some name"); //$NON-NLS-1$
    }

}
