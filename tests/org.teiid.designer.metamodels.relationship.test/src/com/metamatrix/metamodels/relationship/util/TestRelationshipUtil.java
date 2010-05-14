/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.util;

import java.util.ArrayList;
import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcorePackage;
import com.metamatrix.metamodels.relationship.RelationshipPackage;

/**
 * TestRelationshipUtil
 */
public class TestRelationshipUtil extends TestCase {

    public static final EClass RELATIONSHIP = RelationshipPackage.eINSTANCE.getRelationship();
    public static final EClass RELATIONSHIP_ROLE = RelationshipPackage.eINSTANCE.getRelationshipRole();
    public static final EClass RELATIONSHIP_TYPE = RelationshipPackage.eINSTANCE.getRelationshipType();
    public static final EClass RELATIONSHIP_ENTITY = RelationshipPackage.eINSTANCE.getRelationshipEntity();
    public static final EClass EOBJECT = EcorePackage.eINSTANCE.getEObject();

    /**
     * Constructor for TestRelationshipUtil.
     * @param name
     */
    public TestRelationshipUtil(String name) {
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
        TestSuite suite = new TestSuite("TestRelationshipUtil"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationshipUtil.class);
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
//
//    public void testSetup() {
//        UnitTestUtil.assertTestDataPathSet();
//    }
//
    /*
     * Test for boolean isAncestor(EClass, EClass)
     */
    public void testIsAncestorEClassEClass() {
        assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP, RELATIONSHIP));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, RELATIONSHIP_ROLE));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, RELATIONSHIP_TYPE));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, RELATIONSHIP_ENTITY));
		assertEquals(false, RelationshipUtil.isAncestor(EOBJECT, EOBJECT));

        assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP, EOBJECT));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, EOBJECT));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, EOBJECT));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, EOBJECT));

        assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP, RELATIONSHIP_ENTITY));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, RELATIONSHIP_ENTITY));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, RELATIONSHIP_ENTITY));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, RELATIONSHIP_ENTITY));

        assertEquals(false, RelationshipUtil.isAncestor(EOBJECT, RELATIONSHIP));
		assertEquals(false, RelationshipUtil.isAncestor(EOBJECT, RELATIONSHIP_ROLE));
		assertEquals(false, RelationshipUtil.isAncestor(EOBJECT, RELATIONSHIP_TYPE));
		assertEquals(false, RelationshipUtil.isAncestor(EOBJECT, RELATIONSHIP_ENTITY));

        assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, RELATIONSHIP));
    }

    /*
     * Test for boolean isAncestor(EClass, Collection)
     */
    public void testIsAncestorEClassCollection() {
        final List eobject = new ArrayList();
        eobject.add(EOBJECT);

        assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP, eobject));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, eobject));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, eobject));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, eobject));
		assertEquals(false, RelationshipUtil.isAncestor(EOBJECT, eobject));

        eobject.add(RELATIONSHIP_ENTITY);
        assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP, eobject));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, eobject));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, eobject));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, eobject));

        final List relationshipAndRole = new ArrayList();
        relationshipAndRole.add(RELATIONSHIP);
        relationshipAndRole.add(RELATIONSHIP_ROLE);

        assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP, relationshipAndRole));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, relationshipAndRole));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, relationshipAndRole));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, relationshipAndRole));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP, relationshipAndRole));


        final List relationshipEntityAndType = new ArrayList();
        relationshipEntityAndType.add(RELATIONSHIP_ENTITY);
        relationshipEntityAndType.add(RELATIONSHIP_TYPE);

        assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP, relationshipEntityAndType));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP_ROLE, relationshipEntityAndType));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP_TYPE, relationshipEntityAndType));
		assertEquals(false, RelationshipUtil.isAncestor(RELATIONSHIP_ENTITY, relationshipEntityAndType));
		assertEquals(true, RelationshipUtil.isAncestor(RELATIONSHIP, relationshipEntityAndType));
    }

}
