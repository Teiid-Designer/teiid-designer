/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.metadata.impl;

import java.util.ArrayList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.core.runtime.IPath;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.JdbcNodeVisitor;

/**
 * TestJdbcNodeImpl
 */
public class TestJdbcNodeImpl extends TestCase {

    private final List expectedDepthZeroForRoot;
    private final List expectedDepthOneForRoot;
    private final List expectedDepthInfiniteForRoot;
    private final List expectedDepthZeroForA;
    private final List expectedDepthOneForA;
    private final List expectedDepthInfiniteForA;
    private final List expectedDepthZeroForB;
    private final List expectedDepthOneForB;
    private final List expectedDepthInfiniteForB;

    private FakeJdbcDatabase root;
    private FakeJdbcNode a;
    private FakeJdbcNode b;
    private FakeJdbcNode a_a;
    private FakeJdbcNode a_b;
    private FakeJdbcNode a_b_a;
    private FakeJdbcNode a_b_a_a;
    private FakeJdbcNode a_b_b;
    private FakeJdbcNode a_c;

    private AccumulatingVisitor visitor;

    protected class AccumulatingVisitor implements JdbcNodeVisitor {
        private List visitedNodes = new ArrayList();

        public boolean visit( JdbcNode node ) {
            visitedNodes.add(node);
            return true;
        }

        public List getVisitedNodes() {
            return visitedNodes;
        }
    }

