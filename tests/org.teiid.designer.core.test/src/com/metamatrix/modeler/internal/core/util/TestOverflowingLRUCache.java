/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * TestOverflowingLRUCache
 */
public class TestOverflowingLRUCache extends TestCase {

    private static final int DEFAULT_SIZE = 10;

    private OverflowingLRUCache cache;

    /**
     * Constructor for TestOverflowingLRUCache.
     * @param name
     */
    public TestOverflowingLRUCache(String name) {
        super(name);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.cache = new FakeOverflowingLRUCache(DEFAULT_SIZE);
    }

    public class Item {
        public final Object key;
        public Item( final Object key ) { this.key = key; }
        @Override
        public String toString() {return key.toString();}
    }

    public static void helpTestGetCurrentSpace( final OverflowingLRUCache ocache, final boolean shouldOverflow ) {
        final int space = ocache.size();
        final int maxSpace = ocache.getSpaceLimit();
        final int over = ocache.getOverflow();
        if ( space > maxSpace ) {
            if ( !shouldOverflow ) {
                fail("Actual space = " + space + "; max space = " + maxSpace + "; should not have overflowed"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            if ( space > maxSpace + over ) {
                fail("Actual space = " + space + "; max space = " + maxSpace + "; over = " + over); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        } else {
            if ( shouldOverflow ) {
                fail("Actual space = " + space + "; max space = " + maxSpace + "; should have overflowed"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            if ( over != 0 ) {
                fail("Actual space = " + space + "; max space = " + maxSpace + "; over = " + over); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
        }
    }

    public static void helpTestGetCurrentSpace( final OverflowingLRUCache ocache, final int expectedSpace ) {
        final int space = ocache.size();
        assertEquals(expectedSpace, space);
    }

    public static void helpTestFlush( final OverflowingLRUCache ocache ) {
        ocache.clear();
        assertEquals(0, ocache.size());
    }

    public static void helpTestFlush( final OverflowingLRUCache ocache, final Object key ) {
        ocache.remove(key);
        assertNull(ocache.get(key));
    }

    // =========================================================================
    //                         T E S T     C A S E S
    // =========================================================================

    public void testAfterCreation() {
        helpTestGetCurrentSpace(this.cache,0);
        helpTestFlush(this.cache);
        helpTestFlush(this.cache,null);
        helpTestFlush(this.cache,"Some key"); //$NON-NLS-1$
    }

    public void testPopulationWithFakeItem() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for ( int i=0; i!= spaceLimit; ++i ) {
            final Object key = "Key" + i; //$NON-NLS-1$
            final Item item = new Item(key);
            final Object result = this.cache.put(key,item);
            assertNull(result);
            helpTestGetCurrentSpace(this.cache,i+1);  // Item never closes, so always keeps
        }
    }

    public void testPopulationWithFakeModelBufferThatMayBeClosed() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for ( int i=0; i!= spaceLimit; ++i ) {
            final Object key = "Key" + i; //$NON-NLS-1$
            final Object item = new CacheItem(key);
            final Object result = this.cache.put(key,item);
            assertNull(result);
            boolean shouldOverflow = false;
            helpTestGetCurrentSpace(this.cache,shouldOverflow);
        }
        assertEquals(0, this.cache.getOverflow());
    }

    public void testPopulationWithFakeModelBufferThatMayNotBeClosed() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        for ( int i=0; i!= spaceLimit; ++i ) {
            final Object key = "Key" + i; //$NON-NLS-1$
            final CacheItem item = new CacheItem(key);
            item.setChanged(true);
            final Object result = this.cache.put(key,item);
            assertNull(result);
            helpTestGetCurrentSpace(this.cache,i+1);  // item never closes, so always keeps
        }
        assertEquals(1, this.cache.getOverflow());
    }

    public void testPopulationBeyondLimitAndShrink() {
        final int spaceLimit = this.cache.getSpaceLimit() + 1;
        final List items = new ArrayList();
        for ( int i=0; i!= spaceLimit; ++i ) {
            final Object key = "Key" + i; //$NON-NLS-1$
            final CacheItem item = new CacheItem(key);
            item.setChanged(true);
            items.add(item);
            final Object result = this.cache.put(key,item);
            assertNull(result);
            helpTestGetCurrentSpace(this.cache,i+1);  // item never closes, so always keeps
        }

        assertEquals(1, this.cache.getOverflow());

        // Save some of the items ...
        for ( int i=0; i!=spaceLimit/2 ; ++i ) {
            final CacheItem item = (CacheItem)items.get(i);
            item.save();
        }

        assertEquals(1, this.cache.getOverflow());

        // Add one more item to make it shrink ...
        final CacheItem item = new CacheItem("Some other key"); //$NON-NLS-1$
        item.setChanged(true);
        this.cache.put(item.getKey(),item);

        // Ensure there no longer overflowed ...
        assertEquals(0, this.cache.getOverflow());
        
        helpTestGetCurrentSpace(this.cache,spaceLimit - spaceLimit/2 + 1);        // ensure less than max
    }

}

