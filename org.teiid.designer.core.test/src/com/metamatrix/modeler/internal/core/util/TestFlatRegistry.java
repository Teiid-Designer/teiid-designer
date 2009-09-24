/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class TestFlatRegistry extends TestCase {

    private FlatRegistry registry;

    /**
     * Constructor for TestFlatRegistry.
     * @param arg0
     */
    public TestFlatRegistry(String arg0) {
        super(arg0);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFlatRegistry.class);
    }

    /**
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.registry = new FlatRegistry();
    }

    /**
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.registry = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestFlatRegistry"); //$NON-NLS-1$
        suite.addTestSuite(TestFlatRegistry.class);
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

    public Object helpTestRegister( final Object obj, final String name, final boolean unregister ) {
        registry.register(name, obj);
        if ( registry.lookup(name) != obj ) {
            fail("Unable to find registered object (" + obj + ") with name \"" + name + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        Object registeredObj = registry.unregister(name);
        if ( registeredObj != obj ) {
            fail("Result from unregister did not match registered object (" + obj + ") with name \"" + name + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        if ( !unregister ) {
            registry.register(name, obj);
        }
        return obj;
    }

    public void helpCheckSize( final int expectedSize ) {
        if ( registry.size() != expectedSize ) {
            fail("The register has " + registry.size() + " entries; expected " + expectedSize); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testRegister() {
        helpTestRegister("Object 1","obj1",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpTestRegister("Object 2","obj2",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(2);
        helpTestRegister("Object 3","obj2",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(2);
        helpTestRegister("Object 4","obj4",true); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(2);
        helpTestRegister("Object 4","obj4",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(3);
        helpTestRegister("Object 3","obj3",false); //$NON-NLS-1$ //$NON-NLS-2$
        helpCheckSize(4);
    }

}