    /**
     * Constructor for TestJdbcNodeImpl.
     * 
     * @param name
     */
    public TestJdbcNodeImpl( String name ) {
        super(name);
        expectedDepthOneForRoot = new ArrayList();
        expectedDepthZeroForRoot = new ArrayList();
        expectedDepthInfiniteForRoot = new ArrayList();
        expectedDepthOneForA = new ArrayList();
        expectedDepthZeroForA = new ArrayList();
        expectedDepthInfiniteForA = new ArrayList();
        expectedDepthOneForB = new ArrayList();
        expectedDepthZeroForB = new ArrayList();
        expectedDepthInfiniteForB = new ArrayList();
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        visitor = new AccumulatingVisitor();

        root = new FakeJdbcDatabase("Root Node"); //$NON-NLS-1$
        a = new FakeJdbcNode("A", root); //$NON-NLS-1$
        b = new FakeJdbcNode("B", root); //$NON-NLS-1$
        a_a = new FakeJdbcNode("A.A", a); //$NON-NLS-1$
        a_b = new FakeJdbcNode("A.B", a); //$NON-NLS-1$
        a_b_a = new FakeJdbcNode("A.B.A", a_b); //$NON-NLS-1$
        a_b_a_a = new FakeJdbcNode("A.B.A.A", a_b_a); //$NON-NLS-1$
        a_b_b = new FakeJdbcNode("A.B.B", a_b); //$NON-NLS-1$
        a_c = new FakeJdbcNode("A.C", a); //$NON-NLS-1$

        root.addChildNode(a);
        root.addChildNode(b);
        a.addChildNode(a_a);
        a.addChildNode(a_b);
        a.addChildNode(a_c);
        a_b.addChildNode(a_b_a);
        a_b.addChildNode(a_b_b);
        a_b_a.addChildNode(a_b_a_a);

        refreshExpectedLists();

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
        TestSuite suite = new TestSuite("TestJdbcNodeImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestJdbcNodeImpl.class);
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

    public void refreshExpectedLists() {
        expectedDepthZeroForRoot.clear();
        expectedDepthOneForRoot.clear();
        expectedDepthInfiniteForRoot.clear();
        expectedDepthZeroForA.clear();
        expectedDepthOneForA.clear();
        expectedDepthInfiniteForA.clear();
        expectedDepthZeroForB.clear();
        expectedDepthOneForB.clear();
        expectedDepthInfiniteForB.clear();

        expectedDepthZeroForRoot.add(root);
        expectedDepthOneForRoot.add(root);
        expectedDepthOneForRoot.add(a);
        expectedDepthOneForRoot.add(b);
        expectedDepthInfiniteForRoot.add(root);
        expectedDepthInfiniteForRoot.add(a);
        expectedDepthInfiniteForRoot.add(a_a);
        expectedDepthInfiniteForRoot.add(a_b);
        expectedDepthInfiniteForRoot.add(a_b_a);
        expectedDepthInfiniteForRoot.add(a_b_a_a);
        expectedDepthInfiniteForRoot.add(a_b_b);
        expectedDepthInfiniteForRoot.add(a_c);
        expectedDepthInfiniteForRoot.add(b);

        expectedDepthZeroForA.add(a);
        expectedDepthOneForA.add(a);
        expectedDepthOneForA.add(a_a);
        expectedDepthOneForA.add(a_b);
        expectedDepthOneForA.add(a_c);
        expectedDepthInfiniteForA.add(a);
        expectedDepthInfiniteForA.add(a_a);
        expectedDepthInfiniteForA.add(a_b);
        expectedDepthInfiniteForA.add(a_b_a);
        expectedDepthInfiniteForA.add(a_b_a_a);
        expectedDepthInfiniteForA.add(a_b_b);
        expectedDepthInfiniteForA.add(a_c);

        expectedDepthZeroForB.add(b);
        expectedDepthOneForB.add(b);
        expectedDepthInfiniteForB.add(b);
    }

    public void helpRefreshNodeA_B() throws Exception {
        a_b.refresh(); // deletes all children

        // So reconstruct children ...
        a_b_a = new FakeJdbcNode("A.B.A", a_b); //$NON-NLS-1$
        a_b_a_a = new FakeJdbcNode("A.B.A.A", a_b_a); //$NON-NLS-1$
        a_b_b = new FakeJdbcNode("A.B.B", a_b); //$NON-NLS-1$

        a_b.addChildNode(a_b_a);
        a_b.addChildNode(a_b_b);
        a_b_a.addChildNode(a_b_a_a);
    }

    public void helpRefreshRoot() throws Exception {
        root.refresh(); // deletes all children

        // So reconstruct children ...
        a = new FakeJdbcNode("A", root); //$NON-NLS-1$
        b = new FakeJdbcNode("B", root); //$NON-NLS-1$
        a_a = new FakeJdbcNode("A.A", a); //$NON-NLS-1$
        a_b = new FakeJdbcNode("A.B", a); //$NON-NLS-1$
        a_b_a = new FakeJdbcNode("A.B.A", a_b); //$NON-NLS-1$
        a_b_a_a = new FakeJdbcNode("A.B.A.A", a_b_a); //$NON-NLS-1$
        a_b_b = new FakeJdbcNode("A.B.B", a_b); //$NON-NLS-1$
        a_c = new FakeJdbcNode("A.C", a); //$NON-NLS-1$

        root.addChildNode(a);
        root.addChildNode(b);
        a.addChildNode(a_a);
        a.addChildNode(a_b);
        a.addChildNode(a_c);
        a_b.addChildNode(a_b_a);
        a_b.addChildNode(a_b_b);
        a_b_a.addChildNode(a_b_a_a);
    }

    public void helpTestVisit( final JdbcNode node,
                               final int depth,
                               final List expectedNodes ) throws Exception {
        node.accept(visitor, depth);
        assertEquals(expectedNodes.size(), visitor.getVisitedNodes().size());
        assertEquals(expectedNodes, visitor.getVisitedNodes());
    }

    public void helpTestPath( final JdbcNode node,
                              final String expectedStringifiedPath ) {
        assertNotNull(node);
        assertNotNull(expectedStringifiedPath);

        final IPath actualPath = node.getPath();
        assertNotNull(actualPath);
        final String actualStringifiedPath = actualPath.toString();
        assertEquals(expectedStringifiedPath, actualStringifiedPath);
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testGetName() {
    }

    public void testGetType() {
    }

    public void testRefresh() {
    }

    /*
     * Test for String toString()
     */
    public void testToString() {
    }

    public void testAcceptDepthZeroFromRoot() throws Exception {
        helpTestVisit(root, JdbcNode.DEPTH_ZERO, expectedDepthZeroForRoot);
    }

    public void testAcceptDepthOneFromRoot() throws Exception {
        helpTestVisit(root, JdbcNode.DEPTH_ONE, expectedDepthOneForRoot);
    }

    public void testAcceptDepthInfiniteFromRoot() throws Exception {
        helpTestVisit(root, JdbcNode.DEPTH_INFINITE, expectedDepthInfiniteForRoot);
    }

    public void testAcceptDepthZeroFromA() throws Exception {
        helpTestVisit(a, JdbcNode.DEPTH_ZERO, expectedDepthZeroForA);
    }

    public void testAcceptDepthOneFromA() throws Exception {
        helpTestVisit(a, JdbcNode.DEPTH_ONE, expectedDepthOneForA);
    }

    public void testAcceptDepthInfiniteFromA() throws Exception {
        helpTestVisit(a, JdbcNode.DEPTH_INFINITE, expectedDepthInfiniteForA);
    }

    public void testAcceptDepthZeroFromB() throws Exception {
        helpTestVisit(b, JdbcNode.DEPTH_ZERO, expectedDepthZeroForB);
    }

    public void testAcceptDepthOneFromB() throws Exception {
        helpTestVisit(b, JdbcNode.DEPTH_ONE, expectedDepthOneForB);
    }

    public void testAcceptDepthInfiniteFromB() throws Exception {
        helpTestVisit(b, JdbcNode.DEPTH_INFINITE, expectedDepthInfiniteForB);
    }

    public void testGetPathForRoot() {
        helpTestPath(root, "/"); //$NON-NLS-1$
    }

    public void testGetPathForA() {
        helpTestPath(a, "/A"); //$NON-NLS-1$
    }

    public void testGetPathForB() {
        helpTestPath(b, "/B"); //$NON-NLS-1$
    }

    public void testGetPathForAA() {
        helpTestPath(a_a, "/A/A.A"); //$NON-NLS-1$
    }

    public void testGetPathForAB() {
        helpTestPath(a_b, "/A/A.B"); //$NON-NLS-1$
    }

    public void testGetPathForABA() {
        helpTestPath(a_b_a, "/A/A.B/A.B.A"); //$NON-NLS-1$
    }

    public void testGetPathForABAA() {
        helpTestPath(a_b_a_a, "/A/A.B/A.B.A/A.B.A.A"); //$NON-NLS-1$
    }

    public void testGetPathForABB() {
        helpTestPath(a_b_b, "/A/A.B/A.B.B"); //$NON-NLS-1$
    }

    public void testGetPathForAC() {
        helpTestPath(a_c, "/A/A.C"); //$NON-NLS-1$
    }

    public void testGetParentForRoot() {
        assertSame(null, root.getParent());
    }

    public void testGetParentForA() {
        assertSame(root, a.getParent());
    }

    public void testGetParentForB() {
        assertSame(root, b.getParent());
    }

    public void testGetParentForAA() {
        assertSame(a, a_a.getParent());
    }

    public void testGetParentForAB() {
        assertSame(a, a_b.getParent());
    }

    public void testGetParentForABA() {
        assertSame(a_b, a_b_a.getParent());
    }

    public void testGetParentForABAA() {
        assertSame(a_b_a, a_b_a_a.getParent());
    }

    public void testGetParentForABB() {
        assertSame(a_b, a_b_b.getParent());
    }

    public void testGetParentForAC() {
        assertSame(a, a_c.getParent());
    }

    public void testGetNameForRoot() {
        assertSame("Root Node", root.getName()); //$NON-NLS-1$
    }

    public void testGetNametForA() {
        assertSame("A", a.getName()); //$NON-NLS-1$
    }

    public void testGetNameForB() {
        assertSame("B", b.getName()); //$NON-NLS-1$
    }

    public void testGetNameForAA() {
        assertSame("A.A", a_a.getName()); //$NON-NLS-1$
    }

    public void testGetNameForAB() {
        assertSame("A.B", a_b.getName()); //$NON-NLS-1$
    }

    public void testGetNameForABA() {
        assertSame("A.B.A", a_b_a.getName()); //$NON-NLS-1$
    }

    public void testGetNameForABAA() {
        assertSame("A.B.A.A", a_b_a_a.getName()); //$NON-NLS-1$
    }

    public void testGetNameForABB() {
        assertSame("A.B.B", a_b_b.getName()); //$NON-NLS-1$
    }

    public void testGetNameForAC() {
        assertSame("A.C", a_c.getName()); //$NON-NLS-1$
    }

    public void testGetJdbcDatabaseForRoot() {
        assertSame(root, root.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForA() {
        assertSame(root, a.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForB() {
        assertSame(root, b.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForAA() {
        assertSame(root, a_a.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForAB() {
        assertSame(root, a_b.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForABA() {
        assertSame(root, a_b_a.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForABAA() {
        assertSame(root, a_b_a_a.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForABB() {
        assertSame(root, a_b_b.getJdbcDatabase());
    }

    public void testGetJdbcDatabaseForAC() {
        assertSame(root, a_c.getJdbcDatabase());
    }

    // -------------------------------------------------------------------------
    // Test the Selection Mode
    // -------------------------------------------------------------------------

    public void testDefaultSelection() {
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());
    }

    public void testSelectionWhenAllUnselected1() {
        a_b_a.setSelected(true);
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());
    }

    public void testSelectionWhenAllUnselected2() {
        a.setSelected(true);
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_c.getSelectionMode());
    }

    public void testSelectionOfRootWhenAllUnselected() {
        root.setSelected(true);
        assertEquals(JdbcNode.SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_c.getSelectionMode());
    }

    public void testSelectionOfLeafWhenAllUnselected() {
        a_b_a_a.setSelected(true);
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());
    }

    public void testSelectionWhenRefreshingAfterSelection1() throws Exception {
        a.setSelected(true);
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_c.getSelectionMode());

        helpRefreshNodeA_B();

        // Perform the same tests
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_c.getSelectionMode());
    }

    public void testSelectionWhenRefreshingAfterSelection2() throws Exception {
        a_b_a_a.setSelected(true);
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());

        helpRefreshNodeA_B();

        // Perform the same tests
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());
    }

    public void testSelectionWhenRefreshingAfterSelection3() throws Exception {
        a_b_a_a.setSelected(true);
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());

        helpRefreshRoot();

        // Perform the same tests
        assertEquals(JdbcNode.PARTIALLY_SELECTED, root.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_a.getSelectionMode());
        assertEquals(JdbcNode.PARTIALLY_SELECTED, a_b.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a.getSelectionMode());
        assertEquals(JdbcNode.SELECTED, a_b_a_a.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_b_b.getSelectionMode());
        assertEquals(JdbcNode.UNSELECTED, a_c.getSelectionMode());
    }
}
