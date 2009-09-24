/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.metamatrix.modeler.core.workspace.FakeOpenableModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;

/**
 * TestModelWorkspaceCache
 */
public class TestModelWorkspaceCache extends TestCase {

    private ModelWorkspaceCache cache;

    /**
     * Constructor for TestModelWorkspaceCache.
     * @param name
     */
    public TestModelWorkspaceCache(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.cache = new ModelWorkspaceCache();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        this.cache = null;
    }

    /**
     * Test suite, with one-time setup.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite("TestModelWorkspaceCache"); //$NON-NLS-1$
        suite.addTestSuite(TestModelWorkspaceCache.class);
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

    public ModelWorkspaceItem helpCreateItem() {
        return new FakeOpenableModelWorkspaceItem("Some id"); //$NON-NLS-1$
    }

    public ModelWorkspaceItemInfo helpCreateItemInfo() {
        return new ModelWorkspaceItemInfo();
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testModelWorkspaceCache() {
        new ModelWorkspaceCache();
    }

    public void testPkgSize() {
        assertEquals(0, this.cache.pkgSize());
    }

    public void testGetInfoForItemNotInCache() {
        final ModelWorkspaceItem item = helpCreateItem();
        final Object info = this.cache.getInfo(item);
        assertNull("The info should not have been found in the cache",info); //$NON-NLS-1$
    }

    public void testGetInfoForNullItem() {
        try {
            this.cache.getInfo(null);
            fail("Did not throw exception when null arg passed to getInfo()"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testPeekAtInfoForNullItem() {
        try {
            this.cache.peekAtInfo(null);
            fail("Did not throw exception when null arg passed to peekAtInfo()"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testRemoveInfoForNullItem() {
        try {
            this.cache.removeInfo(null);
            fail("Did not throw exception when null arg passed to removeInfo()"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testPutInfoForNullItem() {
        final ModelWorkspaceItem item = null;
        final ModelWorkspaceItemInfo info = helpCreateItemInfo();
        try {
            this.cache.putInfo(item,info);
            fail("Did not throw exception when null arg passed to putInfo()"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testPutInfoForNullInfo() {
        final ModelWorkspaceItem item = helpCreateItem();
        final ModelWorkspaceItemInfo info = null;
        try {
            this.cache.putInfo(item,info);
            fail("Did not throw exception when null arg passed to putInfo()"); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testPeekAtInfo() {
        final ModelWorkspaceItem item = helpCreateItem();
        final ModelWorkspaceItemInfo info = helpCreateItemInfo();
        this.cache.putInfo(item,info);
        final Object infoResult = this.cache.peekAtInfo(item);
        assertSame(infoResult, info);
    }

    public void testPutInfoAndGetInfo() {
        final ModelWorkspaceItem item = helpCreateItem();
        final ModelWorkspaceItemInfo info = helpCreateItemInfo();
        this.cache.putInfo(item,info);
        final Object infoResult = this.cache.getInfo(item);
        assertSame(infoResult, info);
    }

    public void testRemoveInfo() {
        final ModelWorkspaceItem item = helpCreateItem();
        final ModelWorkspaceItemInfo info = helpCreateItemInfo();
        this.cache.putInfo(item,info);
        final Object infoResult = this.cache.getInfo(item);
        assertSame(infoResult, info);

        this.cache.removeInfo(item);

        final Object infoAfterRemove = this.cache.getInfo(item);
        assertNull(infoAfterRemove);
    }

}
