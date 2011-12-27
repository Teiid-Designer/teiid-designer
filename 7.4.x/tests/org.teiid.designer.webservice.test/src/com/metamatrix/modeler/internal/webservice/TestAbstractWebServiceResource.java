/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.webservice;

import java.util.Collection;
import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.metamatrix.core.util.SmartTestDesignerSuite;
import com.metamatrix.modeler.webservice.IWebServiceResource;

/**
 * @since 4.2
 */
public class TestAbstractWebServiceResource extends TestCase {

    private static final String RESOURCE1_NAMESPACE = "http://something"; //$NON-NLS-1$
    private static final String RESOURCE1_FULLPATH = "E:/something/else.wsdl"; //$NON-NLS-1$

    private static final String RESOURCE2_NAMESPACE = RESOURCE1_NAMESPACE;
    private static final String RESOURCE2_FULLPATH = RESOURCE1_FULLPATH;

    private static final String RESOURCE3_NAMESPACE = "http://acme"; //$NON-NLS-1$
    private static final String RESOURCE3_FULLPATH = "D:/abcdef/else.xsd"; //$NON-NLS-1$

    private static final String RESOURCE4_NAMESPACE = null;
    private static final String RESOURCE4_FULLPATH = "D:/abcdef/else.txt"; //$NON-NLS-1$

    private static final String RESOURCE5_NAMESPACE = RESOURCE3_NAMESPACE;
    private static final String RESOURCE5_FULLPATH = null;

