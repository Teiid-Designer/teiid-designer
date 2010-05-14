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

/**
 * TestModelStatisticsVisitor
 */
public class TestModelStatisticsVisitor extends TestCase {

    protected static ModelStatisticsVisitor visitor;

    /**
     * Constructor for TestModelStatisticsVisitor.
     * @param name
     */
    public TestModelStatisticsVisitor(String name) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelStatisticsVisitor"); //$NON-NLS-1$
        suite.addTestSuite(TestModelStatisticsVisitor.class);
        // One-time setup and teardown
        return new TestSetup(suite) {
            @Override
            public void setUp() {
            	visitor = new ModelStatisticsVisitor();
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

//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }

    public void testSetup() {
        assertNotNull(visitor);
    }
    
    public void testGetModelStatistics() {
        final ModelStatistics stats = visitor.getModelStatistics();
        assertNotNull(stats);
    }

}
