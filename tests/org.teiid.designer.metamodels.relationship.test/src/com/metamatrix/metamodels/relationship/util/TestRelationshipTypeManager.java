/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.metamodels.relationship.util;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import com.metamatrix.modeler.core.ExternalResourceDescriptor;
import com.metamatrix.modeler.core.container.Container;

/**
 * TestRelationshipTypeManager
 */
public class TestRelationshipTypeManager extends TestCase {

    /**
     * Constructor for TestRelationshipTypeManager.
     * @param name
     */
    public TestRelationshipTypeManager(String name) {
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
        TestSuite suite = new TestSuite("TestRelationshipTypeManager"); //$NON-NLS-1$
        suite.addTestSuite(TestRelationshipTypeManager.class);
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

    protected ExternalResourceDescriptor helpGetExternalResourceDescriptor() {
        return RelationshipTypeManager.getExternalResourceDescriptor();
    }

    protected Container helpCreateContainer(final String containerName) {
        return RelationshipTypeManager.createContainer(containerName);
    }

    protected Resource helpLoadContainer(final ExternalResourceDescriptor descriptor, final Container container) {
        return RelationshipTypeManager.loadContainer(descriptor,container);
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testSomething() {
        // This test case is really a base class for PdeTestRelationshipTypeManager
    }

    public void testConstructorWithNullResource() {
        try {
            new RelationshipTypeManager((Resource)null);
            fail("Failed to catch null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testConstructorWithNullURI() {
        try {
            new RelationshipTypeManager((URI)null);
            fail("Failed to catch null argument"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

}
