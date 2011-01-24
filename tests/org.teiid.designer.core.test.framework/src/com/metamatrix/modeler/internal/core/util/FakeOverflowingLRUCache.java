/*
 * JBoss, Home of Professional Open Source.
 *
 * See the LEGAL.txt file distributed with this work for information regarding copyright ownership and licensing.
 *
 * See the AUTHORS.txt file distributed with this work for a full listing of individual contributors.
 */
package com.metamatrix.modeler.internal.core.util;

import java.util.Map.Entry;

/**
 * FakeOverflowingLRUCache
 */
public class FakeOverflowingLRUCache extends OverflowingLRUCache {

    /**
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct an instance of FakeOverflowingLRUCache.
     * @param size
     */
    public FakeOverflowingLRUCache(int size) {
        super(size);
    }
    
    @Override
    protected boolean close(Entry entry) {
    	final Object value = entry.getValue();
        if ( value instanceof CacheItem ) {
            CacheItem item = (CacheItem) value;
            return !item.isChanged();
        }
        return false;
    }
    
    @SuppressWarnings( "synthetic-access" )
    public CacheItem createCacheItem(Object key) {
        return new CacheItem(key);
    }

    public class CacheItem {
        private final Object key;
        private boolean changed;
        private CacheItem( final Object key ) {
            this.key = key;
        }
        public Object getKey() {
            return this.key;
        }
        public boolean isChanged() {
            return changed;
        }
        public void setChanged(boolean b) {
            changed = b;
        }
        public void save() {
            this.changed = false;
        }

    }

}

