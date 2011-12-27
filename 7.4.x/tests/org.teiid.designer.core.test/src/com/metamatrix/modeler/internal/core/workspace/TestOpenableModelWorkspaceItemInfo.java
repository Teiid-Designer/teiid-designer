/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestModelWorkspaceItemInfo
 */
public class TestOpenableModelWorkspaceItemInfo extends TestCase {

    // private OpenableModelWorkspaceItemInfo info;

    /**
     * Constructor for TestModelWorkspaceItemInfo.
     * 
     * @param name
     */
    public TestOpenableModelWorkspaceItemInfo( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // this.info = new OpenableModelWorkspaceItemInfo();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // this.info = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestOpenableModelWorkspaceItemInfo"); //$NON-NLS-1$
        suite.addTestSuite(TestOpenableModelWorkspaceItemInfo.class);
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

    public void testOpenableModelWorkspaceItemInfo() {
        new OpenableModelWorkspaceItemInfo();
    }

    public void testAddChild() {
    }

    public void testGetChildren() {
    }

    public void testGrowAndAddToArray() {
    }

    public void testIncludesChild() {
    }

    public void testIsStructureKnown() {
    }

    public void testRemoveAndShrinkArray() {
    }

    public void testRemoveChild() {
    }

    public void testSetChildren() {
    }

    public void testSetIsStructureKnown() {
    }

}
