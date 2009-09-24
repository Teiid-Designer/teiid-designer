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
import org.eclipse.emf.common.util.URI;
import com.metamatrix.modeler.relationship.NavigationLink;

/**
 * TestNavigationLinkImpl
 */
public class TestNavigationLinkImpl extends TestCase {

    private static final URI MODEL_OBJECT_URI = URI.createURI("http://this.sam.com/someModel#uuid"); //$NON-NLS-1$
    private static final String LABEL1 = "Label for Link1"; //$NON-NLS-1$
    private static final String LABEL2 = "Label for Link2"; //$NON-NLS-1$
    private static final String TYPE1 = "Type for Link1"; //$NON-NLS-1$
    private static final String TYPE2 = "Type for Link2"; //$NON-NLS-1$

    private NavigationLink link1;
    private NavigationLink link2;

    /**
     * Constructor for TestNavigationLinkImpl.
     * 
     * @param name
     */
    public TestNavigationLinkImpl( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.link1 = new NavigationLinkImpl(MODEL_OBJECT_URI, LABEL1, TYPE1);
        this.link2 = new NavigationLinkImpl(null, LABEL2, TYPE2);
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
        TestSuite suite = new TestSuite("TestNavigationLinkImpl"); //$NON-NLS-1$
        suite.addTestSuite(TestNavigationLinkImpl.class);
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

    public void testSetup() {
        assertNotNull(MODEL_OBJECT_URI);
        assertNotNull(link1);
        assertNotNull(link2);
    }

    public void testConstructorWithNullArgument() {
        new NavigationLinkImpl(null, LABEL1, TYPE1);
    }

    public void testConstructorWithNonNullArgument() {
        new NavigationLinkImpl(MODEL_OBJECT_URI, LABEL1, TYPE1);
    }

    public void testGetModelObject() {
        assertSame(MODEL_OBJECT_URI, this.link1.getModelObjectUri());
        assertSame(null, this.link2.getModelObjectUri());
    }

    public void testGetLabel() {
        assertSame(LABEL1, this.link1.getLabel());
        assertSame(LABEL2, this.link2.getLabel());
    }

    public void testGetType() {
        assertSame(TYPE1, this.link1.getType());
        assertSame(TYPE2, this.link2.getType());
    }

}
