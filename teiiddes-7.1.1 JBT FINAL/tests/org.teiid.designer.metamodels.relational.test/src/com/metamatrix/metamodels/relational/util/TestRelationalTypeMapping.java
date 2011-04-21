/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relational.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.modeler.core.types.DatatypeManager;
import com.metamatrix.modeler.core.types.FakeDatatypeManager;

/**
 * TestRelationalTypeMapping
 */
public class TestRelationalTypeMapping extends TestCase {

    /**
     * Constructor for TestRelationalTypeMapping.
     * @param name
     */
    public TestRelationalTypeMapping(String name) {
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
        TestSuite suite = new TestSuite("TestRelationalTypeMapping"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationalTypeMapping.class);
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
    
    public void testInstantiatingWithFakeDatatypeManager() {
        final DatatypeManager fakeDTMgr = new FakeDatatypeManager();
        final RelationalTypeMapping mapping = new RelationalTypeMappingImpl(fakeDTMgr);
        assertNotNull(mapping);
    }
    
}
