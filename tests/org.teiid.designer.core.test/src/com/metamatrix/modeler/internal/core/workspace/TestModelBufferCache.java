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
import com.metamatrix.modeler.internal.core.util.TestOverflowingLRUCache;

/**
 * TestModelBufferCache
 */
public class TestModelBufferCache extends TestCase {

    private static final int DEFAULT_SIZE = 10;

    private ModelBufferCache cache;

    /**
     * Constructor for TestModelBufferCache.
     * 
     * @param name
     */
    public TestModelBufferCache( String name ) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.cache = new ModelBufferCache(DEFAULT_SIZE);
    }

    public class Item {
        public final Object key;

        public Item( final Object key ) {
            this.key = key;
        }

        @Override
        public String toString() {
            return key.toString();
        }
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
            final Object key = "Key" + i; //$NON-NLS-1$
            final Item item = new Item(key);
            final Object result = this.cache.put(key, item);
            assertNull(result);
            TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, i + 1); // Item never closes, so always keeps
        }
    }

    public void testPopulationWithFakeModelBufferThatMayBeClosed() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for (int i = 0; i != spaceLimit; ++i) {
            final Object key = "Key" + i; //$NON-NLS-1$
            final FakeModelBuffer item = new FakeModelBuffer(key);
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
            final Object key = "Key" + i; //$NON-NLS-1$
            final FakeModelBuffer item = new FakeModelBuffer(key);
            item.setChanged(true);
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
        final List buffers = new ArrayList();
        for (int i = 0; i != spaceLimit; ++i) {
            final Object key = "Key" + i; //$NON-NLS-1$
            final FakeModelBuffer item = new FakeModelBuffer(key);
            item.setChanged(true);
            buffers.add(item);
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
            final FakeModelBuffer item = (FakeModelBuffer)buffers.get(i);
            item.save(null, true);
        }

        // Ensure there is (still) overflow ...
        if (this.cache.getOverflow() != 1) {
            fail("The cache did not oveflow"); //$NON-NLS-1$
        }

        // Add one more item to make it shrink ...
        final FakeModelBuffer item = new FakeModelBuffer("Some other key"); //$NON-NLS-1$
        item.setChanged(true);
        this.cache.put(item.getKey(), item);

        // Ensure there no longer overflowed ...
        if (this.cache.getOverflow() != 0) {
            fail("The cache did oveflow"); //$NON-NLS-1$
        }
        TestOverflowingLRUCache.helpTestGetCurrentSpace(this.cache, spaceLimit - spaceLimit / 2 + 1); // ensure less than max

    }

}
