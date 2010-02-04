/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.uml2.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.container.Container;

/**
 * test
 */
public class TestPrimitiveTypeManager extends TestCase {
    // -------------------------------------------------
    // Variables initialized during one-time startup ...
    // -------------------------------------------------
    
    // ---------------------------------------
    // Variables initialized for each test ...
    // ---------------------------------------
    
    // =========================================================================
    //                        F R A M E W O R K
    // =========================================================================
    
    /**
     * Constructor for TestMetadataLoadingCache.
     * @param name
     */
    public TestPrimitiveTypeManager(String name) {
        super(name);
    }
    
    // =========================================================================
    //                        T E S T   C O N T R O L
    // =========================================================================
    
    /** 
     * Construct the test suite, which uses a one-time setup call
     * and a one-time tear-down call.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestPrimitiveTypeManager"); //$NON-NLS-1$
        suite.addTestSuite(TestPrimitiveTypeManager.class);
    
        return new TestSetup(suite) { // junit.extensions package
            // One-time setup and teardown
            @Override
            public void setUp() throws Exception {
                oneTimeSetUp();
            }
            @Override
            public void tearDown() {
                oneTimeTearDown();
            }
        };
    }
    
    // =========================================================================
    //                                 M A I N
    // =========================================================================
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
        System.exit(0);
    }
    
    // =========================================================================
    //                 S E T   U P   A N D   T E A R   D O W N
    // =========================================================================
    
    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
    }
    
    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
    }
    
    public static void oneTimeSetUp() {
    }
    
    public static void oneTimeTearDown() {
    }
    
    // =========================================================================
    //                      H E L P E R   M E T H O D S
    // =========================================================================
    
    protected ExternalResourceDescriptor helpGetExternalResourceDescriptor() {
        return PrimitiveTypeManager.getExternalResourceDescriptor();
    }
    
    protected Container helpCreateContainer(final String containerName) {
        return PrimitiveTypeManager.createContainer(containerName);
    }
    
    protected Resource helpLoadContainer(final ExternalResourceDescriptor descriptor, final Container container) {
        return PrimitiveTypeManager.loadContainer(descriptor,container);
    }
    
    // =========================================================================
    //                         T E S T   C A S E S
    // =========================================================================
    
    
    // =========================================================================
    //                   T E S T   I N N E R   C L A S S E S
    // =========================================================================

    public void testSomething() {
        // Trivial test case to keep from failing; this class is used as a base class for other test cases
    }

}
