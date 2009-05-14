/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.jdbc.relational.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.metamodels.relational.RelationalEntity;
import com.metamatrix.metamodels.relational.RelationalFactory;
import com.metamatrix.metamodels.relational.impl.RelationalFactoryImpl;
import com.metamatrix.modeler.jdbc.metadata.JdbcNode;
import com.metamatrix.modeler.jdbc.metadata.impl.FakeJdbcNode;

/**
 * TestObjectMatcher
 */
public class TestObjectMatcher extends TestCase {

    private static final RelationalFactory RELATIONAL_FACTORY = new RelationalFactoryImpl();

    private ObjectMatcher matcher;
    private ObjectMatcher stringMatcher;

    /**
     * Constructor for TestObjectMatcher.
     * 
     * @param name
     */
    public TestObjectMatcher( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.matcher = new ObjectMatcher(null);

        this.stringMatcher = new ObjectMatcher(null);
        this.stringMatcher.setJdbcNodeNameProvider(ObjectMatcher.DEFAULT_MATCH_VALUE_PROVIDER);
        this.stringMatcher.setModelObjectNameProvider(ObjectMatcher.DEFAULT_MATCH_VALUE_PROVIDER);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.matcher = null;
        this.stringMatcher = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestObjectMatcher"); //$NON-NLS-1$
        suite.addTestSuite(TestObjectMatcher.class);
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

    public void helpTestJdbcNodeMatchValueProvider( final String name ) {
        final MatchValueProvider provider = ObjectMatcher.JDBC_NODE_VALUE_PROVIDER;
        final JdbcNode node = new FakeJdbcNode(name, null);
        // JdbcNodes never have a null name
        final String expectedValue = (name == null ? "" : name); //$NON-NLS-1$
        final String matchValue = provider.getMatchValue(node);
        assertEquals(expectedValue, matchValue);
        assertEquals(node.getName(), matchValue);
    }

    public void helpTestRelationalEntityMatchValueProvider( final String name ) {
        final MatchValueProvider provider = ObjectMatcher.RELATION_OBJECT_NAME_PROVIDER;
        final RelationalEntity entity = RELATIONAL_FACTORY.createBaseTable();
        entity.setName(name);
        final String matchValue = provider.getMatchValue(entity);
        assertEquals(name, matchValue);
        assertEquals(entity.getName(), matchValue);
    }

    public static List helpCreateList( final String[] strings ) {
        final List results = new LinkedList();
        for (int i = 0; i < strings.length; ++i) {
            results.add(strings[i]);
        }
        return results;
    }

    public static void helpFindBestMatch( final ObjectMatcher theMatcher,
                                          final String[] nodes,
                                          final String[] objs,
                                          final String[] unmatchedNodes,
                                          final String[] unmatchedObjs,
                                          final String[] matchIndexes ) {
        final List nodesList = helpCreateList(nodes);
        final List objsList = helpCreateList(objs);
        final List unmatchedNodesList = helpCreateList(unmatchedNodes);
        final List unmatchedObjsList = helpCreateList(unmatchedObjs);
        theMatcher.findBestMatches(nodesList, objsList);

        // Check the unmatched lists ...
        final List actualUnmatchedNodes = theMatcher.getUnmatchedJdbcNodes();
        final List actualUnmatchedObjs = theMatcher.getUnmatchedModelObjects();
        assertEquals(unmatchedNodesList.size(), actualUnmatchedNodes.size());
        assertEquals(unmatchedObjsList.size(), actualUnmatchedObjs.size());
        assertTrue(unmatchedNodesList.containsAll(actualUnmatchedNodes));
        assertTrue(actualUnmatchedNodes.containsAll(unmatchedNodesList));
        assertTrue(unmatchedObjsList.containsAll(actualUnmatchedObjs));
        assertTrue(actualUnmatchedObjs.containsAll(unmatchedObjsList));

        // Check the matches ...
        final Map matches = theMatcher.getDestination();
        assertEquals("Actual number of matches differred from expected", //$NON-NLS-1$
                     matchIndexes.length,
                     matches.size());
        for (int i = 0; i < matchIndexes.length; ++i) {
            final String indexPair = matchIndexes[i];
            final int indexOfComma = indexPair.indexOf(',');
            assertTrue("Match pair \"" + indexPair + "\" doesn't contain a comma", //$NON-NLS-1$ //$NON-NLS-2$
                       indexOfComma != -1);
            int index1 = Integer.parseInt(indexPair.substring(0, indexOfComma));
            int index2 = Integer.parseInt(indexPair.substring(indexOfComma + 1));
            assertTrue(index1 >= 0);
            assertTrue(index2 >= 0);
            assertTrue(index1 < nodes.length);
            assertTrue(index2 < objs.length);
            final Object matchedNode = nodes[index1];
            final Object matchedObj = objs[index2];
            final Object actualMatchedObj = matches.get(matchedNode);
            assertSame(matchedObj, actualMatchedObj);
        }
    }

    public void testConstructorWithNullMap() {
        final ObjectMatcher matcher = new ObjectMatcher(null);
        assertNotNull(matcher.getDestination());
    }

    public void testConstructorWithMap() {
        final Map destination = new HashMap();
        final ObjectMatcher matcher = new ObjectMatcher(destination);
        assertSame(destination, matcher.getDestination());
    }

    public void testJdbcNodeMatchValueProviderWithNullName() {
        helpTestJdbcNodeMatchValueProvider(null);
    }

    public void testJdbcNodeMatchValueProviderWithZeroLengthName() {
        helpTestJdbcNodeMatchValueProvider(""); //$NON-NLS-1$
    }

    public void testJdbcNodeMatchValueProviderWithName() {
        helpTestJdbcNodeMatchValueProvider("This is the name"); //$NON-NLS-1$
    }

    public void testRelationalEntityMatchValueProviderWithNullName() {
        helpTestRelationalEntityMatchValueProvider(null);
    }

    public void testRelationalEntityValueProviderWithZeroLengthName() {
        helpTestRelationalEntityMatchValueProvider(""); //$NON-NLS-1$
    }

    public void testRelationalEntityValueProviderWithName() {
        helpTestRelationalEntityMatchValueProvider("This is the name"); //$NON-NLS-1$
    }

    public void testGetJdbcNodeNameProviderOnDefaultConstructedObject() {
        assertSame(ObjectMatcher.JDBC_NODE_VALUE_PROVIDER, matcher.getJdbcNodeNameProvider());
    }

    public void testGetModelObjectNameProviderOnDefaultConstructedObject() {
        assertSame(ObjectMatcher.RELATION_OBJECT_NAME_PROVIDER, matcher.getModelObjectNameProvider());
    }

    public void testSetJdbcNodeNameProvider() {
        assertSame(ObjectMatcher.DEFAULT_MATCH_VALUE_PROVIDER, stringMatcher.getJdbcNodeNameProvider());
    }

    public void testSetModelObjectNameProvider() {
        assertSame(ObjectMatcher.DEFAULT_MATCH_VALUE_PROVIDER, stringMatcher.getModelObjectNameProvider());
    }

    public void testGetUnmatchedModelObjects() {
        assertNotNull(matcher.getUnmatchedModelObjects());
        assertEquals(0, matcher.getUnmatchedModelObjects().size());
    }

    public void testGetUnmatchedJdbcNodes() {
        assertNotNull(matcher.getUnmatchedJdbcNodes());
        assertEquals(0, matcher.getUnmatchedJdbcNodes().size());
    }

    // -------------------------------------------------------------------------
    // Perform the various find tests
    // -------------------------------------------------------------------------

    public void testFindBestMatchWithNoMatches() {
        // Test all exact matches in the same order
        final String[] nodes = new String[] {"abcde", "abCde", "ab", "abCdE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] objects = new String[] {"1234", "13", "55", "33"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] unmatchedNodes = nodes;
        final String[] unmatchedObjects = objects;
        final String[] matchIndexes = new String[] {};

        helpFindBestMatch(this.stringMatcher, nodes, objects, unmatchedNodes, unmatchedObjects, matchIndexes);
    }

    public void testFindBestMatchInSameOrderAndAllMatch() {
        // Test all exact matches in the same order
        final String[] nodes = new String[] {"abcde", "abCde", "ab", "abCdE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] objects = new String[] {"abcde", "abCde", "ab", "abCdE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] unmatchedNodes = new String[] {};
        final String[] unmatchedObjects = new String[] {};
        final String[] matchIndexes = new String[] {"0,0", "1,1", "2,2", "3,3"}; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

        helpFindBestMatch(this.stringMatcher, nodes, objects, unmatchedNodes, unmatchedObjects, matchIndexes);
    }

    public void testFindBestMatchInDifferentOrderAndAllMatch() {
        // Test all exact matches in the same order
        final String[] nodes = new String[] {"abcde", "abCde", "ab", "abCdE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] objects = new String[] {"abCde", "abCdE", "abcde", "ab"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] unmatchedNodes = new String[] {};
        final String[] unmatchedObjects = new String[] {};
        final String[] matchIndexes = new String[] {"0,2", "1,0", "2,3", "3,1"}; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

        helpFindBestMatch(this.stringMatcher, nodes, objects, unmatchedNodes, unmatchedObjects, matchIndexes);
    }

    public void testFindBestMatchTestWithCaseInsensitiveMatches() {
        final String[] nodes = new String[] {"abCde", "ab", "abCdE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        final String[] objects = new String[] {"abcde", "abCde"}; //$NON-NLS-1$ //$NON-NLS-2$
        final String[] unmatchedNodes = new String[] {nodes[1]};
        final String[] unmatchedObjects = new String[] {};
        final String[] matchIndexes = new String[] {"0,1", "2,0"}; //$NON-NLS-1$//$NON-NLS-2$

        helpFindBestMatch(this.stringMatcher, nodes, objects, unmatchedNodes, unmatchedObjects, matchIndexes);
    }

    public void testFindBestMatchTestWithDuplicateMatches() {
        final String[] nodes = new String[] {"abCde", "ab", "abCdE", "abCdE"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        final String[] objects = new String[] {"abcde", "abCde"}; //$NON-NLS-1$ //$NON-NLS-2$
        final String[] unmatchedNodes = new String[] {nodes[1], nodes[3]};
        final String[] unmatchedObjects = new String[] {};
        final String[] matchIndexes = new String[] {"0,1", "2,0"}; //$NON-NLS-1$//$NON-NLS-2$

        helpFindBestMatch(this.stringMatcher, nodes, objects, unmatchedNodes, unmatchedObjects, matchIndexes);
    }

}
