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
import org.eclipse.emf.ecore.resource.ResourceSet;
import com.metamatrix.modeler.core.ModelerCore;
import com.metamatrix.modeler.core.container.ResourceSetFinder;

/**
 * TestDefaultContainerResultSetFinder
 */
public class TestDefaultContainerResultSetFinder extends TestCase {

    private ResourceSetFinder finder;

    /**
     * Constructor for TestDefaultContainerResultSetFinder.
     * 
     * @param name
     */
    public TestDefaultContainerResultSetFinder( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.finder = new DefaultContainerResourceSetFinder();
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
        TestSuite suite = new TestSuite("TestDefaultContainerResultSetFinder"); //$NON-NLS-1$
        suite.addTestSuite(TestDefaultContainerResultSetFinder.class);
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

    public void testDefaultContainerResultSetFinder() {
        new DefaultContainerResourceSetFinder();
    }

    public void testGetResourceSet() throws Exception {
        final ResourceSet result = finder.getResourceSet(null);
        assertSame(ModelerCore.getModelContainer(), result);
    }

}
