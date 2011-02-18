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
import org.eclipse.core.runtime.Path;

/**
 * TestJdbcNodeCache
 */
public class TestJdbcNodeCache extends TestCase {

    private JdbcNodeCache cache;

    /**
     * Constructor for TestJdbcNodeCache.
     * 
     * @param name
     */
    public TestJdbcNodeCache( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.cache = new JdbcNodeCache();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.cache = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestJdbcNodeCache"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcNodeCache.class);
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
        new JdbcNodeCache();
    }

    public void testGetWithPathNotInCache() {
        assertNull(this.cache.get(new Path("Some path not in cache"))); //$NON-NLS-1$
    }

}
