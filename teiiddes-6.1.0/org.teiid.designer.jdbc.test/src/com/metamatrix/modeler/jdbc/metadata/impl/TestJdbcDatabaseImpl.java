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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.metamatrix.modeler.jdbc.metadata.JdbcNode;

/**
 * TestJdbcNodeCache
 */
public class TestJdbcDatabaseImpl extends TestCase {

    private FakeJdbcDatabase root;
    private FakeJdbcNode a;
    private FakeJdbcNode b;
    private FakeJdbcNode a_a;
    private FakeJdbcNode a_b;
    private FakeJdbcNode a_b_a;
    private FakeJdbcNode a_b_a_a;
    private FakeJdbcNode a_b_b;
    private FakeJdbcNode a_c;

    /**
     * Constructor for TestJdbcNodeCache.
     * @param name
     */
    public TestJdbcDatabaseImpl(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root    = new FakeJdbcDatabase("Root Node"); //$NON-NLS-1$
        a       = new FakeJdbcNode("A",root); //$NON-NLS-1$
        b       = new FakeJdbcNode("B",root); //$NON-NLS-1$
        a_a     = new FakeJdbcNode("A.A",a); //$NON-NLS-1$
        a_b     = new FakeJdbcNode("A.B",a); //$NON-NLS-1$
        a_b_a   = new FakeJdbcNode("A.B.A",a_b); //$NON-NLS-1$
        a_b_a_a = new FakeJdbcNode("A.B.A.A",a_b_a); //$NON-NLS-1$
        a_b_b   = new FakeJdbcNode("A.B.B",a_b); //$NON-NLS-1$
        a_c     = new FakeJdbcNode("A.C",a); //$NON-NLS-1$

        root.addChild(a);
        root.addChild(b);
        a.addChild(a_a);
        a.addChild(a_b);
        a.addChild(a_c);
        a_b.addChild(a_b_a);
        a_b.addChild(a_b_b);
        a_b_a.addChild(a_b_a_a);
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
        TestSuite suite = new TestSuite("TestJdbcNodeCache"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcDatabaseImpl.class);
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

    public void helpTestFindJdbcNode( final JdbcNode node ) {
        final IPath path = node.getPath();
        assertSame(node, this.root.findJdbcNode(path));
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testGetWithPathNotInCache1() {
        assertNull(this.root.findJdbcNode( new Path("Some path not in cache"))); //$NON-NLS-1$
    }

    public void testGetWithPathNotInCache2() {
        final IPath path = a_b_a.getPath().append("B");     // shouldn't exist //$NON-NLS-1$
        assertNull(this.root.findJdbcNode(path));
    }

    public void testFindJdbcNodeForRoot() {
        helpTestFindJdbcNode(root);
    }

    public void testFindJdbcNodeForA() {
        helpTestFindJdbcNode(a);
    }

    public void testFindJdbcNodeForB() {
        helpTestFindJdbcNode(b);
    }

    public void testFindJdbcNodeForAA() {
        helpTestFindJdbcNode(a_a);
    }

    public void testFindJdbcNodeForAB() {
        helpTestFindJdbcNode(a_b);
    }

    public void testFindJdbcNodeForABA() {
        helpTestFindJdbcNode(a_b_a);
    }

    public void testFindJdbcNodeForABAA() {
        helpTestFindJdbcNode(a_b_a_a);
    }

    public void testFindJdbcNodeForABB() {
        helpTestFindJdbcNode(a_b_b);
    }

    public void testFindJdbcNodeForAC() {
        helpTestFindJdbcNode(a_c);
    }

    public void testGetPathInSource() {
        final IPath pathInSource = root.getPathInSource();
        assertNull(pathInSource);  // should not have path in source
    }

}
