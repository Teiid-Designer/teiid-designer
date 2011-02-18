/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.relationship;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestRelationshipTypeEditorImpl
 */
public class TestRelationshipTypeEditorImpl extends TestCase {

    /**
     * Constructor for TestRelationshipTypeEditorImpl.
     * @param name
     */
    public TestRelationshipTypeEditorImpl(String name) {
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
        TestSuite suite = new TestSuite("TestRelationshipTypeEditorImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationshipTypeEditorImpl.class);
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

    /*
     * Test for void RelationshipTypeEditorImpl(RelationshipType, boolean)
     */
    public void testRelationshipTypeEditorImplRelationshipTypeboolean() {
    }

    /*
     * Test for void RelationshipTypeEditorImpl(RelationshipType, boolean, RelationshipFactory)
     */
    public void testRelationshipTypeEditorImplRelationshipTypebooleanRelationshipFactory() {
    }

    public void testGetRelationshipType() {
    }

    public void testValidate() {
    }

    public void testGetName() {
    }

    public void testSetName() {
    }

    public void testGetOppositeName() {
    }

    public void testSetOppositeName() {
    }

    public void testGetStereotype() {
    }

    public void testSetStereotype() {
    }

    public void testGetComment() {
    }

    public void testSetComment() {
    }

    public void testGetSourceRoleName() {
    }

    public void testGetTargetRoleName() {
    }

    public void testGetFeatures() {
    }

    public void testIsDirected() {
    }

    public void testSetDirected() {
    }

    public void testIsExclusive() {
    }

    public void testSetExclusive() {
    }

    public void testIsCrossModel() {
    }

    public void testSetCrossModel() {
    }

    public void testIsAbstract() {
    }

    public void testSetAbstract() {
    }

    public void testGetSupertype() {
    }

    public void testSetSupertype() {
    }

    /*
     * Test for boolean canSetSupertype(RelationshipType)
     */
    public void testCanSetSupertypeRelationshipType() {
    }

    /*
     * Test for boolean canSetSupertype(RelationshipType, RelationshipType)
     */
    public void testCanSetSupertypeRelationshipTypeRelationshipType() {
    }

    public void testGetSubtypes() {
    }

    public void testCanAddSubtype() {
    }

    public void testGetSourceRole() {
    }

    public void testGetTargetRole() {
    }

    public void testGetRoleName() {
    }

    public void testSetRoleName() {
    }

    public void testIsNavigable() {
    }

    public void testSetNavigable() {
    }

    public void testIsOrdered() {
    }

    public void testSetOrdered() {
    }

    public void testIsUnique() {
    }

    public void testSetUnique() {
    }

    public void testGetLowerBound() {
    }

    public void testSetLowerBound() {
    }

    public void testGetUpperBound() {
    }

    public void testSetUpperBound() {
    }

    public void testGetIncludedMetaclasses() {
    }

    public void testCanAddIncludedMetaclass() {
    }

    public void testGetExcludedMetaclasses() {
    }

    public void testCanAddExcludedMetaclass() {
    }

    public void testIsAncestor() {
    }

}
