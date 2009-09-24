/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.core.metamodel.aspect;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * TestAbstractMetamodelAspect
 */
public class TestAbstractMetamodelAspect extends TestCase {

    private AbstractMetamodelAspect aspectObj;

    /**
     * Constructor for TestAbstractMetamodelAspect.
     * @param name
     */
    public TestAbstractMetamodelAspect(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        aspectObj = new FakeAbstractMetamodelAspect();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        aspectObj = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestAbstractMetamodelAspect"); //$NON-NLS-1$
        suite.addTestSuite(TestAbstractMetamodelAspect.class);
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

    public void testGetMetamodelEntity() {
        assertNull(aspectObj.getMetamodelEntity());
    }

    public void testSetMetamodelEntity() {
        for (int i = 0; i < 4; ++i) {
            final MetamodelEntity entity = new FakeMetamodelEntity();
            aspectObj.setMetamodelEntity(entity);
            final MetamodelEntity actual = aspectObj.getMetamodelEntity();
            assertSame(entity, actual);
        }
    }

    public void testGetID() {
        assertNull(aspectObj.getID());
    }

    public void testSetID() {
        for (int i = 0; i < 4; ++i) {
            final String newID = "This is an id" + i; //$NON-NLS-1$
            aspectObj.setID(newID);
            final String actual = aspectObj.getID();
            assertEquals(newID, actual);
        }
    }

}