    private AbstractWebServiceResource resource1;
    private AbstractWebServiceResource resource2; // equivalent to reosurce1
    private AbstractWebServiceResource resource3;
    private AbstractWebServiceResource resource4; // null namespace
    private AbstractWebServiceResource resource5; // null full path

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.resource1 = new FakeConcreteAbstractWebServiceResource(RESOURCE1_NAMESPACE, RESOURCE1_FULLPATH, true);
        this.resource2 = new FakeConcreteAbstractWebServiceResource(RESOURCE2_NAMESPACE, RESOURCE2_FULLPATH, true);
        this.resource3 = new FakeConcreteAbstractWebServiceResource(RESOURCE3_NAMESPACE, RESOURCE3_FULLPATH, true);
        this.resource4 = new FakeConcreteAbstractWebServiceResource(RESOURCE4_NAMESPACE, RESOURCE4_FULLPATH, true);
        this.resource5 = new FakeConcreteAbstractWebServiceResource(RESOURCE5_NAMESPACE, RESOURCE5_FULLPATH, false);
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TestAbstractWebServiceResource.
     * 
     * @param name
     */
    public TestAbstractWebServiceResource( String name ) {
        super(name);
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new SmartTestDesignerSuite("org.teiid.designer.webservice", "TestAbstractWebServiceResource"); //$NON-NLS-1$ //$NON-NLS-2$
        suite.addTestSuite(TestAbstractWebServiceResource.class);
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

    public void helpCheckCollection( final int expectedSize,
                                     final Collection actual ) {
        assertNotNull(actual);
        assertEquals(expectedSize, actual.size());
    }

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testSetup() {
        assertNotNull(this.resource1);
        assertNotNull(this.resource2);
        assertNotNull(this.resource3);
        assertNotNull(this.resource4);
        assertNotNull(this.resource5);
    }

    public void testHashCode() {
        assertEquals(this.resource1.hashCode(), this.resource2.hashCode());

        this.resource3.hashCode();

        // test hashcode with a null namespace ...
        this.resource4.hashCode();

        // test hashcode with a null full path ...
        this.resource5.hashCode();

    }

    public void testGetFullPath() {
        assertSame(RESOURCE1_FULLPATH, this.resource1.getFullPath());
        assertSame(RESOURCE2_FULLPATH, this.resource2.getFullPath());
        assertSame(RESOURCE3_FULLPATH, this.resource3.getFullPath());
        assertSame(RESOURCE4_FULLPATH, this.resource4.getFullPath());
        assertSame(RESOURCE5_FULLPATH, this.resource5.getFullPath());
    }

    public void testGetNamespace() {
        assertSame(RESOURCE1_NAMESPACE, this.resource1.getNamespace());
        assertSame(RESOURCE2_NAMESPACE, this.resource2.getNamespace());
        assertSame(RESOURCE3_NAMESPACE, this.resource3.getNamespace());
        assertSame(RESOURCE4_NAMESPACE, this.resource4.getNamespace());
        assertSame(RESOURCE5_NAMESPACE, this.resource5.getNamespace());
    }

    public void testGetFile() {
        // should return null, since these are all fake ...
        assertNull(this.resource1.getFile());
        assertNull(this.resource2.getFile());
        assertNull(this.resource3.getFile());
        assertNull(this.resource4.getFile());
        assertNull(this.resource5.getFile());
    }

    public void testGetInputStream() {
        // should return null, since these are all fake ...
        assertNull(this.resource1.getFile());
        assertNull(this.resource2.getFile());
        assertNull(this.resource3.getFile());
        assertNull(this.resource4.getFile());
        assertNull(this.resource5.getFile());
    }

    public void testIsWsdl() {
        assertEquals(true, this.resource1.isWsdl());
        assertEquals(true, this.resource2.isWsdl());
        assertEquals(false, this.resource3.isWsdl());
        assertEquals(false, this.resource4.isWsdl());
        assertEquals(false, this.resource5.isWsdl());
    }

    public void testIsXsd() {
        assertEquals(false, this.resource1.isXsd());
        assertEquals(false, this.resource2.isXsd());
        assertEquals(true, this.resource3.isXsd());
        assertEquals(false, this.resource4.isXsd());
        assertEquals(false, this.resource5.isXsd());
    }

    public void testGetReferencingResources() {
        helpCheckCollection(0, this.resource1.getReferencingResources());
        helpCheckCollection(0, this.resource2.getReferencingResources());
        helpCheckCollection(0, this.resource3.getReferencingResources());
        helpCheckCollection(0, this.resource4.getReferencingResources());
        helpCheckCollection(0, this.resource5.getReferencingResources());
    }

    public void testGetReferencedResources() {
        helpCheckCollection(0, this.resource1.getReferencedResources());
        helpCheckCollection(0, this.resource2.getReferencedResources());
        helpCheckCollection(0, this.resource3.getReferencedResources());
        helpCheckCollection(0, this.resource4.getReferencedResources());
        helpCheckCollection(0, this.resource5.getReferencedResources());
    }

    public void testRemoveFromAllReferencers() {
        this.resource1.removeFromAllReferencers();
        this.resource2.removeFromAllReferencers();
        this.resource3.removeFromAllReferencers();
        this.resource4.removeFromAllReferencers();
        this.resource5.removeFromAllReferencers();

        helpCheckCollection(0, this.resource1.getReferencingResources());
        helpCheckCollection(0, this.resource2.getReferencingResources());
        helpCheckCollection(0, this.resource3.getReferencingResources());
        helpCheckCollection(0, this.resource4.getReferencingResources());
        helpCheckCollection(0, this.resource5.getReferencingResources());

        helpCheckCollection(0, this.resource1.getReferencedResources());
        helpCheckCollection(0, this.resource2.getReferencedResources());
        helpCheckCollection(0, this.resource3.getReferencedResources());
        helpCheckCollection(0, this.resource4.getReferencedResources());
        helpCheckCollection(0, this.resource5.getReferencedResources());
    }

    public void testRemoveReferencedResource() {
        this.resource1.removeReferencedResource(this.resource2);
        this.resource2.removeReferencedResource(this.resource1);
        this.resource1.removeReferencedResource(this.resource3);
        this.resource1.removeReferencedResource(this.resource4);
        this.resource1.removeReferencedResource(this.resource5);
    }

    public void testAddReferencedResource() {
        assertEquals(true, this.resource5.addReferencedResource(this.resource1));

        helpCheckCollection(0, this.resource5.getReferencingResources());
        helpCheckCollection(1, this.resource1.getReferencingResources());

        helpCheckCollection(1, this.resource5.getReferencedResources());
        helpCheckCollection(0, this.resource1.getReferencedResources());
    }

    public void testIsReferencedDirectlyOrIndirectly() {
        assertEquals(true, this.resource5.addReferencedResource(this.resource3));
        assertEquals(true, this.resource3.addReferencedResource(this.resource1));

        helpCheckCollection(0, this.resource5.getReferencingResources());
        helpCheckCollection(1, this.resource3.getReferencingResources());
        helpCheckCollection(1, this.resource1.getReferencingResources());

        helpCheckCollection(1, this.resource5.getReferencedResources());
        helpCheckCollection(1, this.resource3.getReferencedResources());
        helpCheckCollection(0, this.resource1.getReferencedResources());
    }

    public void testGetStatus() {
    }

    public void testIsResolved() {
        assertEquals(true, this.resource1.isResolved());
        assertEquals(true, this.resource2.isResolved());
        assertEquals(true, this.resource3.isResolved());
        assertEquals(true, this.resource4.isResolved());
        assertEquals(false, this.resource5.isResolved());
    }

    public void testIsResolvedToSelf() {
        assertEquals(true, this.resource1.isResolvedToSelf());
        assertEquals(true, this.resource2.isResolvedToSelf());
        assertEquals(true, this.resource3.isResolvedToSelf());
        assertEquals(true, this.resource4.isResolvedToSelf());
        assertEquals(false, this.resource5.isResolvedToSelf());
    }

    public void testGetResolvedResource() {
        assertSame(this.resource1, this.resource1.getResolvedResource());
        assertSame(this.resource2, this.resource2.getResolvedResource());
        assertSame(this.resource3, this.resource3.getResolvedResource());
        assertSame(this.resource4, this.resource4.getResolvedResource());
        assertSame(null, this.resource5.getResolvedResource());
    }

    public void testGetResourcesResolved() {
    }

    public void testGetLastResolvedResource() {
        assertSame(this.resource1, this.resource1.getLastResolvedResource());
        assertSame(this.resource2, this.resource2.getLastResolvedResource());
        assertSame(this.resource3, this.resource3.getLastResolvedResource());
        assertSame(this.resource4, this.resource4.getLastResolvedResource());
        assertSame(null, this.resource5.getLastResolvedResource());

        assertEquals(true, this.resource5.setResolvedResource(this.resource3));
        assertEquals(true, this.resource3.setResolvedResource(this.resource1));

        assertSame(this.resource3, this.resource5.getResolvedResource());
        assertSame(this.resource1, this.resource3.getResolvedResource());
        assertSame(this.resource1, this.resource5.getLastResolvedResource());
        assertSame(this.resource1, this.resource3.getLastResolvedResource());
    }

    public void testSetResolvedResource() {
    }

    /*
     * Class under test for boolean equals(Object)
     */
    public void testEqualsObject() {
        assertEquals(true, this.resource1.equals(this.resource2));
        assertEquals(true, this.resource2.equals(this.resource1));

        assertEquals(false, this.resource1.equals(this.resource3));
        assertEquals(false, this.resource3.equals(this.resource1));

        IWebServiceResource resource5Dup = new FakeConcreteAbstractWebServiceResource(RESOURCE5_NAMESPACE, RESOURCE5_FULLPATH,
                                                                                      false);

        assertEquals(true, this.resource5.equals(resource5Dup));
        assertEquals(true, resource5Dup.equals(this.resource5));

    }

    /*
     * Class under test for String toString()
     */
    public void testToString() {
    }

}
