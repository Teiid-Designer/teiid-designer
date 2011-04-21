/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.workspace;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import com.metamatrix.modeler.core.workspace.FakeModelWorkspaceItem;
import com.metamatrix.modeler.core.workspace.FakeOpenable;
import com.metamatrix.modeler.core.workspace.ModelWorkspaceItem;
import com.metamatrix.modeler.internal.core.util.TestOverflowingLRUCache;

/**
 * TestModelWorkspaceItemCache
 */
public class TestModelWorkspaceItemCache extends TestCase {

    private static final int DEFAULT_SIZE = 10;

    private ModelWorkspaceItemCache cache;

    /**
     * Constructor for TestModelWorkspaceItemCache.
     * 
     * @param name
     */
    public TestModelWorkspaceItemCache( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.cache = new ModelWorkspaceItemCache(DEFAULT_SIZE);

    }

    // =========================================================================
    // H E L P E R M E T H O D S
    // =========================================================================

    // =========================================================================
    // T E S T C A S E S
    // =========================================================================

    public void testAfterCreation() {
        TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, 0);
        TestOverflowingLRUCache.helpTestFlush(this.cache);
        TestOverflowingLRUCache.helpTestFlush(this.cache, null);
        TestOverflowingLRUCache.helpTestFlush(this.cache, "Some key"); //$NON-NLS-1$
    }

    public void testPopulationWithFakeItem() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for (int i = 0; i != spaceLimit; ++i) {
            final String keyStr = "Key" + i; //$NON-NLS-1$
            final FakeOpenable key = new FakeOpenable(keyStr);
            key.setChanged();
            final ModelWorkspaceItem item = new FakeModelWorkspaceItem();
            final Object result = this.cache.put(key, item);
            assertNull(result);
            TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, i + 1); // Item never closes, so always keeps
        }
    }

    public void testPopulationWithFakeModelBufferThatMayBeClosed() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for (int i = 0; i != spaceLimit; ++i) {
            final String keyStr = "Key" + i; //$NON-NLS-1$
            final Object key = new FakeOpenable(keyStr);
            final ModelWorkspaceItem item = new FakeModelWorkspaceItem();
            final Object result = this.cache.put(key, item);
            assertNull(result);
            final boolean shouldOverflow = false;
            TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, shouldOverflow);
        }
        // Ensure there is overflow ...
        if (this.cache.getOverflow() != 0) {
            fail("The cache did oveflow"); //$NON-NLS-1$
        }
    }

    public void testPopulationWithFakeModelBufferThatMayNotBeClosed() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for (int i = 0; i != spaceLimit; ++i) {
            final String keyStr = "Key" + i; //$NON-NLS-1$
            final FakeOpenable key = new FakeOpenable(keyStr);
            final ModelWorkspaceItem item = new FakeModelWorkspaceItem();
            key.setChanged();
            final Object result = this.cache.put(key, item);
            assertNull(result);
            TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, i + 1); // item never closes, so always keeps

        }
        // Ensure there is overflow ...
        if (this.cache.getOverflow() != 1) {
            fail("The cache did not oveflow"); //$NON-NLS-1$
        }
    }

    public void testPopulationBeyondLimitAndShrink() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        final List openables = new ArrayList();
        for (int i = 0; i != spaceLimit; ++i) {
            final String keyStr = "Key" + i; //$NON-NLS-1$
            final FakeOpenable key = new FakeOpenable(keyStr);
            final ModelWorkspaceItem item = new FakeModelWorkspaceItem();
            key.setChanged();
            openables.add(key);
            final Object result = this.cache.put(key, item);
            assertNull(result);
            TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, i + 1); // item never closes, so always keeps
        }

        // Ensure there is overflow ...
        if (this.cache.getOverflow() != 1) {
            fail("The cache did not oveflow"); //$NON-NLS-1$
        }

        // Save some of the buffers ...
        for (int i = 0; i != spaceLimit / 2; ++i) {
            final FakeOpenable openable = (FakeOpenable)openables.get(i);
            openable.save(null, true);
        }

        // Ensure there is (still) overflow ...
        if (this.cache.getOverflow() != 1) {
            fail("The cache did not oveflow"); //$NON-NLS-1$
        }

        // Add one more item to make it shrink ...
        final String someOtherKey = "Some other key"; //$NON-NLS-1$
        final FakeOpenable key = new FakeOpenable(someOtherKey);
        final FakeModelWorkspaceItem item = new FakeModelWorkspaceItem();
        key.setChanged();
        this.cache.put(key, item);

        // Ensure there no longer overflowed ...
        if (this.cache.getOverflow() != 0) {
            fail("The cache did oveflow"); //$NON-NLS-1$
        }
        TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, spaceLimit - spaceLimit / 2 + 1); // ensure less than max

    }

}
