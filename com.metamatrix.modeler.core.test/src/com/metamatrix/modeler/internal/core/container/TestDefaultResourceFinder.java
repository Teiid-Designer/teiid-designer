/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.container;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestDefaultResourceFinder
 */
public class TestDefaultResourceFinder extends TestCase {
    
    /**
     * Constructor for TestResourceDescriptorImpl.
     * @param name
     */
    public TestDefaultResourceFinder(String name) {
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
        TestSuite suite = new TestSuite("TestDefaultResourceFinder"); //$NON-NLS-1$
        suite.addTestSuite(TestDefaultResourceFinder.class);
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
    
    public void testFindResourceByName() {
    }
}

