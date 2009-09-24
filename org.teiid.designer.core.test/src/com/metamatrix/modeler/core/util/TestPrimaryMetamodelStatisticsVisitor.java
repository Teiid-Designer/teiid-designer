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
 * TestPrimaryMetamodelStatisticsVisitor
 */
public class TestPrimaryMetamodelStatisticsVisitor extends TestCase {

    private ModelStatisticsVisitor visitor;

    /**
     * Constructor for TestPrimaryMetamodelStatisticsVisitor.
     * @param name
     */
    public TestPrimaryMetamodelStatisticsVisitor(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.visitor = new PrimaryMetamodelStatisticsVisitor();
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
        TestSuite suite = new TestSuite("TestPrimaryMetamodelStatisticsVisitor"); //$NON-NLS-1$
        suite.addTestSuite(TestPrimaryMetamodelStatisticsVisitor.class);
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

//    public void testDataPathSupplied() {
//        UnitTestUtil.assertTestDataPathSet();
//    }

    public void testSetup() {
        assertNotNull(this.visitor);
    }
    
    public void testGetModelStatistics() {
        final ModelStatistics stats = this.visitor.getModelStatistics();
        assertNotNull(stats);
    }

}
