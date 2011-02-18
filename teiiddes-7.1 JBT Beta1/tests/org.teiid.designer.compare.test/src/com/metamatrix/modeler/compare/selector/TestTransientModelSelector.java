/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.compare.selector;

import java.util.List;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import com.metamatrix.metamodels.core.ModelAnnotation;

/**
 * TestTransientModelSelector
 */
public class TestTransientModelSelector extends TestCase {

    public static final String URI_STRING = "/some project/some folder/some model.xml"; //$NON-NLS-1$

    private TransientModelSelector selector;
    private URI uri;

    /**
     * Constructor for TestTransientModelSelector.
     * 
     * @param name
     */
    public TestTransientModelSelector( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.uri = URI.createURI(URI_STRING);
        this.selector = new TransientModelSelector(this.uri);
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
        TestSuite suite = new TestSuite("TestTransientModelSelector"); //$NON-NLS-1$
        suite.addTestSuite(TestTransientModelSelector.class);
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

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    // public void testDataPathSupplied() {
    // UnitTestUtil.assertTestDataPathSet();
    // }

    public void testSetup() {
        assertNotNull(this.uri);
        assertNotNull(this.selector);
    }

    public void testGetUri() {
        assertSame(this.uri, this.selector.getUri());
    }

    public void testGetContents() {
        final List contents = this.selector.getRootObjects();
        assertNotNull(contents);
        assertEquals(0, contents.size());

        // Add something ...
        final EObject root1 = EcoreFactory.eINSTANCE.createEObject();
        contents.add(root1);
        assertSame(this.selector.getRootObjects(), contents);
        assertEquals(1, contents.size());
    }

    public void testGetRootObjects() {
        final List contents = this.selector.getRootObjects();
        final List rootObjects = this.selector.getRootObjects();
        assertSame(contents, rootObjects);
    }

    public void getModelAnnotation() {
        assertEquals(0, this.selector.getRootObjects().size());
        final ModelAnnotation model = this.selector.getModelAnnotation();
        assertNotNull(model);
        assertEquals(1, this.selector.getRootObjects().size());
    }

}
